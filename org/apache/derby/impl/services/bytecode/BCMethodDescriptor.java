// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.bytecode;

class BCMethodDescriptor
{
    static final String[] EMPTY;
    private final String[] vmParameterTypes;
    private final String vmReturnType;
    private final String vmDescriptor;
    
    BCMethodDescriptor(final String[] vmParameterTypes, final String vmReturnType, final BCJava bcJava) {
        this.vmParameterTypes = vmParameterTypes;
        this.vmReturnType = vmReturnType;
        this.vmDescriptor = bcJava.vmType(this);
    }
    
    static String get(final String[] array, final String s, final BCJava bcJava) {
        return new BCMethodDescriptor(array, s, bcJava).toString();
    }
    
    String buildMethodDescriptor() {
        final int length = this.vmParameterTypes.length;
        final StringBuffer sb = new StringBuffer(30 * (length + 1));
        sb.append('(');
        for (int i = 0; i < length; ++i) {
            sb.append(this.vmParameterTypes[i]);
        }
        sb.append(')');
        sb.append(this.vmReturnType);
        return sb.toString();
    }
    
    public String toString() {
        return this.vmDescriptor;
    }
    
    public int hashCode() {
        return this.vmParameterTypes.length | (this.vmReturnType.hashCode() & 0xFFFFFF00);
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof BCMethodDescriptor)) {
            return false;
        }
        final BCMethodDescriptor bcMethodDescriptor = (BCMethodDescriptor)o;
        if (bcMethodDescriptor.vmParameterTypes.length != this.vmParameterTypes.length) {
            return false;
        }
        for (int i = 0; i < this.vmParameterTypes.length; ++i) {
            if (!this.vmParameterTypes[i].equals(bcMethodDescriptor.vmParameterTypes[i])) {
                return false;
            }
        }
        return this.vmReturnType.equals(bcMethodDescriptor.vmReturnType);
    }
    
    static {
        EMPTY = new String[0];
    }
}
