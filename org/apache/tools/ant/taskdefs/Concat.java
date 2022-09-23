// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.util.ReaderInputStream;
import java.io.StringReader;
import org.apache.tools.ant.util.ConcatResourceInputStream;
import java.util.Arrays;
import java.io.FileInputStream;
import java.io.FileReader;
import org.apache.tools.ant.types.resources.selectors.Not;
import org.apache.tools.ant.types.resources.selectors.Exists;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.Intersect;
import org.apache.tools.ant.types.resources.StringResource;
import java.util.Collections;
import java.util.Iterator;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.ResourceUtils;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.resources.LogOutputResource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.resources.FileResource;
import java.io.File;
import org.apache.tools.ant.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import org.apache.tools.ant.types.FilterChain;
import java.util.Vector;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.Task;

public class Concat extends Task implements ResourceCollection
{
    private static final int BUFFER_SIZE = 8192;
    private static final FileUtils FILE_UTILS;
    private static final ResourceSelector EXISTS;
    private static final ResourceSelector NOT_EXISTS;
    private Resource dest;
    private boolean append;
    private String encoding;
    private String outputEncoding;
    private boolean binary;
    private StringBuffer textBuffer;
    private Resources rc;
    private Vector<FilterChain> filterChains;
    private boolean forceOverwrite;
    private boolean force;
    private TextElement footer;
    private TextElement header;
    private boolean fixLastLine;
    private String eolString;
    private Writer outputWriter;
    private boolean ignoreEmpty;
    private String resourceName;
    private ReaderFactory<Resource> resourceReaderFactory;
    private ReaderFactory<Reader> identityReaderFactory;
    
    public Concat() {
        this.forceOverwrite = true;
        this.force = false;
        this.fixLastLine = false;
        this.outputWriter = null;
        this.ignoreEmpty = true;
        this.resourceReaderFactory = new ReaderFactory<Resource>() {
            public Reader getReader(final Resource o) throws IOException {
                final InputStream is = o.getInputStream();
                return new BufferedReader((Concat.this.encoding == null) ? new InputStreamReader(is) : new InputStreamReader(is, Concat.this.encoding));
            }
        };
        this.identityReaderFactory = new ReaderFactory<Reader>() {
            public Reader getReader(final Reader o) {
                return o;
            }
        };
        this.reset();
    }
    
    public void reset() {
        this.append = false;
        this.forceOverwrite = true;
        this.dest = null;
        this.encoding = null;
        this.outputEncoding = null;
        this.fixLastLine = false;
        this.filterChains = null;
        this.footer = null;
        this.header = null;
        this.binary = false;
        this.outputWriter = null;
        this.textBuffer = null;
        this.eolString = StringUtils.LINE_SEP;
        this.rc = null;
        this.ignoreEmpty = true;
        this.force = false;
    }
    
    public void setDestfile(final File destinationFile) {
        this.setDest(new FileResource(destinationFile));
    }
    
    public void setDest(final Resource dest) {
        this.dest = dest;
    }
    
    public void setAppend(final boolean append) {
        this.append = append;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
        if (this.outputEncoding == null) {
            this.outputEncoding = encoding;
        }
    }
    
    public void setOutputEncoding(final String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }
    
    @Deprecated
    public void setForce(final boolean forceOverwrite) {
        this.forceOverwrite = forceOverwrite;
    }
    
    public void setOverwrite(final boolean forceOverwrite) {
        this.setForce(forceOverwrite);
    }
    
    public void setForceReadOnly(final boolean f) {
        this.force = f;
    }
    
    public void setIgnoreEmpty(final boolean ignoreEmpty) {
        this.ignoreEmpty = ignoreEmpty;
    }
    
    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }
    
    public Path createPath() {
        final Path path = new Path(this.getProject());
        this.add(path);
        return path;
    }
    
    public void addFileset(final FileSet set) {
        this.add(set);
    }
    
    public void addFilelist(final FileList list) {
        this.add(list);
    }
    
    public void add(final ResourceCollection c) {
        synchronized (this) {
            if (this.rc == null) {
                (this.rc = new Resources()).setProject(this.getProject());
                this.rc.setCache(true);
            }
        }
        this.rc.add(c);
    }
    
    public void addFilterChain(final FilterChain filterChain) {
        if (this.filterChains == null) {
            this.filterChains = new Vector<FilterChain>();
        }
        this.filterChains.addElement(filterChain);
    }
    
    public void addText(final String text) {
        if (this.textBuffer == null) {
            this.textBuffer = new StringBuffer(text.length());
        }
        this.textBuffer.append(text);
    }
    
    public void addHeader(final TextElement headerToAdd) {
        this.header = headerToAdd;
    }
    
    public void addFooter(final TextElement footerToAdd) {
        this.footer = footerToAdd;
    }
    
    public void setFixLastLine(final boolean fixLastLine) {
        this.fixLastLine = fixLastLine;
    }
    
    public void setEol(final FixCRLF.CrLf crlf) {
        final String s = crlf.getValue();
        if (s.equals("cr") || s.equals("mac")) {
            this.eolString = "\r";
        }
        else if (s.equals("lf") || s.equals("unix")) {
            this.eolString = "\n";
        }
        else if (s.equals("crlf") || s.equals("dos")) {
            this.eolString = "\r\n";
        }
    }
    
    public void setWriter(final Writer outputWriter) {
        this.outputWriter = outputWriter;
    }
    
    public void setBinary(final boolean binary) {
        this.binary = binary;
    }
    
    @Override
    public void execute() {
        this.validate();
        if (this.binary && this.dest == null) {
            throw new BuildException("dest|destfile attribute is required for binary concatenation");
        }
        final ResourceCollection c = this.getResources();
        if (this.isUpToDate(c)) {
            this.log(this.dest + " is up-to-date.", 3);
            return;
        }
        if (c.size() == 0 && this.ignoreEmpty) {
            return;
        }
        try {
            ResourceUtils.copyResource(new ConcatResource(c), (this.dest == null) ? new LogOutputResource(this, 1) : this.dest, null, null, true, false, this.append, null, null, this.getProject(), this.force);
        }
        catch (IOException e) {
            throw new BuildException("error concatenating content to " + this.dest, e);
        }
    }
    
    public Iterator<Resource> iterator() {
        this.validate();
        return (Iterator<Resource>)Collections.singletonList(new ConcatResource(this.getResources())).iterator();
    }
    
    public int size() {
        return 1;
    }
    
    public boolean isFilesystemOnly() {
        return false;
    }
    
    private void validate() {
        this.sanitizeText();
        if (this.binary) {
            if (this.textBuffer != null) {
                throw new BuildException("Nested text is incompatible with binary concatenation");
            }
            if (this.encoding != null || this.outputEncoding != null) {
                throw new BuildException("Setting input or output encoding is incompatible with binary concatenation");
            }
            if (this.filterChains != null) {
                throw new BuildException("Setting filters is incompatible with binary concatenation");
            }
            if (this.fixLastLine) {
                throw new BuildException("Setting fixlastline is incompatible with binary concatenation");
            }
            if (this.header != null || this.footer != null) {
                throw new BuildException("Nested header or footer is incompatible with binary concatenation");
            }
        }
        if (this.dest != null && this.outputWriter != null) {
            throw new BuildException("Cannot specify both a destination resource and an output writer");
        }
        if (this.rc == null && this.textBuffer == null) {
            throw new BuildException("At least one resource must be provided, or some text.");
        }
        if (this.rc != null && this.textBuffer != null) {
            throw new BuildException("Cannot include inline text when using resources.");
        }
    }
    
    private ResourceCollection getResources() {
        if (this.rc == null) {
            return new StringResource(this.getProject(), this.textBuffer.toString());
        }
        if (this.dest != null) {
            final Intersect checkDestNotInSources = new Intersect();
            checkDestNotInSources.setProject(this.getProject());
            checkDestNotInSources.add(this.rc);
            checkDestNotInSources.add(this.dest);
            if (checkDestNotInSources.size() > 0) {
                throw new BuildException("Destination resource " + this.dest + " was specified as an input resource.");
            }
        }
        final Restrict noexistRc = new Restrict();
        noexistRc.add(Concat.NOT_EXISTS);
        noexistRc.add(this.rc);
        for (final Resource r : noexistRc) {
            this.log(r + " does not exist.", 0);
        }
        final Restrict result = new Restrict();
        result.add(Concat.EXISTS);
        result.add(this.rc);
        return result;
    }
    
    private boolean isUpToDate(final ResourceCollection c) {
        if (this.dest == null || this.forceOverwrite) {
            return false;
        }
        for (final Resource r : c) {
            if (SelectorUtils.isOutOfDate(r, this.dest, Concat.FILE_UTILS.getFileTimestampGranularity())) {
                return false;
            }
        }
        return true;
    }
    
    private void sanitizeText() {
        if (this.textBuffer != null && "".equals(this.textBuffer.toString().trim())) {
            this.textBuffer = null;
        }
    }
    
    private Reader getFilteredReader(final Reader r) {
        if (this.filterChains == null) {
            return r;
        }
        final ChainReaderHelper helper = new ChainReaderHelper();
        helper.setBufferSize(8192);
        helper.setPrimaryReader(r);
        helper.setFilterChains(this.filterChains);
        helper.setProject(this.getProject());
        return helper.getAssembledReader();
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
        EXISTS = new Exists();
        NOT_EXISTS = new Not(Concat.EXISTS);
    }
    
    public static class TextElement extends ProjectComponent
    {
        private String value;
        private boolean trimLeading;
        private boolean trim;
        private boolean filtering;
        private String encoding;
        
        public TextElement() {
            this.value = "";
            this.trimLeading = false;
            this.trim = false;
            this.filtering = true;
            this.encoding = null;
        }
        
        public void setFiltering(final boolean filtering) {
            this.filtering = filtering;
        }
        
        private boolean getFiltering() {
            return this.filtering;
        }
        
        public void setEncoding(final String encoding) {
            this.encoding = encoding;
        }
        
        public void setFile(final File file) throws BuildException {
            if (!file.exists()) {
                throw new BuildException("File " + file + " does not exist.");
            }
            BufferedReader reader = null;
            try {
                if (this.encoding == null) {
                    reader = new BufferedReader(new FileReader(file));
                }
                else {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), this.encoding));
                }
                this.value = FileUtils.safeReadFully(reader);
            }
            catch (IOException ex) {
                throw new BuildException(ex);
            }
            finally {
                FileUtils.close(reader);
            }
        }
        
        public void addText(final String value) {
            this.value += this.getProject().replaceProperties(value);
        }
        
        public void setTrimLeading(final boolean strip) {
            this.trimLeading = strip;
        }
        
        public void setTrim(final boolean trim) {
            this.trim = trim;
        }
        
        public String getValue() {
            if (this.value == null) {
                this.value = "";
            }
            if (this.value.trim().length() == 0) {
                this.value = "";
            }
            if (this.trimLeading) {
                final char[] current = this.value.toCharArray();
                final StringBuffer b = new StringBuffer(current.length);
                boolean startOfLine = true;
                int pos = 0;
                while (pos < current.length) {
                    final char ch = current[pos++];
                    if (startOfLine) {
                        if (ch == ' ') {
                            continue;
                        }
                        if (ch == '\t') {
                            continue;
                        }
                        startOfLine = false;
                    }
                    b.append(ch);
                    if (ch == '\n' || ch == '\r') {
                        startOfLine = true;
                    }
                }
                this.value = b.toString();
            }
            if (this.trim) {
                this.value = this.value.trim();
            }
            return this.value;
        }
    }
    
    private final class MultiReader<S> extends Reader
    {
        private Reader reader;
        private int lastPos;
        private char[] lastChars;
        private boolean needAddSeparator;
        private Iterator<S> readerSources;
        private ReaderFactory<S> factory;
        
        private MultiReader(final Iterator<S> readerSources, final ReaderFactory<S> factory) {
            this.reader = null;
            this.lastPos = 0;
            this.lastChars = new char[Concat.this.eolString.length()];
            this.needAddSeparator = false;
            this.readerSources = readerSources;
            this.factory = factory;
        }
        
        private Reader getReader() throws IOException {
            if (this.reader == null && this.readerSources.hasNext()) {
                this.reader = this.factory.getReader(this.readerSources.next());
                Arrays.fill(this.lastChars, '\0');
            }
            return this.reader;
        }
        
        private void nextReader() throws IOException {
            this.close();
            this.reader = null;
        }
        
        @Override
        public int read() throws IOException {
            if (this.needAddSeparator) {
                final int ret = Concat.this.eolString.charAt(this.lastPos++);
                if (this.lastPos >= Concat.this.eolString.length()) {
                    this.lastPos = 0;
                    this.needAddSeparator = false;
                }
                return ret;
            }
            while (this.getReader() != null) {
                final int ch = this.getReader().read();
                if (ch != -1) {
                    this.addLastChar((char)ch);
                    return ch;
                }
                this.nextReader();
                if (!this.isFixLastLine() || !this.isMissingEndOfLine()) {
                    continue;
                }
                this.needAddSeparator = true;
                this.lastPos = 0;
            }
            return -1;
        }
        
        @Override
        public int read(final char[] cbuf, int off, int len) throws IOException {
            int amountRead = 0;
            while (this.getReader() != null || this.needAddSeparator) {
                if (this.needAddSeparator) {
                    cbuf[off] = Concat.this.eolString.charAt(this.lastPos++);
                    if (this.lastPos >= Concat.this.eolString.length()) {
                        this.lastPos = 0;
                        this.needAddSeparator = false;
                    }
                    --len;
                    ++off;
                    ++amountRead;
                    if (len == 0) {
                        return amountRead;
                    }
                    continue;
                }
                else {
                    final int nRead = this.getReader().read(cbuf, off, len);
                    if (nRead == -1 || nRead == 0) {
                        this.nextReader();
                        if (!this.isFixLastLine() || !this.isMissingEndOfLine()) {
                            continue;
                        }
                        this.needAddSeparator = true;
                        this.lastPos = 0;
                    }
                    else {
                        if (this.isFixLastLine()) {
                            for (int i = nRead; i > nRead - this.lastChars.length; --i) {
                                if (i <= 0) {
                                    break;
                                }
                                this.addLastChar(cbuf[off + i - 1]);
                            }
                        }
                        len -= nRead;
                        off += nRead;
                        amountRead += nRead;
                        if (len == 0) {
                            return amountRead;
                        }
                        continue;
                    }
                }
            }
            if (amountRead == 0) {
                return -1;
            }
            return amountRead;
        }
        
        @Override
        public void close() throws IOException {
            if (this.reader != null) {
                this.reader.close();
            }
        }
        
        private void addLastChar(final char ch) {
            for (int i = this.lastChars.length - 2; i >= 0; --i) {
                this.lastChars[i] = this.lastChars[i + 1];
            }
            this.lastChars[this.lastChars.length - 1] = ch;
        }
        
        private boolean isMissingEndOfLine() {
            for (int i = 0; i < this.lastChars.length; ++i) {
                if (this.lastChars[i] != Concat.this.eolString.charAt(i)) {
                    return true;
                }
            }
            return false;
        }
        
        private boolean isFixLastLine() {
            return Concat.this.fixLastLine && Concat.this.textBuffer == null;
        }
    }
    
    private final class ConcatResource extends Resource
    {
        private ResourceCollection c;
        
        private ConcatResource(final ResourceCollection c) {
            this.c = c;
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            if (Concat.this.binary) {
                final ConcatResourceInputStream result = new ConcatResourceInputStream(this.c);
                result.setManagingComponent(this);
                return result;
            }
            final Reader resourceReader = Concat.this.getFilteredReader(new MultiReader<Object>((Iterator)this.c.iterator(), Concat.this.resourceReaderFactory));
            Reader rdr;
            if (Concat.this.header == null && Concat.this.footer == null) {
                rdr = resourceReader;
            }
            else {
                int readerCount = 1;
                if (Concat.this.header != null) {
                    ++readerCount;
                }
                if (Concat.this.footer != null) {
                    ++readerCount;
                }
                final Reader[] readers = new Reader[readerCount];
                int pos = 0;
                if (Concat.this.header != null) {
                    readers[pos] = new StringReader(Concat.this.header.getValue());
                    if (Concat.this.header.getFiltering()) {
                        readers[pos] = Concat.this.getFilteredReader(readers[pos]);
                    }
                    ++pos;
                }
                readers[pos++] = resourceReader;
                if (Concat.this.footer != null) {
                    readers[pos] = new StringReader(Concat.this.footer.getValue());
                    if (Concat.this.footer.getFiltering()) {
                        readers[pos] = Concat.this.getFilteredReader(readers[pos]);
                    }
                }
                rdr = new MultiReader<Object>((Iterator)Arrays.asList(readers).iterator(), Concat.this.identityReaderFactory);
            }
            return (Concat.this.outputEncoding == null) ? new ReaderInputStream(rdr) : new ReaderInputStream(rdr, Concat.this.outputEncoding);
        }
        
        @Override
        public String getName() {
            return (Concat.this.resourceName == null) ? ("concat (" + String.valueOf(this.c) + ")") : Concat.this.resourceName;
        }
    }
    
    private interface ReaderFactory<S>
    {
        Reader getReader(final S p0) throws IOException;
    }
}
