// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.PreemptionContainer;
import java.util.Set;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.StrictPreemptionContract;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class StrictPreemptionContractPBImpl extends StrictPreemptionContract
{
    YarnProtos.StrictPreemptionContractProto proto;
    YarnProtos.StrictPreemptionContractProto.Builder builder;
    boolean viaProto;
    private Set<PreemptionContainer> containers;
    
    public StrictPreemptionContractPBImpl() {
        this.proto = YarnProtos.StrictPreemptionContractProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.StrictPreemptionContractProto.newBuilder();
    }
    
    public StrictPreemptionContractPBImpl(final YarnProtos.StrictPreemptionContractProto proto) {
        this.proto = YarnProtos.StrictPreemptionContractProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public synchronized YarnProtos.StrictPreemptionContractProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((StrictPreemptionContractPBImpl)this.getClass().cast(other)).getProto());
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
        if (this.containers != null) {
            this.addContainersToProto();
        }
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.StrictPreemptionContractProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public synchronized Set<PreemptionContainer> getContainers() {
        this.initIds();
        return this.containers;
    }
    
    @Override
    public synchronized void setContainers(final Set<PreemptionContainer> containers) {
        if (null == containers) {
            this.builder.clearContainer();
        }
        this.containers = containers;
    }
    
    private void initIds() {
        if (this.containers != null) {
            return;
        }
        final YarnProtos.StrictPreemptionContractProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.PreemptionContainerProto> list = p.getContainerList();
        this.containers = new HashSet<PreemptionContainer>();
        for (final YarnProtos.PreemptionContainerProto c : list) {
            this.containers.add(this.convertFromProtoFormat(c));
        }
    }
    
    private void addContainersToProto() {
        this.maybeInitBuilder();
        this.builder.clearContainer();
        if (this.containers == null) {
            return;
        }
        final Iterable<YarnProtos.PreemptionContainerProto> iterable = new Iterable<YarnProtos.PreemptionContainerProto>() {
            @Override
            public Iterator<YarnProtos.PreemptionContainerProto> iterator() {
                return new Iterator<YarnProtos.PreemptionContainerProto>() {
                    Iterator<PreemptionContainer> iter = StrictPreemptionContractPBImpl.this.containers.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.PreemptionContainerProto next() {
                        return StrictPreemptionContractPBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllContainer(iterable);
    }
    
    private PreemptionContainerPBImpl convertFromProtoFormat(final YarnProtos.PreemptionContainerProto p) {
        return new PreemptionContainerPBImpl(p);
    }
    
    private YarnProtos.PreemptionContainerProto convertToProtoFormat(final PreemptionContainer t) {
        return ((PreemptionContainerPBImpl)t).getProto();
    }
}
