// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.catalog.types.ReferencedColumnsDescriptorImpl;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import java.util.List;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.AccessPath;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;

public class IndexToBaseRowNode extends FromTable
{
    protected FromBaseTable source;
    protected ConglomerateDescriptor baseCD;
    protected boolean cursorTargetTable;
    protected PredicateList restrictionList;
    protected boolean forUpdate;
    private FormatableBitSet heapReferencedCols;
    private FormatableBitSet indexReferencedCols;
    private FormatableBitSet allReferencedCols;
    private FormatableBitSet heapOnlyReferencedCols;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8, final Object o9) {
        super.init(null, o9);
        this.source = (FromBaseTable)o;
        this.baseCD = (ConglomerateDescriptor)o2;
        this.resultColumns = (ResultColumnList)o3;
        this.cursorTargetTable = (boolean)o4;
        this.restrictionList = (PredicateList)o7;
        this.forUpdate = (boolean)o8;
        this.heapReferencedCols = (FormatableBitSet)o5;
        this.indexReferencedCols = (FormatableBitSet)o6;
        if (this.indexReferencedCols == null) {
            this.allReferencedCols = this.heapReferencedCols;
            this.heapOnlyReferencedCols = this.heapReferencedCols;
        }
        else {
            (this.allReferencedCols = new FormatableBitSet(this.heapReferencedCols)).or(this.indexReferencedCols);
            (this.heapOnlyReferencedCols = new FormatableBitSet(this.allReferencedCols)).xor(this.indexReferencedCols);
        }
    }
    
    public boolean forUpdate() {
        return this.source.forUpdate();
    }
    
    public AccessPath getTrulyTheBestAccessPath() {
        return this.source.getTrulyTheBestAccessPath();
    }
    
    public CostEstimate getCostEstimate() {
        return this.source.getTrulyTheBestAccessPath().getCostEstimate();
    }
    
    public CostEstimate getFinalCostEstimate() {
        return this.source.getFinalCostEstimate();
    }
    
    boolean isOrderedOn(final ColumnReference[] array, final boolean b, final List list) throws StandardException {
        return this.source.isOrderedOn(array, b, list);
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        ValueNode restorePredicates = null;
        this.assignResultSetNumber();
        this.costEstimate = this.getFinalCostEstimate();
        if (this.restrictionList != null) {
            restorePredicates = this.restrictionList.restorePredicates();
            this.restrictionList = null;
        }
        int addItem = -1;
        if (this.heapReferencedCols != null) {
            addItem = activationClassBuilder.addItem(this.heapReferencedCols);
        }
        int addItem2 = -1;
        if (this.allReferencedCols != null) {
            addItem2 = activationClassBuilder.addItem(this.allReferencedCols);
        }
        int addItem3 = -1;
        if (this.heapOnlyReferencedCols != null) {
            addItem3 = activationClassBuilder.addItem(this.heapOnlyReferencedCols);
        }
        final int addItem4 = activationClassBuilder.addItem(new ReferencedColumnsDescriptorImpl(this.getIndexColMapping()));
        final long conglomerateNumber = this.baseCD.getConglomerateNumber();
        final StaticCompiledOpenConglomInfo staticCompiledConglomInfo = this.getLanguageConnectionContext().getTransactionCompile().getStaticCompiledConglomInfo(conglomerateNumber);
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        methodBuilder.push(conglomerateNumber);
        methodBuilder.push(activationClassBuilder.addItem(staticCompiledConglomInfo));
        this.source.generate(activationClassBuilder, methodBuilder);
        methodBuilder.upCast("org.apache.derby.iapi.sql.execute.NoPutResultSet");
        methodBuilder.push(activationClassBuilder.addItem(this.resultColumns.buildRowTemplate(this.heapReferencedCols, this.indexReferencedCols != null && this.indexReferencedCols.getNumBitsSet() != 0)));
        methodBuilder.push(this.resultSetNumber);
        methodBuilder.push(this.source.getBaseTableName());
        methodBuilder.push(addItem);
        methodBuilder.push(addItem2);
        methodBuilder.push(addItem3);
        methodBuilder.push(addItem4);
        if (restorePredicates == null) {
            methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
        }
        else {
            final MethodBuilder userExprFun = activationClassBuilder.newUserExprFun();
            restorePredicates.generate(activationClassBuilder, userExprFun);
            userExprFun.methodReturn();
            userExprFun.complete();
            activationClassBuilder.pushMethodReference(methodBuilder, userExprFun);
        }
        methodBuilder.push(this.forUpdate);
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.callMethod((short)185, null, "getIndexRowToBaseRowResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", 14);
        if (this.cursorTargetTable) {
            activationClassBuilder.rememberCursorTarget(methodBuilder);
        }
    }
    
    public boolean isOneRowResultSet() throws StandardException {
        return this.source.isOneRowResultSet();
    }
    
    public boolean isNotExists() {
        return this.source.isNotExists();
    }
    
    void decrementLevel(final int n) {
        this.source.decrementLevel(n);
    }
    
    public int updateTargetLockMode() {
        return this.source.updateTargetLockMode();
    }
    
    void adjustForSortElimination() {
        this.source.disableBulkFetch();
    }
    
    void adjustForSortElimination(final RequiredRowOrdering requiredRowOrdering) throws StandardException {
        this.adjustForSortElimination();
        this.source.adjustForSortElimination(requiredRowOrdering);
    }
    
    private int[] getIndexColMapping() {
        final int size = this.resultColumns.size();
        final int[] array = new int[size];
        for (int i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.resultColumns.elementAt(i);
            if (this.indexReferencedCols != null && resultColumn.getExpression() instanceof VirtualColumnNode) {
                array[i] = ((VirtualColumnNode)resultColumn.getExpression()).getSourceColumn().getVirtualColumnId() - 1;
            }
            else {
                array[i] = -1;
            }
        }
        return array;
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.source != null) {
            this.source = (FromBaseTable)this.source.accept(visitor);
        }
    }
}
