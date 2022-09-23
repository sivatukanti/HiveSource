// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;
import org.apache.kerby.asn1.Tag;

public abstract class AbstractAsn1Type<T> extends Asn1Encodeable
{
    private T value;
    
    public AbstractAsn1Type(final Tag tag, final T value) {
        super(tag);
        this.value = value;
    }
    
    public AbstractAsn1Type(final Tag tag) {
        super(tag);
    }
    
    public AbstractAsn1Type(final UniversalTag tag, final T value) {
        super(tag);
        this.value = value;
    }
    
    public AbstractAsn1Type(final UniversalTag tag) {
        super(tag);
    }
    
    public T getValue() {
        return this.value;
    }
    
    public void setValue(final T value) {
        this.resetBodyLength();
        this.value = value;
        if (value instanceof Asn1Encodeable) {
            ((Asn1Encodeable)value).outerEncodeable = this;
        }
    }
    
    @Override
    public String toString() {
        return this.tag().typeStr();
    }
}
