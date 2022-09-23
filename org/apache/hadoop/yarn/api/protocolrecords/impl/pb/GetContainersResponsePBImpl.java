// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ContainerReportPBImpl;
import java.util.Iterator;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.ArrayList;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetContainersResponsePBImpl extends GetContainersResponse
{
    YarnServiceProtos.GetContainersResponseProto proto;
    YarnServiceProtos.GetContainersResponseProto.Builder builder;
    boolean viaProto;
    List<ContainerReport> containerList;
    
    public GetContainersResponsePBImpl() {
        this.proto = YarnServiceProtos.GetContainersResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.GetContainersResponseProto.newBuilder();
    }
    
    public GetContainersResponsePBImpl(final YarnServiceProtos.GetContainersResponseProto proto) {
        this.proto = YarnServiceProtos.GetContainersResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public List<ContainerReport> getContainerList() {
        this.initLocalContainerList();
        return this.containerList;
    }
    
    @Override
    public void setContainerList(final List<ContainerReport> containers) {
        this.maybeInitBuilder();
        if (containers == null) {
            this.builder.clearContainers();
        }
        this.containerList = containers;
    }
    
    public YarnServiceProtos.GetContainersResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetContainersResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerList != null) {
            this.addLocalContainersToProto();
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
            this.builder = YarnServiceProtos.GetContainersResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void initLocalContainerList() {
        if (this.containerList != null) {
            return;
        }
        final YarnServiceProtos.GetContainersResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerReportProto> list = p.getContainersList();
        this.containerList = new ArrayList<ContainerReport>();
        for (final YarnProtos.ContainerReportProto c : list) {
            this.containerList.add(this.convertFromProtoFormat(c));
        }
    }
    
    private void addLocalContainersToProto() {
        this.maybeInitBuilder();
        this.builder.clearContainers();
        if (this.containerList == null) {
            return;
        }
        final Iterable<YarnProtos.ContainerReportProto> iterable = new Iterable<YarnProtos.ContainerReportProto>() {
            @Override
            public Iterator<YarnProtos.ContainerReportProto> iterator() {
                return new Iterator<YarnProtos.ContainerReportProto>() {
                    Iterator<ContainerReport> iter = GetContainersResponsePBImpl.this.containerList.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ContainerReportProto next() {
                        return GetContainersResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllContainers(iterable);
    }
    
    private ContainerReportPBImpl convertFromProtoFormat(final YarnProtos.ContainerReportProto p) {
        return new ContainerReportPBImpl(p);
    }
    
    private YarnProtos.ContainerReportProto convertToProtoFormat(final ContainerReport t) {
        return ((ContainerReportPBImpl)t).getProto();
    }
}
