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
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import edu.mit.mobile.android.content.ForeignKeyDBHelper;
import edu.mit.mobile.android.content.test.sample4.Person;

/**
 * Tests {@link ForeignKeyDBHelper} in the special case where there's only one table which points to
 * itself.
 * 
 */
public class SampleProvider4Test extends ProviderTestCase2<SampleProvider4> {

    //@formatter:off
    public static final String
        PERSON1_NAME = "Marco Dahlqvist",
        PERSON2_NAME = "Akira Roudiere",
        PERSON3_NAME = "Charles Crew",
        PERSON4_NAME = "Ernesto Nadir Crespo Keitel";
    //@formatter:on

    public SampleProvider4Test() {
        super(SampleProvider4.class, SampleProvider4.AUTHORITY);
    }

    public void testCRUD() {
        final MockContentResolver cr = getMockContentResolver();

        // person1 is a boss
        final Uri person1 = cr.insert(Person.CONTENT_URI, Person.toCv(PERSON1_NAME));

        assertNotNull(person1);

        assertPerson(person1, PERSON1_NAME);

        assertSupervisor(person1, null);

        // person2 is person1's subordinate
        // URI is something like /person/1/subordinate/2
        final Uri person2AsSubordinate = Person.SUBORDINATES.insert(cr, person1, Person.toCv(PERSON2_NAME));

        assertNotNull(person2AsSubordinate);

        assertPerson(person2AsSubordinate, PERSON2_NAME);

        assertSupervisor(person2AsSubordinate, person1);

        // try querying

        Cursor c = Person.SUBORDINATES.query(cr, person1, null);

        try {
            assertTrue(c.moveToFirst());
            assertEquals(1, c.getCount()); // only one subordinate

        } finally {
            c.close();
        }

        // the above URI
        final Uri person2 = ContentUris.withAppendedId(Person.CONTENT_URI,
                ContentUris.parseId(person2AsSubordinate));

        c = Person.SUBORDINATES.query(cr, person2, null);

        // no one reports to person2
        try {
            assertFalse(c.moveToFirst());
            assertEquals(0, c.getCount()); // no one!

        } finally {
            c.close();
        }

        // try to add the relationship after creation

        final Uri person3 = cr.insert(Person.CONTENT_URI, Person.toCv(PERSON3_NAME));

        assertNotNull(person3);

        assertPerson(person3, PERSON3_NAME);

        assertSupervisor(person3, null);

        final ContentValues cv = new ContentValues();

        // we know the ID of the person based on the URL. Perhaps it's not wise to inspect it like
        // that, but works fine for a test.
        cv.put(Person.SUPERVISOR, person1.getLastPathSegment());

        final int updated = cr.update(person3, cv, null, null);

        assertEquals(1, updated);

        assertSupervisor(person3, person1);

        // re-run the queries to ensure that person1's subordinates include person3.
        c = Person.SUBORDINATES.query(cr, person1, null);

        try {
            assertTrue(c.moveToFirst());

            assertEquals(2, c.getCount());

            final long person3Id = ContentUris.parseId(person3);
            final int idCol = c.getColumnIndex(Person._ID);
            final int nameCol = c.getColumnIndex(Person.NAME);
            boolean found = false;
            for (; !c.isAfterLast(); c.moveToNext()) {
                if (PERSON3_NAME.equals(c.getString(nameCol)) && person3Id == c.getLong(idCol)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);

        } finally {
            c.close();
        }
    }

    private void assertSupervisor(Uri uri, Uri supervisor) {
        final MockContentResolver cr = getMockContentResolver();

        final Cursor c = cr.query(uri, null, null, null, null);

        try {
            assertTrue(c.moveToFirst());

            final int supervisorCol = c.getColumnIndex(Person.SUPERVISOR);

            final String supervisorId = c.getString(supervisorCol);

            if (supervisor != null) {
                assertEquals(supervisor.getLastPathSegment(), supervisorId);
            } else {
                assertNull(supervisorId);
            }

        } finally {

            c.close();
        }
    }

    /**
     * Ensures that there is a person at uri who matches name. the uri can resolve to more than one
     *
     * @param uri
     * @param name
     * @return
     */
    private Cursor assertPerson(Uri uri, String name) {
        final MockContentResolver cr = getMockContentResolver();

        // first look through all the results to ensure that it matches.

        Cursor c = cr.query(uri, null, null, null, null);

        try {
            assertTrue(c.moveToFirst());

            final int nameCol = c.getColumnIndex(Person.NAME);

            boolean found = false;

            for (; !c.isAfterLast(); c.moveToNext()) {
                if (name.equals(c.getString(nameCol))) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        } finally {

            c.close();
        }

        // then try selecting it using a selection

        c = cr.query(uri, null, Person.NAME + "=?", new String[] { name }, null);

        try {
            assertTrue(c.moveToFirst());

            assertEquals(1, c.getCount());

            final int nameCol = c.getColumnIndex(Person.NAME);

            assertEquals(name, c.getString(nameCol));

        } finally {

            c.close();
        }

        return c;

    }
}