// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.QueueState;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.QueueInfo;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class QueueInfoPBImpl extends QueueInfo
{
    YarnProtos.QueueInfoProto proto;
    YarnProtos.QueueInfoProto.Builder builder;
    boolean viaProto;
    List<ApplicationReport> applicationsList;
    List<QueueInfo> childQueuesList;
    Set<String> accessibleNodeLabels;
    
    public QueueInfoPBImpl() {
        this.proto = YarnProtos.QueueInfoProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.QueueInfoProto.newBuilder();
    }
    
    public QueueInfoPBImpl(final YarnProtos.QueueInfoProto proto) {
        this.proto = YarnProtos.QueueInfoProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public List<ApplicationReport> getApplications() {
        this.initLocalApplicationsList();
        return this.applicationsList;
    }
    
    @Override
    public float getCapacity() {
        final YarnProtos.QueueInfoProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasCapacity() ? p.getCapacity() : -1.0f;
    }
    
    @Override
    public List<QueueInfo> getChildQueues() {
        this.initLocalChildQueuesList();
        return this.childQueuesList;
    }
    
    @Override
    public float getCurrentCapacity() {
        final YarnProtos.QueueInfoProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasCurrentCapacity() ? p.getCurrentCapacity() : 0.0f;
    }
    
    @Override
    public float getMaximumCapacity() {
        final YarnProtos.QueueInfoProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasMaximumCapacity() ? p.getMaximumCapacity() : -1.0f;
    }
    
    @Override
    public String getQueueName() {
        final YarnProtos.QueueInfoProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasQueueName() ? p.getQueueName() : null;
    }
    
    @Override
    public QueueState getQueueState() {
        final YarnProtos.QueueInfoProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasState()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getState());
    }
    
    @Override
    public void setApplications(final List<ApplicationReport> applications) {
        if (applications == null) {
            this.builder.clearApplications();
        }
        this.applicationsList = applications;
    }
    
    @Override
    public void setCapacity(final float capacity) {
        this.maybeInitBuilder();
        this.builder.setCapacity(capacity);
    }
    
    @Override
    public void setChildQueues(final List<QueueInfo> childQueues) {
        if (childQueues == null) {
            this.builder.clearChildQueues();
        }
        this.childQueuesList = childQueues;
    }
    
    @Override
    public void setCurrentCapacity(final float currentCapacity) {
        this.maybeInitBuilder();
        this.builder.setCurrentCapacity(currentCapacity);
    }
    
    @Override
    public void setMaximumCapacity(final float maximumCapacity) {
        this.maybeInitBuilder();
        this.builder.setMaximumCapacity(maximumCapacity);
    }
    
    @Override
    public void setQueueName(final String queueName) {
        this.maybeInitBuilder();
        if (queueName == null) {
            this.builder.clearQueueName();
            return;
        }
        this.builder.setQueueName(queueName);
    }
    
    @Override
    public void setQueueState(final QueueState queueState) {
        this.maybeInitBuilder();
        if (queueState == null) {
            this.builder.clearState();
            return;
        }
        this.builder.setState(this.convertToProtoFormat(queueState));
    }
    
    public YarnProtos.QueueInfoProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((QueueInfoPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void initLocalApplicationsList() {
        if (this.applicationsList != null) {
            return;
        }
        final YarnProtos.QueueInfoProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ApplicationReportProto> list = p.getApplicationsList();
        this.applicationsList = new ArrayList<ApplicationReport>();
        for (final YarnProtos.ApplicationReportProto a : list) {
            this.applicationsList.add(this.convertFromProtoFormat(a));
        }
    }
    
    private void addApplicationsToProto() {
        this.maybeInitBuilder();
        this.builder.clearApplications();
        if (this.applicationsList == null) {
            return;
        }
        final Iterable<YarnProtos.ApplicationReportProto> iterable = new Iterable<YarnProtos.ApplicationReportProto>() {
            @Override
            public Iterator<YarnProtos.ApplicationReportProto> iterator() {
                return new Iterator<YarnProtos.ApplicationReportProto>() {
                    Iterator<ApplicationReport> iter = QueueInfoPBImpl.this.applicationsList.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ApplicationReportProto next() {
                        return QueueInfoPBImpl.this.convertToProtoFormat(this.iter.next());
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
    
    private void initLocalChildQueuesList() {
        if (this.childQueuesList != null) {
            return;
        }
        final YarnProtos.QueueInfoProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.QueueInfoProto> list = p.getChildQueuesList();
        this.childQueuesList = new ArrayList<QueueInfo>();
        for (final YarnProtos.QueueInfoProto a : list) {
            this.childQueuesList.add(this.convertFromProtoFormat(a));
        }
    }
    
    private void addChildQueuesInfoToProto() {
        this.maybeInitBuilder();
        this.builder.clearChildQueues();
        if (this.childQueuesList == null) {
            return;
        }
        final Iterable<YarnProtos.QueueInfoProto> iterable = new Iterable<YarnProtos.QueueInfoProto>() {
            @Override
            public Iterator<YarnProtos.QueueInfoProto> iterator() {
                return new Iterator<YarnProtos.QueueInfoProto>() {
                    Iterator<QueueInfo> iter = QueueInfoPBImpl.this.childQueuesList.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.QueueInfoProto next() {
                        return QueueInfoPBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllChildQueues(iterable);
    }
    
    private void mergeLocalToBuilder() {
        if (this.childQueuesList != null) {
            this.addChildQueuesInfoToProto();
        }
        if (this.applicationsList != null) {
            this.addApplicationsToProto();
        }
        if (this.accessibleNodeLabels != null) {
            this.builder.clearAccessibleNodeLabels();
            this.builder.addAllAccessibleNodeLabels(this.accessibleNodeLabels);
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
            this.builder = YarnProtos.QueueInfoProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private ApplicationReportPBImpl convertFromProtoFormat(final YarnProtos.ApplicationReportProto a) {
        return new ApplicationReportPBImpl(a);
    }
    
    private YarnProtos.ApplicationReportProto convertToProtoFormat(final ApplicationReport t) {
        return ((ApplicationReportPBImpl)t).getProto();
    }
    
    private QueueInfoPBImpl convertFromProtoFormat(final YarnProtos.QueueInfoProto a) {
        return new QueueInfoPBImpl(a);
    }
    
    private YarnProtos.QueueInfoProto convertToProtoFormat(final QueueInfo q) {
        return ((QueueInfoPBImpl)q).getProto();
    }
    
    private QueueState convertFromProtoFormat(final YarnProtos.QueueStateProto q) {
        return ProtoUtils.convertFromProtoFormat(q);
    }
    
    private YarnProtos.QueueStateProto convertToProtoFormat(final QueueState queueState) {
        return ProtoUtils.convertToProtoFormat(queueState);
    }
    
    @Override
    public void setAccessibleNodeLabels(final Set<String> nodeLabels) {
        this.maybeInitBuilder();
        this.builder.clearAccessibleNodeLabels();
        this.accessibleNodeLabels = nodeLabels;
    }
    
    private void initNodeLabels() {
        if (this.accessibleNodeLabels != null) {
            return;
        }
        final YarnProtos.QueueInfoProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        (this.accessibleNodeLabels = new HashSet<String>()).addAll(p.getAccessibleNodeLabelsList());
    }
    
    @Override
    public Set<String> getAccessibleNodeLabels() {
        this.initNodeLabels();
        return this.accessibleNodeLabels;
    }
    
    @Override
    public String getDefaultNodeLabelExpression() {
        final YarnProtos.QueueInfoProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasDefaultNodeLabelExpression() ? p.getDefaultNodeLabelExpression() : null;
    }
    
    @Override
    public void setDefaultNodeLabelExpression(final String defaultNodeLabelExpression) {
        this.maybeInitBuilder();
        if (defaultNodeLabelExpression == null) {
            this.builder.clearDefaultNodeLabelExpression();
            return;
        }
        this.builder.setDefaultNodeLabelExpression(defaultNodeLabelExpression);
    }
}
