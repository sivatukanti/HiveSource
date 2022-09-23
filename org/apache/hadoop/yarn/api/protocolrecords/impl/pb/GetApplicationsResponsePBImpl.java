// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationReportPBImpl;
import java.util.Iterator;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.ArrayList;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetApplicationsResponsePBImpl extends GetApplicationsResponse
{
    YarnServiceProtos.GetApplicationsResponseProto proto;
    YarnServiceProtos.GetApplicationsResponseProto.Builder builder;
    boolean viaProto;
    List<ApplicationReport> applicationList;
    
    public GetApplicationsResponsePBImpl() {
        this.proto = YarnServiceProtos.GetApplicationsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.GetApplicationsResponseProto.newBuilder();
    }
    
    public GetApplicationsResponsePBImpl(final YarnServiceProtos.GetApplicationsResponseProto proto) {
        this.proto = YarnServiceProtos.GetApplicationsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public List<ApplicationReport> getApplicationList() {
        this.initLocalApplicationsList();
        return this.applicationList;
    }
    
    @Override
    public void setApplicationList(final List<ApplicationReport> applications) {
        this.maybeInitBuilder();
        if (applications == null) {
            this.builder.clearApplications();
        }
        this.applicationList = applications;
    }
    
    public YarnServiceProtos.GetApplicationsResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetApplicationsResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationList != null) {
            this.addLocalApplicationsToProto();
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
            this.builder = YarnServiceProtos.GetApplicationsResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void initLocalApplicationsList() {
        if (this.applicationList != null) {
            return;
        }
        final YarnServiceProtos.GetApplicationsResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ApplicationReportProto> list = p.getApplicationsList();
        this.applicationList = new ArrayList<ApplicationReport>();
        for (final YarnProtos.ApplicationReportProto a : list) {
            this.applicationList.add(this.convertFromProtoFormat(a));
        }
    }
    
    private void addLocalApplicationsToProto() {
        this.maybeInitBuilder();
        this.builder.clearApplications();
        if (this.applicationList == null) {
            return;
        }
        final Iterable<YarnProtos.ApplicationReportProto> iterable = new Iterable<YarnProtos.ApplicationReportProto>() {
            @Override
            public Iterator<YarnProtos.ApplicationReportProto> iterator() {
                return new Iterator<YarnProtos.ApplicationReportProto>() {
                    Iterator<ApplicationReport> iter = GetApplicationsResponsePBImpl.this.applicationList.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ApplicationReportProto next() {
                        return GetApplicationsResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllApplications(iterable);
    }
    
    private ApplicationReportPBImpl convertFromProtoFormat(final YarnProtos.ApplicationReportProto p) {
        return new ApplicationReportPBImpl(p);
    }
    
    private YarnProtos.ApplicationReportProto convertToProtoFormat(final ApplicationReport t) {
        return ((ApplicationReportPBImpl)t).getProto();
    }
}
