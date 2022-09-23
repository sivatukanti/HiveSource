// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.ClassConstants;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.exceptions.DuplicateColumnException;
import org.datanucleus.store.rdbms.mapping.CorrespondentColumnsMapper;
import org.datanucleus.metadata.ColumnMetaDataContainer;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.util.Localiser;

public final class ColumnCreator
{
    protected static final Localiser LOCALISER;
    
    private ColumnCreator() {
    }
    
    public static Column createIndexColumn(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr, final Table table, final ColumnMetaData colmd, final boolean pk) {
        DatastoreIdentifier identifier = null;
        if (colmd != null && colmd.getName() != null) {
            identifier = storeMgr.getIdentifierFactory().newColumnIdentifier(colmd.getName());
        }
        else {
            identifier = storeMgr.getIdentifierFactory().newAdapterIndexFieldIdentifier();
        }
        final Column column = table.addColumn(mapping.getType(), identifier, mapping, colmd);
        storeMgr.getMappingManager().createDatastoreMapping(mapping, column, mapping.getJavaType().getName());
        if (pk) {
            column.setAsPrimaryKey();
        }
        return column;
    }
    
    public static JavaTypeMapping createColumnsForJoinTables(final Class javaType, final AbstractMemberMetaData mmd, final ColumnMetaData[] columnMetaData, final RDBMSStoreManager storeMgr, final Table table, final boolean primaryKey, final boolean nullable, final int fieldRole, final ClassLoaderResolver clr) {
        final JavaTypeMapping mapping = storeMgr.getMappingManager().getMapping(javaType, false, false, mmd.getFullFieldName());
        mapping.setTable(table);
        createColumnsForField(javaType, mapping, table, storeMgr, mmd, primaryKey, nullable, false, false, fieldRole, columnMetaData, clr, false);
        return mapping;
    }
    
    public static JavaTypeMapping createColumnsForField(final Class javaType, final JavaTypeMapping mapping, final Table table, final RDBMSStoreManager storeMgr, final AbstractMemberMetaData mmd, final boolean isPrimaryKey, final boolean isNullable, final boolean serialised, final boolean embedded, final int fieldRole, final ColumnMetaData[] columnMetaData, final ClassLoaderResolver clr, final boolean isReferenceField) {
        final IdentifierFactory idFactory = storeMgr.getIdentifierFactory();
        if (mapping instanceof ReferenceMapping || mapping instanceof PersistableMapping) {
            JavaTypeMapping container = mapping;
            if (mapping instanceof ReferenceMapping) {
                container = storeMgr.getMappingManager().getMapping(javaType, serialised, embedded, (mmd != null) ? mmd.getFullFieldName() : null);
                ((ReferenceMapping)mapping).addJavaTypeMapping(container);
            }
            DatastoreClass destinationTable = storeMgr.getDatastoreClass(javaType.getName(), clr);
            if (destinationTable == null) {
                final AbstractClassMetaData ownerCmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(javaType, clr);
                final AbstractClassMetaData[] ownerCmds = storeMgr.getClassesManagingTableForClass(ownerCmd, clr);
                if (ownerCmds == null || ownerCmds.length == 0) {
                    throw new NucleusUserException(ColumnCreator.LOCALISER.msg("057023", javaType.getName())).setFatal();
                }
                destinationTable = storeMgr.getDatastoreClass(ownerCmds[0].getFullClassName(), clr);
            }
            if (destinationTable != null) {
                final JavaTypeMapping m = destinationTable.getIdMapping();
                ColumnMetaDataContainer columnContainer = null;
                if (columnMetaData != null && columnMetaData.length > 0) {
                    columnContainer = (ColumnMetaDataContainer)columnMetaData[0].getParent();
                }
                final CorrespondentColumnsMapper correspondentColumnsMapping = new CorrespondentColumnsMapper(columnContainer, columnMetaData, m, true);
                for (int i = 0; i < m.getNumberOfDatastoreMappings(); ++i) {
                    final JavaTypeMapping refDatastoreMapping = storeMgr.getMappingManager().getMapping(m.getDatastoreMapping(i).getJavaTypeMapping().getJavaType());
                    final ColumnMetaData colmd = correspondentColumnsMapping.getColumnMetaDataByIdentifier(m.getDatastoreMapping(i).getColumn().getIdentifier());
                    try {
                        DatastoreIdentifier identifier = null;
                        if (colmd.getName() == null) {
                            if (isReferenceField) {
                                identifier = idFactory.newReferenceFieldIdentifier(mmd, storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(javaType, clr), m.getDatastoreMapping(i).getColumn().getIdentifier(), storeMgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(javaType), fieldRole);
                            }
                            else {
                                final AbstractMemberMetaData[] relatedMmds = mmd.getRelatedMemberMetaData(clr);
                                identifier = idFactory.newJoinTableFieldIdentifier(mmd, (relatedMmds != null) ? relatedMmds[0] : null, m.getDatastoreMapping(i).getColumn().getIdentifier(), storeMgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(javaType), fieldRole);
                            }
                        }
                        else {
                            identifier = idFactory.newColumnIdentifier(colmd.getName());
                        }
                        final Column column = table.addColumn(javaType.getName(), identifier, refDatastoreMapping, colmd);
                        m.getDatastoreMapping(i).getColumn().copyConfigurationTo(column);
                        if (isPrimaryKey) {
                            column.setAsPrimaryKey();
                        }
                        if (isNullable) {
                            column.setNullable();
                        }
                        storeMgr.getMappingManager().createDatastoreMapping(refDatastoreMapping, column, m.getDatastoreMapping(i).getJavaTypeMapping().getJavaTypeForDatastoreMapping(i));
                    }
                    catch (DuplicateColumnException ex) {
                        throw new NucleusUserException("Cannot create column for field " + mmd.getFullFieldName() + " column metadata " + colmd, ex);
                    }
                    ((PersistableMapping)container).addJavaTypeMapping(refDatastoreMapping);
                }
            }
        }
        else {
            Column column2 = null;
            ColumnMetaData colmd2 = null;
            if (columnMetaData != null && columnMetaData.length > 0) {
                colmd2 = columnMetaData[0];
            }
            DatastoreIdentifier identifier2 = null;
            if (colmd2 != null && colmd2.getName() != null) {
                identifier2 = idFactory.newColumnIdentifier(colmd2.getName());
            }
            else {
                identifier2 = idFactory.newJoinTableFieldIdentifier(mmd, null, null, storeMgr.getNucleusContext().getTypeManager().isDefaultEmbeddedType(javaType), fieldRole);
            }
            column2 = table.addColumn(javaType.getName(), identifier2, mapping, colmd2);
            storeMgr.getMappingManager().createDatastoreMapping(mapping, column2, mapping.getJavaTypeForDatastoreMapping(0));
            if (isNullable) {
                column2.setNullable();
            }
        }
        return mapping;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
