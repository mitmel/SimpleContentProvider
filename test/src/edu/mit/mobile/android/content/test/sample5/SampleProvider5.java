package edu.mit.mobile.android.content.test.sample5;

import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.SimpleContentProvider;
import edu.mit.mobile.android.content.m2m.M2MDBHelper;
import edu.mit.mobile.android.content.m2m.M2MReverseHelper;

public class SampleProvider5 extends SimpleContentProvider {

    public static final int DB_VER = 1;
    public static final String AUTHORITY = "edu.mit.mobile.android.content.test.sampleprovider5";

    public SampleProvider5() {
        super(AUTHORITY, DB_VER);

        final GenericDBHelper bookmarks = new GenericDBHelper(Bookmark.class);

        final GenericDBHelper tags = new GenericDBHelper(Tag.class);

        // /bookmark/
        addDirAndItemUri(bookmarks, Bookmark.PATH);

        // /tag/
        // /tag/1/
        addDirAndItemUri(tags, Tag.PATH);

        final M2MDBHelper bookmarks_tags = new M2MDBHelper(bookmarks, tags,
                new IdenticalTagFinder());

        // /bookmark/#/tag/
        // /bookmark/#/tag/1/
        addChildDirAndItemUri(bookmarks_tags, Bookmark.PATH, Tag.PATH);

        final M2MReverseHelper tags_bookmarks = new M2MReverseHelper(bookmarks_tags);

        // /tag/#/bookmark/
        // /tag/#/bookmark/1/
        addChildDirAndItemUri(tags_bookmarks, Tag.PATH, Bookmark.PATH);

    }

}
