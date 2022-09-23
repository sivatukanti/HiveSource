// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.util.Arrays;
import java.nio.charset.Charset;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HBase", "HDFS", "Hive", "MapReduce", "Pig", "Spark", "YARN" })
@InterfaceStability.Evolving
public final class CallerContext
{
    public static final Charset SIGNATURE_ENCODING;
    private final String context;
    private final byte[] signature;
    
    private CallerContext(final Builder builder) {
        this.context = builder.context;
        this.signature = builder.signature;
    }
    
    public String getContext() {
        return this.context;
    }
    
    public byte[] getSignature() {
        return (byte[])((this.signature == null) ? null : Arrays.copyOf(this.signature, this.signature.length));
    }
    
    @InterfaceAudience.Private
    public boolean isContextValid() {
        return this.context != null && !this.context.isEmpty();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.context).toHashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final CallerContext rhs = (CallerContext)obj;
        return new EqualsBuilder().append(this.context, rhs.context).append(this.signature, rhs.signature).isEquals();
    }
    
    @Override
    public String toString() {
        if (!this.isContextValid()) {
            return "";
        }
        String str = this.context;
        if (this.signature != null) {
            str += ":";
            str += new String(this.signature, CallerContext.SIGNATURE_ENCODING);
        }
        return str;
    }
    
    public static CallerContext getCurrent() {
        return CurrentCallerContextHolder.CALLER_CONTEXT.get();
    }
    
    public static void setCurrent(final CallerContext callerContext) {
        CurrentCallerContextHolder.CALLER_CONTEXT.set(callerContext);
    }
    
    static {
        SIGNATURE_ENCODING = StandardCharsets.UTF_8;
    }
    
    public static final class Builder
    {
        private final String context;
        private byte[] signature;
        
        public Builder(final String context) {
            this.context = context;
        }
        
        public Builder setSignature(final byte[] signature) {
            if (signature != null && signature.length > 0) {
                this.signature = Arrays.copyOf(signature, signature.length);
            }
            return this;
        }
        
        public CallerContext build() {
            return new CallerContext(this, null);
        }
    }
    
    private static final class CurrentCallerContextHolder
    {
        static final ThreadLocal<CallerContext> CALLER_CONTEXT;
        
        static {
            CALLER_CONTEXT = new InheritableThreadLocal<CallerContext>();
        }
    }
}
