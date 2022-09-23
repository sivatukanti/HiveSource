// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.protocol;

import parquet.org.apache.thrift.TException;

public class TProtocolUtil
{
    private static int maxSkipDepth;
    
    public static void setMaxSkipDepth(final int depth) {
        TProtocolUtil.maxSkipDepth = depth;
    }
    
    public static void skip(final TProtocol prot, final byte type) throws TException {
        skip(prot, type, TProtocolUtil.maxSkipDepth);
    }
    
    public static void skip(final TProtocol prot, final byte type, final int maxDepth) throws TException {
        if (maxDepth <= 0) {
            throw new TException("Maximum skip depth exceeded");
        }
        switch (type) {
            case 2: {
                prot.readBool();
                break;
            }
            case 3: {
                prot.readByte();
                break;
            }
            case 6: {
                prot.readI16();
                break;
            }
            case 8: {
                prot.readI32();
                break;
            }
            case 10: {
                prot.readI64();
                break;
            }
            case 4: {
                prot.readDouble();
                break;
            }
            case 11: {
                prot.readBinary();
                break;
            }
            case 12: {
                prot.readStructBegin();
                while (true) {
                    final TField field = prot.readFieldBegin();
                    if (field.type == 0) {
                        break;
                    }
                    skip(prot, field.type, maxDepth - 1);
                    prot.readFieldEnd();
                }
                prot.readStructEnd();
                break;
            }
            case 13: {
                final TMap map = prot.readMapBegin();
                for (int i = 0; i < map.size; ++i) {
                    skip(prot, map.keyType, maxDepth - 1);
                    skip(prot, map.valueType, maxDepth - 1);
                }
                prot.readMapEnd();
                break;
            }
            case 14: {
                final TSet set = prot.readSetBegin();
                for (int j = 0; j < set.size; ++j) {
                    skip(prot, set.elemType, maxDepth - 1);
                }
                prot.readSetEnd();
                break;
            }
            case 15: {
                final TList list = prot.readListBegin();
                for (int k = 0; k < list.size; ++k) {
                    skip(prot, list.elemType, maxDepth - 1);
                }
                prot.readListEnd();
                break;
            }
        }
    }
    
    public static TProtocolFactory guessProtocolFactory(final byte[] data, final TProtocolFactory fallback) {
        if (123 == data[0] && 125 == data[data.length - 1]) {
            return new TJSONProtocol.Factory();
        }
        if (data[data.length - 1] != 0) {
            return new TBinaryProtocol.Factory();
        }
        if (data[0] > 16) {
            return new TCompactProtocol.Factory();
        }
        if (data.length > 1 && 0 == data[1]) {
            return new TBinaryProtocol.Factory();
        }
        if (data.length > 1 && (data[1] & 0x80) != 0x0) {
            return new TCompactProtocol.Factory();
        }
        return fallback;
    }
    
    static {
        TProtocolUtil.maxSkipDepth = Integer.MAX_VALUE;
    }
}
