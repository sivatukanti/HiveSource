// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.email;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.tools.ant.util.UUEncoder;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.apache.tools.ant.BuildException;
import java.io.PrintStream;
import java.io.File;

class UUMailer extends PlainMailer
{
    @Override
    protected void attach(final File file, final PrintStream out) throws IOException {
        if (!file.exists() || !file.canRead()) {
            throw new BuildException("File \"" + file.getName() + "\" does not exist or is not " + "readable.");
        }
        final FileInputStream finstr = new FileInputStream(file);
        try {
            final BufferedInputStream in = new BufferedInputStream(finstr);
            final UUEncoder encoder = new UUEncoder(file.getName());
            encoder.encode(in, out);
        }
        finally {
            finstr.close();
        }
    }
}
