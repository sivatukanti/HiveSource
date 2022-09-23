// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import com.google.common.base.Preconditions;
import org.apache.commons.codec.DecoderException;
import java.io.IOException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public enum XAttrCodec
{
    TEXT, 
    HEX, 
    BASE64;
    
    private static final String HEX_PREFIX = "0x";
    private static final String BASE64_PREFIX = "0s";
    private static final Base64 base64;
    
    public static byte[] decodeValue(String value) throws IOException {
        byte[] result = null;
        if (value != null) {
            Label_0128: {
                if (value.length() >= 2) {
                    final String en = value.substring(0, 2);
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                        result = value.getBytes("utf-8");
                    }
                    else {
                        if (en.equalsIgnoreCase("0x")) {
                            value = value.substring(2, value.length());
                            try {
                                result = Hex.decodeHex(value.toCharArray());
                                break Label_0128;
                            }
                            catch (DecoderException e) {
                                throw new IOException(e);
                            }
                        }
                        if (en.equalsIgnoreCase("0s")) {
                            value = value.substring(2, value.length());
                            result = XAttrCodec.base64.decode(value);
                        }
                    }
                }
            }
            if (result == null) {
                result = value.getBytes("utf-8");
            }
        }
        return result;
    }
    
    public static String encodeValue(final byte[] value, final XAttrCodec encoding) throws IOException {
        Preconditions.checkNotNull(value, (Object)"Value can not be null.");
        if (encoding == XAttrCodec.HEX) {
            return "0x" + Hex.encodeHexString(value);
        }
        if (encoding == XAttrCodec.BASE64) {
            return "0s" + XAttrCodec.base64.encodeToString(value);
        }
        return "\"" + new String(value, "utf-8") + "\"";
    }
    
    static {
        base64 = new Base64(0);
    }
}
