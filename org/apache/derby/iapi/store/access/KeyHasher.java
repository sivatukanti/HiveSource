// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

public class KeyHasher
{
    private final Object[] objects;
    
    public KeyHasher(final int n) {
        this.objects = new Object[n];
    }
    
    public void setObject(final int n, final Object o) {
        this.objects[n] = o;
    }
    
    public Object getObject(final int n) {
        return this.objects[n];
    }
    
    public static Object buildHashKey(final Object[] array, final int[] array2) {
        if (array2.length == 1) {
            return array[array2[0]];
        }
        final KeyHasher keyHasher = new KeyHasher(array2.length);
        for (int i = 0; i < array2.length; ++i) {
            keyHasher.setObject(i, array[array2[i]]);
        }
        return keyHasher;
    }
    
    public int hashCode() {
        int n = 0;
        for (int i = 0; i < this.objects.length; ++i) {
            n += this.objects[i].hashCode();
        }
        return n;
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof KeyHasher)) {
            return false;
        }
        final KeyHasher keyHasher = (KeyHasher)o;
        if (keyHasher.objects.length != this.objects.length) {
            return false;
        }
        for (int i = 0; i < this.objects.length; ++i) {
            if (!keyHasher.objects[i].equals(this.objects[i])) {
                return false;
            }
        }
        return true;
    }
}
