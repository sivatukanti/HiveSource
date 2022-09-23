// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;

public class Asn1SetOf<T extends Asn1Type> extends Asn1CollectionOf<T>
{
    public Asn1SetOf() {
        super(UniversalTag.SET_OF);
    }
}
