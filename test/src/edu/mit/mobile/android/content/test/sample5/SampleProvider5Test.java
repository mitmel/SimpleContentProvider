package edu.mit.mobile.android.content.test.sample5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.test.ProviderTestCase2;

public class SampleProvider5Test extends ProviderTestCase2<SampleProvider5> {

    public SampleProvider5Test() {
        super(SampleProvider5.class, SampleProvider5.AUTHORITY);
    }

    public void testBasic() throws RemoteException, OperationApplicationException {

        Set<String> tags;

        final Uri stackOverflow = createBookmark("Stack Overflow", "http://stackoverflow.com/",
                "code", "android", "development");

        tags = queryBookmarkTags(stackOverflow);

        assertEquals(3, tags.size());

        assertTrue(tags.containsAll(Arrays.asList(new String[] { "code", "android" })));


        final Uri delicious = createBookmark("Delicious", "https://delicious.com/", "bookmarks");

        tags = queryBookmarkTags(delicious);

        assertEquals(1, tags.size());

        assertTrue(tags.containsAll(Arrays.asList(new String[] { "bookmarks" })));

        final Uri lastfm = createBookmark("Last.fm", "http://last.fm/");

        tags = queryBookmarkTags(lastfm);

        assertEquals(0, tags.size());


        final Uri github = createBookmark("Github", "https://github.com/", "code", "git",
                "development");

        tags = queryBookmarkTags(github);

        assertEquals(3, tags.size());

        assertTrue(tags.containsAll(Arrays.asList(new String[] { "code", "git", "development" })));

    }

    private Set<String> queryBookmarkTags(Uri bookmark) {
        return Bookmark.getTags(getMockContentResolver(), Bookmark.TAGS.getUri(bookmark));
    }

    private Uri createBookmark(String title, String url, String... tags) throws RemoteException,
            OperationApplicationException {
        final ContentResolver cr = getMockContentResolver();

        final ContentValues cv = new ContentValues();
        cv.put(Bookmark.TITLE, title);
        cv.put(Bookmark.URL, url);

        final Uri bookmark = cr.insert(Bookmark.CONTENT_URI, cv);

        assertNotNull(bookmark);

        final ArrayList<ContentProviderOperation> cpos = new ArrayList<ContentProviderOperation>();

        //cpos.add(ContentProviderOperation.newInsert(Bookmark.CONTENT_URI).withValue(Bookmark.TITLE, title).withValue(Bookmark.URL, url).build());

        if (tags.length > 0){

            for (final String tag : tags){
                cpos.add(ContentProviderOperation.newUpdate(Bookmark.TAGS.getUri(bookmark))
                        .withValue(Tag.NAME, tag)
                        .withSelection(Tag.NAME + "=?", new String[] { tag })
                        .build());

                //
                // .withSelection("NOT EXISTS (SELECT 1 FROM tag WHERE " + Tag.NAME + "=?)",
                // new String[] { tag }
                cpos.add(ContentProviderOperation.newInsert(Bookmark.TAGS.getUri(bookmark))
                        .withValue(Tag.NAME, tag).build());
                //cpos.add(ContentProviderOperation.newInsert(Tag.BOOKMARKS.getUri(Uri.withAppendedPath(Bookmark.CONTENT_URI, "#"))).withValue(Tag.NAME, tag).withValueBackReference(Tag., previousResult).build());
            }

            final ContentProviderResult[] results = cr.applyBatch(SampleProvider5.AUTHORITY, cpos);

            assertEquals(cpos.size(), results.length);
        }

        return bookmark;
    }
}
