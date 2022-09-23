// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.nio.ByteBuffer;

public class Utf8LineParser
{
    private State state;
    private Utf8StringBuilder utf;
    
    public Utf8LineParser() {
        this.state = State.START;
    }
    
    public String parse(final ByteBuffer buf) {
        while (buf.remaining() > 0) {
            final byte b = buf.get();
            if (this.parseByte(b)) {
                this.state = State.START;
                return this.utf.toString();
            }
        }
        return null;
    }
    
    private boolean parseByte(final byte b) {
        switch (this.state) {
            case START: {
                this.utf = new Utf8StringBuilder();
                this.state = State.PARSE;
                return this.parseByte(b);
            }
            case PARSE: {
                if (this.utf.isUtf8SequenceComplete() && (b == 13 || b == 10)) {
                    this.state = State.END;
                    return this.parseByte(b);
                }
                this.utf.append(b);
                break;
            }
            case END: {
                if (b == 10) {
                    this.state = State.START;
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    private enum State
    {
        START, 
        PARSE, 
        END;
    }
}
