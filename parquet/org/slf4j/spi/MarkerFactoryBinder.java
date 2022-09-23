// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.slf4j.spi;

import parquet.org.slf4j.IMarkerFactory;

public interface MarkerFactoryBinder
{
    IMarkerFactory getMarkerFactory();
    
    String getMarkerFactoryClassStr();
}
