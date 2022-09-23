// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.sql.depend.DependencyManager;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Dependent;
import org.apache.derby.iapi.sql.depend.Provider;

public abstract class ConstraintDescriptor extends TupleDescriptor implements UniqueTupleDescriptor, Provider, Dependent
{
    public static final int ENABLED = 1;
    public static final int DISABLED = 2;
    public static final int ALL = 3;
    public static final int SYSCONSTRAINTS_STATE_FIELD = 6;
    TableDescriptor table;
    final String constraintName;
    private final boolean deferrable;
    private final boolean initiallyDeferred;
    boolean isEnabled;
    private final int[] referencedColumns;
    final UUID constraintId;
    private final SchemaDescriptor schemaDesc;
    private ColumnDescriptorList colDL;
    
    ConstraintDescriptor(final DataDictionary dataDictionary, final TableDescriptor table, final String constraintName, final boolean deferrable, final boolean initiallyDeferred, final int[] referencedColumns, final UUID constraintId, final SchemaDescriptor schemaDesc, final boolean isEnabled) {
        super(dataDictionary);
        this.table = table;
        this.constraintName = constraintName;
        this.deferrable = deferrable;
        this.initiallyDeferred = initiallyDeferred;
        this.referencedColumns = referencedColumns;
        this.constraintId = constraintId;
        this.schemaDesc = schemaDesc;
        this.isEnabled = isEnabled;
    }
    
    public UUID getTableId() {
        return this.table.getUUID();
    }
    
    public UUID getUUID() {
        return this.constraintId;
    }
    
    public String getConstraintName() {
        return this.constraintName;
    }
    
    public abstract int getConstraintType();
    
    public abstract UUID getConglomerateId();
    
    public String getConstraintText() {
        return null;
    }
    
    public boolean deferrable() {
        return this.deferrable;
    }
    
    public boolean initiallyDeferred() {
        return this.initiallyDeferred;
    }
    
    public int[] getReferencedColumns() {
        return this.referencedColumns;
    }
    
    public abstract boolean hasBackingIndex();
    
    public SchemaDescriptor getSchemaDescriptor() {
        return this.schemaDesc;
    }
    
    public int[] getKeyColumns() {
        return this.getReferencedColumns();
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public void setEnabled() {
        this.isEnabled = true;
    }
    
    public void setDisabled() {
        this.isEnabled = false;
    }
    
    public boolean isReferenced() {
        return false;
    }
    
    public int getReferenceCount() {
        return 0;
    }
    
    public abstract boolean needsToFire(final int p0, final int[] p1);
    
    public TableDescriptor getTableDescriptor() {
        return this.table;
    }
    
    public ColumnDescriptorList getColumnDescriptors() throws StandardException {
        if (this.colDL == null) {
            this.getDataDictionary();
            this.colDL = new ColumnDescriptorList();
            final int[] referencedColumns = this.getReferencedColumns();
            for (int i = 0; i < referencedColumns.length; ++i) {
                this.colDL.add(this.table.getColumnDescriptor(referencedColumns[i]));
            }
        }
        return this.colDL;
    }
    
    public boolean areColumnsComparable(final ColumnDescriptorList list) throws StandardException {
        final ColumnDescriptorList columnDescriptors = this.getColumnDescriptors();
        if (list.size() != columnDescriptors.size()) {
            return false;
        }
        int size;
        int size2;
        int n;
        for (size = columnDescriptors.size(), size2 = list.size(), n = 0; n < size && n < size2 && columnDescriptors.elementAt(n).getType().isExactTypeAndLengthMatch(list.elementAt(n).getType()); ++n) {}
        return n == size && n == size2;
    }
    
    public boolean columnIntersects(final int[] array) {
        return doColumnsIntersect(this.getReferencedColumns(), array);
    }
    
    static boolean doColumnsIntersect(final int[] array, final int[] array2) {
        if (array == null || array2 == null) {
            return true;
        }
        for (int i = 0; i < array2.length; ++i) {
            for (int j = 0; j < array.length; ++j) {
                if (array2[i] == array[j]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String toString() {
        return "";
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(208);
    }
    
    public String getObjectName() {
        return this.constraintName;
    }
    
    public UUID getObjectID() {
        return this.constraintId;
    }
    
    public String getClassType() {
        return "Constraint";
    }
    
    public synchronized boolean isValid() {
        return true;
    }
    
    public void prepareToInvalidate(final Provider provider, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        final DependencyManager dependencyManager = this.getDataDictionary().getDependencyManager();
        switch (n) {
            case 20:
            case 21:
            case 23:
            case 29:
            case 30:
            case 44:
            case 47:
            case 48: {}
            default: {
                throw StandardException.newException("X0Y25.S", dependencyManager.getActionString(n), provider.getObjectName(), "CONSTRAINT", this.constraintName);
            }
        }
    }
    
    public void makeInvalid(final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        if (n == 44 || n == 47) {
            final ConglomerateDescriptor drop = this.drop(languageConnectionContext, true);
            languageConnectionContext.getLastActivation().addWarning(StandardException.newWarning("01500", this.getConstraintName(), this.getTableDescriptor().getName()));
            if (drop != null) {}
            return;
        }
        if (n == 21 || n == 20 || n == 29 || n == 30 || n == 23 || n != 48) {}
    }
    
    public ConglomerateDescriptor drop(final LanguageConnectionContext languageConnectionContext, final boolean b) throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        final TransactionController transactionExecute = languageConnectionContext.getTransactionExecute();
        if (b) {
            dataDictionary.getDependencyManager().clearDependencies(languageConnectionContext, this);
        }
        dataDictionary.dropConstraintDescriptor(this, transactionExecute);
        ConglomerateDescriptor drop = null;
        if (this.hasBackingIndex()) {
            final ConglomerateDescriptor[] conglomerateDescriptors = dataDictionary.getConglomerateDescriptors(this.getConglomerateId());
            if (conglomerateDescriptors.length != 0) {
                for (int i = 0; i < conglomerateDescriptors.length; ++i) {
                    if (conglomerateDescriptors[i].isConstraint()) {
                        drop = conglomerateDescriptors[i].drop(languageConnectionContext, this.table);
                        break;
                    }
                }
            }
        }
        this.table.removeConstraintDescriptor(this);
        return drop;
    }
    
    public String getDescriptorName() {
        return this.constraintName;
    }
    
    public String getDescriptorType() {
        return "Constraint";
    }
}
