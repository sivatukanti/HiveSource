// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.filter;

import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.extras.DOMConfigurator;
import java.util.Properties;
import org.w3c.dom.Element;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.apache.log4j.spi.Filter;

public class AndFilter extends Filter implements UnrecognizedElementHandler
{
    Filter headFilter;
    Filter tailFilter;
    boolean acceptOnMatch;
    
    public AndFilter() {
        this.headFilter = null;
        this.tailFilter = null;
        this.acceptOnMatch = true;
    }
    
    public void activateOptions() {
    }
    
    public void addFilter(final Filter filter) {
        if (this.headFilter == null) {
            this.headFilter = filter;
            this.tailFilter = filter;
        }
        else {
            this.tailFilter.next = filter;
        }
    }
    
    public void setAcceptOnMatch(final boolean acceptOnMatch) {
        this.acceptOnMatch = acceptOnMatch;
    }
    
    public int decide(final LoggingEvent event) {
        boolean accepted = true;
        for (Filter f = this.headFilter; f != null; f = f.next) {
            accepted = (accepted && 1 == f.decide(event));
        }
        if (!accepted) {
            return 0;
        }
        if (this.acceptOnMatch) {
            return 1;
        }
        return -1;
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
