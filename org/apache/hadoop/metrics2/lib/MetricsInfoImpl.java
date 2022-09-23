// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import java.util.StringJoiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.apache.hadoop.metrics2.MetricsInfo;

class MetricsInfoImpl implements MetricsInfo
{
    private final String name;
    private final String description;
    
    MetricsInfoImpl(final String name, final String description) {
        this.name = Preconditions.checkNotNull(name, (Object)"name");
        this.description = Preconditions.checkNotNull(description, (Object)"description");
    }
    
    @Override
    public String name() {
        return this.name;
    }
    
    @Override
    public String description() {
        return this.description;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof MetricsInfo) {
            final MetricsInfo other = (MetricsInfo)obj;
            return Objects.equal(this.name, other.name()) && Objects.equal(this.description, other.description());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.name, this.description);
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "{", "}").add("name=" + this.name).add("description=" + this.description).toString();
    }
}
