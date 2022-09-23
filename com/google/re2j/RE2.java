// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

import java.util.Arrays;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

class RE2
{
    static final int FOLD_CASE = 1;
    static final int LITERAL = 2;
    static final int CLASS_NL = 4;
    static final int DOT_NL = 8;
    static final int ONE_LINE = 16;
    static final int NON_GREEDY = 32;
    static final int PERL_X = 64;
    static final int UNICODE_GROUPS = 128;
    static final int WAS_DOLLAR = 256;
    static final int MATCH_NL = 12;
    static final int PERL = 212;
    static final int POSIX = 0;
    static final int UNANCHORED = 0;
    static final int ANCHOR_START = 1;
    static final int ANCHOR_BOTH = 2;
    final String expr;
    final Prog prog;
    final int cond;
    final int numSubexp;
    boolean longest;
    String prefix;
    byte[] prefixUTF8;
    boolean prefixComplete;
    int prefixRune;
    private final List<Machine> machine;
    
    RE2(final String expr) {
        this.machine = new ArrayList<Machine>();
        final RE2 re2 = compile(expr);
        this.expr = re2.expr;
        this.prog = re2.prog;
        this.cond = re2.cond;
        this.numSubexp = re2.numSubexp;
        this.longest = re2.longest;
        this.prefix = re2.prefix;
        this.prefixUTF8 = re2.prefixUTF8;
        this.prefixComplete = re2.prefixComplete;
        this.prefixRune = re2.prefixRune;
    }
    
    private RE2(final String expr, final Prog prog, final int numSubexp, final boolean longest) {
        this.machine = new ArrayList<Machine>();
        this.expr = expr;
        this.prog = prog;
        this.numSubexp = numSubexp;
        this.cond = prog.startCond();
        this.longest = longest;
    }
    
    static RE2 compile(final String expr) throws PatternSyntaxException {
        return compileImpl(expr, 212, false);
    }
    
    static RE2 compilePOSIX(final String expr) throws PatternSyntaxException {
        return compileImpl(expr, 0, true);
    }
    
    static RE2 compileImpl(final String expr, final int mode, final boolean longest) throws PatternSyntaxException {
        Regexp re = Parser.parse(expr, mode);
        final int maxCap = re.maxCap();
        re = Simplify.simplify(re);
        final Prog prog = Compiler.compileRegexp(re);
        final RE2 re2 = new RE2(expr, prog, maxCap, longest);
        final StringBuilder prefixBuilder = new StringBuilder();
        re2.prefixComplete = prog.prefix(prefixBuilder);
        re2.prefix = prefixBuilder.toString();
        try {
            re2.prefixUTF8 = re2.prefix.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("can't happen");
        }
        if (!re2.prefix.isEmpty()) {
            re2.prefixRune = re2.prefix.codePointAt(0);
        }
        return re2;
    }
    
    int numberOfCapturingGroups() {
        return this.numSubexp;
    }
    
    synchronized Machine get() {
        final int n = this.machine.size();
        if (n > 0) {
            return this.machine.remove(n - 1);
        }
        return new Machine(this);
    }
    
    synchronized void reset() {
        this.machine.clear();
    }
    
    synchronized void put(final Machine m) {
        this.machine.add(m);
    }
    
    @Override
    public String toString() {
        return this.expr;
    }
    
    private int[] doExecute(final MachineInput in, final int pos, final int anchor, final int ncap) {
        final Machine m = this.get();
        m.init(ncap);
        final int[] cap = (int[])(m.match(in, pos, anchor) ? m.submatches() : null);
        this.put(m);
        return cap;
    }
    
    boolean match(final CharSequence s) {
        return this.doExecute(MachineInput.fromUTF16(s), 0, 0, 0) != null;
    }
    
    boolean match(final CharSequence input, final int start, final int end, final int anchor, final int[] group, final int ngroup) {
        if (start > end) {
            return false;
        }
        final int[] groupMatch = this.doExecute(MachineInput.fromUTF16(input, 0, end), start, anchor, 2 * ngroup);
        if (groupMatch == null) {
            return false;
        }
        if (group != null) {
            System.arraycopy(groupMatch, 0, group, 0, groupMatch.length);
        }
        return true;
    }
    
    boolean matchUTF8(final byte[] b) {
        return this.doExecute(MachineInput.fromUTF8(b), 0, 0, 0) != null;
    }
    
    static boolean match(final String pattern, final CharSequence s) throws PatternSyntaxException {
        return compile(pattern).match(s);
    }
    
    String replaceAll(final String src, final String repl) {
        return this.replaceAllFunc(src, new ReplaceFunc() {
            @Override
            public String replace(final String orig) {
                return repl;
            }
        }, 2 * src.length() + 1);
    }
    
    String replaceFirst(final String src, final String repl) {
        return this.replaceAllFunc(src, new ReplaceFunc() {
            @Override
            public String replace(final String orig) {
                return repl;
            }
        }, 1);
    }
    
    String replaceAllFunc(final String src, final ReplaceFunc repl, final int maxReplaces) {
        int lastMatchEnd = 0;
        int searchPos = 0;
        final StringBuilder buf = new StringBuilder();
        final MachineInput input = MachineInput.fromUTF16(src);
        int numReplaces = 0;
        while (searchPos <= src.length()) {
            final int[] a = this.doExecute(input, searchPos, 0, 2);
            if (a == null) {
                break;
            }
            if (a.length == 0) {
                break;
            }
            buf.append(src.substring(lastMatchEnd, a[0]));
            if (a[1] > lastMatchEnd || a[0] == 0) {
                buf.append(repl.replace(src.substring(a[0], a[1])));
                ++numReplaces;
            }
            lastMatchEnd = a[1];
            final int width = input.step(searchPos) & 0x7;
            if (searchPos + width > a[1]) {
                searchPos += width;
            }
            else if (searchPos + 1 > a[1]) {
                ++searchPos;
            }
            else {
                searchPos = a[1];
            }
            if (numReplaces >= maxReplaces) {
                break;
            }
        }
        buf.append(src.substring(lastMatchEnd));
        return buf.toString();
    }
    
    static String quoteMeta(final String s) {
        final StringBuilder b = new StringBuilder(2 * s.length());
        for (int i = 0, len = s.length(); i < len; ++i) {
            final char c = s.charAt(i);
            if ("\\.+*?()|[]{}^$".indexOf(c) >= 0) {
                b.append('\\');
            }
            b.append(c);
        }
        return b.toString();
    }
    
    private int[] pad(int[] a) {
        if (a == null) {
            return null;
        }
        final int n = (1 + this.numSubexp) * 2;
        if (a.length < n) {
            final int[] a2 = new int[n];
            System.arraycopy(a, 0, a2, 0, a.length);
            Arrays.fill(a2, a.length, n, -1);
            a = a2;
        }
        return a;
    }
    
    private void allMatches(final MachineInput input, int n, final DeliverFunc deliver) {
        final int end = input.endPos();
        if (n < 0) {
            n = end + 1;
        }
        int pos = 0;
        int i = 0;
        int prevMatchEnd = -1;
        while (i < n && pos <= end) {
            final int[] matches = this.doExecute(input, pos, 0, this.prog.numCap);
            if (matches == null) {
                break;
            }
            if (matches.length == 0) {
                break;
            }
            boolean accept = true;
            if (matches[1] == pos) {
                if (matches[0] == prevMatchEnd) {
                    accept = false;
                }
                final int r = input.step(pos);
                if (r < 0) {
                    pos = end + 1;
                }
                else {
                    pos += (r & 0x7);
                }
            }
            else {
                pos = matches[1];
            }
            prevMatchEnd = matches[1];
            if (!accept) {
                continue;
            }
            deliver.deliver(this.pad(matches));
            ++i;
        }
    }
    
    byte[] findUTF8(final byte[] b) {
        final int[] a = this.doExecute(MachineInput.fromUTF8(b), 0, 0, 2);
        if (a == null) {
            return null;
        }
        return Utils.subarray(b, a[0], a[1]);
    }
    
    int[] findUTF8Index(final byte[] b) {
        final int[] a = this.doExecute(MachineInput.fromUTF8(b), 0, 0, 2);
        if (a == null) {
            return null;
        }
        return Utils.subarray(a, 0, 2);
    }
    
    String find(final String s) {
        final int[] a = this.doExecute(MachineInput.fromUTF16(s), 0, 0, 2);
        if (a == null) {
            return "";
        }
        return s.substring(a[0], a[1]);
    }
    
    int[] findIndex(final String s) {
        final int[] a = this.doExecute(MachineInput.fromUTF16(s), 0, 0, 2);
        if (a == null) {
            return null;
        }
        return a;
    }
    
    byte[][] findUTF8Submatch(final byte[] b) {
        final int[] a = this.doExecute(MachineInput.fromUTF8(b), 0, 0, this.prog.numCap);
        if (a == null) {
            return null;
        }
        final byte[][] ret = new byte[1 + this.numSubexp][];
        for (int i = 0; i < ret.length; ++i) {
            if (2 * i < a.length && a[2 * i] >= 0) {
                ret[i] = Utils.subarray(b, a[2 * i], a[2 * i + 1]);
            }
        }
        return ret;
    }
    
    int[] findUTF8SubmatchIndex(final byte[] b) {
        return this.pad(this.doExecute(MachineInput.fromUTF8(b), 0, 0, this.prog.numCap));
    }
    
    String[] findSubmatch(final String s) {
        final int[] a = this.doExecute(MachineInput.fromUTF16(s), 0, 0, this.prog.numCap);
        if (a == null) {
            return null;
        }
        final String[] ret = new String[1 + this.numSubexp];
        for (int i = 0; i < ret.length; ++i) {
            if (2 * i < a.length && a[2 * i] >= 0) {
                ret[i] = s.substring(a[2 * i], a[2 * i + 1]);
            }
        }
        return ret;
    }
    
    int[] findSubmatchIndex(final String s) {
        return this.pad(this.doExecute(MachineInput.fromUTF16(s), 0, 0, this.prog.numCap));
    }
    
    List<byte[]> findAllUTF8(final byte[] b, final int n) {
        final List<byte[]> result = new ArrayList<byte[]>();
        this.allMatches(MachineInput.fromUTF8(b), n, new DeliverFunc() {
            @Override
            public void deliver(final int[] match) {
                result.add(Utils.subarray(b, match[0], match[1]));
            }
        });
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
    
    List<int[]> findAllUTF8Index(final byte[] b, final int n) {
        final List<int[]> result = new ArrayList<int[]>();
        this.allMatches(MachineInput.fromUTF8(b), n, new DeliverFunc() {
            @Override
            public void deliver(final int[] match) {
                result.add(Utils.subarray(match, 0, 2));
            }
        });
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
    
    List<String> findAll(final String s, final int n) {
        final List<String> result = new ArrayList<String>();
        this.allMatches(MachineInput.fromUTF16(s), n, new DeliverFunc() {
            @Override
            public void deliver(final int[] match) {
                result.add(s.substring(match[0], match[1]));
            }
        });
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
    
    List<int[]> findAllIndex(final String s, final int n) {
        final List<int[]> result = new ArrayList<int[]>();
        this.allMatches(MachineInput.fromUTF16(s), n, new DeliverFunc() {
            @Override
            public void deliver(final int[] match) {
                result.add(Utils.subarray(match, 0, 2));
            }
        });
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
    
    List<byte[][]> findAllUTF8Submatch(final byte[] b, final int n) {
        final List<byte[][]> result = new ArrayList<byte[][]>();
        this.allMatches(MachineInput.fromUTF8(b), n, new DeliverFunc() {
            @Override
            public void deliver(final int[] match) {
                final byte[][] slice = new byte[match.length / 2][];
                for (int j = 0; j < slice.length; ++j) {
                    if (match[2 * j] >= 0) {
                        slice[j] = Utils.subarray(b, match[2 * j], match[2 * j + 1]);
                    }
                }
                result.add(slice);
            }
        });
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
    
    List<int[]> findAllUTF8SubmatchIndex(final byte[] b, final int n) {
        final List<int[]> result = new ArrayList<int[]>();
        this.allMatches(MachineInput.fromUTF8(b), n, new DeliverFunc() {
            @Override
            public void deliver(final int[] match) {
                result.add(match);
            }
        });
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
    
    List<String[]> findAllSubmatch(final String s, final int n) {
        final List<String[]> result = new ArrayList<String[]>();
        this.allMatches(MachineInput.fromUTF16(s), n, new DeliverFunc() {
            @Override
            public void deliver(final int[] match) {
                final String[] slice = new String[match.length / 2];
                for (int j = 0; j < slice.length; ++j) {
                    if (match[2 * j] >= 0) {
                        slice[j] = s.substring(match[2 * j], match[2 * j + 1]);
                    }
                }
                result.add(slice);
            }
        });
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
    
    List<int[]> findAllSubmatchIndex(final String s, final int n) {
        final List<int[]> result = new ArrayList<int[]>();
        this.allMatches(MachineInput.fromUTF16(s), n, new DeliverFunc() {
            @Override
            public void deliver(final int[] match) {
                result.add(match);
            }
        });
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
    
    private interface DeliverFunc
    {
        void deliver(final int[] p0);
    }
    
    interface ReplaceFunc
    {
        String replace(final String p0);
    }
}
