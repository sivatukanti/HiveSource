// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type;

import java.util.Iterator;
import java.util.List;

public class KerberosStrings extends KrbSequenceOfType<KerberosString>
{
    public KerberosStrings() {
    }
    
    public KerberosStrings(final List<String> strings) {
        this.setValues(strings);
    }
    
    public void setValues(final List<String> values) {
        this.clear();
        if (values != null) {
            for (final String value : values) {
                this.addElement(new KerberosString(value));
            }
        }
    }
}
