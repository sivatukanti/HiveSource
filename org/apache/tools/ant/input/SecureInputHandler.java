// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.input;

import org.apache.tools.ant.BuildException;
import java.util.Arrays;
import org.apache.tools.ant.util.ReflectUtil;

public class SecureInputHandler extends DefaultInputHandler
{
    @Override
    public void handleInput(final InputRequest request) throws BuildException {
        final String prompt = this.getPrompt(request);
        try {
            final Object console = ReflectUtil.invokeStatic(System.class, "console");
            do {
                final char[] input = (char[])ReflectUtil.invoke(console, "readPassword", String.class, prompt, Object[].class, null);
                request.setInput(new String(input));
                Arrays.fill(input, ' ');
            } while (!request.isInputValid());
        }
        catch (Exception e) {
            super.handleInput(request);
        }
    }
}
