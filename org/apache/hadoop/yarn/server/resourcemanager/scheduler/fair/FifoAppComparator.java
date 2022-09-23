// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Serializable;
import java.util.Comparator;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FifoAppComparator implements Comparator<FSAppAttempt>, Serializable
{
    private static final long serialVersionUID = 3428835083489547918L;
    
    @Override
    public int compare(final FSAppAttempt a1, final FSAppAttempt a2) {
        int res = a1.getPriority().compareTo(a2.getPriority());
        if (res == 0) {
            if (a1.getStartTime() < a2.getStartTime()) {
                res = -1;
            }
            else {
                res = ((a1.getStartTime() != a2.getStartTime()) ? 1 : 0);
            }
        }
        if (res == 0) {
            res = a1.getApplicationId().compareTo(a2.getApplicationId());
        }
        return res;
    }
}
