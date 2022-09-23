// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.protocol;

import org.apache.thrift.TException;
import java.util.BitSet;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.transport.TTransport;

public final class TTupleProtocol extends TCompactProtocol
{
    public TTupleProtocol(final TTransport transport) {
        super(transport);
    }
    
    @Override
    public Class<? extends IScheme> getScheme() {
        return TupleScheme.class;
    }
    
    public void writeBitSet(final BitSet bs, final int vectorWidth) throws TException {
        final byte[] arr$;
        final byte[] bytes = arr$ = toByteArray(bs, vectorWidth);
        for (final byte b : arr$) {
            this.writeByte(b);
        }
    }
    
    public BitSet readBitSet(final int i) throws TException {
        final int length = (int)Math.ceil(i / 8.0);
        final byte[] bytes = new byte[length];
        for (int j = 0; j < length; ++j) {
            bytes[j] = this.readByte();
        }
        final BitSet bs = fromByteArray(bytes);
        return bs;
    }
    
    public static BitSet fromByteArray(final byte[] bytes) {
        final BitSet bits = new BitSet();
        for (int i = 0; i < bytes.length * 8; ++i) {
            if ((bytes[bytes.length - i / 8 - 1] & 1 << i % 8) > 0) {
                bits.set(i);
            }
        }
        return bits;
    }
    
    public static byte[] toByteArray(final BitSet bits, final int vectorWidth) {
        final byte[] bytes = new byte[(int)Math.ceil(vectorWidth / 8.0)];
        for (int i = 0; i < bits.length(); ++i) {
            if (bits.get(i)) {
                final byte[] array = bytes;
                final int n = bytes.length - i / 8 - 1;
                array[n] |= (byte)(1 << i % 8);
            }
        }
        return bytes;
    }
    
    public static class Factory implements TProtocolFactory
    {
        public TProtocol getProtocol(final TTransport trans) {
            return new TTupleProtocol(trans);
        }
    }
}
