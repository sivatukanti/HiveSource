// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling;

import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.extras.DOMConfigurator;
import java.util.Properties;
import org.w3c.dom.Element;
import java.util.Iterator;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Appender;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.xml.UnrecognizedElementHandler;

public final class CompositeTriggeringPolicy implements TriggeringPolicy, UnrecognizedElementHandler
{
    Set triggeringPolicies;
    
    public CompositeTriggeringPolicy() {
        this.triggeringPolicies = new HashSet();
    }
    
    public boolean isTriggeringEvent(final Appender appender, final LoggingEvent event, final String file, final long fileLength) {
        boolean isTriggered = false;
        final Iterator iter = this.triggeringPolicies.iterator();
        while (iter.hasNext()) {
            final boolean result = iter.next().isTriggeringEvent(appender, event, file, fileLength);
            isTriggered = (isTriggered || result);
        }
        return isTriggered;
    }
    
    public void addTriggeringPolicy(final TriggeringPolicy policy) {
        this.triggeringPolicies.add(policy);
    }
    
    public void activateOptions() {
        final Iterator iter = this.triggeringPolicies.iterator();
        while (iter.hasNext()) {
            iter.next().activateOptions();
        }
    }
    
    public boolean parseUnrecognizedElement(final Element element, final Properties props) throws Exception {
        final String nodeName = element.getNodeName();
        if ("triggeringPolicy".equals(nodeName)) {
            final OptionHandler policy = DOMConfigurator.parseElement(element, props, TriggeringPolicy.class);
            if (policy instanceof TriggeringPolicy) {
                policy.activateOptions();
                this.addTriggeringPolicy((TriggeringPolicy)policy);
            }
            return true;
        }
        return false;
    }
}
