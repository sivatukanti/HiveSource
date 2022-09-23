// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.Asn1FieldInfo;

public class Asn1TaggingSet extends Asn1TaggingCollection
{
    public Asn1TaggingSet(final int taggingTagNo, final Asn1FieldInfo[] tags, final boolean isAppSpecific, final boolean isImplicit) {
        super(taggingTagNo, tags, isAppSpecific, isImplicit);
    }
    
    @Override
    protected Asn1CollectionType createTaggedCollection(final Asn1FieldInfo[] tags) {
        return new Asn1SetType(tags);
    }
}
