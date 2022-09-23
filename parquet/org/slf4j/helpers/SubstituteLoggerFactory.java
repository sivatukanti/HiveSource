// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.slf4j.helpers;

import java.util.Collection;
import parquet.org.slf4j.Logger;
import java.util.ArrayList;
import java.util.List;
import parquet.org.slf4j.ILoggerFactory;

public class SubstituteLoggerFactory implements ILoggerFactory
{
    final List loggerNameList;
    
    public SubstituteLoggerFactory() {
        this.loggerNameList = new ArrayList();
    }
    
    public Logger getLogger(final String name) {
        synchronized (this.loggerNameList) {
            this.loggerNameList.add(name);
        }
        return NOPLogger.NOP_LOGGER;
    }
    
    public List getLoggerNameList() {
        final List copy = new ArrayList();
        synchronized (this.loggerNameList) {
            copy.addAll(this.loggerNameList);
        }
        return copy;
    }
}
