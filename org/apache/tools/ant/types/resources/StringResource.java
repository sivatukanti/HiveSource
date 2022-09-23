// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import org.apache.tools.ant.types.Reference;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;

public class StringResource extends Resource
{
    private static final int STRING_MAGIC;
    private static final String DEFAULT_ENCODING = "UTF-8";
    private String encoding;
    
    public StringResource() {
        this.encoding = "UTF-8";
    }
    
    public StringResource(final String value) {
        this(null, value);
    }
    
    public StringResource(final Project project, final String value) {
        this.encoding = "UTF-8";
        this.setProject(project);
        this.setValue((project == null) ? value : project.replaceProperties(value));
    }
    
    @Override
    public synchronized void setName(final String s) {
        if (this.getName() != null) {
            throw new BuildException(new ImmutableResourceException());
        }
        super.setName(s);
    }
    
    public synchronized void setValue(final String s) {
        this.setName(s);
    }
    
    @Override
    public synchronized String getName() {
        return super.getName();
    }
    
    public synchronized String getValue() {
        return this.getName();
    }
    
    @Override
    public boolean isExists() {
        return this.getValue() != null;
    }
    
    public void addText(final String text) {
        this.checkChildrenAllowed();
        this.setValue(this.getProject().replaceProperties(text));
    }
    
    public synchronized void setEncoding(final String s) {
        this.checkAttributesAllowed();
        this.encoding = s;
    }
    
    public synchronized String getEncoding() {
        return this.encoding;
    }
    
    @Override
    public synchronized long getSize() {
        return this.isReference() ? ((Resource)this.getCheckedRef()).getSize() : this.getContent().length();
    }
    
    @Override
    public synchronized int hashCode() {
        if (this.isReference()) {
            return this.getCheckedRef().hashCode();
        }
        return super.hashCode() * StringResource.STRING_MAGIC;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.getContent());
    }
    
    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).getInputStream();
        }
        final String content = this.getContent();
        if (content == null) {
            throw new IllegalStateException("unset string value");
        }
        return new ByteArrayInputStream((this.encoding == null) ? content.getBytes() : content.getBytes(this.encoding));
    }
    
    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).getOutputStream();
        }
        if (this.getValue() != null) {
            throw new ImmutableResourceException();
        }
        return new StringResourceFilterOutputStream();
    }
    
    @Override
    public void setRefid(final Reference r) {
        if (this.encoding != "UTF-8") {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }
    
    protected synchronized String getContent() {
        return this.getValue();
    }
    
    private void setValueFromOutputStream(final String output) {
        String value;
        if (this.getProject() != null) {
            value = this.getProject().replaceProperties(output);
        }
        else {
            value = output;
        }
        this.setValue(value);
    }
    
    static {
        STRING_MAGIC = Resource.getMagicNumber("StringResource".getBytes());
    }
    
    private class StringResourceFilterOutputStream extends FilterOutputStream
    {
        private final ByteArrayOutputStream baos;
        
        public StringResourceFilterOutputStream() {
            super(new ByteArrayOutputStream());
            this.baos = (ByteArrayOutputStream)this.out;
        }
        
        @Override
        public void close() throws IOException {
            super.close();
            final String result = (StringResource.this.encoding == null) ? this.baos.toString() : this.baos.toString(StringResource.this.encoding);
            StringResource.this.setValueFromOutputStream(result);
        }
    }
}
