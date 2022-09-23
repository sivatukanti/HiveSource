// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.util.Iterator;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.StringUtils;
import java.util.List;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.rdbms.sql.SQLStatementHelper;
import org.datanucleus.plugin.ConfigurationElement;
import org.datanucleus.plugin.PluginManager;
import java.util.HashSet;
import java.util.HashMap;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.operation.SQLOperation;
import org.datanucleus.store.rdbms.sql.method.SQLMethod;
import java.util.Set;
import java.util.Map;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;

public class SQLExpressionFactory
{
    protected static final Localiser LOCALISER;
    RDBMSStoreManager storeMgr;
    ClassLoaderResolver clr;
    private final Class[] EXPR_CREATION_ARG_TYPES;
    private final Class[] LIT_CREATION_ARG_TYPES;
    Map<String, Class> expressionClassByMappingName;
    Map<String, Class> literalClassByMappingName;
    Set<MethodKey> methodNamesSupported;
    Map<MethodKey, SQLMethod> methodByClassMethodName;
    Set<String> operationNamesSupported;
    Map<String, SQLOperation> operationByOperationName;
    Map<Class, JavaTypeMapping> mappingByClass;
    
    public SQLExpressionFactory(final RDBMSStoreManager storeMgr) {
        this.EXPR_CREATION_ARG_TYPES = new Class[] { SQLStatement.class, SQLTable.class, JavaTypeMapping.class };
        this.LIT_CREATION_ARG_TYPES = new Class[] { SQLStatement.class, JavaTypeMapping.class, Object.class, String.class };
        this.expressionClassByMappingName = new HashMap<String, Class>();
        this.literalClassByMappingName = new HashMap<String, Class>();
        this.methodNamesSupported = new HashSet<MethodKey>();
        this.methodByClassMethodName = new HashMap<MethodKey, SQLMethod>();
        this.operationNamesSupported = new HashSet<String>();
        this.operationByOperationName = new HashMap<String, SQLOperation>();
        this.mappingByClass = new HashMap<Class, JavaTypeMapping>();
        this.storeMgr = storeMgr;
        this.clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        final PluginManager pluginMgr = storeMgr.getNucleusContext().getPluginManager();
        final ConfigurationElement[] methodElems = pluginMgr.getConfigurationElementsForExtension("org.datanucleus.store.rdbms.sql_method", null, (String)null);
        if (methodElems != null) {
            for (int i = 0; i < methodElems.length; ++i) {
                final String datastoreName = methodElems[i].getAttribute("datastore");
                final String className = methodElems[i].getAttribute("class");
                final String methodName = methodElems[i].getAttribute("method").trim();
                final MethodKey key = this.getSQLMethodKey(datastoreName, className, methodName);
                this.methodNamesSupported.add(key);
            }
        }
        final ConfigurationElement[] operationElems = pluginMgr.getConfigurationElementsForExtension("org.datanucleus.store.rdbms.sql_operation", null, (String)null);
        if (operationElems != null) {
            for (int j = 0; j < operationElems.length; ++j) {
                final String datastoreName2 = operationElems[j].getAttribute("datastore");
                final String name = operationElems[j].getAttribute("name").trim();
                final String key2 = this.getSQLOperationKey(datastoreName2, name);
                this.operationNamesSupported.add(key2);
            }
        }
    }
    
    public SQLExpression newExpression(final SQLStatement stmt, final SQLTable sqlTbl, final JavaTypeMapping mapping) {
        return this.newExpression(stmt, sqlTbl, mapping, null);
    }
    
    public SQLExpression newExpression(final SQLStatement stmt, final SQLTable sqlTbl, final JavaTypeMapping mapping, final JavaTypeMapping parentMapping) {
        final SQLTable exprSqlTbl = SQLStatementHelper.getSQLTableForMappingOfTable(stmt, sqlTbl, (parentMapping == null) ? mapping : parentMapping);
        final Object[] args = { stmt, exprSqlTbl, mapping };
        final Class expressionClass = this.expressionClassByMappingName.get(mapping.getClass().getName());
        if (expressionClass != null) {
            return (SQLExpression)ClassUtils.newInstance(expressionClass, this.EXPR_CREATION_ARG_TYPES, new Object[] { stmt, exprSqlTbl, mapping });
        }
        try {
            final SQLExpression sqlExpr = (SQLExpression)this.storeMgr.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store.rdbms.sql_expression", "mapping-class", mapping.getClass().getName(), "expression-class", this.EXPR_CREATION_ARG_TYPES, args);
            if (sqlExpr == null) {
                throw new NucleusException(SQLExpressionFactory.LOCALISER.msg("060004", mapping.getClass().getName()));
            }
            this.expressionClassByMappingName.put(mapping.getClass().getName(), sqlExpr.getClass());
            return sqlExpr;
        }
        catch (Exception e) {
            final String msg = SQLExpressionFactory.LOCALISER.msg("060005", mapping.getClass().getName());
            NucleusLogger.QUERY.error(msg, e);
            throw new NucleusException(msg, e);
        }
    }
    
    public SQLExpression newLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value) {
        final Object[] args = { stmt, mapping, value, null };
        if (mapping != null) {
            final Class literalClass = this.literalClassByMappingName.get(mapping.getClass().getName());
            if (literalClass != null) {
                return (SQLExpression)ClassUtils.newInstance(literalClass, this.LIT_CREATION_ARG_TYPES, args);
            }
        }
        try {
            if (mapping == null) {
                return (SQLExpression)ClassUtils.newInstance(NullLiteral.class, this.LIT_CREATION_ARG_TYPES, args);
            }
            final SQLExpression sqlExpr = (SQLExpression)this.storeMgr.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store.rdbms.sql_expression", "mapping-class", mapping.getClass().getName(), "literal-class", this.LIT_CREATION_ARG_TYPES, args);
            if (sqlExpr == null) {
                throw new NucleusException(SQLExpressionFactory.LOCALISER.msg("060006", mapping.getClass().getName()));
            }
            this.literalClassByMappingName.put(mapping.getClass().getName(), sqlExpr.getClass());
            return sqlExpr;
        }
        catch (Exception e) {
            NucleusLogger.QUERY.error("Exception creating SQLLiteral for mapping " + mapping.getClass().getName(), e);
            throw new NucleusException(SQLExpressionFactory.LOCALISER.msg("060007", mapping.getClass().getName()));
        }
    }
    
    public SQLExpression newLiteralParameter(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String paramName) {
        try {
            final Object[] args = { stmt, mapping, value, paramName };
            if (mapping == null) {
                return (SQLExpression)ClassUtils.newInstance(ParameterLiteral.class, this.LIT_CREATION_ARG_TYPES, args);
            }
            final SQLExpression sqlExpr = (SQLExpression)this.storeMgr.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store.rdbms.sql_expression", "mapping-class", mapping.getClass().getName(), "literal-class", this.LIT_CREATION_ARG_TYPES, args);
            if (sqlExpr == null) {
                throw new NucleusException(SQLExpressionFactory.LOCALISER.msg("060006", mapping.getClass().getName()));
            }
            return sqlExpr;
        }
        catch (Exception e) {
            NucleusLogger.QUERY.error("Exception creating SQLLiteral for mapping " + mapping.getClass().getName(), e);
            throw new NucleusException(SQLExpressionFactory.LOCALISER.msg("060007", mapping.getClass().getName()));
        }
    }
    
    public SQLExpression invokeMethod(final SQLStatement stmt, String className, final String methodName, final SQLExpression expr, final List args) {
        String datastoreId = this.storeMgr.getDatastoreAdapter().getVendorID();
        final MethodKey methodKey1 = this.getSQLMethodKey(datastoreId, className, methodName);
        MethodKey methodKey2 = null;
        SQLMethod method = this.methodByClassMethodName.get(methodKey1);
        if (method == null) {
            methodKey2 = this.getSQLMethodKey(null, className, methodName);
            method = this.methodByClassMethodName.get(methodKey2);
        }
        if (method != null) {
            synchronized (method) {
                method.setStatement(stmt);
                return method.getExpression(expr, args);
            }
        }
        boolean datastoreDependent = true;
        if (!this.methodNamesSupported.contains(methodKey1)) {
            datastoreDependent = false;
            if (!this.methodNamesSupported.contains(methodKey2)) {
                boolean unsupported = true;
                if (!StringUtils.isWhitespace(className)) {
                    final Class cls = this.clr.classForName(className);
                    for (final MethodKey methodKey3 : this.methodNamesSupported) {
                        if (methodKey3.methodName.equals(methodName) && methodKey3.datastoreName.equals(datastoreId)) {
                            final Class methodCls = this.clr.classForName(methodKey3.clsName);
                            if (methodCls.isAssignableFrom(cls)) {
                                method = this.methodByClassMethodName.get(methodKey3);
                                if (method != null) {
                                    synchronized (method) {
                                        method.setStatement(stmt);
                                        return method.getExpression(expr, args);
                                    }
                                }
                                className = methodKey3.clsName;
                                datastoreId = methodKey3.datastoreName;
                                datastoreDependent = true;
                                unsupported = false;
                                break;
                            }
                            continue;
                        }
                    }
                    if (unsupported) {
                        for (final MethodKey methodKey3 : this.methodNamesSupported) {
                            if (methodKey3.methodName.equals(methodName) && methodKey3.datastoreName.equals("ALL")) {
                                final Class methodCls = this.clr.classForName(methodKey3.clsName);
                                if (methodCls.isAssignableFrom(cls)) {
                                    method = this.methodByClassMethodName.get(methodKey3);
                                    if (method != null) {
                                        synchronized (method) {
                                            method.setStatement(stmt);
                                            return method.getExpression(expr, args);
                                        }
                                    }
                                    className = methodKey3.clsName;
                                    datastoreId = methodKey3.datastoreName;
                                    datastoreDependent = false;
                                    unsupported = false;
                                    break;
                                }
                                continue;
                            }
                        }
                    }
                }
                if (unsupported) {
                    if (className != null) {
                        throw new NucleusUserException(SQLExpressionFactory.LOCALISER.msg("060008", methodName, className));
                    }
                    throw new NucleusUserException(SQLExpressionFactory.LOCALISER.msg("060009", methodName));
                }
            }
        }
        final PluginManager pluginMgr = this.storeMgr.getNucleusContext().getPluginManager();
        final String[] attrNames = datastoreDependent ? new String[] { "class", "method", "datastore" } : new String[] { "class", "method" };
        final String[] attrValues = datastoreDependent ? new String[] { className, methodName, datastoreId } : new String[] { className, methodName };
        try {
            final SQLMethod evaluator = (SQLMethod)pluginMgr.createExecutableExtension("org.datanucleus.store.rdbms.sql_method", attrNames, attrValues, "evaluator", new Class[0], new Object[0]);
            evaluator.setStatement(stmt);
            final MethodKey key = this.getSQLMethodKey(datastoreDependent ? datastoreId : null, className, methodName);
            synchronized (evaluator) {
                this.methodByClassMethodName.put(key, evaluator);
                return evaluator.getExpression(expr, args);
            }
        }
        catch (Exception e) {
            throw new NucleusUserException(SQLExpressionFactory.LOCALISER.msg("060011", "class=" + className + " method=" + methodName), e);
        }
    }
    
    public SQLExpression invokeOperation(final String name, final SQLExpression expr, final SQLExpression expr2) {
        SQLOperation operation = this.operationByOperationName.get(name);
        if (operation != null) {
            return operation.getExpression(expr, expr2);
        }
        final String datastoreId = this.storeMgr.getDatastoreAdapter().getVendorID();
        String key = this.getSQLOperationKey(datastoreId, name);
        boolean datastoreDependent = true;
        if (!this.operationNamesSupported.contains(key)) {
            key = this.getSQLOperationKey(null, name);
            datastoreDependent = false;
            if (!this.operationNamesSupported.contains(key)) {
                throw new UnsupportedOperationException("Operation " + name + " datastore=" + datastoreId + " not supported");
            }
        }
        final PluginManager pluginMgr = this.storeMgr.getNucleusContext().getPluginManager();
        final String[] attrNames = datastoreDependent ? new String[] { "name", "datastore" } : new String[] { "name" };
        final String[] attrValues = datastoreDependent ? new String[] { name, datastoreId } : new String[] { name };
        try {
            operation = (SQLOperation)pluginMgr.createExecutableExtension("org.datanucleus.store.rdbms.sql_operation", attrNames, attrValues, "evaluator", null, null);
            operation.setExpressionFactory(this);
            synchronized (operation) {
                this.operationByOperationName.put(key, operation);
                return operation.getExpression(expr, expr2);
            }
        }
        catch (Exception e) {
            throw new NucleusUserException(SQLExpressionFactory.LOCALISER.msg("060011", "operation=" + name), e);
        }
    }
    
    private MethodKey getSQLMethodKey(final String datastoreName, final String className, final String methodName) {
        final MethodKey key = new MethodKey();
        key.clsName = ((className != null) ? className.trim() : "");
        key.methodName = methodName;
        key.datastoreName = ((datastoreName != null) ? datastoreName.trim() : "ALL");
        return key;
    }
    
    private String getSQLOperationKey(final String datastoreName, final String name) {
        return ((datastoreName != null) ? datastoreName.trim() : "ALL") + "#" + name;
    }
    
    public JavaTypeMapping getMappingForType(final Class cls, final boolean useCached) {
        JavaTypeMapping mapping = null;
        if (useCached) {
            mapping = this.mappingByClass.get(cls);
            if (mapping != null) {
                return mapping;
            }
        }
        mapping = this.storeMgr.getMappingManager().getMappingWithDatastoreMapping(cls, false, false, this.clr);
        this.mappingByClass.put(cls, mapping);
        return mapping;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
    
    private class MethodKey
    {
        String clsName;
        String methodName;
        String datastoreName;
        
        @Override
        public int hashCode() {
            return (this.clsName + this.methodName + this.datastoreName).hashCode();
        }
        
        @Override
        public boolean equals(final Object other) {
            if (other == null || !(other instanceof MethodKey)) {
                return false;
            }
            final MethodKey otherKey = (MethodKey)other;
            return otherKey.clsName.equals(this.clsName) && otherKey.methodName.equals(this.methodName) && otherKey.datastoreName.equals(this.datastoreName);
        }
    }
}
