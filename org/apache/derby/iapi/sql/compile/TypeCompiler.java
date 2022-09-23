// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public interface TypeCompiler
{
    public static final int LONGINT_MAXWIDTH_AS_CHAR = 20;
    public static final int INT_MAXWIDTH_AS_CHAR = 11;
    public static final int SMALLINT_MAXWIDTH_AS_CHAR = 6;
    public static final int TINYINT_MAXWIDTH_AS_CHAR = 4;
    public static final int DOUBLE_MAXWIDTH_AS_CHAR = 54;
    public static final int REAL_MAXWIDTH_AS_CHAR = 25;
    public static final int DEFAULT_DECIMAL_PRECISION = 5;
    public static final int DEFAULT_DECIMAL_SCALE = 0;
    public static final int MAX_DECIMAL_PRECISION_SCALE = 31;
    public static final int BOOLEAN_MAXWIDTH_AS_CHAR = 5;
    public static final String PLUS_OP = "+";
    public static final String DIVIDE_OP = "/";
    public static final String MINUS_OP = "-";
    public static final String TIMES_OP = "*";
    public static final String SUM_OP = "sum";
    public static final String AVG_OP = "avg";
    public static final String MOD_OP = "mod";
    
    DataTypeDescriptor resolveArithmeticOperation(final DataTypeDescriptor p0, final DataTypeDescriptor p1, final String p2) throws StandardException;
    
    boolean convertible(final TypeId p0, final boolean p1);
    
    boolean compatible(final TypeId p0);
    
    boolean storable(final TypeId p0, final ClassFactory p1);
    
    String interfaceName();
    
    String getCorrespondingPrimitiveTypeName();
    
    String getPrimitiveMethodName();
    
    void generateNull(final MethodBuilder p0, final int p1);
    
    void generateDataValue(final MethodBuilder p0, final int p1, final LocalField p2);
    
    int getCastToCharWidth(final DataTypeDescriptor p0);
}
