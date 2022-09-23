// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.ClassConstants;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.Localiser;

public class MetaDataMerger
{
    protected static final Localiser LOCALISER;
    
    public static void mergeFileORMData(final FileMetaData primaryFmd, final FileMetaData ormFmd) {
        if (ormFmd == null || primaryFmd == null) {
            return;
        }
        if (primaryFmd.isInitialised() || primaryFmd.isPopulated()) {
            throw new NucleusException(MetaDataMerger.LOCALISER.msg("MetaData.File.AlreadyPopulatedError", primaryFmd.getFilename())).setFatal();
        }
        if (NucleusLogger.METADATA.isDebugEnabled()) {
            NucleusLogger.METADATA.debug(MetaDataMerger.LOCALISER.msg("044056", primaryFmd.getFilename()));
        }
        if (ormFmd.getCatalog() != null) {
            primaryFmd.setCatalog(ormFmd.getCatalog());
        }
        if (ormFmd.getSchema() != null) {
            primaryFmd.setSchema(ormFmd.getSchema());
        }
    }
    
    public static void mergeClassORMData(final AbstractClassMetaData primaryCmd, final AbstractClassMetaData ormCmd, final MetaDataManager mmgr) {
        if (ormCmd == null || primaryCmd == null) {
            return;
        }
        if (primaryCmd.isInitialised() || primaryCmd.isPopulated()) {
            throw new NucleusException(MetaDataMerger.LOCALISER.msg("044068", primaryCmd.name)).setFatal();
        }
        if (NucleusLogger.METADATA.isDebugEnabled()) {
            NucleusLogger.METADATA.debug(MetaDataMerger.LOCALISER.msg("044096", primaryCmd.getFullClassName()));
        }
        if (ormCmd.getCatalog() != null) {
            primaryCmd.catalog = ormCmd.getCatalog();
        }
        if (ormCmd.getSchema() != null) {
            primaryCmd.schema = ormCmd.getSchema();
        }
        if (ormCmd.getTable() != null) {
            primaryCmd.table = ormCmd.getTable();
        }
        if (ormCmd.detachable) {
            primaryCmd.detachable = true;
        }
        if (!ormCmd.requiresExtent) {
            primaryCmd.requiresExtent = false;
        }
        if (ormCmd.embeddedOnly) {
            primaryCmd.embeddedOnly = true;
        }
        if (ormCmd.getPrimaryKeyMetaData() != null) {
            primaryCmd.setPrimaryKeyMetaData(ormCmd.getPrimaryKeyMetaData());
        }
        if (ormCmd.getInheritanceMetaData() != null) {
            primaryCmd.setInheritanceMetaData(ormCmd.getInheritanceMetaData());
        }
        if (ormCmd.getIdentityMetaData() != null) {
            primaryCmd.setIdentityMetaData(ormCmd.getIdentityMetaData());
        }
        if (ormCmd.getVersionMetaData() != null) {
            primaryCmd.setVersionMetaData(ormCmd.getVersionMetaData());
        }
        if (ormCmd.listeners != null) {
            if (primaryCmd.listeners == null) {
                primaryCmd.listeners = new ArrayList();
            }
            primaryCmd.listeners.addAll(ormCmd.listeners);
        }
        if (ormCmd.queries != null) {
            if (primaryCmd.queries == null) {
                primaryCmd.queries = new ArrayList<QueryMetaData>();
            }
            else {
                primaryCmd.queries.clear();
            }
            primaryCmd.queries.addAll(ormCmd.queries);
        }
        if (ormCmd.joins.size() > 0) {
            primaryCmd.joins.clear();
            final Iterator iter = ormCmd.joins.iterator();
            while (iter.hasNext()) {
                primaryCmd.addJoin(iter.next());
            }
        }
        if (ormCmd.indexes.size() > 0) {
            primaryCmd.indexes.clear();
            final Iterator iter = ormCmd.indexes.iterator();
            while (iter.hasNext()) {
                primaryCmd.addIndex(iter.next());
            }
        }
        if (ormCmd.foreignKeys.size() > 0) {
            primaryCmd.foreignKeys.clear();
            final Iterator iter = ormCmd.foreignKeys.iterator();
            while (iter.hasNext()) {
                primaryCmd.addForeignKey(iter.next());
            }
        }
        if (ormCmd.uniqueConstraints.size() > 0) {
            primaryCmd.uniqueConstraints.clear();
            final Iterator iter = ormCmd.uniqueConstraints.iterator();
            while (iter.hasNext()) {
                primaryCmd.addUniqueConstraint(iter.next());
            }
        }
        if (ormCmd.fetchGroups.size() > 0) {
            primaryCmd.fetchGroups.clear();
            final Iterator iter = ormCmd.fetchGroups.iterator();
            while (iter.hasNext()) {
                primaryCmd.addFetchGroup(iter.next());
            }
        }
        if (ormCmd.unmappedColumns != null) {
            primaryCmd.unmappedColumns = null;
            final Iterator<ColumnMetaData> iter2 = ormCmd.unmappedColumns.iterator();
            while (iter2.hasNext()) {
                primaryCmd.addUnmappedColumn(iter2.next());
            }
        }
        for (int i = 0; i < ormCmd.getNoOfMembers(); ++i) {
            final AbstractMemberMetaData ormFmd = ormCmd.getMetaDataForMemberAtRelativePosition(i);
            final AbstractMemberMetaData primaryFmd = primaryCmd.getMetaDataForMember(ormFmd.getName());
            if (primaryFmd == null) {
                AbstractMemberMetaData fmd = null;
                if (ormFmd.className != null) {
                    AbstractMemberMetaData jdoFmd = mmgr.readMetaDataForMember(ormFmd.className, ormFmd.name);
                    if (jdoFmd == null) {
                        jdoFmd = mmgr.readMetaDataForMember(ormCmd.getPackageName() + "." + ormFmd.className, ormFmd.name);
                    }
                    if (jdoFmd != null) {
                        if (jdoFmd instanceof FieldMetaData) {
                            fmd = new FieldMetaData(primaryCmd, jdoFmd);
                        }
                        else {
                            fmd = new PropertyMetaData(primaryCmd, (PropertyMetaData)jdoFmd);
                        }
                        fmd.className = ormFmd.className;
                        mergeMemberORMData(fmd, ormFmd);
                    }
                    else {
                        if (ormFmd instanceof FieldMetaData) {
                            fmd = new FieldMetaData(primaryCmd, ormFmd);
                        }
                        else {
                            fmd = new PropertyMetaData(primaryCmd, (PropertyMetaData)ormFmd);
                        }
                        fmd.className = ormFmd.className;
                    }
                }
                else if (ormFmd instanceof FieldMetaData) {
                    fmd = new FieldMetaData(primaryCmd, ormFmd);
                }
                else {
                    fmd = new PropertyMetaData(primaryCmd, (PropertyMetaData)ormFmd);
                }
                primaryCmd.addMember(fmd);
            }
            else {
                mergeMemberORMData(primaryFmd, ormFmd);
            }
        }
        final ExtensionMetaData[] ormExtensions = ormCmd.getExtensions();
        if (ormExtensions != null) {
            for (int j = 0; j < ormExtensions.length; ++j) {
                primaryCmd.addExtension(ormExtensions[j].vendorName, ormExtensions[j].key, ormExtensions[j].value);
            }
        }
    }
    
    static void mergeMemberORMData(final AbstractMemberMetaData primaryFmd, final AbstractMemberMetaData ormFmd) {
        if (ormFmd == null || primaryFmd == null) {
            return;
        }
        if (primaryFmd.isInitialised() || primaryFmd.isPopulated()) {
            throw new NucleusException(MetaDataMerger.LOCALISER.msg("044107", primaryFmd.getClassName(), primaryFmd.getName())).setFatal();
        }
        if (ormFmd.persistenceModifier != null && ormFmd.persistenceModifier != FieldPersistenceModifier.DEFAULT && primaryFmd.persistenceModifier != ormFmd.persistenceModifier) {
            primaryFmd.persistenceModifier = ormFmd.persistenceModifier;
        }
        if (ormFmd.className != null) {
            primaryFmd.className = ormFmd.className;
        }
        if (ormFmd.containerMetaData != null) {
            primaryFmd.containerMetaData = ormFmd.containerMetaData;
            primaryFmd.containerMetaData.parent = primaryFmd;
        }
        if (ormFmd.defaultFetchGroup != null) {
            primaryFmd.defaultFetchGroup = ormFmd.defaultFetchGroup;
        }
        if (Boolean.FALSE.equals(primaryFmd.primaryKey) && Boolean.TRUE.equals(ormFmd.primaryKey)) {
            primaryFmd.primaryKey = ormFmd.isPrimaryKey();
        }
        if (ormFmd.getTable() != null) {
            primaryFmd.table = ormFmd.getTable();
        }
        if (ormFmd.getCatalog() != null) {
            primaryFmd.catalog = ormFmd.getCatalog();
        }
        if (ormFmd.getSchema() != null) {
            primaryFmd.schema = ormFmd.getSchema();
        }
        if (ormFmd.column != null) {
            primaryFmd.column = ormFmd.column;
        }
        if (ormFmd.dependent != null) {
            primaryFmd.dependent = ormFmd.dependent;
        }
        if (ormFmd.getMappedBy() != null) {
            primaryFmd.mappedBy = ormFmd.getMappedBy();
        }
        if (ormFmd.getValueStrategy() != null) {
            primaryFmd.valueStrategy = ormFmd.getValueStrategy();
        }
        if (ormFmd.getSequence() != null) {
            primaryFmd.sequence = ormFmd.getSequence();
        }
        if (ormFmd.indexed != null) {
            primaryFmd.indexed = ormFmd.indexed;
        }
        if (ormFmd.nullValue != NullValue.NONE) {
            primaryFmd.nullValue = ormFmd.nullValue;
        }
        if (ormFmd.getJoinMetaData() != null) {
            primaryFmd.setJoinMetaData(ormFmd.joinMetaData);
        }
        if (ormFmd.getEmbeddedMetaData() != null) {
            primaryFmd.setEmbeddedMetaData(ormFmd.embeddedMetaData);
        }
        if (ormFmd.getElementMetaData() != null) {
            primaryFmd.setElementMetaData(ormFmd.elementMetaData);
        }
        if (ormFmd.getKeyMetaData() != null) {
            primaryFmd.setKeyMetaData(ormFmd.keyMetaData);
        }
        if (ormFmd.getValueMetaData() != null) {
            primaryFmd.setValueMetaData(ormFmd.valueMetaData);
        }
        if (ormFmd.getOrderMetaData() != null) {
            primaryFmd.setOrderMetaData(ormFmd.orderMetaData);
        }
        if (ormFmd.getForeignKeyMetaData() != null) {
            primaryFmd.foreignKeyMetaData = ormFmd.getForeignKeyMetaData();
            if (primaryFmd.foreignKeyMetaData != null) {
                primaryFmd.foreignKeyMetaData.parent = primaryFmd;
            }
        }
        if (ormFmd.getIndexMetaData() != null) {
            primaryFmd.indexMetaData = ormFmd.getIndexMetaData();
            if (primaryFmd.indexMetaData != null) {
                primaryFmd.indexMetaData.parent = primaryFmd;
            }
        }
        if (ormFmd.getUniqueMetaData() != null) {
            primaryFmd.uniqueMetaData = ormFmd.getUniqueMetaData();
            if (primaryFmd.uniqueMetaData != null) {
                primaryFmd.uniqueMetaData.parent = primaryFmd;
            }
        }
        final ColumnMetaData[] ormColumns = ormFmd.getColumnMetaData();
        if (ormColumns != null) {
            primaryFmd.columns.clear();
            for (int i = 0; i < ormColumns.length; ++i) {
                primaryFmd.columns.add(ormColumns[i]);
            }
        }
        final ExtensionMetaData[] ormExtensions = ormFmd.getExtensions();
        if (ormExtensions != null) {
            for (int j = 0; j < ormExtensions.length; ++j) {
                primaryFmd.addExtension(ormExtensions[j].vendorName, ormExtensions[j].key, ormExtensions[j].value);
            }
        }
    }
    
    public static void mergeClassAnnotationsData(final AbstractClassMetaData primaryCmd, final AbstractClassMetaData annotCmd, final MetaDataManager mmgr) {
        if (annotCmd == null || primaryCmd == null) {
            return;
        }
        if (primaryCmd.isInitialised() || primaryCmd.isPopulated()) {
            throw new NucleusException(MetaDataMerger.LOCALISER.msg("044068", primaryCmd.name)).setFatal();
        }
        if (NucleusLogger.METADATA.isDebugEnabled()) {
            NucleusLogger.METADATA.debug(MetaDataMerger.LOCALISER.msg("044095", primaryCmd.getFullClassName()));
        }
        final PackageMetaData annotPmd = annotCmd.getPackageMetaData();
        if (annotPmd.getSequences() != null) {
            mmgr.registerSequencesForFile(annotCmd.getPackageMetaData().getFileMetaData());
            final SequenceMetaData[] seqmds = annotPmd.getSequences();
            for (int i = 0; i < seqmds.length; ++i) {
                primaryCmd.getPackageMetaData().addSequence(seqmds[i]);
            }
        }
        if (annotPmd.getTableGenerators() != null) {
            mmgr.registerTableGeneratorsForFile(annotCmd.getPackageMetaData().getFileMetaData());
            final TableGeneratorMetaData[] tablegenmds = annotPmd.getTableGenerators();
            for (int i = 0; i < tablegenmds.length; ++i) {
                primaryCmd.getPackageMetaData().addTableGenerator(tablegenmds[i]);
            }
        }
        if (primaryCmd.entityName == null && annotCmd.entityName != null) {
            primaryCmd.entityName = annotCmd.entityName;
        }
        if (annotCmd.detachable) {
            primaryCmd.detachable = true;
        }
        if (!annotCmd.requiresExtent) {
            primaryCmd.requiresExtent = false;
        }
        if (annotCmd.embeddedOnly) {
            primaryCmd.embeddedOnly = true;
        }
        if (primaryCmd.identityType == null && annotCmd.identityType != null) {
            primaryCmd.identityType = annotCmd.identityType;
        }
        if (primaryCmd.objectidClass == null && annotCmd.objectidClass != null) {
            primaryCmd.objectidClass = annotCmd.objectidClass;
        }
        if (primaryCmd.catalog == null && annotCmd.catalog != null) {
            primaryCmd.catalog = annotCmd.catalog;
        }
        if (primaryCmd.schema == null && annotCmd.schema != null) {
            primaryCmd.schema = annotCmd.schema;
        }
        if (primaryCmd.table == null && annotCmd.table != null) {
            primaryCmd.table = annotCmd.table;
        }
        if (primaryCmd.versionMetaData == null && annotCmd.versionMetaData != null) {
            primaryCmd.setVersionMetaData(annotCmd.versionMetaData);
        }
        if (primaryCmd.identityMetaData == null && annotCmd.identityMetaData != null) {
            primaryCmd.setIdentityMetaData(annotCmd.identityMetaData);
        }
        if (primaryCmd.inheritanceMetaData == null && annotCmd.inheritanceMetaData != null) {
            primaryCmd.setInheritanceMetaData(annotCmd.inheritanceMetaData);
        }
        if (primaryCmd.primaryKeyMetaData == null && annotCmd.primaryKeyMetaData != null) {
            primaryCmd.setPrimaryKeyMetaData(annotCmd.primaryKeyMetaData);
        }
        if (primaryCmd.listeners == null && annotCmd.listeners != null) {
            final Iterator iter = annotCmd.listeners.iterator();
            while (iter.hasNext()) {
                primaryCmd.addListener(iter.next());
            }
        }
        else if (primaryCmd.listeners != null && annotCmd.listeners != null) {
            if (primaryCmd.getListenerForClass(primaryCmd.getFullClassName()) == null) {
                if (annotCmd.getListenerForClass(primaryCmd.getFullClassName()) != null) {
                    primaryCmd.addListener(annotCmd.getListenerForClass(primaryCmd.getFullClassName()));
                }
            }
            else if (primaryCmd.getListenerForClass(primaryCmd.getFullClassName()) != null && primaryCmd.getListeners().size() == 1) {
                final List annotListeners = annotCmd.getListeners();
                for (final EventListenerMetaData elmd : annotListeners) {
                    if (!elmd.getClassName().equals(primaryCmd.getFullClassName())) {
                        primaryCmd.addListener(elmd);
                    }
                }
            }
        }
        if (annotCmd.excludeDefaultListeners != null && primaryCmd.excludeDefaultListeners == null) {
            primaryCmd.excludeDefaultListeners = annotCmd.excludeDefaultListeners;
        }
        if (annotCmd.excludeSuperClassListeners != null && primaryCmd.excludeSuperClassListeners == null) {
            primaryCmd.excludeSuperClassListeners = annotCmd.excludeSuperClassListeners;
        }
        if (primaryCmd.queries == null && annotCmd.queries != null) {
            final Iterator iter = annotCmd.queries.iterator();
            while (iter.hasNext()) {
                primaryCmd.addQuery(iter.next());
            }
        }
        if (primaryCmd.joins.size() == 0 && annotCmd.joins.size() > 0) {
            final Iterator iter = annotCmd.joins.iterator();
            while (iter.hasNext()) {
                primaryCmd.addJoin(iter.next());
            }
        }
        if (primaryCmd.indexes.size() == 0 && annotCmd.indexes.size() > 0) {
            final Iterator iter = annotCmd.indexes.iterator();
            while (iter.hasNext()) {
                primaryCmd.addIndex(iter.next());
            }
        }
        if (primaryCmd.foreignKeys.size() == 0 && annotCmd.foreignKeys.size() > 0) {
            final Iterator iter = annotCmd.foreignKeys.iterator();
            while (iter.hasNext()) {
                primaryCmd.addForeignKey(iter.next());
            }
        }
        if (primaryCmd.uniqueConstraints.size() == 0 && annotCmd.uniqueConstraints.size() > 0) {
            final Iterator iter = annotCmd.uniqueConstraints.iterator();
            while (iter.hasNext()) {
                primaryCmd.addUniqueConstraint(iter.next());
            }
        }
        if (primaryCmd.fetchGroups.size() == 0 && annotCmd.fetchGroups.size() > 0) {
            final Iterator iter = annotCmd.fetchGroups.iterator();
            while (iter.hasNext()) {
                primaryCmd.addFetchGroup(iter.next());
            }
        }
        for (int j = 0; j < annotCmd.getNoOfMembers(); ++j) {
            final AbstractMemberMetaData annotFmd = annotCmd.getMetaDataForMemberAtRelativePosition(j);
            final AbstractMemberMetaData primaryFmd = primaryCmd.getMetaDataForMember(annotFmd.getName());
            if (primaryFmd == null) {
                AbstractMemberMetaData fmd = null;
                if (annotFmd.className != null) {
                    AbstractMemberMetaData baseFmd = mmgr.readMetaDataForMember(annotFmd.className, annotFmd.name);
                    if (baseFmd == null) {
                        baseFmd = mmgr.readMetaDataForMember(annotCmd.getPackageName() + "." + annotFmd.className, annotFmd.name);
                    }
                    if (baseFmd != null) {
                        if (baseFmd instanceof FieldMetaData) {
                            fmd = new FieldMetaData(primaryCmd, baseFmd);
                        }
                        else {
                            fmd = new PropertyMetaData(primaryCmd, (PropertyMetaData)baseFmd);
                        }
                        fmd.className = annotFmd.className;
                        mergeMemberAnnotationsData(fmd, annotFmd);
                    }
                    else {
                        if (annotFmd instanceof FieldMetaData) {
                            fmd = new FieldMetaData(primaryCmd, annotFmd);
                        }
                        else {
                            fmd = new PropertyMetaData(primaryCmd, (PropertyMetaData)annotFmd);
                        }
                        fmd.className = annotFmd.className;
                    }
                }
                else if (annotFmd instanceof FieldMetaData) {
                    fmd = new FieldMetaData(primaryCmd, annotFmd);
                }
                else {
                    fmd = new PropertyMetaData(primaryCmd, (PropertyMetaData)annotFmd);
                }
                primaryCmd.addMember(fmd);
            }
            else {
                mergeMemberAnnotationsData(primaryFmd, annotFmd);
            }
        }
        final ExtensionMetaData[] ormExtensions = annotCmd.getExtensions();
        if (ormExtensions != null) {
            for (int i = 0; i < ormExtensions.length; ++i) {
                primaryCmd.addExtension(ormExtensions[i].vendorName, ormExtensions[i].key, ormExtensions[i].value);
            }
        }
    }
    
    static void mergeMemberAnnotationsData(final AbstractMemberMetaData primaryFmd, final AbstractMemberMetaData annotFmd) {
        if (annotFmd == null || primaryFmd == null) {
            return;
        }
        if (primaryFmd.isInitialised() || primaryFmd.isPopulated()) {
            throw new NucleusException(MetaDataMerger.LOCALISER.msg("044107", primaryFmd.getClassName(), primaryFmd.getName())).setFatal();
        }
        if (primaryFmd.className == null && annotFmd.className != null) {
            primaryFmd.className = annotFmd.className;
        }
        if (primaryFmd.containerMetaData == null && annotFmd.containerMetaData != null) {
            primaryFmd.containerMetaData = annotFmd.containerMetaData;
            primaryFmd.containerMetaData.parent = primaryFmd;
        }
        if (annotFmd.storeInLob) {
            primaryFmd.storeInLob = true;
        }
        if (annotFmd.persistenceModifier != FieldPersistenceModifier.DEFAULT && primaryFmd.persistenceModifier == FieldPersistenceModifier.DEFAULT) {
            primaryFmd.persistenceModifier = annotFmd.persistenceModifier;
        }
        if (annotFmd.defaultFetchGroup != null && primaryFmd.defaultFetchGroup == null) {
            primaryFmd.defaultFetchGroup = annotFmd.defaultFetchGroup;
        }
        if (annotFmd.primaryKey != null) {
            primaryFmd.primaryKey = annotFmd.primaryKey;
        }
        if (primaryFmd.table == null && annotFmd.table != null) {
            primaryFmd.table = annotFmd.table;
        }
        if (primaryFmd.catalog == null && annotFmd.catalog != null) {
            primaryFmd.catalog = annotFmd.catalog;
        }
        if (primaryFmd.schema == null && annotFmd.schema != null) {
            primaryFmd.schema = annotFmd.schema;
        }
        if (primaryFmd.column == null && annotFmd.column != null) {
            primaryFmd.column = annotFmd.column;
        }
        if (primaryFmd.dependent == null && annotFmd.dependent != null) {
            primaryFmd.dependent = annotFmd.dependent;
        }
        if (primaryFmd.mappedBy == null && annotFmd.mappedBy != null) {
            primaryFmd.mappedBy = annotFmd.mappedBy;
        }
        if (primaryFmd.valueStrategy == null && annotFmd.valueStrategy != null) {
            primaryFmd.valueStrategy = annotFmd.valueStrategy;
        }
        if (primaryFmd.sequence == null && annotFmd.sequence != null) {
            primaryFmd.sequence = annotFmd.sequence;
        }
        if (primaryFmd.valueGeneratorName == null && annotFmd.valueGeneratorName != null) {
            primaryFmd.valueGeneratorName = annotFmd.valueGeneratorName;
        }
        if (primaryFmd.indexed == null && annotFmd.indexed != null) {
            primaryFmd.indexed = annotFmd.indexed;
        }
        if (annotFmd.nullValue != NullValue.NONE) {
            primaryFmd.nullValue = annotFmd.nullValue;
        }
        if (annotFmd.cascadePersist != null && primaryFmd.cascadePersist == null) {
            primaryFmd.cascadePersist = annotFmd.cascadePersist;
        }
        if (annotFmd.cascadeUpdate != null && primaryFmd.cascadeUpdate == null) {
            primaryFmd.cascadeUpdate = annotFmd.cascadeUpdate;
        }
        if (annotFmd.cascadeDelete != null && primaryFmd.cascadeDelete == null) {
            primaryFmd.cascadeDelete = annotFmd.cascadeDelete;
        }
        if (annotFmd.cascadeRefresh != null && primaryFmd.cascadeRefresh == null) {
            primaryFmd.cascadeRefresh = annotFmd.cascadeRefresh;
        }
        if (primaryFmd.joinMetaData == null && annotFmd.joinMetaData != null) {
            primaryFmd.setJoinMetaData(annotFmd.joinMetaData);
        }
        if (primaryFmd.embeddedMetaData == null && annotFmd.embeddedMetaData != null) {
            primaryFmd.setEmbeddedMetaData(annotFmd.embeddedMetaData);
        }
        if (primaryFmd.elementMetaData == null && annotFmd.elementMetaData != null) {
            primaryFmd.setElementMetaData(annotFmd.elementMetaData);
        }
        if (primaryFmd.keyMetaData == null && annotFmd.keyMetaData != null) {
            primaryFmd.setKeyMetaData(annotFmd.keyMetaData);
        }
        if (primaryFmd.valueMetaData == null && annotFmd.valueMetaData != null) {
            primaryFmd.setValueMetaData(annotFmd.valueMetaData);
        }
        if (primaryFmd.orderMetaData == null && annotFmd.orderMetaData != null) {
            primaryFmd.setOrderMetaData(annotFmd.orderMetaData);
        }
        if (primaryFmd.foreignKeyMetaData == null && annotFmd.foreignKeyMetaData != null) {
            primaryFmd.foreignKeyMetaData = annotFmd.foreignKeyMetaData;
            if (primaryFmd.foreignKeyMetaData != null) {
                primaryFmd.foreignKeyMetaData.parent = primaryFmd;
            }
        }
        if (primaryFmd.indexMetaData == null && annotFmd.indexMetaData != null) {
            primaryFmd.indexMetaData = annotFmd.indexMetaData;
            if (primaryFmd.indexMetaData != null) {
                primaryFmd.indexMetaData.parent = primaryFmd;
            }
        }
        if (primaryFmd.uniqueMetaData == null && annotFmd.uniqueMetaData != null) {
            primaryFmd.uniqueMetaData = annotFmd.uniqueMetaData;
            if (primaryFmd.uniqueMetaData != null) {
                primaryFmd.uniqueMetaData.parent = primaryFmd;
            }
        }
        if (primaryFmd.columns.size() == 0 && annotFmd.columns.size() > 0) {
            final ColumnMetaData[] annotColumns = annotFmd.getColumnMetaData();
            if (annotColumns != null) {
                for (int i = 0; i < annotColumns.length; ++i) {
                    primaryFmd.columns.add(annotColumns[i]);
                }
            }
        }
        final ExtensionMetaData[] annotExtensions = annotFmd.getExtensions();
        if (annotExtensions != null) {
            for (int i = 0; i < annotExtensions.length; ++i) {
                primaryFmd.addExtension(annotExtensions[i].vendorName, annotExtensions[i].key, annotExtensions[i].value);
            }
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
