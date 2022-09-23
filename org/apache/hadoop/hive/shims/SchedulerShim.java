// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;

public interface SchedulerShim
{
    void refreshDefaultQueue(final Configuration p0, final String p1) throws IOException;
}
