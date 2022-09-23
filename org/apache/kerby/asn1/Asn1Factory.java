// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1;

import org.apache.kerby.asn1.type.Asn1Collection;
import org.apache.kerby.asn1.type.Asn1Simple;
import org.apache.kerby.asn1.type.Asn1Type;

public class Asn1Factory
{
    public static Asn1Type create(final int tagNo) {
        final UniversalTag tagNoEnum = UniversalTag.fromValue(tagNo);
        if (tagNoEnum != UniversalTag.UNKNOWN) {
            return create(tagNoEnum);
        }
        throw new IllegalArgumentException("Unexpected tag " + tagNo);
    }
    
    public static Asn1Type create(final UniversalTag tagNo) {
        if (Asn1Simple.isSimple(tagNo)) {
            return Asn1Simple.createSimple(tagNo);
        }
        if (Asn1Collection.isCollection(tagNo)) {
            return Asn1Collection.createCollection(tagNo);
        }
        throw new IllegalArgumentException("Unexpected tag " + tagNo);
    }
}
