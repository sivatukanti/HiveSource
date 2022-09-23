// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rule;

import java.util.Stack;
import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;

public class ExpressionRule extends AbstractRule
{
    static final long serialVersionUID = 5809121703146893729L;
    private static final InFixToPostFix CONVERTER;
    private static final PostFixExpressionCompiler COMPILER;
    private final Rule rule;
    
    private ExpressionRule(final Rule r) {
        this.rule = r;
    }
    
    public static Rule getRule(final String expression) {
        return getRule(expression, false);
    }
    
    public static Rule getRule(final String expression, final boolean isPostFix) {
        String postFix = expression;
        if (!isPostFix) {
            postFix = ExpressionRule.CONVERTER.convert(expression);
        }
        return new ExpressionRule(ExpressionRule.COMPILER.compileExpression(postFix));
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        return this.rule.evaluate(event, matches);
    }
    
    public String toString() {
        return this.rule.toString();
    }
    
    static {
        CONVERTER = new InFixToPostFix();
        COMPILER = new PostFixExpressionCompiler();
    }
    
    static final class PostFixExpressionCompiler
    {
        public Rule compileExpression(final String expression) {
            final RuleFactory factory = RuleFactory.getInstance();
            final Stack stack = new Stack();
            final InFixToPostFix.CustomTokenizer tokenizer = new InFixToPostFix.CustomTokenizer(expression);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.startsWith("'") || token.startsWith("\"")) {
                    String quoteChar;
                    for (quoteChar = token.substring(0, 1), token = token.substring(1); !token.endsWith(quoteChar) && tokenizer.hasMoreTokens(); token = token + " " + tokenizer.nextToken()) {}
                    if (token.length() > 0) {
                        token = token.substring(0, token.length() - 1);
                    }
                }
                else if (factory.isRule(token)) {
                    final Rule r = factory.getRule(token, stack);
                    stack.push(r);
                    token = null;
                }
                if (token != null && token.length() > 0) {
                    stack.push(token);
                }
            }
            if (stack.size() == 1 && !(stack.peek() instanceof Rule)) {
                final Object o = stack.pop();
                stack.push("MSG");
                stack.push(o);
                return factory.getRule("~=", stack);
            }
            if (stack.size() != 1 || !(stack.peek() instanceof Rule)) {
                throw new IllegalArgumentException("invalid expression: " + expression);
            }
            return stack.pop();
        }
    }
}
