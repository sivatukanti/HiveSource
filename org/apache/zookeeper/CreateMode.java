// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public enum CreateMode
{
    PERSISTENT(0, false, false), 
    PERSISTENT_SEQUENTIAL(2, false, true), 
    EPHEMERAL(1, true, false), 
    EPHEMERAL_SEQUENTIAL(3, true, true);
    
    private static final Logger LOG;
    private boolean ephemeral;
    private boolean sequential;
    private int flag;
    
    private CreateMode(final int flag, final boolean ephemeral, final boolean sequential) {
        this.flag = flag;
        this.ephemeral = ephemeral;
        this.sequential = sequential;
    }
    
    public boolean isEphemeral() {
        return this.ephemeral;
    }
    
    public boolean isSequential() {
        return this.sequential;
    }
    
    public int toFlag() {
        return this.flag;
    }
    
    public static CreateMode fromFlag(final int flag) throws KeeperException {
        switch (flag) {
            case 0: {
                return CreateMode.PERSISTENT;
            }
            case 1: {
                return CreateMode.EPHEMERAL;
            }
            case 2: {
                return CreateMode.PERSISTENT_SEQUENTIAL;
            }
            case 3: {
                return CreateMode.EPHEMERAL_SEQUENTIAL;
            }
            default: {
                final String errMsg = "Received an invalid flag value: " + flag + " to convert to a CreateMode";
                CreateMode.LOG.error(errMsg);
                throw new KeeperException.BadArgumentsException(errMsg);
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(CreateMode.class);
    }
}
