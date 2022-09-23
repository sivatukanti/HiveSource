// 
// Decompiled by Procyon v0.5.36
// 

package org.fusesource.hawtjni.runtime;

public class Callback
{
    Object object;
    String method;
    String signature;
    int argCount;
    long address;
    long errorResult;
    boolean isStatic;
    boolean isArrayBased;
    static final String PTR_SIGNATURE = "J";
    static final String SIGNATURE_0;
    static final String SIGNATURE_1;
    static final String SIGNATURE_2;
    static final String SIGNATURE_3;
    static final String SIGNATURE_4;
    static final String SIGNATURE_N = "([J)J";
    
    public Callback(final Object object, final String method, final int argCount) {
        this(object, method, argCount, false);
    }
    
    public Callback(final Object object, final String method, final int argCount, final boolean isArrayBased) {
        this(object, method, argCount, isArrayBased, 0L);
    }
    
    public Callback(final Object object, final String method, final int argCount, final boolean isArrayBased, final long errorResult) {
        this.object = object;
        this.method = method;
        this.argCount = argCount;
        this.isStatic = (object instanceof Class);
        this.isArrayBased = isArrayBased;
        this.errorResult = errorResult;
        if (isArrayBased) {
            this.signature = "([J)J";
        }
        else {
            switch (argCount) {
                case 0: {
                    this.signature = Callback.SIGNATURE_0;
                    break;
                }
                case 1: {
                    this.signature = Callback.SIGNATURE_1;
                    break;
                }
                case 2: {
                    this.signature = Callback.SIGNATURE_2;
                    break;
                }
                case 3: {
                    this.signature = Callback.SIGNATURE_3;
                    break;
                }
                case 4: {
                    this.signature = Callback.SIGNATURE_4;
                    break;
                }
                default: {
                    this.signature = getSignature(argCount);
                    break;
                }
            }
        }
        this.address = bind(this, object, method, this.signature, argCount, this.isStatic, isArrayBased, errorResult);
    }
    
    static synchronized native long bind(final Callback p0, final Object p1, final String p2, final String p3, final int p4, final boolean p5, final boolean p6, final long p7);
    
    public void dispose() {
        if (this.object == null) {
            return;
        }
        unbind(this);
        final String object = null;
        this.signature = object;
        this.method = object;
        this.object = object;
        this.address = 0L;
    }
    
    public long getAddress() {
        return this.address;
    }
    
    public static native String getPlatform();
    
    public static native int getEntryCount();
    
    static String getSignature(final int argCount) {
        String signature = "(";
        for (int i = 0; i < argCount; ++i) {
            signature += "J";
        }
        signature += ")J";
        return signature;
    }
    
    public static final synchronized native void setEnabled(final boolean p0);
    
    public static final synchronized native boolean getEnabled();
    
    public static final synchronized native void reset();
    
    static final synchronized native void unbind(final Callback p0);
    
    static {
        SIGNATURE_0 = getSignature(0);
        SIGNATURE_1 = getSignature(1);
        SIGNATURE_2 = getSignature(2);
        SIGNATURE_3 = getSignature(3);
        SIGNATURE_4 = getSignature(4);
    }
}
