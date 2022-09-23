// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.filter;

import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.rule.ExpressionRule;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.Filter;

public class LocationInfoFilter extends Filter
{
    boolean convertInFixToPostFix;
    String expression;
    Rule expressionRule;
    
    public LocationInfoFilter() {
        this.convertInFixToPostFix = true;
    }
    
    public void activateOptions() {
        this.expressionRule = ExpressionRule.getRule(this.expression, !this.convertInFixToPostFix);
    }
    
    public void setExpression(final String exp) {
        this.expression = exp;
    }
    
    public String getExpression() {
        return this.expression;
    }
    
    public void setConvertInFixToPostFix(final boolean newValue) {
        this.convertInFixToPostFix = newValue;
    }
    
    public boolean getConvertInFixToPostFix() {
        return this.convertInFixToPostFix;
    }
    
    public int decide(final LoggingEvent event) {
        if (this.expressionRule.evaluate(event, null)) {
            event.getLocationInformation();
        }
        return 0;
    }
}
