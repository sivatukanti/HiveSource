// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Sets;
import org.apache.hadoop.yarn.api.records.impl.pb.NodeIdPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.HashMap;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.yarn.api.protocolrecords.GetNodesToLabelsResponse;

public class GetNodesToLabelsResponsePBImpl extends GetNodesToLabelsResponse
{
    YarnServiceProtos.GetNodesToLabelsResponseProto proto;
    YarnServiceProtos.GetNodesToLabelsResponseProto.Builder builder;
    boolean viaProto;
    private Map<NodeId, Set<String>> nodeToLabels;
    
    public GetNodesToLabelsResponsePBImpl() {
        this.proto = YarnServiceProtos.GetNodesToLabelsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.GetNodesToLabelsResponseProto.newBuilder();
    }
    
    public GetNodesToLabelsResponsePBImpl(final YarnServiceProtos.GetNodesToLabelsResponseProto proto) {
        this.proto = YarnServiceProtos.GetNodesToLabelsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    private void initNodeToLabels() {
        if (this.nodeToLabels != null) {
            return;
        }
        final YarnServiceProtos.GetNodesToLabelsResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.NodeIdToLabelsProto> list = p.getNodeToLabelsList();
        this.nodeToLabels = new HashMap<NodeId, Set<String>>();
        for (final YarnProtos.NodeIdToLabelsProto c : list) {
            this.nodeToLabels.put(new NodeIdPBImpl(c.getNodeId()), (Set<String>)Sets.newHashSet((Iterable<?>)c.getNodeLabelsList()));
        }
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.GetNodesToLabelsResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void addNodeToLabelsToProto() {
        this.maybeInitBuilder();
        this.builder.clearNodeToLabels();
        if (this.nodeToLabels == null) {
            return;
        }
        final Iterable<YarnProtos.NodeIdToLabelsProto> iterable = new Iterable<YarnProtos.NodeIdToLabelsProto>() {
            @Override
            public Iterator<YarnProtos.NodeIdToLabelsProto> iterator() {
                return new Iterator<YarnProtos.NodeIdToLabelsProto>() {
                    Iterator<Map.Entry<NodeId, Set<String>>> iter = GetNodesToLabelsResponsePBImpl.this.nodeToLabels.entrySet().iterator();
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public YarnProtos.NodeIdToLabelsProto next() {
                        final Map.Entry<NodeId, Set<String>> now = this.iter.next();
                        return YarnProtos.NodeIdToLabelsProto.newBuilder().setNodeId(GetNodesToLabelsResponsePBImpl.this.convertToProtoFormat(now.getKey())).addAllNodeLabels(now.getValue()).build();
                    }
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                };
            }
        };
        this.builder.addAllNodeToLabels(iterable);
    }
    
    private void mergeLocalToBuilder() {
        if (this.nodeToLabels != null) {
            this.addNodeToLabelsToProto();
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
    
    public YarnServiceProtos.GetNodesToLabelsResponseProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    @Override
    public Map<NodeId, Set<String>> getNodeToLabels() {
        this.initNodeToLabels();
        return this.nodeToLabels;
    }
    
    @Override
    public void setNodeToLabels(final Map<NodeId, Set<String>> map) {
        this.initNodeToLabels();
        this.nodeToLabels.clear();
        this.nodeToLabels.putAll(map);
    }
    
    private YarnProtos.NodeIdProto convertToProtoFormat(final NodeId t) {
        return ((NodeIdPBImpl)t).getProto();
    }
    
    @Override
    public int hashCode() {
        assert false : "hashCode not designed";
        return 0;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetNodesToLabelsResponsePBImpl)this.getClass().cast(other)).getProto());
    }
}
