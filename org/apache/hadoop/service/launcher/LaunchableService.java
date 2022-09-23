// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service.launcher;

import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.Service;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface LaunchableService extends Service
{
    Configuration bindArgs(final Configuration p0, final List<String> p1) throws Exception;
    
    int execute() throws Exception;
}
