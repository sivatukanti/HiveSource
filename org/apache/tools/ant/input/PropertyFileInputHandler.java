// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.tools.ant.BuildException;
import java.util.Properties;

public class PropertyFileInputHandler implements InputHandler
{
    private Properties props;
    public static final String FILE_NAME_KEY = "ant.input.properties";
    
    public PropertyFileInputHandler() {
        this.props = null;
    }
    
    public void handleInput(final InputRequest request) throws BuildException {
        this.readProps();
        final Object o = this.props.get(request.getPrompt());
        if (o == null) {
            throw new BuildException("Unable to find input for '" + request.getPrompt() + "'");
        }
        request.setInput(o.toString());
        if (!request.isInputValid()) {
            throw new BuildException("Found invalid input " + o + " for '" + request.getPrompt() + "'");
        }
    }
    
    private synchronized void readProps() throws BuildException {
        if (this.props == null) {
            final String propsFile = System.getProperty("ant.input.properties");
            if (propsFile == null) {
                throw new BuildException("System property ant.input.properties for PropertyFileInputHandler not set");
            }
            this.props = new Properties();
            try {
                this.props.load(new FileInputStream(propsFile));
            }
            catch (IOException e) {
                throw new BuildException("Couldn't load " + propsFile, e);
            }
        }
    }
}
