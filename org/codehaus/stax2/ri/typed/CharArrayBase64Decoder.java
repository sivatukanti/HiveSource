// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.ri.Stax2Util;
import java.util.Iterator;
import java.util.List;
import org.codehaus.stax2.typed.Base64Variant;
import java.util.ArrayList;

public final class CharArrayBase64Decoder extends Base64DecoderBase
{
    char[] _currSegment;
    int _currSegmentPtr;
    int _currSegmentEnd;
    final ArrayList _nextSegments;
    int _lastSegmentOffset;
    int _lastSegmentEnd;
    int _nextSegmentIndex;
    
    public CharArrayBase64Decoder() {
        this._nextSegments = new ArrayList();
    }
    
    public void init(final Base64Variant variant, final boolean b, final char[] array, final int n, final int n2, final List list) {
        this._variant = variant;
        if (b) {
            this._state = 0;
        }
        this._nextSegments.clear();
        if (list == null || list.isEmpty()) {
            this._currSegment = array;
            this._currSegmentPtr = n;
            this._currSegmentEnd = n + n2;
        }
        else {
            if (array == null) {
                throw new IllegalArgumentException();
            }
            final Iterator<char[]> iterator = list.iterator();
            this._currSegment = iterator.next();
            this._currSegmentPtr = 0;
            this._currSegmentEnd = this._currSegment.length;
            while (iterator.hasNext()) {
                this._nextSegments.add(iterator.next());
            }
            this._nextSegmentIndex = 0;
            this._nextSegments.add(array);
            this._lastSegmentOffset = n;
            this._lastSegmentEnd = n + n2;
        }
    }
    
    @Override
    public int decode(final byte[] array, int n, final int n2) throws IllegalArgumentException {
        final int n3 = n;
        final int n4 = n + n2;
    Label_0651:
        while (true) {
            Label_0423: {
                switch (this._state) {
                    case 0: {
                        while (this._currSegmentPtr < this._currSegmentEnd || this.nextSegment()) {
                            final char c = this._currSegment[this._currSegmentPtr++];
                            if (c > ' ') {
                                final int decodeBase64Char = this._variant.decodeBase64Char(c);
                                if (decodeBase64Char < 0) {
                                    throw this.reportInvalidChar(c, 0);
                                }
                                this._decodedData = decodeBase64Char;
                                break Label_0423;
                            }
                        }
                        break Label_0651;
                    }
                    case 1: {
                        if (this._currSegmentPtr >= this._currSegmentEnd && !this.nextSegment()) {
                            this._state = 1;
                            break Label_0651;
                        }
                        final char c2 = this._currSegment[this._currSegmentPtr++];
                        final int decodeBase64Char2 = this._variant.decodeBase64Char(c2);
                        if (decodeBase64Char2 < 0) {
                            throw this.reportInvalidChar(c2, 1);
                        }
                        this._decodedData = (this._decodedData << 6 | decodeBase64Char2);
                    }
                    case 2: {
                        if (this._currSegmentPtr >= this._currSegmentEnd && !this.nextSegment()) {
                            this._state = 2;
                            break Label_0651;
                        }
                        final char c3 = this._currSegment[this._currSegmentPtr++];
                        final int decodeBase64Char3 = this._variant.decodeBase64Char(c3);
                        if (decodeBase64Char3 >= 0) {
                            this._decodedData = (this._decodedData << 6 | decodeBase64Char3);
                            break Label_0423;
                        }
                        if (decodeBase64Char3 != -2) {
                            throw this.reportInvalidChar(c3, 2);
                        }
                        this._state = 7;
                        continue;
                    }
                    case 3: {
                        if (this._currSegmentPtr >= this._currSegmentEnd && !this.nextSegment()) {
                            this._state = 3;
                            break Label_0651;
                        }
                        final char c4 = this._currSegment[this._currSegmentPtr++];
                        final int decodeBase64Char4 = this._variant.decodeBase64Char(c4);
                        if (decodeBase64Char4 >= 0) {
                            this._decodedData = (this._decodedData << 6 | decodeBase64Char4);
                            break Label_0423;
                        }
                        if (decodeBase64Char4 != -2) {
                            throw this.reportInvalidChar(c4, 3);
                        }
                        this._decodedData >>= 2;
                        this._state = 5;
                        continue;
                    }
                    case 4: {
                        if (n >= n4) {
                            this._state = 4;
                            break Label_0651;
                        }
                        array[n++] = (byte)(this._decodedData >> 16);
                    }
                    case 5: {
                        if (n >= n4) {
                            this._state = 5;
                            break Label_0651;
                        }
                        array[n++] = (byte)(this._decodedData >> 8);
                    }
                    case 6: {
                        if (n >= n4) {
                            this._state = 6;
                            break Label_0651;
                        }
                        array[n++] = (byte)this._decodedData;
                        this._state = 0;
                        continue;
                    }
                    case 7: {
                        if (this._currSegmentPtr >= this._currSegmentEnd && !this.nextSegment()) {
                            break Label_0651;
                        }
                        final char c5 = this._currSegment[this._currSegmentPtr++];
                        if (!this._variant.usesPaddingChar(c5)) {
                            throw this.reportInvalidChar(c5, 3, "expected padding character '" + this._variant.getPaddingChar() + "'");
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
    
    private boolean nextSegment() {
        if (this._nextSegmentIndex < this._nextSegments.size()) {
            this._currSegment = this._nextSegments.get(this._nextSegmentIndex++);
            if (this._nextSegmentIndex == this._nextSegments.size()) {
                this._currSegmentPtr = this._lastSegmentOffset;
                this._currSegmentEnd = this._lastSegmentEnd;
            }
            else {
                this._currSegmentPtr = 0;
                this._currSegmentEnd = this._currSegment.length;
            }
            return true;
        }
        return false;
    }
}
