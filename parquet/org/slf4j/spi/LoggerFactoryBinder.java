// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.slf4j.spi;

import parquet.org.slf4j.ILoggerFactory;

public interface LoggerFactoryBinder
{
    ILoggerFactory getLoggerFactory();
    
    String getLoggerFactoryClassStr();
}
