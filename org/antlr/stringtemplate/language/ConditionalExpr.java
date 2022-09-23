// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import java.io.IOException;
import antlr.RecognitionException;
import org.antlr.stringtemplate.StringTemplateWriter;
import java.util.ArrayList;
import java.util.Map;
import antlr.collections.AST;
import java.util.List;
import org.antlr.stringtemplate.StringTemplate;

public class ConditionalExpr extends ASTExpr
{
    StringTemplate subtemplate;
    List elseIfSubtemplates;
    StringTemplate elseSubtemplate;
    
    public ConditionalExpr(final StringTemplate enclosingTemplate, final AST tree) {
        super(enclosingTemplate, tree, null);
        this.subtemplate = null;
        this.elseIfSubtemplates = null;
        this.elseSubtemplate = null;
    }
    
    public void setSubtemplate(final StringTemplate subtemplate) {
        this.subtemplate = subtemplate;
    }
    
    public void addElseIfSubtemplate(final ASTExpr conditionalTree, final StringTemplate subtemplate) {
        if (this.elseIfSubtemplates == null) {
            this.elseIfSubtemplates = new ArrayList();
        }
        final ElseIfClauseData d = new ElseIfClauseData() {};
        this.elseIfSubtemplates.add(d);
    }
    
    public StringTemplate getSubtemplate() {
        return this.subtemplate;
    }
    
    public StringTemplate getElseSubtemplate() {
        return this.elseSubtemplate;
    }
    
    public void setElseSubtemplate(final StringTemplate elseSubtemplate) {
        this.elseSubtemplate = elseSubtemplate;
    }
    
    public int write(final StringTemplate self, final StringTemplateWriter out) throws IOException {
        if (this.exprTree == null || self == null || out == null) {
            return 0;
        }
        final ActionEvaluator eval = new ActionEvaluator(self, this, out);
        int n = 0;
        try {
            boolean testedTrue = false;
            final AST cond = this.exprTree.getFirstChild();
            boolean includeSubtemplate = eval.ifCondition(cond);
            if (includeSubtemplate) {
                n = this.writeSubTemplate(self, out, this.subtemplate);
                testedTrue = true;
            }
            else if (this.elseIfSubtemplates != null && this.elseIfSubtemplates.size() > 0) {
                for (int i = 0; i < this.elseIfSubtemplates.size(); ++i) {
                    final ElseIfClauseData elseIfClause = this.elseIfSubtemplates.get(i);
                    includeSubtemplate = eval.ifCondition(elseIfClause.expr.exprTree);
                    if (includeSubtemplate) {
                        this.writeSubTemplate(self, out, elseIfClause.st);
                        testedTrue = true;
                        break;
                    }
                }
            }
            if (!testedTrue && this.elseSubtemplate != null) {
                final StringTemplate s = this.elseSubtemplate.getInstanceOf();
                s.setEnclosingInstance(self);
                s.setGroup(self.getGroup());
                s.setNativeGroup(self.getNativeGroup());
                n = s.write(out);
            }
            if (!testedTrue && this.elseSubtemplate == null) {
                n = -1;
            }
        }
        catch (RecognitionException re) {
            self.error("can't evaluate tree: " + this.exprTree.toStringList(), re);
        }
        return n;
    }
    
    protected int writeSubTemplate(final StringTemplate self, final StringTemplateWriter out, final StringTemplate subtemplate) throws IOException {
        final StringTemplate s = subtemplate.getInstanceOf();
        s.setEnclosingInstance(self);
        s.setGroup(self.getGroup());
        s.setNativeGroup(self.getNativeGroup());
        return s.write(out);
    }
    
    protected static class ElseIfClauseData
    {
        ASTExpr expr;
        StringTemplate st;
    }
}
