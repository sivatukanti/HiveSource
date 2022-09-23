// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

public final class Matcher
{
    private final Pattern pattern;
    private final int[] groups;
    private final int groupCount;
    private CharSequence inputSequence;
    private int inputLength;
    private int appendPos;
    private boolean hasMatch;
    private boolean hasGroups;
    private int anchorFlag;
    
    private Matcher(final Pattern pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern is null");
        }
        this.pattern = pattern;
        final RE2 re2 = pattern.re2();
        this.groupCount = re2.numberOfCapturingGroups();
        this.groups = new int[2 + 2 * this.groupCount];
    }
    
    Matcher(final Pattern pattern, final CharSequence input) {
        this(pattern);
        this.reset(input);
    }
    
    public Pattern pattern() {
        return this.pattern;
    }
    
    public Matcher reset() {
        this.appendPos = 0;
        this.hasMatch = false;
        this.hasGroups = false;
        return this;
    }
    
    public Matcher reset(final CharSequence input) {
        if (input == null) {
            throw new NullPointerException("input is null");
        }
        this.reset();
        this.inputSequence = input;
        this.inputLength = input.length();
        return this;
    }
    
    public int start() {
        return this.start(0);
    }
    
    public int end() {
        return this.end(0);
    }
    
    public int start(final int group) {
        this.loadGroup(group);
        return this.groups[2 * group];
    }
    
    public int end(final int group) {
        this.loadGroup(group);
        return this.groups[2 * group + 1];
    }
    
    public String group() {
        return this.group(0);
    }
    
    public String group(final int group) {
        final int start = this.start(group);
        final int end = this.end(group);
        if (start < 0 && end < 0) {
            return null;
        }
        return this.substring(start, end);
    }
    
    public int groupCount() {
        return this.groupCount;
    }
    
    private void loadGroup(final int group) {
        if (group < 0 || group > this.groupCount) {
            throw new IndexOutOfBoundsException("Group index out of bounds: " + group);
        }
        if (!this.hasMatch) {
            throw new IllegalStateException("perhaps no match attempted");
        }
        if (group == 0 || this.hasGroups) {
            return;
        }
        int end = this.groups[1] + 1;
        if (end > this.inputLength) {
            end = this.inputLength;
        }
        final boolean ok = this.pattern.re2().match(this.inputSequence, this.groups[0], end, this.anchorFlag, this.groups, 1 + this.groupCount);
        if (!ok) {
            throw new IllegalStateException("inconsistency in matching group data");
        }
        this.hasGroups = true;
    }
    
    public boolean matches() {
        return this.genMatch(0, 2);
    }
    
    public boolean lookingAt() {
        return this.genMatch(0, 1);
    }
    
    public boolean find() {
        int start = 0;
        if (this.hasMatch) {
            start = this.groups[1];
            if (this.groups[0] == this.groups[1]) {
                ++start;
            }
        }
        return this.genMatch(start, 0);
    }
    
    public boolean find(final int start) {
        if (start < 0 || start > this.inputLength) {
            throw new IndexOutOfBoundsException("start index out of bounds: " + start);
        }
        this.reset();
        return this.genMatch(start, 0);
    }
    
    private boolean genMatch(final int startByte, final int anchor) {
        final boolean ok = this.pattern.re2().match(this.inputSequence, startByte, this.inputLength, anchor, this.groups, 1);
        if (!ok) {
            return false;
        }
        this.hasMatch = true;
        this.hasGroups = false;
        this.anchorFlag = anchor;
        return true;
    }
    
    String substring(final int start, final int end) {
        return this.inputSequence.subSequence(start, end).toString();
    }
    
    int inputLength() {
        return this.inputLength;
    }
    
    public Matcher appendReplacement(final StringBuffer sb, final String replacement) {
        final int s = this.start();
        final int e = this.end();
        if (this.appendPos < s) {
            sb.append(this.substring(this.appendPos, s));
        }
        this.appendPos = e;
        int last = 0;
        int i;
        int m;
        for (i = 0, m = replacement.length(); i < m - 1; ++i) {
            if (replacement.charAt(i) == '\\') {
                if (last < i) {
                    sb.append(replacement.substring(last, i));
                }
                last = ++i;
            }
            else if (replacement.charAt(i) == '$') {
                int c = replacement.charAt(i + 1);
                if (48 <= c && c <= 57) {
                    int n = c - 48;
                    if (last < i) {
                        sb.append(replacement.substring(last, i));
                    }
                    for (i += 2; i < m; ++i) {
                        c = replacement.charAt(i);
                        if (c < 48 || c > 57) {
                            break;
                        }
                        if (n * 10 + c - 48 > this.groupCount) {
                            break;
                        }
                        n = n * 10 + c - 48;
                    }
                    if (n > this.groupCount) {
                        throw new IndexOutOfBoundsException("n > number of groups: " + n);
                    }
                    final String group = this.group(n);
                    if (group != null) {
                        sb.append(group);
                    }
                    last = i;
                    --i;
                }
            }
        }
        if (last < m) {
            sb.append(replacement.substring(last, m));
        }
        return this;
    }
    
    public StringBuffer appendTail(final StringBuffer sb) {
        sb.append(this.substring(this.appendPos, this.inputLength));
        return sb;
    }
    
    public String replaceAll(final String replacement) {
        return this.replace(replacement, true);
    }
    
    public String replaceFirst(final String replacement) {
        return this.replace(replacement, false);
    }
    
    private String replace(final String replacement, final boolean all) {
        this.reset();
        final StringBuffer sb = new StringBuffer();
        while (this.find()) {
            this.appendReplacement(sb, replacement);
            if (!all) {
                break;
            }
        }
        this.appendTail(sb);
        return sb.toString();
    }
}
