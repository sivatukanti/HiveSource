// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.TypeCompiler;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.SQLTimestamp;
import org.apache.derby.iapi.types.SQLTime;
import org.apache.derby.iapi.types.SQLDate;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.types.TypeId;

public class UserTypeConstantNode extends ConstantNode
{
    Object value;
    
    public void init(final Object value) throws StandardException {
        DataValueDescriptor value2 = null;
        if (value instanceof TypeId) {
            super.init(value, Boolean.TRUE, ReuseFactory.getInteger(-1));
        }
        else {
            Object o = null;
            Object o2 = null;
            if (value instanceof DataValueDescriptor) {
                value2 = (DataValueDescriptor)value;
            }
            if (value instanceof Date || (value2 != null && value2.getTypeFormatId() == 298)) {
                o = ReuseFactory.getInteger(10);
                o2 = TypeId.getBuiltInTypeId(91);
            }
            else if (value instanceof Time || (value2 != null && value2.getTypeFormatId() == 299)) {
                o = ReuseFactory.getInteger(8);
                o2 = TypeId.getBuiltInTypeId(92);
            }
            else if (value instanceof Timestamp || (value2 != null && value2.getTypeFormatId() == 31)) {
                o = ReuseFactory.getInteger(29);
                o2 = TypeId.getBuiltInTypeId(93);
            }
            super.init(o2, (value == null) ? Boolean.TRUE : Boolean.FALSE, o);
            if (value2 != null) {
                this.setValue(value2);
            }
            else if (value instanceof Date) {
                this.setValue(new SQLDate((Date)value));
            }
            else if (value instanceof Time) {
                this.setValue(new SQLTime((Time)value));
            }
            else if (value instanceof Timestamp) {
                this.setValue(new SQLTimestamp((Timestamp)value));
            }
            this.value = value;
        }
    }
    
    public Object getObjectValue() {
        return this.value;
    }
    
    public boolean isNull() {
        return this.value == null;
    }
    
    public Object getConstantValueAsObject() {
        return this.value;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final TypeCompiler typeCompiler = this.getTypeCompiler();
        final String interfaceName = typeCompiler.interfaceName();
        if (this.value == null) {
            expressionClassBuilder.generateNull(methodBuilder, typeCompiler, this.getTypeServices().getCollationType());
        }
        else {
            final String correspondingJavaTypeName = this.getTypeId().getCorrespondingJavaTypeName();
            methodBuilder.push(this.value.toString());
            methodBuilder.callMethod((short)184, correspondingJavaTypeName, "valueOf", correspondingJavaTypeName, 1);
            expressionClassBuilder.generateDataValue(methodBuilder, typeCompiler, this.getTypeServices().getCollationType(), expressionClassBuilder.newFieldDeclaration(2, interfaceName));
        }
    }
    
    void generateConstant(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
    }
}
