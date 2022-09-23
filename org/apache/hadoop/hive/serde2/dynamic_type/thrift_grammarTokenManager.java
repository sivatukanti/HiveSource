// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import java.io.IOException;
import java.io.PrintStream;

public class thrift_grammarTokenManager implements thrift_grammarConstants
{
    public PrintStream debugStream;
    static final long[] jjbitVec0;
    static final int[] jjnextStates;
    public static final String[] jjstrLiteralImages;
    public static final String[] lexStateNames;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    protected SimpleCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
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
    
    private final int jjStopStringLiteralDfa_0(final int pos, final long active0, final long active1) {
        switch (pos) {
            case 0: {
                if ((active0 & 0xFFFFFFFFFFF00L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    return 35;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0xFFFFFFFFFFF00L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 1;
                    return 35;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x14380000000L) != 0x0L) {
                    return 35;
                }
                if ((active0 & 0xFFEBC7FFFFF00L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 2;
                    return 35;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x9008070000000L) != 0x0L) {
                    return 35;
                }
                if ((active0 & 0x6FE3C0FFFFF00L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 3;
                    return 35;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x23000000100L) != 0x0L) {
                    return 35;
                }
                if ((active0 & 0x6FC0C0FFFFE00L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 4;
                    return 35;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x480C00000000L) != 0x0L) {
                    return 35;
                }
                if ((active0 & 0x6B4000FFFFE00L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 5;
                    return 35;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x6100007BFFE00L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 6;
                    return 35;
                }
                if ((active0 & 0xA40008400000L) != 0x0L) {
                    return 35;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x100007BFEE00L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 7;
                    return 35;
                }
                if ((active0 & 0x6000000001000L) != 0x0L) {
                    return 35;
                }
                return -1;
            }
            case 8: {
                if ((active0 & 0x3BDEC00L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 8;
                    return 35;
                }
                if ((active0 & 0x100004020200L) != 0x0L) {
                    return 35;
                }
                return -1;
            }
            case 9: {
                if ((active0 & 0x3BDEC00L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 9;
                    return 35;
                }
                return -1;
            }
            case 10: {
                if ((active0 & 0x800L) != 0x0L) {
                    return 35;
                }
                if ((active0 & 0x3BDE400L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 10;
                    return 35;
                }
                return -1;
            }
            case 11: {
                if ((active0 & 0x1846000L) != 0x0L) {
                    return 35;
                }
                if ((active0 & 0x2398400L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 11;
                    return 35;
                }
                return -1;
            }
            case 12: {
                if ((active0 & 0x2010400L) != 0x0L) {
                    return 35;
                }
                if ((active0 & 0x388000L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 12;
                    return 35;
                }
                return -1;
            }
            case 13: {
                if ((active0 & 0x80000L) != 0x0L) {
                    return 35;
                }
                if ((active0 & 0x308000L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 13;
                    return 35;
                }
                return -1;
            }
            case 14: {
                if ((active0 & 0x308000L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 14;
                    return 35;
                }
                return -1;
            }
            case 15: {
                if ((active0 & 0x208000L) != 0x0L) {
                    return 35;
                }
                if ((active0 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 15;
                    return 35;
                }
                return -1;
            }
            case 16: {
                if ((active0 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 54;
                    this.jjmatchedPos = 16;
                    return 35;
                }
                return -1;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int jjStartNfa_0(final int pos, final long active0, final long active1) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
    }
    
    private int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '(': {
                return this.jjStopAtPos(0, 67);
            }
            case ')': {
                return this.jjStopAtPos(0, 68);
            }
            case ',': {
                return this.jjStopAtPos(0, 59);
            }
            case ':': {
                return this.jjStopAtPos(0, 66);
            }
            case ';': {
                return this.jjStopAtPos(0, 60);
            }
            case '<': {
                return this.jjStopAtPos(0, 69);
            }
            case '=': {
                return this.jjStopAtPos(0, 63);
            }
            case '>': {
                return this.jjStopAtPos(0, 70);
            }
            case '[': {
                return this.jjStopAtPos(0, 64);
            }
            case ']': {
                return this.jjStopAtPos(0, 65);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_0(2199023255552L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa1_0(1610612736L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa1_0(56576L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa1_0(17179869184L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_0(334251534843904L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_0(15166603264L);
            }
            case 'j': {
                return this.jjMoveStringLiteralDfa1_0(8192L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_0(549755813888L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa1_0(274877906944L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_0(512L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa1_0(1125899906842624L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa1_0(458752L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa1_0(562949953945600L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa1_0(2402673428004864L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_0(74766790688768L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa1_0(268435456L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa1_0(130023424L);
            }
            case '{': {
                return this.jjStopAtPos(0, 61);
            }
            case '}': {
                return this.jjStopAtPos(0, 62);
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
            this.jjStopStringLiteralDfa_0(0, active0, 0L);
            return 1;
        }
        switch (this.curChar) {
            case '1': {
                return this.jjMoveStringLiteralDfa2_0(active0, 2147483648L);
            }
            case '3': {
                return this.jjMoveStringLiteralDfa2_0(active0, 4294967296L);
            }
            case '6': {
                return this.jjMoveStringLiteralDfa2_0(active0, 8589934592L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, 274877915648L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa2_0(active0, 704924392620032L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa2_0(active0, 70368744243200L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa2_0(active0, 549755813888L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa2_0(active0, 2251799813685248L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa2_0(active0, 68719476736L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa2_0(active0, 3145728L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa2_0(active0, 281475110928384L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa2_0(active0, 17985192192L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1125899906849792L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa2_0(active0, 2199153311744L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa2_0(active0, 8830452760576L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa2_0(active0, 524288L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa2_0(active0, 52776558133248L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa2_0(active0, 4399120384000L);
            }
            default: {
                return this.jjStartNfa_0(0, active0, 0L);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa2_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(0, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(1, active0, 0L);
            return 2;
        }
        switch (this.curChar) {
            case '2': {
                if ((active0 & 0x100000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 32, 35);
                }
                break;
            }
            case '4': {
                if ((active0 & 0x200000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 33, 35);
                }
                break;
            }
            case '6': {
                if ((active0 & 0x80000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 31, 35);
                }
                break;
            }
            case '_': {
                return this.jjMoveStringLiteralDfa3_0(active0, 131072L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa3_0(active0, 3145728L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa3_0(active0, 524288L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa3_0(active0, 17592320278528L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa3_0(active0, 130023424L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa3_0(active0, 32768L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa3_0(active0, 2251868801597440L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa3_0(active0, 512L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa3_0(active0, 137438953728L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa3_0(active0, 536870912L);
            }
            case 'p': {
                if ((active0 & 0x4000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 38, 35);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 4398046583808L);
            }
            case 'q': {
                return this.jjMoveStringLiteralDfa3_0(active0, 562949953421312L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa3_0(active0, 219936685555712L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa3_0(active0, 549755813888L);
            }
            case 't': {
                if ((active0 & 0x10000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 40, 35);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 1161085352673280L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_0(active0, 281492156579840L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa3_0(active0, 8192L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa3_0(active0, 2199023255552L);
            }
        }
        return this.jjStartNfa_0(1, active0, 0L);
    }
    
    private int jjMoveStringLiteralDfa3_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(1, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(2, active0, 0L);
            return 3;
        }
        switch (this.curChar) {
            case '_': {
                return this.jjMoveStringLiteralDfa4_0(active0, 130096128L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa4_0(active0, 40960L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa4_0(active0, 17179869184L);
            }
            case 'd': {
                if ((active0 & 0x10000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 28, 35);
                }
                break;
            }
            case 'e': {
                if ((active0 & 0x40000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 30, 35);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 57174604644864L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa4_0(active0, 1125934266580992L);
            }
            case 'l': {
                if ((active0 & 0x20000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 29, 35);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 137625600L);
            }
            case 'm': {
                if ((active0 & 0x1000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 48, 35);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 131072L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa4_0(active0, 2199023255552L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa4_0(active0, 70368744194048L);
            }
            case 'p': {
                if ((active0 & 0x8000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 51, 35);
                }
                break;
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_0(active0, 68719476992L);
            }
            case 't': {
                if ((active0 & 0x8000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 39, 35);
                }
                break;
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa4_0(active0, 571883485396992L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa4_0(active0, 140737488355328L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa4_0(active0, 524288L);
            }
        }
        return this.jjStartNfa_0(2, active0, 0L);
    }
    
    private int jjMoveStringLiteralDfa4_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(2, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(3, active0, 0L);
            return 4;
        }
        switch (this.curChar) {
            case '_': {
                return this.jjMoveStringLiteralDfa5_0(active0, 794624L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa5_0(active0, 71319552L);
            }
            case 'c': {
                if ((active0 & 0x20000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 41, 35);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 8796093022208L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa5_0(active0, 4398046511104L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa5_0(active0, 703687441778688L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa5_0(active0, 17183014912L);
            }
            case 'm': {
                if ((active0 & 0x2000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 37, 35);
                }
                break;
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa5_0(active0, 35218782225408L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa5_0(active0, 1125899915362304L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa5_0(active0, 17592186044416L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa5_0(active0, 32768L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa5_0(active0, 512L);
            }
            case 't': {
                if ((active0 & 0x100L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 8, 35);
                }
                if ((active0 & 0x1000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 36, 35);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 4096L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa5_0(active0, 134217728L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa5_0(active0, 70368744177664L);
            }
        }
        return this.jjStartNfa_0(3, active0, 0L);
    }
    
    private int jjMoveStringLiteralDfa5_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(3, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(4, active0, 0L);
            return 5;
        }
        switch (this.curChar) {
            case '_': {
                return this.jjMoveStringLiteralDfa6_0(active0, 16384L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa6_0(active0, 33620992L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa6_0(active0, 140737488355328L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa6_0(active0, 35184506437632L);
            }
            case 'e': {
                if ((active0 & 0x400000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 34, 35);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 4398046511104L);
            }
            case 'g': {
                if ((active0 & 0x800000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 35, 35);
                }
                break;
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa6_0(active0, 16777216L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa6_0(active0, 4194304L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa6_0(active0, 1125899907368960L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa6_0(active0, 8692224L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa6_0(active0, 562949953421312L);
            }
            case 's': {
                if ((active0 & 0x400000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 46, 35);
                }
                break;
            }
            case 't': {
                if ((active0 & 0x80000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 43, 35);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 17592256299008L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa6_0(active0, 4096L);
            }
        }
        return this.jjStartNfa_0(4, active0, 0L);
    }
    
    private int jjMoveStringLiteralDfa6_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(4, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(5, active0, 0L);
            return 6;
        }
        switch (this.curChar) {
            case '_': {
                return this.jjMoveStringLiteralDfa7_0(active0, 32768L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa7_0(active0, 1125899910783488L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa7_0(active0, 2048L);
            }
            case 'e': {
                if ((active0 & 0x8000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 27, 35);
                }
                if ((active0 & 0x800000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 47, 35);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 562949953421312L);
            }
            case 'f': {
                if ((active0 & 0x40000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 42, 35);
                }
                break;
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa7_0(active0, 17592186044416L);
            }
            case 'l': {
                if ((active0 & 0x400000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 22, 35);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 16777216L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa7_0(active0, 33620992L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa7_0(active0, 20480L);
            }
            case 's': {
                if ((active0 & 0x200000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 45, 35);
                }
                break;
            }
            case 't': {
                return this.jjMoveStringLiteralDfa7_0(active0, 75497472L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa7_0(active0, 131072L);
            }
        }
        return this.jjStartNfa_0(5, active0, 0L);
    }
    
    private int jjMoveStringLiteralDfa7_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(5, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(6, active0, 0L);
            return 7;
        }
        switch (this.curChar) {
            case 'c': {
                return this.jjMoveStringLiteralDfa8_0(active0, 270848L);
            }
            case 'd': {
                if ((active0 & 0x2000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 49, 35);
                }
                break;
            }
            case 'e': {
                if ((active0 & 0x1000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 12, 35);
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 33620992L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa8_0(active0, 8388608L);
            }
            case 'l': {
                if ((active0 & 0x4000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 50, 35);
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 20056064L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa8_0(active0, 524288L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa8_0(active0, 32768L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa8_0(active0, 17592186044416L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa8_0(active0, 67125248L);
            }
        }
        return this.jjStartNfa_0(6, active0, 0L);
    }
    
    private int jjMoveStringLiteralDfa8_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(6, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(7, active0, 0L);
            return 8;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa9_0(active0, 16809984L);
            }
            case 'e': {
                if ((active0 & 0x200L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 9, 35);
                }
                if ((active0 & 0x20000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 17, 35);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 540672L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa9_0(active0, 3416064L);
            }
            case 'n': {
                if ((active0 & 0x100000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 44, 35);
                }
                break;
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa9_0(active0, 8388608L);
            }
            case 's': {
                if ((active0 & 0x4000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 26, 35);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 33620992L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa9_0(active0, 2048L);
            }
        }
        return this.jjStartNfa_0(7, active0, 0L);
    }
    
    private int jjMoveStringLiteralDfa9_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(7, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(8, active0, 0L);
            return 9;
        }
        switch (this.curChar) {
            case '_': {
                return this.jjMoveStringLiteralDfa10_0(active0, 3145728L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa10_0(active0, 270336L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa10_0(active0, 16777216L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa10_0(active0, 2048L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa10_0(active0, 16384L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa10_0(active0, 32768L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa10_0(active0, 8388608L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa10_0(active0, 33620992L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa10_0(active0, 524288L);
            }
            default: {
                return this.jjStartNfa_0(8, active0, 0L);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa10_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(8, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(9, active0, 0L);
            return 10;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa11_0(active0, 42009600L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa11_0(active0, 1048576L);
            }
            case 'e': {
                if ((active0 & 0x800L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(10, 11, 35);
                }
                return this.jjMoveStringLiteralDfa11_0(active0, 32768L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa11_0(active0, 270336L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa11_0(active0, 16384L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa11_0(active0, 16777216L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa11_0(active0, 2621440L);
            }
            default: {
                return this.jjStartNfa_0(9, active0, 0L);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa11_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(9, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(10, active0, 0L);
            return 11;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa12_0(active0, 1572864L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa12_0(active0, 33620992L);
            }
            case 'e': {
                if ((active0 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 13, 35);
                }
                if ((active0 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 18, 35);
                }
                if ((active0 & 0x1000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 24, 35);
                }
                break;
            }
            case 'l': {
                if ((active0 & 0x800000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 23, 35);
                }
                break;
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa12_0(active0, 2097152L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa12_0(active0, 32768L);
            }
            case 'x': {
                if ((active0 & 0x4000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 14, 35);
                }
                break;
            }
        }
        return this.jjStartNfa_0(10, active0, 0L);
    }
    
    private int jjMoveStringLiteralDfa12_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(10, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(11, active0, 0L);
            return 12;
        }
        switch (this.curChar) {
            case 'c': {
                return this.jjMoveStringLiteralDfa13_0(active0, 524288L);
            }
            case 'e': {
                if ((active0 & 0x400L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 10, 35);
                }
                if ((active0 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 16, 35);
                }
                if ((active0 & 0x2000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 25, 35);
                }
                return this.jjMoveStringLiteralDfa13_0(active0, 2097152L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa13_0(active0, 32768L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa13_0(active0, 1048576L);
            }
            default: {
                return this.jjStartNfa_0(11, active0, 0L);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa13_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(11, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(12, active0, 0L);
            return 13;
        }
        switch (this.curChar) {
            case 'a': {
                return this.jjMoveStringLiteralDfa14_0(active0, 32768L);
            }
            case 'e': {
                if ((active0 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(13, 19, 35);
                }
                return this.jjMoveStringLiteralDfa14_0(active0, 1048576L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa14_0(active0, 2097152L);
            }
            default: {
                return this.jjStartNfa_0(12, active0, 0L);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa14_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(12, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(13, active0, 0L);
            return 14;
        }
        switch (this.curChar) {
            case 'c': {
                return this.jjMoveStringLiteralDfa15_0(active0, 32768L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa15_0(active0, 1048576L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa15_0(active0, 2097152L);
            }
            default: {
                return this.jjStartNfa_0(13, active0, 0L);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa15_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(13, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(14, active0, 0L);
            return 15;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x8000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(15, 15, 35);
                }
                break;
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa16_0(active0, 1048576L);
            }
            case 'x': {
                if ((active0 & 0x200000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(15, 21, 35);
                }
                break;
            }
        }
        return this.jjStartNfa_0(14, active0, 0L);
    }
    
    private int jjMoveStringLiteralDfa16_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(14, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(15, active0, 0L);
            return 16;
        }
        switch (this.curChar) {
            case 'r': {
                return this.jjMoveStringLiteralDfa17_0(active0, 1048576L);
            }
            default: {
                return this.jjStartNfa_0(15, active0, 0L);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa17_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(15, old0, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException e) {
            this.jjStopStringLiteralDfa_0(16, active0, 0L);
            return 17;
        }
        switch (this.curChar) {
            case 'y': {
                if ((active0 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(17, 20, 35);
                }
                break;
            }
        }
        return this.jjStartNfa_0(16, active0, 0L);
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
        this.jjnewStateCnt = 35;
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
                        case 35: {
                            if ((0x3FF600000000000L & l) != 0x0L) {
                                if (kind > 58) {
                                    kind = 58;
                                }
                                this.jjCheckNAdd(15);
                            }
                            if ((0x3FF400000000000L & l) != 0x0L) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                                this.jjCheckNAdd(7);
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                                this.jjCheckNAdd(7);
                            }
                            else if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                            }
                            else if (this.curChar == '/') {
                                this.jjAddStates(3, 4);
                            }
                            else if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(12, 13);
                            }
                            else if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(9, 10);
                            }
                            else if (this.curChar == '#') {
                                this.jjCheckNAddStates(5, 7);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 52) {
                                    kind = 52;
                                }
                                this.jjCheckNAdd(5);
                                continue;
                            }
                            if (this.curChar == '-') {
                                if (kind > 58) {
                                    kind = 58;
                                }
                                this.jjCheckNAdd(15);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0xFFFFFFFFFFFFFBFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(5, 7);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if ((0x2400L & l) != 0x0L && kind > 5) {
                                kind = 5;
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (this.curChar == '\n' && kind > 5) {
                                kind = 5;
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 52) {
                                kind = 52;
                            }
                            this.jjCheckNAdd(5);
                            continue;
                        }
                        case 6: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAdd(7);
                            continue;
                        }
                        case 7: {
                            if ((0x3FF400000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAdd(7);
                            continue;
                        }
                        case 8: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(9, 10);
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(9, 10);
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if (this.curChar == '\"' && kind > 57) {
                                kind = 57;
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(12, 13);
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(12, 13);
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if (this.curChar == '\'' && kind > 57) {
                                kind = 57;
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if (this.curChar != '-') {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAdd(15);
                            continue;
                        }
                        case 15: {
                            if ((0x3FF600000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAdd(15);
                            continue;
                        }
                        case 16: {
                            if (this.curChar == '/') {
                                this.jjAddStates(3, 4);
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            if (this.curChar == '/') {
                                this.jjCheckNAddStates(8, 10);
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(8, 10);
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if ((0x2400L & l) != 0x0L && kind > 6) {
                                kind = 6;
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if (this.curChar == '\n' && kind > 6) {
                                kind = 6;
                                continue;
                            }
                            continue;
                        }
                        case 21: {
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 20;
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if (this.curChar == '*') {
                                this.jjCheckNAddTwoStates(23, 24);
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(23, 24);
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if (this.curChar == '*') {
                                this.jjAddStates(11, 12);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if ((0xFFFF7FFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(26, 24);
                                continue;
                            }
                            continue;
                        }
                        case 26: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(26, 24);
                                continue;
                            }
                            continue;
                        }
                        case 27: {
                            if (this.curChar == '/' && kind > 7) {
                                kind = 7;
                                continue;
                            }
                            continue;
                        }
                        case 28: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(29, 30);
                                continue;
                            }
                            continue;
                        }
                        case 30: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(31);
                                continue;
                            }
                            continue;
                        }
                        case 31: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(31, 32);
                            continue;
                        }
                        case 33: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(34);
                                continue;
                            }
                            continue;
                        }
                        case 34: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAdd(34);
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
                        case 35: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 58) {
                                    kind = 58;
                                }
                                this.jjCheckNAdd(15);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                                this.jjCheckNAdd(7);
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                                this.jjCheckNAdd(7);
                            }
                            if ((0x7FFFFFE07FFFFFEL & l) != 0x0L) {
                                if (kind > 58) {
                                    kind = 58;
                                }
                                this.jjCheckNAdd(15);
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAdd(7);
                            continue;
                        }
                        case 7: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAdd(7);
                            continue;
                        }
                        case 14: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAdd(15);
                            continue;
                        }
                        case 15: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAdd(15);
                            continue;
                        }
                        case 32: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(17, 18);
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 1: {
                            this.jjAddStates(5, 7);
                            continue;
                        }
                        case 9: {
                            this.jjAddStates(13, 14);
                            continue;
                        }
                        case 12: {
                            this.jjAddStates(15, 16);
                            continue;
                        }
                        case 18: {
                            this.jjAddStates(8, 10);
                            continue;
                        }
                        case 23: {
                            this.jjCheckNAddTwoStates(23, 24);
                            continue;
                        }
                        case 25:
                        case 26: {
                            this.jjCheckNAddTwoStates(26, 24);
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
                            if ((thrift_grammarTokenManager.jjbitVec0[i2] & l2) != 0x0L) {
                                this.jjAddStates(5, 7);
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            if ((thrift_grammarTokenManager.jjbitVec0[i2] & l2) != 0x0L) {
                                this.jjAddStates(13, 14);
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if ((thrift_grammarTokenManager.jjbitVec0[i2] & l2) != 0x0L) {
                                this.jjAddStates(15, 16);
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if ((thrift_grammarTokenManager.jjbitVec0[i2] & l2) != 0x0L) {
                                this.jjAddStates(8, 10);
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if ((thrift_grammarTokenManager.jjbitVec0[i2] & l2) != 0x0L) {
                                this.jjCheckNAddTwoStates(23, 24);
                                continue;
                            }
                            continue;
                        }
                        case 25:
                        case 26: {
                            if ((thrift_grammarTokenManager.jjbitVec0[i2] & l2) != 0x0L) {
                                this.jjCheckNAddTwoStates(26, 24);
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
            final int n2 = 35;
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
    
    public thrift_grammarTokenManager(final SimpleCharStream stream) {
        this.debugStream = System.out;
        this.jjrounds = new int[35];
        this.jjstateSet = new int[70];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = stream;
    }
    
    public thrift_grammarTokenManager(final SimpleCharStream stream, final int lexState) {
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
        int i = 35;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final SimpleCharStream stream, final int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }
    
    public void SwitchTo(final int lexState) {
        if (lexState >= 1 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
    
    protected Token jjFillToken() {
        final String im = thrift_grammarTokenManager.jjstrLiteralImages[this.jjmatchedKind];
        final String curTokenImage = (im == null) ? this.input_stream.GetImage() : im;
        final int beginLine = this.input_stream.getBeginLine();
        final int beginColumn = this.input_stream.getBeginColumn();
        final int endLine = this.input_stream.getEndLine();
        final int endColumn = this.input_stream.getEndColumn();
        final Token t = Token.newToken(this.jjmatchedKind);
        t.kind = this.jjmatchedKind;
        t.image = curTokenImage;
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }
    
    public Token getNextToken() {
        int curPos = 0;
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (IOException e) {
                this.jjmatchedKind = 0;
                final Token matchedToken = this.jjFillToken();
                return matchedToken;
            }
            try {
                this.input_stream.backup(0);
                while (this.curChar <= ' ' && (0x100002600L & 1L << this.curChar) != 0x0L) {
                    this.curChar = this.input_stream.BeginToken();
                }
            }
            catch (IOException e2) {
                continue;
            }
            this.jjmatchedKind = Integer.MAX_VALUE;
            this.jjmatchedPos = 0;
            curPos = this.jjMoveStringLiteralDfa0_0();
            if (this.jjmatchedKind == Integer.MAX_VALUE) {
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
            if (this.jjmatchedPos + 1 < curPos) {
                this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            }
            if ((thrift_grammarTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                final Token matchedToken = this.jjFillToken();
                return matchedToken;
            }
        }
    }
    
    private void jjCheckNAdd(final int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }
    
    private void jjAddStates(int start, final int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = thrift_grammarTokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(thrift_grammarTokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    static {
        jjbitVec0 = new long[] { 0L, 0L, -1L, -1L };
        jjnextStates = new int[] { 5, 29, 30, 17, 22, 1, 2, 4, 18, 19, 21, 25, 27, 9, 10, 12, 13, 33, 34 };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, "const", "namespace", "cpp_namespace", "cpp_include", "cpp_type", "java_package", "cocoa_prefix", "csharp_namespace", "php_namespace", "py_module", "perl_package", "ruby_namespace", "smalltalk_category", "smalltalk_prefix", "xsd_all", "xsd_optional", "xsd_nillable", "xsd_namespace", "xsd_attrs", "include", "void", "bool", "byte", "i16", "i32", "i64", "double", "string", "slist", "senum", "map", "list", "set", "async", "typedef", "struct", "exception", "extends", "throws", "service", "enum", "required", "optional", "skip", null, null, null, null, null, null, null, ",", ";", "{", "}", "=", "[", "]", ":", "(", ")", "<", ">" };
        lexStateNames = new String[] { "DEFAULT" };
        jjtoToken = new long[] { -108086391056892159L, 127L };
        jjtoSkip = new long[] { 254L, 0L };
    }
}
