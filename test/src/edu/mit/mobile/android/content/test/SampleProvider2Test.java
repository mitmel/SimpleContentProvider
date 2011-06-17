package edu.mit.mobile.android.content.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import edu.mit.mobile.android.content.test.sample2.BlogPost;

public class SampleProvider2Test extends ProviderTestCase2<SampleProvider2> {

	private static final String
		TEST_BODY_1 = "test BlogPost 1",
		TEST_BODY_1_MOD = "test BlogPost 1 modified",
		TEST_TITLE = "test title 1";


	public SampleProvider2Test() {
		super(SampleProvider2.class, SampleProvider2.AUTHORITY);
	}

	/**
	 * Test basic functionality with simple, known-good data.
	 */
	public void testCRUD(){
		final MockContentResolver cr = getMockContentResolver();

		Cursor c = cr.query(BlogPost.CONTENT_URI, null, null, null, null);

		assertNotNull(c);
		// the cursor (and DB) should be empty
		assertFalse(c.moveToFirst());

		ContentValues cv = new ContentValues();

		cv.put(BlogPost.BODY, TEST_BODY_1);

		// First check to make sure validation happens.
		// title is required, so this should throw an exception.
		boolean exceptionThrown = false;
		try {
			cr.insert(BlogPost.CONTENT_URI, cv);
		}catch (final SQLException e){
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);

		// ok, now let's try with a title added:
		cv.put(BlogPost.TITLE, TEST_TITLE);

		final Uri test1 = cr.insert(BlogPost.CONTENT_URI, cv);

		assertNotNull(test1);

		// make sure that querying works
		c = cr.query(test1, null, null, null, null);

		assertNotNull(c);

		assertTrue(c.moveToFirst());

		assertEquals(TEST_BODY_1, c.getString(c.getColumnIndex(BlogPost.BODY)));

		assertFalse(c.isNull(c.getColumnIndex(BlogPost.CREATED_DATE)));
		final int createdDate = c.getInt(c.getColumnIndex(BlogPost.CREATED_DATE));
		assertTrue(createdDate > 0);

		// Update

		cv = new ContentValues();
		cv.put(BlogPost.BODY, TEST_BODY_1_MOD);
		int count = cr.update(test1, cv, null, null);

		assertEquals(1, count);

		// check to see that it's updated properly.
		c = cr.query(test1, null, null, null, null);

		assertNotNull(c);

		assertTrue(c.moveToFirst());

		assertEquals(TEST_BODY_1_MOD, c.getString(c.getColumnIndex(BlogPost.BODY)));
		assertEquals(createdDate, c.getInt(c.getColumnIndex(BlogPost.CREATED_DATE))); // this shouldn't update

		// Delete
		count = cr.delete(test1, null, null);

		assertEquals(1, count);

	}
}