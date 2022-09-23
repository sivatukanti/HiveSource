// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.plist;

import java.io.IOException;
import java.io.PrintStream;

public class PropertyListParserTokenManager implements PropertyListParserConstants
{
    public PrintStream debugStream;
    static final long[] jjbitVec0;
    static final int[] jjnextStates;
    public static final String[] jjstrLiteralImages;
    public static final String[] lexStateNames;
    public static final int[] jjnewLexState;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    static final long[] jjtoSpecial;
    static final long[] jjtoMore;
    protected SimpleCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    private final StringBuilder jjimage;
    private StringBuilder image;
    private int jjimageLen;
    private int lengthOfMatch;
    protected char curChar;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    
    public void setDebugStream(final PrintStream ds) {
        this.debugStream = ds;
    }
    
    private final int jjStopStringLiteralDfa_0(final int pos, final long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x20000120L) != 0x0L) {
                    this.jjmatchedKind = 27;
                    return 8;
                }
                if ((active0 & 0x80000L) != 0x0L) {
                    return 8;
                }
                if ((active0 & 0x200000L) != 0x0L) {
                    return 14;
                }
                if ((active0 & 0x140000L) != 0x0L) {
                    return 6;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 27;
                    this.jjmatchedPos = 1;
                    return 3;
                }
                if ((active0 & 0x120L) != 0x0L) {
                    return 8;
                }
                return -1;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int jjStartNfa_0(final int pos, final long active0) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }
    
    private int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '\"': {
                return this.jjStartNfaWithStates_0(0, 21, 14);
            }
            case '(': {
                return this.jjStopAtPos(0, 11);
            }
            case ')': {
                return this.jjStopAtPos(0, 12);
            }
            case ',': {
                return this.jjStopAtPos(0, 13);
            }
            case '/': {
                return this.jjMoveStringLiteralDfa1_0(288L);
            }
            case ';': {
                return this.jjStopAtPos(0, 16);
            }
            case '<': {
                this.jjmatchedKind = 18;
                return this.jjMoveStringLiteralDfa1_0(1048576L);
            }
            case '=': {
                return this.jjStopAtPos(0, 17);
            }
            case '>': {
                return this.jjStartNfaWithStates_0(0, 19, 8);
            }
            case '\\': {
                return this.jjMoveStringLiteralDfa1_0(536870912L);
            }
            case '{': {
                return this.jjStopAtPos(0, 14);
            }
            case '}': {
                return this.jjStopAtPos(0, 15);
            }
            default: {
                return this.jjMoveNfa_0(0, 0);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa1_0(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '\"': {
                if ((active0 & 0x20000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 29);
                }
                break;
            }
            case '*': {
                if ((active0 & 0x20L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 5, 8);
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 1048576L);
            }
            case '/': {
                if ((active0 & 0x100L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 8, 8);
                }
                break;
            }
        }
        return this.jjStartNfa_0(0, active0);
    }
    
    private int jjMoveStringLiteralDfa2_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case 'D': {
                if ((active0 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 20, 15);
                }
                break;
            }
        }
        return this.jjStartNfa_0(1, active0);
    }
    
    private int jjStartNfaWithStates_0(final int pos, final int kind, final int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_0(state, pos + 1);
    }
    
    private int jjMoveNfa_0(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 14;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long l = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 15: {
                            if ((0xD7FFECFAFFFFD9FFL & l) != 0x0L) {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(8);
                            }
                            if ((0x7FF280100000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(4, 5);
                                continue;
                            }
                            if (this.curChar == '>' && kind > 26) {
                                kind = 26;
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if ((0xD7FFECFAFFFFD9FFL & l) != 0x0L) {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(8);
                            }
                            if ((0x3FF000100002600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(1, 2);
                                continue;
                            }
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                                continue;
                            }
                            if (this.curChar == '>' && kind > 25) {
                                kind = 25;
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            if (this.curChar == '\"' && kind > 28) {
                                kind = 28;
                                continue;
                            }
                            continue;
                        }
                        case 3:
                        case 8: {
                            if ((0xD7FFECFAFFFFD9FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(8);
                            continue;
                        }
                        case 0: {
                            if ((0xD7FFECFAFFFFD9FFL & l) != 0x0L) {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(8);
                            }
                            else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(0, 2);
                            }
                            if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                            }
                            if (this.curChar == '<') {
                                this.jjCheckNAddTwoStates(1, 2);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0x3FF000100002600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(1, 2);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if (this.curChar == '>' && kind > 25) {
                                kind = 25;
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if ((0x7FF280100000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(4, 5);
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if (this.curChar == '>' && kind > 26) {
                                kind = 26;
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            if (this.curChar == '<') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                                continue;
                            }
                            continue;
                        }
                        case 9:
                        case 11: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if (this.curChar == '\"' && kind > 28) {
                                kind = 28;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 15: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) != 0x0L) {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(8);
                            }
                            if (this.curChar == 'Z') {
                                this.jjCheckNAddTwoStates(4, 5);
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) != 0x0L) {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(8);
                            }
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(1, 2);
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            this.jjCheckNAddStates(0, 2);
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) != 0x0L) {
                                if (kind > 27) {
                                    kind = 27;
                                }
                                this.jjCheckNAdd(8);
                            }
                            if (this.curChar == 'D') {
                                this.jjCheckNAddTwoStates(4, 5);
                                continue;
                            }
                            continue;
                        }
                        case 0:
                        case 8: {
                            if ((0xD7FFFFFFFFFFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(8);
                            continue;
                        }
                        case 1: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(1, 2);
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if (this.curChar == 'Z') {
                                this.jjCheckNAddTwoStates(4, 5);
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 10: {
                            this.jjCheckNAddStates(0, 2);
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else {
                final int i2 = (this.curChar & '\u00ff') >> 6;
                final long l2 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 8:
                        case 15: {
                            if ((PropertyListParserTokenManager.jjbitVec0[i2] & l2) == 0x0L) {
                                continue;
                            }
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(8);
                            continue;
                        }
                        case 6: {
                            if ((PropertyListParserTokenManager.jjbitVec0[i2] & l2) == 0x0L) {
                                continue;
                            }
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(8);
                            continue;
                        }
                        case 10:
                        case 14: {
                            if ((PropertyListParserTokenManager.jjbitVec0[i2] & l2) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if ((PropertyListParserTokenManager.jjbitVec0[i2] & l2) == 0x0L) {
                                continue;
                            }
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(8);
                            continue;
                        }
                        case 0: {
                            if ((PropertyListParserTokenManager.jjbitVec0[i2] & l2) == 0x0L) {
                                continue;
                            }
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjCheckNAdd(8);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            final int n = i = this.jjnewStateCnt;
            final int n2 = 14;
            final int jjnewStateCnt = startsAt;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n == (startsAt = n2 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException e) {
                return curPos;
            }
        }
        return curPos;
    }
    
    private int jjMoveStringLiteralDfa0_2() {
        return this.jjMoveNfa_2(0, 0);
    }
    
    private int jjMoveNfa_2(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 3;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long l = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x2400L & l) != 0x0L && kind > 9) {
                                kind = 9;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && kind > 9) {
                                kind = 9;
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    final int n = this.jjstateSet[--i];
                } while (i != startsAt);
            }
            else {
                final int i2 = (this.curChar & '\u00ff') >> 6;
                final long l2 = 1L << (this.curChar & '?');
                do {
                    final int n2 = this.jjstateSet[--i];
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            final int n3 = i = this.jjnewStateCnt;
            final int n4 = 3;
            final int jjnewStateCnt = startsAt;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n3 == (startsAt = n4 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException e) {
                return curPos;
            }
        }
        return curPos;
    }
    
    private int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case '*': {
                return this.jjMoveStringLiteralDfa1_1(128L);
            }
            default: {
                return 1;
            }
        }
    }
    
    private int jjMoveStringLiteralDfa1_1(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return 1;
        }
        switch (this.curChar) {
            case '/': {
                if ((active0 & 0x80L) != 0x0L) {
                    return this.jjStopAtPos(1, 7);
                }
                return 2;
            }
            default: {
                return 2;
            }
        }
    }
    
    public PropertyListParserTokenManager(final SimpleCharStream stream) {
        this.debugStream = System.out;
        this.jjrounds = new int[14];
        this.jjstateSet = new int[28];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = stream;
    }
    
    public PropertyListParserTokenManager(final SimpleCharStream stream, final int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }
    
    public void ReInit(final SimpleCharStream stream) {
        final int n = 0;
        this.jjnewStateCnt = n;
        this.jjmatchedPos = n;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }
    
    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 14;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final SimpleCharStream stream, final int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }
    
    public void SwitchTo(final int lexState) {
        if (lexState >= 3 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
    
    protected Token jjFillToken() {
        final String im = PropertyListParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
        final String curTokenImage = (im == null) ? this.input_stream.GetImage() : im;
        final int beginLine = this.input_stream.getBeginLine();
        final int beginColumn = this.input_stream.getBeginColumn();
        final int endLine = this.input_stream.getEndLine();
        final int endColumn = this.input_stream.getEndColumn();
        final Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }
    
    public Token getNextToken() {
        Token specialToken = null;
        int curPos = 0;
    Label_0004_Outer:
        while (true) {
        Label_0004:
            while (true) {
                try {
                    this.curChar = this.input_stream.BeginToken();
                }
                catch (IOException e) {
                    this.jjmatchedKind = 0;
                    final Token matchedToken = this.jjFillToken();
                    matchedToken.specialToken = specialToken;
                    return matchedToken;
                }
                (this.image = this.jjimage).setLength(0);
                this.jjimageLen = 0;
                while (true) {
                    switch (this.curLexState) {
                        case 0: {
                            try {
                                this.input_stream.backup(0);
                                while (this.curChar <= ' ' && (0x100002600L & 1L << this.curChar) != 0x0L) {
                                    this.curChar = this.input_stream.BeginToken();
                                }
                            }
                            catch (IOException e2) {
                                continue Label_0004;
                            }
                            this.jjmatchedKind = Integer.MAX_VALUE;
                            this.jjmatchedPos = 0;
                            curPos = this.jjMoveStringLiteralDfa0_0();
                            break;
                        }
                        case 1: {
                            this.jjmatchedKind = Integer.MAX_VALUE;
                            this.jjmatchedPos = 0;
                            curPos = this.jjMoveStringLiteralDfa0_1();
                            if (this.jjmatchedPos == 0 && this.jjmatchedKind > 6) {
                                this.jjmatchedKind = 6;
                                break;
                            }
                            break;
                        }
                        case 2: {
                            this.jjmatchedKind = Integer.MAX_VALUE;
                            this.jjmatchedPos = 0;
                            curPos = this.jjMoveStringLiteralDfa0_2();
                            if (this.jjmatchedPos == 0 && this.jjmatchedKind > 10) {
                                this.jjmatchedKind = 10;
                                break;
                            }
                            break;
                        }
                    }
                    if (this.jjmatchedKind == Integer.MAX_VALUE) {
                        break Label_0004_Outer;
                    }
                    if (this.jjmatchedPos + 1 < curPos) {
                        this.input_stream.backup(curPos - this.jjmatchedPos - 1);
                    }
                    if ((PropertyListParserTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                        final Token matchedToken = this.jjFillToken();
                        matchedToken.specialToken = specialToken;
                        if (PropertyListParserTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                            this.curLexState = PropertyListParserTokenManager.jjnewLexState[this.jjmatchedKind];
                        }
                        return matchedToken;
                    }
                    if ((PropertyListParserTokenManager.jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0x0L) {
                        this.jjimageLen += this.jjmatchedPos + 1;
                        if (PropertyListParserTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                            this.curLexState = PropertyListParserTokenManager.jjnewLexState[this.jjmatchedKind];
                        }
                        curPos = 0;
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        try {
                            this.curChar = this.input_stream.readChar();
                            continue Label_0004_Outer;
                        }
                        catch (IOException ex) {}
                        break Label_0004_Outer;
                    }
                    if ((PropertyListParserTokenManager.jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                        final Token matchedToken = this.jjFillToken();
                        if (specialToken == null) {
                            specialToken = matchedToken;
                        }
                        else {
                            matchedToken.specialToken = specialToken;
                            final Token token = specialToken;
                            final Token next = matchedToken;
                            token.next = next;
                            specialToken = next;
                        }
                        this.SkipLexicalActions(matchedToken);
                    }
                    else {
                        this.SkipLexicalActions(null);
                    }
                    if (PropertyListParserTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = PropertyListParserTokenManager.jjnewLexState[this.jjmatchedKind];
                        break;
                    }
                    break;
                }
                break;
            }
        }
        int error_line = this.input_stream.getEndLine();
        int error_column = this.input_stream.getEndColumn();
        String error_after = null;
        boolean EOFSeen = false;
        try {
            this.input_stream.readChar();
            this.input_stream.backup(1);
        }
        catch (IOException e3) {
            EOFSeen = true;
            error_after = ((curPos <= 1) ? "" : this.input_stream.GetImage());
            if (this.curChar == '\n' || this.curChar == '\r') {
                ++error_line;
                error_column = 0;
            }
            else {
                ++error_column;
            }
        }
        if (!EOFSeen) {
            this.input_stream.backup(1);
            error_after = ((curPos <= 1) ? "" : this.input_stream.GetImage());
        }
        throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
    }
    
    void SkipLexicalActions(final Token matchedToken) {
        final int jjmatchedKind = this.jjmatchedKind;
    }
    
    private void jjCheckNAdd(final int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }
    
    private void jjAddStates(int start, final int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = PropertyListParserTokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(PropertyListParserTokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    static {
        jjbitVec0 = new long[] { 0L, 0L, -1L, -1L };
        jjnextStates = new int[] { 10, 12, 13 };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, null, "(", ")", ",", "{", "}", ";", "=", "<", ">", "<*D", "\"", null, null, null, null, null, null, null, "\\\"" };
        lexStateNames = new String[] { "DEFAULT", "IN_COMMENT", "IN_SINGLE_LINE_COMMENT" };
        jjnewLexState = new int[] { -1, -1, -1, -1, -1, 1, -1, 0, 2, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        jjtoToken = new long[] { 1044379649L };
        jjtoSkip = new long[] { 670L };
        jjtoSpecial = new long[] { 512L };
        jjtoMore = new long[] { 1376L };
    }
}
