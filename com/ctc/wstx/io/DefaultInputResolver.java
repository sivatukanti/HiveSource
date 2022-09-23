// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.Reader;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.util.URLUtil;
import com.ctc.wstx.api.ReaderConfig;
import javax.xml.stream.XMLResolver;
import java.net.URL;

public final class DefaultInputResolver
{
    private DefaultInputResolver() {
    }
    
    public static WstxInputSource resolveEntity(final WstxInputSource parent, URL pathCtxt, final String entityName, final String publicId, final String systemId, final XMLResolver customResolver, final ReaderConfig cfg, final int xmlVersion) throws IOException, XMLStreamException {
        if (pathCtxt == null) {
            pathCtxt = parent.getSource();
            if (pathCtxt == null) {
                pathCtxt = URLUtil.urlFromCurrentDir();
            }
        }
        if (customResolver != null) {
            final Object source = customResolver.resolveEntity(publicId, systemId, pathCtxt.toExternalForm(), entityName);
            if (source != null) {
                return sourceFrom(parent, cfg, entityName, xmlVersion, source);
            }
        }
        if (systemId == null) {
            throw new XMLStreamException("Can not resolve " + ((entityName == null) ? "[External DTD subset]" : ("entity '" + entityName + "'")) + " without a system id (public id '" + publicId + "')");
        }
        final URL url = URLUtil.urlFromSystemId(systemId, pathCtxt);
        return sourceFromURL(parent, cfg, entityName, xmlVersion, url, publicId);
    }
    
    public static WstxInputSource resolveEntityUsing(final WstxInputSource refCtxt, final String entityName, final String publicId, final String systemId, final XMLResolver resolver, final ReaderConfig cfg, final int xmlVersion) throws IOException, XMLStreamException {
        URL ctxt = (refCtxt == null) ? null : refCtxt.getSource();
        if (ctxt == null) {
            ctxt = URLUtil.urlFromCurrentDir();
        }
        final Object source = resolver.resolveEntity(publicId, systemId, ctxt.toExternalForm(), entityName);
        return (source == null) ? null : sourceFrom(refCtxt, cfg, entityName, xmlVersion, source);
    }
    
    protected static WstxInputSource sourceFrom(final WstxInputSource parent, final ReaderConfig cfg, final String refName, final int xmlVersion, final Object o) throws IllegalArgumentException, IOException, XMLStreamException {
        if (o instanceof Source) {
            if (o instanceof StreamSource) {
                return sourceFromSS(parent, cfg, refName, xmlVersion, (StreamSource)o);
            }
            throw new IllegalArgumentException("Can not use other Source objects than StreamSource: got " + o.getClass());
        }
        else {
            if (o instanceof URL) {
                return sourceFromURL(parent, cfg, refName, xmlVersion, (URL)o, null);
            }
            if (o instanceof InputStream) {
                return sourceFromIS(parent, cfg, refName, xmlVersion, (InputStream)o, null, null);
            }
            if (o instanceof Reader) {
                return sourceFromR(parent, cfg, refName, xmlVersion, (Reader)o, null, null);
            }
            if (o instanceof String) {
                return sourceFromString(parent, cfg, refName, xmlVersion, (String)o);
            }
            if (o instanceof File) {
                final URL u = URLUtil.toURL((File)o);
                return sourceFromURL(parent, cfg, refName, xmlVersion, u, null);
            }
            throw new IllegalArgumentException("Unrecognized input argument type for sourceFrom(): " + o.getClass());
        }
    }
    
    public static Reader constructOptimizedReader(final ReaderConfig cfg, final InputStream in, final boolean isXml11, final String encoding) throws XMLStreamException {
        final int inputBufLen = cfg.getInputBufferLength();
        final String normEnc = CharsetNames.normalize(encoding);
        final boolean recycleBuffer = true;
        BaseReader r;
        if (normEnc == "UTF-8") {
            r = new UTF8Reader(cfg, in, cfg.allocFullBBuffer(inputBufLen), 0, 0, recycleBuffer);
        }
        else if (normEnc == "ISO-8859-1") {
            r = new ISOLatinReader(cfg, in, cfg.allocFullBBuffer(inputBufLen), 0, 0, recycleBuffer);
        }
        else if (normEnc == "US-ASCII") {
            r = new AsciiReader(cfg, in, cfg.allocFullBBuffer(inputBufLen), 0, 0, recycleBuffer);
        }
        else if (normEnc.startsWith("UTF-32")) {
            final boolean isBE = normEnc == "UTF-32BE";
            r = new UTF32Reader(cfg, in, cfg.allocFullBBuffer(inputBufLen), 0, 0, recycleBuffer, isBE);
        }
        else {
            try {
                return new InputStreamReader(in, encoding);
            }
            catch (UnsupportedEncodingException ex) {
                throw new XMLStreamException("[unsupported encoding]: " + ex);
            }
        }
        if (isXml11) {
            r.setXmlCompliancy(272);
        }
        return r;
    }
    
    private static WstxInputSource sourceFromSS(final WstxInputSource parent, final ReaderConfig cfg, final String refName, final int xmlVersion, final StreamSource ssrc) throws IOException, XMLStreamException {
        final Reader r = ssrc.getReader();
        final String pubId = ssrc.getPublicId();
        final String sysId0 = ssrc.getSystemId();
        final URL ctxt = (parent == null) ? null : parent.getSource();
        final URL url = (sysId0 == null || sysId0.length() == 0) ? null : URLUtil.urlFromSystemId(sysId0, ctxt);
        final SystemId systemId = SystemId.construct(sysId0, (url == null) ? ctxt : url);
        InputBootstrapper bs;
        if (r == null) {
            InputStream in = ssrc.getInputStream();
            if (in == null) {
                if (url == null) {
                    throw new IllegalArgumentException("Can not create Stax reader for a StreamSource -- neither reader, input stream nor system id was set.");
                }
                in = URLUtil.inputStreamFromURL(url);
            }
            bs = StreamBootstrapper.getInstance(pubId, systemId, in);
        }
        else {
            bs = ReaderBootstrapper.getInstance(pubId, systemId, r, null);
        }
        final Reader r2 = bs.bootstrapInput(cfg, false, xmlVersion);
        return InputSourceFactory.constructEntitySource(cfg, parent, refName, bs, pubId, systemId, xmlVersion, r2);
    }
    
    private static WstxInputSource sourceFromURL(final WstxInputSource parent, final ReaderConfig cfg, final String refName, final int xmlVersion, final URL url, final String pubId) throws IOException, XMLStreamException {
        final InputStream in = URLUtil.inputStreamFromURL(url);
        final SystemId sysId = SystemId.construct(url);
        final StreamBootstrapper bs = StreamBootstrapper.getInstance(pubId, sysId, in);
        final Reader r = bs.bootstrapInput(cfg, false, xmlVersion);
        return InputSourceFactory.constructEntitySource(cfg, parent, refName, bs, pubId, sysId, xmlVersion, r);
    }
    
    public static WstxInputSource sourceFromString(final WstxInputSource parent, final ReaderConfig cfg, final String refName, final int xmlVersion, final String refContent) throws IOException, XMLStreamException {
        return sourceFromR(parent, cfg, refName, xmlVersion, new StringReader(refContent), null, refName);
    }
    
    private static WstxInputSource sourceFromIS(final WstxInputSource parent, final ReaderConfig cfg, final String refName, final int xmlVersion, final InputStream is, final String pubId, final String sysId) throws IOException, XMLStreamException {
        final StreamBootstrapper bs = StreamBootstrapper.getInstance(pubId, SystemId.construct(sysId), is);
        final Reader r = bs.bootstrapInput(cfg, false, xmlVersion);
        URL ctxt = parent.getSource();
        if (sysId != null && sysId.length() > 0) {
            ctxt = URLUtil.urlFromSystemId(sysId, ctxt);
        }
        return InputSourceFactory.constructEntitySource(cfg, parent, refName, bs, pubId, SystemId.construct(sysId, ctxt), xmlVersion, r);
    }
    
    private static WstxInputSource sourceFromR(final WstxInputSource parent, final ReaderConfig cfg, final String refName, final int xmlVersion, final Reader r, final String pubId, final String sysId) throws IOException, XMLStreamException {
        final ReaderBootstrapper rbs = ReaderBootstrapper.getInstance(pubId, SystemId.construct(sysId), r, null);
        final Reader r2 = rbs.bootstrapInput(cfg, false, xmlVersion);
        URL ctxt = (parent == null) ? null : parent.getSource();
        if (sysId != null && sysId.length() > 0) {
            ctxt = URLUtil.urlFromSystemId(sysId, ctxt);
        }
        return InputSourceFactory.constructEntitySource(cfg, parent, refName, rbs, pubId, SystemId.construct(sysId, ctxt), xmlVersion, r2);
    }
}
