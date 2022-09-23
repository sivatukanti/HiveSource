// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.ri.Stax2Util;
import org.codehaus.stax2.typed.Base64Variant;

abstract class Base64DecoderBase
{
    static final int STATE_INITIAL = 0;
    static final int STATE_VALID_1 = 1;
    static final int STATE_VALID_2 = 2;
    static final int STATE_VALID_3 = 3;
    static final int STATE_OUTPUT_3 = 4;
    static final int STATE_OUTPUT_2 = 5;
    static final int STATE_OUTPUT_1 = 6;
    static final int STATE_VALID_2_AND_PADDING = 7;
    static final int INT_SPACE = 32;
    Base64Variant _variant;
    int _state;
    int _decodedData;
    Stax2Util.ByteAggregator _byteAggr;
    
    protected Base64DecoderBase() {
        this._state = 0;
        this._byteAggr = null;
    }
    
    public abstract int decode(final byte[] p0, final int p1, final int p2) throws IllegalArgumentException;
    
    public final boolean hasData() {
        return this._state >= 4 && this._state <= 6;
    }
    
    public final int endOfContent() {
        if (this._state == 0 || this._state == 4 || this._state == 5 || this._state == 6) {
            return 0;
        }
        if (this._variant.usesPadding()) {
            return -1;
        }
        if (this._state == 2) {
            this._state = 6;
            this._decodedData >>= 4;
            return 1;
        }
        if (this._state == 3) {
            this._decodedData >>= 2;
            this._state = 5;
            return 2;
        }
        return -1;
    }
    
    public byte[] decodeCompletely() {
        final Stax2Util.ByteAggregator byteAggregator = this.getByteAggregator();
        byte[] array = byteAggregator.startAggregation();
        while (true) {
            int n = 0;
            int i = array.length;
            do {
                final int decode = this.decode(array, n, i);
                if (decode < 1) {
                    final int endOfContent = this.endOfContent();
                    if (endOfContent < 0) {
                        throw new IllegalArgumentException("Incomplete base64 triplet at the end of decoded content");
                    }
                    if (endOfContent > 0) {
                        continue;
                    }
                    return byteAggregator.aggregateAll(array, n);
                }
                else {
                    n += decode;
                    i -= decode;
                }
            } while (i > 0);
            array = byteAggregator.addFullBlock(array);
        }
    }
    
    public Stax2Util.ByteAggregator getByteAggregator() {
        if (this._byteAggr == null) {
            this._byteAggr = new Stax2Util.ByteAggregator();
        }
        return this._byteAggr;
    }
    
    protected IllegalArgumentException reportInvalidChar(final char c, final int n) throws IllegalArgumentException {
        return this.reportInvalidChar(c, n, null);
    }
    
    protected IllegalArgumentException reportInvalidChar(final char c, final int n, final String str) throws IllegalArgumentException {
        String s;
        if (c <= ' ') {
            s = "Illegal white space character (code 0x" + Integer.toHexString(c) + ") as character #" + (n + 1) + " of 4-char base64 unit: can only used between units";
        }
        else if (this._variant.usesPaddingChar(c)) {
            s = "Unexpected padding character ('" + this._variant.getPaddingChar() + "') as character #" + (n + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
        }
        else if (!Character.isDefined(c) || Character.isISOControl(c)) {
            s = "Illegal character (code 0x" + Integer.toHexString(c) + ") in base64 content";
        }
        else {
            s = "Illegal character '" + c + "' (code 0x" + Integer.toHexString(c) + ") in base64 content";
        }
        if (str != null) {
            s = s + ": " + str;
        }
        return new IllegalArgumentException(s);
    }
}
