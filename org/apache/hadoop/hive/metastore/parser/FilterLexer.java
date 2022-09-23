// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.parser;

import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.BaseRecognizer;
import java.util.regex.Matcher;
import java.text.ParseException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import org.antlr.runtime.Lexer;

public class FilterLexer extends Lexer
{
    public static final int EOF = -1;
    public static final int BETWEEN = 4;
    public static final int DateLiteral = 5;
    public static final int DateString = 6;
    public static final int Digit = 7;
    public static final int EQUAL = 8;
    public static final int GREATERTHAN = 9;
    public static final int GREATERTHANOREQUALTO = 10;
    public static final int Identifier = 11;
    public static final int IntegralLiteral = 12;
    public static final int KW_AND = 13;
    public static final int KW_DATE = 14;
    public static final int KW_LIKE = 15;
    public static final int KW_NOT = 16;
    public static final int KW_OR = 17;
    public static final int LESSTHAN = 18;
    public static final int LESSTHANOREQUALTO = 19;
    public static final int LPAREN = 20;
    public static final int Letter = 21;
    public static final int NOTEQUAL = 22;
    public static final int RPAREN = 23;
    public static final int StringLiteral = 24;
    public static final int WS = 25;
    public String errorMsg;
    private static final Pattern datePattern;
    private static final ThreadLocal<SimpleDateFormat> dateFormat;
    protected DFA10 dfa10;
    static final String DFA10_eotS = "\u0001\uffff\u0005\u0010\u0003\uffff\u0001\u0018\u0001\uffff\u0001\u001a\u0001\u0010\u0001\u000f\u0004\uffff\u0002\u0010\u0001\u001f\u0002\u0010\u0004\uffff\u0001\u0010\u0001\u000f\u0001$\u0001%\u0001\uffff\u0003\u0010\u0001\u000f\u0002\uffff\u0001*\u0001+\u0001\u0010\u0001\u000f\u0002\uffff\u0002\u0010\u0001\uffff\u0001\u000f\u0003\u0010\u00015\u0001\u0010\u0001\uffff";
    static final String DFA10_eofS = "6\uffff";
    static final String DFA10_minS = "\u0001\t\u0001O\u0001N\u0001R\u0001I\u0001a\u0003\uffff\u0001=\u0001\uffff\u0001=\u0001E\u00010\u0004\uffff\u0001T\u0001D\u00010\u0001K\u0001t\u0004\uffff\u0001T\u00030\u0001\uffff\u0001E\u0001e\u0001W\u00010\u0002\uffff\u00020\u0001E\u0001-\u0002\uffff\u00010\u0001E\u0001\uffff\u00020\u0001N\u00020\u0001-\u0001\uffff";
    static final String DFA10_maxS = "\u0001z\u0001O\u0001N\u0001R\u0001I\u0001a\u0003\uffff\u0001>\u0001\uffff\u0001=\u0001E\u0001z\u0004\uffff\u0001T\u0001D\u0001z\u0001K\u0001t\u0004\uffff\u0001T\u0003z\u0001\uffff\u0001E\u0001e\u0001W\u0001z\u0002\uffff\u0002z\u0001E\u0001z\u0002\uffff\u00019\u0001E\u0001\uffff\u0001z\u00019\u0001N\u00019\u0001z\u0001-\u0001\uffff";
    static final String DFA10_acceptS = "\u0006\uffff\u0001\u0006\u0001\u0007\u0001\b\u0001\uffff\u0001\t\u0003\uffff\u0001\u0010\u0001\u0011\u0001\u0012\u0001\u0013\u0005\uffff\u0001\n\u0001\u000b\u0001\f\u0001\r\u0004\uffff\u0001\u0003\u0004\uffff\u0001\u0001\u0001\u0002\u0004\uffff\u0001\u0004\u0001\u0005\u0002\uffff\u0001\u000f\u0006\uffff\u0001\u000e";
    static final String DFA10_specialS = "6\uffff}>";
    static final String[] DFA10_transitionS;
    static final short[] DFA10_eot;
    static final short[] DFA10_eof;
    static final char[] DFA10_min;
    static final char[] DFA10_max;
    static final short[] DFA10_accept;
    static final short[] DFA10_special;
    static final short[][] DFA10_transition;
    
    public static Date ExtractDate(final String input) {
        final Matcher m = FilterLexer.datePattern.matcher(input);
        if (!m.matches()) {
            return null;
        }
        try {
            return new Date(FilterLexer.dateFormat.get().parse(m.group(1)).getTime());
        }
        catch (ParseException pe) {
            return null;
        }
    }
    
    @Override
    public void emitErrorMessage(final String msg) {
        this.errorMsg = msg;
    }
    
    public Lexer[] getDelegates() {
        return new Lexer[0];
    }
    
    public FilterLexer() {
        this.dfa10 = new DFA10(this);
    }
    
    public FilterLexer(final CharStream input) {
        this(input, new RecognizerSharedState());
    }
    
    public FilterLexer(final CharStream input, final RecognizerSharedState state) {
        super(input, state);
        this.dfa10 = new DFA10(this);
    }
    
    @Override
    public String getGrammarFileName() {
        return "org/apache/hadoop/hive/metastore/parser/Filter.g";
    }
    
    public final void mKW_NOT() throws RecognitionException {
        final int _type = 16;
        final int _channel = 0;
        this.match("NOT");
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mKW_AND() throws RecognitionException {
        final int _type = 13;
        final int _channel = 0;
        this.match("AND");
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mKW_OR() throws RecognitionException {
        final int _type = 17;
        final int _channel = 0;
        this.match("OR");
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mKW_LIKE() throws RecognitionException {
        final int _type = 15;
        final int _channel = 0;
        this.match("LIKE");
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mKW_DATE() throws RecognitionException {
        final int _type = 14;
        final int _channel = 0;
        this.match("date");
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mLPAREN() throws RecognitionException {
        final int _type = 20;
        final int _channel = 0;
        this.match(40);
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mRPAREN() throws RecognitionException {
        final int _type = 23;
        final int _channel = 0;
        this.match(41);
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mEQUAL() throws RecognitionException {
        final int _type = 8;
        final int _channel = 0;
        this.match(61);
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mNOTEQUAL() throws RecognitionException {
        final int _type = 22;
        final int _channel = 0;
        int alt1 = 2;
        switch (this.input.LA(1)) {
            case 60: {
                alt1 = 1;
                break;
            }
            case 33: {
                alt1 = 2;
                break;
            }
            default: {
                final NoViableAltException nvae = new NoViableAltException("", 1, 0, this.input);
                throw nvae;
            }
        }
        switch (alt1) {
            case 1: {
                this.match("<>");
                break;
            }
            case 2: {
                this.match("!=");
                break;
            }
        }
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mLESSTHANOREQUALTO() throws RecognitionException {
        final int _type = 19;
        final int _channel = 0;
        this.match("<=");
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mLESSTHAN() throws RecognitionException {
        final int _type = 18;
        final int _channel = 0;
        this.match(60);
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mGREATERTHANOREQUALTO() throws RecognitionException {
        final int _type = 10;
        final int _channel = 0;
        this.match(">=");
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mGREATERTHAN() throws RecognitionException {
        final int _type = 9;
        final int _channel = 0;
        this.match(62);
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mBETWEEN() throws RecognitionException {
        final int _type = 4;
        final int _channel = 0;
        this.match("BETWEEN");
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mLetter() throws RecognitionException {
        if ((this.input.LA(1) >= 65 && this.input.LA(1) <= 90) || (this.input.LA(1) >= 97 && this.input.LA(1) <= 122)) {
            this.input.consume();
            return;
        }
        final MismatchedSetException mse = new MismatchedSetException(null, this.input);
        this.recover(mse);
        throw mse;
    }
    
    public final void mDigit() throws RecognitionException {
        if (this.input.LA(1) >= 48 && this.input.LA(1) <= 57) {
            this.input.consume();
            return;
        }
        final MismatchedSetException mse = new MismatchedSetException(null, this.input);
        this.recover(mse);
        throw mse;
    }
    
    public final void mDateString() throws RecognitionException {
        if (this.input.LA(1) < 48 || this.input.LA(1) > 57) {
            final MismatchedSetException mse = new MismatchedSetException(null, this.input);
            this.recover(mse);
            throw mse;
        }
        this.input.consume();
        if (this.input.LA(1) < 48 || this.input.LA(1) > 57) {
            final MismatchedSetException mse = new MismatchedSetException(null, this.input);
            this.recover(mse);
            throw mse;
        }
        this.input.consume();
        if (this.input.LA(1) < 48 || this.input.LA(1) > 57) {
            final MismatchedSetException mse = new MismatchedSetException(null, this.input);
            this.recover(mse);
            throw mse;
        }
        this.input.consume();
        if (this.input.LA(1) < 48 || this.input.LA(1) > 57) {
            final MismatchedSetException mse = new MismatchedSetException(null, this.input);
            this.recover(mse);
            throw mse;
        }
        this.input.consume();
        this.match(45);
        if (this.input.LA(1) < 48 || this.input.LA(1) > 57) {
            final MismatchedSetException mse = new MismatchedSetException(null, this.input);
            this.recover(mse);
            throw mse;
        }
        this.input.consume();
        if (this.input.LA(1) < 48 || this.input.LA(1) > 57) {
            final MismatchedSetException mse = new MismatchedSetException(null, this.input);
            this.recover(mse);
            throw mse;
        }
        this.input.consume();
        this.match(45);
        if (this.input.LA(1) < 48 || this.input.LA(1) > 57) {
            final MismatchedSetException mse = new MismatchedSetException(null, this.input);
            this.recover(mse);
            throw mse;
        }
        this.input.consume();
        if (this.input.LA(1) >= 48 && this.input.LA(1) <= 57) {
            this.input.consume();
            return;
        }
        final MismatchedSetException mse = new MismatchedSetException(null, this.input);
        this.recover(mse);
        throw mse;
    }
    
    public final void mDateLiteral() throws RecognitionException {
        final int _type = 5;
        final int _channel = 0;
        int alt2 = 2;
        switch (this.input.LA(1)) {
            case 100: {
                alt2 = 1;
                break;
            }
        }
        switch (alt2) {
            case 1: {
                this.mKW_DATE();
                break;
            }
        }
        this.mDateString();
        if (ExtractDate(this.getText()) == null) {
            throw new FailedPredicateException(this.input, "DateLiteral", " ExtractDate(getText()) != null ");
        }
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mStringLiteral() throws RecognitionException {
        final int _type = 24;
        final int _channel = 0;
        int alt5 = 2;
        switch (this.input.LA(1)) {
            case 39: {
                alt5 = 1;
                break;
            }
            case 34: {
                alt5 = 2;
                break;
            }
            default: {
                final NoViableAltException nvae = new NoViableAltException("", 5, 0, this.input);
                throw nvae;
            }
        }
        Label_0608: {
            switch (alt5) {
                case 1: {
                    this.match(39);
                    while (true) {
                        int alt6 = 3;
                        final int LA3_0 = this.input.LA(1);
                        if ((LA3_0 >= 0 && LA3_0 <= 38) || (LA3_0 >= 40 && LA3_0 <= 91) || (LA3_0 >= 93 && LA3_0 <= 65535)) {
                            alt6 = 1;
                        }
                        else if (LA3_0 == 92) {
                            alt6 = 2;
                        }
                        switch (alt6) {
                            case 1: {
                                if ((this.input.LA(1) >= 0 && this.input.LA(1) <= 38) || (this.input.LA(1) >= 40 && this.input.LA(1) <= 91) || (this.input.LA(1) >= 93 && this.input.LA(1) <= 65535)) {
                                    this.input.consume();
                                    continue;
                                }
                                final MismatchedSetException mse = new MismatchedSetException(null, this.input);
                                this.recover(mse);
                                throw mse;
                            }
                            case 2: {
                                this.match(92);
                                this.matchAny();
                                continue;
                            }
                            default: {
                                this.match(39);
                                break Label_0608;
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    this.match(34);
                    while (true) {
                        int alt7 = 3;
                        final int LA4_0 = this.input.LA(1);
                        if ((LA4_0 >= 0 && LA4_0 <= 33) || (LA4_0 >= 35 && LA4_0 <= 91) || (LA4_0 >= 93 && LA4_0 <= 65535)) {
                            alt7 = 1;
                        }
                        else if (LA4_0 == 92) {
                            alt7 = 2;
                        }
                        switch (alt7) {
                            case 1: {
                                if ((this.input.LA(1) >= 0 && this.input.LA(1) <= 33) || (this.input.LA(1) >= 35 && this.input.LA(1) <= 91) || (this.input.LA(1) >= 93 && this.input.LA(1) <= 65535)) {
                                    this.input.consume();
                                    continue;
                                }
                                final MismatchedSetException mse = new MismatchedSetException(null, this.input);
                                this.recover(mse);
                                throw mse;
                            }
                            case 2: {
                                this.match(92);
                                this.matchAny();
                                continue;
                            }
                            default: {
                                this.match(34);
                                break Label_0608;
                            }
                        }
                    }
                    break;
                }
            }
        }
        this.state.type = _type;
        this.state.channel = _channel;
    }
    
    public final void mIntegralLiteral() throws RecognitionException {
        final int _type = 12;
        final int _channel = 0;
        int alt6 = 2;
        switch (this.input.LA(1)) {
            case 45: {
                alt6 = 1;
                break;
            }
        }
        switch (alt6) {
            case 1: {
                this.match(45);
                break;
            }
        }
        int cnt7 = 0;
        while (true) {
            int alt7 = 2;
            switch (this.input.LA(1)) {
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    alt7 = 1;
                    break;
                }
            }
            switch (alt7) {
                case 1: {
                    if (this.input.LA(1) >= 48 && this.input.LA(1) <= 57) {
                        this.input.consume();
                        ++cnt7;
                        continue;
                    }
                    final MismatchedSetException mse = new MismatchedSetException(null, this.input);
                    this.recover(mse);
                    throw mse;
                }
                default: {
                    if (cnt7 >= 1) {
                        this.state.type = _type;
                        this.state.channel = _channel;
                        return;
                    }
                    final EarlyExitException eee = new EarlyExitException(7, this.input);
                    throw eee;
                }
            }
        }
    }
    
    public final void mIdentifier() throws RecognitionException {
        final int _type = 11;
        final int _channel = 0;
        if ((this.input.LA(1) < 48 || this.input.LA(1) > 57) && (this.input.LA(1) < 65 || this.input.LA(1) > 90) && (this.input.LA(1) < 97 || this.input.LA(1) > 122)) {
            final MismatchedSetException mse = new MismatchedSetException(null, this.input);
            this.recover(mse);
            throw mse;
        }
        this.input.consume();
        while (true) {
            int alt8 = 2;
            switch (this.input.LA(1)) {
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 80:
                case 81:
                case 82:
                case 83:
                case 84:
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 95:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 102:
                case 103:
                case 104:
                case 105:
                case 106:
                case 107:
                case 108:
                case 109:
                case 110:
                case 111:
                case 112:
                case 113:
                case 114:
                case 115:
                case 116:
                case 117:
                case 118:
                case 119:
                case 120:
                case 121:
                case 122: {
                    alt8 = 1;
                    break;
                }
            }
            switch (alt8) {
                case 1: {
                    if ((this.input.LA(1) >= 48 && this.input.LA(1) <= 57) || (this.input.LA(1) >= 65 && this.input.LA(1) <= 90) || this.input.LA(1) == 95 || (this.input.LA(1) >= 97 && this.input.LA(1) <= 122)) {
                        this.input.consume();
                        continue;
                    }
                    final MismatchedSetException mse2 = new MismatchedSetException(null, this.input);
                    this.recover(mse2);
                    throw mse2;
                }
                default: {
                    this.state.type = _type;
                    this.state.channel = _channel;
                }
            }
        }
    }
    
    public final void mWS() throws RecognitionException {
        final int _type = 25;
        final int _channel = 0;
        int cnt9 = 0;
        while (true) {
            int alt9 = 2;
            switch (this.input.LA(1)) {
                case 9:
                case 10:
                case 13:
                case 32: {
                    alt9 = 1;
                    break;
                }
            }
            switch (alt9) {
                case 1: {
                    if ((this.input.LA(1) >= 9 && this.input.LA(1) <= 10) || this.input.LA(1) == 13 || this.input.LA(1) == 32) {
                        this.input.consume();
                        ++cnt9;
                        continue;
                    }
                    final MismatchedSetException mse = new MismatchedSetException(null, this.input);
                    this.recover(mse);
                    throw mse;
                }
                default: {
                    if (cnt9 >= 1) {
                        this.skip();
                        this.state.type = _type;
                        this.state.channel = _channel;
                        return;
                    }
                    final EarlyExitException eee = new EarlyExitException(9, this.input);
                    throw eee;
                }
            }
        }
    }
    
    @Override
    public void mTokens() throws RecognitionException {
        int alt10 = 19;
        alt10 = this.dfa10.predict(this.input);
        switch (alt10) {
            case 1: {
                this.mKW_NOT();
                break;
            }
            case 2: {
                this.mKW_AND();
                break;
            }
            case 3: {
                this.mKW_OR();
                break;
            }
            case 4: {
                this.mKW_LIKE();
                break;
            }
            case 5: {
                this.mKW_DATE();
                break;
            }
            case 6: {
                this.mLPAREN();
                break;
            }
            case 7: {
                this.mRPAREN();
                break;
            }
            case 8: {
                this.mEQUAL();
                break;
            }
            case 9: {
                this.mNOTEQUAL();
                break;
            }
            case 10: {
                this.mLESSTHANOREQUALTO();
                break;
            }
            case 11: {
                this.mLESSTHAN();
                break;
            }
            case 12: {
                this.mGREATERTHANOREQUALTO();
                break;
            }
            case 13: {
                this.mGREATERTHAN();
                break;
            }
            case 14: {
                this.mBETWEEN();
                break;
            }
            case 15: {
                this.mDateLiteral();
                break;
            }
            case 16: {
                this.mStringLiteral();
                break;
            }
            case 17: {
                this.mIntegralLiteral();
                break;
            }
            case 18: {
                this.mIdentifier();
                break;
            }
            case 19: {
                this.mWS();
                break;
            }
        }
    }
    
    static {
        datePattern = Pattern.compile(".*(\\d\\d\\d\\d-\\d\\d-\\d\\d).*");
        dateFormat = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                final SimpleDateFormat val = new SimpleDateFormat("yyyy-MM-dd");
                val.setLenient(false);
                return val;
            }
        };
        DFA10_transitionS = new String[] { "\u0002\u0011\u0002\uffff\u0001\u0011\u0012\uffff\u0001\u0011\u0001\n\u0001\u000e\u0004\uffff\u0001\u000e\u0001\u0006\u0001\u0007\u0003\uffff\u0001\u000f\u0002\uffff\n\r\u0002\uffff\u0001\t\u0001\b\u0001\u000b\u0002\uffff\u0001\u0002\u0001\f\t\u0010\u0001\u0004\u0001\u0010\u0001\u0001\u0001\u0003\u000b\u0010\u0006\uffff\u0003\u0010\u0001\u0005\u0016\u0010", "\u0001\u0012", "\u0001\u0013", "\u0001\u0014", "\u0001\u0015", "\u0001\u0016", "", "", "", "\u0001\u0017\u0001\n", "", "\u0001\u0019", "\u0001\u001b", "\n\u001c\u0007\uffff\u001a\u0010\u0004\uffff\u0001\u0010\u0001\uffff\u001a\u0010", "", "", "", "", "\u0001\u001d", "\u0001\u001e", "\n\u0010\u0007\uffff\u001a\u0010\u0004\uffff\u0001\u0010\u0001\uffff\u001a\u0010", "\u0001 ", "\u0001!", "", "", "", "", "\u0001\"", "\n#\u0007\uffff\u001a\u0010\u0004\uffff\u0001\u0010\u0001\uffff\u001a\u0010", "\n\u0010\u0007\uffff\u001a\u0010\u0004\uffff\u0001\u0010\u0001\uffff\u001a\u0010", "\n\u0010\u0007\uffff\u001a\u0010\u0004\uffff\u0001\u0010\u0001\uffff\u001a\u0010", "", "\u0001&", "\u0001'", "\u0001(", "\n)\u0007\uffff\u001a\u0010\u0004\uffff\u0001\u0010\u0001\uffff\u001a\u0010", "", "", "\n\u0010\u0007\uffff\u001a\u0010\u0004\uffff\u0001\u0010\u0001\uffff\u001a\u0010", "\n,\u0007\uffff\u001a\u0010\u0004\uffff\u0001\u0010\u0001\uffff\u001a\u0010", "\u0001-", "\u0001.\u0002\uffff\n/\u0007\uffff\u001a\u0010\u0004\uffff\u0001\u0010\u0001\uffff\u001a\u0010", "", "", "\n0", "\u00011", "", "\n/\u0007\uffff\u001a\u0010\u0004\uffff\u0001\u0010\u0001\uffff\u001a\u0010", "\n2", "\u00013", "\n4", "\n\u0010\u0007\uffff\u001a\u0010\u0004\uffff\u0001\u0010\u0001\uffff\u001a\u0010", "\u0001.", "" };
        DFA10_eot = DFA.unpackEncodedString("\u0001\uffff\u0005\u0010\u0003\uffff\u0001\u0018\u0001\uffff\u0001\u001a\u0001\u0010\u0001\u000f\u0004\uffff\u0002\u0010\u0001\u001f\u0002\u0010\u0004\uffff\u0001\u0010\u0001\u000f\u0001$\u0001%\u0001\uffff\u0003\u0010\u0001\u000f\u0002\uffff\u0001*\u0001+\u0001\u0010\u0001\u000f\u0002\uffff\u0002\u0010\u0001\uffff\u0001\u000f\u0003\u0010\u00015\u0001\u0010\u0001\uffff");
        DFA10_eof = DFA.unpackEncodedString("6\uffff");
        DFA10_min = DFA.unpackEncodedStringToUnsignedChars("\u0001\t\u0001O\u0001N\u0001R\u0001I\u0001a\u0003\uffff\u0001=\u0001\uffff\u0001=\u0001E\u00010\u0004\uffff\u0001T\u0001D\u00010\u0001K\u0001t\u0004\uffff\u0001T\u00030\u0001\uffff\u0001E\u0001e\u0001W\u00010\u0002\uffff\u00020\u0001E\u0001-\u0002\uffff\u00010\u0001E\u0001\uffff\u00020\u0001N\u00020\u0001-\u0001\uffff");
        DFA10_max = DFA.unpackEncodedStringToUnsignedChars("\u0001z\u0001O\u0001N\u0001R\u0001I\u0001a\u0003\uffff\u0001>\u0001\uffff\u0001=\u0001E\u0001z\u0004\uffff\u0001T\u0001D\u0001z\u0001K\u0001t\u0004\uffff\u0001T\u0003z\u0001\uffff\u0001E\u0001e\u0001W\u0001z\u0002\uffff\u0002z\u0001E\u0001z\u0002\uffff\u00019\u0001E\u0001\uffff\u0001z\u00019\u0001N\u00019\u0001z\u0001-\u0001\uffff");
        DFA10_accept = DFA.unpackEncodedString("\u0006\uffff\u0001\u0006\u0001\u0007\u0001\b\u0001\uffff\u0001\t\u0003\uffff\u0001\u0010\u0001\u0011\u0001\u0012\u0001\u0013\u0005\uffff\u0001\n\u0001\u000b\u0001\f\u0001\r\u0004\uffff\u0001\u0003\u0004\uffff\u0001\u0001\u0001\u0002\u0004\uffff\u0001\u0004\u0001\u0005\u0002\uffff\u0001\u000f\u0006\uffff\u0001\u000e");
        DFA10_special = DFA.unpackEncodedString("6\uffff}>");
        final int numStates = FilterLexer.DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i = 0; i < numStates; ++i) {
            FilterLexer.DFA10_transition[i] = DFA.unpackEncodedString(FilterLexer.DFA10_transitionS[i]);
        }
    }
    
    class DFA10 extends DFA
    {
        public DFA10(final BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = FilterLexer.DFA10_eot;
            this.eof = FilterLexer.DFA10_eof;
            this.min = FilterLexer.DFA10_min;
            this.max = FilterLexer.DFA10_max;
            this.accept = FilterLexer.DFA10_accept;
            this.special = FilterLexer.DFA10_special;
            this.transition = FilterLexer.DFA10_transition;
        }
        
        @Override
        public String getDescription() {
            return "1:1: Tokens : ( KW_NOT | KW_AND | KW_OR | KW_LIKE | KW_DATE | LPAREN | RPAREN | EQUAL | NOTEQUAL | LESSTHANOREQUALTO | LESSTHAN | GREATERTHANOREQUALTO | GREATERTHAN | BETWEEN | DateLiteral | StringLiteral | IntegralLiteral | Identifier | WS );";
        }
    }
}
