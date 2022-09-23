// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import org.apache.kerby.asn1.UniversalTag;

public class Asn1ObjectIdentifier extends Asn1Simple<String>
{
    public Asn1ObjectIdentifier() {
        this((String)null);
    }
    
    public Asn1ObjectIdentifier(final String value) {
        super(UniversalTag.OBJECT_IDENTIFIER, value);
    }
    
    @Override
    protected void toBytes() {
        final byte[][] bytesArr = this.convert(this.getValue());
        int allLen = 0;
        for (final byte[] bytes : bytesArr) {
            allLen += bytes.length;
        }
        final ByteBuffer buffer = ByteBuffer.allocate(allLen);
        for (final byte[] bytes2 : bytesArr) {
            buffer.put(bytes2);
        }
        this.setBytes(buffer.array());
    }
    
    @Override
    protected void toValue() {
        final StringBuilder sb = new StringBuilder();
        final byte[] bytes = this.getBytes();
        final byte[][] bytesGroups = this.group(bytes);
        final BigInteger[] coms = this.convert(bytesGroups);
        final long first = coms[0].longValue();
        sb.append(first / 40L).append('.');
        sb.append(first % 40L);
        if (coms.length > 1) {
            sb.append('.');
        }
        for (int i = 1; i < coms.length; ++i) {
            sb.append(coms[i].toString());
            if (i != coms.length - 1) {
                sb.append('.');
            }
        }
        final String value = sb.toString();
        this.setValue(value);
    }
    
    private BigInteger[] convert(final byte[][] bytesGroups) {
        final BigInteger[] comps = new BigInteger[bytesGroups.length];
        for (int i = 0; i < bytesGroups.length; ++i) {
            comps[i] = this.convert(bytesGroups[i]);
        }
        return comps;
    }
    
    private BigInteger convert(final byte[] bytes) {
        BigInteger value = BigInteger.valueOf(bytes[0] & 0x7F);
        for (int i = 1; i < bytes.length; ++i) {
            value = value.shiftLeft(7);
            value = value.or(BigInteger.valueOf(bytes[i] & 0x7F));
        }
        return value;
    }
    
    private byte[][] group(final byte[] bytes) {
        int count = 0;
        final int[] countArr = new int[bytes.length];
        for (int i = 0; i < countArr.length; ++i) {
            countArr[i] = 0;
        }
        int j = 0;
        for (int i = 0; i < bytes.length; ++i) {
            if ((bytes[i] & 0x80) != 0x0) {
                final int[] array = countArr;
                final int n = j;
                ++array[n];
            }
            else {
                final int[] array2 = countArr;
                final int n2 = j++;
                ++array2[n2];
            }
        }
        count = j;
        final byte[][] bytesGroups = new byte[count][];
        for (int i = 0; i < count; ++i) {
            bytesGroups[i] = new byte[countArr[i]];
        }
        int k = 0;
        j = 0;
        for (int i = 0; i < bytes.length; ++i) {
            bytesGroups[j][k++] = bytes[i];
            if ((bytes[i] & 0x80) == 0x0) {
                ++j;
                k = 0;
            }
        }
        return bytesGroups;
    }
    
    private byte[][] convert(final String oid) {
        final String[] parts = oid.split("\\.");
        final BigInteger[] coms = new BigInteger[parts.length - 1];
        for (int i = 1; i < parts.length; ++i) {
            coms[i - 1] = new BigInteger(parts[i]);
        }
        coms[0] = coms[0].add(BigInteger.valueOf(Integer.parseInt(parts[0]) * 40));
        final byte[][] bytesGroups = new byte[coms.length][];
        for (int j = 0; j < coms.length; ++j) {
            bytesGroups[j] = this.convert(coms[j]);
        }
        return bytesGroups;
    }
    
    private byte[] convert(final BigInteger value) {
        final int bitLen = value.bitLength();
        if (bitLen < 8) {
            return new byte[] { value.byteValue() };
        }
        final int len = (bitLen + 6) / 7;
        final byte[] bytes = new byte[len];
        BigInteger tmpValue = value;
        for (int i = len - 1; i >= 0; --i) {
            bytes[i] = (byte)((tmpValue.byteValue() & 0x7F) | 0x80);
            tmpValue = tmpValue.shiftRight(7);
        }
        final byte[] array = bytes;
        final int n = len - 1;
        array[n] &= 0x7F;
        return bytes;
    }
}
