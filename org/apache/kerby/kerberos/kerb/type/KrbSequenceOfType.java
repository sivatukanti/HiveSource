// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type;

import java.util.Iterator;
import org.apache.kerby.asn1.type.Asn1String;
import java.util.ArrayList;
import java.util.List;
import org.apache.kerby.asn1.type.Asn1SequenceOf;
import org.apache.kerby.asn1.type.Asn1Type;

public class KrbSequenceOfType<T extends Asn1Type> extends Asn1SequenceOf<T>
{
    public List<String> getAsStrings() {
        final List<T> elements = this.getElements();
        if (elements == null) {
            return new ArrayList<String>();
        }
        final List<String> results = new ArrayList<String>(elements.size());
        for (final T ele : elements) {
            if (!(ele instanceof Asn1String)) {
                throw new RuntimeException("The targeted field type isn't of string");
            }
            results.add(((Asn1String)ele).getValue());
        }
        return results;
    }
}
