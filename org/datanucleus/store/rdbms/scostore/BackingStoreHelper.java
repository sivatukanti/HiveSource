// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.mapping.java.EmbeddedValuePCMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedKeyPCMapping;
import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedElementPCMapping;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.Iterator;
import java.util.Collection;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.state.ObjectProvider;

public class BackingStoreHelper
{
    public static int populateOwnerInStatement(final ObjectProvider op, final ExecutionContext ec, final PreparedStatement ps, final int jdbcPosition, final BaseContainerStore bcs) {
        if (!bcs.getStoreManager().insertValuesOnInsert(bcs.getOwnerMapping().getDatastoreMapping(0))) {
            return jdbcPosition;
        }
        if (bcs.getOwnerMemberMetaData() != null) {
            bcs.getOwnerMapping().setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, bcs.getOwnerMapping()), op.getObject(), op, bcs.getOwnerMemberMetaData().getAbsoluteFieldNumber());
        }
        else {
            bcs.getOwnerMapping().setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, bcs.getOwnerMapping()), op.getObject());
        }
        return jdbcPosition + bcs.getOwnerMapping().getNumberOfDatastoreMappings();
    }
    
    public static int populateRelationDiscriminatorInStatement(final ExecutionContext ec, final PreparedStatement ps, final int jdbcPosition, final ElementContainerStore ecs) {
        ecs.getRelationDiscriminatorMapping().setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, ecs.getRelationDiscriminatorMapping()), ecs.getRelationDiscriminatorValue());
        return jdbcPosition + ecs.getRelationDiscriminatorMapping().getNumberOfDatastoreMappings();
    }
    
    public static int populateOrderInStatement(final ExecutionContext ec, final PreparedStatement ps, final int idx, final int jdbcPosition, final JavaTypeMapping orderMapping) {
        orderMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, orderMapping), idx);
        return jdbcPosition + orderMapping.getNumberOfDatastoreMappings();
    }
    
    public static int populateElementInStatement(final ExecutionContext ec, final PreparedStatement ps, final Object element, final int jdbcPosition, final JavaTypeMapping elementMapping) {
        if (!elementMapping.getStoreManager().insertValuesOnInsert(elementMapping.getDatastoreMapping(0))) {
            return jdbcPosition;
        }
        elementMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, elementMapping), element);
        return jdbcPosition + elementMapping.getNumberOfDatastoreMappings();
    }
    
    public static int populateElementForWhereClauseInStatement(final ExecutionContext ec, final PreparedStatement ps, final Object element, int jdbcPosition, final JavaTypeMapping elementMapping) {
        if (elementMapping.getStoreManager().insertValuesOnInsert(elementMapping.getDatastoreMapping(0))) {
            if (elementMapping instanceof ReferenceMapping && elementMapping.getNumberOfDatastoreMappings() > 1) {
                final ReferenceMapping elemRefMapping = (ReferenceMapping)elementMapping;
                final JavaTypeMapping[] elemFkMappings = elemRefMapping.getJavaTypeMapping();
                int[] positions = null;
                for (int i = 0; i < elemFkMappings.length; ++i) {
                    if (elemFkMappings[i].getType().equals(element.getClass().getName())) {
                        positions = new int[elemFkMappings[i].getNumberOfDatastoreMappings()];
                        for (int j = 0; j < positions.length; ++j) {
                            positions[j] = jdbcPosition++;
                        }
                    }
                }
                elementMapping.setObject(ec, ps, positions, element);
                jdbcPosition += positions.length;
            }
            else {
                elementMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, elementMapping), element);
                jdbcPosition += elementMapping.getNumberOfDatastoreMappings();
            }
        }
        return jdbcPosition;
    }
    
    public static int populateKeyInStatement(final ExecutionContext ec, final PreparedStatement ps, final Object key, final int jdbcPosition, final JavaTypeMapping keyMapping) {
        if (!((AbstractDatastoreMapping)keyMapping.getDatastoreMapping(0)).insertValuesOnInsert()) {
            return jdbcPosition;
        }
        keyMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, keyMapping), key);
        return jdbcPosition + keyMapping.getNumberOfDatastoreMappings();
    }
    
    public static int populateValueInStatement(final ExecutionContext ec, final PreparedStatement ps, final Object value, final int jdbcPosition, final JavaTypeMapping valueMapping) {
        if (!((AbstractDatastoreMapping)valueMapping.getDatastoreMapping(0)).insertValuesOnInsert()) {
            return jdbcPosition;
        }
        valueMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, valueMapping), value);
        return jdbcPosition + valueMapping.getNumberOfDatastoreMappings();
    }
    
    public static int populateElementDiscriminatorInStatement(final ExecutionContext ec, final PreparedStatement ps, int jdbcPosition, final boolean includeSubclasses, final ElementContainerStore.ElementInfo info, final ClassLoaderResolver clr) {
        final DiscriminatorStrategy strategy = info.getDiscriminatorStrategy();
        final JavaTypeMapping discrimMapping = info.getDiscriminatorMapping();
        if (strategy == DiscriminatorStrategy.CLASS_NAME) {
            discrimMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, discrimMapping), info.getClassName());
            jdbcPosition += discrimMapping.getNumberOfDatastoreMappings();
        }
        else if (strategy == DiscriminatorStrategy.VALUE_MAP) {
            discrimMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, discrimMapping), info.getAbstractClassMetaData().getInheritanceMetaData().getDiscriminatorMetaData().getValue());
            jdbcPosition += discrimMapping.getNumberOfDatastoreMappings();
        }
        if (includeSubclasses) {
            final RDBMSStoreManager storeMgr = discrimMapping.getStoreManager();
            final Collection<String> subclasses = storeMgr.getSubClassesForClass(info.getClassName(), true, clr);
            if (subclasses != null && subclasses.size() > 0) {
                for (final String subclass : subclasses) {
                    if (strategy == DiscriminatorStrategy.CLASS_NAME) {
                        discrimMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, discrimMapping), subclass);
                        jdbcPosition += discrimMapping.getNumberOfDatastoreMappings();
                    }
                    else {
                        if (strategy != DiscriminatorStrategy.VALUE_MAP) {
                            continue;
                        }
                        final AbstractClassMetaData subclassCmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(subclass, clr);
                        discrimMapping.setObject(ec, ps, MappingHelper.getMappingIndices(jdbcPosition, discrimMapping), subclassCmd.getInheritanceMetaData().getDiscriminatorMetaData().getValue());
                        jdbcPosition += discrimMapping.getNumberOfDatastoreMappings();
                    }
                }
            }
        }
        return jdbcPosition;
    }
    
    public static int populateEmbeddedElementFieldsInStatement(final ObjectProvider op, final Object element, final PreparedStatement ps, int jdbcPosition, final AbstractMemberMetaData ownerFieldMetaData, final JavaTypeMapping elementMapping, final AbstractClassMetaData emd, final BaseContainerStore bcs) {
        final EmbeddedElementPCMapping embeddedMapping = (EmbeddedElementPCMapping)elementMapping;
        final StatementClassMapping mappingDefinition = new StatementClassMapping();
        final int[] elementFieldNumbers = new int[embeddedMapping.getNumberOfJavaTypeMappings()];
        for (int i = 0; i < embeddedMapping.getNumberOfJavaTypeMappings(); ++i) {
            final JavaTypeMapping fieldMapping = embeddedMapping.getJavaTypeMapping(i);
            final int absFieldNum = emd.getAbsolutePositionOfMember(fieldMapping.getMemberMetaData().getName());
            elementFieldNumbers[i] = absFieldNum;
            final StatementMappingIndex stmtMapping = new StatementMappingIndex(fieldMapping);
            final int[] jdbcParamPositions = new int[fieldMapping.getNumberOfDatastoreMappings()];
            for (int j = 0; j < fieldMapping.getNumberOfDatastoreMappings(); ++j) {
                jdbcParamPositions[j] = jdbcPosition++;
            }
            stmtMapping.addParameterOccurrence(jdbcParamPositions);
            mappingDefinition.addMappingForMember(absFieldNum, stmtMapping);
        }
        final ObjectProvider elementSM = bcs.getObjectProviderForEmbeddedPCObject(op, element, ownerFieldMetaData, (short)2);
        elementSM.provideFields(elementFieldNumbers, elementMapping.getStoreManager().getFieldManagerForStatementGeneration(elementSM, ps, mappingDefinition));
        return jdbcPosition;
    }
    
    public static int populateEmbeddedKeyFieldsInStatement(final ObjectProvider op, final Object key, final PreparedStatement ps, int jdbcPosition, final JoinTable joinTable, final AbstractMapStore mapStore) {
        final AbstractClassMetaData kmd = mapStore.getKmd();
        final EmbeddedKeyPCMapping embeddedMapping = (EmbeddedKeyPCMapping)mapStore.getKeyMapping();
        final StatementClassMapping mappingDefinition = new StatementClassMapping();
        final int[] elementFieldNumbers = new int[embeddedMapping.getNumberOfJavaTypeMappings()];
        for (int i = 0; i < embeddedMapping.getNumberOfJavaTypeMappings(); ++i) {
            final JavaTypeMapping fieldMapping = embeddedMapping.getJavaTypeMapping(i);
            final int absFieldNum = kmd.getAbsolutePositionOfMember(fieldMapping.getMemberMetaData().getName());
            elementFieldNumbers[i] = absFieldNum;
            final StatementMappingIndex stmtMapping = new StatementMappingIndex(fieldMapping);
            final int[] jdbcParamPositions = new int[fieldMapping.getNumberOfDatastoreMappings()];
            for (int j = 0; j < fieldMapping.getNumberOfDatastoreMappings(); ++j) {
                jdbcParamPositions[j] = jdbcPosition++;
            }
            stmtMapping.addParameterOccurrence(jdbcParamPositions);
            mappingDefinition.addMappingForMember(absFieldNum, stmtMapping);
        }
        final ObjectProvider elementSM = mapStore.getObjectProviderForEmbeddedPCObject(op, key, joinTable.getOwnerMemberMetaData(), (short)3);
        elementSM.provideFields(elementFieldNumbers, embeddedMapping.getStoreManager().getFieldManagerForStatementGeneration(elementSM, ps, mappingDefinition));
        return jdbcPosition;
    }
    
    public static int populateEmbeddedValueFieldsInStatement(final ObjectProvider op, final Object value, final PreparedStatement ps, int jdbcPosition, final JoinTable joinTable, final AbstractMapStore mapStore) {
        final AbstractClassMetaData vmd = mapStore.getVmd();
        final EmbeddedValuePCMapping embeddedMapping = (EmbeddedValuePCMapping)mapStore.getValueMapping();
        final StatementClassMapping mappingDefinition = new StatementClassMapping();
        final int[] elementFieldNumbers = new int[embeddedMapping.getNumberOfJavaTypeMappings()];
        for (int i = 0; i < embeddedMapping.getNumberOfJavaTypeMappings(); ++i) {
            final JavaTypeMapping fieldMapping = embeddedMapping.getJavaTypeMapping(i);
            final int absFieldNum = vmd.getAbsolutePositionOfMember(fieldMapping.getMemberMetaData().getName());
            elementFieldNumbers[i] = absFieldNum;
            final StatementMappingIndex stmtMapping = new StatementMappingIndex(fieldMapping);
            final int[] jdbcParamPositions = new int[fieldMapping.getNumberOfDatastoreMappings()];
            for (int j = 0; j < fieldMapping.getNumberOfDatastoreMappings(); ++j) {
                jdbcParamPositions[j] = jdbcPosition++;
            }
            stmtMapping.addParameterOccurrence(jdbcParamPositions);
            mappingDefinition.addMappingForMember(absFieldNum, stmtMapping);
        }
        final ObjectProvider elementSM = mapStore.getObjectProviderForEmbeddedPCObject(op, value, joinTable.getOwnerMemberMetaData(), (short)4);
        elementSM.provideFields(elementFieldNumbers, embeddedMapping.getStoreManager().getFieldManagerForStatementGeneration(elementSM, ps, mappingDefinition));
        return jdbcPosition;
    }
    
    public static void appendWhereClauseForElement(final StringBuffer stmt, final JavaTypeMapping elementMapping, final Object element, final boolean elementsSerialised, final String containerAlias, final boolean firstWhereClause) {
        if (!firstWhereClause) {
            stmt.append(" AND ");
        }
        if (elementMapping instanceof ReferenceMapping && elementMapping.getNumberOfDatastoreMappings() > 1) {
            for (int i = 0; i < elementMapping.getNumberOfDatastoreMappings(); ++i) {
                if (i > 0) {
                    stmt.append(" AND ");
                }
                if (containerAlias != null) {
                    stmt.append(containerAlias).append(".");
                }
                stmt.append(elementMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                if (((ReferenceMapping)elementMapping).getJavaTypeMapping()[i].getType().equals(element.getClass().getName())) {
                    if (elementsSerialised) {
                        stmt.append(" LIKE ");
                    }
                    else {
                        stmt.append("=");
                    }
                    stmt.append(((AbstractDatastoreMapping)elementMapping.getDatastoreMapping(i)).getUpdateInputParameter());
                }
                else {
                    stmt.append(" IS NULL");
                }
            }
        }
        else {
            for (int i = 0; i < elementMapping.getNumberOfDatastoreMappings(); ++i) {
                if (i > 0) {
                    stmt.append(" AND ");
                }
                if (containerAlias != null) {
                    stmt.append(containerAlias).append(".");
                }
                stmt.append(elementMapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
                if (elementsSerialised) {
                    stmt.append(" LIKE ");
                }
                else {
                    stmt.append("=");
                }
                stmt.append(((AbstractDatastoreMapping)elementMapping.getDatastoreMapping(i)).getUpdateInputParameter());
            }
        }
    }
    
    public static void appendWhereClauseForMapping(final StringBuffer stmt, final JavaTypeMapping mapping, final String containerAlias, final boolean firstWhereClause) {
        for (int i = 0; i < mapping.getNumberOfDatastoreMappings(); ++i) {
            if (!firstWhereClause || (firstWhereClause && i > 0)) {
                stmt.append(" AND ");
            }
            if (containerAlias != null) {
                stmt.append(containerAlias).append(".");
            }
            stmt.append(mapping.getDatastoreMapping(i).getColumn().getIdentifier().toString());
            stmt.append("=");
            stmt.append(((AbstractDatastoreMapping)mapping.getDatastoreMapping(i)).getInsertionInputParameter());
        }
    }
}
