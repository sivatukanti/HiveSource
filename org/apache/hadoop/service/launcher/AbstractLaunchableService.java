// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service.launcher;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class AbstractLaunchableService extends AbstractService implements LaunchableService
{
    private static final Logger LOG;
    
    protected AbstractLaunchableService(final String name) {
        super(name);
    }
    
    @Override
    public Configuration bindArgs(final Configuration config, final List<String> args) throws Exception {
        if (AbstractLaunchableService.LOG.isDebugEnabled()) {
            AbstractLaunchableService.LOG.debug("Service {} passed in {} arguments:", this.getName(), args.size());
            for (final String arg : args) {
                AbstractLaunchableService.LOG.debug(arg);
            }
        }
        return config;
    }
    
    @Override
    public int execute() throws Exception {
        return 0;
    }
    
    static {
        LOG = LoggerFactory.getLogger(AbstractLaunchableService.class);
    }
}
