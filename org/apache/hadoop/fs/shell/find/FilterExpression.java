// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell.find;

import org.apache.hadoop.conf.Configuration;
import java.util.Deque;
import org.apache.hadoop.fs.shell.PathData;
import java.io.IOException;
import org.apache.hadoop.conf.Configurable;

public abstract class FilterExpression implements Expression, Configurable
{
    protected Expression expression;
    
    protected FilterExpression(final Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public void setOptions(final FindOptions options) throws IOException {
        if (this.expression != null) {
            this.expression.setOptions(options);
        }
    }
    
    @Override
    public void prepare() throws IOException {
        if (this.expression != null) {
            this.expression.prepare();
        }
    }
    
    @Override
    public Result apply(final PathData item, final int depth) throws IOException {
        if (this.expression != null) {
            return this.expression.apply(item, -1);
        }
        return Result.PASS;
    }
    
    @Override
    public void finish() throws IOException {
        if (this.expression != null) {
            this.expression.finish();
        }
    }
    
    @Override
    public String[] getUsage() {
        if (this.expression != null) {
            return this.expression.getUsage();
        }
        return null;
    }
    
    @Override
    public String[] getHelp() {
        if (this.expression != null) {
            return this.expression.getHelp();
        }
        return null;
    }
    
    @Override
    public boolean isAction() {
        return this.expression != null && this.expression.isAction();
    }
    
    @Override
    public boolean isOperator() {
        return this.expression != null && this.expression.isOperator();
    }
    
    @Override
    public int getPrecedence() {
        if (this.expression != null) {
            return this.expression.getPrecedence();
        }
        return -1;
    }
    
    @Override
    public void addChildren(final Deque<Expression> expressions) {
        if (this.expression != null) {
            this.expression.addChildren(expressions);
        }
    }
    
    @Override
    public void addArguments(final Deque<String> args) {
        if (this.expression != null) {
            this.expression.addArguments(args);
        }
    }
    
    @Override
    public void setConf(final Configuration conf) {
        if (this.expression instanceof Configurable) {
            ((Configurable)this.expression).setConf(conf);
        }
    }
    
    @Override
    public Configuration getConf() {
        if (this.expression instanceof Configurable) {
            return ((Configurable)this.expression).getConf();
        }
        return null;
    }
    
    @Override
    public String toString() {
        if (this.expression != null) {
            return this.getClass().getSimpleName() + "-" + this.expression.toString();
        }
        return this.getClass().getSimpleName();
    }
}
