// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.eclipse.jetty.util.log.Log;
import java.io.FilterInputStream;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import javax.servlet.ServletInputStream;
import java.io.File;
import java.io.InputStream;
import javax.servlet.http.Part;
import javax.servlet.MultipartConfigElement;
import org.eclipse.jetty.util.log.Logger;

public class MultiPartInputStreamParser
{
    private static final Logger LOG;
    public static final MultipartConfigElement __DEFAULT_MULTIPART_CONFIG;
    public static final MultiMap<Part> EMPTY_MAP;
    protected InputStream _in;
    protected MultipartConfigElement _config;
    protected String _contentType;
    protected MultiMap<Part> _parts;
    protected Exception _err;
    protected File _tmpDir;
    protected File _contextTmpDir;
    protected boolean _deleteOnExit;
    protected boolean _writeFilesWithFilenames;
    
    public MultiPartInputStreamParser(final InputStream in, final String contentType, final MultipartConfigElement config, final File contextTmpDir) {
        this._contentType = contentType;
        this._config = config;
        this._contextTmpDir = contextTmpDir;
        if (this._contextTmpDir == null) {
            this._contextTmpDir = new File(System.getProperty("java.io.tmpdir"));
        }
        if (this._config == null) {
            this._config = new MultipartConfigElement(this._contextTmpDir.getAbsolutePath());
        }
        if (in instanceof ServletInputStream && ((ServletInputStream)in).isFinished()) {
            this._parts = MultiPartInputStreamParser.EMPTY_MAP;
            return;
        }
        this._in = new ReadLineInputStream(in);
    }
    
    public Collection<Part> getParsedParts() {
        if (this._parts == null) {
            return (Collection<Part>)Collections.emptyList();
        }
        final Collection<List<Part>> values = this._parts.values();
        final List<Part> parts = new ArrayList<Part>();
        for (final List<Part> o : values) {
            final List<Part> asList = LazyList.getList(o, false);
            parts.addAll(asList);
        }
        return parts;
    }
    
    public void deleteParts() throws MultiException {
        final Collection<Part> parts = this.getParsedParts();
        final MultiException err = new MultiException();
        for (final Part p : parts) {
            try {
                ((MultiPart)p).cleanUp();
            }
            catch (Exception e) {
                err.add(e);
            }
        }
        this._parts.clear();
        err.ifExceptionThrowMulti();
    }
    
    public Collection<Part> getParts() throws IOException {
        this.parse();
        this.throwIfError();
        final Collection<List<Part>> values = this._parts.values();
        final List<Part> parts = new ArrayList<Part>();
        for (final List<Part> o : values) {
            final List<Part> asList = LazyList.getList(o, false);
            parts.addAll(asList);
        }
        return parts;
    }
    
    public Part getPart(final String name) throws IOException {
        this.parse();
        this.throwIfError();
        return this._parts.getValue(name, 0);
    }
    
    protected void throwIfError() throws IOException {
        if (this._err == null) {
            return;
        }
        if (this._err instanceof IOException) {
            throw (IOException)this._err;
        }
        if (this._err instanceof IllegalStateException) {
            throw (IllegalStateException)this._err;
        }
        throw new IllegalStateException(this._err);
    }
    
    protected void parse() {
        if (this._parts != null || this._err != null) {
            return;
        }
        long total = 0L;
        this._parts = new MultiMap<Part>();
        if (this._contentType == null || !this._contentType.startsWith("multipart/form-data")) {
            return;
        }
        try {
            if (this._config.getLocation() == null) {
                this._tmpDir = this._contextTmpDir;
            }
            else if ("".equals(this._config.getLocation())) {
                this._tmpDir = this._contextTmpDir;
            }
            else {
                final File f = new File(this._config.getLocation());
                if (f.isAbsolute()) {
                    this._tmpDir = f;
                }
                else {
                    this._tmpDir = new File(this._contextTmpDir, this._config.getLocation());
                }
            }
            if (!this._tmpDir.exists()) {
                this._tmpDir.mkdirs();
            }
            String contentTypeBoundary = "";
            final int bstart = this._contentType.indexOf("boundary=");
            if (bstart >= 0) {
                int bend = this._contentType.indexOf(";", bstart);
                bend = ((bend < 0) ? this._contentType.length() : bend);
                contentTypeBoundary = QuotedStringTokenizer.unquote(this.value(this._contentType.substring(bstart, bend)).trim());
            }
            final String boundary = "--" + contentTypeBoundary;
            final String lastBoundary = boundary + "--";
            final byte[] byteBoundary = lastBoundary.getBytes(StandardCharsets.ISO_8859_1);
            String line = null;
            try {
                line = ((ReadLineInputStream)this._in).readLine();
            }
            catch (IOException e) {
                MultiPartInputStreamParser.LOG.warn("Badly formatted multipart request", new Object[0]);
                throw e;
            }
            if (line == null) {
                throw new IOException("Missing content for multipart request");
            }
            boolean badFormatLogged = false;
            for (line = line.trim(); line != null && !line.equals(boundary) && !line.equals(lastBoundary); line = ((ReadLineInputStream)this._in).readLine(), line = ((line == null) ? line : line.trim())) {
                if (!badFormatLogged) {
                    MultiPartInputStreamParser.LOG.warn("Badly formatted multipart request", new Object[0]);
                    badFormatLogged = true;
                }
            }
            if (line == null) {
                throw new IOException("Missing initial multi part boundary");
            }
            if (line.equals(lastBoundary)) {
                return;
            }
            boolean lastPart = false;
        Label_1394:
            while (!lastPart) {
                String contentDisposition = null;
                String contentType = null;
                String contentTransferEncoding = null;
                final MultiMap<String> headers = new MultiMap<String>();
                while (true) {
                    line = ((ReadLineInputStream)this._in).readLine();
                    if (line == null) {
                        break Label_1394;
                    }
                    if ("".equals(line)) {
                        boolean form_data = false;
                        if (contentDisposition == null) {
                            throw new IOException("Missing content-disposition");
                        }
                        final QuotedStringTokenizer tok = new QuotedStringTokenizer(contentDisposition, ";", false, true);
                        String name = null;
                        String filename = null;
                        while (tok.hasMoreTokens()) {
                            final String t = tok.nextToken().trim();
                            final String tl = t.toLowerCase(Locale.ENGLISH);
                            if (t.startsWith("form-data")) {
                                form_data = true;
                            }
                            else if (tl.startsWith("name=")) {
                                name = this.value(t);
                            }
                            else {
                                if (!tl.startsWith("filename=")) {
                                    continue;
                                }
                                filename = this.filenameValue(t);
                            }
                        }
                        if (!form_data) {
                            break;
                        }
                        if (name == null) {
                            break;
                        }
                        final MultiPart part = new MultiPart(name, filename);
                        part.setHeaders(headers);
                        part.setContentType(contentType);
                        this._parts.add(name, part);
                        part.open();
                        InputStream partInput = null;
                        if ("base64".equalsIgnoreCase(contentTransferEncoding)) {
                            partInput = new Base64InputStream((ReadLineInputStream)this._in);
                        }
                        else if ("quoted-printable".equalsIgnoreCase(contentTransferEncoding)) {
                            partInput = new FilterInputStream(this._in) {
                                @Override
                                public int read() throws IOException {
                                    int c = this.in.read();
                                    if (c >= 0 && c == 61) {
                                        final int hi = this.in.read();
                                        final int lo = this.in.read();
                                        if (hi < 0 || lo < 0) {
                                            throw new IOException("Unexpected end to quoted-printable byte");
                                        }
                                        final char[] chars = { (char)hi, (char)lo };
                                        c = Integer.parseInt(new String(chars), 16);
                                    }
                                    return c;
                                }
                            };
                        }
                        else {
                            partInput = this._in;
                        }
                        try {
                            int state = -2;
                            boolean cr = false;
                            boolean lf = false;
                            while (true) {
                                int b = 0;
                                int c;
                                while ((c = ((state != -2) ? state : partInput.read())) != -1) {
                                    ++total;
                                    if (this._config.getMaxRequestSize() > 0L && total > this._config.getMaxRequestSize()) {
                                        throw new IllegalStateException("Request exceeds maxRequestSize (" + this._config.getMaxRequestSize() + ")");
                                    }
                                    state = -2;
                                    if (c == 13 || c == 10) {
                                        if (c == 13) {
                                            partInput.mark(1);
                                            final int tmp = partInput.read();
                                            if (tmp != 10) {
                                                partInput.reset();
                                            }
                                            else {
                                                state = tmp;
                                            }
                                            break;
                                        }
                                        break;
                                    }
                                    else if (b >= 0 && b < byteBoundary.length && c == byteBoundary[b]) {
                                        ++b;
                                    }
                                    else {
                                        if (cr) {
                                            part.write(13);
                                        }
                                        if (lf) {
                                            part.write(10);
                                        }
                                        lf = (cr = false);
                                        if (b > 0) {
                                            part.write(byteBoundary, 0, b);
                                        }
                                        b = -1;
                                        part.write(c);
                                    }
                                }
                                if ((b > 0 && b < byteBoundary.length - 2) || b == byteBoundary.length - 1) {
                                    if (cr) {
                                        part.write(13);
                                    }
                                    if (lf) {
                                        part.write(10);
                                    }
                                    lf = (cr = false);
                                    part.write(byteBoundary, 0, b);
                                    b = -1;
                                }
                                if (b > 0 || c == -1) {
                                    if (b == byteBoundary.length) {
                                        lastPart = true;
                                    }
                                    if (state == 10) {
                                        state = -2;
                                    }
                                    break;
                                }
                                if (cr) {
                                    part.write(13);
                                }
                                if (lf) {
                                    part.write(10);
                                }
                                cr = (c == 13);
                                lf = (c == 10 || state == 10);
                                if (state != 10) {
                                    continue;
                                }
                                state = -2;
                            }
                        }
                        finally {
                            part.close();
                        }
                        break;
                    }
                    else {
                        total += line.length();
                        if (this._config.getMaxRequestSize() > 0L && total > this._config.getMaxRequestSize()) {
                            throw new IllegalStateException("Request exceeds maxRequestSize (" + this._config.getMaxRequestSize() + ")");
                        }
                        final int c2 = line.indexOf(58, 0);
                        if (c2 <= 0) {
                            continue;
                        }
                        final String key = line.substring(0, c2).trim().toLowerCase(Locale.ENGLISH);
                        final String value = line.substring(c2 + 1, line.length()).trim();
                        headers.put(key, value);
                        if (key.equalsIgnoreCase("content-disposition")) {
                            contentDisposition = value;
                        }
                        if (key.equalsIgnoreCase("content-type")) {
                            contentType = value;
                        }
                        if (!key.equals("content-transfer-encoding")) {
                            continue;
                        }
                        contentTransferEncoding = value;
                    }
                }
            }
            if (!lastPart) {
                throw new IOException("Incomplete parts");
            }
            while (line != null) {
                line = ((ReadLineInputStream)this._in).readLine();
            }
        }
        catch (Exception e2) {
            this._err = e2;
        }
    }
    
    public void setDeleteOnExit(final boolean deleteOnExit) {
        this._deleteOnExit = deleteOnExit;
    }
    
    public void setWriteFilesWithFilenames(final boolean writeFilesWithFilenames) {
        this._writeFilesWithFilenames = writeFilesWithFilenames;
    }
    
    public boolean isWriteFilesWithFilenames() {
        return this._writeFilesWithFilenames;
    }
    
    public boolean isDeleteOnExit() {
        return this._deleteOnExit;
    }
    
    private String value(final String nameEqualsValue) {
        final int idx = nameEqualsValue.indexOf(61);
        final String value = nameEqualsValue.substring(idx + 1).trim();
        return QuotedStringTokenizer.unquoteOnly(value);
    }
    
    private String filenameValue(final String nameEqualsValue) {
        final int idx = nameEqualsValue.indexOf(61);
        String value = nameEqualsValue.substring(idx + 1).trim();
        if (value.matches(".??[a-z,A-Z]\\:\\\\[^\\\\].*")) {
            final char first = value.charAt(0);
            if (first == '\"' || first == '\'') {
                value = value.substring(1);
            }
            final char last = value.charAt(value.length() - 1);
            if (last == '\"' || last == '\'') {
                value = value.substring(0, value.length() - 1);
            }
            return value;
        }
        return QuotedStringTokenizer.unquoteOnly(value, true);
    }
    
    static {
        LOG = Log.getLogger(MultiPartInputStreamParser.class);
        __DEFAULT_MULTIPART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        EMPTY_MAP = new MultiMap<Part>(Collections.emptyMap());
    }
    
    public class MultiPart implements Part
    {
        protected String _name;
        protected String _filename;
        protected File _file;
        protected OutputStream _out;
        protected ByteArrayOutputStream2 _bout;
        protected String _contentType;
        protected MultiMap<String> _headers;
        protected long _size;
        protected boolean _temporary;
        
        public MultiPart(final String name, final String filename) throws IOException {
            this._size = 0L;
            this._temporary = true;
            this._name = name;
            this._filename = filename;
        }
        
        @Override
        public String toString() {
            return String.format("Part{n=%s,fn=%s,ct=%s,s=%d,t=%b,f=%s}", this._name, this._filename, this._contentType, this._size, this._temporary, this._file);
        }
        
        protected void setContentType(final String contentType) {
            this._contentType = contentType;
        }
        
        protected void open() throws IOException {
            if (MultiPartInputStreamParser.this.isWriteFilesWithFilenames() && this._filename != null && this._filename.trim().length() > 0) {
                this.createFile();
            }
            else {
                final ByteArrayOutputStream2 byteArrayOutputStream2 = new ByteArrayOutputStream2();
                this._bout = byteArrayOutputStream2;
                this._out = byteArrayOutputStream2;
            }
        }
        
        protected void close() throws IOException {
            this._out.close();
        }
        
        protected void write(final int b) throws IOException {
            if (MultiPartInputStreamParser.this._config.getMaxFileSize() > 0L && this._size + 1L > MultiPartInputStreamParser.this._config.getMaxFileSize()) {
                throw new IllegalStateException("Multipart Mime part " + this._name + " exceeds max filesize");
            }
            if (MultiPartInputStreamParser.this._config.getFileSizeThreshold() > 0 && this._size + 1L > MultiPartInputStreamParser.this._config.getFileSizeThreshold() && this._file == null) {
                this.createFile();
            }
            this._out.write(b);
            ++this._size;
        }
        
        protected void write(final byte[] bytes, final int offset, final int length) throws IOException {
            if (MultiPartInputStreamParser.this._config.getMaxFileSize() > 0L && this._size + length > MultiPartInputStreamParser.this._config.getMaxFileSize()) {
                throw new IllegalStateException("Multipart Mime part " + this._name + " exceeds max filesize");
            }
            if (MultiPartInputStreamParser.this._config.getFileSizeThreshold() > 0 && this._size + length > MultiPartInputStreamParser.this._config.getFileSizeThreshold() && this._file == null) {
                this.createFile();
            }
            this._out.write(bytes, offset, length);
            this._size += length;
        }
        
        protected void createFile() throws IOException {
            this._file = File.createTempFile("MultiPart", "", MultiPartInputStreamParser.this._tmpDir);
            if (MultiPartInputStreamParser.this._deleteOnExit) {
                this._file.deleteOnExit();
            }
            final FileOutputStream fos = new FileOutputStream(this._file);
            final BufferedOutputStream bos = new BufferedOutputStream(fos);
            if (this._size > 0L && this._out != null) {
                this._out.flush();
                this._bout.writeTo(bos);
                this._out.close();
                this._bout = null;
            }
            this._out = bos;
        }
        
        protected void setHeaders(final MultiMap<String> headers) {
            this._headers = headers;
        }
        
        @Override
        public String getContentType() {
            return this._contentType;
        }
        
        @Override
        public String getHeader(final String name) {
            if (name == null) {
                return null;
            }
            return this._headers.getValue(name.toLowerCase(Locale.ENGLISH), 0);
        }
        
        @Override
        public Collection<String> getHeaderNames() {
            return (Collection<String>)this._headers.keySet();
        }
        
        @Override
        public Collection<String> getHeaders(final String name) {
            return this._headers.getValues(name);
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            if (this._file != null) {
                return new BufferedInputStream(new FileInputStream(this._file));
            }
            return new ByteArrayInputStream(this._bout.getBuf(), 0, this._bout.size());
        }
        
        @Override
        public String getSubmittedFileName() {
            return this.getContentDispositionFilename();
        }
        
        public byte[] getBytes() {
            if (this._bout != null) {
                return this._bout.toByteArray();
            }
            return null;
        }
        
        @Override
        public String getName() {
            return this._name;
        }
        
        @Override
        public long getSize() {
            return this._size;
        }
        
        @Override
        public void write(final String fileName) throws IOException {
            if (this._file == null) {
                this._temporary = false;
                this._file = new File(MultiPartInputStreamParser.this._tmpDir, fileName);
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(this._file));
                    this._bout.writeTo(bos);
                    bos.flush();
                }
                finally {
                    if (bos != null) {
                        bos.close();
                    }
                    this._bout = null;
                }
            }
            else {
                this._temporary = false;
                final Path src = this._file.toPath();
                final Path target = src.resolveSibling(fileName);
                Files.move(src, target, StandardCopyOption.REPLACE_EXISTING);
                this._file = target.toFile();
            }
        }
        
        @Override
        public void delete() throws IOException {
            if (this._file != null && this._file.exists()) {
                this._file.delete();
            }
        }
        
        public void cleanUp() throws IOException {
            if (this._temporary && this._file != null && this._file.exists()) {
                this._file.delete();
            }
        }
        
        public File getFile() {
            return this._file;
        }
        
        public String getContentDispositionFilename() {
            return this._filename;
        }
    }
    
    private static class Base64InputStream extends InputStream
    {
        ReadLineInputStream _in;
        String _line;
        byte[] _buffer;
        int _pos;
        
        public Base64InputStream(final ReadLineInputStream rlis) {
            this._in = rlis;
        }
        
        @Override
        public int read() throws IOException {
            if (this._buffer == null || this._pos >= this._buffer.length) {
                this._line = this._in.readLine();
                if (this._line == null) {
                    return -1;
                }
                if (this._line.startsWith("--")) {
                    this._buffer = (this._line + "\r\n").getBytes();
                }
                else if (this._line.length() == 0) {
                    this._buffer = "\r\n".getBytes();
                }
                else {
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream(4 * this._line.length() / 3 + 2);
                    B64Code.decode(this._line, baos);
                    baos.write(13);
                    baos.write(10);
                    this._buffer = baos.toByteArray();
                }
                this._pos = 0;
            }
            return this._buffer[this._pos++];
        }
    }
}
