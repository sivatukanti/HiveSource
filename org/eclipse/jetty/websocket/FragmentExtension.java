// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.io.IOException;
import java.util.Map;

public class FragmentExtension extends AbstractExtension
{
    private int _maxLength;
    private int _minFragments;
    
    public FragmentExtension() {
        super("fragment");
        this._maxLength = -1;
        this._minFragments = 1;
    }
    
    @Override
    public boolean init(final Map<String, String> parameters) {
        if (super.init(parameters)) {
            this._maxLength = this.getInitParameter("maxLength", this._maxLength);
            this._minFragments = this.getInitParameter("minFragments", this._minFragments);
            return true;
        }
        return false;
    }
    
    @Override
    public void addFrame(final byte flags, byte opcode, final byte[] content, int offset, int length) throws IOException {
        if (this.getConnection().isControl(opcode)) {
            super.addFrame(flags, opcode, content, offset, length);
            return;
        }
        int fragments = 1;
        while (this._maxLength > 0 && length > this._maxLength) {
            ++fragments;
            super.addFrame((byte)(flags & ~this.getConnection().finMask()), opcode, content, offset, this._maxLength);
            length -= this._maxLength;
            offset += this._maxLength;
            opcode = this.getConnection().continuationOpcode();
        }
        while (fragments < this._minFragments) {
            final int frag = length / 2;
            ++fragments;
            super.addFrame((byte)(flags & 0x7), opcode, content, offset, frag);
            length -= frag;
            offset += frag;
            opcode = this.getConnection().continuationOpcode();
        }
        super.addFrame((byte)(flags | this.getConnection().finMask()), opcode, content, offset, length);
    }
}
