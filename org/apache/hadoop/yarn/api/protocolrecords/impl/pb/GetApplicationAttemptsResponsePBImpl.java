// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationAttemptReportPBImpl;
import java.util.Iterator;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.ArrayList;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptsResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetApplicationAttemptsResponsePBImpl extends GetApplicationAttemptsResponse
{
    YarnServiceProtos.GetApplicationAttemptsResponseProto proto;
    YarnServiceProtos.GetApplicationAttemptsResponseProto.Builder builder;
    boolean viaProto;
    List<ApplicationAttemptReport> applicationAttemptList;
    
    public GetApplicationAttemptsResponsePBImpl() {
        this.proto = YarnServiceProtos.GetApplicationAttemptsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.GetApplicationAttemptsResponseProto.newBuilder();
    }
    
    public GetApplicationAttemptsResponsePBImpl(final YarnServiceProtos.GetApplicationAttemptsResponseProto proto) {
        this.proto = YarnServiceProtos.GetApplicationAttemptsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public List<ApplicationAttemptReport> getApplicationAttemptList() {
        this.initLocalApplicationAttemptsList();
        return this.applicationAttemptList;
    }
    
    @Override
    public void setApplicationAttemptList(final List<ApplicationAttemptReport> applicationAttempts) {
        this.maybeInitBuilder();
        if (applicationAttempts == null) {
            this.builder.clearApplicationAttempts();
        }
        this.applicationAttemptList = applicationAttempts;
    }
    
    public YarnServiceProtos.GetApplicationAttemptsResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetApplicationAttemptsResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationAttemptList != null) {
            this.addLocalApplicationAttemptsToProto();
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
            this.builder = YarnServiceProtos.GetApplicationAttemptsResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void initLocalApplicationAttemptsList() {
        if (this.applicationAttemptList != null) {
            return;
        }
        final YarnServiceProtos.GetApplicationAttemptsResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ApplicationAttemptReportProto> list = p.getApplicationAttemptsList();
        this.applicationAttemptList = new ArrayList<ApplicationAttemptReport>();
        for (final YarnProtos.ApplicationAttemptReportProto a : list) {
            this.applicationAttemptList.add(this.convertFromProtoFormat(a));
        }
    }
    
    private void addLocalApplicationAttemptsToProto() {
        this.maybeInitBuilder();
        this.builder.clearApplicationAttempts();
        if (this.applicationAttemptList == null) {
            return;
        }
        final Iterable<YarnProtos.ApplicationAttemptReportProto> iterable = new Iterable<YarnProtos.ApplicationAttemptReportProto>() {
            @Override
            public Iterator<YarnProtos.ApplicationAttemptReportProto> iterator() {
                return new Iterator<YarnProtos.ApplicationAttemptReportProto>() {
                    Iterator<ApplicationAttemptReport> iter = GetApplicationAttemptsResponsePBImpl.this.applicationAttemptList.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ApplicationAttemptReportProto next() {
                        return GetApplicationAttemptsResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllApplicationAttempts(iterable);
    }
    
    private ApplicationAttemptReportPBImpl convertFromProtoFormat(final YarnProtos.ApplicationAttemptReportProto p) {
        return new ApplicationAttemptReportPBImpl(p);
    }
    
    private YarnProtos.ApplicationAttemptReportProto convertToProtoFormat(final ApplicationAttemptReport t) {
        return ((ApplicationAttemptReportPBImpl)t).getProto();
    }
}
