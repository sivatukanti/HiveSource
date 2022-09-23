// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.metrics2.util.SampleStat;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MutableStat extends MutableMetric
{
    private final MetricsInfo numInfo;
    private final MetricsInfo avgInfo;
    private final MetricsInfo stdevInfo;
    private final MetricsInfo iMinInfo;
    private final MetricsInfo iMaxInfo;
    private final MetricsInfo minInfo;
    private final MetricsInfo maxInfo;
    private final MetricsInfo iNumInfo;
    private final SampleStat intervalStat;
    private final SampleStat prevStat;
    private final SampleStat.MinMax minMax;
    private long numSamples;
    private boolean extended;
    
    public MutableStat(final String name, final String description, final String sampleName, final String valueName, final boolean extended) {
        this.intervalStat = new SampleStat();
        this.prevStat = new SampleStat();
        this.minMax = new SampleStat.MinMax();
        this.numSamples = 0L;
        this.extended = false;
        final String ucName = StringUtils.capitalize(name);
        final String usName = StringUtils.capitalize(sampleName);
        final String uvName = StringUtils.capitalize(valueName);
        final String desc = StringUtils.uncapitalize(description);
        final String lsName = StringUtils.uncapitalize(sampleName);
        final String lvName = StringUtils.uncapitalize(valueName);
        this.numInfo = Interns.info(ucName + "Num" + usName, "Number of " + lsName + " for " + desc);
        this.iNumInfo = Interns.info(ucName + "INum" + usName, "Interval number of " + lsName + " for " + desc);
        this.avgInfo = Interns.info(ucName + "Avg" + uvName, "Average " + lvName + " for " + desc);
        this.stdevInfo = Interns.info(ucName + "Stdev" + uvName, "Standard deviation of " + lvName + " for " + desc);
        this.iMinInfo = Interns.info(ucName + "IMin" + uvName, "Interval min " + lvName + " for " + desc);
        this.iMaxInfo = Interns.info(ucName + "IMax" + uvName, "Interval max " + lvName + " for " + desc);
        this.minInfo = Interns.info(ucName + "Min" + uvName, "Min " + lvName + " for " + desc);
        this.maxInfo = Interns.info(ucName + "Max" + uvName, "Max " + lvName + " for " + desc);
        this.extended = extended;
    }
    
    public MutableStat(final String name, final String description, final String sampleName, final String valueName) {
        this(name, description, sampleName, valueName, false);
    }
    
    public synchronized void setExtended(final boolean extended) {
        this.extended = extended;
    }
    
    public synchronized void add(final long numSamples, final long sum) {
        this.intervalStat.add(numSamples, (double)sum);
        this.setChanged();
    }
    
    public synchronized void add(final long value) {
        this.intervalStat.add((double)value);
        this.minMax.add((double)value);
        this.setChanged();
    }
    
    @Override
    public synchronized void snapshot(final MetricsRecordBuilder builder, final boolean all) {
        if (all || this.changed()) {
            this.numSamples += this.intervalStat.numSamples();
            builder.addCounter(this.numInfo, this.numSamples).addGauge(this.avgInfo, this.lastStat().mean());
            if (this.extended) {
                builder.addGauge(this.stdevInfo, this.lastStat().stddev()).addGauge(this.iMinInfo, this.lastStat().min()).addGauge(this.iMaxInfo, this.lastStat().max()).addGauge(this.minInfo, this.minMax.min()).addGauge(this.maxInfo, this.minMax.max()).addGauge(this.iNumInfo, this.lastStat().numSamples());
            }
            if (this.changed()) {
                if (this.numSamples > 0L) {
                    this.intervalStat.copyTo(this.prevStat);
                    this.intervalStat.reset();
                }
                this.clearChanged();
            }
        }
    }
    
    public SampleStat lastStat() {
        return this.changed() ? this.intervalStat : this.prevStat;
    }
    
    public void resetMinMax() {
        this.minMax.reset();
    }
    
    @Override
    public String toString() {
        return this.lastStat().toString();
    }
}
