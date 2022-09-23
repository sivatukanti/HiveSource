// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

import org.apache.hadoop.yarn.exceptions.YarnException;

public class StoreFencedException extends YarnException
{
    private static final long serialVersionUID = 1L;
    
    public StoreFencedException() {
        super("RMStateStore has been fenced");
    }
}
