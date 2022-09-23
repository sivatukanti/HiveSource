// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;

public class J2SEDataValueFactory extends DataValueFactoryImpl
{
    public void boot(final boolean b, final Properties properties) throws StandardException {
        super.boot(b, properties);
    }
    
    public NumberDataValue getDecimalDataValue(final Long value, NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new SQLDecimal();
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDecimalDataValue(final String s) throws StandardException {
        if (s != null) {
            return new SQLDecimal(s);
        }
        return new SQLDecimal();
    }
    
    public NumberDataValue getNullDecimal(final NumberDataValue numberDataValue) {
        if (numberDataValue == null) {
            return new SQLDecimal();
        }
        numberDataValue.setToNull();
        return numberDataValue;
    }
}
