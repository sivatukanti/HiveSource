// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ContainerExitStatus
{
    public static final int SUCCESS = 0;
    public static final int INVALID = -1000;
    public static final int ABORTED = -100;
    public static final int DISKS_FAILED = -101;
    public static final int PREEMPTED = -102;
    public static final int KILLED_EXCEEDED_VMEM = -103;
    public static final int KILLED_EXCEEDED_PMEM = -104;
    public static final int KILLED_BY_APPMASTER = -105;
    public static final int KILLED_BY_RESOURCEMANAGER = -106;
    public static final int KILLED_AFTER_APP_COMPLETION = -107;
}
