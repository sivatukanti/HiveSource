// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.io;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.File;

public class Stax2FileResult extends Stax2ReferentialResult
{
    final File mFile;
    
    public Stax2FileResult(final File mFile) {
        this.mFile = mFile;
    }
    
    @Override
    public Writer constructWriter() throws IOException {
        final String encoding = this.getEncoding();
        if (encoding != null && encoding.length() > 0) {
            return new OutputStreamWriter(this.constructOutputStream(), encoding);
        }
        return new FileWriter(this.mFile);
    }
    
    @Override
    public OutputStream constructOutputStream() throws IOException {
        return new FileOutputStream(this.mFile);
    }
    
    public File getFile() {
        return this.mFile;
    }
}
