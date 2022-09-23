// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.io;

import java.util.Iterator;
import java.io.IOException;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ReflectionUtils;
import java.util.ArrayList;
import org.apache.hadoop.mapred.JobConf;
import java.util.List;

public class HiveIOExceptionHandlerChain
{
    public static String HIVE_IO_EXCEPTION_HANDLE_CHAIN;
    private List<HiveIOExceptionHandler> handlerChain;
    
    public static HiveIOExceptionHandlerChain getHiveIOExceptionHandlerChain(final JobConf conf) {
        final HiveIOExceptionHandlerChain chain = new HiveIOExceptionHandlerChain();
        final String exceptionHandlerStr = conf.get(HiveIOExceptionHandlerChain.HIVE_IO_EXCEPTION_HANDLE_CHAIN);
        final List<HiveIOExceptionHandler> handlerChain = new ArrayList<HiveIOExceptionHandler>();
        if (exceptionHandlerStr != null && !exceptionHandlerStr.trim().equals("")) {
            final String[] handlerArr = exceptionHandlerStr.split(",");
            if (handlerArr != null && handlerArr.length > 0) {
                for (final String handlerStr : handlerArr) {
                    if (!handlerStr.trim().equals("")) {
                        try {
                            final Class<? extends HiveIOExceptionHandler> handlerCls = (Class<? extends HiveIOExceptionHandler>)Class.forName(handlerStr);
                            final HiveIOExceptionHandler handler = ReflectionUtils.newInstance(handlerCls, null);
                            handlerChain.add(handler);
                        }
                        catch (Exception ex) {}
                    }
                }
            }
        }
        chain.setHandlerChain(handlerChain);
        return chain;
    }
    
    protected List<HiveIOExceptionHandler> getHandlerChain() {
        return this.handlerChain;
    }
    
    protected void setHandlerChain(final List<HiveIOExceptionHandler> handlerChain) {
        this.handlerChain = handlerChain;
    }
    
    public RecordReader<?, ?> handleRecordReaderCreationException(final Exception e) throws IOException {
        RecordReader<?, ?> ret = null;
        if (this.handlerChain != null && this.handlerChain.size() > 0) {
            for (final HiveIOExceptionHandler handler : this.handlerChain) {
                ret = handler.handleRecordReaderCreationException(e);
                if (ret != null) {
                    return ret;
                }
            }
        }
        throw new IOException(e);
    }
    
    public boolean handleRecordReaderNextException(final Exception e) throws IOException {
        final HiveIOExceptionNextHandleResult result = new HiveIOExceptionNextHandleResult();
        if (this.handlerChain != null && this.handlerChain.size() > 0) {
            for (final HiveIOExceptionHandler handler : this.handlerChain) {
                handler.handleRecorReaderNextException(e, result);
                if (result.getHandled()) {
                    return result.getHandleResult();
                }
            }
        }
        throw new IOException(e);
    }
    
    static {
        HiveIOExceptionHandlerChain.HIVE_IO_EXCEPTION_HANDLE_CHAIN = "hive.io.exception.handlers";
    }
}
