// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import java.util.Arrays;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variants;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import java.util.UUID;

public class UUIDDeserializer extends FromStringDeserializer<UUID>
{
    private static final long serialVersionUID = 1L;
    static final int[] HEX_DIGITS;
    
    public UUIDDeserializer() {
        super(UUID.class);
    }
    
    @Override
    protected UUID _deserialize(final String id, final DeserializationContext ctxt) throws IOException {
        if (id.length() != 36) {
            if (id.length() == 24) {
                final byte[] stuff = Base64Variants.getDefaultVariant().decode(id);
                return this._fromBytes(stuff, ctxt);
            }
            this._badFormat(id);
        }
        if (id.charAt(8) != '-' || id.charAt(13) != '-' || id.charAt(18) != '-' || id.charAt(23) != '-') {
            this._badFormat(id);
        }
        long l1 = intFromChars(id, 0);
        l1 <<= 32;
        long l2 = (long)shortFromChars(id, 9) << 16;
        l2 |= shortFromChars(id, 14);
        final long hi = l1 + l2;
        final int i1 = shortFromChars(id, 19) << 16 | shortFromChars(id, 24);
        l1 = i1;
        l1 <<= 32;
        l2 = intFromChars(id, 28);
        l2 = l2 << 32 >>> 32;
        final long lo = l1 | l2;
        return new UUID(hi, lo);
    }
    
    @Override
    protected UUID _deserializeEmbedded(final Object ob, final DeserializationContext ctxt) throws IOException {
        if (ob instanceof byte[]) {
            return this._fromBytes((byte[])ob, ctxt);
        }
        super._deserializeEmbedded(ob, ctxt);
        return null;
    }
    
    private void _badFormat(final String uuidStr) {
        throw new NumberFormatException("UUID has to be represented by the standard 36-char representation");
    }
    
    static int intFromChars(final String str, final int index) {
        return (byteFromChars(str, index) << 24) + (byteFromChars(str, index + 2) << 16) + (byteFromChars(str, index + 4) << 8) + byteFromChars(str, index + 6);
    }
    
    static int shortFromChars(final String str, final int index) {
        return (byteFromChars(str, index) << 8) + byteFromChars(str, index + 2);
    }
    
    static int byteFromChars(final String str, final int index) {
        final char c1 = str.charAt(index);
        final char c2 = str.charAt(index + 1);
        if (c1 <= '\u007f' && c2 <= '\u007f') {
            final int hex = UUIDDeserializer.HEX_DIGITS[c1] << 4 | UUIDDeserializer.HEX_DIGITS[c2];
            if (hex >= 0) {
                return hex;
            }
        }
        if (c1 > '\u007f' || UUIDDeserializer.HEX_DIGITS[c1] < 0) {
            return _badChar(str, index, c1);
        }
        return _badChar(str, index + 1, c2);
    }
    
    static int _badChar(final String uuidStr, final int index, final char c) {
        throw new NumberFormatException("Non-hex character '" + c + "', not valid character for a UUID String" + "' (value 0x" + Integer.toHexString(c) + ") for UUID String \"" + uuidStr + "\"");
    }
    
    private UUID _fromBytes(final byte[] bytes, final DeserializationContext ctxt) throws IOException {
        if (bytes.length != 16) {
            ctxt.mappingException("Can only construct UUIDs from byte[16]; got " + bytes.length + " bytes");
        }
        return new UUID(_long(bytes, 0), _long(bytes, 8));
    }
    
    private static long _long(final byte[] b, final int offset) {
        final long l1 = (long)_int(b, offset) << 32;
        long l2 = _int(b, offset + 4);
        l2 = l2 << 32 >>> 32;
        return l1 | l2;
    }
    
    private static int _int(final byte[] b, final int offset) {
        return b[offset] << 24 | (b[offset + 1] & 0xFF) << 16 | (b[offset + 2] & 0xFF) << 8 | (b[offset + 3] & 0xFF);
    }
    
    static {
        Arrays.fill(HEX_DIGITS = new int[127], -1);
        for (int i = 0; i < 10; ++i) {
            UUIDDeserializer.HEX_DIGITS[48 + i] = i;
        }
        for (int i = 0; i < 6; ++i) {
            UUIDDeserializer.HEX_DIGITS[97 + i] = 10 + i;
            UUIDDeserializer.HEX_DIGITS[65 + i] = 10 + i;
        }
    }
}
