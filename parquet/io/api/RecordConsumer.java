// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io.api;

public abstract class RecordConsumer
{
    public abstract void startMessage();
    
    public abstract void endMessage();
    
    public abstract void startField(final String p0, final int p1);
    
    public abstract void endField(final String p0, final int p1);
    
    public abstract void startGroup();
    
    public abstract void endGroup();
    
    public abstract void addInteger(final int p0);
    
    public abstract void addLong(final long p0);
    
    public abstract void addBoolean(final boolean p0);
    
    public abstract void addBinary(final Binary p0);
    
    public abstract void addFloat(final float p0);
    
    public abstract void addDouble(final double p0);
}
