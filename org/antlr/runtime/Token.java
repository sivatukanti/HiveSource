// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public interface Token
{
    public static final int EOR_TOKEN_TYPE = 1;
    public static final int DOWN = 2;
    public static final int UP = 3;
    public static final int MIN_TOKEN_TYPE = 4;
    public static final int EOF = -1;
    public static final Token EOF_TOKEN = new CommonToken(-1);
    public static final int INVALID_TOKEN_TYPE = 0;
    public static final Token INVALID_TOKEN = new CommonToken(0);
    public static final Token SKIP_TOKEN = new CommonToken(0);
    public static final int DEFAULT_CHANNEL = 0;
    public static final int HIDDEN_CHANNEL = 99;
    
    String getText();
    
    void setText(final String p0);
    
    int getType();
    
    void setType(final int p0);
    
    int getLine();
    
    void setLine(final int p0);
    
    int getCharPositionInLine();
    
    void setCharPositionInLine(final int p0);
    
    int getChannel();
    
    void setChannel(final int p0);
    
    int getTokenIndex();
    
    void setTokenIndex(final int p0);
    
    CharStream getInputStream();
    
    void setInputStream(final CharStream p0);
}
