// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift;

public class ShortStack
{
    private short[] vector;
    private int top;
    
    public ShortStack(final int initialCapacity) {
        this.top = -1;
        this.vector = new short[initialCapacity];
    }
    
    public short pop() {
        return this.vector[this.top--];
    }
    
    public void push(final short pushed) {
        if (this.vector.length == this.top + 1) {
            this.grow();
        }
        this.vector[++this.top] = pushed;
    }
    
    private void grow() {
        final short[] newVector = new short[this.vector.length * 2];
        System.arraycopy(this.vector, 0, newVector, 0, this.vector.length);
        this.vector = newVector;
    }
    
    public short peek() {
        return this.vector[this.top];
    }
    
    public void clear() {
        this.top = -1;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<ShortStack vector:[");
        for (int i = 0; i < this.vector.length; ++i) {
            if (i != 0) {
                sb.append(" ");
            }
            if (i == this.top) {
                sb.append(">>");
            }
            sb.append(this.vector[i]);
            if (i == this.top) {
                sb.append("<<");
            }
        }
        sb.append("]>");
        return sb.toString();
    }
}
