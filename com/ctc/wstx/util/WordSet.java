// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import java.util.TreeSet;

public final class WordSet
{
    static final char CHAR_NULL = '\0';
    static final int NEGATIVE_OFFSET = 49152;
    static final int MIN_BINARY_SEARCH = 7;
    final char[] mData;
    
    private WordSet(final char[] data) {
        this.mData = data;
    }
    
    public static WordSet constructSet(final TreeSet<String> wordSet) {
        return new WordSet(new Builder(wordSet).construct());
    }
    
    public static char[] constructRaw(final TreeSet<String> wordSet) {
        return new Builder(wordSet).construct();
    }
    
    public boolean contains(final char[] buf, final int start, final int end) {
        return contains(this.mData, buf, start, end);
    }
    
    public static boolean contains(final char[] data, final char[] str, int start, final int end) {
        int ptr = 0;
    Label_0279:
        do {
            final int left = end - start;
            if (left == 0) {
                return data[ptr + 1] == '\0';
            }
            final int count = data[ptr++];
            if (count >= 49152) {
                final int expCount = count - 49152;
                if (left != expCount) {
                    return false;
                }
                while (start < end) {
                    if (data[ptr] != str[start]) {
                        return false;
                    }
                    ++ptr;
                    ++start;
                }
                return true;
            }
            else {
                final char c = str[start++];
                if (count >= 7) {
                    int low = 0;
                    int high = count - 1;
                    while (low <= high) {
                        final int mid = low + high >> 1;
                        final int ix = ptr + (mid << 1);
                        final int diff = data[ix] - c;
                        if (diff > 0) {
                            high = mid - 1;
                        }
                        else {
                            if (diff >= 0) {
                                ptr = data[ix + 1];
                                continue Label_0279;
                            }
                            low = mid + 1;
                        }
                    }
                    return false;
                }
                if (data[ptr] == c) {
                    ptr = data[ptr + 1];
                }
                else {
                    if (data[ptr + 2] != c) {
                        for (final int branchEnd = ptr + (count << 1), ptr += 4; ptr < branchEnd; ptr += 2) {
                            if (data[ptr] == c) {
                                ptr = data[ptr + 1];
                                continue Label_0279;
                            }
                        }
                        return false;
                    }
                    ptr = data[ptr + 3];
                }
            }
        } while (ptr != 0);
        return start == end;
    }
    
    public boolean contains(final String str) {
        return contains(this.mData, str);
    }
    
    public static boolean contains(final char[] data, final String str) {
        int ptr = 0;
        int start = 0;
        final int end = str.length();
    Label_0276:
        do {
            final int left = end - start;
            if (left == 0) {
                return data[ptr + 1] == '\0';
            }
            final int count = data[ptr++];
            if (count >= 49152) {
                final int expCount = count - 49152;
                if (left != expCount) {
                    return false;
                }
                while (start < end) {
                    if (data[ptr] != str.charAt(start)) {
                        return false;
                    }
                    ++ptr;
                    ++start;
                }
                return true;
            }
            else {
                final char c = str.charAt(start++);
                if (count >= 7) {
                    int low = 0;
                    int high = count - 1;
                    while (low <= high) {
                        final int mid = low + high >> 1;
                        final int ix = ptr + (mid << 1);
                        final int diff = data[ix] - c;
                        if (diff > 0) {
                            high = mid - 1;
                        }
                        else {
                            if (diff >= 0) {
                                ptr = data[ix + 1];
                                continue Label_0276;
                            }
                            low = mid + 1;
                        }
                    }
                    return false;
                }
                if (data[ptr] == c) {
                    ptr = data[ptr + 1];
                }
                else {
                    if (data[ptr + 2] != c) {
                        for (final int branchEnd = ptr + (count << 1), ptr += 4; ptr < branchEnd; ptr += 2) {
                            if (data[ptr] == c) {
                                ptr = data[ptr + 1];
                                continue Label_0276;
                            }
                        }
                        return false;
                    }
                    ptr = data[ptr + 3];
                }
            }
        } while (ptr != 0);
        return start == end;
    }
    
    private static final class Builder
    {
        final String[] mWords;
        char[] mData;
        int mSize;
        
        public Builder(final TreeSet<String> wordSet) {
            final int wordCount = wordSet.size();
            wordSet.toArray(this.mWords = new String[wordCount]);
            int size = wordCount * 12;
            if (size < 256) {
                size = 256;
            }
            this.mData = new char[size];
        }
        
        public char[] construct() {
            if (this.mWords.length == 1) {
                this.constructLeaf(0, 0);
            }
            else {
                this.constructBranch(0, 0, this.mWords.length);
            }
            final char[] result = new char[this.mSize];
            System.arraycopy(this.mData, 0, result, 0, this.mSize);
            return result;
        }
        
        private void constructBranch(int charIndex, final int start, final int end) {
            if (this.mSize >= this.mData.length) {
                this.expand(1);
            }
            this.mData[this.mSize++] = '\0';
            int structStart = this.mSize + 1;
            int groupCount = 0;
            int groupStart = start;
            final String[] words = this.mWords;
            if (words[groupStart].length() == charIndex) {
                if (this.mSize + 2 > this.mData.length) {
                    this.expand(2);
                }
                this.mData[this.mSize++] = '\0';
                this.mData[this.mSize++] = '\0';
                ++groupStart;
                ++groupCount;
            }
            while (groupStart < end) {
                char c;
                int j;
                for (c = words[groupStart].charAt(charIndex), j = groupStart + 1; j < end && words[j].charAt(charIndex) == c; ++j) {}
                if (this.mSize + 2 > this.mData.length) {
                    this.expand(2);
                }
                this.mData[this.mSize++] = c;
                this.mData[this.mSize++] = (char)(j - groupStart);
                groupStart = j;
                ++groupCount;
            }
            this.mData[structStart - 2] = (char)groupCount;
            groupStart = start;
            if (this.mData[structStart] == '\0') {
                structStart += 2;
                ++groupStart;
            }
            final int structEnd = this.mSize;
            ++charIndex;
            while (structStart < structEnd) {
                groupCount = this.mData[structStart];
                this.mData[structStart] = (char)this.mSize;
                if (groupCount == 1) {
                    final String word = words[groupStart];
                    if (word.length() == charIndex) {
                        this.mData[structStart] = '\0';
                    }
                    else {
                        this.constructLeaf(charIndex, groupStart);
                    }
                }
                else {
                    this.constructBranch(charIndex, groupStart, groupStart + groupCount);
                }
                groupStart += groupCount;
                structStart += 2;
            }
        }
        
        private void constructLeaf(int charIndex, final int wordIndex) {
            final String word = this.mWords[wordIndex];
            final int len = word.length();
            char[] data = this.mData;
            if (this.mSize + len + 1 >= data.length) {
                data = this.expand(len + 1);
            }
            data[this.mSize++] = (char)(49152 + (len - charIndex));
            while (charIndex < len) {
                data[this.mSize++] = word.charAt(charIndex);
                ++charIndex;
            }
        }
        
        private char[] expand(final int needSpace) {
            final char[] old = this.mData;
            final int len = old.length;
            int newSize = len + ((len < 4096) ? len : (len >> 1));
            if (newSize < this.mSize + needSpace) {
                newSize = this.mSize + needSpace + 64;
            }
            System.arraycopy(old, 0, this.mData = new char[newSize], 0, len);
            return this.mData;
        }
    }
}
