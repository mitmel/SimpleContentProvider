package edu.mit.mobile.android.content.test;

import edu.mit.mobile.android.content.ForeignKeyDBHelper;
import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.SimpleContentProvider;
import edu.mit.mobile.android.content.test.sample4.Person;

public class SampleProvider4 extends SimpleContentProvider {

    public static final int DB_VER = 1;
    public static final String AUTHORITY = "edu.mit.mobile.android.content.test.sampleprovider4";

    public SampleProvider4() {
        super(AUTHORITY, DB_VER);

        final GenericDBHelper personHelper = new GenericDBHelper(Person.class);

        final ForeignKeyDBHelper subordinateHelper = new ForeignKeyDBHelper(Person.class,
                Person.class, Person.SUPERVISOR);

        //
        // define the interface.
        //

        // /person/
        addDirAndItemUri(personHelper, Person.PATH);

        // /person/1/subordinates/
        addChildDirAndItemUri(subordinateHelper, Person.PATH, Person.SUBORDINATE_PATH);
    }
}
