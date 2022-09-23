// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.info.JVMInfo;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.NumberDataValue;
import org.apache.derby.iapi.types.SQLReal;
import org.apache.derby.iapi.types.SQLDouble;
import org.apache.derby.iapi.types.DataTypeUtilities;
import org.apache.derby.iapi.types.SQLLongint;
import org.apache.derby.iapi.types.SQLSmallint;
import org.apache.derby.iapi.types.SQLInteger;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.SQLTinyint;
import org.apache.derby.iapi.types.TypeId;

public final class NumericConstantNode extends ConstantNode
{
    public void init(final Object o) throws StandardException {
        int decimalValuePrecision = 0;
        int decimalValueScale = 0;
        int computeMaxWidth = 0;
        TypeId typeId = null;
        int n = 0;
        Boolean b;
        boolean b2;
        if (o instanceof TypeId) {
            typeId = (TypeId)o;
            b = Boolean.TRUE;
            b2 = false;
            computeMaxWidth = 0;
        }
        else {
            b = Boolean.FALSE;
            b2 = true;
        }
        switch (this.getNodeType()) {
            case 75: {
                decimalValuePrecision = 5;
                decimalValueScale = 0;
                if (b2) {
                    computeMaxWidth = 2;
                    n = -6;
                    this.setValue(new SQLTinyint((Byte)o));
                    break;
                }
                break;
            }
            case 70: {
                decimalValuePrecision = 10;
                decimalValueScale = 0;
                if (b2) {
                    computeMaxWidth = 4;
                    n = 4;
                    this.setValue(new SQLInteger((Integer)o));
                    break;
                }
                break;
            }
            case 74: {
                decimalValuePrecision = 5;
                decimalValueScale = 0;
                if (b2) {
                    computeMaxWidth = 2;
                    n = 5;
                    this.setValue(new SQLSmallint((Short)o));
                    break;
                }
                break;
            }
            case 71: {
                decimalValuePrecision = 19;
                decimalValueScale = 0;
                if (b2) {
                    computeMaxWidth = 8;
                    n = -5;
                    this.setValue(new SQLLongint((Long)o));
                    break;
                }
                break;
            }
            case 67: {
                if (b2) {
                    final NumberDataValue decimalDataValue = this.getDataValueFactory().getDecimalDataValue((String)o);
                    n = 3;
                    decimalValuePrecision = decimalDataValue.getDecimalValuePrecision();
                    decimalValueScale = decimalDataValue.getDecimalValueScale();
                    computeMaxWidth = DataTypeUtilities.computeMaxWidth(decimalValuePrecision, decimalValueScale);
                    this.setValue(decimalDataValue);
                    break;
                }
                decimalValuePrecision = 5;
                decimalValueScale = 0;
                computeMaxWidth = 31;
                break;
            }
            case 68: {
                decimalValuePrecision = 52;
                decimalValueScale = 0;
                if (b2) {
                    computeMaxWidth = 8;
                    n = 8;
                    this.setValue(new SQLDouble((Double)o));
                    break;
                }
                break;
            }
            case 69: {
                decimalValuePrecision = 23;
                decimalValueScale = 0;
                if (b2) {
                    computeMaxWidth = 4;
                    n = 7;
                    this.setValue(new SQLReal((Float)o));
                    break;
                }
                break;
            }
        }
        this.setType((typeId != null) ? typeId : TypeId.getBuiltInTypeId(n), decimalValuePrecision, decimalValueScale, b, computeMaxWidth);
    }
    
    Object getConstantValueAsObject() throws StandardException {
        return this.value.getObject();
    }
    
    void generateConstant(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        switch (this.getNodeType()) {
            case 70: {
                methodBuilder.push(this.value.getInt());
                break;
            }
            case 75: {
                methodBuilder.push(this.value.getByte());
                break;
            }
            case 74: {
                methodBuilder.push(this.value.getShort());
                break;
            }
            case 67: {
                if (!JVMInfo.J2ME) {
                    methodBuilder.pushNewStart("java.math.BigDecimal");
                }
                methodBuilder.push(this.value.getString());
                if (!JVMInfo.J2ME) {
                    methodBuilder.pushNewComplete(1);
                    break;
                }
                break;
            }
            case 68: {
                methodBuilder.push(this.value.getDouble());
                break;
            }
            case 69: {
                methodBuilder.push(this.value.getFloat());
                break;
            }
            case 71: {
                methodBuilder.push(this.value.getLong());
                break;
            }
        }
    }
}
