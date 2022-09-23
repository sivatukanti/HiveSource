// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.info.JVMInfo;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public final class NumericTypeCompiler extends BaseTypeCompiler
{
    public String interfaceName() {
        return "org.apache.derby.iapi.types.NumberDataValue";
    }
    
    public String getCorrespondingPrimitiveTypeName() {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 6: {
                return "double";
            }
            case 7: {
                return "int";
            }
            case 11: {
                return "long";
            }
            case 8: {
                return "float";
            }
            case 10: {
                return "short";
            }
            case 195: {
                return "byte";
            }
            default: {
                return null;
            }
        }
    }
    
    public String getPrimitiveMethodName() {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 6: {
                return "getDouble";
            }
            case 7: {
                return "getInt";
            }
            case 11: {
                return "getLong";
            }
            case 8: {
                return "getFloat";
            }
            case 10: {
                return "getShort";
            }
            case 195: {
                return "getByte";
            }
            default: {
                return null;
            }
        }
    }
    
    public int getCastToCharWidth(final DataTypeDescriptor dataTypeDescriptor) {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 197: {
                return dataTypeDescriptor.getPrecision() + 2;
            }
            case 6: {
                return 54;
            }
            case 7: {
                return 11;
            }
            case 11: {
                return 20;
            }
            case 8: {
                return 25;
            }
            case 10: {
                return 6;
            }
            case 195: {
                return 4;
            }
            default: {
                return 0;
            }
        }
    }
    
    public DataTypeDescriptor resolveArithmeticOperation(final DataTypeDescriptor dataTypeDescriptor, final DataTypeDescriptor dataTypeDescriptor2, final String anObject) throws StandardException {
        final TypeId typeId = dataTypeDescriptor.getTypeId();
        final TypeId typeId2 = dataTypeDescriptor2.getTypeId();
        boolean b = true;
        if (!typeId2.isNumericTypeId()) {
            b = false;
        }
        if ("mod".equals(anObject)) {
            switch (typeId.getJDBCTypeId()) {
                case -6:
                case -5:
                case 4:
                case 5: {
                    break;
                }
                default: {
                    b = false;
                    break;
                }
            }
            switch (typeId2.getJDBCTypeId()) {
                case -6:
                case -5:
                case 4:
                case 5: {
                    break;
                }
                default: {
                    b = false;
                    break;
                }
            }
        }
        if (!b) {
            throw StandardException.newException("42Y95", anObject, dataTypeDescriptor.getTypeId().getSQLTypeName(), dataTypeDescriptor2.getTypeId().getSQLTypeName());
        }
        DataTypeDescriptor dataTypeDescriptor3;
        NumericTypeCompiler numericTypeCompiler;
        if (typeId2.typePrecedence() > typeId.typePrecedence()) {
            dataTypeDescriptor3 = dataTypeDescriptor2;
            numericTypeCompiler = (NumericTypeCompiler)this.getTypeCompiler(typeId2);
        }
        else {
            dataTypeDescriptor3 = dataTypeDescriptor;
            numericTypeCompiler = (NumericTypeCompiler)this.getTypeCompiler(typeId);
        }
        final int precision = numericTypeCompiler.getPrecision(anObject, dataTypeDescriptor, dataTypeDescriptor2);
        final int scale = numericTypeCompiler.getScale(anObject, dataTypeDescriptor, dataTypeDescriptor2);
        int maximumWidth;
        if (dataTypeDescriptor3.getTypeId().isDecimalTypeId()) {
            maximumWidth = ((scale > 0) ? (precision + 3) : (precision + 1));
            if (maximumWidth < precision) {
                maximumWidth = Integer.MAX_VALUE;
            }
        }
        else {
            maximumWidth = dataTypeDescriptor3.getMaximumWidth();
        }
        return new DataTypeDescriptor(dataTypeDescriptor3.getTypeId(), precision, scale, dataTypeDescriptor.isNullable() || dataTypeDescriptor2.isNullable(), maximumWidth);
    }
    
    public boolean convertible(final TypeId typeId, final boolean b) {
        return this.numberConvertible(typeId, b);
    }
    
    public boolean compatible(final TypeId typeId) {
        return typeId.isNumericTypeId();
    }
    
    public boolean storable(final TypeId typeId, final ClassFactory classFactory) {
        return this.numberStorable(this.getTypeId(), typeId, classFactory);
    }
    
    String dataValueMethodName() {
        if (this.getStoredFormatIdFromTypeId() == 197) {
            return "getDecimalDataValue";
        }
        return super.dataValueMethodName();
    }
    
    String nullMethodName() {
        switch (this.getStoredFormatIdFromTypeId()) {
            case 197: {
                return "getNullDecimal";
            }
            case 6: {
                return "getNullDouble";
            }
            case 7: {
                return "getNullInteger";
            }
            case 11: {
                return "getNullLong";
            }
            case 8: {
                return "getNullFloat";
            }
            case 10: {
                return "getNullShort";
            }
            case 195: {
                return "getNullByte";
            }
            default: {
                return null;
            }
        }
    }
    
    private int getPrecision(final String s, final DataTypeDescriptor dataTypeDescriptor, final DataTypeDescriptor dataTypeDescriptor2) {
        if (this.getStoredFormatIdFromTypeId() != 197) {
            return dataTypeDescriptor.getPrecision();
        }
        final long n = dataTypeDescriptor.getScale();
        final long n2 = dataTypeDescriptor2.getScale();
        final long n3 = dataTypeDescriptor.getPrecision();
        final long n4 = dataTypeDescriptor2.getPrecision();
        long min;
        if (s == null) {
            min = this.getScale(s, dataTypeDescriptor, dataTypeDescriptor2) + Math.max(n3 - n, n4 - n2);
        }
        else if (s.equals("*")) {
            min = n3 + n4;
        }
        else if (s.equals("sum")) {
            min = n3 - n + n4 - n2 + this.getScale(s, dataTypeDescriptor, dataTypeDescriptor2);
        }
        else if (s.equals("/")) {
            min = Math.min(31L, this.getScale(s, dataTypeDescriptor, dataTypeDescriptor2) + n3 - n + n4);
        }
        else {
            min = this.getScale(s, dataTypeDescriptor, dataTypeDescriptor2) + Math.max(n3 - n, n4 - n2) + 1L;
            if (min > 31L) {
                min = 31L;
            }
        }
        if (min > 2147483647L) {
            min = 2147483647L;
        }
        return (int)Math.min(31L, min);
    }
    
    private int getScale(final String anObject, final DataTypeDescriptor dataTypeDescriptor, final DataTypeDescriptor dataTypeDescriptor2) {
        if (this.getStoredFormatIdFromTypeId() != 197) {
            return dataTypeDescriptor.getScale();
        }
        final long n = dataTypeDescriptor.getScale();
        final long n2 = dataTypeDescriptor2.getScale();
        final long n3 = dataTypeDescriptor.getPrecision();
        final long n4 = dataTypeDescriptor2.getPrecision();
        long b;
        if ("*".equals(anObject)) {
            b = n + n2;
        }
        else if ("/".equals(anObject)) {
            final LanguageConnectionContext languageConnectionContext = (LanguageConnectionContext)ContextService.getContext("LanguageConnectionContext");
            b = Math.max(31L - n3 + n - n2, 0L);
        }
        else if ("avg".equals(anObject)) {
            b = Math.max(Math.max(n, n2), 4L);
        }
        else {
            b = Math.max(n, n2);
        }
        if (b > 2147483647L) {
            b = 2147483647L;
        }
        return (int)Math.min(31L, b);
    }
    
    public void generateDataValue(final MethodBuilder methodBuilder, final int n, final LocalField localField) {
        if (!JVMInfo.J2ME && this.getTypeId().isDecimalTypeId()) {
            methodBuilder.upCast("java.lang.Number");
        }
        super.generateDataValue(methodBuilder, n, localField);
    }
}
