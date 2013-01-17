package edu.mit.mobile.android.content.test.query;

import android.net.Uri;
import android.test.AndroidTestCase;
import edu.mit.mobile.android.content.ProviderUtils;
import edu.mit.mobile.android.content.query.QueryBuilder;

public class QueryBuilderTest extends AndroidTestCase {

    private final Uri mBaseUri = ProviderUtils
            .toContentUri("org.example.test.authority", "message");

    private final String mBaseUriString = "content://org.example.test.authority/message";

    public void testQueryBuilder() {
        final QueryBuilder qb = new QueryBuilder(mBaseUri);

        qb.is("a", "bar").andNotLike("b", "baz");

        assertBuilderEquals("a=bar&b!~=baz", qb);

        qb.reset();

        // check that reset works
        assertBuilderEquals(null, qb);

        // add in a child

        qb.isNot("a", "foo");

        qb.andChild(new QueryBuilder().is("a", "1").orIs("a", "2"));

        assertBuilderEquals("a!=foo&(a=1|a=2)", qb);

        qb.reset();

        qb.child(new QueryBuilder().isNot("a", "foo bar baz").orLike("a", "bar")).andIs("b",
                "escape & evade");

        assertBuilderEquals("(a!=foo+bar+baz|a~=bar)&b=escape+%26+evade", qb);

    }

    private void assertBuilderEquals(String expectedQueryString, QueryBuilder qb) {
        assertEquals(mBaseUriString
                + (expectedQueryString != null ? "?" + expectedQueryString : ""), qb.build()
                .toString());
    }
}
