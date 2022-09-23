// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.input;

import java.io.InputStream;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.BuildException;
import java.io.OutputStream;
import org.apache.tools.ant.taskdefs.StreamPumper;
import java.io.ByteArrayOutputStream;

public class GreedyInputHandler extends DefaultInputHandler
{
    @Override
    public void handleInput(final InputRequest request) throws BuildException {
        final String prompt = this.getPrompt(request);
        InputStream in = null;
        try {
            in = this.getInputStream();
            System.err.println(prompt);
            System.err.flush();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final StreamPumper p = new StreamPumper(in, baos);
            final Thread t = new Thread(p);
            t.start();
            try {
                t.join();
            }
            catch (InterruptedException e) {
                try {
                    t.join();
                }
                catch (InterruptedException ex) {}
            }
            request.setInput(new String(baos.toByteArray()));
            if (!request.isInputValid()) {
                throw new BuildException("Received invalid console input");
            }
            if (p.getException() != null) {
                throw new BuildException("Failed to read input from console", p.getException());
            }
        }
        finally {
            FileUtils.close(in);
        }
    }
}
