// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.net;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class MulticastReceiverBeanInfo extends SimpleBeanInfo
{
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            return new PropertyDescriptor[] { new PropertyDescriptor("name", MulticastReceiver.class), new PropertyDescriptor("address", MulticastReceiver.class), new PropertyDescriptor("port", MulticastReceiver.class), new PropertyDescriptor("threshold", MulticastReceiver.class), new PropertyDescriptor("decoder", MulticastReceiver.class), new PropertyDescriptor("advertiseViaMulticastDNS", MulticastReceiver.class) };
        }
        catch (Exception e) {
            return null;
        }
    }
}
