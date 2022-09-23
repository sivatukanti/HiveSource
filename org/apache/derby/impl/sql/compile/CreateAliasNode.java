// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.catalog.types.SynonymAliasInfo;
import org.apache.derby.catalog.types.RoutineAliasInfo;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import java.util.Arrays;
import org.apache.derby.iapi.types.TypeId;
import java.util.List;
import org.apache.derby.catalog.types.UDTAliasInfo;
import org.apache.derby.catalog.types.AggregateAliasInfo;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.catalog.AliasInfo;

public class CreateAliasNode extends DDLStatementNode
{
    public static final int PARAMETER_ARRAY = 0;
    public static final int TABLE_NAME = 1;
    public static final int DYNAMIC_RESULT_SET_COUNT = 2;
    public static final int LANGUAGE = 3;
    public static final int EXTERNAL_NAME = 4;
    public static final int PARAMETER_STYLE = 5;
    public static final int SQL_CONTROL = 6;
    public static final int DETERMINISTIC = 7;
    public static final int NULL_ON_NULL_INPUT = 8;
    public static final int RETURN_TYPE = 9;
    public static final int ROUTINE_SECURITY_DEFINER = 10;
    public static final int VARARGS = 11;
    public static final int ROUTINE_ELEMENT_COUNT = 12;
    private static final String[] NON_RESERVED_FUNCTION_NAMES;
    private static final String[] NON_RESERVED_AGGREGATES;
    public static final int AGG_FOR_TYPE = 0;
    public static final int AGG_RETURN_TYPE = 1;
    public static final int AGG_ELEMENT_COUNT = 2;
    private String javaClassName;
    private String methodName;
    private char aliasType;
    private AliasInfo aliasInfo;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) throws StandardException {
        final TableName tableName = (TableName)o;
        this.aliasType = (char)o5;
        this.initAndCheck(tableName);
        switch (this.aliasType) {
            case 'G': {
                this.javaClassName = (String)o2;
                final Object[] array = (Object[])o4;
                final TypeDescriptor bindUserCatalogType = this.bindUserCatalogType((TypeDescriptor)array[0]);
                final TypeDescriptor bindUserCatalogType2 = this.bindUserCatalogType((TypeDescriptor)array[1]);
                if (bindUserCatalogType.getJDBCTypeId() == 2009 || bindUserCatalogType2.getJDBCTypeId() == 2009) {
                    throw StandardException.newException("42ZB3");
                }
                this.aliasInfo = new AggregateAliasInfo(bindUserCatalogType, bindUserCatalogType2);
                this.implicitCreateSchema = true;
                break;
            }
            case 'A': {
                this.javaClassName = (String)o2;
                this.aliasInfo = new UDTAliasInfo();
                this.implicitCreateSchema = true;
                break;
            }
            case 'F':
            case 'P': {
                this.javaClassName = (String)o2;
                this.methodName = (String)o3;
                final Object[] array2 = (Object[])o4;
                final Object[] array3 = (Object[])array2[0];
                final int size = ((List)array3[0]).size();
                if (this.methodName.indexOf(40) != -1) {
                    this.getDataDictionary().checkVersion(130, "EXTERNAL NAME 'class.method(<signature>)'");
                }
                String[] array4 = null;
                TypeDescriptor[] array5 = null;
                int[] array6 = null;
                if (size != 0) {
                    array4 = ((List)array3[0]).toArray(new String[size]);
                    array5 = ((List)array3[1]).toArray(new TypeDescriptor[size]);
                    array6 = new int[size];
                    for (int i = 0; i < size; ++i) {
                        array6[i] = (int)((List)array3[2]).get(i);
                        if (!array5[i].isUserDefinedType() && TypeId.getBuiltInTypeId(array5[i].getJDBCTypeId()).isXMLTypeId()) {
                            throw StandardException.newException("42962", array4[i]);
                        }
                    }
                    if (size > 1) {
                        final String[] a = new String[size];
                        System.arraycopy(array4, 0, a, 0, size);
                        Arrays.sort(a);
                        for (int j = 1; j < a.length; ++j) {
                            if (!a[j].equals("") && a[j].equals(a[j - 1])) {
                                throw StandardException.newException("42734", a[j], this.getFullName());
                            }
                        }
                    }
                }
                final Integer n = (Integer)array2[2];
                final int n2 = (n == null) ? 0 : n;
                final Short n3 = (Short)array2[6];
                short shortValue;
                if (n3 != null) {
                    shortValue = n3;
                }
                else {
                    shortValue = (short)((this.aliasType != 'P') ? 1 : 0);
                }
                final Boolean b = (Boolean)array2[7];
                final boolean b2 = b != null && b;
                final Boolean b3 = (Boolean)array2[11];
                final boolean b4 = b3 != null && b3;
                final Boolean b5 = (Boolean)array2[10];
                final boolean b6 = b5 != null && b5;
                final Boolean b7 = (Boolean)array2[8];
                final boolean b8 = b7 == null || b7;
                TypeDescriptor catalogType = (TypeDescriptor)array2[9];
                if (catalogType != null) {
                    catalogType = this.bindUserType(DataTypeDescriptor.getType(catalogType)).getCatalogType();
                }
                this.aliasInfo = new RoutineAliasInfo(this.methodName, size, array4, array5, array6, n2, (short)array2[5], shortValue, b2, b4, b6, b8, catalogType);
                this.implicitCreateSchema = true;
                break;
            }
            case 'S': {
                this.implicitCreateSchema = true;
                final TableName tableName2 = (TableName)o2;
                String s;
                if (tableName2.getSchemaName() != null) {
                    s = tableName2.getSchemaName();
                }
                else {
                    s = this.getSchemaDescriptor().getSchemaName();
                }
                this.aliasInfo = new SynonymAliasInfo(s, tableName2.getTableName());
                break;
            }
        }
    }
    
    public String statementToString() {
        switch (this.aliasType) {
            case 'G': {
                return "CREATE DERBY AGGREGATE";
            }
            case 'A': {
                return "CREATE TYPE";
            }
            case 'P': {
                return "CREATE PROCEDURE";
            }
            case 'S': {
                return "CREATE SYNONYM";
            }
            default: {
                return "CREATE FUNCTION";
            }
        }
    }
    
    public void bindStatement() throws StandardException {
        if (this.aliasType == 'F' || this.aliasType == 'P') {
            final RoutineAliasInfo routineAliasInfo = (RoutineAliasInfo)this.aliasInfo;
            routineAliasInfo.setCollationTypeForAllStringTypes(this.getSchemaDescriptor().getCollationType());
            this.bindParameterTypes((RoutineAliasInfo)this.aliasInfo);
            if (routineAliasInfo.hasVarargs()) {
                switch (routineAliasInfo.getParameterStyle()) {
                    case 1:
                    case 2: {
                        if (routineAliasInfo.getMaxDynamicResultSets() > 0) {
                            throw StandardException.newException("42ZCB");
                        }
                        break;
                    }
                    default: {
                        throw StandardException.newException("42ZC9");
                    }
                }
            }
            if (routineAliasInfo.getParameterStyle() == 2 && !routineAliasInfo.hasVarargs()) {
                throw StandardException.newException("42ZCA");
            }
        }
        if (this.aliasType == 'A') {
            final TypeId[] allBuiltinTypeIds = TypeId.getAllBuiltinTypeIds();
            final int length = allBuiltinTypeIds.length;
            int startsWith = this.javaClassName.startsWith("org.apache.derby.") ? 1 : 0;
            if (startsWith == 0) {
                for (int i = 0; i < length; ++i) {
                    if (allBuiltinTypeIds[i].getCorrespondingJavaTypeName().equals(this.javaClassName)) {
                        startsWith = 1;
                        break;
                    }
                }
            }
            if (startsWith != 0) {
                throw StandardException.newException("42Z10", this.javaClassName);
            }
        }
        else {
            if (this.aliasType == 'G') {
                this.bindAggregate();
            }
            if (this.aliasType != 'S') {
                return;
            }
            if (this.isSessionSchema(this.getSchemaDescriptor().getSchemaName())) {
                throw StandardException.newException("XCL51.S");
            }
            final String synonymSchema = ((SynonymAliasInfo)this.aliasInfo).getSynonymSchema();
            final String synonymTable = ((SynonymAliasInfo)this.aliasInfo).getSynonymTable();
            if (this.getObjectName().equals(synonymSchema, synonymTable)) {
                throw StandardException.newException("42916", this.getFullName(), synonymSchema + "." + synonymTable);
            }
            final SchemaDescriptor schemaDescriptor = this.getSchemaDescriptor(synonymSchema, false);
            if (schemaDescriptor != null && this.isSessionSchema(schemaDescriptor)) {
                throw StandardException.newException("XCL51.S");
            }
        }
    }
    
    private void bindAggregate() throws StandardException {
        final String relativeName = this.getRelativeName();
        final List routineList = this.getDataDictionary().getRoutineList(this.getSchemaDescriptor("SYSFUN", true).getUUID().toString(), relativeName, 'F');
        for (int i = 0; i < routineList.size(); ++i) {
            if (((RoutineAliasInfo)routineList.get(i).getAliasInfo()).getParameterCount() == 1) {
                throw this.illegalAggregate();
            }
        }
        for (int j = 0; j < CreateAliasNode.NON_RESERVED_FUNCTION_NAMES.length; ++j) {
            if (CreateAliasNode.NON_RESERVED_FUNCTION_NAMES[j].equals(relativeName)) {
                throw this.illegalAggregate();
            }
        }
        for (int k = 0; k < CreateAliasNode.NON_RESERVED_AGGREGATES.length; ++k) {
            if (CreateAliasNode.NON_RESERVED_AGGREGATES[k].equals(relativeName)) {
                throw this.illegalAggregate();
            }
        }
        ((AggregateAliasInfo)this.aliasInfo).setCollationTypeForAllStringTypes(this.getSchemaDescriptor().getCollationType());
    }
    
    private StandardException illegalAggregate() {
        return StandardException.newException("42ZC3", this.getRelativeName());
    }
    
    private void bindParameterTypes(final RoutineAliasInfo routineAliasInfo) throws StandardException {
        final TypeDescriptor[] parameterTypes = routineAliasInfo.getParameterTypes();
        if (parameterTypes == null) {
            return;
        }
        for (int length = parameterTypes.length, i = 0; i < length; ++i) {
            parameterTypes[i] = this.bindUserCatalogType(parameterTypes[i]);
        }
    }
    
    public ConstantAction makeConstantAction() throws StandardException {
        return this.getGenericConstantActionFactory().getCreateAliasConstantAction(this.getRelativeName(), this.getSchemaDescriptor().getSchemaName(), this.javaClassName, this.aliasInfo, this.aliasType);
    }
    
    static {
        NON_RESERVED_FUNCTION_NAMES = new String[] { "ABS", "ABSVAL", "DATE", "DAY", "LCASE", "LENGTH", "MONTH", "SQRT", "TIME", "TIMESTAMP", "UCASE" };
        NON_RESERVED_AGGREGATES = new String[] { "COLLECT", "COUNT", "EVERY", "FUSION", "INTERSECTION", "STDDEV_POP", "STDDEV_SAMP", "VAR_POP", "VAR_SAMP" };
    }
}
