// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

import java.util.ArrayList;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.NucleusContext;
import org.datanucleus.store.rdbms.mapping.java.DiscriminatorLongMapping;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.sql.expression.BooleanExpression;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.table.PersistableJoinTable;
import java.util.Collection;
import org.datanucleus.store.rdbms.table.DatastoreElementContainer;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.FetchPlanForClass;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.metadata.JoinMetaData;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.store.rdbms.table.SecondaryDatastoreClass;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.FieldMetaData;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.Set;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.Iterator;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.mapping.java.PersistableIdMapping;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import org.datanucleus.metadata.IdentityType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.sql.SQLException;
import org.datanucleus.store.rdbms.SQLController;
import java.sql.PreparedStatement;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.ExecutionContext;

public class SQLStatementHelper
{
    public static PreparedStatement getPreparedStatementForSQLStatement(final SQLStatement sqlStmt, final ExecutionContext ec, final ManagedConnection mconn, final String resultSetType, final String resultSetConcurrency) throws SQLException {
        final SQLText sqlText = sqlStmt.getSelectStatement();
        final SQLController sqlControl = sqlStmt.getRDBMSManager().getSQLController();
        final PreparedStatement ps = sqlControl.getStatementForQuery(mconn, sqlText.toString(), resultSetType, resultSetConcurrency);
        boolean done = false;
        try {
            sqlText.applyParametersToStatement(ec, ps);
            done = true;
        }
        finally {
            if (!done) {
                sqlControl.closeStatement(mconn, ps);
            }
        }
        return ps;
    }
    
    public static void applyParametersToStatement(final PreparedStatement ps, final ExecutionContext ec, final List<SQLStatementParameter> parameters, final Map<Integer, String> paramNameByPosition, final Map paramValuesByName) {
        if (parameters != null) {
            int num = 1;
            Map<String, Integer> paramNumberByName = null;
            int nextParamNumber = 0;
            for (final SQLStatementParameter param : parameters) {
                final JavaTypeMapping mapping = param.getMapping();
                final RDBMSStoreManager storeMgr = mapping.getStoreManager();
                Object value = null;
                if (paramNumberByName != null) {
                    final Integer position = paramNumberByName.get("" + param.getName());
                    if (position == null) {
                        value = paramValuesByName.get(nextParamNumber);
                        paramNumberByName.put(param.getName(), nextParamNumber);
                        ++nextParamNumber;
                    }
                    else {
                        value = paramValuesByName.get(position);
                    }
                }
                else if (paramValuesByName.containsKey(param.getName())) {
                    value = paramValuesByName.get(param.getName());
                }
                else if (paramNameByPosition != null) {
                    int paramPosition = -1;
                    Set<String> paramNamesEncountered = new HashSet<String>();
                    for (final Map.Entry<Integer, String> entry : paramNameByPosition.entrySet()) {
                        final String paramName = entry.getValue();
                        if (!paramNamesEncountered.contains(paramName)) {
                            ++paramPosition;
                            paramNamesEncountered.add(paramName);
                        }
                        if (paramName.equals(param.getName())) {
                            value = paramValuesByName.get(paramPosition);
                            break;
                        }
                    }
                    paramNamesEncountered.clear();
                    paramNamesEncountered = null;
                }
                else {
                    try {
                        value = paramValuesByName.get(Integer.valueOf(param.getName()));
                    }
                    catch (NumberFormatException nfe) {
                        value = paramValuesByName.get(nextParamNumber);
                        paramNumberByName = new HashMap<String, Integer>();
                        paramNumberByName.put(param.getName(), nextParamNumber);
                        ++nextParamNumber;
                    }
                }
                final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(mapping.getType(), ec.getClassLoaderResolver());
                if (param.getColumnNumber() >= 0 && cmd != null) {
                    Object colValue = null;
                    if (value != null) {
                        if (cmd.getIdentityType() == IdentityType.DATASTORE) {
                            colValue = mapping.getValueForDatastoreMapping(ec.getNucleusContext(), param.getColumnNumber(), value);
                        }
                        else if (cmd.getIdentityType() == IdentityType.APPLICATION) {
                            colValue = getValueForPrimaryKeyIndexOfObjectUsingReflection(value, param.getColumnNumber(), cmd, storeMgr, ec.getClassLoaderResolver());
                        }
                    }
                    mapping.getDatastoreMapping(param.getColumnNumber()).setObject(ps, num, colValue);
                }
                else if (ec.getApiAdapter().isPersistable(value)) {
                    if (!ec.getApiAdapter().isPersistent(value) && !ec.getApiAdapter().isDetached(value)) {
                        mapping.setObject(ec, ps, MappingHelper.getMappingIndices(num, mapping), null);
                    }
                    else if (ec.getApiAdapter().isDetached(value)) {
                        final Object id = ec.getApiAdapter().getIdForObject(value);
                        final PersistableIdMapping idMapping = new PersistableIdMapping((PersistableMapping)mapping);
                        idMapping.setObject(ec, ps, MappingHelper.getMappingIndices(num, idMapping), id);
                    }
                    else {
                        mapping.setObject(ec, ps, MappingHelper.getMappingIndices(num, mapping), value);
                    }
                }
                else if (mapping.getNumberOfDatastoreMappings() == 1) {
                    mapping.setObject(ec, ps, MappingHelper.getMappingIndices(num, mapping), value);
                }
                else if (mapping.getNumberOfDatastoreMappings() > 1 && param.getColumnNumber() == mapping.getNumberOfDatastoreMappings() - 1) {
                    mapping.setObject(ec, ps, MappingHelper.getMappingIndices(num - mapping.getNumberOfDatastoreMappings() + 1, mapping), value);
                }
                ++num;
            }
        }
    }
    
    public static Object getValueForPrimaryKeyIndexOfObjectUsingReflection(final Object value, final int index, final AbstractClassMetaData cmd, final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        if (cmd.getIdentityType() == IdentityType.DATASTORE) {
            throw new NucleusException("This method does not support datastore-identity");
        }
        int position = 0;
        final int[] pkPositions = cmd.getPKMemberPositions();
        for (int i = 0; i < pkPositions.length; ++i) {
            final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkPositions[i]);
            Object memberValue = null;
            if (mmd instanceof FieldMetaData) {
                memberValue = ClassUtils.getValueOfFieldByReflection(value, mmd.getName());
            }
            else {
                memberValue = ClassUtils.getValueOfMethodByReflection(value, ClassUtils.getJavaBeanGetterName(mmd.getName(), false), (Object[])null);
            }
            if (storeMgr.getApiAdapter().isPersistable(mmd.getType())) {
                final AbstractClassMetaData subCmd = storeMgr.getMetaDataManager().getMetaDataForClass(mmd.getType(), clr);
                final DatastoreClass subTable = storeMgr.getDatastoreClass(mmd.getTypeName(), clr);
                final JavaTypeMapping subMapping = subTable.getIdMapping();
                final Object subValue = getValueForPrimaryKeyIndexOfObjectUsingReflection(memberValue, index - position, subCmd, storeMgr, clr);
                if (index < position + subMapping.getNumberOfDatastoreMappings()) {
                    return subValue;
                }
                position += subMapping.getNumberOfDatastoreMappings();
            }
            else if (position == index) {
                if (mmd instanceof FieldMetaData) {
                    return ClassUtils.getValueOfFieldByReflection(value, mmd.getName());
                }
                return ClassUtils.getValueOfMethodByReflection(value, ClassUtils.getJavaBeanGetterName(mmd.getName(), false), (Object[])null);
            }
            else {
                ++position;
            }
        }
        return null;
    }
    
    public static SQLTable getSQLTableForMappingOfTable(final SQLStatement stmt, final SQLTable sqlTbl, final JavaTypeMapping mapping) {
        final Table table = sqlTbl.getTable();
        if (table instanceof SecondaryDatastoreClass || table instanceof JoinTable) {
            if (mapping.getTable() != null) {
                final SQLTable mappingSqlTbl = stmt.getTable(mapping.getTable(), sqlTbl.getGroupName());
                if (mappingSqlTbl != null) {
                    return mappingSqlTbl;
                }
            }
            return sqlTbl;
        }
        final DatastoreClass sourceTbl = (DatastoreClass)sqlTbl.getTable();
        DatastoreClass mappingTbl = null;
        if (mapping.getTable() != null) {
            mappingTbl = (DatastoreClass)mapping.getTable();
        }
        else {
            mappingTbl = sourceTbl.getBaseDatastoreClassWithMember(mapping.getMemberMetaData());
        }
        if (mappingTbl == sourceTbl) {
            return sqlTbl;
        }
        SQLTable mappingSqlTbl2 = stmt.getTable(mappingTbl, sqlTbl.getGroupName());
        if (mappingSqlTbl2 == null) {
            boolean forceLeftOuter = false;
            final SQLTableGroup tableGrp = stmt.getTableGroup(sqlTbl.getGroupName());
            if (tableGrp.getJoinType() == SQLJoin.JoinType.LEFT_OUTER_JOIN) {
                forceLeftOuter = true;
            }
            if (mappingTbl instanceof SecondaryDatastoreClass) {
                boolean innerJoin = true;
                final JoinMetaData joinmd = ((SecondaryDatastoreClass)mappingTbl).getJoinMetaData();
                if (joinmd != null && joinmd.isOuter() && !forceLeftOuter) {
                    innerJoin = false;
                }
                if (innerJoin && !forceLeftOuter) {
                    mappingSqlTbl2 = stmt.innerJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), mappingTbl, null, mappingTbl.getIdMapping(), null, sqlTbl.getGroupName());
                }
                else {
                    mappingSqlTbl2 = stmt.leftOuterJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), mappingTbl, null, mappingTbl.getIdMapping(), null, sqlTbl.getGroupName());
                }
            }
            else if (forceLeftOuter) {
                mappingSqlTbl2 = stmt.leftOuterJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), mappingTbl, null, mappingTbl.getIdMapping(), null, sqlTbl.getGroupName());
            }
            else {
                mappingSqlTbl2 = stmt.innerJoin(sqlTbl, sqlTbl.getTable().getIdMapping(), mappingTbl, null, mappingTbl.getIdMapping(), null, sqlTbl.getGroupName());
            }
        }
        return mappingSqlTbl2;
    }
    
    public static void selectIdentityOfCandidateInStatement(final SQLStatement stmt, final StatementClassMapping mappingDefinition, final AbstractClassMetaData candidateCmd) {
        final DatastoreClass candidateTbl = (DatastoreClass)stmt.getPrimaryTable().getTable();
        if (candidateCmd.getIdentityType() == IdentityType.DATASTORE) {
            final JavaTypeMapping idMapping = candidateTbl.getDatastoreObjectIdMapping();
            final int[] colNumbers = stmt.select(stmt.getPrimaryTable(), idMapping, "DN_DATASTOREID", false);
            if (mappingDefinition != null) {
                final StatementMappingIndex datastoreIdIdx = new StatementMappingIndex(idMapping);
                datastoreIdIdx.setColumnPositions(colNumbers);
                mappingDefinition.addMappingForMember(-1, datastoreIdIdx);
            }
        }
        else if (candidateCmd.getIdentityType() == IdentityType.APPLICATION) {
            final int[] pkPositions = candidateCmd.getPKMemberPositions();
            for (int i = 0; i < pkPositions.length; ++i) {
                final AbstractMemberMetaData pkMmd = candidateCmd.getMetaDataForManagedMemberAtAbsolutePosition(pkPositions[i]);
                final JavaTypeMapping pkMapping = candidateTbl.getMemberMapping(pkMmd);
                final int[] colNumbers2 = stmt.select(stmt.getPrimaryTable(), pkMapping, "DN_APPID", false);
                if (mappingDefinition != null) {
                    final StatementMappingIndex appIdIdx = new StatementMappingIndex(pkMapping);
                    appIdIdx.setColumnPositions(colNumbers2);
                    mappingDefinition.addMappingForMember(pkPositions[i], appIdIdx);
                }
            }
        }
        final JavaTypeMapping verMapping = candidateTbl.getVersionMapping(true);
        if (verMapping != null) {
            final SQLTable versionSqlTbl = getSQLTableForMappingOfTable(stmt, stmt.getPrimaryTable(), verMapping);
            final int[] colNumbers3 = stmt.select(versionSqlTbl, verMapping, "DN_VERSION", false);
            if (mappingDefinition != null) {
                final StatementMappingIndex versionIdx = new StatementMappingIndex(verMapping);
                versionIdx.setColumnPositions(colNumbers3);
                mappingDefinition.addMappingForMember(-2, versionIdx);
            }
        }
        final JavaTypeMapping discrimMapping = candidateTbl.getDiscriminatorMapping(true);
        if (discrimMapping != null) {
            final SQLTable discrimSqlTbl = getSQLTableForMappingOfTable(stmt, stmt.getPrimaryTable(), discrimMapping);
            final int[] colNumbers4 = stmt.select(discrimSqlTbl, discrimMapping, "DN_DISCRIM", false);
            if (mappingDefinition != null) {
                final StatementMappingIndex discrimIdx = new StatementMappingIndex(discrimMapping);
                discrimIdx.setColumnPositions(colNumbers4);
                mappingDefinition.addMappingForMember(-3, discrimIdx);
            }
        }
        final List<SQLStatement> unionStmts = stmt.getUnions();
        if (unionStmts != null) {
            for (final SQLStatement unionStmt : unionStmts) {
                selectIdentityOfCandidateInStatement(unionStmt, null, candidateCmd);
            }
        }
    }
    
    public static void selectFetchPlanOfCandidateInStatement(final SQLStatement stmt, final StatementClassMapping mappingDefinition, final AbstractClassMetaData candidateCmd, final FetchPlan fetchPlan, final int maxFetchDepth) {
        selectFetchPlanOfSourceClassInStatement(stmt, mappingDefinition, fetchPlan, stmt.getPrimaryTable(), candidateCmd, maxFetchDepth);
    }
    
    public static void selectFetchPlanOfSourceClassInStatement(final SQLStatement stmt, final StatementClassMapping mappingDefinition, final FetchPlan fetchPlan, final SQLTable sourceSqlTbl, final AbstractClassMetaData sourceCmd, final int maxFetchDepth) {
        final DatastoreClass sourceTbl = (DatastoreClass)sourceSqlTbl.getTable();
        int[] fieldNumbers;
        if (fetchPlan != null) {
            final FetchPlanForClass fpc = fetchPlan.getFetchPlanForClass(sourceCmd);
            fieldNumbers = fpc.getMemberNumbers();
        }
        else {
            fieldNumbers = sourceCmd.getDFGMemberPositions();
        }
        final ClassLoaderResolver clr = stmt.getRDBMSManager().getNucleusContext().getClassLoaderResolver(null);
        for (int i = 0; i < fieldNumbers.length; ++i) {
            final AbstractMemberMetaData mmd = sourceCmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumbers[i]);
            selectMemberOfSourceInStatement(stmt, mappingDefinition, fetchPlan, sourceSqlTbl, mmd, clr, maxFetchDepth);
        }
        if (sourceCmd.getIdentityType() == IdentityType.DATASTORE) {
            final JavaTypeMapping idMapping = sourceTbl.getDatastoreObjectIdMapping();
            final int[] colNumbers = stmt.select(sourceSqlTbl, idMapping, null);
            if (mappingDefinition != null) {
                final StatementMappingIndex datastoreIdIdx = new StatementMappingIndex(idMapping);
                datastoreIdIdx.setColumnPositions(colNumbers);
                mappingDefinition.addMappingForMember(-1, datastoreIdIdx);
            }
        }
        final JavaTypeMapping verMapping = sourceTbl.getVersionMapping(true);
        if (verMapping != null) {
            final SQLTable versionSqlTbl = getSQLTableForMappingOfTable(stmt, sourceSqlTbl, verMapping);
            final int[] colNumbers2 = stmt.select(versionSqlTbl, verMapping, null);
            if (mappingDefinition != null) {
                final StatementMappingIndex versionIdx = new StatementMappingIndex(verMapping);
                versionIdx.setColumnPositions(colNumbers2);
                mappingDefinition.addMappingForMember(-2, versionIdx);
            }
        }
        final JavaTypeMapping discrimMapping = sourceTbl.getDiscriminatorMapping(true);
        if (discrimMapping != null) {
            final SQLTable discrimSqlTbl = getSQLTableForMappingOfTable(stmt, sourceSqlTbl, discrimMapping);
            final int[] colNumbers3 = stmt.select(discrimSqlTbl, discrimMapping, null);
            if (mappingDefinition != null) {
                final StatementMappingIndex discrimIdx = new StatementMappingIndex(discrimMapping);
                discrimIdx.setColumnPositions(colNumbers3);
                mappingDefinition.addMappingForMember(-3, discrimIdx);
            }
        }
    }
    
    public static void selectMemberOfSourceInStatement(final SQLStatement stmt, final StatementClassMapping mappingDefinition, final FetchPlan fetchPlan, final SQLTable sourceSqlTbl, final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final int maxFetchPlanLimit) {
        boolean selectSubobjects = false;
        if (maxFetchPlanLimit > 0) {
            selectSubobjects = true;
        }
        final String tableGroupName = sourceSqlTbl.getGroupName() + "." + mmd.getName();
        final JavaTypeMapping m = sourceSqlTbl.getTable().getMemberMapping(mmd);
        if (m != null && m.includeInFetchStatement()) {
            final RelationType relationType = mmd.getRelationType(clr);
            final RDBMSStoreManager storeMgr = stmt.getRDBMSManager();
            final DatastoreAdapter dba = storeMgr.getDatastoreAdapter();
            if (!dba.validToSelectMappingInStatement(stmt, m)) {
                return;
            }
            final MetaDataManager mmgr = storeMgr.getMetaDataManager();
            final StatementMappingIndex stmtMapping = new StatementMappingIndex(m);
            if (m.getNumberOfDatastoreMappings() > 0) {
                final SQLTable sqlTbl = getSQLTableForMappingOfTable(stmt, sourceSqlTbl, m);
                boolean selectFK = true;
                if (selectSubobjects && (relationType == RelationType.ONE_TO_ONE_UNI || (relationType == RelationType.ONE_TO_ONE_BI && mmd.getMappedBy() == null)) && !mmd.isSerialized() && !mmd.isEmbedded()) {
                    selectFK = selectFetchPlanFieldsOfFKRelatedObject(stmt, mappingDefinition, fetchPlan, sourceSqlTbl, mmd, clr, maxFetchPlanLimit, m, tableGroupName, stmtMapping, sqlTbl);
                }
                else if (selectSubobjects && !mmd.isEmbedded() && !mmd.isSerialized() && relationType == RelationType.MANY_TO_ONE_BI) {
                    final AbstractMemberMetaData[] relatedMmds = mmd.getRelatedMemberMetaData(clr);
                    if (mmd.getJoinMetaData() != null || relatedMmds[0].getJoinMetaData() != null) {
                        final Table joinTable = storeMgr.getTable(relatedMmds[0]);
                        final DatastoreElementContainer collTable = (DatastoreElementContainer)joinTable;
                        final JavaTypeMapping selectMapping = collTable.getOwnerMapping();
                        SQLTable joinSqlTbl = null;
                        if (stmt.getPrimaryTable().getTable() != joinTable) {
                            final JavaTypeMapping referenceMapping = collTable.getElementMapping();
                            joinSqlTbl = stmt.leftOuterJoin(sourceSqlTbl, sourceSqlTbl.getTable().getIdMapping(), collTable, null, referenceMapping, null, tableGroupName);
                        }
                        else {
                            joinSqlTbl = stmt.getPrimaryTable();
                        }
                        final int[] colNumbers = stmt.select(joinSqlTbl, selectMapping, null);
                        stmtMapping.setColumnPositions(colNumbers);
                    }
                    else {
                        selectFK = selectFetchPlanFieldsOfFKRelatedObject(stmt, mappingDefinition, fetchPlan, sourceSqlTbl, mmd, clr, maxFetchPlanLimit, m, tableGroupName, stmtMapping, sqlTbl);
                    }
                }
                if (selectFK) {
                    final int[] colNumbers2 = stmt.select(sqlTbl, m, null);
                    stmtMapping.setColumnPositions(colNumbers2);
                }
            }
            else if (relationType == RelationType.ONE_TO_ONE_BI && mmd.getMappedBy() != null) {
                final AbstractMemberMetaData[] relatedMmds2 = mmd.getRelatedMemberMetaData(clr);
                final AbstractMemberMetaData relatedMmd = relatedMmds2[0];
                String[] clsNames = null;
                if (mmd.getType().isInterface()) {
                    if (mmd.getFieldTypes() != null && mmd.getFieldTypes().length == 1) {
                        final Class fldTypeCls = clr.classForName(mmd.getFieldTypes()[0]);
                        if (fldTypeCls.isInterface()) {
                            clsNames = mmgr.getClassesImplementingInterface(mmd.getFieldTypes()[0], clr);
                        }
                        else {
                            clsNames = new String[] { mmd.getFieldTypes()[0] };
                        }
                    }
                    if (clsNames == null) {
                        clsNames = mmgr.getClassesImplementingInterface(mmd.getTypeName(), clr);
                    }
                }
                else {
                    clsNames = new String[] { mmd.getTypeName() };
                }
                final DatastoreClass relatedTbl = storeMgr.getDatastoreClass(clsNames[0], clr);
                final JavaTypeMapping relatedMapping = relatedTbl.getMemberMapping(relatedMmd);
                final JavaTypeMapping relatedDiscrimMapping = relatedTbl.getDiscriminatorMapping(true);
                Object[] discrimValues = null;
                JavaTypeMapping relatedTypeMapping = null;
                final AbstractClassMetaData relatedCmd = relatedMmd.getAbstractClassMetaData();
                if (relatedDiscrimMapping != null && (relatedCmd.getSuperAbstractClassMetaData() != null || !relatedCmd.getFullClassName().equals(mmd.getTypeName()))) {
                    List discValueList = null;
                    for (int i = 0; i < clsNames.length; ++i) {
                        final List values = getDiscriminatorValuesForMember(clsNames[i], relatedDiscrimMapping, storeMgr, clr);
                        if (discValueList == null) {
                            discValueList = values;
                        }
                        else {
                            discValueList.addAll(values);
                        }
                    }
                    discrimValues = discValueList.toArray(new Object[discValueList.size()]);
                }
                else if (relatedTbl != relatedMapping.getTable()) {
                    relatedTypeMapping = relatedTbl.getIdMapping();
                }
                SQLTable relatedSqlTbl = null;
                if (relatedTypeMapping == null) {
                    relatedSqlTbl = addJoinForOneToOneRelation(stmt, sourceSqlTbl.getTable().getIdMapping(), sourceSqlTbl, relatedMapping, relatedTbl, null, discrimValues, tableGroupName, null);
                    final int[] colNumbers3 = stmt.select(relatedSqlTbl, relatedTbl.getIdMapping(), null);
                    stmtMapping.setColumnPositions(colNumbers3);
                }
                else {
                    final DatastoreClass relationTbl = (DatastoreClass)relatedMapping.getTable();
                    if (relatedTbl != relatedMapping.getTable()) {
                        if (relatedMapping.isNullable()) {
                            relatedSqlTbl = stmt.leftOuterJoin(sourceSqlTbl, sourceSqlTbl.getTable().getIdMapping(), relatedMapping.getTable(), null, relatedMapping, null, tableGroupName);
                            relatedSqlTbl = stmt.innerJoin(relatedSqlTbl, relatedMapping.getTable().getIdMapping(), relatedTbl, null, relatedTbl.getIdMapping(), null, tableGroupName);
                        }
                        else {
                            relatedSqlTbl = stmt.innerJoin(sourceSqlTbl, sourceSqlTbl.getTable().getIdMapping(), relatedMapping.getTable(), null, relatedMapping, null, tableGroupName);
                            relatedSqlTbl = stmt.innerJoin(relatedSqlTbl, relatedMapping.getTable().getIdMapping(), relatedTbl, null, relatedTbl.getIdMapping(), null, tableGroupName);
                        }
                    }
                    else {
                        relatedSqlTbl = addJoinForOneToOneRelation(stmt, sourceSqlTbl.getTable().getIdMapping(), sourceSqlTbl, relatedMapping, relationTbl, null, null, tableGroupName, null);
                    }
                    relatedSqlTbl = getSQLTableForMappingOfTable(stmt, relatedSqlTbl, relatedTbl.getIdMapping());
                    final int[] colNumbers4 = stmt.select(relatedSqlTbl, relatedTbl.getIdMapping(), null);
                    stmtMapping.setColumnPositions(colNumbers4);
                }
                if (selectSubobjects && !mmd.isSerialized() && !mmd.isEmbedded()) {
                    final StatementClassMapping subMappingDefinition = new StatementClassMapping(mmd.getName());
                    selectFetchPlanOfSourceClassInStatement(stmt, subMappingDefinition, fetchPlan, relatedSqlTbl, relatedMmd.getAbstractClassMetaData(), maxFetchPlanLimit - 1);
                    if (mappingDefinition != null) {
                        mappingDefinition.addMappingDefinitionForMember(mmd.getAbsoluteFieldNumber(), subMappingDefinition);
                    }
                }
            }
            else if (relationType == RelationType.MANY_TO_ONE_BI) {
                final AbstractMemberMetaData[] relatedMmds2 = mmd.getRelatedMemberMetaData(clr);
                if (mmd.getJoinMetaData() != null || relatedMmds2[0].getJoinMetaData() != null) {
                    final Table joinTable2 = storeMgr.getTable(relatedMmds2[0]);
                    final DatastoreElementContainer collTable2 = (DatastoreElementContainer)joinTable2;
                    final JavaTypeMapping selectMapping2 = collTable2.getOwnerMapping();
                    SQLTable joinSqlTbl2 = null;
                    if (stmt.getPrimaryTable().getTable() != joinTable2) {
                        final JavaTypeMapping referenceMapping2 = collTable2.getElementMapping();
                        joinSqlTbl2 = stmt.leftOuterJoin(sourceSqlTbl, sourceSqlTbl.getTable().getIdMapping(), collTable2, null, referenceMapping2, null, tableGroupName);
                    }
                    else {
                        joinSqlTbl2 = stmt.getPrimaryTable();
                    }
                    final int[] colNumbers5 = stmt.select(joinSqlTbl2, selectMapping2, null);
                    stmtMapping.setColumnPositions(colNumbers5);
                }
            }
            else if (relationType == RelationType.MANY_TO_ONE_UNI) {
                final PersistableJoinTable joinTable3 = (PersistableJoinTable)storeMgr.getTable(mmd);
                final SQLTable joinSqlTbl3 = stmt.leftOuterJoin(sourceSqlTbl, sourceSqlTbl.getTable().getIdMapping(), joinTable3, null, joinTable3.getOwnerMapping(), null, tableGroupName);
                final int[] colNumbers2 = stmt.select(joinSqlTbl3, joinTable3.getRelatedMapping(), null);
                stmtMapping.setColumnPositions(colNumbers2);
            }
            if (mappingDefinition != null) {
                mappingDefinition.addMappingForMember(mmd.getAbsoluteFieldNumber(), stmtMapping);
            }
        }
    }
    
    private static boolean selectFetchPlanFieldsOfFKRelatedObject(final SQLStatement stmt, final StatementClassMapping mappingDefinition, final FetchPlan fetchPlan, final SQLTable sourceSqlTbl, final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final int maxFetchPlanLimit, final JavaTypeMapping m, final String tableGroupName, final StatementMappingIndex stmtMapping, final SQLTable sqlTbl) {
        boolean selectFK = true;
        if (!mmd.fetchFKOnly()) {
            final RDBMSStoreManager storeMgr = stmt.getRDBMSManager();
            final AbstractClassMetaData relatedCmd = storeMgr.getMetaDataManager().getMetaDataForClass(mmd.getType(), clr);
            if (relatedCmd != null) {
                DatastoreClass relatedTbl = storeMgr.getDatastoreClass(relatedCmd.getFullClassName(), clr);
                if (relatedTbl == null) {
                    final AbstractClassMetaData[] ownerParentCmds = storeMgr.getClassesManagingTableForClass(relatedCmd, clr);
                    if (ownerParentCmds.length > 1) {
                        throw new NucleusUserException("Relation (" + mmd.getFullFieldName() + ") with multiple related tables (using subclass-table). Not supported");
                    }
                    relatedTbl = storeMgr.getDatastoreClass(ownerParentCmds[0].getFullClassName(), clr);
                }
                String requiredGroupName = null;
                if (sourceSqlTbl.getGroupName() != null) {
                    requiredGroupName = sourceSqlTbl.getGroupName() + "." + mmd.getName();
                }
                SQLTable relatedSqlTbl = stmt.getTable(relatedTbl, requiredGroupName);
                if (relatedSqlTbl == null) {
                    relatedSqlTbl = addJoinForOneToOneRelation(stmt, m, sqlTbl, relatedTbl.getIdMapping(), relatedTbl, null, null, tableGroupName, null);
                }
                final StatementClassMapping subMappingDefinition = new StatementClassMapping(mmd.getClassName(), mmd.getName());
                selectFetchPlanOfSourceClassInStatement(stmt, subMappingDefinition, fetchPlan, relatedSqlTbl, relatedCmd, maxFetchPlanLimit - 1);
                if (mappingDefinition != null) {
                    if (relatedCmd.getIdentityType() == IdentityType.APPLICATION) {
                        final int[] pkFields = relatedCmd.getPKMemberPositions();
                        final int[] pkCols = new int[m.getNumberOfDatastoreMappings()];
                        int pkColNo = 0;
                        for (int i = 0; i < pkFields.length; ++i) {
                            final StatementMappingIndex pkIdx = subMappingDefinition.getMappingForMemberPosition(pkFields[i]);
                            final int[] pkColNumbers = pkIdx.getColumnPositions();
                            for (int j = 0; j < pkColNumbers.length; ++j) {
                                pkCols[pkColNo] = pkColNumbers[j];
                                ++pkColNo;
                            }
                        }
                        selectFK = false;
                        stmtMapping.setColumnPositions(pkCols);
                    }
                    else if (relatedCmd.getIdentityType() == IdentityType.DATASTORE) {
                        final StatementMappingIndex pkIdx2 = subMappingDefinition.getMappingForMemberPosition(-1);
                        selectFK = false;
                        stmtMapping.setColumnPositions(pkIdx2.getColumnPositions());
                    }
                    mappingDefinition.addMappingDefinitionForMember(mmd.getAbsoluteFieldNumber(), subMappingDefinition);
                }
            }
        }
        return selectFK;
    }
    
    public static SQLTable addJoinForOneToOneRelation(final SQLStatement stmt, final JavaTypeMapping sourceMapping, final SQLTable sourceSqlTbl, final JavaTypeMapping targetMapping, final Table targetTable, final String targetAlias, final Object[] discrimValues, final String targetTablegroupName, SQLJoin.JoinType joinType) {
        if (joinType == null) {
            joinType = SQLJoin.JoinType.LEFT_OUTER_JOIN;
            if (sourceMapping != sourceSqlTbl.getTable().getIdMapping()) {
                joinType = (sourceMapping.isNullable() ? SQLJoin.JoinType.LEFT_OUTER_JOIN : SQLJoin.JoinType.INNER_JOIN);
            }
        }
        SQLTable targetSqlTbl = null;
        if (joinType == SQLJoin.JoinType.LEFT_OUTER_JOIN) {
            targetSqlTbl = stmt.leftOuterJoin(sourceSqlTbl, sourceMapping, targetTable, targetAlias, targetMapping, discrimValues, targetTablegroupName);
        }
        else if (joinType == SQLJoin.JoinType.INNER_JOIN) {
            targetSqlTbl = stmt.innerJoin(sourceSqlTbl, sourceMapping, targetTable, targetAlias, targetMapping, discrimValues, targetTablegroupName);
        }
        else if (joinType == SQLJoin.JoinType.RIGHT_OUTER_JOIN) {
            targetSqlTbl = stmt.rightOuterJoin(sourceSqlTbl, sourceMapping, targetTable, targetAlias, targetMapping, discrimValues, targetTablegroupName);
        }
        else if (joinType == SQLJoin.JoinType.CROSS_JOIN) {
            targetSqlTbl = stmt.crossJoin(targetTable, targetAlias, targetTablegroupName);
        }
        return targetSqlTbl;
    }
    
    public static BooleanExpression getExpressionForDiscriminatorForClass(final SQLStatement stmt, final String className, final DiscriminatorMetaData dismd, final JavaTypeMapping discriminatorMapping, final SQLTable discrimSqlTbl, final ClassLoaderResolver clr) {
        Object discriminatorValue = className;
        Label_0133: {
            if (dismd.getStrategy() == DiscriminatorStrategy.VALUE_MAP) {
                final NucleusContext nucleusCtx = stmt.getRDBMSManager().getNucleusContext();
                final AbstractClassMetaData targetCmd = nucleusCtx.getMetaDataManager().getMetaDataForClass(className, clr);
                String strValue = null;
                if (targetCmd.getInheritanceMetaData() != null && targetCmd.getInheritanceMetaData().getDiscriminatorMetaData() != null) {
                    strValue = targetCmd.getInheritanceMetaData().getDiscriminatorMetaData().getValue();
                }
                if (strValue == null) {
                    strValue = className;
                }
                if (discriminatorMapping instanceof DiscriminatorLongMapping) {
                    try {
                        discriminatorValue = Integer.valueOf(strValue);
                        break Label_0133;
                    }
                    catch (NumberFormatException nfe) {
                        throw new NucleusUserException("Discriminator for " + className + " is not integer-based but needs to be!");
                    }
                }
                discriminatorValue = strValue;
            }
        }
        final SQLExpression discrExpr = stmt.getSQLExpressionFactory().newExpression(stmt, discrimSqlTbl, discriminatorMapping);
        final SQLExpression discrVal = stmt.getSQLExpressionFactory().newLiteral(stmt, discriminatorMapping, discriminatorValue);
        return discrExpr.eq(discrVal);
    }
    
    public static List getDiscriminatorValuesForMember(final String className, final JavaTypeMapping discMapping, final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        final List discrimValues = new ArrayList();
        final DiscriminatorStrategy strategy = discMapping.getTable().getDiscriminatorMetaData().getStrategy();
        if (strategy == DiscriminatorStrategy.CLASS_NAME) {
            discrimValues.add(className);
            final Collection<String> subclasses = storeMgr.getSubClassesForClass(className, true, clr);
            if (subclasses != null && subclasses.size() > 0) {
                discrimValues.addAll(subclasses);
            }
        }
        else if (strategy == DiscriminatorStrategy.VALUE_MAP) {
            final MetaDataManager mmgr = storeMgr.getMetaDataManager();
            final AbstractClassMetaData cmd = mmgr.getMetaDataForClass(className, clr);
            final Collection<String> subclasses2 = storeMgr.getSubClassesForClass(className, true, clr);
            discrimValues.add(cmd.getInheritanceMetaData().getDiscriminatorMetaData().getValue());
            if (subclasses2 != null && subclasses2.size() > 0) {
                for (final String subclassName : subclasses2) {
                    final AbstractClassMetaData subclassCmd = mmgr.getMetaDataForClass(subclassName, clr);
                    discrimValues.add(subclassCmd.getInheritanceMetaData().getDiscriminatorMetaData().getValue());
                }
            }
        }
        return discrimValues;
    }
}
