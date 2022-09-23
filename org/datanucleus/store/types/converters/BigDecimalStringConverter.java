// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import org.datanucleus.exceptions.NucleusDataStoreException;
import java.math.BigDecimal;

public class BigDecimalStringConverter implements TypeConverter<BigDecimal, String>
{
    @Override
    public BigDecimal toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        try {
            return new BigDecimal(str.trim());
        }
        catch (NumberFormatException nfe) {
            throw new NucleusDataStoreException(BigDecimalStringConverter.LOCALISER.msg("016002", str, BigDecimal.class.getName()), nfe);
        }
    }
    
    @Override
    public String toDatastoreType(final BigDecimal bd) {
        return (bd != null) ? bd.toString() : null;
    }
}
