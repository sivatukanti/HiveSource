// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.NMTokenPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ResourcePBImpl;
import java.util.Collection;
import java.util.ArrayList;
import com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import org.apache.hadoop.yarn.proto.YarnProtos;
import java.util.HashMap;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import java.util.EnumSet;
import org.apache.hadoop.yarn.api.records.NMToken;
import org.apache.hadoop.yarn.api.records.Container;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RegisterApplicationMasterResponsePBImpl extends RegisterApplicationMasterResponse
{
    YarnServiceProtos.RegisterApplicationMasterResponseProto proto;
    YarnServiceProtos.RegisterApplicationMasterResponseProto.Builder builder;
    boolean viaProto;
    private Resource maximumResourceCapability;
    private Map<ApplicationAccessType, String> applicationACLS;
    private List<Container> containersFromPreviousAttempts;
    private List<NMToken> nmTokens;
    private EnumSet<YarnServiceProtos.SchedulerResourceTypes> schedulerResourceTypes;
    
    public RegisterApplicationMasterResponsePBImpl() {
        this.proto = YarnServiceProtos.RegisterApplicationMasterResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationACLS = null;
        this.containersFromPreviousAttempts = null;
        this.nmTokens = null;
        this.schedulerResourceTypes = null;
        this.builder = YarnServiceProtos.RegisterApplicationMasterResponseProto.newBuilder();
    }
    
    public RegisterApplicationMasterResponsePBImpl(final YarnServiceProtos.RegisterApplicationMasterResponseProto proto) {
        this.proto = YarnServiceProtos.RegisterApplicationMasterResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationACLS = null;
        this.containersFromPreviousAttempts = null;
        this.nmTokens = null;
        this.schedulerResourceTypes = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.RegisterApplicationMasterResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((RegisterApplicationMasterResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
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
        if (this.maximumResourceCapability != null) {
            this.builder.setMaximumCapability(this.convertToProtoFormat(this.maximumResourceCapability));
        }
        if (this.applicationACLS != null) {
            this.addApplicationACLs();
        }
        if (this.containersFromPreviousAttempts != null) {
            this.addContainersFromPreviousAttemptToProto();
        }
        if (this.nmTokens != null) {
            this.builder.clearNmTokensFromPreviousAttempts();
            final Iterable<YarnServiceProtos.NMTokenProto> iterable = this.getTokenProtoIterable(this.nmTokens);
            this.builder.addAllNmTokensFromPreviousAttempts(iterable);
        }
        if (this.schedulerResourceTypes != null) {
            this.addSchedulerResourceTypes();
        }
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.RegisterApplicationMasterResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public Resource getMaximumResourceCapability() {
        if (this.maximumResourceCapability != null) {
            return this.maximumResourceCapability;
        }
        final YarnServiceProtos.RegisterApplicationMasterResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasMaximumCapability()) {
            return null;
        }
        return this.maximumResourceCapability = this.convertFromProtoFormat(p.getMaximumCapability());
    }
    
    @Override
    public void setMaximumResourceCapability(final Resource capability) {
        this.maybeInitBuilder();
        if (this.maximumResourceCapability == null) {
            this.builder.clearMaximumCapability();
        }
        this.maximumResourceCapability = capability;
    }
    
    @Override
    public Map<ApplicationAccessType, String> getApplicationACLs() {
        this.initApplicationACLs();
        return this.applicationACLS;
    }
    
    private void initApplicationACLs() {
        if (this.applicationACLS != null) {
            return;
        }
        final YarnServiceProtos.RegisterApplicationMasterResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ApplicationACLMapProto> list = p.getApplicationACLsList();
        this.applicationACLS = new HashMap<ApplicationAccessType, String>(list.size());
        for (final YarnProtos.ApplicationACLMapProto aclProto : list) {
            this.applicationACLS.put(ProtoUtils.convertFromProtoFormat(aclProto.getAccessType()), aclProto.getAcl());
        }
    }
    
    private void addApplicationACLs() {
        this.maybeInitBuilder();
        this.builder.clearApplicationACLs();
        if (this.applicationACLS == null) {
            return;
        }
        final Iterable<? extends YarnProtos.ApplicationACLMapProto> values = new Iterable<YarnProtos.ApplicationACLMapProto>() {
            @Override
            public Iterator<YarnProtos.ApplicationACLMapProto> iterator() {
                return new Iterator<YarnProtos.ApplicationACLMapProto>() {
                    Iterator<ApplicationAccessType> aclsIterator = RegisterApplicationMasterResponsePBImpl.this.applicationACLS.keySet().iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.aclsIterator.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ApplicationACLMapProto next() {
                        final ApplicationAccessType key = this.aclsIterator.next();
                        return YarnProtos.ApplicationACLMapProto.newBuilder().setAcl(RegisterApplicationMasterResponsePBImpl.this.applicationACLS.get(key)).setAccessType(ProtoUtils.convertToProtoFormat(key)).build();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllApplicationACLs(values);
    }
    
    @Override
    public void setApplicationACLs(final Map<ApplicationAccessType, String> appACLs) {
        if (appACLs == null) {
            return;
        }
        this.initApplicationACLs();
        this.applicationACLS.clear();
        this.applicationACLS.putAll(appACLs);
    }
    
    @Override
    public void setClientToAMTokenMasterKey(final ByteBuffer key) {
        if (key == null) {
            this.builder.clearClientToAmTokenMasterKey();
            return;
        }
        this.maybeInitBuilder();
        this.builder.setClientToAmTokenMasterKey(ByteString.copyFrom(key));
    }
    
    @Override
    public ByteBuffer getClientToAMTokenMasterKey() {
        this.maybeInitBuilder();
        final ByteBuffer key = ByteBuffer.wrap(this.builder.getClientToAmTokenMasterKey().toByteArray());
        return key;
    }
    
    @Override
    public List<Container> getContainersFromPreviousAttempts() {
        if (this.containersFromPreviousAttempts != null) {
            return this.containersFromPreviousAttempts;
        }
        this.initContainersPreviousAttemptList();
        return this.containersFromPreviousAttempts;
    }
    
    @Override
    public void setContainersFromPreviousAttempts(final List<Container> containers) {
        if (containers == null) {
            return;
        }
        (this.containersFromPreviousAttempts = new ArrayList<Container>()).addAll(containers);
    }
    
    @Override
    public String getQueue() {
        final YarnServiceProtos.RegisterApplicationMasterResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasQueue()) {
            return null;
        }
        return p.getQueue();
    }
    
    @Override
    public void setQueue(final String queue) {
        this.maybeInitBuilder();
        if (queue == null) {
            this.builder.clearQueue();
        }
        else {
            this.builder.setQueue(queue);
        }
    }
    
    private void initContainersPreviousAttemptList() {
        final YarnServiceProtos.RegisterApplicationMasterResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ContainerProto> list = p.getContainersFromPreviousAttemptsList();
        this.containersFromPreviousAttempts = new ArrayList<Container>();
        for (final YarnProtos.ContainerProto c : list) {
            this.containersFromPreviousAttempts.add(this.convertFromProtoFormat(c));
        }
    }
    
    private void addContainersFromPreviousAttemptToProto() {
        this.maybeInitBuilder();
        this.builder.clearContainersFromPreviousAttempts();
        final List<YarnProtos.ContainerProto> list = new ArrayList<YarnProtos.ContainerProto>();
        for (final Container c : this.containersFromPreviousAttempts) {
            list.add(this.convertToProtoFormat(c));
        }
        this.builder.addAllContainersFromPreviousAttempts(list);
    }
    
    @Override
    public List<NMToken> getNMTokensFromPreviousAttempts() {
        if (this.nmTokens != null) {
            return this.nmTokens;
        }
        this.initLocalNewNMTokenList();
        return this.nmTokens;
    }
    
    @Override
    public void setNMTokensFromPreviousAttempts(final List<NMToken> nmTokens) {
        if (nmTokens == null || nmTokens.isEmpty()) {
            if (this.nmTokens != null) {
                this.nmTokens.clear();
            }
            this.builder.clearNmTokensFromPreviousAttempts();
            return;
        }
        (this.nmTokens = new ArrayList<NMToken>()).addAll(nmTokens);
    }
    
    private synchronized void initLocalNewNMTokenList() {
        final YarnServiceProtos.RegisterApplicationMasterResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnServiceProtos.NMTokenProto> list = p.getNmTokensFromPreviousAttemptsList();
        this.nmTokens = new ArrayList<NMToken>();
        for (final YarnServiceProtos.NMTokenProto t : list) {
            this.nmTokens.add(this.convertFromProtoFormat(t));
        }
    }
    
    private synchronized Iterable<YarnServiceProtos.NMTokenProto> getTokenProtoIterable(final List<NMToken> nmTokenList) {
        this.maybeInitBuilder();
        return new Iterable<YarnServiceProtos.NMTokenProto>() {
            @Override
            public synchronized Iterator<YarnServiceProtos.NMTokenProto> iterator() {
                return new Iterator<YarnServiceProtos.NMTokenProto>() {
                    Iterator<NMToken> iter = nmTokenList.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnServiceProtos.NMTokenProto next() {
                        return RegisterApplicationMasterResponsePBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
    
    @Override
    public EnumSet<YarnServiceProtos.SchedulerResourceTypes> getSchedulerResourceTypes() {
        this.initSchedulerResourceTypes();
        return this.schedulerResourceTypes;
    }
    
    private void initSchedulerResourceTypes() {
        if (this.schedulerResourceTypes != null) {
            return;
        }
        final YarnServiceProtos.RegisterApplicationMasterResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnServiceProtos.SchedulerResourceTypes> list = p.getSchedulerResourceTypesList();
        if (list.isEmpty()) {
            this.schedulerResourceTypes = EnumSet.noneOf(YarnServiceProtos.SchedulerResourceTypes.class);
        }
        else {
            this.schedulerResourceTypes = EnumSet.copyOf(list);
        }
    }
    
    private void addSchedulerResourceTypes() {
        this.maybeInitBuilder();
        this.builder.clearSchedulerResourceTypes();
        if (this.schedulerResourceTypes == null) {
            return;
        }
        final Iterable<? extends YarnServiceProtos.SchedulerResourceTypes> values = new Iterable<YarnServiceProtos.SchedulerResourceTypes>() {
            @Override
            public Iterator<YarnServiceProtos.SchedulerResourceTypes> iterator() {
                return new Iterator<YarnServiceProtos.SchedulerResourceTypes>() {
                    Iterator<YarnServiceProtos.SchedulerResourceTypes> settingsIterator = RegisterApplicationMasterResponsePBImpl.this.schedulerResourceTypes.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.settingsIterator.hasNext();
                    }
                    
                    @Override
                    public YarnServiceProtos.SchedulerResourceTypes next() {
                        return this.settingsIterator.next();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllSchedulerResourceTypes(values);
    }
    
    @Override
    public void setSchedulerResourceTypes(final EnumSet<YarnServiceProtos.SchedulerResourceTypes> types) {
        if (types == null) {
            return;
        }
        this.initSchedulerResourceTypes();
        this.schedulerResourceTypes.clear();
        this.schedulerResourceTypes.addAll((Collection<?>)types);
    }
    
    private Resource convertFromProtoFormat(final YarnProtos.ResourceProto resource) {
        return new ResourcePBImpl(resource);
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource resource) {
        return ((ResourcePBImpl)resource).getProto();
    }
    
    private ContainerPBImpl convertFromProtoFormat(final YarnProtos.ContainerProto p) {
        return new ContainerPBImpl(p);
    }
    
    private YarnProtos.ContainerProto convertToProtoFormat(final Container t) {
        return ((ContainerPBImpl)t).getProto();
    }
    
    private YarnServiceProtos.NMTokenProto convertToProtoFormat(final NMToken token) {
        return ((NMTokenPBImpl)token).getProto();
    }
    
    private NMToken convertFromProtoFormat(final YarnServiceProtos.NMTokenProto proto) {
        return new NMTokenPBImpl(proto);
    }
}
