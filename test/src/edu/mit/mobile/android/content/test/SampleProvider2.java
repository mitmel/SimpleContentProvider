package edu.mit.mobile.android.content.test;
/*
 * Copyright (C) 2011  MIT Mobile Experience Lab
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
import edu.mit.mobile.android.content.ForeignKeyDBHelper;
import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.QuerystringWrapper;
import edu.mit.mobile.android.content.SimpleContentProvider;
import edu.mit.mobile.android.content.test.sample2.BlogPost;
import edu.mit.mobile.android.content.test.sample2.Comment;

public class SampleProvider2 extends SimpleContentProvider {
	public static final String AUTHORITY = "edu.mit.mobile.android.content.test.sampleprovider2";

	public SampleProvider2() {
		//    authority   DB ver
		super(AUTHORITY, 1);

		final QuerystringWrapper blogPosts = new QuerystringWrapper(new GenericDBHelper(BlogPost.class));

		blogPosts.setOnSaveListener(BlogPost.ON_SAVE_LISTENER);

		// creates a relationship between BlogPosts and Comments, using Comment.POST as the column.
		// It's also responsible for creating the tables for the child.
		final ForeignKeyDBHelper comments = new ForeignKeyDBHelper(BlogPost.class, Comment.class, Comment.POST);

		addDirAndItemUri(blogPosts, BlogPost.PATH);
		addChildDirAndItemUri(comments, BlogPost.PATH, Comment.PATH);

		addDirAndItemUri(comments, Comment.PATH_ALL_COMMENTS);

	}
}
