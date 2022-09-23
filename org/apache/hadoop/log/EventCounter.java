// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.log;

@Deprecated
public class EventCounter extends org.apache.hadoop.log.metrics.EventCounter
{
    static {
        System.err.println("WARNING: " + EventCounter.class.getName() + " is deprecated. Please use " + org.apache.hadoop.log.metrics.EventCounter.class.getName() + " in all the log4j.properties files.");
    }
}
