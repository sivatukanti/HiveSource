// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security.client;

import org.apache.hadoop.yarn.client.ClientRMProxy;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenRequest;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenRequest;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.apache.hadoop.security.token.Token;
import java.net.InetSocketAddress;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.apache.hadoop.security.token.TokenRenewer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class RMDelegationTokenIdentifier extends YARNDelegationTokenIdentifier
{
    public static final Text KIND_NAME;
    
    public RMDelegationTokenIdentifier() {
    }
    
    public RMDelegationTokenIdentifier(final Text owner, final Text renewer, final Text realUser) {
        super(owner, renewer, realUser);
    }
    
    @Override
    public Text getKind() {
        return RMDelegationTokenIdentifier.KIND_NAME;
    }
    
    static {
        KIND_NAME = new Text("RM_DELEGATION_TOKEN");
    }
    
    public static class Renewer extends TokenRenewer
    {
        private static AbstractDelegationTokenSecretManager<RMDelegationTokenIdentifier> localSecretManager;
        private static InetSocketAddress localServiceAddress;
        
        @Override
        public boolean handleKind(final Text kind) {
            return RMDelegationTokenIdentifier.KIND_NAME.equals(kind);
        }
        
        @Override
        public boolean isManaged(final Token<?> token) throws IOException {
            return true;
        }
        
        @InterfaceAudience.Private
        public static void setSecretManager(final AbstractDelegationTokenSecretManager<RMDelegationTokenIdentifier> secretManager, final InetSocketAddress serviceAddress) {
            Renewer.localSecretManager = secretManager;
            Renewer.localServiceAddress = serviceAddress;
        }
        
        @Override
        public long renew(final Token<?> token, final Configuration conf) throws IOException, InterruptedException {
            final ApplicationClientProtocol rmClient = getRmClient(token, conf);
            if (rmClient != null) {
                try {
                    final RenewDelegationTokenRequest request = Records.newRecord(RenewDelegationTokenRequest.class);
                    request.setDelegationToken(convertToProtoToken(token));
                    return rmClient.renewDelegationToken(request).getNextExpirationTime();
                }
                catch (YarnException e) {
                    throw new IOException(e);
                }
                finally {
                    RPC.stopProxy(rmClient);
                }
            }
            return Renewer.localSecretManager.renewToken((Token<RMDelegationTokenIdentifier>)token, getRenewer(token));
        }
        
        @Override
        public void cancel(final Token<?> token, final Configuration conf) throws IOException, InterruptedException {
            final ApplicationClientProtocol rmClient = getRmClient(token, conf);
            if (rmClient != null) {
                try {
                    final CancelDelegationTokenRequest request = Records.newRecord(CancelDelegationTokenRequest.class);
                    request.setDelegationToken(convertToProtoToken(token));
                    rmClient.cancelDelegationToken(request);
                }
                catch (YarnException e) {
                    throw new IOException(e);
                }
                finally {
                    RPC.stopProxy(rmClient);
                }
            }
            else {
                Renewer.localSecretManager.cancelToken((Token<RMDelegationTokenIdentifier>)token, getRenewer(token));
            }
        }
        
        private static ApplicationClientProtocol getRmClient(final Token<?> token, final Configuration conf) throws IOException {
            final String[] arr$;
            final String[] services = arr$ = token.getService().toString().split(",");
            for (final String service : arr$) {
                final InetSocketAddress addr = NetUtils.createSocketAddr(service);
                if (Renewer.localSecretManager != null) {
                    if (Renewer.localServiceAddress.getAddress().isAnyLocalAddress()) {
                        if (NetUtils.isLocalAddress(addr.getAddress()) && addr.getPort() == Renewer.localServiceAddress.getPort()) {
                            return null;
                        }
                    }
                    else if (addr.equals(Renewer.localServiceAddress)) {
                        return null;
                    }
                }
            }
            return ClientRMProxy.createRMProxy(conf, ApplicationClientProtocol.class);
        }
        
        private static String getRenewer(final Token<?> token) throws IOException {
            return ((RMDelegationTokenIdentifier)token.decodeIdentifier()).getRenewer().toString();
        }
        
        private static org.apache.hadoop.yarn.api.records.Token convertToProtoToken(final Token<?> token) {
            return org.apache.hadoop.yarn.api.records.Token.newInstance(token.getIdentifier(), token.getKind().toString(), token.getPassword(), token.getService().toString());
        }
    }
}
