// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.Collection;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.util.Collections;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DescriptorProtos
{
    private static Descriptors.Descriptor internal_static_google_protobuf_FileDescriptorSet_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_FileDescriptorSet_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_FileDescriptorProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_FileDescriptorProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_DescriptorProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_DescriptorProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_DescriptorProto_ExtensionRange_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_DescriptorProto_ExtensionRange_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_FieldDescriptorProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_FieldDescriptorProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_EnumDescriptorProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_EnumDescriptorProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_EnumValueDescriptorProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_EnumValueDescriptorProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_ServiceDescriptorProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_ServiceDescriptorProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_MethodDescriptorProto_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_MethodDescriptorProto_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_FileOptions_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_FileOptions_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_MessageOptions_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_MessageOptions_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_FieldOptions_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_FieldOptions_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_EnumOptions_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_EnumOptions_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_EnumValueOptions_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_EnumValueOptions_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_ServiceOptions_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_ServiceOptions_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_MethodOptions_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_MethodOptions_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_UninterpretedOption_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_UninterpretedOption_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_UninterpretedOption_NamePart_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_UninterpretedOption_NamePart_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_SourceCodeInfo_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_SourceCodeInfo_fieldAccessorTable;
    private static Descriptors.Descriptor internal_static_google_protobuf_SourceCodeInfo_Location_descriptor;
    private static GeneratedMessage.FieldAccessorTable internal_static_google_protobuf_SourceCodeInfo_Location_fieldAccessorTable;
    private static Descriptors.FileDescriptor descriptor;
    
    private DescriptorProtos() {
    }
    
    public static void registerAllExtensions(final ExtensionRegistry registry) {
    }
    
    public static Descriptors.FileDescriptor getDescriptor() {
        return DescriptorProtos.descriptor;
    }
    
    static {
        final String[] descriptorData = { "\n google/protobuf/descriptor.proto\u0012\u000fgoogle.protobuf\"G\n\u0011FileDescriptorSet\u00122\n\u0004file\u0018\u0001 \u0003(\u000b2$.google.protobuf.FileDescriptorProto\"\u00cb\u0003\n\u0013FileDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u0012\u000f\n\u0007package\u0018\u0002 \u0001(\t\u0012\u0012\n\ndependency\u0018\u0003 \u0003(\t\u0012\u0019\n\u0011public_dependency\u0018\n \u0003(\u0005\u0012\u0017\n\u000fweak_dependency\u0018\u000b \u0003(\u0005\u00126\n\fmessage_type\u0018\u0004 \u0003(\u000b2 .google.protobuf.DescriptorProto\u00127\n\tenum_type\u0018\u0005 \u0003(\u000b2$.google.protobuf.EnumDescriptorProto\u00128\n\u0007service\u0018\u0006 \u0003(\u000b2'.google.protobuf.", "ServiceDescriptorProto\u00128\n\textension\u0018\u0007 \u0003(\u000b2%.google.protobuf.FieldDescriptorProto\u0012-\n\u0007options\u0018\b \u0001(\u000b2\u001c.google.protobuf.FileOptions\u00129\n\u0010source_code_info\u0018\t \u0001(\u000b2\u001f.google.protobuf.SourceCodeInfo\"©\u0003\n\u000fDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u00124\n\u0005field\u0018\u0002 \u0003(\u000b2%.google.protobuf.FieldDescriptorProto\u00128\n\textension\u0018\u0006 \u0003(\u000b2%.google.protobuf.FieldDescriptorProto\u00125\n\u000bnested_type\u0018\u0003 \u0003(\u000b2 .google.protobuf.DescriptorProto\u00127\n\tenum_type", "\u0018\u0004 \u0003(\u000b2$.google.protobuf.EnumDescriptorProto\u0012H\n\u000fextension_range\u0018\u0005 \u0003(\u000b2/.google.protobuf.DescriptorProto.ExtensionRange\u00120\n\u0007options\u0018\u0007 \u0001(\u000b2\u001f.google.protobuf.MessageOptions\u001a,\n\u000eExtensionRange\u0012\r\n\u0005start\u0018\u0001 \u0001(\u0005\u0012\u000b\n\u0003end\u0018\u0002 \u0001(\u0005\"\u0094\u0005\n\u0014FieldDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u0012\u000e\n\u0006number\u0018\u0003 \u0001(\u0005\u0012:\n\u0005label\u0018\u0004 \u0001(\u000e2+.google.protobuf.FieldDescriptorProto.Label\u00128\n\u0004type\u0018\u0005 \u0001(\u000e2*.google.protobuf.FieldDescriptorProto.Type\u0012\u0011\n\ttype_name", "\u0018\u0006 \u0001(\t\u0012\u0010\n\bextendee\u0018\u0002 \u0001(\t\u0012\u0015\n\rdefault_value\u0018\u0007 \u0001(\t\u0012.\n\u0007options\u0018\b \u0001(\u000b2\u001d.google.protobuf.FieldOptions\"¶\u0002\n\u0004Type\u0012\u000f\n\u000bTYPE_DOUBLE\u0010\u0001\u0012\u000e\n\nTYPE_FLOAT\u0010\u0002\u0012\u000e\n\nTYPE_INT64\u0010\u0003\u0012\u000f\n\u000bTYPE_UINT64\u0010\u0004\u0012\u000e\n\nTYPE_INT32\u0010\u0005\u0012\u0010\n\fTYPE_FIXED64\u0010\u0006\u0012\u0010\n\fTYPE_FIXED32\u0010\u0007\u0012\r\n\tTYPE_BOOL\u0010\b\u0012\u000f\n\u000bTYPE_STRING\u0010\t\u0012\u000e\n\nTYPE_GROUP\u0010\n\u0012\u0010\n\fTYPE_MESSAGE\u0010\u000b\u0012\u000e\n\nTYPE_BYTES\u0010\f\u0012\u000f\n\u000bTYPE_UINT32\u0010\r\u0012\r\n\tTYPE_ENUM\u0010\u000e\u0012\u0011\n\rTYPE_SFIXED32\u0010\u000f\u0012\u0011\n\rTYPE_SFIXED64\u0010\u0010\u0012\u000f\n\u000bTYPE_SINT32\u0010\u0011\u0012\u000f\n\u000bTYPE_", "SINT64\u0010\u0012\"C\n\u0005Label\u0012\u0012\n\u000eLABEL_OPTIONAL\u0010\u0001\u0012\u0012\n\u000eLABEL_REQUIRED\u0010\u0002\u0012\u0012\n\u000eLABEL_REPEATED\u0010\u0003\"\u008c\u0001\n\u0013EnumDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u00128\n\u0005value\u0018\u0002 \u0003(\u000b2).google.protobuf.EnumValueDescriptorProto\u0012-\n\u0007options\u0018\u0003 \u0001(\u000b2\u001c.google.protobuf.EnumOptions\"l\n\u0018EnumValueDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u0012\u000e\n\u0006number\u0018\u0002 \u0001(\u0005\u00122\n\u0007options\u0018\u0003 \u0001(\u000b2!.google.protobuf.EnumValueOptions\"\u0090\u0001\n\u0016ServiceDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u00126\n\u0006method\u0018\u0002 \u0003(\u000b2&.google.pro", "tobuf.MethodDescriptorProto\u00120\n\u0007options\u0018\u0003 \u0001(\u000b2\u001f.google.protobuf.ServiceOptions\"\u007f\n\u0015MethodDescriptorProto\u0012\f\n\u0004name\u0018\u0001 \u0001(\t\u0012\u0012\n\ninput_type\u0018\u0002 \u0001(\t\u0012\u0013\n\u000boutput_type\u0018\u0003 \u0001(\t\u0012/\n\u0007options\u0018\u0004 \u0001(\u000b2\u001e.google.protobuf.MethodOptions\"\u00e9\u0003\n\u000bFileOptions\u0012\u0014\n\fjava_package\u0018\u0001 \u0001(\t\u0012\u001c\n\u0014java_outer_classname\u0018\b \u0001(\t\u0012\"\n\u0013java_multiple_files\u0018\n \u0001(\b:\u0005false\u0012,\n\u001djava_generate_equals_and_hash\u0018\u0014 \u0001(\b:\u0005false\u0012F\n\foptimize_for\u0018\t \u0001(\u000e2).google.protobuf.Fil", "eOptions.OptimizeMode:\u0005SPEED\u0012\u0012\n\ngo_package\u0018\u000b \u0001(\t\u0012\"\n\u0013cc_generic_services\u0018\u0010 \u0001(\b:\u0005false\u0012$\n\u0015java_generic_services\u0018\u0011 \u0001(\b:\u0005false\u0012\"\n\u0013py_generic_services\u0018\u0012 \u0001(\b:\u0005false\u0012C\n\u0014uninterpreted_option\u0018\u00e7\u0007 \u0003(\u000b2$.google.protobuf.UninterpretedOption\":\n\fOptimizeMode\u0012\t\n\u0005SPEED\u0010\u0001\u0012\r\n\tCODE_SIZE\u0010\u0002\u0012\u0010\n\fLITE_RUNTIME\u0010\u0003*\t\b\u00e8\u0007\u0010\u0080\u0080\u0080\u0080\u0002\"¸\u0001\n\u000eMessageOptions\u0012&\n\u0017message_set_wire_format\u0018\u0001 \u0001(\b:\u0005false\u0012.\n\u001fno_standard_descriptor_accessor\u0018\u0002 \u0001(\b:\u0005", "false\u0012C\n\u0014uninterpreted_option\u0018\u00e7\u0007 \u0003(\u000b2$.google.protobuf.UninterpretedOption*\t\b\u00e8\u0007\u0010\u0080\u0080\u0080\u0080\u0002\"¾\u0002\n\fFieldOptions\u0012:\n\u0005ctype\u0018\u0001 \u0001(\u000e2#.google.protobuf.FieldOptions.CType:\u0006STRING\u0012\u000e\n\u0006packed\u0018\u0002 \u0001(\b\u0012\u0013\n\u0004lazy\u0018\u0005 \u0001(\b:\u0005false\u0012\u0019\n\ndeprecated\u0018\u0003 \u0001(\b:\u0005false\u0012\u001c\n\u0014experimental_map_key\u0018\t \u0001(\t\u0012\u0013\n\u0004weak\u0018\n \u0001(\b:\u0005false\u0012C\n\u0014uninterpreted_option\u0018\u00e7\u0007 \u0003(\u000b2$.google.protobuf.UninterpretedOption\"/\n\u0005CType\u0012\n\n\u0006STRING\u0010\u0000\u0012\b\n\u0004CORD\u0010\u0001\u0012\u0010\n\fSTRING_PIECE\u0010\u0002*\t\b\u00e8\u0007", "\u0010\u0080\u0080\u0080\u0080\u0002\"x\n\u000bEnumOptions\u0012\u0019\n\u000ballow_alias\u0018\u0002 \u0001(\b:\u0004true\u0012C\n\u0014uninterpreted_option\u0018\u00e7\u0007 \u0003(\u000b2$.google.protobuf.UninterpretedOption*\t\b\u00e8\u0007\u0010\u0080\u0080\u0080\u0080\u0002\"b\n\u0010EnumValueOptions\u0012C\n\u0014uninterpreted_option\u0018\u00e7\u0007 \u0003(\u000b2$.google.protobuf.UninterpretedOption*\t\b\u00e8\u0007\u0010\u0080\u0080\u0080\u0080\u0002\"`\n\u000eServiceOptions\u0012C\n\u0014uninterpreted_option\u0018\u00e7\u0007 \u0003(\u000b2$.google.protobuf.UninterpretedOption*\t\b\u00e8\u0007\u0010\u0080\u0080\u0080\u0080\u0002\"_\n\rMethodOptions\u0012C\n\u0014uninterpreted_option\u0018\u00e7\u0007 \u0003(\u000b2$.google.protobuf.Uninter", "pretedOption*\t\b\u00e8\u0007\u0010\u0080\u0080\u0080\u0080\u0002\"\u009e\u0002\n\u0013UninterpretedOption\u0012;\n\u0004name\u0018\u0002 \u0003(\u000b2-.google.protobuf.UninterpretedOption.NamePart\u0012\u0018\n\u0010identifier_value\u0018\u0003 \u0001(\t\u0012\u001a\n\u0012positive_int_value\u0018\u0004 \u0001(\u0004\u0012\u001a\n\u0012negative_int_value\u0018\u0005 \u0001(\u0003\u0012\u0014\n\fdouble_value\u0018\u0006 \u0001(\u0001\u0012\u0014\n\fstring_value\u0018\u0007 \u0001(\f\u0012\u0017\n\u000faggregate_value\u0018\b \u0001(\t\u001a3\n\bNamePart\u0012\u0011\n\tname_part\u0018\u0001 \u0002(\t\u0012\u0014\n\fis_extension\u0018\u0002 \u0002(\b\"±\u0001\n\u000eSourceCodeInfo\u0012:\n\blocation\u0018\u0001 \u0003(\u000b2(.google.protobuf.SourceCodeInfo.Location\u001ac\n\bLocat", "ion\u0012\u0010\n\u0004path\u0018\u0001 \u0003(\u0005B\u0002\u0010\u0001\u0012\u0010\n\u0004span\u0018\u0002 \u0003(\u0005B\u0002\u0010\u0001\u0012\u0018\n\u0010leading_comments\u0018\u0003 \u0001(\t\u0012\u0019\n\u0011trailing_comments\u0018\u0004 \u0001(\tB)\n\u0013com.google.protobufB\u0010DescriptorProtosH\u0001" };
        final Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            public ExtensionRegistry assignDescriptors(final Descriptors.FileDescriptor root) {
                DescriptorProtos.descriptor = root;
                DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(0);
                DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_descriptor, new String[] { "File" });
                DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(1);
                DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_descriptor, new String[] { "Name", "Package", "Dependency", "PublicDependency", "WeakDependency", "MessageType", "EnumType", "Service", "Extension", "Options", "SourceCodeInfo" });
                DescriptorProtos.internal_static_google_protobuf_DescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(2);
                DescriptorProtos.internal_static_google_protobuf_DescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_DescriptorProto_descriptor, new String[] { "Name", "Field", "Extension", "NestedType", "EnumType", "ExtensionRange", "Options" });
                DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_descriptor = DescriptorProtos.internal_static_google_protobuf_DescriptorProto_descriptor.getNestedTypes().get(0);
                DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_descriptor, new String[] { "Start", "End" });
                DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(3);
                DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_descriptor, new String[] { "Name", "Number", "Label", "Type", "TypeName", "Extendee", "DefaultValue", "Options" });
                DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(4);
                DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_descriptor, new String[] { "Name", "Value", "Options" });
                DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(5);
                DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_descriptor, new String[] { "Name", "Number", "Options" });
                DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(6);
                DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_descriptor, new String[] { "Name", "Method", "Options" });
                DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(7);
                DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_descriptor, new String[] { "Name", "InputType", "OutputType", "Options" });
                DescriptorProtos.internal_static_google_protobuf_FileOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(8);
                DescriptorProtos.internal_static_google_protobuf_FileOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_FileOptions_descriptor, new String[] { "JavaPackage", "JavaOuterClassname", "JavaMultipleFiles", "JavaGenerateEqualsAndHash", "OptimizeFor", "GoPackage", "CcGenericServices", "JavaGenericServices", "PyGenericServices", "UninterpretedOption" });
                DescriptorProtos.internal_static_google_protobuf_MessageOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(9);
                DescriptorProtos.internal_static_google_protobuf_MessageOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_MessageOptions_descriptor, new String[] { "MessageSetWireFormat", "NoStandardDescriptorAccessor", "UninterpretedOption" });
                DescriptorProtos.internal_static_google_protobuf_FieldOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(10);
                DescriptorProtos.internal_static_google_protobuf_FieldOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_FieldOptions_descriptor, new String[] { "Ctype", "Packed", "Lazy", "Deprecated", "ExperimentalMapKey", "Weak", "UninterpretedOption" });
                DescriptorProtos.internal_static_google_protobuf_EnumOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(11);
                DescriptorProtos.internal_static_google_protobuf_EnumOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_EnumOptions_descriptor, new String[] { "AllowAlias", "UninterpretedOption" });
                DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(12);
                DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_descriptor, new String[] { "UninterpretedOption" });
                DescriptorProtos.internal_static_google_protobuf_ServiceOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(13);
                DescriptorProtos.internal_static_google_protobuf_ServiceOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_ServiceOptions_descriptor, new String[] { "UninterpretedOption" });
                DescriptorProtos.internal_static_google_protobuf_MethodOptions_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(14);
                DescriptorProtos.internal_static_google_protobuf_MethodOptions_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_MethodOptions_descriptor, new String[] { "UninterpretedOption" });
                DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(15);
                DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_descriptor, new String[] { "Name", "IdentifierValue", "PositiveIntValue", "NegativeIntValue", "DoubleValue", "StringValue", "AggregateValue" });
                DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_descriptor = DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_descriptor.getNestedTypes().get(0);
                DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_descriptor, new String[] { "NamePart", "IsExtension" });
                DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_descriptor = DescriptorProtos.getDescriptor().getMessageTypes().get(16);
                DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_descriptor, new String[] { "Location" });
                DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_descriptor = DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_descriptor.getNestedTypes().get(0);
                DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_fieldAccessorTable = new GeneratedMessage.FieldAccessorTable(DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_descriptor, new String[] { "Path", "Span", "LeadingComments", "TrailingComments" });
                return null;
            }
        };
        Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0], assigner);
    }
    
    public static final class FileDescriptorSet extends GeneratedMessage implements FileDescriptorSetOrBuilder
    {
        private static final FileDescriptorSet defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<FileDescriptorSet> PARSER;
        public static final int FILE_FIELD_NUMBER = 1;
        private List<FileDescriptorProto> file_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private FileDescriptorSet(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private FileDescriptorSet(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static FileDescriptorSet getDefaultInstance() {
            return FileDescriptorSet.defaultInstance;
        }
        
        public FileDescriptorSet getDefaultInstanceForType() {
            return FileDescriptorSet.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private FileDescriptorSet(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if ((mutable_bitField0_ & 0x1) != 0x1) {
                                this.file_ = new ArrayList<FileDescriptorProto>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.file_.add(input.readMessage(FileDescriptorProto.PARSER, extensionRegistry));
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x1) == 0x1) {
                    this.file_ = Collections.unmodifiableList((List<? extends FileDescriptorProto>)this.file_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_fieldAccessorTable.ensureFieldAccessorsInitialized(FileDescriptorSet.class, Builder.class);
        }
        
        @Override
        public Parser<FileDescriptorSet> getParserForType() {
            return FileDescriptorSet.PARSER;
        }
        
        public List<FileDescriptorProto> getFileList() {
            return this.file_;
        }
        
        public List<? extends FileDescriptorProtoOrBuilder> getFileOrBuilderList() {
            return this.file_;
        }
        
        public int getFileCount() {
            return this.file_.size();
        }
        
        public FileDescriptorProto getFile(final int index) {
            return this.file_.get(index);
        }
        
        public FileDescriptorProtoOrBuilder getFileOrBuilder(final int index) {
            return this.file_.get(index);
        }
        
        private void initFields() {
            this.file_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getFileCount(); ++i) {
                if (!this.getFile(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            for (int i = 0; i < this.file_.size(); ++i) {
                output.writeMessage(1, this.file_.get(i));
            }
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            for (int i = 0; i < this.file_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(1, this.file_.get(i));
            }
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static FileDescriptorSet parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return FileDescriptorSet.PARSER.parseFrom(data);
        }
        
        public static FileDescriptorSet parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FileDescriptorSet.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FileDescriptorSet parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return FileDescriptorSet.PARSER.parseFrom(data);
        }
        
        public static FileDescriptorSet parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FileDescriptorSet.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FileDescriptorSet parseFrom(final InputStream input) throws IOException {
            return FileDescriptorSet.PARSER.parseFrom(input);
        }
        
        public static FileDescriptorSet parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FileDescriptorSet.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static FileDescriptorSet parseDelimitedFrom(final InputStream input) throws IOException {
            return FileDescriptorSet.PARSER.parseDelimitedFrom(input);
        }
        
        public static FileDescriptorSet parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FileDescriptorSet.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static FileDescriptorSet parseFrom(final CodedInputStream input) throws IOException {
            return FileDescriptorSet.PARSER.parseFrom(input);
        }
        
        public static FileDescriptorSet parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FileDescriptorSet.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final FileDescriptorSet prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            FileDescriptorSet.PARSER = new AbstractParser<FileDescriptorSet>() {
                public FileDescriptorSet parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new FileDescriptorSet(input, extensionRegistry);
                }
            };
            (defaultInstance = new FileDescriptorSet(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements FileDescriptorSetOrBuilder
        {
            private int bitField0_;
            private List<FileDescriptorProto> file_;
            private RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> fileBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_fieldAccessorTable.ensureFieldAccessorsInitialized(FileDescriptorSet.class, Builder.class);
            }
            
            private Builder() {
                this.file_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.file_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getFileFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.fileBuilder_ == null) {
                    this.file_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.fileBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_FileDescriptorSet_descriptor;
            }
            
            public FileDescriptorSet getDefaultInstanceForType() {
                return FileDescriptorSet.getDefaultInstance();
            }
            
            public FileDescriptorSet build() {
                final FileDescriptorSet result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public FileDescriptorSet buildPartial() {
                final FileDescriptorSet result = new FileDescriptorSet((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if (this.fileBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.file_ = Collections.unmodifiableList((List<? extends FileDescriptorProto>)this.file_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.file_ = this.file_;
                }
                else {
                    result.file_ = this.fileBuilder_.build();
                }
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof FileDescriptorSet) {
                    return this.mergeFrom((FileDescriptorSet)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final FileDescriptorSet other) {
                if (other == FileDescriptorSet.getDefaultInstance()) {
                    return this;
                }
                if (this.fileBuilder_ == null) {
                    if (!other.file_.isEmpty()) {
                        if (this.file_.isEmpty()) {
                            this.file_ = other.file_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureFileIsMutable();
                            this.file_.addAll(other.file_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.file_.isEmpty()) {
                    if (this.fileBuilder_.isEmpty()) {
                        this.fileBuilder_.dispose();
                        this.fileBuilder_ = null;
                        this.file_ = other.file_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.fileBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getFileFieldBuilder() : null);
                    }
                    else {
                        this.fileBuilder_.addAllMessages(other.file_);
                    }
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getFileCount(); ++i) {
                    if (!this.getFile(i).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                FileDescriptorSet parsedMessage = null;
                try {
                    parsedMessage = FileDescriptorSet.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (FileDescriptorSet)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureFileIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.file_ = new ArrayList<FileDescriptorProto>(this.file_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            public List<FileDescriptorProto> getFileList() {
                if (this.fileBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends FileDescriptorProto>)this.file_);
                }
                return this.fileBuilder_.getMessageList();
            }
            
            public int getFileCount() {
                if (this.fileBuilder_ == null) {
                    return this.file_.size();
                }
                return this.fileBuilder_.getCount();
            }
            
            public FileDescriptorProto getFile(final int index) {
                if (this.fileBuilder_ == null) {
                    return this.file_.get(index);
                }
                return this.fileBuilder_.getMessage(index);
            }
            
            public Builder setFile(final int index, final FileDescriptorProto value) {
                if (this.fileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureFileIsMutable();
                    this.file_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.fileBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setFile(final int index, final FileDescriptorProto.Builder builderForValue) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    this.file_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.fileBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addFile(final FileDescriptorProto value) {
                if (this.fileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureFileIsMutable();
                    this.file_.add(value);
                    this.onChanged();
                }
                else {
                    this.fileBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addFile(final int index, final FileDescriptorProto value) {
                if (this.fileBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureFileIsMutable();
                    this.file_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.fileBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addFile(final FileDescriptorProto.Builder builderForValue) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    this.file_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.fileBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addFile(final int index, final FileDescriptorProto.Builder builderForValue) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    this.file_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.fileBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllFile(final Iterable<? extends FileDescriptorProto> values) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.file_);
                    this.onChanged();
                }
                else {
                    this.fileBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearFile() {
                if (this.fileBuilder_ == null) {
                    this.file_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.fileBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeFile(final int index) {
                if (this.fileBuilder_ == null) {
                    this.ensureFileIsMutable();
                    this.file_.remove(index);
                    this.onChanged();
                }
                else {
                    this.fileBuilder_.remove(index);
                }
                return this;
            }
            
            public FileDescriptorProto.Builder getFileBuilder(final int index) {
                return this.getFileFieldBuilder().getBuilder(index);
            }
            
            public FileDescriptorProtoOrBuilder getFileOrBuilder(final int index) {
                if (this.fileBuilder_ == null) {
                    return this.file_.get(index);
                }
                return this.fileBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends FileDescriptorProtoOrBuilder> getFileOrBuilderList() {
                if (this.fileBuilder_ != null) {
                    return this.fileBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends FileDescriptorProtoOrBuilder>)this.file_);
            }
            
            public FileDescriptorProto.Builder addFileBuilder() {
                return this.getFileFieldBuilder().addBuilder(FileDescriptorProto.getDefaultInstance());
            }
            
            public FileDescriptorProto.Builder addFileBuilder(final int index) {
                return this.getFileFieldBuilder().addBuilder(index, FileDescriptorProto.getDefaultInstance());
            }
            
            public List<FileDescriptorProto.Builder> getFileBuilderList() {
                return this.getFileFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder> getFileFieldBuilder() {
                if (this.fileBuilder_ == null) {
                    this.fileBuilder_ = new RepeatedFieldBuilder<FileDescriptorProto, FileDescriptorProto.Builder, FileDescriptorProtoOrBuilder>(this.file_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.file_ = null;
                }
                return this.fileBuilder_;
            }
        }
    }
    
    public static final class FileDescriptorProto extends GeneratedMessage implements FileDescriptorProtoOrBuilder
    {
        private static final FileDescriptorProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<FileDescriptorProto> PARSER;
        private int bitField0_;
        public static final int NAME_FIELD_NUMBER = 1;
        private Object name_;
        public static final int PACKAGE_FIELD_NUMBER = 2;
        private Object package_;
        public static final int DEPENDENCY_FIELD_NUMBER = 3;
        private LazyStringList dependency_;
        public static final int PUBLIC_DEPENDENCY_FIELD_NUMBER = 10;
        private List<Integer> publicDependency_;
        public static final int WEAK_DEPENDENCY_FIELD_NUMBER = 11;
        private List<Integer> weakDependency_;
        public static final int MESSAGE_TYPE_FIELD_NUMBER = 4;
        private List<DescriptorProto> messageType_;
        public static final int ENUM_TYPE_FIELD_NUMBER = 5;
        private List<EnumDescriptorProto> enumType_;
        public static final int SERVICE_FIELD_NUMBER = 6;
        private List<ServiceDescriptorProto> service_;
        public static final int EXTENSION_FIELD_NUMBER = 7;
        private List<FieldDescriptorProto> extension_;
        public static final int OPTIONS_FIELD_NUMBER = 8;
        private FileOptions options_;
        public static final int SOURCE_CODE_INFO_FIELD_NUMBER = 9;
        private SourceCodeInfo sourceCodeInfo_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private FileDescriptorProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private FileDescriptorProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static FileDescriptorProto getDefaultInstance() {
            return FileDescriptorProto.defaultInstance;
        }
        
        public FileDescriptorProto getDefaultInstanceForType() {
            return FileDescriptorProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private FileDescriptorProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            this.bitField0_ |= 0x1;
                            this.name_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.package_ = input.readBytes();
                            continue;
                        }
                        case 26: {
                            if ((mutable_bitField0_ & 0x4) != 0x4) {
                                this.dependency_ = new LazyStringArrayList();
                                mutable_bitField0_ |= 0x4;
                            }
                            this.dependency_.add(input.readBytes());
                            continue;
                        }
                        case 34: {
                            if ((mutable_bitField0_ & 0x20) != 0x20) {
                                this.messageType_ = new ArrayList<DescriptorProto>();
                                mutable_bitField0_ |= 0x20;
                            }
                            this.messageType_.add(input.readMessage(DescriptorProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 42: {
                            if ((mutable_bitField0_ & 0x40) != 0x40) {
                                this.enumType_ = new ArrayList<EnumDescriptorProto>();
                                mutable_bitField0_ |= 0x40;
                            }
                            this.enumType_.add(input.readMessage(EnumDescriptorProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 50: {
                            if ((mutable_bitField0_ & 0x80) != 0x80) {
                                this.service_ = new ArrayList<ServiceDescriptorProto>();
                                mutable_bitField0_ |= 0x80;
                            }
                            this.service_.add(input.readMessage(ServiceDescriptorProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 58: {
                            if ((mutable_bitField0_ & 0x100) != 0x100) {
                                this.extension_ = new ArrayList<FieldDescriptorProto>();
                                mutable_bitField0_ |= 0x100;
                            }
                            this.extension_.add(input.readMessage(FieldDescriptorProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 66: {
                            FileOptions.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x4) == 0x4) {
                                subBuilder = this.options_.toBuilder();
                            }
                            this.options_ = input.readMessage(FileOptions.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.options_);
                                this.options_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x4;
                            continue;
                        }
                        case 74: {
                            SourceCodeInfo.Builder subBuilder2 = null;
                            if ((this.bitField0_ & 0x8) == 0x8) {
                                subBuilder2 = this.sourceCodeInfo_.toBuilder();
                            }
                            this.sourceCodeInfo_ = input.readMessage(SourceCodeInfo.PARSER, extensionRegistry);
                            if (subBuilder2 != null) {
                                subBuilder2.mergeFrom(this.sourceCodeInfo_);
                                this.sourceCodeInfo_ = subBuilder2.buildPartial();
                            }
                            this.bitField0_ |= 0x8;
                            continue;
                        }
                        case 80: {
                            if ((mutable_bitField0_ & 0x8) != 0x8) {
                                this.publicDependency_ = new ArrayList<Integer>();
                                mutable_bitField0_ |= 0x8;
                            }
                            this.publicDependency_.add(input.readInt32());
                            continue;
                        }
                        case 82: {
                            final int length = input.readRawVarint32();
                            final int limit = input.pushLimit(length);
                            if ((mutable_bitField0_ & 0x8) != 0x8 && input.getBytesUntilLimit() > 0) {
                                this.publicDependency_ = new ArrayList<Integer>();
                                mutable_bitField0_ |= 0x8;
                            }
                            while (input.getBytesUntilLimit() > 0) {
                                this.publicDependency_.add(input.readInt32());
                            }
                            input.popLimit(limit);
                            continue;
                        }
                        case 88: {
                            if ((mutable_bitField0_ & 0x10) != 0x10) {
                                this.weakDependency_ = new ArrayList<Integer>();
                                mutable_bitField0_ |= 0x10;
                            }
                            this.weakDependency_.add(input.readInt32());
                            continue;
                        }
                        case 90: {
                            final int length = input.readRawVarint32();
                            final int limit = input.pushLimit(length);
                            if ((mutable_bitField0_ & 0x10) != 0x10 && input.getBytesUntilLimit() > 0) {
                                this.weakDependency_ = new ArrayList<Integer>();
                                mutable_bitField0_ |= 0x10;
                            }
                            while (input.getBytesUntilLimit() > 0) {
                                this.weakDependency_.add(input.readInt32());
                            }
                            input.popLimit(limit);
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x4) == 0x4) {
                    this.dependency_ = new UnmodifiableLazyStringList(this.dependency_);
                }
                if ((mutable_bitField0_ & 0x20) == 0x20) {
                    this.messageType_ = Collections.unmodifiableList((List<? extends DescriptorProto>)this.messageType_);
                }
                if ((mutable_bitField0_ & 0x40) == 0x40) {
                    this.enumType_ = Collections.unmodifiableList((List<? extends EnumDescriptorProto>)this.enumType_);
                }
                if ((mutable_bitField0_ & 0x80) == 0x80) {
                    this.service_ = Collections.unmodifiableList((List<? extends ServiceDescriptorProto>)this.service_);
                }
                if ((mutable_bitField0_ & 0x100) == 0x100) {
                    this.extension_ = Collections.unmodifiableList((List<? extends FieldDescriptorProto>)this.extension_);
                }
                if ((mutable_bitField0_ & 0x8) == 0x8) {
                    this.publicDependency_ = Collections.unmodifiableList((List<? extends Integer>)this.publicDependency_);
                }
                if ((mutable_bitField0_ & 0x10) == 0x10) {
                    this.weakDependency_ = Collections.unmodifiableList((List<? extends Integer>)this.weakDependency_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(FileDescriptorProto.class, Builder.class);
        }
        
        @Override
        public Parser<FileDescriptorProto> getParserForType() {
            return FileDescriptorProto.PARSER;
        }
        
        public boolean hasName() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        public String getName() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }
        
        public ByteString getNameBytes() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.name_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasPackage() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        public String getPackage() {
            final Object ref = this.package_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.package_ = s;
            }
            return s;
        }
        
        public ByteString getPackageBytes() {
            final Object ref = this.package_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.package_ = b);
            }
            return (ByteString)ref;
        }
        
        public List<String> getDependencyList() {
            return this.dependency_;
        }
        
        public int getDependencyCount() {
            return this.dependency_.size();
        }
        
        public String getDependency(final int index) {
            return this.dependency_.get(index);
        }
        
        public ByteString getDependencyBytes(final int index) {
            return this.dependency_.getByteString(index);
        }
        
        public List<Integer> getPublicDependencyList() {
            return this.publicDependency_;
        }
        
        public int getPublicDependencyCount() {
            return this.publicDependency_.size();
        }
        
        public int getPublicDependency(final int index) {
            return this.publicDependency_.get(index);
        }
        
        public List<Integer> getWeakDependencyList() {
            return this.weakDependency_;
        }
        
        public int getWeakDependencyCount() {
            return this.weakDependency_.size();
        }
        
        public int getWeakDependency(final int index) {
            return this.weakDependency_.get(index);
        }
        
        public List<DescriptorProto> getMessageTypeList() {
            return this.messageType_;
        }
        
        public List<? extends DescriptorProtoOrBuilder> getMessageTypeOrBuilderList() {
            return this.messageType_;
        }
        
        public int getMessageTypeCount() {
            return this.messageType_.size();
        }
        
        public DescriptorProto getMessageType(final int index) {
            return this.messageType_.get(index);
        }
        
        public DescriptorProtoOrBuilder getMessageTypeOrBuilder(final int index) {
            return this.messageType_.get(index);
        }
        
        public List<EnumDescriptorProto> getEnumTypeList() {
            return this.enumType_;
        }
        
        public List<? extends EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList() {
            return this.enumType_;
        }
        
        public int getEnumTypeCount() {
            return this.enumType_.size();
        }
        
        public EnumDescriptorProto getEnumType(final int index) {
            return this.enumType_.get(index);
        }
        
        public EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(final int index) {
            return this.enumType_.get(index);
        }
        
        public List<ServiceDescriptorProto> getServiceList() {
            return this.service_;
        }
        
        public List<? extends ServiceDescriptorProtoOrBuilder> getServiceOrBuilderList() {
            return this.service_;
        }
        
        public int getServiceCount() {
            return this.service_.size();
        }
        
        public ServiceDescriptorProto getService(final int index) {
            return this.service_.get(index);
        }
        
        public ServiceDescriptorProtoOrBuilder getServiceOrBuilder(final int index) {
            return this.service_.get(index);
        }
        
        public List<FieldDescriptorProto> getExtensionList() {
            return this.extension_;
        }
        
        public List<? extends FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList() {
            return this.extension_;
        }
        
        public int getExtensionCount() {
            return this.extension_.size();
        }
        
        public FieldDescriptorProto getExtension(final int index) {
            return this.extension_.get(index);
        }
        
        public FieldDescriptorProtoOrBuilder getExtensionOrBuilder(final int index) {
            return this.extension_.get(index);
        }
        
        public boolean hasOptions() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        public FileOptions getOptions() {
            return this.options_;
        }
        
        public FileOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }
        
        public boolean hasSourceCodeInfo() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        public SourceCodeInfo getSourceCodeInfo() {
            return this.sourceCodeInfo_;
        }
        
        public SourceCodeInfoOrBuilder getSourceCodeInfoOrBuilder() {
            return this.sourceCodeInfo_;
        }
        
        private void initFields() {
            this.name_ = "";
            this.package_ = "";
            this.dependency_ = LazyStringArrayList.EMPTY;
            this.publicDependency_ = Collections.emptyList();
            this.weakDependency_ = Collections.emptyList();
            this.messageType_ = Collections.emptyList();
            this.enumType_ = Collections.emptyList();
            this.service_ = Collections.emptyList();
            this.extension_ = Collections.emptyList();
            this.options_ = FileOptions.getDefaultInstance();
            this.sourceCodeInfo_ = SourceCodeInfo.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getMessageTypeCount(); ++i) {
                if (!this.getMessageType(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i = 0; i < this.getEnumTypeCount(); ++i) {
                if (!this.getEnumType(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i = 0; i < this.getServiceCount(); ++i) {
                if (!this.getService(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i = 0; i < this.getExtensionCount(); ++i) {
                if (!this.getExtension(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (this.hasOptions() && !this.getOptions().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBytes(1, this.getNameBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getPackageBytes());
            }
            for (int i = 0; i < this.dependency_.size(); ++i) {
                output.writeBytes(3, this.dependency_.getByteString(i));
            }
            for (int i = 0; i < this.messageType_.size(); ++i) {
                output.writeMessage(4, this.messageType_.get(i));
            }
            for (int i = 0; i < this.enumType_.size(); ++i) {
                output.writeMessage(5, this.enumType_.get(i));
            }
            for (int i = 0; i < this.service_.size(); ++i) {
                output.writeMessage(6, this.service_.get(i));
            }
            for (int i = 0; i < this.extension_.size(); ++i) {
                output.writeMessage(7, this.extension_.get(i));
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeMessage(8, this.options_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeMessage(9, this.sourceCodeInfo_);
            }
            for (int i = 0; i < this.publicDependency_.size(); ++i) {
                output.writeInt32(10, this.publicDependency_.get(i));
            }
            for (int i = 0; i < this.weakDependency_.size(); ++i) {
                output.writeInt32(11, this.weakDependency_.get(i));
            }
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeBytesSize(1, this.getNameBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getPackageBytes());
            }
            int dataSize = 0;
            for (int i = 0; i < this.dependency_.size(); ++i) {
                dataSize += CodedOutputStream.computeBytesSizeNoTag(this.dependency_.getByteString(i));
            }
            size += dataSize;
            size += 1 * this.getDependencyList().size();
            for (int j = 0; j < this.messageType_.size(); ++j) {
                size += CodedOutputStream.computeMessageSize(4, this.messageType_.get(j));
            }
            for (int j = 0; j < this.enumType_.size(); ++j) {
                size += CodedOutputStream.computeMessageSize(5, this.enumType_.get(j));
            }
            for (int j = 0; j < this.service_.size(); ++j) {
                size += CodedOutputStream.computeMessageSize(6, this.service_.get(j));
            }
            for (int j = 0; j < this.extension_.size(); ++j) {
                size += CodedOutputStream.computeMessageSize(7, this.extension_.get(j));
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeMessageSize(8, this.options_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeMessageSize(9, this.sourceCodeInfo_);
            }
            dataSize = 0;
            for (int i = 0; i < this.publicDependency_.size(); ++i) {
                dataSize += CodedOutputStream.computeInt32SizeNoTag(this.publicDependency_.get(i));
            }
            size += dataSize;
            size += 1 * this.getPublicDependencyList().size();
            dataSize = 0;
            for (int i = 0; i < this.weakDependency_.size(); ++i) {
                dataSize += CodedOutputStream.computeInt32SizeNoTag(this.weakDependency_.get(i));
            }
            size += dataSize;
            size += 1 * this.getWeakDependencyList().size();
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static FileDescriptorProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return FileDescriptorProto.PARSER.parseFrom(data);
        }
        
        public static FileDescriptorProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FileDescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FileDescriptorProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return FileDescriptorProto.PARSER.parseFrom(data);
        }
        
        public static FileDescriptorProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FileDescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FileDescriptorProto parseFrom(final InputStream input) throws IOException {
            return FileDescriptorProto.PARSER.parseFrom(input);
        }
        
        public static FileDescriptorProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FileDescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static FileDescriptorProto parseDelimitedFrom(final InputStream input) throws IOException {
            return FileDescriptorProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static FileDescriptorProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FileDescriptorProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static FileDescriptorProto parseFrom(final CodedInputStream input) throws IOException {
            return FileDescriptorProto.PARSER.parseFrom(input);
        }
        
        public static FileDescriptorProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FileDescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final FileDescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            FileDescriptorProto.PARSER = new AbstractParser<FileDescriptorProto>() {
                public FileDescriptorProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new FileDescriptorProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new FileDescriptorProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements FileDescriptorProtoOrBuilder
        {
            private int bitField0_;
            private Object name_;
            private Object package_;
            private LazyStringList dependency_;
            private List<Integer> publicDependency_;
            private List<Integer> weakDependency_;
            private List<DescriptorProto> messageType_;
            private RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> messageTypeBuilder_;
            private List<EnumDescriptorProto> enumType_;
            private RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> enumTypeBuilder_;
            private List<ServiceDescriptorProto> service_;
            private RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> serviceBuilder_;
            private List<FieldDescriptorProto> extension_;
            private RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> extensionBuilder_;
            private FileOptions options_;
            private SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder> optionsBuilder_;
            private SourceCodeInfo sourceCodeInfo_;
            private SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder> sourceCodeInfoBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(FileDescriptorProto.class, Builder.class);
            }
            
            private Builder() {
                this.name_ = "";
                this.package_ = "";
                this.dependency_ = LazyStringArrayList.EMPTY;
                this.publicDependency_ = Collections.emptyList();
                this.weakDependency_ = Collections.emptyList();
                this.messageType_ = Collections.emptyList();
                this.enumType_ = Collections.emptyList();
                this.service_ = Collections.emptyList();
                this.extension_ = Collections.emptyList();
                this.options_ = FileOptions.getDefaultInstance();
                this.sourceCodeInfo_ = SourceCodeInfo.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.package_ = "";
                this.dependency_ = LazyStringArrayList.EMPTY;
                this.publicDependency_ = Collections.emptyList();
                this.weakDependency_ = Collections.emptyList();
                this.messageType_ = Collections.emptyList();
                this.enumType_ = Collections.emptyList();
                this.service_ = Collections.emptyList();
                this.extension_ = Collections.emptyList();
                this.options_ = FileOptions.getDefaultInstance();
                this.sourceCodeInfo_ = SourceCodeInfo.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getMessageTypeFieldBuilder();
                    this.getEnumTypeFieldBuilder();
                    this.getServiceFieldBuilder();
                    this.getExtensionFieldBuilder();
                    this.getOptionsFieldBuilder();
                    this.getSourceCodeInfoFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.package_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.dependency_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFB;
                this.publicDependency_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFF7;
                this.weakDependency_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFEF;
                if (this.messageTypeBuilder_ == null) {
                    this.messageType_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFDF;
                }
                else {
                    this.messageTypeBuilder_.clear();
                }
                if (this.enumTypeBuilder_ == null) {
                    this.enumType_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFBF;
                }
                else {
                    this.enumTypeBuilder_.clear();
                }
                if (this.serviceBuilder_ == null) {
                    this.service_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFF7F;
                }
                else {
                    this.serviceBuilder_.clear();
                }
                if (this.extensionBuilder_ == null) {
                    this.extension_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFEFF;
                }
                else {
                    this.extensionBuilder_.clear();
                }
                if (this.optionsBuilder_ == null) {
                    this.options_ = FileOptions.getDefaultInstance();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFDFF;
                if (this.sourceCodeInfoBuilder_ == null) {
                    this.sourceCodeInfo_ = SourceCodeInfo.getDefaultInstance();
                }
                else {
                    this.sourceCodeInfoBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFBFF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_FileDescriptorProto_descriptor;
            }
            
            public FileDescriptorProto getDefaultInstanceForType() {
                return FileDescriptorProto.getDefaultInstance();
            }
            
            public FileDescriptorProto build() {
                final FileDescriptorProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public FileDescriptorProto buildPartial() {
                final FileDescriptorProto result = new FileDescriptorProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.name_ = this.name_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.package_ = this.package_;
                if ((this.bitField0_ & 0x4) == 0x4) {
                    this.dependency_ = new UnmodifiableLazyStringList(this.dependency_);
                    this.bitField0_ &= 0xFFFFFFFB;
                }
                result.dependency_ = this.dependency_;
                if ((this.bitField0_ & 0x8) == 0x8) {
                    this.publicDependency_ = Collections.unmodifiableList((List<? extends Integer>)this.publicDependency_);
                    this.bitField0_ &= 0xFFFFFFF7;
                }
                result.publicDependency_ = this.publicDependency_;
                if ((this.bitField0_ & 0x10) == 0x10) {
                    this.weakDependency_ = Collections.unmodifiableList((List<? extends Integer>)this.weakDependency_);
                    this.bitField0_ &= 0xFFFFFFEF;
                }
                result.weakDependency_ = this.weakDependency_;
                if (this.messageTypeBuilder_ == null) {
                    if ((this.bitField0_ & 0x20) == 0x20) {
                        this.messageType_ = Collections.unmodifiableList((List<? extends DescriptorProto>)this.messageType_);
                        this.bitField0_ &= 0xFFFFFFDF;
                    }
                    result.messageType_ = this.messageType_;
                }
                else {
                    result.messageType_ = this.messageTypeBuilder_.build();
                }
                if (this.enumTypeBuilder_ == null) {
                    if ((this.bitField0_ & 0x40) == 0x40) {
                        this.enumType_ = Collections.unmodifiableList((List<? extends EnumDescriptorProto>)this.enumType_);
                        this.bitField0_ &= 0xFFFFFFBF;
                    }
                    result.enumType_ = this.enumType_;
                }
                else {
                    result.enumType_ = this.enumTypeBuilder_.build();
                }
                if (this.serviceBuilder_ == null) {
                    if ((this.bitField0_ & 0x80) == 0x80) {
                        this.service_ = Collections.unmodifiableList((List<? extends ServiceDescriptorProto>)this.service_);
                        this.bitField0_ &= 0xFFFFFF7F;
                    }
                    result.service_ = this.service_;
                }
                else {
                    result.service_ = this.serviceBuilder_.build();
                }
                if (this.extensionBuilder_ == null) {
                    if ((this.bitField0_ & 0x100) == 0x100) {
                        this.extension_ = Collections.unmodifiableList((List<? extends FieldDescriptorProto>)this.extension_);
                        this.bitField0_ &= 0xFFFFFEFF;
                    }
                    result.extension_ = this.extension_;
                }
                else {
                    result.extension_ = this.extensionBuilder_.build();
                }
                if ((from_bitField0_ & 0x200) == 0x200) {
                    to_bitField0_ |= 0x4;
                }
                if (this.optionsBuilder_ == null) {
                    result.options_ = this.options_;
                }
                else {
                    result.options_ = this.optionsBuilder_.build();
                }
                if ((from_bitField0_ & 0x400) == 0x400) {
                    to_bitField0_ |= 0x8;
                }
                if (this.sourceCodeInfoBuilder_ == null) {
                    result.sourceCodeInfo_ = this.sourceCodeInfo_;
                }
                else {
                    result.sourceCodeInfo_ = this.sourceCodeInfoBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof FileDescriptorProto) {
                    return this.mergeFrom((FileDescriptorProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final FileDescriptorProto other) {
                if (other == FileDescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 0x1;
                    this.name_ = other.name_;
                    this.onChanged();
                }
                if (other.hasPackage()) {
                    this.bitField0_ |= 0x2;
                    this.package_ = other.package_;
                    this.onChanged();
                }
                if (!other.dependency_.isEmpty()) {
                    if (this.dependency_.isEmpty()) {
                        this.dependency_ = other.dependency_;
                        this.bitField0_ &= 0xFFFFFFFB;
                    }
                    else {
                        this.ensureDependencyIsMutable();
                        this.dependency_.addAll(other.dependency_);
                    }
                    this.onChanged();
                }
                if (!other.publicDependency_.isEmpty()) {
                    if (this.publicDependency_.isEmpty()) {
                        this.publicDependency_ = other.publicDependency_;
                        this.bitField0_ &= 0xFFFFFFF7;
                    }
                    else {
                        this.ensurePublicDependencyIsMutable();
                        this.publicDependency_.addAll(other.publicDependency_);
                    }
                    this.onChanged();
                }
                if (!other.weakDependency_.isEmpty()) {
                    if (this.weakDependency_.isEmpty()) {
                        this.weakDependency_ = other.weakDependency_;
                        this.bitField0_ &= 0xFFFFFFEF;
                    }
                    else {
                        this.ensureWeakDependencyIsMutable();
                        this.weakDependency_.addAll(other.weakDependency_);
                    }
                    this.onChanged();
                }
                if (this.messageTypeBuilder_ == null) {
                    if (!other.messageType_.isEmpty()) {
                        if (this.messageType_.isEmpty()) {
                            this.messageType_ = other.messageType_;
                            this.bitField0_ &= 0xFFFFFFDF;
                        }
                        else {
                            this.ensureMessageTypeIsMutable();
                            this.messageType_.addAll(other.messageType_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.messageType_.isEmpty()) {
                    if (this.messageTypeBuilder_.isEmpty()) {
                        this.messageTypeBuilder_.dispose();
                        this.messageTypeBuilder_ = null;
                        this.messageType_ = other.messageType_;
                        this.bitField0_ &= 0xFFFFFFDF;
                        this.messageTypeBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getMessageTypeFieldBuilder() : null);
                    }
                    else {
                        this.messageTypeBuilder_.addAllMessages(other.messageType_);
                    }
                }
                if (this.enumTypeBuilder_ == null) {
                    if (!other.enumType_.isEmpty()) {
                        if (this.enumType_.isEmpty()) {
                            this.enumType_ = other.enumType_;
                            this.bitField0_ &= 0xFFFFFFBF;
                        }
                        else {
                            this.ensureEnumTypeIsMutable();
                            this.enumType_.addAll(other.enumType_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.enumType_.isEmpty()) {
                    if (this.enumTypeBuilder_.isEmpty()) {
                        this.enumTypeBuilder_.dispose();
                        this.enumTypeBuilder_ = null;
                        this.enumType_ = other.enumType_;
                        this.bitField0_ &= 0xFFFFFFBF;
                        this.enumTypeBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getEnumTypeFieldBuilder() : null);
                    }
                    else {
                        this.enumTypeBuilder_.addAllMessages(other.enumType_);
                    }
                }
                if (this.serviceBuilder_ == null) {
                    if (!other.service_.isEmpty()) {
                        if (this.service_.isEmpty()) {
                            this.service_ = other.service_;
                            this.bitField0_ &= 0xFFFFFF7F;
                        }
                        else {
                            this.ensureServiceIsMutable();
                            this.service_.addAll(other.service_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.service_.isEmpty()) {
                    if (this.serviceBuilder_.isEmpty()) {
                        this.serviceBuilder_.dispose();
                        this.serviceBuilder_ = null;
                        this.service_ = other.service_;
                        this.bitField0_ &= 0xFFFFFF7F;
                        this.serviceBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getServiceFieldBuilder() : null);
                    }
                    else {
                        this.serviceBuilder_.addAllMessages(other.service_);
                    }
                }
                if (this.extensionBuilder_ == null) {
                    if (!other.extension_.isEmpty()) {
                        if (this.extension_.isEmpty()) {
                            this.extension_ = other.extension_;
                            this.bitField0_ &= 0xFFFFFEFF;
                        }
                        else {
                            this.ensureExtensionIsMutable();
                            this.extension_.addAll(other.extension_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.extension_.isEmpty()) {
                    if (this.extensionBuilder_.isEmpty()) {
                        this.extensionBuilder_.dispose();
                        this.extensionBuilder_ = null;
                        this.extension_ = other.extension_;
                        this.bitField0_ &= 0xFFFFFEFF;
                        this.extensionBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getExtensionFieldBuilder() : null);
                    }
                    else {
                        this.extensionBuilder_.addAllMessages(other.extension_);
                    }
                }
                if (other.hasOptions()) {
                    this.mergeOptions(other.getOptions());
                }
                if (other.hasSourceCodeInfo()) {
                    this.mergeSourceCodeInfo(other.getSourceCodeInfo());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getMessageTypeCount(); ++i) {
                    if (!this.getMessageType(i).isInitialized()) {
                        return false;
                    }
                }
                for (int i = 0; i < this.getEnumTypeCount(); ++i) {
                    if (!this.getEnumType(i).isInitialized()) {
                        return false;
                    }
                }
                for (int i = 0; i < this.getServiceCount(); ++i) {
                    if (!this.getService(i).isInitialized()) {
                        return false;
                    }
                }
                for (int i = 0; i < this.getExtensionCount(); ++i) {
                    if (!this.getExtension(i).isInitialized()) {
                        return false;
                    }
                }
                return !this.hasOptions() || this.getOptions().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                FileDescriptorProto parsedMessage = null;
                try {
                    parsedMessage = FileDescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (FileDescriptorProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            public boolean hasName() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public String getName() {
                final Object ref = this.name_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.name_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getNameBytes() {
                final Object ref = this.name_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.name_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearName() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.name_ = FileDescriptorProto.getDefaultInstance().getName();
                this.onChanged();
                return this;
            }
            
            public Builder setNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasPackage() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            public String getPackage() {
                final Object ref = this.package_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.package_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getPackageBytes() {
                final Object ref = this.package_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.package_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setPackage(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.package_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearPackage() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.package_ = FileDescriptorProto.getDefaultInstance().getPackage();
                this.onChanged();
                return this;
            }
            
            public Builder setPackageBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.package_ = value;
                this.onChanged();
                return this;
            }
            
            private void ensureDependencyIsMutable() {
                if ((this.bitField0_ & 0x4) != 0x4) {
                    this.dependency_ = new LazyStringArrayList(this.dependency_);
                    this.bitField0_ |= 0x4;
                }
            }
            
            public List<String> getDependencyList() {
                return Collections.unmodifiableList((List<? extends String>)this.dependency_);
            }
            
            public int getDependencyCount() {
                return this.dependency_.size();
            }
            
            public String getDependency(final int index) {
                return this.dependency_.get(index);
            }
            
            public ByteString getDependencyBytes(final int index) {
                return this.dependency_.getByteString(index);
            }
            
            public Builder setDependency(final int index, final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureDependencyIsMutable();
                this.dependency_.set(index, value);
                this.onChanged();
                return this;
            }
            
            public Builder addDependency(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureDependencyIsMutable();
                this.dependency_.add(value);
                this.onChanged();
                return this;
            }
            
            public Builder addAllDependency(final Iterable<String> values) {
                this.ensureDependencyIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.dependency_);
                this.onChanged();
                return this;
            }
            
            public Builder clearDependency() {
                this.dependency_ = LazyStringArrayList.EMPTY;
                this.bitField0_ &= 0xFFFFFFFB;
                this.onChanged();
                return this;
            }
            
            public Builder addDependencyBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureDependencyIsMutable();
                this.dependency_.add(value);
                this.onChanged();
                return this;
            }
            
            private void ensurePublicDependencyIsMutable() {
                if ((this.bitField0_ & 0x8) != 0x8) {
                    this.publicDependency_ = new ArrayList<Integer>(this.publicDependency_);
                    this.bitField0_ |= 0x8;
                }
            }
            
            public List<Integer> getPublicDependencyList() {
                return Collections.unmodifiableList((List<? extends Integer>)this.publicDependency_);
            }
            
            public int getPublicDependencyCount() {
                return this.publicDependency_.size();
            }
            
            public int getPublicDependency(final int index) {
                return this.publicDependency_.get(index);
            }
            
            public Builder setPublicDependency(final int index, final int value) {
                this.ensurePublicDependencyIsMutable();
                this.publicDependency_.set(index, value);
                this.onChanged();
                return this;
            }
            
            public Builder addPublicDependency(final int value) {
                this.ensurePublicDependencyIsMutable();
                this.publicDependency_.add(value);
                this.onChanged();
                return this;
            }
            
            public Builder addAllPublicDependency(final Iterable<? extends Integer> values) {
                this.ensurePublicDependencyIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.publicDependency_);
                this.onChanged();
                return this;
            }
            
            public Builder clearPublicDependency() {
                this.publicDependency_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFF7;
                this.onChanged();
                return this;
            }
            
            private void ensureWeakDependencyIsMutable() {
                if ((this.bitField0_ & 0x10) != 0x10) {
                    this.weakDependency_ = new ArrayList<Integer>(this.weakDependency_);
                    this.bitField0_ |= 0x10;
                }
            }
            
            public List<Integer> getWeakDependencyList() {
                return Collections.unmodifiableList((List<? extends Integer>)this.weakDependency_);
            }
            
            public int getWeakDependencyCount() {
                return this.weakDependency_.size();
            }
            
            public int getWeakDependency(final int index) {
                return this.weakDependency_.get(index);
            }
            
            public Builder setWeakDependency(final int index, final int value) {
                this.ensureWeakDependencyIsMutable();
                this.weakDependency_.set(index, value);
                this.onChanged();
                return this;
            }
            
            public Builder addWeakDependency(final int value) {
                this.ensureWeakDependencyIsMutable();
                this.weakDependency_.add(value);
                this.onChanged();
                return this;
            }
            
            public Builder addAllWeakDependency(final Iterable<? extends Integer> values) {
                this.ensureWeakDependencyIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.weakDependency_);
                this.onChanged();
                return this;
            }
            
            public Builder clearWeakDependency() {
                this.weakDependency_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFEF;
                this.onChanged();
                return this;
            }
            
            private void ensureMessageTypeIsMutable() {
                if ((this.bitField0_ & 0x20) != 0x20) {
                    this.messageType_ = new ArrayList<DescriptorProto>(this.messageType_);
                    this.bitField0_ |= 0x20;
                }
            }
            
            public List<DescriptorProto> getMessageTypeList() {
                if (this.messageTypeBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends DescriptorProto>)this.messageType_);
                }
                return this.messageTypeBuilder_.getMessageList();
            }
            
            public int getMessageTypeCount() {
                if (this.messageTypeBuilder_ == null) {
                    return this.messageType_.size();
                }
                return this.messageTypeBuilder_.getCount();
            }
            
            public DescriptorProto getMessageType(final int index) {
                if (this.messageTypeBuilder_ == null) {
                    return this.messageType_.get(index);
                }
                return this.messageTypeBuilder_.getMessage(index);
            }
            
            public Builder setMessageType(final int index, final DescriptorProto value) {
                if (this.messageTypeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureMessageTypeIsMutable();
                    this.messageType_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.messageTypeBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setMessageType(final int index, final DescriptorProto.Builder builderForValue) {
                if (this.messageTypeBuilder_ == null) {
                    this.ensureMessageTypeIsMutable();
                    this.messageType_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.messageTypeBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addMessageType(final DescriptorProto value) {
                if (this.messageTypeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureMessageTypeIsMutable();
                    this.messageType_.add(value);
                    this.onChanged();
                }
                else {
                    this.messageTypeBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addMessageType(final int index, final DescriptorProto value) {
                if (this.messageTypeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureMessageTypeIsMutable();
                    this.messageType_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.messageTypeBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addMessageType(final DescriptorProto.Builder builderForValue) {
                if (this.messageTypeBuilder_ == null) {
                    this.ensureMessageTypeIsMutable();
                    this.messageType_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.messageTypeBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addMessageType(final int index, final DescriptorProto.Builder builderForValue) {
                if (this.messageTypeBuilder_ == null) {
                    this.ensureMessageTypeIsMutable();
                    this.messageType_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.messageTypeBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllMessageType(final Iterable<? extends DescriptorProto> values) {
                if (this.messageTypeBuilder_ == null) {
                    this.ensureMessageTypeIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.messageType_);
                    this.onChanged();
                }
                else {
                    this.messageTypeBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearMessageType() {
                if (this.messageTypeBuilder_ == null) {
                    this.messageType_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFDF;
                    this.onChanged();
                }
                else {
                    this.messageTypeBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeMessageType(final int index) {
                if (this.messageTypeBuilder_ == null) {
                    this.ensureMessageTypeIsMutable();
                    this.messageType_.remove(index);
                    this.onChanged();
                }
                else {
                    this.messageTypeBuilder_.remove(index);
                }
                return this;
            }
            
            public DescriptorProto.Builder getMessageTypeBuilder(final int index) {
                return this.getMessageTypeFieldBuilder().getBuilder(index);
            }
            
            public DescriptorProtoOrBuilder getMessageTypeOrBuilder(final int index) {
                if (this.messageTypeBuilder_ == null) {
                    return this.messageType_.get(index);
                }
                return this.messageTypeBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends DescriptorProtoOrBuilder> getMessageTypeOrBuilderList() {
                if (this.messageTypeBuilder_ != null) {
                    return this.messageTypeBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends DescriptorProtoOrBuilder>)this.messageType_);
            }
            
            public DescriptorProto.Builder addMessageTypeBuilder() {
                return this.getMessageTypeFieldBuilder().addBuilder(DescriptorProto.getDefaultInstance());
            }
            
            public DescriptorProto.Builder addMessageTypeBuilder(final int index) {
                return this.getMessageTypeFieldBuilder().addBuilder(index, DescriptorProto.getDefaultInstance());
            }
            
            public List<DescriptorProto.Builder> getMessageTypeBuilderList() {
                return this.getMessageTypeFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder> getMessageTypeFieldBuilder() {
                if (this.messageTypeBuilder_ == null) {
                    this.messageTypeBuilder_ = new RepeatedFieldBuilder<DescriptorProto, DescriptorProto.Builder, DescriptorProtoOrBuilder>(this.messageType_, (this.bitField0_ & 0x20) == 0x20, this.getParentForChildren(), this.isClean());
                    this.messageType_ = null;
                }
                return this.messageTypeBuilder_;
            }
            
            private void ensureEnumTypeIsMutable() {
                if ((this.bitField0_ & 0x40) != 0x40) {
                    this.enumType_ = new ArrayList<EnumDescriptorProto>(this.enumType_);
                    this.bitField0_ |= 0x40;
                }
            }
            
            public List<EnumDescriptorProto> getEnumTypeList() {
                if (this.enumTypeBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends EnumDescriptorProto>)this.enumType_);
                }
                return this.enumTypeBuilder_.getMessageList();
            }
            
            public int getEnumTypeCount() {
                if (this.enumTypeBuilder_ == null) {
                    return this.enumType_.size();
                }
                return this.enumTypeBuilder_.getCount();
            }
            
            public EnumDescriptorProto getEnumType(final int index) {
                if (this.enumTypeBuilder_ == null) {
                    return this.enumType_.get(index);
                }
                return this.enumTypeBuilder_.getMessage(index);
            }
            
            public Builder setEnumType(final int index, final EnumDescriptorProto value) {
                if (this.enumTypeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setEnumType(final int index, final EnumDescriptorProto.Builder builderForValue) {
                if (this.enumTypeBuilder_ == null) {
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addEnumType(final EnumDescriptorProto value) {
                if (this.enumTypeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.add(value);
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addEnumType(final int index, final EnumDescriptorProto value) {
                if (this.enumTypeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addEnumType(final EnumDescriptorProto.Builder builderForValue) {
                if (this.enumTypeBuilder_ == null) {
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addEnumType(final int index, final EnumDescriptorProto.Builder builderForValue) {
                if (this.enumTypeBuilder_ == null) {
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllEnumType(final Iterable<? extends EnumDescriptorProto> values) {
                if (this.enumTypeBuilder_ == null) {
                    this.ensureEnumTypeIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.enumType_);
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearEnumType() {
                if (this.enumTypeBuilder_ == null) {
                    this.enumType_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFBF;
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeEnumType(final int index) {
                if (this.enumTypeBuilder_ == null) {
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.remove(index);
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.remove(index);
                }
                return this;
            }
            
            public EnumDescriptorProto.Builder getEnumTypeBuilder(final int index) {
                return this.getEnumTypeFieldBuilder().getBuilder(index);
            }
            
            public EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(final int index) {
                if (this.enumTypeBuilder_ == null) {
                    return this.enumType_.get(index);
                }
                return this.enumTypeBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList() {
                if (this.enumTypeBuilder_ != null) {
                    return this.enumTypeBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends EnumDescriptorProtoOrBuilder>)this.enumType_);
            }
            
            public EnumDescriptorProto.Builder addEnumTypeBuilder() {
                return this.getEnumTypeFieldBuilder().addBuilder(EnumDescriptorProto.getDefaultInstance());
            }
            
            public EnumDescriptorProto.Builder addEnumTypeBuilder(final int index) {
                return this.getEnumTypeFieldBuilder().addBuilder(index, EnumDescriptorProto.getDefaultInstance());
            }
            
            public List<EnumDescriptorProto.Builder> getEnumTypeBuilderList() {
                return this.getEnumTypeFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> getEnumTypeFieldBuilder() {
                if (this.enumTypeBuilder_ == null) {
                    this.enumTypeBuilder_ = new RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder>(this.enumType_, (this.bitField0_ & 0x40) == 0x40, this.getParentForChildren(), this.isClean());
                    this.enumType_ = null;
                }
                return this.enumTypeBuilder_;
            }
            
            private void ensureServiceIsMutable() {
                if ((this.bitField0_ & 0x80) != 0x80) {
                    this.service_ = new ArrayList<ServiceDescriptorProto>(this.service_);
                    this.bitField0_ |= 0x80;
                }
            }
            
            public List<ServiceDescriptorProto> getServiceList() {
                if (this.serviceBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends ServiceDescriptorProto>)this.service_);
                }
                return this.serviceBuilder_.getMessageList();
            }
            
            public int getServiceCount() {
                if (this.serviceBuilder_ == null) {
                    return this.service_.size();
                }
                return this.serviceBuilder_.getCount();
            }
            
            public ServiceDescriptorProto getService(final int index) {
                if (this.serviceBuilder_ == null) {
                    return this.service_.get(index);
                }
                return this.serviceBuilder_.getMessage(index);
            }
            
            public Builder setService(final int index, final ServiceDescriptorProto value) {
                if (this.serviceBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureServiceIsMutable();
                    this.service_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.serviceBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setService(final int index, final ServiceDescriptorProto.Builder builderForValue) {
                if (this.serviceBuilder_ == null) {
                    this.ensureServiceIsMutable();
                    this.service_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.serviceBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addService(final ServiceDescriptorProto value) {
                if (this.serviceBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureServiceIsMutable();
                    this.service_.add(value);
                    this.onChanged();
                }
                else {
                    this.serviceBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addService(final int index, final ServiceDescriptorProto value) {
                if (this.serviceBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureServiceIsMutable();
                    this.service_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.serviceBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addService(final ServiceDescriptorProto.Builder builderForValue) {
                if (this.serviceBuilder_ == null) {
                    this.ensureServiceIsMutable();
                    this.service_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.serviceBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addService(final int index, final ServiceDescriptorProto.Builder builderForValue) {
                if (this.serviceBuilder_ == null) {
                    this.ensureServiceIsMutable();
                    this.service_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.serviceBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllService(final Iterable<? extends ServiceDescriptorProto> values) {
                if (this.serviceBuilder_ == null) {
                    this.ensureServiceIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.service_);
                    this.onChanged();
                }
                else {
                    this.serviceBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearService() {
                if (this.serviceBuilder_ == null) {
                    this.service_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFF7F;
                    this.onChanged();
                }
                else {
                    this.serviceBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeService(final int index) {
                if (this.serviceBuilder_ == null) {
                    this.ensureServiceIsMutable();
                    this.service_.remove(index);
                    this.onChanged();
                }
                else {
                    this.serviceBuilder_.remove(index);
                }
                return this;
            }
            
            public ServiceDescriptorProto.Builder getServiceBuilder(final int index) {
                return this.getServiceFieldBuilder().getBuilder(index);
            }
            
            public ServiceDescriptorProtoOrBuilder getServiceOrBuilder(final int index) {
                if (this.serviceBuilder_ == null) {
                    return this.service_.get(index);
                }
                return this.serviceBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends ServiceDescriptorProtoOrBuilder> getServiceOrBuilderList() {
                if (this.serviceBuilder_ != null) {
                    return this.serviceBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends ServiceDescriptorProtoOrBuilder>)this.service_);
            }
            
            public ServiceDescriptorProto.Builder addServiceBuilder() {
                return this.getServiceFieldBuilder().addBuilder(ServiceDescriptorProto.getDefaultInstance());
            }
            
            public ServiceDescriptorProto.Builder addServiceBuilder(final int index) {
                return this.getServiceFieldBuilder().addBuilder(index, ServiceDescriptorProto.getDefaultInstance());
            }
            
            public List<ServiceDescriptorProto.Builder> getServiceBuilderList() {
                return this.getServiceFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder> getServiceFieldBuilder() {
                if (this.serviceBuilder_ == null) {
                    this.serviceBuilder_ = new RepeatedFieldBuilder<ServiceDescriptorProto, ServiceDescriptorProto.Builder, ServiceDescriptorProtoOrBuilder>(this.service_, (this.bitField0_ & 0x80) == 0x80, this.getParentForChildren(), this.isClean());
                    this.service_ = null;
                }
                return this.serviceBuilder_;
            }
            
            private void ensureExtensionIsMutable() {
                if ((this.bitField0_ & 0x100) != 0x100) {
                    this.extension_ = new ArrayList<FieldDescriptorProto>(this.extension_);
                    this.bitField0_ |= 0x100;
                }
            }
            
            public List<FieldDescriptorProto> getExtensionList() {
                if (this.extensionBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends FieldDescriptorProto>)this.extension_);
                }
                return this.extensionBuilder_.getMessageList();
            }
            
            public int getExtensionCount() {
                if (this.extensionBuilder_ == null) {
                    return this.extension_.size();
                }
                return this.extensionBuilder_.getCount();
            }
            
            public FieldDescriptorProto getExtension(final int index) {
                if (this.extensionBuilder_ == null) {
                    return this.extension_.get(index);
                }
                return this.extensionBuilder_.getMessage(index);
            }
            
            public Builder setExtension(final int index, final FieldDescriptorProto value) {
                if (this.extensionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureExtensionIsMutable();
                    this.extension_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setExtension(final int index, final FieldDescriptorProto.Builder builderForValue) {
                if (this.extensionBuilder_ == null) {
                    this.ensureExtensionIsMutable();
                    this.extension_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addExtension(final FieldDescriptorProto value) {
                if (this.extensionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureExtensionIsMutable();
                    this.extension_.add(value);
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addExtension(final int index, final FieldDescriptorProto value) {
                if (this.extensionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureExtensionIsMutable();
                    this.extension_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addExtension(final FieldDescriptorProto.Builder builderForValue) {
                if (this.extensionBuilder_ == null) {
                    this.ensureExtensionIsMutable();
                    this.extension_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addExtension(final int index, final FieldDescriptorProto.Builder builderForValue) {
                if (this.extensionBuilder_ == null) {
                    this.ensureExtensionIsMutable();
                    this.extension_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllExtension(final Iterable<? extends FieldDescriptorProto> values) {
                if (this.extensionBuilder_ == null) {
                    this.ensureExtensionIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.extension_);
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearExtension() {
                if (this.extensionBuilder_ == null) {
                    this.extension_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFEFF;
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeExtension(final int index) {
                if (this.extensionBuilder_ == null) {
                    this.ensureExtensionIsMutable();
                    this.extension_.remove(index);
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.remove(index);
                }
                return this;
            }
            
            public FieldDescriptorProto.Builder getExtensionBuilder(final int index) {
                return this.getExtensionFieldBuilder().getBuilder(index);
            }
            
            public FieldDescriptorProtoOrBuilder getExtensionOrBuilder(final int index) {
                if (this.extensionBuilder_ == null) {
                    return this.extension_.get(index);
                }
                return this.extensionBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList() {
                if (this.extensionBuilder_ != null) {
                    return this.extensionBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends FieldDescriptorProtoOrBuilder>)this.extension_);
            }
            
            public FieldDescriptorProto.Builder addExtensionBuilder() {
                return this.getExtensionFieldBuilder().addBuilder(FieldDescriptorProto.getDefaultInstance());
            }
            
            public FieldDescriptorProto.Builder addExtensionBuilder(final int index) {
                return this.getExtensionFieldBuilder().addBuilder(index, FieldDescriptorProto.getDefaultInstance());
            }
            
            public List<FieldDescriptorProto.Builder> getExtensionBuilderList() {
                return this.getExtensionFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> getExtensionFieldBuilder() {
                if (this.extensionBuilder_ == null) {
                    this.extensionBuilder_ = new RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder>(this.extension_, (this.bitField0_ & 0x100) == 0x100, this.getParentForChildren(), this.isClean());
                    this.extension_ = null;
                }
                return this.extensionBuilder_;
            }
            
            public boolean hasOptions() {
                return (this.bitField0_ & 0x200) == 0x200;
            }
            
            public FileOptions getOptions() {
                if (this.optionsBuilder_ == null) {
                    return this.options_;
                }
                return this.optionsBuilder_.getMessage();
            }
            
            public Builder setOptions(final FileOptions value) {
                if (this.optionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.options_ = value;
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x200;
                return this;
            }
            
            public Builder setOptions(final FileOptions.Builder builderForValue) {
                if (this.optionsBuilder_ == null) {
                    this.options_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x200;
                return this;
            }
            
            public Builder mergeOptions(final FileOptions value) {
                if (this.optionsBuilder_ == null) {
                    if ((this.bitField0_ & 0x200) == 0x200 && this.options_ != FileOptions.getDefaultInstance()) {
                        this.options_ = FileOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.options_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x200;
                return this;
            }
            
            public Builder clearOptions() {
                if (this.optionsBuilder_ == null) {
                    this.options_ = FileOptions.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFDFF;
                return this;
            }
            
            public FileOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 0x200;
                this.onChanged();
                return this.getOptionsFieldBuilder().getBuilder();
            }
            
            public FileOptionsOrBuilder getOptionsOrBuilder() {
                if (this.optionsBuilder_ != null) {
                    return this.optionsBuilder_.getMessageOrBuilder();
                }
                return this.options_;
            }
            
            private SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<FileOptions, FileOptions.Builder, FileOptionsOrBuilder>(this.options_, this.getParentForChildren(), this.isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
            
            public boolean hasSourceCodeInfo() {
                return (this.bitField0_ & 0x400) == 0x400;
            }
            
            public SourceCodeInfo getSourceCodeInfo() {
                if (this.sourceCodeInfoBuilder_ == null) {
                    return this.sourceCodeInfo_;
                }
                return this.sourceCodeInfoBuilder_.getMessage();
            }
            
            public Builder setSourceCodeInfo(final SourceCodeInfo value) {
                if (this.sourceCodeInfoBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.sourceCodeInfo_ = value;
                    this.onChanged();
                }
                else {
                    this.sourceCodeInfoBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x400;
                return this;
            }
            
            public Builder setSourceCodeInfo(final SourceCodeInfo.Builder builderForValue) {
                if (this.sourceCodeInfoBuilder_ == null) {
                    this.sourceCodeInfo_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.sourceCodeInfoBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x400;
                return this;
            }
            
            public Builder mergeSourceCodeInfo(final SourceCodeInfo value) {
                if (this.sourceCodeInfoBuilder_ == null) {
                    if ((this.bitField0_ & 0x400) == 0x400 && this.sourceCodeInfo_ != SourceCodeInfo.getDefaultInstance()) {
                        this.sourceCodeInfo_ = SourceCodeInfo.newBuilder(this.sourceCodeInfo_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.sourceCodeInfo_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.sourceCodeInfoBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x400;
                return this;
            }
            
            public Builder clearSourceCodeInfo() {
                if (this.sourceCodeInfoBuilder_ == null) {
                    this.sourceCodeInfo_ = SourceCodeInfo.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.sourceCodeInfoBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFBFF;
                return this;
            }
            
            public SourceCodeInfo.Builder getSourceCodeInfoBuilder() {
                this.bitField0_ |= 0x400;
                this.onChanged();
                return this.getSourceCodeInfoFieldBuilder().getBuilder();
            }
            
            public SourceCodeInfoOrBuilder getSourceCodeInfoOrBuilder() {
                if (this.sourceCodeInfoBuilder_ != null) {
                    return this.sourceCodeInfoBuilder_.getMessageOrBuilder();
                }
                return this.sourceCodeInfo_;
            }
            
            private SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder> getSourceCodeInfoFieldBuilder() {
                if (this.sourceCodeInfoBuilder_ == null) {
                    this.sourceCodeInfoBuilder_ = new SingleFieldBuilder<SourceCodeInfo, SourceCodeInfo.Builder, SourceCodeInfoOrBuilder>(this.sourceCodeInfo_, this.getParentForChildren(), this.isClean());
                    this.sourceCodeInfo_ = null;
                }
                return this.sourceCodeInfoBuilder_;
            }
        }
    }
    
    public static final class DescriptorProto extends GeneratedMessage implements DescriptorProtoOrBuilder
    {
        private static final DescriptorProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<DescriptorProto> PARSER;
        private int bitField0_;
        public static final int NAME_FIELD_NUMBER = 1;
        private Object name_;
        public static final int FIELD_FIELD_NUMBER = 2;
        private List<FieldDescriptorProto> field_;
        public static final int EXTENSION_FIELD_NUMBER = 6;
        private List<FieldDescriptorProto> extension_;
        public static final int NESTED_TYPE_FIELD_NUMBER = 3;
        private List<DescriptorProto> nestedType_;
        public static final int ENUM_TYPE_FIELD_NUMBER = 4;
        private List<EnumDescriptorProto> enumType_;
        public static final int EXTENSION_RANGE_FIELD_NUMBER = 5;
        private List<ExtensionRange> extensionRange_;
        public static final int OPTIONS_FIELD_NUMBER = 7;
        private MessageOptions options_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private DescriptorProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private DescriptorProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static DescriptorProto getDefaultInstance() {
            return DescriptorProto.defaultInstance;
        }
        
        public DescriptorProto getDefaultInstanceForType() {
            return DescriptorProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private DescriptorProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            this.bitField0_ |= 0x1;
                            this.name_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            if ((mutable_bitField0_ & 0x2) != 0x2) {
                                this.field_ = new ArrayList<FieldDescriptorProto>();
                                mutable_bitField0_ |= 0x2;
                            }
                            this.field_.add(input.readMessage(FieldDescriptorProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 26: {
                            if ((mutable_bitField0_ & 0x8) != 0x8) {
                                this.nestedType_ = new ArrayList<DescriptorProto>();
                                mutable_bitField0_ |= 0x8;
                            }
                            this.nestedType_.add(input.readMessage(DescriptorProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 34: {
                            if ((mutable_bitField0_ & 0x10) != 0x10) {
                                this.enumType_ = new ArrayList<EnumDescriptorProto>();
                                mutable_bitField0_ |= 0x10;
                            }
                            this.enumType_.add(input.readMessage(EnumDescriptorProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 42: {
                            if ((mutable_bitField0_ & 0x20) != 0x20) {
                                this.extensionRange_ = new ArrayList<ExtensionRange>();
                                mutable_bitField0_ |= 0x20;
                            }
                            this.extensionRange_.add(input.readMessage(ExtensionRange.PARSER, extensionRegistry));
                            continue;
                        }
                        case 50: {
                            if ((mutable_bitField0_ & 0x4) != 0x4) {
                                this.extension_ = new ArrayList<FieldDescriptorProto>();
                                mutable_bitField0_ |= 0x4;
                            }
                            this.extension_.add(input.readMessage(FieldDescriptorProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 58: {
                            MessageOptions.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder = this.options_.toBuilder();
                            }
                            this.options_ = input.readMessage(MessageOptions.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.options_);
                                this.options_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x2) == 0x2) {
                    this.field_ = Collections.unmodifiableList((List<? extends FieldDescriptorProto>)this.field_);
                }
                if ((mutable_bitField0_ & 0x8) == 0x8) {
                    this.nestedType_ = Collections.unmodifiableList((List<? extends DescriptorProto>)this.nestedType_);
                }
                if ((mutable_bitField0_ & 0x10) == 0x10) {
                    this.enumType_ = Collections.unmodifiableList((List<? extends EnumDescriptorProto>)this.enumType_);
                }
                if ((mutable_bitField0_ & 0x20) == 0x20) {
                    this.extensionRange_ = Collections.unmodifiableList((List<? extends ExtensionRange>)this.extensionRange_);
                }
                if ((mutable_bitField0_ & 0x4) == 0x4) {
                    this.extension_ = Collections.unmodifiableList((List<? extends FieldDescriptorProto>)this.extension_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(DescriptorProto.class, Builder.class);
        }
        
        @Override
        public Parser<DescriptorProto> getParserForType() {
            return DescriptorProto.PARSER;
        }
        
        public boolean hasName() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        public String getName() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }
        
        public ByteString getNameBytes() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.name_ = b);
            }
            return (ByteString)ref;
        }
        
        public List<FieldDescriptorProto> getFieldList() {
            return this.field_;
        }
        
        public List<? extends FieldDescriptorProtoOrBuilder> getFieldOrBuilderList() {
            return this.field_;
        }
        
        public int getFieldCount() {
            return this.field_.size();
        }
        
        public FieldDescriptorProto getField(final int index) {
            return this.field_.get(index);
        }
        
        public FieldDescriptorProtoOrBuilder getFieldOrBuilder(final int index) {
            return this.field_.get(index);
        }
        
        public List<FieldDescriptorProto> getExtensionList() {
            return this.extension_;
        }
        
        public List<? extends FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList() {
            return this.extension_;
        }
        
        public int getExtensionCount() {
            return this.extension_.size();
        }
        
        public FieldDescriptorProto getExtension(final int index) {
            return this.extension_.get(index);
        }
        
        public FieldDescriptorProtoOrBuilder getExtensionOrBuilder(final int index) {
            return this.extension_.get(index);
        }
        
        public List<DescriptorProto> getNestedTypeList() {
            return this.nestedType_;
        }
        
        public List<? extends DescriptorProtoOrBuilder> getNestedTypeOrBuilderList() {
            return this.nestedType_;
        }
        
        public int getNestedTypeCount() {
            return this.nestedType_.size();
        }
        
        public DescriptorProto getNestedType(final int index) {
            return this.nestedType_.get(index);
        }
        
        public DescriptorProtoOrBuilder getNestedTypeOrBuilder(final int index) {
            return this.nestedType_.get(index);
        }
        
        public List<EnumDescriptorProto> getEnumTypeList() {
            return this.enumType_;
        }
        
        public List<? extends EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList() {
            return this.enumType_;
        }
        
        public int getEnumTypeCount() {
            return this.enumType_.size();
        }
        
        public EnumDescriptorProto getEnumType(final int index) {
            return this.enumType_.get(index);
        }
        
        public EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(final int index) {
            return this.enumType_.get(index);
        }
        
        public List<ExtensionRange> getExtensionRangeList() {
            return this.extensionRange_;
        }
        
        public List<? extends ExtensionRangeOrBuilder> getExtensionRangeOrBuilderList() {
            return this.extensionRange_;
        }
        
        public int getExtensionRangeCount() {
            return this.extensionRange_.size();
        }
        
        public ExtensionRange getExtensionRange(final int index) {
            return this.extensionRange_.get(index);
        }
        
        public ExtensionRangeOrBuilder getExtensionRangeOrBuilder(final int index) {
            return this.extensionRange_.get(index);
        }
        
        public boolean hasOptions() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        public MessageOptions getOptions() {
            return this.options_;
        }
        
        public MessageOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }
        
        private void initFields() {
            this.name_ = "";
            this.field_ = Collections.emptyList();
            this.extension_ = Collections.emptyList();
            this.nestedType_ = Collections.emptyList();
            this.enumType_ = Collections.emptyList();
            this.extensionRange_ = Collections.emptyList();
            this.options_ = MessageOptions.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getFieldCount(); ++i) {
                if (!this.getField(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i = 0; i < this.getExtensionCount(); ++i) {
                if (!this.getExtension(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i = 0; i < this.getNestedTypeCount(); ++i) {
                if (!this.getNestedType(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            for (int i = 0; i < this.getEnumTypeCount(); ++i) {
                if (!this.getEnumType(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (this.hasOptions() && !this.getOptions().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBytes(1, this.getNameBytes());
            }
            for (int i = 0; i < this.field_.size(); ++i) {
                output.writeMessage(2, this.field_.get(i));
            }
            for (int i = 0; i < this.nestedType_.size(); ++i) {
                output.writeMessage(3, this.nestedType_.get(i));
            }
            for (int i = 0; i < this.enumType_.size(); ++i) {
                output.writeMessage(4, this.enumType_.get(i));
            }
            for (int i = 0; i < this.extensionRange_.size(); ++i) {
                output.writeMessage(5, this.extensionRange_.get(i));
            }
            for (int i = 0; i < this.extension_.size(); ++i) {
                output.writeMessage(6, this.extension_.get(i));
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeMessage(7, this.options_);
            }
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeBytesSize(1, this.getNameBytes());
            }
            for (int i = 0; i < this.field_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(2, this.field_.get(i));
            }
            for (int i = 0; i < this.nestedType_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(3, this.nestedType_.get(i));
            }
            for (int i = 0; i < this.enumType_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(4, this.enumType_.get(i));
            }
            for (int i = 0; i < this.extensionRange_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(5, this.extensionRange_.get(i));
            }
            for (int i = 0; i < this.extension_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(6, this.extension_.get(i));
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeMessageSize(7, this.options_);
            }
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static DescriptorProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return DescriptorProto.PARSER.parseFrom(data);
        }
        
        public static DescriptorProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return DescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static DescriptorProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return DescriptorProto.PARSER.parseFrom(data);
        }
        
        public static DescriptorProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return DescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static DescriptorProto parseFrom(final InputStream input) throws IOException {
            return DescriptorProto.PARSER.parseFrom(input);
        }
        
        public static DescriptorProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return DescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static DescriptorProto parseDelimitedFrom(final InputStream input) throws IOException {
            return DescriptorProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static DescriptorProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return DescriptorProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static DescriptorProto parseFrom(final CodedInputStream input) throws IOException {
            return DescriptorProto.PARSER.parseFrom(input);
        }
        
        public static DescriptorProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return DescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final DescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            DescriptorProto.PARSER = new AbstractParser<DescriptorProto>() {
                public DescriptorProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new DescriptorProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new DescriptorProto(true)).initFields();
        }
        
        public static final class ExtensionRange extends GeneratedMessage implements ExtensionRangeOrBuilder
        {
            private static final ExtensionRange defaultInstance;
            private final UnknownFieldSet unknownFields;
            public static Parser<ExtensionRange> PARSER;
            private int bitField0_;
            public static final int START_FIELD_NUMBER = 1;
            private int start_;
            public static final int END_FIELD_NUMBER = 2;
            private int end_;
            private byte memoizedIsInitialized;
            private int memoizedSerializedSize;
            private static final long serialVersionUID = 0L;
            
            private ExtensionRange(final GeneratedMessage.Builder<?> builder) {
                super(builder);
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.unknownFields = builder.getUnknownFields();
            }
            
            private ExtensionRange(final boolean noInit) {
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.unknownFields = UnknownFieldSet.getDefaultInstance();
            }
            
            public static ExtensionRange getDefaultInstance() {
                return ExtensionRange.defaultInstance;
            }
            
            public ExtensionRange getDefaultInstanceForType() {
                return ExtensionRange.defaultInstance;
            }
            
            @Override
            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }
            
            private ExtensionRange(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.initFields();
                final int mutable_bitField0_ = 0;
                final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
                try {
                    boolean done = false;
                    while (!done) {
                        final int tag = input.readTag();
                        switch (tag) {
                            case 0: {
                                done = true;
                                continue;
                            }
                            default: {
                                if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                    done = true;
                                    continue;
                                }
                                continue;
                            }
                            case 8: {
                                this.bitField0_ |= 0x1;
                                this.start_ = input.readInt32();
                                continue;
                            }
                            case 16: {
                                this.bitField0_ |= 0x2;
                                this.end_ = input.readInt32();
                                continue;
                            }
                        }
                    }
                }
                catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                }
                catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                }
                finally {
                    this.unknownFields = unknownFields.build();
                    this.makeExtensionsImmutable();
                }
            }
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_fieldAccessorTable.ensureFieldAccessorsInitialized(ExtensionRange.class, Builder.class);
            }
            
            @Override
            public Parser<ExtensionRange> getParserForType() {
                return ExtensionRange.PARSER;
            }
            
            public boolean hasStart() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public int getStart() {
                return this.start_;
            }
            
            public boolean hasEnd() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            public int getEnd() {
                return this.end_;
            }
            
            private void initFields() {
                this.start_ = 0;
                this.end_ = 0;
            }
            
            @Override
            public final boolean isInitialized() {
                final byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized != -1) {
                    return isInitialized == 1;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }
            
            @Override
            public void writeTo(final CodedOutputStream output) throws IOException {
                this.getSerializedSize();
                if ((this.bitField0_ & 0x1) == 0x1) {
                    output.writeInt32(1, this.start_);
                }
                if ((this.bitField0_ & 0x2) == 0x2) {
                    output.writeInt32(2, this.end_);
                }
                this.getUnknownFields().writeTo(output);
            }
            
            @Override
            public int getSerializedSize() {
                int size = this.memoizedSerializedSize;
                if (size != -1) {
                    return size;
                }
                size = 0;
                if ((this.bitField0_ & 0x1) == 0x1) {
                    size += CodedOutputStream.computeInt32Size(1, this.start_);
                }
                if ((this.bitField0_ & 0x2) == 0x2) {
                    size += CodedOutputStream.computeInt32Size(2, this.end_);
                }
                size += this.getUnknownFields().getSerializedSize();
                return this.memoizedSerializedSize = size;
            }
            
            @Override
            protected Object writeReplace() throws ObjectStreamException {
                return super.writeReplace();
            }
            
            public static ExtensionRange parseFrom(final ByteString data) throws InvalidProtocolBufferException {
                return ExtensionRange.PARSER.parseFrom(data);
            }
            
            public static ExtensionRange parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return ExtensionRange.PARSER.parseFrom(data, extensionRegistry);
            }
            
            public static ExtensionRange parseFrom(final byte[] data) throws InvalidProtocolBufferException {
                return ExtensionRange.PARSER.parseFrom(data);
            }
            
            public static ExtensionRange parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return ExtensionRange.PARSER.parseFrom(data, extensionRegistry);
            }
            
            public static ExtensionRange parseFrom(final InputStream input) throws IOException {
                return ExtensionRange.PARSER.parseFrom(input);
            }
            
            public static ExtensionRange parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                return ExtensionRange.PARSER.parseFrom(input, extensionRegistry);
            }
            
            public static ExtensionRange parseDelimitedFrom(final InputStream input) throws IOException {
                return ExtensionRange.PARSER.parseDelimitedFrom(input);
            }
            
            public static ExtensionRange parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                return ExtensionRange.PARSER.parseDelimitedFrom(input, extensionRegistry);
            }
            
            public static ExtensionRange parseFrom(final CodedInputStream input) throws IOException {
                return ExtensionRange.PARSER.parseFrom(input);
            }
            
            public static ExtensionRange parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                return ExtensionRange.PARSER.parseFrom(input, extensionRegistry);
            }
            
            public static Builder newBuilder() {
                return create();
            }
            
            public Builder newBuilderForType() {
                return newBuilder();
            }
            
            public static Builder newBuilder(final ExtensionRange prototype) {
                return newBuilder().mergeFrom(prototype);
            }
            
            public Builder toBuilder() {
                return newBuilder(this);
            }
            
            @Override
            protected Builder newBuilderForType(final BuilderParent parent) {
                final Builder builder = new Builder(parent);
                return builder;
            }
            
            static {
                ExtensionRange.PARSER = new AbstractParser<ExtensionRange>() {
                    public ExtensionRange parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                        return new ExtensionRange(input, extensionRegistry);
                    }
                };
                (defaultInstance = new ExtensionRange(true)).initFields();
            }
            
            public static final class Builder extends GeneratedMessage.Builder<Builder> implements ExtensionRangeOrBuilder
            {
                private int bitField0_;
                private int start_;
                private int end_;
                
                public static final Descriptors.Descriptor getDescriptor() {
                    return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_descriptor;
                }
                
                @Override
                protected FieldAccessorTable internalGetFieldAccessorTable() {
                    return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_fieldAccessorTable.ensureFieldAccessorsInitialized(ExtensionRange.class, Builder.class);
                }
                
                private Builder() {
                    this.maybeForceBuilderInitialization();
                }
                
                private Builder(final BuilderParent parent) {
                    super(parent);
                    this.maybeForceBuilderInitialization();
                }
                
                private void maybeForceBuilderInitialization() {
                    if (GeneratedMessage.alwaysUseFieldBuilders) {}
                }
                
                private static Builder create() {
                    return new Builder();
                }
                
                @Override
                public Builder clear() {
                    super.clear();
                    this.start_ = 0;
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.end_ = 0;
                    this.bitField0_ &= 0xFFFFFFFD;
                    return this;
                }
                
                @Override
                public Builder clone() {
                    return create().mergeFrom(this.buildPartial());
                }
                
                @Override
                public Descriptors.Descriptor getDescriptorForType() {
                    return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_ExtensionRange_descriptor;
                }
                
                public ExtensionRange getDefaultInstanceForType() {
                    return ExtensionRange.getDefaultInstance();
                }
                
                public ExtensionRange build() {
                    final ExtensionRange result = this.buildPartial();
                    if (!result.isInitialized()) {
                        throw AbstractMessage.Builder.newUninitializedMessageException(result);
                    }
                    return result;
                }
                
                public ExtensionRange buildPartial() {
                    final ExtensionRange result = new ExtensionRange((GeneratedMessage.Builder)this);
                    final int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 0x1) == 0x1) {
                        to_bitField0_ |= 0x1;
                    }
                    result.start_ = this.start_;
                    if ((from_bitField0_ & 0x2) == 0x2) {
                        to_bitField0_ |= 0x2;
                    }
                    result.end_ = this.end_;
                    result.bitField0_ = to_bitField0_;
                    this.onBuilt();
                    return result;
                }
                
                @Override
                public Builder mergeFrom(final Message other) {
                    if (other instanceof ExtensionRange) {
                        return this.mergeFrom((ExtensionRange)other);
                    }
                    super.mergeFrom(other);
                    return this;
                }
                
                public Builder mergeFrom(final ExtensionRange other) {
                    if (other == ExtensionRange.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasStart()) {
                        this.setStart(other.getStart());
                    }
                    if (other.hasEnd()) {
                        this.setEnd(other.getEnd());
                    }
                    this.mergeUnknownFields(other.getUnknownFields());
                    return this;
                }
                
                @Override
                public final boolean isInitialized() {
                    return true;
                }
                
                @Override
                public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                    ExtensionRange parsedMessage = null;
                    try {
                        parsedMessage = ExtensionRange.PARSER.parsePartialFrom(input, extensionRegistry);
                    }
                    catch (InvalidProtocolBufferException e) {
                        parsedMessage = (ExtensionRange)e.getUnfinishedMessage();
                        throw e;
                    }
                    finally {
                        if (parsedMessage != null) {
                            this.mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }
                
                public boolean hasStart() {
                    return (this.bitField0_ & 0x1) == 0x1;
                }
                
                public int getStart() {
                    return this.start_;
                }
                
                public Builder setStart(final int value) {
                    this.bitField0_ |= 0x1;
                    this.start_ = value;
                    this.onChanged();
                    return this;
                }
                
                public Builder clearStart() {
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.start_ = 0;
                    this.onChanged();
                    return this;
                }
                
                public boolean hasEnd() {
                    return (this.bitField0_ & 0x2) == 0x2;
                }
                
                public int getEnd() {
                    return this.end_;
                }
                
                public Builder setEnd(final int value) {
                    this.bitField0_ |= 0x2;
                    this.end_ = value;
                    this.onChanged();
                    return this;
                }
                
                public Builder clearEnd() {
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.end_ = 0;
                    this.onChanged();
                    return this;
                }
            }
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements DescriptorProtoOrBuilder
        {
            private int bitField0_;
            private Object name_;
            private List<FieldDescriptorProto> field_;
            private RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> fieldBuilder_;
            private List<FieldDescriptorProto> extension_;
            private RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> extensionBuilder_;
            private List<DescriptorProto> nestedType_;
            private RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> nestedTypeBuilder_;
            private List<EnumDescriptorProto> enumType_;
            private RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> enumTypeBuilder_;
            private List<ExtensionRange> extensionRange_;
            private RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> extensionRangeBuilder_;
            private MessageOptions options_;
            private SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder> optionsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(DescriptorProto.class, Builder.class);
            }
            
            private Builder() {
                this.name_ = "";
                this.field_ = Collections.emptyList();
                this.extension_ = Collections.emptyList();
                this.nestedType_ = Collections.emptyList();
                this.enumType_ = Collections.emptyList();
                this.extensionRange_ = Collections.emptyList();
                this.options_ = MessageOptions.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.field_ = Collections.emptyList();
                this.extension_ = Collections.emptyList();
                this.nestedType_ = Collections.emptyList();
                this.enumType_ = Collections.emptyList();
                this.extensionRange_ = Collections.emptyList();
                this.options_ = MessageOptions.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getFieldFieldBuilder();
                    this.getExtensionFieldBuilder();
                    this.getNestedTypeFieldBuilder();
                    this.getEnumTypeFieldBuilder();
                    this.getExtensionRangeFieldBuilder();
                    this.getOptionsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.fieldBuilder_ == null) {
                    this.field_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                }
                else {
                    this.fieldBuilder_.clear();
                }
                if (this.extensionBuilder_ == null) {
                    this.extension_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFB;
                }
                else {
                    this.extensionBuilder_.clear();
                }
                if (this.nestedTypeBuilder_ == null) {
                    this.nestedType_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFF7;
                }
                else {
                    this.nestedTypeBuilder_.clear();
                }
                if (this.enumTypeBuilder_ == null) {
                    this.enumType_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFEF;
                }
                else {
                    this.enumTypeBuilder_.clear();
                }
                if (this.extensionRangeBuilder_ == null) {
                    this.extensionRange_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFDF;
                }
                else {
                    this.extensionRangeBuilder_.clear();
                }
                if (this.optionsBuilder_ == null) {
                    this.options_ = MessageOptions.getDefaultInstance();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFBF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_DescriptorProto_descriptor;
            }
            
            public DescriptorProto getDefaultInstanceForType() {
                return DescriptorProto.getDefaultInstance();
            }
            
            public DescriptorProto build() {
                final DescriptorProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public DescriptorProto buildPartial() {
                final DescriptorProto result = new DescriptorProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.name_ = this.name_;
                if (this.fieldBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2) {
                        this.field_ = Collections.unmodifiableList((List<? extends FieldDescriptorProto>)this.field_);
                        this.bitField0_ &= 0xFFFFFFFD;
                    }
                    result.field_ = this.field_;
                }
                else {
                    result.field_ = this.fieldBuilder_.build();
                }
                if (this.extensionBuilder_ == null) {
                    if ((this.bitField0_ & 0x4) == 0x4) {
                        this.extension_ = Collections.unmodifiableList((List<? extends FieldDescriptorProto>)this.extension_);
                        this.bitField0_ &= 0xFFFFFFFB;
                    }
                    result.extension_ = this.extension_;
                }
                else {
                    result.extension_ = this.extensionBuilder_.build();
                }
                if (this.nestedTypeBuilder_ == null) {
                    if ((this.bitField0_ & 0x8) == 0x8) {
                        this.nestedType_ = Collections.unmodifiableList((List<? extends DescriptorProto>)this.nestedType_);
                        this.bitField0_ &= 0xFFFFFFF7;
                    }
                    result.nestedType_ = this.nestedType_;
                }
                else {
                    result.nestedType_ = this.nestedTypeBuilder_.build();
                }
                if (this.enumTypeBuilder_ == null) {
                    if ((this.bitField0_ & 0x10) == 0x10) {
                        this.enumType_ = Collections.unmodifiableList((List<? extends EnumDescriptorProto>)this.enumType_);
                        this.bitField0_ &= 0xFFFFFFEF;
                    }
                    result.enumType_ = this.enumType_;
                }
                else {
                    result.enumType_ = this.enumTypeBuilder_.build();
                }
                if (this.extensionRangeBuilder_ == null) {
                    if ((this.bitField0_ & 0x20) == 0x20) {
                        this.extensionRange_ = Collections.unmodifiableList((List<? extends ExtensionRange>)this.extensionRange_);
                        this.bitField0_ &= 0xFFFFFFDF;
                    }
                    result.extensionRange_ = this.extensionRange_;
                }
                else {
                    result.extensionRange_ = this.extensionRangeBuilder_.build();
                }
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x2;
                }
                if (this.optionsBuilder_ == null) {
                    result.options_ = this.options_;
                }
                else {
                    result.options_ = this.optionsBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof DescriptorProto) {
                    return this.mergeFrom((DescriptorProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final DescriptorProto other) {
                if (other == DescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 0x1;
                    this.name_ = other.name_;
                    this.onChanged();
                }
                if (this.fieldBuilder_ == null) {
                    if (!other.field_.isEmpty()) {
                        if (this.field_.isEmpty()) {
                            this.field_ = other.field_;
                            this.bitField0_ &= 0xFFFFFFFD;
                        }
                        else {
                            this.ensureFieldIsMutable();
                            this.field_.addAll(other.field_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.field_.isEmpty()) {
                    if (this.fieldBuilder_.isEmpty()) {
                        this.fieldBuilder_.dispose();
                        this.fieldBuilder_ = null;
                        this.field_ = other.field_;
                        this.bitField0_ &= 0xFFFFFFFD;
                        this.fieldBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getFieldFieldBuilder() : null);
                    }
                    else {
                        this.fieldBuilder_.addAllMessages(other.field_);
                    }
                }
                if (this.extensionBuilder_ == null) {
                    if (!other.extension_.isEmpty()) {
                        if (this.extension_.isEmpty()) {
                            this.extension_ = other.extension_;
                            this.bitField0_ &= 0xFFFFFFFB;
                        }
                        else {
                            this.ensureExtensionIsMutable();
                            this.extension_.addAll(other.extension_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.extension_.isEmpty()) {
                    if (this.extensionBuilder_.isEmpty()) {
                        this.extensionBuilder_.dispose();
                        this.extensionBuilder_ = null;
                        this.extension_ = other.extension_;
                        this.bitField0_ &= 0xFFFFFFFB;
                        this.extensionBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getExtensionFieldBuilder() : null);
                    }
                    else {
                        this.extensionBuilder_.addAllMessages(other.extension_);
                    }
                }
                if (this.nestedTypeBuilder_ == null) {
                    if (!other.nestedType_.isEmpty()) {
                        if (this.nestedType_.isEmpty()) {
                            this.nestedType_ = other.nestedType_;
                            this.bitField0_ &= 0xFFFFFFF7;
                        }
                        else {
                            this.ensureNestedTypeIsMutable();
                            this.nestedType_.addAll(other.nestedType_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.nestedType_.isEmpty()) {
                    if (this.nestedTypeBuilder_.isEmpty()) {
                        this.nestedTypeBuilder_.dispose();
                        this.nestedTypeBuilder_ = null;
                        this.nestedType_ = other.nestedType_;
                        this.bitField0_ &= 0xFFFFFFF7;
                        this.nestedTypeBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getNestedTypeFieldBuilder() : null);
                    }
                    else {
                        this.nestedTypeBuilder_.addAllMessages(other.nestedType_);
                    }
                }
                if (this.enumTypeBuilder_ == null) {
                    if (!other.enumType_.isEmpty()) {
                        if (this.enumType_.isEmpty()) {
                            this.enumType_ = other.enumType_;
                            this.bitField0_ &= 0xFFFFFFEF;
                        }
                        else {
                            this.ensureEnumTypeIsMutable();
                            this.enumType_.addAll(other.enumType_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.enumType_.isEmpty()) {
                    if (this.enumTypeBuilder_.isEmpty()) {
                        this.enumTypeBuilder_.dispose();
                        this.enumTypeBuilder_ = null;
                        this.enumType_ = other.enumType_;
                        this.bitField0_ &= 0xFFFFFFEF;
                        this.enumTypeBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getEnumTypeFieldBuilder() : null);
                    }
                    else {
                        this.enumTypeBuilder_.addAllMessages(other.enumType_);
                    }
                }
                if (this.extensionRangeBuilder_ == null) {
                    if (!other.extensionRange_.isEmpty()) {
                        if (this.extensionRange_.isEmpty()) {
                            this.extensionRange_ = other.extensionRange_;
                            this.bitField0_ &= 0xFFFFFFDF;
                        }
                        else {
                            this.ensureExtensionRangeIsMutable();
                            this.extensionRange_.addAll(other.extensionRange_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.extensionRange_.isEmpty()) {
                    if (this.extensionRangeBuilder_.isEmpty()) {
                        this.extensionRangeBuilder_.dispose();
                        this.extensionRangeBuilder_ = null;
                        this.extensionRange_ = other.extensionRange_;
                        this.bitField0_ &= 0xFFFFFFDF;
                        this.extensionRangeBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getExtensionRangeFieldBuilder() : null);
                    }
                    else {
                        this.extensionRangeBuilder_.addAllMessages(other.extensionRange_);
                    }
                }
                if (other.hasOptions()) {
                    this.mergeOptions(other.getOptions());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getFieldCount(); ++i) {
                    if (!this.getField(i).isInitialized()) {
                        return false;
                    }
                }
                for (int i = 0; i < this.getExtensionCount(); ++i) {
                    if (!this.getExtension(i).isInitialized()) {
                        return false;
                    }
                }
                for (int i = 0; i < this.getNestedTypeCount(); ++i) {
                    if (!this.getNestedType(i).isInitialized()) {
                        return false;
                    }
                }
                for (int i = 0; i < this.getEnumTypeCount(); ++i) {
                    if (!this.getEnumType(i).isInitialized()) {
                        return false;
                    }
                }
                return !this.hasOptions() || this.getOptions().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                DescriptorProto parsedMessage = null;
                try {
                    parsedMessage = DescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (DescriptorProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            public boolean hasName() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public String getName() {
                final Object ref = this.name_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.name_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getNameBytes() {
                final Object ref = this.name_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.name_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearName() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.name_ = DescriptorProto.getDefaultInstance().getName();
                this.onChanged();
                return this;
            }
            
            public Builder setNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            private void ensureFieldIsMutable() {
                if ((this.bitField0_ & 0x2) != 0x2) {
                    this.field_ = new ArrayList<FieldDescriptorProto>(this.field_);
                    this.bitField0_ |= 0x2;
                }
            }
            
            public List<FieldDescriptorProto> getFieldList() {
                if (this.fieldBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends FieldDescriptorProto>)this.field_);
                }
                return this.fieldBuilder_.getMessageList();
            }
            
            public int getFieldCount() {
                if (this.fieldBuilder_ == null) {
                    return this.field_.size();
                }
                return this.fieldBuilder_.getCount();
            }
            
            public FieldDescriptorProto getField(final int index) {
                if (this.fieldBuilder_ == null) {
                    return this.field_.get(index);
                }
                return this.fieldBuilder_.getMessage(index);
            }
            
            public Builder setField(final int index, final FieldDescriptorProto value) {
                if (this.fieldBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureFieldIsMutable();
                    this.field_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.fieldBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setField(final int index, final FieldDescriptorProto.Builder builderForValue) {
                if (this.fieldBuilder_ == null) {
                    this.ensureFieldIsMutable();
                    this.field_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.fieldBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addField(final FieldDescriptorProto value) {
                if (this.fieldBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureFieldIsMutable();
                    this.field_.add(value);
                    this.onChanged();
                }
                else {
                    this.fieldBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addField(final int index, final FieldDescriptorProto value) {
                if (this.fieldBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureFieldIsMutable();
                    this.field_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.fieldBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addField(final FieldDescriptorProto.Builder builderForValue) {
                if (this.fieldBuilder_ == null) {
                    this.ensureFieldIsMutable();
                    this.field_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.fieldBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addField(final int index, final FieldDescriptorProto.Builder builderForValue) {
                if (this.fieldBuilder_ == null) {
                    this.ensureFieldIsMutable();
                    this.field_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.fieldBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllField(final Iterable<? extends FieldDescriptorProto> values) {
                if (this.fieldBuilder_ == null) {
                    this.ensureFieldIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.field_);
                    this.onChanged();
                }
                else {
                    this.fieldBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearField() {
                if (this.fieldBuilder_ == null) {
                    this.field_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.onChanged();
                }
                else {
                    this.fieldBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeField(final int index) {
                if (this.fieldBuilder_ == null) {
                    this.ensureFieldIsMutable();
                    this.field_.remove(index);
                    this.onChanged();
                }
                else {
                    this.fieldBuilder_.remove(index);
                }
                return this;
            }
            
            public FieldDescriptorProto.Builder getFieldBuilder(final int index) {
                return this.getFieldFieldBuilder().getBuilder(index);
            }
            
            public FieldDescriptorProtoOrBuilder getFieldOrBuilder(final int index) {
                if (this.fieldBuilder_ == null) {
                    return this.field_.get(index);
                }
                return this.fieldBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends FieldDescriptorProtoOrBuilder> getFieldOrBuilderList() {
                if (this.fieldBuilder_ != null) {
                    return this.fieldBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends FieldDescriptorProtoOrBuilder>)this.field_);
            }
            
            public FieldDescriptorProto.Builder addFieldBuilder() {
                return this.getFieldFieldBuilder().addBuilder(FieldDescriptorProto.getDefaultInstance());
            }
            
            public FieldDescriptorProto.Builder addFieldBuilder(final int index) {
                return this.getFieldFieldBuilder().addBuilder(index, FieldDescriptorProto.getDefaultInstance());
            }
            
            public List<FieldDescriptorProto.Builder> getFieldBuilderList() {
                return this.getFieldFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> getFieldFieldBuilder() {
                if (this.fieldBuilder_ == null) {
                    this.fieldBuilder_ = new RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder>(this.field_, (this.bitField0_ & 0x2) == 0x2, this.getParentForChildren(), this.isClean());
                    this.field_ = null;
                }
                return this.fieldBuilder_;
            }
            
            private void ensureExtensionIsMutable() {
                if ((this.bitField0_ & 0x4) != 0x4) {
                    this.extension_ = new ArrayList<FieldDescriptorProto>(this.extension_);
                    this.bitField0_ |= 0x4;
                }
            }
            
            public List<FieldDescriptorProto> getExtensionList() {
                if (this.extensionBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends FieldDescriptorProto>)this.extension_);
                }
                return this.extensionBuilder_.getMessageList();
            }
            
            public int getExtensionCount() {
                if (this.extensionBuilder_ == null) {
                    return this.extension_.size();
                }
                return this.extensionBuilder_.getCount();
            }
            
            public FieldDescriptorProto getExtension(final int index) {
                if (this.extensionBuilder_ == null) {
                    return this.extension_.get(index);
                }
                return this.extensionBuilder_.getMessage(index);
            }
            
            public Builder setExtension(final int index, final FieldDescriptorProto value) {
                if (this.extensionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureExtensionIsMutable();
                    this.extension_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setExtension(final int index, final FieldDescriptorProto.Builder builderForValue) {
                if (this.extensionBuilder_ == null) {
                    this.ensureExtensionIsMutable();
                    this.extension_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addExtension(final FieldDescriptorProto value) {
                if (this.extensionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureExtensionIsMutable();
                    this.extension_.add(value);
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addExtension(final int index, final FieldDescriptorProto value) {
                if (this.extensionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureExtensionIsMutable();
                    this.extension_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addExtension(final FieldDescriptorProto.Builder builderForValue) {
                if (this.extensionBuilder_ == null) {
                    this.ensureExtensionIsMutable();
                    this.extension_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addExtension(final int index, final FieldDescriptorProto.Builder builderForValue) {
                if (this.extensionBuilder_ == null) {
                    this.ensureExtensionIsMutable();
                    this.extension_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllExtension(final Iterable<? extends FieldDescriptorProto> values) {
                if (this.extensionBuilder_ == null) {
                    this.ensureExtensionIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.extension_);
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearExtension() {
                if (this.extensionBuilder_ == null) {
                    this.extension_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFB;
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeExtension(final int index) {
                if (this.extensionBuilder_ == null) {
                    this.ensureExtensionIsMutable();
                    this.extension_.remove(index);
                    this.onChanged();
                }
                else {
                    this.extensionBuilder_.remove(index);
                }
                return this;
            }
            
            public FieldDescriptorProto.Builder getExtensionBuilder(final int index) {
                return this.getExtensionFieldBuilder().getBuilder(index);
            }
            
            public FieldDescriptorProtoOrBuilder getExtensionOrBuilder(final int index) {
                if (this.extensionBuilder_ == null) {
                    return this.extension_.get(index);
                }
                return this.extensionBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList() {
                if (this.extensionBuilder_ != null) {
                    return this.extensionBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends FieldDescriptorProtoOrBuilder>)this.extension_);
            }
            
            public FieldDescriptorProto.Builder addExtensionBuilder() {
                return this.getExtensionFieldBuilder().addBuilder(FieldDescriptorProto.getDefaultInstance());
            }
            
            public FieldDescriptorProto.Builder addExtensionBuilder(final int index) {
                return this.getExtensionFieldBuilder().addBuilder(index, FieldDescriptorProto.getDefaultInstance());
            }
            
            public List<FieldDescriptorProto.Builder> getExtensionBuilderList() {
                return this.getExtensionFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder> getExtensionFieldBuilder() {
                if (this.extensionBuilder_ == null) {
                    this.extensionBuilder_ = new RepeatedFieldBuilder<FieldDescriptorProto, FieldDescriptorProto.Builder, FieldDescriptorProtoOrBuilder>(this.extension_, (this.bitField0_ & 0x4) == 0x4, this.getParentForChildren(), this.isClean());
                    this.extension_ = null;
                }
                return this.extensionBuilder_;
            }
            
            private void ensureNestedTypeIsMutable() {
                if ((this.bitField0_ & 0x8) != 0x8) {
                    this.nestedType_ = new ArrayList<DescriptorProto>(this.nestedType_);
                    this.bitField0_ |= 0x8;
                }
            }
            
            public List<DescriptorProto> getNestedTypeList() {
                if (this.nestedTypeBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends DescriptorProto>)this.nestedType_);
                }
                return this.nestedTypeBuilder_.getMessageList();
            }
            
            public int getNestedTypeCount() {
                if (this.nestedTypeBuilder_ == null) {
                    return this.nestedType_.size();
                }
                return this.nestedTypeBuilder_.getCount();
            }
            
            public DescriptorProto getNestedType(final int index) {
                if (this.nestedTypeBuilder_ == null) {
                    return this.nestedType_.get(index);
                }
                return this.nestedTypeBuilder_.getMessage(index);
            }
            
            public Builder setNestedType(final int index, final DescriptorProto value) {
                if (this.nestedTypeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureNestedTypeIsMutable();
                    this.nestedType_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.nestedTypeBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setNestedType(final int index, final Builder builderForValue) {
                if (this.nestedTypeBuilder_ == null) {
                    this.ensureNestedTypeIsMutable();
                    this.nestedType_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.nestedTypeBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addNestedType(final DescriptorProto value) {
                if (this.nestedTypeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureNestedTypeIsMutable();
                    this.nestedType_.add(value);
                    this.onChanged();
                }
                else {
                    this.nestedTypeBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addNestedType(final int index, final DescriptorProto value) {
                if (this.nestedTypeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureNestedTypeIsMutable();
                    this.nestedType_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.nestedTypeBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addNestedType(final Builder builderForValue) {
                if (this.nestedTypeBuilder_ == null) {
                    this.ensureNestedTypeIsMutable();
                    this.nestedType_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.nestedTypeBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addNestedType(final int index, final Builder builderForValue) {
                if (this.nestedTypeBuilder_ == null) {
                    this.ensureNestedTypeIsMutable();
                    this.nestedType_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.nestedTypeBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllNestedType(final Iterable<? extends DescriptorProto> values) {
                if (this.nestedTypeBuilder_ == null) {
                    this.ensureNestedTypeIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.nestedType_);
                    this.onChanged();
                }
                else {
                    this.nestedTypeBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearNestedType() {
                if (this.nestedTypeBuilder_ == null) {
                    this.nestedType_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFF7;
                    this.onChanged();
                }
                else {
                    this.nestedTypeBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeNestedType(final int index) {
                if (this.nestedTypeBuilder_ == null) {
                    this.ensureNestedTypeIsMutable();
                    this.nestedType_.remove(index);
                    this.onChanged();
                }
                else {
                    this.nestedTypeBuilder_.remove(index);
                }
                return this;
            }
            
            public Builder getNestedTypeBuilder(final int index) {
                return this.getNestedTypeFieldBuilder().getBuilder(index);
            }
            
            public DescriptorProtoOrBuilder getNestedTypeOrBuilder(final int index) {
                if (this.nestedTypeBuilder_ == null) {
                    return this.nestedType_.get(index);
                }
                return this.nestedTypeBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends DescriptorProtoOrBuilder> getNestedTypeOrBuilderList() {
                if (this.nestedTypeBuilder_ != null) {
                    return this.nestedTypeBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends DescriptorProtoOrBuilder>)this.nestedType_);
            }
            
            public Builder addNestedTypeBuilder() {
                return this.getNestedTypeFieldBuilder().addBuilder(DescriptorProto.getDefaultInstance());
            }
            
            public Builder addNestedTypeBuilder(final int index) {
                return this.getNestedTypeFieldBuilder().addBuilder(index, DescriptorProto.getDefaultInstance());
            }
            
            public List<Builder> getNestedTypeBuilderList() {
                return this.getNestedTypeFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder> getNestedTypeFieldBuilder() {
                if (this.nestedTypeBuilder_ == null) {
                    this.nestedTypeBuilder_ = new RepeatedFieldBuilder<DescriptorProto, Builder, DescriptorProtoOrBuilder>(this.nestedType_, (this.bitField0_ & 0x8) == 0x8, this.getParentForChildren(), this.isClean());
                    this.nestedType_ = null;
                }
                return this.nestedTypeBuilder_;
            }
            
            private void ensureEnumTypeIsMutable() {
                if ((this.bitField0_ & 0x10) != 0x10) {
                    this.enumType_ = new ArrayList<EnumDescriptorProto>(this.enumType_);
                    this.bitField0_ |= 0x10;
                }
            }
            
            public List<EnumDescriptorProto> getEnumTypeList() {
                if (this.enumTypeBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends EnumDescriptorProto>)this.enumType_);
                }
                return this.enumTypeBuilder_.getMessageList();
            }
            
            public int getEnumTypeCount() {
                if (this.enumTypeBuilder_ == null) {
                    return this.enumType_.size();
                }
                return this.enumTypeBuilder_.getCount();
            }
            
            public EnumDescriptorProto getEnumType(final int index) {
                if (this.enumTypeBuilder_ == null) {
                    return this.enumType_.get(index);
                }
                return this.enumTypeBuilder_.getMessage(index);
            }
            
            public Builder setEnumType(final int index, final EnumDescriptorProto value) {
                if (this.enumTypeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setEnumType(final int index, final EnumDescriptorProto.Builder builderForValue) {
                if (this.enumTypeBuilder_ == null) {
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addEnumType(final EnumDescriptorProto value) {
                if (this.enumTypeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.add(value);
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addEnumType(final int index, final EnumDescriptorProto value) {
                if (this.enumTypeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addEnumType(final EnumDescriptorProto.Builder builderForValue) {
                if (this.enumTypeBuilder_ == null) {
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addEnumType(final int index, final EnumDescriptorProto.Builder builderForValue) {
                if (this.enumTypeBuilder_ == null) {
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllEnumType(final Iterable<? extends EnumDescriptorProto> values) {
                if (this.enumTypeBuilder_ == null) {
                    this.ensureEnumTypeIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.enumType_);
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearEnumType() {
                if (this.enumTypeBuilder_ == null) {
                    this.enumType_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFEF;
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeEnumType(final int index) {
                if (this.enumTypeBuilder_ == null) {
                    this.ensureEnumTypeIsMutable();
                    this.enumType_.remove(index);
                    this.onChanged();
                }
                else {
                    this.enumTypeBuilder_.remove(index);
                }
                return this;
            }
            
            public EnumDescriptorProto.Builder getEnumTypeBuilder(final int index) {
                return this.getEnumTypeFieldBuilder().getBuilder(index);
            }
            
            public EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(final int index) {
                if (this.enumTypeBuilder_ == null) {
                    return this.enumType_.get(index);
                }
                return this.enumTypeBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList() {
                if (this.enumTypeBuilder_ != null) {
                    return this.enumTypeBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends EnumDescriptorProtoOrBuilder>)this.enumType_);
            }
            
            public EnumDescriptorProto.Builder addEnumTypeBuilder() {
                return this.getEnumTypeFieldBuilder().addBuilder(EnumDescriptorProto.getDefaultInstance());
            }
            
            public EnumDescriptorProto.Builder addEnumTypeBuilder(final int index) {
                return this.getEnumTypeFieldBuilder().addBuilder(index, EnumDescriptorProto.getDefaultInstance());
            }
            
            public List<EnumDescriptorProto.Builder> getEnumTypeBuilderList() {
                return this.getEnumTypeFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder> getEnumTypeFieldBuilder() {
                if (this.enumTypeBuilder_ == null) {
                    this.enumTypeBuilder_ = new RepeatedFieldBuilder<EnumDescriptorProto, EnumDescriptorProto.Builder, EnumDescriptorProtoOrBuilder>(this.enumType_, (this.bitField0_ & 0x10) == 0x10, this.getParentForChildren(), this.isClean());
                    this.enumType_ = null;
                }
                return this.enumTypeBuilder_;
            }
            
            private void ensureExtensionRangeIsMutable() {
                if ((this.bitField0_ & 0x20) != 0x20) {
                    this.extensionRange_ = new ArrayList<ExtensionRange>(this.extensionRange_);
                    this.bitField0_ |= 0x20;
                }
            }
            
            public List<ExtensionRange> getExtensionRangeList() {
                if (this.extensionRangeBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends ExtensionRange>)this.extensionRange_);
                }
                return this.extensionRangeBuilder_.getMessageList();
            }
            
            public int getExtensionRangeCount() {
                if (this.extensionRangeBuilder_ == null) {
                    return this.extensionRange_.size();
                }
                return this.extensionRangeBuilder_.getCount();
            }
            
            public ExtensionRange getExtensionRange(final int index) {
                if (this.extensionRangeBuilder_ == null) {
                    return this.extensionRange_.get(index);
                }
                return this.extensionRangeBuilder_.getMessage(index);
            }
            
            public Builder setExtensionRange(final int index, final ExtensionRange value) {
                if (this.extensionRangeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureExtensionRangeIsMutable();
                    this.extensionRange_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.extensionRangeBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setExtensionRange(final int index, final ExtensionRange.Builder builderForValue) {
                if (this.extensionRangeBuilder_ == null) {
                    this.ensureExtensionRangeIsMutable();
                    this.extensionRange_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.extensionRangeBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addExtensionRange(final ExtensionRange value) {
                if (this.extensionRangeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureExtensionRangeIsMutable();
                    this.extensionRange_.add(value);
                    this.onChanged();
                }
                else {
                    this.extensionRangeBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addExtensionRange(final int index, final ExtensionRange value) {
                if (this.extensionRangeBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureExtensionRangeIsMutable();
                    this.extensionRange_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.extensionRangeBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addExtensionRange(final ExtensionRange.Builder builderForValue) {
                if (this.extensionRangeBuilder_ == null) {
                    this.ensureExtensionRangeIsMutable();
                    this.extensionRange_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.extensionRangeBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addExtensionRange(final int index, final ExtensionRange.Builder builderForValue) {
                if (this.extensionRangeBuilder_ == null) {
                    this.ensureExtensionRangeIsMutable();
                    this.extensionRange_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.extensionRangeBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllExtensionRange(final Iterable<? extends ExtensionRange> values) {
                if (this.extensionRangeBuilder_ == null) {
                    this.ensureExtensionRangeIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.extensionRange_);
                    this.onChanged();
                }
                else {
                    this.extensionRangeBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearExtensionRange() {
                if (this.extensionRangeBuilder_ == null) {
                    this.extensionRange_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFDF;
                    this.onChanged();
                }
                else {
                    this.extensionRangeBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeExtensionRange(final int index) {
                if (this.extensionRangeBuilder_ == null) {
                    this.ensureExtensionRangeIsMutable();
                    this.extensionRange_.remove(index);
                    this.onChanged();
                }
                else {
                    this.extensionRangeBuilder_.remove(index);
                }
                return this;
            }
            
            public ExtensionRange.Builder getExtensionRangeBuilder(final int index) {
                return this.getExtensionRangeFieldBuilder().getBuilder(index);
            }
            
            public ExtensionRangeOrBuilder getExtensionRangeOrBuilder(final int index) {
                if (this.extensionRangeBuilder_ == null) {
                    return this.extensionRange_.get(index);
                }
                return this.extensionRangeBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends ExtensionRangeOrBuilder> getExtensionRangeOrBuilderList() {
                if (this.extensionRangeBuilder_ != null) {
                    return this.extensionRangeBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends ExtensionRangeOrBuilder>)this.extensionRange_);
            }
            
            public ExtensionRange.Builder addExtensionRangeBuilder() {
                return this.getExtensionRangeFieldBuilder().addBuilder(ExtensionRange.getDefaultInstance());
            }
            
            public ExtensionRange.Builder addExtensionRangeBuilder(final int index) {
                return this.getExtensionRangeFieldBuilder().addBuilder(index, ExtensionRange.getDefaultInstance());
            }
            
            public List<ExtensionRange.Builder> getExtensionRangeBuilderList() {
                return this.getExtensionRangeFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder> getExtensionRangeFieldBuilder() {
                if (this.extensionRangeBuilder_ == null) {
                    this.extensionRangeBuilder_ = new RepeatedFieldBuilder<ExtensionRange, ExtensionRange.Builder, ExtensionRangeOrBuilder>(this.extensionRange_, (this.bitField0_ & 0x20) == 0x20, this.getParentForChildren(), this.isClean());
                    this.extensionRange_ = null;
                }
                return this.extensionRangeBuilder_;
            }
            
            public boolean hasOptions() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            public MessageOptions getOptions() {
                if (this.optionsBuilder_ == null) {
                    return this.options_;
                }
                return this.optionsBuilder_.getMessage();
            }
            
            public Builder setOptions(final MessageOptions value) {
                if (this.optionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.options_ = value;
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x40;
                return this;
            }
            
            public Builder setOptions(final MessageOptions.Builder builderForValue) {
                if (this.optionsBuilder_ == null) {
                    this.options_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x40;
                return this;
            }
            
            public Builder mergeOptions(final MessageOptions value) {
                if (this.optionsBuilder_ == null) {
                    if ((this.bitField0_ & 0x40) == 0x40 && this.options_ != MessageOptions.getDefaultInstance()) {
                        this.options_ = MessageOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.options_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x40;
                return this;
            }
            
            public Builder clearOptions() {
                if (this.optionsBuilder_ == null) {
                    this.options_ = MessageOptions.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFBF;
                return this;
            }
            
            public MessageOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 0x40;
                this.onChanged();
                return this.getOptionsFieldBuilder().getBuilder();
            }
            
            public MessageOptionsOrBuilder getOptionsOrBuilder() {
                if (this.optionsBuilder_ != null) {
                    return this.optionsBuilder_.getMessageOrBuilder();
                }
                return this.options_;
            }
            
            private SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<MessageOptions, MessageOptions.Builder, MessageOptionsOrBuilder>(this.options_, this.getParentForChildren(), this.isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
        }
        
        public interface ExtensionRangeOrBuilder extends MessageOrBuilder
        {
            boolean hasStart();
            
            int getStart();
            
            boolean hasEnd();
            
            int getEnd();
        }
    }
    
    public static final class FieldDescriptorProto extends GeneratedMessage implements FieldDescriptorProtoOrBuilder
    {
        private static final FieldDescriptorProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<FieldDescriptorProto> PARSER;
        private int bitField0_;
        public static final int NAME_FIELD_NUMBER = 1;
        private Object name_;
        public static final int NUMBER_FIELD_NUMBER = 3;
        private int number_;
        public static final int LABEL_FIELD_NUMBER = 4;
        private Label label_;
        public static final int TYPE_FIELD_NUMBER = 5;
        private Type type_;
        public static final int TYPE_NAME_FIELD_NUMBER = 6;
        private Object typeName_;
        public static final int EXTENDEE_FIELD_NUMBER = 2;
        private Object extendee_;
        public static final int DEFAULT_VALUE_FIELD_NUMBER = 7;
        private Object defaultValue_;
        public static final int OPTIONS_FIELD_NUMBER = 8;
        private FieldOptions options_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private FieldDescriptorProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private FieldDescriptorProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static FieldDescriptorProto getDefaultInstance() {
            return FieldDescriptorProto.defaultInstance;
        }
        
        public FieldDescriptorProto getDefaultInstanceForType() {
            return FieldDescriptorProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private FieldDescriptorProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            final int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            this.bitField0_ |= 0x1;
                            this.name_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x20;
                            this.extendee_ = input.readBytes();
                            continue;
                        }
                        case 24: {
                            this.bitField0_ |= 0x2;
                            this.number_ = input.readInt32();
                            continue;
                        }
                        case 32: {
                            final int rawValue = input.readEnum();
                            final Label value = Label.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(4, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x4;
                            this.label_ = value;
                            continue;
                        }
                        case 40: {
                            final int rawValue = input.readEnum();
                            final Type value2 = Type.valueOf(rawValue);
                            if (value2 == null) {
                                unknownFields.mergeVarintField(5, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x8;
                            this.type_ = value2;
                            continue;
                        }
                        case 50: {
                            this.bitField0_ |= 0x10;
                            this.typeName_ = input.readBytes();
                            continue;
                        }
                        case 58: {
                            this.bitField0_ |= 0x40;
                            this.defaultValue_ = input.readBytes();
                            continue;
                        }
                        case 66: {
                            FieldOptions.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x80) == 0x80) {
                                subBuilder = this.options_.toBuilder();
                            }
                            this.options_ = input.readMessage(FieldOptions.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.options_);
                                this.options_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x80;
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(FieldDescriptorProto.class, Builder.class);
        }
        
        @Override
        public Parser<FieldDescriptorProto> getParserForType() {
            return FieldDescriptorProto.PARSER;
        }
        
        public boolean hasName() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        public String getName() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }
        
        public ByteString getNameBytes() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.name_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasNumber() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        public int getNumber() {
            return this.number_;
        }
        
        public boolean hasLabel() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        public Label getLabel() {
            return this.label_;
        }
        
        public boolean hasType() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        public Type getType() {
            return this.type_;
        }
        
        public boolean hasTypeName() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        public String getTypeName() {
            final Object ref = this.typeName_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.typeName_ = s;
            }
            return s;
        }
        
        public ByteString getTypeNameBytes() {
            final Object ref = this.typeName_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.typeName_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasExtendee() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        public String getExtendee() {
            final Object ref = this.extendee_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.extendee_ = s;
            }
            return s;
        }
        
        public ByteString getExtendeeBytes() {
            final Object ref = this.extendee_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.extendee_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasDefaultValue() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        public String getDefaultValue() {
            final Object ref = this.defaultValue_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.defaultValue_ = s;
            }
            return s;
        }
        
        public ByteString getDefaultValueBytes() {
            final Object ref = this.defaultValue_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.defaultValue_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasOptions() {
            return (this.bitField0_ & 0x80) == 0x80;
        }
        
        public FieldOptions getOptions() {
            return this.options_;
        }
        
        public FieldOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }
        
        private void initFields() {
            this.name_ = "";
            this.number_ = 0;
            this.label_ = Label.LABEL_OPTIONAL;
            this.type_ = Type.TYPE_DOUBLE;
            this.typeName_ = "";
            this.extendee_ = "";
            this.defaultValue_ = "";
            this.options_ = FieldOptions.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (this.hasOptions() && !this.getOptions().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBytes(1, this.getNameBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeBytes(2, this.getExtendeeBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeInt32(3, this.number_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeEnum(4, this.label_.getNumber());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeEnum(5, this.type_.getNumber());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeBytes(6, this.getTypeNameBytes());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeBytes(7, this.getDefaultValueBytes());
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                output.writeMessage(8, this.options_);
            }
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeBytesSize(1, this.getNameBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeBytesSize(2, this.getExtendeeBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeInt32Size(3, this.number_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeEnumSize(4, this.label_.getNumber());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeEnumSize(5, this.type_.getNumber());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeBytesSize(6, this.getTypeNameBytes());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeBytesSize(7, this.getDefaultValueBytes());
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                size += CodedOutputStream.computeMessageSize(8, this.options_);
            }
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static FieldDescriptorProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return FieldDescriptorProto.PARSER.parseFrom(data);
        }
        
        public static FieldDescriptorProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FieldDescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FieldDescriptorProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return FieldDescriptorProto.PARSER.parseFrom(data);
        }
        
        public static FieldDescriptorProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FieldDescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FieldDescriptorProto parseFrom(final InputStream input) throws IOException {
            return FieldDescriptorProto.PARSER.parseFrom(input);
        }
        
        public static FieldDescriptorProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FieldDescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static FieldDescriptorProto parseDelimitedFrom(final InputStream input) throws IOException {
            return FieldDescriptorProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static FieldDescriptorProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FieldDescriptorProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static FieldDescriptorProto parseFrom(final CodedInputStream input) throws IOException {
            return FieldDescriptorProto.PARSER.parseFrom(input);
        }
        
        public static FieldDescriptorProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FieldDescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final FieldDescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            FieldDescriptorProto.PARSER = new AbstractParser<FieldDescriptorProto>() {
                public FieldDescriptorProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new FieldDescriptorProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new FieldDescriptorProto(true)).initFields();
        }
        
        public enum Type implements ProtocolMessageEnum
        {
            TYPE_DOUBLE(0, 1), 
            TYPE_FLOAT(1, 2), 
            TYPE_INT64(2, 3), 
            TYPE_UINT64(3, 4), 
            TYPE_INT32(4, 5), 
            TYPE_FIXED64(5, 6), 
            TYPE_FIXED32(6, 7), 
            TYPE_BOOL(7, 8), 
            TYPE_STRING(8, 9), 
            TYPE_GROUP(9, 10), 
            TYPE_MESSAGE(10, 11), 
            TYPE_BYTES(11, 12), 
            TYPE_UINT32(12, 13), 
            TYPE_ENUM(13, 14), 
            TYPE_SFIXED32(14, 15), 
            TYPE_SFIXED64(15, 16), 
            TYPE_SINT32(16, 17), 
            TYPE_SINT64(17, 18);
            
            public static final int TYPE_DOUBLE_VALUE = 1;
            public static final int TYPE_FLOAT_VALUE = 2;
            public static final int TYPE_INT64_VALUE = 3;
            public static final int TYPE_UINT64_VALUE = 4;
            public static final int TYPE_INT32_VALUE = 5;
            public static final int TYPE_FIXED64_VALUE = 6;
            public static final int TYPE_FIXED32_VALUE = 7;
            public static final int TYPE_BOOL_VALUE = 8;
            public static final int TYPE_STRING_VALUE = 9;
            public static final int TYPE_GROUP_VALUE = 10;
            public static final int TYPE_MESSAGE_VALUE = 11;
            public static final int TYPE_BYTES_VALUE = 12;
            public static final int TYPE_UINT32_VALUE = 13;
            public static final int TYPE_ENUM_VALUE = 14;
            public static final int TYPE_SFIXED32_VALUE = 15;
            public static final int TYPE_SFIXED64_VALUE = 16;
            public static final int TYPE_SINT32_VALUE = 17;
            public static final int TYPE_SINT64_VALUE = 18;
            private static Internal.EnumLiteMap<Type> internalValueMap;
            private static final Type[] VALUES;
            private final int index;
            private final int value;
            
            public final int getNumber() {
                return this.value;
            }
            
            public static Type valueOf(final int value) {
                switch (value) {
                    case 1: {
                        return Type.TYPE_DOUBLE;
                    }
                    case 2: {
                        return Type.TYPE_FLOAT;
                    }
                    case 3: {
                        return Type.TYPE_INT64;
                    }
                    case 4: {
                        return Type.TYPE_UINT64;
                    }
                    case 5: {
                        return Type.TYPE_INT32;
                    }
                    case 6: {
                        return Type.TYPE_FIXED64;
                    }
                    case 7: {
                        return Type.TYPE_FIXED32;
                    }
                    case 8: {
                        return Type.TYPE_BOOL;
                    }
                    case 9: {
                        return Type.TYPE_STRING;
                    }
                    case 10: {
                        return Type.TYPE_GROUP;
                    }
                    case 11: {
                        return Type.TYPE_MESSAGE;
                    }
                    case 12: {
                        return Type.TYPE_BYTES;
                    }
                    case 13: {
                        return Type.TYPE_UINT32;
                    }
                    case 14: {
                        return Type.TYPE_ENUM;
                    }
                    case 15: {
                        return Type.TYPE_SFIXED32;
                    }
                    case 16: {
                        return Type.TYPE_SFIXED64;
                    }
                    case 17: {
                        return Type.TYPE_SINT32;
                    }
                    case 18: {
                        return Type.TYPE_SINT64;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static Internal.EnumLiteMap<Type> internalGetValueMap() {
                return Type.internalValueMap;
            }
            
            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return getDescriptor().getValues().get(this.index);
            }
            
            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return getDescriptor();
            }
            
            public static final Descriptors.EnumDescriptor getDescriptor() {
                return FieldDescriptorProto.getDescriptor().getEnumTypes().get(0);
            }
            
            public static Type valueOf(final Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return Type.VALUES[desc.getIndex()];
            }
            
            private Type(final int index, final int value) {
                this.index = index;
                this.value = value;
            }
            
            static {
                Type.internalValueMap = new Internal.EnumLiteMap<Type>() {
                    public Type findValueByNumber(final int number) {
                        return Type.valueOf(number);
                    }
                };
                VALUES = values();
            }
        }
        
        public enum Label implements ProtocolMessageEnum
        {
            LABEL_OPTIONAL(0, 1), 
            LABEL_REQUIRED(1, 2), 
            LABEL_REPEATED(2, 3);
            
            public static final int LABEL_OPTIONAL_VALUE = 1;
            public static final int LABEL_REQUIRED_VALUE = 2;
            public static final int LABEL_REPEATED_VALUE = 3;
            private static Internal.EnumLiteMap<Label> internalValueMap;
            private static final Label[] VALUES;
            private final int index;
            private final int value;
            
            public final int getNumber() {
                return this.value;
            }
            
            public static Label valueOf(final int value) {
                switch (value) {
                    case 1: {
                        return Label.LABEL_OPTIONAL;
                    }
                    case 2: {
                        return Label.LABEL_REQUIRED;
                    }
                    case 3: {
                        return Label.LABEL_REPEATED;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static Internal.EnumLiteMap<Label> internalGetValueMap() {
                return Label.internalValueMap;
            }
            
            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return getDescriptor().getValues().get(this.index);
            }
            
            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return getDescriptor();
            }
            
            public static final Descriptors.EnumDescriptor getDescriptor() {
                return FieldDescriptorProto.getDescriptor().getEnumTypes().get(1);
            }
            
            public static Label valueOf(final Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return Label.VALUES[desc.getIndex()];
            }
            
            private Label(final int index, final int value) {
                this.index = index;
                this.value = value;
            }
            
            static {
                Label.internalValueMap = new Internal.EnumLiteMap<Label>() {
                    public Label findValueByNumber(final int number) {
                        return Label.valueOf(number);
                    }
                };
                VALUES = values();
            }
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements FieldDescriptorProtoOrBuilder
        {
            private int bitField0_;
            private Object name_;
            private int number_;
            private Label label_;
            private Type type_;
            private Object typeName_;
            private Object extendee_;
            private Object defaultValue_;
            private FieldOptions options_;
            private SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder> optionsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(FieldDescriptorProto.class, Builder.class);
            }
            
            private Builder() {
                this.name_ = "";
                this.label_ = Label.LABEL_OPTIONAL;
                this.type_ = Type.TYPE_DOUBLE;
                this.typeName_ = "";
                this.extendee_ = "";
                this.defaultValue_ = "";
                this.options_ = FieldOptions.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.label_ = Label.LABEL_OPTIONAL;
                this.type_ = Type.TYPE_DOUBLE;
                this.typeName_ = "";
                this.extendee_ = "";
                this.defaultValue_ = "";
                this.options_ = FieldOptions.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getOptionsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.number_ = 0;
                this.bitField0_ &= 0xFFFFFFFD;
                this.label_ = Label.LABEL_OPTIONAL;
                this.bitField0_ &= 0xFFFFFFFB;
                this.type_ = Type.TYPE_DOUBLE;
                this.bitField0_ &= 0xFFFFFFF7;
                this.typeName_ = "";
                this.bitField0_ &= 0xFFFFFFEF;
                this.extendee_ = "";
                this.bitField0_ &= 0xFFFFFFDF;
                this.defaultValue_ = "";
                this.bitField0_ &= 0xFFFFFFBF;
                if (this.optionsBuilder_ == null) {
                    this.options_ = FieldOptions.getDefaultInstance();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFF7F;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_FieldDescriptorProto_descriptor;
            }
            
            public FieldDescriptorProto getDefaultInstanceForType() {
                return FieldDescriptorProto.getDefaultInstance();
            }
            
            public FieldDescriptorProto build() {
                final FieldDescriptorProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public FieldDescriptorProto buildPartial() {
                final FieldDescriptorProto result = new FieldDescriptorProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.name_ = this.name_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.number_ = this.number_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.label_ = this.label_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.type_ = this.type_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.typeName_ = this.typeName_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.extendee_ = this.extendee_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                result.defaultValue_ = this.defaultValue_;
                if ((from_bitField0_ & 0x80) == 0x80) {
                    to_bitField0_ |= 0x80;
                }
                if (this.optionsBuilder_ == null) {
                    result.options_ = this.options_;
                }
                else {
                    result.options_ = this.optionsBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof FieldDescriptorProto) {
                    return this.mergeFrom((FieldDescriptorProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final FieldDescriptorProto other) {
                if (other == FieldDescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 0x1;
                    this.name_ = other.name_;
                    this.onChanged();
                }
                if (other.hasNumber()) {
                    this.setNumber(other.getNumber());
                }
                if (other.hasLabel()) {
                    this.setLabel(other.getLabel());
                }
                if (other.hasType()) {
                    this.setType(other.getType());
                }
                if (other.hasTypeName()) {
                    this.bitField0_ |= 0x10;
                    this.typeName_ = other.typeName_;
                    this.onChanged();
                }
                if (other.hasExtendee()) {
                    this.bitField0_ |= 0x20;
                    this.extendee_ = other.extendee_;
                    this.onChanged();
                }
                if (other.hasDefaultValue()) {
                    this.bitField0_ |= 0x40;
                    this.defaultValue_ = other.defaultValue_;
                    this.onChanged();
                }
                if (other.hasOptions()) {
                    this.mergeOptions(other.getOptions());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return !this.hasOptions() || this.getOptions().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                FieldDescriptorProto parsedMessage = null;
                try {
                    parsedMessage = FieldDescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (FieldDescriptorProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            public boolean hasName() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public String getName() {
                final Object ref = this.name_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.name_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getNameBytes() {
                final Object ref = this.name_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.name_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearName() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.name_ = FieldDescriptorProto.getDefaultInstance().getName();
                this.onChanged();
                return this;
            }
            
            public Builder setNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasNumber() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            public int getNumber() {
                return this.number_;
            }
            
            public Builder setNumber(final int value) {
                this.bitField0_ |= 0x2;
                this.number_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearNumber() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.number_ = 0;
                this.onChanged();
                return this;
            }
            
            public boolean hasLabel() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            public Label getLabel() {
                return this.label_;
            }
            
            public Builder setLabel(final Label value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.label_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearLabel() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.label_ = Label.LABEL_OPTIONAL;
                this.onChanged();
                return this;
            }
            
            public boolean hasType() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            public Type getType() {
                return this.type_;
            }
            
            public Builder setType(final Type value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x8;
                this.type_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearType() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.type_ = Type.TYPE_DOUBLE;
                this.onChanged();
                return this;
            }
            
            public boolean hasTypeName() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            public String getTypeName() {
                final Object ref = this.typeName_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.typeName_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getTypeNameBytes() {
                final Object ref = this.typeName_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.typeName_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setTypeName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.typeName_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearTypeName() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.typeName_ = FieldDescriptorProto.getDefaultInstance().getTypeName();
                this.onChanged();
                return this;
            }
            
            public Builder setTypeNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.typeName_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasExtendee() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            public String getExtendee() {
                final Object ref = this.extendee_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.extendee_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getExtendeeBytes() {
                final Object ref = this.extendee_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.extendee_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setExtendee(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.extendee_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearExtendee() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.extendee_ = FieldDescriptorProto.getDefaultInstance().getExtendee();
                this.onChanged();
                return this;
            }
            
            public Builder setExtendeeBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.extendee_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasDefaultValue() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            public String getDefaultValue() {
                final Object ref = this.defaultValue_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.defaultValue_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getDefaultValueBytes() {
                final Object ref = this.defaultValue_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.defaultValue_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setDefaultValue(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x40;
                this.defaultValue_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDefaultValue() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.defaultValue_ = FieldDescriptorProto.getDefaultInstance().getDefaultValue();
                this.onChanged();
                return this;
            }
            
            public Builder setDefaultValueBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x40;
                this.defaultValue_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasOptions() {
                return (this.bitField0_ & 0x80) == 0x80;
            }
            
            public FieldOptions getOptions() {
                if (this.optionsBuilder_ == null) {
                    return this.options_;
                }
                return this.optionsBuilder_.getMessage();
            }
            
            public Builder setOptions(final FieldOptions value) {
                if (this.optionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.options_ = value;
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x80;
                return this;
            }
            
            public Builder setOptions(final FieldOptions.Builder builderForValue) {
                if (this.optionsBuilder_ == null) {
                    this.options_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x80;
                return this;
            }
            
            public Builder mergeOptions(final FieldOptions value) {
                if (this.optionsBuilder_ == null) {
                    if ((this.bitField0_ & 0x80) == 0x80 && this.options_ != FieldOptions.getDefaultInstance()) {
                        this.options_ = FieldOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.options_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x80;
                return this;
            }
            
            public Builder clearOptions() {
                if (this.optionsBuilder_ == null) {
                    this.options_ = FieldOptions.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFF7F;
                return this;
            }
            
            public FieldOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 0x80;
                this.onChanged();
                return this.getOptionsFieldBuilder().getBuilder();
            }
            
            public FieldOptionsOrBuilder getOptionsOrBuilder() {
                if (this.optionsBuilder_ != null) {
                    return this.optionsBuilder_.getMessageOrBuilder();
                }
                return this.options_;
            }
            
            private SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<FieldOptions, FieldOptions.Builder, FieldOptionsOrBuilder>(this.options_, this.getParentForChildren(), this.isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
        }
    }
    
    public static final class EnumDescriptorProto extends GeneratedMessage implements EnumDescriptorProtoOrBuilder
    {
        private static final EnumDescriptorProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<EnumDescriptorProto> PARSER;
        private int bitField0_;
        public static final int NAME_FIELD_NUMBER = 1;
        private Object name_;
        public static final int VALUE_FIELD_NUMBER = 2;
        private List<EnumValueDescriptorProto> value_;
        public static final int OPTIONS_FIELD_NUMBER = 3;
        private EnumOptions options_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private EnumDescriptorProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private EnumDescriptorProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static EnumDescriptorProto getDefaultInstance() {
            return EnumDescriptorProto.defaultInstance;
        }
        
        public EnumDescriptorProto getDefaultInstanceForType() {
            return EnumDescriptorProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private EnumDescriptorProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            this.bitField0_ |= 0x1;
                            this.name_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            if ((mutable_bitField0_ & 0x2) != 0x2) {
                                this.value_ = new ArrayList<EnumValueDescriptorProto>();
                                mutable_bitField0_ |= 0x2;
                            }
                            this.value_.add(input.readMessage(EnumValueDescriptorProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 26: {
                            EnumOptions.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder = this.options_.toBuilder();
                            }
                            this.options_ = input.readMessage(EnumOptions.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.options_);
                                this.options_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x2) == 0x2) {
                    this.value_ = Collections.unmodifiableList((List<? extends EnumValueDescriptorProto>)this.value_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumDescriptorProto.class, Builder.class);
        }
        
        @Override
        public Parser<EnumDescriptorProto> getParserForType() {
            return EnumDescriptorProto.PARSER;
        }
        
        public boolean hasName() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        public String getName() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }
        
        public ByteString getNameBytes() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.name_ = b);
            }
            return (ByteString)ref;
        }
        
        public List<EnumValueDescriptorProto> getValueList() {
            return this.value_;
        }
        
        public List<? extends EnumValueDescriptorProtoOrBuilder> getValueOrBuilderList() {
            return this.value_;
        }
        
        public int getValueCount() {
            return this.value_.size();
        }
        
        public EnumValueDescriptorProto getValue(final int index) {
            return this.value_.get(index);
        }
        
        public EnumValueDescriptorProtoOrBuilder getValueOrBuilder(final int index) {
            return this.value_.get(index);
        }
        
        public boolean hasOptions() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        public EnumOptions getOptions() {
            return this.options_;
        }
        
        public EnumOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }
        
        private void initFields() {
            this.name_ = "";
            this.value_ = Collections.emptyList();
            this.options_ = EnumOptions.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getValueCount(); ++i) {
                if (!this.getValue(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (this.hasOptions() && !this.getOptions().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBytes(1, this.getNameBytes());
            }
            for (int i = 0; i < this.value_.size(); ++i) {
                output.writeMessage(2, this.value_.get(i));
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeMessage(3, this.options_);
            }
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeBytesSize(1, this.getNameBytes());
            }
            for (int i = 0; i < this.value_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(2, this.value_.get(i));
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeMessageSize(3, this.options_);
            }
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static EnumDescriptorProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return EnumDescriptorProto.PARSER.parseFrom(data);
        }
        
        public static EnumDescriptorProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return EnumDescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static EnumDescriptorProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return EnumDescriptorProto.PARSER.parseFrom(data);
        }
        
        public static EnumDescriptorProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return EnumDescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static EnumDescriptorProto parseFrom(final InputStream input) throws IOException {
            return EnumDescriptorProto.PARSER.parseFrom(input);
        }
        
        public static EnumDescriptorProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EnumDescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static EnumDescriptorProto parseDelimitedFrom(final InputStream input) throws IOException {
            return EnumDescriptorProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static EnumDescriptorProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EnumDescriptorProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static EnumDescriptorProto parseFrom(final CodedInputStream input) throws IOException {
            return EnumDescriptorProto.PARSER.parseFrom(input);
        }
        
        public static EnumDescriptorProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EnumDescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final EnumDescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            EnumDescriptorProto.PARSER = new AbstractParser<EnumDescriptorProto>() {
                public EnumDescriptorProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new EnumDescriptorProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new EnumDescriptorProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements EnumDescriptorProtoOrBuilder
        {
            private int bitField0_;
            private Object name_;
            private List<EnumValueDescriptorProto> value_;
            private RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> valueBuilder_;
            private EnumOptions options_;
            private SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder> optionsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumDescriptorProto.class, Builder.class);
            }
            
            private Builder() {
                this.name_ = "";
                this.value_ = Collections.emptyList();
                this.options_ = EnumOptions.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.value_ = Collections.emptyList();
                this.options_ = EnumOptions.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getValueFieldBuilder();
                    this.getOptionsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.valueBuilder_ == null) {
                    this.value_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                }
                else {
                    this.valueBuilder_.clear();
                }
                if (this.optionsBuilder_ == null) {
                    this.options_ = EnumOptions.getDefaultInstance();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_EnumDescriptorProto_descriptor;
            }
            
            public EnumDescriptorProto getDefaultInstanceForType() {
                return EnumDescriptorProto.getDefaultInstance();
            }
            
            public EnumDescriptorProto build() {
                final EnumDescriptorProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public EnumDescriptorProto buildPartial() {
                final EnumDescriptorProto result = new EnumDescriptorProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.name_ = this.name_;
                if (this.valueBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2) {
                        this.value_ = Collections.unmodifiableList((List<? extends EnumValueDescriptorProto>)this.value_);
                        this.bitField0_ &= 0xFFFFFFFD;
                    }
                    result.value_ = this.value_;
                }
                else {
                    result.value_ = this.valueBuilder_.build();
                }
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x2;
                }
                if (this.optionsBuilder_ == null) {
                    result.options_ = this.options_;
                }
                else {
                    result.options_ = this.optionsBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof EnumDescriptorProto) {
                    return this.mergeFrom((EnumDescriptorProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final EnumDescriptorProto other) {
                if (other == EnumDescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 0x1;
                    this.name_ = other.name_;
                    this.onChanged();
                }
                if (this.valueBuilder_ == null) {
                    if (!other.value_.isEmpty()) {
                        if (this.value_.isEmpty()) {
                            this.value_ = other.value_;
                            this.bitField0_ &= 0xFFFFFFFD;
                        }
                        else {
                            this.ensureValueIsMutable();
                            this.value_.addAll(other.value_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.value_.isEmpty()) {
                    if (this.valueBuilder_.isEmpty()) {
                        this.valueBuilder_.dispose();
                        this.valueBuilder_ = null;
                        this.value_ = other.value_;
                        this.bitField0_ &= 0xFFFFFFFD;
                        this.valueBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getValueFieldBuilder() : null);
                    }
                    else {
                        this.valueBuilder_.addAllMessages(other.value_);
                    }
                }
                if (other.hasOptions()) {
                    this.mergeOptions(other.getOptions());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getValueCount(); ++i) {
                    if (!this.getValue(i).isInitialized()) {
                        return false;
                    }
                }
                return !this.hasOptions() || this.getOptions().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                EnumDescriptorProto parsedMessage = null;
                try {
                    parsedMessage = EnumDescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (EnumDescriptorProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            public boolean hasName() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public String getName() {
                final Object ref = this.name_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.name_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getNameBytes() {
                final Object ref = this.name_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.name_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearName() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.name_ = EnumDescriptorProto.getDefaultInstance().getName();
                this.onChanged();
                return this;
            }
            
            public Builder setNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            private void ensureValueIsMutable() {
                if ((this.bitField0_ & 0x2) != 0x2) {
                    this.value_ = new ArrayList<EnumValueDescriptorProto>(this.value_);
                    this.bitField0_ |= 0x2;
                }
            }
            
            public List<EnumValueDescriptorProto> getValueList() {
                if (this.valueBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends EnumValueDescriptorProto>)this.value_);
                }
                return this.valueBuilder_.getMessageList();
            }
            
            public int getValueCount() {
                if (this.valueBuilder_ == null) {
                    return this.value_.size();
                }
                return this.valueBuilder_.getCount();
            }
            
            public EnumValueDescriptorProto getValue(final int index) {
                if (this.valueBuilder_ == null) {
                    return this.value_.get(index);
                }
                return this.valueBuilder_.getMessage(index);
            }
            
            public Builder setValue(final int index, final EnumValueDescriptorProto value) {
                if (this.valueBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureValueIsMutable();
                    this.value_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.valueBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setValue(final int index, final EnumValueDescriptorProto.Builder builderForValue) {
                if (this.valueBuilder_ == null) {
                    this.ensureValueIsMutable();
                    this.value_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.valueBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addValue(final EnumValueDescriptorProto value) {
                if (this.valueBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureValueIsMutable();
                    this.value_.add(value);
                    this.onChanged();
                }
                else {
                    this.valueBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addValue(final int index, final EnumValueDescriptorProto value) {
                if (this.valueBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureValueIsMutable();
                    this.value_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.valueBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addValue(final EnumValueDescriptorProto.Builder builderForValue) {
                if (this.valueBuilder_ == null) {
                    this.ensureValueIsMutable();
                    this.value_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.valueBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addValue(final int index, final EnumValueDescriptorProto.Builder builderForValue) {
                if (this.valueBuilder_ == null) {
                    this.ensureValueIsMutable();
                    this.value_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.valueBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllValue(final Iterable<? extends EnumValueDescriptorProto> values) {
                if (this.valueBuilder_ == null) {
                    this.ensureValueIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.value_);
                    this.onChanged();
                }
                else {
                    this.valueBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearValue() {
                if (this.valueBuilder_ == null) {
                    this.value_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.onChanged();
                }
                else {
                    this.valueBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeValue(final int index) {
                if (this.valueBuilder_ == null) {
                    this.ensureValueIsMutable();
                    this.value_.remove(index);
                    this.onChanged();
                }
                else {
                    this.valueBuilder_.remove(index);
                }
                return this;
            }
            
            public EnumValueDescriptorProto.Builder getValueBuilder(final int index) {
                return this.getValueFieldBuilder().getBuilder(index);
            }
            
            public EnumValueDescriptorProtoOrBuilder getValueOrBuilder(final int index) {
                if (this.valueBuilder_ == null) {
                    return this.value_.get(index);
                }
                return this.valueBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends EnumValueDescriptorProtoOrBuilder> getValueOrBuilderList() {
                if (this.valueBuilder_ != null) {
                    return this.valueBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends EnumValueDescriptorProtoOrBuilder>)this.value_);
            }
            
            public EnumValueDescriptorProto.Builder addValueBuilder() {
                return this.getValueFieldBuilder().addBuilder(EnumValueDescriptorProto.getDefaultInstance());
            }
            
            public EnumValueDescriptorProto.Builder addValueBuilder(final int index) {
                return this.getValueFieldBuilder().addBuilder(index, EnumValueDescriptorProto.getDefaultInstance());
            }
            
            public List<EnumValueDescriptorProto.Builder> getValueBuilderList() {
                return this.getValueFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder> getValueFieldBuilder() {
                if (this.valueBuilder_ == null) {
                    this.valueBuilder_ = new RepeatedFieldBuilder<EnumValueDescriptorProto, EnumValueDescriptorProto.Builder, EnumValueDescriptorProtoOrBuilder>(this.value_, (this.bitField0_ & 0x2) == 0x2, this.getParentForChildren(), this.isClean());
                    this.value_ = null;
                }
                return this.valueBuilder_;
            }
            
            public boolean hasOptions() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            public EnumOptions getOptions() {
                if (this.optionsBuilder_ == null) {
                    return this.options_;
                }
                return this.optionsBuilder_.getMessage();
            }
            
            public Builder setOptions(final EnumOptions value) {
                if (this.optionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.options_ = value;
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder setOptions(final EnumOptions.Builder builderForValue) {
                if (this.optionsBuilder_ == null) {
                    this.options_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder mergeOptions(final EnumOptions value) {
                if (this.optionsBuilder_ == null) {
                    if ((this.bitField0_ & 0x4) == 0x4 && this.options_ != EnumOptions.getDefaultInstance()) {
                        this.options_ = EnumOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.options_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder clearOptions() {
                if (this.optionsBuilder_ == null) {
                    this.options_ = EnumOptions.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            public EnumOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 0x4;
                this.onChanged();
                return this.getOptionsFieldBuilder().getBuilder();
            }
            
            public EnumOptionsOrBuilder getOptionsOrBuilder() {
                if (this.optionsBuilder_ != null) {
                    return this.optionsBuilder_.getMessageOrBuilder();
                }
                return this.options_;
            }
            
            private SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<EnumOptions, EnumOptions.Builder, EnumOptionsOrBuilder>(this.options_, this.getParentForChildren(), this.isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
        }
    }
    
    public static final class EnumValueDescriptorProto extends GeneratedMessage implements EnumValueDescriptorProtoOrBuilder
    {
        private static final EnumValueDescriptorProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<EnumValueDescriptorProto> PARSER;
        private int bitField0_;
        public static final int NAME_FIELD_NUMBER = 1;
        private Object name_;
        public static final int NUMBER_FIELD_NUMBER = 2;
        private int number_;
        public static final int OPTIONS_FIELD_NUMBER = 3;
        private EnumValueOptions options_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private EnumValueDescriptorProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private EnumValueDescriptorProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static EnumValueDescriptorProto getDefaultInstance() {
            return EnumValueDescriptorProto.defaultInstance;
        }
        
        public EnumValueDescriptorProto getDefaultInstanceForType() {
            return EnumValueDescriptorProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private EnumValueDescriptorProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            final int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            this.bitField0_ |= 0x1;
                            this.name_ = input.readBytes();
                            continue;
                        }
                        case 16: {
                            this.bitField0_ |= 0x2;
                            this.number_ = input.readInt32();
                            continue;
                        }
                        case 26: {
                            EnumValueOptions.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x4) == 0x4) {
                                subBuilder = this.options_.toBuilder();
                            }
                            this.options_ = input.readMessage(EnumValueOptions.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.options_);
                                this.options_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x4;
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumValueDescriptorProto.class, Builder.class);
        }
        
        @Override
        public Parser<EnumValueDescriptorProto> getParserForType() {
            return EnumValueDescriptorProto.PARSER;
        }
        
        public boolean hasName() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        public String getName() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }
        
        public ByteString getNameBytes() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.name_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasNumber() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        public int getNumber() {
            return this.number_;
        }
        
        public boolean hasOptions() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        public EnumValueOptions getOptions() {
            return this.options_;
        }
        
        public EnumValueOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }
        
        private void initFields() {
            this.name_ = "";
            this.number_ = 0;
            this.options_ = EnumValueOptions.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (this.hasOptions() && !this.getOptions().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBytes(1, this.getNameBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeInt32(2, this.number_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeMessage(3, this.options_);
            }
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeBytesSize(1, this.getNameBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeInt32Size(2, this.number_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeMessageSize(3, this.options_);
            }
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static EnumValueDescriptorProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return EnumValueDescriptorProto.PARSER.parseFrom(data);
        }
        
        public static EnumValueDescriptorProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return EnumValueDescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static EnumValueDescriptorProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return EnumValueDescriptorProto.PARSER.parseFrom(data);
        }
        
        public static EnumValueDescriptorProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return EnumValueDescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static EnumValueDescriptorProto parseFrom(final InputStream input) throws IOException {
            return EnumValueDescriptorProto.PARSER.parseFrom(input);
        }
        
        public static EnumValueDescriptorProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EnumValueDescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static EnumValueDescriptorProto parseDelimitedFrom(final InputStream input) throws IOException {
            return EnumValueDescriptorProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static EnumValueDescriptorProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EnumValueDescriptorProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static EnumValueDescriptorProto parseFrom(final CodedInputStream input) throws IOException {
            return EnumValueDescriptorProto.PARSER.parseFrom(input);
        }
        
        public static EnumValueDescriptorProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EnumValueDescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final EnumValueDescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            EnumValueDescriptorProto.PARSER = new AbstractParser<EnumValueDescriptorProto>() {
                public EnumValueDescriptorProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new EnumValueDescriptorProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new EnumValueDescriptorProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements EnumValueDescriptorProtoOrBuilder
        {
            private int bitField0_;
            private Object name_;
            private int number_;
            private EnumValueOptions options_;
            private SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder> optionsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumValueDescriptorProto.class, Builder.class);
            }
            
            private Builder() {
                this.name_ = "";
                this.options_ = EnumValueOptions.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.options_ = EnumValueOptions.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getOptionsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.number_ = 0;
                this.bitField0_ &= 0xFFFFFFFD;
                if (this.optionsBuilder_ == null) {
                    this.options_ = EnumValueOptions.getDefaultInstance();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_EnumValueDescriptorProto_descriptor;
            }
            
            public EnumValueDescriptorProto getDefaultInstanceForType() {
                return EnumValueDescriptorProto.getDefaultInstance();
            }
            
            public EnumValueDescriptorProto build() {
                final EnumValueDescriptorProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public EnumValueDescriptorProto buildPartial() {
                final EnumValueDescriptorProto result = new EnumValueDescriptorProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.name_ = this.name_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.number_ = this.number_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                if (this.optionsBuilder_ == null) {
                    result.options_ = this.options_;
                }
                else {
                    result.options_ = this.optionsBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof EnumValueDescriptorProto) {
                    return this.mergeFrom((EnumValueDescriptorProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final EnumValueDescriptorProto other) {
                if (other == EnumValueDescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 0x1;
                    this.name_ = other.name_;
                    this.onChanged();
                }
                if (other.hasNumber()) {
                    this.setNumber(other.getNumber());
                }
                if (other.hasOptions()) {
                    this.mergeOptions(other.getOptions());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return !this.hasOptions() || this.getOptions().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                EnumValueDescriptorProto parsedMessage = null;
                try {
                    parsedMessage = EnumValueDescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (EnumValueDescriptorProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            public boolean hasName() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public String getName() {
                final Object ref = this.name_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.name_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getNameBytes() {
                final Object ref = this.name_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.name_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearName() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.name_ = EnumValueDescriptorProto.getDefaultInstance().getName();
                this.onChanged();
                return this;
            }
            
            public Builder setNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasNumber() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            public int getNumber() {
                return this.number_;
            }
            
            public Builder setNumber(final int value) {
                this.bitField0_ |= 0x2;
                this.number_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearNumber() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.number_ = 0;
                this.onChanged();
                return this;
            }
            
            public boolean hasOptions() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            public EnumValueOptions getOptions() {
                if (this.optionsBuilder_ == null) {
                    return this.options_;
                }
                return this.optionsBuilder_.getMessage();
            }
            
            public Builder setOptions(final EnumValueOptions value) {
                if (this.optionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.options_ = value;
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder setOptions(final EnumValueOptions.Builder builderForValue) {
                if (this.optionsBuilder_ == null) {
                    this.options_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder mergeOptions(final EnumValueOptions value) {
                if (this.optionsBuilder_ == null) {
                    if ((this.bitField0_ & 0x4) == 0x4 && this.options_ != EnumValueOptions.getDefaultInstance()) {
                        this.options_ = EnumValueOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.options_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder clearOptions() {
                if (this.optionsBuilder_ == null) {
                    this.options_ = EnumValueOptions.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            public EnumValueOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 0x4;
                this.onChanged();
                return this.getOptionsFieldBuilder().getBuilder();
            }
            
            public EnumValueOptionsOrBuilder getOptionsOrBuilder() {
                if (this.optionsBuilder_ != null) {
                    return this.optionsBuilder_.getMessageOrBuilder();
                }
                return this.options_;
            }
            
            private SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<EnumValueOptions, EnumValueOptions.Builder, EnumValueOptionsOrBuilder>(this.options_, this.getParentForChildren(), this.isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
        }
    }
    
    public static final class ServiceDescriptorProto extends GeneratedMessage implements ServiceDescriptorProtoOrBuilder
    {
        private static final ServiceDescriptorProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ServiceDescriptorProto> PARSER;
        private int bitField0_;
        public static final int NAME_FIELD_NUMBER = 1;
        private Object name_;
        public static final int METHOD_FIELD_NUMBER = 2;
        private List<MethodDescriptorProto> method_;
        public static final int OPTIONS_FIELD_NUMBER = 3;
        private ServiceOptions options_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private ServiceDescriptorProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ServiceDescriptorProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ServiceDescriptorProto getDefaultInstance() {
            return ServiceDescriptorProto.defaultInstance;
        }
        
        public ServiceDescriptorProto getDefaultInstanceForType() {
            return ServiceDescriptorProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ServiceDescriptorProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            this.bitField0_ |= 0x1;
                            this.name_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            if ((mutable_bitField0_ & 0x2) != 0x2) {
                                this.method_ = new ArrayList<MethodDescriptorProto>();
                                mutable_bitField0_ |= 0x2;
                            }
                            this.method_.add(input.readMessage(MethodDescriptorProto.PARSER, extensionRegistry));
                            continue;
                        }
                        case 26: {
                            ServiceOptions.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x2) == 0x2) {
                                subBuilder = this.options_.toBuilder();
                            }
                            this.options_ = input.readMessage(ServiceOptions.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.options_);
                                this.options_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x2;
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x2) == 0x2) {
                    this.method_ = Collections.unmodifiableList((List<? extends MethodDescriptorProto>)this.method_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ServiceDescriptorProto.class, Builder.class);
        }
        
        @Override
        public Parser<ServiceDescriptorProto> getParserForType() {
            return ServiceDescriptorProto.PARSER;
        }
        
        public boolean hasName() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        public String getName() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }
        
        public ByteString getNameBytes() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.name_ = b);
            }
            return (ByteString)ref;
        }
        
        public List<MethodDescriptorProto> getMethodList() {
            return this.method_;
        }
        
        public List<? extends MethodDescriptorProtoOrBuilder> getMethodOrBuilderList() {
            return this.method_;
        }
        
        public int getMethodCount() {
            return this.method_.size();
        }
        
        public MethodDescriptorProto getMethod(final int index) {
            return this.method_.get(index);
        }
        
        public MethodDescriptorProtoOrBuilder getMethodOrBuilder(final int index) {
            return this.method_.get(index);
        }
        
        public boolean hasOptions() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        public ServiceOptions getOptions() {
            return this.options_;
        }
        
        public ServiceOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }
        
        private void initFields() {
            this.name_ = "";
            this.method_ = Collections.emptyList();
            this.options_ = ServiceOptions.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getMethodCount(); ++i) {
                if (!this.getMethod(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (this.hasOptions() && !this.getOptions().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBytes(1, this.getNameBytes());
            }
            for (int i = 0; i < this.method_.size(); ++i) {
                output.writeMessage(2, this.method_.get(i));
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeMessage(3, this.options_);
            }
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeBytesSize(1, this.getNameBytes());
            }
            for (int i = 0; i < this.method_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(2, this.method_.get(i));
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeMessageSize(3, this.options_);
            }
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static ServiceDescriptorProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ServiceDescriptorProto.PARSER.parseFrom(data);
        }
        
        public static ServiceDescriptorProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ServiceDescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ServiceDescriptorProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ServiceDescriptorProto.PARSER.parseFrom(data);
        }
        
        public static ServiceDescriptorProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ServiceDescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ServiceDescriptorProto parseFrom(final InputStream input) throws IOException {
            return ServiceDescriptorProto.PARSER.parseFrom(input);
        }
        
        public static ServiceDescriptorProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ServiceDescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ServiceDescriptorProto parseDelimitedFrom(final InputStream input) throws IOException {
            return ServiceDescriptorProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static ServiceDescriptorProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ServiceDescriptorProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ServiceDescriptorProto parseFrom(final CodedInputStream input) throws IOException {
            return ServiceDescriptorProto.PARSER.parseFrom(input);
        }
        
        public static ServiceDescriptorProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ServiceDescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ServiceDescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            ServiceDescriptorProto.PARSER = new AbstractParser<ServiceDescriptorProto>() {
                public ServiceDescriptorProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ServiceDescriptorProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new ServiceDescriptorProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements ServiceDescriptorProtoOrBuilder
        {
            private int bitField0_;
            private Object name_;
            private List<MethodDescriptorProto> method_;
            private RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> methodBuilder_;
            private ServiceOptions options_;
            private SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder> optionsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(ServiceDescriptorProto.class, Builder.class);
            }
            
            private Builder() {
                this.name_ = "";
                this.method_ = Collections.emptyList();
                this.options_ = ServiceOptions.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.method_ = Collections.emptyList();
                this.options_ = ServiceOptions.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getMethodFieldBuilder();
                    this.getOptionsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.methodBuilder_ == null) {
                    this.method_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                }
                else {
                    this.methodBuilder_.clear();
                }
                if (this.optionsBuilder_ == null) {
                    this.options_ = ServiceOptions.getDefaultInstance();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_ServiceDescriptorProto_descriptor;
            }
            
            public ServiceDescriptorProto getDefaultInstanceForType() {
                return ServiceDescriptorProto.getDefaultInstance();
            }
            
            public ServiceDescriptorProto build() {
                final ServiceDescriptorProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public ServiceDescriptorProto buildPartial() {
                final ServiceDescriptorProto result = new ServiceDescriptorProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.name_ = this.name_;
                if (this.methodBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2) {
                        this.method_ = Collections.unmodifiableList((List<? extends MethodDescriptorProto>)this.method_);
                        this.bitField0_ &= 0xFFFFFFFD;
                    }
                    result.method_ = this.method_;
                }
                else {
                    result.method_ = this.methodBuilder_.build();
                }
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x2;
                }
                if (this.optionsBuilder_ == null) {
                    result.options_ = this.options_;
                }
                else {
                    result.options_ = this.optionsBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ServiceDescriptorProto) {
                    return this.mergeFrom((ServiceDescriptorProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ServiceDescriptorProto other) {
                if (other == ServiceDescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 0x1;
                    this.name_ = other.name_;
                    this.onChanged();
                }
                if (this.methodBuilder_ == null) {
                    if (!other.method_.isEmpty()) {
                        if (this.method_.isEmpty()) {
                            this.method_ = other.method_;
                            this.bitField0_ &= 0xFFFFFFFD;
                        }
                        else {
                            this.ensureMethodIsMutable();
                            this.method_.addAll(other.method_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.method_.isEmpty()) {
                    if (this.methodBuilder_.isEmpty()) {
                        this.methodBuilder_.dispose();
                        this.methodBuilder_ = null;
                        this.method_ = other.method_;
                        this.bitField0_ &= 0xFFFFFFFD;
                        this.methodBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getMethodFieldBuilder() : null);
                    }
                    else {
                        this.methodBuilder_.addAllMessages(other.method_);
                    }
                }
                if (other.hasOptions()) {
                    this.mergeOptions(other.getOptions());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getMethodCount(); ++i) {
                    if (!this.getMethod(i).isInitialized()) {
                        return false;
                    }
                }
                return !this.hasOptions() || this.getOptions().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                ServiceDescriptorProto parsedMessage = null;
                try {
                    parsedMessage = ServiceDescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ServiceDescriptorProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            public boolean hasName() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public String getName() {
                final Object ref = this.name_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.name_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getNameBytes() {
                final Object ref = this.name_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.name_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearName() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.name_ = ServiceDescriptorProto.getDefaultInstance().getName();
                this.onChanged();
                return this;
            }
            
            public Builder setNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            private void ensureMethodIsMutable() {
                if ((this.bitField0_ & 0x2) != 0x2) {
                    this.method_ = new ArrayList<MethodDescriptorProto>(this.method_);
                    this.bitField0_ |= 0x2;
                }
            }
            
            public List<MethodDescriptorProto> getMethodList() {
                if (this.methodBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends MethodDescriptorProto>)this.method_);
                }
                return this.methodBuilder_.getMessageList();
            }
            
            public int getMethodCount() {
                if (this.methodBuilder_ == null) {
                    return this.method_.size();
                }
                return this.methodBuilder_.getCount();
            }
            
            public MethodDescriptorProto getMethod(final int index) {
                if (this.methodBuilder_ == null) {
                    return this.method_.get(index);
                }
                return this.methodBuilder_.getMessage(index);
            }
            
            public Builder setMethod(final int index, final MethodDescriptorProto value) {
                if (this.methodBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureMethodIsMutable();
                    this.method_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.methodBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setMethod(final int index, final MethodDescriptorProto.Builder builderForValue) {
                if (this.methodBuilder_ == null) {
                    this.ensureMethodIsMutable();
                    this.method_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.methodBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addMethod(final MethodDescriptorProto value) {
                if (this.methodBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureMethodIsMutable();
                    this.method_.add(value);
                    this.onChanged();
                }
                else {
                    this.methodBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addMethod(final int index, final MethodDescriptorProto value) {
                if (this.methodBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureMethodIsMutable();
                    this.method_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.methodBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addMethod(final MethodDescriptorProto.Builder builderForValue) {
                if (this.methodBuilder_ == null) {
                    this.ensureMethodIsMutable();
                    this.method_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.methodBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addMethod(final int index, final MethodDescriptorProto.Builder builderForValue) {
                if (this.methodBuilder_ == null) {
                    this.ensureMethodIsMutable();
                    this.method_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.methodBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllMethod(final Iterable<? extends MethodDescriptorProto> values) {
                if (this.methodBuilder_ == null) {
                    this.ensureMethodIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.method_);
                    this.onChanged();
                }
                else {
                    this.methodBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearMethod() {
                if (this.methodBuilder_ == null) {
                    this.method_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.onChanged();
                }
                else {
                    this.methodBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeMethod(final int index) {
                if (this.methodBuilder_ == null) {
                    this.ensureMethodIsMutable();
                    this.method_.remove(index);
                    this.onChanged();
                }
                else {
                    this.methodBuilder_.remove(index);
                }
                return this;
            }
            
            public MethodDescriptorProto.Builder getMethodBuilder(final int index) {
                return this.getMethodFieldBuilder().getBuilder(index);
            }
            
            public MethodDescriptorProtoOrBuilder getMethodOrBuilder(final int index) {
                if (this.methodBuilder_ == null) {
                    return this.method_.get(index);
                }
                return this.methodBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends MethodDescriptorProtoOrBuilder> getMethodOrBuilderList() {
                if (this.methodBuilder_ != null) {
                    return this.methodBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends MethodDescriptorProtoOrBuilder>)this.method_);
            }
            
            public MethodDescriptorProto.Builder addMethodBuilder() {
                return this.getMethodFieldBuilder().addBuilder(MethodDescriptorProto.getDefaultInstance());
            }
            
            public MethodDescriptorProto.Builder addMethodBuilder(final int index) {
                return this.getMethodFieldBuilder().addBuilder(index, MethodDescriptorProto.getDefaultInstance());
            }
            
            public List<MethodDescriptorProto.Builder> getMethodBuilderList() {
                return this.getMethodFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder> getMethodFieldBuilder() {
                if (this.methodBuilder_ == null) {
                    this.methodBuilder_ = new RepeatedFieldBuilder<MethodDescriptorProto, MethodDescriptorProto.Builder, MethodDescriptorProtoOrBuilder>(this.method_, (this.bitField0_ & 0x2) == 0x2, this.getParentForChildren(), this.isClean());
                    this.method_ = null;
                }
                return this.methodBuilder_;
            }
            
            public boolean hasOptions() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            public ServiceOptions getOptions() {
                if (this.optionsBuilder_ == null) {
                    return this.options_;
                }
                return this.optionsBuilder_.getMessage();
            }
            
            public Builder setOptions(final ServiceOptions value) {
                if (this.optionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.options_ = value;
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder setOptions(final ServiceOptions.Builder builderForValue) {
                if (this.optionsBuilder_ == null) {
                    this.options_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder mergeOptions(final ServiceOptions value) {
                if (this.optionsBuilder_ == null) {
                    if ((this.bitField0_ & 0x4) == 0x4 && this.options_ != ServiceOptions.getDefaultInstance()) {
                        this.options_ = ServiceOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.options_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x4;
                return this;
            }
            
            public Builder clearOptions() {
                if (this.optionsBuilder_ == null) {
                    this.options_ = ServiceOptions.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFFB;
                return this;
            }
            
            public ServiceOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 0x4;
                this.onChanged();
                return this.getOptionsFieldBuilder().getBuilder();
            }
            
            public ServiceOptionsOrBuilder getOptionsOrBuilder() {
                if (this.optionsBuilder_ != null) {
                    return this.optionsBuilder_.getMessageOrBuilder();
                }
                return this.options_;
            }
            
            private SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<ServiceOptions, ServiceOptions.Builder, ServiceOptionsOrBuilder>(this.options_, this.getParentForChildren(), this.isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
        }
    }
    
    public static final class MethodDescriptorProto extends GeneratedMessage implements MethodDescriptorProtoOrBuilder
    {
        private static final MethodDescriptorProto defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<MethodDescriptorProto> PARSER;
        private int bitField0_;
        public static final int NAME_FIELD_NUMBER = 1;
        private Object name_;
        public static final int INPUT_TYPE_FIELD_NUMBER = 2;
        private Object inputType_;
        public static final int OUTPUT_TYPE_FIELD_NUMBER = 3;
        private Object outputType_;
        public static final int OPTIONS_FIELD_NUMBER = 4;
        private MethodOptions options_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private MethodDescriptorProto(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private MethodDescriptorProto(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static MethodDescriptorProto getDefaultInstance() {
            return MethodDescriptorProto.defaultInstance;
        }
        
        public MethodDescriptorProto getDefaultInstanceForType() {
            return MethodDescriptorProto.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private MethodDescriptorProto(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            final int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            this.bitField0_ |= 0x1;
                            this.name_ = input.readBytes();
                            continue;
                        }
                        case 18: {
                            this.bitField0_ |= 0x2;
                            this.inputType_ = input.readBytes();
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x4;
                            this.outputType_ = input.readBytes();
                            continue;
                        }
                        case 34: {
                            MethodOptions.Builder subBuilder = null;
                            if ((this.bitField0_ & 0x8) == 0x8) {
                                subBuilder = this.options_.toBuilder();
                            }
                            this.options_ = input.readMessage(MethodOptions.PARSER, extensionRegistry);
                            if (subBuilder != null) {
                                subBuilder.mergeFrom(this.options_);
                                this.options_ = subBuilder.buildPartial();
                            }
                            this.bitField0_ |= 0x8;
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(MethodDescriptorProto.class, Builder.class);
        }
        
        @Override
        public Parser<MethodDescriptorProto> getParserForType() {
            return MethodDescriptorProto.PARSER;
        }
        
        public boolean hasName() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        public String getName() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.name_ = s;
            }
            return s;
        }
        
        public ByteString getNameBytes() {
            final Object ref = this.name_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.name_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasInputType() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        public String getInputType() {
            final Object ref = this.inputType_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.inputType_ = s;
            }
            return s;
        }
        
        public ByteString getInputTypeBytes() {
            final Object ref = this.inputType_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.inputType_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasOutputType() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        public String getOutputType() {
            final Object ref = this.outputType_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.outputType_ = s;
            }
            return s;
        }
        
        public ByteString getOutputTypeBytes() {
            final Object ref = this.outputType_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.outputType_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasOptions() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        public MethodOptions getOptions() {
            return this.options_;
        }
        
        public MethodOptionsOrBuilder getOptionsOrBuilder() {
            return this.options_;
        }
        
        private void initFields() {
            this.name_ = "";
            this.inputType_ = "";
            this.outputType_ = "";
            this.options_ = MethodOptions.getDefaultInstance();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            if (this.hasOptions() && !this.getOptions().isInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBytes(1, this.getNameBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(2, this.getInputTypeBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBytes(3, this.getOutputTypeBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeMessage(4, this.options_);
            }
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeBytesSize(1, this.getNameBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(2, this.getInputTypeBytes());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBytesSize(3, this.getOutputTypeBytes());
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeMessageSize(4, this.options_);
            }
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static MethodDescriptorProto parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return MethodDescriptorProto.PARSER.parseFrom(data);
        }
        
        public static MethodDescriptorProto parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return MethodDescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static MethodDescriptorProto parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return MethodDescriptorProto.PARSER.parseFrom(data);
        }
        
        public static MethodDescriptorProto parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return MethodDescriptorProto.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static MethodDescriptorProto parseFrom(final InputStream input) throws IOException {
            return MethodDescriptorProto.PARSER.parseFrom(input);
        }
        
        public static MethodDescriptorProto parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MethodDescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static MethodDescriptorProto parseDelimitedFrom(final InputStream input) throws IOException {
            return MethodDescriptorProto.PARSER.parseDelimitedFrom(input);
        }
        
        public static MethodDescriptorProto parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MethodDescriptorProto.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static MethodDescriptorProto parseFrom(final CodedInputStream input) throws IOException {
            return MethodDescriptorProto.PARSER.parseFrom(input);
        }
        
        public static MethodDescriptorProto parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MethodDescriptorProto.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final MethodDescriptorProto prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            MethodDescriptorProto.PARSER = new AbstractParser<MethodDescriptorProto>() {
                public MethodDescriptorProto parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new MethodDescriptorProto(input, extensionRegistry);
                }
            };
            (defaultInstance = new MethodDescriptorProto(true)).initFields();
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements MethodDescriptorProtoOrBuilder
        {
            private int bitField0_;
            private Object name_;
            private Object inputType_;
            private Object outputType_;
            private MethodOptions options_;
            private SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder> optionsBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_fieldAccessorTable.ensureFieldAccessorsInitialized(MethodDescriptorProto.class, Builder.class);
            }
            
            private Builder() {
                this.name_ = "";
                this.inputType_ = "";
                this.outputType_ = "";
                this.options_ = MethodOptions.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.name_ = "";
                this.inputType_ = "";
                this.outputType_ = "";
                this.options_ = MethodOptions.getDefaultInstance();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getOptionsFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.name_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.inputType_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.outputType_ = "";
                this.bitField0_ &= 0xFFFFFFFB;
                if (this.optionsBuilder_ == null) {
                    this.options_ = MethodOptions.getDefaultInstance();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_MethodDescriptorProto_descriptor;
            }
            
            public MethodDescriptorProto getDefaultInstanceForType() {
                return MethodDescriptorProto.getDefaultInstance();
            }
            
            public MethodDescriptorProto build() {
                final MethodDescriptorProto result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public MethodDescriptorProto buildPartial() {
                final MethodDescriptorProto result = new MethodDescriptorProto((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.name_ = this.name_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.inputType_ = this.inputType_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.outputType_ = this.outputType_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                if (this.optionsBuilder_ == null) {
                    result.options_ = this.options_;
                }
                else {
                    result.options_ = this.optionsBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof MethodDescriptorProto) {
                    return this.mergeFrom((MethodDescriptorProto)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final MethodDescriptorProto other) {
                if (other == MethodDescriptorProto.getDefaultInstance()) {
                    return this;
                }
                if (other.hasName()) {
                    this.bitField0_ |= 0x1;
                    this.name_ = other.name_;
                    this.onChanged();
                }
                if (other.hasInputType()) {
                    this.bitField0_ |= 0x2;
                    this.inputType_ = other.inputType_;
                    this.onChanged();
                }
                if (other.hasOutputType()) {
                    this.bitField0_ |= 0x4;
                    this.outputType_ = other.outputType_;
                    this.onChanged();
                }
                if (other.hasOptions()) {
                    this.mergeOptions(other.getOptions());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return !this.hasOptions() || this.getOptions().isInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                MethodDescriptorProto parsedMessage = null;
                try {
                    parsedMessage = MethodDescriptorProto.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (MethodDescriptorProto)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            public boolean hasName() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public String getName() {
                final Object ref = this.name_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.name_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getNameBytes() {
                final Object ref = this.name_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.name_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setName(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearName() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.name_ = MethodDescriptorProto.getDefaultInstance().getName();
                this.onChanged();
                return this;
            }
            
            public Builder setNameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.name_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasInputType() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            public String getInputType() {
                final Object ref = this.inputType_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.inputType_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getInputTypeBytes() {
                final Object ref = this.inputType_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.inputType_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setInputType(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.inputType_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearInputType() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.inputType_ = MethodDescriptorProto.getDefaultInstance().getInputType();
                this.onChanged();
                return this;
            }
            
            public Builder setInputTypeBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.inputType_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasOutputType() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            public String getOutputType() {
                final Object ref = this.outputType_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.outputType_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getOutputTypeBytes() {
                final Object ref = this.outputType_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.outputType_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setOutputType(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.outputType_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearOutputType() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.outputType_ = MethodDescriptorProto.getDefaultInstance().getOutputType();
                this.onChanged();
                return this;
            }
            
            public Builder setOutputTypeBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x4;
                this.outputType_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasOptions() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            public MethodOptions getOptions() {
                if (this.optionsBuilder_ == null) {
                    return this.options_;
                }
                return this.optionsBuilder_.getMessage();
            }
            
            public Builder setOptions(final MethodOptions value) {
                if (this.optionsBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.options_ = value;
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(value);
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder setOptions(final MethodOptions.Builder builderForValue) {
                if (this.optionsBuilder_ == null) {
                    this.options_ = builderForValue.build();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder mergeOptions(final MethodOptions value) {
                if (this.optionsBuilder_ == null) {
                    if ((this.bitField0_ & 0x8) == 0x8 && this.options_ != MethodOptions.getDefaultInstance()) {
                        this.options_ = MethodOptions.newBuilder(this.options_).mergeFrom(value).buildPartial();
                    }
                    else {
                        this.options_ = value;
                    }
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.mergeFrom(value);
                }
                this.bitField0_ |= 0x8;
                return this;
            }
            
            public Builder clearOptions() {
                if (this.optionsBuilder_ == null) {
                    this.options_ = MethodOptions.getDefaultInstance();
                    this.onChanged();
                }
                else {
                    this.optionsBuilder_.clear();
                }
                this.bitField0_ &= 0xFFFFFFF7;
                return this;
            }
            
            public MethodOptions.Builder getOptionsBuilder() {
                this.bitField0_ |= 0x8;
                this.onChanged();
                return this.getOptionsFieldBuilder().getBuilder();
            }
            
            public MethodOptionsOrBuilder getOptionsOrBuilder() {
                if (this.optionsBuilder_ != null) {
                    return this.optionsBuilder_.getMessageOrBuilder();
                }
                return this.options_;
            }
            
            private SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder> getOptionsFieldBuilder() {
                if (this.optionsBuilder_ == null) {
                    this.optionsBuilder_ = new SingleFieldBuilder<MethodOptions, MethodOptions.Builder, MethodOptionsOrBuilder>(this.options_, this.getParentForChildren(), this.isClean());
                    this.options_ = null;
                }
                return this.optionsBuilder_;
            }
        }
    }
    
    public static final class FileOptions extends ExtendableMessage<FileOptions> implements FileOptionsOrBuilder
    {
        private static final FileOptions defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<FileOptions> PARSER;
        private int bitField0_;
        public static final int JAVA_PACKAGE_FIELD_NUMBER = 1;
        private Object javaPackage_;
        public static final int JAVA_OUTER_CLASSNAME_FIELD_NUMBER = 8;
        private Object javaOuterClassname_;
        public static final int JAVA_MULTIPLE_FILES_FIELD_NUMBER = 10;
        private boolean javaMultipleFiles_;
        public static final int JAVA_GENERATE_EQUALS_AND_HASH_FIELD_NUMBER = 20;
        private boolean javaGenerateEqualsAndHash_;
        public static final int OPTIMIZE_FOR_FIELD_NUMBER = 9;
        private OptimizeMode optimizeFor_;
        public static final int GO_PACKAGE_FIELD_NUMBER = 11;
        private Object goPackage_;
        public static final int CC_GENERIC_SERVICES_FIELD_NUMBER = 16;
        private boolean ccGenericServices_;
        public static final int JAVA_GENERIC_SERVICES_FIELD_NUMBER = 17;
        private boolean javaGenericServices_;
        public static final int PY_GENERIC_SERVICES_FIELD_NUMBER = 18;
        private boolean pyGenericServices_;
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private List<UninterpretedOption> uninterpretedOption_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private FileOptions(final ExtendableBuilder<FileOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private FileOptions(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static FileOptions getDefaultInstance() {
            return FileOptions.defaultInstance;
        }
        
        public FileOptions getDefaultInstanceForType() {
            return FileOptions.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private FileOptions(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            this.bitField0_ |= 0x1;
                            this.javaPackage_ = input.readBytes();
                            continue;
                        }
                        case 66: {
                            this.bitField0_ |= 0x2;
                            this.javaOuterClassname_ = input.readBytes();
                            continue;
                        }
                        case 72: {
                            final int rawValue = input.readEnum();
                            final OptimizeMode value = OptimizeMode.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(9, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x10;
                            this.optimizeFor_ = value;
                            continue;
                        }
                        case 80: {
                            this.bitField0_ |= 0x4;
                            this.javaMultipleFiles_ = input.readBool();
                            continue;
                        }
                        case 90: {
                            this.bitField0_ |= 0x20;
                            this.goPackage_ = input.readBytes();
                            continue;
                        }
                        case 128: {
                            this.bitField0_ |= 0x40;
                            this.ccGenericServices_ = input.readBool();
                            continue;
                        }
                        case 136: {
                            this.bitField0_ |= 0x80;
                            this.javaGenericServices_ = input.readBool();
                            continue;
                        }
                        case 144: {
                            this.bitField0_ |= 0x100;
                            this.pyGenericServices_ = input.readBool();
                            continue;
                        }
                        case 160: {
                            this.bitField0_ |= 0x8;
                            this.javaGenerateEqualsAndHash_ = input.readBool();
                            continue;
                        }
                        case 7994: {
                            if ((mutable_bitField0_ & 0x200) != 0x200) {
                                this.uninterpretedOption_ = new ArrayList<UninterpretedOption>();
                                mutable_bitField0_ |= 0x200;
                            }
                            this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x200) == 0x200) {
                    this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_FileOptions_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_FileOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(FileOptions.class, Builder.class);
        }
        
        @Override
        public Parser<FileOptions> getParserForType() {
            return FileOptions.PARSER;
        }
        
        public boolean hasJavaPackage() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        public String getJavaPackage() {
            final Object ref = this.javaPackage_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.javaPackage_ = s;
            }
            return s;
        }
        
        public ByteString getJavaPackageBytes() {
            final Object ref = this.javaPackage_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.javaPackage_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasJavaOuterClassname() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        public String getJavaOuterClassname() {
            final Object ref = this.javaOuterClassname_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.javaOuterClassname_ = s;
            }
            return s;
        }
        
        public ByteString getJavaOuterClassnameBytes() {
            final Object ref = this.javaOuterClassname_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.javaOuterClassname_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasJavaMultipleFiles() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        public boolean getJavaMultipleFiles() {
            return this.javaMultipleFiles_;
        }
        
        public boolean hasJavaGenerateEqualsAndHash() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        public boolean getJavaGenerateEqualsAndHash() {
            return this.javaGenerateEqualsAndHash_;
        }
        
        public boolean hasOptimizeFor() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        public OptimizeMode getOptimizeFor() {
            return this.optimizeFor_;
        }
        
        public boolean hasGoPackage() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        public String getGoPackage() {
            final Object ref = this.goPackage_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.goPackage_ = s;
            }
            return s;
        }
        
        public ByteString getGoPackageBytes() {
            final Object ref = this.goPackage_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.goPackage_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasCcGenericServices() {
            return (this.bitField0_ & 0x40) == 0x40;
        }
        
        public boolean getCcGenericServices() {
            return this.ccGenericServices_;
        }
        
        public boolean hasJavaGenericServices() {
            return (this.bitField0_ & 0x80) == 0x80;
        }
        
        public boolean getJavaGenericServices() {
            return this.javaGenericServices_;
        }
        
        public boolean hasPyGenericServices() {
            return (this.bitField0_ & 0x100) == 0x100;
        }
        
        public boolean getPyGenericServices() {
            return this.pyGenericServices_;
        }
        
        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }
        
        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }
        
        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }
        
        public UninterpretedOption getUninterpretedOption(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        private void initFields() {
            this.javaPackage_ = "";
            this.javaOuterClassname_ = "";
            this.javaMultipleFiles_ = false;
            this.javaGenerateEqualsAndHash_ = false;
            this.optimizeFor_ = OptimizeMode.SPEED;
            this.goPackage_ = "";
            this.ccGenericServices_ = false;
            this.javaGenericServices_ = false;
            this.pyGenericServices_ = false;
            this.uninterpretedOption_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                if (!this.getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (!this.extensionsAreInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            final ExtensionWriter extensionWriter = this.newExtensionWriter();
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBytes(1, this.getJavaPackageBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBytes(8, this.getJavaOuterClassnameBytes());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeEnum(9, this.optimizeFor_.getNumber());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBool(10, this.javaMultipleFiles_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeBytes(11, this.getGoPackageBytes());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                output.writeBool(16, this.ccGenericServices_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                output.writeBool(17, this.javaGenericServices_);
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                output.writeBool(18, this.pyGenericServices_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeBool(20, this.javaGenerateEqualsAndHash_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            extensionWriter.writeUntil(536870912, output);
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeBytesSize(1, this.getJavaPackageBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBytesSize(8, this.getJavaOuterClassnameBytes());
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeEnumSize(9, this.optimizeFor_.getNumber());
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBoolSize(10, this.javaMultipleFiles_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeBytesSize(11, this.getGoPackageBytes());
            }
            if ((this.bitField0_ & 0x40) == 0x40) {
                size += CodedOutputStream.computeBoolSize(16, this.ccGenericServices_);
            }
            if ((this.bitField0_ & 0x80) == 0x80) {
                size += CodedOutputStream.computeBoolSize(17, this.javaGenericServices_);
            }
            if ((this.bitField0_ & 0x100) == 0x100) {
                size += CodedOutputStream.computeBoolSize(18, this.pyGenericServices_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeBoolSize(20, this.javaGenerateEqualsAndHash_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            size += this.extensionsSerializedSize();
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static FileOptions parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return FileOptions.PARSER.parseFrom(data);
        }
        
        public static FileOptions parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FileOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FileOptions parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return FileOptions.PARSER.parseFrom(data);
        }
        
        public static FileOptions parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FileOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FileOptions parseFrom(final InputStream input) throws IOException {
            return FileOptions.PARSER.parseFrom(input);
        }
        
        public static FileOptions parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FileOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static FileOptions parseDelimitedFrom(final InputStream input) throws IOException {
            return FileOptions.PARSER.parseDelimitedFrom(input);
        }
        
        public static FileOptions parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FileOptions.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static FileOptions parseFrom(final CodedInputStream input) throws IOException {
            return FileOptions.PARSER.parseFrom(input);
        }
        
        public static FileOptions parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FileOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final FileOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            FileOptions.PARSER = new AbstractParser<FileOptions>() {
                public FileOptions parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new FileOptions(input, extensionRegistry);
                }
            };
            (defaultInstance = new FileOptions(true)).initFields();
        }
        
        public enum OptimizeMode implements ProtocolMessageEnum
        {
            SPEED(0, 1), 
            CODE_SIZE(1, 2), 
            LITE_RUNTIME(2, 3);
            
            public static final int SPEED_VALUE = 1;
            public static final int CODE_SIZE_VALUE = 2;
            public static final int LITE_RUNTIME_VALUE = 3;
            private static Internal.EnumLiteMap<OptimizeMode> internalValueMap;
            private static final OptimizeMode[] VALUES;
            private final int index;
            private final int value;
            
            public final int getNumber() {
                return this.value;
            }
            
            public static OptimizeMode valueOf(final int value) {
                switch (value) {
                    case 1: {
                        return OptimizeMode.SPEED;
                    }
                    case 2: {
                        return OptimizeMode.CODE_SIZE;
                    }
                    case 3: {
                        return OptimizeMode.LITE_RUNTIME;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static Internal.EnumLiteMap<OptimizeMode> internalGetValueMap() {
                return OptimizeMode.internalValueMap;
            }
            
            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return getDescriptor().getValues().get(this.index);
            }
            
            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return getDescriptor();
            }
            
            public static final Descriptors.EnumDescriptor getDescriptor() {
                return FileOptions.getDescriptor().getEnumTypes().get(0);
            }
            
            public static OptimizeMode valueOf(final Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return OptimizeMode.VALUES[desc.getIndex()];
            }
            
            private OptimizeMode(final int index, final int value) {
                this.index = index;
                this.value = value;
            }
            
            static {
                OptimizeMode.internalValueMap = new Internal.EnumLiteMap<OptimizeMode>() {
                    public OptimizeMode findValueByNumber(final int number) {
                        return OptimizeMode.valueOf(number);
                    }
                };
                VALUES = values();
            }
        }
        
        public static final class Builder extends ExtendableBuilder<FileOptions, Builder> implements FileOptionsOrBuilder
        {
            private int bitField0_;
            private Object javaPackage_;
            private Object javaOuterClassname_;
            private boolean javaMultipleFiles_;
            private boolean javaGenerateEqualsAndHash_;
            private OptimizeMode optimizeFor_;
            private Object goPackage_;
            private boolean ccGenericServices_;
            private boolean javaGenericServices_;
            private boolean pyGenericServices_;
            private List<UninterpretedOption> uninterpretedOption_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_FileOptions_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_FileOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(FileOptions.class, Builder.class);
            }
            
            private Builder() {
                this.javaPackage_ = "";
                this.javaOuterClassname_ = "";
                this.optimizeFor_ = OptimizeMode.SPEED;
                this.goPackage_ = "";
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.javaPackage_ = "";
                this.javaOuterClassname_ = "";
                this.optimizeFor_ = OptimizeMode.SPEED;
                this.goPackage_ = "";
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getUninterpretedOptionFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.javaPackage_ = "";
                this.bitField0_ &= 0xFFFFFFFE;
                this.javaOuterClassname_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.javaMultipleFiles_ = false;
                this.bitField0_ &= 0xFFFFFFFB;
                this.javaGenerateEqualsAndHash_ = false;
                this.bitField0_ &= 0xFFFFFFF7;
                this.optimizeFor_ = OptimizeMode.SPEED;
                this.bitField0_ &= 0xFFFFFFEF;
                this.goPackage_ = "";
                this.bitField0_ &= 0xFFFFFFDF;
                this.ccGenericServices_ = false;
                this.bitField0_ &= 0xFFFFFFBF;
                this.javaGenericServices_ = false;
                this.bitField0_ &= 0xFFFFFF7F;
                this.pyGenericServices_ = false;
                this.bitField0_ &= 0xFFFFFEFF;
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFDFF;
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_FileOptions_descriptor;
            }
            
            public FileOptions getDefaultInstanceForType() {
                return FileOptions.getDefaultInstance();
            }
            
            public FileOptions build() {
                final FileOptions result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public FileOptions buildPartial() {
                final FileOptions result = new FileOptions((ExtendableBuilder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.javaPackage_ = this.javaPackage_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.javaOuterClassname_ = this.javaOuterClassname_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.javaMultipleFiles_ = this.javaMultipleFiles_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.javaGenerateEqualsAndHash_ = this.javaGenerateEqualsAndHash_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.optimizeFor_ = this.optimizeFor_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.goPackage_ = this.goPackage_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x40;
                }
                result.ccGenericServices_ = this.ccGenericServices_;
                if ((from_bitField0_ & 0x80) == 0x80) {
                    to_bitField0_ |= 0x80;
                }
                result.javaGenericServices_ = this.javaGenericServices_;
                if ((from_bitField0_ & 0x100) == 0x100) {
                    to_bitField0_ |= 0x100;
                }
                result.pyGenericServices_ = this.pyGenericServices_;
                if (this.uninterpretedOptionBuilder_ == null) {
                    if ((this.bitField0_ & 0x200) == 0x200) {
                        this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                        this.bitField0_ &= 0xFFFFFDFF;
                    }
                    result.uninterpretedOption_ = this.uninterpretedOption_;
                }
                else {
                    result.uninterpretedOption_ = this.uninterpretedOptionBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof FileOptions) {
                    return this.mergeFrom((FileOptions)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final FileOptions other) {
                if (other == FileOptions.getDefaultInstance()) {
                    return this;
                }
                if (other.hasJavaPackage()) {
                    this.bitField0_ |= 0x1;
                    this.javaPackage_ = other.javaPackage_;
                    this.onChanged();
                }
                if (other.hasJavaOuterClassname()) {
                    this.bitField0_ |= 0x2;
                    this.javaOuterClassname_ = other.javaOuterClassname_;
                    this.onChanged();
                }
                if (other.hasJavaMultipleFiles()) {
                    this.setJavaMultipleFiles(other.getJavaMultipleFiles());
                }
                if (other.hasJavaGenerateEqualsAndHash()) {
                    this.setJavaGenerateEqualsAndHash(other.getJavaGenerateEqualsAndHash());
                }
                if (other.hasOptimizeFor()) {
                    this.setOptimizeFor(other.getOptimizeFor());
                }
                if (other.hasGoPackage()) {
                    this.bitField0_ |= 0x20;
                    this.goPackage_ = other.goPackage_;
                    this.onChanged();
                }
                if (other.hasCcGenericServices()) {
                    this.setCcGenericServices(other.getCcGenericServices());
                }
                if (other.hasJavaGenericServices()) {
                    this.setJavaGenericServices(other.getJavaGenericServices());
                }
                if (other.hasPyGenericServices()) {
                    this.setPyGenericServices(other.getPyGenericServices());
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= 0xFFFFFDFF;
                        }
                        else {
                            this.ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= 0xFFFFFDFF;
                        this.uninterpretedOptionBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getUninterpretedOptionFieldBuilder() : null);
                    }
                    else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                this.mergeExtensionFields(other);
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                    if (!this.getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                return this.extensionsAreInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                FileOptions parsedMessage = null;
                try {
                    parsedMessage = FileOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (FileOptions)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            public boolean hasJavaPackage() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public String getJavaPackage() {
                final Object ref = this.javaPackage_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.javaPackage_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getJavaPackageBytes() {
                final Object ref = this.javaPackage_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.javaPackage_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setJavaPackage(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.javaPackage_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearJavaPackage() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.javaPackage_ = FileOptions.getDefaultInstance().getJavaPackage();
                this.onChanged();
                return this;
            }
            
            public Builder setJavaPackageBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.javaPackage_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasJavaOuterClassname() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            public String getJavaOuterClassname() {
                final Object ref = this.javaOuterClassname_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.javaOuterClassname_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getJavaOuterClassnameBytes() {
                final Object ref = this.javaOuterClassname_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.javaOuterClassname_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setJavaOuterClassname(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.javaOuterClassname_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearJavaOuterClassname() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.javaOuterClassname_ = FileOptions.getDefaultInstance().getJavaOuterClassname();
                this.onChanged();
                return this;
            }
            
            public Builder setJavaOuterClassnameBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.javaOuterClassname_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasJavaMultipleFiles() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            public boolean getJavaMultipleFiles() {
                return this.javaMultipleFiles_;
            }
            
            public Builder setJavaMultipleFiles(final boolean value) {
                this.bitField0_ |= 0x4;
                this.javaMultipleFiles_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearJavaMultipleFiles() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.javaMultipleFiles_ = false;
                this.onChanged();
                return this;
            }
            
            public boolean hasJavaGenerateEqualsAndHash() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            public boolean getJavaGenerateEqualsAndHash() {
                return this.javaGenerateEqualsAndHash_;
            }
            
            public Builder setJavaGenerateEqualsAndHash(final boolean value) {
                this.bitField0_ |= 0x8;
                this.javaGenerateEqualsAndHash_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearJavaGenerateEqualsAndHash() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.javaGenerateEqualsAndHash_ = false;
                this.onChanged();
                return this;
            }
            
            public boolean hasOptimizeFor() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            public OptimizeMode getOptimizeFor() {
                return this.optimizeFor_;
            }
            
            public Builder setOptimizeFor(final OptimizeMode value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.optimizeFor_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearOptimizeFor() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.optimizeFor_ = OptimizeMode.SPEED;
                this.onChanged();
                return this;
            }
            
            public boolean hasGoPackage() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            public String getGoPackage() {
                final Object ref = this.goPackage_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.goPackage_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getGoPackageBytes() {
                final Object ref = this.goPackage_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.goPackage_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setGoPackage(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.goPackage_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearGoPackage() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.goPackage_ = FileOptions.getDefaultInstance().getGoPackage();
                this.onChanged();
                return this;
            }
            
            public Builder setGoPackageBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.goPackage_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasCcGenericServices() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            public boolean getCcGenericServices() {
                return this.ccGenericServices_;
            }
            
            public Builder setCcGenericServices(final boolean value) {
                this.bitField0_ |= 0x40;
                this.ccGenericServices_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearCcGenericServices() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.ccGenericServices_ = false;
                this.onChanged();
                return this;
            }
            
            public boolean hasJavaGenericServices() {
                return (this.bitField0_ & 0x80) == 0x80;
            }
            
            public boolean getJavaGenericServices() {
                return this.javaGenericServices_;
            }
            
            public Builder setJavaGenericServices(final boolean value) {
                this.bitField0_ |= 0x80;
                this.javaGenericServices_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearJavaGenericServices() {
                this.bitField0_ &= 0xFFFFFF7F;
                this.javaGenericServices_ = false;
                this.onChanged();
                return this;
            }
            
            public boolean hasPyGenericServices() {
                return (this.bitField0_ & 0x100) == 0x100;
            }
            
            public boolean getPyGenericServices() {
                return this.pyGenericServices_;
            }
            
            public Builder setPyGenericServices(final boolean value) {
                this.bitField0_ |= 0x100;
                this.pyGenericServices_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearPyGenericServices() {
                this.bitField0_ &= 0xFFFFFEFF;
                this.pyGenericServices_ = false;
                this.onChanged();
                return this;
            }
            
            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 0x200) != 0x200) {
                    this.uninterpretedOption_ = new ArrayList<UninterpretedOption>(this.uninterpretedOption_);
                    this.bitField0_ |= 0x200;
                }
            }
            
            public List<UninterpretedOption> getUninterpretedOptionList() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                return this.uninterpretedOptionBuilder_.getMessageList();
            }
            
            public int getUninterpretedOptionCount() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.size();
                }
                return this.uninterpretedOptionBuilder_.getCount();
            }
            
            public UninterpretedOption getUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessage(index);
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllUninterpretedOption(final Iterable<? extends UninterpretedOption> values) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.uninterpretedOption_);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearUninterpretedOption() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFDFF;
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.remove(index);
                }
                return this;
            }
            
            public UninterpretedOption.Builder getUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().getBuilder(index);
            }
            
            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                if (this.uninterpretedOptionBuilder_ != null) {
                    return this.uninterpretedOptionBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends UninterpretedOptionOrBuilder>)this.uninterpretedOption_);
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }
            
            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return this.getUninterpretedOptionFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder>(this.uninterpretedOption_, (this.bitField0_ & 0x200) == 0x200, this.getParentForChildren(), this.isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }
    
    public static final class MessageOptions extends ExtendableMessage<MessageOptions> implements MessageOptionsOrBuilder
    {
        private static final MessageOptions defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<MessageOptions> PARSER;
        private int bitField0_;
        public static final int MESSAGE_SET_WIRE_FORMAT_FIELD_NUMBER = 1;
        private boolean messageSetWireFormat_;
        public static final int NO_STANDARD_DESCRIPTOR_ACCESSOR_FIELD_NUMBER = 2;
        private boolean noStandardDescriptorAccessor_;
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private List<UninterpretedOption> uninterpretedOption_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private MessageOptions(final ExtendableBuilder<MessageOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private MessageOptions(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static MessageOptions getDefaultInstance() {
            return MessageOptions.defaultInstance;
        }
        
        public MessageOptions getDefaultInstanceForType() {
            return MessageOptions.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private MessageOptions(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            this.bitField0_ |= 0x1;
                            this.messageSetWireFormat_ = input.readBool();
                            continue;
                        }
                        case 16: {
                            this.bitField0_ |= 0x2;
                            this.noStandardDescriptorAccessor_ = input.readBool();
                            continue;
                        }
                        case 7994: {
                            if ((mutable_bitField0_ & 0x4) != 0x4) {
                                this.uninterpretedOption_ = new ArrayList<UninterpretedOption>();
                                mutable_bitField0_ |= 0x4;
                            }
                            this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x4) == 0x4) {
                    this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_MessageOptions_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_MessageOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(MessageOptions.class, Builder.class);
        }
        
        @Override
        public Parser<MessageOptions> getParserForType() {
            return MessageOptions.PARSER;
        }
        
        public boolean hasMessageSetWireFormat() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        public boolean getMessageSetWireFormat() {
            return this.messageSetWireFormat_;
        }
        
        public boolean hasNoStandardDescriptorAccessor() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        public boolean getNoStandardDescriptorAccessor() {
            return this.noStandardDescriptorAccessor_;
        }
        
        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }
        
        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }
        
        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }
        
        public UninterpretedOption getUninterpretedOption(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        private void initFields() {
            this.messageSetWireFormat_ = false;
            this.noStandardDescriptorAccessor_ = false;
            this.uninterpretedOption_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                if (!this.getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (!this.extensionsAreInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            final ExtensionWriter extensionWriter = this.newExtensionWriter();
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBool(1, this.messageSetWireFormat_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBool(2, this.noStandardDescriptorAccessor_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            extensionWriter.writeUntil(536870912, output);
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeBoolSize(1, this.messageSetWireFormat_);
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBoolSize(2, this.noStandardDescriptorAccessor_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            size += this.extensionsSerializedSize();
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static MessageOptions parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return MessageOptions.PARSER.parseFrom(data);
        }
        
        public static MessageOptions parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return MessageOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static MessageOptions parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return MessageOptions.PARSER.parseFrom(data);
        }
        
        public static MessageOptions parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return MessageOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static MessageOptions parseFrom(final InputStream input) throws IOException {
            return MessageOptions.PARSER.parseFrom(input);
        }
        
        public static MessageOptions parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MessageOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static MessageOptions parseDelimitedFrom(final InputStream input) throws IOException {
            return MessageOptions.PARSER.parseDelimitedFrom(input);
        }
        
        public static MessageOptions parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MessageOptions.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static MessageOptions parseFrom(final CodedInputStream input) throws IOException {
            return MessageOptions.PARSER.parseFrom(input);
        }
        
        public static MessageOptions parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MessageOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final MessageOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            MessageOptions.PARSER = new AbstractParser<MessageOptions>() {
                public MessageOptions parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new MessageOptions(input, extensionRegistry);
                }
            };
            (defaultInstance = new MessageOptions(true)).initFields();
        }
        
        public static final class Builder extends ExtendableBuilder<MessageOptions, Builder> implements MessageOptionsOrBuilder
        {
            private int bitField0_;
            private boolean messageSetWireFormat_;
            private boolean noStandardDescriptorAccessor_;
            private List<UninterpretedOption> uninterpretedOption_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_MessageOptions_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_MessageOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(MessageOptions.class, Builder.class);
            }
            
            private Builder() {
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getUninterpretedOptionFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.messageSetWireFormat_ = false;
                this.bitField0_ &= 0xFFFFFFFE;
                this.noStandardDescriptorAccessor_ = false;
                this.bitField0_ &= 0xFFFFFFFD;
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFB;
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_MessageOptions_descriptor;
            }
            
            public MessageOptions getDefaultInstanceForType() {
                return MessageOptions.getDefaultInstance();
            }
            
            public MessageOptions build() {
                final MessageOptions result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public MessageOptions buildPartial() {
                final MessageOptions result = new MessageOptions((ExtendableBuilder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.messageSetWireFormat_ = this.messageSetWireFormat_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.noStandardDescriptorAccessor_ = this.noStandardDescriptorAccessor_;
                if (this.uninterpretedOptionBuilder_ == null) {
                    if ((this.bitField0_ & 0x4) == 0x4) {
                        this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                        this.bitField0_ &= 0xFFFFFFFB;
                    }
                    result.uninterpretedOption_ = this.uninterpretedOption_;
                }
                else {
                    result.uninterpretedOption_ = this.uninterpretedOptionBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof MessageOptions) {
                    return this.mergeFrom((MessageOptions)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final MessageOptions other) {
                if (other == MessageOptions.getDefaultInstance()) {
                    return this;
                }
                if (other.hasMessageSetWireFormat()) {
                    this.setMessageSetWireFormat(other.getMessageSetWireFormat());
                }
                if (other.hasNoStandardDescriptorAccessor()) {
                    this.setNoStandardDescriptorAccessor(other.getNoStandardDescriptorAccessor());
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= 0xFFFFFFFB;
                        }
                        else {
                            this.ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= 0xFFFFFFFB;
                        this.uninterpretedOptionBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getUninterpretedOptionFieldBuilder() : null);
                    }
                    else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                this.mergeExtensionFields(other);
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                    if (!this.getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                return this.extensionsAreInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                MessageOptions parsedMessage = null;
                try {
                    parsedMessage = MessageOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (MessageOptions)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            public boolean hasMessageSetWireFormat() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public boolean getMessageSetWireFormat() {
                return this.messageSetWireFormat_;
            }
            
            public Builder setMessageSetWireFormat(final boolean value) {
                this.bitField0_ |= 0x1;
                this.messageSetWireFormat_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearMessageSetWireFormat() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.messageSetWireFormat_ = false;
                this.onChanged();
                return this;
            }
            
            public boolean hasNoStandardDescriptorAccessor() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            public boolean getNoStandardDescriptorAccessor() {
                return this.noStandardDescriptorAccessor_;
            }
            
            public Builder setNoStandardDescriptorAccessor(final boolean value) {
                this.bitField0_ |= 0x2;
                this.noStandardDescriptorAccessor_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearNoStandardDescriptorAccessor() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.noStandardDescriptorAccessor_ = false;
                this.onChanged();
                return this;
            }
            
            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 0x4) != 0x4) {
                    this.uninterpretedOption_ = new ArrayList<UninterpretedOption>(this.uninterpretedOption_);
                    this.bitField0_ |= 0x4;
                }
            }
            
            public List<UninterpretedOption> getUninterpretedOptionList() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                return this.uninterpretedOptionBuilder_.getMessageList();
            }
            
            public int getUninterpretedOptionCount() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.size();
                }
                return this.uninterpretedOptionBuilder_.getCount();
            }
            
            public UninterpretedOption getUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessage(index);
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllUninterpretedOption(final Iterable<? extends UninterpretedOption> values) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.uninterpretedOption_);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearUninterpretedOption() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFB;
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.remove(index);
                }
                return this;
            }
            
            public UninterpretedOption.Builder getUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().getBuilder(index);
            }
            
            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                if (this.uninterpretedOptionBuilder_ != null) {
                    return this.uninterpretedOptionBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends UninterpretedOptionOrBuilder>)this.uninterpretedOption_);
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }
            
            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return this.getUninterpretedOptionFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder>(this.uninterpretedOption_, (this.bitField0_ & 0x4) == 0x4, this.getParentForChildren(), this.isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }
    
    public static final class FieldOptions extends ExtendableMessage<FieldOptions> implements FieldOptionsOrBuilder
    {
        private static final FieldOptions defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<FieldOptions> PARSER;
        private int bitField0_;
        public static final int CTYPE_FIELD_NUMBER = 1;
        private CType ctype_;
        public static final int PACKED_FIELD_NUMBER = 2;
        private boolean packed_;
        public static final int LAZY_FIELD_NUMBER = 5;
        private boolean lazy_;
        public static final int DEPRECATED_FIELD_NUMBER = 3;
        private boolean deprecated_;
        public static final int EXPERIMENTAL_MAP_KEY_FIELD_NUMBER = 9;
        private Object experimentalMapKey_;
        public static final int WEAK_FIELD_NUMBER = 10;
        private boolean weak_;
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private List<UninterpretedOption> uninterpretedOption_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private FieldOptions(final ExtendableBuilder<FieldOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private FieldOptions(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static FieldOptions getDefaultInstance() {
            return FieldOptions.defaultInstance;
        }
        
        public FieldOptions getDefaultInstanceForType() {
            return FieldOptions.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private FieldOptions(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            final int rawValue = input.readEnum();
                            final CType value = CType.valueOf(rawValue);
                            if (value == null) {
                                unknownFields.mergeVarintField(1, rawValue);
                                continue;
                            }
                            this.bitField0_ |= 0x1;
                            this.ctype_ = value;
                            continue;
                        }
                        case 16: {
                            this.bitField0_ |= 0x2;
                            this.packed_ = input.readBool();
                            continue;
                        }
                        case 24: {
                            this.bitField0_ |= 0x8;
                            this.deprecated_ = input.readBool();
                            continue;
                        }
                        case 40: {
                            this.bitField0_ |= 0x4;
                            this.lazy_ = input.readBool();
                            continue;
                        }
                        case 74: {
                            this.bitField0_ |= 0x10;
                            this.experimentalMapKey_ = input.readBytes();
                            continue;
                        }
                        case 80: {
                            this.bitField0_ |= 0x20;
                            this.weak_ = input.readBool();
                            continue;
                        }
                        case 7994: {
                            if ((mutable_bitField0_ & 0x40) != 0x40) {
                                this.uninterpretedOption_ = new ArrayList<UninterpretedOption>();
                                mutable_bitField0_ |= 0x40;
                            }
                            this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x40) == 0x40) {
                    this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_FieldOptions_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_FieldOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(FieldOptions.class, Builder.class);
        }
        
        @Override
        public Parser<FieldOptions> getParserForType() {
            return FieldOptions.PARSER;
        }
        
        public boolean hasCtype() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        public CType getCtype() {
            return this.ctype_;
        }
        
        public boolean hasPacked() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        public boolean getPacked() {
            return this.packed_;
        }
        
        public boolean hasLazy() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        public boolean getLazy() {
            return this.lazy_;
        }
        
        public boolean hasDeprecated() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        public boolean getDeprecated() {
            return this.deprecated_;
        }
        
        public boolean hasExperimentalMapKey() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        public String getExperimentalMapKey() {
            final Object ref = this.experimentalMapKey_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.experimentalMapKey_ = s;
            }
            return s;
        }
        
        public ByteString getExperimentalMapKeyBytes() {
            final Object ref = this.experimentalMapKey_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.experimentalMapKey_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasWeak() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        public boolean getWeak() {
            return this.weak_;
        }
        
        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }
        
        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }
        
        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }
        
        public UninterpretedOption getUninterpretedOption(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        private void initFields() {
            this.ctype_ = CType.STRING;
            this.packed_ = false;
            this.lazy_ = false;
            this.deprecated_ = false;
            this.experimentalMapKey_ = "";
            this.weak_ = false;
            this.uninterpretedOption_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                if (!this.getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (!this.extensionsAreInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            final ExtensionWriter extensionWriter = this.newExtensionWriter();
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeEnum(1, this.ctype_.getNumber());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeBool(2, this.packed_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeBool(3, this.deprecated_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeBool(5, this.lazy_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeBytes(9, this.getExperimentalMapKeyBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeBool(10, this.weak_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            extensionWriter.writeUntil(536870912, output);
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeEnumSize(1, this.ctype_.getNumber());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeBoolSize(2, this.packed_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeBoolSize(3, this.deprecated_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeBoolSize(5, this.lazy_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeBytesSize(9, this.getExperimentalMapKeyBytes());
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeBoolSize(10, this.weak_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            size += this.extensionsSerializedSize();
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static FieldOptions parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return FieldOptions.PARSER.parseFrom(data);
        }
        
        public static FieldOptions parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FieldOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FieldOptions parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return FieldOptions.PARSER.parseFrom(data);
        }
        
        public static FieldOptions parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return FieldOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static FieldOptions parseFrom(final InputStream input) throws IOException {
            return FieldOptions.PARSER.parseFrom(input);
        }
        
        public static FieldOptions parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FieldOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static FieldOptions parseDelimitedFrom(final InputStream input) throws IOException {
            return FieldOptions.PARSER.parseDelimitedFrom(input);
        }
        
        public static FieldOptions parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FieldOptions.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static FieldOptions parseFrom(final CodedInputStream input) throws IOException {
            return FieldOptions.PARSER.parseFrom(input);
        }
        
        public static FieldOptions parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return FieldOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final FieldOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            FieldOptions.PARSER = new AbstractParser<FieldOptions>() {
                public FieldOptions parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new FieldOptions(input, extensionRegistry);
                }
            };
            (defaultInstance = new FieldOptions(true)).initFields();
        }
        
        public enum CType implements ProtocolMessageEnum
        {
            STRING(0, 0), 
            CORD(1, 1), 
            STRING_PIECE(2, 2);
            
            public static final int STRING_VALUE = 0;
            public static final int CORD_VALUE = 1;
            public static final int STRING_PIECE_VALUE = 2;
            private static Internal.EnumLiteMap<CType> internalValueMap;
            private static final CType[] VALUES;
            private final int index;
            private final int value;
            
            public final int getNumber() {
                return this.value;
            }
            
            public static CType valueOf(final int value) {
                switch (value) {
                    case 0: {
                        return CType.STRING;
                    }
                    case 1: {
                        return CType.CORD;
                    }
                    case 2: {
                        return CType.STRING_PIECE;
                    }
                    default: {
                        return null;
                    }
                }
            }
            
            public static Internal.EnumLiteMap<CType> internalGetValueMap() {
                return CType.internalValueMap;
            }
            
            public final Descriptors.EnumValueDescriptor getValueDescriptor() {
                return getDescriptor().getValues().get(this.index);
            }
            
            public final Descriptors.EnumDescriptor getDescriptorForType() {
                return getDescriptor();
            }
            
            public static final Descriptors.EnumDescriptor getDescriptor() {
                return FieldOptions.getDescriptor().getEnumTypes().get(0);
            }
            
            public static CType valueOf(final Descriptors.EnumValueDescriptor desc) {
                if (desc.getType() != getDescriptor()) {
                    throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
                }
                return CType.VALUES[desc.getIndex()];
            }
            
            private CType(final int index, final int value) {
                this.index = index;
                this.value = value;
            }
            
            static {
                CType.internalValueMap = new Internal.EnumLiteMap<CType>() {
                    public CType findValueByNumber(final int number) {
                        return CType.valueOf(number);
                    }
                };
                VALUES = values();
            }
        }
        
        public static final class Builder extends ExtendableBuilder<FieldOptions, Builder> implements FieldOptionsOrBuilder
        {
            private int bitField0_;
            private CType ctype_;
            private boolean packed_;
            private boolean lazy_;
            private boolean deprecated_;
            private Object experimentalMapKey_;
            private boolean weak_;
            private List<UninterpretedOption> uninterpretedOption_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_FieldOptions_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_FieldOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(FieldOptions.class, Builder.class);
            }
            
            private Builder() {
                this.ctype_ = CType.STRING;
                this.experimentalMapKey_ = "";
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.ctype_ = CType.STRING;
                this.experimentalMapKey_ = "";
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getUninterpretedOptionFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.ctype_ = CType.STRING;
                this.bitField0_ &= 0xFFFFFFFE;
                this.packed_ = false;
                this.bitField0_ &= 0xFFFFFFFD;
                this.lazy_ = false;
                this.bitField0_ &= 0xFFFFFFFB;
                this.deprecated_ = false;
                this.bitField0_ &= 0xFFFFFFF7;
                this.experimentalMapKey_ = "";
                this.bitField0_ &= 0xFFFFFFEF;
                this.weak_ = false;
                this.bitField0_ &= 0xFFFFFFDF;
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFBF;
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_FieldOptions_descriptor;
            }
            
            public FieldOptions getDefaultInstanceForType() {
                return FieldOptions.getDefaultInstance();
            }
            
            public FieldOptions build() {
                final FieldOptions result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public FieldOptions buildPartial() {
                final FieldOptions result = new FieldOptions((ExtendableBuilder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.ctype_ = this.ctype_;
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x2;
                }
                result.packed_ = this.packed_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x4;
                }
                result.lazy_ = this.lazy_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x8;
                }
                result.deprecated_ = this.deprecated_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x10;
                }
                result.experimentalMapKey_ = this.experimentalMapKey_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x20;
                }
                result.weak_ = this.weak_;
                if (this.uninterpretedOptionBuilder_ == null) {
                    if ((this.bitField0_ & 0x40) == 0x40) {
                        this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                        this.bitField0_ &= 0xFFFFFFBF;
                    }
                    result.uninterpretedOption_ = this.uninterpretedOption_;
                }
                else {
                    result.uninterpretedOption_ = this.uninterpretedOptionBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof FieldOptions) {
                    return this.mergeFrom((FieldOptions)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final FieldOptions other) {
                if (other == FieldOptions.getDefaultInstance()) {
                    return this;
                }
                if (other.hasCtype()) {
                    this.setCtype(other.getCtype());
                }
                if (other.hasPacked()) {
                    this.setPacked(other.getPacked());
                }
                if (other.hasLazy()) {
                    this.setLazy(other.getLazy());
                }
                if (other.hasDeprecated()) {
                    this.setDeprecated(other.getDeprecated());
                }
                if (other.hasExperimentalMapKey()) {
                    this.bitField0_ |= 0x10;
                    this.experimentalMapKey_ = other.experimentalMapKey_;
                    this.onChanged();
                }
                if (other.hasWeak()) {
                    this.setWeak(other.getWeak());
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= 0xFFFFFFBF;
                        }
                        else {
                            this.ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= 0xFFFFFFBF;
                        this.uninterpretedOptionBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getUninterpretedOptionFieldBuilder() : null);
                    }
                    else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                this.mergeExtensionFields(other);
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                    if (!this.getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                return this.extensionsAreInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                FieldOptions parsedMessage = null;
                try {
                    parsedMessage = FieldOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (FieldOptions)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            public boolean hasCtype() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public CType getCtype() {
                return this.ctype_;
            }
            
            public Builder setCtype(final CType value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x1;
                this.ctype_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearCtype() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.ctype_ = CType.STRING;
                this.onChanged();
                return this;
            }
            
            public boolean hasPacked() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            public boolean getPacked() {
                return this.packed_;
            }
            
            public Builder setPacked(final boolean value) {
                this.bitField0_ |= 0x2;
                this.packed_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearPacked() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.packed_ = false;
                this.onChanged();
                return this;
            }
            
            public boolean hasLazy() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            public boolean getLazy() {
                return this.lazy_;
            }
            
            public Builder setLazy(final boolean value) {
                this.bitField0_ |= 0x4;
                this.lazy_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearLazy() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.lazy_ = false;
                this.onChanged();
                return this;
            }
            
            public boolean hasDeprecated() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            public boolean getDeprecated() {
                return this.deprecated_;
            }
            
            public Builder setDeprecated(final boolean value) {
                this.bitField0_ |= 0x8;
                this.deprecated_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDeprecated() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.deprecated_ = false;
                this.onChanged();
                return this;
            }
            
            public boolean hasExperimentalMapKey() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            public String getExperimentalMapKey() {
                final Object ref = this.experimentalMapKey_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.experimentalMapKey_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getExperimentalMapKeyBytes() {
                final Object ref = this.experimentalMapKey_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.experimentalMapKey_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setExperimentalMapKey(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.experimentalMapKey_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearExperimentalMapKey() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.experimentalMapKey_ = FieldOptions.getDefaultInstance().getExperimentalMapKey();
                this.onChanged();
                return this;
            }
            
            public Builder setExperimentalMapKeyBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x10;
                this.experimentalMapKey_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasWeak() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            public boolean getWeak() {
                return this.weak_;
            }
            
            public Builder setWeak(final boolean value) {
                this.bitField0_ |= 0x20;
                this.weak_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearWeak() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.weak_ = false;
                this.onChanged();
                return this;
            }
            
            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 0x40) != 0x40) {
                    this.uninterpretedOption_ = new ArrayList<UninterpretedOption>(this.uninterpretedOption_);
                    this.bitField0_ |= 0x40;
                }
            }
            
            public List<UninterpretedOption> getUninterpretedOptionList() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                return this.uninterpretedOptionBuilder_.getMessageList();
            }
            
            public int getUninterpretedOptionCount() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.size();
                }
                return this.uninterpretedOptionBuilder_.getCount();
            }
            
            public UninterpretedOption getUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessage(index);
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllUninterpretedOption(final Iterable<? extends UninterpretedOption> values) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.uninterpretedOption_);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearUninterpretedOption() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFBF;
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.remove(index);
                }
                return this;
            }
            
            public UninterpretedOption.Builder getUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().getBuilder(index);
            }
            
            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                if (this.uninterpretedOptionBuilder_ != null) {
                    return this.uninterpretedOptionBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends UninterpretedOptionOrBuilder>)this.uninterpretedOption_);
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }
            
            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return this.getUninterpretedOptionFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder>(this.uninterpretedOption_, (this.bitField0_ & 0x40) == 0x40, this.getParentForChildren(), this.isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }
    
    public static final class EnumOptions extends ExtendableMessage<EnumOptions> implements EnumOptionsOrBuilder
    {
        private static final EnumOptions defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<EnumOptions> PARSER;
        private int bitField0_;
        public static final int ALLOW_ALIAS_FIELD_NUMBER = 2;
        private boolean allowAlias_;
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private List<UninterpretedOption> uninterpretedOption_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private EnumOptions(final ExtendableBuilder<EnumOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private EnumOptions(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static EnumOptions getDefaultInstance() {
            return EnumOptions.defaultInstance;
        }
        
        public EnumOptions getDefaultInstanceForType() {
            return EnumOptions.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private EnumOptions(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            this.bitField0_ |= 0x1;
                            this.allowAlias_ = input.readBool();
                            continue;
                        }
                        case 7994: {
                            if ((mutable_bitField0_ & 0x2) != 0x2) {
                                this.uninterpretedOption_ = new ArrayList<UninterpretedOption>();
                                mutable_bitField0_ |= 0x2;
                            }
                            this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x2) == 0x2) {
                    this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_EnumOptions_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_EnumOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumOptions.class, Builder.class);
        }
        
        @Override
        public Parser<EnumOptions> getParserForType() {
            return EnumOptions.PARSER;
        }
        
        public boolean hasAllowAlias() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        public boolean getAllowAlias() {
            return this.allowAlias_;
        }
        
        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }
        
        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }
        
        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }
        
        public UninterpretedOption getUninterpretedOption(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        private void initFields() {
            this.allowAlias_ = true;
            this.uninterpretedOption_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                if (!this.getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (!this.extensionsAreInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            final ExtensionWriter extensionWriter = this.newExtensionWriter();
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBool(2, this.allowAlias_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            extensionWriter.writeUntil(536870912, output);
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeBoolSize(2, this.allowAlias_);
            }
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            size += this.extensionsSerializedSize();
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static EnumOptions parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return EnumOptions.PARSER.parseFrom(data);
        }
        
        public static EnumOptions parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return EnumOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static EnumOptions parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return EnumOptions.PARSER.parseFrom(data);
        }
        
        public static EnumOptions parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return EnumOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static EnumOptions parseFrom(final InputStream input) throws IOException {
            return EnumOptions.PARSER.parseFrom(input);
        }
        
        public static EnumOptions parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EnumOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static EnumOptions parseDelimitedFrom(final InputStream input) throws IOException {
            return EnumOptions.PARSER.parseDelimitedFrom(input);
        }
        
        public static EnumOptions parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EnumOptions.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static EnumOptions parseFrom(final CodedInputStream input) throws IOException {
            return EnumOptions.PARSER.parseFrom(input);
        }
        
        public static EnumOptions parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EnumOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final EnumOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            EnumOptions.PARSER = new AbstractParser<EnumOptions>() {
                public EnumOptions parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new EnumOptions(input, extensionRegistry);
                }
            };
            (defaultInstance = new EnumOptions(true)).initFields();
        }
        
        public static final class Builder extends ExtendableBuilder<EnumOptions, Builder> implements EnumOptionsOrBuilder
        {
            private int bitField0_;
            private boolean allowAlias_;
            private List<UninterpretedOption> uninterpretedOption_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_EnumOptions_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_EnumOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumOptions.class, Builder.class);
            }
            
            private Builder() {
                this.allowAlias_ = true;
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.allowAlias_ = true;
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getUninterpretedOptionFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                this.allowAlias_ = true;
                this.bitField0_ &= 0xFFFFFFFE;
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_EnumOptions_descriptor;
            }
            
            public EnumOptions getDefaultInstanceForType() {
                return EnumOptions.getDefaultInstance();
            }
            
            public EnumOptions build() {
                final EnumOptions result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public EnumOptions buildPartial() {
                final EnumOptions result = new EnumOptions((ExtendableBuilder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 0x1) == 0x1) {
                    to_bitField0_ |= 0x1;
                }
                result.allowAlias_ = this.allowAlias_;
                if (this.uninterpretedOptionBuilder_ == null) {
                    if ((this.bitField0_ & 0x2) == 0x2) {
                        this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                        this.bitField0_ &= 0xFFFFFFFD;
                    }
                    result.uninterpretedOption_ = this.uninterpretedOption_;
                }
                else {
                    result.uninterpretedOption_ = this.uninterpretedOptionBuilder_.build();
                }
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof EnumOptions) {
                    return this.mergeFrom((EnumOptions)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final EnumOptions other) {
                if (other == EnumOptions.getDefaultInstance()) {
                    return this;
                }
                if (other.hasAllowAlias()) {
                    this.setAllowAlias(other.getAllowAlias());
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= 0xFFFFFFFD;
                        }
                        else {
                            this.ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= 0xFFFFFFFD;
                        this.uninterpretedOptionBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getUninterpretedOptionFieldBuilder() : null);
                    }
                    else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                this.mergeExtensionFields(other);
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                    if (!this.getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                return this.extensionsAreInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                EnumOptions parsedMessage = null;
                try {
                    parsedMessage = EnumOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (EnumOptions)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            public boolean hasAllowAlias() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public boolean getAllowAlias() {
                return this.allowAlias_;
            }
            
            public Builder setAllowAlias(final boolean value) {
                this.bitField0_ |= 0x1;
                this.allowAlias_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearAllowAlias() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.allowAlias_ = true;
                this.onChanged();
                return this;
            }
            
            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 0x2) != 0x2) {
                    this.uninterpretedOption_ = new ArrayList<UninterpretedOption>(this.uninterpretedOption_);
                    this.bitField0_ |= 0x2;
                }
            }
            
            public List<UninterpretedOption> getUninterpretedOptionList() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                return this.uninterpretedOptionBuilder_.getMessageList();
            }
            
            public int getUninterpretedOptionCount() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.size();
                }
                return this.uninterpretedOptionBuilder_.getCount();
            }
            
            public UninterpretedOption getUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessage(index);
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllUninterpretedOption(final Iterable<? extends UninterpretedOption> values) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.uninterpretedOption_);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearUninterpretedOption() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.remove(index);
                }
                return this;
            }
            
            public UninterpretedOption.Builder getUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().getBuilder(index);
            }
            
            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                if (this.uninterpretedOptionBuilder_ != null) {
                    return this.uninterpretedOptionBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends UninterpretedOptionOrBuilder>)this.uninterpretedOption_);
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }
            
            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return this.getUninterpretedOptionFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder>(this.uninterpretedOption_, (this.bitField0_ & 0x2) == 0x2, this.getParentForChildren(), this.isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }
    
    public static final class EnumValueOptions extends ExtendableMessage<EnumValueOptions> implements EnumValueOptionsOrBuilder
    {
        private static final EnumValueOptions defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<EnumValueOptions> PARSER;
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private List<UninterpretedOption> uninterpretedOption_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private EnumValueOptions(final ExtendableBuilder<EnumValueOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private EnumValueOptions(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static EnumValueOptions getDefaultInstance() {
            return EnumValueOptions.defaultInstance;
        }
        
        public EnumValueOptions getDefaultInstanceForType() {
            return EnumValueOptions.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private EnumValueOptions(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 7994: {
                            if ((mutable_bitField0_ & 0x1) != 0x1) {
                                this.uninterpretedOption_ = new ArrayList<UninterpretedOption>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x1) == 0x1) {
                    this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumValueOptions.class, Builder.class);
        }
        
        @Override
        public Parser<EnumValueOptions> getParserForType() {
            return EnumValueOptions.PARSER;
        }
        
        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }
        
        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }
        
        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }
        
        public UninterpretedOption getUninterpretedOption(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        private void initFields() {
            this.uninterpretedOption_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                if (!this.getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (!this.extensionsAreInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            final ExtensionWriter extensionWriter = this.newExtensionWriter();
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            extensionWriter.writeUntil(536870912, output);
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            size += this.extensionsSerializedSize();
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static EnumValueOptions parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return EnumValueOptions.PARSER.parseFrom(data);
        }
        
        public static EnumValueOptions parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return EnumValueOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static EnumValueOptions parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return EnumValueOptions.PARSER.parseFrom(data);
        }
        
        public static EnumValueOptions parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return EnumValueOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static EnumValueOptions parseFrom(final InputStream input) throws IOException {
            return EnumValueOptions.PARSER.parseFrom(input);
        }
        
        public static EnumValueOptions parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EnumValueOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static EnumValueOptions parseDelimitedFrom(final InputStream input) throws IOException {
            return EnumValueOptions.PARSER.parseDelimitedFrom(input);
        }
        
        public static EnumValueOptions parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EnumValueOptions.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static EnumValueOptions parseFrom(final CodedInputStream input) throws IOException {
            return EnumValueOptions.PARSER.parseFrom(input);
        }
        
        public static EnumValueOptions parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return EnumValueOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final EnumValueOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            EnumValueOptions.PARSER = new AbstractParser<EnumValueOptions>() {
                public EnumValueOptions parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new EnumValueOptions(input, extensionRegistry);
                }
            };
            (defaultInstance = new EnumValueOptions(true)).initFields();
        }
        
        public static final class Builder extends ExtendableBuilder<EnumValueOptions, Builder> implements EnumValueOptionsOrBuilder
        {
            private int bitField0_;
            private List<UninterpretedOption> uninterpretedOption_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(EnumValueOptions.class, Builder.class);
            }
            
            private Builder() {
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getUninterpretedOptionFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_EnumValueOptions_descriptor;
            }
            
            public EnumValueOptions getDefaultInstanceForType() {
                return EnumValueOptions.getDefaultInstance();
            }
            
            public EnumValueOptions build() {
                final EnumValueOptions result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public EnumValueOptions buildPartial() {
                final EnumValueOptions result = new EnumValueOptions((ExtendableBuilder)this);
                final int from_bitField0_ = this.bitField0_;
                if (this.uninterpretedOptionBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.uninterpretedOption_ = this.uninterpretedOption_;
                }
                else {
                    result.uninterpretedOption_ = this.uninterpretedOptionBuilder_.build();
                }
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof EnumValueOptions) {
                    return this.mergeFrom((EnumValueOptions)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final EnumValueOptions other) {
                if (other == EnumValueOptions.getDefaultInstance()) {
                    return this;
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.uninterpretedOptionBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getUninterpretedOptionFieldBuilder() : null);
                    }
                    else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                this.mergeExtensionFields(other);
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                    if (!this.getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                return this.extensionsAreInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                EnumValueOptions parsedMessage = null;
                try {
                    parsedMessage = EnumValueOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (EnumValueOptions)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.uninterpretedOption_ = new ArrayList<UninterpretedOption>(this.uninterpretedOption_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            public List<UninterpretedOption> getUninterpretedOptionList() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                return this.uninterpretedOptionBuilder_.getMessageList();
            }
            
            public int getUninterpretedOptionCount() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.size();
                }
                return this.uninterpretedOptionBuilder_.getCount();
            }
            
            public UninterpretedOption getUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessage(index);
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllUninterpretedOption(final Iterable<? extends UninterpretedOption> values) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.uninterpretedOption_);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearUninterpretedOption() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.remove(index);
                }
                return this;
            }
            
            public UninterpretedOption.Builder getUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().getBuilder(index);
            }
            
            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                if (this.uninterpretedOptionBuilder_ != null) {
                    return this.uninterpretedOptionBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends UninterpretedOptionOrBuilder>)this.uninterpretedOption_);
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }
            
            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return this.getUninterpretedOptionFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder>(this.uninterpretedOption_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }
    
    public static final class ServiceOptions extends ExtendableMessage<ServiceOptions> implements ServiceOptionsOrBuilder
    {
        private static final ServiceOptions defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<ServiceOptions> PARSER;
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private List<UninterpretedOption> uninterpretedOption_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private ServiceOptions(final ExtendableBuilder<ServiceOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private ServiceOptions(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static ServiceOptions getDefaultInstance() {
            return ServiceOptions.defaultInstance;
        }
        
        public ServiceOptions getDefaultInstanceForType() {
            return ServiceOptions.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private ServiceOptions(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 7994: {
                            if ((mutable_bitField0_ & 0x1) != 0x1) {
                                this.uninterpretedOption_ = new ArrayList<UninterpretedOption>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x1) == 0x1) {
                    this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_ServiceOptions_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_ServiceOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(ServiceOptions.class, Builder.class);
        }
        
        @Override
        public Parser<ServiceOptions> getParserForType() {
            return ServiceOptions.PARSER;
        }
        
        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }
        
        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }
        
        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }
        
        public UninterpretedOption getUninterpretedOption(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        private void initFields() {
            this.uninterpretedOption_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                if (!this.getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (!this.extensionsAreInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            final ExtensionWriter extensionWriter = this.newExtensionWriter();
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            extensionWriter.writeUntil(536870912, output);
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            size += this.extensionsSerializedSize();
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static ServiceOptions parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return ServiceOptions.PARSER.parseFrom(data);
        }
        
        public static ServiceOptions parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ServiceOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ServiceOptions parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return ServiceOptions.PARSER.parseFrom(data);
        }
        
        public static ServiceOptions parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return ServiceOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static ServiceOptions parseFrom(final InputStream input) throws IOException {
            return ServiceOptions.PARSER.parseFrom(input);
        }
        
        public static ServiceOptions parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ServiceOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static ServiceOptions parseDelimitedFrom(final InputStream input) throws IOException {
            return ServiceOptions.PARSER.parseDelimitedFrom(input);
        }
        
        public static ServiceOptions parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ServiceOptions.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static ServiceOptions parseFrom(final CodedInputStream input) throws IOException {
            return ServiceOptions.PARSER.parseFrom(input);
        }
        
        public static ServiceOptions parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return ServiceOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final ServiceOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            ServiceOptions.PARSER = new AbstractParser<ServiceOptions>() {
                public ServiceOptions parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new ServiceOptions(input, extensionRegistry);
                }
            };
            (defaultInstance = new ServiceOptions(true)).initFields();
        }
        
        public static final class Builder extends ExtendableBuilder<ServiceOptions, Builder> implements ServiceOptionsOrBuilder
        {
            private int bitField0_;
            private List<UninterpretedOption> uninterpretedOption_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_ServiceOptions_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_ServiceOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(ServiceOptions.class, Builder.class);
            }
            
            private Builder() {
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getUninterpretedOptionFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_ServiceOptions_descriptor;
            }
            
            public ServiceOptions getDefaultInstanceForType() {
                return ServiceOptions.getDefaultInstance();
            }
            
            public ServiceOptions build() {
                final ServiceOptions result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public ServiceOptions buildPartial() {
                final ServiceOptions result = new ServiceOptions((ExtendableBuilder)this);
                final int from_bitField0_ = this.bitField0_;
                if (this.uninterpretedOptionBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.uninterpretedOption_ = this.uninterpretedOption_;
                }
                else {
                    result.uninterpretedOption_ = this.uninterpretedOptionBuilder_.build();
                }
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof ServiceOptions) {
                    return this.mergeFrom((ServiceOptions)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final ServiceOptions other) {
                if (other == ServiceOptions.getDefaultInstance()) {
                    return this;
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.uninterpretedOptionBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getUninterpretedOptionFieldBuilder() : null);
                    }
                    else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                this.mergeExtensionFields(other);
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                    if (!this.getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                return this.extensionsAreInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                ServiceOptions parsedMessage = null;
                try {
                    parsedMessage = ServiceOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (ServiceOptions)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.uninterpretedOption_ = new ArrayList<UninterpretedOption>(this.uninterpretedOption_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            public List<UninterpretedOption> getUninterpretedOptionList() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                return this.uninterpretedOptionBuilder_.getMessageList();
            }
            
            public int getUninterpretedOptionCount() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.size();
                }
                return this.uninterpretedOptionBuilder_.getCount();
            }
            
            public UninterpretedOption getUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessage(index);
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllUninterpretedOption(final Iterable<? extends UninterpretedOption> values) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.uninterpretedOption_);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearUninterpretedOption() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.remove(index);
                }
                return this;
            }
            
            public UninterpretedOption.Builder getUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().getBuilder(index);
            }
            
            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                if (this.uninterpretedOptionBuilder_ != null) {
                    return this.uninterpretedOptionBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends UninterpretedOptionOrBuilder>)this.uninterpretedOption_);
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }
            
            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return this.getUninterpretedOptionFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder>(this.uninterpretedOption_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }
    
    public static final class MethodOptions extends ExtendableMessage<MethodOptions> implements MethodOptionsOrBuilder
    {
        private static final MethodOptions defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<MethodOptions> PARSER;
        public static final int UNINTERPRETED_OPTION_FIELD_NUMBER = 999;
        private List<UninterpretedOption> uninterpretedOption_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private MethodOptions(final ExtendableBuilder<MethodOptions, ?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private MethodOptions(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static MethodOptions getDefaultInstance() {
            return MethodOptions.defaultInstance;
        }
        
        public MethodOptions getDefaultInstanceForType() {
            return MethodOptions.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private MethodOptions(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 7994: {
                            if ((mutable_bitField0_ & 0x1) != 0x1) {
                                this.uninterpretedOption_ = new ArrayList<UninterpretedOption>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.uninterpretedOption_.add(input.readMessage(UninterpretedOption.PARSER, extensionRegistry));
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x1) == 0x1) {
                    this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_MethodOptions_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_MethodOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(MethodOptions.class, Builder.class);
        }
        
        @Override
        public Parser<MethodOptions> getParserForType() {
            return MethodOptions.PARSER;
        }
        
        public List<UninterpretedOption> getUninterpretedOptionList() {
            return this.uninterpretedOption_;
        }
        
        public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
            return this.uninterpretedOption_;
        }
        
        public int getUninterpretedOptionCount() {
            return this.uninterpretedOption_.size();
        }
        
        public UninterpretedOption getUninterpretedOption(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
            return this.uninterpretedOption_.get(index);
        }
        
        private void initFields() {
            this.uninterpretedOption_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                if (!this.getUninterpretedOption(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            if (!this.extensionsAreInitialized()) {
                this.memoizedIsInitialized = 0;
                return false;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            final ExtensionWriter extensionWriter = this.newExtensionWriter();
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                output.writeMessage(999, this.uninterpretedOption_.get(i));
            }
            extensionWriter.writeUntil(536870912, output);
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            for (int i = 0; i < this.uninterpretedOption_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(999, this.uninterpretedOption_.get(i));
            }
            size += this.extensionsSerializedSize();
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static MethodOptions parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return MethodOptions.PARSER.parseFrom(data);
        }
        
        public static MethodOptions parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return MethodOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static MethodOptions parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return MethodOptions.PARSER.parseFrom(data);
        }
        
        public static MethodOptions parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return MethodOptions.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static MethodOptions parseFrom(final InputStream input) throws IOException {
            return MethodOptions.PARSER.parseFrom(input);
        }
        
        public static MethodOptions parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MethodOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static MethodOptions parseDelimitedFrom(final InputStream input) throws IOException {
            return MethodOptions.PARSER.parseDelimitedFrom(input);
        }
        
        public static MethodOptions parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MethodOptions.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static MethodOptions parseFrom(final CodedInputStream input) throws IOException {
            return MethodOptions.PARSER.parseFrom(input);
        }
        
        public static MethodOptions parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return MethodOptions.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final MethodOptions prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            MethodOptions.PARSER = new AbstractParser<MethodOptions>() {
                public MethodOptions parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new MethodOptions(input, extensionRegistry);
                }
            };
            (defaultInstance = new MethodOptions(true)).initFields();
        }
        
        public static final class Builder extends ExtendableBuilder<MethodOptions, Builder> implements MethodOptionsOrBuilder
        {
            private int bitField0_;
            private List<UninterpretedOption> uninterpretedOption_;
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> uninterpretedOptionBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_MethodOptions_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_MethodOptions_fieldAccessorTable.ensureFieldAccessorsInitialized(MethodOptions.class, Builder.class);
            }
            
            private Builder() {
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.uninterpretedOption_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getUninterpretedOptionFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_MethodOptions_descriptor;
            }
            
            public MethodOptions getDefaultInstanceForType() {
                return MethodOptions.getDefaultInstance();
            }
            
            public MethodOptions build() {
                final MethodOptions result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public MethodOptions buildPartial() {
                final MethodOptions result = new MethodOptions((ExtendableBuilder)this);
                final int from_bitField0_ = this.bitField0_;
                if (this.uninterpretedOptionBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.uninterpretedOption_ = Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.uninterpretedOption_ = this.uninterpretedOption_;
                }
                else {
                    result.uninterpretedOption_ = this.uninterpretedOptionBuilder_.build();
                }
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof MethodOptions) {
                    return this.mergeFrom((MethodOptions)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final MethodOptions other) {
                if (other == MethodOptions.getDefaultInstance()) {
                    return this;
                }
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (!other.uninterpretedOption_.isEmpty()) {
                        if (this.uninterpretedOption_.isEmpty()) {
                            this.uninterpretedOption_ = other.uninterpretedOption_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureUninterpretedOptionIsMutable();
                            this.uninterpretedOption_.addAll(other.uninterpretedOption_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.uninterpretedOption_.isEmpty()) {
                    if (this.uninterpretedOptionBuilder_.isEmpty()) {
                        this.uninterpretedOptionBuilder_.dispose();
                        this.uninterpretedOptionBuilder_ = null;
                        this.uninterpretedOption_ = other.uninterpretedOption_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.uninterpretedOptionBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getUninterpretedOptionFieldBuilder() : null);
                    }
                    else {
                        this.uninterpretedOptionBuilder_.addAllMessages(other.uninterpretedOption_);
                    }
                }
                this.mergeExtensionFields(other);
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getUninterpretedOptionCount(); ++i) {
                    if (!this.getUninterpretedOption(i).isInitialized()) {
                        return false;
                    }
                }
                return this.extensionsAreInitialized();
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                MethodOptions parsedMessage = null;
                try {
                    parsedMessage = MethodOptions.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (MethodOptions)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureUninterpretedOptionIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.uninterpretedOption_ = new ArrayList<UninterpretedOption>(this.uninterpretedOption_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            public List<UninterpretedOption> getUninterpretedOptionList() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends UninterpretedOption>)this.uninterpretedOption_);
                }
                return this.uninterpretedOptionBuilder_.getMessageList();
            }
            
            public int getUninterpretedOptionCount() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.size();
                }
                return this.uninterpretedOptionBuilder_.getCount();
            }
            
            public UninterpretedOption getUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessage(index);
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption value) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addUninterpretedOption(final int index, final UninterpretedOption.Builder builderForValue) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllUninterpretedOption(final Iterable<? extends UninterpretedOption> values) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.uninterpretedOption_);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearUninterpretedOption() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOption_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeUninterpretedOption(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.ensureUninterpretedOptionIsMutable();
                    this.uninterpretedOption_.remove(index);
                    this.onChanged();
                }
                else {
                    this.uninterpretedOptionBuilder_.remove(index);
                }
                return this;
            }
            
            public UninterpretedOption.Builder getUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().getBuilder(index);
            }
            
            public UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int index) {
                if (this.uninterpretedOptionBuilder_ == null) {
                    return this.uninterpretedOption_.get(index);
                }
                return this.uninterpretedOptionBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList() {
                if (this.uninterpretedOptionBuilder_ != null) {
                    return this.uninterpretedOptionBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends UninterpretedOptionOrBuilder>)this.uninterpretedOption_);
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder() {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(UninterpretedOption.getDefaultInstance());
            }
            
            public UninterpretedOption.Builder addUninterpretedOptionBuilder(final int index) {
                return this.getUninterpretedOptionFieldBuilder().addBuilder(index, UninterpretedOption.getDefaultInstance());
            }
            
            public List<UninterpretedOption.Builder> getUninterpretedOptionBuilderList() {
                return this.getUninterpretedOptionFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder> getUninterpretedOptionFieldBuilder() {
                if (this.uninterpretedOptionBuilder_ == null) {
                    this.uninterpretedOptionBuilder_ = new RepeatedFieldBuilder<UninterpretedOption, UninterpretedOption.Builder, UninterpretedOptionOrBuilder>(this.uninterpretedOption_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.uninterpretedOption_ = null;
                }
                return this.uninterpretedOptionBuilder_;
            }
        }
    }
    
    public static final class UninterpretedOption extends GeneratedMessage implements UninterpretedOptionOrBuilder
    {
        private static final UninterpretedOption defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<UninterpretedOption> PARSER;
        private int bitField0_;
        public static final int NAME_FIELD_NUMBER = 2;
        private List<NamePart> name_;
        public static final int IDENTIFIER_VALUE_FIELD_NUMBER = 3;
        private Object identifierValue_;
        public static final int POSITIVE_INT_VALUE_FIELD_NUMBER = 4;
        private long positiveIntValue_;
        public static final int NEGATIVE_INT_VALUE_FIELD_NUMBER = 5;
        private long negativeIntValue_;
        public static final int DOUBLE_VALUE_FIELD_NUMBER = 6;
        private double doubleValue_;
        public static final int STRING_VALUE_FIELD_NUMBER = 7;
        private ByteString stringValue_;
        public static final int AGGREGATE_VALUE_FIELD_NUMBER = 8;
        private Object aggregateValue_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private UninterpretedOption(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private UninterpretedOption(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static UninterpretedOption getDefaultInstance() {
            return UninterpretedOption.defaultInstance;
        }
        
        public UninterpretedOption getDefaultInstanceForType() {
            return UninterpretedOption.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private UninterpretedOption(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if ((mutable_bitField0_ & 0x1) != 0x1) {
                                this.name_ = new ArrayList<NamePart>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.name_.add(input.readMessage(NamePart.PARSER, extensionRegistry));
                            continue;
                        }
                        case 26: {
                            this.bitField0_ |= 0x1;
                            this.identifierValue_ = input.readBytes();
                            continue;
                        }
                        case 32: {
                            this.bitField0_ |= 0x2;
                            this.positiveIntValue_ = input.readUInt64();
                            continue;
                        }
                        case 40: {
                            this.bitField0_ |= 0x4;
                            this.negativeIntValue_ = input.readInt64();
                            continue;
                        }
                        case 49: {
                            this.bitField0_ |= 0x8;
                            this.doubleValue_ = input.readDouble();
                            continue;
                        }
                        case 58: {
                            this.bitField0_ |= 0x10;
                            this.stringValue_ = input.readBytes();
                            continue;
                        }
                        case 66: {
                            this.bitField0_ |= 0x20;
                            this.aggregateValue_ = input.readBytes();
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x1) == 0x1) {
                    this.name_ = Collections.unmodifiableList((List<? extends NamePart>)this.name_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_fieldAccessorTable.ensureFieldAccessorsInitialized(UninterpretedOption.class, Builder.class);
        }
        
        @Override
        public Parser<UninterpretedOption> getParserForType() {
            return UninterpretedOption.PARSER;
        }
        
        public List<NamePart> getNameList() {
            return this.name_;
        }
        
        public List<? extends NamePartOrBuilder> getNameOrBuilderList() {
            return this.name_;
        }
        
        public int getNameCount() {
            return this.name_.size();
        }
        
        public NamePart getName(final int index) {
            return this.name_.get(index);
        }
        
        public NamePartOrBuilder getNameOrBuilder(final int index) {
            return this.name_.get(index);
        }
        
        public boolean hasIdentifierValue() {
            return (this.bitField0_ & 0x1) == 0x1;
        }
        
        public String getIdentifierValue() {
            final Object ref = this.identifierValue_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.identifierValue_ = s;
            }
            return s;
        }
        
        public ByteString getIdentifierValueBytes() {
            final Object ref = this.identifierValue_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.identifierValue_ = b);
            }
            return (ByteString)ref;
        }
        
        public boolean hasPositiveIntValue() {
            return (this.bitField0_ & 0x2) == 0x2;
        }
        
        public long getPositiveIntValue() {
            return this.positiveIntValue_;
        }
        
        public boolean hasNegativeIntValue() {
            return (this.bitField0_ & 0x4) == 0x4;
        }
        
        public long getNegativeIntValue() {
            return this.negativeIntValue_;
        }
        
        public boolean hasDoubleValue() {
            return (this.bitField0_ & 0x8) == 0x8;
        }
        
        public double getDoubleValue() {
            return this.doubleValue_;
        }
        
        public boolean hasStringValue() {
            return (this.bitField0_ & 0x10) == 0x10;
        }
        
        public ByteString getStringValue() {
            return this.stringValue_;
        }
        
        public boolean hasAggregateValue() {
            return (this.bitField0_ & 0x20) == 0x20;
        }
        
        public String getAggregateValue() {
            final Object ref = this.aggregateValue_;
            if (ref instanceof String) {
                return (String)ref;
            }
            final ByteString bs = (ByteString)ref;
            final String s = bs.toStringUtf8();
            if (bs.isValidUtf8()) {
                this.aggregateValue_ = s;
            }
            return s;
        }
        
        public ByteString getAggregateValueBytes() {
            final Object ref = this.aggregateValue_;
            if (ref instanceof String) {
                final ByteString b = ByteString.copyFromUtf8((String)ref);
                return (ByteString)(this.aggregateValue_ = b);
            }
            return (ByteString)ref;
        }
        
        private void initFields() {
            this.name_ = Collections.emptyList();
            this.identifierValue_ = "";
            this.positiveIntValue_ = 0L;
            this.negativeIntValue_ = 0L;
            this.doubleValue_ = 0.0;
            this.stringValue_ = ByteString.EMPTY;
            this.aggregateValue_ = "";
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            for (int i = 0; i < this.getNameCount(); ++i) {
                if (!this.getName(i).isInitialized()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            for (int i = 0; i < this.name_.size(); ++i) {
                output.writeMessage(2, this.name_.get(i));
            }
            if ((this.bitField0_ & 0x1) == 0x1) {
                output.writeBytes(3, this.getIdentifierValueBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                output.writeUInt64(4, this.positiveIntValue_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                output.writeInt64(5, this.negativeIntValue_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                output.writeDouble(6, this.doubleValue_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                output.writeBytes(7, this.stringValue_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                output.writeBytes(8, this.getAggregateValueBytes());
            }
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            for (int i = 0; i < this.name_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(2, this.name_.get(i));
            }
            if ((this.bitField0_ & 0x1) == 0x1) {
                size += CodedOutputStream.computeBytesSize(3, this.getIdentifierValueBytes());
            }
            if ((this.bitField0_ & 0x2) == 0x2) {
                size += CodedOutputStream.computeUInt64Size(4, this.positiveIntValue_);
            }
            if ((this.bitField0_ & 0x4) == 0x4) {
                size += CodedOutputStream.computeInt64Size(5, this.negativeIntValue_);
            }
            if ((this.bitField0_ & 0x8) == 0x8) {
                size += CodedOutputStream.computeDoubleSize(6, this.doubleValue_);
            }
            if ((this.bitField0_ & 0x10) == 0x10) {
                size += CodedOutputStream.computeBytesSize(7, this.stringValue_);
            }
            if ((this.bitField0_ & 0x20) == 0x20) {
                size += CodedOutputStream.computeBytesSize(8, this.getAggregateValueBytes());
            }
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static UninterpretedOption parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return UninterpretedOption.PARSER.parseFrom(data);
        }
        
        public static UninterpretedOption parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return UninterpretedOption.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static UninterpretedOption parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return UninterpretedOption.PARSER.parseFrom(data);
        }
        
        public static UninterpretedOption parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return UninterpretedOption.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static UninterpretedOption parseFrom(final InputStream input) throws IOException {
            return UninterpretedOption.PARSER.parseFrom(input);
        }
        
        public static UninterpretedOption parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return UninterpretedOption.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static UninterpretedOption parseDelimitedFrom(final InputStream input) throws IOException {
            return UninterpretedOption.PARSER.parseDelimitedFrom(input);
        }
        
        public static UninterpretedOption parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return UninterpretedOption.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static UninterpretedOption parseFrom(final CodedInputStream input) throws IOException {
            return UninterpretedOption.PARSER.parseFrom(input);
        }
        
        public static UninterpretedOption parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return UninterpretedOption.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final UninterpretedOption prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            UninterpretedOption.PARSER = new AbstractParser<UninterpretedOption>() {
                public UninterpretedOption parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new UninterpretedOption(input, extensionRegistry);
                }
            };
            (defaultInstance = new UninterpretedOption(true)).initFields();
        }
        
        public static final class NamePart extends GeneratedMessage implements NamePartOrBuilder
        {
            private static final NamePart defaultInstance;
            private final UnknownFieldSet unknownFields;
            public static Parser<NamePart> PARSER;
            private int bitField0_;
            public static final int NAME_PART_FIELD_NUMBER = 1;
            private Object namePart_;
            public static final int IS_EXTENSION_FIELD_NUMBER = 2;
            private boolean isExtension_;
            private byte memoizedIsInitialized;
            private int memoizedSerializedSize;
            private static final long serialVersionUID = 0L;
            
            private NamePart(final GeneratedMessage.Builder<?> builder) {
                super(builder);
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.unknownFields = builder.getUnknownFields();
            }
            
            private NamePart(final boolean noInit) {
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.unknownFields = UnknownFieldSet.getDefaultInstance();
            }
            
            public static NamePart getDefaultInstance() {
                return NamePart.defaultInstance;
            }
            
            public NamePart getDefaultInstanceForType() {
                return NamePart.defaultInstance;
            }
            
            @Override
            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }
            
            private NamePart(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.initFields();
                final int mutable_bitField0_ = 0;
                final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
                try {
                    boolean done = false;
                    while (!done) {
                        final int tag = input.readTag();
                        switch (tag) {
                            case 0: {
                                done = true;
                                continue;
                            }
                            default: {
                                if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                    done = true;
                                    continue;
                                }
                                continue;
                            }
                            case 10: {
                                this.bitField0_ |= 0x1;
                                this.namePart_ = input.readBytes();
                                continue;
                            }
                            case 16: {
                                this.bitField0_ |= 0x2;
                                this.isExtension_ = input.readBool();
                                continue;
                            }
                        }
                    }
                }
                catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                }
                catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                }
                finally {
                    this.unknownFields = unknownFields.build();
                    this.makeExtensionsImmutable();
                }
            }
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_fieldAccessorTable.ensureFieldAccessorsInitialized(NamePart.class, Builder.class);
            }
            
            @Override
            public Parser<NamePart> getParserForType() {
                return NamePart.PARSER;
            }
            
            public boolean hasNamePart() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public String getNamePart() {
                final Object ref = this.namePart_;
                if (ref instanceof String) {
                    return (String)ref;
                }
                final ByteString bs = (ByteString)ref;
                final String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.namePart_ = s;
                }
                return s;
            }
            
            public ByteString getNamePartBytes() {
                final Object ref = this.namePart_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.namePart_ = b);
                }
                return (ByteString)ref;
            }
            
            public boolean hasIsExtension() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            public boolean getIsExtension() {
                return this.isExtension_;
            }
            
            private void initFields() {
                this.namePart_ = "";
                this.isExtension_ = false;
            }
            
            @Override
            public final boolean isInitialized() {
                final byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized != -1) {
                    return isInitialized == 1;
                }
                if (!this.hasNamePart()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                if (!this.hasIsExtension()) {
                    this.memoizedIsInitialized = 0;
                    return false;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }
            
            @Override
            public void writeTo(final CodedOutputStream output) throws IOException {
                this.getSerializedSize();
                if ((this.bitField0_ & 0x1) == 0x1) {
                    output.writeBytes(1, this.getNamePartBytes());
                }
                if ((this.bitField0_ & 0x2) == 0x2) {
                    output.writeBool(2, this.isExtension_);
                }
                this.getUnknownFields().writeTo(output);
            }
            
            @Override
            public int getSerializedSize() {
                int size = this.memoizedSerializedSize;
                if (size != -1) {
                    return size;
                }
                size = 0;
                if ((this.bitField0_ & 0x1) == 0x1) {
                    size += CodedOutputStream.computeBytesSize(1, this.getNamePartBytes());
                }
                if ((this.bitField0_ & 0x2) == 0x2) {
                    size += CodedOutputStream.computeBoolSize(2, this.isExtension_);
                }
                size += this.getUnknownFields().getSerializedSize();
                return this.memoizedSerializedSize = size;
            }
            
            @Override
            protected Object writeReplace() throws ObjectStreamException {
                return super.writeReplace();
            }
            
            public static NamePart parseFrom(final ByteString data) throws InvalidProtocolBufferException {
                return NamePart.PARSER.parseFrom(data);
            }
            
            public static NamePart parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return NamePart.PARSER.parseFrom(data, extensionRegistry);
            }
            
            public static NamePart parseFrom(final byte[] data) throws InvalidProtocolBufferException {
                return NamePart.PARSER.parseFrom(data);
            }
            
            public static NamePart parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return NamePart.PARSER.parseFrom(data, extensionRegistry);
            }
            
            public static NamePart parseFrom(final InputStream input) throws IOException {
                return NamePart.PARSER.parseFrom(input);
            }
            
            public static NamePart parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                return NamePart.PARSER.parseFrom(input, extensionRegistry);
            }
            
            public static NamePart parseDelimitedFrom(final InputStream input) throws IOException {
                return NamePart.PARSER.parseDelimitedFrom(input);
            }
            
            public static NamePart parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                return NamePart.PARSER.parseDelimitedFrom(input, extensionRegistry);
            }
            
            public static NamePart parseFrom(final CodedInputStream input) throws IOException {
                return NamePart.PARSER.parseFrom(input);
            }
            
            public static NamePart parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                return NamePart.PARSER.parseFrom(input, extensionRegistry);
            }
            
            public static Builder newBuilder() {
                return create();
            }
            
            public Builder newBuilderForType() {
                return newBuilder();
            }
            
            public static Builder newBuilder(final NamePart prototype) {
                return newBuilder().mergeFrom(prototype);
            }
            
            public Builder toBuilder() {
                return newBuilder(this);
            }
            
            @Override
            protected Builder newBuilderForType(final BuilderParent parent) {
                final Builder builder = new Builder(parent);
                return builder;
            }
            
            static {
                NamePart.PARSER = new AbstractParser<NamePart>() {
                    public NamePart parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                        return new NamePart(input, extensionRegistry);
                    }
                };
                (defaultInstance = new NamePart(true)).initFields();
            }
            
            public static final class Builder extends GeneratedMessage.Builder<Builder> implements NamePartOrBuilder
            {
                private int bitField0_;
                private Object namePart_;
                private boolean isExtension_;
                
                public static final Descriptors.Descriptor getDescriptor() {
                    return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_descriptor;
                }
                
                @Override
                protected FieldAccessorTable internalGetFieldAccessorTable() {
                    return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_fieldAccessorTable.ensureFieldAccessorsInitialized(NamePart.class, Builder.class);
                }
                
                private Builder() {
                    this.namePart_ = "";
                    this.maybeForceBuilderInitialization();
                }
                
                private Builder(final BuilderParent parent) {
                    super(parent);
                    this.namePart_ = "";
                    this.maybeForceBuilderInitialization();
                }
                
                private void maybeForceBuilderInitialization() {
                    if (GeneratedMessage.alwaysUseFieldBuilders) {}
                }
                
                private static Builder create() {
                    return new Builder();
                }
                
                @Override
                public Builder clear() {
                    super.clear();
                    this.namePart_ = "";
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.isExtension_ = false;
                    this.bitField0_ &= 0xFFFFFFFD;
                    return this;
                }
                
                @Override
                public Builder clone() {
                    return create().mergeFrom(this.buildPartial());
                }
                
                @Override
                public Descriptors.Descriptor getDescriptorForType() {
                    return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_NamePart_descriptor;
                }
                
                public NamePart getDefaultInstanceForType() {
                    return NamePart.getDefaultInstance();
                }
                
                public NamePart build() {
                    final NamePart result = this.buildPartial();
                    if (!result.isInitialized()) {
                        throw AbstractMessage.Builder.newUninitializedMessageException(result);
                    }
                    return result;
                }
                
                public NamePart buildPartial() {
                    final NamePart result = new NamePart((GeneratedMessage.Builder)this);
                    final int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((from_bitField0_ & 0x1) == 0x1) {
                        to_bitField0_ |= 0x1;
                    }
                    result.namePart_ = this.namePart_;
                    if ((from_bitField0_ & 0x2) == 0x2) {
                        to_bitField0_ |= 0x2;
                    }
                    result.isExtension_ = this.isExtension_;
                    result.bitField0_ = to_bitField0_;
                    this.onBuilt();
                    return result;
                }
                
                @Override
                public Builder mergeFrom(final Message other) {
                    if (other instanceof NamePart) {
                        return this.mergeFrom((NamePart)other);
                    }
                    super.mergeFrom(other);
                    return this;
                }
                
                public Builder mergeFrom(final NamePart other) {
                    if (other == NamePart.getDefaultInstance()) {
                        return this;
                    }
                    if (other.hasNamePart()) {
                        this.bitField0_ |= 0x1;
                        this.namePart_ = other.namePart_;
                        this.onChanged();
                    }
                    if (other.hasIsExtension()) {
                        this.setIsExtension(other.getIsExtension());
                    }
                    this.mergeUnknownFields(other.getUnknownFields());
                    return this;
                }
                
                @Override
                public final boolean isInitialized() {
                    return this.hasNamePart() && this.hasIsExtension();
                }
                
                @Override
                public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                    NamePart parsedMessage = null;
                    try {
                        parsedMessage = NamePart.PARSER.parsePartialFrom(input, extensionRegistry);
                    }
                    catch (InvalidProtocolBufferException e) {
                        parsedMessage = (NamePart)e.getUnfinishedMessage();
                        throw e;
                    }
                    finally {
                        if (parsedMessage != null) {
                            this.mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }
                
                public boolean hasNamePart() {
                    return (this.bitField0_ & 0x1) == 0x1;
                }
                
                public String getNamePart() {
                    final Object ref = this.namePart_;
                    if (!(ref instanceof String)) {
                        final String s = ((ByteString)ref).toStringUtf8();
                        return (String)(this.namePart_ = s);
                    }
                    return (String)ref;
                }
                
                public ByteString getNamePartBytes() {
                    final Object ref = this.namePart_;
                    if (ref instanceof String) {
                        final ByteString b = ByteString.copyFromUtf8((String)ref);
                        return (ByteString)(this.namePart_ = b);
                    }
                    return (ByteString)ref;
                }
                
                public Builder setNamePart(final String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x1;
                    this.namePart_ = value;
                    this.onChanged();
                    return this;
                }
                
                public Builder clearNamePart() {
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.namePart_ = NamePart.getDefaultInstance().getNamePart();
                    this.onChanged();
                    return this;
                }
                
                public Builder setNamePartBytes(final ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x1;
                    this.namePart_ = value;
                    this.onChanged();
                    return this;
                }
                
                public boolean hasIsExtension() {
                    return (this.bitField0_ & 0x2) == 0x2;
                }
                
                public boolean getIsExtension() {
                    return this.isExtension_;
                }
                
                public Builder setIsExtension(final boolean value) {
                    this.bitField0_ |= 0x2;
                    this.isExtension_ = value;
                    this.onChanged();
                    return this;
                }
                
                public Builder clearIsExtension() {
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.isExtension_ = false;
                    this.onChanged();
                    return this;
                }
            }
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements UninterpretedOptionOrBuilder
        {
            private int bitField0_;
            private List<NamePart> name_;
            private RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> nameBuilder_;
            private Object identifierValue_;
            private long positiveIntValue_;
            private long negativeIntValue_;
            private double doubleValue_;
            private ByteString stringValue_;
            private Object aggregateValue_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_fieldAccessorTable.ensureFieldAccessorsInitialized(UninterpretedOption.class, Builder.class);
            }
            
            private Builder() {
                this.name_ = Collections.emptyList();
                this.identifierValue_ = "";
                this.stringValue_ = ByteString.EMPTY;
                this.aggregateValue_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.name_ = Collections.emptyList();
                this.identifierValue_ = "";
                this.stringValue_ = ByteString.EMPTY;
                this.aggregateValue_ = "";
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getNameFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.nameBuilder_ == null) {
                    this.name_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.nameBuilder_.clear();
                }
                this.identifierValue_ = "";
                this.bitField0_ &= 0xFFFFFFFD;
                this.positiveIntValue_ = 0L;
                this.bitField0_ &= 0xFFFFFFFB;
                this.negativeIntValue_ = 0L;
                this.bitField0_ &= 0xFFFFFFF7;
                this.doubleValue_ = 0.0;
                this.bitField0_ &= 0xFFFFFFEF;
                this.stringValue_ = ByteString.EMPTY;
                this.bitField0_ &= 0xFFFFFFDF;
                this.aggregateValue_ = "";
                this.bitField0_ &= 0xFFFFFFBF;
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_UninterpretedOption_descriptor;
            }
            
            public UninterpretedOption getDefaultInstanceForType() {
                return UninterpretedOption.getDefaultInstance();
            }
            
            public UninterpretedOption build() {
                final UninterpretedOption result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public UninterpretedOption buildPartial() {
                final UninterpretedOption result = new UninterpretedOption((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if (this.nameBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.name_ = Collections.unmodifiableList((List<? extends NamePart>)this.name_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.name_ = this.name_;
                }
                else {
                    result.name_ = this.nameBuilder_.build();
                }
                if ((from_bitField0_ & 0x2) == 0x2) {
                    to_bitField0_ |= 0x1;
                }
                result.identifierValue_ = this.identifierValue_;
                if ((from_bitField0_ & 0x4) == 0x4) {
                    to_bitField0_ |= 0x2;
                }
                result.positiveIntValue_ = this.positiveIntValue_;
                if ((from_bitField0_ & 0x8) == 0x8) {
                    to_bitField0_ |= 0x4;
                }
                result.negativeIntValue_ = this.negativeIntValue_;
                if ((from_bitField0_ & 0x10) == 0x10) {
                    to_bitField0_ |= 0x8;
                }
                result.doubleValue_ = this.doubleValue_;
                if ((from_bitField0_ & 0x20) == 0x20) {
                    to_bitField0_ |= 0x10;
                }
                result.stringValue_ = this.stringValue_;
                if ((from_bitField0_ & 0x40) == 0x40) {
                    to_bitField0_ |= 0x20;
                }
                result.aggregateValue_ = this.aggregateValue_;
                result.bitField0_ = to_bitField0_;
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof UninterpretedOption) {
                    return this.mergeFrom((UninterpretedOption)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final UninterpretedOption other) {
                if (other == UninterpretedOption.getDefaultInstance()) {
                    return this;
                }
                if (this.nameBuilder_ == null) {
                    if (!other.name_.isEmpty()) {
                        if (this.name_.isEmpty()) {
                            this.name_ = other.name_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureNameIsMutable();
                            this.name_.addAll(other.name_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.name_.isEmpty()) {
                    if (this.nameBuilder_.isEmpty()) {
                        this.nameBuilder_.dispose();
                        this.nameBuilder_ = null;
                        this.name_ = other.name_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.nameBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getNameFieldBuilder() : null);
                    }
                    else {
                        this.nameBuilder_.addAllMessages(other.name_);
                    }
                }
                if (other.hasIdentifierValue()) {
                    this.bitField0_ |= 0x2;
                    this.identifierValue_ = other.identifierValue_;
                    this.onChanged();
                }
                if (other.hasPositiveIntValue()) {
                    this.setPositiveIntValue(other.getPositiveIntValue());
                }
                if (other.hasNegativeIntValue()) {
                    this.setNegativeIntValue(other.getNegativeIntValue());
                }
                if (other.hasDoubleValue()) {
                    this.setDoubleValue(other.getDoubleValue());
                }
                if (other.hasStringValue()) {
                    this.setStringValue(other.getStringValue());
                }
                if (other.hasAggregateValue()) {
                    this.bitField0_ |= 0x40;
                    this.aggregateValue_ = other.aggregateValue_;
                    this.onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                for (int i = 0; i < this.getNameCount(); ++i) {
                    if (!this.getName(i).isInitialized()) {
                        return false;
                    }
                }
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                UninterpretedOption parsedMessage = null;
                try {
                    parsedMessage = UninterpretedOption.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (UninterpretedOption)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureNameIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.name_ = new ArrayList<NamePart>(this.name_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            public List<NamePart> getNameList() {
                if (this.nameBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends NamePart>)this.name_);
                }
                return this.nameBuilder_.getMessageList();
            }
            
            public int getNameCount() {
                if (this.nameBuilder_ == null) {
                    return this.name_.size();
                }
                return this.nameBuilder_.getCount();
            }
            
            public NamePart getName(final int index) {
                if (this.nameBuilder_ == null) {
                    return this.name_.get(index);
                }
                return this.nameBuilder_.getMessage(index);
            }
            
            public Builder setName(final int index, final NamePart value) {
                if (this.nameBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureNameIsMutable();
                    this.name_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.nameBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setName(final int index, final NamePart.Builder builderForValue) {
                if (this.nameBuilder_ == null) {
                    this.ensureNameIsMutable();
                    this.name_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.nameBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addName(final NamePart value) {
                if (this.nameBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureNameIsMutable();
                    this.name_.add(value);
                    this.onChanged();
                }
                else {
                    this.nameBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addName(final int index, final NamePart value) {
                if (this.nameBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureNameIsMutable();
                    this.name_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.nameBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addName(final NamePart.Builder builderForValue) {
                if (this.nameBuilder_ == null) {
                    this.ensureNameIsMutable();
                    this.name_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.nameBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addName(final int index, final NamePart.Builder builderForValue) {
                if (this.nameBuilder_ == null) {
                    this.ensureNameIsMutable();
                    this.name_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.nameBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllName(final Iterable<? extends NamePart> values) {
                if (this.nameBuilder_ == null) {
                    this.ensureNameIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.name_);
                    this.onChanged();
                }
                else {
                    this.nameBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearName() {
                if (this.nameBuilder_ == null) {
                    this.name_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.nameBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeName(final int index) {
                if (this.nameBuilder_ == null) {
                    this.ensureNameIsMutable();
                    this.name_.remove(index);
                    this.onChanged();
                }
                else {
                    this.nameBuilder_.remove(index);
                }
                return this;
            }
            
            public NamePart.Builder getNameBuilder(final int index) {
                return this.getNameFieldBuilder().getBuilder(index);
            }
            
            public NamePartOrBuilder getNameOrBuilder(final int index) {
                if (this.nameBuilder_ == null) {
                    return this.name_.get(index);
                }
                return this.nameBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends NamePartOrBuilder> getNameOrBuilderList() {
                if (this.nameBuilder_ != null) {
                    return this.nameBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends NamePartOrBuilder>)this.name_);
            }
            
            public NamePart.Builder addNameBuilder() {
                return this.getNameFieldBuilder().addBuilder(NamePart.getDefaultInstance());
            }
            
            public NamePart.Builder addNameBuilder(final int index) {
                return this.getNameFieldBuilder().addBuilder(index, NamePart.getDefaultInstance());
            }
            
            public List<NamePart.Builder> getNameBuilderList() {
                return this.getNameFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder> getNameFieldBuilder() {
                if (this.nameBuilder_ == null) {
                    this.nameBuilder_ = new RepeatedFieldBuilder<NamePart, NamePart.Builder, NamePartOrBuilder>(this.name_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.name_ = null;
                }
                return this.nameBuilder_;
            }
            
            public boolean hasIdentifierValue() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            public String getIdentifierValue() {
                final Object ref = this.identifierValue_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.identifierValue_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getIdentifierValueBytes() {
                final Object ref = this.identifierValue_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.identifierValue_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setIdentifierValue(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.identifierValue_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearIdentifierValue() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.identifierValue_ = UninterpretedOption.getDefaultInstance().getIdentifierValue();
                this.onChanged();
                return this;
            }
            
            public Builder setIdentifierValueBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x2;
                this.identifierValue_ = value;
                this.onChanged();
                return this;
            }
            
            public boolean hasPositiveIntValue() {
                return (this.bitField0_ & 0x4) == 0x4;
            }
            
            public long getPositiveIntValue() {
                return this.positiveIntValue_;
            }
            
            public Builder setPositiveIntValue(final long value) {
                this.bitField0_ |= 0x4;
                this.positiveIntValue_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearPositiveIntValue() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.positiveIntValue_ = 0L;
                this.onChanged();
                return this;
            }
            
            public boolean hasNegativeIntValue() {
                return (this.bitField0_ & 0x8) == 0x8;
            }
            
            public long getNegativeIntValue() {
                return this.negativeIntValue_;
            }
            
            public Builder setNegativeIntValue(final long value) {
                this.bitField0_ |= 0x8;
                this.negativeIntValue_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearNegativeIntValue() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.negativeIntValue_ = 0L;
                this.onChanged();
                return this;
            }
            
            public boolean hasDoubleValue() {
                return (this.bitField0_ & 0x10) == 0x10;
            }
            
            public double getDoubleValue() {
                return this.doubleValue_;
            }
            
            public Builder setDoubleValue(final double value) {
                this.bitField0_ |= 0x10;
                this.doubleValue_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearDoubleValue() {
                this.bitField0_ &= 0xFFFFFFEF;
                this.doubleValue_ = 0.0;
                this.onChanged();
                return this;
            }
            
            public boolean hasStringValue() {
                return (this.bitField0_ & 0x20) == 0x20;
            }
            
            public ByteString getStringValue() {
                return this.stringValue_;
            }
            
            public Builder setStringValue(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x20;
                this.stringValue_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearStringValue() {
                this.bitField0_ &= 0xFFFFFFDF;
                this.stringValue_ = UninterpretedOption.getDefaultInstance().getStringValue();
                this.onChanged();
                return this;
            }
            
            public boolean hasAggregateValue() {
                return (this.bitField0_ & 0x40) == 0x40;
            }
            
            public String getAggregateValue() {
                final Object ref = this.aggregateValue_;
                if (!(ref instanceof String)) {
                    final String s = ((ByteString)ref).toStringUtf8();
                    return (String)(this.aggregateValue_ = s);
                }
                return (String)ref;
            }
            
            public ByteString getAggregateValueBytes() {
                final Object ref = this.aggregateValue_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.aggregateValue_ = b);
                }
                return (ByteString)ref;
            }
            
            public Builder setAggregateValue(final String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x40;
                this.aggregateValue_ = value;
                this.onChanged();
                return this;
            }
            
            public Builder clearAggregateValue() {
                this.bitField0_ &= 0xFFFFFFBF;
                this.aggregateValue_ = UninterpretedOption.getDefaultInstance().getAggregateValue();
                this.onChanged();
                return this;
            }
            
            public Builder setAggregateValueBytes(final ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 0x40;
                this.aggregateValue_ = value;
                this.onChanged();
                return this;
            }
        }
        
        public interface NamePartOrBuilder extends MessageOrBuilder
        {
            boolean hasNamePart();
            
            String getNamePart();
            
            ByteString getNamePartBytes();
            
            boolean hasIsExtension();
            
            boolean getIsExtension();
        }
    }
    
    public static final class SourceCodeInfo extends GeneratedMessage implements SourceCodeInfoOrBuilder
    {
        private static final SourceCodeInfo defaultInstance;
        private final UnknownFieldSet unknownFields;
        public static Parser<SourceCodeInfo> PARSER;
        public static final int LOCATION_FIELD_NUMBER = 1;
        private List<Location> location_;
        private byte memoizedIsInitialized;
        private int memoizedSerializedSize;
        private static final long serialVersionUID = 0L;
        
        private SourceCodeInfo(final GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = builder.getUnknownFields();
        }
        
        private SourceCodeInfo(final boolean noInit) {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.unknownFields = UnknownFieldSet.getDefaultInstance();
        }
        
        public static SourceCodeInfo getDefaultInstance() {
            return SourceCodeInfo.defaultInstance;
        }
        
        public SourceCodeInfo getDefaultInstanceForType() {
            return SourceCodeInfo.defaultInstance;
        }
        
        @Override
        public final UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }
        
        private SourceCodeInfo(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            this.memoizedIsInitialized = -1;
            this.memoizedSerializedSize = -1;
            this.initFields();
            int mutable_bitField0_ = 0;
            final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    final int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue;
                        }
                        default: {
                            if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if ((mutable_bitField0_ & 0x1) != 0x1) {
                                this.location_ = new ArrayList<Location>();
                                mutable_bitField0_ |= 0x1;
                            }
                            this.location_.add(input.readMessage(Location.PARSER, extensionRegistry));
                            continue;
                        }
                    }
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            }
            catch (IOException e2) {
                throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
            }
            finally {
                if ((mutable_bitField0_ & 0x1) == 0x1) {
                    this.location_ = Collections.unmodifiableList((List<? extends Location>)this.location_);
                }
                this.unknownFields = unknownFields.build();
                this.makeExtensionsImmutable();
            }
        }
        
        public static final Descriptors.Descriptor getDescriptor() {
            return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(SourceCodeInfo.class, Builder.class);
        }
        
        @Override
        public Parser<SourceCodeInfo> getParserForType() {
            return SourceCodeInfo.PARSER;
        }
        
        public List<Location> getLocationList() {
            return this.location_;
        }
        
        public List<? extends LocationOrBuilder> getLocationOrBuilderList() {
            return this.location_;
        }
        
        public int getLocationCount() {
            return this.location_.size();
        }
        
        public Location getLocation(final int index) {
            return this.location_.get(index);
        }
        
        public LocationOrBuilder getLocationOrBuilder(final int index) {
            return this.location_.get(index);
        }
        
        private void initFields() {
            this.location_ = Collections.emptyList();
        }
        
        @Override
        public final boolean isInitialized() {
            final byte isInitialized = this.memoizedIsInitialized;
            if (isInitialized != -1) {
                return isInitialized == 1;
            }
            this.memoizedIsInitialized = 1;
            return true;
        }
        
        @Override
        public void writeTo(final CodedOutputStream output) throws IOException {
            this.getSerializedSize();
            for (int i = 0; i < this.location_.size(); ++i) {
                output.writeMessage(1, this.location_.get(i));
            }
            this.getUnknownFields().writeTo(output);
        }
        
        @Override
        public int getSerializedSize() {
            int size = this.memoizedSerializedSize;
            if (size != -1) {
                return size;
            }
            size = 0;
            for (int i = 0; i < this.location_.size(); ++i) {
                size += CodedOutputStream.computeMessageSize(1, this.location_.get(i));
            }
            size += this.getUnknownFields().getSerializedSize();
            return this.memoizedSerializedSize = size;
        }
        
        @Override
        protected Object writeReplace() throws ObjectStreamException {
            return super.writeReplace();
        }
        
        public static SourceCodeInfo parseFrom(final ByteString data) throws InvalidProtocolBufferException {
            return SourceCodeInfo.PARSER.parseFrom(data);
        }
        
        public static SourceCodeInfo parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return SourceCodeInfo.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static SourceCodeInfo parseFrom(final byte[] data) throws InvalidProtocolBufferException {
            return SourceCodeInfo.PARSER.parseFrom(data);
        }
        
        public static SourceCodeInfo parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return SourceCodeInfo.PARSER.parseFrom(data, extensionRegistry);
        }
        
        public static SourceCodeInfo parseFrom(final InputStream input) throws IOException {
            return SourceCodeInfo.PARSER.parseFrom(input);
        }
        
        public static SourceCodeInfo parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return SourceCodeInfo.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static SourceCodeInfo parseDelimitedFrom(final InputStream input) throws IOException {
            return SourceCodeInfo.PARSER.parseDelimitedFrom(input);
        }
        
        public static SourceCodeInfo parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return SourceCodeInfo.PARSER.parseDelimitedFrom(input, extensionRegistry);
        }
        
        public static SourceCodeInfo parseFrom(final CodedInputStream input) throws IOException {
            return SourceCodeInfo.PARSER.parseFrom(input);
        }
        
        public static SourceCodeInfo parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
            return SourceCodeInfo.PARSER.parseFrom(input, extensionRegistry);
        }
        
        public static Builder newBuilder() {
            return create();
        }
        
        public Builder newBuilderForType() {
            return newBuilder();
        }
        
        public static Builder newBuilder(final SourceCodeInfo prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        
        public Builder toBuilder() {
            return newBuilder(this);
        }
        
        @Override
        protected Builder newBuilderForType(final BuilderParent parent) {
            final Builder builder = new Builder(parent);
            return builder;
        }
        
        static {
            SourceCodeInfo.PARSER = new AbstractParser<SourceCodeInfo>() {
                public SourceCodeInfo parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new SourceCodeInfo(input, extensionRegistry);
                }
            };
            (defaultInstance = new SourceCodeInfo(true)).initFields();
        }
        
        public static final class Location extends GeneratedMessage implements LocationOrBuilder
        {
            private static final Location defaultInstance;
            private final UnknownFieldSet unknownFields;
            public static Parser<Location> PARSER;
            private int bitField0_;
            public static final int PATH_FIELD_NUMBER = 1;
            private List<Integer> path_;
            private int pathMemoizedSerializedSize;
            public static final int SPAN_FIELD_NUMBER = 2;
            private List<Integer> span_;
            private int spanMemoizedSerializedSize;
            public static final int LEADING_COMMENTS_FIELD_NUMBER = 3;
            private Object leadingComments_;
            public static final int TRAILING_COMMENTS_FIELD_NUMBER = 4;
            private Object trailingComments_;
            private byte memoizedIsInitialized;
            private int memoizedSerializedSize;
            private static final long serialVersionUID = 0L;
            
            private Location(final GeneratedMessage.Builder<?> builder) {
                super(builder);
                this.pathMemoizedSerializedSize = -1;
                this.spanMemoizedSerializedSize = -1;
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.unknownFields = builder.getUnknownFields();
            }
            
            private Location(final boolean noInit) {
                this.pathMemoizedSerializedSize = -1;
                this.spanMemoizedSerializedSize = -1;
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.unknownFields = UnknownFieldSet.getDefaultInstance();
            }
            
            public static Location getDefaultInstance() {
                return Location.defaultInstance;
            }
            
            public Location getDefaultInstanceForType() {
                return Location.defaultInstance;
            }
            
            @Override
            public final UnknownFieldSet getUnknownFields() {
                return this.unknownFields;
            }
            
            private Location(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                this.pathMemoizedSerializedSize = -1;
                this.spanMemoizedSerializedSize = -1;
                this.memoizedIsInitialized = -1;
                this.memoizedSerializedSize = -1;
                this.initFields();
                int mutable_bitField0_ = 0;
                final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
                try {
                    boolean done = false;
                    while (!done) {
                        final int tag = input.readTag();
                        switch (tag) {
                            case 0: {
                                done = true;
                                continue;
                            }
                            default: {
                                if (!this.parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                    done = true;
                                    continue;
                                }
                                continue;
                            }
                            case 8: {
                                if ((mutable_bitField0_ & 0x1) != 0x1) {
                                    this.path_ = new ArrayList<Integer>();
                                    mutable_bitField0_ |= 0x1;
                                }
                                this.path_.add(input.readInt32());
                                continue;
                            }
                            case 10: {
                                final int length = input.readRawVarint32();
                                final int limit = input.pushLimit(length);
                                if ((mutable_bitField0_ & 0x1) != 0x1 && input.getBytesUntilLimit() > 0) {
                                    this.path_ = new ArrayList<Integer>();
                                    mutable_bitField0_ |= 0x1;
                                }
                                while (input.getBytesUntilLimit() > 0) {
                                    this.path_.add(input.readInt32());
                                }
                                input.popLimit(limit);
                                continue;
                            }
                            case 16: {
                                if ((mutable_bitField0_ & 0x2) != 0x2) {
                                    this.span_ = new ArrayList<Integer>();
                                    mutable_bitField0_ |= 0x2;
                                }
                                this.span_.add(input.readInt32());
                                continue;
                            }
                            case 18: {
                                final int length = input.readRawVarint32();
                                final int limit = input.pushLimit(length);
                                if ((mutable_bitField0_ & 0x2) != 0x2 && input.getBytesUntilLimit() > 0) {
                                    this.span_ = new ArrayList<Integer>();
                                    mutable_bitField0_ |= 0x2;
                                }
                                while (input.getBytesUntilLimit() > 0) {
                                    this.span_.add(input.readInt32());
                                }
                                input.popLimit(limit);
                                continue;
                            }
                            case 26: {
                                this.bitField0_ |= 0x1;
                                this.leadingComments_ = input.readBytes();
                                continue;
                            }
                            case 34: {
                                this.bitField0_ |= 0x2;
                                this.trailingComments_ = input.readBytes();
                                continue;
                            }
                        }
                    }
                }
                catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(this);
                }
                catch (IOException e2) {
                    throw new InvalidProtocolBufferException(e2.getMessage()).setUnfinishedMessage(this);
                }
                finally {
                    if ((mutable_bitField0_ & 0x1) == 0x1) {
                        this.path_ = Collections.unmodifiableList((List<? extends Integer>)this.path_);
                    }
                    if ((mutable_bitField0_ & 0x2) == 0x2) {
                        this.span_ = Collections.unmodifiableList((List<? extends Integer>)this.span_);
                    }
                    this.unknownFields = unknownFields.build();
                    this.makeExtensionsImmutable();
                }
            }
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_fieldAccessorTable.ensureFieldAccessorsInitialized(Location.class, Builder.class);
            }
            
            @Override
            public Parser<Location> getParserForType() {
                return Location.PARSER;
            }
            
            public List<Integer> getPathList() {
                return this.path_;
            }
            
            public int getPathCount() {
                return this.path_.size();
            }
            
            public int getPath(final int index) {
                return this.path_.get(index);
            }
            
            public List<Integer> getSpanList() {
                return this.span_;
            }
            
            public int getSpanCount() {
                return this.span_.size();
            }
            
            public int getSpan(final int index) {
                return this.span_.get(index);
            }
            
            public boolean hasLeadingComments() {
                return (this.bitField0_ & 0x1) == 0x1;
            }
            
            public String getLeadingComments() {
                final Object ref = this.leadingComments_;
                if (ref instanceof String) {
                    return (String)ref;
                }
                final ByteString bs = (ByteString)ref;
                final String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.leadingComments_ = s;
                }
                return s;
            }
            
            public ByteString getLeadingCommentsBytes() {
                final Object ref = this.leadingComments_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.leadingComments_ = b);
                }
                return (ByteString)ref;
            }
            
            public boolean hasTrailingComments() {
                return (this.bitField0_ & 0x2) == 0x2;
            }
            
            public String getTrailingComments() {
                final Object ref = this.trailingComments_;
                if (ref instanceof String) {
                    return (String)ref;
                }
                final ByteString bs = (ByteString)ref;
                final String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    this.trailingComments_ = s;
                }
                return s;
            }
            
            public ByteString getTrailingCommentsBytes() {
                final Object ref = this.trailingComments_;
                if (ref instanceof String) {
                    final ByteString b = ByteString.copyFromUtf8((String)ref);
                    return (ByteString)(this.trailingComments_ = b);
                }
                return (ByteString)ref;
            }
            
            private void initFields() {
                this.path_ = Collections.emptyList();
                this.span_ = Collections.emptyList();
                this.leadingComments_ = "";
                this.trailingComments_ = "";
            }
            
            @Override
            public final boolean isInitialized() {
                final byte isInitialized = this.memoizedIsInitialized;
                if (isInitialized != -1) {
                    return isInitialized == 1;
                }
                this.memoizedIsInitialized = 1;
                return true;
            }
            
            @Override
            public void writeTo(final CodedOutputStream output) throws IOException {
                this.getSerializedSize();
                if (this.getPathList().size() > 0) {
                    output.writeRawVarint32(10);
                    output.writeRawVarint32(this.pathMemoizedSerializedSize);
                }
                for (int i = 0; i < this.path_.size(); ++i) {
                    output.writeInt32NoTag(this.path_.get(i));
                }
                if (this.getSpanList().size() > 0) {
                    output.writeRawVarint32(18);
                    output.writeRawVarint32(this.spanMemoizedSerializedSize);
                }
                for (int i = 0; i < this.span_.size(); ++i) {
                    output.writeInt32NoTag(this.span_.get(i));
                }
                if ((this.bitField0_ & 0x1) == 0x1) {
                    output.writeBytes(3, this.getLeadingCommentsBytes());
                }
                if ((this.bitField0_ & 0x2) == 0x2) {
                    output.writeBytes(4, this.getTrailingCommentsBytes());
                }
                this.getUnknownFields().writeTo(output);
            }
            
            @Override
            public int getSerializedSize() {
                int size = this.memoizedSerializedSize;
                if (size != -1) {
                    return size;
                }
                size = 0;
                int dataSize = 0;
                for (int i = 0; i < this.path_.size(); ++i) {
                    dataSize += CodedOutputStream.computeInt32SizeNoTag(this.path_.get(i));
                }
                size += dataSize;
                if (!this.getPathList().isEmpty()) {
                    size = ++size + CodedOutputStream.computeInt32SizeNoTag(dataSize);
                }
                this.pathMemoizedSerializedSize = dataSize;
                dataSize = 0;
                for (int i = 0; i < this.span_.size(); ++i) {
                    dataSize += CodedOutputStream.computeInt32SizeNoTag(this.span_.get(i));
                }
                size += dataSize;
                if (!this.getSpanList().isEmpty()) {
                    size = ++size + CodedOutputStream.computeInt32SizeNoTag(dataSize);
                }
                this.spanMemoizedSerializedSize = dataSize;
                if ((this.bitField0_ & 0x1) == 0x1) {
                    size += CodedOutputStream.computeBytesSize(3, this.getLeadingCommentsBytes());
                }
                if ((this.bitField0_ & 0x2) == 0x2) {
                    size += CodedOutputStream.computeBytesSize(4, this.getTrailingCommentsBytes());
                }
                size += this.getUnknownFields().getSerializedSize();
                return this.memoizedSerializedSize = size;
            }
            
            @Override
            protected Object writeReplace() throws ObjectStreamException {
                return super.writeReplace();
            }
            
            public static Location parseFrom(final ByteString data) throws InvalidProtocolBufferException {
                return Location.PARSER.parseFrom(data);
            }
            
            public static Location parseFrom(final ByteString data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return Location.PARSER.parseFrom(data, extensionRegistry);
            }
            
            public static Location parseFrom(final byte[] data) throws InvalidProtocolBufferException {
                return Location.PARSER.parseFrom(data);
            }
            
            public static Location parseFrom(final byte[] data, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                return Location.PARSER.parseFrom(data, extensionRegistry);
            }
            
            public static Location parseFrom(final InputStream input) throws IOException {
                return Location.PARSER.parseFrom(input);
            }
            
            public static Location parseFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                return Location.PARSER.parseFrom(input, extensionRegistry);
            }
            
            public static Location parseDelimitedFrom(final InputStream input) throws IOException {
                return Location.PARSER.parseDelimitedFrom(input);
            }
            
            public static Location parseDelimitedFrom(final InputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                return Location.PARSER.parseDelimitedFrom(input, extensionRegistry);
            }
            
            public static Location parseFrom(final CodedInputStream input) throws IOException {
                return Location.PARSER.parseFrom(input);
            }
            
            public static Location parseFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                return Location.PARSER.parseFrom(input, extensionRegistry);
            }
            
            public static Builder newBuilder() {
                return create();
            }
            
            public Builder newBuilderForType() {
                return newBuilder();
            }
            
            public static Builder newBuilder(final Location prototype) {
                return newBuilder().mergeFrom(prototype);
            }
            
            public Builder toBuilder() {
                return newBuilder(this);
            }
            
            @Override
            protected Builder newBuilderForType(final BuilderParent parent) {
                final Builder builder = new Builder(parent);
                return builder;
            }
            
            static {
                Location.PARSER = new AbstractParser<Location>() {
                    public Location parsePartialFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                        return new Location(input, extensionRegistry);
                    }
                };
                (defaultInstance = new Location(true)).initFields();
            }
            
            public static final class Builder extends GeneratedMessage.Builder<Builder> implements LocationOrBuilder
            {
                private int bitField0_;
                private List<Integer> path_;
                private List<Integer> span_;
                private Object leadingComments_;
                private Object trailingComments_;
                
                public static final Descriptors.Descriptor getDescriptor() {
                    return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_descriptor;
                }
                
                @Override
                protected FieldAccessorTable internalGetFieldAccessorTable() {
                    return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_fieldAccessorTable.ensureFieldAccessorsInitialized(Location.class, Builder.class);
                }
                
                private Builder() {
                    this.path_ = Collections.emptyList();
                    this.span_ = Collections.emptyList();
                    this.leadingComments_ = "";
                    this.trailingComments_ = "";
                    this.maybeForceBuilderInitialization();
                }
                
                private Builder(final BuilderParent parent) {
                    super(parent);
                    this.path_ = Collections.emptyList();
                    this.span_ = Collections.emptyList();
                    this.leadingComments_ = "";
                    this.trailingComments_ = "";
                    this.maybeForceBuilderInitialization();
                }
                
                private void maybeForceBuilderInitialization() {
                    if (GeneratedMessage.alwaysUseFieldBuilders) {}
                }
                
                private static Builder create() {
                    return new Builder();
                }
                
                @Override
                public Builder clear() {
                    super.clear();
                    this.path_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.span_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.leadingComments_ = "";
                    this.bitField0_ &= 0xFFFFFFFB;
                    this.trailingComments_ = "";
                    this.bitField0_ &= 0xFFFFFFF7;
                    return this;
                }
                
                @Override
                public Builder clone() {
                    return create().mergeFrom(this.buildPartial());
                }
                
                @Override
                public Descriptors.Descriptor getDescriptorForType() {
                    return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_Location_descriptor;
                }
                
                public Location getDefaultInstanceForType() {
                    return Location.getDefaultInstance();
                }
                
                public Location build() {
                    final Location result = this.buildPartial();
                    if (!result.isInitialized()) {
                        throw AbstractMessage.Builder.newUninitializedMessageException(result);
                    }
                    return result;
                }
                
                public Location buildPartial() {
                    final Location result = new Location((GeneratedMessage.Builder)this);
                    final int from_bitField0_ = this.bitField0_;
                    int to_bitField0_ = 0;
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.path_ = Collections.unmodifiableList((List<? extends Integer>)this.path_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.path_ = this.path_;
                    if ((this.bitField0_ & 0x2) == 0x2) {
                        this.span_ = Collections.unmodifiableList((List<? extends Integer>)this.span_);
                        this.bitField0_ &= 0xFFFFFFFD;
                    }
                    result.span_ = this.span_;
                    if ((from_bitField0_ & 0x4) == 0x4) {
                        to_bitField0_ |= 0x1;
                    }
                    result.leadingComments_ = this.leadingComments_;
                    if ((from_bitField0_ & 0x8) == 0x8) {
                        to_bitField0_ |= 0x2;
                    }
                    result.trailingComments_ = this.trailingComments_;
                    result.bitField0_ = to_bitField0_;
                    this.onBuilt();
                    return result;
                }
                
                @Override
                public Builder mergeFrom(final Message other) {
                    if (other instanceof Location) {
                        return this.mergeFrom((Location)other);
                    }
                    super.mergeFrom(other);
                    return this;
                }
                
                public Builder mergeFrom(final Location other) {
                    if (other == Location.getDefaultInstance()) {
                        return this;
                    }
                    if (!other.path_.isEmpty()) {
                        if (this.path_.isEmpty()) {
                            this.path_ = other.path_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensurePathIsMutable();
                            this.path_.addAll(other.path_);
                        }
                        this.onChanged();
                    }
                    if (!other.span_.isEmpty()) {
                        if (this.span_.isEmpty()) {
                            this.span_ = other.span_;
                            this.bitField0_ &= 0xFFFFFFFD;
                        }
                        else {
                            this.ensureSpanIsMutable();
                            this.span_.addAll(other.span_);
                        }
                        this.onChanged();
                    }
                    if (other.hasLeadingComments()) {
                        this.bitField0_ |= 0x4;
                        this.leadingComments_ = other.leadingComments_;
                        this.onChanged();
                    }
                    if (other.hasTrailingComments()) {
                        this.bitField0_ |= 0x8;
                        this.trailingComments_ = other.trailingComments_;
                        this.onChanged();
                    }
                    this.mergeUnknownFields(other.getUnknownFields());
                    return this;
                }
                
                @Override
                public final boolean isInitialized() {
                    return true;
                }
                
                @Override
                public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                    Location parsedMessage = null;
                    try {
                        parsedMessage = Location.PARSER.parsePartialFrom(input, extensionRegistry);
                    }
                    catch (InvalidProtocolBufferException e) {
                        parsedMessage = (Location)e.getUnfinishedMessage();
                        throw e;
                    }
                    finally {
                        if (parsedMessage != null) {
                            this.mergeFrom(parsedMessage);
                        }
                    }
                    return this;
                }
                
                private void ensurePathIsMutable() {
                    if ((this.bitField0_ & 0x1) != 0x1) {
                        this.path_ = new ArrayList<Integer>(this.path_);
                        this.bitField0_ |= 0x1;
                    }
                }
                
                public List<Integer> getPathList() {
                    return Collections.unmodifiableList((List<? extends Integer>)this.path_);
                }
                
                public int getPathCount() {
                    return this.path_.size();
                }
                
                public int getPath(final int index) {
                    return this.path_.get(index);
                }
                
                public Builder setPath(final int index, final int value) {
                    this.ensurePathIsMutable();
                    this.path_.set(index, value);
                    this.onChanged();
                    return this;
                }
                
                public Builder addPath(final int value) {
                    this.ensurePathIsMutable();
                    this.path_.add(value);
                    this.onChanged();
                    return this;
                }
                
                public Builder addAllPath(final Iterable<? extends Integer> values) {
                    this.ensurePathIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.path_);
                    this.onChanged();
                    return this;
                }
                
                public Builder clearPath() {
                    this.path_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                    return this;
                }
                
                private void ensureSpanIsMutable() {
                    if ((this.bitField0_ & 0x2) != 0x2) {
                        this.span_ = new ArrayList<Integer>(this.span_);
                        this.bitField0_ |= 0x2;
                    }
                }
                
                public List<Integer> getSpanList() {
                    return Collections.unmodifiableList((List<? extends Integer>)this.span_);
                }
                
                public int getSpanCount() {
                    return this.span_.size();
                }
                
                public int getSpan(final int index) {
                    return this.span_.get(index);
                }
                
                public Builder setSpan(final int index, final int value) {
                    this.ensureSpanIsMutable();
                    this.span_.set(index, value);
                    this.onChanged();
                    return this;
                }
                
                public Builder addSpan(final int value) {
                    this.ensureSpanIsMutable();
                    this.span_.add(value);
                    this.onChanged();
                    return this;
                }
                
                public Builder addAllSpan(final Iterable<? extends Integer> values) {
                    this.ensureSpanIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.span_);
                    this.onChanged();
                    return this;
                }
                
                public Builder clearSpan() {
                    this.span_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.onChanged();
                    return this;
                }
                
                public boolean hasLeadingComments() {
                    return (this.bitField0_ & 0x4) == 0x4;
                }
                
                public String getLeadingComments() {
                    final Object ref = this.leadingComments_;
                    if (!(ref instanceof String)) {
                        final String s = ((ByteString)ref).toStringUtf8();
                        return (String)(this.leadingComments_ = s);
                    }
                    return (String)ref;
                }
                
                public ByteString getLeadingCommentsBytes() {
                    final Object ref = this.leadingComments_;
                    if (ref instanceof String) {
                        final ByteString b = ByteString.copyFromUtf8((String)ref);
                        return (ByteString)(this.leadingComments_ = b);
                    }
                    return (ByteString)ref;
                }
                
                public Builder setLeadingComments(final String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x4;
                    this.leadingComments_ = value;
                    this.onChanged();
                    return this;
                }
                
                public Builder clearLeadingComments() {
                    this.bitField0_ &= 0xFFFFFFFB;
                    this.leadingComments_ = Location.getDefaultInstance().getLeadingComments();
                    this.onChanged();
                    return this;
                }
                
                public Builder setLeadingCommentsBytes(final ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x4;
                    this.leadingComments_ = value;
                    this.onChanged();
                    return this;
                }
                
                public boolean hasTrailingComments() {
                    return (this.bitField0_ & 0x8) == 0x8;
                }
                
                public String getTrailingComments() {
                    final Object ref = this.trailingComments_;
                    if (!(ref instanceof String)) {
                        final String s = ((ByteString)ref).toStringUtf8();
                        return (String)(this.trailingComments_ = s);
                    }
                    return (String)ref;
                }
                
                public ByteString getTrailingCommentsBytes() {
                    final Object ref = this.trailingComments_;
                    if (ref instanceof String) {
                        final ByteString b = ByteString.copyFromUtf8((String)ref);
                        return (ByteString)(this.trailingComments_ = b);
                    }
                    return (ByteString)ref;
                }
                
                public Builder setTrailingComments(final String value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x8;
                    this.trailingComments_ = value;
                    this.onChanged();
                    return this;
                }
                
                public Builder clearTrailingComments() {
                    this.bitField0_ &= 0xFFFFFFF7;
                    this.trailingComments_ = Location.getDefaultInstance().getTrailingComments();
                    this.onChanged();
                    return this;
                }
                
                public Builder setTrailingCommentsBytes(final ByteString value) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.bitField0_ |= 0x8;
                    this.trailingComments_ = value;
                    this.onChanged();
                    return this;
                }
            }
        }
        
        public static final class Builder extends GeneratedMessage.Builder<Builder> implements SourceCodeInfoOrBuilder
        {
            private int bitField0_;
            private List<Location> location_;
            private RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> locationBuilder_;
            
            public static final Descriptors.Descriptor getDescriptor() {
                return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_descriptor;
            }
            
            @Override
            protected FieldAccessorTable internalGetFieldAccessorTable() {
                return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(SourceCodeInfo.class, Builder.class);
            }
            
            private Builder() {
                this.location_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private Builder(final BuilderParent parent) {
                super(parent);
                this.location_ = Collections.emptyList();
                this.maybeForceBuilderInitialization();
            }
            
            private void maybeForceBuilderInitialization() {
                if (GeneratedMessage.alwaysUseFieldBuilders) {
                    this.getLocationFieldBuilder();
                }
            }
            
            private static Builder create() {
                return new Builder();
            }
            
            @Override
            public Builder clear() {
                super.clear();
                if (this.locationBuilder_ == null) {
                    this.location_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                else {
                    this.locationBuilder_.clear();
                }
                return this;
            }
            
            @Override
            public Builder clone() {
                return create().mergeFrom(this.buildPartial());
            }
            
            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return DescriptorProtos.internal_static_google_protobuf_SourceCodeInfo_descriptor;
            }
            
            public SourceCodeInfo getDefaultInstanceForType() {
                return SourceCodeInfo.getDefaultInstance();
            }
            
            public SourceCodeInfo build() {
                final SourceCodeInfo result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw AbstractMessage.Builder.newUninitializedMessageException(result);
                }
                return result;
            }
            
            public SourceCodeInfo buildPartial() {
                final SourceCodeInfo result = new SourceCodeInfo((GeneratedMessage.Builder)this);
                final int from_bitField0_ = this.bitField0_;
                if (this.locationBuilder_ == null) {
                    if ((this.bitField0_ & 0x1) == 0x1) {
                        this.location_ = Collections.unmodifiableList((List<? extends Location>)this.location_);
                        this.bitField0_ &= 0xFFFFFFFE;
                    }
                    result.location_ = this.location_;
                }
                else {
                    result.location_ = this.locationBuilder_.build();
                }
                this.onBuilt();
                return result;
            }
            
            @Override
            public Builder mergeFrom(final Message other) {
                if (other instanceof SourceCodeInfo) {
                    return this.mergeFrom((SourceCodeInfo)other);
                }
                super.mergeFrom(other);
                return this;
            }
            
            public Builder mergeFrom(final SourceCodeInfo other) {
                if (other == SourceCodeInfo.getDefaultInstance()) {
                    return this;
                }
                if (this.locationBuilder_ == null) {
                    if (!other.location_.isEmpty()) {
                        if (this.location_.isEmpty()) {
                            this.location_ = other.location_;
                            this.bitField0_ &= 0xFFFFFFFE;
                        }
                        else {
                            this.ensureLocationIsMutable();
                            this.location_.addAll(other.location_);
                        }
                        this.onChanged();
                    }
                }
                else if (!other.location_.isEmpty()) {
                    if (this.locationBuilder_.isEmpty()) {
                        this.locationBuilder_.dispose();
                        this.locationBuilder_ = null;
                        this.location_ = other.location_;
                        this.bitField0_ &= 0xFFFFFFFE;
                        this.locationBuilder_ = (GeneratedMessage.alwaysUseFieldBuilders ? this.getLocationFieldBuilder() : null);
                    }
                    else {
                        this.locationBuilder_.addAllMessages(other.location_);
                    }
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }
            
            @Override
            public final boolean isInitialized() {
                return true;
            }
            
            @Override
            public Builder mergeFrom(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry) throws IOException {
                SourceCodeInfo parsedMessage = null;
                try {
                    parsedMessage = SourceCodeInfo.PARSER.parsePartialFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    parsedMessage = (SourceCodeInfo)e.getUnfinishedMessage();
                    throw e;
                }
                finally {
                    if (parsedMessage != null) {
                        this.mergeFrom(parsedMessage);
                    }
                }
                return this;
            }
            
            private void ensureLocationIsMutable() {
                if ((this.bitField0_ & 0x1) != 0x1) {
                    this.location_ = new ArrayList<Location>(this.location_);
                    this.bitField0_ |= 0x1;
                }
            }
            
            public List<Location> getLocationList() {
                if (this.locationBuilder_ == null) {
                    return Collections.unmodifiableList((List<? extends Location>)this.location_);
                }
                return this.locationBuilder_.getMessageList();
            }
            
            public int getLocationCount() {
                if (this.locationBuilder_ == null) {
                    return this.location_.size();
                }
                return this.locationBuilder_.getCount();
            }
            
            public Location getLocation(final int index) {
                if (this.locationBuilder_ == null) {
                    return this.location_.get(index);
                }
                return this.locationBuilder_.getMessage(index);
            }
            
            public Builder setLocation(final int index, final Location value) {
                if (this.locationBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureLocationIsMutable();
                    this.location_.set(index, value);
                    this.onChanged();
                }
                else {
                    this.locationBuilder_.setMessage(index, value);
                }
                return this;
            }
            
            public Builder setLocation(final int index, final Location.Builder builderForValue) {
                if (this.locationBuilder_ == null) {
                    this.ensureLocationIsMutable();
                    this.location_.set(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.locationBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addLocation(final Location value) {
                if (this.locationBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureLocationIsMutable();
                    this.location_.add(value);
                    this.onChanged();
                }
                else {
                    this.locationBuilder_.addMessage(value);
                }
                return this;
            }
            
            public Builder addLocation(final int index, final Location value) {
                if (this.locationBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.ensureLocationIsMutable();
                    this.location_.add(index, value);
                    this.onChanged();
                }
                else {
                    this.locationBuilder_.addMessage(index, value);
                }
                return this;
            }
            
            public Builder addLocation(final Location.Builder builderForValue) {
                if (this.locationBuilder_ == null) {
                    this.ensureLocationIsMutable();
                    this.location_.add(builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.locationBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }
            
            public Builder addLocation(final int index, final Location.Builder builderForValue) {
                if (this.locationBuilder_ == null) {
                    this.ensureLocationIsMutable();
                    this.location_.add(index, builderForValue.build());
                    this.onChanged();
                }
                else {
                    this.locationBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }
            
            public Builder addAllLocation(final Iterable<? extends Location> values) {
                if (this.locationBuilder_ == null) {
                    this.ensureLocationIsMutable();
                    AbstractMessageLite.Builder.addAll(values, this.location_);
                    this.onChanged();
                }
                else {
                    this.locationBuilder_.addAllMessages(values);
                }
                return this;
            }
            
            public Builder clearLocation() {
                if (this.locationBuilder_ == null) {
                    this.location_ = Collections.emptyList();
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.onChanged();
                }
                else {
                    this.locationBuilder_.clear();
                }
                return this;
            }
            
            public Builder removeLocation(final int index) {
                if (this.locationBuilder_ == null) {
                    this.ensureLocationIsMutable();
                    this.location_.remove(index);
                    this.onChanged();
                }
                else {
                    this.locationBuilder_.remove(index);
                }
                return this;
            }
            
            public Location.Builder getLocationBuilder(final int index) {
                return this.getLocationFieldBuilder().getBuilder(index);
            }
            
            public LocationOrBuilder getLocationOrBuilder(final int index) {
                if (this.locationBuilder_ == null) {
                    return this.location_.get(index);
                }
                return this.locationBuilder_.getMessageOrBuilder(index);
            }
            
            public List<? extends LocationOrBuilder> getLocationOrBuilderList() {
                if (this.locationBuilder_ != null) {
                    return this.locationBuilder_.getMessageOrBuilderList();
                }
                return Collections.unmodifiableList((List<? extends LocationOrBuilder>)this.location_);
            }
            
            public Location.Builder addLocationBuilder() {
                return this.getLocationFieldBuilder().addBuilder(Location.getDefaultInstance());
            }
            
            public Location.Builder addLocationBuilder(final int index) {
                return this.getLocationFieldBuilder().addBuilder(index, Location.getDefaultInstance());
            }
            
            public List<Location.Builder> getLocationBuilderList() {
                return this.getLocationFieldBuilder().getBuilderList();
            }
            
            private RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder> getLocationFieldBuilder() {
                if (this.locationBuilder_ == null) {
                    this.locationBuilder_ = new RepeatedFieldBuilder<Location, Location.Builder, LocationOrBuilder>(this.location_, (this.bitField0_ & 0x1) == 0x1, this.getParentForChildren(), this.isClean());
                    this.location_ = null;
                }
                return this.locationBuilder_;
            }
        }
        
        public interface LocationOrBuilder extends MessageOrBuilder
        {
            List<Integer> getPathList();
            
            int getPathCount();
            
            int getPath(final int p0);
            
            List<Integer> getSpanList();
            
            int getSpanCount();
            
            int getSpan(final int p0);
            
            boolean hasLeadingComments();
            
            String getLeadingComments();
            
            ByteString getLeadingCommentsBytes();
            
            boolean hasTrailingComments();
            
            String getTrailingComments();
            
            ByteString getTrailingCommentsBytes();
        }
    }
    
    public interface FieldDescriptorProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasName();
        
        String getName();
        
        ByteString getNameBytes();
        
        boolean hasNumber();
        
        int getNumber();
        
        boolean hasLabel();
        
        FieldDescriptorProto.Label getLabel();
        
        boolean hasType();
        
        FieldDescriptorProto.Type getType();
        
        boolean hasTypeName();
        
        String getTypeName();
        
        ByteString getTypeNameBytes();
        
        boolean hasExtendee();
        
        String getExtendee();
        
        ByteString getExtendeeBytes();
        
        boolean hasDefaultValue();
        
        String getDefaultValue();
        
        ByteString getDefaultValueBytes();
        
        boolean hasOptions();
        
        FieldOptions getOptions();
        
        FieldOptionsOrBuilder getOptionsOrBuilder();
    }
    
    public interface EnumValueDescriptorProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasName();
        
        String getName();
        
        ByteString getNameBytes();
        
        boolean hasNumber();
        
        int getNumber();
        
        boolean hasOptions();
        
        EnumValueOptions getOptions();
        
        EnumValueOptionsOrBuilder getOptionsOrBuilder();
    }
    
    public interface EnumValueOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<EnumValueOptions>
    {
        List<UninterpretedOption> getUninterpretedOptionList();
        
        UninterpretedOption getUninterpretedOption(final int p0);
        
        int getUninterpretedOptionCount();
        
        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();
        
        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int p0);
    }
    
    public interface UninterpretedOptionOrBuilder extends MessageOrBuilder
    {
        List<UninterpretedOption.NamePart> getNameList();
        
        UninterpretedOption.NamePart getName(final int p0);
        
        int getNameCount();
        
        List<? extends UninterpretedOption.NamePartOrBuilder> getNameOrBuilderList();
        
        UninterpretedOption.NamePartOrBuilder getNameOrBuilder(final int p0);
        
        boolean hasIdentifierValue();
        
        String getIdentifierValue();
        
        ByteString getIdentifierValueBytes();
        
        boolean hasPositiveIntValue();
        
        long getPositiveIntValue();
        
        boolean hasNegativeIntValue();
        
        long getNegativeIntValue();
        
        boolean hasDoubleValue();
        
        double getDoubleValue();
        
        boolean hasStringValue();
        
        ByteString getStringValue();
        
        boolean hasAggregateValue();
        
        String getAggregateValue();
        
        ByteString getAggregateValueBytes();
    }
    
    public interface MessageOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<MessageOptions>
    {
        boolean hasMessageSetWireFormat();
        
        boolean getMessageSetWireFormat();
        
        boolean hasNoStandardDescriptorAccessor();
        
        boolean getNoStandardDescriptorAccessor();
        
        List<UninterpretedOption> getUninterpretedOptionList();
        
        UninterpretedOption getUninterpretedOption(final int p0);
        
        int getUninterpretedOptionCount();
        
        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();
        
        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int p0);
    }
    
    public interface EnumOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<EnumOptions>
    {
        boolean hasAllowAlias();
        
        boolean getAllowAlias();
        
        List<UninterpretedOption> getUninterpretedOptionList();
        
        UninterpretedOption getUninterpretedOption(final int p0);
        
        int getUninterpretedOptionCount();
        
        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();
        
        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int p0);
    }
    
    public interface EnumDescriptorProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasName();
        
        String getName();
        
        ByteString getNameBytes();
        
        List<EnumValueDescriptorProto> getValueList();
        
        EnumValueDescriptorProto getValue(final int p0);
        
        int getValueCount();
        
        List<? extends EnumValueDescriptorProtoOrBuilder> getValueOrBuilderList();
        
        EnumValueDescriptorProtoOrBuilder getValueOrBuilder(final int p0);
        
        boolean hasOptions();
        
        EnumOptions getOptions();
        
        EnumOptionsOrBuilder getOptionsOrBuilder();
    }
    
    public interface DescriptorProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasName();
        
        String getName();
        
        ByteString getNameBytes();
        
        List<FieldDescriptorProto> getFieldList();
        
        FieldDescriptorProto getField(final int p0);
        
        int getFieldCount();
        
        List<? extends FieldDescriptorProtoOrBuilder> getFieldOrBuilderList();
        
        FieldDescriptorProtoOrBuilder getFieldOrBuilder(final int p0);
        
        List<FieldDescriptorProto> getExtensionList();
        
        FieldDescriptorProto getExtension(final int p0);
        
        int getExtensionCount();
        
        List<? extends FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList();
        
        FieldDescriptorProtoOrBuilder getExtensionOrBuilder(final int p0);
        
        List<DescriptorProto> getNestedTypeList();
        
        DescriptorProto getNestedType(final int p0);
        
        int getNestedTypeCount();
        
        List<? extends DescriptorProtoOrBuilder> getNestedTypeOrBuilderList();
        
        DescriptorProtoOrBuilder getNestedTypeOrBuilder(final int p0);
        
        List<EnumDescriptorProto> getEnumTypeList();
        
        EnumDescriptorProto getEnumType(final int p0);
        
        int getEnumTypeCount();
        
        List<? extends EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList();
        
        EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(final int p0);
        
        List<DescriptorProto.ExtensionRange> getExtensionRangeList();
        
        DescriptorProto.ExtensionRange getExtensionRange(final int p0);
        
        int getExtensionRangeCount();
        
        List<? extends DescriptorProto.ExtensionRangeOrBuilder> getExtensionRangeOrBuilderList();
        
        DescriptorProto.ExtensionRangeOrBuilder getExtensionRangeOrBuilder(final int p0);
        
        boolean hasOptions();
        
        MessageOptions getOptions();
        
        MessageOptionsOrBuilder getOptionsOrBuilder();
    }
    
    public interface MethodOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<MethodOptions>
    {
        List<UninterpretedOption> getUninterpretedOptionList();
        
        UninterpretedOption getUninterpretedOption(final int p0);
        
        int getUninterpretedOptionCount();
        
        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();
        
        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int p0);
    }
    
    public interface MethodDescriptorProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasName();
        
        String getName();
        
        ByteString getNameBytes();
        
        boolean hasInputType();
        
        String getInputType();
        
        ByteString getInputTypeBytes();
        
        boolean hasOutputType();
        
        String getOutputType();
        
        ByteString getOutputTypeBytes();
        
        boolean hasOptions();
        
        MethodOptions getOptions();
        
        MethodOptionsOrBuilder getOptionsOrBuilder();
    }
    
    public interface ServiceOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<ServiceOptions>
    {
        List<UninterpretedOption> getUninterpretedOptionList();
        
        UninterpretedOption getUninterpretedOption(final int p0);
        
        int getUninterpretedOptionCount();
        
        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();
        
        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int p0);
    }
    
    public interface ServiceDescriptorProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasName();
        
        String getName();
        
        ByteString getNameBytes();
        
        List<MethodDescriptorProto> getMethodList();
        
        MethodDescriptorProto getMethod(final int p0);
        
        int getMethodCount();
        
        List<? extends MethodDescriptorProtoOrBuilder> getMethodOrBuilderList();
        
        MethodDescriptorProtoOrBuilder getMethodOrBuilder(final int p0);
        
        boolean hasOptions();
        
        ServiceOptions getOptions();
        
        ServiceOptionsOrBuilder getOptionsOrBuilder();
    }
    
    public interface FileOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<FileOptions>
    {
        boolean hasJavaPackage();
        
        String getJavaPackage();
        
        ByteString getJavaPackageBytes();
        
        boolean hasJavaOuterClassname();
        
        String getJavaOuterClassname();
        
        ByteString getJavaOuterClassnameBytes();
        
        boolean hasJavaMultipleFiles();
        
        boolean getJavaMultipleFiles();
        
        boolean hasJavaGenerateEqualsAndHash();
        
        boolean getJavaGenerateEqualsAndHash();
        
        boolean hasOptimizeFor();
        
        FileOptions.OptimizeMode getOptimizeFor();
        
        boolean hasGoPackage();
        
        String getGoPackage();
        
        ByteString getGoPackageBytes();
        
        boolean hasCcGenericServices();
        
        boolean getCcGenericServices();
        
        boolean hasJavaGenericServices();
        
        boolean getJavaGenericServices();
        
        boolean hasPyGenericServices();
        
        boolean getPyGenericServices();
        
        List<UninterpretedOption> getUninterpretedOptionList();
        
        UninterpretedOption getUninterpretedOption(final int p0);
        
        int getUninterpretedOptionCount();
        
        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();
        
        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int p0);
    }
    
    public interface SourceCodeInfoOrBuilder extends MessageOrBuilder
    {
        List<SourceCodeInfo.Location> getLocationList();
        
        SourceCodeInfo.Location getLocation(final int p0);
        
        int getLocationCount();
        
        List<? extends SourceCodeInfo.LocationOrBuilder> getLocationOrBuilderList();
        
        SourceCodeInfo.LocationOrBuilder getLocationOrBuilder(final int p0);
    }
    
    public interface FileDescriptorProtoOrBuilder extends MessageOrBuilder
    {
        boolean hasName();
        
        String getName();
        
        ByteString getNameBytes();
        
        boolean hasPackage();
        
        String getPackage();
        
        ByteString getPackageBytes();
        
        List<String> getDependencyList();
        
        int getDependencyCount();
        
        String getDependency(final int p0);
        
        ByteString getDependencyBytes(final int p0);
        
        List<Integer> getPublicDependencyList();
        
        int getPublicDependencyCount();
        
        int getPublicDependency(final int p0);
        
        List<Integer> getWeakDependencyList();
        
        int getWeakDependencyCount();
        
        int getWeakDependency(final int p0);
        
        List<DescriptorProto> getMessageTypeList();
        
        DescriptorProto getMessageType(final int p0);
        
        int getMessageTypeCount();
        
        List<? extends DescriptorProtoOrBuilder> getMessageTypeOrBuilderList();
        
        DescriptorProtoOrBuilder getMessageTypeOrBuilder(final int p0);
        
        List<EnumDescriptorProto> getEnumTypeList();
        
        EnumDescriptorProto getEnumType(final int p0);
        
        int getEnumTypeCount();
        
        List<? extends EnumDescriptorProtoOrBuilder> getEnumTypeOrBuilderList();
        
        EnumDescriptorProtoOrBuilder getEnumTypeOrBuilder(final int p0);
        
        List<ServiceDescriptorProto> getServiceList();
        
        ServiceDescriptorProto getService(final int p0);
        
        int getServiceCount();
        
        List<? extends ServiceDescriptorProtoOrBuilder> getServiceOrBuilderList();
        
        ServiceDescriptorProtoOrBuilder getServiceOrBuilder(final int p0);
        
        List<FieldDescriptorProto> getExtensionList();
        
        FieldDescriptorProto getExtension(final int p0);
        
        int getExtensionCount();
        
        List<? extends FieldDescriptorProtoOrBuilder> getExtensionOrBuilderList();
        
        FieldDescriptorProtoOrBuilder getExtensionOrBuilder(final int p0);
        
        boolean hasOptions();
        
        FileOptions getOptions();
        
        FileOptionsOrBuilder getOptionsOrBuilder();
        
        boolean hasSourceCodeInfo();
        
        SourceCodeInfo getSourceCodeInfo();
        
        SourceCodeInfoOrBuilder getSourceCodeInfoOrBuilder();
    }
    
    public interface FieldOptionsOrBuilder extends GeneratedMessage.ExtendableMessageOrBuilder<FieldOptions>
    {
        boolean hasCtype();
        
        FieldOptions.CType getCtype();
        
        boolean hasPacked();
        
        boolean getPacked();
        
        boolean hasLazy();
        
        boolean getLazy();
        
        boolean hasDeprecated();
        
        boolean getDeprecated();
        
        boolean hasExperimentalMapKey();
        
        String getExperimentalMapKey();
        
        ByteString getExperimentalMapKeyBytes();
        
        boolean hasWeak();
        
        boolean getWeak();
        
        List<UninterpretedOption> getUninterpretedOptionList();
        
        UninterpretedOption getUninterpretedOption(final int p0);
        
        int getUninterpretedOptionCount();
        
        List<? extends UninterpretedOptionOrBuilder> getUninterpretedOptionOrBuilderList();
        
        UninterpretedOptionOrBuilder getUninterpretedOptionOrBuilder(final int p0);
    }
    
    public interface FileDescriptorSetOrBuilder extends MessageOrBuilder
    {
        List<FileDescriptorProto> getFileList();
        
        FileDescriptorProto getFile(final int p0);
        
        int getFileCount();
        
        List<? extends FileDescriptorProtoOrBuilder> getFileOrBuilderList();
        
        FileDescriptorProtoOrBuilder getFileOrBuilder(final int p0);
    }
}
