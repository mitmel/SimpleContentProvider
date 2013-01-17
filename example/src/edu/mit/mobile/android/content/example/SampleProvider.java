package edu.mit.mobile.android.content.example;

import android.content.ContentUris;
import android.net.Uri;
import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.QuerystringWrapper;
import edu.mit.mobile.android.content.SimpleContentProvider;
import edu.mit.mobile.android.content.dbhelper.SearchDBHelper;

/**
 * <p>
 * This creates a ContentProvider (which you must define in your AndroidManifest.xml) that you can
 * use to store all your data. You should only need one ContentProvider per Application, as each
 * ContentProvider can contain multiple types of data.
 * </p>
 *
 * <p>
 * ContentProviders are accessed by querying content:// URIs. Content URIs have two types: "dir" and
 * "item". "dir" indicates that the URI represents a collection of logical items and "item"
 * indicates that it refers to a unique item. In {@link SimpleContentProvider}-generated providers,
 * this usually means that "dir" is a list of all the content of a given type, while "item" is an
 * individual content item, identified uniquely by its serial database row ID
 * {@link ContentItem#_ID}.
 * </p>
 *
 * <p>
 * A "dir" URI ends in a non-numerical path:
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
 * <p>
 * To make it easier to query in the future, that URI is stored in {@link Message#CONTENT_URI}.
 * </p>
 *
 * <p>
 * An "item" URI builds off the "dir" URI and ends in a number.
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
 * <p>
 * URIs of this type can be constructed using
 * {@link ContentUris#withAppendedId(android.net.Uri, long)}.
 * </p>
 */
public class SampleProvider extends SimpleContentProvider {

    // Each ContentProvider must have a globally-unique authority. You can choose an arbitrary
    // string here, however to ensure that they will be globally-unique, best-practice is to build
    // one off your Application's package string.
    public static final String AUTHORITY = "edu.mit.mobile.android.content.example.sampleprovider";

    public static final String SEARCH_PATH = null;

    public static final Uri SEARCH = ProviderUtils.toContentUri(AUTHORITY,
            getSearchPath(SEARCH_PATH));

    // Every time you update your database schema, you must increment the
    // database version.
    private static final int DB_VERSION = 1;

    public SampleProvider() {
        super(AUTHORITY, DB_VERSION);

        // This helper is responsible for creating the tables and performing the actual database
        // queries. See Message for more info.
        final GenericDBHelper messageHelper = new GenericDBHelper(Message.class);

        // By wrapping the main helper like so, this will translate the query portion of the URI
        // (that is, the part after the "?") into a select statement to limit the results.
        final QuerystringWrapper queryWrapper = new QuerystringWrapper(messageHelper);

        // This adds a mapping between the given content:// URI path and the
        // helper.
        addDirAndItemUri(queryWrapper, Message.PATH);

        // the above statements can be repeated to create multiple data
        // stores. Each will have separate tables and URIs.

        // this hooks in search
        final SearchDBHelper searchHelper = new SearchDBHelper();

        searchHelper.registerDBHelper(messageHelper, Message.CONTENT_URI, Message.TITLE,
                Message.BODY, Message.TITLE, Message.BODY);

        addSearchUri(searchHelper, SEARCH_PATH);

    }
}
