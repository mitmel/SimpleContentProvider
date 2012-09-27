package edu.mit.mobile.android.content.test;

import edu.mit.mobile.android.content.GenericDBHelper;
import edu.mit.mobile.android.content.SimpleContentProvider;
import edu.mit.mobile.android.content.m2m.M2MDBHelper;
import edu.mit.mobile.android.content.m2m.M2MReverseHelper;
import edu.mit.mobile.android.content.test.sample3.Person;
import edu.mit.mobile.android.content.test.sample3.Project;

public class SampleProvider3 extends SimpleContentProvider {

    public static final int DB_VER = 1;
    public static final String AUTHORITY = "edu.mit.mobile.android.content.test.sampleprovider3";

    public SampleProvider3() {
        super(AUTHORITY, DB_VER);

        final GenericDBHelper personHelper = new GenericDBHelper(Person.class);

        final GenericDBHelper projectHelper = new GenericDBHelper(Project.class);

        // projects having multiple people
        final M2MDBHelper projectPersonHelper = new M2MDBHelper(projectHelper, personHelper,
                Person.CONTENT_URI);

        // a helper than knows how to handle the reverse of another m2m helper
        // For example, this will let you look up people in a given project
        final M2MReverseHelper personProjectHelper = new M2MReverseHelper(projectPersonHelper);

        //
        // define the interface.
        //

        // /person/
        // /project/1/
        addDirAndItemUri(personHelper, Person.PATH);

        // /project/
        // /project/1/
        addDirAndItemUri(projectHelper, Project.PATH);

        // the list of all projects that a person is on
        addDirUri(personProjectHelper, Person.PATH + "/#/" + Project.PATH);

        // /project/1/person/
        // the list of all people on a project
        addDirUri(projectPersonHelper, Project.PATH + "/#/" + Person.PATH);
    }
}
