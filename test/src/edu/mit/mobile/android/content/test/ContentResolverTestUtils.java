package edu.mit.mobile.android.content.test;

import java.util.Random;

import junit.framework.Assert;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

public class ContentResolverTestUtils {

    public static Cursor testQuery(ContentResolver cr, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, int expectedCount){
        final Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        ProviderTestCase2.assertNotNull(c);
        ProviderTestCase2.assertEquals(expectedCount, c.getCount());

        if (expectedCount == 0){
            ProviderTestCase2.assertFalse("expected moveToFirst to signal an empty cursor", c.moveToFirst());
        }else{
            ProviderTestCase2.assertTrue("expected moveToFirst to return true", c.moveToFirst());
        }

        return c;
    }

    private static final int BULK_INSERTS = 100;


    public static final String[] TEST_RAND = new String[]{
        "foo",
        "bar",
        "baz",
        "fnord"
    };

    private static final Random r = new Random();

    public static String getRandMessage(){
        return TEST_RAND[r.nextInt(TEST_RAND.length)];
    }

    public static <A extends ContentProvider> void testBulkInsert(ProviderTestCase2<A> tc, Uri dest, String field, ContentValues cvExtra){

        final MockContentResolver cr = tc.getMockContentResolver();

        final ContentValues[] cvs = new ContentValues[BULK_INSERTS];
        for (int i = 0; i < BULK_INSERTS; i++){
            final ContentValues cv = new ContentValues();

            cv.put(field, TEST_RAND[r.nextInt(TEST_RAND.length)]);

            if (cvExtra != null){
                cv.putAll(cvExtra);
            }

            cvs[i] = cv;
        }

        final int created = cr.bulkInsert(dest, cvs);

        Assert.assertEquals(BULK_INSERTS, created);

        ContentResolverTestUtils.testQuery(cr, dest, null, null, null, null, BULK_INSERTS).close();
    }

}
