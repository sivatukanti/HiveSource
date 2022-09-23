// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.io.IOException;
import java.util.Iterator;
import org.apache.kerby.asn1.Asn1Binder;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import org.apache.kerby.asn1.UniversalTag;

public abstract class Asn1CollectionOf<T extends Asn1Type> extends Asn1Collection
{
    public Asn1CollectionOf(final UniversalTag universalTag) {
        super(universalTag);
    }
    
    @Override
    protected void decodeElements() throws IOException {
        for (final Asn1ParseResult parsingItem : this.getContainer().getChildren()) {
            if (parsingItem.isEOC()) {
                continue;
            }
            final Asn1Type tmpValue = this.createElement();
            Asn1Binder.bind(parsingItem, tmpValue);
            this.addItem(tmpValue);
        }
    }
    
    public List<T> getElements() {
        return (List<T>)this.getValue();
    }
    
    public void setElements(final List<T> elements) {
        super.clear();
        for (final T ele : elements) {
            this.addElement(ele);
        }
    }
    
    public void addElements(final T... elements) {
        for (final T ele : elements) {
            this.addElement(ele);
        }
    }
    
    public void addElement(final T element) {
        super.addItem(element);
    }
    
    private Class<T> getElementType() {
        final Class<T> elementType = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return elementType;
    }
    
    protected T createElement() throws IOException {
        final Class<?> eleType = this.getElementType();
        try {
            final T result = (T)eleType.newInstance();
            return result;
        }
        catch (Exception e) {
            throw new IOException("Failed to create element type, no default constructor? " + eleType.getName(), e);
        }
    }
}
