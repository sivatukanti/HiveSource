// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.ResultSet;
import java.lang.reflect.Method;
import org.datanucleus.util.ClassUtils;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.ClassNameConstants;

public class EnumMapping extends SingleFieldMapping
{
    protected static final String ENUM_VALUE_GETTER = "enum-value-getter";
    protected static final String ENUM_GETTER_BY_VALUE = "enum-getter-by-value";
    protected String datastoreJavaType;
    
    public EnumMapping() {
        this.datastoreJavaType = ClassNameConstants.JAVA_LANG_STRING;
    }
    
    @Override
    public void initialize(final AbstractMemberMetaData fmd, final Table table, final ClassLoaderResolver clr) {
        if (fmd != null && fmd.isSerialized()) {
            this.datastoreJavaType = ClassNameConstants.JAVA_IO_SERIALIZABLE;
        }
        else if (fmd != null) {
            final ColumnMetaData[] colmds = JavaTypeMapping.getColumnMetaDataForMember(fmd, this.roleForMember);
            if (colmds != null && colmds.length > 0 && MetaDataUtils.isJdbcTypeNumeric(colmds[0].getJdbcType())) {
                this.datastoreJavaType = ClassNameConstants.JAVA_LANG_INTEGER;
            }
        }
        super.initialize(fmd, table, clr);
    }
    
    @Override
    public Object[] getValidValues(final int index) {
        if (this.mmd != null && this.mmd.getColumnMetaData() != null && this.mmd.getColumnMetaData().length > 0 && this.mmd.getColumnMetaData()[0].hasExtension("enum-check-constraint") && this.mmd.getColumnMetaData()[0].getValueForExtension("enum-check-constraint").equalsIgnoreCase("true")) {
            try {
                final Enum[] values = (Enum[])this.mmd.getType().getMethod("values", (Class[])null).invoke(null, (Object[])null);
                if (this.datastoreJavaType.equals(ClassNameConstants.JAVA_LANG_STRING)) {
                    final String[] valueStrings = new String[values.length];
                    for (int i = 0; i < values.length; ++i) {
                        valueStrings[i] = values[i].toString();
                    }
                    return valueStrings;
                }
                final Integer[] valueInts = new Integer[values.length];
                for (int i = 0; i < values.length; ++i) {
                    valueInts[i] = values[i].ordinal();
                }
                return valueInts;
            }
            catch (Exception e) {
                NucleusLogger.PERSISTENCE.warn(StringUtils.getStringFromStackTrace(e));
            }
        }
        return super.getValidValues(index);
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        return this.datastoreJavaType;
    }
    
    @Override
    public Class getJavaType() {
        return Enum.class;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
        if (value == null) {
            this.getDatastoreMapping(0).setObject(ps, exprIndex[0], null);
        }
        else if (this.datastoreJavaType.equals(ClassNameConstants.JAVA_LANG_INTEGER)) {
            if (value instanceof Enum) {
                int intVal = ((Enum)value).ordinal();
                String methodName = null;
                if (this.roleForMember == 2) {
                    if (this.mmd != null && this.mmd.hasExtension("enum-value-getter")) {
                        methodName = this.mmd.getValueForExtension("enum-value-getter");
                    }
                }
                else if (this.roleForMember == 3 || this.roleForMember == 4) {
                    if (this.mmd != null && this.mmd.getElementMetaData() != null && this.mmd.getElementMetaData().hasExtension("enum-value-getter")) {
                        methodName = this.mmd.getElementMetaData().getValueForExtension("enum-value-getter");
                    }
                }
                else if (this.roleForMember == 5) {
                    if (this.mmd != null && this.mmd.getKeyMetaData() != null && this.mmd.getKeyMetaData().hasExtension("enum-value-getter")) {
                        methodName = this.mmd.getKeyMetaData().getValueForExtension("enum-value-getter");
                    }
                }
                else if (this.roleForMember == 6 && this.mmd != null && this.mmd.getValueMetaData() != null && this.mmd.getValueMetaData().hasExtension("enum-value-getter")) {
                    methodName = this.mmd.getValueMetaData().getValueForExtension("enum-value-getter");
                }
                if (methodName != null) {
                    final String getterMethodName = this.mmd.getValueForExtension("enum-value-getter");
                    final Long longVal = this.getValueForEnumUsingMethod((Enum)value, getterMethodName);
                    if (longVal != null) {
                        intVal = longVal.intValue();
                    }
                }
                this.getDatastoreMapping(0).setInt(ps, exprIndex[0], intVal);
            }
            else if (value instanceof BigInteger) {
                this.getDatastoreMapping(0).setInt(ps, exprIndex[0], ((BigInteger)value).intValue());
            }
        }
        else if (this.datastoreJavaType.equals(ClassNameConstants.JAVA_LANG_STRING)) {
            String stringVal;
            if (value instanceof String) {
                stringVal = (String)value;
            }
            else {
                stringVal = ((Enum)value).name();
            }
            this.getDatastoreMapping(0).setString(ps, exprIndex[0], stringVal);
        }
        else {
            super.setObject(ec, ps, exprIndex, value);
        }
    }
    
    protected Long getValueForEnumUsingMethod(final Enum value, final String methodName) {
        try {
            final Method getterMethod = ClassUtils.getMethodForClass(value.getClass(), methodName, null);
            final Number num = (Number)getterMethod.invoke(value, (Object[])null);
            return num.longValue();
        }
        catch (Exception e) {
            NucleusLogger.PERSISTENCE.warn("Specified enum value-getter for method " + methodName + " on field " + this.mmd.getFullFieldName() + " gave an error on extracting the value : " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        if (exprIndex == null) {
            return null;
        }
        if (this.getDatastoreMapping(0).getObject(resultSet, exprIndex[0]) == null) {
            return null;
        }
        if (this.datastoreJavaType.equals(ClassNameConstants.JAVA_LANG_INTEGER)) {
            final long longVal = this.getDatastoreMapping(0).getLong(resultSet, exprIndex[0]);
            Class enumType = null;
            if (this.mmd == null) {
                enumType = ec.getClassLoaderResolver().classForName(this.type);
            }
            else {
                enumType = this.mmd.getType();
                if (this.roleForMember == 2 && this.mmd != null && this.mmd.hasExtension("enum-getter-by-value")) {
                    final String getterMethodName = this.mmd.getValueForExtension("enum-getter-by-value");
                    return this.getEnumValueForMethod(enumType, longVal, getterMethodName);
                }
                if (this.roleForMember == 3) {
                    enumType = ec.getClassLoaderResolver().classForName(this.mmd.getCollection().getElementType());
                    if (this.mmd != null && this.mmd.getElementMetaData() != null && this.mmd.getElementMetaData().hasExtension("enum-getter-by-value")) {
                        final String getterMethodName = this.mmd.getElementMetaData().getValueForExtension("enum-getter-by-value");
                        return this.getEnumValueForMethod(enumType, longVal, getterMethodName);
                    }
                }
                else if (this.roleForMember == 4) {
                    enumType = ec.getClassLoaderResolver().classForName(this.mmd.getArray().getElementType());
                    if (this.mmd != null && this.mmd.getElementMetaData() != null && this.mmd.getElementMetaData().hasExtension("enum-getter-by-value")) {
                        final String getterMethodName = this.mmd.getElementMetaData().getValueForExtension("enum-getter-by-value");
                        return this.getEnumValueForMethod(enumType, longVal, getterMethodName);
                    }
                }
                else if (this.roleForMember == 5) {
                    enumType = ec.getClassLoaderResolver().classForName(this.mmd.getMap().getKeyType());
                    if (this.mmd != null && this.mmd.getKeyMetaData() != null && this.mmd.getKeyMetaData().hasExtension("enum-getter-by-value")) {
                        final String getterMethodName = this.mmd.getKeyMetaData().getValueForExtension("enum-getter-by-value");
                        return this.getEnumValueForMethod(enumType, longVal, getterMethodName);
                    }
                }
                else if (this.roleForMember == 6) {
                    enumType = ec.getClassLoaderResolver().classForName(this.mmd.getMap().getValueType());
                    if (this.mmd != null && this.mmd.getValueMetaData() != null && this.mmd.getValueMetaData().hasExtension("enum-getter-by-value")) {
                        final String getterMethodName = this.mmd.getValueMetaData().getValueForExtension("enum-getter-by-value");
                        return this.getEnumValueForMethod(enumType, longVal, getterMethodName);
                    }
                }
            }
            return enumType.getEnumConstants()[(int)longVal];
        }
        if (this.datastoreJavaType.equals(ClassNameConstants.JAVA_LANG_STRING)) {
            final String stringVal = this.getDatastoreMapping(0).getString(resultSet, exprIndex[0]);
            Class enumType2 = null;
            if (this.mmd == null) {
                enumType2 = ec.getClassLoaderResolver().classForName(this.type);
            }
            else {
                enumType2 = this.mmd.getType();
                if (this.roleForMember == 3) {
                    enumType2 = ec.getClassLoaderResolver().classForName(this.mmd.getCollection().getElementType());
                }
                else if (this.roleForMember == 4) {
                    enumType2 = ec.getClassLoaderResolver().classForName(this.mmd.getArray().getElementType());
                }
                else if (this.roleForMember == 5) {
                    enumType2 = ec.getClassLoaderResolver().classForName(this.mmd.getMap().getKeyType());
                }
                else if (this.roleForMember == 6) {
                    enumType2 = ec.getClassLoaderResolver().classForName(this.mmd.getMap().getValueType());
                }
            }
            return Enum.valueOf((Class<Object>)enumType2, stringVal);
        }
        return super.getObject(ec, resultSet, exprIndex);
    }
    
    protected Object getEnumValueForMethod(final Class enumType, final long val, final String methodName) {
        try {
            final Method getterMethod = ClassUtils.getMethodForClass(enumType, methodName, new Class[] { Short.TYPE });
            return getterMethod.invoke(null, (short)val);
        }
        catch (Exception e) {
            NucleusLogger.PERSISTENCE.warn("Specified enum getter-by-value for field " + this.mmd.getFullFieldName() + " gave an error on extracting the enum so just using the ordinal : " + e.getMessage());
            try {
                final Method getterMethod = ClassUtils.getMethodForClass(enumType, methodName, new Class[] { Integer.TYPE });
                return getterMethod.invoke(null, (int)val);
            }
            catch (Exception e) {
                NucleusLogger.PERSISTENCE.warn("Specified enum getter-by-value for field " + this.mmd.getFullFieldName() + " gave an error on extracting the enum so just using the ordinal : " + e.getMessage());
                return null;
            }
        }
    }
}
