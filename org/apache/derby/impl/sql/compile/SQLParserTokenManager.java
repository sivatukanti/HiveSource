// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.io.IOException;
import java.io.PrintStream;

public class SQLParserTokenManager implements SQLParserConstants
{
    int commentNestingDepth;
    public PrintStream debugStream;
    static final long[] jjbitVec0;
    static final long[] jjbitVec2;
    static final long[] jjbitVec3;
    static final long[] jjbitVec4;
    static final long[] jjbitVec5;
    static final long[] jjbitVec6;
    static final long[] jjbitVec7;
    static final long[] jjbitVec8;
    static final long[] jjbitVec9;
    static final long[] jjbitVec10;
    static final long[] jjbitVec11;
    static final long[] jjbitVec12;
    static final long[] jjbitVec13;
    static final long[] jjbitVec14;
    static final long[] jjbitVec15;
    static final long[] jjbitVec16;
    static final long[] jjbitVec17;
    static final long[] jjbitVec18;
    static final long[] jjbitVec19;
    static final long[] jjbitVec20;
    static final long[] jjbitVec21;
    static final long[] jjbitVec22;
    static final long[] jjbitVec23;
    static final long[] jjbitVec24;
    static final long[] jjbitVec25;
    static final long[] jjbitVec26;
    static final long[] jjbitVec27;
    static final long[] jjbitVec28;
    static final long[] jjbitVec29;
    static final long[] jjbitVec30;
    static final long[] jjbitVec31;
    static final long[] jjbitVec32;
    static final long[] jjbitVec33;
    static final long[] jjbitVec34;
    static final long[] jjbitVec35;
    static final long[] jjbitVec36;
    static final long[] jjbitVec37;
    static final long[] jjbitVec38;
    static final long[] jjbitVec39;
    static final long[] jjbitVec40;
    static final long[] jjbitVec41;
    static final int[] jjnextStates;
    public static final String[] jjstrLiteralImages;
    public static final String[] lexStateNames;
    public static final int[] jjnewLexState;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    static final long[] jjtoMore;
    protected CharStream input_stream;
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
    
    void CommonTokenAction(final Token token) {
        token.beginOffset = this.input_stream.getBeginOffset();
        token.endOffset = this.input_stream.getEndOffset();
    }
    
    public void setDebugStream(final PrintStream debugStream) {
        this.debugStream = debugStream;
    }
    
    private final int jjStopAtPos(final int jjmatchedPos, final int jjmatchedKind) {
        this.jjmatchedKind = jjmatchedKind;
        return (this.jjmatchedPos = jjmatchedPos) + 1;
    }
    
    private final int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case '*': {
                return this.jjMoveStringLiteralDfa1_1(128L);
            }
            case '/': {
                return this.jjMoveStringLiteralDfa1_1(64L);
            }
            default: {
                return 1;
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa1_1(final long n) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return 1;
        }
        switch (this.curChar) {
            case '*': {
                if ((n & 0x40L) != 0x0L) {
                    return this.jjStopAtPos(1, 6);
                }
                break;
            }
            case '/': {
                if ((n & 0x80L) != 0x0L) {
                    return this.jjStopAtPos(1, 7);
                }
                break;
            }
            default: {
                return 2;
            }
        }
        return 2;
    }
    
    private final int jjStopStringLiteralDfa_17(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_17(final int n, final long n2) {
        return this.jjMoveNfa_17(this.jjStopStringLiteralDfa_17(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_17(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_17(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_17() {
        switch (this.curChar) {
            case 'E': {
                return this.jjStopAtPos(0, 56);
            }
            case 'e': {
                return this.jjStopAtPos(0, 56);
            }
            default: {
                return this.jjMoveNfa_17(0, 0);
            }
        }
    }
    
    private final void jjCheckNAdd(final int n) {
        if (this.jjrounds[n] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = n;
            this.jjrounds[n] = this.jjround;
        }
    }
    
    private final void jjAddStates(int n, final int n2) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = SQLParserTokenManager.jjnextStates[n];
        } while (n++ != n2);
    }
    
    private final void jjCheckNAddTwoStates(final int n, final int n2) {
        this.jjCheckNAdd(n);
        this.jjCheckNAdd(n2);
    }
    
    private final void jjCheckNAddStates(int n, final int n2) {
        do {
            this.jjCheckNAdd(SQLParserTokenManager.jjnextStates[n]);
        } while (n++ != n2);
    }
    
    private final void jjCheckNAddStates(final int n) {
        this.jjCheckNAdd(SQLParserTokenManager.jjnextStates[n]);
        this.jjCheckNAdd(SQLParserTokenManager.jjnextStates[n + 1]);
    }
    
    private final int jjMoveNfa_17(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 58) {
                                jjmatchedKind = 58;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 57) {
                                jjmatchedKind = 57;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 57) {
                                jjmatchedKind = 57;
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
                        case 3: {
                            if (jjmatchedKind > 58) {
                                jjmatchedKind = 58;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFDFFFFFFFDFL & n4) != 0x0L) {
                                jjmatchedKind = 58;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 58) {
                                jjmatchedKind = 58;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjStopStringLiteralDfa_16(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_16(final int n, final long n2) {
        return this.jjMoveNfa_16(this.jjStopStringLiteralDfa_16(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_16(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_16(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_16() {
        switch (this.curChar) {
            case 'I': {
                return this.jjStopAtPos(0, 53);
            }
            case 'i': {
                return this.jjStopAtPos(0, 53);
            }
            default: {
                return this.jjMoveNfa_16(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_16(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 55) {
                                jjmatchedKind = 55;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 54) {
                                jjmatchedKind = 54;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 54) {
                                jjmatchedKind = 54;
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
                        case 3: {
                            if (jjmatchedKind > 55) {
                                jjmatchedKind = 55;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFDFFFFFFFDFFL & n4) != 0x0L) {
                                jjmatchedKind = 55;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 55) {
                                jjmatchedKind = 55;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjMoveStringLiteralDfa0_19() {
        return this.jjMoveNfa_19(4, 0);
    }
    
    private final int jjMoveNfa_19(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 4: {
                            if ((0xFFFFFFFFFFFFDBFFL & n3) != 0x0L) {
                                if (jjmatchedKind > 63) {
                                    jjmatchedKind = 63;
                                }
                                this.jjCheckNAddStates(0, 2);
                            }
                            else if ((0x2400L & n3) != 0x0L && jjmatchedKind > 63) {
                                jjmatchedKind = 63;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0xFFFFFFFFFFFFDBFFL & n3) == 0x0L) {
                                continue;
                            }
                            jjmatchedKind = 63;
                            this.jjCheckNAddStates(0, 2);
                            continue;
                        }
                        case 1: {
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 63) {
                                jjmatchedKind = 63;
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if (this.curChar == '\n' && jjmatchedKind > 63) {
                                jjmatchedKind = 63;
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0:
                        case 4: {
                            jjmatchedKind = 63;
                            this.jjCheckNAddStates(0, 2);
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0:
                        case 4: {
                            if (!jjCanMove_0(n5, n6, n8, n7, n9)) {
                                continue;
                            }
                            if (jjmatchedKind > 63) {
                                jjmatchedKind = 63;
                            }
                            this.jjCheckNAddStates(0, 2);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjStopStringLiteralDfa_12(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_12(final int n, final long n2) {
        return this.jjMoveNfa_12(this.jjStopStringLiteralDfa_12(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_12(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_12(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_12() {
        switch (this.curChar) {
            case 'P': {
                return this.jjStopAtPos(0, 41);
            }
            case 'p': {
                return this.jjStopAtPos(0, 41);
            }
            default: {
                return this.jjMoveNfa_12(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_12(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 43) {
                                jjmatchedKind = 43;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 42) {
                                jjmatchedKind = 42;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 42) {
                                jjmatchedKind = 42;
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
                        case 3: {
                            if (jjmatchedKind > 43) {
                                jjmatchedKind = 43;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFEFFFFFFFEFFFFL & n4) != 0x0L) {
                                jjmatchedKind = 43;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 43) {
                                jjmatchedKind = 43;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjStopStringLiteralDfa_6(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_6(final int n, final long n2) {
        return this.jjMoveNfa_6(this.jjStopStringLiteralDfa_6(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_6(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_6(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_6() {
        switch (this.curChar) {
            case 'B': {
                return this.jjStopAtPos(0, 23);
            }
            case 'b': {
                return this.jjStopAtPos(0, 23);
            }
            default: {
                return this.jjMoveNfa_6(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_6(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 25) {
                                jjmatchedKind = 25;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 24) {
                                jjmatchedKind = 24;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 24) {
                                jjmatchedKind = 24;
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
                        case 3: {
                            if (jjmatchedKind > 25) {
                                jjmatchedKind = 25;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFBFFFFFFFBL & n4) != 0x0L) {
                                jjmatchedKind = 25;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 25) {
                                jjmatchedKind = 25;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case '*': {
                return this.jjMoveStringLiteralDfa1_2(512L);
            }
            case '/': {
                return this.jjMoveStringLiteralDfa1_2(256L);
            }
            default: {
                return 1;
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa1_2(final long n) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return 1;
        }
        switch (this.curChar) {
            case '*': {
                if ((n & 0x100L) != 0x0L) {
                    return this.jjStopAtPos(1, 8);
                }
                break;
            }
            case '/': {
                if ((n & 0x200L) != 0x0L) {
                    return this.jjStopAtPos(1, 9);
                }
                break;
            }
            default: {
                return 2;
            }
        }
        return 2;
    }
    
    private final int jjStopStringLiteralDfa_15(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_15(final int n, final long n2) {
        return this.jjMoveNfa_15(this.jjStopStringLiteralDfa_15(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_15(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_15(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_15() {
        switch (this.curChar) {
            case 'T': {
                return this.jjStopAtPos(0, 50);
            }
            case 't': {
                return this.jjStopAtPos(0, 50);
            }
            default: {
                return this.jjMoveNfa_15(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_15(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 52) {
                                jjmatchedKind = 52;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 51) {
                                jjmatchedKind = 51;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 51) {
                                jjmatchedKind = 51;
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
                        case 3: {
                            if (jjmatchedKind > 52) {
                                jjmatchedKind = 52;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFEFFFFFFFEFFFFFL & n4) != 0x0L) {
                                jjmatchedKind = 52;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 52) {
                                jjmatchedKind = 52;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjStopStringLiteralDfa_13(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_13(final int n, final long n2) {
        return this.jjMoveNfa_13(this.jjStopStringLiteralDfa_13(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_13(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_13(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_13() {
        switch (this.curChar) {
            case 'E': {
                return this.jjStopAtPos(0, 44);
            }
            case 'e': {
                return this.jjStopAtPos(0, 44);
            }
            default: {
                return this.jjMoveNfa_13(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_13(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 46) {
                                jjmatchedKind = 46;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 45) {
                                jjmatchedKind = 45;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 45) {
                                jjmatchedKind = 45;
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
                        case 3: {
                            if (jjmatchedKind > 46) {
                                jjmatchedKind = 46;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFDFFFFFFFDFL & n4) != 0x0L) {
                                jjmatchedKind = 46;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 46) {
                                jjmatchedKind = 46;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjStopStringLiteralDfa_7(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_7(final int n, final long n2) {
        return this.jjMoveNfa_7(this.jjStopStringLiteralDfa_7(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_7(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_7(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_7() {
        switch (this.curChar) {
            case 'Y': {
                return this.jjStopAtPos(0, 26);
            }
            case 'y': {
                return this.jjStopAtPos(0, 26);
            }
            default: {
                return this.jjMoveNfa_7(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_7(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 28) {
                                jjmatchedKind = 28;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 27) {
                                jjmatchedKind = 27;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 27) {
                                jjmatchedKind = 27;
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
                        case 3: {
                            if (jjmatchedKind > 28) {
                                jjmatchedKind = 28;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFDFFFFFFFDFFFFFFL & n4) != 0x0L) {
                                jjmatchedKind = 28;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 28) {
                                jjmatchedKind = 28;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjStopStringLiteralDfa_11(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_11(final int n, final long n2) {
        return this.jjMoveNfa_11(this.jjStopStringLiteralDfa_11(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_11(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_11(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_11() {
        switch (this.curChar) {
            case 'O': {
                return this.jjStopAtPos(0, 38);
            }
            case 'o': {
                return this.jjStopAtPos(0, 38);
            }
            default: {
                return this.jjMoveNfa_11(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_11(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 40) {
                                jjmatchedKind = 40;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 39) {
                                jjmatchedKind = 39;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 39) {
                                jjmatchedKind = 39;
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
                        case 3: {
                            if (jjmatchedKind > 40) {
                                jjmatchedKind = 40;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFF7FFFFFFF7FFFL & n4) != 0x0L) {
                                jjmatchedKind = 40;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 40) {
                                jjmatchedKind = 40;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjMoveStringLiteralDfa0_3() {
        switch (this.curChar) {
            case '\t': {
                this.jjmatchedKind = 13;
                this.jjmatchedPos = 0;
                return this.jjMoveNfa_3(0, 0);
            }
            case ' ': {
                this.jjmatchedKind = 12;
                this.jjmatchedPos = 0;
                return this.jjMoveNfa_3(0, 0);
            }
            case 'D': {
                this.jjmatchedKind = 14;
                this.jjmatchedPos = 0;
                return this.jjMoveNfa_3(0, 0);
            }
            case 'd': {
                this.jjmatchedKind = 14;
                this.jjmatchedPos = 0;
                return this.jjMoveNfa_3(0, 0);
            }
            default: {
                return this.jjMoveNfa_3(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_3(final int n, int a) {
        final int jjmatchedKind = this.jjmatchedKind;
        final int jjmatchedPos = this.jjmatchedPos;
        final int n2;
        this.input_stream.backup(n2 = a + 1);
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            throw new Error("Internal Error");
        }
        a = 0;
        int n3 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind2 = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n4 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind2 > 16) {
                                jjmatchedKind2 = 16;
                            }
                            if ((0x2400L & n4) != 0x0L && jjmatchedKind2 > 15) {
                                jjmatchedKind2 = 15;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind2 > 15) {
                                jjmatchedKind2 = 15;
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
                        case 3: {
                            if (jjmatchedKind2 > 16) {
                                jjmatchedKind2 = 16;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n3);
            }
            else if (this.curChar < '\u0080') {
                final long n5 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFEFFFFFFFEFL & n5) != 0x0L) {
                                jjmatchedKind2 = 16;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n3);
            }
            else {
                final int n6 = this.curChar >> 8;
                final int n7 = n6 >> 6;
                final long n8 = 1L << (n6 & 0x3F);
                final int n9 = (this.curChar & '\u00ff') >> 6;
                final long n10 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n6, n7, n9, n8, n10) && jjmatchedKind2 > 16) {
                                jjmatchedKind2 = 16;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n3);
            }
            if (jjmatchedKind2 != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind2;
                this.jjmatchedPos = a;
                jjmatchedKind2 = Integer.MAX_VALUE;
            }
            ++a;
            final int n11 = i = this.jjnewStateCnt;
            final int n12 = 4;
            final int jjnewStateCnt = n3;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n11 == (n3 = n12 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
                continue;
            }
            catch (IOException ex2) {}
            break;
        }
        if (this.jjmatchedPos > jjmatchedPos) {
            return a;
        }
        final int max = Math.max(a, n2);
        if (a < max) {
            int n13 = max - Math.min(a, n2);
            while (n13-- > 0) {
                try {
                    this.curChar = this.input_stream.readChar();
                    continue;
                }
                catch (IOException ex3) {
                    throw new Error("Internal Error : Please send a bug report.");
                }
                break;
            }
        }
        if (this.jjmatchedPos < jjmatchedPos) {
            this.jjmatchedKind = jjmatchedKind;
            this.jjmatchedPos = jjmatchedPos;
        }
        else if (this.jjmatchedPos == jjmatchedPos && this.jjmatchedKind > jjmatchedKind) {
            this.jjmatchedKind = jjmatchedKind;
        }
        return max;
    }
    
    private final int jjMoveStringLiteralDfa0_20() {
        return this.jjMoveNfa_20(4, 0);
    }
    
    private final int jjMoveNfa_20(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 4: {
                            if ((0xFFFFFFFFFFFFDBFFL & n3) != 0x0L) {
                                if (jjmatchedKind > 64) {
                                    jjmatchedKind = 64;
                                }
                                this.jjCheckNAddStates(0, 2);
                            }
                            else if ((0x2400L & n3) != 0x0L && jjmatchedKind > 64) {
                                jjmatchedKind = 64;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0xFFFFFFFFFFFFDBFFL & n3) == 0x0L) {
                                continue;
                            }
                            jjmatchedKind = 64;
                            this.jjCheckNAddStates(0, 2);
                            continue;
                        }
                        case 1: {
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 64) {
                                jjmatchedKind = 64;
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if (this.curChar == '\n' && jjmatchedKind > 64) {
                                jjmatchedKind = 64;
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0:
                        case 4: {
                            jjmatchedKind = 64;
                            this.jjCheckNAddStates(0, 2);
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0:
                        case 4: {
                            if (!jjCanMove_0(n5, n6, n8, n7, n9)) {
                                continue;
                            }
                            if (jjmatchedKind > 64) {
                                jjmatchedKind = 64;
                            }
                            this.jjCheckNAddStates(0, 2);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjStopStringLiteralDfa_5(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_5(final int n, final long n2) {
        return this.jjMoveNfa_5(this.jjStopStringLiteralDfa_5(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_5(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_5(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_5() {
        switch (this.curChar) {
            case 'R': {
                return this.jjStopAtPos(0, 20);
            }
            case 'r': {
                return this.jjStopAtPos(0, 20);
            }
            default: {
                return this.jjMoveNfa_5(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_5(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 22) {
                                jjmatchedKind = 22;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 21) {
                                jjmatchedKind = 21;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 21) {
                                jjmatchedKind = 21;
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
                        case 3: {
                            if (jjmatchedKind > 22) {
                                jjmatchedKind = 22;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFBFFFFFFFBFFFFL & n4) != 0x0L) {
                                jjmatchedKind = 22;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 22) {
                                jjmatchedKind = 22;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjStopStringLiteralDfa_4(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_4(final int n, final long n2) {
        return this.jjMoveNfa_4(this.jjStopStringLiteralDfa_4(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_4(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_4(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_4() {
        switch (this.curChar) {
            case 'E': {
                return this.jjStopAtPos(0, 17);
            }
            case 'e': {
                return this.jjStopAtPos(0, 17);
            }
            default: {
                return this.jjMoveNfa_4(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_4(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 19) {
                                jjmatchedKind = 19;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 18) {
                                jjmatchedKind = 18;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 18) {
                                jjmatchedKind = 18;
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
                        case 3: {
                            if (jjmatchedKind > 19) {
                                jjmatchedKind = 19;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFDFFFFFFFDFL & n4) != 0x0L) {
                                jjmatchedKind = 19;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 19) {
                                jjmatchedKind = 19;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjStopStringLiteralDfa_8(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_8(final int n, final long n2) {
        return this.jjMoveNfa_8(this.jjStopStringLiteralDfa_8(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_8(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_8(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_8() {
        switch (this.curChar) {
            case '-': {
                return this.jjStopAtPos(0, 29);
            }
            default: {
                return this.jjMoveNfa_8(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_8(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFDFFFFFFFFFFFL & n3) != 0x0L && jjmatchedKind > 31) {
                                jjmatchedKind = 31;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 30) {
                                jjmatchedKind = 30;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 30) {
                                jjmatchedKind = 30;
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
                        case 3: {
                            if ((0xFFFFDFFFFFFFFFFFL & n3) != 0x0L && jjmatchedKind > 31) {
                                jjmatchedKind = 31;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            jjmatchedKind = 31;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 31) {
                                jjmatchedKind = 31;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjStopStringLiteralDfa_10(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_10(final int n, final long n2) {
        return this.jjMoveNfa_10(this.jjStopStringLiteralDfa_10(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_10(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_10(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_10() {
        switch (this.curChar) {
            case 'R': {
                return this.jjStopAtPos(0, 35);
            }
            case 'r': {
                return this.jjStopAtPos(0, 35);
            }
            default: {
                return this.jjMoveNfa_10(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_10(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 37) {
                                jjmatchedKind = 37;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 36) {
                                jjmatchedKind = 36;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 36) {
                                jjmatchedKind = 36;
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
                        case 3: {
                            if (jjmatchedKind > 37) {
                                jjmatchedKind = 37;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFBFFFFFFFBFFFFL & n4) != 0x0L) {
                                jjmatchedKind = 37;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 37) {
                                jjmatchedKind = 37;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '\t': {
                this.jjmatchedKind = 2;
                return this.jjMoveNfa_0(0, 0);
            }
            case '\n': {
                this.jjmatchedKind = 3;
                return this.jjMoveNfa_0(0, 0);
            }
            case '\r': {
                this.jjmatchedKind = 4;
                return this.jjMoveNfa_0(0, 0);
            }
            case ' ': {
                this.jjmatchedKind = 1;
                return this.jjMoveNfa_0(0, 0);
            }
            case '!': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 65536L);
            }
            case '\"': {
                this.jjmatchedKind = 443;
                return this.jjMoveNfa_0(0, 0);
            }
            case '%': {
                this.jjmatchedKind = 444;
                return this.jjMoveNfa_0(0, 0);
            }
            case '&': {
                this.jjmatchedKind = 445;
                return this.jjMoveNfa_0(0, 0);
            }
            case '\'': {
                this.jjmatchedKind = 446;
                return this.jjMoveNfa_0(0, 0);
            }
            case '(': {
                this.jjmatchedKind = 449;
                return this.jjMoveNfa_0(0, 0);
            }
            case ')': {
                this.jjmatchedKind = 450;
                return this.jjMoveNfa_0(0, 0);
            }
            case '*': {
                this.jjmatchedKind = 451;
                return this.jjMoveNfa_0(0, 0);
            }
            case '+': {
                this.jjmatchedKind = 452;
                return this.jjMoveNfa_0(0, 0);
            }
            case ',': {
                this.jjmatchedKind = 453;
                return this.jjMoveNfa_0(0, 0);
            }
            case '-': {
                this.jjmatchedKind = 454;
                return this.jjMoveStringLiteralDfa1_0(2048L, 0L, 0L, 0L, 0L, 0L, 0L, 33554432L);
            }
            case '.': {
                this.jjmatchedKind = 455;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 67108864L);
            }
            case '/': {
                this.jjmatchedKind = 456;
                return this.jjMoveStringLiteralDfa1_0(32L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
            }
            case ':': {
                this.jjmatchedKind = 457;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 1024L);
            }
            case ';': {
                this.jjmatchedKind = 459;
                return this.jjMoveNfa_0(0, 0);
            }
            case '<': {
                this.jjmatchedKind = 460;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 40960L);
            }
            case '=': {
                this.jjmatchedKind = 462;
                return this.jjMoveNfa_0(0, 0);
            }
            case '>': {
                this.jjmatchedKind = 465;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 262144L);
            }
            case '?': {
                this.jjmatchedKind = 467;
                return this.jjMoveNfa_0(0, 0);
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa1_0(0L, 16382L, 0L, 0L, 491520L, 864691128455135232L, 0L, 0L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa1_0(0L, 1032192L, 0L, 0L, 524288L, 1152939165512368128L, 0L, 0L);
            }
            case 'C': {
                this.jjmatchedKind = 276;
                return this.jjMoveStringLiteralDfa1_0(0L, 1125899905794048L, 0L, 0L, 266338304L, -2305840947629391872L, 3L, 0L);
            }
            case 'D': {
                this.jjmatchedKind = 114;
                return this.jjMoveStringLiteralDfa1_0(0L, -2251799813685248L, 3L, 0L, 16911433728L, 2199023255552L, 252L, 0L);
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 4092L, 0L, 0L, 8796093022208L, 1792L, 0L);
            }
            case 'F': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 4190208L, 0L, 17179869184L, 0L, 2048L, 0L);
            }
            case 'G': {
                this.jjmatchedKind = 478;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 264241152L, 0L, 34359738368L, 4398046511104L, 0L, 0L);
            }
            case 'H': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 805306368L, 0L, 0L, 0L, 0L, 0L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 35183298347008L, 0L, 4329327034368L, 0L, 12288L, 0L);
            }
            case 'J': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 35184372088832L, 0L, 0L, 0L, 16384L, 0L);
            }
            case 'K': {
                this.jjmatchedKind = 476;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 70368744177664L, 0L, 0L, 0L, 0L, 0L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 4362862139015168L, 0L, 1121501860331520L, 105553116266496L, 229376L, 0L);
            }
            case 'M': {
                this.jjmatchedKind = 477;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 139611588448485376L, 0L, 287104476244869120L, 0L, 1835008L, 0L);
            }
            case 'N': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, -144115188075855872L, 3L, 8935141660703064064L, 140737488355328L, 14680064L, 0L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 4092L, Long.MIN_VALUE, 281474976710657L, 520093696L, 0L);
            }
            case 'P': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 2093056L, 0L, 14L, 8053063680L, 0L);
            }
            case 'R': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 1071644672L, 0L, 3940649673950192L, 8787503087616L, 0L);
            }
            case 'S': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 70367670435840L, 0L, 4503599694478336L, 18005602416459776L, 0L);
            }
            case 'T': {
                this.jjmatchedKind = 238;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 576320014815068160L, 0L, 8522825728L, 54043195528445952L, 0L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, -576460752303423488L, 3L, 25769803776L, 216172782113783808L, 0L);
            }
            case 'V': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 252L, 0L, 0L, 0L);
            }
            case 'W': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 16128L, 34359738368L, 288230376151711744L, 0L);
            }
            case 'X': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 279223176896970752L, 0L, 0L);
            }
            case 'Y': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 16384L, 0L, 0L, 0L);
            }
            case '[': {
                this.jjmatchedKind = 470;
                return this.jjMoveNfa_0(0, 0);
            }
            case ']': {
                this.jjmatchedKind = 471;
                return this.jjMoveNfa_0(0, 0);
            }
            case '_': {
                this.jjmatchedKind = 468;
                return this.jjMoveNfa_0(0, 0);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_0(0L, 16382L, 0L, 0L, 491520L, 864691128455135232L, 0L, 0L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa1_0(0L, 1032192L, 0L, 0L, 524288L, 1152939165512368128L, 0L, 0L);
            }
            case 'c': {
                this.jjmatchedKind = 276;
                return this.jjMoveStringLiteralDfa1_0(0L, 1125899905794048L, 0L, 0L, 266338304L, -2305840947629391872L, 3L, 0L);
            }
            case 'd': {
                this.jjmatchedKind = 114;
                return this.jjMoveStringLiteralDfa1_0(0L, -2251799813685248L, 3L, 0L, 16911433728L, 2199023255552L, 252L, 0L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 4092L, 0L, 0L, 8796093022208L, 1792L, 0L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 4190208L, 0L, 17179869184L, 0L, 2048L, 0L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 264241152L, 0L, 34359738368L, 4398046511104L, 0L, 0L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 805306368L, 0L, 0L, 0L, 0L, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 35183298347008L, 0L, 4329327034368L, 0L, 12288L, 0L);
            }
            case 'j': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 35184372088832L, 0L, 0L, 0L, 16384L, 0L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 70368744177664L, 0L, 0L, 0L, 0L, 0L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 4362862139015168L, 0L, 1121501860331520L, 105553116266496L, 229376L, 0L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 139611588448485376L, 0L, 287104476244869120L, 0L, 1835008L, 0L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, -144115188075855872L, 3L, 8935141660703064064L, 140737488355328L, 14680064L, 0L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 4092L, Long.MIN_VALUE, 281474976710657L, 520093696L, 0L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 2093056L, 0L, 14L, 8053063680L, 0L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 1071644672L, 0L, 3940649673950192L, 8787503087616L, 0L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 70367670435840L, 0L, 4503599694478336L, 18005602416459776L, 0L);
            }
            case 't': {
                this.jjmatchedKind = 238;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 576320014815068160L, 0L, 8522825728L, 54043195528445952L, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, -576460752303423488L, 3L, 25769803776L, 216172782113783808L, 0L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 252L, 0L, 0L, 0L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 16128L, 34359738368L, 288230376151711744L, 0L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 279223176896970752L, 0L, 0L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 16384L, 0L, 0L, 0L);
            }
            case '{': {
                this.jjmatchedKind = 447;
                return this.jjMoveNfa_0(0, 0);
            }
            case '|': {
                this.jjmatchedKind = 469;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 16777216L);
            }
            case '}': {
                this.jjmatchedKind = 448;
                return this.jjMoveNfa_0(0, 0);
            }
            default: {
                return this.jjMoveNfa_0(0, 0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa1_0(final long n, final long n2, final long n3, final long n4, final long n5, final long n6, final long n7, final long n8) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 0);
        }
        switch (this.curChar) {
            case '*': {
                if ((n & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 5;
                    this.jjmatchedPos = 1;
                    break;
                }
                break;
            }
            case '-': {
                if ((n & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 11;
                    this.jjmatchedPos = 1;
                    break;
                }
                break;
            }
            case '.': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 0L, n5, 0L, n6, 0L, n7, 0L, n8, 67108864L);
            }
            case ':': {
                if ((n8 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 458;
                    this.jjmatchedPos = 1;
                    break;
                }
                break;
            }
            case '=': {
                if ((n8 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 461;
                    this.jjmatchedPos = 1;
                    break;
                }
                if ((n8 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 464;
                    this.jjmatchedPos = 1;
                    break;
                }
                if ((n8 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 466;
                    this.jjmatchedPos = 1;
                    break;
                }
                break;
            }
            case '>': {
                if ((n8 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 463;
                    this.jjmatchedPos = 1;
                    break;
                }
                if ((n8 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 473;
                    this.jjmatchedPos = 1;
                    break;
                }
                break;
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 15728640L, n3, 445997100866473984L, n4, 140737488367616L, n5, 289369472079233148L, n6, 2336462210050L, n7, 1610629376L, n8, 0L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 0L, n5, -9223372036854677504L, n6, 0L, n7, 4L, n8, 0L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 576460752303423488L, n4, 3221225472L, n5, 576460752303554560L, n6, 2048L, n7, 72057594037960704L, n8, 0L);
            }
            case 'D': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 2L, n3, 1073741824L, n4, 0L, n5, 68719476736L, n6, 0L, n7, 0L, n8, 0L);
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 2303591209400057856L, n3, 1153836298285359104L, n4, 281539533340672L, n5, 52817360338944L, n6, 1152925902653370608L, n7, 125335742709816L, n8, 0L);
            }
            case 'F': {
                if ((n4 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 194;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 0L, n5, 0L, n6, 288230376151711745L, n7, 33554432L, n8, 0L);
            }
            case 'G': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 0L, n5, 0L, n6, 576460752303423488L, n7, 0L, n8, 0L);
            }
            case 'H': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 251658240L, n3, 0L, n4, 0L, n5, 768L, n6, 34426847232L, n7, 288371113640067072L, n8, 0L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, -2305843009213497344L, n3, 55169095435304960L, n4, 1688849994481664L, n5, 2322168557866112L, n6, 17594199310336L, n7, 64L, n8, 0L);
            }
            case 'J': {
                if ((n7 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 408;
                    this.jjmatchedPos = 1;
                    break;
                }
                break;
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 268435484L, n3, 8421380L, n4, 0L, n5, 4980736L, n6, 2305843009213693956L, n7, 201326592L, n8, 0L);
            }
            case 'M': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 2147483648L, n4, 68719476736L, n5, 0L, n6, 279223176896970752L, n7, 512L, n8, 0L);
            }
            case 'N': {
                if ((n3 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 160;
                    this.jjmatchedPos = 1;
                }
                else if ((n4 & 0x8L) != 0x0L) {
                    this.jjmatchedKind = 195;
                    this.jjmatchedPos = 1;
                }
                else if ((n7 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 395;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 96L, n3, 8787503087640L, n4, 4035225266123964432L, n5, 4260607557632L, n6, 8589934592L, n7, 12288L, n8, 0L);
            }
            case 'O': {
                if ((n3 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 152;
                    this.jjmatchedPos = 1;
                }
                else if ((n3 & 0x2000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 189;
                    this.jjmatchedPos = 1;
                }
                else if ((n4 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 243;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 4397509902336L, n3, 4686030597221974017L, n4, 138244259840L, n5, 140596768172675072L, n6, -4609821177987202304L, n7, 1245312L, n8, 0L);
            }
            case 'P': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, -4611685743549480864L, n5, 0L, n6, 0L, n7, 562949953421312L, n8, 0L);
            }
            case 'Q': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 8246337208320L, n5, 0L, n6, 8372224L, n7, 1407374883553280L, n8, 0L);
            }
            case 'R': {
                if ((n4 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 199;
                    this.jjmatchedPos = 1;
                }
                else if ((n7 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 425;
                    this.jjmatchedPos = 1;
                }
                else if ((n7 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 441;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 13194139533440L, n3, 201850882L, n4, 283726776525373696L, n5, 8192L, n6, 2147483656L, n7, 54043197675929600L, n8, 0L);
            }
            case 'S': {
                if ((n2 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 72;
                    this.jjmatchedPos = 1;
                }
                else if ((n3 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 171;
                    this.jjmatchedPos = 1;
                }
                else if ((n4 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 250;
                    this.jjmatchedPos = 1;
                }
                else if ((n7 & 0x1L) != 0x0L) {
                    this.jjmatchedKind = 384;
                    this.jjmatchedPos = 1;
                }
                else if ((n7 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 426;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 1536L, n3, 17592186044448L, n4, 0L, n5, 3L, n6, 17179869184L, n7, 0L, n8, 0L);
            }
            case 'T': {
                if ((n2 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 75;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 0L, n5, 0L, n6, 2322168583028736L, n7, 15771394788818944L, n8, 0L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 1108307720802304L, n3, -9223372036851630080L, n4, 26388280116739L, n5, 8214565720323784704L, n6, 4505523772719104L, n7, 4563402754L, n8, 0L);
            }
            case 'V': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 8192L, n3, 0L, n4, 2048L, n5, 0L, n6, 281474976710656L, n7, 8388608L, n8, 0L);
            }
            case 'X': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 4032L, n4, 0L, n5, 0L, n6, 8796093022208L, n7, 1024L, n8, 0L);
            }
            case 'Y': {
                if ((n2 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 83;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 35184372088832L, n5, 8724152320L, n6, 4328521728L, n7, 0L, n8, 0L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 15728640L, n3, 445997100866473984L, n4, 140737488367616L, n5, 289369472079233148L, n6, 2336462210050L, n7, 1610629376L, n8, 0L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 0L, n5, -9223372036854677504L, n6, 0L, n7, 4L, n8, 0L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 576460752303423488L, n4, 3221225472L, n5, 576460752303554560L, n6, 2048L, n7, 72057594037960704L, n8, 0L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 2L, n3, 1073741824L, n4, 0L, n5, 68719476736L, n6, 0L, n7, 0L, n8, 0L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 2303591209400057856L, n3, 1153836298285359104L, n4, 281539533340672L, n5, 52817360338944L, n6, 1152925902653370608L, n7, 125335742709816L, n8, 0L);
            }
            case 'f': {
                if ((n4 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 194;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 0L, n5, 0L, n6, 288230376151711745L, n7, 33554432L, n8, 0L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 0L, n5, 0L, n6, 576460752303423488L, n7, 0L, n8, 0L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 251658240L, n3, 0L, n4, 0L, n5, 768L, n6, 34426847232L, n7, 288371113640067072L, n8, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, -2305843009213497344L, n3, 55169095435304960L, n4, 1688849994481664L, n5, 2322168557866112L, n6, 17594199310336L, n7, 64L, n8, 0L);
            }
            case 'j': {
                if ((n7 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 408;
                    this.jjmatchedPos = 1;
                    break;
                }
                break;
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 268435484L, n3, 8421380L, n4, 0L, n5, 4980736L, n6, 2305843009213693956L, n7, 201326592L, n8, 0L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 2147483648L, n4, 68719476736L, n5, 0L, n6, 279223176896970752L, n7, 512L, n8, 0L);
            }
            case 'n': {
                if ((n3 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 160;
                    this.jjmatchedPos = 1;
                }
                else if ((n4 & 0x8L) != 0x0L) {
                    this.jjmatchedKind = 195;
                    this.jjmatchedPos = 1;
                }
                else if ((n7 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 395;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 96L, n3, 8787503087640L, n4, 4035225266123964432L, n5, 4260607557632L, n6, 8589934592L, n7, 12288L, n8, 0L);
            }
            case 'o': {
                if ((n3 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 152;
                    this.jjmatchedPos = 1;
                }
                else if ((n3 & 0x2000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 189;
                    this.jjmatchedPos = 1;
                }
                else if ((n4 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 243;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 4397509902336L, n3, 4686030597221974017L, n4, 138244259840L, n5, 140596768172675072L, n6, -4609821177987202304L, n7, 1245312L, n8, 0L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, -4611685743549480864L, n5, 0L, n6, 0L, n7, 562949953421312L, n8, 0L);
            }
            case 'q': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 8246337208320L, n5, 0L, n6, 8372224L, n7, 1407374883553280L, n8, 0L);
            }
            case 'r': {
                if ((n4 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 199;
                    this.jjmatchedPos = 1;
                }
                else if ((n7 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 425;
                    this.jjmatchedPos = 1;
                }
                else if ((n7 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 441;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 13194139533440L, n3, 201850882L, n4, 283726776525373696L, n5, 8192L, n6, 2147483656L, n7, 54043197675929600L, n8, 0L);
            }
            case 's': {
                if ((n2 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 72;
                    this.jjmatchedPos = 1;
                }
                else if ((n3 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 171;
                    this.jjmatchedPos = 1;
                }
                else if ((n4 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 250;
                    this.jjmatchedPos = 1;
                }
                else if ((n7 & 0x1L) != 0x0L) {
                    this.jjmatchedKind = 384;
                    this.jjmatchedPos = 1;
                }
                else if ((n7 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 426;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 1536L, n3, 17592186044448L, n4, 0L, n5, 3L, n6, 17179869184L, n7, 0L, n8, 0L);
            }
            case 't': {
                if ((n2 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 75;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 0L, n5, 0L, n6, 2322168583028736L, n7, 15771394788818944L, n8, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 1108307720802304L, n3, -9223372036851630080L, n4, 26388280116739L, n5, 8214565720323784704L, n6, 4505523772719104L, n7, 4563402754L, n8, 0L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 8192L, n3, 0L, n4, 2048L, n5, 0L, n6, 281474976710656L, n7, 8388608L, n8, 0L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 4032L, n4, 0L, n5, 0L, n6, 8796093022208L, n7, 1024L, n8, 0L);
            }
            case 'y': {
                if ((n2 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 83;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(n, 0L, n2, 0L, n3, 0L, n4, 35184372088832L, n5, 8724152320L, n6, 4328521728L, n7, 0L, n8, 0L);
            }
            case '|': {
                if ((n8 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 472;
                    this.jjmatchedPos = 1;
                    break;
                }
                break;
            }
        }
        return this.jjMoveNfa_0(0, 1);
    }
    
    private final int jjMoveStringLiteralDfa2_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10, final long n11, long n12, final long n13, long n14, final long n15, long n16) {
        n2 &= n;
        if ((n2 | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9) | (n12 &= n11) | (n14 &= n13) | (n16 &= n15)) == 0x0L) {
            return this.jjMoveNfa_0(0, 1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 1);
        }
        switch (this.curChar) {
            case '.': {
                if ((n16 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 474;
                    this.jjmatchedPos = 2;
                    break;
                }
                break;
            }
            case '2': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 0L, n14, 4L, n16, 0L);
            }
            case 'A': {
                return this.jjMoveStringLiteralDfa3_0(n4, 2308094809681690624L, n6, 281475043819520L, n8, 67554338014232576L, n10, 16384L, n12, 2305843026418731008L, n14, 74458936031346688L, n16, 0L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 0L, n8, 149533582426112L, n10, 8388608L, n12, 4503599627370496L, n14, 0L, n16, 0L);
            }
            case 'C': {
                if ((n4 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 73;
                    this.jjmatchedPos = 2;
                }
                else if ((n4 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 116;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 27021597764222976L, n6, 224L, n8, 4294967296L, n10, 422350038237184L, n12, 8589938688L, n14, 198016L, n16, 0L);
            }
            case 'D': {
                if ((n4 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 65;
                    this.jjmatchedPos = 2;
                }
                else if ((n4 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 69;
                    this.jjmatchedPos = 2;
                }
                else if ((n6 & 0x8L) != 0x0L) {
                    this.jjmatchedKind = 131;
                    this.jjmatchedPos = 2;
                }
                else if ((n8 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 204;
                    this.jjmatchedPos = 2;
                }
                else if ((n10 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 308;
                    this.jjmatchedPos = 2;
                }
                else if ((n14 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 410;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 72057602627862544L, n8, 4611686018427388160L, n10, 27021597764222976L, n12, 0L, n14, 135270400L, n16, 0L);
            }
            case 'E': {
                if ((n4 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 71;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 4398180728832L, n6, 1073742592L, n8, 51232L, n10, 68719477633L, n12, 281509403557896L, n14, 562949953421312L, n16, 0L);
            }
            case 'F': {
                if ((n14 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 409;
                    this.jjmatchedPos = 2;
                }
                else if ((n14 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 418;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 252201579132747776L, n6, 562949953421312L, n8, 8388608L, n10, 2147483648L, n12, 1152921504606846977L, n14, 34359738400L, n16, 0L);
            }
            case 'G': {
                if ((n4 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 77;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 16384L, n6, 0L, n8, 134217728L, n10, 562949953421312L, n12, 576478344489467904L, n14, 0L, n16, 0L);
            }
            case 'H': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 576460752303423488L, n8, 1073741824L, n10, 0L, n12, 0L, n14, 0L, n16, 0L);
            }
            case 'I': {
                if ((n12 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 322;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 35201551959040L, n8, 1801439850948657152L, n10, 274877915138L, n12, 0L, n14, 306244774661193728L, n16, 0L);
            }
            case 'J': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 0L, n8, 0L, n10, Long.MIN_VALUE, n12, 0L, n14, 0L, n16, 0L);
            }
            case 'K': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 1125899906842624L, n8, 2305843009213693952L, n10, 0L, n12, 0L, n14, 0L, n16, 0L);
            }
            case 'L': {
                if ((n4 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 66;
                    this.jjmatchedPos = 2;
                }
                else if ((n8 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 231;
                    this.jjmatchedPos = 2;
                }
                else if ((n12 & 0x20000000000000L) != 0x0L) {
                    this.jjmatchedKind = 373;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 288230383667904520L, n6, -9223372036853723136L, n8, 7705456541713L, n10, 4035225266126061580L, n12, 270779065042977040L, n14, 281474976710656L, n16, 0L);
            }
            case 'M': {
                if ((n8 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 236;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 8589934592L, n6, 2147483648L, n8, 1970462275928066L, n10, 5044101951415910400L, n12, 4611686020440653824L, n14, 0L, n16, 0L);
            }
            case 'N': {
                if ((n6 & 0x40000000000000L) != 0x0L) {
                    this.jjmatchedKind = 182;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 1082331824128L, n6, 36028831380799488L, n8, 0L, n10, 38302630115542016L, n12, -9223196114960777216L, n14, 68719476736L, n16, 0L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa3_0(n4, 8796361457664L, n6, 17592329207810L, n8, 524288L, n10, 549760532480L, n12, 68719476736L, n14, 2147483648L, n16, 0L);
            }
            case 'P': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 68719476736L, n8, Long.MIN_VALUE, n10, 0L, n12, 8800387989536L, n14, 8704L, n16, 0L);
            }
            case 'Q': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 0L, n14, 52776558133248L, n16, 0L);
            }
            case 'R': {
                if ((n6 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 144;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 1109407232425984L, n6, 147456L, n8, 2147491840L, n10, 72066407310823536L, n12, 2324092703219712L, n14, 5629504366051418L, n16, 0L);
            }
            case 'S': {
                if ((n10 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 271;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, -2882303761501387776L, n6, 141149805215748L, n8, 35201585512448L, n10, 65536L, n12, 66L, n14, 413390864384L, n16, 0L);
            }
            case 'T': {
                if ((n4 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 81;
                    this.jjmatchedPos = 2;
                }
                else if ((n6 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 150;
                    this.jjmatchedPos = 2;
                }
                else if ((n6 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 167;
                    this.jjmatchedPos = 2;
                }
                else if ((n6 & 0x4000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 190;
                    this.jjmatchedPos = 2;
                }
                else if ((n8 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 227;
                    this.jjmatchedPos = 2;
                }
                else if ((n14 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 412;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 299024L, n6, 436856860469897216L, n8, 1600L, n10, 1104612034560L, n12, 288236973221478528L, n14, 72018012143616L, n16, 0L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa3_0(n4, 2199023255552L, n6, 537133057L, n8, 144115188075855872L, n10, 0L, n12, 2147483648L, n14, 36028797018963968L, n16, 0L);
            }
            case 'V': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 268435456L, n8, 67108864L, n10, 37383395344384L, n12, 1024L, n14, 16384L, n16, 0L);
            }
            case 'W': {
                if ((n12 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 329;
                    this.jjmatchedPos = 2;
                }
                else if ((n14 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 405;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 2251799813685248L, n8, 536870912L, n10, 262144L, n12, 1125899906842624L, n14, 4194304L, n16, 0L);
            }
            case 'X': {
                if ((n6 & 0x20000000000000L) != 0x0L) {
                    this.jjmatchedKind = 181;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 1152921504606846976L, n8, 0L, n10, 1125899906842624L, n12, 0L, n14, 0L, n16, 0L);
            }
            case 'Y': {
                if ((n4 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 70;
                    this.jjmatchedPos = 2;
                }
                else if ((n6 & 0x400000000000L) != 0x0L) {
                    this.jjmatchedKind = 174;
                    this.jjmatchedPos = 2;
                }
                else if ((n10 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 286;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 0L, n14, 9007199254740992L, n16, 0L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa3_0(n4, 2308094809681690624L, n6, 281475043819520L, n8, 67554338014232576L, n10, 16384L, n12, 2305843026418731008L, n14, 74458936031346688L, n16, 0L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 0L, n8, 149533582426112L, n10, 8388608L, n12, 4503599627370496L, n14, 0L, n16, 0L);
            }
            case 'c': {
                if ((n4 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 73;
                    this.jjmatchedPos = 2;
                }
                else if ((n4 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 116;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 27021597764222976L, n6, 224L, n8, 4294967296L, n10, 422350038237184L, n12, 8589938688L, n14, 198016L, n16, 0L);
            }
            case 'd': {
                if ((n4 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 65;
                    this.jjmatchedPos = 2;
                }
                else if ((n4 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 69;
                    this.jjmatchedPos = 2;
                }
                else if ((n6 & 0x8L) != 0x0L) {
                    this.jjmatchedKind = 131;
                    this.jjmatchedPos = 2;
                }
                else if ((n8 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 204;
                    this.jjmatchedPos = 2;
                }
                else if ((n10 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 308;
                    this.jjmatchedPos = 2;
                }
                else if ((n14 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 410;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 72057602627862544L, n8, 4611686018427388160L, n10, 27021597764222976L, n12, 0L, n14, 135270400L, n16, 0L);
            }
            case 'e': {
                if ((n4 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 71;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 4398180728832L, n6, 1073742592L, n8, 51232L, n10, 68719477633L, n12, 281509403557896L, n14, 562949953421312L, n16, 0L);
            }
            case 'f': {
                if ((n14 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 409;
                    this.jjmatchedPos = 2;
                }
                else if ((n14 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 418;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 252201579132747776L, n6, 562949953421312L, n8, 8388608L, n10, 2147483648L, n12, 1152921504606846977L, n14, 34359738400L, n16, 0L);
            }
            case 'g': {
                if ((n4 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 77;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 16384L, n6, 0L, n8, 134217728L, n10, 562949953421312L, n12, 576478344489467904L, n14, 0L, n16, 0L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 576460752303423488L, n8, 1073741824L, n10, 0L, n12, 0L, n14, 0L, n16, 0L);
            }
            case 'i': {
                if ((n12 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 322;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 35201551959040L, n8, 1801439850948657152L, n10, 274877915138L, n12, 0L, n14, 306244774661193728L, n16, 0L);
            }
            case 'j': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 0L, n8, 0L, n10, Long.MIN_VALUE, n12, 0L, n14, 0L, n16, 0L);
            }
            case 'k': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 1125899906842624L, n8, 2305843009213693952L, n10, 0L, n12, 0L, n14, 0L, n16, 0L);
            }
            case 'l': {
                if ((n4 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 66;
                    this.jjmatchedPos = 2;
                }
                else if ((n8 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 231;
                    this.jjmatchedPos = 2;
                }
                else if ((n12 & 0x20000000000000L) != 0x0L) {
                    this.jjmatchedKind = 373;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 288230383667904520L, n6, -9223372036853723136L, n8, 7705456541713L, n10, 4035225266126061580L, n12, 270779065042977040L, n14, 281474976710656L, n16, 0L);
            }
            case 'm': {
                if ((n8 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 236;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 8589934592L, n6, 2147483648L, n8, 1970462275928066L, n10, 5044101951415910400L, n12, 4611686020440653824L, n14, 0L, n16, 0L);
            }
            case 'n': {
                if ((n6 & 0x40000000000000L) != 0x0L) {
                    this.jjmatchedKind = 182;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 1082331824128L, n6, 36028831380799488L, n8, 0L, n10, 38302630115542016L, n12, -9223196114960777216L, n14, 68719476736L, n16, 0L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa3_0(n4, 8796361457664L, n6, 17592329207810L, n8, 524288L, n10, 549760532480L, n12, 68719476736L, n14, 2147483648L, n16, 0L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 68719476736L, n8, Long.MIN_VALUE, n10, 0L, n12, 8800387989536L, n14, 8704L, n16, 0L);
            }
            case 'q': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 0L, n14, 52776558133248L, n16, 0L);
            }
            case 'r': {
                if ((n6 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 144;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 1109407232425984L, n6, 147456L, n8, 2147491840L, n10, 72066407310823536L, n12, 2324092703219712L, n14, 5629504366051418L, n16, 0L);
            }
            case 's': {
                if ((n10 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 271;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, -2882303761501387776L, n6, 141149805215748L, n8, 35201585512448L, n10, 65536L, n12, 66L, n14, 413390864384L, n16, 0L);
            }
            case 't': {
                if ((n4 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 81;
                    this.jjmatchedPos = 2;
                }
                else if ((n6 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 150;
                    this.jjmatchedPos = 2;
                }
                else if ((n6 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 167;
                    this.jjmatchedPos = 2;
                }
                else if ((n6 & 0x4000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 190;
                    this.jjmatchedPos = 2;
                }
                else if ((n8 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 227;
                    this.jjmatchedPos = 2;
                }
                else if ((n14 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 412;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 299024L, n6, 436856860469897216L, n8, 1600L, n10, 1104612034560L, n12, 288236973221478528L, n14, 72018012143616L, n16, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_0(n4, 2199023255552L, n6, 537133057L, n8, 144115188075855872L, n10, 0L, n12, 2147483648L, n14, 36028797018963968L, n16, 0L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 268435456L, n8, 67108864L, n10, 37383395344384L, n12, 1024L, n14, 16384L, n16, 0L);
            }
            case 'w': {
                if ((n12 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 329;
                    this.jjmatchedPos = 2;
                }
                else if ((n14 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 405;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 2251799813685248L, n8, 536870912L, n10, 262144L, n12, 1125899906842624L, n14, 4194304L, n16, 0L);
            }
            case 'x': {
                if ((n6 & 0x20000000000000L) != 0x0L) {
                    this.jjmatchedKind = 181;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 1152921504606846976L, n8, 0L, n10, 1125899906842624L, n12, 0L, n14, 0L, n16, 0L);
            }
            case 'y': {
                if ((n4 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 70;
                    this.jjmatchedPos = 2;
                }
                else if ((n6 & 0x400000000000L) != 0x0L) {
                    this.jjmatchedKind = 174;
                    this.jjmatchedPos = 2;
                }
                else if ((n10 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 286;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 0L, n14, 9007199254740992L, n16, 0L);
            }
        }
        return this.jjMoveNfa_0(0, 2);
    }
    
    private final int jjMoveStringLiteralDfa3_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10, final long n11, long n12, final long n13, long n14) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9) | (n12 &= n11) | (n14 &= n13)) == 0x0L) {
            return this.jjMoveNfa_0(0, 2);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 2);
        }
        switch (this.curChar) {
            case '-': {
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 16L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'A': {
                if ((n8 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 284;
                    this.jjmatchedPos = 3;
                }
                else if ((n12 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 398;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 36033195065540608L, n4, 576460752303456288L, n6, 4611686018444165120L, n8, 8590196736L, n10, 2199023255552L, n12, 619012227072L);
            }
            case 'B': {
                if ((n8 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 275;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 278;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 8388609L, n6, 0L, n8, 4611686018427387920L, n10, 0L, n12, 2251799813685272L);
            }
            case 'C': {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 123;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 136;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 5764607523171598336L, n4, 4503599629476352L, n6, 1374390059008L, n8, 33554464L, n10, 4398046511114L, n12, 562949953421312L);
            }
            case 'D': {
                if ((n6 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 281474976710656L, n6, 0L, n8, 1024L, n10, 274877906944L, n12, 8589934592L);
            }
            case 'E': {
                if ((n2 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 86;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 130;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x4000000000000L) != 0x0L) {
                    this.jjmatchedKind = 178;
                    this.jjmatchedPos = 3;
                }
                else if ((n6 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 229;
                    this.jjmatchedPos = 3;
                }
                else if ((n6 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 285;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 312;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 314;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 347;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 352;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 367;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 369;
                    this.jjmatchedPos = 3;
                }
                else if ((n12 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 404;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 504403158265496592L, n4, 2255547172784320L, n6, -9221680978299190526L, n8, -9223335714316353536L, n10, 306244776540242992L, n12, 171798695936L);
            }
            case 'G': {
                if ((n10 & 0x200000000000L) != 0x0L) {
                    this.jjmatchedKind = 365;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 2305843009213693952L, n4, 0L, n6, 0L, n8, 593736278999040L, n10, 17179869184L, n12, 18014402804449280L);
            }
            case 'H': {
                if ((n2 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 82;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 267;
                    this.jjmatchedPos = 3;
                }
                else if ((n12 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 392;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 4096L, n4, 0L, n6, 134217728L, n8, 0L, n10, 0L, n12, 524288L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa4_0(n2, 9007199254757376L, n4, 144115196934225920L, n6, 36028797018964032L, n8, 27091968656015360L, n10, 2339760743915520L, n12, 4785074604081152L);
            }
            case 'K': {
                if ((n8 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 268;
                    this.jjmatchedPos = 3;
                }
                else if ((n12 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 401;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 0L, n6, 0L, n8, 422212465065984L, n10, 0L, n12, 0L);
            }
            case 'L': {
                if ((n4 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 148;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & Long.MIN_VALUE) != 0x0L) {
                    this.jjmatchedKind = 191;
                    this.jjmatchedPos = 3;
                }
                else if ((n6 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 214;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 357;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 20266202081263616L, n4, 17592186044416L, n6, 140806477316097L, n8, 3458764513956855808L, n10, 8864812501248L, n12, 9007199254750208L);
            }
            case 'M': {
                if ((n4 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 147;
                    this.jjmatchedPos = 3;
                }
                else if ((n6 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 248;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 8589934592L, n4, 0L, n6, 65536L, n8, 16777216L, n10, 0L, n12, 0L);
            }
            case 'N': {
                if ((n4 & 0x200000000000L) != 0x0L) {
                    this.jjmatchedKind = 173;
                    this.jjmatchedPos = 3;
                }
                else if ((n6 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 197;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 346;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 355;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 2250562863104L, n4, 1141112832L, n6, 2337368206605287424L, n8, 68719476994L, n10, 2147483648L, n12, 36028797018963968L);
            }
            case 'O': {
                if ((n4 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 153;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 170;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 8L, n4, 0L, n6, 576460758813114368L, n8, 576462951335067648L, n10, 1152921513230336000L, n12, 0L);
            }
            case 'P': {
                if ((n4 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 129;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 0L, n6, 281474976728064L, n8, 144115188075855872L, n10, 4647714815446351872L, n12, 2147483648L);
            }
            case 'Q': {
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 0L, n6, 1152921504606846976L, n8, 0L, n10, 72057594037927936L, n12, 0L);
            }
            case 'R': {
                if ((n2 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 88;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 157;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x1L) != 0x0L) {
                    this.jjmatchedKind = 256;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 270;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 368;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 546457379667968L, n4, 0L, n6, 2048L, n8, 137438953984L, n10, 576461302067625984L, n12, 140737496743968L);
            }
            case 'S': {
                if ((n6 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 221;
                    this.jjmatchedPos = 3;
                }
                else if ((n12 & 0x400000000000L) != 0x0L) {
                    this.jjmatchedKind = 430;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 571952473309184L, n4, 21504L, n6, 13211319435264L, n8, 0L, n10, 2454461796916920321L, n12, 72057595111964678L);
            }
            case 'T': {
                if ((n2 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 87;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 175;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 177;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x1000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 188;
                    this.jjmatchedPos = 3;
                }
                else if ((n12 & 0x4000000000000L) != 0x0L) {
                    this.jjmatchedKind = 434;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, -9223371761976868864L, n4, 17179869184L, n6, 35184405651456L, n8, 36029089143857152L, n10, -9223370937326370752L, n12, 288239172244734528L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa4_0(n2, 4294967296L, n4, 396316836062298112L, n6, 0L, n8, 549755813900L, n10, 4224L, n12, 54150947668096L);
            }
            case 'V': {
                return this.jjMoveStringLiteralDfa4_0(n2, 549755813888L, n4, 0L, n6, 262144L, n8, 3377699720593408L, n10, 0L, n12, 0L);
            }
            case 'W': {
                if ((n8 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 263;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 32768L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'Y': {
                if ((n6 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 196;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 0L, n6, 0L, n8, 64L, n10, 0L, n12, 0L);
            }
            case '_': {
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1125899915214848L, n12, 138412032L);
            }
            case 'a': {
                if ((n8 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 284;
                    this.jjmatchedPos = 3;
                }
                else if ((n12 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 398;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 36033195065540608L, n4, 576460752303456288L, n6, 4611686018444165120L, n8, 8590196736L, n10, 2199023255552L, n12, 619012227072L);
            }
            case 'b': {
                if ((n8 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 275;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 278;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 8388609L, n6, 0L, n8, 4611686018427387920L, n10, 0L, n12, 2251799813685272L);
            }
            case 'c': {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 123;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 136;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 5764607523171598336L, n4, 4503599629476352L, n6, 1374390059008L, n8, 33554464L, n10, 4398046511114L, n12, 562949953421312L);
            }
            case 'd': {
                if ((n6 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 213;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 281474976710656L, n6, 0L, n8, 1024L, n10, 274877906944L, n12, 8589934592L);
            }
            case 'e': {
                if ((n2 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 86;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 130;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x4000000000000L) != 0x0L) {
                    this.jjmatchedKind = 178;
                    this.jjmatchedPos = 3;
                }
                else if ((n6 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 229;
                    this.jjmatchedPos = 3;
                }
                else if ((n6 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 249;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 285;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 312;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 314;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 347;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 352;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 367;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 369;
                    this.jjmatchedPos = 3;
                }
                else if ((n12 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 404;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 504403158265496592L, n4, 2255547172784320L, n6, -9221680978299190526L, n8, -9223335714316353536L, n10, 306244776540242992L, n12, 171798695936L);
            }
            case 'g': {
                if ((n10 & 0x200000000000L) != 0x0L) {
                    this.jjmatchedKind = 365;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 2305843009213693952L, n4, 0L, n6, 0L, n8, 593736278999040L, n10, 17179869184L, n12, 18014402804449280L);
            }
            case 'h': {
                if ((n2 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 82;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 267;
                    this.jjmatchedPos = 3;
                }
                else if ((n12 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 392;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 4096L, n4, 0L, n6, 134217728L, n8, 0L, n10, 0L, n12, 524288L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa4_0(n2, 9007199254757376L, n4, 144115196934225920L, n6, 36028797018964032L, n8, 27091968656015360L, n10, 2339760743915520L, n12, 4785074604081152L);
            }
            case 'k': {
                if ((n8 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 268;
                    this.jjmatchedPos = 3;
                }
                else if ((n12 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 401;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 0L, n6, 0L, n8, 422212465065984L, n10, 0L, n12, 0L);
            }
            case 'l': {
                if ((n4 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 148;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & Long.MIN_VALUE) != 0x0L) {
                    this.jjmatchedKind = 191;
                    this.jjmatchedPos = 3;
                }
                else if ((n6 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 214;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 357;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 20266202081263616L, n4, 17592186044416L, n6, 140806477316097L, n8, 3458764513956855808L, n10, 8864812501248L, n12, 9007199254750208L);
            }
            case 'm': {
                if ((n4 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 147;
                    this.jjmatchedPos = 3;
                }
                else if ((n6 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 248;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 8589934592L, n4, 0L, n6, 65536L, n8, 16777216L, n10, 0L, n12, 0L);
            }
            case 'n': {
                if ((n4 & 0x200000000000L) != 0x0L) {
                    this.jjmatchedKind = 173;
                    this.jjmatchedPos = 3;
                }
                else if ((n6 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 197;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 346;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 355;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 2250562863104L, n4, 1141112832L, n6, 2337368206605287424L, n8, 68719476994L, n10, 2147483648L, n12, 36028797018963968L);
            }
            case 'o': {
                if ((n4 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 153;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 170;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 8L, n4, 0L, n6, 576460758813114368L, n8, 576462951335067648L, n10, 1152921513230336000L, n12, 0L);
            }
            case 'p': {
                if ((n4 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 129;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 0L, n6, 281474976728064L, n8, 144115188075855872L, n10, 4647714815446351872L, n12, 2147483648L);
            }
            case 'q': {
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 0L, n6, 1152921504606846976L, n8, 0L, n10, 72057594037927936L, n12, 0L);
            }
            case 'r': {
                if ((n2 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 88;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 157;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x1L) != 0x0L) {
                    this.jjmatchedKind = 256;
                    this.jjmatchedPos = 3;
                }
                else if ((n8 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 270;
                    this.jjmatchedPos = 3;
                }
                else if ((n10 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 368;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 546457379667968L, n4, 0L, n6, 2048L, n8, 137438953984L, n10, 576461302067625984L, n12, 140737496743968L);
            }
            case 's': {
                if ((n6 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 221;
                    this.jjmatchedPos = 3;
                }
                else if ((n12 & 0x400000000000L) != 0x0L) {
                    this.jjmatchedKind = 430;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 571952473309184L, n4, 21504L, n6, 13211319435264L, n8, 0L, n10, 2454461796916920321L, n12, 72057595111964678L);
            }
            case 't': {
                if ((n2 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 87;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 175;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 177;
                    this.jjmatchedPos = 3;
                }
                else if ((n4 & 0x1000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 188;
                    this.jjmatchedPos = 3;
                }
                else if ((n12 & 0x4000000000000L) != 0x0L) {
                    this.jjmatchedKind = 434;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, -9223371761976868864L, n4, 17179869184L, n6, 35184405651456L, n8, 36029089143857152L, n10, -9223370937326370752L, n12, 288239172244734528L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa4_0(n2, 4294967296L, n4, 396316836062298112L, n6, 0L, n8, 549755813900L, n10, 4224L, n12, 54150947668096L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa4_0(n2, 549755813888L, n4, 0L, n6, 262144L, n8, 3377699720593408L, n10, 0L, n12, 0L);
            }
            case 'w': {
                if ((n8 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 263;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 32768L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'y': {
                if ((n6 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 196;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(n2, 0L, n4, 0L, n6, 0L, n8, 64L, n10, 0L, n12, 0L);
            }
            default: {
                return this.jjMoveNfa_0(0, 3);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa4_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10, final long n11, long n12) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9) | (n12 &= n11)) == 0x0L) {
            return this.jjMoveNfa_0(0, 3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 3);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa5_0(n2, 18014401834516480L, n4, 17592194433024L, n6, 81920L, n8, 1156299204428103680L, n10, 36037867989901426L, n12, 270368L);
            }
            case 'B': {
                if ((n8 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 315;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 268435456L, n8, 0L, n10, 2199023255552L, n12, 0L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa5_0(n2, 8L, n4, 8589934592L, n6, 8589934592L, n8, Long.MIN_VALUE, n10, 2147483648L, n12, 36028797027352576L);
            }
            case 'D': {
                if ((n4 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 146;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 432;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 2147483648L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'E': {
                if ((n2 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 92;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 140;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 230;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 239;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 258;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 265;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 269;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 283;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 299;
                    this.jjmatchedPos = 4;
                }
                else if ((n10 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 331;
                    this.jjmatchedPos = 4;
                }
                else if ((n10 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 354;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 399;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 416;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 431;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x20000000000000L) != 0x0L) {
                    this.jjmatchedKind = 437;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 440;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 547059111329792L, n4, 16L, n6, 35184372645888L, n8, 4612249105821860104L, n10, -8502795477983428607L, n12, 288283154857328640L);
            }
            case 'F': {
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 0L, n8, 27021597764222976L, n10, 0L, n12, 0L);
            }
            case 'G': {
                if ((n8 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 257;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 1099511627776L, n6, 0L, n8, 0L, n10, 0L, n12, 18014398509481984L);
            }
            case 'H': {
                if ((n4 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 141;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 180;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 311;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 0L, n8, 32L, n10, 0L, n12, 0L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa5_0(n2, -9223371753386934272L, n4, 281492156710912L, n6, 17181188097L, n8, 274894684240L, n10, 1099511627784L, n12, 2824096689684480L);
            }
            case 'K': {
                if ((n2 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 91;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 67108864L, n8, 2199023255552L, n10, 0L, n12, 0L);
            }
            case 'L': {
                if ((n8 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 279;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x200000000000L) != 0x0L) {
                    this.jjmatchedKind = 301;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 2251799813685248L, n4, 72057594037927937L, n6, 36028867885926400L, n8, 0L, n10, 0L, n12, 274877906944L);
            }
            case 'M': {
                if ((n10 & 0x400000000000L) != 0x0L) {
                    this.jjmatchedKind = 366;
                    this.jjmatchedPos = 4;
                }
                else if ((n10 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 371;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 9007203549708288L, n4, 0L, n6, 1073741824L, n8, 8589934592L, n10, 8589934592L, n12, 69256347776L);
            }
            case 'N': {
                if ((n2 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 78;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 251;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 2305843009213693952L, n4, 137707388928L, n6, 4294967296L, n8, 2147483648L, n10, 1143492126441472L, n12, 0L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa5_0(n2, 4612248968380813312L, n4, 144115188075855872L, n6, 2306125583702032448L, n8, 132096L, n10, 0L, n12, 524290L);
            }
            case 'P': {
                if ((n4 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 155;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 436;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 224L, n6, 0L, n8, 0L, n10, 1024L, n12, 0L);
            }
            case 'Q': {
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 4L);
            }
            case 'R': {
                if ((n2 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 68;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 163;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 179;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 187;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 200;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 201;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 209;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & Long.MIN_VALUE) != 0x0L) {
                    this.jjmatchedKind = 255;
                    this.jjmatchedPos = 4;
                }
                else if ((n10 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 378;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 1369094286720697344L, n4, 288232850052876288L, n6, 2199065198594L, n8, 1155346202624L, n10, 5764607523034239104L, n12, 1133871366144L);
            }
            case 'S': {
                if ((n2 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 107;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 303;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 313;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x2000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 317;
                    this.jjmatchedPos = 4;
                }
                else if ((n10 & 0x2000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 381;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 417;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 31525197391593472L, n8, 281474976710656L, n10, 1879048192L, n12, 0L);
            }
            case 'T': {
                if ((n2 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 105;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 142;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 143;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 154;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 164;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 219;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 295;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x400000000000L) != 0x0L) {
                    this.jjmatchedKind = 302;
                    this.jjmatchedPos = 4;
                }
                else if ((n10 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 343;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 421;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 288234980356653056L, n4, 36028798094803968L, n6, 4611699212583698432L, n8, 17660905521152L, n10, 4503599635742720L, n12, 138477568L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa5_0(n2, 36028797018963968L, n4, 512L, n6, 1152921504606848000L, n8, 4398046511104L, n10, 72061992084439296L, n12, 1024L);
            }
            case 'X': {
                if ((n12 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 396;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 18014398509481984L, n12, 0L);
            }
            case 'Y': {
                if ((n12 & 0x8L) != 0x0L) {
                    this.jjmatchedKind = 387;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 390;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 393;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 0L, n8, 262144L, n10, 0L, n12, 16L);
            }
            case 'Z': {
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 1688849860263936L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa5_0(n2, 18014401834516480L, n4, 17592194433024L, n6, 81920L, n8, 1156299204428103680L, n10, 36037867989901426L, n12, 270368L);
            }
            case 'b': {
                if ((n8 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 315;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 268435456L, n8, 0L, n10, 2199023255552L, n12, 0L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa5_0(n2, 8L, n4, 8589934592L, n6, 8589934592L, n8, Long.MIN_VALUE, n10, 2147483648L, n12, 36028797027352576L);
            }
            case 'd': {
                if ((n4 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 146;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 432;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 2147483648L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'e': {
                if ((n2 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 92;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 140;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 230;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 239;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 258;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 265;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 269;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 283;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 299;
                    this.jjmatchedPos = 4;
                }
                else if ((n10 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 331;
                    this.jjmatchedPos = 4;
                }
                else if ((n10 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 354;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 399;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 416;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 431;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x20000000000000L) != 0x0L) {
                    this.jjmatchedKind = 437;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 440;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 547059111329792L, n4, 16L, n6, 35184372645888L, n8, 4612249105821860104L, n10, -8502795477983428607L, n12, 288283154857328640L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 0L, n8, 27021597764222976L, n10, 0L, n12, 0L);
            }
            case 'g': {
                if ((n8 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 257;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 1099511627776L, n6, 0L, n8, 0L, n10, 0L, n12, 18014398509481984L);
            }
            case 'h': {
                if ((n4 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 141;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 180;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 311;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 0L, n8, 32L, n10, 0L, n12, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa5_0(n2, -9223371753386934272L, n4, 281492156710912L, n6, 17181188097L, n8, 274894684240L, n10, 1099511627784L, n12, 2824096689684480L);
            }
            case 'k': {
                if ((n2 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 91;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 67108864L, n8, 2199023255552L, n10, 0L, n12, 0L);
            }
            case 'l': {
                if ((n8 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 279;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x200000000000L) != 0x0L) {
                    this.jjmatchedKind = 301;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 2251799813685248L, n4, 72057594037927937L, n6, 36028867885926400L, n8, 0L, n10, 0L, n12, 274877906944L);
            }
            case 'm': {
                if ((n10 & 0x400000000000L) != 0x0L) {
                    this.jjmatchedKind = 366;
                    this.jjmatchedPos = 4;
                }
                else if ((n10 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 371;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 9007203549708288L, n4, 0L, n6, 1073741824L, n8, 8589934592L, n10, 8589934592L, n12, 69256347776L);
            }
            case 'n': {
                if ((n2 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 78;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 251;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 2305843009213693952L, n4, 137707388928L, n6, 4294967296L, n8, 2147483648L, n10, 1143492126441472L, n12, 0L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa5_0(n2, 4612248968380813312L, n4, 144115188075855872L, n6, 2306125583702032448L, n8, 132096L, n10, 0L, n12, 524290L);
            }
            case 'p': {
                if ((n4 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 155;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 436;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 224L, n6, 0L, n8, 0L, n10, 1024L, n12, 0L);
            }
            case 'q': {
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 4L);
            }
            case 'r': {
                if ((n2 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 68;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 163;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 179;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 187;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 200;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 201;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 209;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & Long.MIN_VALUE) != 0x0L) {
                    this.jjmatchedKind = 255;
                    this.jjmatchedPos = 4;
                }
                else if ((n10 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 378;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 1369094286720697344L, n4, 288232850052876288L, n6, 2199065198594L, n8, 1155346202624L, n10, 5764607523034239104L, n12, 1133871366144L);
            }
            case 's': {
                if ((n2 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 107;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 303;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 313;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x2000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 317;
                    this.jjmatchedPos = 4;
                }
                else if ((n10 & 0x2000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 381;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 417;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 31525197391593472L, n8, 281474976710656L, n10, 1879048192L, n12, 0L);
            }
            case 't': {
                if ((n2 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 105;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 142;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 143;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 154;
                    this.jjmatchedPos = 4;
                }
                else if ((n4 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 164;
                    this.jjmatchedPos = 4;
                }
                else if ((n6 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 219;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 295;
                    this.jjmatchedPos = 4;
                }
                else if ((n8 & 0x400000000000L) != 0x0L) {
                    this.jjmatchedKind = 302;
                    this.jjmatchedPos = 4;
                }
                else if ((n10 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 343;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 421;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 288234980356653056L, n4, 36028798094803968L, n6, 4611699212583698432L, n8, 17660905521152L, n10, 4503599635742720L, n12, 138477568L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa5_0(n2, 36028797018963968L, n4, 512L, n6, 1152921504606848000L, n8, 4398046511104L, n10, 72061992084439296L, n12, 1024L);
            }
            case 'x': {
                if ((n12 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 396;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 18014398509481984L, n12, 0L);
            }
            case 'y': {
                if ((n12 & 0x8L) != 0x0L) {
                    this.jjmatchedKind = 387;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 390;
                    this.jjmatchedPos = 4;
                }
                else if ((n12 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 393;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 0L, n8, 262144L, n10, 0L, n12, 16L);
            }
            case 'z': {
                return this.jjMoveStringLiteralDfa5_0(n2, 0L, n4, 0L, n6, 1688849860263936L, n8, 0L, n10, 0L, n12, 0L);
            }
            default: {
                return this.jjMoveNfa_0(0, 4);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa5_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10, final long n11, long n12) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9) | (n12 &= n11)) == 0x0L) {
            return this.jjMoveNfa_0(0, 4);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 4);
        }
        switch (this.curChar) {
            case 'A': {
                if ((n6 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 222;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 9007199254741000L, n4, 288230401921515520L, n6, 4507997942327296L, n8, 4724464025632L, n10, 2269890215936L, n12, 36028797157376000L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 1152921504606846976L, n10, 0L, n12, 0L);
            }
            case 'C': {
                if ((n6 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 212;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 51640270848L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 8192L);
            }
            case 'D': {
                if ((n6 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 224;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 277;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 305;
                    this.jjmatchedPos = 5;
                }
                else if ((n12 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 403;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 3145728L, n4, 0L, n6, 1099512152064L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'E': {
                if ((n2 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 106;
                    this.jjmatchedPos = 5;
                }
                else if ((n2 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 122;
                    this.jjmatchedPos = 5;
                }
                else if ((n4 & 0x1L) != 0x0L) {
                    this.jjmatchedKind = 128;
                    this.jjmatchedPos = 5;
                }
                else if ((n4 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 133;
                    this.jjmatchedPos = 5;
                }
                else if ((n4 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 183;
                    this.jjmatchedPos = 5;
                }
                else if ((n4 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 184;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 218;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x1000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 252;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x4000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 254;
                    this.jjmatchedPos = 5;
                }
                else if ((n10 & 0x1000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 380;
                    this.jjmatchedPos = 5;
                }
                else if ((n12 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 400;
                    this.jjmatchedPos = 5;
                }
                else if ((n12 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 420;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 32768L, n4, 1099511627776L, n6, 8388608L, n8, 2201170739200L, n10, 4683743612465315840L, n12, 18014433406091392L);
            }
            case 'F': {
                if ((n6 & 0x1L) != 0x0L) {
                    this.jjmatchedKind = 192;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 562949953421312L);
            }
            case 'G': {
                if ((n4 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 156;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 131072L, n6, 0L, n8, 0L, n10, 576460752303423488L, n12, 262176L);
            }
            case 'H': {
                if ((n8 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 300;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 8388608L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa6_0(n2, 1152921504606846976L, n4, 3223322624L, n6, 36028865788772354L, n8, 9288751607971840L, n10, 18023194602508288L, n12, 0L);
            }
            case 'L': {
                if ((n4 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 151;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 223;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 272;
                    this.jjmatchedPos = 5;
                }
                else if ((n10 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 321;
                    this.jjmatchedPos = 5;
                }
                else if ((n12 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 386;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 36028797018963968L, n4, 0L, n6, 27021597764485120L, n8, 3377699720527872L, n10, 8192L, n12, 2251799813685248L);
            }
            case 'M': {
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 35184372088832L, n8, 141733920768L, n10, 1108118339584L, n12, 0L);
            }
            case 'N': {
                if ((n2 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 96;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 198;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 273;
                    this.jjmatchedPos = 5;
                }
                else if ((n12 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 423;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, -4611140385782104064L, n4, 144396663052568576L, n6, 0L, n8, 80L, n10, -9223371487098961792L, n12, 53877143502848L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa6_0(n2, 2308094809027379200L, n4, 0L, n6, 1688867040133120L, n8, 0L, n10, 1024L, n12, 0L);
            }
            case 'P': {
                if ((n10 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 328;
                    this.jjmatchedPos = 5;
                    break;
                }
                break;
            }
            case 'R': {
                if ((n2 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 113;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x4000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 318;
                    this.jjmatchedPos = 5;
                }
                else if ((n10 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 372;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 234187936537513984L, n4, 0L, n6, 292470093103104L, n8, 0L, n10, 180148383141331008L, n12, 2147483650L);
            }
            case 'S': {
                if ((n4 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 138;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x8L) != 0x0L) {
                    this.jjmatchedKind = 259;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 274;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 1100048498688L, n4, 2336462209024L, n6, 0L, n8, 0L, n10, 8372248L, n12, 288239172244734976L);
            }
            case 'T': {
                if ((n2 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 97;
                    this.jjmatchedPos = 5;
                }
                else if ((n4 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 134;
                    this.jjmatchedPos = 5;
                }
                else if ((n4 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 166;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 202;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 225;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 281;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & Long.MIN_VALUE) != 0x0L) {
                    this.jjmatchedKind = 319;
                    this.jjmatchedPos = 5;
                }
                else if ((n10 & 0x1L) != 0x0L) {
                    this.jjmatchedKind = 320;
                    this.jjmatchedPos = 5;
                }
                else if ((n10 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 364;
                    this.jjmatchedPos = 5;
                }
                else if ((n12 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 422;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 3221226496L, n4, 17592186045056L, n6, 0L, n8, 16777216L, n10, 276756955168L, n12, 0L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1125899906842624L, n12, 0L);
            }
            case 'V': {
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 1099511628032L, n10, 0L, n12, 0L);
            }
            case 'W': {
                if ((n8 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 266;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 2305843009213693952L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'X': {
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 16L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'Y': {
                if ((n2 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 80;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x40000000000000L) != 0x0L) {
                    this.jjmatchedKind = 310;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 33554432L, n12, 0L);
            }
            case '_': {
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 16L);
            }
            case 'a': {
                if ((n6 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 222;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 9007199254741000L, n4, 288230401921515520L, n6, 4507997942327296L, n8, 4724464025632L, n10, 2269890215936L, n12, 36028797157376000L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 1152921504606846976L, n10, 0L, n12, 0L);
            }
            case 'c': {
                if ((n6 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 212;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 51640270848L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 8192L);
            }
            case 'd': {
                if ((n6 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 224;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 277;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 305;
                    this.jjmatchedPos = 5;
                }
                else if ((n12 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 403;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 3145728L, n4, 0L, n6, 1099512152064L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'e': {
                if ((n2 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 106;
                    this.jjmatchedPos = 5;
                }
                else if ((n2 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 122;
                    this.jjmatchedPos = 5;
                }
                else if ((n4 & 0x1L) != 0x0L) {
                    this.jjmatchedKind = 128;
                    this.jjmatchedPos = 5;
                }
                else if ((n4 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 133;
                    this.jjmatchedPos = 5;
                }
                else if ((n4 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 183;
                    this.jjmatchedPos = 5;
                }
                else if ((n4 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 184;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 218;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x1000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 252;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x4000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 254;
                    this.jjmatchedPos = 5;
                }
                else if ((n10 & 0x1000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 380;
                    this.jjmatchedPos = 5;
                }
                else if ((n12 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 400;
                    this.jjmatchedPos = 5;
                }
                else if ((n12 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 420;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 32768L, n4, 1099511627776L, n6, 8388608L, n8, 2201170739200L, n10, 4683743612465315840L, n12, 18014433406091392L);
            }
            case 'f': {
                if ((n6 & 0x1L) != 0x0L) {
                    this.jjmatchedKind = 192;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 562949953421312L);
            }
            case 'g': {
                if ((n4 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 156;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 131072L, n6, 0L, n8, 0L, n10, 576460752303423488L, n12, 262176L);
            }
            case 'h': {
                if ((n8 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 300;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 8388608L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa6_0(n2, 1152921504606846976L, n4, 3223322624L, n6, 36028865788772354L, n8, 9288751607971840L, n10, 18023194602508288L, n12, 0L);
            }
            case 'l': {
                if ((n4 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 151;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 223;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 272;
                    this.jjmatchedPos = 5;
                }
                else if ((n10 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 321;
                    this.jjmatchedPos = 5;
                }
                else if ((n12 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 386;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 36028797018963968L, n4, 0L, n6, 27021597764485120L, n8, 3377699720527872L, n10, 8192L, n12, 2251799813685248L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 35184372088832L, n8, 141733920768L, n10, 1108118339584L, n12, 0L);
            }
            case 'n': {
                if ((n2 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 96;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 198;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 273;
                    this.jjmatchedPos = 5;
                }
                else if ((n12 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 423;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, -4611140385782104064L, n4, 144396663052568576L, n6, 0L, n8, 80L, n10, -9223371487098961792L, n12, 53877143502848L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa6_0(n2, 2308094809027379200L, n4, 0L, n6, 1688867040133120L, n8, 0L, n10, 1024L, n12, 0L);
            }
            case 'p': {
                if ((n10 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 328;
                    this.jjmatchedPos = 5;
                    break;
                }
                break;
            }
            case 'r': {
                if ((n2 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 113;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x4000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 318;
                    this.jjmatchedPos = 5;
                }
                else if ((n10 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 372;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 234187936537513984L, n4, 0L, n6, 292470093103104L, n8, 0L, n10, 180148383141331008L, n12, 2147483650L);
            }
            case 's': {
                if ((n4 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 138;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x8L) != 0x0L) {
                    this.jjmatchedKind = 259;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 274;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 1100048498688L, n4, 2336462209024L, n6, 0L, n8, 0L, n10, 8372248L, n12, 288239172244734976L);
            }
            case 't': {
                if ((n2 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 97;
                    this.jjmatchedPos = 5;
                }
                else if ((n4 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 134;
                    this.jjmatchedPos = 5;
                }
                else if ((n4 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 166;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 202;
                    this.jjmatchedPos = 5;
                }
                else if ((n6 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 225;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 281;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & Long.MIN_VALUE) != 0x0L) {
                    this.jjmatchedKind = 319;
                    this.jjmatchedPos = 5;
                }
                else if ((n10 & 0x1L) != 0x0L) {
                    this.jjmatchedKind = 320;
                    this.jjmatchedPos = 5;
                }
                else if ((n10 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 364;
                    this.jjmatchedPos = 5;
                }
                else if ((n12 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 422;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 3221226496L, n4, 17592186045056L, n6, 0L, n8, 16777216L, n10, 276756955168L, n12, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1125899906842624L, n12, 0L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 1099511628032L, n10, 0L, n12, 0L);
            }
            case 'w': {
                if ((n8 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 266;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 2305843009213693952L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'x': {
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 16L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'y': {
                if ((n2 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 80;
                    this.jjmatchedPos = 5;
                }
                else if ((n8 & 0x40000000000000L) != 0x0L) {
                    this.jjmatchedKind = 310;
                    this.jjmatchedPos = 5;
                }
                return this.jjMoveStringLiteralDfa6_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 33554432L, n12, 0L);
            }
        }
        return this.jjMoveNfa_0(0, 5);
    }
    
    private final int jjMoveStringLiteralDfa6_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10, final long n11, long n12) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9) | (n12 &= n11)) == 0x0L) {
            return this.jjMoveNfa_0(0, 5);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 5);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa7_0(n2, 72057800196358144L, n4, 144115190223341568L, n6, 27303072740933632L, n8, 1099511627792L, n10, 576460754182471712L, n12, 8388608L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa7_0(n2, 1152921504606846976L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 138412032L);
            }
            case 'C': {
                if ((n6 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 193;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 289;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, -9221120236504219648L, n4, 0L, n6, 4503599929360384L, n8, 0L, n10, 0L, n12, 17592186044416L);
            }
            case 'E': {
                if ((n2 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 84;
                    this.jjmatchedPos = 6;
                }
                else if ((n2 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 94;
                    this.jjmatchedPos = 6;
                }
                else if ((n2 & 0x40000000000000L) != 0x0L) {
                    this.jjmatchedKind = 118;
                    this.jjmatchedPos = 6;
                }
                else if ((n4 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 137;
                    this.jjmatchedPos = 6;
                }
                else if ((n6 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 206;
                    this.jjmatchedPos = 6;
                }
                else if ((n6 & 0x10000000000L) != 0x0L) {
                    this.jjmatchedKind = 232;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 324;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 358;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & 0x10000000000L) != 0x0L) {
                    this.jjmatchedKind = 360;
                    this.jjmatchedPos = 6;
                }
                else if ((n12 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 397;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 144115188077953024L, n4, 2199023255568L, n6, 262144L, n8, 9007336693694720L, n10, 16777216L, n12, 262144L);
            }
            case 'G': {
                if ((n4 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 176;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 262;
                    this.jjmatchedPos = 6;
                }
                else if ((n12 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 414;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 0L, n8, 4398046511104L, n10, 0L, n12, 0L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa7_0(n2, 2147488768L, n4, 17729624998016L, n6, 8796093022208L, n8, 4294967296L, n10, 144115196674171912L, n12, 2815849278735360L);
            }
            case 'J': {
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 16L);
            }
            case 'L': {
                if ((n2 & 0x20000000000000L) != 0x0L) {
                    this.jjmatchedKind = 117;
                    this.jjmatchedPos = 6;
                }
                else if ((n4 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 186;
                    this.jjmatchedPos = 6;
                }
                else if ((n6 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 205;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 294;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 17179869184L, n6, 0L, n8, 1152921504606846976L, n10, 0L, n12, 0L);
            }
            case 'M': {
                if ((n10 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 345;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1125899906842624L, n12, 32L);
            }
            case 'N': {
                if ((n2 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 79;
                    this.jjmatchedPos = 6;
                }
                else if ((n4 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 145;
                    this.jjmatchedPos = 6;
                }
                else if ((n6 & 0x2000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 253;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 290;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 356;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 363;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 4611686018427387904L, n4, 0L, n6, 37717732786962432L, n8, 67108864L, n10, 0L, n12, 34359738496L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 2097152L, n6, 2199023255552L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'P': {
                return this.jjMoveStringLiteralDfa7_0(n2, 1099511627776L, n4, 0L, n6, 2048L, n8, 0L, n10, 0L, n12, 288230376151711744L);
            }
            case 'R': {
                if ((n4 & 0x10000000000L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 261;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 287;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 297;
                    this.jjmatchedPos = 6;
                }
                else if ((n12 & 0x40000000000000L) != 0x0L) {
                    this.jjmatchedKind = 438;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 72061992084439040L, n12, 0L);
            }
            case 'S': {
                if ((n10 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 327;
                    this.jjmatchedPos = 6;
                }
                else if ((n12 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 385;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 2305843009213693952L, n4, 0L, n6, 0L, n8, 0L, n10, 4665731412979089408L, n12, 0L);
            }
            case 'T': {
                if ((n2 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 6;
                }
                else if ((n2 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 103;
                    this.jjmatchedPos = 6;
                }
                else if ((n2 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 108;
                    this.jjmatchedPos = 6;
                }
                else if ((n2 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 119;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 326;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & Long.MIN_VALUE) != 0x0L) {
                    this.jjmatchedKind = 383;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 527800041734152L, n4, 9663676416L, n6, 4398046511104L, n8, 103095992320L, n10, 551903301632L, n12, 36072780168429568L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa7_0(n2, 274877906944L, n4, 0L, n6, 524288L, n8, 3377699720527872L, n10, 0L, n12, 0L);
            }
            case 'V': {
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 16809984L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'Y': {
                if ((n6 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 208;
                    this.jjmatchedPos = 6;
                    break;
                }
                break;
            }
            case 'Z': {
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 0L, n8, 281474976710656L, n10, 0L, n12, 0L);
            }
            case '_': {
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 35184372088832L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa7_0(n2, 72057800196358144L, n4, 144115190223341568L, n6, 27303072740933632L, n8, 1099511627792L, n10, 576460754182471712L, n12, 8388608L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa7_0(n2, 1152921504606846976L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 138412032L);
            }
            case 'c': {
                if ((n6 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 193;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 289;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, -9221120236504219648L, n4, 0L, n6, 4503599929360384L, n8, 0L, n10, 0L, n12, 17592186044416L);
            }
            case 'e': {
                if ((n2 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 84;
                    this.jjmatchedPos = 6;
                }
                else if ((n2 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 94;
                    this.jjmatchedPos = 6;
                }
                else if ((n2 & 0x40000000000000L) != 0x0L) {
                    this.jjmatchedKind = 118;
                    this.jjmatchedPos = 6;
                }
                else if ((n4 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 137;
                    this.jjmatchedPos = 6;
                }
                else if ((n6 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 206;
                    this.jjmatchedPos = 6;
                }
                else if ((n6 & 0x10000000000L) != 0x0L) {
                    this.jjmatchedKind = 232;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 324;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 358;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & 0x10000000000L) != 0x0L) {
                    this.jjmatchedKind = 360;
                    this.jjmatchedPos = 6;
                }
                else if ((n12 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 397;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 144115188077953024L, n4, 2199023255568L, n6, 262144L, n8, 9007336693694720L, n10, 16777216L, n12, 262144L);
            }
            case 'g': {
                if ((n4 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 176;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 262;
                    this.jjmatchedPos = 6;
                }
                else if ((n12 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 414;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 0L, n8, 4398046511104L, n10, 0L, n12, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa7_0(n2, 2147488768L, n4, 17729624998016L, n6, 8796093022208L, n8, 4294967296L, n10, 144115196674171912L, n12, 2815849278735360L);
            }
            case 'j': {
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 16L);
            }
            case 'l': {
                if ((n2 & 0x20000000000000L) != 0x0L) {
                    this.jjmatchedKind = 117;
                    this.jjmatchedPos = 6;
                }
                else if ((n4 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 186;
                    this.jjmatchedPos = 6;
                }
                else if ((n6 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 205;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 294;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 17179869184L, n6, 0L, n8, 1152921504606846976L, n10, 0L, n12, 0L);
            }
            case 'm': {
                if ((n10 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 345;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1125899906842624L, n12, 32L);
            }
            case 'n': {
                if ((n2 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 79;
                    this.jjmatchedPos = 6;
                }
                else if ((n4 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 145;
                    this.jjmatchedPos = 6;
                }
                else if ((n6 & 0x2000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 253;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 290;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 356;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 363;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 4611686018427387904L, n4, 0L, n6, 37717732786962432L, n8, 67108864L, n10, 0L, n12, 34359738496L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 2097152L, n6, 2199023255552L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa7_0(n2, 1099511627776L, n4, 0L, n6, 2048L, n8, 0L, n10, 0L, n12, 288230376151711744L);
            }
            case 'r': {
                if ((n4 & 0x10000000000L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 261;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 287;
                    this.jjmatchedPos = 6;
                }
                else if ((n8 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 297;
                    this.jjmatchedPos = 6;
                }
                else if ((n12 & 0x40000000000000L) != 0x0L) {
                    this.jjmatchedKind = 438;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 72061992084439040L, n12, 0L);
            }
            case 's': {
                if ((n10 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 327;
                    this.jjmatchedPos = 6;
                }
                else if ((n12 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 385;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 2305843009213693952L, n4, 0L, n6, 0L, n8, 0L, n10, 4665731412979089408L, n12, 0L);
            }
            case 't': {
                if ((n2 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 98;
                    this.jjmatchedPos = 6;
                }
                else if ((n2 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 103;
                    this.jjmatchedPos = 6;
                }
                else if ((n2 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 108;
                    this.jjmatchedPos = 6;
                }
                else if ((n2 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 119;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 326;
                    this.jjmatchedPos = 6;
                }
                else if ((n10 & Long.MIN_VALUE) != 0x0L) {
                    this.jjmatchedKind = 383;
                    this.jjmatchedPos = 6;
                }
                return this.jjMoveStringLiteralDfa7_0(n2, 527800041734152L, n4, 9663676416L, n6, 4398046511104L, n8, 103095992320L, n10, 551903301632L, n12, 36072780168429568L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa7_0(n2, 274877906944L, n4, 0L, n6, 524288L, n8, 3377699720527872L, n10, 0L, n12, 0L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 16809984L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'y': {
                if ((n6 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 208;
                    this.jjmatchedPos = 6;
                    break;
                }
                break;
            }
            case 'z': {
                return this.jjMoveStringLiteralDfa7_0(n2, 0L, n4, 0L, n6, 0L, n8, 281474976710656L, n10, 0L, n12, 0L);
            }
        }
        return this.jjMoveNfa_0(0, 6);
    }
    
    private final int jjMoveStringLiteralDfa7_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10, final long n11, long n12) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9) | (n12 &= n11)) == 0x0L) {
            return this.jjMoveNfa_0(0, 6);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 6);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa8_0(n2, 2251799813685248L, n4, 0L, n6, 0L, n8, 0L, n10, 144115188075855872L, n12, 288230376151711744L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa8_0(n2, 72057594037927936L, n4, 0L, n6, 0L, n8, 0L, n10, 1125899906842656L, n12, 0L);
            }
            case 'C': {
                if ((n4 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 132;
                    this.jjmatchedPos = 7;
                }
                else if ((n12 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 433;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 2199023255552L, n6, 8388608L, n8, 0L, n10, 0L, n12, 34359738368L);
            }
            case 'D': {
                if ((n2 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 85;
                    this.jjmatchedPos = 7;
                }
                else if ((n2 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 121;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 16L);
            }
            case 'E': {
                if ((n2 & 0x8L) != 0x0L) {
                    this.jjmatchedKind = 67;
                    this.jjmatchedPos = 7;
                }
                else if ((n2 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 93;
                    this.jjmatchedPos = 7;
                }
                else if ((n2 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 102;
                    this.jjmatchedPos = 7;
                }
                else if ((n2 & 0x1000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 124;
                    this.jjmatchedPos = 7;
                }
                else if ((n6 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 207;
                    this.jjmatchedPos = 7;
                }
                else if ((n6 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 216;
                    this.jjmatchedPos = 7;
                }
                else if ((n6 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 234;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 298;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 304;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x4000000000000L) != 0x0L) {
                    this.jjmatchedKind = 306;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 307;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x1000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 316;
                    this.jjmatchedPos = 7;
                }
                else if ((n10 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 351;
                    this.jjmatchedPos = 7;
                }
                else if ((n10 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 361;
                    this.jjmatchedPos = 7;
                }
                else if ((n10 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 375;
                    this.jjmatchedPos = 7;
                }
                else if ((n12 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 428;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 4611686018528051200L, n4, 0L, n6, 1688849860263936L, n8, 34376515584L, n10, 4398046511104L, n12, 36028797555834912L);
            }
            case 'G': {
                if ((n6 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 247;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 262144L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa8_0(n2, 240518168576L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 43982612594688L);
            }
            case 'K': {
                if ((n6 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 220;
                    this.jjmatchedPos = 7;
                    break;
                }
                break;
            }
            case 'L': {
                if ((n4 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 139;
                    this.jjmatchedPos = 7;
                }
                else if ((n4 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 185;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x10000000000L) != 0x0L) {
                    this.jjmatchedKind = 296;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 17179869184L, n6, 0L, n8, 0L, n10, 0L, n12, 138412032L);
            }
            case 'M': {
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1879048192L, n12, 0L);
            }
            case 'N': {
                if ((n4 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 149;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 8796093022208L, n8, 141733920768L, n10, 16778240L, n12, 1099511627776L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa8_0(n2, 1101659112448L, n4, 17600775979136L, n6, 0L, n8, 0L, n10, 8L, n12, 0L);
            }
            case 'R': {
                if ((n6 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 233;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 264;
                    this.jjmatchedPos = 7;
                }
                else if ((n12 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 407;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 281474977234944L, n8, 16L, n10, 0L, n12, 0L);
            }
            case 'S': {
                if ((n6 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 203;
                    this.jjmatchedPos = 7;
                    break;
                }
                if ((n8 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 282;
                    this.jjmatchedPos = 7;
                    break;
                }
                if ((n8 & 0x20000000000000L) != 0x0L) {
                    this.jjmatchedKind = 309;
                    this.jjmatchedPos = 7;
                    break;
                }
                if ((n10 & 0x4000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 382;
                    this.jjmatchedPos = 7;
                    break;
                }
                break;
            }
            case 'T': {
                if ((n2 & Long.MIN_VALUE) != 0x0L) {
                    this.jjmatchedKind = 127;
                    this.jjmatchedPos = 7;
                }
                else if ((n6 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 217;
                    this.jjmatchedPos = 7;
                }
                else if ((n6 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 228;
                    this.jjmatchedPos = 7;
                }
                else if ((n12 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 391;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 2305843009213693952L, n4, 139586437120L, n6, 31525197391593472L, n8, 0L, n10, 594475159402840064L, n12, 2251799813685248L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 35184372088832L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'V': {
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 1024L);
            }
            case 'Y': {
                if ((n4 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 158;
                    this.jjmatchedPos = 7;
                }
                else if ((n10 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 332;
                    this.jjmatchedPos = 7;
                }
                else if ((n10 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 376;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 0L, n8, 68719476736L, n10, 0L, n12, 0L);
            }
            case 'Z': {
                return this.jjMoveStringLiteralDfa8_0(n2, 4096L, n4, 0L, n6, 0L, n8, 0L, n10, 8192L, n12, 0L);
            }
            case '_': {
                return this.jjMoveStringLiteralDfa8_0(n2, 527765581332480L, n4, 0L, n6, 17179869184L, n8, 0L, n10, 549764186112L, n12, 262144L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa8_0(n2, 2251799813685248L, n4, 0L, n6, 0L, n8, 0L, n10, 144115188075855872L, n12, 288230376151711744L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa8_0(n2, 72057594037927936L, n4, 0L, n6, 0L, n8, 0L, n10, 1125899906842656L, n12, 0L);
            }
            case 'c': {
                if ((n4 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 132;
                    this.jjmatchedPos = 7;
                }
                else if ((n12 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 433;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 2199023255552L, n6, 8388608L, n8, 0L, n10, 0L, n12, 34359738368L);
            }
            case 'd': {
                if ((n2 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 85;
                    this.jjmatchedPos = 7;
                }
                else if ((n2 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 121;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 16L);
            }
            case 'e': {
                if ((n2 & 0x8L) != 0x0L) {
                    this.jjmatchedKind = 67;
                    this.jjmatchedPos = 7;
                }
                else if ((n2 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 93;
                    this.jjmatchedPos = 7;
                }
                else if ((n2 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 102;
                    this.jjmatchedPos = 7;
                }
                else if ((n2 & 0x1000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 124;
                    this.jjmatchedPos = 7;
                }
                else if ((n6 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 207;
                    this.jjmatchedPos = 7;
                }
                else if ((n6 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 216;
                    this.jjmatchedPos = 7;
                }
                else if ((n6 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 234;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 298;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 304;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x4000000000000L) != 0x0L) {
                    this.jjmatchedKind = 306;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 307;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x1000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 316;
                    this.jjmatchedPos = 7;
                }
                else if ((n10 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 351;
                    this.jjmatchedPos = 7;
                }
                else if ((n10 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 361;
                    this.jjmatchedPos = 7;
                }
                else if ((n10 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 375;
                    this.jjmatchedPos = 7;
                }
                else if ((n12 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 428;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 4611686018528051200L, n4, 0L, n6, 1688849860263936L, n8, 34376515584L, n10, 4398046511104L, n12, 36028797555834912L);
            }
            case 'g': {
                if ((n6 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 247;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 262144L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa8_0(n2, 240518168576L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 43982612594688L);
            }
            case 'k': {
                if ((n6 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 220;
                    this.jjmatchedPos = 7;
                    break;
                }
                break;
            }
            case 'l': {
                if ((n4 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 139;
                    this.jjmatchedPos = 7;
                }
                else if ((n4 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 185;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x10000000000L) != 0x0L) {
                    this.jjmatchedKind = 296;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 17179869184L, n6, 0L, n8, 0L, n10, 0L, n12, 138412032L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1879048192L, n12, 0L);
            }
            case 'n': {
                if ((n4 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 149;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 8796093022208L, n8, 141733920768L, n10, 16778240L, n12, 1099511627776L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa8_0(n2, 1101659112448L, n4, 17600775979136L, n6, 0L, n8, 0L, n10, 8L, n12, 0L);
            }
            case 'r': {
                if ((n6 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 233;
                    this.jjmatchedPos = 7;
                }
                else if ((n8 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 264;
                    this.jjmatchedPos = 7;
                }
                else if ((n12 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 407;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 281474977234944L, n8, 16L, n10, 0L, n12, 0L);
            }
            case 's': {
                if ((n6 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 203;
                    this.jjmatchedPos = 7;
                    break;
                }
                if ((n8 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 282;
                    this.jjmatchedPos = 7;
                    break;
                }
                if ((n8 & 0x20000000000000L) != 0x0L) {
                    this.jjmatchedKind = 309;
                    this.jjmatchedPos = 7;
                    break;
                }
                if ((n10 & 0x4000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 382;
                    this.jjmatchedPos = 7;
                    break;
                }
                break;
            }
            case 't': {
                if ((n2 & Long.MIN_VALUE) != 0x0L) {
                    this.jjmatchedKind = 127;
                    this.jjmatchedPos = 7;
                }
                else if ((n6 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 217;
                    this.jjmatchedPos = 7;
                }
                else if ((n6 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 228;
                    this.jjmatchedPos = 7;
                }
                else if ((n12 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 391;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 2305843009213693952L, n4, 139586437120L, n6, 31525197391593472L, n8, 0L, n10, 594475159402840064L, n12, 2251799813685248L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 35184372088832L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 1024L);
            }
            case 'y': {
                if ((n4 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 158;
                    this.jjmatchedPos = 7;
                }
                else if ((n10 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 332;
                    this.jjmatchedPos = 7;
                }
                else if ((n10 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 376;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(n2, 0L, n4, 0L, n6, 0L, n8, 68719476736L, n10, 0L, n12, 0L);
            }
            case 'z': {
                return this.jjMoveStringLiteralDfa8_0(n2, 4096L, n4, 0L, n6, 0L, n8, 0L, n10, 8192L, n12, 0L);
            }
        }
        return this.jjMoveNfa_0(0, 7);
    }
    
    private final int jjMoveStringLiteralDfa8_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10, final long n11, long n12) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9) | (n12 &= n11)) == 0x0L) {
            return this.jjMoveNfa_0(0, 7);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 7);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa9_0(n2, 4096L, n4, 0L, n6, 0L, n8, 0L, n10, 8192L, n12, 35184372088832L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 16L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa9_0(n2, 4611686018427387904L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 288239172244733952L);
            }
            case 'D': {
                if ((n8 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 280;
                    this.jjmatchedPos = 8;
                }
                else if ((n8 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 291;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 35184372088832L, n4, 0L, n6, 0L, n8, 0L, n10, 262144L, n12, 0L);
            }
            case 'E': {
                if ((n4 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 159;
                    this.jjmatchedPos = 8;
                }
                else if ((n6 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 211;
                    this.jjmatchedPos = 8;
                }
                else if ((n6 & 0x20000000000000L) != 0x0L) {
                    this.jjmatchedKind = 245;
                    this.jjmatchedPos = 8;
                }
                else if ((n10 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 379;
                    this.jjmatchedPos = 8;
                }
                else if ((n12 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 394;
                    this.jjmatchedPos = 8;
                }
                else if ((n12 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 406;
                    this.jjmatchedPos = 8;
                }
                else if ((n12 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 411;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 8650752L, n8, 0L, n10, 1125899906842624L, n12, 2147483648L);
            }
            case 'F': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 16384L, n12, 0L);
            }
            case 'G': {
                if ((n6 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 235;
                    this.jjmatchedPos = 8;
                    break;
                }
                if ((n12 & 0x10000000000L) != 0x0L) {
                    this.jjmatchedKind = 424;
                    this.jjmatchedPos = 8;
                    break;
                }
                break;
            }
            case 'H': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 131072L, n12, 0L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa9_0(n2, 2305843009213693952L, n4, 137438953472L, n6, 22517998136852480L, n8, 4294967296L, n10, 0L, n12, 34359738368L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa9_0(n2, 72057594037927936L, n4, 0L, n6, 0L, n8, 0L, n10, 144115188075855904L, n12, 262144L);
            }
            case 'M': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1114112L, n12, 0L);
            }
            case 'N': {
                if ((n2 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 74;
                    this.jjmatchedPos = 8;
                }
                else if ((n2 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 95;
                    this.jjmatchedPos = 8;
                }
                else if ((n4 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 135;
                    this.jjmatchedPos = 8;
                }
                else if ((n4 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 172;
                    this.jjmatchedPos = 8;
                }
                else if ((n10 & 0x8L) != 0x0L) {
                    this.jjmatchedKind = 323;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 1305670057984L, n4, 0L, n6, 0L, n8, 0L, n10, 4398046511104L, n12, 32L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa9_0(n2, 34359738368L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'P': {
                if ((n10 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 348;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1610612736L, n12, 0L);
            }
            case 'Q': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 2097152L, n12, 0L);
            }
            case 'R': {
                if ((n2 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 89;
                    this.jjmatchedPos = 8;
                }
                else if ((n4 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 161;
                    this.jjmatchedPos = 8;
                }
                else if ((n12 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 413;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 67108864L, n4, 0L, n6, 0L, n8, 0L, n10, 549755813888L, n12, 0L);
            }
            case 'S': {
                if ((n10 & 0x40000000000000L) != 0x0L) {
                    this.jjmatchedKind = 374;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 35184372088832L, n8, 0L, n10, 32768L, n12, 0L);
            }
            case 'T': {
                if ((n4 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 169;
                    this.jjmatchedPos = 8;
                }
                else if ((n8 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 293;
                    this.jjmatchedPos = 8;
                }
                else if ((n10 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 330;
                    this.jjmatchedPos = 8;
                }
                else if ((n10 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 344;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 2462906046218240L, n4, 0L, n6, 0L, n8, 0L, n10, 8589934592L, n12, 0L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa9_0(n2, 281474976710656L, n4, 0L, n6, 17179869184L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'W': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 524288L, n12, 0L);
            }
            case 'Y': {
                if ((n4 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 162;
                    this.jjmatchedPos = 8;
                }
                else if ((n6 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 240;
                    this.jjmatchedPos = 8;
                }
                else if ((n8 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 260;
                    this.jjmatchedPos = 8;
                }
                else if ((n12 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 435;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 4194304L, n12, 0L);
            }
            case '_': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 1688849860263936L, n8, 68719476736L, n10, 0L, n12, 36028797018963968L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa9_0(n2, 4096L, n4, 0L, n6, 0L, n8, 0L, n10, 8192L, n12, 35184372088832L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 16L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa9_0(n2, 4611686018427387904L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 288239172244733952L);
            }
            case 'd': {
                if ((n8 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 280;
                    this.jjmatchedPos = 8;
                }
                else if ((n8 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 291;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 35184372088832L, n4, 0L, n6, 0L, n8, 0L, n10, 262144L, n12, 0L);
            }
            case 'e': {
                if ((n4 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 159;
                    this.jjmatchedPos = 8;
                }
                else if ((n6 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 211;
                    this.jjmatchedPos = 8;
                }
                else if ((n6 & 0x20000000000000L) != 0x0L) {
                    this.jjmatchedKind = 245;
                    this.jjmatchedPos = 8;
                }
                else if ((n10 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 379;
                    this.jjmatchedPos = 8;
                }
                else if ((n12 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 394;
                    this.jjmatchedPos = 8;
                }
                else if ((n12 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 406;
                    this.jjmatchedPos = 8;
                }
                else if ((n12 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 411;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 8650752L, n8, 0L, n10, 1125899906842624L, n12, 2147483648L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 16384L, n12, 0L);
            }
            case 'g': {
                if ((n6 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 235;
                    this.jjmatchedPos = 8;
                    break;
                }
                if ((n12 & 0x10000000000L) != 0x0L) {
                    this.jjmatchedKind = 424;
                    this.jjmatchedPos = 8;
                    break;
                }
                break;
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 131072L, n12, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa9_0(n2, 2305843009213693952L, n4, 137438953472L, n6, 22517998136852480L, n8, 4294967296L, n10, 0L, n12, 34359738368L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa9_0(n2, 72057594037927936L, n4, 0L, n6, 0L, n8, 0L, n10, 144115188075855904L, n12, 262144L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1114112L, n12, 0L);
            }
            case 'n': {
                if ((n2 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 74;
                    this.jjmatchedPos = 8;
                }
                else if ((n2 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 95;
                    this.jjmatchedPos = 8;
                }
                else if ((n4 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 135;
                    this.jjmatchedPos = 8;
                }
                else if ((n4 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 172;
                    this.jjmatchedPos = 8;
                }
                else if ((n10 & 0x8L) != 0x0L) {
                    this.jjmatchedKind = 323;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 1305670057984L, n4, 0L, n6, 0L, n8, 0L, n10, 4398046511104L, n12, 32L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa9_0(n2, 34359738368L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'p': {
                if ((n10 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 348;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1610612736L, n12, 0L);
            }
            case 'q': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 2097152L, n12, 0L);
            }
            case 'r': {
                if ((n2 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 89;
                    this.jjmatchedPos = 8;
                }
                else if ((n4 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 161;
                    this.jjmatchedPos = 8;
                }
                else if ((n12 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 413;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 67108864L, n4, 0L, n6, 0L, n8, 0L, n10, 549755813888L, n12, 0L);
            }
            case 's': {
                if ((n10 & 0x40000000000000L) != 0x0L) {
                    this.jjmatchedKind = 374;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 35184372088832L, n8, 0L, n10, 32768L, n12, 0L);
            }
            case 't': {
                if ((n4 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 169;
                    this.jjmatchedPos = 8;
                }
                else if ((n8 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 293;
                    this.jjmatchedPos = 8;
                }
                else if ((n10 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 330;
                    this.jjmatchedPos = 8;
                }
                else if ((n10 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 344;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 2462906046218240L, n4, 0L, n6, 0L, n8, 0L, n10, 8589934592L, n12, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa9_0(n2, 281474976710656L, n4, 0L, n6, 17179869184L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'w': {
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 524288L, n12, 0L);
            }
            case 'y': {
                if ((n4 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 162;
                    this.jjmatchedPos = 8;
                }
                else if ((n6 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 240;
                    this.jjmatchedPos = 8;
                }
                else if ((n8 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 260;
                    this.jjmatchedPos = 8;
                }
                else if ((n12 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 435;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 4194304L, n12, 0L);
            }
        }
        return this.jjMoveNfa_0(0, 8);
    }
    
    private final int jjMoveStringLiteralDfa9_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10, final long n11, long n12) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9) | (n12 &= n11)) == 0x0L) {
            return this.jjMoveNfa_0(0, 8);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 8);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa10_0(n2, 35184372088832L, n4, 0L, n6, 0L, n8, 0L, n10, 537133056L, n12, 0L);
            }
            case 'B': {
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 8192L, n12, 0L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa10_0(n2, 2305843009213693952L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 16L);
            }
            case 'D': {
                return this.jjMoveStringLiteralDfa10_0(n2, 1099511627776L, n4, 0L, n6, 0L, n8, 0L, n10, 1073741824L, n12, 0L);
            }
            case 'E': {
                if ((n2 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 115;
                    this.jjmatchedPos = 9;
                }
                else if ((n2 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 120;
                    this.jjmatchedPos = 9;
                }
                else if ((n10 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 325;
                    this.jjmatchedPos = 9;
                }
                else if ((n12 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 442;
                    this.jjmatchedPos = 9;
                }
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 35184372088832L, n8, 0L, n10, 8594685952L, n12, 36028797018963968L);
            }
            case 'H': {
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 562949953421312L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa10_0(n2, 211106232532992L, n4, 0L, n6, 0L, n8, 0L, n10, 144115188075921408L, n12, 0L);
            }
            case 'L': {
                if ((n12 & 0x200000000000L) != 0x0L) {
                    this.jjmatchedKind = 429;
                    this.jjmatchedPos = 9;
                    break;
                }
                break;
            }
            case 'M': {
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 1125899906842624L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'N': {
                if ((n2 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 99;
                    this.jjmatchedPos = 9;
                }
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 34359738368L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 22517998136852480L, n8, 0L, n10, 549756993536L, n12, 262144L);
            }
            case 'R': {
                if ((n10 & 0x4000000000000L) != 0x0L) {
                    this.jjmatchedKind = 370;
                    this.jjmatchedPos = 9;
                }
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 16384L, n12, 0L);
            }
            case 'S': {
                if ((n6 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 210;
                    this.jjmatchedPos = 9;
                }
                else if ((n6 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 215;
                    this.jjmatchedPos = 9;
                }
                else if ((n12 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 415;
                    this.jjmatchedPos = 9;
                }
                else if ((n12 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 427;
                    this.jjmatchedPos = 9;
                }
                return this.jjMoveStringLiteralDfa10_0(n2, 281474976710656L, n4, 0L, n6, 17179869184L, n8, 4294967296L, n10, 0L, n12, 0L);
            }
            case 'T': {
                if ((n2 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 100;
                    this.jjmatchedPos = 9;
                }
                else if ((n2 & 0x4000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 126;
                    this.jjmatchedPos = 9;
                }
                else if ((n12 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 389;
                    this.jjmatchedPos = 9;
                }
                return this.jjMoveStringLiteralDfa10_0(n2, 137438957568L, n4, 0L, n6, 0L, n8, 0L, n10, 4398046511104L, n12, 0L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 2097152L, n12, 0L);
            }
            case 'V': {
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 137438953472L, n6, 0L, n8, 68719476736L, n10, 0L, n12, 0L);
            }
            case '_': {
                return this.jjMoveStringLiteralDfa10_0(n2, 67108864L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa10_0(n2, 35184372088832L, n4, 0L, n6, 0L, n8, 0L, n10, 537133056L, n12, 0L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 8192L, n12, 0L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa10_0(n2, 2305843009213693952L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 16L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa10_0(n2, 1099511627776L, n4, 0L, n6, 0L, n8, 0L, n10, 1073741824L, n12, 0L);
            }
            case 'e': {
                if ((n2 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 115;
                    this.jjmatchedPos = 9;
                }
                else if ((n2 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 120;
                    this.jjmatchedPos = 9;
                }
                else if ((n10 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 325;
                    this.jjmatchedPos = 9;
                }
                else if ((n12 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 442;
                    this.jjmatchedPos = 9;
                }
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 35184372088832L, n8, 0L, n10, 8594685952L, n12, 36028797018963968L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 562949953421312L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa10_0(n2, 211106232532992L, n4, 0L, n6, 0L, n8, 0L, n10, 144115188075921408L, n12, 0L);
            }
            case 'l': {
                if ((n12 & 0x200000000000L) != 0x0L) {
                    this.jjmatchedKind = 429;
                    this.jjmatchedPos = 9;
                    break;
                }
                break;
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 1125899906842624L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'n': {
                if ((n2 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 99;
                    this.jjmatchedPos = 9;
                }
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 34359738368L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 22517998136852480L, n8, 0L, n10, 549756993536L, n12, 262144L);
            }
            case 'r': {
                if ((n10 & 0x4000000000000L) != 0x0L) {
                    this.jjmatchedKind = 370;
                    this.jjmatchedPos = 9;
                }
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 16384L, n12, 0L);
            }
            case 's': {
                if ((n6 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 210;
                    this.jjmatchedPos = 9;
                }
                else if ((n6 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 215;
                    this.jjmatchedPos = 9;
                }
                else if ((n12 & 0x80000000L) != 0x0L) {
                    this.jjmatchedKind = 415;
                    this.jjmatchedPos = 9;
                }
                else if ((n12 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 427;
                    this.jjmatchedPos = 9;
                }
                return this.jjMoveStringLiteralDfa10_0(n2, 281474976710656L, n4, 0L, n6, 17179869184L, n8, 4294967296L, n10, 0L, n12, 0L);
            }
            case 't': {
                if ((n2 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 100;
                    this.jjmatchedPos = 9;
                }
                else if ((n2 & 0x4000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 126;
                    this.jjmatchedPos = 9;
                }
                else if ((n12 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 389;
                    this.jjmatchedPos = 9;
                }
                return this.jjMoveStringLiteralDfa10_0(n2, 137438957568L, n4, 0L, n6, 0L, n8, 0L, n10, 4398046511104L, n12, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 2097152L, n12, 0L);
            }
            case 'v': {
                return this.jjMoveStringLiteralDfa10_0(n2, 0L, n4, 137438953472L, n6, 0L, n8, 68719476736L, n10, 0L, n12, 0L);
            }
        }
        return this.jjMoveNfa_0(0, 9);
    }
    
    private final int jjMoveStringLiteralDfa10_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10, final long n11, long n12) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9) | (n12 &= n11)) == 0x0L) {
            return this.jjMoveNfa_0(0, 9);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 9);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 68719476736L, n10, 6307840L, n12, 0L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 4398046543872L, n12, 262144L);
            }
            case 'D': {
                if ((n10 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 353;
                    this.jjmatchedPos = 10;
                }
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 536870912L, n12, 0L);
            }
            case 'E': {
                if ((n4 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 165;
                    this.jjmatchedPos = 10;
                }
                return this.jjMoveStringLiteralDfa11_0(n2, 281474976710656L, n4, 0L, n6, 17179869184L, n8, 0L, n10, 524288L, n12, 0L);
            }
            case 'G': {
                if ((n12 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 419;
                    this.jjmatchedPos = 10;
                    break;
                }
                break;
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa11_0(n2, 1099511631872L, n4, 0L, n6, 1125899906842624L, n8, 0L, n10, 1073741824L, n12, 0L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa11_0(n2, 67108864L, n4, 0L, n6, 0L, n8, 0L, n10, 549755822080L, n12, 0L);
            }
            case 'M': {
                return this.jjMoveStringLiteralDfa11_0(n2, 211106232532992L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'N': {
                if ((n6 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 244;
                    this.jjmatchedPos = 10;
                }
                else if ((n6 & 0x40000000000000L) != 0x0L) {
                    this.jjmatchedKind = 246;
                    this.jjmatchedPos = 10;
                }
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1114112L, n12, 36028797018963968L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 562949953421312L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'R': {
                if ((n6 & 0x200000000000L) != 0x0L) {
                    this.jjmatchedKind = 237;
                    this.jjmatchedPos = 10;
                    break;
                }
                break;
            }
            case 'S': {
                if ((n2 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 101;
                    this.jjmatchedPos = 10;
                    break;
                }
                if ((n2 & 0x2000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 10;
                    break;
                }
                break;
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa11_0(n2, 35184372088832L, n4, 0L, n6, 0L, n8, 4294967296L, n10, 0L, n12, 0L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 131072L, n12, 0L);
            }
            case 'Y': {
                if ((n10 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 338;
                    this.jjmatchedPos = 10;
                    break;
                }
                break;
            }
            case 'Z': {
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 144115188075855872L, n12, 0L);
            }
            case '_': {
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 16L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 68719476736L, n10, 6307840L, n12, 0L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 4398046543872L, n12, 262144L);
            }
            case 'd': {
                if ((n10 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 353;
                    this.jjmatchedPos = 10;
                }
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 536870912L, n12, 0L);
            }
            case 'e': {
                if ((n4 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 165;
                    this.jjmatchedPos = 10;
                }
                return this.jjMoveStringLiteralDfa11_0(n2, 281474976710656L, n4, 0L, n6, 17179869184L, n8, 0L, n10, 524288L, n12, 0L);
            }
            case 'g': {
                if ((n12 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 419;
                    this.jjmatchedPos = 10;
                    break;
                }
                break;
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa11_0(n2, 1099511631872L, n4, 0L, n6, 1125899906842624L, n8, 0L, n10, 1073741824L, n12, 0L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa11_0(n2, 67108864L, n4, 0L, n6, 0L, n8, 0L, n10, 549755822080L, n12, 0L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa11_0(n2, 211106232532992L, n4, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'n': {
                if ((n6 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 244;
                    this.jjmatchedPos = 10;
                }
                else if ((n6 & 0x40000000000000L) != 0x0L) {
                    this.jjmatchedKind = 246;
                    this.jjmatchedPos = 10;
                }
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 1114112L, n12, 36028797018963968L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 562949953421312L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'r': {
                if ((n6 & 0x200000000000L) != 0x0L) {
                    this.jjmatchedKind = 237;
                    this.jjmatchedPos = 10;
                    break;
                }
                break;
            }
            case 's': {
                if ((n2 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 101;
                    this.jjmatchedPos = 10;
                    break;
                }
                if ((n2 & 0x2000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 125;
                    this.jjmatchedPos = 10;
                    break;
                }
                break;
            }
            case 't': {
                return this.jjMoveStringLiteralDfa11_0(n2, 35184372088832L, n4, 0L, n6, 0L, n8, 4294967296L, n10, 0L, n12, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 131072L, n12, 0L);
            }
            case 'y': {
                if ((n10 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 338;
                    this.jjmatchedPos = 10;
                    break;
                }
                break;
            }
            case 'z': {
                return this.jjMoveStringLiteralDfa11_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 144115188075855872L, n12, 0L);
            }
        }
        return this.jjMoveNfa_0(0, 10);
    }
    
    private final int jjMoveStringLiteralDfa11_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10, final long n11, long n12) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9) | (n12 &= n11)) == 0x0L) {
            return this.jjMoveNfa_0(0, 10);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 10);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 262144L);
            }
            case 'C': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 0L, n10, 16384L, n12, 0L);
            }
            case 'D': {
                if ((n10 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 349;
                    this.jjmatchedPos = 11;
                    break;
                }
                if ((n12 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 439;
                    this.jjmatchedPos = 11;
                    break;
                }
                break;
            }
            case 'E': {
                if ((n2 & 0x200000000000L) != 0x0L) {
                    this.jjmatchedKind = 109;
                    this.jjmatchedPos = 11;
                }
                else if ((n2 & 0x400000000000L) != 0x0L) {
                    this.jjmatchedKind = 110;
                    this.jjmatchedPos = 11;
                }
                else if ((n10 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 333;
                    this.jjmatchedPos = 11;
                }
                else if ((n10 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 359;
                    this.jjmatchedPos = 11;
                }
                else if ((n10 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 377;
                    this.jjmatchedPos = 11;
                }
                return this.jjMoveStringLiteralDfa12_0(n2, 140737555464192L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'F': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 0L, n10, 1073741824L, n12, 0L);
            }
            case 'I': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 4294967296L, n10, 0L, n12, 0L);
            }
            case 'K': {
                if ((n10 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 339;
                    this.jjmatchedPos = 11;
                    break;
                }
                break;
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 68719476736L, n10, 0L, n12, 0L);
            }
            case 'N': {
                return this.jjMoveStringLiteralDfa12_0(n2, 1099511627776L, n6, 1125899906842624L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa12_0(n2, 4096L, n6, 0L, n8, 0L, n10, 4398046543872L, n12, 0L);
            }
            case 'R': {
                if ((n2 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 112;
                    this.jjmatchedPos = 11;
                }
                else if ((n6 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 226;
                    this.jjmatchedPos = 11;
                }
                else if ((n10 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 337;
                    this.jjmatchedPos = 11;
                }
                else if ((n10 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 342;
                    this.jjmatchedPos = 11;
                }
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 0L, n10, 2097152L, n12, 16L);
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 0L, n10, 1048576L, n12, 0L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 562949953421312L, n8, 0L, n10, 65536L, n12, 0L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 0L, n10, 0L, n12, 262144L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 0L, n10, 16384L, n12, 0L);
            }
            case 'd': {
                if ((n10 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 349;
                    this.jjmatchedPos = 11;
                    break;
                }
                if ((n12 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 439;
                    this.jjmatchedPos = 11;
                    break;
                }
                break;
            }
            case 'e': {
                if ((n2 & 0x200000000000L) != 0x0L) {
                    this.jjmatchedKind = 109;
                    this.jjmatchedPos = 11;
                }
                else if ((n2 & 0x400000000000L) != 0x0L) {
                    this.jjmatchedKind = 110;
                    this.jjmatchedPos = 11;
                }
                else if ((n10 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 333;
                    this.jjmatchedPos = 11;
                }
                else if ((n10 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 359;
                    this.jjmatchedPos = 11;
                }
                else if ((n10 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 377;
                    this.jjmatchedPos = 11;
                }
                return this.jjMoveStringLiteralDfa12_0(n2, 140737555464192L, n6, 0L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 0L, n10, 1073741824L, n12, 0L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 4294967296L, n10, 0L, n12, 0L);
            }
            case 'k': {
                if ((n10 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 339;
                    this.jjmatchedPos = 11;
                    break;
                }
                break;
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 68719476736L, n10, 0L, n12, 0L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa12_0(n2, 1099511627776L, n6, 1125899906842624L, n8, 0L, n10, 0L, n12, 0L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa12_0(n2, 4096L, n6, 0L, n8, 0L, n10, 4398046543872L, n12, 0L);
            }
            case 'r': {
                if ((n2 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 112;
                    this.jjmatchedPos = 11;
                }
                else if ((n6 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 226;
                    this.jjmatchedPos = 11;
                }
                else if ((n10 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 337;
                    this.jjmatchedPos = 11;
                }
                else if ((n10 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 342;
                    this.jjmatchedPos = 11;
                }
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 0L, n10, 2097152L, n12, 16L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 0L, n8, 0L, n10, 1048576L, n12, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa12_0(n2, 0L, n6, 562949953421312L, n8, 0L, n10, 65536L, n12, 0L);
            }
        }
        return this.jjMoveNfa_0(0, 11);
    }
    
    private final int jjMoveStringLiteralDfa12_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9)) == 0x0L) {
            return this.jjMoveNfa_0(0, 11);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 11);
        }
        switch (this.curChar) {
            case 'C': {
                if ((n6 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 288;
                    this.jjmatchedPos = 12;
                    break;
                }
                break;
            }
            case 'E': {
                return this.jjMoveStringLiteralDfa13_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 16L);
            }
            case 'F': {
                if ((n8 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 350;
                    this.jjmatchedPos = 12;
                    break;
                }
                break;
            }
            case 'G': {
                if ((n2 & 0x10000000000L) != 0x0L) {
                    this.jjmatchedKind = 104;
                    this.jjmatchedPos = 12;
                    break;
                }
                break;
            }
            case 'H': {
                if ((n8 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 340;
                    this.jjmatchedPos = 12;
                    break;
                }
                break;
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa13_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 262144L);
            }
            case 'N': {
                if ((n2 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 76;
                    this.jjmatchedPos = 12;
                }
                return this.jjMoveStringLiteralDfa13_0(n2, 67108864L, n4, 0L, n6, 0L, n8, 4398046543872L, n10, 0L);
            }
            case 'R': {
                if ((n4 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 241;
                    this.jjmatchedPos = 12;
                    break;
                }
                break;
            }
            case 'S': {
                return this.jjMoveStringLiteralDfa13_0(n2, 140737488355328L, n4, 0L, n6, 0L, n8, 0L, n10, 0L);
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa13_0(n2, 0L, n4, 0L, n6, 0L, n8, 2162688L, n10, 0L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa13_0(n2, 0L, n4, 1125899906842624L, n6, 0L, n8, 0L, n10, 0L);
            }
            case '_': {
                return this.jjMoveStringLiteralDfa13_0(n2, 0L, n4, 0L, n6, 68719476736L, n8, 16384L, n10, 0L);
            }
            case 'c': {
                if ((n6 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 288;
                    this.jjmatchedPos = 12;
                    break;
                }
                break;
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa13_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 16L);
            }
            case 'f': {
                if ((n8 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 350;
                    this.jjmatchedPos = 12;
                    break;
                }
                break;
            }
            case 'g': {
                if ((n2 & 0x10000000000L) != 0x0L) {
                    this.jjmatchedKind = 104;
                    this.jjmatchedPos = 12;
                    break;
                }
                break;
            }
            case 'h': {
                if ((n8 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 340;
                    this.jjmatchedPos = 12;
                    break;
                }
                break;
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa13_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 262144L);
            }
            case 'n': {
                if ((n2 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 76;
                    this.jjmatchedPos = 12;
                }
                return this.jjMoveStringLiteralDfa13_0(n2, 67108864L, n4, 0L, n6, 0L, n8, 4398046543872L, n10, 0L);
            }
            case 'r': {
                if ((n4 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 241;
                    this.jjmatchedPos = 12;
                    break;
                }
                break;
            }
            case 's': {
                return this.jjMoveStringLiteralDfa13_0(n2, 140737488355328L, n4, 0L, n6, 0L, n8, 0L, n10, 0L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa13_0(n2, 0L, n4, 0L, n6, 0L, n8, 2162688L, n10, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa13_0(n2, 0L, n4, 1125899906842624L, n6, 0L, n8, 0L, n10, 0L);
            }
        }
        return this.jjMoveNfa_0(0, 12);
    }
    
    private final int jjMoveStringLiteralDfa13_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9)) == 0x0L) {
            return this.jjMoveNfa_0(0, 12);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 12);
        }
        switch (this.curChar) {
            case 'D': {
                if ((n8 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 335;
                    this.jjmatchedPos = 13;
                    break;
                }
                break;
            }
            case 'E': {
                if ((n8 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 336;
                    this.jjmatchedPos = 13;
                }
                else if ((n10 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 402;
                    this.jjmatchedPos = 13;
                }
                return this.jjMoveStringLiteralDfa14_0(n2, 0L, n4, 0L, n6, 0L, n8, 2097152L, n10, 0L);
            }
            case 'G': {
                return this.jjMoveStringLiteralDfa14_0(n2, 67108864L, n4, 0L, n6, 0L, n8, 0L, n10, 0L);
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa14_0(n2, 0L, n4, 0L, n6, 68719476736L, n8, 0L, n10, 0L);
            }
            case 'N': {
                return this.jjMoveStringLiteralDfa14_0(n2, 0L, n4, 0L, n6, 0L, n8, 4398046511104L, n10, 0L);
            }
            case 'S': {
                return this.jjMoveStringLiteralDfa14_0(n2, 0L, n4, 0L, n6, 0L, n8, 16384L, n10, 16L);
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa14_0(n2, 140737488355328L, n4, 1125899906842624L, n6, 0L, n8, 0L, n10, 0L);
            }
            case 'd': {
                if ((n8 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 335;
                    this.jjmatchedPos = 13;
                    break;
                }
                break;
            }
            case 'e': {
                if ((n8 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 336;
                    this.jjmatchedPos = 13;
                }
                else if ((n10 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 402;
                    this.jjmatchedPos = 13;
                }
                return this.jjMoveStringLiteralDfa14_0(n2, 0L, n4, 0L, n6, 0L, n8, 2097152L, n10, 0L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa14_0(n2, 67108864L, n4, 0L, n6, 0L, n8, 0L, n10, 0L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa14_0(n2, 0L, n4, 0L, n6, 68719476736L, n8, 0L, n10, 0L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa14_0(n2, 0L, n4, 0L, n6, 0L, n8, 4398046511104L, n10, 0L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa14_0(n2, 0L, n4, 0L, n6, 0L, n8, 16384L, n10, 16L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa14_0(n2, 140737488355328L, n4, 1125899906842624L, n6, 0L, n8, 0L, n10, 0L);
            }
        }
        return this.jjMoveNfa_0(0, 13);
    }
    
    private final int jjMoveStringLiteralDfa14_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9)) == 0x0L) {
            return this.jjMoveNfa_0(0, 13);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 13);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa15_0(n2, 140737488355328L, n4, 0L, n6, 0L, n8, 0L, n10, 0L);
            }
            case 'E': {
                if ((n4 & 0x4000000000000L) != 0x0L) {
                    this.jjmatchedKind = 242;
                    this.jjmatchedPos = 14;
                }
                return this.jjMoveStringLiteralDfa15_0(n2, 0L, n4, 0L, n6, 0L, n8, 4398046527488L, n10, 0L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa15_0(n2, 0L, n4, 0L, n6, 68719476736L, n8, 0L, n10, 0L);
            }
            case 'R': {
                if ((n8 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 341;
                    this.jjmatchedPos = 14;
                    break;
                }
                break;
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa15_0(n2, 67108864L, n4, 0L, n6, 0L, n8, 0L, n10, 0L);
            }
            case 'U': {
                return this.jjMoveStringLiteralDfa15_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 16L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa15_0(n2, 140737488355328L, n4, 0L, n6, 0L, n8, 0L, n10, 0L);
            }
            case 'e': {
                if ((n4 & 0x4000000000000L) != 0x0L) {
                    this.jjmatchedKind = 242;
                    this.jjmatchedPos = 14;
                }
                return this.jjMoveStringLiteralDfa15_0(n2, 0L, n4, 0L, n6, 0L, n8, 4398046527488L, n10, 0L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa15_0(n2, 0L, n4, 0L, n6, 68719476736L, n8, 0L, n10, 0L);
            }
            case 'r': {
                if ((n8 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 341;
                    this.jjmatchedPos = 14;
                    break;
                }
                break;
            }
            case 't': {
                return this.jjMoveStringLiteralDfa15_0(n2, 67108864L, n4, 0L, n6, 0L, n8, 0L, n10, 0L);
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa15_0(n2, 0L, n4, 0L, n6, 0L, n8, 0L, n10, 16L);
            }
        }
        return this.jjMoveNfa_0(0, 14);
    }
    
    private final int jjMoveStringLiteralDfa15_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8, final long n9, long n10) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7) | (n10 &= n9)) == 0x0L) {
            return this.jjMoveNfa_0(0, 14);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 14);
        }
        switch (this.curChar) {
            case 'C': {
                return this.jjMoveStringLiteralDfa16_0(n2, 0L, n6, 68719476736L, n8, 4398046527488L, n10, 0L);
            }
            case 'H': {
                if ((n2 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 90;
                    this.jjmatchedPos = 15;
                    break;
                }
                break;
            }
            case 'L': {
                return this.jjMoveStringLiteralDfa16_0(n2, 0L, n6, 0L, n8, 0L, n10, 16L);
            }
            case 'M': {
                return this.jjMoveStringLiteralDfa16_0(n2, 140737488355328L, n6, 0L, n8, 0L, n10, 0L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa16_0(n2, 0L, n6, 68719476736L, n8, 4398046527488L, n10, 0L);
            }
            case 'h': {
                if ((n2 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 90;
                    this.jjmatchedPos = 15;
                    break;
                }
                break;
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa16_0(n2, 0L, n6, 0L, n8, 0L, n10, 16L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa16_0(n2, 140737488355328L, n6, 0L, n8, 0L, n10, 0L);
            }
        }
        return this.jjMoveNfa_0(0, 15);
    }
    
    private final int jjMoveStringLiteralDfa16_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8) {
        if (((n2 &= n) | (n4 &= n3) | (n6 &= n5) | (n8 &= n7)) == 0x0L) {
            return this.jjMoveNfa_0(0, 15);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 15);
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa17_0(n2, 0L, n4, 68719476736L, n6, 0L, n8, 0L);
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa17_0(n2, 0L, n4, 0L, n6, 16384L, n8, 0L);
            }
            case 'P': {
                if ((n2 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 111;
                    this.jjmatchedPos = 16;
                    break;
                }
                break;
            }
            case 'T': {
                return this.jjMoveStringLiteralDfa17_0(n2, 0L, n4, 0L, n6, 4398046511104L, n8, 16L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa17_0(n2, 0L, n4, 68719476736L, n6, 0L, n8, 0L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa17_0(n2, 0L, n4, 0L, n6, 16384L, n8, 0L);
            }
            case 'p': {
                if ((n2 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 111;
                    this.jjmatchedPos = 16;
                    break;
                }
                break;
            }
            case 't': {
                return this.jjMoveStringLiteralDfa17_0(n2, 0L, n4, 0L, n6, 4398046511104L, n8, 16L);
            }
        }
        return this.jjMoveNfa_0(0, 16);
    }
    
    private final int jjMoveStringLiteralDfa17_0(final long n, long n2, final long n3, long n4, final long n5, long n6, final long n7, long n8) {
        n2 &= n;
        if ((n2 | (n4 &= n3) | (n6 &= n5) | (n8 &= n7)) == 0x0L) {
            return this.jjMoveNfa_0(0, 16);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 16);
        }
        switch (this.curChar) {
            case 'I': {
                return this.jjMoveStringLiteralDfa18_0(n4, 0L, n6, 4398046511104L, n8, 0L);
            }
            case 'L': {
                if ((n4 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 292;
                    this.jjmatchedPos = 17;
                    break;
                }
                break;
            }
            case 'N': {
                return this.jjMoveStringLiteralDfa18_0(n4, 0L, n6, 16384L, n8, 0L);
            }
            case '_': {
                return this.jjMoveStringLiteralDfa18_0(n4, 0L, n6, 0L, n8, 16L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa18_0(n4, 0L, n6, 4398046511104L, n8, 0L);
            }
            case 'l': {
                if ((n4 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 292;
                    this.jjmatchedPos = 17;
                    break;
                }
                break;
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa18_0(n4, 0L, n6, 16384L, n8, 0L);
            }
        }
        return this.jjMoveNfa_0(0, 17);
    }
    
    private final int jjMoveStringLiteralDfa18_0(final long n, long n2, final long n3, long n4, final long n5, long n6) {
        n2 &= n;
        if ((n2 | (n4 &= n3) | (n6 &= n5)) == 0x0L) {
            return this.jjMoveNfa_0(0, 17);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 17);
        }
        switch (this.curChar) {
            case 'D': {
                if ((n4 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 334;
                    this.jjmatchedPos = 18;
                    break;
                }
                break;
            }
            case 'O': {
                return this.jjMoveStringLiteralDfa19_0(n4, 4398046511104L, n6, 0L);
            }
            case 'S': {
                return this.jjMoveStringLiteralDfa19_0(n4, 0L, n6, 16L);
            }
            case 'd': {
                if ((n4 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 334;
                    this.jjmatchedPos = 18;
                    break;
                }
                break;
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa19_0(n4, 4398046511104L, n6, 0L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa19_0(n4, 0L, n6, 16L);
            }
        }
        return this.jjMoveNfa_0(0, 18);
    }
    
    private final int jjMoveStringLiteralDfa19_0(final long n, long n2, final long n3, long n4) {
        if (((n2 &= n) | (n4 &= n3)) == 0x0L) {
            return this.jjMoveNfa_0(0, 18);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 18);
        }
        switch (this.curChar) {
            case 'E': {
                return this.jjMoveStringLiteralDfa20_0(n2, 0L, n4, 16L);
            }
            case 'N': {
                if ((n2 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 362;
                    this.jjmatchedPos = 19;
                    break;
                }
                break;
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa20_0(n2, 0L, n4, 16L);
            }
            case 'n': {
                if ((n2 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 362;
                    this.jjmatchedPos = 19;
                    break;
                }
                break;
            }
        }
        return this.jjMoveNfa_0(0, 19);
    }
    
    private final int jjMoveStringLiteralDfa20_0(final long n, long n2, final long n3, long n4) {
        n2 &= n;
        if ((n2 | (n4 &= n3)) == 0x0L) {
            return this.jjMoveNfa_0(0, 19);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return this.jjMoveNfa_0(0, 19);
        }
        switch (this.curChar) {
            case 'T': {
                if ((n4 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 388;
                    this.jjmatchedPos = 20;
                    break;
                }
                break;
            }
            case 't': {
                if ((n4 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 388;
                    this.jjmatchedPos = 20;
                    break;
                }
                break;
            }
        }
        return this.jjMoveNfa_0(0, 20);
    }
    
    private final int jjMoveNfa_0(final int n, int a) {
        final int jjmatchedKind = this.jjmatchedKind;
        final int jjmatchedPos = this.jjmatchedPos;
        final int n2;
        this.input_stream.backup(n2 = a + 1);
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            throw new Error("Internal Error");
        }
        a = 0;
        int n3 = 0;
        this.jjnewStateCnt = 137;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind2 = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n4 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                if (jjmatchedKind2 > 482) {
                                    jjmatchedKind2 = 482;
                                }
                                this.jjCheckNAddStates(3, 10);
                                continue;
                            }
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(135, 136);
                                continue;
                            }
                            if (this.curChar == '\'') {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(4, 5);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0x3FF000000000000L & n4) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind2 > 475) {
                                jjmatchedKind2 = 475;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            continue;
                        }
                        case 2: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(4, 5);
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if (this.curChar == '\"') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if ((0xFFFFFFFBFFFFFFFFL & n4) != 0x0L) {
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if (this.curChar == '\"' && jjmatchedKind2 > 481) {
                                jjmatchedKind2 = 481;
                                continue;
                            }
                            continue;
                        }
                        case 7:
                        case 8: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            if (this.curChar == '\'') {
                                this.jjstateSet[this.jjnewStateCnt++] = 8;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if ((0xFFFFFF7FFFFFFFFFL & n4) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if (this.curChar == '\'' && jjmatchedKind2 > 485) {
                                jjmatchedKind2 = 485;
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(14, 15);
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddTwoStates(14, 15);
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if (this.curChar == '\'' && jjmatchedKind2 > 486) {
                                jjmatchedKind2 = 486;
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(18, 19);
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if ((0x280000000000L & n4) != 0x0L) {
                                this.jjCheckNAdd(19);
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if (this.curChar == '\'') {
                                this.jjstateSet[this.jjnewStateCnt++] = 20;
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(17, 29);
                                continue;
                            }
                            continue;
                        }
                        case 21: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddTwoStates(21, 22);
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if (this.curChar == '-') {
                                this.jjCheckNAdd(23);
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddTwoStates(23, 24);
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if (this.curChar == '\'') {
                                this.jjAddStates(30, 33);
                                continue;
                            }
                            continue;
                        }
                        case 27: {
                            if (this.curChar == '(') {
                                this.jjCheckNAdd(28);
                                continue;
                            }
                            continue;
                        }
                        case 28: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddTwoStates(28, 29);
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if (this.curChar == ')' && jjmatchedKind2 > 488) {
                                jjmatchedKind2 = 488;
                                continue;
                            }
                            continue;
                        }
                        case 33: {
                            if (this.curChar == '(') {
                                this.jjCheckNAdd(34);
                                continue;
                            }
                            continue;
                        }
                        case 34: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddTwoStates(34, 35);
                                continue;
                            }
                            continue;
                        }
                        case 35: {
                            if (this.curChar == ')') {
                                this.jjstateSet[this.jjnewStateCnt++] = 57;
                                continue;
                            }
                            continue;
                        }
                        case 89: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(34, 36);
                                continue;
                            }
                            continue;
                        }
                        case 90: {
                            if (this.curChar == ' ') {
                                this.jjCheckNAdd(91);
                                continue;
                            }
                            continue;
                        }
                        case 91: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(37, 39);
                                continue;
                            }
                            continue;
                        }
                        case 92: {
                            if (this.curChar == ':') {
                                this.jjCheckNAdd(93);
                                continue;
                            }
                            continue;
                        }
                        case 93: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(40, 42);
                                continue;
                            }
                            continue;
                        }
                        case 94: {
                            if (this.curChar == ':') {
                                this.jjCheckNAdd(95);
                                continue;
                            }
                            continue;
                        }
                        case 95: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(43, 45);
                                continue;
                            }
                            continue;
                        }
                        case 96: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(97, 24);
                                continue;
                            }
                            continue;
                        }
                        case 97: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddTwoStates(97, 24);
                                continue;
                            }
                            continue;
                        }
                        case 98: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(46, 48);
                                continue;
                            }
                            continue;
                        }
                        case 99: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(100, 24);
                                continue;
                            }
                            continue;
                        }
                        case 100: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddTwoStates(100, 24);
                                continue;
                            }
                            continue;
                        }
                        case 101: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(49, 51);
                                continue;
                            }
                            continue;
                        }
                        case 102: {
                            if (this.curChar == ':') {
                                this.jjCheckNAdd(103);
                                continue;
                            }
                            continue;
                        }
                        case 103: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(52, 54);
                                continue;
                            }
                            continue;
                        }
                        case 104: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(105, 24);
                                continue;
                            }
                            continue;
                        }
                        case 105: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddTwoStates(105, 24);
                                continue;
                            }
                            continue;
                        }
                        case 106: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(55, 57);
                                continue;
                            }
                            continue;
                        }
                        case 107: {
                            if (this.curChar == ':') {
                                this.jjCheckNAdd(108);
                                continue;
                            }
                            continue;
                        }
                        case 108: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(58, 60);
                                continue;
                            }
                            continue;
                        }
                        case 109: {
                            if (this.curChar == ':') {
                                this.jjCheckNAdd(110);
                                continue;
                            }
                            continue;
                        }
                        case 110: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(61, 63);
                                continue;
                            }
                            continue;
                        }
                        case 111: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(112, 24);
                                continue;
                            }
                            continue;
                        }
                        case 112: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddTwoStates(112, 24);
                                continue;
                            }
                            continue;
                        }
                        case 113: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(64, 66);
                                continue;
                            }
                            continue;
                        }
                        case 121: {
                            if ((0x3FF000000000000L & n4) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind2 > 482) {
                                jjmatchedKind2 = 482;
                            }
                            this.jjCheckNAddStates(3, 10);
                            continue;
                        }
                        case 122: {
                            if ((0x3FF000000000000L & n4) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind2 > 482) {
                                jjmatchedKind2 = 482;
                            }
                            this.jjCheckNAddTwoStates(122, 123);
                            continue;
                        }
                        case 123: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (jjmatchedKind2 > 482) {
                                jjmatchedKind2 = 482;
                            }
                            this.jjCheckNAdd(124);
                            continue;
                        }
                        case 124: {
                            if ((0x3FF000000000000L & n4) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind2 > 482) {
                                jjmatchedKind2 = 482;
                            }
                            this.jjCheckNAdd(124);
                            continue;
                        }
                        case 125: {
                            if ((0x3FF000000000000L & n4) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind2 > 483) {
                                jjmatchedKind2 = 483;
                            }
                            this.jjCheckNAdd(125);
                            continue;
                        }
                        case 126: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddTwoStates(126, 127);
                                continue;
                            }
                            continue;
                        }
                        case 128: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(67, 69);
                                continue;
                            }
                            continue;
                        }
                        case 129: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(130, 131);
                                continue;
                            }
                            continue;
                        }
                        case 130: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddTwoStates(130, 131);
                                continue;
                            }
                            continue;
                        }
                        case 132: {
                            if ((0x280000000000L & n4) != 0x0L) {
                                this.jjCheckNAdd(133);
                                continue;
                            }
                            continue;
                        }
                        case 133: {
                            if ((0x3FF000000000000L & n4) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind2 > 487) {
                                jjmatchedKind2 = 487;
                            }
                            this.jjCheckNAdd(133);
                            continue;
                        }
                        case 134: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(135, 136);
                                continue;
                            }
                            continue;
                        }
                        case 135: {
                            if ((0x3FF000000000000L & n4) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind2 > 482) {
                                jjmatchedKind2 = 482;
                            }
                            this.jjCheckNAdd(135);
                            continue;
                        }
                        case 136: {
                            if ((0x3FF000000000000L & n4) != 0x0L) {
                                this.jjCheckNAddTwoStates(136, 131);
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n3);
            }
            else if (this.curChar < '\u0080') {
                final long n5 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x7FFFFFE07FFFFFEL & n5) != 0x0L) {
                                if (jjmatchedKind2 > 475) {
                                    jjmatchedKind2 = 475;
                                }
                                this.jjCheckNAdd(1);
                            }
                            if ((0x100000001000000L & n5) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 13;
                                continue;
                            }
                            if (this.curChar == 'I') {
                                this.jjstateSet[this.jjnewStateCnt++] = 119;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0x7FFFFFE87FFFFFEL & n5) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind2 > 475) {
                                jjmatchedKind2 = 475;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 12: {
                            if ((0x100000001000000L & n5) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 13;
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if ((0x7E0000007EL & n5) != 0x0L) {
                                this.jjAddStates(70, 71);
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if (this.curChar == 'L') {
                                this.jjstateSet[this.jjnewStateCnt++] = 17;
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if (this.curChar == 'y') {
                                this.jjAddStates(72, 73);
                                continue;
                            }
                            continue;
                        }
                        case 26: {
                            if (this.curChar != 'r') {
                                continue;
                            }
                            if (jjmatchedKind2 > 488) {
                                jjmatchedKind2 = 488;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 30: {
                            if (this.curChar == 'a') {
                                this.jjCheckNAdd(26);
                                continue;
                            }
                            continue;
                        }
                        case 31: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 30;
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if (this.curChar == 'r') {
                                this.jjCheckNAddTwoStates(33, 57);
                                continue;
                            }
                            continue;
                        }
                        case 36: {
                            if (this.curChar == 'o') {
                                this.jjAddStates(74, 77);
                                continue;
                            }
                            continue;
                        }
                        case 37: {
                            if (this.curChar == 'r' && jjmatchedKind2 > 488) {
                                jjmatchedKind2 = 488;
                                continue;
                            }
                            continue;
                        }
                        case 38: {
                            if (this.curChar == 'a') {
                                this.jjCheckNAdd(37);
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 38;
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if (this.curChar == 'y') {
                                this.jjstateSet[this.jjnewStateCnt++] = 39;
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if (this.curChar == 'y' && jjmatchedKind2 > 488) {
                                jjmatchedKind2 = 488;
                                continue;
                            }
                            continue;
                        }
                        case 42: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 41;
                                continue;
                            }
                            continue;
                        }
                        case 43: {
                            if (this.curChar == 'd') {
                                this.jjstateSet[this.jjnewStateCnt++] = 42;
                                continue;
                            }
                            continue;
                        }
                        case 44: {
                            if (this.curChar == 'u') {
                                this.jjCheckNAdd(37);
                                continue;
                            }
                            continue;
                        }
                        case 45: {
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 44;
                                continue;
                            }
                            continue;
                        }
                        case 46: {
                            if (this.curChar == 'h') {
                                this.jjstateSet[this.jjnewStateCnt++] = 45;
                                continue;
                            }
                            continue;
                        }
                        case 47: {
                            if (this.curChar == 'm') {
                                this.jjAddStates(78, 79);
                                continue;
                            }
                            continue;
                        }
                        case 48: {
                            if (this.curChar == 'h' && jjmatchedKind2 > 488) {
                                jjmatchedKind2 = 488;
                                continue;
                            }
                            continue;
                        }
                        case 49: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 48;
                                continue;
                            }
                            continue;
                        }
                        case 50: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 49;
                                continue;
                            }
                            continue;
                        }
                        case 51: {
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 50;
                                continue;
                            }
                            continue;
                        }
                        case 52: {
                            if (this.curChar == 'e' && jjmatchedKind2 > 488) {
                                jjmatchedKind2 = 488;
                                continue;
                            }
                            continue;
                        }
                        case 53: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 52;
                                continue;
                            }
                            continue;
                        }
                        case 54: {
                            if (this.curChar == 'u') {
                                this.jjstateSet[this.jjnewStateCnt++] = 53;
                                continue;
                            }
                            continue;
                        }
                        case 55: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 54;
                                continue;
                            }
                            continue;
                        }
                        case 56: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 55;
                                continue;
                            }
                            continue;
                        }
                        case 57: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 36;
                                continue;
                            }
                            continue;
                        }
                        case 58: {
                            if (this.curChar == 'a') {
                                this.jjCheckNAdd(32);
                                continue;
                            }
                            continue;
                        }
                        case 59: {
                            if (this.curChar == 'e') {
                                this.jjstateSet[this.jjnewStateCnt++] = 58;
                                continue;
                            }
                            continue;
                        }
                        case 60: {
                            if (this.curChar == 'd') {
                                this.jjAddStates(80, 81);
                                continue;
                            }
                            continue;
                        }
                        case 61: {
                            if (this.curChar != 'y') {
                                continue;
                            }
                            if (jjmatchedKind2 > 488) {
                                jjmatchedKind2 = 488;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 62: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 61;
                                continue;
                            }
                            continue;
                        }
                        case 63: {
                            if (this.curChar == 'y') {
                                this.jjCheckNAddTwoStates(33, 57);
                                continue;
                            }
                            continue;
                        }
                        case 64: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 63;
                                continue;
                            }
                            continue;
                        }
                        case 65: {
                            if (this.curChar == 'h') {
                                this.jjAddStates(82, 83);
                                continue;
                            }
                            continue;
                        }
                        case 66: {
                            if (this.curChar == 'u') {
                                this.jjCheckNAdd(26);
                                continue;
                            }
                            continue;
                        }
                        case 67: {
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 66;
                                continue;
                            }
                            continue;
                        }
                        case 68: {
                            if (this.curChar == 'u') {
                                this.jjCheckNAdd(32);
                                continue;
                            }
                            continue;
                        }
                        case 69: {
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 68;
                                continue;
                            }
                            continue;
                        }
                        case 70: {
                            if (this.curChar == 'm') {
                                this.jjAddStates(84, 87);
                                continue;
                            }
                            continue;
                        }
                        case 71: {
                            if (this.curChar != 'h') {
                                continue;
                            }
                            if (jjmatchedKind2 > 488) {
                                jjmatchedKind2 = 488;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 72: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 71;
                                continue;
                            }
                            continue;
                        }
                        case 73: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 72;
                                continue;
                            }
                            continue;
                        }
                        case 74: {
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 73;
                                continue;
                            }
                            continue;
                        }
                        case 75: {
                            if (this.curChar != 'e') {
                                continue;
                            }
                            if (jjmatchedKind2 > 488) {
                                jjmatchedKind2 = 488;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 76: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 75;
                                continue;
                            }
                            continue;
                        }
                        case 77: {
                            if (this.curChar == 'u') {
                                this.jjstateSet[this.jjnewStateCnt++] = 76;
                                continue;
                            }
                            continue;
                        }
                        case 78: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 77;
                                continue;
                            }
                            continue;
                        }
                        case 79: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 78;
                                continue;
                            }
                            continue;
                        }
                        case 80: {
                            if (this.curChar == 'h') {
                                this.jjCheckNAddTwoStates(33, 57);
                                continue;
                            }
                            continue;
                        }
                        case 81: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 80;
                                continue;
                            }
                            continue;
                        }
                        case 82: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 81;
                                continue;
                            }
                            continue;
                        }
                        case 83: {
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 82;
                                continue;
                            }
                            continue;
                        }
                        case 84: {
                            if (this.curChar == 'e') {
                                this.jjCheckNAddTwoStates(33, 57);
                                continue;
                            }
                            continue;
                        }
                        case 85: {
                            if (this.curChar == 't') {
                                this.jjstateSet[this.jjnewStateCnt++] = 84;
                                continue;
                            }
                            continue;
                        }
                        case 86: {
                            if (this.curChar == 'u') {
                                this.jjstateSet[this.jjnewStateCnt++] = 85;
                                continue;
                            }
                            continue;
                        }
                        case 87: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 86;
                                continue;
                            }
                            continue;
                        }
                        case 88: {
                            if (this.curChar == 'i') {
                                this.jjstateSet[this.jjnewStateCnt++] = 87;
                                continue;
                            }
                            continue;
                        }
                        case 114: {
                            if (this.curChar == 'A') {
                                this.jjstateSet[this.jjnewStateCnt++] = 16;
                                continue;
                            }
                            continue;
                        }
                        case 115: {
                            if (this.curChar == 'V') {
                                this.jjstateSet[this.jjnewStateCnt++] = 114;
                                continue;
                            }
                            continue;
                        }
                        case 116: {
                            if (this.curChar == 'R') {
                                this.jjstateSet[this.jjnewStateCnt++] = 115;
                                continue;
                            }
                            continue;
                        }
                        case 117: {
                            if (this.curChar == 'E') {
                                this.jjstateSet[this.jjnewStateCnt++] = 116;
                                continue;
                            }
                            continue;
                        }
                        case 118: {
                            if (this.curChar == 'T') {
                                this.jjstateSet[this.jjnewStateCnt++] = 117;
                                continue;
                            }
                            continue;
                        }
                        case 119: {
                            if (this.curChar == 'N') {
                                this.jjstateSet[this.jjnewStateCnt++] = 118;
                                continue;
                            }
                            continue;
                        }
                        case 120: {
                            if (this.curChar == 'I') {
                                this.jjstateSet[this.jjnewStateCnt++] = 119;
                                continue;
                            }
                            continue;
                        }
                        case 127: {
                            if ((0x288000002880L & n5) != 0x0L && jjmatchedKind2 > 484) {
                                jjmatchedKind2 = 484;
                                continue;
                            }
                            continue;
                        }
                        case 131: {
                            if ((0x2000000020L & n5) != 0x0L) {
                                this.jjAddStates(88, 89);
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 5: {
                            this.jjAddStates(14, 16);
                            continue;
                        }
                        case 10: {
                            this.jjAddStates(11, 13);
                            continue;
                        }
                    }
                } while (i != n3);
            }
            else {
                final int n6 = this.curChar >> 8;
                final int n7 = n6 >> 6;
                final long n8 = 1L << (n6 & 0x3F);
                final int n9 = (this.curChar & '\u00ff') >> 6;
                final long n10 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!jjCanMove_1(n6, n7, n9, n8, n10)) {
                                continue;
                            }
                            if (jjmatchedKind2 > 475) {
                                jjmatchedKind2 = 475;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 1: {
                            if (!jjCanMove_2(n6, n7, n9, n8, n10)) {
                                continue;
                            }
                            if (jjmatchedKind2 > 475) {
                                jjmatchedKind2 = 475;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 5: {
                            if (jjCanMove_0(n6, n7, n9, n8, n10)) {
                                this.jjAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if (jjCanMove_0(n6, n7, n9, n8, n10)) {
                                this.jjAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n3);
            }
            if (jjmatchedKind2 != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind2;
                this.jjmatchedPos = a;
                jjmatchedKind2 = Integer.MAX_VALUE;
            }
            ++a;
            final int n11 = i = this.jjnewStateCnt;
            final int n12 = 137;
            final int jjnewStateCnt = n3;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n11 == (n3 = n12 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
                continue;
            }
            catch (IOException ex2) {}
            break;
        }
        if (this.jjmatchedPos > jjmatchedPos) {
            return a;
        }
        final int max = Math.max(a, n2);
        if (a < max) {
            int n13 = max - Math.min(a, n2);
            while (n13-- > 0) {
                try {
                    this.curChar = this.input_stream.readChar();
                    continue;
                }
                catch (IOException ex3) {
                    throw new Error("Internal Error : Please send a bug report.");
                }
                break;
            }
        }
        if (this.jjmatchedPos < jjmatchedPos) {
            this.jjmatchedKind = jjmatchedKind;
            this.jjmatchedPos = jjmatchedPos;
        }
        else if (this.jjmatchedPos == jjmatchedPos && this.jjmatchedKind > jjmatchedKind) {
            this.jjmatchedKind = jjmatchedKind;
        }
        return max;
    }
    
    private final int jjStopStringLiteralDfa_18(final int n, final long n2) {
        switch (n) {
            case 0: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 62;
                    this.jjmatchedPos = 0;
                    return -1;
                }
                return -1;
            }
            case 1: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 2: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 3: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 4: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 5: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 6: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 7: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 8: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 9: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 10: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 11: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 12: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 13: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 14: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 15: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 16: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            case 17: {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    if (this.jjmatchedPos == 0) {
                        this.jjmatchedKind = 62;
                        this.jjmatchedPos = 0;
                    }
                    return -1;
                }
                return -1;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int jjStartNfa_18(final int n, final long n2) {
        return this.jjMoveNfa_18(this.jjStopStringLiteralDfa_18(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_18(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_18(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_18() {
        switch (this.curChar) {
            case 'D': {
                return this.jjMoveStringLiteralDfa1_18(576460752303423488L);
            }
            case 'S': {
                return this.jjStopAtPos(0, 60);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa1_18(576460752303423488L);
            }
            case 's': {
                return this.jjStopAtPos(0, 60);
            }
            default: {
                return this.jjMoveNfa_18(0, 0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa1_18(final long n) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(0, n);
            return 1;
        }
        switch (this.curChar) {
            case 'E': {
                return this.jjMoveStringLiteralDfa2_18(n, 576460752303423488L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa2_18(n, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(0, n);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa2_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(0, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(1, n2);
            return 2;
        }
        switch (this.curChar) {
            case 'R': {
                return this.jjMoveStringLiteralDfa3_18(n2, 576460752303423488L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa3_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(1, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa3_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(1, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(2, n2);
            return 3;
        }
        switch (this.curChar) {
            case 'B': {
                return this.jjMoveStringLiteralDfa4_18(n2, 576460752303423488L);
            }
            case 'b': {
                return this.jjMoveStringLiteralDfa4_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(2, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa4_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(2, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(3, n2);
            return 4;
        }
        switch (this.curChar) {
            case 'Y': {
                return this.jjMoveStringLiteralDfa5_18(n2, 576460752303423488L);
            }
            case 'y': {
                return this.jjMoveStringLiteralDfa5_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(3, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa5_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(3, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(4, n2);
            return 5;
        }
        switch (this.curChar) {
            case 'D': {
                return this.jjMoveStringLiteralDfa6_18(n2, 576460752303423488L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa6_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(4, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa6_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(4, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(5, n2);
            return 6;
        }
        switch (this.curChar) {
            case 'A': {
                return this.jjMoveStringLiteralDfa7_18(n2, 576460752303423488L);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa7_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(5, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa7_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(5, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(6, n2);
            return 7;
        }
        switch (this.curChar) {
            case 'S': {
                return this.jjMoveStringLiteralDfa8_18(n2, 576460752303423488L);
            }
            case 's': {
                return this.jjMoveStringLiteralDfa8_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(6, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa8_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(6, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(7, n2);
            return 8;
        }
        switch (this.curChar) {
            case 'H': {
                return this.jjMoveStringLiteralDfa9_18(n2, 576460752303423488L);
            }
            case 'h': {
                return this.jjMoveStringLiteralDfa9_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(7, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa9_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(7, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(8, n2);
            return 9;
        }
        switch (this.curChar) {
            case 'P': {
                return this.jjMoveStringLiteralDfa10_18(n2, 576460752303423488L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa10_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(8, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa10_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(8, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(9, n2);
            return 10;
        }
        switch (this.curChar) {
            case 'R': {
                return this.jjMoveStringLiteralDfa11_18(n2, 576460752303423488L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa11_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(9, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa11_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(9, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(10, n2);
            return 11;
        }
        switch (this.curChar) {
            case 'O': {
                return this.jjMoveStringLiteralDfa12_18(n2, 576460752303423488L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa12_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(10, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa12_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(10, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(11, n2);
            return 12;
        }
        switch (this.curChar) {
            case 'P': {
                return this.jjMoveStringLiteralDfa13_18(n2, 576460752303423488L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa13_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(11, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa13_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(11, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(12, n2);
            return 13;
        }
        switch (this.curChar) {
            case 'E': {
                return this.jjMoveStringLiteralDfa14_18(n2, 576460752303423488L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa14_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(12, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa14_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(12, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(13, n2);
            return 14;
        }
        switch (this.curChar) {
            case 'R': {
                return this.jjMoveStringLiteralDfa15_18(n2, 576460752303423488L);
            }
            case 'r': {
                return this.jjMoveStringLiteralDfa15_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(13, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa15_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(13, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(14, n2);
            return 15;
        }
        switch (this.curChar) {
            case 'T': {
                return this.jjMoveStringLiteralDfa16_18(n2, 576460752303423488L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa16_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(14, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa16_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(14, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(15, n2);
            return 16;
        }
        switch (this.curChar) {
            case 'I': {
                return this.jjMoveStringLiteralDfa17_18(n2, 576460752303423488L);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa17_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(15, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa17_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(15, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(16, n2);
            return 17;
        }
        switch (this.curChar) {
            case 'E': {
                return this.jjMoveStringLiteralDfa18_18(n2, 576460752303423488L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa18_18(n2, 576460752303423488L);
            }
            default: {
                return this.jjStartNfa_18(16, n2);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa18_18(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_18(16, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            this.jjStopStringLiteralDfa_18(17, n2);
            return 18;
        }
        switch (this.curChar) {
            case 'S': {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    return this.jjStopAtPos(18, 59);
                }
                break;
            }
            case 's': {
                if ((n2 & 0x800000000000000L) != 0x0L) {
                    return this.jjStopAtPos(18, 59);
                }
                break;
            }
        }
        return this.jjStartNfa_18(17, n2);
    }
    
    private final int jjMoveNfa_18(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 62) {
                                jjmatchedKind = 62;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 61) {
                                jjmatchedKind = 61;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 61) {
                                jjmatchedKind = 61;
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
                        case 3: {
                            if (jjmatchedKind > 62) {
                                jjmatchedKind = 62;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFF7FFFFFFF7FFFFL & n4) != 0x0L) {
                                jjmatchedKind = 62;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 62) {
                                jjmatchedKind = 62;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjStopStringLiteralDfa_14(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_14(final int n, final long n2) {
        return this.jjMoveNfa_14(this.jjStopStringLiteralDfa_14(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_14(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_14(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_14() {
        switch (this.curChar) {
            case 'R': {
                return this.jjStopAtPos(0, 47);
            }
            case 'r': {
                return this.jjStopAtPos(0, 47);
            }
            default: {
                return this.jjMoveNfa_14(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_14(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 49) {
                                jjmatchedKind = 49;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 48) {
                                jjmatchedKind = 48;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 48) {
                                jjmatchedKind = 48;
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
                        case 3: {
                            if (jjmatchedKind > 49) {
                                jjmatchedKind = 49;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFBFFFFFFFBFFFFL & n4) != 0x0L) {
                                jjmatchedKind = 49;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 49) {
                                jjmatchedKind = 49;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private final int jjStopStringLiteralDfa_9(final int n, final long n2) {
        return -1;
    }
    
    private final int jjStartNfa_9(final int n, final long n2) {
        return this.jjMoveNfa_9(this.jjStopStringLiteralDfa_9(n, n2), n + 1);
    }
    
    private final int jjStartNfaWithStates_9(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_9(n, jjmatchedPos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_9() {
        switch (this.curChar) {
            case 'P': {
                return this.jjStopAtPos(0, 32);
            }
            case 'p': {
                return this.jjStopAtPos(0, 32);
            }
            default: {
                return this.jjMoveNfa_9(0, 0);
            }
        }
    }
    
    private final int jjMoveNfa_9(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 4;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjmatchedKind > 34) {
                                jjmatchedKind = 34;
                            }
                            if ((0x2400L & n3) != 0x0L && jjmatchedKind > 33) {
                                jjmatchedKind = 33;
                            }
                            if (this.curChar == '\r') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '\n' && jjmatchedKind > 33) {
                                jjmatchedKind = 33;
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
                        case 3: {
                            if (jjmatchedKind > 34) {
                                jjmatchedKind = 34;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFEFFFFFFFEFFFFL & n4) != 0x0L) {
                                jjmatchedKind = 34;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = this.curChar >> 8;
                final int n6 = n5 >> 6;
                final long n7 = 1L << (n5 & 0x3F);
                final int n8 = (this.curChar & '\u00ff') >> 6;
                final long n9 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (jjCanMove_0(n5, n6, n8, n7, n9) && jjmatchedKind > 34) {
                                jjmatchedKind = 34;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n10 = i = this.jjnewStateCnt;
            final int n11 = 4;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n10 == (n2 = n11 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    private static final boolean jjCanMove_0(final int n, final int n2, final int n3, final long n4, final long n5) {
        switch (n) {
            case 0: {
                return (SQLParserTokenManager.jjbitVec2[n3] & n5) != 0x0L;
            }
            default: {
                return (SQLParserTokenManager.jjbitVec0[n2] & n4) != 0x0L;
            }
        }
    }
    
    private static final boolean jjCanMove_1(final int n, final int n2, final int n3, final long n4, final long n5) {
        switch (n) {
            case 0: {
                return (SQLParserTokenManager.jjbitVec4[n3] & n5) != 0x0L;
            }
            case 1: {
                return (SQLParserTokenManager.jjbitVec5[n3] & n5) != 0x0L;
            }
            case 2: {
                return (SQLParserTokenManager.jjbitVec6[n3] & n5) != 0x0L;
            }
            case 3: {
                return (SQLParserTokenManager.jjbitVec7[n3] & n5) != 0x0L;
            }
            case 4: {
                return (SQLParserTokenManager.jjbitVec8[n3] & n5) != 0x0L;
            }
            case 5: {
                return (SQLParserTokenManager.jjbitVec9[n3] & n5) != 0x0L;
            }
            case 6: {
                return (SQLParserTokenManager.jjbitVec10[n3] & n5) != 0x0L;
            }
            case 9: {
                return (SQLParserTokenManager.jjbitVec11[n3] & n5) != 0x0L;
            }
            case 10: {
                return (SQLParserTokenManager.jjbitVec12[n3] & n5) != 0x0L;
            }
            case 11: {
                return (SQLParserTokenManager.jjbitVec13[n3] & n5) != 0x0L;
            }
            case 12: {
                return (SQLParserTokenManager.jjbitVec14[n3] & n5) != 0x0L;
            }
            case 13: {
                return (SQLParserTokenManager.jjbitVec15[n3] & n5) != 0x0L;
            }
            case 14: {
                return (SQLParserTokenManager.jjbitVec16[n3] & n5) != 0x0L;
            }
            case 15: {
                return (SQLParserTokenManager.jjbitVec17[n3] & n5) != 0x0L;
            }
            case 16: {
                return (SQLParserTokenManager.jjbitVec18[n3] & n5) != 0x0L;
            }
            case 17: {
                return (SQLParserTokenManager.jjbitVec19[n3] & n5) != 0x0L;
            }
            case 30: {
                return (SQLParserTokenManager.jjbitVec20[n3] & n5) != 0x0L;
            }
            case 31: {
                return (SQLParserTokenManager.jjbitVec21[n3] & n5) != 0x0L;
            }
            case 32: {
                return (SQLParserTokenManager.jjbitVec22[n3] & n5) != 0x0L;
            }
            case 33: {
                return (SQLParserTokenManager.jjbitVec23[n3] & n5) != 0x0L;
            }
            case 48: {
                return (SQLParserTokenManager.jjbitVec24[n3] & n5) != 0x0L;
            }
            case 49: {
                return (SQLParserTokenManager.jjbitVec25[n3] & n5) != 0x0L;
            }
            case 159: {
                return (SQLParserTokenManager.jjbitVec26[n3] & n5) != 0x0L;
            }
            case 215: {
                return (SQLParserTokenManager.jjbitVec27[n3] & n5) != 0x0L;
            }
            case 250: {
                return (SQLParserTokenManager.jjbitVec28[n3] & n5) != 0x0L;
            }
            case 251: {
                return (SQLParserTokenManager.jjbitVec29[n3] & n5) != 0x0L;
            }
            case 253: {
                return (SQLParserTokenManager.jjbitVec30[n3] & n5) != 0x0L;
            }
            case 254: {
                return (SQLParserTokenManager.jjbitVec31[n3] & n5) != 0x0L;
            }
            case 255: {
                return (SQLParserTokenManager.jjbitVec32[n3] & n5) != 0x0L;
            }
            default: {
                return (SQLParserTokenManager.jjbitVec3[n2] & n4) != 0x0L;
            }
        }
    }
    
    private static final boolean jjCanMove_2(final int n, final int n2, final int n3, final long n4, final long n5) {
        switch (n) {
            case 0: {
                return (SQLParserTokenManager.jjbitVec4[n3] & n5) != 0x0L;
            }
            case 1: {
                return (SQLParserTokenManager.jjbitVec5[n3] & n5) != 0x0L;
            }
            case 2: {
                return (SQLParserTokenManager.jjbitVec6[n3] & n5) != 0x0L;
            }
            case 3: {
                return (SQLParserTokenManager.jjbitVec7[n3] & n5) != 0x0L;
            }
            case 4: {
                return (SQLParserTokenManager.jjbitVec8[n3] & n5) != 0x0L;
            }
            case 5: {
                return (SQLParserTokenManager.jjbitVec9[n3] & n5) != 0x0L;
            }
            case 6: {
                return (SQLParserTokenManager.jjbitVec33[n3] & n5) != 0x0L;
            }
            case 9: {
                return (SQLParserTokenManager.jjbitVec34[n3] & n5) != 0x0L;
            }
            case 10: {
                return (SQLParserTokenManager.jjbitVec35[n3] & n5) != 0x0L;
            }
            case 11: {
                return (SQLParserTokenManager.jjbitVec36[n3] & n5) != 0x0L;
            }
            case 12: {
                return (SQLParserTokenManager.jjbitVec37[n3] & n5) != 0x0L;
            }
            case 13: {
                return (SQLParserTokenManager.jjbitVec38[n3] & n5) != 0x0L;
            }
            case 14: {
                return (SQLParserTokenManager.jjbitVec39[n3] & n5) != 0x0L;
            }
            case 15: {
                return (SQLParserTokenManager.jjbitVec40[n3] & n5) != 0x0L;
            }
            case 16: {
                return (SQLParserTokenManager.jjbitVec18[n3] & n5) != 0x0L;
            }
            case 17: {
                return (SQLParserTokenManager.jjbitVec19[n3] & n5) != 0x0L;
            }
            case 30: {
                return (SQLParserTokenManager.jjbitVec20[n3] & n5) != 0x0L;
            }
            case 31: {
                return (SQLParserTokenManager.jjbitVec21[n3] & n5) != 0x0L;
            }
            case 32: {
                return (SQLParserTokenManager.jjbitVec22[n3] & n5) != 0x0L;
            }
            case 33: {
                return (SQLParserTokenManager.jjbitVec23[n3] & n5) != 0x0L;
            }
            case 48: {
                return (SQLParserTokenManager.jjbitVec24[n3] & n5) != 0x0L;
            }
            case 49: {
                return (SQLParserTokenManager.jjbitVec25[n3] & n5) != 0x0L;
            }
            case 159: {
                return (SQLParserTokenManager.jjbitVec26[n3] & n5) != 0x0L;
            }
            case 215: {
                return (SQLParserTokenManager.jjbitVec27[n3] & n5) != 0x0L;
            }
            case 250: {
                return (SQLParserTokenManager.jjbitVec28[n3] & n5) != 0x0L;
            }
            case 251: {
                return (SQLParserTokenManager.jjbitVec29[n3] & n5) != 0x0L;
            }
            case 253: {
                return (SQLParserTokenManager.jjbitVec30[n3] & n5) != 0x0L;
            }
            case 254: {
                return (SQLParserTokenManager.jjbitVec31[n3] & n5) != 0x0L;
            }
            case 255: {
                return (SQLParserTokenManager.jjbitVec41[n3] & n5) != 0x0L;
            }
            default: {
                return (SQLParserTokenManager.jjbitVec3[n2] & n4) != 0x0L;
            }
        }
    }
    
    public SQLParserTokenManager(final CharStream input_stream) {
        this.commentNestingDepth = 0;
        this.debugStream = System.out;
        this.jjrounds = new int[137];
        this.jjstateSet = new int[274];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = input_stream;
    }
    
    public SQLParserTokenManager(final CharStream charStream, final int n) {
        this(charStream);
        this.SwitchTo(n);
    }
    
    public void ReInit(final CharStream input_stream) {
        final int n = 0;
        this.jjnewStateCnt = n;
        this.jjmatchedPos = n;
        this.curLexState = this.defaultLexState;
        this.input_stream = input_stream;
        this.ReInitRounds();
    }
    
    private final void ReInitRounds() {
        this.jjround = -2147483647;
        int n = 137;
        while (n-- > 0) {
            this.jjrounds[n] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final CharStream charStream, final int n) {
        this.ReInit(charStream);
        this.SwitchTo(n);
    }
    
    public void SwitchTo(final int n) {
        if (n >= 21 || n < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + n + ". State unchanged.", 2);
        }
        this.curLexState = n;
    }
    
    protected Token jjFillToken() {
        final Token token = Token.newToken(this.jjmatchedKind);
        token.kind = this.jjmatchedKind;
        if (this.jjmatchedPos < 0) {
            if (this.image == null) {
                token.image = "";
            }
            else {
                token.image = this.image.toString();
            }
            final Token token2 = token;
            final Token token3 = token;
            final int beginLine = this.input_stream.getBeginLine();
            token3.endLine = beginLine;
            token2.beginLine = beginLine;
            final Token token4 = token;
            final Token token5 = token;
            final int beginColumn = this.input_stream.getBeginColumn();
            token5.endColumn = beginColumn;
            token4.beginColumn = beginColumn;
        }
        else {
            final String s = SQLParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
            token.image = ((s == null) ? this.input_stream.GetImage() : s);
            token.beginLine = this.input_stream.getBeginLine();
            token.beginColumn = this.input_stream.getBeginColumn();
            token.endLine = this.input_stream.getEndLine();
            token.endColumn = this.input_stream.getEndColumn();
        }
        return token;
    }
    
    public Token getNextToken() {
        int n = 0;
    Label_0885:
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (IOException ex) {
                this.jjmatchedKind = 0;
                final Token jjFillToken = this.jjFillToken();
                this.CommonTokenAction(jjFillToken);
                return jjFillToken;
            }
            this.image = null;
            this.jjimageLen = 0;
            while (true) {
                switch (this.curLexState) {
                    case 0: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        n = this.jjMoveStringLiteralDfa0_0();
                        break;
                    }
                    case 1: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        n = this.jjMoveStringLiteralDfa0_1();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 10) {
                            this.jjmatchedKind = 10;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        n = this.jjMoveStringLiteralDfa0_2();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 10) {
                            this.jjmatchedKind = 10;
                            break;
                        }
                        break;
                    }
                    case 3: {
                        this.jjmatchedKind = 15;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_3();
                        break;
                    }
                    case 4: {
                        this.jjmatchedKind = 18;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_4();
                        break;
                    }
                    case 5: {
                        this.jjmatchedKind = 21;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_5();
                        break;
                    }
                    case 6: {
                        this.jjmatchedKind = 24;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_6();
                        break;
                    }
                    case 7: {
                        this.jjmatchedKind = 27;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_7();
                        break;
                    }
                    case 8: {
                        this.jjmatchedKind = 30;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_8();
                        break;
                    }
                    case 9: {
                        this.jjmatchedKind = 33;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_9();
                        break;
                    }
                    case 10: {
                        this.jjmatchedKind = 36;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_10();
                        break;
                    }
                    case 11: {
                        this.jjmatchedKind = 39;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_11();
                        break;
                    }
                    case 12: {
                        this.jjmatchedKind = 42;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_12();
                        break;
                    }
                    case 13: {
                        this.jjmatchedKind = 45;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_13();
                        break;
                    }
                    case 14: {
                        this.jjmatchedKind = 48;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_14();
                        break;
                    }
                    case 15: {
                        this.jjmatchedKind = 51;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_15();
                        break;
                    }
                    case 16: {
                        this.jjmatchedKind = 54;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_16();
                        break;
                    }
                    case 17: {
                        this.jjmatchedKind = 57;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_17();
                        break;
                    }
                    case 18: {
                        this.jjmatchedKind = 61;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_18();
                        break;
                    }
                    case 19: {
                        this.jjmatchedKind = 63;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_19();
                        break;
                    }
                    case 20: {
                        this.jjmatchedKind = 64;
                        this.jjmatchedPos = -1;
                        n = this.jjMoveStringLiteralDfa0_20();
                        break;
                    }
                }
                if (this.jjmatchedKind == Integer.MAX_VALUE) {
                    break Label_0885;
                }
                if (this.jjmatchedPos + 1 < n) {
                    this.input_stream.backup(n - this.jjmatchedPos - 1);
                }
                if ((SQLParserTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                    final Token jjFillToken2 = this.jjFillToken();
                    this.TokenLexicalActions(jjFillToken2);
                    if (SQLParserTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = SQLParserTokenManager.jjnewLexState[this.jjmatchedKind];
                    }
                    this.CommonTokenAction(jjFillToken2);
                    return jjFillToken2;
                }
                if ((SQLParserTokenManager.jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0x0L) {
                    this.MoreLexicalActions();
                    if (SQLParserTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = SQLParserTokenManager.jjnewLexState[this.jjmatchedKind];
                    }
                    n = 0;
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    try {
                        this.curChar = this.input_stream.readChar();
                        continue;
                    }
                    catch (IOException ex2) {}
                    break Label_0885;
                }
                this.SkipLexicalActions(null);
                if (SQLParserTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = SQLParserTokenManager.jjnewLexState[this.jjmatchedKind];
                    break;
                }
                break;
            }
        }
        int endLine = this.input_stream.getEndLine();
        int endColumn = this.input_stream.getEndColumn();
        String s = null;
        boolean b = false;
        try {
            this.input_stream.readChar();
            this.input_stream.backup(1);
        }
        catch (IOException ex3) {
            b = true;
            s = ((n <= 1) ? "" : this.input_stream.GetImage());
            if (this.curChar == '\n' || this.curChar == '\r') {
                ++endLine;
                endColumn = 0;
            }
            else {
                ++endColumn;
            }
        }
        if (!b) {
            this.input_stream.backup(1);
            s = ((n <= 1) ? "" : this.input_stream.GetImage());
        }
        throw new TokenMgrError(b, this.curLexState, endLine, endColumn, s, this.curChar, 0);
    }
    
    void SkipLexicalActions(final Token token) {
        final int jjmatchedKind = this.jjmatchedKind;
    }
    
    void MoreLexicalActions() {
        final int jjimageLen = this.jjimageLen;
        final int lengthOfMatch = this.jjmatchedPos + 1;
        this.lengthOfMatch = lengthOfMatch;
        this.jjimageLen = jjimageLen + lengthOfMatch;
        switch (this.jjmatchedKind) {
            case 6: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                this.commentNestingDepth = 1;
                break;
            }
            case 8: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                ++this.commentNestingDepth;
                break;
            }
            case 9: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                this.image.append(this.input_stream.GetSuffix(this.jjimageLen));
                this.jjimageLen = 0;
                --this.commentNestingDepth;
                this.SwitchTo((this.commentNestingDepth == 0) ? 1 : 2);
                break;
            }
        }
    }
    
    void TokenLexicalActions(final Token token) {
        switch (this.jjmatchedKind) {
            case 60: {
                if (this.image == null) {
                    this.image = new StringBuffer();
                }
                final StringBuffer image = this.image;
                final CharStream input_stream = this.input_stream;
                final int jjimageLen = this.jjimageLen;
                final int lengthOfMatch = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch;
                image.append(input_stream.GetSuffix(jjimageLen + lengthOfMatch));
                token.kind = 59;
                break;
            }
        }
    }
    
    static {
        jjbitVec0 = new long[] { -2L, -1L, -1L, -1L };
        jjbitVec2 = new long[] { 0L, 0L, -1L, -1L };
        jjbitVec3 = new long[] { 0L, -16384L, -17590038560769L, 1297036692691091455L };
        jjbitVec4 = new long[] { 0L, 0L, 297241973452963840L, -36028797027352577L };
        jjbitVec5 = new long[] { -1L, -1L, -1L, -270215977642229761L };
        jjbitVec6 = new long[] { 16777215L, -65536L, -432624840181022721L, 133144182787L };
        jjbitVec7 = new long[] { 0L, 288230376151711744L, -17179879616L, 4503588160110591L };
        jjbitVec8 = new long[] { -8194L, -536936449L, -65533L, 234134404065073567L };
        jjbitVec9 = new long[] { -562949953421312L, -8547991553L, 255L, 1979120929931264L };
        jjbitVec10 = new long[] { 576460743713488896L, -562949953419265L, 9007199254740991999L, 412319973375L };
        jjbitVec11 = new long[] { 2594073385365405664L, 17163091968L, 271902628478820320L, 844440767823872L };
        jjbitVec12 = new long[] { 247132830528276448L, 7881300924956672L, 2589004636761075680L, 4294967296L };
        jjbitVec13 = new long[] { 2579997437506199520L, 15837691904L, 270153412153034720L, 0L };
        jjbitVec14 = new long[] { 283724577500946400L, 12884901888L, 283724577500946400L, 13958643712L };
        jjbitVec15 = new long[] { 288228177128316896L, 12884901888L, 0L, 0L };
        jjbitVec16 = new long[] { 3799912185593854L, 127L, 2309621682768192918L, 805306463L };
        jjbitVec17 = new long[] { 0L, 4398046510847L, 0L, 0L };
        jjbitVec18 = new long[] { 0L, 0L, -4294967296L, 36028797018898495L };
        jjbitVec19 = new long[] { -1L, -2080374785L, -1065151889409L, 288230376151711743L };
        jjbitVec20 = new long[] { -1L, -1L, -4026531841L, 288230376151711743L };
        jjbitVec21 = new long[] { -3233808385L, 4611686017001275199L, 6908521828386340863L, 2295745090394464220L };
        jjbitVec22 = new long[] { 0L, Long.MIN_VALUE, 0L, 0L };
        jjbitVec23 = new long[] { 142986334291623044L, 0L, 0L, 0L };
        jjbitVec24 = new long[] { 17451448556060704L, -2L, -6574571521L, 8646911284551352319L };
        jjbitVec25 = new long[] { -527765581332512L, -1L, 32767L, 0L };
        jjbitVec26 = new long[] { -1L, -1L, 274877906943L, 0L };
        jjbitVec27 = new long[] { -1L, -1L, 68719476735L, 0L };
        jjbitVec28 = new long[] { 70368744177663L, 0L, 0L, 0L };
        jjbitVec29 = new long[] { 6881498029467631743L, -37L, 1125899906842623L, -524288L };
        jjbitVec30 = new long[] { 4611686018427387903L, -65536L, -196609L, 1152640029630136575L };
        jjbitVec31 = new long[] { 0L, -11540474045136896L, -1L, 2305843009213693951L };
        jjbitVec32 = new long[] { 576460743713488896L, -274743689218L, Long.MAX_VALUE, 486341884L };
        jjbitVec33 = new long[] { 576460743713488896L, -558556201875457L, 9007199254740991999L, 287949313494974463L };
        jjbitVec34 = new long[] { 2594073385365405664L, 281217261895680L, 271902628478820320L, 1125640866627584L };
        jjbitVec35 = new long[] { 247132830528276448L, 8162501023760384L, 2589004636761075680L, 281204393771008L };
        jjbitVec36 = new long[] { 2579997437506199520L, 281215936495616L, 270153412153034720L, 280925220896768L };
        jjbitVec37 = new long[] { 283724577500946400L, 281212983705600L, 283724577500946400L, 281214057447424L };
        jjbitVec38 = new long[] { 288228177128316896L, 281212983705600L, 0L, 0L };
        jjbitVec39 = new long[] { 3799912185593854L, 67043455L, 2309621682768192918L, 872349791L };
        jjbitVec40 = new long[] { 4393751543808L, 4398046510847L, 0L, 0L };
        jjbitVec41 = new long[] { 576460743780532224L, -274743689218L, Long.MAX_VALUE, 486341884L };
        jjnextStates = new int[] { 0, 1, 3, 122, 123, 125, 126, 127, 128, 129, 131, 9, 10, 11, 4, 5, 6, 21, 22, 23, 89, 90, 98, 99, 101, 102, 106, 107, 24, 113, 25, 60, 65, 70, 89, 90, 24, 91, 92, 24, 93, 94, 24, 95, 96, 24, 98, 99, 24, 101, 102, 24, 103, 104, 24, 106, 107, 24, 108, 109, 24, 110, 111, 24, 23, 24, 113, 128, 129, 131, 14, 15, 31, 59, 40, 43, 46, 47, 51, 56, 62, 64, 67, 69, 74, 79, 83, 88, 132, 133 };
        jjstrLiteralImages = new String[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        lexStateNames = new String[] { "DEFAULT", "IN_BRACKETED_COMMENT", "IN_NESTED_BRACKETED_COMMENT", "IN_COMMENT", "LOOKFOR_DE", "LOOKFOR_DER", "LOOKFOR_DERB", "LOOKFOR_DERBY", "LOOKFOR_DERBYDASH", "LOOKFOR_DERBYDASHP", "LOOKFOR_DERBYDASHPR", "LOOKFOR_DERBYDASHPRO", "LOOKFOR_DERBYDASHPROP", "LOOKFOR_DERBYDASHPROPE", "LOOKFOR_DERBYDASHPROPER", "LOOKFOR_DERBYDASHPROPERT", "LOOKFOR_DERBYDASHPROPERTI", "LOOKFOR_DERBYDASHPROPERTIE", "LOOKFOR_DERBYDASHPROPERTIES", "IT_IS_NOT_DERBYPROPERTIES_COMMENT", "PROPERTIES_LIST" };
        jjnewLexState = new int[] { -1, -1, -1, -1, -1, 1, 2, 0, -1, -1, -1, 3, -1, -1, 4, 0, 19, 5, 0, 19, 6, 0, 19, 7, 0, 19, 8, 0, 19, 9, 0, 19, 10, 0, 19, 11, 0, 19, 12, 0, 19, 13, 0, 19, 14, 0, 19, 15, 0, 19, 16, 0, 19, 17, 0, 19, 18, 0, 19, -1, 20, 0, 19, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        jjtoToken = new long[] { 1729382256910270465L, -1L, -1L, -1L, -1L, -1L, -1L, 2192580804607L };
        jjtoSkip = new long[] { -2305843009213679458L, 0L, 0L, 0L, 0L, 0L, 0L, 0L };
        jjtoMore = new long[] { 576460752303408992L, 0L, 0L, 0L, 0L, 0L, 0L, 0L };
    }
}
