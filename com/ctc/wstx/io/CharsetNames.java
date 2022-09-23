// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import java.io.OutputStreamWriter;
import java.io.Writer;
import com.ctc.wstx.util.StringUtil;

public final class CharsetNames
{
    public static final String CS_US_ASCII = "US-ASCII";
    public static final String CS_UTF8 = "UTF-8";
    public static final String CS_UTF16 = "UTF-16";
    public static final String CS_UTF16BE = "UTF-16BE";
    public static final String CS_UTF16LE = "UTF-16LE";
    public static final String CS_UTF32 = "UTF-32";
    public static final String CS_UTF32BE = "UTF-32BE";
    public static final String CS_UTF32LE = "UTF-32LE";
    public static final String CS_ISO_LATIN1 = "ISO-8859-1";
    public static final String CS_SHIFT_JIS = "Shift_JIS";
    public static final String CS_EBCDIC_SUBSET = "IBM037";
    
    public static String normalize(String csName) {
        if (csName == null || csName.length() < 3) {
            return csName;
        }
        boolean gotCsPrefix = false;
        char c = csName.charAt(0);
        if (c == 'c' || c == 'C') {
            final char d = csName.charAt(1);
            if (d == 's' || d == 'S') {
                csName = csName.substring(2);
                c = csName.charAt(0);
                gotCsPrefix = true;
            }
        }
        Label_1048: {
            switch (c) {
                case 'A':
                case 'a': {
                    if (StringUtil.equalEncodings(csName, "ASCII")) {
                        return "US-ASCII";
                    }
                    break;
                }
                case 'C':
                case 'c': {
                    if (StringUtil.encodingStartsWith(csName, "cp")) {
                        return "IBM" + StringUtil.trimEncoding(csName, true).substring(2);
                    }
                    if (StringUtil.encodingStartsWith(csName, "cs") && StringUtil.encodingStartsWith(csName, "csIBM")) {
                        return StringUtil.trimEncoding(csName, true).substring(2);
                    }
                    break;
                }
                case 'E':
                case 'e': {
                    if (!csName.startsWith("EBCDIC-CP-") && !csName.startsWith("ebcdic-cp-")) {
                        break;
                    }
                    final String type = StringUtil.trimEncoding(csName, true).substring(8);
                    if (type.equals("US") || type.equals("CA") || type.equals("WT") || type.equals("NL")) {
                        return "IBM037";
                    }
                    if (type.equals("DK") || type.equals("NO")) {
                        return "IBM277";
                    }
                    if (type.equals("FI") || type.equals("SE")) {
                        return "IBM278";
                    }
                    if (type.equals("ROECE") || type.equals("YU")) {
                        return "IBM870";
                    }
                    if (type.equals("IT")) {
                        return "IBM280";
                    }
                    if (type.equals("ES")) {
                        return "IBM284";
                    }
                    if (type.equals("GB")) {
                        return "IBM285";
                    }
                    if (type.equals("FR")) {
                        return "IBM297";
                    }
                    if (type.equals("AR1")) {
                        return "IBM420";
                    }
                    if (type.equals("AR2")) {
                        return "IBM918";
                    }
                    if (type.equals("HE")) {
                        return "IBM424";
                    }
                    if (type.equals("CH")) {
                        return "IBM500";
                    }
                    if (type.equals("IS")) {
                        return "IBM871";
                    }
                    return "IBM037";
                }
                case 'I':
                case 'i': {
                    if (StringUtil.equalEncodings(csName, "ISO-8859-1") || StringUtil.equalEncodings(csName, "ISO-Latin1")) {
                        return "ISO-8859-1";
                    }
                    if (StringUtil.encodingStartsWith(csName, "ISO-10646")) {
                        final int ix = csName.indexOf("10646");
                        final String suffix = csName.substring(ix + 5);
                        if (StringUtil.equalEncodings(suffix, "UCS-Basic")) {
                            return "US-ASCII";
                        }
                        if (StringUtil.equalEncodings(suffix, "Unicode-Latin1")) {
                            return "ISO-8859-1";
                        }
                        if (StringUtil.equalEncodings(suffix, "UCS-2")) {
                            return "UTF-16";
                        }
                        if (StringUtil.equalEncodings(suffix, "UCS-4")) {
                            return "UTF-32";
                        }
                        if (StringUtil.equalEncodings(suffix, "UTF-1")) {
                            return "US-ASCII";
                        }
                        if (StringUtil.equalEncodings(suffix, "J-1")) {
                            return "US-ASCII";
                        }
                        if (StringUtil.equalEncodings(suffix, "US-ASCII")) {
                            return "US-ASCII";
                        }
                        break;
                    }
                    else {
                        if (StringUtil.encodingStartsWith(csName, "IBM")) {
                            return csName;
                        }
                        break;
                    }
                    break;
                }
                case 'J':
                case 'j': {
                    if (StringUtil.equalEncodings(csName, "JIS_Encoding")) {
                        return "Shift_JIS";
                    }
                    break;
                }
                case 'S':
                case 's': {
                    if (StringUtil.equalEncodings(csName, "Shift_JIS")) {
                        return "Shift_JIS";
                    }
                    break;
                }
                case 'U':
                case 'u': {
                    if (csName.length() < 2) {
                        break;
                    }
                    switch (csName.charAt(1)) {
                        case 'C':
                        case 'c': {
                            if (StringUtil.equalEncodings(csName, "UCS-2")) {
                                return "UTF-16";
                            }
                            if (StringUtil.equalEncodings(csName, "UCS-4")) {
                                return "UTF-32";
                            }
                            break Label_1048;
                        }
                        case 'N':
                        case 'n': {
                            if (!gotCsPrefix) {
                                break Label_1048;
                            }
                            if (StringUtil.equalEncodings(csName, "Unicode")) {
                                return "UTF-16";
                            }
                            if (StringUtil.equalEncodings(csName, "UnicodeAscii")) {
                                return "ISO-8859-1";
                            }
                            if (StringUtil.equalEncodings(csName, "UnicodeAscii")) {
                                return "US-ASCII";
                            }
                            break Label_1048;
                        }
                        case 'S':
                        case 's': {
                            if (StringUtil.equalEncodings(csName, "US-ASCII")) {
                                return "US-ASCII";
                            }
                            break Label_1048;
                        }
                        case 'T':
                        case 't': {
                            if (StringUtil.equalEncodings(csName, "UTF-8")) {
                                return "UTF-8";
                            }
                            if (StringUtil.equalEncodings(csName, "UTF-16BE")) {
                                return "UTF-16BE";
                            }
                            if (StringUtil.equalEncodings(csName, "UTF-16LE")) {
                                return "UTF-16LE";
                            }
                            if (StringUtil.equalEncodings(csName, "UTF-16")) {
                                return "UTF-16";
                            }
                            if (StringUtil.equalEncodings(csName, "UTF-32BE")) {
                                return "UTF-32BE";
                            }
                            if (StringUtil.equalEncodings(csName, "UTF-32LE")) {
                                return "UTF-32LE";
                            }
                            if (StringUtil.equalEncodings(csName, "UTF-32")) {
                                return "UTF-32";
                            }
                            if (StringUtil.equalEncodings(csName, "UTF")) {
                                return "UTF-16";
                            }
                            break Label_1048;
                        }
                    }
                    break;
                }
            }
        }
        return csName;
    }
    
    public static String findEncodingFor(final Writer w) {
        if (w instanceof OutputStreamWriter) {
            final String enc = ((OutputStreamWriter)w).getEncoding();
            return normalize(enc);
        }
        return null;
    }
}
