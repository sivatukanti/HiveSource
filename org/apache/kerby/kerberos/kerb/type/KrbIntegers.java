// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.kerby.asn1.type.Asn1Integer;

public class KrbIntegers extends KrbSequenceOfType<Asn1Integer>
{
    public KrbIntegers() {
    }
    
    public KrbIntegers(final List<Integer> values) {
        this.setValues(values);
    }
    
    public void setValues(final List<Integer> values) {
        this.clear();
        if (values != null) {
            for (final Integer value : values) {
                this.addElement(new Asn1Integer(value));
            }
        }
    }
    
    public List<Integer> getValues() {
        final List<Integer> results = new ArrayList<Integer>();
        for (final Asn1Integer value : this.getElements()) {
            results.add(value.getValue().intValue());
        }
        return results;
    }
}
