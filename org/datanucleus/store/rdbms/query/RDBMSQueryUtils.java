// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import java.sql.ResultSetMetaData;
import org.datanucleus.store.rdbms.sql.StatementGenerator;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.sql.UnionStatementGenerator;
import org.datanucleus.store.rdbms.sql.DiscriminatorStatementGenerator;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import org.datanucleus.store.rdbms.sql.expression.StringLiteral;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.PersistenceConfiguration;
import org.datanucleus.NucleusContext;
import org.datanucleus.store.rdbms.SQLController;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.exceptions.NucleusUserException;
import java.sql.PreparedStatement;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.query.Query;
import java.sql.SQLException;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.ExecutionContext;
import java.sql.ResultSet;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.util.Localiser;
import org.datanucleus.query.QueryUtils;

public class RDBMSQueryUtils extends QueryUtils
{
    protected static final Localiser LOCALISER_RDBMS;
    
    public static String getClassNameFromDiscriminatorResultSetRow(final JavaTypeMapping discrimMapping, final DiscriminatorMetaData dismd, final ResultSet rs, final ExecutionContext ec) {
        String rowClassName = null;
        if (discrimMapping != null && dismd.getStrategy() != DiscriminatorStrategy.NONE) {
            try {
                final String discriminatorColName = discrimMapping.getDatastoreMapping(0).getColumn().getIdentifier().getIdentifierName();
                final String discriminatorValue = rs.getString(discriminatorColName);
                rowClassName = ec.getMetaDataManager().getClassNameFromDiscriminatorValue(discriminatorValue, dismd);
            }
            catch (SQLException ex) {}
        }
        return rowClassName;
    }
    
    public static String getResultSetTypeForQuery(final Query query) {
        String rsTypeString = query.getExecutionContext().getNucleusContext().getPersistenceConfiguration().getStringProperty("datanucleus.rdbms.query.resultSetType");
        final Object rsTypeExt = query.getExtension("datanucleus.rdbms.query.resultSetType");
        if (rsTypeExt != null) {
            rsTypeString = (String)rsTypeExt;
        }
        return rsTypeString;
    }
    
    public static String getResultSetConcurrencyForQuery(final Query query) {
        String rsConcurrencyString = query.getExecutionContext().getNucleusContext().getPersistenceConfiguration().getStringProperty("datanucleus.rdbms.query.resultSetConcurrency");
        final Object rsConcurrencyExt = query.getExtension("datanucleus.rdbms.query.resultSetConcurrency");
        if (rsConcurrencyExt != null) {
            rsConcurrencyString = (String)rsConcurrencyExt;
        }
        return rsConcurrencyString;
    }
    
    public static boolean useUpdateLockForQuery(final Query query) {
        if (query.getSerializeRead() != null) {
            return query.getExecutionContext().getTransaction().isActive() && query.getSerializeRead();
        }
        return query.getExecutionContext().getSerializeReadForClass(query.getCandidateClassName());
    }
    
    public static PreparedStatement getPreparedStatementForQuery(final ManagedConnection conn, final String queryStmt, final Query query) throws SQLException {
        final String rsTypeString = getResultSetTypeForQuery(query);
        if (rsTypeString != null && !rsTypeString.equals("scroll-sensitive") && !rsTypeString.equals("forward-only") && !rsTypeString.equals("scroll-insensitive")) {
            throw new NucleusUserException(RDBMSQueryUtils.LOCALISER.msg("052510"));
        }
        final String rsConcurrencyString = getResultSetConcurrencyForQuery(query);
        if (rsConcurrencyString != null && !rsConcurrencyString.equals("read-only") && !rsConcurrencyString.equals("updateable")) {
            throw new NucleusUserException(RDBMSQueryUtils.LOCALISER.msg("052511"));
        }
        final SQLController sqlControl = ((RDBMSStoreManager)query.getStoreManager()).getSQLController();
        final PreparedStatement ps = sqlControl.getStatementForQuery(conn, queryStmt, rsTypeString, rsConcurrencyString);
        return ps;
    }
    
    public static void prepareStatementForExecution(final PreparedStatement ps, final Query query, final boolean applyTimeout) throws SQLException {
        final NucleusContext nucleusCtx = query.getExecutionContext().getNucleusContext();
        final RDBMSStoreManager storeMgr = (RDBMSStoreManager)query.getStoreManager();
        final PersistenceConfiguration conf = nucleusCtx.getPersistenceConfiguration();
        if (applyTimeout) {
            final Integer timeout = query.getDatastoreReadTimeoutMillis();
            if (timeout != null && timeout > 0) {
                ps.setQueryTimeout(timeout / 1000);
            }
        }
        int fetchSize = 0;
        if (query.getFetchPlan().getFetchSize() > 0) {
            fetchSize = query.getFetchPlan().getFetchSize();
        }
        if (storeMgr.getDatastoreAdapter().supportsQueryFetchSize(fetchSize)) {
            ps.setFetchSize(fetchSize);
        }
        String fetchDir = conf.getStringProperty("datanucleus.rdbms.query.fetchDirection");
        final Object fetchDirExt = query.getExtension("datanucleus.rdbms.query.fetchDirection");
        if (fetchDirExt != null) {
            fetchDir = (String)fetchDirExt;
            if (!fetchDir.equals("forward") && !fetchDir.equals("reverse") && !fetchDir.equals("unknown")) {
                throw new NucleusUserException(RDBMSQueryUtils.LOCALISER.msg("052512"));
            }
        }
        if (fetchDir.equals("reverse")) {
            ps.setFetchDirection(1001);
        }
        else if (fetchDir.equals("unknown")) {
            ps.setFetchDirection(1002);
        }
        final long toExclNo = query.getRangeToExcl();
        if (toExclNo != 0L && toExclNo != Long.MAX_VALUE) {
            if (toExclNo > 2147483647L) {
                ps.setMaxRows(Integer.MAX_VALUE);
            }
            else {
                ps.setMaxRows((int)toExclNo);
            }
        }
    }
    
    public static SQLStatement getStatementForCandidates(final RDBMSStoreManager storeMgr, final SQLStatement parentStmt, final AbstractClassMetaData cmd, final StatementClassMapping clsMapping, final ExecutionContext ec, final Class candidateCls, final boolean subclasses, final String result, final String candidateAlias, final String candidateTableGroupName) {
        SQLStatement stmt = null;
        DatastoreIdentifier candidateAliasId = null;
        if (candidateAlias != null) {
            candidateAliasId = storeMgr.getIdentifierFactory().newTableIdentifier(candidateAlias);
        }
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        final List<DatastoreClass> candidateTables = new ArrayList<DatastoreClass>();
        if (cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.COMPLETE_TABLE) {
            final DatastoreClass candidateTable = storeMgr.getDatastoreClass(cmd.getFullClassName(), clr);
            if (candidateTable != null) {
                candidateTables.add(candidateTable);
            }
            if (subclasses) {
                final Collection<String> subclassNames = storeMgr.getSubClassesForClass(cmd.getFullClassName(), subclasses, clr);
                if (subclassNames != null) {
                    for (final String subclassName : subclassNames) {
                        final DatastoreClass tbl = storeMgr.getDatastoreClass(subclassName, clr);
                        if (tbl != null) {
                            candidateTables.add(tbl);
                        }
                    }
                }
            }
            Iterator<DatastoreClass> iter = candidateTables.iterator();
            int maxClassNameLength = cmd.getFullClassName().length();
            while (iter.hasNext()) {
                final DatastoreClass cls = iter.next();
                final String className = cls.getType();
                if (className.length() > maxClassNameLength) {
                    maxClassNameLength = className.length();
                }
            }
            iter = candidateTables.iterator();
            while (iter.hasNext()) {
                final DatastoreClass cls = iter.next();
                final SQLStatement tblStmt = new SQLStatement(parentStmt, storeMgr, cls, candidateAliasId, candidateTableGroupName);
                tblStmt.setClassLoaderResolver(clr);
                tblStmt.setCandidateClassName(cls.getType());
                final JavaTypeMapping m = storeMgr.getMappingManager().getMapping(String.class);
                String nuctypeName = cls.getType();
                if (maxClassNameLength > nuctypeName.length()) {
                    nuctypeName = StringUtils.leftAlignedPaddedString(nuctypeName, maxClassNameLength);
                }
                final StringLiteral lit = new StringLiteral(tblStmt, m, nuctypeName, (String)null);
                tblStmt.select(lit, "NUCLEUS_TYPE");
                if (stmt == null) {
                    stmt = tblStmt;
                }
                else {
                    stmt.union(tblStmt);
                }
            }
            if (clsMapping != null) {
                clsMapping.setNucleusTypeColumnName("NUCLEUS_TYPE");
            }
        }
        else {
            final List<Class> candidateClasses = new ArrayList<Class>();
            if (ClassUtils.isReferenceType(candidateCls)) {
                final String[] clsNames = storeMgr.getNucleusContext().getMetaDataManager().getClassesImplementingInterface(candidateCls.getName(), clr);
                for (int i = 0; i < clsNames.length; ++i) {
                    final Class cls2 = clr.classForName(clsNames[i]);
                    final DatastoreClass table = storeMgr.getDatastoreClass(clsNames[i], clr);
                    candidateClasses.add(cls2);
                    candidateTables.add(table);
                    final AbstractClassMetaData implCmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(cls2, clr);
                    if (implCmd.getIdentityType() != cmd.getIdentityType()) {
                        throw new NucleusUserException("You are querying an interface (" + cmd.getFullClassName() + ") " + "yet one of its implementations (" + implCmd.getFullClassName() + ") " + " uses a different identity type!");
                    }
                    if (cmd.getIdentityType() == IdentityType.APPLICATION && cmd.getPKMemberPositions().length != implCmd.getPKMemberPositions().length) {
                        throw new NucleusUserException("You are querying an interface (" + cmd.getFullClassName() + ") " + "yet one of its implementations (" + implCmd.getFullClassName() + ") " + " has a different number of PK members!");
                    }
                }
            }
            else {
                final DatastoreClass candidateTable2 = storeMgr.getDatastoreClass(cmd.getFullClassName(), clr);
                if (candidateTable2 != null) {
                    candidateClasses.add(candidateCls);
                    candidateTables.add(candidateTable2);
                }
                else {
                    final AbstractClassMetaData[] cmds = storeMgr.getClassesManagingTableForClass(cmd, clr);
                    if (cmds == null || cmds.length <= 0) {
                        throw new UnsupportedOperationException("No tables for query of " + cmd.getFullClassName());
                    }
                    for (int j = 0; j < cmds.length; ++j) {
                        final DatastoreClass table = storeMgr.getDatastoreClass(cmds[j].getFullClassName(), clr);
                        final Class cls3 = clr.classForName(cmds[j].getFullClassName());
                        candidateClasses.add(cls3);
                        candidateTables.add(table);
                    }
                }
            }
            for (int k = 0; k < candidateTables.size(); ++k) {
                final DatastoreClass tbl2 = candidateTables.get(k);
                final Class cls2 = candidateClasses.get(k);
                StatementGenerator stmtGen = null;
                if (tbl2.getDiscriminatorMapping(true) != null || QueryUtils.resultHasOnlyAggregates(result)) {
                    stmtGen = new DiscriminatorStatementGenerator(storeMgr, clr, cls2, subclasses, candidateAliasId, candidateTableGroupName);
                    stmtGen.setOption("restrictDiscriminator");
                }
                else {
                    stmtGen = new UnionStatementGenerator(storeMgr, clr, cls2, subclasses, candidateAliasId, candidateTableGroupName);
                    if (result == null) {
                        stmtGen.setOption("selectNucleusType");
                        clsMapping.setNucleusTypeColumnName("NUCLEUS_TYPE");
                    }
                }
                stmtGen.setParentStatement(parentStmt);
                final SQLStatement tblStmt2 = stmtGen.getStatement();
                if (stmt == null) {
                    stmt = tblStmt2;
                }
                else {
                    stmt.union(tblStmt2);
                }
            }
        }
        return stmt;
    }
    
    public static ResultObjectFactory getResultObjectFactoryForNoCandidateClass(final RDBMSStoreManager storeMgr, final ResultSet rs, final Class resultClass) {
        Class requiredResultClass = resultClass;
        int numberOfColumns = 0;
        String[] resultFieldNames = null;
        try {
            final ResultSetMetaData rsmd = rs.getMetaData();
            numberOfColumns = rsmd.getColumnCount();
            if (requiredResultClass == null) {
                if (numberOfColumns == 1) {
                    requiredResultClass = Object.class;
                }
                else {
                    requiredResultClass = Object[].class;
                }
            }
            resultFieldNames = new String[numberOfColumns];
            for (int i = 0; i < numberOfColumns; ++i) {
                final String colName = rsmd.getColumnName(i + 1);
                final String colLabel = rsmd.getColumnLabel(i + 1);
                if (StringUtils.isWhitespace(colLabel)) {
                    resultFieldNames[i] = colName;
                }
                else {
                    resultFieldNames[i] = colLabel;
                }
            }
        }
        catch (SQLException ex) {}
        return new ResultClassROF(storeMgr, requiredResultClass, resultFieldNames);
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
