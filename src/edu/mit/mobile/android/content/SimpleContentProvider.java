package edu.mit.mobile.android.content;

/*
 * Copyright (C) 2011 MIT Mobile Experience Lab
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import edu.mit.mobile.android.content.column.DBColumn;

/**
 * <h2>A simplified content provider.</h2>
 *
 *
 * <p>
 * This provider simplifies the construction of a content provider by splitting
 * it into two parts: the request handlers and a request mapper. The request
 * handlers — subclasses of {@link DBHelper} — handle the generation and
 * execution of the SQL, while {@link DBHelperMapper} maps URIs, types and verbs
 * to those handlers.
 * </p>
 *
 * <p>
 * {@link ContentItem}s describe the data that you wish to be persisted. In SQL
 * terms, these would be your tables or in Java terms, your objects. Unlike ORMs
 * which have typed fields, these are constructed using classes with numerous
 * static fields that describe the data. This is done in order to avoid creation
 * of short-lived data objects, which would lead to considerable garbage
 * collector churn.
 * </p>
 *
 * <p>
 * To use, first create your content item. Each content item is an
 * implementation of {@link ContentItem} and generally consists of a number of
 * annotated static fields.
 * </p>
 *
 * <h4>columns</h4>
 * <p>
 * A column is defined by creating a static final String whose value is the
 * desired column name. That column must be annotated with @{@link DBColumn},
 * where the column type and column definitions are specified. This is done this
 * way, so that queries can easily be constructed using the standard
 * {@link ContentProvider#query(Uri, String[], String, String[], String)} and
 * {@link ContentValues} frameworks.
 * </p>
 *
 * <p>
 * The content item below describes a message with two columns: a created date
 * and a body.
 * </p>
 *
 * <pre>
 * public class Message implements ContentItem {
 * 	// Column definitions below. ContentItem contains one column definition
 * 	// for the BaseColumns._ID which defines the primary key.
 * 	&#064;DBColumn(type = DateColumn.class, defaultValue = DateColumn.CURRENT_TIMESTAMP)
 * 	public static final String CREATED_DATE = &quot;created&quot;;
 *
 * 	&#064;DBColumn(type = TextColumn.class)
 * 	public static final String BODY = &quot;body&quot;;
 *
 * 	// The path component of the content URI.
 * 	public static final String PATH = &quot;message&quot;;
 *
 * 	// The SimpleContentProvider constructs content URIs based on your provided
 * 	// path and authority.
 * 	// This constant is not necessary, but is very handy for doing queries.
 * 	public static final Uri CONTENT_URI = ProviderUtils.toContentUri(
 * 			SampleProvider1.AUTHORITY, PATH);
 * }
 * </pre>
 *
 * <p>
 * To use this, subclass and use the following pattern:
 * </p>
 *
 * <pre>
 * public class MyProvider extends SimpleContentProvider {
 * 	public static final String AUTHORITY = &quot;edu.mit.mobile.android.content.test.sampleprovider1&quot;;
 *
 * 	public MyProvider() {
 * 		// authority DB name DB ver
 * 		super(AUTHORITY, &quot;myprovider&quot;, 1);
 *
 * 		// This helper creates the table and can do basic CRUD for items
 * 		// that
 * 		// use the dir/item scheme with the BaseColumns._ID integer primary
 * 		// key.
 * 		final DBHelper messageHelper = new GenericDBHelper(Message.class,
 * 				Message.CONTENT_URI);
 *
 * 		// This binds the helper to the provider.
 * 		addDBHelper(messageHelper);
 *
 * 		// Adds a mapping between the given content:// URI path and the
 * 		// helper.
 * 		//
 * 		// There's an optional fourth parameter which lets you have
 * 		// different
 * 		// helpers handle different SQL verbs (eg. use a GenericDBHelper for
 * 		// basic insert, delete, update, but have a custom helper for
 * 		// querying).
 * 		addDirUri(messageHelper, Message.PATH, getDirType(Message.PATH));
 * 		addItemUri(messageHelper, Message.PATH + &quot;/#&quot;,
 * 				getItemType(Message.PATH));
 *
 * 	}
 * }
 * </pre>
 *
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 *
 */
public abstract class SimpleContentProvider extends ContentProvider {

	private static final String TAG = SimpleContentProvider.class.getSimpleName();

	///////////////////// public API constants

	/**
	 * Suffix that turns a dir path into an item path. The # represents the item
	 * ID number.
	 */
	public static final String PATH_ITEM_ID_SUFFIX = "/#";

	/**
	 * This is the starting value for the automatically-generated URI mapper
	 * entries. You can freely use any numbers below this without any risk of
	 * conflict.
	 */
	public static final int URI_MATCHER_CODE_START = 0x100000;

	///////////////////////// private fields
	private final String mAuthority;
	private String mDBName;
	private final int mDBVersion;

	private final DBHelperMapper mDBHelperMapper;

	private static final UriMatcher MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	private DatabaseHelper mDatabaseHelper;

	private final List<DBHelper> mDBHelpers = new ArrayList<DBHelper>();

	private int mMatcherID = URI_MATCHER_CODE_START;

	///////////////////////////////// pubilc methods

	/**
	 * The database name for this provider is generated based on the provider's
	 * class name. If you have multiple providers in your {@link Application},
	 * you should ensure that these class names are unique or specify your own
	 * dbName with {@link #SimpleContentProvider(String, String, int)}.
	 *
	 * @param authority
	 *            the full authority string for this provider
	 * @param dbVersion
	 *            the version of the database schema associated with this
	 *            provider. <strong>NOTE:</strong> This number must be increased
	 *            each time the database schema (in this instance, your
	 *            {@link ContentItem} fields) changes in order for the tables to
	 *            be recreated. At the moment, there <strong>WILL BE DATA
	 *            LOSS</strong> when this number increases, so please make sure
	 *            to that the data is backed up first.
	 */
	public SimpleContentProvider(String authority, int dbVersion) {
		this(authority, null, dbVersion);
	}

	/**
	 * @param authority
	 *            the full authority string for this provider
	 * @param dbName
	 *            the name of the database. This must be unique throughout your
	 *            {@link Application}.
	 * @param dbVersion
	 *            the version of the database schema associated with this
	 *            provider. <strong>NOTE:</strong> This number must be increased
	 *            each time the database schema (in this instance, your
	 *            {@link ContentItem} fields) changes in order for the tables to
	 *            be recreated. At the moment, there <strong>WILL BE DATA
	 *            LOSS</strong> when this number increases, so please make sure
	 *            to that the data is backed up first.
	 */
	public SimpleContentProvider(String authority, String dbName, int dbVersion) {
		super();
		mAuthority = authority;
		mDBHelperMapper = new DBHelperMapper();
		mDBName = dbName;
		mDBVersion = dbVersion;
	}

	/**
	 * Registers a DBHelper with the content provider. You must call this in the
	 * constructor of any subclasses.
	 *
	 * @param dbHelper
	 */
	public void addDBHelper(DBHelper dbHelper) {
		mDBHelpers.add(dbHelper);
	}


	/**
	 * Adds an entry for a directory of a given type. This should be called in
	 * the constructor of any subclasses.
	 *
	 * @param dbHelper
	 *            the DBHelper to associate with the given path.
	 * @param path
	 *            a complete path on top of the content provider's authority.
	 * @param type
	 *            the complete MIME type for the item's directory.
	 * @param verb
	 *            one or more of {@link DBHelperMapper#VERB_ALL},
	 *            {@link DBHelperMapper#VERB_INSERT},
	 *            {@link DBHelperMapper#VERB_QUERY},
	 *            {@link DBHelperMapper#VERB_UPDATE},
	 *            {@link DBHelperMapper#VERB_DELETE} joined bitwise.
	 */
	public void addDirUri(DBHelper dbHelper, String path, String type, int verb) {
		mDBHelperMapper.addDirMapping(mMatcherID, dbHelper, verb, type);
		MATCHER.addURI(mAuthority, path, mMatcherID);
		mMatcherID++;
	}

	/**
	 * Adds an entry for a directory of a given type. This should be called in
	 * the constructor of any subclasses.
	 *
	 * Defaults to handle all method types.
	 *
	 * @param dbHelper
	 *            the DBHelper to associate with the given path.
	 * @param path
	 *            a complete path on top of the content provider's authority.
	 *
	 * @param type
	 *            the complete MIME type for the item's directory.
	 */
	public void addDirUri(DBHelper dbHelper, String path, String type) {
		addDirUri(dbHelper, path, type, DBHelperMapper.VERB_ALL);
	}

	/**
	 * Adds dir and item entries for the given helper at the given path. The
	 * types are generated using {@link #getDirType(String)} and
	 * {@link #getItemType(String)} passing path in for the suffix.
	 *
	 * @param dbHelper
	 * @param path
	 *            a complete path on top of the content provider's authority.
	 */
	public void addDirAndItemUri(DBHelper dbHelper, String path) {
		addDirAndItemUri(dbHelper, path, getDirType(path), getItemType(path));
	}

	/**
	 * Adds both dir and item entries for the given
	 *
	 * @param dbHelper
	 * @param path
	 *            the path that will be used for the item's dir. For the item,
	 *            it will be suffixed with {@value #PATH_ITEM_ID_SUFFIX}.
	 * @param dirType
	 *            The complete MIME type for the item's directory.
	 * @param itemType
	 *            The complete MIME type for the item.
	 */
	public void addDirAndItemUri(DBHelper dbHelper, String path,
			String dirType, String itemType) {
		addDirUri(dbHelper, path, dirType, DBHelperMapper.VERB_ALL);
		addItemUri(dbHelper, path + PATH_ITEM_ID_SUFFIX, itemType,
				DBHelperMapper.VERB_ALL);
	}

	/**
	 * Adds an entry for an item of a given type. This should be called in the
	 * constructor of any subclasses.
	 *
	 * @param dbHelper
	 *            the DBHelper to associate with the given path.
	 * @param path
	 *            a complete path on top of the content provider's authority.
	 *            <strong>This must end in
	 *            <code>{@value #PATH_ITEM_ID_SUFFIX}</code></strong>
	 * @param type
	 *            The complete MIME type for this item.
	 * @param verb
	 *            one or more of {@link DBHelperMapper#VERB_ALL},
	 *            {@link DBHelperMapper#VERB_INSERT},
	 *            {@link DBHelperMapper#VERB_QUERY},
	 *            {@link DBHelperMapper#VERB_UPDATE},
	 *            {@link DBHelperMapper#VERB_DELETE} joined bitwise.
	 */
	public void addItemUri(DBHelper dbHelper, String path, String type, int verb) {
		mDBHelperMapper.addItemMapping(mMatcherID, dbHelper, verb, type);
		MATCHER.addURI(mAuthority, path, mMatcherID);
		mMatcherID++;
	}

	/**
	 * Adds an entry for an item of a given type. This should be called in the
	 * constructor of any subclasses.
	 *
	 * Defaults to handle all method types.
	 *
	 * @param dbHelper
	 *            the DBHelper to associate with the given path.
	 * @param path
	 *            a complete path on top of the content provider's authority.
	 *            <strong>This must end in
	 *            <code>{@value #PATH_ITEM_ID_SUFFIX}</code></strong>
	 */
	public void addItemUri(DBHelper dbHelper, String path, String type) {
		addItemUri(dbHelper, path, type, DBHelperMapper.VERB_ALL);
	}

	@Override
	public boolean onCreate() {
		if (mDBName == null) {
			mDBName = extractDBName();
		}
		mDatabaseHelper = new DatabaseHelper(getContext(), mDBName, mDBVersion);

		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		final int match = MATCHER.match(uri);
		if (!mDBHelperMapper.canDelete(match)) {
			throw new IllegalArgumentException("delete note supported");
		}
		final int count = mDBHelperMapper.delete(match, this, db, uri, selection,
				selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public String getType(Uri uri) {
		final int match = MATCHER.match(uri);
		return mDBHelperMapper.getType(match);
	}

	/**
	 * Generates a complete MIME type string in the following format:
	 * {@code vnd.android.cursor.dir/vnd.AUTHORITY.SUFFIX}
	 *
	 * @param suffix
	 * @return the MIME type for the given suffix
	 */
	public String getDirType(String suffix) {
		return ProviderUtils.TYPE_DIR_PREFIX + mAuthority + "." + suffix;
	}

	/**
	 * Generates a complete MIME type string in the following format:
	 * {@code vnd.android.cursor.item/vnd.AUTHORITY.SUFFIX}
	 *
	 * @param suffix
	 * @return the MIME type for the given suffix
	 */
	public String getItemType(String suffix) {
		return ProviderUtils.TYPE_ITEM_PREFIX + mAuthority + "." + suffix;
	}

	/**
	 *
	 * @return the UriMatcher that's used to route the URIs of this handler
	 */
	public static UriMatcher getMatcher() {
		return MATCHER;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		final int match = MATCHER.match(uri);
		if (!mDBHelperMapper.canInsert(match)) {
			throw new IllegalArgumentException("insert not supported");
		}
		final Uri newUri = mDBHelperMapper.insert(match, this, db, uri, values);
		if (newUri != null) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return newUri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		final int match = MATCHER.match(uri);
		if (!mDBHelperMapper.canQuery(match)) {
			throw new IllegalArgumentException("query not supported");
		}
		final Cursor c = mDBHelperMapper.query(match, this, db, uri,
				projection, selection, selectionArgs, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		final int match = MATCHER.match(uri);
		if (!mDBHelperMapper.canUpdate(match)) {
			throw new IllegalArgumentException("update not supported");
		}
		final int changed = mDBHelperMapper.update(match, this, db, uri,
				values, selection, selectionArgs);
		if (changed != 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return changed;
	}

	/////////////////////// private methods
	/**
	 * Generates a name for the database from the content provider.
	 *
	 * @return a valid name, based on the classname.
	 */
	private String extractDBName() {
		return SQLGenUtils.toValidName(getClass());
	}

	////////////////////// internal classes

	private class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			for (final DBHelper dbHelper : mDBHelpers) {
				dbHelper.createTables(db);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			for (final DBHelper dbHelper : mDBHelpers) {
				dbHelper.upgradeTables(db, oldVersion, newVersion);
			}
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);

			if (AndroidVersions.SQLITE_SUPPORTS_FOREIGN_KEYS){
				db.execSQL("PRAGMA foreign_keys = ON;");
			}
		}
	}
}