// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling;

import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.extras.DOMConfigurator;
import java.util.Properties;
import org.w3c.dom.Element;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.apache.log4j.spi.TriggeringEventEvaluator;

public final class FilterBasedTriggeringPolicy implements TriggeringPolicy, TriggeringEventEvaluator, UnrecognizedElementHandler
{
    private Filter headFilter;
    private Filter tailFilter;
    
    public boolean isTriggeringEvent(final LoggingEvent event) {
        if (this.headFilter == null) {
            return false;
        }
        Filter f = this.headFilter;
        while (f != null) {
            switch (f.decide(event)) {
                case -1: {
                    return false;
                }
                case 1: {
                    return true;
                }
                default: {
                    f = f.next;
                    continue;
                }
            }
        }
        return true;
    }
    
    public boolean isTriggeringEvent(final Appender appender, final LoggingEvent event, final String file, final long fileLength) {
        return this.isTriggeringEvent(event);
    }
    
    public void addFilter(final Filter newFilter) {
        if (this.headFilter == null) {
            this.headFilter = newFilter;
            this.tailFilter = newFilter;
        }
        else {
            this.tailFilter.next = newFilter;
            this.tailFilter = newFilter;
        }
    }
    
    public void clearFilters() {
        this.headFilter = null;
        this.tailFilter = null;
    }
    
    public Filter getFilter() {
        return this.headFilter;
    }
    
    public void activateOptions() {
        for (Filter f = this.headFilter; f != null; f = f.next) {
            f.activateOptions();
        }
    }
    
    public boolean parseUnrecognizedElement(final Element element, final Properties props) throws Exception {
        final String nodeName = element.getNodeName();
        if ("filter".equals(nodeName)) {
            final OptionHandler filter = DOMConfigurator.parseElement(element, props, Filter.class);
            if (filter instanceof Filter) {
                filter.activateOptions();
                this.addFilter((Filter)filter);
            }
            return true;
        }
        return false;
    }
}
