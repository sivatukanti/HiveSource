// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Sets;
import org.apache.hadoop.yarn.api.records.impl.pb.NodeIdPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.HashMap;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import org.apache.hadoop.yarn.server.api.protocolrecords.ReplaceLabelsOnNodeRequest;

public class ReplaceLabelsOnNodeRequestPBImpl extends ReplaceLabelsOnNodeRequest
{
    YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto proto;
    YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto.Builder builder;
    boolean viaProto;
    private Map<NodeId, Set<String>> nodeIdToLabels;
    
    public ReplaceLabelsOnNodeRequestPBImpl() {
        this.proto = YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto.newBuilder();
    }
    
    public ReplaceLabelsOnNodeRequestPBImpl(final YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto proto) {
        this.proto = YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    private void initNodeToLabels() {
        if (this.nodeIdToLabels != null) {
            return;
        }
        final YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.NodeIdToLabelsProto> list = p.getNodeToLabelsList();
        this.nodeIdToLabels = new HashMap<NodeId, Set<String>>();
        for (final YarnProtos.NodeIdToLabelsProto c : list) {
            this.nodeIdToLabels.put(new NodeIdPBImpl(c.getNodeId()), (Set<String>)Sets.newHashSet((Iterable<?>)c.getNodeLabelsList()));
        }
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void addNodeToLabelsToProto() {
        this.maybeInitBuilder();
        this.builder.clearNodeToLabels();
        if (this.nodeIdToLabels == null) {
            return;
        }
        final Iterable<YarnProtos.NodeIdToLabelsProto> iterable = new Iterable<YarnProtos.NodeIdToLabelsProto>() {
            @Override
            public Iterator<YarnProtos.NodeIdToLabelsProto> iterator() {
                return new Iterator<YarnProtos.NodeIdToLabelsProto>() {
                    Iterator<Map.Entry<NodeId, Set<String>>> iter = ReplaceLabelsOnNodeRequestPBImpl.this.nodeIdToLabels.entrySet().iterator();
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public YarnProtos.NodeIdToLabelsProto next() {
                        final Map.Entry<NodeId, Set<String>> now = this.iter.next();
                        return YarnProtos.NodeIdToLabelsProto.newBuilder().setNodeId(ReplaceLabelsOnNodeRequestPBImpl.this.convertToProtoFormat(now.getKey())).clearNodeLabels().addAllNodeLabels(now.getValue()).build();
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
        if (this.nodeIdToLabels != null) {
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
    
    public YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    @Override
    public Map<NodeId, Set<String>> getNodeToLabels() {
        this.initNodeToLabels();
        return this.nodeIdToLabels;
    }
    
    @Override
    public void setNodeToLabels(final Map<NodeId, Set<String>> map) {
        this.initNodeToLabels();
        this.nodeIdToLabels.clear();
        this.nodeIdToLabels.putAll(map);
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ReplaceLabelsOnNodeRequestPBImpl)this.getClass().cast(other)).getProto());
    }
}
