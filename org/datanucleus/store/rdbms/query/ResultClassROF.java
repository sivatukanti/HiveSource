// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import java.sql.Array;
import java.io.Reader;
import java.util.Date;
import java.sql.Timestamp;
import java.math.BigDecimal;
import org.datanucleus.ClassConstants;
import java.lang.reflect.Constructor;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.util.StringUtils;
import java.sql.SQLException;
import org.datanucleus.util.NucleusLogger;
import java.sql.ResultSet;
import org.datanucleus.ExecutionContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;

public class ResultClassROF implements ResultObjectFactory
{
    protected static final Localiser LOCALISER;
    private final RDBMSStoreManager storeMgr;
    private final Class resultClass;
    private final StatementMappingIndex[] stmtMappings;
    private StatementResultMapping resultDefinition;
    private final String[] resultFieldNames;
    private final Class[] resultFieldTypes;
    private final Map resultClassFieldsByName;
    private static Map<Class, ResultSetGetter> resultSetGetters;
    
    public ResultClassROF(final RDBMSStoreManager storeMgr, final Class cls, final StatementResultMapping resultDefinition) {
        this.resultClassFieldsByName = new HashMap();
        this.storeMgr = storeMgr;
        Class tmpClass = null;
        if (cls != null && cls.getName().equals("java.util.Map")) {
            tmpClass = HashMap.class;
        }
        else if (cls == null) {
            if (resultDefinition.getNumberOfResultExpressions() == 1) {
                tmpClass = Object.class;
            }
            else {
                tmpClass = Object[].class;
            }
        }
        else {
            tmpClass = cls;
        }
        this.resultClass = tmpClass;
        this.resultDefinition = resultDefinition;
        this.stmtMappings = null;
        if (resultDefinition != null) {
            this.resultFieldNames = new String[resultDefinition.getNumberOfResultExpressions()];
            this.resultFieldTypes = new Class[resultDefinition.getNumberOfResultExpressions()];
            for (int i = 0; i < this.resultFieldNames.length; ++i) {
                final Object stmtMap = resultDefinition.getMappingForResultExpression(i);
                if (stmtMap instanceof StatementMappingIndex) {
                    final StatementMappingIndex idx = (StatementMappingIndex)stmtMap;
                    this.resultFieldNames[i] = idx.getColumnAlias();
                    this.resultFieldTypes[i] = idx.getMapping().getJavaType();
                }
                else if (!(stmtMap instanceof StatementNewObjectMapping)) {
                    if (!(stmtMap instanceof StatementClassMapping)) {
                        throw new NucleusUserException("Unsupported component " + stmtMap.getClass().getName() + " found in results");
                    }
                }
            }
        }
        else {
            this.resultFieldNames = null;
            this.resultFieldTypes = null;
        }
    }
    
    public ResultClassROF(final RDBMSStoreManager storeMgr, final Class cls, final StatementClassMapping classDefinition) {
        this.resultClassFieldsByName = new HashMap();
        this.storeMgr = storeMgr;
        Class tmpClass = null;
        if (cls != null && cls.getName().equals("java.util.Map")) {
            tmpClass = HashMap.class;
        }
        else {
            tmpClass = cls;
        }
        this.resultClass = tmpClass;
        this.resultDefinition = null;
        final int[] memberNumbers = classDefinition.getMemberNumbers();
        this.stmtMappings = new StatementMappingIndex[memberNumbers.length];
        this.resultFieldNames = new String[this.stmtMappings.length];
        this.resultFieldTypes = new Class[this.stmtMappings.length];
        for (int i = 0; i < this.stmtMappings.length; ++i) {
            this.stmtMappings[i] = classDefinition.getMappingForMemberPosition(memberNumbers[i]);
            this.resultFieldNames[i] = this.stmtMappings[i].getMapping().getMemberMetaData().getName();
            this.resultFieldTypes[i] = this.stmtMappings[i].getMapping().getJavaType();
        }
    }
    
    public ResultClassROF(final RDBMSStoreManager storeMgr, final Class cls, final String[] resultFieldNames) {
        this.resultClassFieldsByName = new HashMap();
        this.storeMgr = storeMgr;
        Class tmpClass = null;
        if (cls != null && cls.getName().equals("java.util.Map")) {
            tmpClass = HashMap.class;
        }
        else {
            tmpClass = cls;
        }
        this.resultClass = tmpClass;
        if (QueryUtils.resultClassIsUserType(this.resultClass.getName())) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    ResultClassROF.this.populateDeclaredFieldsForUserType(ResultClassROF.this.resultClass);
                    return null;
                }
            });
        }
        this.stmtMappings = null;
        this.resultFieldTypes = null;
        if (resultFieldNames == null) {
            this.resultFieldNames = new String[0];
        }
        else {
            this.resultFieldNames = resultFieldNames;
        }
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet rs) {
        Object[] fieldValues = null;
        if (this.resultDefinition != null) {
            fieldValues = new Object[this.resultDefinition.getNumberOfResultExpressions()];
            for (int i = 0; i < this.resultDefinition.getNumberOfResultExpressions(); ++i) {
                final Object stmtMap = this.resultDefinition.getMappingForResultExpression(i);
                if (stmtMap instanceof StatementMappingIndex) {
                    final StatementMappingIndex idx = (StatementMappingIndex)stmtMap;
                    fieldValues[i] = idx.getMapping().getObject(ec, rs, idx.getColumnPositions());
                }
                else if (stmtMap instanceof StatementNewObjectMapping) {
                    final StatementNewObjectMapping newIdx = (StatementNewObjectMapping)stmtMap;
                    fieldValues[i] = this.getValueForNewObject(newIdx, ec, rs);
                }
                else if (stmtMap instanceof StatementClassMapping) {
                    final StatementClassMapping classMap = (StatementClassMapping)stmtMap;
                    final Class cls = ec.getClassLoaderResolver().classForName(classMap.getClassName());
                    final AbstractClassMetaData acmd = ec.getMetaDataManager().getMetaDataForClass(cls, ec.getClassLoaderResolver());
                    final PersistentClassROF rof = new PersistentClassROF(this.storeMgr, acmd, classMap, false, ec.getFetchPlan(), cls);
                    fieldValues[i] = rof.getObject(ec, rs);
                }
            }
        }
        else if (this.stmtMappings != null) {
            fieldValues = new Object[this.stmtMappings.length];
            for (int i = 0; i < this.stmtMappings.length; ++i) {
                if (this.stmtMappings[i] != null) {
                    fieldValues[i] = this.stmtMappings[i].getMapping().getObject(ec, rs, this.stmtMappings[i].getColumnPositions());
                }
                else {
                    fieldValues[i] = null;
                }
            }
        }
        else {
            try {
                fieldValues = new Object[this.resultFieldNames.length];
                for (int i = 0; i < fieldValues.length; ++i) {
                    fieldValues[i] = this.getResultObject(rs, i + 1);
                }
            }
            catch (SQLException sqe) {
                final String msg = ResultClassROF.LOCALISER.msg("021043", sqe.getMessage());
                NucleusLogger.QUERY.error(msg);
                throw new NucleusUserException(msg);
            }
        }
        if (this.resultClass == Object[].class) {
            return fieldValues;
        }
        if (QueryUtils.resultClassIsSimple(this.resultClass.getName())) {
            if (fieldValues.length == 1 && (fieldValues[0] == null || this.resultClass.isAssignableFrom(fieldValues[0].getClass()))) {
                return fieldValues[0];
            }
            if (fieldValues.length == 1 && !this.resultClass.isAssignableFrom(fieldValues[0].getClass())) {
                final String msg2 = ResultClassROF.LOCALISER.msg("021202", this.resultClass.getName(), fieldValues[0].getClass().getName());
                NucleusLogger.QUERY.error(msg2);
                throw new NucleusUserException(msg2);
            }
            final String msg2 = ResultClassROF.LOCALISER.msg("021203", this.resultClass.getName());
            NucleusLogger.QUERY.error(msg2);
            throw new NucleusUserException(msg2);
        }
        else {
            if (fieldValues.length == 1 && fieldValues[0] != null && this.resultClass.isAssignableFrom(fieldValues[0].getClass())) {
                return fieldValues[0];
            }
            Object obj = QueryUtils.createResultObjectUsingArgumentedConstructor(this.resultClass, fieldValues, this.resultFieldTypes);
            if (obj != null) {
                return obj;
            }
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                if (this.resultFieldNames != null) {
                    final Class[] ctr_arg_types = new Class[this.resultFieldNames.length];
                    for (int j = 0; j < this.resultFieldNames.length; ++j) {
                        if (fieldValues[j] != null) {
                            ctr_arg_types[j] = fieldValues[j].getClass();
                        }
                        else {
                            ctr_arg_types[j] = null;
                        }
                    }
                    NucleusLogger.QUERY.debug(ResultClassROF.LOCALISER.msg("021206", this.resultClass.getName(), StringUtils.objectArrayToString(ctr_arg_types)));
                }
                else {
                    final StringBuffer str = new StringBuffer();
                    for (int j = 0; j < this.stmtMappings.length; ++j) {
                        if (j > 0) {
                            str.append(",");
                        }
                        final Class javaType = this.stmtMappings[j].getMapping().getJavaType();
                        str.append(javaType.getName());
                    }
                    NucleusLogger.QUERY.debug(ResultClassROF.LOCALISER.msg("021206", this.resultClass.getName(), str.toString()));
                }
            }
            obj = QueryUtils.createResultObjectUsingDefaultConstructorAndSetters(this.resultClass, this.resultFieldNames, this.resultClassFieldsByName, fieldValues);
            return obj;
        }
    }
    
    protected Object getValueForNewObject(final StatementNewObjectMapping newMap, final ExecutionContext ec, final ResultSet rs) {
        Object value = null;
        if (newMap.getNumberOfConstructorArgMappings() == 0) {
            try {
                value = newMap.getObjectClass().newInstance();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            final int numArgs = newMap.getNumberOfConstructorArgMappings();
            final Class[] ctrArgTypes = new Class[numArgs];
            final Object[] ctrArgValues = new Object[numArgs];
            for (int i = 0; i < numArgs; ++i) {
                final Object obj = newMap.getConstructorArgMapping(i);
                if (obj instanceof StatementMappingIndex) {
                    final StatementMappingIndex idx = (StatementMappingIndex)obj;
                    ctrArgValues[i] = idx.getMapping().getObject(ec, rs, idx.getColumnPositions());
                }
                else if (obj instanceof StatementNewObjectMapping) {
                    ctrArgValues[i] = this.getValueForNewObject((StatementNewObjectMapping)obj, ec, rs);
                }
                else {
                    ctrArgValues[i] = obj;
                }
                if (ctrArgValues[i] != null) {
                    ctrArgTypes[i] = ctrArgValues[i].getClass();
                }
                else {
                    ctrArgTypes[i] = null;
                }
            }
            final Constructor ctr = ClassUtils.getConstructorWithArguments(newMap.getObjectClass(), ctrArgTypes);
            if (ctr == null) {
                final StringBuffer str = new StringBuffer(newMap.getObjectClass().getName() + "(");
                for (int j = 0; j < ctrArgTypes.length; ++j) {
                    str.append(ctrArgTypes[j].getName());
                    if (j != ctrArgTypes.length - 1) {
                        str.append(',');
                    }
                }
                str.append(")");
                throw new NucleusUserException(ResultClassROF.LOCALISER.msg("037013", str.toString()));
            }
            try {
                value = ctr.newInstance(ctrArgValues);
            }
            catch (Exception e2) {
                throw new NucleusUserException(ResultClassROF.LOCALISER.msg("037015", newMap.getObjectClass().getName(), e2));
            }
        }
        return value;
    }
    
    private void populateDeclaredFieldsForUserType(final Class cls) {
        for (int i = 0; i < cls.getDeclaredFields().length; ++i) {
            if (this.resultClassFieldsByName.put(cls.getDeclaredFields()[i].getName().toUpperCase(), cls.getDeclaredFields()[i]) != null) {
                throw new NucleusUserException(ResultClassROF.LOCALISER.msg("021210", cls.getDeclaredFields()[i].getName()));
            }
        }
        if (cls.getSuperclass() != null) {
            this.populateDeclaredFieldsForUserType(cls.getSuperclass());
        }
    }
    
    private Object getResultObject(final ResultSet rs, final int columnNumber) throws SQLException {
        final ResultSetGetter getter = ResultClassROF.resultSetGetters.get(this.resultClass);
        if (getter != null) {
            return getter.getValue(rs, columnNumber);
        }
        return rs.getObject(columnNumber);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        (ResultClassROF.resultSetGetters = new HashMap<Class, ResultSetGetter>(20)).put(Boolean.class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getBoolean(i);
            }
        });
        ResultClassROF.resultSetGetters.put(Byte.class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getByte(i);
            }
        });
        ResultClassROF.resultSetGetters.put(Short.class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getShort(i);
            }
        });
        ResultClassROF.resultSetGetters.put(Integer.class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getInt(i);
            }
        });
        ResultClassROF.resultSetGetters.put(Long.class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getLong(i);
            }
        });
        ResultClassROF.resultSetGetters.put(Float.class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getFloat(i);
            }
        });
        ResultClassROF.resultSetGetters.put(Double.class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getDouble(i);
            }
        });
        ResultClassROF.resultSetGetters.put(BigDecimal.class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getBigDecimal(i);
            }
        });
        ResultClassROF.resultSetGetters.put(byte[].class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getBytes(i);
            }
        });
        final ResultSetGetter timestampGetter = new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getTimestamp(i);
            }
        };
        ResultClassROF.resultSetGetters.put(Timestamp.class, timestampGetter);
        ResultClassROF.resultSetGetters.put(Date.class, timestampGetter);
        ResultClassROF.resultSetGetters.put(java.sql.Date.class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getDate(i);
            }
        });
        ResultClassROF.resultSetGetters.put(String.class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getString(i);
            }
        });
        ResultClassROF.resultSetGetters.put(Reader.class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getCharacterStream(i);
            }
        });
        ResultClassROF.resultSetGetters.put(Array.class, new ResultSetGetter() {
            @Override
            public Object getValue(final ResultSet rs, final int i) throws SQLException {
                return rs.getArray(i);
            }
        });
    }
    
    private interface ResultSetGetter
    {
        Object getValue(final ResultSet p0, final int p1) throws SQLException;
    }
}
