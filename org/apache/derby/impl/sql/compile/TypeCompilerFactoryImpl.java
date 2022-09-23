// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.sql.compile.TypeCompiler;
import org.apache.derby.iapi.sql.compile.TypeCompilerFactory;

public class TypeCompilerFactoryImpl implements TypeCompilerFactory
{
    private static final String PACKAGE_NAME = "org.apache.derby.impl.sql.compile.";
    static TypeCompiler bitTypeCompiler;
    static TypeCompiler booleanTypeCompiler;
    static TypeCompiler charTypeCompiler;
    static TypeCompiler decimalTypeCompiler;
    static TypeCompiler doubleTypeCompiler;
    static TypeCompiler intTypeCompiler;
    static TypeCompiler longintTypeCompiler;
    static TypeCompiler longvarbitTypeCompiler;
    static TypeCompiler longvarcharTypeCompiler;
    static TypeCompiler realTypeCompiler;
    static TypeCompiler smallintTypeCompiler;
    static TypeCompiler tinyintTypeCompiler;
    static TypeCompiler dateTypeCompiler;
    static TypeCompiler timeTypeCompiler;
    static TypeCompiler timestampTypeCompiler;
    static TypeCompiler varbitTypeCompiler;
    static TypeCompiler varcharTypeCompiler;
    static TypeCompiler refTypeCompiler;
    static TypeCompiler blobTypeCompiler;
    static TypeCompiler clobTypeCompiler;
    static TypeCompiler xmlTypeCompiler;
    
    public TypeCompiler getTypeCompiler(final TypeId typeId) {
        return staticGetTypeCompiler(typeId);
    }
    
    static TypeCompiler staticGetTypeCompiler(final TypeId typeId) {
        switch (typeId.getJDBCTypeId()) {
            case -2: {
                return TypeCompilerFactoryImpl.bitTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.BitTypeCompiler", TypeCompilerFactoryImpl.bitTypeCompiler, typeId);
            }
            case -7:
            case 16: {
                return TypeCompilerFactoryImpl.booleanTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.BooleanTypeCompiler", TypeCompilerFactoryImpl.booleanTypeCompiler, typeId);
            }
            case 1: {
                typeId.getSQLTypeName();
                return TypeCompilerFactoryImpl.charTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.CharTypeCompiler", TypeCompilerFactoryImpl.charTypeCompiler, typeId);
            }
            case 2:
            case 3: {
                return TypeCompilerFactoryImpl.decimalTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.NumericTypeCompiler", TypeCompilerFactoryImpl.decimalTypeCompiler, typeId);
            }
            case 8: {
                return TypeCompilerFactoryImpl.doubleTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.NumericTypeCompiler", TypeCompilerFactoryImpl.doubleTypeCompiler, typeId);
            }
            case 4: {
                return TypeCompilerFactoryImpl.intTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.NumericTypeCompiler", TypeCompilerFactoryImpl.intTypeCompiler, typeId);
            }
            case -5: {
                return TypeCompilerFactoryImpl.longintTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.NumericTypeCompiler", TypeCompilerFactoryImpl.longintTypeCompiler, typeId);
            }
            case 2004: {
                return TypeCompilerFactoryImpl.blobTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.LOBTypeCompiler", TypeCompilerFactoryImpl.blobTypeCompiler, typeId);
            }
            case -4: {
                return TypeCompilerFactoryImpl.longvarbitTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.BitTypeCompiler", TypeCompilerFactoryImpl.longvarbitTypeCompiler, typeId);
            }
            case 2005: {
                typeId.getSQLTypeName();
                return TypeCompilerFactoryImpl.clobTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.CLOBTypeCompiler", TypeCompilerFactoryImpl.clobTypeCompiler, typeId);
            }
            case -1: {
                typeId.getSQLTypeName();
                return TypeCompilerFactoryImpl.longvarcharTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.CharTypeCompiler", TypeCompilerFactoryImpl.longvarcharTypeCompiler, typeId);
            }
            case 7: {
                return TypeCompilerFactoryImpl.realTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.NumericTypeCompiler", TypeCompilerFactoryImpl.realTypeCompiler, typeId);
            }
            case 5: {
                return TypeCompilerFactoryImpl.smallintTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.NumericTypeCompiler", TypeCompilerFactoryImpl.smallintTypeCompiler, typeId);
            }
            case -6: {
                return TypeCompilerFactoryImpl.tinyintTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.NumericTypeCompiler", TypeCompilerFactoryImpl.tinyintTypeCompiler, typeId);
            }
            case 91: {
                return TypeCompilerFactoryImpl.dateTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.DateTypeCompiler", TypeCompilerFactoryImpl.dateTypeCompiler, typeId);
            }
            case 92: {
                return TypeCompilerFactoryImpl.timeTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.TimeTypeCompiler", TypeCompilerFactoryImpl.timeTypeCompiler, typeId);
            }
            case 93: {
                return TypeCompilerFactoryImpl.timestampTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.TimestampTypeCompiler", TypeCompilerFactoryImpl.timestampTypeCompiler, typeId);
            }
            case -3: {
                return TypeCompilerFactoryImpl.varbitTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.BitTypeCompiler", TypeCompilerFactoryImpl.varbitTypeCompiler, typeId);
            }
            case 12: {
                typeId.getSQLTypeName();
                return TypeCompilerFactoryImpl.varcharTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.CharTypeCompiler", TypeCompilerFactoryImpl.varcharTypeCompiler, typeId);
            }
            case 1111:
            case 2000: {
                if (typeId.isRefTypeId()) {
                    return TypeCompilerFactoryImpl.refTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.RefTypeCompiler", TypeCompilerFactoryImpl.refTypeCompiler, typeId);
                }
                final UserDefinedTypeCompiler userDefinedTypeCompiler = new UserDefinedTypeCompiler();
                userDefinedTypeCompiler.setTypeId(typeId);
                return userDefinedTypeCompiler;
            }
            case 2009: {
                return TypeCompilerFactoryImpl.xmlTypeCompiler = getAnInstance("org.apache.derby.impl.sql.compile.XMLTypeCompiler", TypeCompilerFactoryImpl.xmlTypeCompiler, typeId);
            }
            default: {
                return null;
            }
        }
    }
    
    private static TypeCompiler getAnInstance(final String className, TypeCompiler typeCompiler, final TypeId typeId) {
        if (typeCompiler == null) {
            try {
                typeCompiler = (TypeCompiler)Class.forName(className).newInstance();
                ((BaseTypeCompiler)typeCompiler).setTypeId(typeId);
            }
            catch (ClassNotFoundException ex) {}
            catch (IllegalAccessException ex2) {}
            catch (InstantiationException ex3) {}
        }
        return typeCompiler;
    }
}
