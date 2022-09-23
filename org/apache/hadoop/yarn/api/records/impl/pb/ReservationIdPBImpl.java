// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.common.base.Preconditions;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.ReservationId;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ReservationIdPBImpl extends ReservationId
{
    YarnProtos.ReservationIdProto proto;
    YarnProtos.ReservationIdProto.Builder builder;
    
    public ReservationIdPBImpl() {
        this.proto = null;
        this.builder = null;
        this.builder = YarnProtos.ReservationIdProto.newBuilder();
    }
    
    public ReservationIdPBImpl(final YarnProtos.ReservationIdProto proto) {
        this.proto = null;
        this.builder = null;
        this.proto = proto;
    }
    
    public YarnProtos.ReservationIdProto getProto() {
        return this.proto;
    }
    
    @Override
    public long getId() {
        Preconditions.checkNotNull(this.proto);
        return this.proto.getId();
    }
    
    @Override
    protected void setId(final long id) {
        Preconditions.checkNotNull(this.builder);
        this.builder.setId(id);
    }
    
    @Override
    public long getClusterTimestamp() {
        Preconditions.checkNotNull(this.proto);
        return this.proto.getClusterTimestamp();
    }
    
    @Override
    protected void setClusterTimestamp(final long clusterTimestamp) {
        Preconditions.checkNotNull(this.builder);
        this.builder.setClusterTimestamp(clusterTimestamp);
    }
    
    @Override
    protected void build() {
        this.proto = this.builder.build();
        this.builder = null;
    }
}
