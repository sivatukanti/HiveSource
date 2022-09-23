// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.collections.map;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.io.Serializable;

public class CaseInsensitiveMap extends AbstractHashedMap implements Serializable, Cloneable
{
    private static final long serialVersionUID = -7074655917369299456L;
    
    public CaseInsensitiveMap() {
        super(16, 0.75f, 12);
    }
    
    public CaseInsensitiveMap(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public CaseInsensitiveMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    public CaseInsensitiveMap(final Map map) {
        super(map);
    }
    
    protected Object convertKey(final Object key) {
        if (key != null) {
            final char[] chars = key.toString().toCharArray();
            for (int i = chars.length - 1; i >= 0; --i) {
                chars[i] = Character.toLowerCase(Character.toUpperCase(chars[i]));
            }
            return new String(chars);
        }
        return AbstractHashedMap.NULL;
    }
    
    public Object clone() {
        return super.clone();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        this.doWriteObject(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.doReadObject(in);
    }
}
