// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.sql.Timestamp;
import org.apache.derby.catalog.types.ReferencedColumnsDescriptorImpl;
import org.apache.derby.catalog.ReferencedColumns;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.uuid.UUIDFactory;

public class DataDescriptorGenerator
{
    private UUIDFactory uuidf;
    protected final DataDictionary dataDictionary;
    
    public DataDescriptorGenerator(final DataDictionary dataDictionary) {
        this.dataDictionary = dataDictionary;
    }
    
    public SchemaDescriptor newSchemaDescriptor(final String s, final String s2, final UUID uuid) throws StandardException {
        return new SchemaDescriptor(this.dataDictionary, s, s2, uuid, this.dataDictionary.isSystemSchemaName(s));
    }
    
    public TableDescriptor newTableDescriptor(final String s, final SchemaDescriptor schemaDescriptor, final int n, final char c) {
        return new TableDescriptor(this.dataDictionary, s, schemaDescriptor, n, c);
    }
    
    public TableDescriptor newTableDescriptor(final String s, final SchemaDescriptor schemaDescriptor, final int n, final boolean b, final boolean b2) {
        return new TableDescriptor(this.dataDictionary, s, schemaDescriptor, n, b, b2);
    }
    
    public ViewDescriptor newViewDescriptor(final UUID uuid, final String s, final String s2, final int n, final UUID uuid2) {
        return new ViewDescriptor(this.dataDictionary, uuid, s, s2, n, uuid2);
    }
    
    public ReferencedKeyConstraintDescriptor newUniqueConstraintDescriptor(final TableDescriptor tableDescriptor, final String s, final boolean b, final boolean b2, final int[] array, final UUID uuid, final UUID uuid2, final SchemaDescriptor schemaDescriptor, final boolean b3, final int n) {
        return new ReferencedKeyConstraintDescriptor(3, this.dataDictionary, tableDescriptor, s, b, b2, array, uuid, uuid2, schemaDescriptor, b3, n);
    }
    
    public ReferencedKeyConstraintDescriptor newPrimaryKeyConstraintDescriptor(final TableDescriptor tableDescriptor, final String s, final boolean b, final boolean b2, final int[] array, final UUID uuid, final UUID uuid2, final SchemaDescriptor schemaDescriptor, final boolean b3, final int n) {
        return new ReferencedKeyConstraintDescriptor(2, this.dataDictionary, tableDescriptor, s, b, b2, array, uuid, uuid2, schemaDescriptor, b3, n);
    }
    
    public ForeignKeyConstraintDescriptor newForeignKeyConstraintDescriptor(final TableDescriptor tableDescriptor, final String s, final boolean b, final boolean b2, final int[] array, final UUID uuid, final UUID uuid2, final SchemaDescriptor schemaDescriptor, final ReferencedKeyConstraintDescriptor referencedKeyConstraintDescriptor, final boolean b3, final int n, final int n2) {
        return new ForeignKeyConstraintDescriptor(this.dataDictionary, tableDescriptor, s, b, b2, array, uuid, uuid2, schemaDescriptor, referencedKeyConstraintDescriptor, b3, n, n2);
    }
    
    public ForeignKeyConstraintDescriptor newForeignKeyConstraintDescriptor(final TableDescriptor tableDescriptor, final String s, final boolean b, final boolean b2, final int[] array, final UUID uuid, final UUID uuid2, final SchemaDescriptor schemaDescriptor, final UUID uuid3, final boolean b3, final int n, final int n2) {
        return new ForeignKeyConstraintDescriptor(this.dataDictionary, tableDescriptor, s, b, b2, array, uuid, uuid2, schemaDescriptor, uuid3, b3, n, n2);
    }
    
    public CheckConstraintDescriptor newCheckConstraintDescriptor(final TableDescriptor tableDescriptor, final String s, final boolean b, final boolean b2, final UUID uuid, final String s2, final ReferencedColumns referencedColumns, final SchemaDescriptor schemaDescriptor, final boolean b3) {
        return new CheckConstraintDescriptor(this.dataDictionary, tableDescriptor, s, b, b2, uuid, s2, referencedColumns, schemaDescriptor, b3);
    }
    
    public CheckConstraintDescriptor newCheckConstraintDescriptor(final TableDescriptor tableDescriptor, final String s, final boolean b, final boolean b2, final UUID uuid, final String s2, final int[] array, final SchemaDescriptor schemaDescriptor, final boolean b3) {
        return new CheckConstraintDescriptor(this.dataDictionary, tableDescriptor, s, b, b2, uuid, s2, new ReferencedColumnsDescriptorImpl(array), schemaDescriptor, b3);
    }
    
    public ConglomerateDescriptor newConglomerateDescriptor(final long n, final String s, final boolean b, final IndexRowGenerator indexRowGenerator, final boolean b2, final UUID uuid, final UUID uuid2, final UUID uuid3) {
        return new ConglomerateDescriptor(this.dataDictionary, n, s, b, indexRowGenerator, b2, uuid, uuid2, uuid3);
    }
    
    public TriggerDescriptor newTriggerDescriptor(final SchemaDescriptor schemaDescriptor, final UUID uuid, final String s, final int n, final boolean b, final boolean b2, final boolean b3, final TableDescriptor tableDescriptor, final UUID uuid2, final UUID uuid3, final Timestamp timestamp, final int[] array, final int[] array2, final String s2, final boolean b4, final boolean b5, final String s3, final String s4) throws StandardException {
        return new TriggerDescriptor(this.dataDictionary, schemaDescriptor, uuid, s, n, b, b2, b3, tableDescriptor, uuid2, uuid3, timestamp, array, array2, s2, b4, b5, s3, s4);
    }
    
    protected UUIDFactory getUUIDFactory() {
        if (this.uuidf == null) {
            this.uuidf = Monitor.getMonitor().getUUIDFactory();
        }
        return this.uuidf;
    }
    
    public FileInfoDescriptor newFileInfoDescriptor(final UUID uuid, final SchemaDescriptor schemaDescriptor, final String s, final long n) {
        return new FileInfoDescriptor(this.dataDictionary, uuid, schemaDescriptor, s, n);
    }
    
    public UserDescriptor newUserDescriptor(final String s, final String s2, final char[] array, final Timestamp timestamp) {
        return new UserDescriptor(this.dataDictionary, s, s2, array, timestamp);
    }
    
    public TablePermsDescriptor newTablePermsDescriptor(final TableDescriptor tableDescriptor, final String anObject, final String anObject2, final String anObject3, final String anObject4, final String anObject5, final String anObject6, final String s) throws StandardException {
        if ("N".equals(anObject) && "N".equals(anObject2) && "N".equals(anObject3) && "N".equals(anObject4) && "N".equals(anObject5) && "N".equals(anObject6)) {
            return null;
        }
        return new TablePermsDescriptor(this.dataDictionary, null, s, tableDescriptor.getUUID(), anObject, anObject2, anObject3, anObject4, anObject5, anObject6);
    }
    
    public ColPermsDescriptor newColPermsDescriptor(final TableDescriptor tableDescriptor, final String s, final FormatableBitSet set, final String s2) throws StandardException {
        return new ColPermsDescriptor(this.dataDictionary, null, s2, tableDescriptor.getUUID(), s, set);
    }
    
    public RoutinePermsDescriptor newRoutinePermsDescriptor(final AliasDescriptor aliasDescriptor, final String s) throws StandardException {
        return new RoutinePermsDescriptor(this.dataDictionary, null, s, aliasDescriptor.getUUID());
    }
    
    public RoleGrantDescriptor newRoleGrantDescriptor(final UUID uuid, final String s, final String s2, final String s3, final boolean b, final boolean b2) throws StandardException {
        return new RoleGrantDescriptor(this.dataDictionary, uuid, s, s2, s3, b, b2);
    }
    
    public SequenceDescriptor newSequenceDescriptor(final SchemaDescriptor schemaDescriptor, final UUID uuid, final String s, final DataTypeDescriptor dataTypeDescriptor, final Long n, final long n2, final long n3, final long n4, final long n5, final boolean b) {
        return new SequenceDescriptor(this.dataDictionary, schemaDescriptor, uuid, s, dataTypeDescriptor, n, n2, n3, n4, n5, b);
    }
    
    public PermDescriptor newPermDescriptor(final UUID uuid, final String s, final UUID uuid2, final String s2, final String s3, final String s4, final boolean b) {
        return new PermDescriptor(this.dataDictionary, uuid, s, uuid2, s2, s3, s4, b);
    }
}
