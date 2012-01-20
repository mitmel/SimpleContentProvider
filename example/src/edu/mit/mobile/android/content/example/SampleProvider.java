package edu.mit.mobile.android.content.example;

import android.content.ContentUris;
import edu.mit.mobile.android.content.DBHelper;
import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.SimpleContentProvider;

/**
 * <p>
 * This creates a ContentProvider (which you must define in your
 * AndroidManifest.xml) that you can use to store all your data. You should only
 * need one ContentProvider per Application, as each ContentProvider can contain
 * multiple types of data.
 * </p>
 *
 * <p>
 * ContentProviders are accessed by querying content:// URIs. These helpers
 * create URIs for you using the following scheme:
 * </p>
 *
 * <p>
 * a list of messages:
 * </p>
 *
 * <pre>
 * content://AUTHORITY/PATH
 * </pre>
 * <p>
 * For this example, that would be:
 * </p>
 *
 * <pre>
 * content://edu.mit.mobile.android.content.example.sampleprovider/message
 * </pre>
 *
 * <p>To make it easier to query in the future, that URI is stored in {@link Message#CONTENT_URI}.</p>
 *
 * <p>
 * a single message:
 * </p>
 *
 * <pre>
 * content://AUTHORITY/PATH/#
 * </pre>
 * <p>
 * For this example, that would be:
 * </p>
 *
 * <pre>
 * content://edu.mit.mobile.android.content.example.sampleprovider/message/3
 * </pre>
 *
 * <p>URIs of this type can be constructed using {@link ContentUris#withAppendedId(android.net.Uri, long)}.</p>
 */
public class SampleProvider extends SimpleContentProvider {

	// Each ContentProvider must have a globally unique authority. You should
	// specify one here starting from your your Application's package string:
	public static final String AUTHORITY = "edu.mit.mobile.android.content.example.sampleprovider";

	// Every time you update your database schema, you must increment the
	// database version.
	private static final int DB_VERSION = 1;

	public SampleProvider() {
		super(AUTHORITY, DB_VERSION);

		// This helper creates the table and can do basic database queries. See
		// Message for more info.
		final DBHelper messageHelper = new GenericDBHelper(Message.class);

		// This adds a mapping between the given content:// URI path and the
		// helper.
		addDirAndItemUri(messageHelper, Message.PATH);

		// the above three statements can be repeated to create multiple data
		// stores. Each will have separate tables and URIs.
	}
}
