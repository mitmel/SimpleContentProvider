package edu.mit.mobile.android.content;


/**
 * If a {@link DBHelper} has a specific {@link ContentItem} that's associated with the given data,
 * it should implement this interface and return that ContentItem. This can later be retrieved from
 * {@link SimpleContentProvider#getContentItem(android.net.Uri)}.
 *
 */
public interface ContentItemRegisterable {

    /**
     * Implement this to return the {@link ContentItem} associated with the implementing class.
     *
     * @param isItem
     *            true if the registration is being done for an item. False if it's a directory.
     * @return the class of the {@link ContentItem}
     */
    public Class<? extends ContentItem> getContentItem(boolean isItem);
}
