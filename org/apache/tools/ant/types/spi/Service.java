// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.spi;

import org.apache.tools.ant.BuildException;
import java.io.IOException;
import java.util.Iterator;
import java.io.Writer;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.ProjectComponent;

public class Service extends ProjectComponent
{
    private List<Provider> providerList;
    private String type;
    
    public Service() {
        this.providerList = new ArrayList<Provider>();
    }
    
    public void setProvider(final String className) {
        final Provider provider = new Provider();
        provider.setClassName(className);
        this.providerList.add(provider);
    }
    
    public void addConfiguredProvider(final Provider provider) {
        provider.check();
        this.providerList.add(provider);
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public InputStream getAsStream() throws IOException {
        final ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
        final Writer writer = new OutputStreamWriter(arrayOut, "UTF-8");
        for (final Provider provider : this.providerList) {
            writer.write(provider.getClassName());
            writer.write("\n");
        }
        writer.close();
        return new ByteArrayInputStream(arrayOut.toByteArray());
    }
    
    public void check() {
        if (this.type == null) {
            throw new BuildException("type attribute must be set for service element", this.getLocation());
        }
        if (this.type.length() == 0) {
            throw new BuildException("Invalid empty type classname", this.getLocation());
        }
        if (this.providerList.size() == 0) {
            throw new BuildException("provider attribute or nested provider element must be set!", this.getLocation());
        }
    }
}
