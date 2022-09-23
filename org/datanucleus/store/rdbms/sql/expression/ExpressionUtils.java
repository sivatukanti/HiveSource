// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import org.datanucleus.ClassConstants;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import java.math.BigDecimal;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.mapping.java.ReferenceMapping;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.identity.OID;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.fieldmanager.SingleValueFieldManager;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.mapping.java.EmbeddedMapping;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.List;
import java.util.ArrayList;
import java.math.BigInteger;
import org.datanucleus.util.Localiser;

public class ExpressionUtils
{
    protected static final Localiser LOCALISER;
    
    public static NumericExpression getNumericExpression(final SQLExpression expr) {
        final RDBMSStoreManager storeMgr = expr.getSQLStatement().getRDBMSManager();
        final SQLExpressionFactory factory = storeMgr.getSQLExpressionFactory();
        final DatastoreAdapter dba = expr.getSQLStatement().getDatastoreAdapter();
        if (expr instanceof CharacterLiteral) {
            final char c = (char)((CharacterLiteral)expr).getValue();
            final BigInteger value = new BigInteger("" + (int)c);
            return (NumericExpression)factory.newLiteral(expr.getSQLStatement(), storeMgr.getMappingManager().getMapping(value.getClass()), value);
        }
        if (expr instanceof SQLLiteral) {
            final BigInteger value2 = new BigInteger((String)((SQLLiteral)expr).getValue());
            return (NumericExpression)factory.newLiteral(expr.getSQLStatement(), storeMgr.getMappingManager().getMapping(value2.getClass()), value2);
        }
        final ArrayList args = new ArrayList();
        args.add(expr);
        return new NumericExpression(expr.getSQLStatement(), expr.getJavaTypeMapping(), dba.getNumericConversionFunction(), args);
    }
    
    public static SQLExpression getLiteralForOne(final SQLStatement stmt) {
        final RDBMSStoreManager storeMgr = stmt.getRDBMSManager();
        final JavaTypeMapping mapping = storeMgr.getMappingManager().getMapping(BigInteger.class);
        return storeMgr.getSQLExpressionFactory().newLiteral(stmt, mapping, BigInteger.ONE);
    }
    
    public static StringExpression getStringExpression(final SQLExpression expr) {
        final RDBMSStoreManager storeMgr = expr.getSQLStatement().getRDBMSManager();
        final JavaTypeMapping m = storeMgr.getSQLExpressionFactory().getMappingForType(String.class, false);
        if (expr instanceof SQLLiteral) {
            return new StringLiteral(expr.getSQLStatement(), m, ((SQLLiteral)expr).getValue().toString(), (String)null);
        }
        final List args = new ArrayList();
        args.add(expr);
        final List types = new ArrayList();
        types.add("VARCHAR(4000)");
        return new StringExpression(expr.getSQLStatement(), m, "CAST", args, types);
    }
    
    public static SQLExpression getEscapedPatternExpression(final SQLExpression patternExpr) {
        if (patternExpr instanceof StringLiteral) {
            String value = (String)((StringLiteral)patternExpr).getValue();
            final SQLExpressionFactory exprFactory = patternExpr.getSQLStatement().getSQLExpressionFactory();
            final JavaTypeMapping m = exprFactory.getMappingForType(String.class, false);
            if (value != null) {
                value = value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
            }
            return exprFactory.newLiteral(patternExpr.getSQLStatement(), m, value);
        }
        return patternExpr;
    }
    
    public static int populatePrimaryKeyMappingsValuesForPCMapping(final JavaTypeMapping[] pkMappings, final Object[] pkFieldValues, int position, final PersistableMapping pcMapping, final AbstractClassMetaData cmd, final AbstractMemberMetaData mmd, final Object fieldValue, final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        final ExecutionContext ec = storeMgr.getApiAdapter().getExecutionContext(fieldValue);
        final JavaTypeMapping[] subMappings = pcMapping.getJavaTypeMapping();
        if (subMappings.length == 0) {
            final DatastoreClass table = storeMgr.getDatastoreClass(cmd.getFullClassName(), clr);
            final JavaTypeMapping ownerMapping = table.getMemberMapping(mmd);
            final EmbeddedMapping embMapping = (EmbeddedMapping)ownerMapping;
            for (int k = 0; k < embMapping.getNumberOfJavaTypeMappings(); ++k) {
                final JavaTypeMapping subMapping = embMapping.getJavaTypeMapping(k);
                pkMappings[position] = subMapping;
                pkFieldValues[position] = getValueForMemberOfObject(ec, subMapping.getMemberMetaData(), fieldValue);
                ++position;
            }
        }
        else {
            final AbstractClassMetaData pcCmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(pcMapping.getType(), clr);
            final int[] pcPkPositions = pcCmd.getPKMemberPositions();
            for (int i = 0; i < subMappings.length; ++i) {
                final AbstractMemberMetaData pcMmd = pcCmd.getMetaDataForManagedMemberAtAbsolutePosition(pcPkPositions[i]);
                if (subMappings[i] instanceof PersistableMapping) {
                    final Object val = getValueForMemberOfObject(ec, pcMmd, fieldValue);
                    position = populatePrimaryKeyMappingsValuesForPCMapping(pkMappings, pkFieldValues, position, (PersistableMapping)subMappings[i], pcCmd, pcMmd, val, storeMgr, clr);
                }
                else {
                    final Object val = getValueForMemberOfObject(ec, pcMmd, fieldValue);
                    pkMappings[position] = subMappings[i];
                    pkFieldValues[position] = val;
                    ++position;
                }
            }
        }
        return position;
    }
    
    public static Object getValueForMemberOfObject(final ExecutionContext ec, final AbstractMemberMetaData mmd, final Object object) {
        if (ec == null) {
            return ClassUtils.getValueOfFieldByReflection(object, mmd.getName());
        }
        final ObjectProvider sm = ec.findObjectProvider(object);
        if (!mmd.isPrimaryKey()) {
            sm.isLoaded(mmd.getAbsoluteFieldNumber());
        }
        final FieldManager fm = new SingleValueFieldManager();
        sm.provideFields(new int[] { mmd.getAbsoluteFieldNumber() }, fm);
        return fm.fetchObjectField(mmd.getAbsoluteFieldNumber());
    }
    
    public static BooleanExpression getAppIdEqualityExpression(final Object id, final SQLExpression expr, final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr, final AbstractClassMetaData acmd, Integer index, BooleanExpression bExpr) {
        if (index == null) {
            index = 0;
        }
        final String[] pkFieldNames = acmd.getPrimaryKeyMemberNames();
        for (int i = 0; i < pkFieldNames.length; ++i) {
            final Object value = ClassUtils.getValueOfFieldByReflection(id, pkFieldNames[i]);
            final String pcClassName = storeMgr.getClassNameForObjectID(value, clr, null);
            if (pcClassName != null) {
                final AbstractClassMetaData scmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(pcClassName, clr);
                if (bExpr == null) {
                    bExpr = getAppIdEqualityExpression(value, expr, storeMgr, clr, scmd, index, null);
                }
                else {
                    bExpr = bExpr.and(getAppIdEqualityExpression(value, expr, storeMgr, clr, scmd, index, bExpr));
                }
            }
            else {
                final SQLExpression source = expr.subExprs.getExpression(index);
                final JavaTypeMapping mapping = storeMgr.getMappingManager().getMappingWithDatastoreMapping(value.getClass(), false, false, clr);
                final SQLExpression target = expr.getSQLStatement().getSQLExpressionFactory().newLiteral(expr.getSQLStatement(), mapping, value);
                if (bExpr == null) {
                    bExpr = source.eq(target);
                }
                else {
                    bExpr = bExpr.and(source.eq(target));
                }
                if (target.subExprs.size() == 0) {
                    ++index;
                }
                else {
                    index += target.subExprs.size();
                }
            }
        }
        return bExpr;
    }
    
    public static BooleanExpression getEqualityExpressionForObjectExpressions(final ObjectExpression expr1, final ObjectExpression expr2, final boolean equals) {
        final SQLStatement stmt = expr1.stmt;
        final RDBMSStoreManager storeMgr = stmt.getRDBMSManager();
        final SQLExpressionFactory exprFactory = storeMgr.getSQLExpressionFactory();
        final ClassLoaderResolver clr = stmt.getClassLoaderResolver();
        final ApiAdapter api = storeMgr.getApiAdapter();
        if (expr1 instanceof ObjectLiteral && expr2 instanceof ObjectLiteral) {
            final ObjectLiteral lit1 = (ObjectLiteral)expr1;
            final ObjectLiteral lit2 = (ObjectLiteral)expr2;
            return new BooleanLiteral(stmt, expr1.mapping, equals ? lit1.getValue().equals(lit2.getValue()) : (!lit1.getValue().equals(lit2.getValue())));
        }
        if (!(expr1 instanceof ObjectLiteral) && !(expr2 instanceof ObjectLiteral)) {
            BooleanExpression resultExpr = null;
            for (int i = 0; i < expr1.subExprs.size(); ++i) {
                final SQLExpression sourceExpr = expr1.subExprs.getExpression(i);
                final SQLExpression targetExpr = expr2.subExprs.getExpression(i);
                if (resultExpr == null) {
                    resultExpr = sourceExpr.eq(targetExpr);
                }
                else {
                    resultExpr = resultExpr.and(sourceExpr.eq(targetExpr));
                }
            }
            if (!equals) {
                resultExpr = new BooleanExpression(Expression.OP_NOT, resultExpr.encloseInParentheses());
            }
            return resultExpr;
        }
        BooleanExpression bExpr = null;
        final boolean secondIsLiteral = expr2 instanceof ObjectLiteral;
        final Object value = secondIsLiteral ? ((ObjectLiteral)expr2).getValue() : ((ObjectLiteral)expr1).getValue();
        if (value instanceof OID) {
            final JavaTypeMapping m = storeMgr.getSQLExpressionFactory().getMappingForType(((OID)value).getKeyValue().getClass(), false);
            final SQLExpression oidLit = exprFactory.newLiteral(stmt, m, ((OID)value).getKeyValue());
            if (equals) {
                return secondIsLiteral ? expr1.subExprs.getExpression(0).eq(oidLit) : expr2.subExprs.getExpression(0).eq(oidLit);
            }
            return secondIsLiteral ? expr1.subExprs.getExpression(0).ne(oidLit) : expr2.subExprs.getExpression(0).ne(oidLit);
        }
        else if (api.isSingleFieldIdentity(value)) {
            final JavaTypeMapping m = storeMgr.getSQLExpressionFactory().getMappingForType(api.getTargetClassForSingleFieldIdentity(value), false);
            final SQLExpression oidLit = exprFactory.newLiteral(stmt, m, api.getTargetKeyForSingleFieldIdentity(value));
            if (equals) {
                return secondIsLiteral ? expr1.subExprs.getExpression(0).eq(oidLit) : expr2.subExprs.getExpression(0).eq(oidLit);
            }
            return secondIsLiteral ? expr1.subExprs.getExpression(0).ne(oidLit) : expr2.subExprs.getExpression(0).ne(oidLit);
        }
        else {
            AbstractClassMetaData cmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(value.getClass(), clr);
            if (cmd != null) {
                if (cmd.getIdentityType() == IdentityType.APPLICATION) {
                    if (api.getIdForObject(value) != null) {
                        final ObjectExpression expr3 = secondIsLiteral ? expr1 : expr2;
                        final JavaTypeMapping[] pkMappingsApp = new JavaTypeMapping[expr3.subExprs.size()];
                        final Object[] pkFieldValues = new Object[expr3.subExprs.size()];
                        int position = 0;
                        final ExecutionContext ec = api.getExecutionContext(value);
                        JavaTypeMapping thisMapping = expr3.mapping;
                        if (expr3.mapping instanceof ReferenceMapping) {
                            thisMapping = null;
                            final ReferenceMapping refMapping = (ReferenceMapping)expr3.mapping;
                            final JavaTypeMapping[] implMappings = refMapping.getJavaTypeMapping();
                            for (int j = 0; j < implMappings.length; ++j) {
                                final Class implType = clr.classForName(implMappings[j].getType());
                                if (implType.isAssignableFrom(value.getClass())) {
                                    thisMapping = implMappings[j];
                                    break;
                                }
                            }
                        }
                        if (thisMapping == null) {
                            return exprFactory.newLiteral(stmt, expr1.mapping, false).eq(exprFactory.newLiteral(stmt, expr1.mapping, true));
                        }
                        for (int k = 0; k < cmd.getNoOfPrimaryKeyMembers(); ++k) {
                            final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(cmd.getPKMemberPositions()[k]);
                            final Object fieldValue = getValueForMemberOfObject(ec, mmd, value);
                            final JavaTypeMapping mapping = ((PersistableMapping)thisMapping).getJavaTypeMapping()[k];
                            if (mapping instanceof PersistableMapping) {
                                position = populatePrimaryKeyMappingsValuesForPCMapping(pkMappingsApp, pkFieldValues, position, (PersistableMapping)mapping, cmd, mmd, fieldValue, storeMgr, clr);
                            }
                            else {
                                pkMappingsApp[position] = mapping;
                                pkFieldValues[position] = fieldValue;
                                ++position;
                            }
                        }
                        for (int k = 0; k < expr3.subExprs.size(); ++k) {
                            final SQLExpression source = expr3.subExprs.getExpression(k);
                            final SQLExpression target = exprFactory.newLiteral(stmt, pkMappingsApp[k], pkFieldValues[k]);
                            final BooleanExpression subExpr = secondIsLiteral ? source.eq(target) : target.eq(source);
                            if (bExpr == null) {
                                bExpr = subExpr;
                            }
                            else {
                                bExpr = bExpr.and(subExpr);
                            }
                        }
                    }
                    else if (secondIsLiteral) {
                        for (int l = 0; l < expr1.subExprs.size(); ++l) {
                            NucleusLogger.QUERY.warn(ExpressionUtils.LOCALISER.msg("037003", value));
                            bExpr = exprFactory.newLiteral(stmt, expr1.mapping, false).eq(exprFactory.newLiteral(stmt, expr1.mapping, true));
                        }
                    }
                    else {
                        for (int l = 0; l < expr2.subExprs.size(); ++l) {
                            NucleusLogger.QUERY.warn(ExpressionUtils.LOCALISER.msg("037003", value));
                            bExpr = exprFactory.newLiteral(stmt, expr2.mapping, false).eq(exprFactory.newLiteral(stmt, expr2.mapping, true));
                        }
                    }
                    return bExpr;
                }
                if (cmd.getIdentityType() != IdentityType.DATASTORE) {
                    return null;
                }
                final SQLExpression source2 = secondIsLiteral ? expr1.subExprs.getExpression(0) : expr2.subExprs.getExpression(0);
                final JavaTypeMapping mapping2 = secondIsLiteral ? expr1.mapping : expr2.mapping;
                final OID objectId = (OID)api.getIdForObject(value);
                if (objectId == null) {
                    NucleusLogger.QUERY.warn(ExpressionUtils.LOCALISER.msg("037003", value));
                    return exprFactory.newLiteral(stmt, mapping2, false).eq(exprFactory.newLiteral(stmt, mapping2, true));
                }
                final JavaTypeMapping m2 = storeMgr.getSQLExpressionFactory().getMappingForType(objectId.getKeyValue().getClass(), false);
                final SQLExpression oidExpr = exprFactory.newLiteral(stmt, m2, objectId.getKeyValue());
                if (equals) {
                    return source2.eq(oidExpr);
                }
                return source2.ne(oidExpr);
            }
            else {
                final String pcClassName = storeMgr.getClassNameForObjectID(value, clr, null);
                if (pcClassName != null) {
                    cmd = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(pcClassName, clr);
                    return secondIsLiteral ? getAppIdEqualityExpression(value, expr1, storeMgr, clr, cmd, null, null) : getAppIdEqualityExpression(value, expr2, storeMgr, clr, cmd, null, null);
                }
                return exprFactory.newLiteral(stmt, expr1.mapping, false).eq(exprFactory.newLiteral(stmt, expr1.mapping, true));
            }
        }
    }
    
    public static void checkAndCorrectExpressionMappingsForBooleanComparison(final SQLExpression expr1, final SQLExpression expr2) {
        if (expr1.isParameter() && expr2.isParameter()) {
            if (expr1 instanceof SQLLiteral && ((SQLLiteral)expr2).getValue() != null) {
                expr1.getSQLStatement().getQueryGenerator().useParameterExpressionAsLiteral((SQLLiteral)expr2);
            }
            else if (expr2 instanceof SQLLiteral && ((SQLLiteral)expr2).getValue() != null) {
                expr1.getSQLStatement().getQueryGenerator().useParameterExpressionAsLiteral((SQLLiteral)expr2);
            }
        }
        if (expr1 instanceof SQLLiteral) {
            checkAndCorrectLiteralForConsistentMappingsForBooleanComparison((SQLLiteral)expr1, expr2);
        }
        else if (expr2 instanceof SQLLiteral) {
            checkAndCorrectLiteralForConsistentMappingsForBooleanComparison((SQLLiteral)expr2, expr1);
        }
    }
    
    protected static void checkAndCorrectLiteralForConsistentMappingsForBooleanComparison(final SQLLiteral lit, final SQLExpression expr) {
        final JavaTypeMapping litMapping = ((SQLExpression)lit).getJavaTypeMapping();
        final JavaTypeMapping exprMapping = expr.getJavaTypeMapping();
        if (exprMapping == null || exprMapping.getNumberOfDatastoreMappings() == 0) {
            return;
        }
        if (litMapping instanceof PersistableMapping && exprMapping instanceof ReferenceMapping) {
            return;
        }
        boolean needsUpdating = false;
        if (litMapping.getNumberOfDatastoreMappings() != exprMapping.getNumberOfDatastoreMappings()) {
            needsUpdating = true;
        }
        else {
            for (int i = 0; i < litMapping.getNumberOfDatastoreMappings(); ++i) {
                final DatastoreMapping colMapping = litMapping.getDatastoreMapping(i);
                if (colMapping == null || colMapping.getClass() != exprMapping.getDatastoreMapping(i).getClass()) {
                    needsUpdating = true;
                    break;
                }
            }
        }
        if (needsUpdating) {
            final Class litMappingCls = litMapping.getJavaType();
            final Class mappingCls = exprMapping.getJavaType();
            if ((litMappingCls == Double.class || litMappingCls == Float.class || litMappingCls == BigDecimal.class) && (mappingCls == Integer.class || mappingCls == Long.class || mappingCls == Short.class || mappingCls == BigInteger.class || mappingCls == Byte.class)) {
                if (litMappingCls == BigDecimal.class) {
                    expr.getSQLStatement().getQueryGenerator().useParameterExpressionAsLiteral(lit);
                }
                needsUpdating = false;
            }
            if (litMappingCls == Byte.class && mappingCls != Byte.class) {
                needsUpdating = false;
            }
        }
        if (needsUpdating) {
            NucleusLogger.QUERY.debug("Updating mapping of " + lit + " to be " + expr.getJavaTypeMapping());
            ((SQLExpression)lit).setJavaTypeMapping(expr.getJavaTypeMapping());
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
