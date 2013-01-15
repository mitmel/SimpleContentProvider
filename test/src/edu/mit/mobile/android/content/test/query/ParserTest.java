package edu.mit.mobile.android.content.test.query;

import java.io.IOException;

import android.test.AndroidTestCase;
import edu.mit.mobile.android.content.SQLGenerationException;
import edu.mit.mobile.android.content.query.QuerystringParser;

public class ParserTest extends AndroidTestCase {

    public void testParserBasic() throws IOException {
        final String query = "a=b";
        final QuerystringParser q = new QuerystringParser(query);

        q.parse();
        assertNull(q.getError());

        assertEquals(qIs("a"), q.getResult());

        assertEquals(1, q.getSelectionArgs().length);
    }

    public void testParser() throws IOException {
        String sql = testParser("a=b", new String[] { "b" });
        assertEquals("\"a\" IS ?", sql);

        // longer keywords and reserved words
        sql = testParser("select=insert", new String[] { "insert" });
        assertEquals("\"select\" IS ?", sql);

        sql = testParser("", new String[] {});
        assertEquals("", sql);

        sql = testParser("a=foo&b=bar", new String[] { "foo", "bar" });
        assertEquals(qIs("a") + " AND " + qIs("b"), sql);

        sql = testParser("a=foo|b=bar", new String[] { "foo", "bar" });
        assertEquals(qIs("a") + " OR " + qIs("b"), sql);

        sql = testParser("a=foo&b=bar&c=baz", new String[] { "foo", "bar", "baz" });
        assertEquals(qIs("a") + " AND " + qIs("b") + " AND " + qIs("c"), sql);

        sql = testParser("a=foo&b=bar|c=baz", new String[] { "foo", "bar", "baz" });
        assertEquals(qIs("a") + " AND " + qIs("b") + " OR " + qIs("c"), sql);

        sql = testParser("(a=foo|b=bar)&c=baz", new String[] { "foo", "bar", "baz" });
        assertEquals("(" + qIs("a") + " OR " + qIs("b") + ") AND " + qIs("c"), sql);

        sql = testParser("a~=foo", new String[] { "foo" });
        assertEquals("\"a\" LIKE ?", sql);

        sql = testParser("a!~=foo", new String[] { "foo" });
        assertEquals("\"a\" NOT LIKE ?", sql);

        sql = testParser("a!=foo", new String[] { "foo" });
        assertEquals("\"a\" NOT IS ?", sql);

        sql = testParser("(((a~=foo|b!=bar)))", new String[] { "foo", "bar" });
        assertEquals("(((\"a\" LIKE ? OR \"b\" NOT IS ?)))", sql);

        // unicode
        sql = testParser("a=flambée&b=☃♥⃠☀", new String[] { "flambée", "☃♥⃠☀" });
        assertEquals(qIs("a") + " AND " + qIs("b"), sql);

        // symbols
        sql = testParser("a=__3.14152+12%28", new String[] { "__3.14152+12%28" });
        assertEquals(qIs("a"), sql);
    }

    public void testFailures() throws IOException {
        // missing operator
        testExpectFailure("a");

        // missing value
        testExpectFailure("a=");
        testExpectFailure("a==");

        // missing column
        testExpectFailure("=a");
        testExpectFailure("==a");

        // missing paren
        testExpectFailure("(a=b");
        testExpectFailure("a=b)");
        testExpectFailure("(a=b))");

        // too many
        testExpectFailure("a==b");
        testExpectFailure("a======b");
        testExpectFailure("a~~=b");

        testExpectFailure("a!!=b");
        testExpectFailure("a!!!!!=b");

        // invalid symbols
        testExpectFailure("a='; drop students; //");

        // bad column name
        boolean thrown = false;
        try {
            testExpectFailure("a%b=c");
        } catch (final SQLGenerationException e) {
            thrown = true;
        }
        assertTrue(thrown);

    }

    /**
     * Quote the parameter to escape any SQL terms. Does not escape anything.
     *
     * @param params
     * @return
     */
    private String qIs(String params) {
        return "\"" + params + "\" IS ?";
    }

    public String testParser(String query, String[] expectedParams) throws IOException {

        final QuerystringParser q = new QuerystringParser(query);

        q.parse();
        assertNull("Parser returned an error: " + q.getError(), q.getError());

        final String sqlQuery = q.getResult();

        final String[] args = q.getSelectionArgs();

        assertEquals(expectedParams.length, args.length);

        for (int i = 0; i < expectedParams.length; i++) {
            assertEquals(expectedParams[i], args[i]);
        }

        return sqlQuery;
    }

    private void testExpectFailure(String query) throws IOException, SQLGenerationException {
        final QuerystringParser q = new QuerystringParser(query);

        q.parse();

        assertNotNull(q.getError());

        assertNull(q.getResult());

        assertNull(q.getSelectionArgs());
    }
}
