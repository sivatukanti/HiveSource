// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public class ClassicToken implements Token
{
    protected String text;
    protected int type;
    protected int line;
    protected int charPositionInLine;
    protected int channel;
    protected int index;
    
    public ClassicToken(final int type) {
        this.channel = 0;
        this.type = type;
    }
    
    public ClassicToken(final Token oldToken) {
        this.channel = 0;
        this.text = oldToken.getText();
        this.type = oldToken.getType();
        this.line = oldToken.getLine();
        this.charPositionInLine = oldToken.getCharPositionInLine();
        this.channel = oldToken.getChannel();
    }
    
    public ClassicToken(final int type, final String text) {
        this.channel = 0;
        this.type = type;
        this.text = text;
    }
    
    public ClassicToken(final int type, final String text, final int channel) {
        this.channel = 0;
        this.type = type;
        this.text = text;
        this.channel = channel;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setLine(final int line) {
        this.line = line;
    }
    
    public String getText() {
        return this.text;
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
    
    public int getTokenIndex() {
        return this.index;
    }
    
    public void setTokenIndex(final int index) {
        this.index = index;
    }
    
    public CharStream getInputStream() {
        return null;
    }
    
    public void setInputStream(final CharStream input) {
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
        return "[@" + this.getTokenIndex() + ",'" + txt + "',<" + this.type + ">" + channelStr + "," + this.line + ":" + this.getCharPositionInLine() + "]";
    }
}
