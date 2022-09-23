// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.logging.Logger;

public final class $Stopwatch
{
    private static final Logger logger;
    private long start;
    
    public $Stopwatch() {
        this.start = System.currentTimeMillis();
    }
    
    public long reset() {
        final long now = System.currentTimeMillis();
        try {
            return now - this.start;
        }
        finally {
            this.start = now;
        }
    }
    
    public void resetAndLog(final String label) {
        $Stopwatch.logger.fine(label + ": " + this.reset() + "ms");
    }
    
    static {
        logger = Logger.getLogger($Stopwatch.class.getName());
    }
}
