// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.io;

import java.io.IOException;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.JobConf;

public class HiveIOExceptionHandlerUtil
{
    private static ThreadLocal<HiveIOExceptionHandlerChain> handlerChainInstance;
    
    private static HiveIOExceptionHandlerChain get(final JobConf job) {
        HiveIOExceptionHandlerChain cache = HiveIOExceptionHandlerUtil.handlerChainInstance.get();
        if (cache == null) {
            final HiveIOExceptionHandlerChain toSet = HiveIOExceptionHandlerChain.getHiveIOExceptionHandlerChain(job);
            HiveIOExceptionHandlerUtil.handlerChainInstance.set(toSet);
            cache = HiveIOExceptionHandlerUtil.handlerChainInstance.get();
        }
        return cache;
    }
    
    public static RecordReader handleRecordReaderCreationException(final Exception e, final JobConf job) throws IOException {
        final HiveIOExceptionHandlerChain ioExpectionHandlerChain = get(job);
        if (ioExpectionHandlerChain != null) {
            return ioExpectionHandlerChain.handleRecordReaderCreationException(e);
        }
        throw new IOException(e);
    }
    
    public static boolean handleRecordReaderNextException(final Exception e, final JobConf job) throws IOException {
        final HiveIOExceptionHandlerChain ioExpectionHandlerChain = get(job);
        if (ioExpectionHandlerChain != null) {
            return ioExpectionHandlerChain.handleRecordReaderNextException(e);
        }
        throw new IOException(e);
    }
    
    static {
        HiveIOExceptionHandlerUtil.handlerChainInstance = new ThreadLocal<HiveIOExceptionHandlerChain>();
    }
}
