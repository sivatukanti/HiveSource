// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.floats;

import parquet.it.unimi.dsi.fastutil.Stack;

public interface FloatStack extends Stack<Float>
{
    void push(final float p0);
    
    float popFloat();
    
    float topFloat();
    
    float peekFloat(final int p0);
}
