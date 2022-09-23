// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.execute.ExecutionContext;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.catalog.types.IndexDescriptorImpl;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.catalog.IndexDescriptor;

public class IndexRowGenerator implements IndexDescriptor, Formatable
{
    private IndexDescriptor id;
    private ExecutionFactory ef;
    
    public IndexRowGenerator(final String s, final boolean b, final int[] array, final boolean[] array2, final int n) {
        this.id = new IndexDescriptorImpl(s, b, false, array, array2, n);
    }
    
    public IndexRowGenerator(final String s, final boolean b, final boolean b2, final int[] array, final boolean[] array2, final int n) {
        this.id = new IndexDescriptorImpl(s, b, b2, array, array2, n);
    }
    
    public IndexRowGenerator(final IndexDescriptor id) {
        this.id = id;
    }
    
    public ExecIndexRow getIndexRowTemplate() {
        return this.getExecutionFactory().getIndexableRow(this.id.baseColumnPositions().length + 1);
    }
    
    public ExecIndexRow getNullIndexRow(final ColumnDescriptorList list, final RowLocation rowLocation) throws StandardException {
        final int[] baseColumnPositions = this.id.baseColumnPositions();
        final ExecIndexRow indexRowTemplate = this.getIndexRowTemplate();
        for (int i = 0; i < baseColumnPositions.length; ++i) {
            indexRowTemplate.setColumn(i + 1, list.elementAt(baseColumnPositions[i] - 1).getType().getNull());
        }
        indexRowTemplate.setColumn(baseColumnPositions.length + 1, rowLocation);
        return indexRowTemplate;
    }
    
    public void getIndexRow(final ExecRow execRow, final RowLocation rowLocation, final ExecIndexRow execIndexRow, final FormatableBitSet set) throws StandardException {
        final int[] baseColumnPositions = this.id.baseColumnPositions();
        final int length = baseColumnPositions.length;
        if (set == null) {
            for (int i = 0; i < length; ++i) {
                execIndexRow.setColumn(i + 1, execRow.getColumn(baseColumnPositions[i]));
            }
        }
        else {
            for (int j = 0; j < length; ++j) {
                final int n = baseColumnPositions[j];
                int n2 = 0;
                for (int k = 1; k <= n; ++k) {
                    if (set.get(k)) {
                        ++n2;
                    }
                }
                execIndexRow.setColumn(j + 1, execRow.getColumn(n2));
            }
        }
        execIndexRow.setColumn(length + 1, rowLocation);
    }
    
    public int[] getColumnCollationIds(final ColumnDescriptorList list) throws StandardException {
        final int[] baseColumnPositions = this.id.baseColumnPositions();
        final int[] array = new int[baseColumnPositions.length + 1];
        for (int i = 0; i < baseColumnPositions.length; ++i) {
            array[i] = list.elementAt(baseColumnPositions[i] - 1).getType().getCollationType();
        }
        array[array.length - 1] = 0;
        return array;
    }
    
    public IndexDescriptor getIndexDescriptor() {
        return this.id;
    }
    
    public IndexRowGenerator() {
    }
    
    public boolean isUniqueWithDuplicateNulls() {
        return this.id.isUniqueWithDuplicateNulls();
    }
    
    public boolean isUnique() {
        return this.id.isUnique();
    }
    
    public int[] baseColumnPositions() {
        return this.id.baseColumnPositions();
    }
    
    public int getKeyColumnPosition(final int n) {
        return this.id.getKeyColumnPosition(n);
    }
    
    public int numberOfOrderedColumns() {
        return this.id.numberOfOrderedColumns();
    }
    
    public String indexType() {
        return this.id.indexType();
    }
    
    public String toString() {
        return this.id.toString();
    }
    
    public boolean isAscending(final Integer n) {
        return this.id.isAscending(n);
    }
    
    public boolean isDescending(final Integer n) {
        return this.id.isDescending(n);
    }
    
    public boolean[] isAscending() {
        return this.id.isAscending();
    }
    
    public void setBaseColumnPositions(final int[] baseColumnPositions) {
        this.id.setBaseColumnPositions(baseColumnPositions);
    }
    
    public void setIsAscending(final boolean[] isAscending) {
        this.id.setIsAscending(isAscending);
    }
    
    public void setNumberOfOrderedColumns(final int numberOfOrderedColumns) {
        this.id.setNumberOfOrderedColumns(numberOfOrderedColumns);
    }
    
    public boolean equals(final Object obj) {
        return this.id.equals(obj);
    }
    
    public int hashCode() {
        return this.id.hashCode();
    }
    
    private ExecutionFactory getExecutionFactory() {
        if (this.ef == null) {
            this.ef = ((ExecutionContext)ContextService.getContext("ExecutionContext")).getExecutionFactory();
        }
        return this.ef;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.id = (IndexDescriptor)objectInput.readObject();
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.id);
    }
    
    public int getTypeFormatId() {
        return 268;
    }
}
