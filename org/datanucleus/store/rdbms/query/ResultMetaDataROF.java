// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import org.datanucleus.FetchPlan;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.FieldValues;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import java.util.Iterator;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import java.util.Map;
import java.util.Set;
import java.sql.ResultSetMetaData;
import java.util.List;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.NucleusLogger;
import java.util.Collection;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.metadata.IdentityType;
import java.util.HashMap;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.HashSet;
import java.sql.SQLException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.util.StringUtils;
import java.util.ArrayList;
import java.sql.ResultSet;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.QueryResultMetaData;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;

public class ResultMetaDataROF implements ResultObjectFactory
{
    protected static final Localiser LOCALISER;
    RDBMSStoreManager storeMgr;
    QueryResultMetaData queryResultMetaData;
    String[] columnNames;
    private boolean ignoreCache;
    
    public ResultMetaDataROF(final RDBMSStoreManager storeMgr, final QueryResultMetaData qrmd) {
        this.queryResultMetaData = null;
        this.columnNames = null;
        this.ignoreCache = false;
        this.storeMgr = storeMgr;
        this.queryResultMetaData = qrmd;
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet rs) {
        final List returnObjects = new ArrayList();
        if (this.columnNames == null) {
            try {
                final ResultSetMetaData rsmd = rs.getMetaData();
                final int columnCount = rsmd.getColumnCount();
                this.columnNames = new String[columnCount];
                for (int i = 0; i < columnCount; ++i) {
                    final String colName = rsmd.getColumnName(i + 1);
                    final String colLabel = rsmd.getColumnLabel(i + 1);
                    this.columnNames[i] = (StringUtils.isWhitespace(colLabel) ? colName : colLabel);
                }
            }
            catch (SQLException ex) {
                throw new NucleusDataStoreException("Error obtaining objects", ex);
            }
        }
        final QueryResultMetaData.PersistentTypeMapping[] persistentTypes = this.queryResultMetaData.getPersistentTypeMappings();
        if (persistentTypes != null) {
            int startColumnIndex = 0;
            for (int i = 0; i < persistentTypes.length; ++i) {
                final Set columnsInThisType = new HashSet();
                final AbstractMemberMetaData[] fmds = new AbstractMemberMetaData[this.columnNames.length];
                final Map fieldColumns = new HashMap();
                final DatastoreClass dc = this.storeMgr.getDatastoreClass(persistentTypes[i].getClassName(), ec.getClassLoaderResolver());
                final AbstractClassMetaData acmd = ec.getMetaDataManager().getMetaDataForClass(persistentTypes[i].getClassName(), ec.getClassLoaderResolver());
                Object id = null;
                for (int j = startColumnIndex; j < this.columnNames.length; ++j) {
                    if (columnsInThisType.contains(this.columnNames[j])) {
                        startColumnIndex = j;
                        break;
                    }
                    boolean found = false;
                    if (acmd.getIdentityType() == IdentityType.DATASTORE) {
                        final Column df = dc.getDatastoreObjectIdMapping().getDatastoreMapping(0).getColumn();
                        if (df.getIdentifier().getIdentifierName().equalsIgnoreCase(this.columnNames[j])) {
                            final int datastoreIdentityExpressionIndex = j + 1;
                            if (dc.getDatastoreObjectIdMapping() != null) {
                                id = dc.getDatastoreObjectIdMapping().getObject(ec, rs, new int[] { datastoreIdentityExpressionIndex });
                            }
                            found = true;
                        }
                    }
                    for (int k = 0; k < acmd.getNoOfManagedMembers() + acmd.getNoOfInheritedManagedMembers() && !found; ++k) {
                        final AbstractMemberMetaData apmd = acmd.getMetaDataForManagedMemberAtAbsolutePosition(k);
                        if (persistentTypes[i].getColumnForField(apmd.getName()) != null) {
                            if (persistentTypes[i].getColumnForField(apmd.getName()).equalsIgnoreCase(this.columnNames[j])) {
                                fieldColumns.put(this.columnNames[j], apmd);
                                columnsInThisType.add(this.columnNames[j]);
                                fmds[j] = apmd;
                                found = true;
                            }
                        }
                        else {
                            final JavaTypeMapping mapping = dc.getMemberMapping(apmd);
                            for (int l = 0; l < mapping.getDatastoreMappings().length && !found; ++l) {
                                final Column df2 = mapping.getDatastoreMapping(l).getColumn();
                                if (df2.getIdentifier().getIdentifierName().equalsIgnoreCase(this.columnNames[j])) {
                                    fieldColumns.put(this.columnNames[j], apmd);
                                    columnsInThisType.add(this.columnNames[j]);
                                    fmds[j] = apmd;
                                    found = true;
                                }
                            }
                        }
                    }
                    if (!columnsInThisType.contains(this.columnNames[j])) {
                        startColumnIndex = j;
                        break;
                    }
                }
                final StatementMappingIndex[] stmtMappings = new StatementMappingIndex[acmd.getNoOfManagedMembers() + acmd.getNoOfInheritedManagedMembers()];
                final Set fields = new HashSet();
                fields.addAll(fieldColumns.values());
                final int[] fieldNumbers = new int[fields.size()];
                final Iterator it = fields.iterator();
                int m = 0;
                while (it.hasNext()) {
                    final AbstractMemberMetaData apmd2 = it.next();
                    final StatementMappingIndex stmtMapping = new StatementMappingIndex(dc.getMemberMapping(apmd2));
                    fieldNumbers[m] = apmd2.getAbsoluteFieldNumber();
                    final List indexes = new ArrayList();
                    for (int k2 = 0; k2 < fmds.length; ++k2) {
                        if (fmds[k2] == apmd2) {
                            indexes.add(k2);
                        }
                    }
                    final int[] indxs = new int[indexes.size()];
                    for (int k3 = 0; k3 < indxs.length; ++k3) {
                        indxs[k3] = indexes.get(k3) + 1;
                    }
                    stmtMapping.setColumnPositions(indxs);
                    stmtMappings[fieldNumbers[m]] = stmtMapping;
                    ++m;
                }
                Object obj = null;
                final Class type = ec.getClassLoaderResolver().classForName(persistentTypes[i].getClassName());
                if (acmd.getIdentityType() == IdentityType.APPLICATION) {
                    obj = this.getObjectForApplicationId(ec, rs, fieldNumbers, acmd, type, false, stmtMappings);
                }
                else if (acmd.getIdentityType() == IdentityType.DATASTORE) {
                    obj = this.getObjectForDatastoreId(ec, rs, fieldNumbers, acmd, id, type, stmtMappings);
                }
                returnObjects.add(obj);
            }
        }
        final String[] columns = this.queryResultMetaData.getScalarColumns();
        if (columns != null) {
            for (int i = 0; i < columns.length; ++i) {
                try {
                    final Object obj2 = this.getResultObject(rs, columns[i]);
                    returnObjects.add(obj2);
                }
                catch (SQLException sqe) {
                    final String msg = ResultMetaDataROF.LOCALISER.msg("059027", sqe.getMessage());
                    NucleusLogger.QUERY.error(msg);
                    throw new NucleusUserException(msg, sqe);
                }
            }
        }
        if (returnObjects.size() == 0) {
            return null;
        }
        if (returnObjects.size() == 1) {
            return returnObjects.get(0);
        }
        return returnObjects.toArray(new Object[returnObjects.size()]);
    }
    
    private Object getResultObject(final ResultSet rs, final String columnName) throws SQLException {
        return rs.getObject(columnName);
    }
    
    private Object getObjectForApplicationId(final ExecutionContext ec, final ResultSet rs, final int[] fieldNumbers, final AbstractClassMetaData cmd, final Class pcClass, final boolean requiresInheritanceCheck, final StatementMappingIndex[] stmtMappings) {
        final StatementClassMapping resultMappings = new StatementClassMapping();
        for (int i = 0; i < fieldNumbers.length; ++i) {
            resultMappings.addMappingForMember(fieldNumbers[i], stmtMappings[fieldNumbers[i]]);
        }
        final Object id = IdentityUtils.getApplicationIdentityForResultSetRow(ec, cmd, null, requiresInheritanceCheck, this.storeMgr.getFieldManagerForResultProcessing(ec, rs, resultMappings, cmd));
        return ec.findObject(id, new FieldValues() {
            @Override
            public void fetchFields(final ObjectProvider sm) {
                final FieldManager fm = ResultMetaDataROF.this.storeMgr.getFieldManagerForResultProcessing(sm, rs, resultMappings);
                sm.replaceFields(fieldNumbers, fm, false);
            }
            
            @Override
            public void fetchNonLoadedFields(final ObjectProvider sm) {
                final FieldManager fm = ResultMetaDataROF.this.storeMgr.getFieldManagerForResultProcessing(sm, rs, resultMappings);
                sm.replaceNonLoadedFields(fieldNumbers, fm);
            }
            
            @Override
            public FetchPlan getFetchPlanForLoading() {
                return null;
            }
        }, pcClass, this.ignoreCache, false);
    }
    
    private Object getObjectForDatastoreId(final ExecutionContext ec, final ResultSet rs, final int[] fieldNumbers, final AbstractClassMetaData cmd, final Object oid, final Class pcClass, final StatementMappingIndex[] stmtMappings) {
        final StatementClassMapping resultMappings = new StatementClassMapping();
        for (int i = 0; i < fieldNumbers.length; ++i) {
            resultMappings.addMappingForMember(fieldNumbers[i], stmtMappings[fieldNumbers[i]]);
        }
        return ec.findObject(oid, new FieldValues() {
            @Override
            public void fetchFields(final ObjectProvider sm) {
                final FieldManager fm = ResultMetaDataROF.this.storeMgr.getFieldManagerForResultProcessing(sm, rs, resultMappings);
                sm.replaceFields(fieldNumbers, fm, false);
            }
            
            @Override
            public void fetchNonLoadedFields(final ObjectProvider sm) {
                final FieldManager fm = ResultMetaDataROF.this.storeMgr.getFieldManagerForResultProcessing(sm, rs, resultMappings);
                sm.replaceNonLoadedFields(fieldNumbers, fm);
            }
            
            @Override
            public FetchPlan getFetchPlanForLoading() {
                return ec.getFetchPlan();
            }
        }, pcClass, this.ignoreCache, false);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
