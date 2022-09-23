// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.interpol;

import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import org.apache.commons.lang3.text.StrSubstitutor;

public class ExprLookup implements Lookup
{
    private static final String CLASS = "Class:";
    private static final String DEFAULT_PREFIX = "$[";
    private static final String DEFAULT_SUFFIX = "]";
    private ConfigurationInterpolator interpolator;
    private StrSubstitutor substitutor;
    private ConfigurationLogger logger;
    private final JexlEngine engine;
    private Variables variables;
    private String prefixMatcher;
    private String suffixMatcher;
    
    public ExprLookup() {
        this.engine = new JexlEngine();
        this.prefixMatcher = "$[";
        this.suffixMatcher = "]";
    }
    
    public ExprLookup(final Variables list) {
        this.engine = new JexlEngine();
        this.prefixMatcher = "$[";
        this.suffixMatcher = "]";
        this.setVariables(list);
    }
    
    public ExprLookup(final Variables list, final String prefix, final String suffix) {
        this(list);
        this.setVariablePrefixMatcher(prefix);
        this.setVariableSuffixMatcher(suffix);
    }
    
    public void setVariablePrefixMatcher(final String prefix) {
        this.prefixMatcher = prefix;
    }
    
    public void setVariableSuffixMatcher(final String suffix) {
        this.suffixMatcher = suffix;
    }
    
    public void setVariables(final Variables list) {
        this.variables = new Variables(list);
    }
    
    public Variables getVariables() {
        return null;
    }
    
    public ConfigurationLogger getLogger() {
        return this.logger;
    }
    
    public void setLogger(final ConfigurationLogger logger) {
        this.logger = logger;
    }
    
    public ConfigurationInterpolator getInterpolator() {
        return this.interpolator;
    }
    
    public void setInterpolator(final ConfigurationInterpolator interpolator) {
        this.installSubstitutor(this.interpolator = interpolator);
    }
    
    @Override
    public String lookup(final String var) {
        if (this.substitutor == null) {
            return var;
        }
        String result = this.substitutor.replace(var);
        try {
            final Expression exp = this.engine.createExpression(result);
            result = (String)exp.evaluate(this.createContext());
        }
        catch (Exception e) {
            final ConfigurationLogger l = this.getLogger();
            if (l != null) {
                l.debug("Error encountered evaluating " + result + ": " + e);
            }
        }
        return result;
    }
    
    private void installSubstitutor(final ConfigurationInterpolator ip) {
        if (ip == null) {
            this.substitutor = null;
        }
        else {
            final StrLookup<String> variableResolver = new StrLookup<String>() {
                @Override
                public String lookup(final String key) {
                    final Object value = ip.resolve(key);
                    return (value != null) ? value.toString() : null;
                }
            };
            this.substitutor = new StrSubstitutor(variableResolver, this.prefixMatcher, this.suffixMatcher, '$');
        }
    }
    
    private JexlContext createContext() {
        final JexlContext ctx = (JexlContext)new MapContext();
        this.initializeContext(ctx);
        return ctx;
    }
    
    private void initializeContext(final JexlContext ctx) {
        for (final Variable var : this.variables) {
            ctx.set(var.getName(), var.getValue());
        }
    }
    
    public static class Variables extends ArrayList<Variable>
    {
        private static final long serialVersionUID = 20111205L;
        
        public Variables() {
        }
        
        public Variables(final Variables vars) {
            super(vars);
        }
        
        public Variable getVariable() {
            if (this.size() > 0) {
                return this.get(this.size() - 1);
            }
            return null;
        }
    }
    
    public static class Variable
    {
        private String key;
        private Object value;
        
        public Variable() {
        }
        
        public Variable(final String name, final Object value) {
            this.setName(name);
            this.setValue(value);
        }
        
        public String getName() {
            return this.key;
        }
        
        public void setName(final String name) {
            this.key = name;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public void setValue(final Object value) throws ConfigurationRuntimeException {
            try {
                if (!(value instanceof String)) {
                    this.value = value;
                    return;
                }
                final String val = (String)value;
                final String name = StringUtils.removeStartIgnoreCase(val, "Class:");
                final Class<?> clazz = ClassUtils.getClass(name);
                if (name.length() == val.length()) {
                    this.value = clazz.newInstance();
                }
                else {
                    this.value = clazz;
                }
            }
            catch (Exception e) {
                throw new ConfigurationRuntimeException("Unable to create " + value, e);
            }
        }
    }
}
