// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.net;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class JMSReceiverBeanInfo extends SimpleBeanInfo
{
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            return new PropertyDescriptor[] { new PropertyDescriptor("name", JMSReceiver.class), new PropertyDescriptor("topicFactoryName", JMSReceiver.class), new PropertyDescriptor("topicName", JMSReceiver.class), new PropertyDescriptor("threshold", JMSReceiver.class), new PropertyDescriptor("jndiPath", JMSReceiver.class), new PropertyDescriptor("userId", JMSReceiver.class) };
        }
        catch (Exception e) {
            return null;
        }
    }
}
