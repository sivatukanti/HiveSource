// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.types.BitDataValue;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.util.ReuseFactory;

public class BitConstantNode extends ConstantNode
{
    private int bitLength;
    
    public void init(final Object o) throws StandardException {
        super.init(o, Boolean.TRUE, ReuseFactory.getInteger(0));
    }
    
    public void init(final Object o, final Object o2) throws StandardException {
        final String s = (String)o;
        final byte[] fromHexString = StringUtil.fromHexString(s, 0, s.length());
        final Integer n = (Integer)o2;
        this.bitLength = n;
        this.init(TypeId.getBuiltInTypeId(-2), Boolean.FALSE, n);
        final BitDataValue bitDataValue = this.getDataValueFactory().getBitDataValue(fromHexString);
        bitDataValue.setWidth(this.bitLength, 0, false);
        this.setValue(bitDataValue);
    }
    
    Object getConstantValueAsObject() throws StandardException {
        return this.value.getBytes();
    }
    
    void generateConstant(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final byte[] bytes = this.value.getBytes();
        final String hexString = StringUtil.toHexString(bytes, 0, bytes.length);
        methodBuilder.push(hexString);
        methodBuilder.push(0);
        methodBuilder.push(hexString.length());
        methodBuilder.callMethod((short)184, "org.apache.derby.iapi.util.StringUtil", "fromHexString", "byte[]", 3);
    }
}
