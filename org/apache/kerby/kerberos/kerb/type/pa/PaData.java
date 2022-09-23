// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa;

import java.util.Iterator;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceOfType;

public class PaData extends KrbSequenceOfType<PaDataEntry>
{
    public PaDataEntry findEntry(final PaDataType paType) {
        for (final PaDataEntry pae : this.getElements()) {
            if (pae.getPaDataType() == paType) {
                return pae;
            }
        }
        return null;
    }
}
