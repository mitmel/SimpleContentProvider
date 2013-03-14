/* A Bison parser, made by GNU Bison 2.5.  */

/* Skeleton implementation for Bison LALR(1) parsers in Java
   
      Copyright (C) 2007-2011 Free Software Foundation, Inc.
   
   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.
   
   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

package edu.mit.mobile.android.content.query;
/* First part of user declarations.  */

/* "%code imports" blocks.  */

/* Line 33 of lalr1.java  */
/* Line 37 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */


import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.StringBuilder;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;

import edu.mit.mobile.android.content.AndroidVersions;
import edu.mit.mobile.android.content.SQLGenUtils;
import edu.mit.mobile.android.content.SQLGenerationException;




/* Line 33 of lalr1.java  */
/* Line 60 of "src/edu/mit/mobile/android/content/query/QuerystringParser.java"  */

/**
 * A Bison parser, automatically generated from <tt>src/edu/mit/mobile/android/content/query/QuerystringParser.y</tt>.
 *
 * @author LALR (1) parser skeleton written by Paolo Bonzini.
 */
public class QuerystringParser
{
    /** Version number for the Bison executable that generated this parser.  */
  public static final String bisonVersion = "2.5";

  /** Name of the skeleton that generated this parser.  */
  public static final String bisonSkeleton = "lalr1.java";


  /** True if verbose error messages are enabled.  */
  public boolean errorVerbose = false;



  /** Token returned by the scanner to signal the end of its input.  */
  public static final int EOF = 0;

/* Tokens.  */
  /** Token number, to be returned by the scanner.  */
  public static final int STR = 258;



  

  /**
   * Communication interface between the scanner and the Bison-generated
   * parser <tt>QuerystringParser</tt>.
   */
  public interface Lexer {
    

    /**
     * Method to retrieve the semantic value of the last scanned token.
     * @return the semantic value of the last scanned token.  */
    String getLVal ();

    /**
     * Entry point for the scanner.  Returns the token identifier corresponding
     * to the next token and prepares to return the semantic value
     * of the token.
     * @return the token identifier corresponding to the next token. */
    int yylex () throws java.io.IOException;

    /**
     * Entry point for error reporting.  Emits an error
     * in a user-defined way.
     *
     * 
     * @param s The string for the error message.  */
     void yyerror (String s);
  }

  private class YYLexer implements Lexer {
/* "%code lexer" blocks.  */

/* Line 147 of lalr1.java  */
/* Line 106 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */

        private final StreamTokenizer mTokenizer;
        private String mLVal;

        public YYLexer(String str) {
            mTokenizer = new StreamTokenizer(new StringReader(str));
            mTokenizer.resetSyntax();
            mTokenizer.eolIsSignificant(false);
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

        @Override
        public void yyerror(String s) throws ParseException {
            throw new ParseException(s + " near '" + mLVal + "'");
        }



/* Line 147 of lalr1.java  */
/* Line 178 of "src/edu/mit/mobile/android/content/query/QuerystringParser.java"  */

  }

  /** The object doing lexical analysis for us.  */
  private Lexer yylexer;
  
  
    /* User arguments.  */
    protected final HashMap<String, String> columnAliases;


  /**
   * Instantiates the Bison-generated parser.
   */
  public QuerystringParser (String query, HashMap<String, String> columnAliases) {
    this.yylexer = new YYLexer(query);
    this.columnAliases = columnAliases;
	  
  }


  /**
   * Instantiates the Bison-generated parser.
   * @param yylexer The scanner that will supply tokens to the parser.
   */
  protected QuerystringParser (Lexer yylexer, HashMap<String, String> columnAliases) {
    this.yylexer = yylexer;
    this.columnAliases = columnAliases;
	  
  }

  private java.io.PrintStream yyDebugStream = System.err;

  /**
   * Return the <tt>PrintStream</tt> on which the debugging output is
   * printed.
   */
  public final java.io.PrintStream getDebugStream () { return yyDebugStream; }

  /**
   * Set the <tt>PrintStream</tt> on which the debug output is printed.
   * @param s The stream that is used for debugging output.
   */
  public final void setDebugStream(java.io.PrintStream s) { yyDebugStream = s; }

  private int yydebug = 0;

  /**
   * Answer the verbosity of the debugging output; 0 means that all kinds of
   * output from the parser are suppressed.
   */
  public final int getDebugLevel() { return yydebug; }

  /**
   * Set the verbosity of the debugging output; 0 means that all kinds of
   * output from the parser are suppressed.
   * @param level The verbosity level for debugging output.
   */
  public final void setDebugLevel(int level) { yydebug = level; }

  private final int yylex () throws java.io.IOException {
    return yylexer.yylex ();
  }
  protected final void yyerror (String s) {
    yylexer.yyerror (s);
  }

  

  protected final void yycdebug (String s) {
    if (yydebug > 0)
      yyDebugStream.println (s);
  }

  private final class YYStack {
    private int[] stateStack = new int[16];
    
    private String[] valueStack = new String[16];

    public int size = 16;
    public int height = -1;

    public final void push (int state, String value			    ) {
      height++;
      if (size == height)
        {
	  int[] newStateStack = new int[size * 2];
	  System.arraycopy (stateStack, 0, newStateStack, 0, height);
	  stateStack = newStateStack;
	  

	  String[] newValueStack = new String[size * 2];
	  System.arraycopy (valueStack, 0, newValueStack, 0, height);
	  valueStack = newValueStack;

	  size *= 2;
	}

      stateStack[height] = state;
      
      valueStack[height] = value;
    }

    public final void pop () {
      pop (1);
    }

    public final void pop (int num) {
      // Avoid memory leaks... garbage collection is a white lie!
      if (num > 0) {
	java.util.Arrays.fill (valueStack, height - num + 1, height + 1, null);
        
      }
      height -= num;
    }

    public final int stateAt (int i) {
      return stateStack[height - i];
    }

    public final String valueAt (int i) {
      return valueStack[height - i];
    }

    // Print the state stack on the debug stream.
    public void print (java.io.PrintStream out)
    {
      out.print ("Stack now");

      for (int i = 0; i <= height; i++)
        {
	  out.print (' ');
	  out.print (stateStack[i]);
        }
      out.println ();
    }
  }

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return success (<tt>true</tt>).  */
  public static final int YYACCEPT = 0;

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return failure (<tt>false</tt>).  */
  public static final int YYABORT = 1;

  /**
   * Returned by a Bison action in order to start error recovery without
   * printing an error message.  */
  public static final int YYERROR = 2;

  // Internal return codes that are not supported for user semantic
  // actions.
  private static final int YYERRLAB = 3;
  private static final int YYNEWSTATE = 4;
  private static final int YYDEFAULT = 5;
  private static final int YYREDUCE = 6;
  private static final int YYERRLAB1 = 7;
  private static final int YYRETURN = 8;

  private int yyerrstatus_ = 0;

  /**
   * Return whether error recovery is being done.  In this state, the parser
   * reads token until it reaches a known state, and then restarts normal
   * operation.  */
  public final boolean recovering ()
  {
    return yyerrstatus_ == 0;
  }

  private int yyaction (int yyn, YYStack yystack, int yylen) throws SQLGenerationException
  {
    String yyval;
    

    /* If YYLEN is nonzero, implement the default value of the action:
       `$$ = $1'.  Otherwise, use the top of the stack.

       Otherwise, the following line sets YYVAL to garbage.
       This behavior is undocumented and Bison
       users should not rely upon it.  */
    if (yylen > 0)
      yyval = yystack.valueAt (yylen - 1);
    else
      yyval = yystack.valueAt (0);

    yy_reduce_print (yyn, yystack);

    switch (yyn)
      {
	  case 8:
  if (yyn == 8)
    
/* Line 351 of lalr1.java  */
/* Line 172 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { mSb.append('('); };
  break;
    

  case 9:
  if (yyn == 9)
    
/* Line 351 of lalr1.java  */
/* Line 173 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { mSb.append(')'); };
  break;
    

  case 10:
  if (yyn == 10)
    
/* Line 351 of lalr1.java  */
/* Line 175 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { mSb.append(" AND "); };
  break;
    

  case 11:
  if (yyn == 11)
    
/* Line 351 of lalr1.java  */
/* Line 176 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { mSb.append(" OR "); };
  break;
    

  case 20:
  if (yyn == 20)
    
/* Line 351 of lalr1.java  */
/* Line 189 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { mSb.append(AndroidVersions.SQLITE_SUPPORTS_IS_ISNOT ? " IS NOT ?" : " != ?"); };
  break;
    

  case 21:
  if (yyn == 21)
    
/* Line 351 of lalr1.java  */
/* Line 191 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { mSb.append(AndroidVersions.SQLITE_SUPPORTS_IS_ISNOT ? " IS ?" : " = ?"); };
  break;
    

  case 22:
  if (yyn == 22)
    
/* Line 351 of lalr1.java  */
/* Line 193 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { mSb.append(" > ?"); };
  break;
    

  case 23:
  if (yyn == 23)
    
/* Line 351 of lalr1.java  */
/* Line 194 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { mSb.append(" >= ?"); };
  break;
    

  case 24:
  if (yyn == 24)
    
/* Line 351 of lalr1.java  */
/* Line 195 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { mSb.append(" < ?"); };
  break;
    

  case 25:
  if (yyn == 25)
    
/* Line 351 of lalr1.java  */
/* Line 196 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { mSb.append(" <= ?"); };
  break;
    

  case 26:
  if (yyn == 26)
    
/* Line 351 of lalr1.java  */
/* Line 198 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { mSb.append(" LIKE ?"); };
  break;
    

  case 28:
  if (yyn == 28)
    
/* Line 351 of lalr1.java  */
/* Line 201 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { mSb.append(" NOT"); };
  break;
    

  case 29:
  if (yyn == 29)
    
/* Line 351 of lalr1.java  */
/* Line 203 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    { appendColumnName(((String)(yystack.valueAt (1-(1))))); };
  break;
    

  case 30:
  if (yyn == 30)
    
/* Line 351 of lalr1.java  */
/* Line 205 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    {
    try {
        mSelectionArgs.add(URLDecoder.decode(((String)(yystack.valueAt (1-(1)))), "utf-8"));
     }catch(UnsupportedEncodingException e){
         SQLGenerationException sqle = new SQLGenerationException();
         sqle.initCause(e);
         throw sqle;
     }
};
  break;
    

  case 31:
  if (yyn == 31)
    
/* Line 351 of lalr1.java  */
/* Line 215 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */
    {
    try {
        mSelectionArgs.add('%' + URLDecoder.decode(((String)(yystack.valueAt (1-(1)))), "utf-8") + '%');
     }catch(UnsupportedEncodingException e){
         SQLGenerationException sqle = new SQLGenerationException();
         sqle.initCause(e);
         throw sqle;
     }
};
  break;
    



/* Line 351 of lalr1.java  */
/* Line 526 of "src/edu/mit/mobile/android/content/query/QuerystringParser.java"  */
	default: break;
      }

    yy_symbol_print ("-> $$ =", yyr1_[yyn], yyval);

    yystack.pop (yylen);
    yylen = 0;

    /* Shift the result of the reduction.  */
    yyn = yyr1_[yyn];
    int yystate = yypgoto_[yyn - yyntokens_] + yystack.stateAt (0);
    if (0 <= yystate && yystate <= yylast_
	&& yycheck_[yystate] == yystack.stateAt (0))
      yystate = yytable_[yystate];
    else
      yystate = yydefgoto_[yyn - yyntokens_];

    yystack.push (yystate, yyval);
    return YYNEWSTATE;
  }

  /* Return YYSTR after stripping away unnecessary quotes and
     backslashes, so that it's suitable for yyerror.  The heuristic is
     that double-quoting is unnecessary unless the string contains an
     apostrophe, a comma, or backslash (other than backslash-backslash).
     YYSTR is taken from yytname.  */
  private final String yytnamerr_ (String yystr)
  {
    if (yystr.charAt (0) == '"')
      {
        StringBuffer yyr = new StringBuffer ();
        strip_quotes: for (int i = 1; i < yystr.length (); i++)
          switch (yystr.charAt (i))
            {
            case '\'':
            case ',':
              break strip_quotes;

            case '\\':
	      if (yystr.charAt(++i) != '\\')
                break strip_quotes;
              /* Fall through.  */
            default:
              yyr.append (yystr.charAt (i));
              break;

            case '"':
              return yyr.toString ();
            }
      }
    else if (yystr.equals ("$end"))
      return "end of input";

    return yystr;
  }

  /*--------------------------------.
  | Print this symbol on YYOUTPUT.  |
  `--------------------------------*/

  private void yy_symbol_print (String s, int yytype,
			         String yyvaluep				 )
  {
    if (yydebug > 0)
    yycdebug (s + (yytype < yyntokens_ ? " token " : " nterm ")
	      + yytname_[yytype] + " ("
	      + (yyvaluep == null ? "(null)" : yyvaluep.toString ()) + ")");
  }

  /**
   * Parse input from the scanner that was specified at object construction
   * time.  Return whether the end of the input was reached successfully.
   *
   * @return <tt>true</tt> if the parsing succeeds.  Note that this does not
   *          imply that there were no syntax errors.
   */
  public boolean parse () throws java.io.IOException, SQLGenerationException
  {
    /// Lookahead and lookahead in internal form.
    int yychar = yyempty_;
    int yytoken = 0;

    /* State.  */
    int yyn = 0;
    int yylen = 0;
    int yystate = 0;

    YYStack yystack = new YYStack ();

    /* Error handling.  */
    int yynerrs_ = 0;
    

    /// Semantic value of the lookahead.
    String yylval = null;

    int yyresult;

    yycdebug ("Starting parse\n");
    yyerrstatus_ = 0;


    /* Initialize the stack.  */
    yystack.push (yystate, yylval);

    int label = YYNEWSTATE;
    for (;;)
      switch (label)
      {
        /* New state.  Unlike in the C/C++ skeletons, the state is already
	   pushed when we come here.  */
      case YYNEWSTATE:
        yycdebug ("Entering state " + yystate + "\n");
        if (yydebug > 0)
          yystack.print (yyDebugStream);

        /* Accept?  */
        if (yystate == yyfinal_)
          return true;

        /* Take a decision.  First try without lookahead.  */
        yyn = yypact_[yystate];
        if (yy_pact_value_is_default_ (yyn))
          {
            label = YYDEFAULT;
	    break;
          }

        /* Read a lookahead token.  */
        if (yychar == yyempty_)
          {
	    yycdebug ("Reading a token: ");
	    yychar = yylex ();
            
            yylval = yylexer.getLVal ();
          }

        /* Convert token to internal form.  */
        if (yychar <= EOF)
          {
	    yychar = yytoken = EOF;
	    yycdebug ("Now at end of input.\n");
          }
        else
          {
	    yytoken = yytranslate_ (yychar);
	    yy_symbol_print ("Next token is", yytoken,
			     yylval);
          }

        /* If the proper action on seeing token YYTOKEN is to reduce or to
           detect an error, take that action.  */
        yyn += yytoken;
        if (yyn < 0 || yylast_ < yyn || yycheck_[yyn] != yytoken)
          label = YYDEFAULT;

        /* <= 0 means reduce or error.  */
        else if ((yyn = yytable_[yyn]) <= 0)
          {
	    if (yy_table_value_is_error_ (yyn))
	      label = YYERRLAB;
	    else
	      {
	        yyn = -yyn;
	        label = YYREDUCE;
	      }
          }

        else
          {
            /* Shift the lookahead token.  */
	    yy_symbol_print ("Shifting", yytoken,
			     yylval);

            /* Discard the token being shifted.  */
            yychar = yyempty_;

            /* Count tokens shifted since error; after three, turn off error
               status.  */
            if (yyerrstatus_ > 0)
              --yyerrstatus_;

            yystate = yyn;
            yystack.push (yystate, yylval);
            label = YYNEWSTATE;
          }
        break;

      /*-----------------------------------------------------------.
      | yydefault -- do the default action for the current state.  |
      `-----------------------------------------------------------*/
      case YYDEFAULT:
        yyn = yydefact_[yystate];
        if (yyn == 0)
          label = YYERRLAB;
        else
          label = YYREDUCE;
        break;

      /*-----------------------------.
      | yyreduce -- Do a reduction.  |
      `-----------------------------*/
      case YYREDUCE:
        yylen = yyr2_[yyn];
        label = yyaction (yyn, yystack, yylen);
	yystate = yystack.stateAt (0);
        break;

      /*------------------------------------.
      | yyerrlab -- here on detecting error |
      `------------------------------------*/
      case YYERRLAB:
        /* If not already recovering from an error, report this error.  */
        if (yyerrstatus_ == 0)
          {
            ++yynerrs_;
            if (yychar == yyempty_)
              yytoken = yyempty_;
            yyerror (yysyntax_error (yystate, yytoken));
          }

        
        if (yyerrstatus_ == 3)
          {
	    /* If just tried and failed to reuse lookahead token after an
	     error, discard it.  */

	    if (yychar <= EOF)
	      {
	      /* Return failure if at end of input.  */
	      if (yychar == EOF)
	        return false;
	      }
	    else
	      yychar = yyempty_;
          }

        /* Else will try to reuse lookahead token after shifting the error
           token.  */
        label = YYERRLAB1;
        break;

      /*---------------------------------------------------.
      | errorlab -- error raised explicitly by YYERROR.  |
      `---------------------------------------------------*/
      case YYERROR:

        
        /* Do not reclaim the symbols of the rule which action triggered
           this YYERROR.  */
        yystack.pop (yylen);
        yylen = 0;
        yystate = yystack.stateAt (0);
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------------------.
      | yyerrlab1 -- common code for both syntax error and YYERROR.  |
      `-------------------------------------------------------------*/
      case YYERRLAB1:
        yyerrstatus_ = 3;	/* Each real token shifted decrements this.  */

        for (;;)
          {
	    yyn = yypact_[yystate];
	    if (!yy_pact_value_is_default_ (yyn))
	      {
	        yyn += yyterror_;
	        if (0 <= yyn && yyn <= yylast_ && yycheck_[yyn] == yyterror_)
	          {
	            yyn = yytable_[yyn];
	            if (0 < yyn)
		      break;
	          }
	      }

	    /* Pop the current state because it cannot handle the error token.  */
	    if (yystack.height == 1)
	      return false;

	    
	    yystack.pop ();
	    yystate = yystack.stateAt (0);
	    if (yydebug > 0)
	      yystack.print (yyDebugStream);
          }

	

        /* Shift the error token.  */
        yy_symbol_print ("Shifting", yystos_[yyn],
			 yylval);

        yystate = yyn;
	yystack.push (yyn, yylval);
        label = YYNEWSTATE;
        break;

        /* Accept.  */
      case YYACCEPT:
        return true;

        /* Abort.  */
      case YYABORT:
        return false;
      }
  }

  // Generate an error message.
  private String yysyntax_error (int yystate, int tok)
  {
    if (errorVerbose)
      {
        /* There are many possibilities here to consider:
           - Assume YYFAIL is not used.  It's too flawed to consider.
             See
             <http://lists.gnu.org/archive/html/bison-patches/2009-12/msg00024.html>
             for details.  YYERROR is fine as it does not invoke this
             function.
           - If this state is a consistent state with a default action,
             then the only way this function was invoked is if the
             default action is an error action.  In that case, don't
             check for expected tokens because there are none.
           - The only way there can be no lookahead present (in tok) is
             if this state is a consistent state with a default action.
             Thus, detecting the absence of a lookahead is sufficient to
             determine that there is no unexpected or expected token to
             report.  In that case, just report a simple "syntax error".
           - Don't assume there isn't a lookahead just because this
             state is a consistent state with a default action.  There
             might have been a previous inconsistent state, consistent
             state with a non-default action, or user semantic action
             that manipulated yychar.  (However, yychar is currently out
             of scope during semantic actions.)
           - Of course, the expected token list depends on states to
             have correct lookahead information, and it depends on the
             parser not to perform extra reductions after fetching a
             lookahead from the scanner and before detecting a syntax
             error.  Thus, state merging (from LALR or IELR) and default
             reductions corrupt the expected token list.  However, the
             list is correct for canonical LR with one exception: it
             will still contain any token that will not be accepted due
             to an error action in a later state.
        */
        if (tok != yyempty_)
          {
            // FIXME: This method of building the message is not compatible
            // with internationalization.
            StringBuffer res =
              new StringBuffer ("syntax error, unexpected ");
            res.append (yytnamerr_ (yytname_[tok]));
            int yyn = yypact_[yystate];
            if (!yy_pact_value_is_default_ (yyn))
              {
                /* Start YYX at -YYN if negative to avoid negative
                   indexes in YYCHECK.  In other words, skip the first
                   -YYN actions for this state because they are default
                   actions.  */
                int yyxbegin = yyn < 0 ? -yyn : 0;
                /* Stay within bounds of both yycheck and yytname.  */
                int yychecklim = yylast_ - yyn + 1;
                int yyxend = yychecklim < yyntokens_ ? yychecklim : yyntokens_;
                int count = 0;
                for (int x = yyxbegin; x < yyxend; ++x)
                  if (yycheck_[x + yyn] == x && x != yyterror_
                      && !yy_table_value_is_error_ (yytable_[x + yyn]))
                    ++count;
                if (count < 5)
                  {
                    count = 0;
                    for (int x = yyxbegin; x < yyxend; ++x)
                      if (yycheck_[x + yyn] == x && x != yyterror_
                          && !yy_table_value_is_error_ (yytable_[x + yyn]))
                        {
                          res.append (count++ == 0 ? ", expecting " : " or ");
                          res.append (yytnamerr_ (yytname_[x]));
                        }
                  }
              }
            return res.toString ();
          }
      }

    return "syntax error";
  }

  /**
   * Whether the given <code>yypact_</code> value indicates a defaulted state.
   * @param yyvalue   the value to check
   */
  private static boolean yy_pact_value_is_default_ (int yyvalue)
  {
    return yyvalue == yypact_ninf_;
  }

  /**
   * Whether the given <code>yytable_</code> value indicates a syntax error.
   * @param yyvalue   the value to check
   */
  private static boolean yy_table_value_is_error_ (int yyvalue)
  {
    return yyvalue == yytable_ninf_;
  }

  /* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
     STATE-NUM.  */
  private static final byte yypact_ninf_ = -16;
  private static final byte yypact_[] =
  {
         5,   -16,   -16,    12,     4,     5,   -16,    -7,   -16,   -16,
     -16,     5,     0,     6,   -16,     7,     8,    10,   -16,   -16,
     -16,   -16,   -16,   -16,     2,     5,   -16,   -16,   -16,   -16,
     -16,   -16,   -16,   -16,     9,    16,     0,   -16,   -16,   -16,
     -16
  };

  /* YYDEFACT[S] -- default reduction number in state S.  Performed when
     YYTABLE doesn't specify something else to do.  Zero means the
     default is an error.  */
  private static final byte yydefact_[] =
  {
         2,    29,     8,     0,     3,     0,     4,    27,     1,    10,
      11,     0,     0,    28,    21,    22,    24,     0,    15,    14,
      16,    17,    18,    19,     0,     0,     5,     9,     7,    20,
      23,    25,    30,    12,     0,     0,     0,    26,    31,    13,
       6
  };

  /* YYPGOTO[NTERM-NUM].  */
  private static final byte yypgoto_[] =
  {
       -16,   -16,    -5,    11,   -15,   -16,    13,   -16,   -16,   -16,
     -16,   -16,   -16,   -16,   -16,   -16,   -16,   -16,   -16
  };

  /* YYDEFGOTO[NTERM-NUM].  */
  private static final byte
  yydefgoto_[] =
  {
        -1,     3,     4,     5,    28,    11,     6,    17,    18,    19,
      20,    21,    22,    23,    35,    24,     7,    33,    39
  };

  /* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
     positive, shift that token.  If negative, reduce the rule which
     number is the opposite.  If YYTABLE_NINF_, syntax error.  */
  private static final byte yytable_ninf_ = -1;
  private static final byte
  yytable_[] =
  {
        12,    13,    14,    15,    16,    27,     9,    10,     1,     2,
       9,    10,     8,    32,    34,    29,    30,    31,    37,    38,
      36,    40,    25,     0,    26
  };

  /* YYCHECK.  */
  private static final byte
  yycheck_[] =
  {
         5,     8,     9,    10,    11,     5,     6,     7,     3,     4,
       6,     7,     0,     3,    12,     9,     9,     9,     9,     3,
      25,    36,    11,    -1,    11
  };

  /* STOS_[STATE-NUM] -- The (internal number of the) accessing
     symbol of state STATE-NUM.  */
  private static final byte
  yystos_[] =
  {
         0,     3,     4,    14,    15,    16,    19,    29,     0,     6,
       7,    18,    15,     8,     9,    10,    11,    20,    21,    22,
      23,    24,    25,    26,    28,    16,    19,     5,    17,     9,
       9,     9,     3,    30,    12,    27,    15,     9,     3,    31,
      17
  };

  /* TOKEN_NUMBER_[YYLEX-NUM] -- Internal symbol number corresponding
     to YYLEX-NUM.  */
  private static final short
  yytoken_number_[] =
  {
         0,   256,   257,   258,    40,    41,    38,   124,    33,    61,
      62,    60,   126
  };

  /* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  private static final byte
  yyr1_[] =
  {
         0,    13,    14,    14,    15,    15,    15,    15,    16,    17,
      18,    18,    19,    19,    20,    20,    20,    20,    20,    20,
      21,    22,    23,    24,    25,    26,    27,    28,    28,    29,
      30,    31
  };

  /* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
  private static final byte
  yyr2_[] =
  {
         0,     2,     0,     1,     1,     3,     5,     3,     1,     1,
       1,     1,     3,     4,     1,     1,     1,     1,     1,     1,
       2,     1,     1,     2,     1,     2,     2,     0,     1,     1,
       1,     1
  };

  /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
     First, the terminals, then, starting at \a yyntokens_, nonterminals.  */
  private static final String yytname_[] =
  {
    "$end", "error", "$undefined", "\"string\"", "'('", "')'", "'&'", "'|'",
  "'!'", "'='", "'>'", "'<'", "'~'", "$accept", "query", "params",
  "open_paren", "close_paren", "join", "param", "comparison", "not_equals",
  "equals", "gt", "gte", "lt", "lte", "like", "not", "key", "value",
  "likevalue", null
  };

  /* YYRHS -- A `-1'-separated list of the rules' RHS.  */
  private static final byte yyrhs_[] =
  {
        14,     0,    -1,    -1,    15,    -1,    19,    -1,    15,    18,
      19,    -1,    15,    18,    16,    15,    17,    -1,    16,    15,
      17,    -1,     4,    -1,     5,    -1,     6,    -1,     7,    -1,
      29,    20,    30,    -1,    29,    28,    27,    31,    -1,    22,
      -1,    21,    -1,    23,    -1,    24,    -1,    25,    -1,    26,
      -1,     8,     9,    -1,     9,    -1,    10,    -1,    10,     9,
      -1,    11,    -1,    11,     9,    -1,    12,     9,    -1,    -1,
       8,    -1,     3,    -1,     3,    -1,     3,    -1
  };

  /* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
     YYRHS.  */
  private static final byte yyprhs_[] =
  {
         0,     0,     3,     4,     6,     8,    12,    18,    22,    24,
      26,    28,    30,    34,    39,    41,    43,    45,    47,    49,
      51,    54,    56,    58,    61,    63,    66,    69,    70,    72,
      74,    76
  };

  /* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
  private static final short yyrline_[] =
  {
         0,   164,   164,   165,   167,   168,   169,   170,   172,   173,
     175,   176,   179,   180,   182,   183,   184,   185,   186,   187,
     189,   191,   193,   194,   195,   196,   198,   200,   201,   203,
     205,   215
  };

  // Report on the debug stream that the rule yyrule is going to be reduced.
  private void yy_reduce_print (int yyrule, YYStack yystack)
  {
    if (yydebug == 0)
      return;

    int yylno = yyrline_[yyrule];
    int yynrhs = yyr2_[yyrule];
    /* Print the symbols being reduced, and their result.  */
    yycdebug ("Reducing stack by rule " + (yyrule - 1)
	      + " (line " + yylno + "), ");

    /* The symbols being reduced.  */
    for (int yyi = 0; yyi < yynrhs; yyi++)
      yy_symbol_print ("   $" + (yyi + 1) + " =",
		       yyrhs_[yyprhs_[yyrule] + yyi],
		       ((yystack.valueAt (yynrhs-(yyi + 1)))));
  }

  /* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
  private static final byte yytranslate_table_[] =
  {
         0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     8,     2,     2,     2,     2,     6,     2,
       4,     5,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
      11,     9,    10,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     7,     2,    12,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3
  };

  private static final byte yytranslate_ (int t)
  {
    if (t >= 0 && t <= yyuser_token_number_max_)
      return yytranslate_table_[t];
    else
      return yyundef_token_;
  }

  private static final int yylast_ = 24;
  private static final int yynnts_ = 19;
  private static final int yyempty_ = -2;
  private static final int yyfinal_ = 8;
  private static final int yyterror_ = 1;
  private static final int yyerrcode_ = 256;
  private static final int yyntokens_ = 13;

  private static final int yyuser_token_number_max_ = 258;
  private static final int yyundef_token_ = 2;

/* User implementation code.  */
/* Unqualified %code blocks.  */

/* Line 927 of lalr1.java  */
/* Line 54 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */


	private StringBuilder mSb = new StringBuilder();
	private LinkedList<String> mSelectionArgs = new LinkedList<String>();

    private final HashMap<String, String> mColumnAlias = new HashMap<String, String>();


/**
 * Returns the resulting SQL query string generated by {@link #parse()}. Any
 * arguments passed in will be replaced with placeholders and can be retrieved
 * by {@link #getSelectionArgs()}.
 */
public String getResult(){
    return mSb.toString();
}

/**
 * Returns the selection arguments that were discovered by {@link #parse()}.
 */
public String[] getSelectionArgs(){
	return mSelectionArgs.toArray(new String[mSelectionArgs.size()]);
}

/**
 * Indicates that the parser has encountered an error while parsing the query.
 */
public static class ParseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ParseException(String msg){
        super(msg);
    }
}


    private void appendColumnName(String key) {
    if (!SQLGenUtils.isValidName(key)) {
        throw new SQLGenerationException("illegal column name in query: '" + key
                                                + "'");
    }
        if (columnAliases != null && columnAliases.containsKey(key)) {
            mSb.append(SQLGenUtils.escapeQualifiedColumn(columnAliases.get(key)));
        } else {
            mSb.append('"');
            mSb.append(key);
            mSb.append('"');
        }
	}



/* Line 927 of lalr1.java  */
/* Line 1203 of "src/edu/mit/mobile/android/content/query/QuerystringParser.java"  */

}


/* Line 931 of lalr1.java  */
/* Line 225 of "src/edu/mit/mobile/android/content/query/QuerystringParser.y"  */



