// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlets;

import org.eclipse.jetty.util.B64Code;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Collections;
import org.eclipse.jetty.util.LazyList;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.OutputStream;
import java.util.Iterator;
import java.io.FilterInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.eclipse.jetty.util.TypeUtil;
import java.util.Map;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import java.io.InputStream;
import java.io.BufferedInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.io.File;
import javax.servlet.Filter;

public class MultiPartFilter implements Filter
{
    public static final String CONTENT_TYPE_SUFFIX = ".org.eclipse.jetty.servlet.contentType";
    private static final String FILES = "org.eclipse.jetty.servlet.MultiPartFilter.files";
    private File tempdir;
    private boolean _deleteFiles;
    private ServletContext _context;
    private int _fileOutputBuffer;
    private int _maxFormKeys;
    
    public MultiPartFilter() {
        this._fileOutputBuffer = 0;
        this._maxFormKeys = Integer.getInteger("org.eclipse.jetty.server.Request.maxFormKeys", 1000);
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.tempdir = (File)filterConfig.getServletContext().getAttribute("javax.servlet.context.tempdir");
        this._deleteFiles = "true".equals(filterConfig.getInitParameter("deleteFiles"));
        final String fileOutputBuffer = filterConfig.getInitParameter("fileOutputBuffer");
        if (fileOutputBuffer != null) {
            this._fileOutputBuffer = Integer.parseInt(fileOutputBuffer);
        }
        this._context = filterConfig.getServletContext();
        final String mfks = filterConfig.getInitParameter("maxFormKeys");
        if (mfks != null) {
            this._maxFormKeys = Integer.parseInt(mfks);
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest srequest = (HttpServletRequest)request;
        if (srequest.getContentType() == null || !srequest.getContentType().startsWith("multipart/form-data")) {
            chain.doFilter(request, response);
            return;
        }
        InputStream in = new BufferedInputStream(request.getInputStream());
        final String content_type = srequest.getContentType();
        final String boundary = "--" + QuotedStringTokenizer.unquote(this.value(content_type.substring(content_type.indexOf("boundary="))).trim());
        final byte[] byteBoundary = (boundary + "--").getBytes("ISO-8859-1");
        final MultiMap params = new MultiMap();
        for (final Map.Entry entry : request.getParameterMap().entrySet()) {
            final Object value = entry.getValue();
            if (value instanceof String[]) {
                params.addValues(entry.getKey(), (String[])value);
            }
            else {
                params.add(entry.getKey(), value);
            }
        }
        try {
            byte[] bytes = TypeUtil.readLine(in);
            String line = (bytes == null) ? null : new String(bytes, "UTF-8");
            if (line == null || !line.equals(boundary)) {
                throw new IOException("Missing initial multi part boundary");
            }
            boolean lastPart = false;
            String content_disposition = null;
            String content_transfer_encoding = null;
        Label_1240:
            while (!lastPart && params.size() < this._maxFormKeys) {
                String type_content = null;
                while (true) {
                    bytes = TypeUtil.readLine(in);
                    if (bytes == null) {
                        break Label_1240;
                    }
                    if (bytes.length == 0) {
                        boolean form_data = false;
                        if (content_disposition == null) {
                            throw new IOException("Missing content-disposition");
                        }
                        final QuotedStringTokenizer tok = new QuotedStringTokenizer(content_disposition, ";");
                        String name = null;
                        String filename = null;
                        while (tok.hasMoreTokens()) {
                            final String t = tok.nextToken().trim();
                            final String tl = t.toLowerCase();
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
                                filename = this.value(t);
                            }
                        }
                        if (!form_data) {
                            break;
                        }
                        if (name == null) {
                            break;
                        }
                        OutputStream out = null;
                        File file = null;
                        try {
                            if (filename != null && filename.length() > 0) {
                                file = File.createTempFile("MultiPart", "", this.tempdir);
                                out = new FileOutputStream(file);
                                if (this._fileOutputBuffer > 0) {
                                    out = new BufferedOutputStream(out, this._fileOutputBuffer);
                                }
                                request.setAttribute(name, file);
                                params.add((Object)name, (Object)filename);
                                if (type_content != null) {
                                    params.add((Object)(name + ".org.eclipse.jetty.servlet.contentType"), (Object)type_content);
                                }
                                if (this._deleteFiles) {
                                    file.deleteOnExit();
                                    ArrayList files = (ArrayList)request.getAttribute("org.eclipse.jetty.servlet.MultiPartFilter.files");
                                    if (files == null) {
                                        files = new ArrayList();
                                        request.setAttribute("org.eclipse.jetty.servlet.MultiPartFilter.files", files);
                                    }
                                    files.add(file);
                                }
                            }
                            else {
                                out = new ByteArrayOutputStream();
                            }
                            if ("base64".equalsIgnoreCase(content_transfer_encoding)) {
                                in = new Base64InputStream(in);
                            }
                            else if ("quoted-printable".equalsIgnoreCase(content_transfer_encoding)) {
                                in = new FilterInputStream(in) {
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
                            int state = -2;
                            boolean cr = false;
                            boolean lf = false;
                            int b;
                            while (true) {
                                b = 0;
                                int c;
                                while ((c = ((state != -2) ? state : in.read())) != -1) {
                                    state = -2;
                                    if (c == 13 || c == 10) {
                                        if (c == 13) {
                                            state = in.read();
                                            break;
                                        }
                                        break;
                                    }
                                    else if (b >= 0 && b < byteBoundary.length && c == byteBoundary[b]) {
                                        ++b;
                                    }
                                    else {
                                        if (cr) {
                                            out.write(13);
                                        }
                                        if (lf) {
                                            out.write(10);
                                        }
                                        lf = (cr = false);
                                        if (b > 0) {
                                            out.write(byteBoundary, 0, b);
                                        }
                                        b = -1;
                                        out.write(c);
                                    }
                                }
                                if ((b > 0 && b < byteBoundary.length - 2) || b == byteBoundary.length - 1) {
                                    if (cr) {
                                        out.write(13);
                                    }
                                    if (lf) {
                                        out.write(10);
                                    }
                                    lf = (cr = false);
                                    out.write(byteBoundary, 0, b);
                                    b = -1;
                                }
                                if (b > 0 || c == -1) {
                                    break;
                                }
                                if (cr) {
                                    out.write(13);
                                }
                                if (lf) {
                                    out.write(10);
                                }
                                cr = (c == 13);
                                lf = (c == 10 || state == 10);
                                if (state != 10) {
                                    continue;
                                }
                                state = -2;
                            }
                            if (b == byteBoundary.length) {
                                lastPart = true;
                            }
                            if (state == 10) {
                                state = -2;
                            }
                        }
                        finally {
                            out.close();
                        }
                        if (file == null) {
                            bytes = ((ByteArrayOutputStream)out).toByteArray();
                            params.add((Object)name, (Object)bytes);
                            if (type_content != null) {
                                params.add((Object)(name + ".org.eclipse.jetty.servlet.contentType"), (Object)type_content);
                            }
                        }
                        break;
                    }
                    else {
                        line = new String(bytes, "UTF-8");
                        final int c2 = line.indexOf(58, 0);
                        if (c2 <= 0) {
                            continue;
                        }
                        final String key = line.substring(0, c2).trim().toLowerCase();
                        final String value2 = line.substring(c2 + 1, line.length()).trim();
                        if (key.equals("content-disposition")) {
                            content_disposition = value2;
                        }
                        else if (key.equals("content-transfer-encoding")) {
                            content_transfer_encoding = value2;
                        }
                        else {
                            if (!key.equals("content-type")) {
                                continue;
                            }
                            type_content = value2;
                        }
                    }
                }
            }
            chain.doFilter(new Wrapper(srequest, params), response);
        }
        finally {
            this.deleteFiles(request);
        }
    }
    
    private void deleteFiles(final ServletRequest request) {
        final ArrayList files = (ArrayList)request.getAttribute("org.eclipse.jetty.servlet.MultiPartFilter.files");
        if (files != null) {
            for (final File file : files) {
                try {
                    file.delete();
                }
                catch (Exception e) {
                    this._context.log("failed to delete " + file, e);
                }
            }
        }
    }
    
    private String value(final String nameEqualsValue) {
        return nameEqualsValue.substring(nameEqualsValue.indexOf(61) + 1).trim();
    }
    
    public void destroy() {
    }
    
    private static class Wrapper extends HttpServletRequestWrapper
    {
        String _encoding;
        MultiMap _params;
        
        public Wrapper(final HttpServletRequest request, final MultiMap map) {
            super(request);
            this._encoding = "UTF-8";
            this._params = map;
        }
        
        @Override
        public int getContentLength() {
            return 0;
        }
        
        @Override
        public String getParameter(final String name) {
            Object o = this._params.get(name);
            if (!(o instanceof byte[]) && LazyList.size(o) > 0) {
                o = LazyList.get(o, 0);
            }
            if (o instanceof byte[]) {
                try {
                    final String s = new String((byte[])o, this._encoding);
                    return s;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            if (o != null) {
                return String.valueOf(o);
            }
            return null;
        }
        
        @Override
        public Map getParameterMap() {
            return Collections.unmodifiableMap((Map<?, ?>)this._params.toStringArrayMap());
        }
        
        @Override
        public Enumeration getParameterNames() {
            return Collections.enumeration((Collection<Object>)this._params.keySet());
        }
        
        @Override
        public String[] getParameterValues(final String name) {
            final List l = this._params.getValues((Object)name);
            if (l == null || l.size() == 0) {
                return new String[0];
            }
            final String[] v = new String[l.size()];
            for (int i = 0; i < l.size(); ++i) {
                final Object o = l.get(i);
                if (o instanceof byte[]) {
                    try {
                        v[i] = new String((byte[])o, this._encoding);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (o instanceof String) {
                    v[i] = (String)o;
                }
            }
            return v;
        }
        
        @Override
        public void setCharacterEncoding(final String enc) throws UnsupportedEncodingException {
            this._encoding = enc;
        }
    }
    
    private static class Base64InputStream extends InputStream
    {
        BufferedReader _in;
        String _line;
        byte[] _buffer;
        int _pos;
        
        public Base64InputStream(final InputStream in) {
            this._in = new BufferedReader(new InputStreamReader(in));
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
                    this._buffer = B64Code.decode(this._line);
                }
                this._pos = 0;
            }
            return this._buffer[this._pos++];
        }
    }
}
