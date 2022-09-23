// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class QueueUserACLInfo
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static QueueUserACLInfo newInstance(final String queueName, final List<QueueACL> acls) {
        final QueueUserACLInfo info = Records.newRecord(QueueUserACLInfo.class);
        info.setQueueName(queueName);
        info.setUserAcls(acls);
        return info;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getQueueName();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setQueueName(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<QueueACL> getUserAcls();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setUserAcls(final List<QueueACL> p0);
}
