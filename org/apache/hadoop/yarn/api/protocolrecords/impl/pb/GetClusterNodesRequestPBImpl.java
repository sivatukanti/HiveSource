// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import java.util.List;
import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import java.util.Iterator;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.Collection;
import org.apache.hadoop.yarn.api.records.NodeState;
import java.util.EnumSet;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetClusterNodesRequestPBImpl extends GetClusterNodesRequest
{
    YarnServiceProtos.GetClusterNodesRequestProto proto;
    YarnServiceProtos.GetClusterNodesRequestProto.Builder builder;
    boolean viaProto;
    private EnumSet<NodeState> states;
    
    public GetClusterNodesRequestPBImpl() {
        this.proto = YarnServiceProtos.GetClusterNodesRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.states = null;
        this.builder = YarnServiceProtos.GetClusterNodesRequestProto.newBuilder();
    }
    
    public GetClusterNodesRequestPBImpl(final YarnServiceProtos.GetClusterNodesRequestProto proto) {
        this.proto = YarnServiceProtos.GetClusterNodesRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.states = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetClusterNodesRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    @Override
    public EnumSet<NodeState> getNodeStates() {
        this.initNodeStates();
        return this.states;
    }
    
    @Override
    public void setNodeStates(final EnumSet<NodeState> states) {
        this.initNodeStates();
        this.states.clear();
        if (states == null) {
            return;
        }
        this.states.addAll((Collection<?>)states);
    }
    
    private void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.GetClusterNodesRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void mergeLocalToBuilder() {
        if (this.states != null) {
            this.maybeInitBuilder();
            this.builder.clearNodeStates();
            final Iterable<YarnProtos.NodeStateProto> iterable = new Iterable<YarnProtos.NodeStateProto>() {
                @Override
                public Iterator<YarnProtos.NodeStateProto> iterator() {
                    return new Iterator<YarnProtos.NodeStateProto>() {
                        Iterator<NodeState> iter = GetClusterNodesRequestPBImpl.this.states.iterator();
                        
                        @Override
                        public boolean hasNext() {
                            return this.iter.hasNext();
                        }
                        
                        @Override
                        public YarnProtos.NodeStateProto next() {
                            return ProtoUtils.convertToProtoFormat(this.iter.next());
                        }
                        
                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };
            this.builder.addAllNodeStates(iterable);
        }
    }
    
    private void initNodeStates() {
        if (this.states != null) {
            return;
        }
        final YarnServiceProtos.GetClusterNodesRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.NodeStateProto> list = p.getNodeStatesList();
        this.states = EnumSet.noneOf(NodeState.class);
        for (final YarnProtos.NodeStateProto c : list) {
            this.states.add(ProtoUtils.convertFromProtoFormat(c));
        }
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetClusterNodesRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
