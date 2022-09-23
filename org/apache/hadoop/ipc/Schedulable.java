// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public interface Schedulable
{
    UserGroupInformation getUserGroupInformation();
    
    int getPriorityLevel();
}
