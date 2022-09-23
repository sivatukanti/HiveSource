// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;

public class TApplicationException extends TException
{
    private static final TStruct TAPPLICATION_EXCEPTION_STRUCT;
    private static final TField MESSAGE_FIELD;
    private static final TField TYPE_FIELD;
    private static final long serialVersionUID = 1L;
    public static final int UNKNOWN = 0;
    public static final int UNKNOWN_METHOD = 1;
    public static final int INVALID_MESSAGE_TYPE = 2;
    public static final int WRONG_METHOD_NAME = 3;
    public static final int BAD_SEQUENCE_ID = 4;
    public static final int MISSING_RESULT = 5;
    public static final int INTERNAL_ERROR = 6;
    public static final int PROTOCOL_ERROR = 7;
    public static final int INVALID_TRANSFORM = 8;
    public static final int INVALID_PROTOCOL = 9;
    public static final int UNSUPPORTED_CLIENT_TYPE = 10;
    protected int type_;
    
    public TApplicationException() {
        this.type_ = 0;
    }
    
    public TApplicationException(final int type) {
        this.type_ = 0;
        this.type_ = type;
    }
    
    public TApplicationException(final int type, final String message) {
        super(message);
        this.type_ = 0;
        this.type_ = type;
    }
    
    public TApplicationException(final String message) {
        super(message);
        this.type_ = 0;
    }
    
    public int getType() {
        return this.type_;
    }
    
    public static TApplicationException read(final TProtocol iprot) throws TException {
        iprot.readStructBegin();
        String message = null;
        int type = 0;
        while (true) {
            final TField field = iprot.readFieldBegin();
            if (field.type == 0) {
                break;
            }
            switch (field.id) {
                case 1: {
                    if (field.type == 11) {
                        message = iprot.readString();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                case 2: {
                    if (field.type == 8) {
                        type = iprot.readI32();
                        break;
                    }
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
                default: {
                    TProtocolUtil.skip(iprot, field.type);
                    break;
                }
            }
            iprot.readFieldEnd();
        }
        iprot.readStructEnd();
        return new TApplicationException(type, message);
    }
    
    public void write(final TProtocol oprot) throws TException {
        oprot.writeStructBegin(TApplicationException.TAPPLICATION_EXCEPTION_STRUCT);
        if (this.getMessage() != null) {
            oprot.writeFieldBegin(TApplicationException.MESSAGE_FIELD);
            oprot.writeString(this.getMessage());
            oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(TApplicationException.TYPE_FIELD);
        oprot.writeI32(this.type_);
        oprot.writeFieldEnd();
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }
    
    static {
        TAPPLICATION_EXCEPTION_STRUCT = new TStruct("TApplicationException");
        MESSAGE_FIELD = new TField("message", (byte)11, (short)1);
        TYPE_FIELD = new TField("type", (byte)8, (short)2);
    }
}
