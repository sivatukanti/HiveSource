// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import java.util.List;
import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.api.records.LocalResource;
import java.util.Map;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ContainerLaunchContextPBImpl extends ContainerLaunchContext
{
    YarnProtos.ContainerLaunchContextProto proto;
    YarnProtos.ContainerLaunchContextProto.Builder builder;
    boolean viaProto;
    private Map<String, LocalResource> localResources;
    private ByteBuffer tokens;
    private Map<String, ByteBuffer> serviceData;
    private Map<String, String> environment;
    private List<String> commands;
    private Map<ApplicationAccessType, String> applicationACLS;
    
    public ContainerLaunchContextPBImpl() {
        this.proto = YarnProtos.ContainerLaunchContextProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.localResources = null;
        this.tokens = null;
        this.serviceData = null;
        this.environment = null;
        this.commands = null;
        this.applicationACLS = null;
        this.builder = YarnProtos.ContainerLaunchContextProto.newBuilder();
    }
    
    public ContainerLaunchContextPBImpl(final YarnProtos.ContainerLaunchContextProto proto) {
        this.proto = YarnProtos.ContainerLaunchContextProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.localResources = null;
        this.tokens = null;
        this.serviceData = null;
        this.environment = null;
        this.commands = null;
        this.applicationACLS = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.ContainerLaunchContextProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ContainerLaunchContextPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    protected final ByteBuffer convertFromProtoFormat(final ByteString byteString) {
        return ProtoUtils.convertFromProtoFormat(byteString);
    }
    
    protected final ByteString convertToProtoFormat(final ByteBuffer byteBuffer) {
        return ProtoUtils.convertToProtoFormat(byteBuffer);
    }
    
    private void mergeLocalToBuilder() {
        if (this.localResources != null) {
            this.addLocalResourcesToProto();
        }
        if (this.tokens != null) {
            this.builder.setTokens(this.convertToProtoFormat(this.tokens));
        }
        if (this.serviceData != null) {
            this.addServiceDataToProto();
        }
        if (this.environment != null) {
            this.addEnvToProto();
        }
        if (this.commands != null) {
            this.addCommandsToProto();
        }
        if (this.applicationACLS != null) {
            this.addApplicationACLs();
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
            this.builder = YarnProtos.ContainerLaunchContextProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public List<String> getCommands() {
        this.initCommands();
        return this.commands;
    }
    
    private void initCommands() {
        if (this.commands != null) {
            return;
        }
        final YarnProtos.ContainerLaunchContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<String> list = p.getCommandList();
        this.commands = new ArrayList<String>();
        for (final String c : list) {
            this.commands.add(c);
        }
    }
    
    @Override
    public void setCommands(final List<String> commands) {
        if (commands == null) {
            return;
        }
        this.initCommands();
        this.commands.clear();
        this.commands.addAll(commands);
    }
    
    private void addCommandsToProto() {
        this.maybeInitBuilder();
        this.builder.clearCommand();
        if (this.commands == null) {
            return;
        }
        this.builder.addAllCommand(this.commands);
    }
    
    @Override
    public Map<String, LocalResource> getLocalResources() {
        this.initLocalResources();
        return this.localResources;
    }
    
    private void initLocalResources() {
        if (this.localResources != null) {
            return;
        }
        final YarnProtos.ContainerLaunchContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.StringLocalResourceMapProto> list = p.getLocalResourcesList();
        this.localResources = new HashMap<String, LocalResource>();
        for (final YarnProtos.StringLocalResourceMapProto c : list) {
            this.localResources.put(c.getKey(), this.convertFromProtoFormat(c.getValue()));
        }
    }
    
    @Override
    public void setLocalResources(final Map<String, LocalResource> localResources) {
        if (localResources == null) {
            return;
        }
        this.initLocalResources();
        this.localResources.clear();
        this.localResources.putAll(localResources);
    }
    
    private void addLocalResourcesToProto() {
        this.maybeInitBuilder();
        this.builder.clearLocalResources();
        if (this.localResources == null) {
            return;
        }
        final Iterable<YarnProtos.StringLocalResourceMapProto> iterable = new Iterable<YarnProtos.StringLocalResourceMapProto>() {
            @Override
            public Iterator<YarnProtos.StringLocalResourceMapProto> iterator() {
                return new Iterator<YarnProtos.StringLocalResourceMapProto>() {
                    Iterator<String> keyIter = ContainerLaunchContextPBImpl.this.localResources.keySet().iterator();
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public YarnProtos.StringLocalResourceMapProto next() {
                        final String key = this.keyIter.next();
                        return YarnProtos.StringLocalResourceMapProto.newBuilder().setKey(key).setValue(ContainerLaunchContextPBImpl.this.convertToProtoFormat(ContainerLaunchContextPBImpl.this.localResources.get(key))).build();
                    }
                    
                    @Override
                    public boolean hasNext() {
                        return this.keyIter.hasNext();
                    }
                };
            }
        };
        this.builder.addAllLocalResources(iterable);
    }
    
    @Override
    public ByteBuffer getTokens() {
        final YarnProtos.ContainerLaunchContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.tokens != null) {
            return this.tokens;
        }
        if (!p.hasTokens()) {
            return null;
        }
        return this.tokens = this.convertFromProtoFormat(p.getTokens());
    }
    
    @Override
    public void setTokens(final ByteBuffer tokens) {
        this.maybeInitBuilder();
        if (tokens == null) {
            this.builder.clearTokens();
        }
        this.tokens = tokens;
    }
    
    @Override
    public Map<String, ByteBuffer> getServiceData() {
        this.initServiceData();
        return this.serviceData;
    }
    
    private void initServiceData() {
        if (this.serviceData != null) {
            return;
        }
        final YarnProtos.ContainerLaunchContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.StringBytesMapProto> list = p.getServiceDataList();
        this.serviceData = new HashMap<String, ByteBuffer>();
        for (final YarnProtos.StringBytesMapProto c : list) {
            this.serviceData.put(c.getKey(), this.convertFromProtoFormat(c.getValue()));
        }
    }
    
    @Override
    public void setServiceData(final Map<String, ByteBuffer> serviceData) {
        if (serviceData == null) {
            return;
        }
        this.initServiceData();
        this.serviceData.putAll(serviceData);
    }
    
    private void addServiceDataToProto() {
        this.maybeInitBuilder();
        this.builder.clearServiceData();
        if (this.serviceData == null) {
            return;
        }
        final Iterable<YarnProtos.StringBytesMapProto> iterable = new Iterable<YarnProtos.StringBytesMapProto>() {
            @Override
            public Iterator<YarnProtos.StringBytesMapProto> iterator() {
                return new Iterator<YarnProtos.StringBytesMapProto>() {
                    Iterator<String> keyIter = ContainerLaunchContextPBImpl.this.serviceData.keySet().iterator();
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public YarnProtos.StringBytesMapProto next() {
                        final String key = this.keyIter.next();
                        return YarnProtos.StringBytesMapProto.newBuilder().setKey(key).setValue(ContainerLaunchContextPBImpl.this.convertToProtoFormat(ContainerLaunchContextPBImpl.this.serviceData.get(key))).build();
                    }
                    
                    @Override
                    public boolean hasNext() {
                        return this.keyIter.hasNext();
                    }
                };
            }
        };
        this.builder.addAllServiceData(iterable);
    }
    
    @Override
    public Map<String, String> getEnvironment() {
        this.initEnv();
        return this.environment;
    }
    
    private void initEnv() {
        if (this.environment != null) {
            return;
        }
        final YarnProtos.ContainerLaunchContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.StringStringMapProto> list = p.getEnvironmentList();
        this.environment = new HashMap<String, String>();
        for (final YarnProtos.StringStringMapProto c : list) {
            this.environment.put(c.getKey(), c.getValue());
        }
    }
    
    @Override
    public void setEnvironment(final Map<String, String> env) {
        if (env == null) {
            return;
        }
        this.initEnv();
        this.environment.clear();
        this.environment.putAll(env);
    }
    
    private void addEnvToProto() {
        this.maybeInitBuilder();
        this.builder.clearEnvironment();
        if (this.environment == null) {
            return;
        }
        final Iterable<YarnProtos.StringStringMapProto> iterable = new Iterable<YarnProtos.StringStringMapProto>() {
            @Override
            public Iterator<YarnProtos.StringStringMapProto> iterator() {
                return new Iterator<YarnProtos.StringStringMapProto>() {
                    Iterator<String> keyIter = ContainerLaunchContextPBImpl.this.environment.keySet().iterator();
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                    @Override
                    public YarnProtos.StringStringMapProto next() {
                        final String key = this.keyIter.next();
                        return YarnProtos.StringStringMapProto.newBuilder().setKey(key).setValue(ContainerLaunchContextPBImpl.this.environment.get(key)).build();
                    }
                    
                    @Override
                    public boolean hasNext() {
                        return this.keyIter.hasNext();
                    }
                };
            }
        };
        this.builder.addAllEnvironment(iterable);
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
        final YarnProtos.ContainerLaunchContextProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
                    Iterator<ApplicationAccessType> aclsIterator = ContainerLaunchContextPBImpl.this.applicationACLS.keySet().iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.aclsIterator.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ApplicationACLMapProto next() {
                        final ApplicationAccessType key = this.aclsIterator.next();
                        return YarnProtos.ApplicationACLMapProto.newBuilder().setAcl(ContainerLaunchContextPBImpl.this.applicationACLS.get(key)).setAccessType(ProtoUtils.convertToProtoFormat(key)).build();
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
    
    private LocalResourcePBImpl convertFromProtoFormat(final YarnProtos.LocalResourceProto p) {
        return new LocalResourcePBImpl(p);
    }
    
    private YarnProtos.LocalResourceProto convertToProtoFormat(final LocalResource t) {
        return ((LocalResourcePBImpl)t).getProto();
    }
}
