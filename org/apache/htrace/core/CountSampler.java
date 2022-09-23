// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import java.util.concurrent.ThreadLocalRandom;

public class CountSampler extends Sampler
{
    public static final String SAMPLER_FREQUENCY_CONF_KEY = "sampler.frequency";
    final long frequency;
    long count;
    
    public CountSampler(final HTraceConfiguration conf) {
        this.count = ThreadLocalRandom.current().nextLong();
        this.frequency = Long.parseLong(conf.get("sampler.frequency"), 10);
    }
    
    @Override
    public boolean next() {
        return this.count++ % this.frequency == 0L;
    }
}
