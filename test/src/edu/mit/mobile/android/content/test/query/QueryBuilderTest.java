package edu.mit.mobile.android.content.test.query;

import android.net.Uri;
import android.test.AndroidTestCase;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.query.QueryBuilder;

public class QueryBuilderTest extends AndroidTestCase {

    private final Uri mBaseUri = ProviderUtils
            .toContentUri("org.example.test.authority", "message");

    private final String mBaseUriString = "content://org.example.test.authority/message";

    private QueryBuilder qb;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        qb = new QueryBuilder(mBaseUri);
    }

    @Override
    protected void tearDown() throws Exception {

        super.tearDown();
        qb = null;
    }

    public void testQueryAndReset() {

        qb.is("a", "bar").andNotLike("b", "baz");

        assertBuilderEquals("a=bar&b!~=baz", qb);

        qb.reset();

        // check that reset works
        assertBuilderEquals(null, qb);
    }

    public void testQueryChild1() {
        // add in a child

        qb.isNot("a", "foo");

        qb.andChild(new QueryBuilder().is("a", "1").orIs("a", "2"));

        assertBuilderEquals("a!=foo&(a=1|a=2)", qb);
    }

    public void testQueryChild2() {

        qb.child(new QueryBuilder().isNot("a", "foo bar baz").orLike("a", "bar")).andIs("b",
                "escape & evade");

        assertBuilderEquals("(a!=foo+bar+baz|a~=bar)&b=escape+%26+evade", qb);
    }

    // ///////// full sweep /////////

    public void testQueryIs() {

        qb.is("a", "1").andIs("b", "2").orIs("c", "3");

        assertBuilderEquals("a=1&b=2|c=3", qb);
    }

    public void testQueryIsNot() {

        qb.isNot("a", "1").andIsNot("b", "2").orIsNot("c", "3");

        assertBuilderEquals("a!=1&b!=2|c!=3", qb);
    }

    public void testQueryLike() {

        qb.like("a", "1").andLike("b", "2").orLike("c", "3");

        assertBuilderEquals("a~=1&b~=2|c~=3", qb);
    }

    public void testQueryNotLike() {

        qb.notLike("a", "1").andNotLike("b", "2").orNotLike("c", "3");

        assertBuilderEquals("a!~=1&b!~=2|c!~=3", qb);
    }

    public void testQueryGreater() {

        qb.greater("a", "1").andGreater("b", "2").orGreater("c", "3");

        assertBuilderEquals("a>1&b>2|c>3", qb);
    }

    public void testQueryGreaterEqual() {

        qb.greaterEquals("a", "1").andGreaterEquals("b", "2").orGreaterEquals("c", "3");

        assertBuilderEquals("a>=1&b>=2|c>=3", qb);

    }

    public void testQueryLess() {

        qb.less("a", "1").andLess("b", "2").orLess("c", "3");

        assertBuilderEquals("a<1&b<2|c<3", qb);

    }

    public void testQueryLessEqual() {

        qb.lessEquals("a", "1").andLessEquals("b", "2").orLessEquals("c", "3");

        assertBuilderEquals("a<=1&b<=2|c<=3", qb);
    }

    private void assertBuilderEquals(String expectedQueryString, QueryBuilder qb) {
        assertEquals(mBaseUriString
                + (expectedQueryString != null ? "?" + expectedQueryString : ""), qb.build()
                .toString());
    }
}
