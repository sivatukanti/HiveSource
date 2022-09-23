// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api;

import java.nio.ByteBuffer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class AuxiliaryService extends AbstractService
{
    private Path recoveryPath;
    
    protected AuxiliaryService(final String name) {
        super(name);
        this.recoveryPath = null;
    }
    
    protected Path getRecoveryPath() {
        return this.recoveryPath;
    }
    
    public abstract void initializeApplication(final ApplicationInitializationContext p0);
    
    public abstract void stopApplication(final ApplicationTerminationContext p0);
    
    public abstract ByteBuffer getMetaData();
    
    public void initializeContainer(final ContainerInitializationContext initContainerContext) {
    }
    
    public void stopContainer(final ContainerTerminationContext stopContainerContext) {
    }
    
    public void setRecoveryPath(final Path recoveryPath) {
        this.recoveryPath = recoveryPath;
    }
}
