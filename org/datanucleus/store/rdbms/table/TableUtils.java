// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.store.rdbms.key.CandidateKey;
import org.datanucleus.metadata.UniqueMetaData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import org.datanucleus.store.rdbms.key.Index;
import org.datanucleus.metadata.IndexMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.ForeignKeyMetaData;
import java.util.List;
import org.datanucleus.store.rdbms.key.ForeignKey;
import org.datanucleus.metadata.ForeignKeyAction;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import java.util.Collection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class TableUtils
{
    public static Collection getForeignKeysForReferenceField(final JavaTypeMapping fieldMapping, final AbstractMemberMetaData mmd, final boolean autoMode, final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        final ReferenceMapping refMapping = (ReferenceMapping)fieldMapping;
        final JavaTypeMapping[] refJavaTypeMappings = refMapping.getJavaTypeMapping();
        final List fks = new ArrayList();
        for (int i = 0; i < refJavaTypeMappings.length; ++i) {
            final JavaTypeMapping implMapping = refJavaTypeMappings[i];
            if (storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(implMapping.getType(), clr) != null && implMapping.getNumberOfDatastoreMappings() > 0) {
                final DatastoreClass referencedTable = storeMgr.getDatastoreClass(implMapping.getType(), clr);
                if (referencedTable != null) {
                    final ForeignKeyMetaData fkmd = mmd.getForeignKeyMetaData();
                    if ((fkmd != null && fkmd.getDeleteAction() != ForeignKeyAction.NONE) || autoMode) {
                        final ForeignKey fk = new ForeignKey(implMapping, storeMgr.getDatastoreAdapter(), referencedTable, true);
                        fk.setForMetaData(fkmd);
                        fks.add(fk);
                    }
                }
            }
        }
        return fks;
    }
    
    public static ForeignKey getForeignKeyForPCField(final JavaTypeMapping fieldMapping, final AbstractMemberMetaData mmd, final boolean autoMode, final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        DatastoreClass referencedTable = storeMgr.getDatastoreClass(mmd.getTypeName(), clr);
        if (referencedTable == null) {
            final AbstractClassMetaData refCmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(mmd.getType(), clr);
            if (refCmd != null) {
                final AbstractClassMetaData[] refCmds = storeMgr.getClassesManagingTableForClass(refCmd, clr);
                if (refCmds != null && refCmds.length == 1) {
                    referencedTable = storeMgr.getDatastoreClass(refCmds[0].getFullClassName(), clr);
                }
            }
        }
        if (referencedTable != null) {
            final ForeignKeyMetaData fkmd = mmd.getForeignKeyMetaData();
            if ((fkmd != null && (fkmd.getDeleteAction() != ForeignKeyAction.NONE || fkmd.getFkDefinitionApplies())) || autoMode) {
                final ForeignKey fk = new ForeignKey(fieldMapping, storeMgr.getDatastoreAdapter(), referencedTable, true);
                fk.setForMetaData(fkmd);
                if (fkmd != null && fkmd.getName() != null) {
                    fk.setName(fkmd.getName());
                }
                return fk;
            }
        }
        return null;
    }
    
    public static Index getIndexForField(final Table table, final IndexMetaData imd, final JavaTypeMapping fieldMapping) {
        if (fieldMapping.getNumberOfDatastoreMappings() == 0) {
            return null;
        }
        final boolean unique = imd.isUnique();
        final Index index = new Index(table, unique, imd.getValueForExtension("extended-setting"));
        if (imd.getName() != null) {
            final IdentifierFactory idFactory = table.getStoreManager().getIdentifierFactory();
            final DatastoreIdentifier idxId = idFactory.newIdentifier(IdentifierType.INDEX, imd.getName());
            index.setName(idxId.toString());
        }
        for (int countFields = fieldMapping.getNumberOfDatastoreMappings(), j = 0; j < countFields; ++j) {
            index.addColumn(fieldMapping.getDatastoreMapping(j).getColumn());
        }
        return index;
    }
    
    public static CandidateKey getCandidateKeyForField(final Table table, final UniqueMetaData umd, final JavaTypeMapping fieldMapping) {
        final CandidateKey ck = new CandidateKey(table);
        if (umd.getName() != null) {
            final IdentifierFactory idFactory = table.getStoreManager().getIdentifierFactory();
            final DatastoreIdentifier ckId = idFactory.newIdentifier(IdentifierType.CANDIDATE_KEY, umd.getName());
            ck.setName(ckId.toString());
        }
        for (int countFields = fieldMapping.getNumberOfDatastoreMappings(), j = 0; j < countFields; ++j) {
            ck.addColumn(fieldMapping.getDatastoreMapping(j).getColumn());
        }
        return ck;
    }
}
