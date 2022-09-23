// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import java.util.TreeSet;

public final class WordResolver
{
    public static final int MAX_WORDS = 8192;
    static final char CHAR_NULL = '\0';
    static final int NEGATIVE_OFFSET = 57344;
    static final int MIN_BINARY_SEARCH = 7;
    final char[] mData;
    final String[] mWords;
    
    private WordResolver(final String[] words, final char[] index) {
        this.mWords = words;
        this.mData = index;
    }
    
    public static WordResolver constructInstance(final TreeSet<String> wordSet) {
        if (wordSet.size() > 8192) {
            return null;
        }
        return new Builder(wordSet).construct();
    }
    
    public int size() {
        return this.mWords.length;
    }
    
    public String find(final char[] str, final int start, final int end) {
        final char[] data = this.mData;
        if (data == null) {
            return this.findFromOne(str, start, end);
        }
        int ptr = 0;
        int offset = start;
        while (offset != end) {
            final int count = data[ptr++];
            final char c = str[offset++];
            Label_0269: {
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
                                break Label_0269;
                            }
                            low = mid + 1;
                        }
                    }
                    return null;
                }
                if (data[ptr] == c) {
                    ptr = data[ptr + 1];
                }
                else {
                    if (data[ptr + 2] != c) {
                        for (final int branchEnd = ptr + (count << 1), ptr += 4; ptr < branchEnd; ptr += 2) {
                            if (data[ptr] == c) {
                                ptr = data[ptr + 1];
                                break Label_0269;
                            }
                        }
                        return null;
                    }
                    ptr = data[ptr + 3];
                }
            }
            if (ptr >= 57344) {
                final String word = this.mWords[ptr - 57344];
                final int expLen = end - start;
                if (word.length() != expLen) {
                    return null;
                }
                int i = offset - start;
                while (offset < end) {
                    if (word.charAt(i) != str[offset]) {
                        return null;
                    }
                    ++i;
                    ++offset;
                }
                return word;
            }
        }
        if (data[ptr + 1] == '\0') {
            return this.mWords[data[ptr + 2] - '\ue000'];
        }
        return null;
    }
    
    private String findFromOne(final char[] str, final int start, final int end) {
        final String word = this.mWords[0];
        final int len = end - start;
        if (word.length() != len) {
            return null;
        }
        for (int i = 0; i < len; ++i) {
            if (word.charAt(i) != str[start + i]) {
                return null;
            }
        }
        return word;
    }
    
    public String find(final String str) {
        final char[] data = this.mData;
        if (data == null) {
            final String word = this.mWords[0];
            return word.equals(str) ? word : null;
        }
        int ptr = 0;
        int offset = 0;
        final int end = str.length();
        while (offset != end) {
            final int count = data[ptr++];
            final char c = str.charAt(offset++);
            Label_0261: {
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
                                break Label_0261;
                            }
                            low = mid + 1;
                        }
                    }
                    return null;
                }
                if (data[ptr] == c) {
                    ptr = data[ptr + 1];
                }
                else {
                    if (data[ptr + 2] != c) {
                        for (final int branchEnd = ptr + (count << 1), ptr += 4; ptr < branchEnd; ptr += 2) {
                            if (data[ptr] == c) {
                                ptr = data[ptr + 1];
                                break Label_0261;
                            }
                        }
                        return null;
                    }
                    ptr = data[ptr + 3];
                }
            }
            if (ptr >= 57344) {
                final String word2 = this.mWords[ptr - 57344];
                if (word2.length() != str.length()) {
                    return null;
                }
                while (offset < end) {
                    if (word2.charAt(offset) != str.charAt(offset)) {
                        return null;
                    }
                    ++offset;
                }
                return word2;
            }
        }
        if (data[ptr + 1] == '\0') {
            return this.mWords[data[ptr + 2] - '\ue000'];
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(16 + (this.mWords.length << 3));
        for (int i = 0, len = this.mWords.length; i < len; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.mWords[i]);
        }
        return sb.toString();
    }
    
    private static final class Builder
    {
        final String[] mWords;
        char[] mData;
        int mSize;
        
        public Builder(final TreeSet<String> wordSet) {
            final int wordCount = wordSet.size();
            wordSet.toArray(this.mWords = new String[wordCount]);
            if (wordCount < 2) {
                if (wordCount == 0) {
                    throw new IllegalArgumentException();
                }
                this.mData = null;
            }
            else {
                int size = wordCount * 6;
                if (size < 256) {
                    size = 256;
                }
                this.mData = new char[size];
            }
        }
        
        public WordResolver construct() {
            char[] result;
            if (this.mData == null) {
                result = null;
            }
            else {
                this.constructBranch(0, 0, this.mWords.length);
                if (this.mSize > 57344) {
                    return null;
                }
                result = new char[this.mSize];
                System.arraycopy(this.mData, 0, result, 0, this.mSize);
            }
            return new WordResolver(this.mWords, result, null);
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
            boolean gotRunt;
            if (words[groupStart].length() == charIndex) {
                if (this.mSize + 2 > this.mData.length) {
                    this.expand(2);
                }
                this.mData[this.mSize++] = '\0';
                this.mData[this.mSize++] = (char)(57344 + groupStart);
                ++groupStart;
                ++groupCount;
                gotRunt = true;
            }
            else {
                gotRunt = false;
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
            if (gotRunt) {
                structStart += 2;
                ++groupStart;
            }
            final int structEnd = this.mSize;
            ++charIndex;
            while (structStart < structEnd) {
                groupCount = this.mData[structStart];
                if (groupCount == 1) {
                    this.mData[structStart] = (char)(57344 + groupStart);
                }
                else {
                    this.mData[structStart] = (char)this.mSize;
                    this.constructBranch(charIndex, groupStart, groupStart + groupCount);
                }
                groupStart += groupCount;
                structStart += 2;
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
