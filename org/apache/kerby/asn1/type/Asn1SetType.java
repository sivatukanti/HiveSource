// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;
import org.apache.kerby.asn1.Asn1FieldInfo;

public class Asn1SetType extends Asn1CollectionType
{
    public Asn1SetType(final Asn1FieldInfo[] tags) {
        super(UniversalTag.SET, tags);
    }
    
    @Override
    protected Asn1Collection createCollection() {
        return new Asn1Set();
    }
}
