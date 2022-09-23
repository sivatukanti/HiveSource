// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.filter;

import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.rule.ExpressionRule;
import org.apache.log4j.rule.Rule;
import org.apache.log4j.spi.Filter;

public class ExpressionFilter extends Filter
{
    boolean acceptOnMatch;
    boolean convertInFixToPostFix;
    String expression;
    Rule expressionRule;
    
    public ExpressionFilter() {
        this.acceptOnMatch = true;
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
    
    public void setAcceptOnMatch(final boolean newValue) {
        this.acceptOnMatch = newValue;
    }
    
    public boolean getAcceptOnMatch() {
        return this.acceptOnMatch;
    }
    
    public int decide(final LoggingEvent event) {
        if (!this.expressionRule.evaluate(event, null)) {
            return 0;
        }
        if (this.acceptOnMatch) {
            return 1;
        }
        return -1;
    }
}
