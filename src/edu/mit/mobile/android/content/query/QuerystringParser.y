/*
 * A parser to translate from querystring parameters to SQL queries.
 *
 * Copyright (C) 2012-2013 MIT Mobile Experience Lab
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version
 * 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

%language "Java"
%name-prefix "QuerystringParser"
%define parser_class_name "QuerystringParser"
%define public
/* all tokens will be used as strings. */
%define stype "String"
%define throws SQLGenerationException

/* this lets us avoid needing to instantiate the lexer in application code */
%lex-param {String query}

%{
package edu.mit.mobile.android.content.query;
%}

%code imports {

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.StringBuilder;
import java.util.LinkedList;

import edu.mit.mobile.android.content.SQLGenUtils;
import edu.mit.mobile.android.content.SQLGenerationException;


}

%code {

StringBuilder mSb = new StringBuilder();
LinkedList<String> mSelectionArgs = new LinkedList<String>();

public String getResult(){
    return ((YYLexer) yylexer).getError() == null ? mSb.toString() : null;
}

public String[] getSelectionArgs(){
	return ((YYLexer) yylexer).getError() == null ? mSelectionArgs.toArray(new String[mSelectionArgs.size()]) : null;
}

public void appendValidated(String key){
    if (!SQLGenUtils.isValidName(key)) {
        throw new SQLGenerationException("illegal column name in query: '" + key
                                                + "'");
    }
    mSb.append('"');
    mSb.append(key);
    mSb.append('"');
}

public String getError(){
    return ((YYLexer) yylexer).getError();
}

}

%code lexer {
        private final StreamTokenizer mTokenizer;
        private String mLVal;
        private String mError;

        public YYLexer(String str) {
            mTokenizer = new StreamTokenizer(new StringReader(str));
            mTokenizer.resetSyntax();
            mTokenizer.eolIsSignificant(false);
            mTokenizer.whitespaceChars(0, 0x20);
            mTokenizer.lowerCaseMode(false);

            // standard words, no symbols.
            mTokenizer.wordChars('A', 'Z');
            mTokenizer.wordChars('a', 'z');
            mTokenizer.wordChars('a', 'z');
            mTokenizer.wordChars('0', '9');
            mTokenizer.wordChars('%', '%');
            mTokenizer.wordChars('.', '.');
            mTokenizer.wordChars('-', '-');
            mTokenizer.wordChars('*', '*');
            mTokenizer.wordChars('_', '_');
            mTokenizer.wordChars('+', '+');
            mTokenizer.wordChars('\u00A0', '\u00FF');
        }

        @Override
        public String getLVal() {
            return mLVal;
        }

        @Override
        public int yylex() throws IOException {
            final int ttype = mTokenizer.nextToken();
            switch (ttype){
                case StreamTokenizer.TT_WORD:
                    mLVal = mTokenizer.sval;
                    return QuerystringParser.STR;

                case StreamTokenizer.TT_EOF:
                    return QuerystringParser.EOF;

                default:
                    return mTokenizer.ttype;
            }
        }

        public String getError() {
            return (mError == null) ? null : mError + " near " + mLVal;
        }

        @Override
        public void yyerror(String s) {
            mError = s;
        }
}

%token <String> STR "string"
%type <String> key
%type <String> value

%%
query: /* empty */ | query params

open_paren: '(' { mSb.append('('); }
close_paren: ')' { mSb.append(')'); }

join: '&' { mSb.append(" AND "); } | '|' { mSb.append(" OR "); }

params: param | params join params | open_paren params close_paren

param: key not oper value

oper: '=' { mSb.append(" IS ?"); } | '~' '=' { mSb.append(" LIKE ?"); }

not: /* empty */ | '!' { mSb.append(" NOT"); }

key: STR { appendValidated($1); }

value: STR { mSelectionArgs.add($1); }

%%

