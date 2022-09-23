// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

abstract class MachineInput
{
    static final int EOF = -8;
    
    static MachineInput fromUTF8(final byte[] b) {
        return new UTF8Input(b);
    }
    
    static MachineInput fromUTF8(final byte[] b, final int start, final int end) {
        return new UTF8Input(b, start, end);
    }
    
    static MachineInput fromUTF16(final CharSequence s) {
        return new UTF16Input(s, 0, s.length());
    }
    
    static MachineInput fromUTF16(final CharSequence s, final int start, final int end) {
        return new UTF16Input(s, start, end);
    }
    
    abstract int step(final int p0);
    
    abstract boolean canCheckPrefix();
    
    abstract int index(final RE2 p0, final int p1);
    
    abstract int context(final int p0);
    
    abstract int endPos();
    
    private static class UTF8Input extends MachineInput
    {
        final byte[] b;
        final int start;
        final int end;
        
        UTF8Input(final byte[] b) {
            this.b = b;
            this.start = 0;
            this.end = b.length;
        }
        
        UTF8Input(final byte[] b, final int start, final int end) {
            if (end > b.length) {
                throw new ArrayIndexOutOfBoundsException("end is greater than length: " + end + " > " + b.length);
            }
            this.b = b;
            this.start = start;
            this.end = end;
        }
        
        @Override
        int step(int i) {
            i += this.start;
            if (i >= this.end) {
                return -8;
            }
            int x = this.b[i++] & 0xFF;
            if ((x & 0x80) == 0x0) {
                return x << 3 | 0x1;
            }
            if ((x & 0xE0) == 0xC0) {
                x &= 0x1F;
                if (i >= this.end) {
                    return -8;
                }
                x = (x << 6 | (this.b[i++] & 0x3F));
                return x << 3 | 0x2;
            }
            else if ((x & 0xF0) == 0xE0) {
                x &= 0xF;
                if (i + 1 >= this.end) {
                    return -8;
                }
                x = (x << 6 | (this.b[i++] & 0x3F));
                x = (x << 6 | (this.b[i++] & 0x3F));
                return x << 3 | 0x3;
            }
            else {
                x &= 0x7;
                if (i + 2 >= this.end) {
                    return -8;
                }
                x = (x << 6 | (this.b[i++] & 0x3F));
                x = (x << 6 | (this.b[i++] & 0x3F));
                x = (x << 6 | (this.b[i++] & 0x3F));
                return x << 3 | 0x4;
            }
        }
        
        @Override
        boolean canCheckPrefix() {
            return true;
        }
        
        @Override
        int index(final RE2 re2, int pos) {
            pos += this.start;
            final int i = Utils.indexOf(this.b, re2.prefixUTF8, pos);
            return (i < 0) ? i : (i - pos);
        }
        
        @Override
        int context(int pos) {
            pos += this.start;
            int r1 = -1;
            if (pos > this.start && pos <= this.end) {
                int start = pos - 1;
                r1 = this.b[start--];
                if (r1 >= 128) {
                    int lim = pos - 4;
                    if (lim < this.start) {
                        lim = this.start;
                    }
                    while (start >= lim && (this.b[start] & 0xC0) == 0x80) {
                        --start;
                    }
                    if (start < this.start) {
                        start = this.start;
                    }
                    r1 = this.step(start) >> 3;
                }
            }
            final int r2 = (pos < this.end) ? (this.step(pos) >> 3) : -1;
            return Utils.emptyOpContext(r1, r2);
        }
        
        @Override
        int endPos() {
            return this.end;
        }
    }
    
    private static class UTF16Input extends MachineInput
    {
        final CharSequence str;
        final int start;
        final int end;
        
        public UTF16Input(final CharSequence str, final int start, final int end) {
            this.str = str;
            this.start = start;
            this.end = end;
        }
        
        @Override
        int step(int pos) {
            pos += this.start;
            if (pos < this.end) {
                final int rune = Character.codePointAt(this.str, pos);
                final int nextPos = pos + Character.charCount(rune);
                final int width = nextPos - pos;
                return rune << 3 | width;
            }
            return -8;
        }
        
        @Override
        boolean canCheckPrefix() {
            return true;
        }
        
        @Override
        int index(final RE2 re2, int pos) {
            pos += this.start;
            final int i = this.indexOf(this.str, re2.prefix, pos);
            return (i < 0) ? i : (i - pos);
        }
        
        @Override
        int context(int pos) {
            pos += this.start;
            final int r1 = (pos > this.start && pos <= this.end) ? Character.codePointBefore(this.str, pos) : -1;
            final int r2 = (pos < this.end) ? Character.codePointAt(this.str, pos) : -1;
            return Utils.emptyOpContext(r1, r2);
        }
        
        @Override
        int endPos() {
            return this.end;
        }
        
        private int indexOf(final CharSequence hayStack, final String needle, final int pos) {
            if (hayStack instanceof String) {
                return ((String)hayStack).indexOf(needle, pos);
            }
            if (hayStack instanceof StringBuilder) {
                return ((StringBuilder)hayStack).indexOf(needle, pos);
            }
            return this.indexOfFallback(hayStack, needle, pos);
        }
        
        private int indexOfFallback(final CharSequence hayStack, final String needle, int fromIndex) {
            if (fromIndex >= hayStack.length()) {
                return needle.isEmpty() ? 0 : -1;
            }
            if (fromIndex < 0) {
                fromIndex = 0;
            }
            if (needle.isEmpty()) {
                return fromIndex;
            }
            final char first = needle.charAt(0);
            for (int max = hayStack.length() - needle.length(), i = fromIndex; i <= max; ++i) {
                if (hayStack.charAt(i) != first) {
                    while (++i <= max && hayStack.charAt(i) != first) {}
                }
                if (i <= max) {
                    int j = i + 1;
                    final int end = j + needle.length() - 1;
                    for (int k = 1; j < end && hayStack.charAt(j) == needle.charAt(k); ++j, ++k) {}
                    if (j == end) {
                        return i;
                    }
                }
            }
            return -1;
        }
    }
}
