// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

public final class TextAccumulator
{
    private String mText;
    private StringBuilder mBuilder;
    
    public TextAccumulator() {
        this.mText = null;
        this.mBuilder = null;
    }
    
    public boolean hasText() {
        return this.mBuilder != null || this.mText != null;
    }
    
    public void addText(final String text) {
        final int len = text.length();
        if (len > 0) {
            if (this.mText != null) {
                (this.mBuilder = new StringBuilder(this.mText.length() + len)).append(this.mText);
                this.mText = null;
            }
            if (this.mBuilder != null) {
                this.mBuilder.append(text);
            }
            else {
                this.mText = text;
            }
        }
    }
    
    public void addText(final char[] buf, final int start, final int end) {
        final int len = end - start;
        if (len > 0) {
            if (this.mText != null) {
                (this.mBuilder = new StringBuilder(this.mText.length() + len)).append(this.mText);
                this.mText = null;
            }
            else if (this.mBuilder == null) {
                this.mBuilder = new StringBuilder(len);
            }
            this.mBuilder.append(buf, start, end - start);
        }
    }
    
    public String getAndClear() {
        if (this.mText != null) {
            final String result = this.mText;
            this.mText = null;
            return result;
        }
        if (this.mBuilder != null) {
            final String result = this.mBuilder.toString();
            this.mBuilder = null;
            return result;
        }
        return "";
    }
}
