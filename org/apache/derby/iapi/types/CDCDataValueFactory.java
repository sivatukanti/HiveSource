// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.info.JVMInfo;
import java.util.Properties;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;

public class CDCDataValueFactory extends DataValueFactoryImpl implements ModuleSupportable
{
    public boolean canSupport(final Properties properties) {
        return JVMInfo.J2ME;
    }
    
    public NumberDataValue getDecimalDataValue(final Long value, NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new BigIntegerDecimal();
        }
        numberDataValue.setValue(value);
        return numberDataValue;
    }
    
    public NumberDataValue getDecimalDataValue(final String value) throws StandardException {
        final BigIntegerDecimal bigIntegerDecimal = new BigIntegerDecimal();
        bigIntegerDecimal.setValue(value);
        return bigIntegerDecimal;
    }
    
    public NumberDataValue getNullDecimal(final NumberDataValue numberDataValue) {
        if (numberDataValue == null) {
            return new BigIntegerDecimal();
        }
        numberDataValue.setToNull();
        return numberDataValue;
    }
}
