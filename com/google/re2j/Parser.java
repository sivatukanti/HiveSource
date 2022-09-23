// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

import java.util.ArrayList;

class Parser
{
    private static final String ERR_INTERNAL_ERROR = "regexp/syntax: internal error";
    private static final String ERR_INVALID_CHAR_CLASS = "invalid character class";
    private static final String ERR_INVALID_CHAR_RANGE = "invalid character class range";
    private static final String ERR_INVALID_ESCAPE = "invalid escape sequence";
    private static final String ERR_INVALID_NAMED_CAPTURE = "invalid named capture";
    private static final String ERR_INVALID_PERL_OP = "invalid or unsupported Perl syntax";
    private static final String ERR_INVALID_REPEAT_OP = "invalid nested repetition operator";
    private static final String ERR_INVALID_REPEAT_SIZE = "invalid repeat count";
    private static final String ERR_MISSING_BRACKET = "missing closing ]";
    private static final String ERR_MISSING_PAREN = "missing closing )";
    private static final String ERR_MISSING_REPEAT_ARGUMENT = "missing argument to repetition operator";
    private static final String ERR_TRAILING_BACKSLASH = "trailing backslash at end of expression";
    private final String wholeRegexp;
    private int flags;
    private final Stack stack;
    private Regexp free;
    private int numCap;
    private static final int[][] ANY_TABLE;
    
    Parser(final String wholeRegexp, final int flags) {
        this.stack = new Stack();
        this.numCap = 0;
        this.wholeRegexp = wholeRegexp;
        this.flags = flags;
    }
    
    private Regexp newRegexp(final Regexp.Op op) {
        Regexp re = this.free;
        if (re != null && re.subs != null && re.subs.length > 0) {
            this.free = re.subs[0];
            re.reinit();
            re.op = op;
        }
        else {
            re = new Regexp(op);
        }
        return re;
    }
    
    private void reuse(final Regexp re) {
        if (re.subs != null && re.subs.length > 0) {
            re.subs[0] = this.free;
        }
        this.free = re;
    }
    
    private Regexp pop() {
        return this.stack.remove(this.stack.size() - 1);
    }
    
    private Regexp[] popToPseudo() {
        int i;
        int n;
        for (n = (i = this.stack.size()); i > 0 && !this.stack.get(i - 1).op.isPseudo(); --i) {}
        final Regexp[] r = this.stack.subList(i, n).toArray(new Regexp[n - i]);
        this.stack.removeRange(i, n);
        return r;
    }
    
    private Regexp push(final Regexp re) {
        if (re.op == Regexp.Op.CHAR_CLASS && re.runes.length == 2 && re.runes[0] == re.runes[1]) {
            if (this.maybeConcat(re.runes[0], this.flags & 0xFFFFFFFE)) {
                return null;
            }
            re.op = Regexp.Op.LITERAL;
            re.runes = new int[] { re.runes[0] };
            re.flags = (this.flags & 0xFFFFFFFE);
        }
        else if ((re.op == Regexp.Op.CHAR_CLASS && re.runes.length == 4 && re.runes[0] == re.runes[1] && re.runes[2] == re.runes[3] && Unicode.simpleFold(re.runes[0]) == re.runes[2] && Unicode.simpleFold(re.runes[2]) == re.runes[0]) || (re.op == Regexp.Op.CHAR_CLASS && re.runes.length == 2 && re.runes[0] + 1 == re.runes[1] && Unicode.simpleFold(re.runes[0]) == re.runes[1] && Unicode.simpleFold(re.runes[1]) == re.runes[0])) {
            if (this.maybeConcat(re.runes[0], this.flags | 0x1)) {
                return null;
            }
            re.op = Regexp.Op.LITERAL;
            re.runes = new int[] { re.runes[0] };
            re.flags = (this.flags | 0x1);
        }
        else {
            this.maybeConcat(-1, 0);
        }
        this.stack.add(re);
        return re;
    }
    
    private boolean maybeConcat(final int r, final int flags) {
        final int n = this.stack.size();
        if (n < 2) {
            return false;
        }
        final Regexp re1 = this.stack.get(n - 1);
        final Regexp re2 = this.stack.get(n - 2);
        if (re1.op != Regexp.Op.LITERAL || re2.op != Regexp.Op.LITERAL || (re1.flags & 0x1) != (re2.flags & 0x1)) {
            return false;
        }
        re2.runes = concatRunes(re2.runes, re1.runes);
        if (r >= 0) {
            re1.runes = new int[] { r };
            re1.flags = flags;
            return true;
        }
        this.pop();
        this.reuse(re1);
        return false;
    }
    
    private Regexp newLiteral(int r, final int flags) {
        final Regexp re = this.newRegexp(Regexp.Op.LITERAL);
        re.flags = flags;
        if ((flags & 0x1) != 0x0) {
            r = minFoldRune(r);
        }
        re.runes = new int[] { r };
        return re;
    }
    
    private static int minFoldRune(int r) {
        if (r < 65 || r > 66639) {
            return r;
        }
        int min = r;
        int r2;
        for (r2 = r, r = Unicode.simpleFold(r); r != r2; r = Unicode.simpleFold(r)) {
            if (min > r) {
                min = r;
            }
        }
        return min;
    }
    
    private void literal(final int r) {
        this.push(this.newLiteral(r, this.flags));
    }
    
    private Regexp op(final Regexp.Op op) {
        final Regexp re = this.newRegexp(op);
        re.flags = this.flags;
        return this.push(re);
    }
    
    private void repeat(final Regexp.Op op, final int min, final int max, final int beforePos, final StringIterator t, final int lastRepeatPos) throws PatternSyntaxException {
        int flags = this.flags;
        if ((flags & 0x40) != 0x0) {
            if (t.more() && t.lookingAt('?')) {
                t.skip(1);
                flags ^= 0x20;
            }
            if (lastRepeatPos != -1) {
                throw new PatternSyntaxException("invalid nested repetition operator", t.from(lastRepeatPos));
            }
        }
        final int n = this.stack.size();
        if (n == 0) {
            throw new PatternSyntaxException("missing argument to repetition operator", t.from(beforePos));
        }
        final Regexp sub = this.stack.get(n - 1);
        if (sub.op.isPseudo()) {
            throw new PatternSyntaxException("missing argument to repetition operator", t.from(beforePos));
        }
        final Regexp re = this.newRegexp(op);
        re.min = min;
        re.max = max;
        re.flags = flags;
        re.subs = new Regexp[] { sub };
        this.stack.set(n - 1, re);
    }
    
    private Regexp concat() {
        this.maybeConcat(-1, 0);
        final Regexp[] subs = this.popToPseudo();
        if (subs.length == 0) {
            return this.push(this.newRegexp(Regexp.Op.EMPTY_MATCH));
        }
        return this.push(this.collapse(subs, Regexp.Op.CONCAT));
    }
    
    private Regexp alternate() {
        final Regexp[] subs = this.popToPseudo();
        if (subs.length > 0) {
            this.cleanAlt(subs[subs.length - 1]);
        }
        if (subs.length == 0) {
            return this.push(this.newRegexp(Regexp.Op.NO_MATCH));
        }
        return this.push(this.collapse(subs, Regexp.Op.ALTERNATE));
    }
    
    private void cleanAlt(final Regexp re) {
        switch (re.op) {
            case CHAR_CLASS: {
                re.runes = new CharClass(re.runes).cleanClass().toArray();
                if (re.runes.length == 2 && re.runes[0] == 0 && re.runes[1] == 1114111) {
                    re.runes = null;
                    re.op = Regexp.Op.ANY_CHAR;
                    return;
                }
                if (re.runes.length == 4 && re.runes[0] == 0 && re.runes[1] == 9 && re.runes[2] == 11 && re.runes[3] == 1114111) {
                    re.runes = null;
                    re.op = Regexp.Op.ANY_CHAR_NOT_NL;
                    return;
                }
                break;
            }
        }
    }
    
    private Regexp collapse(final Regexp[] subs, final Regexp.Op op) {
        if (subs.length == 1) {
            return subs[0];
        }
        int len = 0;
        for (final Regexp sub : subs) {
            len += ((sub.op == op) ? sub.subs.length : 1);
        }
        final Regexp[] newsubs = new Regexp[len];
        int i = 0;
        for (final Regexp sub2 : subs) {
            if (sub2.op == op) {
                System.arraycopy(sub2.subs, 0, newsubs, i, sub2.subs.length);
                i += sub2.subs.length;
                this.reuse(sub2);
            }
            else {
                newsubs[i++] = sub2;
            }
        }
        Regexp re = this.newRegexp(op);
        re.subs = newsubs;
        if (op == Regexp.Op.ALTERNATE) {
            re.subs = this.factor(re.subs, re.flags);
            if (re.subs.length == 1) {
                final Regexp old = re;
                re = re.subs[0];
                this.reuse(old);
            }
        }
        return re;
    }
    
    private Regexp[] factor(final Regexp[] array, final int flags) {
        if (array.length < 2) {
            return array;
        }
        int s = 0;
        int lensub = array.length;
        int lenout = 0;
        int[] str = null;
        int strlen = 0;
        int strflags = 0;
        int start = 0;
        for (int i = 0; i <= lensub; ++i) {
            int[] istr = null;
            int istrlen = 0;
            int iflags = 0;
            if (i < lensub) {
                Regexp re = array[s + i];
                if (re.op == Regexp.Op.CONCAT && re.subs.length > 0) {
                    re = re.subs[0];
                }
                if (re.op == Regexp.Op.LITERAL) {
                    istr = re.runes;
                    istrlen = re.runes.length;
                    iflags = (re.flags & 0x1);
                }
                if (iflags == strflags) {
                    int same;
                    for (same = 0; same < strlen && same < istrlen && str[same] == istr[same]; ++same) {}
                    if (same > 0) {
                        strlen = same;
                        continue;
                    }
                }
            }
            if (i != start) {
                if (i == start + 1) {
                    array[lenout++] = array[s + start];
                }
                else {
                    final Regexp prefix = this.newRegexp(Regexp.Op.LITERAL);
                    prefix.flags = strflags;
                    prefix.runes = Utils.subarray(str, 0, strlen);
                    for (int j = start; j < i; ++j) {
                        array[s + j] = this.removeLeadingString(array[s + j], strlen);
                    }
                    final Regexp suffix = this.collapse(subarray(array, s + start, s + i), Regexp.Op.ALTERNATE);
                    final Regexp re2 = this.newRegexp(Regexp.Op.CONCAT);
                    re2.subs = new Regexp[] { prefix, suffix };
                    array[lenout++] = re2;
                }
            }
            start = i;
            str = istr;
            strlen = istrlen;
            strflags = iflags;
        }
        lensub = lenout;
        s = 0;
        start = 0;
        lenout = 0;
        Regexp first = null;
        for (int k = 0; k <= lensub; ++k) {
            Regexp ifirst = null;
            if (k < lensub) {
                ifirst = leadingRegexp(array[s + k]);
                if (first != null && first.equals(ifirst)) {
                    continue;
                }
            }
            if (k != start) {
                if (k == start + 1) {
                    array[lenout++] = array[s + start];
                }
                else {
                    final Regexp prefix2 = first;
                    for (int l = start; l < k; ++l) {
                        final boolean reuse = l != start;
                        array[s + l] = this.removeLeadingRegexp(array[s + l], reuse);
                    }
                    final Regexp suffix2 = this.collapse(subarray(array, s + start, s + k), Regexp.Op.ALTERNATE);
                    final Regexp re3 = this.newRegexp(Regexp.Op.CONCAT);
                    re3.subs = new Regexp[] { prefix2, suffix2 };
                    array[lenout++] = re3;
                }
            }
            start = k;
            first = ifirst;
        }
        lensub = lenout;
        s = 0;
        start = 0;
        lenout = 0;
        for (int k = 0; k <= lensub; ++k) {
            if (k >= lensub || !isCharClass(array[s + k])) {
                if (k != start) {
                    if (k == start + 1) {
                        array[lenout++] = array[s + start];
                    }
                    else {
                        int max = start;
                        for (int m = start + 1; m < k; ++m) {
                            final Regexp subMax = array[s + max];
                            final Regexp subJ = array[s + m];
                            if (subMax.op.ordinal() < subJ.op.ordinal() || (subMax.op == subJ.op && subMax.runes.length < subJ.runes.length)) {
                                max = m;
                            }
                        }
                        final Regexp tmp = array[s + start];
                        array[s + start] = array[s + max];
                        array[s + max] = tmp;
                        for (int l = start + 1; l < k; ++l) {
                            mergeCharClass(array[s + start], array[s + l]);
                            this.reuse(array[s + l]);
                        }
                        this.cleanAlt(array[s + start]);
                        array[lenout++] = array[s + start];
                    }
                }
                if (k < lensub) {
                    array[lenout++] = array[s + k];
                }
                start = k + 1;
            }
        }
        lensub = lenout;
        s = 0;
        start = 0;
        lenout = 0;
        for (int k = 0; k < lensub; ++k) {
            if (k + 1 >= lensub || array[s + k].op != Regexp.Op.EMPTY_MATCH || array[s + k + 1].op != Regexp.Op.EMPTY_MATCH) {
                array[lenout++] = array[s + k];
            }
        }
        lensub = lenout;
        s = 0;
        return subarray(array, s, lensub);
    }
    
    private Regexp removeLeadingString(Regexp re, final int n) {
        if (re.op == Regexp.Op.CONCAT && re.subs.length > 0) {
            final Regexp sub = this.removeLeadingString(re.subs[0], n);
            re.subs[0] = sub;
            if (sub.op == Regexp.Op.EMPTY_MATCH) {
                this.reuse(sub);
                switch (re.subs.length) {
                    case 0:
                    case 1: {
                        re.op = Regexp.Op.EMPTY_MATCH;
                        re.subs = null;
                        break;
                    }
                    case 2: {
                        final Regexp old = re;
                        re = re.subs[1];
                        this.reuse(old);
                        break;
                    }
                    default: {
                        re.subs = subarray(re.subs, 1, re.subs.length);
                        break;
                    }
                }
            }
            return re;
        }
        if (re.op == Regexp.Op.LITERAL) {
            re.runes = Utils.subarray(re.runes, n, re.runes.length);
            if (re.runes.length == 0) {
                re.op = Regexp.Op.EMPTY_MATCH;
            }
        }
        return re;
    }
    
    private static Regexp leadingRegexp(final Regexp re) {
        if (re.op == Regexp.Op.EMPTY_MATCH) {
            return null;
        }
        if (re.op != Regexp.Op.CONCAT || re.subs.length <= 0) {
            return re;
        }
        final Regexp sub = re.subs[0];
        if (sub.op == Regexp.Op.EMPTY_MATCH) {
            return null;
        }
        return sub;
    }
    
    private Regexp removeLeadingRegexp(Regexp re, final boolean reuse) {
        if (re.op == Regexp.Op.CONCAT && re.subs.length > 0) {
            if (reuse) {
                this.reuse(re.subs[0]);
            }
            re.subs = subarray(re.subs, 1, re.subs.length);
            switch (re.subs.length) {
                case 0: {
                    re.op = Regexp.Op.EMPTY_MATCH;
                    re.subs = Regexp.EMPTY_SUBS;
                    break;
                }
                case 1: {
                    final Regexp old = re;
                    re = re.subs[0];
                    this.reuse(old);
                    break;
                }
            }
            return re;
        }
        if (reuse) {
            this.reuse(re);
        }
        return this.newRegexp(Regexp.Op.EMPTY_MATCH);
    }
    
    private static Regexp literalRegexp(final String s, final int flags) {
        final Regexp re = new Regexp(Regexp.Op.LITERAL);
        re.flags = flags;
        re.runes = Utils.stringToRunes(s);
        return re;
    }
    
    static Regexp parse(final String pattern, final int flags) throws PatternSyntaxException {
        return new Parser(pattern, flags).parseInternal();
    }
    
    private Regexp parseInternal() throws PatternSyntaxException {
        if ((this.flags & 0x2) != 0x0) {
            return literalRegexp(this.wholeRegexp, this.flags);
        }
        int lastRepeatPos = -1;
        int min = -1;
        int max = -1;
        final StringIterator t = new StringIterator(this.wholeRegexp);
        while (t.more()) {
            int repeatPos = -1;
            Label_0916: {
                switch (t.peek()) {
                    default: {
                        this.literal(t.pop());
                        break;
                    }
                    case 40: {
                        if ((this.flags & 0x40) != 0x0 && t.lookingAt("(?")) {
                            this.parsePerlFlags(t);
                            break;
                        }
                        this.op(Regexp.Op.LEFT_PAREN).cap = ++this.numCap;
                        t.skip(1);
                        break;
                    }
                    case 124: {
                        this.parseVerticalBar();
                        t.skip(1);
                        break;
                    }
                    case 41: {
                        this.parseRightParen();
                        t.skip(1);
                        break;
                    }
                    case 94: {
                        if ((this.flags & 0x10) != 0x0) {
                            this.op(Regexp.Op.BEGIN_TEXT);
                        }
                        else {
                            this.op(Regexp.Op.BEGIN_LINE);
                        }
                        t.skip(1);
                        break;
                    }
                    case 36: {
                        if ((this.flags & 0x10) != 0x0) {
                            final Regexp op2 = this.op(Regexp.Op.END_TEXT);
                            op2.flags |= 0x100;
                        }
                        else {
                            this.op(Regexp.Op.END_LINE);
                        }
                        t.skip(1);
                        break;
                    }
                    case 46: {
                        if ((this.flags & 0x8) != 0x0) {
                            this.op(Regexp.Op.ANY_CHAR);
                        }
                        else {
                            this.op(Regexp.Op.ANY_CHAR_NOT_NL);
                        }
                        t.skip(1);
                        break;
                    }
                    case 91: {
                        this.parseClass(t);
                        break;
                    }
                    case 42:
                    case 43:
                    case 63: {
                        repeatPos = t.pos();
                        Regexp.Op op = null;
                        switch (t.pop()) {
                            case 42: {
                                op = Regexp.Op.STAR;
                                break;
                            }
                            case 43: {
                                op = Regexp.Op.PLUS;
                                break;
                            }
                            case 63: {
                                op = Regexp.Op.QUEST;
                                break;
                            }
                        }
                        this.repeat(op, min, max, repeatPos, t, lastRepeatPos);
                        break;
                    }
                    case 123: {
                        repeatPos = t.pos();
                        final int minMax = parseRepeat(t);
                        if (minMax < 0) {
                            t.rewindTo(repeatPos);
                            this.literal(t.pop());
                            break;
                        }
                        min = minMax >> 16;
                        max = (short)(minMax & 0xFFFF);
                        this.repeat(Regexp.Op.REPEAT, min, max, repeatPos, t, lastRepeatPos);
                        break;
                    }
                    case 92: {
                        final int savedPos = t.pos();
                        t.skip(1);
                        if ((this.flags & 0x40) != 0x0 && t.more()) {
                            final int c = t.pop();
                            switch (c) {
                                case 65: {
                                    this.op(Regexp.Op.BEGIN_TEXT);
                                    break Label_0916;
                                }
                                case 98: {
                                    this.op(Regexp.Op.WORD_BOUNDARY);
                                    break Label_0916;
                                }
                                case 66: {
                                    this.op(Regexp.Op.NO_WORD_BOUNDARY);
                                    break Label_0916;
                                }
                                case 67: {
                                    throw new PatternSyntaxException("invalid escape sequence", "\\C");
                                }
                                case 81: {
                                    String lit = t.rest();
                                    final int i = lit.indexOf("\\E");
                                    if (i >= 0) {
                                        lit = lit.substring(0, i);
                                    }
                                    t.skipString(lit);
                                    t.skipString("\\E");
                                    this.push(literalRegexp(lit, this.flags));
                                    break Label_0916;
                                }
                                case 122: {
                                    this.op(Regexp.Op.END_TEXT);
                                    break Label_0916;
                                }
                                default: {
                                    t.rewindTo(savedPos);
                                    break;
                                }
                            }
                        }
                        final Regexp re = this.newRegexp(Regexp.Op.CHAR_CLASS);
                        re.flags = this.flags;
                        if (t.lookingAt("\\p") || t.lookingAt("\\P")) {
                            final CharClass cc = new CharClass();
                            if (this.parseUnicodeClass(t, cc)) {
                                re.runes = cc.toArray();
                                this.push(re);
                                break;
                            }
                        }
                        final CharClass cc = new CharClass();
                        if (this.parsePerlClassEscape(t, cc)) {
                            re.runes = cc.toArray();
                            this.push(re);
                            break;
                        }
                        t.rewindTo(savedPos);
                        this.reuse(re);
                        this.literal(parseEscape(t));
                        break;
                    }
                }
            }
            lastRepeatPos = repeatPos;
        }
        this.concat();
        if (this.swapVerticalBar()) {
            this.pop();
        }
        this.alternate();
        final int n = this.stack.size();
        if (n != 1) {
            throw new PatternSyntaxException("missing closing )", this.wholeRegexp);
        }
        return this.stack.get(0);
    }
    
    private static int parseRepeat(final StringIterator t) throws PatternSyntaxException {
        final int start = t.pos();
        if (!t.more() || !t.lookingAt('{')) {
            return -1;
        }
        t.skip(1);
        final int min = parseInt(t);
        if (min == -1) {
            return -1;
        }
        if (!t.more()) {
            return -1;
        }
        int max;
        if (!t.lookingAt(',')) {
            max = min;
        }
        else {
            t.skip(1);
            if (!t.more()) {
                return -1;
            }
            if (t.lookingAt('}')) {
                max = -1;
            }
            else if ((max = parseInt(t)) == -1) {
                return -1;
            }
        }
        if (!t.more() || !t.lookingAt('}')) {
            return -1;
        }
        t.skip(1);
        if (min < 0 || min > 1000 || max == -2 || max > 1000 || (max >= 0 && min > max)) {
            throw new PatternSyntaxException("invalid repeat count", t.from(start));
        }
        return min << 16 | (max & 0xFFFF);
    }
    
    private void parsePerlFlags(final StringIterator t) throws PatternSyntaxException {
        final int startPos = t.pos();
        final String s = t.rest();
        if (!s.startsWith("(?P<")) {
            t.skip(2);
            int flags = this.flags;
            int sign = 1;
            boolean sawFlag = false;
        Label_0341:
            while (t.more()) {
                final int c = t.pop();
                switch (c) {
                    default: {
                        break Label_0341;
                    }
                    case 105: {
                        flags |= 0x1;
                        sawFlag = true;
                        continue;
                    }
                    case 109: {
                        flags &= 0xFFFFFFEF;
                        sawFlag = true;
                        continue;
                    }
                    case 115: {
                        flags |= 0x8;
                        sawFlag = true;
                        continue;
                    }
                    case 85: {
                        flags |= 0x20;
                        sawFlag = true;
                        continue;
                    }
                    case 45: {
                        if (sign < 0) {
                            break Label_0341;
                        }
                        sign = -1;
                        flags ^= -1;
                        sawFlag = false;
                        continue;
                    }
                    case 41:
                    case 58: {
                        if (sign < 0) {
                            if (!sawFlag) {
                                break Label_0341;
                            }
                            flags ^= -1;
                        }
                        if (c == 58) {
                            this.op(Regexp.Op.LEFT_PAREN);
                        }
                        this.flags = flags;
                        return;
                    }
                }
            }
            throw new PatternSyntaxException("invalid or unsupported Perl syntax", t.from(startPos));
        }
        final int end = s.indexOf(62);
        if (end < 0) {
            throw new PatternSyntaxException("invalid named capture", s);
        }
        final String name = s.substring(4, end);
        t.skipString(name);
        t.skip(5);
        if (!isValidCaptureName(name)) {
            throw new PatternSyntaxException("invalid named capture", s.substring(0, end));
        }
        final Regexp re = this.op(Regexp.Op.LEFT_PAREN);
        re.cap = ++this.numCap;
        re.name = name;
    }
    
    private static boolean isValidCaptureName(final String name) {
        if (name.isEmpty()) {
            return false;
        }
        for (int i = 0; i < name.length(); ++i) {
            final char c = name.charAt(i);
            if (c != '_' && !Utils.isalnum(c)) {
                return false;
            }
        }
        return true;
    }
    
    private static int parseInt(final StringIterator t) {
        final int start = t.pos();
        int c;
        while (t.more() && (c = t.peek()) >= 48 && c <= 57) {
            t.skip(1);
        }
        final String n = t.from(start);
        if (n.isEmpty() || (n.length() > 1 && n.charAt(0) == '0')) {
            return -1;
        }
        if (n.length() > 8) {
            return -2;
        }
        return Integer.valueOf(n, 10);
    }
    
    private static boolean isCharClass(final Regexp re) {
        return (re.op == Regexp.Op.LITERAL && re.runes.length == 1) || re.op == Regexp.Op.CHAR_CLASS || re.op == Regexp.Op.ANY_CHAR_NOT_NL || re.op == Regexp.Op.ANY_CHAR;
    }
    
    private static boolean matchRune(final Regexp re, final int r) {
        switch (re.op) {
            case LITERAL: {
                return re.runes.length == 1 && re.runes[0] == r;
            }
            case CHAR_CLASS: {
                for (int i = 0; i < re.runes.length; i += 2) {
                    if (re.runes[i] <= r && r <= re.runes[i + 1]) {
                        return true;
                    }
                }
                return false;
            }
            case ANY_CHAR_NOT_NL: {
                return r != 10;
            }
            case ANY_CHAR: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void parseVerticalBar() {
        this.concat();
        if (!this.swapVerticalBar()) {
            this.op(Regexp.Op.VERTICAL_BAR);
        }
    }
    
    private static void mergeCharClass(final Regexp dst, final Regexp src) {
        switch (dst.op) {
            case ANY_CHAR_NOT_NL: {
                if (matchRune(src, 10)) {
                    dst.op = Regexp.Op.ANY_CHAR;
                    break;
                }
                break;
            }
            case CHAR_CLASS: {
                if (src.op == Regexp.Op.LITERAL) {
                    dst.runes = new CharClass(dst.runes).appendLiteral(src.runes[0], src.flags).toArray();
                    break;
                }
                dst.runes = new CharClass(dst.runes).appendClass(src.runes).toArray();
                break;
            }
            case LITERAL: {
                if (src.runes[0] == dst.runes[0] && src.flags == dst.flags) {
                    break;
                }
                dst.op = Regexp.Op.CHAR_CLASS;
                dst.runes = new CharClass().appendLiteral(dst.runes[0], dst.flags).appendLiteral(src.runes[0], src.flags).toArray();
                break;
            }
        }
    }
    
    private boolean swapVerticalBar() {
        final int n = this.stack.size();
        if (n >= 3 && this.stack.get(n - 2).op == Regexp.Op.VERTICAL_BAR && isCharClass(this.stack.get(n - 1)) && isCharClass(this.stack.get(n - 3))) {
            Regexp re1 = this.stack.get(n - 1);
            Regexp re2 = this.stack.get(n - 3);
            if (re1.op.ordinal() > re2.op.ordinal()) {
                final Regexp tmp = re2;
                re2 = re1;
                re1 = tmp;
                this.stack.set(n - 3, re2);
            }
            mergeCharClass(re2, re1);
            this.reuse(re1);
            this.pop();
            return true;
        }
        if (n >= 2) {
            final Regexp re1 = this.stack.get(n - 1);
            final Regexp re3 = this.stack.get(n - 2);
            if (re3.op == Regexp.Op.VERTICAL_BAR) {
                if (n >= 3) {
                    this.cleanAlt(this.stack.get(n - 3));
                }
                this.stack.set(n - 2, re1);
                this.stack.set(n - 1, re3);
                return true;
            }
        }
        return false;
    }
    
    private void parseRightParen() throws PatternSyntaxException {
        this.concat();
        if (this.swapVerticalBar()) {
            this.pop();
        }
        this.alternate();
        final int n = this.stack.size();
        if (n < 2) {
            throw new PatternSyntaxException("regexp/syntax: internal error", "stack underflow");
        }
        final Regexp re1 = this.pop();
        final Regexp re2 = this.pop();
        if (re2.op != Regexp.Op.LEFT_PAREN) {
            throw new PatternSyntaxException("missing closing )", this.wholeRegexp);
        }
        this.flags = re2.flags;
        if (re2.cap == 0) {
            this.push(re1);
        }
        else {
            re2.op = Regexp.Op.CAPTURE;
            re2.subs = new Regexp[] { re1 };
            this.push(re2);
        }
    }
    
    private static int parseEscape(final StringIterator t) throws PatternSyntaxException {
        final int startPos = t.pos();
        t.skip(1);
        if (!t.more()) {
            throw new PatternSyntaxException("trailing backslash at end of expression");
        }
        int c = t.pop();
        switch (c) {
            default: {
                if (!Utils.isalnum(c)) {
                    return c;
                }
                break;
            }
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55: {
                if (!t.more() || t.peek() < 48) {
                    break;
                }
                if (t.peek() > 55) {
                    break;
                }
            }
            case 48: {
                int r = c - 48;
                for (int i = 1; i < 3 && t.more() && t.peek() >= 48 && t.peek() <= 55; ++i) {
                    r = r * 8 + t.peek() - 48;
                    t.skip(1);
                }
                return r;
            }
            case 120: {
                if (!t.more()) {
                    break;
                }
                c = t.pop();
                if (c == 123) {
                    int nhex = 0;
                    int r = 0;
                    while (t.more()) {
                        c = t.pop();
                        if (c == 125) {
                            if (nhex == 0) {
                                break;
                            }
                            return r;
                        }
                        else {
                            final int v = Utils.unhex(c);
                            if (v < 0) {
                                break;
                            }
                            r = r * 16 + v;
                            if (r > 1114111) {
                                break;
                            }
                            ++nhex;
                        }
                    }
                    break;
                }
                final int x = Utils.unhex(c);
                c = t.pop();
                final int y = Utils.unhex(c);
                if (x < 0) {
                    break;
                }
                if (y < 0) {
                    break;
                }
                return x * 16 + y;
            }
            case 97: {
                return 7;
            }
            case 102: {
                return 12;
            }
            case 110: {
                return 10;
            }
            case 114: {
                return 13;
            }
            case 116: {
                return 9;
            }
            case 118: {
                return 11;
            }
        }
        throw new PatternSyntaxException("invalid escape sequence", t.from(startPos));
    }
    
    private static int parseClassChar(final StringIterator t, final int wholeClassPos) throws PatternSyntaxException {
        if (!t.more()) {
            throw new PatternSyntaxException("missing closing ]", t.from(wholeClassPos));
        }
        if (t.lookingAt('\\')) {
            return parseEscape(t);
        }
        return t.pop();
    }
    
    private boolean parsePerlClassEscape(final StringIterator t, final CharClass cc) {
        final int beforePos = t.pos();
        if ((this.flags & 0x40) == 0x0 || !t.more() || t.pop() != 92 || !t.more()) {
            return false;
        }
        t.pop();
        final CharGroup g = CharGroup.PERL_GROUPS.get(t.from(beforePos));
        if (g == null) {
            return false;
        }
        cc.appendGroup(g, (this.flags & 0x1) != 0x0);
        return true;
    }
    
    private boolean parseNamedClass(final StringIterator t, final CharClass cc) throws PatternSyntaxException {
        final String cls = t.rest();
        final int i = cls.indexOf(":]");
        if (i < 0) {
            return false;
        }
        final String name = cls.substring(0, i + 2);
        t.skipString(name);
        final CharGroup g = CharGroup.POSIX_GROUPS.get(name);
        if (g.sign == 0) {
            throw new PatternSyntaxException("invalid character class range", name);
        }
        cc.appendGroup(g, (this.flags & 0x1) != 0x0);
        return true;
    }
    
    private static Pair<int[][], int[][]> unicodeTable(final String name) {
        if (name.equals("Any")) {
            return Pair.of(Parser.ANY_TABLE, Parser.ANY_TABLE);
        }
        int[][] table = UnicodeTables.CATEGORIES.get(name);
        if (table != null) {
            return Pair.of(table, UnicodeTables.FOLD_CATEGORIES.get(name));
        }
        table = UnicodeTables.SCRIPTS.get(name);
        if (table != null) {
            return Pair.of(table, UnicodeTables.FOLD_SCRIPT.get(name));
        }
        return null;
    }
    
    private boolean parseUnicodeClass(final StringIterator t, final CharClass cc) throws PatternSyntaxException {
        final int startPos = t.pos();
        if ((this.flags & 0x80) == 0x0 || (!t.lookingAt("\\p") && !t.lookingAt("\\P"))) {
            return false;
        }
        t.skip(1);
        int sign = 1;
        int c = t.pop();
        if (c == 80) {
            sign = -1;
        }
        c = t.pop();
        String name;
        if (c != 123) {
            name = Utils.runeToString(c);
        }
        else {
            final String rest = t.rest();
            final int end = rest.indexOf(125);
            if (end < 0) {
                t.rewindTo(startPos);
                throw new PatternSyntaxException("invalid character class range", t.rest());
            }
            name = rest.substring(0, end);
            t.skipString(name);
            t.skip(1);
        }
        if (!name.isEmpty() && name.charAt(0) == '^') {
            sign = -sign;
            name = name.substring(1);
        }
        final Pair<int[][], int[][]> pair = unicodeTable(name);
        if (pair == null) {
            throw new PatternSyntaxException("invalid character class range", t.from(startPos));
        }
        final int[][] tab = pair.first;
        final int[][] fold = pair.second;
        if ((this.flags & 0x1) == 0x0 || fold == null) {
            cc.appendTableWithSign(tab, sign);
        }
        else {
            final int[] tmp = new CharClass().appendTable(tab).appendTable(fold).cleanClass().toArray();
            cc.appendClassWithSign(tmp, sign);
        }
        return true;
    }
    
    private void parseClass(final StringIterator t) throws PatternSyntaxException {
        final int startPos = t.pos();
        t.skip(1);
        final Regexp re = this.newRegexp(Regexp.Op.CHAR_CLASS);
        re.flags = this.flags;
        final CharClass cc = new CharClass();
        int sign = 1;
        if (t.more() && t.lookingAt('^')) {
            sign = -1;
            t.skip(1);
            if ((this.flags & 0x4) == 0x0) {
                cc.appendRange(10, 10);
            }
        }
        boolean first = true;
        while (!t.more() || t.peek() != 93 || first) {
            if (t.more() && t.lookingAt('-') && (this.flags & 0x40) == 0x0 && !first) {
                final String s = t.rest();
                if (s.equals("-") || !s.startsWith("-]")) {
                    t.rewindTo(startPos);
                    throw new PatternSyntaxException("invalid character class range", t.rest());
                }
            }
            first = false;
            final int beforePos = t.pos();
            if (t.lookingAt("[:")) {
                if (this.parseNamedClass(t, cc)) {
                    continue;
                }
                t.rewindTo(beforePos);
            }
            if (this.parseUnicodeClass(t, cc)) {
                continue;
            }
            if (this.parsePerlClassEscape(t, cc)) {
                continue;
            }
            t.rewindTo(beforePos);
            int hi;
            final int lo = hi = parseClassChar(t, startPos);
            if (t.more() && t.lookingAt('-')) {
                t.skip(1);
                if (t.more() && t.lookingAt(']')) {
                    t.skip(-1);
                }
                else {
                    hi = parseClassChar(t, startPos);
                    if (hi < lo) {
                        throw new PatternSyntaxException("invalid character class range", t.from(beforePos));
                    }
                }
            }
            if ((this.flags & 0x1) == 0x0) {
                cc.appendRange(lo, hi);
            }
            else {
                cc.appendFoldedRange(lo, hi);
            }
        }
        t.skip(1);
        cc.cleanClass();
        if (sign < 0) {
            cc.negateClass();
        }
        re.runes = cc.toArray();
        this.push(re);
    }
    
    static Regexp[] subarray(final Regexp[] array, final int start, final int end) {
        final Regexp[] r = new Regexp[end - start];
        for (int i = start; i < end; ++i) {
            r[i - start] = array[i];
        }
        return r;
    }
    
    private static int[] concatRunes(final int[] x, final int[] y) {
        final int[] z = new int[x.length + y.length];
        System.arraycopy(x, 0, z, 0, x.length);
        System.arraycopy(y, 0, z, x.length, y.length);
        return z;
    }
    
    static {
        ANY_TABLE = new int[][] { { 0, 1114111, 1 } };
    }
    
    private static class Stack extends ArrayList<Regexp>
    {
        public void removeRange(final int fromIndex, final int toIndex) {
            super.removeRange(fromIndex, toIndex);
        }
    }
    
    private static class StringIterator
    {
        private final String str;
        private int pos;
        
        StringIterator(final String str) {
            this.pos = 0;
            this.str = str;
        }
        
        int pos() {
            return this.pos;
        }
        
        void rewindTo(final int pos) {
            this.pos = pos;
        }
        
        boolean more() {
            return this.pos < this.str.length();
        }
        
        int peek() {
            return this.str.codePointAt(this.pos);
        }
        
        void skip(final int n) {
            this.pos += n;
        }
        
        void skipString(final String s) {
            this.pos += s.length();
        }
        
        int pop() {
            final int r = this.str.codePointAt(this.pos);
            this.pos += Character.charCount(r);
            return r;
        }
        
        boolean lookingAt(final char c) {
            return this.str.charAt(this.pos) == c;
        }
        
        boolean lookingAt(final String s) {
            return this.rest().startsWith(s);
        }
        
        String rest() {
            return this.str.substring(this.pos);
        }
        
        String from(final int beforePos) {
            return this.str.substring(beforePos, this.pos);
        }
        
        @Override
        public String toString() {
            return this.rest();
        }
    }
    
    private static class Pair<F, S>
    {
        final F first;
        final S second;
        
        Pair(final F first, final S second) {
            this.first = first;
            this.second = second;
        }
        
        static <F, S> Pair<F, S> of(final F first, final S second) {
            return new Pair<F, S>(first, second);
        }
    }
}
