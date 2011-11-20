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
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import edu.mit.mobile.android.content.AndroidVersions;
import edu.mit.mobile.android.content.SQLGenerationException;
import edu.mit.mobile.android.content.test.sample2.BlogPost;
import edu.mit.mobile.android.content.test.sample2.Comment;

public class SampleProvider2Test extends ProviderTestCase2<SampleProvider2> {

	private static final String
		TEST_BODY_1 = "test BlogPost body 1",
		TEST_BODY_1_MOD = "test BlogPost body 1 modified",
		TEST_BODY_2 = "test BlogPost body 2",
		TEST_COMMENT_BODY_1 = "first post!!!",
		TEST_COMMENT_BODY_1_MOD = "[comment poster has been banned]",
		TEST_COMMENT_BODY_2 = "actually, comment #2 is better",
		TEST_COMMENT_BODY_3 = "third time is the charm",
		TEST_COMMENT_ALL_MOD = "everyone's comment has been edited",
		TEST_TITLE = "test title 1",
		TEST_TITLE_2 = "test title 2";


	public SampleProvider2Test() {
		super(SampleProvider2.class, SampleProvider2.AUTHORITY);
	}

	/**
	 * Test basic functionality with simple, known-good data.
	 */
	public void testCRUD(){
		final MockContentResolver cr = getMockContentResolver();

		final Cursor c = cr.query(BlogPost.CONTENT_URI, null, null, null, null);

		assertNotNull(c);
		// the cursor (and DB) should be empty
		assertFalse(c.moveToFirst());

		c.close();

		ContentValues cv;

		// First check to make sure validation happens.
		// title is required, so this should throw an exception.
		boolean exceptionThrown = false;
		try {
			createTestPost(cr, null, TEST_BODY_1);
		}catch (final SQLException e){
			exceptionThrown = true;
		}
		assertTrue("expecting an exception to be thrown", exceptionThrown);

		// ok, now let's try with a title added:
		final Uri test1 = createTestPost(cr, TEST_TITLE, TEST_BODY_1);

		testQueryItem(cr, test1, TEST_TITLE, TEST_BODY_1).close();
		// Update


		cv = new ContentValues();
		cv.put(BlogPost.BODY, TEST_BODY_1_MOD);
		int count = cr.update(test1, cv, null, null);

		assertEquals(1, count);

		// check to see that it's updated properly.
		testQueryItem(cr, test1, TEST_TITLE, TEST_BODY_1_MOD).close();

		// now try two parameters

		// Delete
		count = cr.delete(test1, null, null);

		assertEquals(1, count);

	}

	/**
	 * Queries only one item.
	 *
	 * @param cr
	 * @param uri
	 * @param expectedTitle
	 * @param expectedBody
	 * @return
	 */
	private Cursor testQueryItem(ContentResolver cr, Uri uri, String expectedTitle, String expectedBody){

		// make sure that querying works
		final Cursor c = testQuery(cr, uri, null, null, null, null, 1);

		if (expectedBody != null){
			assertEquals(expectedBody, c.getString(c.getColumnIndex(BlogPost.BODY)));
		}

		if (expectedTitle != null){
			assertEquals(expectedTitle, c.getString(c.getColumnIndex(BlogPost.TITLE)));
		}

		assertFalse(c.isNull(c.getColumnIndex(BlogPost.CREATED_DATE)));
		final long createdDate = c.getLong(c.getColumnIndex(BlogPost.CREATED_DATE));
		assertTrue("createdDate <"+createdDate+"> was not reasonably recent", createdDate > 1200000000); // reasonably recently

		return c;
	}

	/**
	 * Queries only one item.
	 *
	 * @param cr
	 * @param uri
	 * @param expectedTitle
	 * @param expectedBody
	 * @return
	 */
	private Cursor testQueryCommentItem(ContentResolver cr, Uri uri, String expectedBody){

		// make sure that querying works
		final Cursor c = testQuery(cr, uri, null, null, null, null, 1);

		if (expectedBody != null){
			assertEquals(expectedBody, c.getString(c.getColumnIndex(Comment.BODY)));
		}

		assertFalse(c.isNull(c.getColumnIndex(Comment.CREATED_DATE)));
		final long createdDate = c.getLong(c.getColumnIndex(Comment.CREATED_DATE));
		assertTrue("createdDate <"+createdDate+"> was not reasonably recent", createdDate > 1200000000); // reasonably recently

		return c;
	}

	private Uri createTestPost(ContentResolver cr, String title, String body){
		final ContentValues cv = new ContentValues();

		// ok, now let's try with a title added:
		if (title != null){
			cv.put(BlogPost.TITLE, title);
		}

		if (body != null){
			cv.put(BlogPost.BODY, body);
		}

		final Uri newItem = cr.insert(BlogPost.CONTENT_URI, cv);

		assertNotNull(newItem);


		return newItem;
	}

	private Uri createTestComment(ContentResolver cr, Uri post, String body){
		final ContentValues cv = new ContentValues();

		if (body != null){
			cv.put(Comment.BODY, body);
		}

		final Uri newItem = cr.insert(BlogPost.COMMENTS.getUri(post), cv);

		assertNotNull(newItem);

		return newItem;
	}

	public void testQuerystring(){
		final MockContentResolver cr = getMockContentResolver();

		final Uri item = createTestPost(cr, TEST_TITLE, TEST_BODY_1);

		testQueryItem(cr, item, TEST_TITLE, TEST_BODY_1).close();

		final Uri queryTitle = BlogPost.CONTENT_URI.buildUpon().appendQueryParameter(BlogPost.TITLE, TEST_TITLE).build();

		testQueryItem(cr, queryTitle, TEST_TITLE, TEST_BODY_1).close();

		createTestPost(cr, TEST_TITLE_2, TEST_BODY_2);

		// should stay the same
		testQueryItem(cr, queryTitle, TEST_TITLE, TEST_BODY_1).close();

		final Uri queryTitle2 = BlogPost.CONTENT_URI.buildUpon().appendQueryParameter(BlogPost.TITLE, TEST_TITLE_2).build();
		// should get the new item
		testQueryItem(cr, queryTitle2, TEST_TITLE_2, TEST_BODY_2).close();


		final Uri queryTitle1Or2 = BlogPost.CONTENT_URI.buildUpon().appendQueryParameter(BlogPost.TITLE, TEST_TITLE).appendQueryParameter("|"+BlogPost.TITLE, TEST_TITLE_2).build();
		testQuery(cr, queryTitle1Or2, null, null, null, null, 2).close();


		final Uri queryTitle1And2 = BlogPost.CONTENT_URI.buildUpon().appendQueryParameter(BlogPost.TITLE, TEST_TITLE).appendQueryParameter(BlogPost.TITLE, TEST_TITLE_2).build();
		testQuery(cr, queryTitle1And2, null, null, null, null, 0).close();

		final Uri queryTitle1AndBody1 = BlogPost.CONTENT_URI.buildUpon().appendQueryParameter(BlogPost.TITLE, TEST_TITLE).appendQueryParameter(BlogPost.BODY, TEST_BODY_1).build();
		testQuery(cr, queryTitle1AndBody1, null, null, null, null, 1).close();


		final Uri queryIllegalName = BlogPost.CONTENT_URI.buildUpon().appendQueryParameter("foo'; '", TEST_TITLE).build();

		boolean exceptionThrown = false;
		try {
			testQuery(cr, queryIllegalName, null, null, null, null, 1).close();
		}catch (final SQLGenerationException e) {
			exceptionThrown = true;
		}
		assertTrue("expecting exception to be thrown", exceptionThrown);

	}

	private Cursor testQuery(ContentResolver cr, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, int expectedCount){
		final Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);
		assertNotNull(c);
		assertEquals(expectedCount, c.getCount());

		if (expectedCount == 0){
			assertFalse("expected moveToFirst to signal an empty cursor", c.moveToFirst());
		}else{
			assertTrue("expected moveToFirst to return true", c.moveToFirst());
		}

		return c;
	}

	public void testForeignKeyCrud(){
		final MockContentResolver cr = getMockContentResolver();

		final Uri post1 = createTestPost(cr, TEST_TITLE, TEST_BODY_1);
		final Uri post2 = createTestPost(cr, TEST_TITLE_2, TEST_BODY_2);

		final Uri post1Comments = BlogPost.COMMENTS.getUri(post1);
		final Uri post2Comments = BlogPost.COMMENTS.getUri(post2);

		final Uri comment1 = createTestComment(cr, post1, TEST_COMMENT_BODY_1);

		// ensure path creation is correct
		assertEquals(Uri.withAppendedPath(post1, Comment.PATH + "/1").toString(), comment1.toString());

		testQueryCommentItem(cr, comment1, TEST_COMMENT_BODY_1).close();

		// ensure that we actually handle the IDs
		testQuery(cr, Uri.withAppendedPath(post1, Comment.PATH + "/2"), null, null, null, null, 0).close();

		final Uri comment2 = createTestComment(cr, post1, TEST_COMMENT_BODY_2);

		testQueryCommentItem(cr, comment2, TEST_COMMENT_BODY_2).close();

		// ensure that comments are bound to their appropriate parent
		testQuery(cr, BlogPost.COMMENTS.getUri(post2), null, null, null, null, 0).close();

		final Uri comment1_post2 = createTestComment(cr, post2, TEST_COMMENT_BODY_1);

		testQuery(cr, comment1_post2, null, null, null, null, 1).close();

		// ensure that comments are bound to their appropriate parent
		testQuery(cr, post2Comments, null, null, null, null, 1).close();

		// ensure that comments are bound to their appropriate parent
		testQuery(cr, post1Comments, null, null, null, null, 2).close();


		//////////////////////////////////////////
		// update

		final ContentValues cv2 = new ContentValues();

		cv2.put(Comment.BODY, TEST_COMMENT_BODY_1_MOD);

		int updateCount = cr.update(comment1, cv2, null, null);

		assertEquals(1, updateCount);

		testQueryCommentItem(cr, comment1, TEST_COMMENT_BODY_1_MOD).close();

		// ensure only one has been updated
		testQueryCommentItem(cr, comment2, TEST_COMMENT_BODY_2).close();

		final Uri doesntExist = Uri.withAppendedPath(post2, Comment.PATH + "/5");

		updateCount = cr.update(doesntExist, cv2, null, null);

		assertEquals(0, updateCount);

		final ContentValues cv3 = new ContentValues();

		cv3.put(Comment.BODY, TEST_COMMENT_ALL_MOD);

		cr.update(BlogPost.COMMENTS.getUri(post1), cv3, null, null);

		testQueryCommentItem(cr, comment1, TEST_COMMENT_ALL_MOD).close();
		testQueryCommentItem(cr, comment2, TEST_COMMENT_ALL_MOD).close();

		///////////////////////////////////////////
		// delete

		int deletedCount = cr.delete(comment1, null, null);

		assertEquals(1, deletedCount);

		testQuery(cr, post1Comments, null, null, null, null, 1).close();

		final Uri comment3 = createTestComment(cr, post1, TEST_COMMENT_BODY_3);

		// test deleting of all comments
		// 2 comment should remain
		deletedCount = cr.delete(post1Comments, null, null);

		assertEquals(2, deletedCount);

		// make sure they're deleted.
		testQuery(cr, post1Comments, null, null, null, null, 0).close();

		// make sure we didn't delete any of post2's comments
		testQuery(cr, post2Comments, null, null, null, null, 1).close();

		// only do this if we know that cascade deletes are supported.
		if (AndroidVersions.SQLITE_SUPPORTS_FOREIGN_KEYS){
			// let's try deleting the post and ensuring that the child actually gets deleted too.
			cr.delete(post2, null, null);

			// This should work, as it only checks the foreign key field of the child (comment)
			// and doesn't ensure that the parent exists
			testQuery(cr, post2Comments, null, null, null, null, 0).close();
		}

	}

	public void testBulkInsert(){

	}
}