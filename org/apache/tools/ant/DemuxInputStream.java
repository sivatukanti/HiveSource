// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

import java.io.IOException;
import java.io.InputStream;

public class DemuxInputStream extends InputStream
{
    private static final int MASK_8BIT = 255;
    private Project project;
    
    public DemuxInputStream(final Project project) {
        this.project = project;
    }
    
    @Override
    public int read() throws IOException {
        final byte[] buffer = { 0 };
        if (this.project.demuxInput(buffer, 0, 1) == -1) {
            return -1;
        }
        return buffer[0] & 0xFF;
    }
    
    @Override
    public int read(final byte[] buffer, final int offset, final int length) throws IOException {
        return this.project.demuxInput(buffer, offset, length);
    }
}
