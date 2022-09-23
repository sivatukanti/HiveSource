// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;
import org.apache.kerby.asn1.Asn1FieldInfo;

public class Asn1SequenceType extends Asn1CollectionType
{
    public Asn1SequenceType(final Asn1FieldInfo[] tags) {
        super(UniversalTag.SEQUENCE, tags);
    }
    
    @Override
    protected Asn1Collection createCollection() {
        return new Asn1Sequence();
    }
}
