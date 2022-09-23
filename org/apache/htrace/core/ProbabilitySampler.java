// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import org.apache.htrace.shaded.commons.logging.LogFactory;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.htrace.shaded.commons.logging.Log;

public class ProbabilitySampler extends Sampler
{
    private static final Log LOG;
    public final double threshold;
    public static final String SAMPLER_FRACTION_CONF_KEY = "sampler.fraction";
    
    public ProbabilitySampler(final HTraceConfiguration conf) {
        this.threshold = Double.parseDouble(conf.get("sampler.fraction"));
        if (ProbabilitySampler.LOG.isTraceEnabled()) {
            ProbabilitySampler.LOG.trace("Created new ProbabilitySampler with threshold = " + this.threshold + ".");
        }
    }
    
    @Override
    public boolean next() {
        return ThreadLocalRandom.current().nextDouble() < this.threshold;
    }
    
    static {
        LOG = LogFactory.getLog(ProbabilitySampler.class);
    }
}
