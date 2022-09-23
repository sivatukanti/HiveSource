// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.net.URLStreamHandler;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.net.URLStreamHandlerFactory;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FsUrlStreamHandlerFactory implements URLStreamHandlerFactory
{
    private static final Logger LOG;
    public static final String[] UNEXPORTED_PROTOCOLS;
    private Configuration conf;
    private Map<String, Boolean> protocols;
    private URLStreamHandler handler;
    
    public FsUrlStreamHandlerFactory() {
        this(new Configuration());
    }
    
    public FsUrlStreamHandlerFactory(final Configuration conf) {
        this.protocols = new ConcurrentHashMap<String, Boolean>();
        this.conf = new Configuration(conf);
        try {
            FileSystem.getFileSystemClass("file", conf);
        }
        catch (IOException io) {
            throw new RuntimeException(io);
        }
        this.handler = new FsUrlStreamHandler(this.conf);
        for (final String protocol : FsUrlStreamHandlerFactory.UNEXPORTED_PROTOCOLS) {
            this.protocols.put(protocol, false);
        }
    }
    
    @Override
    public URLStreamHandler createURLStreamHandler(final String protocol) {
        FsUrlStreamHandlerFactory.LOG.debug("Creating handler for protocol {}", protocol);
        if (!this.protocols.containsKey(protocol)) {
            boolean known = true;
            try {
                final Class<? extends FileSystem> impl = FileSystem.getFileSystemClass(protocol, this.conf);
                FsUrlStreamHandlerFactory.LOG.debug("Found implementation of {}: {}", protocol, impl);
            }
            catch (IOException ex) {
                known = false;
            }
            this.protocols.put(protocol, known);
        }
        if (this.protocols.get(protocol)) {
            FsUrlStreamHandlerFactory.LOG.debug("Using handler for protocol {}", protocol);
            return this.handler;
        }
        FsUrlStreamHandlerFactory.LOG.debug("Unknown protocol {}, delegating to default implementation", protocol);
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(FsUrlStreamHandlerFactory.class);
        UNEXPORTED_PROTOCOLS = new String[] { "http", "https" };
    }
}
