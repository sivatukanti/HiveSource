// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ResourceOptionPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.NodeIdPBImpl;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.HashMap;
import org.apache.hadoop.yarn.api.records.ResourceOption;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceRequest;

public class UpdateNodeResourceRequestPBImpl extends UpdateNodeResourceRequest
{
    YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto proto;
    YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto.Builder builder;
    boolean viaProto;
    Map<NodeId, ResourceOption> nodeResourceMap;
    
    public UpdateNodeResourceRequestPBImpl() {
        this.proto = YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.nodeResourceMap = null;
        this.builder = YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto.newBuilder();
    }
    
    public UpdateNodeResourceRequestPBImpl(final YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto proto) {
        this.proto = YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.nodeResourceMap = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public Map<NodeId, ResourceOption> getNodeResourceMap() {
        this.initNodeResourceMap();
        return this.nodeResourceMap;
    }
    
    @Override
    public void setNodeResourceMap(final Map<NodeId, ResourceOption> nodeResourceMap) {
        if (nodeResourceMap == null) {
            return;
        }
        this.initNodeResourceMap();
        this.nodeResourceMap.clear();
        this.nodeResourceMap.putAll(nodeResourceMap);
    }
    
    public YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.nodeResourceMap != null) {
            this.addNodeResourceMap();
        }
    }
    
    private void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private void initNodeResourceMap() {
        if (this.nodeResourceMap != null) {
            return;
        }
        final YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.NodeResourceMapProto> list = p.getNodeResourceMapList();
        this.nodeResourceMap = new HashMap<NodeId, ResourceOption>(list.size());
        for (final YarnProtos.NodeResourceMapProto nodeResourceProto : list) {
            this.nodeResourceMap.put(this.convertFromProtoFormat(nodeResourceProto.getNodeId()), this.convertFromProtoFormat(nodeResourceProto.getResourceOption()));
        }
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServerResourceManagerServiceProtos.UpdateNodeResourceRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private YarnProtos.NodeIdProto convertToProtoFormat(final NodeId nodeId) {
        return ((NodeIdPBImpl)nodeId).getProto();
    }
    
    private NodeId convertFromProtoFormat(final YarnProtos.NodeIdProto proto) {
        return new NodeIdPBImpl(proto);
    }
    
    private ResourceOptionPBImpl convertFromProtoFormat(final YarnProtos.ResourceOptionProto c) {
        return new ResourceOptionPBImpl(c);
    }
    
    private YarnProtos.ResourceOptionProto convertToProtoFormat(final ResourceOption c) {
        return ((ResourceOptionPBImpl)c).getProto();
    }
    
    private void addNodeResourceMap() {
        this.maybeInitBuilder();
        this.builder.clearNodeResourceMap();
        if (this.nodeResourceMap == null) {
            return;
        }
        final Iterable<? extends YarnProtos.NodeResourceMapProto> values = new Iterable<YarnProtos.NodeResourceMapProto>() {
            @Override
            public Iterator<YarnProtos.NodeResourceMapProto> iterator() {
                return new Iterator<YarnProtos.NodeResourceMapProto>() {
                    Iterator<NodeId> nodeIterator = UpdateNodeResourceRequestPBImpl.this.nodeResourceMap.keySet().iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.nodeIterator.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.NodeResourceMapProto next() {
                        final NodeId nodeId = this.nodeIterator.next();
                        return YarnProtos.NodeResourceMapProto.newBuilder().setNodeId(UpdateNodeResourceRequestPBImpl.this.convertToProtoFormat(nodeId)).setResourceOption(UpdateNodeResourceRequestPBImpl.this.convertToProtoFormat(UpdateNodeResourceRequestPBImpl.this.nodeResourceMap.get(nodeId))).build();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllNodeResourceMap(values);
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((UpdateNodeResourceRequestPBImpl)this.getClass().cast(other)).getProto());
    }
}
