package edu.mit.mobile.android.content.test.sample5;

import edu.mit.mobile.android.content.ContentItem;
import edu.mit.mobile.android.content.UriPath;
import edu.mit.mobile.android.content.column.DBColumn;
import edu.mit.mobile.android.content.column.TextColumn;
import edu.mit.mobile.android.content.m2m.M2MManager;

@UriPath(Tag.PATH)
public class Tag implements ContentItem {

    @DBColumn(type = TextColumn.class, unique = true)
    public static final String NAME = "name";
    public static final String PATH = "tag";

    public static M2MManager BOOKMARKS = new M2MManager(Bookmark.class);

}
