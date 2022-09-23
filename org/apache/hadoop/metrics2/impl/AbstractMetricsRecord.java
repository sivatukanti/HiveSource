// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import java.util.StringJoiner;
import com.google.common.collect.Iterables;
import com.google.common.base.Objects;
import org.apache.hadoop.metrics2.MetricsRecord;

abstract class AbstractMetricsRecord implements MetricsRecord
{
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof MetricsRecord) {
            final MetricsRecord other = (MetricsRecord)obj;
            return Objects.equal(this.timestamp(), other.timestamp()) && Objects.equal(this.name(), other.name()) && Objects.equal(this.description(), other.description()) && Objects.equal(this.tags(), other.tags()) && Iterables.elementsEqual(this.metrics(), other.metrics());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.name(), this.description(), this.tags());
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "{", "}").add("timestamp=" + this.timestamp()).add("name=" + this.name()).add("description=" + this.description()).add("tags=" + this.tags()).add("metrics=" + Iterables.toString(this.metrics())).toString();
    }
}
