// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import org.apache.hive.service.cli.thrift.TCLIService;
import org.apache.thrift.TProcessor;
import javax.security.auth.callback.CallbackHandler;
import org.apache.thrift.transport.TSaslClientTransport;
import java.io.IOException;
import javax.security.sasl.SaslException;
import org.apache.hadoop.hive.shims.ShimLoader;
import java.util.Map;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.TProcessorFactory;
import org.apache.hive.service.cli.thrift.ThriftCLIService;
import org.apache.hadoop.hive.thrift.HadoopThriftAuthBridge;

public final class KerberosSaslHelper
{
    public static TProcessorFactory getKerberosProcessorFactory(final HadoopThriftAuthBridge.Server saslServer, final ThriftCLIService service) {
        return new CLIServiceProcessorFactory(saslServer, service);
    }
    
    public static TTransport getKerberosTransport(final String principal, final String host, final TTransport underlyingTransport, final Map<String, String> saslProps, final boolean assumeSubject) throws SaslException {
        try {
            final String[] names = principal.split("[/@]");
            if (names.length != 3) {
                throw new IllegalArgumentException("Kerberos principal should have 3 parts: " + principal);
            }
            if (assumeSubject) {
                return createSubjectAssumedTransport(principal, underlyingTransport, saslProps);
            }
            final HadoopThriftAuthBridge.Client authBridge = ShimLoader.getHadoopThriftAuthBridge().createClientWithConf("kerberos");
            return authBridge.createClientTransport(principal, host, "KERBEROS", null, underlyingTransport, saslProps);
        }
        catch (IOException e) {
            throw new SaslException("Failed to open client transport", e);
        }
    }
    
    public static TTransport createSubjectAssumedTransport(final String principal, final TTransport underlyingTransport, final Map<String, String> saslProps) throws IOException {
        final String[] names = principal.split("[/@]");
        try {
            final TTransport saslTransport = new TSaslClientTransport("GSSAPI", null, names[0], names[1], saslProps, null, underlyingTransport);
            return new TSubjectAssumingTransport(saslTransport);
        }
        catch (SaslException se) {
            throw new IOException("Could not instantiate SASL transport", se);
        }
    }
    
    public static TTransport getTokenTransport(final String tokenStr, final String host, final TTransport underlyingTransport, final Map<String, String> saslProps) throws SaslException {
        final HadoopThriftAuthBridge.Client authBridge = ShimLoader.getHadoopThriftAuthBridge().createClientWithConf("kerberos");
        try {
            return authBridge.createClientTransport(null, host, "DIGEST", tokenStr, underlyingTransport, saslProps);
        }
        catch (IOException e) {
            throw new SaslException("Failed to open client transport", e);
        }
    }
    
    private KerberosSaslHelper() {
        throw new UnsupportedOperationException("Can't initialize class");
    }
    
    private static class CLIServiceProcessorFactory extends TProcessorFactory
    {
        private final ThriftCLIService service;
        private final HadoopThriftAuthBridge.Server saslServer;
        
        public CLIServiceProcessorFactory(final HadoopThriftAuthBridge.Server saslServer, final ThriftCLIService service) {
            super(null);
            this.service = service;
            this.saslServer = saslServer;
        }
        
        @Override
        public TProcessor getProcessor(final TTransport trans) {
            final TProcessor sqlProcessor = new TCLIService.Processor<Object>(this.service);
            return this.saslServer.wrapNonAssumingProcessor(sqlProcessor);
        }
    }
}
