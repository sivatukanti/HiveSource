// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import java.util.Iterator;
import com.google.common.base.CharMatcher;
import java.util.Collection;
import java.util.HashSet;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.LogAggregationContext;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ApplicationSubmissionContextPBImpl extends ApplicationSubmissionContext
{
    YarnProtos.ApplicationSubmissionContextProto proto;
    YarnProtos.ApplicationSubmissionContextProto.Builder builder;
    boolean viaProto;
    private ApplicationId applicationId;
    private Priority priority;
    private ContainerLaunchContext amContainer;
    private Resource resource;
    private Set<String> applicationTags;
    private ResourceRequest amResourceRequest;
    private LogAggregationContext logAggregationContext;
    private ReservationId reservationId;
    
    public ApplicationSubmissionContextPBImpl() {
        this.proto = YarnProtos.ApplicationSubmissionContextProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationId = null;
        this.priority = null;
        this.amContainer = null;
        this.resource = null;
        this.applicationTags = null;
        this.amResourceRequest = null;
        this.logAggregationContext = null;
        this.reservationId = null;
        this.builder = YarnProtos.ApplicationSubmissionContextProto.newBuilder();
    }
    
    public ApplicationSubmissionContextPBImpl(final YarnProtos.ApplicationSubmissionContextProto proto) {
        this.proto = YarnProtos.ApplicationSubmissionContextProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationId = null;
        this.priority = null;
        this.amContainer = null;
        this.resource = null;
        this.applicationTags = null;
        this.amResourceRequest = null;
        this.logAggregationContext = null;
        this.reservationId = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.ApplicationSubmissionContextProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ApplicationSubmissionContextPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationId != null) {
            this.builder.setApplicationId(this.convertToProtoFormat(this.applicationId));
        }
        if (this.priority != null) {
            this.builder.setPriority(this.convertToProtoFormat(this.priority));
        }
        if (this.amContainer != null) {
            this.builder.setAmContainerSpec(this.convertToProtoFormat(this.amContainer));
        }
        if (this.resource != null && !((ResourcePBImpl)this.resource).getProto().equals(this.builder.getResource())) {
            this.builder.setResource(this.convertToProtoFormat(this.resource));
        }
        if (this.applicationTags != null && !this.applicationTags.isEmpty()) {
            this.builder.clearApplicationTags();
            this.builder.addAllApplicationTags(this.applicationTags);
        }
        if (this.amResourceRequest != null) {
            this.builder.setAmContainerResourceRequest(this.convertToProtoFormat(this.amResourceRequest));
        }
        if (this.logAggregationContext != null) {
            this.builder.setLogAggregationContext(this.convertToProtoFormat(this.logAggregationContext));
        }
        if (this.reservationId != null) {
            this.builder.setReservationId(this.convertToProtoFormat(this.reservationId));
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
            this.builder = YarnProtos.ApplicationSubmissionContextProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public Priority getPriority() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.priority != null) {
            return this.priority;
        }
        if (!p.hasPriority()) {
            return null;
        }
        return this.priority = this.convertFromProtoFormat(p.getPriority());
    }
    
    @Override
    public void setPriority(final Priority priority) {
        this.maybeInitBuilder();
        if (priority == null) {
            this.builder.clearPriority();
        }
        this.priority = priority;
    }
    
    @Override
    public ApplicationId getApplicationId() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.applicationId != null) {
            return this.applicationId;
        }
        if (!p.hasApplicationId()) {
            return null;
        }
        return this.applicationId = this.convertFromProtoFormat(p.getApplicationId());
    }
    
    @Override
    public void setApplicationId(final ApplicationId applicationId) {
        this.maybeInitBuilder();
        if (applicationId == null) {
            this.builder.clearApplicationId();
        }
        this.applicationId = applicationId;
    }
    
    @Override
    public String getApplicationName() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationName()) {
            return null;
        }
        return p.getApplicationName();
    }
    
    @Override
    public void setApplicationName(final String applicationName) {
        this.maybeInitBuilder();
        if (applicationName == null) {
            this.builder.clearApplicationName();
            return;
        }
        this.builder.setApplicationName(applicationName);
    }
    
    @Override
    public String getQueue() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasQueue()) {
            return null;
        }
        return p.getQueue();
    }
    
    @Override
    public String getApplicationType() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationType()) {
            return null;
        }
        return p.getApplicationType();
    }
    
    private void initApplicationTags() {
        if (this.applicationTags != null) {
            return;
        }
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        (this.applicationTags = new HashSet<String>()).addAll(p.getApplicationTagsList());
    }
    
    @Override
    public Set<String> getApplicationTags() {
        this.initApplicationTags();
        return this.applicationTags;
    }
    
    @Override
    public void setQueue(final String queue) {
        this.maybeInitBuilder();
        if (queue == null) {
            this.builder.clearQueue();
            return;
        }
        this.builder.setQueue(queue);
    }
    
    @Override
    public void setApplicationType(final String applicationType) {
        this.maybeInitBuilder();
        if (applicationType == null) {
            this.builder.clearApplicationType();
            return;
        }
        this.builder.setApplicationType(applicationType);
    }
    
    private void checkTags(final Set<String> tags) {
        if (tags.size() > 10) {
            throw new IllegalArgumentException("Too many applicationTags, a maximum of only 10 are allowed!");
        }
        for (final String tag : tags) {
            if (tag.length() > 100) {
                throw new IllegalArgumentException("Tag " + tag + " is too long, " + "maximum allowed length of a tag is " + 100);
            }
            if (!CharMatcher.ASCII.matchesAllOf(tag)) {
                throw new IllegalArgumentException("A tag can only have ASCII characters! Invalid tag - " + tag);
            }
        }
    }
    
    @Override
    public void setApplicationTags(final Set<String> tags) {
        this.maybeInitBuilder();
        if (tags == null || tags.isEmpty()) {
            this.builder.clearApplicationTags();
            this.applicationTags = null;
            return;
        }
        this.checkTags(tags);
        this.applicationTags = new HashSet<String>();
        for (final String tag : tags) {
            this.applicationTags.add(tag.toLowerCase());
        }
    }
    
    @Override
    public ContainerLaunchContext getAMContainerSpec() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.amContainer != null) {
            return this.amContainer;
        }
        if (!p.hasAmContainerSpec()) {
            return null;
        }
        return this.amContainer = this.convertFromProtoFormat(p.getAmContainerSpec());
    }
    
    @Override
    public void setAMContainerSpec(final ContainerLaunchContext amContainer) {
        this.maybeInitBuilder();
        if (amContainer == null) {
            this.builder.clearAmContainerSpec();
        }
        this.amContainer = amContainer;
    }
    
    @Override
    public boolean getUnmanagedAM() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getUnmanagedAm();
    }
    
    @Override
    public void setUnmanagedAM(final boolean value) {
        this.maybeInitBuilder();
        this.builder.setUnmanagedAm(value);
    }
    
    @Override
    public boolean getCancelTokensWhenComplete() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getCancelTokensWhenComplete();
    }
    
    @Override
    public void setCancelTokensWhenComplete(final boolean cancel) {
        this.maybeInitBuilder();
        this.builder.setCancelTokensWhenComplete(cancel);
    }
    
    @Override
    public int getMaxAppAttempts() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getMaxAppAttempts();
    }
    
    @Override
    public void setMaxAppAttempts(final int maxAppAttempts) {
        this.maybeInitBuilder();
        this.builder.setMaxAppAttempts(maxAppAttempts);
    }
    
    @Override
    public Resource getResource() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.resource != null) {
            return this.resource;
        }
        if (!p.hasResource()) {
            return null;
        }
        return this.resource = this.convertFromProtoFormat(p.getResource());
    }
    
    @Override
    public void setResource(final Resource resource) {
        this.maybeInitBuilder();
        if (resource == null) {
            this.builder.clearResource();
        }
        this.resource = resource;
    }
    
    @Override
    public ReservationId getReservationID() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.reservationId != null) {
            return this.reservationId;
        }
        if (!p.hasReservationId()) {
            return null;
        }
        return this.reservationId = this.convertFromProtoFormat(p.getReservationId());
    }
    
    @Override
    public void setReservationID(final ReservationId reservationID) {
        this.maybeInitBuilder();
        if (reservationID == null) {
            this.builder.clearReservationId();
            return;
        }
        this.reservationId = reservationID;
    }
    
    @Override
    public void setKeepContainersAcrossApplicationAttempts(final boolean keepContainers) {
        this.maybeInitBuilder();
        this.builder.setKeepContainersAcrossApplicationAttempts(keepContainers);
    }
    
    @Override
    public boolean getKeepContainersAcrossApplicationAttempts() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getKeepContainersAcrossApplicationAttempts();
    }
    
    private PriorityPBImpl convertFromProtoFormat(final YarnProtos.PriorityProto p) {
        return new PriorityPBImpl(p);
    }
    
    private YarnProtos.PriorityProto convertToProtoFormat(final Priority t) {
        return ((PriorityPBImpl)t).getProto();
    }
    
    private ResourceRequestPBImpl convertFromProtoFormat(final YarnProtos.ResourceRequestProto p) {
        return new ResourceRequestPBImpl(p);
    }
    
    private YarnProtos.ResourceRequestProto convertToProtoFormat(final ResourceRequest t) {
        return ((ResourceRequestPBImpl)t).getProto();
    }
    
    private ApplicationIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationIdProto p) {
        return new ApplicationIdPBImpl(p);
    }
    
    private YarnProtos.ApplicationIdProto convertToProtoFormat(final ApplicationId t) {
        return ((ApplicationIdPBImpl)t).getProto();
    }
    
    private ContainerLaunchContextPBImpl convertFromProtoFormat(final YarnProtos.ContainerLaunchContextProto p) {
        return new ContainerLaunchContextPBImpl(p);
    }
    
    private YarnProtos.ContainerLaunchContextProto convertToProtoFormat(final ContainerLaunchContext t) {
        return ((ContainerLaunchContextPBImpl)t).getProto();
    }
    
    private ResourcePBImpl convertFromProtoFormat(final YarnProtos.ResourceProto p) {
        return new ResourcePBImpl(p);
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource t) {
        return ((ResourcePBImpl)t).getProto();
    }
    
    @Override
    public String getNodeLabelExpression() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasNodeLabelExpression()) {
            return null;
        }
        return p.getNodeLabelExpression();
    }
    
    @Override
    public void setNodeLabelExpression(final String labelExpression) {
        this.maybeInitBuilder();
        if (labelExpression == null) {
            this.builder.clearNodeLabelExpression();
            return;
        }
        this.builder.setNodeLabelExpression(labelExpression);
    }
    
    @Override
    public ResourceRequest getAMContainerResourceRequest() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.amResourceRequest != null) {
            return this.amResourceRequest;
        }
        if (!p.hasAmContainerResourceRequest()) {
            return null;
        }
        return this.amResourceRequest = this.convertFromProtoFormat(p.getAmContainerResourceRequest());
    }
    
    @Override
    public void setAMContainerResourceRequest(final ResourceRequest request) {
        this.maybeInitBuilder();
        if (request == null) {
            this.builder.clearAmContainerResourceRequest();
        }
        this.amResourceRequest = request;
    }
    
    @Override
    public long getAttemptFailuresValidityInterval() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getAttemptFailuresValidityInterval();
    }
    
    @Override
    public void setAttemptFailuresValidityInterval(final long attemptFailuresValidityInterval) {
        this.maybeInitBuilder();
        this.builder.setAttemptFailuresValidityInterval(attemptFailuresValidityInterval);
    }
    
    private LogAggregationContextPBImpl convertFromProtoFormat(final YarnProtos.LogAggregationContextProto p) {
        return new LogAggregationContextPBImpl(p);
    }
    
    private YarnProtos.LogAggregationContextProto convertToProtoFormat(final LogAggregationContext t) {
        return ((LogAggregationContextPBImpl)t).getProto();
    }
    
    @Override
    public LogAggregationContext getLogAggregationContext() {
        final YarnProtos.ApplicationSubmissionContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.logAggregationContext != null) {
            return this.logAggregationContext;
        }
        if (!p.hasLogAggregationContext()) {
            return null;
        }
        return this.logAggregationContext = this.convertFromProtoFormat(p.getLogAggregationContext());
    }
    
    @Override
    public void setLogAggregationContext(final LogAggregationContext logAggregationContext) {
        this.maybeInitBuilder();
        if (logAggregationContext == null) {
            this.builder.clearLogAggregationContext();
        }
        this.logAggregationContext = logAggregationContext;
    }
    
    private ReservationIdPBImpl convertFromProtoFormat(final YarnProtos.ReservationIdProto p) {
        return new ReservationIdPBImpl(p);
    }
    
    private YarnProtos.ReservationIdProto convertToProtoFormat(final ReservationId t) {
        return ((ReservationIdPBImpl)t).getProto();
    }
}
