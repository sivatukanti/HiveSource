// 
// Decompiled by Procyon v0.5.36
// 

package antlr.collections;

public interface Enumerator
{
    Object cursor();
    
    Object next();
    
    boolean valid();
}
