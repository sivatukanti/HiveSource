// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import org.datanucleus.exceptions.NucleusDataStoreException;
import java.math.BigInteger;

public class BigIntegerStringConverter implements TypeConverter<BigInteger, String>
{
    @Override
    public BigInteger toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        try {
            return new BigInteger(str.trim());
        }
        catch (NumberFormatException nfe) {
            throw new NucleusDataStoreException(BigIntegerStringConverter.LOCALISER.msg("016002", str, BigInteger.class.getName()), nfe);
        }
    }
    
    @Override
    public String toDatastoreType(final BigInteger bi) {
        return (bi != null) ? bi.toString() : null;
    }
}
