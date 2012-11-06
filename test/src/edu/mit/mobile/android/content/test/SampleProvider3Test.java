package edu.mit.mobile.android.content.test;

import java.util.GregorianCalendar;
import java.util.HashSet;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import edu.mit.mobile.android.content.m2m.M2MDBHelper;
import edu.mit.mobile.android.content.m2m.M2MManager;
import edu.mit.mobile.android.content.m2m.M2MReverseHelper;
import edu.mit.mobile.android.content.test.sample3.Person;
import edu.mit.mobile.android.content.test.sample3.Project;

/**
 * Tests {@link M2MDBHelper}, {@link M2MManager}, and {@link M2MReverseHelper}
 *
 */
public class SampleProvider3Test extends ProviderTestCase2<SampleProvider3> {

    //@formatter:off
    public static final String
        PERSON1_NAME = "Marco Dahlqvist",
        PERSON2_NAME = "Akira Roudiere",
        PERSON3_NAME = "Charles Crew",
        PERSON4_NAME = "Ernesto Nadir Crespo Keitel";

    public static final String
        PROJECT1_NAME = "Shufflestorm",
        PROJECT2_NAME = "Brightspot",
        PROJECT3_NAME = "Fivebean";
    //@formatter:on
    public SampleProvider3Test() {
        super(SampleProvider3.class, SampleProvider3.AUTHORITY);

    }

    public void testCRUD() {
        final MockContentResolver cr = getMockContentResolver();

        final Uri project1 = cr.insert(Project.CONTENT_URI,
                Project.toCv(PROJECT1_NAME, new GregorianCalendar(2012, 01, 12).getTime()));

        assertNotNull(project1);

        final Uri project2 = cr.insert(Project.CONTENT_URI,
                Project.toCv(PROJECT2_NAME, new GregorianCalendar(2013, 12, 25).getTime()));

        assertNotNull(project2);

        // person1 is a loner and isn't on any projects
        final Uri person1 = cr.insert(Person.CONTENT_URI, Person.toCv(PERSON1_NAME));

        assertNotNull(person1);

        assertPerson(person1, PERSON1_NAME);

        // person2 is only on one project, that is project 1
        final Uri person2 = Project.PEOPLE.insert(cr, project1, Person.toCv(PERSON2_NAME));

        assertNotNull(person2);

        assertPerson(person2, PERSON2_NAME);

        assertPersonOnProject(person2, PROJECT1_NAME);

        // person 3 is on project 2
        final Uri person3 = Project.PEOPLE.insert(cr, project2, Person.toCv(PERSON3_NAME));

        assertPersonOnProject(person3, PROJECT2_NAME);

    }

    private void assertPersonOnProject(Uri person, String name) {
        // now try the reverse lookup
        final Cursor c = Person.PROJECTS.query(getMockContentResolver(), person, null);

        try {

            assertTrue(c.moveToFirst());

            // person2 is only on one project
            assertEquals(1, c.getCount());

            assertEquals(name, c.getString(c.getColumnIndex(Project.NAME)));

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

    public void testMimeTypes() {
        final ContentResolver cr = getMockContentResolver();

        final HashSet<String> typemap = new HashSet<String>();

        checkContentType(cr, typemap, Person.CONTENT_URI, false);

        checkContentType(cr, typemap, Project.CONTENT_URI, false);

        checkContentType(cr, typemap,
                Person.PROJECTS.getUri(ContentUris.withAppendedId(Person.CONTENT_URI, 1)), true);

        checkContentType(cr, typemap,
                Project.PEOPLE.getUri(ContentUris.withAppendedId(Project.CONTENT_URI, 1)), true);

        assertTypesEqual(cr, Person.CONTENT_URI,
                Project.PEOPLE.getUri(ContentUris.withAppendedId(Project.CONTENT_URI, 1)));

        assertTypesEqual(cr, Project.CONTENT_URI,
                Person.PROJECTS.getUri(ContentUris.withAppendedId(Person.CONTENT_URI, 1)));
    }

    private void assertTypesEqual(ContentResolver cr, Uri dir1, Uri dir2) {
        assertEquals(cr.getType(dir1), cr.getType(dir2));
        assertEquals(cr.getType(ContentUris.withAppendedId(dir1, 1)),
                cr.getType(ContentUris.withAppendedId(dir2, 1)));
    }

    private void checkContentType(ContentResolver cr, HashSet<String> typemap, Uri dir,
            boolean shouldExist) {
        final String typeD = cr.getType(dir);

        assertNotNull(typeD);

        assertEquals(shouldExist, typemap.contains(typeD));

        typemap.add(typeD);

        final String typeI = cr.getType(ContentUris.withAppendedId(dir, 1));

        assertNotNull(typeI);

        assertEquals(shouldExist, typemap.contains(typeI));

        typemap.add(typeI);
    }

}
