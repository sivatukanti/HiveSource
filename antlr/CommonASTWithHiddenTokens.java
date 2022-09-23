// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.AST;

public class CommonASTWithHiddenTokens extends CommonAST
{
    protected CommonHiddenStreamToken hiddenBefore;
    protected CommonHiddenStreamToken hiddenAfter;
    
    public CommonASTWithHiddenTokens() {
    }
    
    public CommonASTWithHiddenTokens(final Token token) {
        super(token);
    }
    
    public CommonHiddenStreamToken getHiddenAfter() {
        return this.hiddenAfter;
    }
    
    public CommonHiddenStreamToken getHiddenBefore() {
        return this.hiddenBefore;
    }
    
    public void initialize(final AST ast) {
        this.hiddenBefore = ((CommonASTWithHiddenTokens)ast).getHiddenBefore();
        this.hiddenAfter = ((CommonASTWithHiddenTokens)ast).getHiddenAfter();
        super.initialize(ast);
    }
    
    public void initialize(final Token token) {
        final CommonHiddenStreamToken commonHiddenStreamToken = (CommonHiddenStreamToken)token;
        super.initialize(commonHiddenStreamToken);
        this.hiddenBefore = commonHiddenStreamToken.getHiddenBefore();
        this.hiddenAfter = commonHiddenStreamToken.getHiddenAfter();
    }
}
