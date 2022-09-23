// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import java.util.StringJoiner;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.metrics2.MetricsInfo;

@InterfaceAudience.Private
public enum MsInfo implements MetricsInfo
{
    NumActiveSources("Number of active metrics sources"), 
    NumAllSources("Number of all registered metrics sources"), 
    NumActiveSinks("Number of active metrics sinks"), 
    NumAllSinks("Number of all registered metrics sinks"), 
    Context("Metrics context"), 
    Hostname("Local hostname"), 
    SessionId("Session ID"), 
    ProcessName("Process name");
    
    private final String desc;
    
    private MsInfo(final String desc) {
        this.desc = desc;
    }
    
    @Override
    public String description() {
        return this.desc;
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "{", "}").add("name=" + this.name()).add("description=" + this.desc).toString();
    }
}
