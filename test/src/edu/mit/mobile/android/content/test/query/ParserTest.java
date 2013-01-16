package edu.mit.mobile.android.content.test.query;

import java.io.IOException;

import android.test.AndroidTestCase;
import edu.mit.mobile.android.content.SQLGenerationException;
import edu.mit.mobile.android.content.query.QuerystringParser;
import edu.mit.mobile.android.content.query.QuerystringParser.ParseException;

public class ParserTest extends AndroidTestCase {

    public void testParserBasic() throws IOException {
        final String query = "a=b";
        final QuerystringParser q = new QuerystringParser(query);

        q.parse();

        assertEquals(qIs("a"), q.getResult());

        assertEquals(1, q.getSelectionArgs().length);
    }

    public void testParser() throws IOException {
        String sql;

        // empty is allowed
        sql = testParser("", new String[] {});
        assertEquals("", sql);

        // one parameter
        sql = testParser("a=b", new String[] { "b" });
        assertEquals("\"a\" IS ?", sql);

        // longer keywords and reserved words
        sql = testParser("select=insert", new String[] { "insert" });
        assertEquals("\"select\" IS ?", sql);

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

        sql = testParser("a=foo&(b=bar|c=baz)", new String[] { "foo", "bar", "baz" });
        assertEquals(qIs("a") + " AND (" + qIs("b") + " OR " + qIs("c") + ")", sql);

        sql = testParser("(a=foo)&(b=bar|c=baz)", new String[] { "foo", "bar", "baz" });
        assertEquals("(" + qIs("a") + ") AND (" + qIs("b") + " OR " + qIs("c") + ")", sql);

        sql = testParser("(a=foo|d=snth)&(b=bar|c=baz)",
                new String[] { "foo", "snth", "bar", "baz" });
        assertEquals("(" + qIs("a") + " OR " + qIs("d") + ") AND (" + qIs("b") + " OR " + qIs("c")
                + ")", sql);

        sql = testParser("a~=foo", new String[] { "%foo%" });
        assertEquals("\"a\" LIKE ?", sql);

        sql = testParser("a!~=foo", new String[] { "%foo%" });
        assertEquals("\"a\" NOT LIKE ?", sql);

        sql = testParser("a!=foo", new String[] { "foo" });
        assertEquals("\"a\" IS NOT ?", sql);

        sql = testParser("(((a~=foo|b!=bar)))", new String[] { "%foo%", "bar" });
        assertEquals("(((\"a\" LIKE ? OR \"b\" IS NOT ?)))", sql);

        // unicode
        sql = testParser("a=flambée&b=☃♥⃠☀", new String[] { "flambée", "☃♥⃠☀" });
        assertEquals(qIs("a") + " AND " + qIs("b"), sql);

        // symbols
        sql = testParser("a=__%283.14152+*+2%29", new String[] { "__(3.14152 * 2)" });
        assertEquals(qIs("a"), sql);
    }

    public void testFailures() throws IOException {

        // missing operator
        testExpectFailure("a");
        testExpectFailure("a=1b=2");
        testExpectFailure("(a=1)b=2");
        testExpectFailure("a=1(b=2)");
        testExpectFailure("(a=1)(b=2)");
        testExpectFailure("()");

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

        // symbols out of place
        testExpectFailure("a~b");
        testExpectFailure("a!b");
        testExpectFailure("a=1~b=2");
        testExpectFailure("a=1!b=2");
        testExpectFailure("!a=1");
        testExpectFailure("a=1!");
        testExpectFailure("a=!1");
        testExpectFailure("a=~1");

        testExpectFailure("a!!=b");
        testExpectFailure("a!!!!!=b");

        // invalid symbols
        testExpectFailure("a='; drop students; //");
        testExpectFailure("a='");
        testExpectFailure("a=\"");

        // bad column name
        testExpectFailure("a%b=c");

        // whitespace is invalid
        testExpectFailure("a=b c");
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

    public String testParser(String query, String[] expectedParams) throws IOException,
            SQLGenerationException, ParseException {

        final QuerystringParser q = new QuerystringParser(query);

        q.parse();

        final String sqlQuery = q.getResult();

        final String[] args = q.getSelectionArgs();

        assertEquals(expectedParams.length, args.length);

        for (int i = 0; i < expectedParams.length; i++) {
            assertEquals(expectedParams[i], args[i]);
        }

        return sqlQuery;
    }

    private void testExpectFailure(String query) throws IOException {

        final QuerystringParser q = new QuerystringParser(query);
        boolean thrown = false;
        boolean parsed = false;
        try {
            parsed = q.parse();
        } catch (final ParseException e) {
            thrown = true;
        } catch (final SQLGenerationException e) {
            thrown = true;
        }
        assertFalse("parser succeeded when it was expected to fail", parsed);

        assertTrue("exception not thrown", thrown);
    }
}
