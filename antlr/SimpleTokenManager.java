// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.util.Enumeration;
import java.util.Hashtable;
import antlr.collections.impl.Vector;

class SimpleTokenManager implements TokenManager, Cloneable
{
    protected int maxToken;
    protected Vector vocabulary;
    private Hashtable table;
    protected Tool antlrTool;
    protected String name;
    protected boolean readOnly;
    
    SimpleTokenManager(final String name, final Tool antlrTool) {
        this.maxToken = 4;
        this.readOnly = false;
        this.antlrTool = antlrTool;
        this.name = name;
        this.vocabulary = new Vector(1);
        this.table = new Hashtable();
        final TokenSymbol tokenSymbol = new TokenSymbol("EOF");
        tokenSymbol.setTokenType(1);
        this.define(tokenSymbol);
        this.vocabulary.ensureCapacity(3);
        this.vocabulary.setElementAt("NULL_TREE_LOOKAHEAD", 3);
    }
    
    public Object clone() {
        SimpleTokenManager simpleTokenManager;
        try {
            simpleTokenManager = (SimpleTokenManager)super.clone();
            simpleTokenManager.vocabulary = (Vector)this.vocabulary.clone();
            simpleTokenManager.table = (Hashtable)this.table.clone();
            simpleTokenManager.maxToken = this.maxToken;
            simpleTokenManager.antlrTool = this.antlrTool;
            simpleTokenManager.name = this.name;
        }
        catch (CloneNotSupportedException ex) {
            this.antlrTool.panic("cannot clone token manager");
            return null;
        }
        return simpleTokenManager;
    }
    
    public void define(final TokenSymbol tokenSymbol) {
        this.vocabulary.ensureCapacity(tokenSymbol.getTokenType());
        this.vocabulary.setElementAt(tokenSymbol.getId(), tokenSymbol.getTokenType());
        this.mapToTokenSymbol(tokenSymbol.getId(), tokenSymbol);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getTokenStringAt(final int n) {
        return (String)this.vocabulary.elementAt(n);
    }
    
    public TokenSymbol getTokenSymbol(final String key) {
        return this.table.get(key);
    }
    
    public TokenSymbol getTokenSymbolAt(final int n) {
        return this.getTokenSymbol(this.getTokenStringAt(n));
    }
    
    public Enumeration getTokenSymbolElements() {
        return this.table.elements();
    }
    
    public Enumeration getTokenSymbolKeys() {
        return this.table.keys();
    }
    
    public Vector getVocabulary() {
        return this.vocabulary;
    }
    
    public boolean isReadOnly() {
        return false;
    }
    
    public void mapToTokenSymbol(final String key, final TokenSymbol value) {
        this.table.put(key, value);
    }
    
    public int maxTokenType() {
        return this.maxToken - 1;
    }
    
    public int nextTokenType() {
        return this.maxToken++;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    public boolean tokenDefined(final String key) {
        return this.table.containsKey(key);
    }
}
