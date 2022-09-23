// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.key;

import java.util.Collection;
import java.util.List;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.metadata.ForeignKeyAction;
import org.datanucleus.metadata.ForeignKeyMetaData;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.Table;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;

public class ForeignKey extends Key
{
    private DatastoreAdapter dba;
    private boolean initiallyDeferred;
    private DatastoreClass refTable;
    private FKAction updateAction;
    private FKAction deleteAction;
    private ArrayList refColumns;
    private String foreignKeyDefinition;
    
    public ForeignKey(final boolean initiallyDeferred) {
        super(null);
        this.refColumns = new ArrayList();
        this.foreignKeyDefinition = null;
        this.initiallyDeferred = initiallyDeferred;
        this.refTable = null;
        this.dba = null;
    }
    
    public ForeignKey(final JavaTypeMapping mapping, final DatastoreAdapter dba, final DatastoreClass refTable, final boolean initiallyDeferred) {
        super(mapping.getTable());
        this.refColumns = new ArrayList();
        this.foreignKeyDefinition = null;
        this.initiallyDeferred = initiallyDeferred;
        this.refTable = refTable;
        this.dba = dba;
        if (refTable.getIdMapping() == null) {
            throw new NucleusException("ForeignKey ID mapping is not initilized for " + mapping + ". Table referenced: " + refTable.toString()).setFatal();
        }
        for (int i = 0; i < refTable.getIdMapping().getNumberOfDatastoreMappings(); ++i) {
            this.setColumn(i, mapping.getDatastoreMapping(i).getColumn(), refTable.getIdMapping().getDatastoreMapping(i).getColumn());
        }
    }
    
    public void setForMetaData(final ForeignKeyMetaData fkmd) {
        if (fkmd == null) {
            return;
        }
        if (fkmd.getFkDefinitionApplies() && fkmd.getFkDefinition() != null) {
            this.foreignKeyDefinition = fkmd.getFkDefinition();
            this.name = fkmd.getName();
            this.refColumns = null;
            this.updateAction = null;
            this.deleteAction = null;
            this.refTable = null;
            this.refColumns = null;
        }
        else {
            if (fkmd.getName() != null) {
                this.setName(fkmd.getName());
            }
            final ForeignKeyAction deleteAction = fkmd.getDeleteAction();
            if (deleteAction != null) {
                if (deleteAction.equals(ForeignKeyAction.CASCADE)) {
                    this.setDeleteAction(FKAction.CASCADE);
                }
                else if (deleteAction.equals(ForeignKeyAction.RESTRICT)) {
                    this.setDeleteAction(FKAction.RESTRICT);
                }
                else if (deleteAction.equals(ForeignKeyAction.NULL)) {
                    this.setDeleteAction(FKAction.NULL);
                }
                else if (deleteAction.equals(ForeignKeyAction.DEFAULT)) {
                    this.setDeleteAction(FKAction.DEFAULT);
                }
            }
            final ForeignKeyAction updateAction = fkmd.getUpdateAction();
            if (updateAction != null) {
                if (updateAction.equals(ForeignKeyAction.CASCADE)) {
                    this.setUpdateAction(FKAction.CASCADE);
                }
                else if (updateAction.equals(ForeignKeyAction.RESTRICT)) {
                    this.setUpdateAction(FKAction.RESTRICT);
                }
                else if (updateAction.equals(ForeignKeyAction.NULL)) {
                    this.setUpdateAction(FKAction.NULL);
                }
                else if (updateAction.equals(ForeignKeyAction.DEFAULT)) {
                    this.setUpdateAction(FKAction.DEFAULT);
                }
            }
            if (fkmd.isDeferred()) {
                this.initiallyDeferred = true;
            }
        }
    }
    
    public void setDeleteAction(final FKAction deleteAction) {
        this.deleteAction = deleteAction;
    }
    
    public void setUpdateAction(final FKAction updateAction) {
        this.updateAction = updateAction;
    }
    
    public void addColumn(final Column col, final Column refCol) {
        this.setColumn(this.columns.size(), col, refCol);
    }
    
    public void setColumn(final int seq, final Column col, final Column refCol) {
        if (this.table == null) {
            this.table = col.getTable();
            this.refTable = (DatastoreClass)refCol.getTable();
            this.dba = this.table.getStoreManager().getDatastoreAdapter();
        }
        else {
            if (!this.table.equals(col.getTable())) {
                throw new NucleusException("Cannot add " + col + " as FK column for " + this.table).setFatal();
            }
            if (!this.refTable.equals(refCol.getTable())) {
                throw new NucleusException("Cannot add " + refCol + " as referenced FK column for " + this.refTable).setFatal();
            }
        }
        Key.setMinSize(this.columns, seq + 1);
        Key.setMinSize(this.refColumns, seq + 1);
        this.columns.set(seq, col);
        this.refColumns.set(seq, refCol);
    }
    
    @Override
    public int hashCode() {
        if (this.foreignKeyDefinition != null) {
            return this.foreignKeyDefinition.hashCode();
        }
        return super.hashCode() ^ this.refColumns.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ForeignKey)) {
            return false;
        }
        final ForeignKey fk = (ForeignKey)obj;
        return (this.refColumns == null || this.refColumns.equals(fk.refColumns)) && super.equals(obj);
    }
    
    @Override
    public String toString() {
        if (this.foreignKeyDefinition != null) {
            return this.foreignKeyDefinition;
        }
        final StringBuffer s = new StringBuffer("FOREIGN KEY ");
        s.append(Key.getColumnList(this.columns));
        if (this.refTable != null) {
            s.append(" REFERENCES ");
            s.append(this.refTable.toString());
            s.append(" ").append(Key.getColumnList(this.refColumns));
        }
        if (this.deleteAction != null && ((this.deleteAction == FKAction.CASCADE && this.dba.supportsOption("FkDeleteActionCascade")) || (this.deleteAction == FKAction.RESTRICT && this.dba.supportsOption("FkDeleteActionRestrict")) || (this.deleteAction == FKAction.NULL && this.dba.supportsOption("FkDeleteActionNull")) || (this.deleteAction == FKAction.DEFAULT && this.dba.supportsOption("FkDeleteActionDefault")))) {
            s.append(" ON DELETE ").append(this.deleteAction.toString());
        }
        if (this.updateAction != null && ((this.updateAction == FKAction.CASCADE && this.dba.supportsOption("FkUpdateActionCascade")) || (this.updateAction == FKAction.RESTRICT && this.dba.supportsOption("FkUpdateActionRestrict")) || (this.updateAction == FKAction.NULL && this.dba.supportsOption("FkUpdateActionNull")) || (this.updateAction == FKAction.DEFAULT && this.dba.supportsOption("FkUpdateActionDefault")))) {
            s.append(" ON UPDATE ").append(this.updateAction.toString());
        }
        if (this.initiallyDeferred && this.dba.supportsOption("DeferredConstraints")) {
            s.append(" INITIALLY DEFERRED");
        }
        s.append(" ");
        return s.toString();
    }
    
    public enum FKAction
    {
        CASCADE("CASCADE"), 
        RESTRICT("RESTRICT"), 
        NULL("SET NULL"), 
        DEFAULT("SET DEFAULT");
        
        String keyword;
        
        private FKAction(final String word) {
            this.keyword = word;
        }
        
        @Override
        public String toString() {
            return this.keyword;
        }
    }
}
