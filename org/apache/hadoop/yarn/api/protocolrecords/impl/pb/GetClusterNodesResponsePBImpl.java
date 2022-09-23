// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.NodeReportPBImpl;
import java.util.Iterator;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.ArrayList;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.NodeReport;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetClusterNodesResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetClusterNodesResponsePBImpl extends GetClusterNodesResponse
{
    YarnServiceProtos.GetClusterNodesResponseProto proto;
    YarnServiceProtos.GetClusterNodesResponseProto.Builder builder;
    boolean viaProto;
    List<NodeReport> nodeManagerInfoList;
    
    public GetClusterNodesResponsePBImpl() {
        this.proto = YarnServiceProtos.GetClusterNodesResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.GetClusterNodesResponseProto.newBuilder();
    }
    
    public GetClusterNodesResponsePBImpl(final YarnServiceProtos.GetClusterNodesResponseProto proto) {
        this.proto = YarnServiceProtos.GetClusterNodesResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public List<NodeReport> getNodeReports() {
        this.initLocalNodeManagerInfosList();
        return this.nodeManagerInfoList;
    }
    
    @Override
    public void setNodeReports(final List<NodeReport> nodeManagers) {
        if (nodeManagers == null) {
            this.builder.clearNodeReports();
        }
        this.nodeManagerInfoList = nodeManagers;
    }
    
    public YarnServiceProtos.GetClusterNodesResponseProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetClusterNodesResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.nodeManagerInfoList != null) {
            this.addLocalNodeManagerInfosToProto();
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
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.GetClusterNodesResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void initLocalNodeManagerInfosList() {
        if (this.nodeManagerInfoList != null) {
            return;
        }
        final YarnServiceProtos.GetClusterNodesResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.NodeReportProto> list = p.getNodeReportsList();
        this.nodeManagerInfoList = new ArrayList<NodeReport>();
        for (final YarnProtos.NodeReportProto a : list) {
            this.nodeManagerInfoList.add(this.convertFromProtoFormat(a));
        }
    }
    
    private void addLocalNodeManagerInfosToProto() {
        this.maybeInitBuilder();
        this.builder.clearNodeReports();
        if (this.nodeManagerInfoList == null) {
            return;
        }
        final Iterable<YarnProtos.NodeReportProto> iterable = new Iterable<YarnProtos.NodeReportProto>() {
            @Override
            public Iterator<YarnProtos.NodeReportProto> iterator() {
                return new Iterator<YarnProtos.NodeReportProto>() {
                    Iterator<NodeReport> iter = GetClusterNodesResponsePBImpl.this.nodeManagerInfoList.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.NodeReportProto next() {
                        return GetClusterNodesResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllNodeReports(iterable);
    }
    
    private NodeReportPBImpl convertFromProtoFormat(final YarnProtos.NodeReportProto p) {
        return new NodeReportPBImpl(p);
    }
    
    private YarnProtos.NodeReportProto convertToProtoFormat(final NodeReport t) {
        return ((NodeReportPBImpl)t).getProto();
    }
}
