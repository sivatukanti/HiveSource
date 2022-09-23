// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.log;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public class StacklessLogging implements AutoCloseable
{
    private final Set<StdErrLog> squelched;
    
    public StacklessLogging(final Class<?>... classesToSquelch) {
        this.squelched = new HashSet<StdErrLog>();
        for (final Class<?> clazz : classesToSquelch) {
            final Logger log = Log.getLogger(clazz);
            if (log instanceof StdErrLog) {
                final StdErrLog stdErrLog = (StdErrLog)log;
                if (!stdErrLog.isHideStacks()) {
                    stdErrLog.setHideStacks(true);
                    this.squelched.add(stdErrLog);
                }
            }
        }
    }
    
    public StacklessLogging(final Logger... logs) {
        this.squelched = new HashSet<StdErrLog>();
        for (final Logger log : logs) {
            if (log instanceof StdErrLog) {
                final StdErrLog stdErrLog = (StdErrLog)log;
                if (!stdErrLog.isHideStacks()) {
                    stdErrLog.setHideStacks(true);
                    this.squelched.add(stdErrLog);
                }
            }
        }
    }
    
    @Override
    public void close() {
        for (final StdErrLog log : this.squelched) {
            log.setHideStacks(false);
        }
    }
}
