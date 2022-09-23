// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rule;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;
import java.util.regex.PatternSyntaxException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.spi.LoggingEventFieldResolver;

public class LikeRule extends AbstractRule
{
    static final long serialVersionUID = -3375458885595683156L;
    private static final LoggingEventFieldResolver RESOLVER;
    private transient Pattern pattern;
    private transient Matcher matcher;
    private transient String field;
    
    private LikeRule(final String field, final Pattern pattern) {
        this.matcher = null;
        if (!LikeRule.RESOLVER.isField(field)) {
            throw new IllegalArgumentException("Invalid LIKE rule - " + field + " is not a supported field");
        }
        this.field = field;
        this.pattern = pattern;
    }
    
    public static Rule getRule(final Stack stack) {
        if (stack.size() < 2) {
            throw new IllegalArgumentException("Invalid LIKE rule - expected two parameters but received " + stack.size());
        }
        final String p2 = stack.pop().toString();
        final String p3 = stack.pop().toString();
        return getRule(p3, p2);
    }
    
    public static Rule getRule(final String field, final String pattern) {
        try {
            return new LikeRule(field, Pattern.compile(pattern, 2));
        }
        catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid LIKE rule - " + e.getMessage());
        }
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        final Object input = LikeRule.RESOLVER.getValue(this.field, event);
        if (input != null && this.pattern != null) {
            if (this.matcher == null) {
                this.matcher = this.pattern.matcher(input.toString());
            }
            else {
                this.matcher.reset(input.toString());
            }
            final boolean result = this.matcher.matches();
            if (result && matches != null) {
                Set entries = matches.get(this.field.toUpperCase());
                if (entries == null) {
                    entries = new HashSet();
                    matches.put(this.field.toUpperCase(), entries);
                }
                entries.add(input);
            }
            return result;
        }
        return false;
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            this.field = (String)in.readObject();
            final String patternString = (String)in.readObject();
            this.pattern = Pattern.compile(patternString, 2);
        }
        catch (PatternSyntaxException e) {
            throw new IOException("Invalid LIKE rule - " + e.getMessage());
        }
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(this.field);
        out.writeObject(this.pattern.pattern());
    }
    
    static {
        RESOLVER = LoggingEventFieldResolver.getInstance();
    }
}
