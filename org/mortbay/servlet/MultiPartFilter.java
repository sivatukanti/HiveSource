// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.servlet;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Collections;
import org.mortbay.util.LazyList;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.OutputStream;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.StringTokenizer;
import java.io.IOException;
import org.mortbay.util.TypeUtil;
import java.util.Map;
import org.mortbay.util.MultiMap;
import org.mortbay.util.StringUtil;
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
    private static final String FILES = "org.mortbay.servlet.MultiPartFilter.files";
    private File tempdir;
    private boolean _deleteFiles;
    private ServletContext _context;
    private int _fileOutputBuffer;
    
    public MultiPartFilter() {
        this._fileOutputBuffer = 0;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.tempdir = (File)filterConfig.getServletContext().getAttribute("javax.servlet.context.tempdir");
        this._deleteFiles = "true".equals(filterConfig.getInitParameter("deleteFiles"));
        final String fileOutputBuffer = filterConfig.getInitParameter("fileOutputBuffer");
        if (fileOutputBuffer != null) {
            this._fileOutputBuffer = Integer.parseInt(fileOutputBuffer);
        }
        this._context = filterConfig.getServletContext();
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest srequest = (HttpServletRequest)request;
        if (srequest.getContentType() == null || !srequest.getContentType().startsWith("multipart/form-data")) {
            chain.doFilter(request, response);
            return;
        }
        final BufferedInputStream in = new BufferedInputStream(request.getInputStream());
        final String content_type = srequest.getContentType();
        final String boundary = "--" + this.value(content_type.substring(content_type.indexOf("boundary=")));
        final byte[] byteBoundary = (boundary + "--").getBytes(StringUtil.__ISO_8859_1);
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
        Label_1090:
            while (!lastPart) {
                while (true) {
                    bytes = TypeUtil.readLine(in);
                    if (bytes == null) {
                        break Label_1090;
                    }
                    if (bytes.length == 0) {
                        boolean form_data = false;
                        if (content_disposition == null) {
                            throw new IOException("Missing content-disposition");
                        }
                        final StringTokenizer tok = new StringTokenizer(content_disposition, ";");
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
                                params.add(name, filename);
                                if (this._deleteFiles) {
                                    file.deleteOnExit();
                                    ArrayList files = (ArrayList)request.getAttribute("org.mortbay.servlet.MultiPartFilter.files");
                                    if (files == null) {
                                        files = new ArrayList();
                                        request.setAttribute("org.mortbay.servlet.MultiPartFilter.files", files);
                                    }
                                    files.add(file);
                                }
                            }
                            else {
                                out = new ByteArrayOutputStream();
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
                            params.add(name, bytes);
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
                        if (!key.equals("content-disposition")) {
                            continue;
                        }
                        content_disposition = value2;
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
        final ArrayList files = (ArrayList)request.getAttribute("org.mortbay.servlet.MultiPartFilter.files");
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
        String value = nameEqualsValue.substring(nameEqualsValue.indexOf(61) + 1).trim();
        int i = value.indexOf(59);
        if (i > 0) {
            value = value.substring(0, i);
        }
        if (value.startsWith("\"")) {
            value = value.substring(1, value.indexOf(34, 1));
        }
        else {
            i = value.indexOf(32);
            if (i > 0) {
                value = value.substring(0, i);
            }
        }
        return value;
    }
    
    public void destroy() {
    }
    
    private static class Wrapper extends HttpServletRequestWrapper
    {
        String encoding;
        MultiMap map;
        
        public Wrapper(final HttpServletRequest request, final MultiMap map) {
            super(request);
            this.encoding = "UTF-8";
            this.map = map;
        }
        
        public int getContentLength() {
            return 0;
        }
        
        public String getParameter(final String name) {
            Object o = this.map.get(name);
            if (!(o instanceof byte[]) && LazyList.size(o) > 0) {
                o = LazyList.get(o, 0);
            }
            if (o instanceof byte[]) {
                try {
                    final String s = new String((byte[])o, this.encoding);
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
        
        public Map getParameterMap() {
            return Collections.unmodifiableMap((Map<?, ?>)this.map.toStringArrayMap());
        }
        
        public Enumeration getParameterNames() {
            return Collections.enumeration(this.map.keySet());
        }
        
        public String[] getParameterValues(final String name) {
            final List l = this.map.getValues(name);
            if (l == null || l.size() == 0) {
                return new String[0];
            }
            final String[] v = new String[l.size()];
            for (int i = 0; i < l.size(); ++i) {
                final Object o = l.get(i);
                if (o instanceof byte[]) {
                    try {
                        v[i] = new String((byte[])o, this.encoding);
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
        
        public void setCharacterEncoding(final String enc) throws UnsupportedEncodingException {
            this.encoding = enc;
        }
    }
}
