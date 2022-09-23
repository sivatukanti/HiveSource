// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import com.ctc.wstx.util.XmlChars;

public class WstxInputData
{
    public static final char CHAR_NULL = '\0';
    public static final char INT_NULL = '\0';
    public static final char CHAR_SPACE = ' ';
    public static final char INT_SPACE = ' ';
    public static final int MAX_UNICODE_CHAR = 1114111;
    private static final int VALID_CHAR_COUNT = 256;
    private static final byte NAME_CHAR_INVALID_B = 0;
    private static final byte NAME_CHAR_ALL_VALID_B = 1;
    private static final byte NAME_CHAR_VALID_NONFIRST_B = -1;
    private static final byte[] sCharValidity;
    private static final int VALID_PUBID_CHAR_COUNT = 128;
    private static final byte[] sPubidValidity;
    private static final byte PUBID_CHAR_VALID_B = 1;
    protected boolean mXml11;
    protected char[] mInputBuffer;
    protected int mInputPtr;
    protected int mInputEnd;
    protected long mCurrInputProcessed;
    protected int mCurrInputRow;
    protected int mCurrInputRowStart;
    
    protected WstxInputData() {
        this.mXml11 = false;
        this.mInputPtr = 0;
        this.mInputEnd = 0;
        this.mCurrInputProcessed = 0L;
        this.mCurrInputRow = 1;
        this.mCurrInputRowStart = 0;
    }
    
    public void copyBufferStateFrom(final WstxInputData src) {
        this.mInputBuffer = src.mInputBuffer;
        this.mInputPtr = src.mInputPtr;
        this.mInputEnd = src.mInputEnd;
        this.mCurrInputProcessed = src.mCurrInputProcessed;
        this.mCurrInputRow = src.mCurrInputRow;
        this.mCurrInputRowStart = src.mCurrInputRowStart;
    }
    
    protected final boolean isNameStartChar(final char c) {
        if (c <= 'z') {
            return c >= 'a' || (c >= 'A' && (c <= 'Z' || c == '_'));
        }
        return this.mXml11 ? XmlChars.is11NameStartChar(c) : XmlChars.is10NameStartChar(c);
    }
    
    protected final boolean isNameChar(final char c) {
        if (c > 'z') {
            return this.mXml11 ? XmlChars.is11NameChar(c) : XmlChars.is10NameChar(c);
        }
        if (c >= 'a') {
            return true;
        }
        if (c <= 'Z') {
            return c >= 'A' || (c >= '0' && c <= '9') || c == '.' || c == '-';
        }
        return c == '_';
    }
    
    public static final boolean isNameStartChar(final char c, final boolean nsAware, final boolean xml11) {
        if (c > 'z') {
            return xml11 ? XmlChars.is11NameStartChar(c) : XmlChars.is10NameStartChar(c);
        }
        if (c >= 'a') {
            return true;
        }
        if (c < 'A') {
            return c == ':' && !nsAware;
        }
        return c <= 'Z' || c == '_';
    }
    
    public static final boolean isNameChar(final char c, final boolean nsAware, final boolean xml11) {
        if (c > 'z') {
            return xml11 ? XmlChars.is11NameChar(c) : XmlChars.is10NameChar(c);
        }
        if (c >= 'a') {
            return true;
        }
        if (c <= 'Z') {
            return c >= 'A' || (c >= '0' && c <= '9') || c == '.' || c == '-' || (c == ':' && !nsAware);
        }
        return c == '_';
    }
    
    public static final int findIllegalNameChar(final String name, final boolean nsAware, final boolean xml11) {
        final int len = name.length();
        if (len < 1) {
            return -1;
        }
        char c = name.charAt(0);
        if (c <= 'z') {
            if (c < 'a') {
                if (c < 'A') {
                    if (c != ':' || nsAware) {
                        return 0;
                    }
                }
                else if (c > 'Z' && c != '_') {
                    return 0;
                }
            }
        }
        else if (xml11) {
            if (!XmlChars.is11NameStartChar(c)) {
                return 0;
            }
        }
        else if (!XmlChars.is10NameStartChar(c)) {
            return 0;
        }
        for (int i = 1; i < len; ++i) {
            c = name.charAt(i);
            if (c <= 'z') {
                if (c < 'a') {
                    if (c <= 'Z') {
                        if (c < 'A') {
                            if ((c < '0' || c > '9') && c != '.') {
                                if (c != '-') {
                                    if (c != ':' || nsAware) {
                                        return i;
                                    }
                                }
                            }
                        }
                    }
                    else if (c != '_') {
                        return i;
                    }
                }
            }
            else if (xml11) {
                if (!XmlChars.is11NameChar(c)) {
                    return i;
                }
            }
            else if (!XmlChars.is10NameChar(c)) {
                return i;
            }
        }
        return -1;
    }
    
    public static final int findIllegalNmtokenChar(final String nmtoken, final boolean nsAware, final boolean xml11) {
        for (int len = nmtoken.length(), i = 1; i < len; ++i) {
            final char c = nmtoken.charAt(i);
            if (c <= 'z') {
                if (c < 'a') {
                    if (c <= 'Z') {
                        if (c < 'A') {
                            if ((c < '0' || c > '9') && c != '.') {
                                if (c != '-') {
                                    if (c != ':' || nsAware) {
                                        return i;
                                    }
                                }
                            }
                        }
                    }
                    else if (c != '_') {
                        return i;
                    }
                }
            }
            else if (xml11) {
                if (!XmlChars.is11NameChar(c)) {
                    return i;
                }
            }
            else if (!XmlChars.is10NameChar(c)) {
                return i;
            }
        }
        return -1;
    }
    
    public static final boolean isSpaceChar(final char c) {
        return c <= ' ';
    }
    
    public static String getCharDesc(final char c) {
        final int i = c;
        if (Character.isISOControl(c)) {
            return "(CTRL-CHAR, code " + i + ")";
        }
        if (i > 255) {
            return "'" + c + "' (code " + i + " / 0x" + Integer.toHexString(i) + ")";
        }
        return "'" + c + "' (code " + i + ")";
    }
    
    static {
        (sCharValidity = new byte[256])[95] = 1;
        for (int i = 0, last = 25; i <= last; ++i) {
            WstxInputData.sCharValidity[65 + i] = 1;
            WstxInputData.sCharValidity[97 + i] = 1;
        }
        for (int i = 192; i < 256; ++i) {
            WstxInputData.sCharValidity[i] = 1;
        }
        WstxInputData.sCharValidity[215] = 0;
        WstxInputData.sCharValidity[247] = 0;
        WstxInputData.sCharValidity[45] = -1;
        WstxInputData.sCharValidity[46] = -1;
        WstxInputData.sCharValidity[183] = -1;
        for (int i = 48; i <= 57; ++i) {
            WstxInputData.sCharValidity[i] = -1;
        }
        sPubidValidity = new byte[128];
        for (int i = 0, last = 25; i <= last; ++i) {
            WstxInputData.sPubidValidity[65 + i] = 1;
            WstxInputData.sPubidValidity[97 + i] = 1;
        }
        for (int i = 48; i <= 57; ++i) {
            WstxInputData.sPubidValidity[i] = 1;
        }
        WstxInputData.sPubidValidity[10] = 1;
        WstxInputData.sPubidValidity[13] = 1;
        WstxInputData.sPubidValidity[32] = 1;
        WstxInputData.sPubidValidity[45] = 1;
        WstxInputData.sPubidValidity[39] = 1;
        WstxInputData.sPubidValidity[40] = 1;
        WstxInputData.sPubidValidity[41] = 1;
        WstxInputData.sPubidValidity[43] = 1;
        WstxInputData.sPubidValidity[44] = 1;
        WstxInputData.sPubidValidity[46] = 1;
        WstxInputData.sPubidValidity[47] = 1;
        WstxInputData.sPubidValidity[58] = 1;
        WstxInputData.sPubidValidity[61] = 1;
        WstxInputData.sPubidValidity[63] = 1;
        WstxInputData.sPubidValidity[59] = 1;
        WstxInputData.sPubidValidity[33] = 1;
        WstxInputData.sPubidValidity[42] = 1;
        WstxInputData.sPubidValidity[35] = 1;
        WstxInputData.sPubidValidity[64] = 1;
        WstxInputData.sPubidValidity[36] = 1;
        WstxInputData.sPubidValidity[95] = 1;
        WstxInputData.sPubidValidity[37] = 1;
    }
}
