package edu.mit.mobile.android.content.query;
/*
 * Copyright (C) 2011-2013 MIT Mobile Experience Lab
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, visit
 * http://www.gnu.org/licenses/lgpl.html
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.net.Uri;
import android.util.Log;
import edu.mit.mobile.android.content.QuerystringWrapper;

/**
 * <p>
 * A helper to build {@link QuerystringWrapper} queries.
 * </p>
 *
 * <p>
 * All column names and values are escaped using {@link URLEncoder#encode(String, String)}.
 * </p>
 *
 * @author <a href="mailto:spomeroy@mit.edu">Steve Pomeroy</a>
 *
 */
public class QueryBuilder {

    private static final String TAG = QueryBuilder.class.getSimpleName();

    private final Uri mUri;
    private StringBuilder mSb = new StringBuilder();

    private boolean mIsFirstParam = true;

    /**
     * Primary constructor. Provide a base URI and {@link #build()} will return a URI built off
     * that, with the query part replaced by this builder's content.
     *
     * @param baseUri
     *            the uri whose query string will be replaced
     */
    public QueryBuilder(Uri baseUri) {
        mUri = baseUri;
    }

    /**
     * Constructor used when constructing a child query. {@link #build()} will not work when this
     * constructor is used.
     */
    public QueryBuilder() {
        mUri = null;
    }

    public QueryBuilder is(String column, String value) {
        operParam(column, QuerystringWrapper.QUERY_OPERATOR_EQUALS, value);

        return this;
    }

    public QueryBuilder isNot(String column, String value) {
        operParam(column, QuerystringWrapper.QUERY_OPERATOR_NOT_EQUALS, value);

        return this;
    }

    public QueryBuilder like(String column, String value) {
        operParam(column, QuerystringWrapper.QUERY_OPERATOR_LIKE, value);

        return this;
    }

    public QueryBuilder notLike(String column, String value) {
        operParam(column, QuerystringWrapper.QUERY_OPERATOR_NOT_LIKE, value);

        return this;
    }

    public QueryBuilder greater(String column, String value) {
        operParam(column, QuerystringWrapper.QUERY_OPERATOR_GREATER_THAN, value);

        return this;
    }

    public QueryBuilder greaterEquals(String column, String value) {
        operParam(column, QuerystringWrapper.QUERY_OPERATOR_GREATER_THAN_EQUALS, value);

        return this;
    }

    public QueryBuilder less(String column, String value) {
        operParam(column, QuerystringWrapper.QUERY_OPERATOR_LESS_THAN, value);

        return this;
    }

    public QueryBuilder lessEquals(String column, String value) {
        operParam(column, QuerystringWrapper.QUERY_OPERATOR_LESS_THAN_EQUALS, value);

        return this;
    }

    // AND

    public QueryBuilder andIs(String column, String value) {

        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_AND, column,
                QuerystringWrapper.QUERY_OPERATOR_EQUALS, value);

        return this;
    }

    public QueryBuilder andIsNot(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_AND, column,
                QuerystringWrapper.QUERY_OPERATOR_NOT_EQUALS, value);
        return this;
    }

    public QueryBuilder andLike(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_AND, column,
                QuerystringWrapper.QUERY_OPERATOR_LIKE, value);

        return this;
    }

    public QueryBuilder andNotLike(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_AND, column,
                QuerystringWrapper.QUERY_OPERATOR_NOT_LIKE, value);

        return this;
    }

    public QueryBuilder andGreater(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_AND, column,
                QuerystringWrapper.QUERY_OPERATOR_GREATER_THAN, value);

        return this;
    }

    public QueryBuilder andGreaterEquals(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_AND, column,
                QuerystringWrapper.QUERY_OPERATOR_GREATER_THAN_EQUALS, value);

        return this;
    }

    public QueryBuilder andLess(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_AND, column,
                QuerystringWrapper.QUERY_OPERATOR_LESS_THAN, value);

        return this;
    }

    public QueryBuilder andLessEquals(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_AND, column,
                QuerystringWrapper.QUERY_OPERATOR_LESS_THAN_EQUALS, value);

        return this;
    }

    // OR

    public QueryBuilder orIs(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_OR, column,
                QuerystringWrapper.QUERY_OPERATOR_EQUALS, value);

        return this;
    }

    public QueryBuilder orIsNot(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_OR, column,
                QuerystringWrapper.QUERY_OPERATOR_NOT_EQUALS, value);

        return this;
    }

    public QueryBuilder orLike(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_OR, column,
                QuerystringWrapper.QUERY_OPERATOR_LIKE, value);

        return this;
    }

    public QueryBuilder orNotLike(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_OR, column,
                QuerystringWrapper.QUERY_OPERATOR_NOT_LIKE, value);

        return this;
    }

    public QueryBuilder orGreater(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_OR, column,
                QuerystringWrapper.QUERY_OPERATOR_GREATER_THAN, value);

        return this;
    }

    public QueryBuilder orGreaterEquals(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_OR, column,
                QuerystringWrapper.QUERY_OPERATOR_GREATER_THAN_EQUALS, value);

        return this;
    }

    public QueryBuilder orLess(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_OR, column,
                QuerystringWrapper.QUERY_OPERATOR_LESS_THAN, value);

        return this;
    }

    public QueryBuilder orLessEquals(String column, String value) {
        joinOperParam(QuerystringWrapper.QUERY_OPERATOR_OR, column,
                QuerystringWrapper.QUERY_OPERATOR_LESS_THAN_EQUALS, value);

        return this;
    }

    /**
     * Adds a child to the query (wrapped in parentheses). Use the {@link #QueryBuilder()}
     * constructor for these children.
     *
     * @param child
     * @return this QueryBuilder for chaining
     */
    public QueryBuilder child(QueryBuilder child) {
        checkFirstParam(true);

        mIsFirstParam = false;

        mSb.append('(');
        mSb.append(child.mSb);
        mSb.append(')');
        return this;
    }

    /**
     * Adds a child to the query (wrapped in parentheses), joined with the existing query by "and".
     * Use the {@link #QueryBuilder()} constructor for these children.
     *
     * @param child
     * @return this QueryBuilder for chaining
     */
    public QueryBuilder andChild(QueryBuilder child) {
        checkFirstParam(false);

        mSb.append(QuerystringWrapper.QUERY_OPERATOR_AND);
        mSb.append('(');
        mSb.append(child.mSb);
        mSb.append(')');
        return this;
    }

    /**
     * Adds a child to the query (wrapped in parentheses), joined with the existing query by "or".
     * Use the {@link #QueryBuilder()} constructor for these children.
     *
     * @param child
     * @return this QueryBuilder for chaining
     */
    public QueryBuilder orChild(QueryBuilder child) {
        checkFirstParam(false);

        mSb.append(QuerystringWrapper.QUERY_OPERATOR_OR);
        mSb.append('(');
        mSb.append(child.mSb);
        mSb.append(')');
        return this;
    }

    /**
     * @return the base URI with the query part set to the query assembled by this builder.
     */
    public Uri build() {
        if (mUri == null) {
            throw new IllegalStateException("no base URI specified");
        }

        return mUri.buildUpon().encodedQuery(mSb.toString()).build();
    }

    /**
     * Resets the built query back to its original state. This lets you reuse a builder which has
     * the same base URI.
     */
    public void reset() {
        mSb = new StringBuilder();
        mIsFirstParam = true;
    }

    private void checkFirstParam(boolean shouldBeFirst) {
        if (shouldBeFirst == mIsFirstParam) {
            return;
        } else if (shouldBeFirst) {
            throw new IllegalStateException("need to add a first parameter");
        } else {
            throw new IllegalStateException("already added the first parameter");
        }
    }

    private void operParam(String column, String oper, String value) {
        checkFirstParam(true);

        addEscapedValue(column, oper, value);

        mIsFirstParam = false;
    }

    private void joinOperParam(String join, String column, String oper, String value) {
        checkFirstParam(false);

        mSb.append(join);

        addEscapedValue(column, oper, value);
    }

    private void addEscapedValue(String column, String oper, String value) {
        try {
            mSb.append(URLEncoder.encode(column, "utf-8"));
            mSb.append(oper);
            mSb.append(URLEncoder.encode(value, "utf-8"));
        } catch (final UnsupportedEncodingException e) {
            Log.e(TAG, "error encoding", e);
        }
    }
}
