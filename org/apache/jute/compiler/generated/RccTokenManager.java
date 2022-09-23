// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler.generated;

import java.io.IOException;
import java.io.PrintStream;

public class RccTokenManager implements RccConstants
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
    StringBuffer image;
    int jjimageLen;
    int lengthOfMatch;
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
    
    private final int jjMoveStringLiteralDfa0_1() {
        return this.jjMoveNfa_1(0, 0);
    }
    
    private final void jjCheckNAdd(final int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }
    
    private final void jjAddStates(int start, final int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = RccTokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private final void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private final void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(RccTokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    private final void jjCheckNAddStates(final int start) {
        this.jjCheckNAdd(RccTokenManager.jjnextStates[start]);
        this.jjCheckNAdd(RccTokenManager.jjnextStates[start + 1]);
    }
    
    private final int jjMoveNfa_1(final int startState, int curPos) {
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
                            if ((0x2400L & l) != 0x0L && kind > 6) {
                                kind = 6;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && kind > 6) {
                                kind = 6;
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
    
    private final int jjStopStringLiteralDfa_0(final int pos, final long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0xFFF800L) != 0x0L) {
                    this.jjmatchedKind = 32;
                    return 4;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0xFFF800L) != 0x0L) {
                    this.jjmatchedKind = 32;
                    this.jjmatchedPos = 1;
                    return 4;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x7EF800L) != 0x0L) {
                    this.jjmatchedKind = 32;
                    this.jjmatchedPos = 2;
                    return 4;
                }
                if ((active0 & 0x810000L) != 0x0L) {
                    return 4;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x24000L) != 0x0L) {
                    return 4;
                }
                if ((active0 & 0x7CB800L) != 0x0L) {
                    this.jjmatchedKind = 32;
                    this.jjmatchedPos = 3;
                    return 4;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x41000L) != 0x0L) {
                    return 4;
                }
                if ((active0 & 0x78A800L) != 0x0L) {
                    this.jjmatchedKind = 32;
                    return this.jjmatchedPos = 4;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x680800L) != 0x0L) {
                    return 4;
                }
                if ((active0 & 0x10A000L) != 0x0L) {
                    this.jjmatchedKind = 32;
                    this.jjmatchedPos = 5;
                    return 4;
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
    
    private final int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private final int jjStartNfaWithStates_0(final int pos, final int kind, final int state) {
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
    
    private final int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case ',': {
                return this.jjStopAtPos(0, 29);
            }
            case '.': {
                return this.jjStopAtPos(0, 30);
            }
            case '/': {
                return this.jjMoveStringLiteralDfa1_0(288L);
            }
            case ';': {
                return this.jjStopAtPos(0, 28);
            }
            case '<': {
                return this.jjStopAtPos(0, 26);
            }
            case '>': {
                return this.jjStopAtPos(0, 27);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa1_0(2146304L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa1_0(4096L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa1_0(524288L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_0(262144L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_0(73728L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_0(131072L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa1_0(8390656L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa1_0(1048576L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa1_0(4194304L);
            }
            case '{': {
                return this.jjStopAtPos(0, 24);
            }
            case '}': {
                return this.jjStopAtPos(0, 25);
            }
            default: {
                return this.jjMoveNfa_0(0, 0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa1_0(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '*': {
                if ((active0 & 0x100L) != 0x0L) {
                    return this.jjStopAtPos(1, 8);
                }
                break;
            }
            case '/': {
                if ((active0 & 0x20L) != 0x0L) {
                    return this.jjStopAtPos(1, 5);
                }
                break;
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, 8388608L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa2_0(active0, 4194304L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa2_0(active0, 266240L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa2_0(active0, 73728L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa2_0(active0, 690176L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1048576L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa2_0(active0, 2097152L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa2_0(active0, 16384L);
            }
        }
        return this.jjStartNfa_0(0, active0);
    }
    
    private final int jjMoveStringLiteralDfa2_0(final long old0, long active0) {
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
            case 'a': {
                return this.jjMoveStringLiteralDfa3_0(active0, 4096L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa3_0(active0, 4202496L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa3_0(active0, 2048L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa3_0(active0, 2097152L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa3_0(active0, 131072L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa3_0(active0, 294912L);
            }
            case 'p': {
                if ((active0 & 0x800000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 23, 4);
                }
                break;
            }
            case 't': {
                if ((active0 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 16, 4);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 1064960L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_0(active0, 524288L);
            }
        }
        return this.jjStartNfa_0(1, active0);
    }
    
    private final int jjMoveStringLiteralDfa3_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa4_0(active0, 262144L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa4_0(active0, 524288L);
            }
            case 'e': {
                if ((active0 & 0x4000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 14, 4);
                }
                break;
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa4_0(active0, 2097152L);
            }
            case 'g': {
                if ((active0 & 0x20000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 17, 4);
                }
                break;
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa4_0(active0, 40960L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa4_0(active0, 1048576L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_0(active0, 4096L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa4_0(active0, 4194304L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa4_0(active0, 2048L);
            }
        }
        return this.jjStartNfa_0(2, active0);
    }
    
    private final int jjMoveStringLiteralDfa4_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 'e': {
                return this.jjMoveStringLiteralDfa5_0(active0, 2129920L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa5_0(active0, 1048576L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa5_0(active0, 526336L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa5_0(active0, 4194304L);
            }
            case 's': {
                if ((active0 & 0x1000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 12, 4);
                }
                break;
            }
            case 't': {
                if ((active0 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 18, 4);
                }
                break;
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa5_0(active0, 8192L);
            }
        }
        return this.jjStartNfa_0(3, active0);
    }
    
    private final int jjMoveStringLiteralDfa5_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(3, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(4, active0);
            return 5;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa6_0(active0, 32768L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa6_0(active0, 8192L);
            }
            case 'e': {
                if ((active0 & 0x800L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 11, 4);
                }
                if ((active0 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 19, 4);
                }
                break;
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa6_0(active0, 1048576L);
            }
            case 'r': {
                if ((active0 & 0x200000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 21, 4);
                }
                if ((active0 & 0x400000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 22, 4);
                }
                break;
            }
        }
        return this.jjStartNfa_0(4, active0);
    }
    
    private final int jjMoveStringLiteralDfa6_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(4, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(5, active0);
            return 6;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 13, 4);
                }
                break;
            }
            case 'g': {
                if ((active0 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 20, 4);
                }
                break;
            }
            case 'n': {
                if ((active0 & 0x8000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 15, 4);
                }
                break;
            }
        }
        return this.jjStartNfa_0(5, active0);
    }
    
    private final int jjMoveNfa_0(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 5;
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
                            if (this.curChar == '\"') {
                                this.jjCheckNAdd(1);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(1, 2);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if (this.curChar == '\"' && kind > 31) {
                                kind = 31;
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 4;
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
                        case 0: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAdd(4);
                            continue;
                        }
                        case 4: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAdd(4);
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 1: {
                            this.jjAddStates(0, 1);
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
                        case 1: {
                            if ((RccTokenManager.jjbitVec0[i2] & l2) != 0x0L) {
                                this.jjAddStates(0, 1);
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
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            final int n = i = this.jjnewStateCnt;
            final int n2 = 5;
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
    
    private final int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case '*': {
                return this.jjMoveStringLiteralDfa1_2(512L);
            }
            default: {
                return 1;
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa1_2(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            return 1;
        }
        switch (this.curChar) {
            case '/': {
                if ((active0 & 0x200L) != 0x0L) {
                    return this.jjStopAtPos(1, 9);
                }
                return 2;
            }
            default: {
                return 2;
            }
        }
    }
    
    public RccTokenManager(final SimpleCharStream stream) {
        this.debugStream = System.out;
        this.jjrounds = new int[5];
        this.jjstateSet = new int[10];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = stream;
    }
    
    public RccTokenManager(final SimpleCharStream stream, final int lexState) {
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
    
    private final void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 5;
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
        final Token t = Token.newToken(this.jjmatchedKind);
        t.kind = this.jjmatchedKind;
        final String im = RccTokenManager.jjstrLiteralImages[this.jjmatchedKind];
        t.image = ((im == null) ? this.input_stream.GetImage() : im);
        t.beginLine = this.input_stream.getBeginLine();
        t.beginColumn = this.input_stream.getBeginColumn();
        t.endLine = this.input_stream.getEndLine();
        t.endColumn = this.input_stream.getEndColumn();
        return t;
    }
    
    public Token getNextToken() {
        Token specialToken = null;
        int curPos = 0;
    Label_0005_Outer:
        while (true) {
        Label_0005:
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
                this.image = null;
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
                                continue Label_0005;
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
                            if (this.jjmatchedPos == 0 && this.jjmatchedKind > 7) {
                                this.jjmatchedKind = 7;
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
                        break Label_0005_Outer;
                    }
                    if (this.jjmatchedPos + 1 < curPos) {
                        this.input_stream.backup(curPos - this.jjmatchedPos - 1);
                    }
                    if ((RccTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                        final Token matchedToken = this.jjFillToken();
                        matchedToken.specialToken = specialToken;
                        if (RccTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                            this.curLexState = RccTokenManager.jjnewLexState[this.jjmatchedKind];
                        }
                        return matchedToken;
                    }
                    if ((RccTokenManager.jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0x0L) {
                        this.jjimageLen += this.jjmatchedPos + 1;
                        if (RccTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                            this.curLexState = RccTokenManager.jjnewLexState[this.jjmatchedKind];
                        }
                        curPos = 0;
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        try {
                            this.curChar = this.input_stream.readChar();
                            continue Label_0005_Outer;
                        }
                        catch (IOException ex) {}
                        break Label_0005_Outer;
                    }
                    if ((RccTokenManager.jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
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
                    if (RccTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = RccTokenManager.jjnewLexState[this.jjmatchedKind];
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
    
    static {
        jjbitVec0 = new long[] { 0L, 0L, -1L, -1L };
        jjnextStates = new int[] { 1, 2 };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, null, "module", "class", "include", "byte", "boolean", "int", "long", "float", "double", "ustring", "buffer", "vector", "map", "{", "}", "<", ">", ";", ",", ".", null, null };
        lexStateNames = new String[] { "DEFAULT", "WithinOneLineComment", "WithinMultiLineComment" };
        jjnewLexState = new int[] { -1, -1, -1, -1, -1, 1, 0, -1, 2, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        jjtoToken = new long[] { 8589932545L };
        jjtoSkip = new long[] { 894L };
        jjtoSpecial = new long[] { 864L };
        jjtoMore = new long[] { 1152L };
    }
}
