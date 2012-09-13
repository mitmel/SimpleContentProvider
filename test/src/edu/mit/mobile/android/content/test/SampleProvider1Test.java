package edu.mit.mobile.android.content.test;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.test.sample1.Message;

/**
 * Tests basic CRUD using a single {@link GenericDBHelper}.
 * 
 */
public class SampleProvider1Test extends ProviderTestCase2<SampleProvider1> {

    private static final String
        TEST_MESSAGE_1 = "test message 1",
        TEST_MESSAGE_1_MOD = "test message 1 modified";

    public SampleProvider1Test() {
        super(SampleProvider1.class, SampleProvider1.AUTHORITY);
    }

    /**
     * Test basic functionality with simple, known-good data.
     */
    public void testCRUD(){
        final MockContentResolver cr = getMockContentResolver();

        Cursor c = cr.query(Message.CONTENT_URI, null, null, null, null);

        assertNotNull(c);
        // the cursor (and DB) should be empty
        assertFalse(c.moveToFirst());

        c.close();

        ContentValues cv = new ContentValues();

        cv.put(Message.BODY, TEST_MESSAGE_1);

        final Uri test1 = cr.insert(Message.CONTENT_URI, cv);

        assertNotNull(test1);


        // make sure that querying works
        c = cr.query(test1, null, null, null, null);

        assertNotNull(c);

        assertTrue(c.moveToFirst());

        assertEquals(TEST_MESSAGE_1, c.getString(c.getColumnIndex(Message.BODY)));

        assertFalse(c.isNull(c.getColumnIndex(Message.CREATED_DATE)));
        final String createdTimestamp = c.getString(c.getColumnIndex(Message.CREATED_DATE));
        assertFalse("".equals(createdTimestamp));

        c.close();
        // Update

        cv = new ContentValues();
        cv.put(Message.BODY, TEST_MESSAGE_1_MOD);
        int count = cr.update(test1, cv, null, null);

        assertEquals(1, count);

        // check to see that it's updated properly.
        c = cr.query(test1, null, null, null, null);

        assertNotNull(c);

        assertTrue(c.moveToFirst());

        assertEquals(TEST_MESSAGE_1_MOD, c.getString(c.getColumnIndex(Message.BODY)));
        assertEquals(createdTimestamp, c.getString(c.getColumnIndex(Message.CREATED_DATE))); // this shouldn't update

        c.close();

        // Delete

        count = cr.delete(test1, null, null);

        assertEquals(1, count);

    }

    private static final int BULK_INSERTS = 100;

    public void testBulkInsert(){
        ContentResolverTestUtils.testBulkInsert(this, Message.CONTENT_URI, Message.BODY, null);
    }

    // this API was added in API level 5.
    public void testBatchActions() throws RemoteException, OperationApplicationException {
        final MockContentResolver cr = getMockContentResolver();

        final ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        for (int i = 0; i < BULK_INSERTS; i++){
            final Builder ins = ContentProviderOperation.newInsert(Message.CONTENT_URI);
            ins.withValue(Message.BODY, ContentResolverTestUtils.getRandMessage());
            ops.add(ins.build());
        }

        cr.applyBatch(SampleProvider1.AUTHORITY, ops);
    }
}