// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;

class DateTimeParser
{
    private String str;
    private int len;
    private int fieldStart;
    private char currentSeparator;
    
    DateTimeParser(final String str) {
        this.str = str;
        this.len = str.length();
    }
    
    int parseInt(final int n, final boolean b, final char[] array, final boolean b2) throws StandardException {
        int n2 = 0;
        int n3 = 0;
        while (this.fieldStart < this.len) {
            final char char1 = this.str.charAt(this.fieldStart);
            if (!Character.isDigit(char1)) {
                break;
            }
            if (n3 >= n) {
                throw StandardException.newException("22007.S.181");
            }
            ++n3;
            n2 = n2 * 10 + Character.digit(char1, 10);
            ++this.fieldStart;
        }
        Label_0110: {
            if (b) {
                if (n3 != 0 || b2) {
                    break Label_0110;
                }
            }
            else if (n3 == n) {
                break Label_0110;
            }
            throw StandardException.newException("22007.S.181");
        }
        this.updateCurrentSeparator();
        if (array == null) {
            if (this.fieldStart < this.len) {
                ++this.fieldStart;
            }
        }
        else {
            int i;
            for (i = 0; i < array.length; ++i) {
                if (array[i] != '\0') {
                    if (this.currentSeparator == array[i]) {
                        ++this.fieldStart;
                        break;
                    }
                }
                else {
                    int fieldStart;
                    for (fieldStart = this.fieldStart; fieldStart < this.len && this.str.charAt(fieldStart) == ' '; ++fieldStart) {}
                    if (fieldStart == this.len) {
                        this.fieldStart = fieldStart;
                        break;
                    }
                }
            }
            if (i >= array.length) {
                throw StandardException.newException("22007.S.181");
            }
        }
        if (b2) {
            for (int j = n3; j < n; ++j) {
                n2 *= 10;
            }
        }
        return n2;
    }
    
    int parseChoice(final String[] array) throws StandardException {
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            final int length = s.length();
            if (this.fieldStart + length <= this.len) {
                int index;
                for (index = 0; index < length && s.charAt(index) == this.str.charAt(this.fieldStart + index); ++index) {}
                if (index == length) {
                    this.fieldStart += length;
                    this.updateCurrentSeparator();
                    return i;
                }
            }
        }
        throw StandardException.newException("22007.S.181");
    }
    
    private void updateCurrentSeparator() {
        if (this.fieldStart >= this.len) {
            this.currentSeparator = '\0';
        }
        else {
            this.currentSeparator = this.str.charAt(this.fieldStart);
            if (this.currentSeparator == ' ') {
                for (int i = this.fieldStart + 1; i < this.len; ++i) {
                    if (this.str.charAt(i) != ' ') {
                        return;
                    }
                }
                this.currentSeparator = '\0';
                this.fieldStart = this.len;
            }
        }
    }
    
    void checkEnd() throws StandardException {
        while (this.fieldStart < this.len) {
            if (this.str.charAt(this.fieldStart) != ' ') {
                throw StandardException.newException("22007.S.181");
            }
            ++this.fieldStart;
        }
        this.currentSeparator = '\0';
    }
    
    char nextSeparator() {
        for (int i = this.fieldStart + 1; i < this.len; ++i) {
            final char char1 = this.str.charAt(i);
            if (!Character.isLetterOrDigit(char1)) {
                return char1;
            }
        }
        return '\0';
    }
    
    char getCurrentSeparator() {
        return this.currentSeparator;
    }
}
