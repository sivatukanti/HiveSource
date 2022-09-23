// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class NewAppWeightBooster extends Configured implements WeightAdjuster
{
    private static final float DEFAULT_FACTOR = 3.0f;
    private static final long DEFAULT_DURATION = 300000L;
    private float factor;
    private long duration;
    
    @Override
    public void setConf(final Configuration conf) {
        if (conf != null) {
            this.factor = conf.getFloat("mapred.newjobweightbooster.factor", 3.0f);
            this.duration = conf.getLong("mapred.newjobweightbooster.duration", 300000L);
        }
        super.setConf(conf);
    }
    
    @Override
    public double adjustWeight(final FSAppAttempt app, final double curWeight) {
        final long start = app.getStartTime();
        final long now = System.currentTimeMillis();
        if (now - start < this.duration) {
            return curWeight * this.factor;
        }
        return curWeight;
    }
}
