// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;
import org.w3c.dom.Node;
import java.io.OutputStream;
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Element;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import java.io.FileOutputStream;
import org.apache.tools.ant.util.DOMElementWriter;
import java.io.File;
import org.apache.tools.ant.util.XMLFragment;

public class EchoXML extends XMLFragment
{
    private File file;
    private boolean append;
    private NamespacePolicy namespacePolicy;
    private static final String ERROR_NO_XML = "No nested XML specified";
    
    public EchoXML() {
        this.namespacePolicy = NamespacePolicy.DEFAULT;
    }
    
    public void setFile(final File f) {
        this.file = f;
    }
    
    public void setNamespacePolicy(final NamespacePolicy n) {
        this.namespacePolicy = n;
    }
    
    public void setAppend(final boolean b) {
        this.append = b;
    }
    
    public void execute() {
        final DOMElementWriter writer = new DOMElementWriter(!this.append, this.namespacePolicy.getPolicy());
        OutputStream os = null;
        try {
            if (this.file != null) {
                os = new FileOutputStream(this.file.getAbsolutePath(), this.append);
            }
            else {
                os = new LogOutputStream(this, 2);
            }
            final Node n = this.getFragment().getFirstChild();
            if (n == null) {
                throw new BuildException("No nested XML specified");
            }
            writer.write((Element)n, os);
        }
        catch (BuildException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new BuildException(e2);
        }
        finally {
            FileUtils.close(os);
        }
    }
    
    public static class NamespacePolicy extends EnumeratedAttribute
    {
        private static final String IGNORE = "ignore";
        private static final String ELEMENTS = "elementsOnly";
        private static final String ALL = "all";
        public static final NamespacePolicy DEFAULT;
        
        public NamespacePolicy() {
        }
        
        public NamespacePolicy(final String s) {
            this.setValue(s);
        }
        
        @Override
        public String[] getValues() {
            return new String[] { "ignore", "elementsOnly", "all" };
        }
        
        public DOMElementWriter.XmlNamespacePolicy getPolicy() {
            final String s = this.getValue();
            if ("ignore".equalsIgnoreCase(s)) {
                return DOMElementWriter.XmlNamespacePolicy.IGNORE;
            }
            if ("elementsOnly".equalsIgnoreCase(s)) {
                return DOMElementWriter.XmlNamespacePolicy.ONLY_QUALIFY_ELEMENTS;
            }
            if ("all".equalsIgnoreCase(s)) {
                return DOMElementWriter.XmlNamespacePolicy.QUALIFY_ALL;
            }
            throw new BuildException("Invalid namespace policy: " + s);
        }
        
        static {
            DEFAULT = new NamespacePolicy("ignore");
        }
    }
}
