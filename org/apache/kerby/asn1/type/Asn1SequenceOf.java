// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;

public class Asn1SequenceOf<T extends Asn1Type> extends Asn1CollectionOf<T>
{
    public Asn1SequenceOf() {
        super(UniversalTag.SEQUENCE_OF);
    }
    
    public boolean isEmpty() {
        return this.getValue() == null || this.getElements().size() == 0;
    }
    
    public void add(final T element) {
        this.addElement(element);
    }
}
