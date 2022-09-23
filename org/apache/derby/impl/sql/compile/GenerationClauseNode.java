// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;
import org.apache.derby.iapi.sql.depend.ProviderList;

public class GenerationClauseNode extends ValueNode
{
    private ValueNode _generationExpression;
    private String _expressionText;
    private ValueNode _boundExpression;
    private ProviderList _apl;
    
    public void init(final Object o, final Object o2) {
        this._generationExpression = (ValueNode)o;
        this._expressionText = (String)o2;
    }
    
    public String getExpressionText() {
        return this._expressionText;
    }
    
    void setAuxiliaryProviderList(final ProviderList apl) {
        this._apl = apl;
    }
    
    public ProviderList getAuxiliaryProviderList() {
        return this._apl;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        return this._boundExpression = this._generationExpression.bindExpression(list, list2, list3);
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) throws StandardException {
        return valueNode instanceof GenerationClauseNode && this._generationExpression.isEquivalent(((GenerationClauseNode)valueNode)._generationExpression);
    }
    
    public List findReferencedColumns() throws StandardException {
        final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(ColumnReference.class);
        this._generationExpression.accept(collectNodesVisitor);
        return collectNodesVisitor.getList();
    }
    
    public String toString() {
        return "expressionText: GENERATED ALWAYS AS ( " + this._expressionText + " )\n" + super.toString();
    }
    
    public void printSubNodes(final int n) {
    }
}
