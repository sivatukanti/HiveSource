// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.common.base.Preconditions;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.ApplicationId;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ApplicationIdPBImpl extends ApplicationId
{
    YarnProtos.ApplicationIdProto proto;
    YarnProtos.ApplicationIdProto.Builder builder;
    
    public ApplicationIdPBImpl() {
        this.proto = null;
        this.builder = null;
        this.builder = YarnProtos.ApplicationIdProto.newBuilder();
    }
    
    public ApplicationIdPBImpl(final YarnProtos.ApplicationIdProto proto) {
        this.proto = null;
        this.builder = null;
        this.proto = proto;
    }
    
    public YarnProtos.ApplicationIdProto getProto() {
        return this.proto;
    }
    
    @Override
    public int getId() {
        Preconditions.checkNotNull(this.proto);
        return this.proto.getId();
    }
    
    @Override
    protected void setId(final int id) {
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
