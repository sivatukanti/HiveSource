// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.filter;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.Filter;

public class StringMatchFilter extends Filter
{
    boolean acceptOnMatch;
    String stringToMatch;
    
    public StringMatchFilter() {
        this.acceptOnMatch = true;
    }
    
    public void setStringToMatch(final String s) {
        this.stringToMatch = s;
    }
    
    public String getStringToMatch() {
        return this.stringToMatch;
    }
    
    public void setAcceptOnMatch(final boolean acceptOnMatch) {
        this.acceptOnMatch = acceptOnMatch;
    }
    
    public boolean getAcceptOnMatch() {
        return this.acceptOnMatch;
    }
    
    public int decide(final LoggingEvent event) {
        final String msg = event.getRenderedMessage();
        if (msg == null || this.stringToMatch == null) {
            return 0;
        }
        if (msg.indexOf(this.stringToMatch) == -1) {
            return 0;
        }
        if (this.acceptOnMatch) {
            return 1;
        }
        return -1;
    }
}
