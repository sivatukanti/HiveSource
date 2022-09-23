// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;

@InterfaceAudience.Private
public class QueueNotFoundException extends YarnRuntimeException
{
    private static final long serialVersionUID = 187239430L;
    
    public QueueNotFoundException(final String message) {
        super(message);
    }
}
