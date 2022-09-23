// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rule;

import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;
import java.awt.Color;
import java.io.Serializable;

public class ColorRule extends AbstractRule implements Serializable
{
    static final long serialVersionUID = -794434783372847773L;
    private final Rule rule;
    private final Color foregroundColor;
    private final Color backgroundColor;
    private final String expression;
    
    public ColorRule(final String expression, final Rule rule, final Color backgroundColor, final Color foregroundColor) {
        this.expression = expression;
        this.rule = rule;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
    }
    
    public Rule getRule() {
        return this.rule;
    }
    
    public Color getForegroundColor() {
        return this.foregroundColor;
    }
    
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
    
    public String getExpression() {
        return this.expression;
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        return this.rule != null && this.rule.evaluate(event, null);
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer("color rule - expression: ");
        buf.append(this.expression);
        buf.append(", rule: ");
        buf.append(this.rule);
        buf.append(" bg: ");
        buf.append(this.backgroundColor);
        buf.append(" fg: ");
        buf.append(this.foregroundColor);
        return buf.toString();
    }
}
