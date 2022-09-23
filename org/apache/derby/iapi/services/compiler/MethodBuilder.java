// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.compiler;

public interface MethodBuilder
{
    void addThrownException(final String p0);
    
    String getName();
    
    void complete();
    
    void getParameter(final int p0);
    
    void push(final byte p0);
    
    void push(final boolean p0);
    
    void push(final short p0);
    
    void push(final int p0);
    
    void push(final long p0);
    
    void push(final float p0);
    
    void push(final double p0);
    
    void push(final String p0);
    
    void pushNull(final String p0);
    
    void getField(final LocalField p0);
    
    void getField(final String p0, final String p1, final String p2);
    
    void getStaticField(final String p0, final String p1, final String p2);
    
    void setField(final LocalField p0);
    
    void putField(final LocalField p0);
    
    void putField(final String p0, final String p1);
    
    void putField(final String p0, final String p1, final String p2);
    
    void pushNewStart(final String p0);
    
    void pushNewComplete(final int p0);
    
    void pushNewArray(final String p0, final int p1);
    
    void pushThis();
    
    void upCast(final String p0);
    
    void cast(final String p0);
    
    void isInstanceOf(final String p0);
    
    void pop();
    
    void endStatement();
    
    void methodReturn();
    
    void conditionalIfNull();
    
    void conditionalIf();
    
    void startElseCode();
    
    void completeConditional();
    
    int callMethod(final short p0, final String p1, final String p2, final String p3, final int p4);
    
    Object describeMethod(final short p0, final String p1, final String p2, final String p3);
    
    int callMethod(final Object p0);
    
    void callSuper();
    
    void getArrayElement(final int p0);
    
    void setArrayElement(final int p0);
    
    void swap();
    
    void dup();
    
    boolean statementNumHitLimit(final int p0);
}
