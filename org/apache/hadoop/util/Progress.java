// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "MapReduce" })
@InterfaceStability.Unstable
public class Progress
{
    private static final Logger LOG;
    private String status;
    private float progress;
    private int currentPhase;
    private ArrayList<Progress> phases;
    private Progress parent;
    private boolean fixedWeightageForAllPhases;
    private float progressPerPhase;
    private ArrayList<Float> progressWeightagesForPhases;
    
    public Progress() {
        this.status = "";
        this.phases = new ArrayList<Progress>();
        this.fixedWeightageForAllPhases = false;
        this.progressPerPhase = 0.0f;
        this.progressWeightagesForPhases = new ArrayList<Float>();
    }
    
    public Progress addPhase(final String status) {
        final Progress phase = this.addPhase();
        phase.setStatus(status);
        return phase;
    }
    
    public synchronized Progress addPhase() {
        final Progress phase = this.addNewPhase();
        this.progressPerPhase = 1.0f / this.phases.size();
        this.fixedWeightageForAllPhases = true;
        return phase;
    }
    
    private synchronized Progress addNewPhase() {
        final Progress phase = new Progress();
        this.phases.add(phase);
        phase.setParent(this);
        return phase;
    }
    
    public Progress addPhase(final String status, final float weightage) {
        final Progress phase = this.addPhase(weightage);
        phase.setStatus(status);
        return phase;
    }
    
    public synchronized Progress addPhase(final float weightage) {
        final Progress phase = new Progress();
        this.progressWeightagesForPhases.add(weightage);
        this.phases.add(phase);
        phase.setParent(this);
        float sum = 0.0f;
        for (int i = 0; i < this.phases.size(); ++i) {
            sum += this.progressWeightagesForPhases.get(i);
        }
        if (sum > 1.0) {
            Progress.LOG.warn("Sum of weightages can not be more than 1.0; But sum = " + sum);
        }
        return phase;
    }
    
    public synchronized void addPhases(final int n) {
        for (int i = 0; i < n; ++i) {
            this.addNewPhase();
        }
        this.progressPerPhase = 1.0f / this.phases.size();
        this.fixedWeightageForAllPhases = true;
    }
    
    float getProgressWeightage(final int phaseNum) {
        if (this.fixedWeightageForAllPhases) {
            return this.progressPerPhase;
        }
        return this.progressWeightagesForPhases.get(phaseNum);
    }
    
    synchronized Progress getParent() {
        return this.parent;
    }
    
    synchronized void setParent(final Progress parent) {
        this.parent = parent;
    }
    
    public synchronized void startNextPhase() {
        ++this.currentPhase;
    }
    
    public synchronized Progress phase() {
        return this.phases.get(this.currentPhase);
    }
    
    public void complete() {
        final Progress myParent;
        synchronized (this) {
            this.progress = 1.0f;
            myParent = this.parent;
        }
        if (myParent != null) {
            myParent.startNextPhase();
        }
    }
    
    public synchronized void set(float progress) {
        if (Float.isNaN(progress)) {
            progress = 0.0f;
            Progress.LOG.debug("Illegal progress value found, progress is Float.NaN. Progress will be changed to 0");
        }
        else if (progress == Float.NEGATIVE_INFINITY) {
            progress = 0.0f;
            Progress.LOG.debug("Illegal progress value found, progress is Float.NEGATIVE_INFINITY. Progress will be changed to 0");
        }
        else if (progress < 0.0f) {
            progress = 0.0f;
            Progress.LOG.debug("Illegal progress value found, progress is less than 0. Progress will be changed to 0");
        }
        else if (progress > 1.0f) {
            progress = 1.0f;
            Progress.LOG.debug("Illegal progress value found, progress is larger than 1. Progress will be changed to 1");
        }
        else if (progress == Float.POSITIVE_INFINITY) {
            progress = 1.0f;
            Progress.LOG.debug("Illegal progress value found, progress is Float.POSITIVE_INFINITY. Progress will be changed to 1");
        }
        this.progress = progress;
    }
    
    public synchronized float get() {
        Progress node;
        for (node = this; node.getParent() != null; node = this.parent) {}
        return node.getInternal();
    }
    
    public synchronized float getProgress() {
        return this.getInternal();
    }
    
    private synchronized float getInternal() {
        final int phaseCount = this.phases.size();
        if (phaseCount != 0) {
            float subProgress = 0.0f;
            float progressFromCurrentPhase = 0.0f;
            if (this.currentPhase < phaseCount) {
                subProgress = this.phase().getInternal();
                progressFromCurrentPhase = this.getProgressWeightage(this.currentPhase) * subProgress;
            }
            float progressFromCompletedPhases = 0.0f;
            if (this.fixedWeightageForAllPhases) {
                progressFromCompletedPhases = this.progressPerPhase * this.currentPhase;
            }
            else {
                for (int i = 0; i < this.currentPhase; ++i) {
                    progressFromCompletedPhases += this.getProgressWeightage(i);
                }
            }
            return progressFromCompletedPhases + progressFromCurrentPhase;
        }
        return this.progress;
    }
    
    public synchronized void setStatus(final String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        this.toString(result);
        return result.toString();
    }
    
    private synchronized void toString(final StringBuilder buffer) {
        buffer.append(this.status);
        if (this.phases.size() != 0 && this.currentPhase < this.phases.size()) {
            buffer.append(" > ");
            this.phase().toString(buffer);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(Progress.class);
    }
}
