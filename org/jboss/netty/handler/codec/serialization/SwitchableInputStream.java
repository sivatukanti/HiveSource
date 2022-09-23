// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.serialization;

import java.io.InputStream;
import java.io.FilterInputStream;

final class SwitchableInputStream extends FilterInputStream
{
    SwitchableInputStream() {
        super(null);
    }
    
    void switchStream(final InputStream in) {
        this.in = in;
    }
}
