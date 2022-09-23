// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.ri.Stax2Util;
import org.codehaus.stax2.typed.Base64Variant;

public final class StringBase64Decoder extends Base64DecoderBase
{
    String _currSegment;
    int _currSegmentPtr;
    int _currSegmentEnd;
    
    public void init(final Base64Variant variant, final boolean b, final String currSegment) {
        this._variant = variant;
        if (b) {
            this._state = 0;
        }
        this._currSegment = currSegment;
        this._currSegmentPtr = 0;
        this._currSegmentEnd = currSegment.length();
    }
    
    @Override
    public int decode(final byte[] array, int n, final int n2) throws IllegalArgumentException {
        final int n3 = n;
        final int n4 = n + n2;
    Label_0598:
        while (true) {
            Label_0403: {
                switch (this._state) {
                    case 0: {
                        while (this._currSegmentPtr < this._currSegmentEnd) {
                            final char char1 = this._currSegment.charAt(this._currSegmentPtr++);
                            if (char1 > ' ') {
                                final int decodeBase64Char = this._variant.decodeBase64Char(char1);
                                if (decodeBase64Char < 0) {
                                    throw this.reportInvalidChar(char1, 0);
                                }
                                this._decodedData = decodeBase64Char;
                                break Label_0403;
                            }
                        }
                        break Label_0598;
                    }
                    case 1: {
                        if (this._currSegmentPtr >= this._currSegmentEnd) {
                            this._state = 1;
                            break Label_0598;
                        }
                        final char char2 = this._currSegment.charAt(this._currSegmentPtr++);
                        final int decodeBase64Char2 = this._variant.decodeBase64Char(char2);
                        if (decodeBase64Char2 < 0) {
                            throw this.reportInvalidChar(char2, 1);
                        }
                        this._decodedData = (this._decodedData << 6 | decodeBase64Char2);
                    }
                    case 2: {
                        if (this._currSegmentPtr >= this._currSegmentEnd) {
                            this._state = 2;
                            break Label_0598;
                        }
                        final char char3 = this._currSegment.charAt(this._currSegmentPtr++);
                        final int decodeBase64Char3 = this._variant.decodeBase64Char(char3);
                        if (decodeBase64Char3 >= 0) {
                            this._decodedData = (this._decodedData << 6 | decodeBase64Char3);
                            break Label_0403;
                        }
                        if (decodeBase64Char3 != -2) {
                            throw this.reportInvalidChar(char3, 2);
                        }
                        this._state = 7;
                        continue;
                    }
                    case 3: {
                        if (this._currSegmentPtr >= this._currSegmentEnd) {
                            this._state = 3;
                            break Label_0598;
                        }
                        final char char4 = this._currSegment.charAt(this._currSegmentPtr++);
                        final int decodeBase64Char4 = this._variant.decodeBase64Char(char4);
                        if (decodeBase64Char4 >= 0) {
                            this._decodedData = (this._decodedData << 6 | decodeBase64Char4);
                            break Label_0403;
                        }
                        if (decodeBase64Char4 != -2) {
                            throw this.reportInvalidChar(char4, 3);
                        }
                        this._decodedData >>= 2;
                        this._state = 5;
                        continue;
                    }
                    case 4: {
                        if (n >= n4) {
                            this._state = 4;
                            break Label_0598;
                        }
                        array[n++] = (byte)(this._decodedData >> 16);
                    }
                    case 5: {
                        if (n >= n4) {
                            this._state = 5;
                            break Label_0598;
                        }
                        array[n++] = (byte)(this._decodedData >> 8);
                    }
                    case 6: {
                        if (n >= n4) {
                            this._state = 6;
                            break Label_0598;
                        }
                        array[n++] = (byte)this._decodedData;
                        this._state = 0;
                        continue;
                    }
                    case 7: {
                        if (this._currSegmentPtr >= this._currSegmentEnd) {
                            break Label_0598;
                        }
                        final char char5 = this._currSegment.charAt(this._currSegmentPtr++);
                        if (!this._variant.usesPaddingChar(char5)) {
                            throw this.reportInvalidChar(char5, 3, "expected padding character '='");
                        }
                        this._state = 6;
                        this._decodedData >>= 4;
                        continue;
                    }
                    default: {
                        throw new IllegalStateException("Illegal internal state " + this._state);
                    }
                }
            }
        }
        return n - n3;
    }
}
