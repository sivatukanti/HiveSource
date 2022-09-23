// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;

public class WindowResultSetNode extends SingleChildResultSetNode
{
    FromTable parent;
    List windowFuncCalls;
    WindowDefinitionNode wdn;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) throws StandardException {
        super.init(o, null);
        this.wdn = (WindowDefinitionNode)o2;
        this.windowFuncCalls = (List)o3;
        this.setLevel((int)o4);
        this.parent = this;
        final ResultColumnList copyListAndObjects = this.childResult.getResultColumns().copyListAndObjects();
        this.resultColumns = this.childResult.getResultColumns();
        this.childResult.setResultColumns(copyListAndObjects);
        this.addNewPRNode();
        this.addNewColumns();
    }
    
    private void addNewPRNode() throws StandardException {
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        for (int size = this.resultColumns.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.resultColumns.elementAt(i);
            if (!resultColumn.isGenerated()) {
                list.addElement(resultColumn);
            }
        }
        list.copyOrderBySelect(this.resultColumns);
        this.parent = (FromTable)this.getNodeFactory().getNode(151, this, list, null, null, null, null, null, this.getContextManager());
        this.childResult.setResultColumns((ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager()));
        this.resultColumns = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(ColumnReference.class);
        this.parent.getResultColumns();
        this.parent.getResultColumns().accept(collectNodesVisitor);
        final List list2 = collectNodesVisitor.getList();
        final ArrayList list3 = new ArrayList<ColumnReference>();
        for (int j = 0; j < list2.size(); ++j) {
            final ColumnReference e = list2.get(j);
            if (!this.colRefAlreadySeen(list3, e)) {
                list3.add(e);
            }
        }
        final CollectNodesVisitor collectNodesVisitor2 = new CollectNodesVisitor(VirtualColumnNode.class);
        this.parent.getResultColumns().accept(collectNodesVisitor2);
        final List list4 = collectNodesVisitor2.getList();
        for (int k = 0; k < list4.size(); ++k) {
            list3.add(list4.get(k));
        }
        final ResultColumnList resultColumns = this.childResult.getResultColumns();
        final ResultColumnList resultColumns2 = this.resultColumns;
        for (int l = 0; l < list3.size(); ++l) {
            final ColumnReference columnReference = list3.get(l);
            final ResultColumn resultColumn2 = (ResultColumn)this.getNodeFactory().getNode(80, "##UnWindowingColumn", columnReference, this.getContextManager());
            resultColumns.addElement(resultColumn2);
            resultColumn2.markGenerated();
            resultColumn2.bindResultColumnToExpression();
            resultColumn2.setVirtualColumnId(resultColumns.size());
            final ResultColumn resultColumn3 = (ResultColumn)this.getNodeFactory().getNode(80, "##UnWindowingColumn", columnReference, this.getContextManager());
            resultColumns2.addElement(resultColumn3);
            resultColumn3.markGenerated();
            resultColumn3.bindResultColumnToExpression();
            resultColumn3.setVirtualColumnId(resultColumns2.size());
            this.parent.getResultColumns().accept(new SubstituteExpressionVisitor(columnReference, (ValueNode)this.getNodeFactory().getNode(107, this, resultColumn3, new Integer(resultColumns2.size()), this.getContextManager()), null));
        }
    }
    
    private boolean colRefAlreadySeen(final List list, final ColumnReference columnReference) throws StandardException {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).isEquivalent(columnReference)) {
                return true;
            }
        }
        return false;
    }
    
    private void addNewColumns() throws StandardException {
        this.getLanguageConnectionContext().getLanguageFactory();
        final ResultColumnList resultColumns = this.childResult.getResultColumns();
        final ResultColumnList resultColumns2 = this.resultColumns;
        this.parent.getResultColumns().accept(new ReplaceWindowFuncCallsWithCRVisitor((ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager()), ((FromTable)this.childResult).getTableNumber(), ResultSetNode.class));
        for (int i = 0; i < this.windowFuncCalls.size(); ++i) {
            final WindowFunctionNode windowFunctionNode = this.windowFuncCalls.get(i);
            if (windowFunctionNode.getWindow() == this.wdn) {
                final ResultColumn source = (ResultColumn)this.getNodeFactory().getNode(80, "##winFuncResult", windowFunctionNode.getNewNullResultExpression(), this.getContextManager());
                source.markGenerated();
                source.bindResultColumnToExpression();
                resultColumns.addElement(source);
                source.setVirtualColumnId(resultColumns.size());
                source.getVirtualColumnId();
                final ColumnReference columnReference = (ColumnReference)this.getNodeFactory().getNode(62, source.getName(), null, this.getContextManager());
                columnReference.setSource(source);
                columnReference.setNestingLevel(this.getLevel());
                columnReference.setSourceLevel(this.getLevel());
                columnReference.markGeneratedToReplaceWindowFunctionCall();
                final ResultColumn source2 = (ResultColumn)this.getNodeFactory().getNode(80, source.getColumnName(), columnReference, this.getContextManager());
                source2.markGenerated();
                source2.bindResultColumnToExpression();
                resultColumns2.addElement(source2);
                source2.setVirtualColumnId(resultColumns2.size());
                final ColumnReference generatedRef = windowFunctionNode.getGeneratedRef();
                if (generatedRef != null) {
                    generatedRef.setSource(source2);
                }
            }
        }
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        this.assignResultSetNumber();
        this.costEstimate = this.childResult.getFinalCostEstimate();
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        final int size = this.resultColumns.size();
        final FormatableBitSet set = new FormatableBitSet(size);
        for (int i = size - 1; i >= 0; --i) {
            final ResultColumn resultColumn = (ResultColumn)this.resultColumns.elementAt(i);
            final ValueNode expression = resultColumn.getExpression();
            if (!resultColumn.isGenerated() || !(expression instanceof ColumnReference) || !((ColumnReference)expression).getGeneratedToReplaceWindowFunctionCall()) {
                set.set(i);
            }
        }
        final int addItem = activationClassBuilder.addItem(set);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        this.childResult.generate(activationClassBuilder, methodBuilder);
        methodBuilder.upCast("org.apache.derby.iapi.sql.execute.NoPutResultSet");
        methodBuilder.push(activationClassBuilder.addItem(this.resultColumns.buildRowTemplate()));
        methodBuilder.push(this.resultSetNumber);
        methodBuilder.push(addItem);
        methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, "getWindowResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 8);
    }
    
    public FromTable getParent() {
        return this.parent;
    }
    
    public void printSubNodes(final int n) {
    }
}
