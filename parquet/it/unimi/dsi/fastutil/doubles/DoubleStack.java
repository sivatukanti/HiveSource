// 
// Decompiled by Procyon v0.5.36
// 

package parquet.it.unimi.dsi.fastutil.doubles;

import parquet.it.unimi.dsi.fastutil.Stack;

public interface DoubleStack extends Stack<Double>
{
    void push(final double p0);
    
    double popDouble();
    
    double topDouble();
    
    double peekDouble(final int p0);
}
