// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.Reader;
import org.apache.tools.ant.types.ResourceCollection;
import java.io.InputStream;
import java.io.IOException;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.DirectoryScanner;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.types.resources.Union;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.tools.ant.types.Resource;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;

public class Replace extends MatchingTask
{
    private static final FileUtils FILE_UTILS;
    private File sourceFile;
    private NestedString token;
    private NestedString value;
    private Resource propertyResource;
    private Resource replaceFilterResource;
    private Properties properties;
    private ArrayList replacefilters;
    private File dir;
    private int fileCount;
    private int replaceCount;
    private boolean summary;
    private String encoding;
    private Union resources;
    private boolean preserveLastModified;
    private boolean failOnNoReplacements;
    
    public Replace() {
        this.sourceFile = null;
        this.token = null;
        this.value = new NestedString();
        this.propertyResource = null;
        this.replaceFilterResource = null;
        this.properties = null;
        this.replacefilters = new ArrayList();
        this.dir = null;
        this.summary = false;
        this.encoding = null;
        this.preserveLastModified = false;
        this.failOnNoReplacements = false;
    }
    
    @Override
    public void execute() throws BuildException {
        final ArrayList savedFilters = (ArrayList)this.replacefilters.clone();
        final Properties savedProperties = (this.properties == null) ? null : ((Properties)this.properties.clone());
        if (this.token != null) {
            final StringBuffer val = new StringBuffer(this.value.getText());
            this.stringReplace(val, "\r\n", "\n");
            this.stringReplace(val, "\n", StringUtils.LINE_SEP);
            final StringBuffer tok = new StringBuffer(this.token.getText());
            this.stringReplace(tok, "\r\n", "\n");
            this.stringReplace(tok, "\n", StringUtils.LINE_SEP);
            final Replacefilter firstFilter = this.createPrimaryfilter();
            firstFilter.setToken(tok.toString());
            firstFilter.setValue(val.toString());
        }
        try {
            if (this.replaceFilterResource != null) {
                final Properties props = this.getProperties(this.replaceFilterResource);
                final Iterator e = props.keySet().iterator();
                while (e.hasNext()) {
                    final String tok2 = e.next().toString();
                    final Replacefilter replaceFilter = this.createReplacefilter();
                    replaceFilter.setToken(tok2);
                    replaceFilter.setValue(props.getProperty(tok2));
                }
            }
            this.validateAttributes();
            if (this.propertyResource != null) {
                this.properties = this.getProperties(this.propertyResource);
            }
            this.validateReplacefilters();
            this.fileCount = 0;
            this.replaceCount = 0;
            if (this.sourceFile != null) {
                this.processFile(this.sourceFile);
            }
            if (this.dir != null) {
                final DirectoryScanner ds = super.getDirectoryScanner(this.dir);
                final String[] srcs = ds.getIncludedFiles();
                for (int i = 0; i < srcs.length; ++i) {
                    final File file = new File(this.dir, srcs[i]);
                    this.processFile(file);
                }
            }
            if (this.resources != null) {
                for (final Resource r : this.resources) {
                    final FileProvider fp = r.as(FileProvider.class);
                    this.processFile(fp.getFile());
                }
            }
            if (this.summary) {
                this.log("Replaced " + this.replaceCount + " occurrences in " + this.fileCount + " files.", 2);
            }
            if (this.failOnNoReplacements && this.replaceCount == 0) {
                throw new BuildException("didn't replace anything");
            }
        }
        finally {
            this.replacefilters = savedFilters;
            this.properties = savedProperties;
        }
    }
    
    public void validateAttributes() throws BuildException {
        if (this.sourceFile == null && this.dir == null && this.resources == null) {
            final String message = "Either the file or the dir attribute or nested resources must be specified";
            throw new BuildException(message, this.getLocation());
        }
        if (this.propertyResource != null && !this.propertyResource.isExists()) {
            final String message = "Property file " + this.propertyResource.getName() + " does not exist.";
            throw new BuildException(message, this.getLocation());
        }
        if (this.token == null && this.replacefilters.size() == 0) {
            final String message = "Either token or a nested replacefilter must be specified";
            throw new BuildException(message, this.getLocation());
        }
        if (this.token != null && "".equals(this.token.getText())) {
            final String message = "The token attribute must not be an empty string.";
            throw new BuildException(message, this.getLocation());
        }
    }
    
    public void validateReplacefilters() throws BuildException {
        for (int size = this.replacefilters.size(), i = 0; i < size; ++i) {
            final Replacefilter element = this.replacefilters.get(i);
            element.validate();
        }
    }
    
    public Properties getProperties(final File propertyFile) throws BuildException {
        return this.getProperties(new FileResource(this.getProject(), propertyFile));
    }
    
    public Properties getProperties(final Resource propertyResource) throws BuildException {
        final Properties props = new Properties();
        InputStream in = null;
        try {
            in = propertyResource.getInputStream();
            props.load(in);
        }
        catch (IOException e) {
            final String message = "Property resource (" + propertyResource.getName() + ") cannot be loaded.";
            throw new BuildException(message);
        }
        finally {
            FileUtils.close(in);
        }
        return props;
    }
    
    private void processFile(final File src) throws BuildException {
        if (!src.exists()) {
            throw new BuildException("Replace: source file " + src.getPath() + " doesn't exist", this.getLocation());
        }
        final int repCountStart = this.replaceCount;
        this.logFilterChain(src.getPath());
        try {
            final File temp = Replace.FILE_UTILS.createTempFile("rep", ".tmp", src.getParentFile(), false, true);
            try {
                final FileInput in = new FileInput(src);
                try {
                    final FileOutput out = new FileOutput(temp);
                    try {
                        out.setInputBuffer(this.buildFilterChain(in.getOutputBuffer()));
                        while (in.readChunk()) {
                            if (this.processFilterChain()) {
                                out.process();
                            }
                        }
                        this.flushFilterChain();
                        out.flush();
                    }
                    finally {
                        out.close();
                    }
                }
                finally {
                    in.close();
                }
                final boolean changes = this.replaceCount != repCountStart;
                if (changes) {
                    ++this.fileCount;
                    final long origLastModified = src.lastModified();
                    Replace.FILE_UTILS.rename(temp, src);
                    if (this.preserveLastModified) {
                        Replace.FILE_UTILS.setFileLastModified(src, origLastModified);
                    }
                }
            }
            finally {
                if (temp.isFile() && !temp.delete()) {
                    temp.deleteOnExit();
                }
            }
        }
        catch (IOException ioe) {
            throw new BuildException("IOException in " + src + " - " + ioe.getClass().getName() + ":" + ioe.getMessage(), ioe, this.getLocation());
        }
    }
    
    private void flushFilterChain() {
        for (int size = this.replacefilters.size(), i = 0; i < size; ++i) {
            final Replacefilter filter = this.replacefilters.get(i);
            filter.flush();
        }
    }
    
    private boolean processFilterChain() {
        for (int size = this.replacefilters.size(), i = 0; i < size; ++i) {
            final Replacefilter filter = this.replacefilters.get(i);
            if (!filter.process()) {
                return false;
            }
        }
        return true;
    }
    
    private StringBuffer buildFilterChain(final StringBuffer inputBuffer) {
        StringBuffer buf = inputBuffer;
        for (int size = this.replacefilters.size(), i = 0; i < size; ++i) {
            final Replacefilter filter = this.replacefilters.get(i);
            filter.setInputBuffer(buf);
            buf = filter.getOutputBuffer();
        }
        return buf;
    }
    
    private void logFilterChain(final String filename) {
        for (int size = this.replacefilters.size(), i = 0; i < size; ++i) {
            final Replacefilter filter = this.replacefilters.get(i);
            this.log("Replacing in " + filename + ": " + filter.getToken() + " --> " + filter.getReplaceValue(), 3);
        }
    }
    
    public void setFile(final File file) {
        this.sourceFile = file;
    }
    
    public void setSummary(final boolean summary) {
        this.summary = summary;
    }
    
    public void setReplaceFilterFile(final File replaceFilterFile) {
        this.setReplaceFilterResource(new FileResource(this.getProject(), replaceFilterFile));
    }
    
    public void setReplaceFilterResource(final Resource replaceFilter) {
        this.replaceFilterResource = replaceFilter;
    }
    
    public void setDir(final File dir) {
        this.dir = dir;
    }
    
    public void setToken(final String token) {
        this.createReplaceToken().addText(token);
    }
    
    public void setValue(final String value) {
        this.createReplaceValue().addText(value);
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public NestedString createReplaceToken() {
        if (this.token == null) {
            this.token = new NestedString();
        }
        return this.token;
    }
    
    public NestedString createReplaceValue() {
        return this.value;
    }
    
    public void setPropertyFile(final File propertyFile) {
        this.setPropertyResource(new FileResource(propertyFile));
    }
    
    public void setPropertyResource(final Resource propertyResource) {
        this.propertyResource = propertyResource;
    }
    
    public Replacefilter createReplacefilter() {
        final Replacefilter filter = new Replacefilter();
        this.replacefilters.add(filter);
        return filter;
    }
    
    public void addConfigured(final ResourceCollection rc) {
        if (!rc.isFilesystemOnly()) {
            throw new BuildException("only filesystem resources are supported");
        }
        if (this.resources == null) {
            this.resources = new Union();
        }
        this.resources.add(rc);
    }
    
    public void setPreserveLastModified(final boolean b) {
        this.preserveLastModified = b;
    }
    
    public void setFailOnNoReplacements(final boolean b) {
        this.failOnNoReplacements = b;
    }
    
    private Replacefilter createPrimaryfilter() {
        final Replacefilter filter = new Replacefilter();
        this.replacefilters.add(0, filter);
        return filter;
    }
    
    private void stringReplace(final StringBuffer str, final String str1, final String str2) {
        int found = str.indexOf(str1);
        final int str1Length = str1.length();
        for (int str2Length = str2.length(); found >= 0; found = str.indexOf(str1, found + str2Length)) {
            str.replace(found, found + str1Length, str2);
        }
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
    
    public class NestedString
    {
        private boolean expandProperties;
        private StringBuffer buf;
        
        public NestedString() {
            this.expandProperties = false;
            this.buf = new StringBuffer();
        }
        
        public void setExpandProperties(final boolean b) {
            this.expandProperties = b;
        }
        
        public void addText(final String val) {
            this.buf.append(val);
        }
        
        public String getText() {
            final String s = this.buf.toString();
            return this.expandProperties ? Replace.this.getProject().replaceProperties(s) : s;
        }
    }
    
    public class Replacefilter
    {
        private NestedString token;
        private NestedString value;
        private String replaceValue;
        private String property;
        private StringBuffer inputBuffer;
        private StringBuffer outputBuffer;
        
        public Replacefilter() {
            this.outputBuffer = new StringBuffer();
        }
        
        public void validate() throws BuildException {
            if (this.token == null) {
                final String message = "token is a mandatory for replacefilter.";
                throw new BuildException(message);
            }
            if ("".equals(this.token.getText())) {
                final String message = "The token must not be an empty string.";
                throw new BuildException(message);
            }
            if (this.value != null && this.property != null) {
                final String message = "Either value or property can be specified, but a replacefilter element cannot have both.";
                throw new BuildException(message);
            }
            if (this.property != null) {
                if (Replace.this.propertyResource == null) {
                    final String message = "The replacefilter's property attribute can only be used with the replacetask's propertyFile/Resource attribute.";
                    throw new BuildException(message);
                }
                if (Replace.this.properties == null || Replace.this.properties.getProperty(this.property) == null) {
                    final String message = "property \"" + this.property + "\" was not found in " + Replace.this.propertyResource.getName();
                    throw new BuildException(message);
                }
            }
            this.replaceValue = this.getReplaceValue();
        }
        
        public String getReplaceValue() {
            if (this.property != null) {
                return Replace.this.properties.getProperty(this.property);
            }
            if (this.value != null) {
                return this.value.getText();
            }
            if (Replace.this.value != null) {
                return Replace.this.value.getText();
            }
            return "";
        }
        
        public void setToken(final String t) {
            this.createReplaceToken().addText(t);
        }
        
        public String getToken() {
            return this.token.getText();
        }
        
        public void setValue(final String value) {
            this.createReplaceValue().addText(value);
        }
        
        public String getValue() {
            return this.value.getText();
        }
        
        public void setProperty(final String property) {
            this.property = property;
        }
        
        public String getProperty() {
            return this.property;
        }
        
        public NestedString createReplaceToken() {
            if (this.token == null) {
                this.token = new NestedString();
            }
            return this.token;
        }
        
        public NestedString createReplaceValue() {
            if (this.value == null) {
                this.value = new NestedString();
            }
            return this.value;
        }
        
        StringBuffer getOutputBuffer() {
            return this.outputBuffer;
        }
        
        void setInputBuffer(final StringBuffer input) {
            this.inputBuffer = input;
        }
        
        boolean process() {
            final String t = this.getToken();
            if (this.inputBuffer.length() > t.length()) {
                int pos = this.replace();
                pos = Math.max(this.inputBuffer.length() - t.length(), pos);
                this.outputBuffer.append(this.inputBuffer.substring(0, pos));
                this.inputBuffer.delete(0, pos);
                return true;
            }
            return false;
        }
        
        void flush() {
            this.replace();
            this.outputBuffer.append(this.inputBuffer);
            this.inputBuffer.delete(0, this.inputBuffer.length());
        }
        
        private int replace() {
            final String t = this.getToken();
            int found = this.inputBuffer.indexOf(t);
            int pos = -1;
            final int tokenLength = t.length();
            final int replaceValueLength = this.replaceValue.length();
            while (found >= 0) {
                this.inputBuffer.replace(found, found + tokenLength, this.replaceValue);
                pos = found + replaceValueLength;
                found = this.inputBuffer.indexOf(t, pos);
                ++Replace.this.replaceCount;
            }
            return pos;
        }
    }
    
    private class FileInput
    {
        private StringBuffer outputBuffer;
        private final InputStream is;
        private Reader reader;
        private char[] buffer;
        private static final int BUFF_SIZE = 4096;
        
        FileInput(final File source) throws IOException {
            this.outputBuffer = new StringBuffer();
            this.buffer = new char[4096];
            this.is = new FileInputStream(source);
            try {
                this.reader = new BufferedReader((Replace.this.encoding != null) ? new InputStreamReader(this.is, Replace.this.encoding) : new InputStreamReader(this.is));
            }
            finally {
                if (this.reader == null) {
                    this.is.close();
                }
            }
        }
        
        StringBuffer getOutputBuffer() {
            return this.outputBuffer;
        }
        
        boolean readChunk() throws IOException {
            int bufferLength = 0;
            bufferLength = this.reader.read(this.buffer);
            if (bufferLength < 0) {
                return false;
            }
            this.outputBuffer.append(new String(this.buffer, 0, bufferLength));
            return true;
        }
        
        public void close() throws IOException {
            this.is.close();
        }
    }
    
    private class FileOutput
    {
        private StringBuffer inputBuffer;
        private final OutputStream os;
        private Writer writer;
        
        FileOutput(final File out) throws IOException {
            this.os = new FileOutputStream(out);
            try {
                this.writer = new BufferedWriter((Replace.this.encoding != null) ? new OutputStreamWriter(this.os, Replace.this.encoding) : new OutputStreamWriter(this.os));
            }
            finally {
                if (this.writer == null) {
                    this.os.close();
                }
            }
        }
        
        void setInputBuffer(final StringBuffer input) {
            this.inputBuffer = input;
        }
        
        boolean process() throws IOException {
            this.writer.write(this.inputBuffer.toString());
            this.inputBuffer.delete(0, this.inputBuffer.length());
            return false;
        }
        
        void flush() throws IOException {
            this.process();
            this.writer.flush();
        }
        
        public void close() throws IOException {
            this.os.close();
        }
    }
}
