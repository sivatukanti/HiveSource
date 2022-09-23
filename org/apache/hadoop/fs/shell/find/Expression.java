// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell.find;

import java.util.Deque;
import org.apache.hadoop.fs.shell.PathData;
import java.io.IOException;

public interface Expression
{
    void setOptions(final FindOptions p0) throws IOException;
    
    void prepare() throws IOException;
    
    Result apply(final PathData p0, final int p1) throws IOException;
    
    void finish() throws IOException;
    
    String[] getUsage();
    
    String[] getHelp();
    
    boolean isAction();
    
    boolean isOperator();
    
    int getPrecedence();
    
    void addChildren(final Deque<Expression> p0);
    
    void addArguments(final Deque<String> p0);
}
