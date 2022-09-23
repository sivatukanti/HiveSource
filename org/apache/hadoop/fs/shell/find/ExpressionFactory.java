// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell.find;

import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import java.lang.reflect.Method;
import org.apache.hadoop.util.StringUtils;
import java.util.HashMap;
import java.util.Map;

final class ExpressionFactory
{
    private static final String REGISTER_EXPRESSION_METHOD = "registerExpression";
    private Map<String, Class<? extends Expression>> expressionMap;
    private static final ExpressionFactory INSTANCE;
    
    static ExpressionFactory getExpressionFactory() {
        return ExpressionFactory.INSTANCE;
    }
    
    private ExpressionFactory() {
        this.expressionMap = new HashMap<String, Class<? extends Expression>>();
    }
    
    void registerExpression(final Class<? extends Expression> expressionClass) {
        try {
            final Method register = expressionClass.getMethod("registerExpression", ExpressionFactory.class);
            if (register != null) {
                register.invoke(null, this);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(StringUtils.stringifyException(e));
        }
    }
    
    void addClass(final Class<? extends Expression> expressionClass, final String... names) throws IOException {
        for (final String name : names) {
            this.expressionMap.put(name, expressionClass);
        }
    }
    
    boolean isExpression(final String expressionName) {
        return this.expressionMap.containsKey(expressionName);
    }
    
    Expression getExpression(final String expressionName, final Configuration conf) {
        if (conf == null) {
            throw new NullPointerException("configuration is null");
        }
        final Class<? extends Expression> expressionClass = this.expressionMap.get(expressionName);
        final Expression instance = this.createExpression(expressionClass, conf);
        return instance;
    }
    
    Expression createExpression(final Class<? extends Expression> expressionClass, final Configuration conf) {
        Expression instance = null;
        if (expressionClass != null) {
            instance = ReflectionUtils.newInstance(expressionClass, conf);
        }
        return instance;
    }
    
    Expression createExpression(final String expressionClassname, final Configuration conf) {
        try {
            final Class<? extends Expression> expressionClass = Class.forName(expressionClassname).asSubclass(Expression.class);
            return this.createExpression(expressionClass, conf);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Invalid classname " + expressionClassname);
        }
    }
    
    static {
        INSTANCE = new ExpressionFactory();
    }
}
