// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public final class Descriptors
{
    private static String computeFullName(final FileDescriptor file, final Descriptor parent, final String name) {
        if (parent != null) {
            return parent.getFullName() + '.' + name;
        }
        if (file.getPackage().length() > 0) {
            return file.getPackage() + '.' + name;
        }
        return name;
    }
    
    public static final class FileDescriptor
    {
        private DescriptorProtos.FileDescriptorProto proto;
        private final Descriptor[] messageTypes;
        private final EnumDescriptor[] enumTypes;
        private final ServiceDescriptor[] services;
        private final FieldDescriptor[] extensions;
        private final FileDescriptor[] dependencies;
        private final FileDescriptor[] publicDependencies;
        private final DescriptorPool pool;
        
        public DescriptorProtos.FileDescriptorProto toProto() {
            return this.proto;
        }
        
        public String getName() {
            return this.proto.getName();
        }
        
        public String getPackage() {
            return this.proto.getPackage();
        }
        
        public DescriptorProtos.FileOptions getOptions() {
            return this.proto.getOptions();
        }
        
        public List<Descriptor> getMessageTypes() {
            return Collections.unmodifiableList((List<? extends Descriptor>)Arrays.asList((T[])this.messageTypes));
        }
        
        public List<EnumDescriptor> getEnumTypes() {
            return Collections.unmodifiableList((List<? extends EnumDescriptor>)Arrays.asList((T[])this.enumTypes));
        }
        
        public List<ServiceDescriptor> getServices() {
            return Collections.unmodifiableList((List<? extends ServiceDescriptor>)Arrays.asList((T[])this.services));
        }
        
        public List<FieldDescriptor> getExtensions() {
            return Collections.unmodifiableList((List<? extends FieldDescriptor>)Arrays.asList((T[])this.extensions));
        }
        
        public List<FileDescriptor> getDependencies() {
            return Collections.unmodifiableList((List<? extends FileDescriptor>)Arrays.asList((T[])this.dependencies));
        }
        
        public List<FileDescriptor> getPublicDependencies() {
            return Collections.unmodifiableList((List<? extends FileDescriptor>)Arrays.asList((T[])this.publicDependencies));
        }
        
        public Descriptor findMessageTypeByName(String name) {
            if (name.indexOf(46) != -1) {
                return null;
            }
            if (this.getPackage().length() > 0) {
                name = this.getPackage() + '.' + name;
            }
            final GenericDescriptor result = this.pool.findSymbol(name);
            if (result != null && result instanceof Descriptor && result.getFile() == this) {
                return (Descriptor)result;
            }
            return null;
        }
        
        public EnumDescriptor findEnumTypeByName(String name) {
            if (name.indexOf(46) != -1) {
                return null;
            }
            if (this.getPackage().length() > 0) {
                name = this.getPackage() + '.' + name;
            }
            final GenericDescriptor result = this.pool.findSymbol(name);
            if (result != null && result instanceof EnumDescriptor && result.getFile() == this) {
                return (EnumDescriptor)result;
            }
            return null;
        }
        
        public ServiceDescriptor findServiceByName(String name) {
            if (name.indexOf(46) != -1) {
                return null;
            }
            if (this.getPackage().length() > 0) {
                name = this.getPackage() + '.' + name;
            }
            final GenericDescriptor result = this.pool.findSymbol(name);
            if (result != null && result instanceof ServiceDescriptor && result.getFile() == this) {
                return (ServiceDescriptor)result;
            }
            return null;
        }
        
        public FieldDescriptor findExtensionByName(String name) {
            if (name.indexOf(46) != -1) {
                return null;
            }
            if (this.getPackage().length() > 0) {
                name = this.getPackage() + '.' + name;
            }
            final GenericDescriptor result = this.pool.findSymbol(name);
            if (result != null && result instanceof FieldDescriptor && result.getFile() == this) {
                return (FieldDescriptor)result;
            }
            return null;
        }
        
        public static FileDescriptor buildFrom(final DescriptorProtos.FileDescriptorProto proto, final FileDescriptor[] dependencies) throws DescriptorValidationException {
            final DescriptorPool pool = new DescriptorPool(dependencies);
            final FileDescriptor result = new FileDescriptor(proto, dependencies, pool);
            if (dependencies.length != proto.getDependencyCount()) {
                throw new DescriptorValidationException(result, "Dependencies passed to FileDescriptor.buildFrom() don't match those listed in the FileDescriptorProto.");
            }
            for (int i = 0; i < proto.getDependencyCount(); ++i) {
                if (!dependencies[i].getName().equals(proto.getDependency(i))) {
                    throw new DescriptorValidationException(result, "Dependencies passed to FileDescriptor.buildFrom() don't match those listed in the FileDescriptorProto.");
                }
            }
            result.crossLink();
            return result;
        }
        
        public static void internalBuildGeneratedFileFrom(final String[] descriptorDataParts, final FileDescriptor[] dependencies, final InternalDescriptorAssigner descriptorAssigner) {
            final StringBuilder descriptorData = new StringBuilder();
            for (final String part : descriptorDataParts) {
                descriptorData.append(part);
            }
            byte[] descriptorBytes;
            try {
                descriptorBytes = descriptorData.toString().getBytes("ISO-8859-1");
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Standard encoding ISO-8859-1 not supported by JVM.", e);
            }
            DescriptorProtos.FileDescriptorProto proto;
            try {
                proto = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes);
            }
            catch (InvalidProtocolBufferException e2) {
                throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", e2);
            }
            FileDescriptor result;
            try {
                result = buildFrom(proto, dependencies);
            }
            catch (DescriptorValidationException e3) {
                throw new IllegalArgumentException("Invalid embedded descriptor for \"" + proto.getName() + "\".", e3);
            }
            final ExtensionRegistry registry = descriptorAssigner.assignDescriptors(result);
            if (registry != null) {
                try {
                    proto = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes, registry);
                }
                catch (InvalidProtocolBufferException e4) {
                    throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", e4);
                }
                result.setProto(proto);
            }
        }
        
        private FileDescriptor(final DescriptorProtos.FileDescriptorProto proto, final FileDescriptor[] dependencies, final DescriptorPool pool) throws DescriptorValidationException {
            this.pool = pool;
            this.proto = proto;
            this.dependencies = dependencies.clone();
            this.publicDependencies = new FileDescriptor[proto.getPublicDependencyCount()];
            for (int i = 0; i < proto.getPublicDependencyCount(); ++i) {
                final int index = proto.getPublicDependency(i);
                if (index < 0 || index >= this.dependencies.length) {
                    throw new DescriptorValidationException(this, "Invalid public dependency index.");
                }
                this.publicDependencies[i] = this.dependencies[proto.getPublicDependency(i)];
            }
            pool.addPackage(this.getPackage(), this);
            this.messageTypes = new Descriptor[proto.getMessageTypeCount()];
            for (int i = 0; i < proto.getMessageTypeCount(); ++i) {
                this.messageTypes[i] = new Descriptor(proto.getMessageType(i), this, (Descriptor)null, i);
            }
            this.enumTypes = new EnumDescriptor[proto.getEnumTypeCount()];
            for (int i = 0; i < proto.getEnumTypeCount(); ++i) {
                this.enumTypes[i] = new EnumDescriptor(proto.getEnumType(i), this, (Descriptor)null, i);
            }
            this.services = new ServiceDescriptor[proto.getServiceCount()];
            for (int i = 0; i < proto.getServiceCount(); ++i) {
                this.services[i] = new ServiceDescriptor(proto.getService(i), this, i);
            }
            this.extensions = new FieldDescriptor[proto.getExtensionCount()];
            for (int i = 0; i < proto.getExtensionCount(); ++i) {
                this.extensions[i] = new FieldDescriptor(proto.getExtension(i), this, (Descriptor)null, i, true);
            }
        }
        
        private void crossLink() throws DescriptorValidationException {
            for (final Descriptor messageType : this.messageTypes) {
                messageType.crossLink();
            }
            for (final ServiceDescriptor service : this.services) {
                service.crossLink();
            }
            for (final FieldDescriptor extension : this.extensions) {
                extension.crossLink();
            }
        }
        
        private void setProto(final DescriptorProtos.FileDescriptorProto proto) {
            this.proto = proto;
            for (int i = 0; i < this.messageTypes.length; ++i) {
                this.messageTypes[i].setProto(proto.getMessageType(i));
            }
            for (int i = 0; i < this.enumTypes.length; ++i) {
                this.enumTypes[i].setProto(proto.getEnumType(i));
            }
            for (int i = 0; i < this.services.length; ++i) {
                this.services[i].setProto(proto.getService(i));
            }
            for (int i = 0; i < this.extensions.length; ++i) {
                this.extensions[i].setProto(proto.getExtension(i));
            }
        }
        
        public interface InternalDescriptorAssigner
        {
            ExtensionRegistry assignDescriptors(final FileDescriptor p0);
        }
    }
    
    public static final class Descriptor implements GenericDescriptor
    {
        private final int index;
        private DescriptorProtos.DescriptorProto proto;
        private final String fullName;
        private final FileDescriptor file;
        private final Descriptor containingType;
        private final Descriptor[] nestedTypes;
        private final EnumDescriptor[] enumTypes;
        private final FieldDescriptor[] fields;
        private final FieldDescriptor[] extensions;
        
        public int getIndex() {
            return this.index;
        }
        
        public DescriptorProtos.DescriptorProto toProto() {
            return this.proto;
        }
        
        public String getName() {
            return this.proto.getName();
        }
        
        public String getFullName() {
            return this.fullName;
        }
        
        public FileDescriptor getFile() {
            return this.file;
        }
        
        public Descriptor getContainingType() {
            return this.containingType;
        }
        
        public DescriptorProtos.MessageOptions getOptions() {
            return this.proto.getOptions();
        }
        
        public List<FieldDescriptor> getFields() {
            return Collections.unmodifiableList((List<? extends FieldDescriptor>)Arrays.asList((T[])this.fields));
        }
        
        public List<FieldDescriptor> getExtensions() {
            return Collections.unmodifiableList((List<? extends FieldDescriptor>)Arrays.asList((T[])this.extensions));
        }
        
        public List<Descriptor> getNestedTypes() {
            return Collections.unmodifiableList((List<? extends Descriptor>)Arrays.asList((T[])this.nestedTypes));
        }
        
        public List<EnumDescriptor> getEnumTypes() {
            return Collections.unmodifiableList((List<? extends EnumDescriptor>)Arrays.asList((T[])this.enumTypes));
        }
        
        public boolean isExtensionNumber(final int number) {
            for (final DescriptorProtos.DescriptorProto.ExtensionRange range : this.proto.getExtensionRangeList()) {
                if (range.getStart() <= number && number < range.getEnd()) {
                    return true;
                }
            }
            return false;
        }
        
        public FieldDescriptor findFieldByName(final String name) {
            final GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
            if (result != null && result instanceof FieldDescriptor) {
                return (FieldDescriptor)result;
            }
            return null;
        }
        
        public FieldDescriptor findFieldByNumber(final int number) {
            return this.file.pool.fieldsByNumber.get(new DescriptorPool.DescriptorIntPair(this, number));
        }
        
        public Descriptor findNestedTypeByName(final String name) {
            final GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
            if (result != null && result instanceof Descriptor) {
                return (Descriptor)result;
            }
            return null;
        }
        
        public EnumDescriptor findEnumTypeByName(final String name) {
            final GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
            if (result != null && result instanceof EnumDescriptor) {
                return (EnumDescriptor)result;
            }
            return null;
        }
        
        private Descriptor(final DescriptorProtos.DescriptorProto proto, final FileDescriptor file, final Descriptor parent, final int index) throws DescriptorValidationException {
            this.index = index;
            this.proto = proto;
            this.fullName = computeFullName(file, parent, proto.getName());
            this.file = file;
            this.containingType = parent;
            this.nestedTypes = new Descriptor[proto.getNestedTypeCount()];
            for (int i = 0; i < proto.getNestedTypeCount(); ++i) {
                this.nestedTypes[i] = new Descriptor(proto.getNestedType(i), file, this, i);
            }
            this.enumTypes = new EnumDescriptor[proto.getEnumTypeCount()];
            for (int i = 0; i < proto.getEnumTypeCount(); ++i) {
                this.enumTypes[i] = new EnumDescriptor(proto.getEnumType(i), file, this, i);
            }
            this.fields = new FieldDescriptor[proto.getFieldCount()];
            for (int i = 0; i < proto.getFieldCount(); ++i) {
                this.fields[i] = new FieldDescriptor(proto.getField(i), file, this, i, false);
            }
            this.extensions = new FieldDescriptor[proto.getExtensionCount()];
            for (int i = 0; i < proto.getExtensionCount(); ++i) {
                this.extensions[i] = new FieldDescriptor(proto.getExtension(i), file, this, i, true);
            }
            file.pool.addSymbol(this);
        }
        
        private void crossLink() throws DescriptorValidationException {
            for (final Descriptor nestedType : this.nestedTypes) {
                nestedType.crossLink();
            }
            for (final FieldDescriptor field : this.fields) {
                field.crossLink();
            }
            for (final FieldDescriptor extension : this.extensions) {
                extension.crossLink();
            }
        }
        
        private void setProto(final DescriptorProtos.DescriptorProto proto) {
            this.proto = proto;
            for (int i = 0; i < this.nestedTypes.length; ++i) {
                this.nestedTypes[i].setProto(proto.getNestedType(i));
            }
            for (int i = 0; i < this.enumTypes.length; ++i) {
                this.enumTypes[i].setProto(proto.getEnumType(i));
            }
            for (int i = 0; i < this.fields.length; ++i) {
                this.fields[i].setProto(proto.getField(i));
            }
            for (int i = 0; i < this.extensions.length; ++i) {
                this.extensions[i].setProto(proto.getExtension(i));
            }
        }
    }
    
    public static final class FieldDescriptor implements GenericDescriptor, Comparable<FieldDescriptor>, FieldSet.FieldDescriptorLite<FieldDescriptor>
    {
        private static final WireFormat.FieldType[] table;
        private final int index;
        private DescriptorProtos.FieldDescriptorProto proto;
        private final String fullName;
        private final FileDescriptor file;
        private final Descriptor extensionScope;
        private Type type;
        private Descriptor containingType;
        private Descriptor messageType;
        private EnumDescriptor enumType;
        private Object defaultValue;
        
        public int getIndex() {
            return this.index;
        }
        
        public DescriptorProtos.FieldDescriptorProto toProto() {
            return this.proto;
        }
        
        public String getName() {
            return this.proto.getName();
        }
        
        public int getNumber() {
            return this.proto.getNumber();
        }
        
        public String getFullName() {
            return this.fullName;
        }
        
        public JavaType getJavaType() {
            return this.type.getJavaType();
        }
        
        public WireFormat.JavaType getLiteJavaType() {
            return this.getLiteType().getJavaType();
        }
        
        public FileDescriptor getFile() {
            return this.file;
        }
        
        public Type getType() {
            return this.type;
        }
        
        public WireFormat.FieldType getLiteType() {
            return FieldDescriptor.table[this.type.ordinal()];
        }
        
        public boolean isRequired() {
            return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED;
        }
        
        public boolean isOptional() {
            return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL;
        }
        
        public boolean isRepeated() {
            return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
        }
        
        public boolean isPacked() {
            return this.getOptions().getPacked();
        }
        
        public boolean isPackable() {
            return this.isRepeated() && this.getLiteType().isPackable();
        }
        
        public boolean hasDefaultValue() {
            return this.proto.hasDefaultValue();
        }
        
        public Object getDefaultValue() {
            if (this.getJavaType() == JavaType.MESSAGE) {
                throw new UnsupportedOperationException("FieldDescriptor.getDefaultValue() called on an embedded message field.");
            }
            return this.defaultValue;
        }
        
        public DescriptorProtos.FieldOptions getOptions() {
            return this.proto.getOptions();
        }
        
        public boolean isExtension() {
            return this.proto.hasExtendee();
        }
        
        public Descriptor getContainingType() {
            return this.containingType;
        }
        
        public Descriptor getExtensionScope() {
            if (!this.isExtension()) {
                throw new UnsupportedOperationException("This field is not an extension.");
            }
            return this.extensionScope;
        }
        
        public Descriptor getMessageType() {
            if (this.getJavaType() != JavaType.MESSAGE) {
                throw new UnsupportedOperationException("This field is not of message type.");
            }
            return this.messageType;
        }
        
        public EnumDescriptor getEnumType() {
            if (this.getJavaType() != JavaType.ENUM) {
                throw new UnsupportedOperationException("This field is not of enum type.");
            }
            return this.enumType;
        }
        
        public int compareTo(final FieldDescriptor other) {
            if (other.containingType != this.containingType) {
                throw new IllegalArgumentException("FieldDescriptors can only be compared to other FieldDescriptors for fields of the same message type.");
            }
            return this.getNumber() - other.getNumber();
        }
        
        private FieldDescriptor(final DescriptorProtos.FieldDescriptorProto proto, final FileDescriptor file, final Descriptor parent, final int index, final boolean isExtension) throws DescriptorValidationException {
            this.index = index;
            this.proto = proto;
            this.fullName = computeFullName(file, parent, proto.getName());
            this.file = file;
            if (proto.hasType()) {
                this.type = Type.valueOf(proto.getType());
            }
            if (this.getNumber() <= 0) {
                throw new DescriptorValidationException((GenericDescriptor)this, "Field numbers must be positive integers.");
            }
            if (proto.getOptions().getPacked() && !this.isPackable()) {
                throw new DescriptorValidationException((GenericDescriptor)this, "[packed = true] can only be specified for repeated primitive fields.");
            }
            if (isExtension) {
                if (!proto.hasExtendee()) {
                    throw new DescriptorValidationException((GenericDescriptor)this, "FieldDescriptorProto.extendee not set for extension field.");
                }
                this.containingType = null;
                if (parent != null) {
                    this.extensionScope = parent;
                }
                else {
                    this.extensionScope = null;
                }
            }
            else {
                if (proto.hasExtendee()) {
                    throw new DescriptorValidationException((GenericDescriptor)this, "FieldDescriptorProto.extendee set for non-extension field.");
                }
                this.containingType = parent;
                this.extensionScope = null;
            }
            file.pool.addSymbol(this);
        }
        
        private void crossLink() throws DescriptorValidationException {
            if (this.proto.hasExtendee()) {
                final GenericDescriptor extendee = this.file.pool.lookupSymbol(this.proto.getExtendee(), this, DescriptorPool.SearchFilter.TYPES_ONLY);
                if (!(extendee instanceof Descriptor)) {
                    throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.proto.getExtendee() + "\" is not a message type.");
                }
                this.containingType = (Descriptor)extendee;
                if (!this.getContainingType().isExtensionNumber(this.getNumber())) {
                    throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.getContainingType().getFullName() + "\" does not declare " + this.getNumber() + " as an extension number.");
                }
            }
            if (this.proto.hasTypeName()) {
                final GenericDescriptor typeDescriptor = this.file.pool.lookupSymbol(this.proto.getTypeName(), this, DescriptorPool.SearchFilter.TYPES_ONLY);
                if (!this.proto.hasType()) {
                    if (typeDescriptor instanceof Descriptor) {
                        this.type = Type.MESSAGE;
                    }
                    else {
                        if (!(typeDescriptor instanceof EnumDescriptor)) {
                            throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.proto.getTypeName() + "\" is not a type.");
                        }
                        this.type = Type.ENUM;
                    }
                }
                if (this.getJavaType() == JavaType.MESSAGE) {
                    if (!(typeDescriptor instanceof Descriptor)) {
                        throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.proto.getTypeName() + "\" is not a message type.");
                    }
                    this.messageType = (Descriptor)typeDescriptor;
                    if (this.proto.hasDefaultValue()) {
                        throw new DescriptorValidationException((GenericDescriptor)this, "Messages can't have default values.");
                    }
                }
                else {
                    if (this.getJavaType() != JavaType.ENUM) {
                        throw new DescriptorValidationException((GenericDescriptor)this, "Field with primitive type has type_name.");
                    }
                    if (!(typeDescriptor instanceof EnumDescriptor)) {
                        throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.proto.getTypeName() + "\" is not an enum type.");
                    }
                    this.enumType = (EnumDescriptor)typeDescriptor;
                }
            }
            else if (this.getJavaType() == JavaType.MESSAGE || this.getJavaType() == JavaType.ENUM) {
                throw new DescriptorValidationException((GenericDescriptor)this, "Field with message or enum type missing type_name.");
            }
            Label_1171: {
                if (this.proto.hasDefaultValue()) {
                    if (this.isRepeated()) {
                        throw new DescriptorValidationException((GenericDescriptor)this, "Repeated fields cannot have default values.");
                    }
                    try {
                        switch (this.getType()) {
                            case INT32:
                            case SINT32:
                            case SFIXED32: {
                                this.defaultValue = TextFormat.parseInt32(this.proto.getDefaultValue());
                                break;
                            }
                            case UINT32:
                            case FIXED32: {
                                this.defaultValue = TextFormat.parseUInt32(this.proto.getDefaultValue());
                                break;
                            }
                            case INT64:
                            case SINT64:
                            case SFIXED64: {
                                this.defaultValue = TextFormat.parseInt64(this.proto.getDefaultValue());
                                break;
                            }
                            case UINT64:
                            case FIXED64: {
                                this.defaultValue = TextFormat.parseUInt64(this.proto.getDefaultValue());
                                break;
                            }
                            case FLOAT: {
                                if (this.proto.getDefaultValue().equals("inf")) {
                                    this.defaultValue = Float.POSITIVE_INFINITY;
                                    break;
                                }
                                if (this.proto.getDefaultValue().equals("-inf")) {
                                    this.defaultValue = Float.NEGATIVE_INFINITY;
                                    break;
                                }
                                if (this.proto.getDefaultValue().equals("nan")) {
                                    this.defaultValue = Float.NaN;
                                    break;
                                }
                                this.defaultValue = Float.valueOf(this.proto.getDefaultValue());
                                break;
                            }
                            case DOUBLE: {
                                if (this.proto.getDefaultValue().equals("inf")) {
                                    this.defaultValue = Double.POSITIVE_INFINITY;
                                    break;
                                }
                                if (this.proto.getDefaultValue().equals("-inf")) {
                                    this.defaultValue = Double.NEGATIVE_INFINITY;
                                    break;
                                }
                                if (this.proto.getDefaultValue().equals("nan")) {
                                    this.defaultValue = Double.NaN;
                                    break;
                                }
                                this.defaultValue = Double.valueOf(this.proto.getDefaultValue());
                                break;
                            }
                            case BOOL: {
                                this.defaultValue = Boolean.valueOf(this.proto.getDefaultValue());
                                break;
                            }
                            case STRING: {
                                this.defaultValue = this.proto.getDefaultValue();
                                break;
                            }
                            case BYTES: {
                                try {
                                    this.defaultValue = TextFormat.unescapeBytes(this.proto.getDefaultValue());
                                    break;
                                }
                                catch (TextFormat.InvalidEscapeSequenceException e) {
                                    throw new DescriptorValidationException((GenericDescriptor)this, "Couldn't parse default value: " + e.getMessage(), (Throwable)e);
                                }
                            }
                            case ENUM: {
                                this.defaultValue = this.enumType.findValueByName(this.proto.getDefaultValue());
                                if (this.defaultValue == null) {
                                    throw new DescriptorValidationException((GenericDescriptor)this, "Unknown enum default value: \"" + this.proto.getDefaultValue() + '\"');
                                }
                                break;
                            }
                            case MESSAGE:
                            case GROUP: {
                                throw new DescriptorValidationException((GenericDescriptor)this, "Message type had default value.");
                            }
                        }
                        break Label_1171;
                    }
                    catch (NumberFormatException e2) {
                        throw new DescriptorValidationException((GenericDescriptor)this, "Could not parse default value: \"" + this.proto.getDefaultValue() + '\"', (Throwable)e2);
                    }
                }
                if (this.isRepeated()) {
                    this.defaultValue = Collections.emptyList();
                }
                else {
                    switch (this.getJavaType()) {
                        case ENUM: {
                            this.defaultValue = this.enumType.getValues().get(0);
                            break;
                        }
                        case MESSAGE: {
                            this.defaultValue = null;
                            break;
                        }
                        default: {
                            this.defaultValue = this.getJavaType().defaultDefault;
                            break;
                        }
                    }
                }
            }
            if (!this.isExtension()) {
                this.file.pool.addFieldByNumber(this);
            }
            if (this.containingType != null && this.containingType.getOptions().getMessageSetWireFormat()) {
                if (!this.isExtension()) {
                    throw new DescriptorValidationException((GenericDescriptor)this, "MessageSets cannot have fields, only extensions.");
                }
                if (!this.isOptional() || this.getType() != Type.MESSAGE) {
                    throw new DescriptorValidationException((GenericDescriptor)this, "Extensions of MessageSets must be optional messages.");
                }
            }
        }
        
        private void setProto(final DescriptorProtos.FieldDescriptorProto proto) {
            this.proto = proto;
        }
        
        public MessageLite.Builder internalMergeFrom(final MessageLite.Builder to, final MessageLite from) {
            return ((Message.Builder)to).mergeFrom((Message)from);
        }
        
        static {
            table = WireFormat.FieldType.values();
            if (Type.values().length != DescriptorProtos.FieldDescriptorProto.Type.values().length) {
                throw new RuntimeException("descriptor.proto has a new declared type but Desrciptors.java wasn't updated.");
            }
        }
        
        public enum Type
        {
            DOUBLE(JavaType.DOUBLE), 
            FLOAT(JavaType.FLOAT), 
            INT64(JavaType.LONG), 
            UINT64(JavaType.LONG), 
            INT32(JavaType.INT), 
            FIXED64(JavaType.LONG), 
            FIXED32(JavaType.INT), 
            BOOL(JavaType.BOOLEAN), 
            STRING(JavaType.STRING), 
            GROUP(JavaType.MESSAGE), 
            MESSAGE(JavaType.MESSAGE), 
            BYTES(JavaType.BYTE_STRING), 
            UINT32(JavaType.INT), 
            ENUM(JavaType.ENUM), 
            SFIXED32(JavaType.INT), 
            SFIXED64(JavaType.LONG), 
            SINT32(JavaType.INT), 
            SINT64(JavaType.LONG);
            
            private JavaType javaType;
            
            private Type(final JavaType javaType) {
                this.javaType = javaType;
            }
            
            public DescriptorProtos.FieldDescriptorProto.Type toProto() {
                return DescriptorProtos.FieldDescriptorProto.Type.valueOf(this.ordinal() + 1);
            }
            
            public JavaType getJavaType() {
                return this.javaType;
            }
            
            public static Type valueOf(final DescriptorProtos.FieldDescriptorProto.Type type) {
                return values()[type.getNumber() - 1];
            }
        }
        
        public enum JavaType
        {
            INT((Object)0), 
            LONG((Object)0L), 
            FLOAT((Object)0.0f), 
            DOUBLE((Object)0.0), 
            BOOLEAN((Object)false), 
            STRING((Object)""), 
            BYTE_STRING((Object)ByteString.EMPTY), 
            ENUM((Object)null), 
            MESSAGE((Object)null);
            
            private final Object defaultDefault;
            
            private JavaType(final Object defaultDefault) {
                this.defaultDefault = defaultDefault;
            }
        }
    }
    
    public static final class EnumDescriptor implements GenericDescriptor, Internal.EnumLiteMap<EnumValueDescriptor>
    {
        private final int index;
        private DescriptorProtos.EnumDescriptorProto proto;
        private final String fullName;
        private final FileDescriptor file;
        private final Descriptor containingType;
        private EnumValueDescriptor[] values;
        
        public int getIndex() {
            return this.index;
        }
        
        public DescriptorProtos.EnumDescriptorProto toProto() {
            return this.proto;
        }
        
        public String getName() {
            return this.proto.getName();
        }
        
        public String getFullName() {
            return this.fullName;
        }
        
        public FileDescriptor getFile() {
            return this.file;
        }
        
        public Descriptor getContainingType() {
            return this.containingType;
        }
        
        public DescriptorProtos.EnumOptions getOptions() {
            return this.proto.getOptions();
        }
        
        public List<EnumValueDescriptor> getValues() {
            return Collections.unmodifiableList((List<? extends EnumValueDescriptor>)Arrays.asList((T[])this.values));
        }
        
        public EnumValueDescriptor findValueByName(final String name) {
            final GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
            if (result != null && result instanceof EnumValueDescriptor) {
                return (EnumValueDescriptor)result;
            }
            return null;
        }
        
        public EnumValueDescriptor findValueByNumber(final int number) {
            return this.file.pool.enumValuesByNumber.get(new DescriptorPool.DescriptorIntPair(this, number));
        }
        
        private EnumDescriptor(final DescriptorProtos.EnumDescriptorProto proto, final FileDescriptor file, final Descriptor parent, final int index) throws DescriptorValidationException {
            this.index = index;
            this.proto = proto;
            this.fullName = computeFullName(file, parent, proto.getName());
            this.file = file;
            this.containingType = parent;
            if (proto.getValueCount() == 0) {
                throw new DescriptorValidationException((GenericDescriptor)this, "Enums must contain at least one value.");
            }
            this.values = new EnumValueDescriptor[proto.getValueCount()];
            for (int i = 0; i < proto.getValueCount(); ++i) {
                this.values[i] = new EnumValueDescriptor(proto.getValue(i), file, this, i);
            }
            file.pool.addSymbol(this);
        }
        
        private void setProto(final DescriptorProtos.EnumDescriptorProto proto) {
            this.proto = proto;
            for (int i = 0; i < this.values.length; ++i) {
                this.values[i].setProto(proto.getValue(i));
            }
        }
    }
    
    public static final class EnumValueDescriptor implements GenericDescriptor, Internal.EnumLite
    {
        private final int index;
        private DescriptorProtos.EnumValueDescriptorProto proto;
        private final String fullName;
        private final FileDescriptor file;
        private final EnumDescriptor type;
        
        public int getIndex() {
            return this.index;
        }
        
        public DescriptorProtos.EnumValueDescriptorProto toProto() {
            return this.proto;
        }
        
        public String getName() {
            return this.proto.getName();
        }
        
        public int getNumber() {
            return this.proto.getNumber();
        }
        
        public String getFullName() {
            return this.fullName;
        }
        
        public FileDescriptor getFile() {
            return this.file;
        }
        
        public EnumDescriptor getType() {
            return this.type;
        }
        
        public DescriptorProtos.EnumValueOptions getOptions() {
            return this.proto.getOptions();
        }
        
        private EnumValueDescriptor(final DescriptorProtos.EnumValueDescriptorProto proto, final FileDescriptor file, final EnumDescriptor parent, final int index) throws DescriptorValidationException {
            this.index = index;
            this.proto = proto;
            this.file = file;
            this.type = parent;
            this.fullName = parent.getFullName() + '.' + proto.getName();
            file.pool.addSymbol(this);
            file.pool.addEnumValueByNumber(this);
        }
        
        private void setProto(final DescriptorProtos.EnumValueDescriptorProto proto) {
            this.proto = proto;
        }
    }
    
    public static final class ServiceDescriptor implements GenericDescriptor
    {
        private final int index;
        private DescriptorProtos.ServiceDescriptorProto proto;
        private final String fullName;
        private final FileDescriptor file;
        private MethodDescriptor[] methods;
        
        public int getIndex() {
            return this.index;
        }
        
        public DescriptorProtos.ServiceDescriptorProto toProto() {
            return this.proto;
        }
        
        public String getName() {
            return this.proto.getName();
        }
        
        public String getFullName() {
            return this.fullName;
        }
        
        public FileDescriptor getFile() {
            return this.file;
        }
        
        public DescriptorProtos.ServiceOptions getOptions() {
            return this.proto.getOptions();
        }
        
        public List<MethodDescriptor> getMethods() {
            return Collections.unmodifiableList((List<? extends MethodDescriptor>)Arrays.asList((T[])this.methods));
        }
        
        public MethodDescriptor findMethodByName(final String name) {
            final GenericDescriptor result = this.file.pool.findSymbol(this.fullName + '.' + name);
            if (result != null && result instanceof MethodDescriptor) {
                return (MethodDescriptor)result;
            }
            return null;
        }
        
        private ServiceDescriptor(final DescriptorProtos.ServiceDescriptorProto proto, final FileDescriptor file, final int index) throws DescriptorValidationException {
            this.index = index;
            this.proto = proto;
            this.fullName = computeFullName(file, null, proto.getName());
            this.file = file;
            this.methods = new MethodDescriptor[proto.getMethodCount()];
            for (int i = 0; i < proto.getMethodCount(); ++i) {
                this.methods[i] = new MethodDescriptor(proto.getMethod(i), file, this, i);
            }
            file.pool.addSymbol(this);
        }
        
        private void crossLink() throws DescriptorValidationException {
            for (final MethodDescriptor method : this.methods) {
                method.crossLink();
            }
        }
        
        private void setProto(final DescriptorProtos.ServiceDescriptorProto proto) {
            this.proto = proto;
            for (int i = 0; i < this.methods.length; ++i) {
                this.methods[i].setProto(proto.getMethod(i));
            }
        }
    }
    
    public static final class MethodDescriptor implements GenericDescriptor
    {
        private final int index;
        private DescriptorProtos.MethodDescriptorProto proto;
        private final String fullName;
        private final FileDescriptor file;
        private final ServiceDescriptor service;
        private Descriptor inputType;
        private Descriptor outputType;
        
        public int getIndex() {
            return this.index;
        }
        
        public DescriptorProtos.MethodDescriptorProto toProto() {
            return this.proto;
        }
        
        public String getName() {
            return this.proto.getName();
        }
        
        public String getFullName() {
            return this.fullName;
        }
        
        public FileDescriptor getFile() {
            return this.file;
        }
        
        public ServiceDescriptor getService() {
            return this.service;
        }
        
        public Descriptor getInputType() {
            return this.inputType;
        }
        
        public Descriptor getOutputType() {
            return this.outputType;
        }
        
        public DescriptorProtos.MethodOptions getOptions() {
            return this.proto.getOptions();
        }
        
        private MethodDescriptor(final DescriptorProtos.MethodDescriptorProto proto, final FileDescriptor file, final ServiceDescriptor parent, final int index) throws DescriptorValidationException {
            this.index = index;
            this.proto = proto;
            this.file = file;
            this.service = parent;
            this.fullName = parent.getFullName() + '.' + proto.getName();
            file.pool.addSymbol(this);
        }
        
        private void crossLink() throws DescriptorValidationException {
            final GenericDescriptor input = this.file.pool.lookupSymbol(this.proto.getInputType(), this, DescriptorPool.SearchFilter.TYPES_ONLY);
            if (!(input instanceof Descriptor)) {
                throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.proto.getInputType() + "\" is not a message type.");
            }
            this.inputType = (Descriptor)input;
            final GenericDescriptor output = this.file.pool.lookupSymbol(this.proto.getOutputType(), this, DescriptorPool.SearchFilter.TYPES_ONLY);
            if (!(output instanceof Descriptor)) {
                throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.proto.getOutputType() + "\" is not a message type.");
            }
            this.outputType = (Descriptor)output;
        }
        
        private void setProto(final DescriptorProtos.MethodDescriptorProto proto) {
            this.proto = proto;
        }
    }
    
    public static class DescriptorValidationException extends Exception
    {
        private static final long serialVersionUID = 5750205775490483148L;
        private final String name;
        private final Message proto;
        private final String description;
        
        public String getProblemSymbolName() {
            return this.name;
        }
        
        public Message getProblemProto() {
            return this.proto;
        }
        
        public String getDescription() {
            return this.description;
        }
        
        private DescriptorValidationException(final GenericDescriptor problemDescriptor, final String description) {
            super(problemDescriptor.getFullName() + ": " + description);
            this.name = problemDescriptor.getFullName();
            this.proto = problemDescriptor.toProto();
            this.description = description;
        }
        
        private DescriptorValidationException(final GenericDescriptor problemDescriptor, final String description, final Throwable cause) {
            this(problemDescriptor, description);
            this.initCause(cause);
        }
        
        private DescriptorValidationException(final FileDescriptor problemDescriptor, final String description) {
            super(problemDescriptor.getName() + ": " + description);
            this.name = problemDescriptor.getName();
            this.proto = problemDescriptor.toProto();
            this.description = description;
        }
    }
    
    private static final class DescriptorPool
    {
        private final Set<FileDescriptor> dependencies;
        private final Map<String, GenericDescriptor> descriptorsByName;
        private final Map<DescriptorIntPair, FieldDescriptor> fieldsByNumber;
        private final Map<DescriptorIntPair, EnumValueDescriptor> enumValuesByNumber;
        
        DescriptorPool(final FileDescriptor[] dependencies) {
            this.descriptorsByName = new HashMap<String, GenericDescriptor>();
            this.fieldsByNumber = new HashMap<DescriptorIntPair, FieldDescriptor>();
            this.enumValuesByNumber = new HashMap<DescriptorIntPair, EnumValueDescriptor>();
            this.dependencies = new HashSet<FileDescriptor>();
            for (int i = 0; i < dependencies.length; ++i) {
                this.dependencies.add(dependencies[i]);
                this.importPublicDependencies(dependencies[i]);
            }
            for (final FileDescriptor dependency : this.dependencies) {
                try {
                    this.addPackage(dependency.getPackage(), dependency);
                }
                catch (DescriptorValidationException e) {
                    assert false;
                    continue;
                }
            }
        }
        
        private void importPublicDependencies(final FileDescriptor file) {
            for (final FileDescriptor dependency : file.getPublicDependencies()) {
                if (this.dependencies.add(dependency)) {
                    this.importPublicDependencies(dependency);
                }
            }
        }
        
        GenericDescriptor findSymbol(final String fullName) {
            return this.findSymbol(fullName, SearchFilter.ALL_SYMBOLS);
        }
        
        GenericDescriptor findSymbol(final String fullName, final SearchFilter filter) {
            GenericDescriptor result = this.descriptorsByName.get(fullName);
            if (result != null && (filter == SearchFilter.ALL_SYMBOLS || (filter == SearchFilter.TYPES_ONLY && this.isType(result)) || (filter == SearchFilter.AGGREGATES_ONLY && this.isAggregate(result)))) {
                return result;
            }
            for (final FileDescriptor dependency : this.dependencies) {
                result = dependency.pool.descriptorsByName.get(fullName);
                if (result != null && (filter == SearchFilter.ALL_SYMBOLS || (filter == SearchFilter.TYPES_ONLY && this.isType(result)) || (filter == SearchFilter.AGGREGATES_ONLY && this.isAggregate(result)))) {
                    return result;
                }
            }
            return null;
        }
        
        boolean isType(final GenericDescriptor descriptor) {
            return descriptor instanceof Descriptor || descriptor instanceof EnumDescriptor;
        }
        
        boolean isAggregate(final GenericDescriptor descriptor) {
            return descriptor instanceof Descriptor || descriptor instanceof EnumDescriptor || descriptor instanceof PackageDescriptor || descriptor instanceof ServiceDescriptor;
        }
        
        GenericDescriptor lookupSymbol(final String name, final GenericDescriptor relativeTo, final SearchFilter filter) throws DescriptorValidationException {
            GenericDescriptor result;
            if (name.startsWith(".")) {
                result = this.findSymbol(name.substring(1), filter);
            }
            else {
                final int firstPartLength = name.indexOf(46);
                String firstPart;
                if (firstPartLength == -1) {
                    firstPart = name;
                }
                else {
                    firstPart = name.substring(0, firstPartLength);
                }
                final StringBuilder scopeToTry = new StringBuilder(relativeTo.getFullName());
                while (true) {
                    final int dotpos = scopeToTry.lastIndexOf(".");
                    if (dotpos == -1) {
                        result = this.findSymbol(name, filter);
                        break;
                    }
                    scopeToTry.setLength(dotpos + 1);
                    scopeToTry.append(firstPart);
                    result = this.findSymbol(scopeToTry.toString(), SearchFilter.AGGREGATES_ONLY);
                    if (result != null) {
                        if (firstPartLength != -1) {
                            scopeToTry.setLength(dotpos + 1);
                            scopeToTry.append(name);
                            result = this.findSymbol(scopeToTry.toString(), filter);
                            break;
                        }
                        break;
                    }
                    else {
                        scopeToTry.setLength(dotpos);
                    }
                }
            }
            if (result == null) {
                throw new DescriptorValidationException(relativeTo, '\"' + name + "\" is not defined.");
            }
            return result;
        }
        
        void addSymbol(final GenericDescriptor descriptor) throws DescriptorValidationException {
            validateSymbolName(descriptor);
            final String fullName = descriptor.getFullName();
            final int dotpos = fullName.lastIndexOf(46);
            final GenericDescriptor old = this.descriptorsByName.put(fullName, descriptor);
            if (old == null) {
                return;
            }
            this.descriptorsByName.put(fullName, old);
            if (descriptor.getFile() != old.getFile()) {
                throw new DescriptorValidationException(descriptor, '\"' + fullName + "\" is already defined in file \"" + old.getFile().getName() + "\".");
            }
            if (dotpos == -1) {
                throw new DescriptorValidationException(descriptor, '\"' + fullName + "\" is already defined.");
            }
            throw new DescriptorValidationException(descriptor, '\"' + fullName.substring(dotpos + 1) + "\" is already defined in \"" + fullName.substring(0, dotpos) + "\".");
        }
        
        void addPackage(final String fullName, final FileDescriptor file) throws DescriptorValidationException {
            final int dotpos = fullName.lastIndexOf(46);
            String name;
            if (dotpos == -1) {
                name = fullName;
            }
            else {
                this.addPackage(fullName.substring(0, dotpos), file);
                name = fullName.substring(dotpos + 1);
            }
            final GenericDescriptor old = this.descriptorsByName.put(fullName, new PackageDescriptor(name, fullName, file));
            if (old != null) {
                this.descriptorsByName.put(fullName, old);
                if (!(old instanceof PackageDescriptor)) {
                    throw new DescriptorValidationException(file, '\"' + name + "\" is already defined (as something other than a " + "package) in file \"" + old.getFile().getName() + "\".");
                }
            }
        }
        
        void addFieldByNumber(final FieldDescriptor field) throws DescriptorValidationException {
            final DescriptorIntPair key = new DescriptorIntPair(field.getContainingType(), field.getNumber());
            final FieldDescriptor old = this.fieldsByNumber.put(key, field);
            if (old != null) {
                this.fieldsByNumber.put(key, old);
                throw new DescriptorValidationException((GenericDescriptor)field, "Field number " + field.getNumber() + "has already been used in \"" + field.getContainingType().getFullName() + "\" by field \"" + old.getName() + "\".");
            }
        }
        
        void addEnumValueByNumber(final EnumValueDescriptor value) {
            final DescriptorIntPair key = new DescriptorIntPair(value.getType(), value.getNumber());
            final EnumValueDescriptor old = this.enumValuesByNumber.put(key, value);
            if (old != null) {
                this.enumValuesByNumber.put(key, old);
            }
        }
        
        static void validateSymbolName(final GenericDescriptor descriptor) throws DescriptorValidationException {
            final String name = descriptor.getName();
            if (name.length() == 0) {
                throw new DescriptorValidationException(descriptor, "Missing name.");
            }
            boolean valid = true;
            for (int i = 0; i < name.length(); ++i) {
                final char c = name.charAt(i);
                if (c >= '\u0080') {
                    valid = false;
                }
                if (!Character.isLetter(c) && c != '_') {
                    if (!Character.isDigit(c) || i <= 0) {
                        valid = false;
                    }
                }
            }
            if (!valid) {
                throw new DescriptorValidationException(descriptor, '\"' + name + "\" is not a valid identifier.");
            }
        }
        
        enum SearchFilter
        {
            TYPES_ONLY, 
            AGGREGATES_ONLY, 
            ALL_SYMBOLS;
        }
        
        private static final class PackageDescriptor implements GenericDescriptor
        {
            private final String name;
            private final String fullName;
            private final FileDescriptor file;
            
            public Message toProto() {
                return this.file.toProto();
            }
            
            public String getName() {
                return this.name;
            }
            
            public String getFullName() {
                return this.fullName;
            }
            
            public FileDescriptor getFile() {
                return this.file;
            }
            
            PackageDescriptor(final String name, final String fullName, final FileDescriptor file) {
                this.file = file;
                this.fullName = fullName;
                this.name = name;
            }
        }
        
        private static final class DescriptorIntPair
        {
            private final GenericDescriptor descriptor;
            private final int number;
            
            DescriptorIntPair(final GenericDescriptor descriptor, final int number) {
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
    
    private interface GenericDescriptor
    {
        Message toProto();
        
        String getName();
        
        String getFullName();
        
        FileDescriptor getFile();
    }
}
