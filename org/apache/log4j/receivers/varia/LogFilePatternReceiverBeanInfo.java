// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.varia;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class LogFilePatternReceiverBeanInfo extends SimpleBeanInfo
{
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            return new PropertyDescriptor[] { new PropertyDescriptor("fileURL", LogFilePatternReceiver.class), new PropertyDescriptor("timestampFormat", LogFilePatternReceiver.class), new PropertyDescriptor("logFormat", LogFilePatternReceiver.class), new PropertyDescriptor("name", LogFilePatternReceiver.class), new PropertyDescriptor("tailing", LogFilePatternReceiver.class), new PropertyDescriptor("filterExpression", LogFilePatternReceiver.class), new PropertyDescriptor("waitMillis", LogFilePatternReceiver.class), new PropertyDescriptor("appendNonMatches", LogFilePatternReceiver.class), new PropertyDescriptor("customLevelDefinitions", LogFilePatternReceiver.class), new PropertyDescriptor("useCurrentThread", LogFilePatternReceiver.class) };
        }
        catch (Exception e) {
            return null;
        }
    }
}
