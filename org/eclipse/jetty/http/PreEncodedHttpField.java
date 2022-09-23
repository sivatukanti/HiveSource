// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ArrayList;
import org.eclipse.jetty.util.log.Log;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.log.Logger;

public class PreEncodedHttpField extends HttpField
{
    private static final Logger LOG;
    private static final HttpFieldPreEncoder[] __encoders;
    private final byte[][] _encodedField;
    
    private static int index(final HttpVersion version) {
        switch (version) {
            case HTTP_1_0:
            case HTTP_1_1: {
                return 0;
            }
            case HTTP_2: {
                return 1;
            }
            default: {
                return -1;
            }
        }
    }
    
    public PreEncodedHttpField(final HttpHeader header, final String name, final String value) {
        super(header, name, value);
        this._encodedField = new byte[PreEncodedHttpField.__encoders.length][];
        for (int i = 0; i < PreEncodedHttpField.__encoders.length; ++i) {
            this._encodedField[i] = PreEncodedHttpField.__encoders[i].getEncodedField(header, header.asString(), value);
        }
    }
    
    public PreEncodedHttpField(final HttpHeader header, final String value) {
        this(header, header.asString(), value);
    }
    
    public PreEncodedHttpField(final String name, final String value) {
        this(null, name, value);
    }
    
    public void putTo(final ByteBuffer bufferInFillMode, final HttpVersion version) {
        bufferInFillMode.put(this._encodedField[index(version)]);
    }
    
    static {
        LOG = Log.getLogger(PreEncodedHttpField.class);
        final List<HttpFieldPreEncoder> encoders = new ArrayList<HttpFieldPreEncoder>();
        final Iterator<HttpFieldPreEncoder> iter = ServiceLoader.load(HttpFieldPreEncoder.class, PreEncodedHttpField.class.getClassLoader()).iterator();
        while (iter.hasNext()) {
            try {
                final HttpFieldPreEncoder encoder = iter.next();
                if (index(encoder.getHttpVersion()) < 0) {
                    continue;
                }
                encoders.add(encoder);
            }
            catch (Error | RuntimeException error) {
                final Throwable t;
                final Throwable e = t;
                PreEncodedHttpField.LOG.debug(e);
            }
        }
        PreEncodedHttpField.LOG.debug("HttpField encoders loaded: {}", encoders);
        final int size = encoders.size();
        __encoders = new HttpFieldPreEncoder[(size == 0) ? 1 : size];
        for (final HttpFieldPreEncoder e2 : encoders) {
            final int i = index(e2.getHttpVersion());
            if (PreEncodedHttpField.__encoders[i] == null) {
                PreEncodedHttpField.__encoders[i] = e2;
            }
            else {
                PreEncodedHttpField.LOG.warn("multiple PreEncoders for " + e2.getHttpVersion(), new Object[0]);
            }
        }
        if (PreEncodedHttpField.__encoders[0] == null) {
            PreEncodedHttpField.__encoders[0] = new Http1FieldPreEncoder();
        }
    }
}
