// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public abstract class WindowFunctionNode extends UnaryOperatorNode
{
    private WindowNode window;
    private ResultColumn generatedRC;
    private ColumnReference generatedRef;
    
    public void init(final Object o, final Object o2, final Object o3) {
        super.init(o, o2, null);
        this.window = (WindowNode)o3;
    }
    
    public boolean isConstantExpression() {
        return false;
    }
    
    public boolean constantExpression(final PredicateList list) {
        return false;
    }
    
    public WindowNode getWindow() {
        return this.window;
    }
    
    public void setWindow(final WindowDefinitionNode window) {
        this.window = window;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        if (this.window instanceof WindowReferenceNode) {
            final WindowDefinitionNode definedWindow = this.definedWindow(list.getWindows(), this.window.getName());
            if (definedWindow == null) {
                throw StandardException.newException("42ZC0", this.window.getName());
            }
            this.window = definedWindow;
        }
        return this;
    }
    
    private WindowDefinitionNode definedWindow(final WindowList list, final String anObject) {
        for (int i = 0; i < list.size(); ++i) {
            final WindowDefinitionNode windowDefinitionNode = (WindowDefinitionNode)list.elementAt(i);
            if (windowDefinitionNode.getName().equals(anObject)) {
                return windowDefinitionNode;
            }
        }
        return null;
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ValueNode replaceCallsWithColumnReferences(final ResultColumnList list, final int tableNumber) throws StandardException {
        if (this.generatedRef == null) {
            (this.generatedRC = (ResultColumn)this.getNodeFactory().getNode(80, "SQLCol" + this.getCompilerContext().getNextColumnNumber(), this, this.getContextManager())).markGenerated();
            (this.generatedRef = (ColumnReference)this.getNodeFactory().getNode(62, this.generatedRC.getName(), null, this.getContextManager())).setSource(this.generatedRC);
            this.generatedRef.setNestingLevel(0);
            this.generatedRef.setSourceLevel(0);
            if (tableNumber != -1) {
                this.generatedRef.setTableNumber(tableNumber);
            }
            list.addResultColumn(this.generatedRC);
            this.generatedRef.markGeneratedToReplaceWindowFunctionCall();
        }
        else {
            list.addResultColumn(this.generatedRC);
        }
        return this.generatedRef;
    }
    
    public ColumnReference getGeneratedRef() {
        return this.generatedRef;
    }
    
    public ValueNode getNewNullResultExpression() throws StandardException {
        return this.getNullNode(this.getTypeServices());
    }
}
