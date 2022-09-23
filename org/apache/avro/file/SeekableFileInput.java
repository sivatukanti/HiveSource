// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.file;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

public class SeekableFileInput extends FileInputStream implements SeekableInput
{
    public SeekableFileInput(final File file) throws IOException {
        super(file);
    }
    
    public SeekableFileInput(final FileDescriptor fd) throws IOException {
        super(fd);
    }
    
    @Override
    public void seek(final long p) throws IOException {
        this.getChannel().position(p);
    }
    
    @Override
    public long tell() throws IOException {
        return this.getChannel().position();
    }
    
    @Override
    public long length() throws IOException {
        return this.getChannel().size();
    }
}
