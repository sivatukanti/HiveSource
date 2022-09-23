// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.datastore;

public interface Sequence
{
    String getName();
    
    Object next();
    
    void allocate(final int p0);
    
    Object current();
    
    long nextValue();
    
    long currentValue();
}
