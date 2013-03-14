package edu.mit.mobile.android.content.test.query;

import java.io.IOException;
import java.util.HashMap;

import android.test.AndroidTestCase;
import edu.mit.mobile.android.content.AndroidVersions;
import edu.mit.mobile.android.content.SQLGenerationException;
import edu.mit.mobile.android.content.query.QuerystringParser;
import edu.mit.mobile.android.content.query.QuerystringParser.ParseException;

public class ParserTest extends AndroidTestCase {

    private static final String IS = AndroidVersions.SQLITE_SUPPORTS_IS_ISNOT ? "IS" : "=";
    private static final String IS_NOT = AndroidVersions.SQLITE_SUPPORTS_IS_ISNOT ? "IS NOT" : "!=";

    public void testParserBasic() throws IOException {
        final String query = "a=b";
        final QuerystringParser q = new QuerystringParser(query, null);

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
        assertEquals("\"a\" " + IS + " ?", sql);

        // longer keywords and reserved words
        sql = testParser("select=insert", new String[] { "insert" });
        assertEquals("\"select\" " + IS + " ?", sql);

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

        // all operators

        sql = testParser("a~=foo", new String[] { "%foo%" });
        assertEquals("\"a\" LIKE ?", sql);

        sql = testParser("a!~=foo", new String[] { "%foo%" });
        assertEquals("\"a\" NOT LIKE ?", sql);

        sql = testParser("a!=foo", new String[] { "foo" });
        assertEquals("\"a\" " + IS_NOT + " ?", sql);

        sql = testParser("a>1", new String[] { "1" });
        assertEquals("\"a\" > ?", sql);

        sql = testParser("a>=1", new String[] { "1" });
        assertEquals("\"a\" >= ?", sql);

        sql = testParser("a<1", new String[] { "1" });
        assertEquals("\"a\" < ?", sql);

        sql = testParser("a<=1", new String[] { "1" });
        assertEquals("\"a\" <= ?", sql);

        sql = testParser("(((a~=foo|b!=bar)))", new String[] { "%foo%", "bar" });
        assertEquals("(((\"a\" LIKE ? OR \"b\" " + IS_NOT + " ?)))", sql);

        // unicode
        sql = testParser("a=flambée&b=☃♥⃠☀", new String[] { "flambée", "☃♥⃠☀" });
        assertEquals(qIs("a") + " AND " + qIs("b"), sql);

        // symbols
        sql = testParser("a=__%283.14152+*+2%29", new String[] { "__(3.14152 * 2)" });
        assertEquals(qIs("a"), sql);
    }

    public void testColumnAlias() throws SQLGenerationException, ParseException, IOException {
        String sql;
        final HashMap<String, String> mapping = new HashMap<String, String>();
        mapping.put("abbv", "abbreviation");
        sql = testParser("abbv=1", new String[] { "1" }, mapping);

        assertEquals(qIs("abbreviation"), sql);

        mapping.put("short", "long.column");
        sql = testParser("short=yes", new String[] { "yes" }, mapping);

        assertEquals("\"long\".\"column\" " + IS + " ?", sql);
    }

    public void testFailMissingOperators() throws IOException {

        // missing operator
        testExpectFailure("a");
        testExpectFailure("a=1b=2");
        testExpectFailure("(a=1)b=2");
        testExpectFailure("a=1(b=2)");
        testExpectFailure("(a=1)(b=2)");
        testExpectFailure("()");
    }

    public void testFailMissingValue() throws IOException {
        // missing value
        testExpectFailure("a=");
        testExpectFailure("a==");

    }

    public void testFailMissingColumn() throws IOException {
        // missing column
        testExpectFailure("=a");
        testExpectFailure("==a");

    }

    public void testFailMissingParen() throws IOException {
        // missing paren
        testExpectFailure("(a=b");
        testExpectFailure("a=b)");
        testExpectFailure("(a=b))");

    }

    public void testFailTooMany() throws IOException {
        // too many
        testExpectFailure("a==b");
        testExpectFailure("a======b");
        testExpectFailure("a~~=b");

    }

    public void testFailSymbolsOutOfPlace() throws IOException {
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

    }

    public void testFailInvalidSymbols() throws IOException {
        // invalid symbols
        testExpectFailure("a='; drop students; //");
        testExpectFailure("a='");
        testExpectFailure("a=\"");

    }

    public void testFailBadColumnName() throws IOException {
        // bad column name
        testExpectFailure("a%b=c");

    }

    public void testFailWhitespace() throws IOException {
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
        return "\"" + params + "\" " + IS + " ?";
    }

    public String testParser(String query, String[] expectedParams) throws IOException,
            SQLGenerationException, ParseException {
        return testParser(query, expectedParams, null);
    }

    public String testParser(String query, String[] expectedParams,
            HashMap<String, String> columnMap) throws IOException,
            SQLGenerationException, ParseException {

        final QuerystringParser q = new QuerystringParser(query, columnMap);

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

        final QuerystringParser q = new QuerystringParser(query, null);
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
