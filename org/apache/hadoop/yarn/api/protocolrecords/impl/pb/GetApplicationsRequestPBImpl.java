// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;
import com.google.common.collect.Iterables;
import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.common.base.Function;
import org.apache.hadoop.yarn.api.protocolrecords.ApplicationsRequestScope;
import org.apache.commons.lang.math.LongRange;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import java.util.EnumSet;
import java.util.Set;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetApplicationsRequestPBImpl extends GetApplicationsRequest
{
    YarnServiceProtos.GetApplicationsRequestProto proto;
    YarnServiceProtos.GetApplicationsRequestProto.Builder builder;
    boolean viaProto;
    Set<String> applicationTypes;
    EnumSet<YarnApplicationState> applicationStates;
    Set<String> users;
    Set<String> queues;
    long limit;
    LongRange start;
    LongRange finish;
    private Set<String> applicationTags;
    private ApplicationsRequestScope scope;
    
    public GetApplicationsRequestPBImpl() {
        this.proto = YarnServiceProtos.GetApplicationsRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationTypes = null;
        this.applicationStates = null;
        this.users = null;
        this.queues = null;
        this.limit = Long.MAX_VALUE;
        this.start = null;
        this.finish = null;
        this.builder = YarnServiceProtos.GetApplicationsRequestProto.newBuilder();
    }
    
    public GetApplicationsRequestPBImpl(final YarnServiceProtos.GetApplicationsRequestProto proto) {
        this.proto = YarnServiceProtos.GetApplicationsRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationTypes = null;
        this.applicationStates = null;
        this.users = null;
        this.queues = null;
        this.limit = Long.MAX_VALUE;
        this.start = null;
        this.finish = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetApplicationsRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationTypes != null && !this.applicationTypes.isEmpty()) {
            this.builder.clearApplicationTypes();
            this.builder.addAllApplicationTypes(this.applicationTypes);
        }
        if (this.applicationStates != null && !this.applicationStates.isEmpty()) {
            this.builder.clearApplicationStates();
            this.builder.addAllApplicationStates(Iterables.transform((Iterable<YarnApplicationState>)this.applicationStates, (Function<? super YarnApplicationState, ? extends YarnProtos.YarnApplicationStateProto>)new Function<YarnApplicationState, YarnProtos.YarnApplicationStateProto>() {
                @Override
                public YarnProtos.YarnApplicationStateProto apply(final YarnApplicationState input) {
                    return ProtoUtils.convertToProtoFormat(input);
                }
            }));
        }
        if (this.applicationTags != null && !this.applicationTags.isEmpty()) {
            this.builder.clearApplicationTags();
            this.builder.addAllApplicationTags(this.applicationTags);
        }
        if (this.scope != null) {
            this.builder.setScope(ProtoUtils.convertToProtoFormat(this.scope));
        }
        if (this.start != null) {
            this.builder.setStartBegin(this.start.getMinimumLong());
            this.builder.setStartEnd(this.start.getMaximumLong());
        }
        if (this.finish != null) {
            this.builder.setFinishBegin(this.finish.getMinimumLong());
            this.builder.setFinishEnd(this.finish.getMaximumLong());
        }
        if (this.limit != Long.MAX_VALUE) {
            this.builder.setLimit(this.limit);
        }
        if (this.users != null && !this.users.isEmpty()) {
            this.builder.clearUsers();
            this.builder.addAllUsers(this.users);
        }
        if (this.queues != null && !this.queues.isEmpty()) {
            this.builder.clearQueues();
            this.builder.addAllQueues(this.queues);
        }
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.GetApplicationsRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void initApplicationTypes() {
        if (this.applicationTypes != null) {
            return;
        }
        final YarnServiceProtos.GetApplicationsRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<String> appTypeList = p.getApplicationTypesList();
        (this.applicationTypes = new HashSet<String>()).addAll(appTypeList);
    }
    
    private void initApplicationStates() {
        if (this.applicationStates != null) {
            return;
        }
        final YarnServiceProtos.GetApplicationsRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.YarnApplicationStateProto> appStatesList = p.getApplicationStatesList();
        this.applicationStates = EnumSet.noneOf(YarnApplicationState.class);
        for (final YarnProtos.YarnApplicationStateProto c : appStatesList) {
            this.applicationStates.add(ProtoUtils.convertFromProtoFormat(c));
        }
    }
    
    private void initUsers() {
        if (this.users != null) {
            return;
        }
        final YarnServiceProtos.GetApplicationsRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<String> usersList = p.getUsersList();
        (this.users = new HashSet<String>()).addAll(usersList);
    }
    
    private void initQueues() {
        if (this.queues != null) {
            return;
        }
        final YarnServiceProtos.GetApplicationsRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<String> queuesList = p.getQueuesList();
        (this.queues = new HashSet<String>()).addAll(queuesList);
    }
    
    @Override
    public Set<String> getApplicationTypes() {
        this.initApplicationTypes();
        return this.applicationTypes;
    }
    
    @Override
    public void setApplicationTypes(final Set<String> applicationTypes) {
        this.maybeInitBuilder();
        if (applicationTypes == null) {
            this.builder.clearApplicationTypes();
        }
        this.applicationTypes = applicationTypes;
    }
    
    private void initApplicationTags() {
        if (this.applicationTags != null) {
            return;
        }
        final YarnServiceProtos.GetApplicationsRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        (this.applicationTags = new HashSet<String>()).addAll(p.getApplicationTagsList());
    }
    
    @Override
    public Set<String> getApplicationTags() {
        this.initApplicationTags();
        return this.applicationTags;
    }
    
    @Override
    public void setApplicationTags(final Set<String> tags) {
        this.maybeInitBuilder();
        if (tags == null || tags.isEmpty()) {
            this.builder.clearApplicationTags();
            this.applicationTags = null;
            return;
        }
        this.applicationTags = new HashSet<String>();
        for (final String tag : tags) {
            this.applicationTags.add(tag.toLowerCase());
        }
    }
    
    @Override
    public EnumSet<YarnApplicationState> getApplicationStates() {
        this.initApplicationStates();
        return this.applicationStates;
    }
    
    private void initScope() {
        if (this.scope != null) {
            return;
        }
        final YarnServiceProtos.GetApplicationsRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        this.scope = ProtoUtils.convertFromProtoFormat(p.getScope());
    }
    
    @Override
    public ApplicationsRequestScope getScope() {
        this.initScope();
        return this.scope;
    }
    
    @Override
    public void setScope(final ApplicationsRequestScope scope) {
        this.maybeInitBuilder();
        if (scope == null) {
            this.builder.clearScope();
        }
        this.scope = scope;
    }
    
    @Override
    public void setApplicationStates(final EnumSet<YarnApplicationState> applicationStates) {
        this.maybeInitBuilder();
        if (applicationStates == null) {
            this.builder.clearApplicationStates();
        }
        this.applicationStates = applicationStates;
    }
    
    @Override
    public void setApplicationStates(final Set<String> applicationStates) {
        EnumSet<YarnApplicationState> appStates = null;
        for (final YarnApplicationState state : YarnApplicationState.values()) {
            if (applicationStates.contains(state.name().toLowerCase())) {
                if (appStates == null) {
                    appStates = EnumSet.of(state);
                }
                else {
                    appStates.add(state);
                }
            }
        }
        this.setApplicationStates(appStates);
    }
    
    @Override
    public Set<String> getUsers() {
        this.initUsers();
        return this.users;
    }
    
    @Override
    public void setUsers(final Set<String> users) {
        this.maybeInitBuilder();
        if (users == null) {
            this.builder.clearUsers();
        }
        this.users = users;
    }
    
    @Override
    public Set<String> getQueues() {
        this.initQueues();
        return this.queues;
    }
    
    @Override
    public void setQueues(final Set<String> queues) {
        this.maybeInitBuilder();
        if (queues == null) {
            this.builder.clearQueues();
        }
        this.queues = queues;
    }
    
    @Override
    public long getLimit() {
        if (this.limit == Long.MAX_VALUE) {
            final YarnServiceProtos.GetApplicationsRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
            this.limit = (p.hasLimit() ? p.getLimit() : Long.MAX_VALUE);
        }
        return this.limit;
    }
    
    @Override
    public void setLimit(final long limit) {
        this.maybeInitBuilder();
        this.limit = limit;
    }
    
    @Override
    public LongRange getStartRange() {
        if (this.start == null) {
            final YarnServiceProtos.GetApplicationsRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
            if (p.hasStartBegin() || p.hasStartEnd()) {
                final long begin = p.hasStartBegin() ? p.getStartBegin() : 0L;
                final long end = p.hasStartEnd() ? p.getStartEnd() : Long.MAX_VALUE;
                this.start = new LongRange(begin, end);
            }
        }
        return this.start;
    }
    
    @Override
    public void setStartRange(final LongRange range) {
        this.start = range;
    }
    
    @Override
    public void setStartRange(final long begin, final long end) throws IllegalArgumentException {
        if (begin > end) {
            throw new IllegalArgumentException("begin > end in range (begin, end): (" + begin + ", " + end + ")");
        }
        this.start = new LongRange(begin, end);
    }
    
    @Override
    public LongRange getFinishRange() {
        if (this.finish == null) {
            final YarnServiceProtos.GetApplicationsRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
            if (p.hasFinishBegin() || p.hasFinishEnd()) {
                final long begin = p.hasFinishBegin() ? p.getFinishBegin() : 0L;
                final long end = p.hasFinishEnd() ? p.getFinishEnd() : Long.MAX_VALUE;
                this.finish = new LongRange(begin, end);
            }
        }
        return this.finish;
    }
    
    @Override
    public void setFinishRange(final LongRange range) {
        this.finish = range;
    }
    
    @Override
    public void setFinishRange(final long begin, final long end) {
        if (begin > end) {
            throw new IllegalArgumentException("begin > end in range (begin, end): (" + begin + ", " + end + ")");
        }
        this.finish = new LongRange(begin, end);
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetApplicationsRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
