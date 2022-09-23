// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.BitSet;
import antlr.collections.AST;

public class MismatchedTokenException extends RecognitionException
{
    String[] tokenNames;
    public Token token;
    public AST node;
    String tokenText;
    public static final int TOKEN = 1;
    public static final int NOT_TOKEN = 2;
    public static final int RANGE = 3;
    public static final int NOT_RANGE = 4;
    public static final int SET = 5;
    public static final int NOT_SET = 6;
    public int mismatchType;
    public int expecting;
    public int upper;
    public BitSet set;
    
    public MismatchedTokenException() {
        super("Mismatched Token: expecting any AST node", "<AST>", -1, -1);
        this.tokenText = null;
    }
    
    public MismatchedTokenException(final String[] tokenNames, final AST node, final int expecting, final int upper, final boolean b) {
        super("Mismatched Token", "<AST>", (node == null) ? -1 : node.getLine(), (node == null) ? -1 : node.getColumn());
        this.tokenText = null;
        this.tokenNames = tokenNames;
        this.node = node;
        if (node == null) {
            this.tokenText = "<empty tree>";
        }
        else {
            this.tokenText = node.toString();
        }
        this.mismatchType = (b ? 4 : 3);
        this.expecting = expecting;
        this.upper = upper;
    }
    
    public MismatchedTokenException(final String[] tokenNames, final AST node, final int expecting, final boolean b) {
        super("Mismatched Token", "<AST>", (node == null) ? -1 : node.getLine(), (node == null) ? -1 : node.getColumn());
        this.tokenText = null;
        this.tokenNames = tokenNames;
        this.node = node;
        if (node == null) {
            this.tokenText = "<empty tree>";
        }
        else {
            this.tokenText = node.toString();
        }
        this.mismatchType = (b ? 2 : 1);
        this.expecting = expecting;
    }
    
    public MismatchedTokenException(final String[] tokenNames, final AST node, final BitSet set, final boolean b) {
        super("Mismatched Token", "<AST>", (node == null) ? -1 : node.getLine(), (node == null) ? -1 : node.getColumn());
        this.tokenText = null;
        this.tokenNames = tokenNames;
        this.node = node;
        if (node == null) {
            this.tokenText = "<empty tree>";
        }
        else {
            this.tokenText = node.toString();
        }
        this.mismatchType = (b ? 6 : 5);
        this.set = set;
    }
    
    public MismatchedTokenException(final String[] tokenNames, final Token token, final int expecting, final int upper, final boolean b, final String s) {
        super("Mismatched Token", s, token.getLine(), token.getColumn());
        this.tokenText = null;
        this.tokenNames = tokenNames;
        this.token = token;
        this.tokenText = token.getText();
        this.mismatchType = (b ? 4 : 3);
        this.expecting = expecting;
        this.upper = upper;
    }
    
    public MismatchedTokenException(final String[] tokenNames, final Token token, final int expecting, final boolean b, final String s) {
        super("Mismatched Token", s, token.getLine(), token.getColumn());
        this.tokenText = null;
        this.tokenNames = tokenNames;
        this.token = token;
        this.tokenText = token.getText();
        this.mismatchType = (b ? 2 : 1);
        this.expecting = expecting;
    }
    
    public MismatchedTokenException(final String[] tokenNames, final Token token, final BitSet set, final boolean b, final String s) {
        super("Mismatched Token", s, token.getLine(), token.getColumn());
        this.tokenText = null;
        this.tokenNames = tokenNames;
        this.token = token;
        this.tokenText = token.getText();
        this.mismatchType = (b ? 6 : 5);
        this.set = set;
    }
    
    public String getMessage() {
        final StringBuffer sb = new StringBuffer();
        switch (this.mismatchType) {
            case 1: {
                sb.append("expecting " + this.tokenName(this.expecting) + ", found '" + this.tokenText + "'");
                break;
            }
            case 2: {
                sb.append("expecting anything but " + this.tokenName(this.expecting) + "; got it anyway");
                break;
            }
            case 3: {
                sb.append("expecting token in range: " + this.tokenName(this.expecting) + ".." + this.tokenName(this.upper) + ", found '" + this.tokenText + "'");
                break;
            }
            case 4: {
                sb.append("expecting token NOT in range: " + this.tokenName(this.expecting) + ".." + this.tokenName(this.upper) + ", found '" + this.tokenText + "'");
                break;
            }
            case 5:
            case 6: {
                sb.append("expecting " + ((this.mismatchType == 6) ? "NOT " : "") + "one of (");
                final int[] array = this.set.toArray();
                for (int i = 0; i < array.length; ++i) {
                    sb.append(" ");
                    sb.append(this.tokenName(array[i]));
                }
                sb.append("), found '" + this.tokenText + "'");
                break;
            }
            default: {
                sb.append(super.getMessage());
                break;
            }
        }
        return sb.toString();
    }
    
    private String tokenName(final int i) {
        if (i == 0) {
            return "<Set of tokens>";
        }
        if (i < 0 || i >= this.tokenNames.length) {
            return "<" + String.valueOf(i) + ">";
        }
        return this.tokenNames[i];
    }
}
