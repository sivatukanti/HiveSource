// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.ClassConstants;
import java.util.Iterator;
import org.datanucleus.metadata.DiscriminatorMetaData;
import java.lang.reflect.Modifier;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.store.rdbms.mapping.java.DiscriminatorMapping;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedMapping;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.store.rdbms.mapping.java.StringMapping;
import org.datanucleus.store.rdbms.mapping.java.SqlTimestampMapping;
import org.datanucleus.store.rdbms.mapping.java.SqlTimeMapping;
import org.datanucleus.store.rdbms.mapping.java.SqlDateMapping;
import org.datanucleus.store.rdbms.mapping.java.DateMapping;
import org.datanucleus.store.rdbms.mapping.java.ShortMapping;
import org.datanucleus.store.rdbms.mapping.java.BigIntegerMapping;
import org.datanucleus.store.rdbms.mapping.java.LongMapping;
import org.datanucleus.store.rdbms.mapping.java.IntegerMapping;
import org.datanucleus.store.rdbms.mapping.java.BigDecimalMapping;
import org.datanucleus.store.rdbms.mapping.java.DoubleMapping;
import org.datanucleus.store.rdbms.mapping.java.FloatMapping;
import org.datanucleus.store.rdbms.mapping.java.CharacterMapping;
import org.datanucleus.store.rdbms.mapping.java.ByteMapping;
import org.datanucleus.store.rdbms.mapping.java.BooleanMapping;
import org.datanucleus.identity.OID;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.identity.OIDFactory;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.rdbms.mapping.java.PersistableIdMapping;
import java.util.List;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.util.Localiser;

public class ObjectExpression extends SQLExpression
{
    protected static final Localiser LOCALISER_CORE;
    
    public ObjectExpression(final SQLStatement stmt, final SQLTable table, final JavaTypeMapping mapping) {
        super(stmt, table, mapping);
    }
    
    public ObjectExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List args) {
        super(stmt, mapping, functionName, args, null);
    }
    
    public ObjectExpression(final SQLStatement stmt, final JavaTypeMapping mapping, final String functionName, final List args, final List types) {
        super(stmt, mapping, functionName, args, types);
    }
    
    public void useFirstColumnOnly() {
        if (this.mapping.getNumberOfDatastoreMappings() <= 1) {
            return;
        }
        this.subExprs = new ColumnExpressionList();
        final ColumnExpression colExpr = new ColumnExpression(this.stmt, this.table, this.mapping.getDatastoreMapping(0).getColumn());
        this.subExprs.addExpression(colExpr);
        this.st.clearStatement();
        this.st.append(this.subExprs.toString());
    }
    
    @Override
    public BooleanExpression eq(final SQLExpression expr) {
        this.addSubexpressionsToRelatedExpression(expr);
        Label_0218: {
            if (this.mapping instanceof PersistableIdMapping && expr instanceof StringLiteral) {
                final String oidString = (String)((StringLiteral)expr).getValue();
                if (oidString != null) {
                    final AbstractClassMetaData cmd = this.stmt.getRDBMSManager().getMetaDataManager().getMetaDataForClass(this.mapping.getType(), this.stmt.getQueryGenerator().getClassLoaderResolver());
                    if (cmd.getIdentityType() == IdentityType.DATASTORE) {
                        try {
                            final OID oid = OIDFactory.getInstance(this.stmt.getRDBMSManager().getNucleusContext(), oidString);
                            if (oid == null) {}
                            break Label_0218;
                        }
                        catch (IllegalArgumentException iae) {
                            NucleusLogger.QUERY.info("Attempted comparison of " + this + " and " + expr + " where the former is a datastore-identity and the latter is of incorrect form (" + oidString + ")");
                            final SQLExpressionFactory exprFactory = this.stmt.getSQLExpressionFactory();
                            final JavaTypeMapping m = exprFactory.getMappingForType(Boolean.TYPE, true);
                            return exprFactory.newLiteral(this.stmt, m, false).eq(exprFactory.newLiteral(this.stmt, m, true));
                        }
                    }
                    if (cmd.getIdentityType() == IdentityType.APPLICATION) {}
                }
            }
        }
        if (this.mapping instanceof ReferenceMapping && expr.mapping instanceof PersistableMapping) {
            return this.processComparisonOfImplementationWithReference(this, expr, false);
        }
        if (this.mapping instanceof PersistableMapping && expr.mapping instanceof ReferenceMapping) {
            return this.processComparisonOfImplementationWithReference(expr, this, false);
        }
        BooleanExpression bExpr = null;
        if (this.isParameter() || expr.isParameter()) {
            if (this.subExprs.size() > 1) {
                for (int i = 0; i < this.subExprs.size(); ++i) {
                    final BooleanExpression subexpr = this.subExprs.getExpression(i).eq(((ObjectExpression)expr).subExprs.getExpression(i));
                    bExpr = ((bExpr == null) ? subexpr : bExpr.and(subexpr));
                }
                return bExpr;
            }
            return new BooleanExpression(this, Expression.OP_EQ, expr);
        }
        else {
            if (expr instanceof NullLiteral) {
                for (int i = 0; i < this.subExprs.size(); ++i) {
                    final BooleanExpression subexpr = expr.eq(this.subExprs.getExpression(i));
                    bExpr = ((bExpr == null) ? subexpr : bExpr.and(subexpr));
                }
                return bExpr;
            }
            if (this.literalIsValidForSimpleComparison(expr)) {
                if (this.subExprs.size() > 1) {
                    return super.eq(expr);
                }
                return new BooleanExpression(this, Expression.OP_EQ, expr);
            }
            else {
                if (expr instanceof ObjectExpression) {
                    return ExpressionUtils.getEqualityExpressionForObjectExpressions(this, (ObjectExpression)expr, true);
                }
                if (this.subExprs == null) {
                    return new BooleanExpression(this, Expression.OP_EQ, expr);
                }
                return super.eq(expr);
            }
        }
    }
    
    protected BooleanExpression processComparisonOfImplementationWithReference(final SQLExpression refExpr, final SQLExpression implExpr, final boolean negate) {
        final ReferenceMapping refMapping = (ReferenceMapping)refExpr.mapping;
        final JavaTypeMapping[] implMappings = refMapping.getJavaTypeMapping();
        int subExprStart = 0;
        int subExprEnd = 0;
        for (int i = 0; i < implMappings.length; ++i) {
            if (implMappings[i].getType().equals(implExpr.mapping.getType())) {
                subExprEnd = subExprStart + implMappings[i].getNumberOfDatastoreMappings();
                break;
            }
            subExprStart += implMappings[i].getNumberOfDatastoreMappings();
        }
        BooleanExpression bExpr = null;
        int implMappingNum = 0;
        for (int j = subExprStart; j < subExprEnd; ++j) {
            final BooleanExpression subexpr = refExpr.subExprs.getExpression(j).eq(implExpr.subExprs.getExpression(implMappingNum++));
            bExpr = ((bExpr == null) ? subexpr : bExpr.and(subexpr));
        }
        if (bExpr == null) {
            return ExpressionUtils.getEqualityExpressionForObjectExpressions((ObjectExpression)refExpr, (ObjectExpression)implExpr, true);
        }
        return negate ? new BooleanExpression(Expression.OP_NOT, bExpr.encloseInParentheses()) : bExpr;
    }
    
    @Override
    public BooleanExpression ne(final SQLExpression expr) {
        this.addSubexpressionsToRelatedExpression(expr);
        if (this.mapping instanceof ReferenceMapping && expr.mapping instanceof PersistableMapping) {
            return this.processComparisonOfImplementationWithReference(this, expr, true);
        }
        if (this.mapping instanceof PersistableMapping && expr.mapping instanceof ReferenceMapping) {
            return this.processComparisonOfImplementationWithReference(expr, this, true);
        }
        BooleanExpression bExpr = null;
        if (this.isParameter() || expr.isParameter()) {
            if (this.subExprs.size() > 1) {
                for (int i = 0; i < this.subExprs.size(); ++i) {
                    final BooleanExpression subexpr = this.subExprs.getExpression(i).eq(((ObjectExpression)expr).subExprs.getExpression(i));
                    bExpr = ((bExpr == null) ? subexpr : bExpr.and(subexpr));
                }
                return new BooleanExpression(Expression.OP_NOT, bExpr.encloseInParentheses());
            }
            return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
        }
        else {
            if (expr instanceof NullLiteral) {
                for (int i = 0; i < this.subExprs.size(); ++i) {
                    final BooleanExpression subexpr = expr.eq(this.subExprs.getExpression(i));
                    bExpr = ((bExpr == null) ? subexpr : bExpr.and(subexpr));
                }
                return new BooleanExpression(Expression.OP_NOT, bExpr.encloseInParentheses());
            }
            if (this.literalIsValidForSimpleComparison(expr)) {
                if (this.subExprs.size() > 1) {
                    return super.ne(expr);
                }
                return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
            }
            else {
                if (expr instanceof ObjectExpression) {
                    return ExpressionUtils.getEqualityExpressionForObjectExpressions(this, (ObjectExpression)expr, false);
                }
                if (this.subExprs == null) {
                    return new BooleanExpression(this, Expression.OP_NOTEQ, expr);
                }
                return super.ne(expr);
            }
        }
    }
    
    protected void addSubexpressionsToRelatedExpression(final SQLExpression expr) {
        if (expr.subExprs == null && this.subExprs != null) {
            expr.subExprs = new ColumnExpressionList();
            for (int i = 0; i < this.subExprs.size(); ++i) {
                expr.subExprs.addExpression(new ColumnExpression(this.stmt, expr.parameterName, expr.mapping, null, i));
            }
        }
    }
    
    private boolean literalIsValidForSimpleComparison(final SQLExpression expr) {
        return (expr instanceof BooleanLiteral && this.mapping instanceof BooleanMapping) || (expr instanceof ByteLiteral && this.mapping instanceof ByteMapping) || (expr instanceof CharacterLiteral && this.mapping instanceof CharacterMapping) || (expr instanceof FloatingPointLiteral && (this.mapping instanceof FloatMapping || this.mapping instanceof DoubleMapping || this.mapping instanceof BigDecimalMapping)) || (expr instanceof IntegerLiteral && (this.mapping instanceof IntegerMapping || this.mapping instanceof LongMapping || this.mapping instanceof BigIntegerMapping)) || this.mapping instanceof ShortMapping || (expr instanceof TemporalLiteral && (this.mapping instanceof DateMapping || this.mapping instanceof SqlDateMapping || this.mapping instanceof SqlTimeMapping || this.mapping instanceof SqlTimestampMapping)) || (expr instanceof StringLiteral && (this.mapping instanceof StringMapping || this.mapping instanceof CharacterMapping));
    }
    
    @Override
    public BooleanExpression in(final SQLExpression expr, final boolean not) {
        return new BooleanExpression(this, not ? Expression.OP_NOTIN : Expression.OP_IN, expr);
    }
    
    @Override
    public BooleanExpression lt(final SQLExpression expr) {
        if (this.subExprs == null) {
            return new BooleanExpression(this, Expression.OP_LT, expr);
        }
        return super.lt(expr);
    }
    
    @Override
    public BooleanExpression le(final SQLExpression expr) {
        if (this.subExprs == null) {
            return new BooleanExpression(this, Expression.OP_LTEQ, expr);
        }
        return super.le(expr);
    }
    
    @Override
    public BooleanExpression gt(final SQLExpression expr) {
        if (this.subExprs == null) {
            return new BooleanExpression(this, Expression.OP_GT, expr);
        }
        return super.gt(expr);
    }
    
    @Override
    public BooleanExpression ge(final SQLExpression expr) {
        if (this.subExprs == null) {
            return new BooleanExpression(this, Expression.OP_GTEQ, expr);
        }
        return super.ge(expr);
    }
    
    @Override
    public SQLExpression cast(final SQLExpression expr) {
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final ClassLoaderResolver clr = this.stmt.getClassLoaderResolver();
        final String castClassName = (String)((StringLiteral)expr).getValue();
        Class type = null;
        try {
            type = this.stmt.getQueryGenerator().resolveClass(castClassName);
        }
        catch (ClassNotResolvedException cnre) {
            type = null;
        }
        if (type == null) {
            throw new NucleusUserException(ObjectExpression.LOCALISER_CORE.msg("037017", castClassName));
        }
        final SQLExpressionFactory exprFactory = this.stmt.getSQLExpressionFactory();
        final Class memberType = clr.classForName(this.mapping.getType());
        if (!memberType.isAssignableFrom(type) && !type.isAssignableFrom(memberType)) {
            final JavaTypeMapping m = exprFactory.getMappingForType(Boolean.TYPE, true);
            return exprFactory.newLiteral(this.stmt, m, false).eq(exprFactory.newLiteral(this.stmt, m, true));
        }
        if (memberType == type) {
            return this;
        }
        if (this.mapping instanceof EmbeddedMapping) {
            final JavaTypeMapping m = exprFactory.getMappingForType(Boolean.TYPE, true);
            return exprFactory.newLiteral(this.stmt, m, false).eq(exprFactory.newLiteral(this.stmt, m, true));
        }
        if (this.mapping instanceof ReferenceMapping) {
            final ReferenceMapping refMapping = (ReferenceMapping)this.mapping;
            if (refMapping.getMappingStrategy() != 0) {
                throw new NucleusUserException("Impossible to do cast of interface to " + type.getName() + " since interface is persisted as embedded String." + " Use per-implementation mapping to allow this query");
            }
            final JavaTypeMapping[] implMappings = refMapping.getJavaTypeMapping();
            for (int i = 0; i < implMappings.length; ++i) {
                final Class implType = clr.classForName(implMappings[i].getType());
                if (type.isAssignableFrom(implType)) {
                    final DatastoreClass castTable = storeMgr.getDatastoreClass(type.getName(), clr);
                    final SQLTable castSqlTbl = this.stmt.leftOuterJoin(this.table, implMappings[i], refMapping, castTable, null, castTable.getIdMapping(), null, null, null);
                    return exprFactory.newExpression(this.stmt, castSqlTbl, castTable.getIdMapping());
                }
            }
            NucleusLogger.QUERY.warn("Unable to process cast of interface field to " + type.getName() + " since it has no implementations that match that type");
            final JavaTypeMapping j = exprFactory.getMappingForType(Boolean.TYPE, true);
            return exprFactory.newLiteral(this.stmt, j, false).eq(exprFactory.newLiteral(this.stmt, j, true));
        }
        else {
            if (this.mapping instanceof PersistableMapping) {
                final DatastoreClass castTable2 = storeMgr.getDatastoreClass(type.getName(), clr);
                SQLTable castSqlTbl2 = this.stmt.getTable(castTable2, this.table.getGroupName());
                if (castSqlTbl2 == null) {
                    castSqlTbl2 = this.stmt.leftOuterJoin(this.table, this.table.getTable().getIdMapping(), castTable2, null, castTable2.getIdMapping(), null, this.table.getGroupName());
                }
                return exprFactory.newExpression(this.stmt, castSqlTbl2, castTable2.getIdMapping());
            }
            throw new NucleusUserException("Dont currently support ObjectExpression.cast(" + type + ")");
        }
    }
    
    @Override
    public BooleanExpression is(final SQLExpression expr, final boolean not) {
        final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
        final ClassLoaderResolver clr = this.stmt.getClassLoaderResolver();
        final String instanceofClassName = (String)((StringLiteral)expr).getValue();
        Class type = null;
        try {
            type = this.stmt.getQueryGenerator().resolveClass(instanceofClassName);
        }
        catch (ClassNotResolvedException cnre) {
            type = null;
        }
        if (type == null) {
            throw new NucleusUserException(ObjectExpression.LOCALISER_CORE.msg("037016", instanceofClassName));
        }
        final SQLExpressionFactory exprFactory = this.stmt.getSQLExpressionFactory();
        final Class memberType = clr.classForName(this.mapping.getType());
        if (!memberType.isAssignableFrom(type) && !type.isAssignableFrom(memberType)) {
            final JavaTypeMapping m = exprFactory.getMappingForType(Boolean.TYPE, true);
            return exprFactory.newLiteral(this.stmt, m, true).eq(exprFactory.newLiteral(this.stmt, m, not));
        }
        if (memberType == type) {
            final JavaTypeMapping m = exprFactory.getMappingForType(Boolean.TYPE, true);
            return exprFactory.newLiteral(this.stmt, m, true).eq(exprFactory.newLiteral(this.stmt, m, !not));
        }
        if (this.mapping instanceof EmbeddedMapping) {
            final AbstractClassMetaData fieldCmd = storeMgr.getMetaDataManager().getMetaDataForClass(this.mapping.getType(), clr);
            if (fieldCmd.hasDiscriminatorStrategy()) {
                final JavaTypeMapping discMapping = ((EmbeddedMapping)this.mapping).getDiscriminatorMapping();
                final DiscriminatorMetaData dismd = fieldCmd.getDiscriminatorMetaDataRoot();
                final AbstractClassMetaData typeCmd = storeMgr.getMetaDataManager().getMetaDataForClass(type, clr);
                final SQLExpression discExpr = this.stmt.getSQLExpressionFactory().newExpression(this.stmt, this.table, discMapping);
                SQLExpression discVal = null;
                if (dismd.getStrategy() == DiscriminatorStrategy.CLASS_NAME) {
                    discVal = this.stmt.getSQLExpressionFactory().newLiteral(this.stmt, discMapping, typeCmd.getFullClassName());
                }
                else {
                    discVal = this.stmt.getSQLExpressionFactory().newLiteral(this.stmt, discMapping, typeCmd.getDiscriminatorMetaData().getValue());
                }
                BooleanExpression typeExpr = not ? discExpr.ne(discVal) : discExpr.eq(discVal);
                for (final String subclassName : storeMgr.getSubClassesForClass(type.getName(), true, clr)) {
                    final AbstractClassMetaData subtypeCmd = storeMgr.getMetaDataManager().getMetaDataForClass(subclassName, clr);
                    if (dismd.getStrategy() == DiscriminatorStrategy.CLASS_NAME) {
                        discVal = this.stmt.getSQLExpressionFactory().newLiteral(this.stmt, discMapping, subtypeCmd.getFullClassName());
                    }
                    else {
                        discVal = this.stmt.getSQLExpressionFactory().newLiteral(this.stmt, discMapping, subtypeCmd.getDiscriminatorMetaData().getValue());
                    }
                    final BooleanExpression subtypeExpr = not ? discExpr.ne(discVal) : discExpr.eq(discVal);
                    if (not) {
                        typeExpr = typeExpr.and(subtypeExpr);
                    }
                    else {
                        typeExpr = typeExpr.ior(subtypeExpr);
                    }
                }
                return typeExpr;
            }
            final JavaTypeMapping i = exprFactory.getMappingForType(Boolean.TYPE, true);
            return exprFactory.newLiteral(this.stmt, i, true).eq(exprFactory.newLiteral(this.stmt, i, not));
        }
        else {
            if (!(this.mapping instanceof PersistableMapping) && !(this.mapping instanceof ReferenceMapping)) {
                throw new NucleusException("Dont currently support " + this + " instanceof " + type.getName());
            }
            final AbstractClassMetaData memberCmd = storeMgr.getMetaDataManager().getMetaDataForClass(this.mapping.getType(), clr);
            DatastoreClass memberTable = null;
            if (memberCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                final AbstractClassMetaData[] cmds = storeMgr.getClassesManagingTableForClass(memberCmd, clr);
                if (cmds == null) {
                    throw new NucleusUserException(ObjectExpression.LOCALISER_CORE.msg("037005", this.mapping.getMemberMetaData().getFullFieldName()));
                }
                if (cmds.length > 1) {
                    NucleusLogger.QUERY.warn(ObjectExpression.LOCALISER_CORE.msg("037006", this.mapping.getMemberMetaData().getFullFieldName(), cmds[0].getFullClassName()));
                }
                memberTable = storeMgr.getDatastoreClass(cmds[0].getFullClassName(), clr);
            }
            else {
                memberTable = storeMgr.getDatastoreClass(this.mapping.getType(), clr);
            }
            final DiscriminatorMetaData dismd = memberTable.getDiscriminatorMetaData();
            final DiscriminatorMapping discMapping2 = (DiscriminatorMapping)memberTable.getDiscriminatorMapping(false);
            if (discMapping2 != null) {
                SQLTable targetSqlTbl = null;
                if (this.mapping.getTable() != memberTable) {
                    targetSqlTbl = this.stmt.getTable(memberTable, null);
                    if (targetSqlTbl == null) {
                        targetSqlTbl = this.stmt.innerJoin(this.getSQLTable(), this.mapping, memberTable, null, memberTable.getIdMapping(), null, null);
                    }
                }
                else {
                    targetSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(this.stmt, this.getSQLTable(), discMapping2);
                }
                final SQLTable discSqlTbl = targetSqlTbl;
                BooleanExpression discExpr2 = null;
                if (!Modifier.isAbstract(type.getModifiers())) {
                    discExpr2 = SQLStatementHelper.getExpressionForDiscriminatorForClass(this.stmt, type.getName(), dismd, discMapping2, discSqlTbl, clr);
                }
                final Iterator subclassIter2 = storeMgr.getSubClassesForClass(type.getName(), true, clr).iterator();
                boolean multiplePossibles = false;
                while (subclassIter2.hasNext()) {
                    final String subclassName2 = subclassIter2.next();
                    final Class subclass = clr.classForName(subclassName2);
                    if (Modifier.isAbstract(subclass.getModifiers())) {
                        continue;
                    }
                    final BooleanExpression discExprSub = SQLStatementHelper.getExpressionForDiscriminatorForClass(this.stmt, subclassName2, dismd, discMapping2, discSqlTbl, clr);
                    if (discExpr2 != null) {
                        multiplePossibles = true;
                        discExpr2 = discExpr2.ior(discExprSub);
                    }
                    else {
                        discExpr2 = discExprSub;
                    }
                }
                if (multiplePossibles) {
                    discExpr2.encloseInParentheses();
                }
                return not ? discExpr2.not() : discExpr2;
            }
            DatastoreClass table = null;
            if (memberCmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                final AbstractClassMetaData[] cmds2 = storeMgr.getClassesManagingTableForClass(memberCmd, clr);
                if (cmds2 == null) {
                    throw new NucleusUserException(ObjectExpression.LOCALISER_CORE.msg("037005", this.mapping.getMemberMetaData().getFullFieldName()));
                }
                if (cmds2.length > 1) {
                    NucleusLogger.QUERY.warn(ObjectExpression.LOCALISER_CORE.msg("037006", this.mapping.getMemberMetaData().getFullFieldName(), cmds2[0].getFullClassName()));
                }
                table = storeMgr.getDatastoreClass(cmds2[0].getFullClassName(), clr);
            }
            else {
                table = storeMgr.getDatastoreClass(this.mapping.getType(), clr);
            }
            if (table.managesClass(type.getName())) {
                final JavaTypeMapping j = exprFactory.getMappingForType(Boolean.TYPE, true);
                return exprFactory.newLiteral(this.stmt, j, true).eq(exprFactory.newLiteral(this.stmt, j, !not));
            }
            if (table != this.stmt.getPrimaryTable().getTable()) {
                final DatastoreClass instanceofTable = storeMgr.getDatastoreClass(type.getName(), clr);
                if (this.stmt.getNumberOfUnions() > 0) {
                    NucleusLogger.QUERY.debug("InstanceOf for " + table + " but no discriminator so adding inner join to " + instanceofTable + " : in some cases with UNIONs this may fail");
                }
                this.stmt.innerJoin(this.table, this.table.getTable().getIdMapping(), instanceofTable, null, instanceofTable.getIdMapping(), null, this.table.getGroupName());
                final JavaTypeMapping k = exprFactory.getMappingForType(Boolean.TYPE, true);
                return exprFactory.newLiteral(this.stmt, k, true).eq(exprFactory.newLiteral(this.stmt, k, !not));
            }
            final JavaTypeMapping j = exprFactory.getMappingForType(Boolean.TYPE, true);
            if (this.stmt.getNumberOfUnions() > 0) {
                final Class mainCandidateCls = clr.classForName(this.stmt.getCandidateClassName());
                if (type.isAssignableFrom(mainCandidateCls) == not) {
                    final SQLExpression unionClauseExpr = exprFactory.newLiteral(this.stmt, j, true).eq(exprFactory.newLiteral(this.stmt, j, false));
                    this.stmt.whereAnd((BooleanExpression)unionClauseExpr, false);
                }
                final List<SQLStatement> unionStmts = this.stmt.getUnions();
                for (final SQLStatement unionStmt : unionStmts) {
                    final Class unionCandidateCls = clr.classForName(unionStmt.getCandidateClassName());
                    if (type.isAssignableFrom(unionCandidateCls) == not) {
                        final SQLExpression unionClauseExpr2 = exprFactory.newLiteral(unionStmt, j, true).eq(exprFactory.newLiteral(unionStmt, j, false));
                        unionStmt.whereAnd((BooleanExpression)unionClauseExpr2, false);
                    }
                }
                final SQLExpression returnExpr = exprFactory.newLiteral(this.stmt, j, true).eq(exprFactory.newLiteral(this.stmt, j, true));
                return (BooleanExpression)returnExpr;
            }
            final DatastoreClass instanceofTable2 = storeMgr.getDatastoreClass(type.getName(), clr);
            this.stmt.innerJoin(this.table, this.table.getTable().getIdMapping(), instanceofTable2, null, instanceofTable2.getIdMapping(), null, this.table.getGroupName());
            return exprFactory.newLiteral(this.stmt, j, true).eq(exprFactory.newLiteral(this.stmt, j, !not));
        }
    }
    
    @Override
    public SQLExpression invoke(final String methodName, final List args) {
        return this.stmt.getRDBMSManager().getSQLExpressionFactory().invokeMethod(this.stmt, Object.class.getName(), methodName, this, args);
    }
    
    static {
        LOCALISER_CORE = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
