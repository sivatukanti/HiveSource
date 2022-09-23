// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.io.Writer;
import java.util.List;

public class AutoIndentWriter implements StringTemplateWriter
{
    protected List indents;
    protected int[] anchors;
    protected int anchors_sp;
    protected String newline;
    protected Writer out;
    protected boolean atStartOfLine;
    protected int charPosition;
    protected int lineWidth;
    protected int charPositionOfStartOfExpr;
    
    public AutoIndentWriter(final Writer out, final String newline) {
        this.indents = new ArrayList();
        this.anchors = new int[10];
        this.anchors_sp = -1;
        this.out = null;
        this.atStartOfLine = true;
        this.charPosition = 0;
        this.lineWidth = -1;
        this.charPositionOfStartOfExpr = 0;
        this.out = out;
        this.indents.add(null);
        this.newline = newline;
    }
    
    public AutoIndentWriter(final Writer out) {
        this(out, System.getProperty("line.separator"));
    }
    
    public void setLineWidth(final int lineWidth) {
        this.lineWidth = lineWidth;
    }
    
    public void pushIndentation(final String indent) {
        int lastAnchor = 0;
        final int indentWidth = this.getIndentationWidth();
        if (this.anchors_sp >= 0 && this.anchors[this.anchors_sp] > indentWidth) {
            lastAnchor = this.anchors[this.anchors_sp];
            final StringBuffer buf = this.getIndentString(lastAnchor - indentWidth);
            if (indent != null) {
                buf.append(indent);
            }
            this.indents.add(buf.toString());
            return;
        }
        this.indents.add(indent);
    }
    
    public String popIndentation() {
        return this.indents.remove(this.indents.size() - 1);
    }
    
    public void pushAnchorPoint() {
        if (this.anchors_sp + 1 >= this.anchors.length) {
            final int[] a = new int[this.anchors.length * 2];
            System.arraycopy(this.anchors, 0, a, 0, this.anchors.length - 1);
            this.anchors = a;
        }
        ++this.anchors_sp;
        this.anchors[this.anchors_sp] = this.charPosition;
    }
    
    public void popAnchorPoint() {
        --this.anchors_sp;
    }
    
    public int getIndentationWidth() {
        int n = 0;
        for (int i = 0; i < this.indents.size(); ++i) {
            final String ind = this.indents.get(i);
            if (ind != null) {
                n += ind.length();
            }
        }
        return n;
    }
    
    public int write(final String str) throws IOException {
        int n = 0;
        for (int i = 0; i < str.length(); ++i) {
            final char c = str.charAt(i);
            if (c == '\r' || c == '\n') {
                this.atStartOfLine = true;
                this.charPosition = -1;
                n += this.newline.length();
                this.out.write(this.newline);
                this.charPosition += n;
                if (c == '\r' && i + 1 < str.length() && str.charAt(i + 1) == '\n') {
                    ++i;
                }
            }
            else {
                if (this.atStartOfLine) {
                    n += this.indent();
                    this.atStartOfLine = false;
                }
                ++n;
                this.out.write(c);
                ++this.charPosition;
            }
        }
        return n;
    }
    
    public int writeSeparator(final String str) throws IOException {
        return this.write(str);
    }
    
    public int write(final String str, final String wrap) throws IOException {
        final int n = this.writeWrapSeparator(wrap);
        return n + this.write(str);
    }
    
    public int writeWrapSeparator(final String wrap) throws IOException {
        int n = 0;
        if (this.lineWidth != -1 && wrap != null && !this.atStartOfLine && this.charPosition >= this.lineWidth) {
            for (int i = 0; i < wrap.length(); ++i) {
                final char c = wrap.charAt(i);
                if (c == '\n') {
                    ++n;
                    this.out.write(c);
                    this.charPosition = 0;
                    n += this.indent();
                }
                else {
                    ++n;
                    this.out.write(c);
                    ++this.charPosition;
                }
            }
        }
        return n;
    }
    
    public int indent() throws IOException {
        int n = 0;
        for (int i = 0; i < this.indents.size(); ++i) {
            final String ind = this.indents.get(i);
            if (ind != null) {
                n += ind.length();
                this.out.write(ind);
            }
        }
        this.charPosition += n;
        return n;
    }
    
    public int indent(final int spaces) throws IOException {
        for (int i = 1; i <= spaces; ++i) {
            this.out.write(32);
        }
        this.charPosition += spaces;
        return spaces;
    }
    
    protected StringBuffer getIndentString(final int spaces) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 1; i <= spaces; ++i) {
            buf.append(' ');
        }
        return buf;
    }
}
