// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.resource;

import java.util.Comparator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;

public class Priority
{
    public static org.apache.hadoop.yarn.api.records.Priority create(final int prio) {
        final org.apache.hadoop.yarn.api.records.Priority priority = RecordFactoryProvider.getRecordFactory(null).newRecordInstance(org.apache.hadoop.yarn.api.records.Priority.class);
        priority.setPriority(prio);
        return priority;
    }
    
    public static class Comparator implements java.util.Comparator<org.apache.hadoop.yarn.api.records.Priority>
    {
        @Override
        public int compare(final org.apache.hadoop.yarn.api.records.Priority o1, final org.apache.hadoop.yarn.api.records.Priority o2) {
            return o1.getPriority() - o2.getPriority();
        }
    }
}
