// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import org.apache.hadoop.metrics2.MetricsException;

class MetricsConfigException extends MetricsException
{
    private static final long serialVersionUID = 1L;
    
    MetricsConfigException(final String message) {
        super(message);
    }
    
    MetricsConfigException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    MetricsConfigException(final Throwable cause) {
        super(cause);
    }
}
