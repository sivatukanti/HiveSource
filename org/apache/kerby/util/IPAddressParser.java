// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.util;

public class IPAddressParser
{
    public static byte[] parseIPv4Literal(String s) {
        s = ((s != null) ? s.trim() : "");
        final String[] toks = s.split("\\.");
        final byte[] ip = new byte[4];
        if (toks.length == 4) {
            for (int i = 0; i < ip.length; ++i) {
                try {
                    final int val = Integer.parseInt(toks[i]);
                    if (val < 0 || val > 255) {
                        return null;
                    }
                    ip[i] = (byte)val;
                }
                catch (NumberFormatException nfe) {
                    return null;
                }
            }
            return ip;
        }
        return null;
    }
    
    public static byte[] parseIPv6Literal(String s) {
        s = ((s != null) ? s.trim() : "");
        if (s.length() > 0 && s.charAt(0) == '[' && s.charAt(s.length() - 1) == ']') {
            s = s.substring(1, s.length() - 1).trim();
        }
        int x = s.lastIndexOf(58);
        final int y = s.indexOf(46);
        if (x >= 0 && y > x) {
            final byte[] ip4Suffix = parseIPv4Literal(s.substring(x + 1));
            if (ip4Suffix == null) {
                return null;
            }
            s = s.substring(0, x) + ":" + ip4ToHex(ip4Suffix);
        }
        x = s.indexOf("::");
        if (x >= 0 && s.indexOf("::", x + 1) >= 0) {
            return null;
        }
        String[] raw = { "0000", "0000", "0000", "0000", "0000", "0000", "0000", "0000" };
        if (s.indexOf("::") >= 0) {
            final String[] split = s.split("::", -1);
            final String[] prefix = splitOnColon(split[0]);
            final String[] suffix = splitOnColon(split[1]);
            if (prefix.length + suffix.length > 7) {
                return null;
            }
            for (int i = 0; i < prefix.length; ++i) {
                raw[i] = prependZeroes(prefix[i]);
            }
            final int startPos = raw.length - suffix.length;
            for (int j = 0; j < suffix.length; ++j) {
                raw[startPos + j] = prependZeroes(suffix[j]);
            }
        }
        else {
            raw = splitOnColon(s);
            if (raw.length != 8) {
                return null;
            }
            for (int k = 0; k < raw.length; ++k) {
                raw[k] = prependZeroes(raw[k]);
            }
        }
        final byte[] ip6 = new byte[16];
        int l = 0;
        for (int m = 0; m < raw.length; ++m) {
            final String tok = raw[m];
            if (tok.length() > 4) {
                return null;
            }
            final String prefix2 = tok.substring(0, 2);
            final String suffix2 = tok.substring(2, 4);
            try {
                ip6[l++] = (byte)Integer.parseInt(prefix2, 16);
                ip6[l++] = (byte)Integer.parseInt(suffix2, 16);
            }
            catch (NumberFormatException nfe) {
                return null;
            }
        }
        return ip6;
    }
    
    private static String prependZeroes(final String s) {
        switch (s.length()) {
            case 0: {
                return "0000";
            }
            case 1: {
                return "000" + s;
            }
            case 2: {
                return "00" + s;
            }
            case 3: {
                return "0" + s;
            }
            default: {
                return s;
            }
        }
    }
    
    private static String[] splitOnColon(final String s) {
        if ("".equals(s)) {
            return new String[0];
        }
        return s.split(":");
    }
    
    private static String ip4ToHex(final byte[] b) {
        return b2s(b[0]) + b2s(b[1]) + ":" + b2s(b[2]) + b2s(b[3]);
    }
    
    private static String b2s(final byte b) {
        String s = Integer.toHexString((b >= 0) ? b : (256 + b));
        if (s.length() < 2) {
            s = "0" + s;
        }
        return s;
    }
}
