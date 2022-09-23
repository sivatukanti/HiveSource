// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.identifier;

import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;

public interface IdentifierFactory
{
    DatastoreAdapter getDatastoreAdapter();
    
    IdentifierCase getIdentifierCase();
    
    String getIdentifierInAdapterCase(final String p0);
    
    DatastoreIdentifier newIdentifier(final IdentifierType p0, final String p1);
    
    DatastoreIdentifier newTableIdentifier(final String p0);
    
    DatastoreIdentifier newTableIdentifier(final AbstractClassMetaData p0);
    
    DatastoreIdentifier newTableIdentifier(final AbstractMemberMetaData p0);
    
    DatastoreIdentifier newColumnIdentifier(final String p0);
    
    DatastoreIdentifier newColumnIdentifier(final String p0, final boolean p1, final int p2);
    
    DatastoreIdentifier newReferenceFieldIdentifier(final AbstractMemberMetaData p0, final AbstractClassMetaData p1, final DatastoreIdentifier p2, final boolean p3, final int p4);
    
    DatastoreIdentifier newDiscriminatorFieldIdentifier();
    
    DatastoreIdentifier newVersionFieldIdentifier();
    
    DatastoreIdentifier newIdentifier(final DatastoreIdentifier p0, final String p1);
    
    DatastoreIdentifier newJoinTableFieldIdentifier(final AbstractMemberMetaData p0, final AbstractMemberMetaData p1, final DatastoreIdentifier p2, final boolean p3, final int p4);
    
    DatastoreIdentifier newForeignKeyFieldIdentifier(final AbstractMemberMetaData p0, final AbstractMemberMetaData p1, final DatastoreIdentifier p2, final boolean p3, final int p4);
    
    DatastoreIdentifier newIndexFieldIdentifier(final AbstractMemberMetaData p0);
    
    DatastoreIdentifier newAdapterIndexFieldIdentifier();
    
    DatastoreIdentifier newSequenceIdentifier(final String p0);
    
    DatastoreIdentifier newPrimaryKeyIdentifier(final Table p0);
    
    DatastoreIdentifier newIndexIdentifier(final Table p0, final boolean p1, final int p2);
    
    DatastoreIdentifier newCandidateKeyIdentifier(final Table p0, final int p1);
    
    DatastoreIdentifier newForeignKeyIdentifier(final Table p0, final int p1);
}
