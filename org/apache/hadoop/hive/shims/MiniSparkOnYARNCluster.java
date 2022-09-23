// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import org.apache.hadoop.yarn.server.MiniYARNCluster;

public class MiniSparkOnYARNCluster extends MiniYARNCluster
{
    public MiniSparkOnYARNCluster(final String testName) {
        this(testName, 1, 1);
    }
    
    public MiniSparkOnYARNCluster(final String testName, final int numResourceManagers, final int numNodeManagers) {
        this(testName, numResourceManagers, numNodeManagers, 1, 1);
    }
    
    public MiniSparkOnYARNCluster(final String testName, final int numResourceManagers, final int numNodeManagers, final int numLocalDirs, final int numLogDirs) {
        super(testName, numResourceManagers, numNodeManagers, numLocalDirs, numLogDirs);
    }
}
