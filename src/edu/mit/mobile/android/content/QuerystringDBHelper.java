package edu.mit.mobile.android.content;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import edu.mit.mobile.android.utils.ListUtils;

public class QuerystringDBHelper extends GenericDBHelper {
	public static final String TAG = QuerystringDBHelper.class.getSimpleName();

	public QuerystringDBHelper(Class<? extends ContentItem> contentItem,
			Uri contentUri) {
		super(contentItem, contentUri);
	}

	@Override
	public Cursor queryDir(SQLiteDatabase db, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		final String query = uri.getEncodedQuery();
		String newSelection = selection;
		String[] newSelectionArgs = selectionArgs;
		Log.d(TAG, "query uri " + uri);
		try {
			if (query != null){
				final StringBuilder sb = new StringBuilder();
				// TODO the below doesn't work
				final List<NameValuePair> qs = URLEncodedUtils.parse(new URI(uri.toString()), "utf-8");

				final int count = qs.size();
				newSelectionArgs = new String[count];
				int i = 0;
				String name;
				for (final NameValuePair nvp : qs){
					name = nvp.getName();
					if (i > 0){
						if (name.startsWith("|")){
							sb.append("OR ");
							name = name.substring(1);
						}else{
							sb.append("AND ");
						}
					}
					sb.append(DatabaseUtils.sqlEscapeString(name));
					sb.append("=? ");
					newSelectionArgs[i] = nvp.getValue();
					i++;
				}

				newSelection = ProviderUtils.addExtraWhere(selection, sb.toString());
				newSelectionArgs = ProviderUtils.addExtraWhereArgs(selectionArgs, newSelectionArgs);
			}
		} catch (final URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "query:" + newSelection + "; args: "+ ListUtils.join(Arrays.asList(newSelectionArgs), ","));

		return super.queryDir(db, uri, projection, newSelection, newSelectionArgs, sortOrder);
	}
}
