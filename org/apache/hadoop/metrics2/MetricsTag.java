// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2;

import java.util.StringJoiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MetricsTag implements MetricsInfo
{
    private final MetricsInfo info;
    private final String value;
    
    public MetricsTag(final MetricsInfo info, final String value) {
        this.info = Preconditions.checkNotNull(info, (Object)"tag info");
        this.value = value;
    }
    
    @Override
    public String name() {
        return this.info.name();
    }
    
    @Override
    public String description() {
        return this.info.description();
    }
    
    public MetricsInfo info() {
        return this.info;
    }
    
    public String value() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof MetricsTag) {
            final MetricsTag other = (MetricsTag)obj;
            return Objects.equal(this.info, other.info()) && Objects.equal(this.value, other.value());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.info, this.value);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "{", "}").add("info=" + this.info).add("value=" + this.value()).toString();
    }
}
