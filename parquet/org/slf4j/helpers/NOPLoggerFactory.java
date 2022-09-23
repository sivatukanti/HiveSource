// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.slf4j.helpers;

import parquet.org.slf4j.Logger;
import parquet.org.slf4j.ILoggerFactory;

public class NOPLoggerFactory implements ILoggerFactory
{
    public Logger getLogger(final String name) {
        return NOPLogger.NOP_LOGGER;
    }
}
