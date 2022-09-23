// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ExtensionRegistry extends ExtensionRegistryLite
{
    private final Map<String, ExtensionInfo> extensionsByName;
    private final Map<DescriptorIntPair, ExtensionInfo> extensionsByNumber;
    private static final ExtensionRegistry EMPTY;
    
    public static ExtensionRegistry newInstance() {
        return new ExtensionRegistry();
    }
    
    public static ExtensionRegistry getEmptyRegistry() {
        return ExtensionRegistry.EMPTY;
    }
    
    @Override
    public ExtensionRegistry getUnmodifiable() {
        return new ExtensionRegistry(this);
    }
    
    public ExtensionInfo findExtensionByName(final String fullName) {
        return this.extensionsByName.get(fullName);
    }
    
    public ExtensionInfo findExtensionByNumber(final Descriptors.Descriptor containingType, final int fieldNumber) {
        return this.extensionsByNumber.get(new DescriptorIntPair(containingType, fieldNumber));
    }
    
    public void add(final GeneratedMessage.GeneratedExtension<?, ?> extension) {
        if (extension.getDescriptor().getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            if (extension.getMessageDefaultInstance() == null) {
                throw new IllegalStateException("Registered message-type extension had null default instance: " + extension.getDescriptor().getFullName());
            }
            this.add(new ExtensionInfo(extension.getDescriptor(), extension.getMessageDefaultInstance()));
        }
        else {
            this.add(new ExtensionInfo(extension.getDescriptor(), (Message)null));
        }
    }
    
    public void add(final Descriptors.FieldDescriptor type) {
        if (type.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            throw new IllegalArgumentException("ExtensionRegistry.add() must be provided a default instance when adding an embedded message extension.");
        }
        this.add(new ExtensionInfo(type, (Message)null));
    }
    
    public void add(final Descriptors.FieldDescriptor type, final Message defaultInstance) {
        if (type.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            throw new IllegalArgumentException("ExtensionRegistry.add() provided a default instance for a non-message extension.");
        }
        this.add(new ExtensionInfo(type, defaultInstance));
    }
    
    private ExtensionRegistry() {
        this.extensionsByName = new HashMap<String, ExtensionInfo>();
        this.extensionsByNumber = new HashMap<DescriptorIntPair, ExtensionInfo>();
    }
    
    private ExtensionRegistry(final ExtensionRegistry other) {
        super(other);
        this.extensionsByName = Collections.unmodifiableMap((Map<? extends String, ? extends ExtensionInfo>)other.extensionsByName);
        this.extensionsByNumber = Collections.unmodifiableMap((Map<? extends DescriptorIntPair, ? extends ExtensionInfo>)other.extensionsByNumber);
    }
    
    private ExtensionRegistry(final boolean empty) {
        super(ExtensionRegistryLite.getEmptyRegistry());
        this.extensionsByName = Collections.emptyMap();
        this.extensionsByNumber = Collections.emptyMap();
    }
    
    private void add(final ExtensionInfo extension) {
        if (!extension.descriptor.isExtension()) {
            throw new IllegalArgumentException("ExtensionRegistry.add() was given a FieldDescriptor for a regular (non-extension) field.");
        }
        this.extensionsByName.put(extension.descriptor.getFullName(), extension);
        this.extensionsByNumber.put(new DescriptorIntPair(extension.descriptor.getContainingType(), extension.descriptor.getNumber()), extension);
        final Descriptors.FieldDescriptor field = extension.descriptor;
        if (field.getContainingType().getOptions().getMessageSetWireFormat() && field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && field.isOptional() && field.getExtensionScope() == field.getMessageType()) {
            this.extensionsByName.put(field.getMessageType().getFullName(), extension);
        }
    }
    
    static {
        EMPTY = new ExtensionRegistry(true);
    }
    
    public static final class ExtensionInfo
    {
        public final Descriptors.FieldDescriptor descriptor;
        public final Message defaultInstance;
        
        private ExtensionInfo(final Descriptors.FieldDescriptor descriptor) {
            this.descriptor = descriptor;
            this.defaultInstance = null;
        }
        
        private ExtensionInfo(final Descriptors.FieldDescriptor descriptor, final Message defaultInstance) {
            this.descriptor = descriptor;
            this.defaultInstance = defaultInstance;
        }
    }
    
    private static final class DescriptorIntPair
    {
        private final Descriptors.Descriptor descriptor;
        private final int number;
        
        DescriptorIntPair(final Descriptors.Descriptor descriptor, final int number) {
            this.descriptor = descriptor;
            this.number = number;
        }
        
        @Override
        public int hashCode() {
            return this.descriptor.hashCode() * 65535 + this.number;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof DescriptorIntPair)) {
                return false;
            }
            final DescriptorIntPair other = (DescriptorIntPair)obj;
            return this.descriptor == other.descriptor && this.number == other.number;
        }
    }
}
