package edu.mit.mobile.android.content.test;
import java.util.GregorianCalendar;

import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import edu.mit.mobile.android.content.test.sample3.Person;
import edu.mit.mobile.android.content.test.sample3.Project;


public class SampleProvider3Test extends ProviderTestCase2<SampleProvider3> {

	public static final String
		PERSON1_NAME = "Marco Dahlqvist",
		PERSON2_NAME = "Akira Roudiere",
		PERSON3_NAME = "Charles Crew",
		PERSON4_NAME = "Ernesto Nadir Crespo Keitel";

	public static final String
		PROJECT1_NAME = "Shufflestorm",
		PROJECT2_NAME = "Brightspot",
		PROJECT3_NAME = "Fivebean";

	public SampleProvider3Test() {
		super(SampleProvider3.class, SampleProvider3.AUTHORITY);

	}

	public void testCRUD(){
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

		// now try the reverse lookup
		final Cursor c = Person.PROJECTS.query(cr, person2, null);

		try {

			assertTrue(c.moveToFirst());

			// person2 is only on one project
			assertEquals(1, c.getCount());

			assertEquals(PROJECT1_NAME, c.getString(c.getColumnIndex(Project.NAME)));

		} finally {
			c.close();
		}

		// final Cursor c = cr.query(uri, projection, selection, selectionArgs, sortOrder)

		//final Uri person3 = cr.insert(Person.CONTENT_URI, Person.toCv(PERSON3_NAME));

		//Person.PROJECTS.insert(cr, parent, cv)
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
