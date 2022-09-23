// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import java.io.Serializable;

public class CommonToken implements Token, Serializable
{
    protected int type;
    protected int line;
    protected int charPositionInLine;
    protected int channel;
    protected transient CharStream input;
    protected String text;
    protected int index;
    protected int start;
    protected int stop;
    
    public CommonToken(final int type) {
        this.charPositionInLine = -1;
        this.channel = 0;
        this.index = -1;
        this.type = type;
    }
    
    public CommonToken(final CharStream input, final int type, final int channel, final int start, final int stop) {
        this.charPositionInLine = -1;
        this.channel = 0;
        this.index = -1;
        this.input = input;
        this.type = type;
        this.channel = channel;
        this.start = start;
        this.stop = stop;
    }
    
    public CommonToken(final int type, final String text) {
        this.charPositionInLine = -1;
        this.channel = 0;
        this.index = -1;
        this.type = type;
        this.channel = 0;
        this.text = text;
    }
    
    public CommonToken(final Token oldToken) {
        this.charPositionInLine = -1;
        this.channel = 0;
        this.index = -1;
        this.text = oldToken.getText();
        this.type = oldToken.getType();
        this.line = oldToken.getLine();
        this.index = oldToken.getTokenIndex();
        this.charPositionInLine = oldToken.getCharPositionInLine();
        this.channel = oldToken.getChannel();
        this.input = oldToken.getInputStream();
        if (oldToken instanceof CommonToken) {
            this.start = ((CommonToken)oldToken).start;
            this.stop = ((CommonToken)oldToken).stop;
        }
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setLine(final int line) {
        this.line = line;
    }
    
    public String getText() {
        if (this.text != null) {
            return this.text;
        }
        if (this.input == null) {
            return null;
        }
        final int n = this.input.size();
        if (this.start < n && this.stop < n) {
            return this.input.substring(this.start, this.stop);
        }
        return "<EOF>";
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public int getLine() {
        return this.line;
    }
    
    public int getCharPositionInLine() {
        return this.charPositionInLine;
    }
    
    public void setCharPositionInLine(final int charPositionInLine) {
        this.charPositionInLine = charPositionInLine;
    }
    
    public int getChannel() {
        return this.channel;
    }
    
    public void setChannel(final int channel) {
        this.channel = channel;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public int getStartIndex() {
        return this.start;
    }
    
    public void setStartIndex(final int start) {
        this.start = start;
    }
    
    public int getStopIndex() {
        return this.stop;
    }
    
    public void setStopIndex(final int stop) {
        this.stop = stop;
    }
    
    public int getTokenIndex() {
        return this.index;
    }
    
    public void setTokenIndex(final int index) {
        this.index = index;
    }
    
    public CharStream getInputStream() {
        return this.input;
    }
    
    public void setInputStream(final CharStream input) {
        this.input = input;
    }
    
    public String toString() {
        String channelStr = "";
        if (this.channel > 0) {
            channelStr = ",channel=" + this.channel;
        }
        String txt = this.getText();
        if (txt != null) {
            txt = txt.replaceAll("\n", "\\\\n");
            txt = txt.replaceAll("\r", "\\\\r");
            txt = txt.replaceAll("\t", "\\\\t");
        }
        else {
            txt = "<no text>";
        }
        return "[@" + this.getTokenIndex() + "," + this.start + ":" + this.stop + "='" + txt + "',<" + this.type + ">" + channelStr + "," + this.line + ":" + this.getCharPositionInLine() + "]";
    }
}
