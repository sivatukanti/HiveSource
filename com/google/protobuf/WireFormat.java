// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

public final class WireFormat
{
    public static final int WIRETYPE_VARINT = 0;
    public static final int WIRETYPE_FIXED64 = 1;
    public static final int WIRETYPE_LENGTH_DELIMITED = 2;
    public static final int WIRETYPE_START_GROUP = 3;
    public static final int WIRETYPE_END_GROUP = 4;
    public static final int WIRETYPE_FIXED32 = 5;
    static final int TAG_TYPE_BITS = 3;
    static final int TAG_TYPE_MASK = 7;
    static final int MESSAGE_SET_ITEM = 1;
    static final int MESSAGE_SET_TYPE_ID = 2;
    static final int MESSAGE_SET_MESSAGE = 3;
    static final int MESSAGE_SET_ITEM_TAG;
    static final int MESSAGE_SET_ITEM_END_TAG;
    static final int MESSAGE_SET_TYPE_ID_TAG;
    static final int MESSAGE_SET_MESSAGE_TAG;
    
    private WireFormat() {
    }
    
    static int getTagWireType(final int tag) {
        return tag & 0x7;
    }
    
    public static int getTagFieldNumber(final int tag) {
        return tag >>> 3;
    }
    
    static int makeTag(final int fieldNumber, final int wireType) {
        return fieldNumber << 3 | wireType;
    }
    
    static {
        MESSAGE_SET_ITEM_TAG = makeTag(1, 3);
        MESSAGE_SET_ITEM_END_TAG = makeTag(1, 4);
        MESSAGE_SET_TYPE_ID_TAG = makeTag(2, 0);
        MESSAGE_SET_MESSAGE_TAG = makeTag(3, 2);
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
        
        Object getDefaultDefault() {
            return this.defaultDefault;
        }
    }
    
    public enum FieldType
    {
        DOUBLE(JavaType.DOUBLE, 1), 
        FLOAT(JavaType.FLOAT, 5), 
        INT64(JavaType.LONG, 0), 
        UINT64(JavaType.LONG, 0), 
        INT32(JavaType.INT, 0), 
        FIXED64(JavaType.LONG, 1), 
        FIXED32(JavaType.INT, 5), 
        BOOL(JavaType.BOOLEAN, 0), 
        STRING(JavaType.STRING, 2) {
            @Override
            public boolean isPackable() {
                return false;
            }
        }, 
        GROUP(JavaType.MESSAGE, 3) {
            @Override
            public boolean isPackable() {
                return false;
            }
        }, 
        MESSAGE(JavaType.MESSAGE, 2) {
            @Override
            public boolean isPackable() {
                return false;
            }
        }, 
        BYTES(JavaType.BYTE_STRING, 2) {
            @Override
            public boolean isPackable() {
                return false;
            }
        }, 
        UINT32(JavaType.INT, 0), 
        ENUM(JavaType.ENUM, 0), 
        SFIXED32(JavaType.INT, 5), 
        SFIXED64(JavaType.LONG, 1), 
        SINT32(JavaType.INT, 0), 
        SINT64(JavaType.LONG, 0);
        
        private final JavaType javaType;
        private final int wireType;
        
        private FieldType(final JavaType javaType, final int wireType) {
            this.javaType = javaType;
            this.wireType = wireType;
        }
        
        public JavaType getJavaType() {
            return this.javaType;
        }
        
        public int getWireType() {
            return this.wireType;
        }
        
        public boolean isPackable() {
            return true;
        }
    }
}
