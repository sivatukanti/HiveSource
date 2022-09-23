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
public abstract class AbstractMetric implements MetricsInfo
{
    private final MetricsInfo info;
    
    protected AbstractMetric(final MetricsInfo info) {
        this.info = Preconditions.checkNotNull(info, (Object)"metric info");
    }
    
    @Override
    public String name() {
        return this.info.name();
    }
    
    @Override
    public String description() {
        return this.info.description();
    }
    
    protected MetricsInfo info() {
        return this.info;
    }
    
    public abstract Number value();
    
    public abstract MetricType type();
    
    public abstract void visit(final MetricsVisitor p0);
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof AbstractMetric) {
            final AbstractMetric other = (AbstractMetric)obj;
            return Objects.equal(this.info, other.info()) && Objects.equal(this.value(), other.value());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.info, this.value());
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "{", "}").add("info=" + this.info).add("value=" + this.value()).toString();
    }
}
