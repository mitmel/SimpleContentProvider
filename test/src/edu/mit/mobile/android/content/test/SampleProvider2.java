package edu.mit.mobile.android.content.test;

/*
 * Copyright (C) 2011-2012  MIT Mobile Experience Lab
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
import android.net.Uri;
import edu.mit.mobile.android.content.ForeignKeyDBHelper;
import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.QuerystringWrapper;
import edu.mit.mobile.android.content.SimpleContentProvider;
import edu.mit.mobile.android.content.dbhelper.SearchDBHelper;
import edu.mit.mobile.android.content.test.sample2.BlogPost;
import edu.mit.mobile.android.content.test.sample2.Comment;

public class SampleProvider2 extends SimpleContentProvider {
    public static final String AUTHORITY = "edu.mit.mobile.android.content.test.sampleprovider2";

    public static final String SEARCH_PATH = null; // use the default search path
    public static final Uri SEARCH = ProviderUtils.toContentUri(AUTHORITY,
            getSearchPath(SEARCH_PATH));

    public SampleProvider2() {
        // authority DB ver
        super(AUTHORITY, 1);

        final GenericDBHelper blogPostsRaw = new GenericDBHelper(BlogPost.class);
        final QuerystringWrapper blogPosts = new QuerystringWrapper(blogPostsRaw);

        blogPosts.setOnSaveListener(BlogPost.ON_SAVE_LISTENER);

        // creates a relationship between BlogPosts and Comments, using Comment.POST as the column.
        // It's also responsible for creating the tables for the child.
        final ForeignKeyDBHelper comments = new ForeignKeyDBHelper(BlogPost.class, Comment.class,
                Comment.POST);

        addDirAndItemUri(blogPosts, BlogPost.PATH);
        addChildDirAndItemUri(comments, BlogPost.PATH, Comment.PATH);

        addDirAndItemUri(comments, Comment.PATH_ALL_COMMENTS);

        // add in a search interface
        final SearchDBHelper searchHelper = new SearchDBHelper();

        searchHelper.registerDBHelper(blogPostsRaw, BlogPost.CONTENT_URI, BlogPost.TITLE,
                BlogPost.BODY, BlogPost.BODY, BlogPost.TITLE);

        searchHelper.registerDBHelper(comments, Comment.ALL_COMMENTS, Comment.BODY, null,
                Comment.BODY);

        addSearchUri(searchHelper, SEARCH_PATH);
    }
}
