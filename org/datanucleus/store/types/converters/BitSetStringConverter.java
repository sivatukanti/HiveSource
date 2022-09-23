// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types.converters;

import org.datanucleus.exceptions.NucleusDataStoreException;
import java.util.StringTokenizer;
import java.util.BitSet;

public class BitSetStringConverter implements TypeConverter<BitSet, String>
{
    @Override
    public BitSet toMemberType(final String str) {
        if (str == null) {
            return null;
        }
        final BitSet set = new BitSet();
        final StringTokenizer tokeniser = new StringTokenizer(str.substring(1, str.length() - 1), ",");
        while (tokeniser.hasMoreTokens()) {
            final String token = tokeniser.nextToken().trim();
            try {
                final int position = new Integer(token);
                set.set(position);
            }
            catch (NumberFormatException nfe) {
                throw new NucleusDataStoreException(BitSetStringConverter.LOCALISER.msg("016002", str, BitSet.class.getName()), nfe);
            }
        }
        return set;
    }
    
    @Override
    public String toDatastoreType(final BitSet set) {
        return (set != null) ? set.toString() : null;
    }
}
