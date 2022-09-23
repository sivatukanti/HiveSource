// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.Recoverable;

@InterfaceAudience.LimitedPrivate({ "yarn" })
@InterfaceStability.Evolving
public interface ResourceScheduler extends YarnScheduler, Recoverable
{
    void setRMContext(final RMContext p0);
    
    void reinitialize(final Configuration p0, final RMContext p1) throws IOException;
}
