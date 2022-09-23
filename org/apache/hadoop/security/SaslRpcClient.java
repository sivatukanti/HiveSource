// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.auth.callback.Callback;
import java.io.FilterOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import org.apache.hadoop.util.ProtoUtil;
import org.apache.hadoop.ipc.RpcConstants;
import org.apache.hadoop.ipc.RPC;
import org.slf4j.LoggerFactory;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.util.Locale;
import com.google.protobuf.ByteString;
import org.apache.hadoop.ipc.ResponseBuffer;
import java.nio.ByteBuffer;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.ipc.RpcWritable;
import java.io.OutputStream;
import org.apache.hadoop.ipc.Client;
import com.google.re2j.Pattern;
import org.apache.hadoop.security.authentication.util.KerberosName;
import org.apache.hadoop.fs.GlobPattern;
import javax.security.auth.kerberos.KerberosPrincipal;
import org.apache.hadoop.security.token.TokenInfo;
import org.apache.hadoop.security.token.TokenSelector;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import javax.security.sasl.Sasl;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.Token;
import java.io.IOException;
import javax.security.sasl.SaslException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos;
import javax.security.sasl.SaslClient;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class SaslRpcClient
{
    public static final Logger LOG;
    private final UserGroupInformation ugi;
    private final Class<?> protocol;
    private final InetSocketAddress serverAddr;
    private final Configuration conf;
    private SaslClient saslClient;
    private SaslPropertiesResolver saslPropsResolver;
    private SaslRpcServer.AuthMethod authMethod;
    private static final RpcHeaderProtos.RpcRequestHeaderProto saslHeader;
    private static final RpcHeaderProtos.RpcSaslProto negotiateRequest;
    
    public SaslRpcClient(final UserGroupInformation ugi, final Class<?> protocol, final InetSocketAddress serverAddr, final Configuration conf) {
        this.ugi = ugi;
        this.protocol = protocol;
        this.serverAddr = serverAddr;
        this.conf = conf;
        this.saslPropsResolver = SaslPropertiesResolver.getInstance(conf);
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public Object getNegotiatedProperty(final String key) {
        return (this.saslClient != null) ? this.saslClient.getNegotiatedProperty(key) : null;
    }
    
    @InterfaceAudience.Private
    public SaslRpcServer.AuthMethod getAuthMethod() {
        return this.authMethod;
    }
    
    private RpcHeaderProtos.RpcSaslProto.SaslAuth selectSaslClient(final List<RpcHeaderProtos.RpcSaslProto.SaslAuth> authTypes) throws SaslException, AccessControlException, IOException {
        RpcHeaderProtos.RpcSaslProto.SaslAuth selectedAuthType = null;
        boolean switchToSimple = false;
        for (final RpcHeaderProtos.RpcSaslProto.SaslAuth authType : authTypes) {
            if (!this.isValidAuthType(authType)) {
                continue;
            }
            final SaslRpcServer.AuthMethod authMethod = SaslRpcServer.AuthMethod.valueOf(authType.getMethod());
            if (authMethod == SaslRpcServer.AuthMethod.SIMPLE) {
                switchToSimple = true;
            }
            else {
                this.saslClient = this.createSaslClient(authType);
                if (this.saslClient == null) {
                    continue;
                }
            }
            selectedAuthType = authType;
            break;
        }
        if (this.saslClient == null && !switchToSimple) {
            final List<String> serverAuthMethods = new ArrayList<String>();
            for (final RpcHeaderProtos.RpcSaslProto.SaslAuth authType2 : authTypes) {
                serverAuthMethods.add(authType2.getMethod());
            }
            throw new AccessControlException("Client cannot authenticate via:" + serverAuthMethods);
        }
        if (SaslRpcClient.LOG.isDebugEnabled() && selectedAuthType != null) {
            SaslRpcClient.LOG.debug("Use " + selectedAuthType.getMethod() + " authentication for protocol " + this.protocol.getSimpleName());
        }
        return selectedAuthType;
    }
    
    private boolean isValidAuthType(final RpcHeaderProtos.RpcSaslProto.SaslAuth authType) {
        SaslRpcServer.AuthMethod authMethod;
        try {
            authMethod = SaslRpcServer.AuthMethod.valueOf(authType.getMethod());
        }
        catch (IllegalArgumentException iae) {
            authMethod = null;
        }
        return authMethod != null && authMethod.getMechanismName().equals(authType.getMechanism());
    }
    
    private SaslClient createSaslClient(final RpcHeaderProtos.RpcSaslProto.SaslAuth authType) throws SaslException, IOException {
        final String saslUser = null;
        final String saslProtocol = authType.getProtocol();
        final String saslServerName = authType.getServerId();
        final Map<String, String> saslProperties = this.saslPropsResolver.getClientProperties(this.serverAddr.getAddress());
        CallbackHandler saslCallback = null;
        final SaslRpcServer.AuthMethod method = SaslRpcServer.AuthMethod.valueOf(authType.getMethod());
        switch (method) {
            case TOKEN: {
                final Token<?> token = this.getServerToken(authType);
                if (token == null) {
                    SaslRpcClient.LOG.debug("tokens aren't supported for this protocol or user doesn't have one");
                    return null;
                }
                saslCallback = new SaslClientCallbackHandler((Token<? extends TokenIdentifier>)token);
                break;
            }
            case KERBEROS: {
                if (this.ugi.getRealAuthenticationMethod().getAuthMethod() != SaslRpcServer.AuthMethod.KERBEROS) {
                    SaslRpcClient.LOG.debug("client isn't using kerberos");
                    return null;
                }
                final String serverPrincipal = this.getServerPrincipal(authType);
                if (serverPrincipal == null) {
                    SaslRpcClient.LOG.debug("protocol doesn't use kerberos");
                    return null;
                }
                if (SaslRpcClient.LOG.isDebugEnabled()) {
                    SaslRpcClient.LOG.debug("RPC Server's Kerberos principal name for protocol=" + this.protocol.getCanonicalName() + " is " + serverPrincipal);
                    break;
                }
                break;
            }
            default: {
                throw new IOException("Unknown authentication method " + method);
            }
        }
        final String mechanism = method.getMechanismName();
        if (SaslRpcClient.LOG.isDebugEnabled()) {
            SaslRpcClient.LOG.debug("Creating SASL " + mechanism + "(" + method + ")  client to authenticate to service at " + saslServerName);
        }
        return Sasl.createSaslClient(new String[] { mechanism }, saslUser, saslProtocol, saslServerName, saslProperties, saslCallback);
    }
    
    private Token<?> getServerToken(final RpcHeaderProtos.RpcSaslProto.SaslAuth authType) throws IOException {
        final TokenInfo tokenInfo = SecurityUtil.getTokenInfo(this.protocol, this.conf);
        SaslRpcClient.LOG.debug("Get token info proto:" + this.protocol + " info:" + tokenInfo);
        if (tokenInfo == null) {
            return null;
        }
        TokenSelector<?> tokenSelector = null;
        try {
            tokenSelector = (TokenSelector<?>)tokenInfo.value().newInstance();
        }
        catch (InstantiationException | IllegalAccessException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new IOException(e.toString(), e);
        }
        return tokenSelector.selectToken(SecurityUtil.buildTokenService(this.serverAddr), this.ugi.getTokens());
    }
    
    @VisibleForTesting
    String getServerPrincipal(final RpcHeaderProtos.RpcSaslProto.SaslAuth authType) throws IOException {
        final KerberosInfo krbInfo = SecurityUtil.getKerberosInfo(this.protocol, this.conf);
        SaslRpcClient.LOG.debug("Get kerberos info proto:" + this.protocol + " info:" + krbInfo);
        if (krbInfo == null) {
            return null;
        }
        final String serverKey = krbInfo.serverPrincipal();
        if (serverKey == null) {
            throw new IllegalArgumentException("Can't obtain server Kerberos config key from protocol=" + this.protocol.getCanonicalName());
        }
        final String serverPrincipal = new KerberosPrincipal(authType.getProtocol() + "/" + authType.getServerId(), 3).getName();
        final String serverKeyPattern = this.conf.get(serverKey + ".pattern");
        if (serverKeyPattern != null && !serverKeyPattern.isEmpty()) {
            final Pattern pattern = GlobPattern.compile(serverKeyPattern);
            if (!pattern.matcher(serverPrincipal).matches()) {
                throw new IllegalArgumentException(String.format("Server has invalid Kerberos principal: %s, doesn't match the pattern: %s", serverPrincipal, serverKeyPattern));
            }
        }
        else {
            final String confPrincipal = SecurityUtil.getServerPrincipal(this.conf.get(serverKey), this.serverAddr.getAddress());
            if (SaslRpcClient.LOG.isDebugEnabled()) {
                SaslRpcClient.LOG.debug("getting serverKey: " + serverKey + " conf value: " + this.conf.get(serverKey) + " principal: " + confPrincipal);
            }
            if (confPrincipal == null || confPrincipal.isEmpty()) {
                throw new IllegalArgumentException("Failed to specify server's Kerberos principal name");
            }
            final KerberosName name = new KerberosName(confPrincipal);
            if (name.getHostName() == null) {
                throw new IllegalArgumentException("Kerberos principal name does NOT have the expected hostname part: " + confPrincipal);
            }
            if (!serverPrincipal.equals(confPrincipal)) {
                throw new IllegalArgumentException(String.format("Server has invalid Kerberos principal: %s, expecting: %s", serverPrincipal, confPrincipal));
            }
        }
        return serverPrincipal;
    }
    
    public SaslRpcServer.AuthMethod saslConnect(final Client.IpcStreams ipcStreams) throws IOException {
        this.authMethod = SaslRpcServer.AuthMethod.SIMPLE;
        this.sendSaslMessage(ipcStreams.out, SaslRpcClient.negotiateRequest);
        boolean done = false;
        do {
            final ByteBuffer bb = ipcStreams.readResponse();
            final RpcWritable.Buffer saslPacket = RpcWritable.Buffer.wrap(bb);
            final RpcHeaderProtos.RpcResponseHeaderProto header = saslPacket.getValue(RpcHeaderProtos.RpcResponseHeaderProto.getDefaultInstance());
            switch (header.getStatus()) {
                case ERROR:
                case FATAL: {
                    throw new RemoteException(header.getExceptionClassName(), header.getErrorMsg());
                }
                default: {
                    if (header.getCallId() != Server.AuthProtocol.SASL.callId) {
                        throw new SaslException("Non-SASL response during negotiation");
                    }
                    final RpcHeaderProtos.RpcSaslProto saslMessage = saslPacket.getValue(RpcHeaderProtos.RpcSaslProto.getDefaultInstance());
                    if (saslPacket.remaining() > 0) {
                        throw new SaslException("Received malformed response length");
                    }
                    RpcHeaderProtos.RpcSaslProto.Builder response = null;
                    switch (saslMessage.getState()) {
                        case NEGOTIATE: {
                            RpcHeaderProtos.RpcSaslProto.SaslAuth saslAuthType = this.selectSaslClient(saslMessage.getAuthsList());
                            this.authMethod = SaslRpcServer.AuthMethod.valueOf(saslAuthType.getMethod());
                            byte[] responseToken = null;
                            if (this.authMethod == SaslRpcServer.AuthMethod.SIMPLE) {
                                done = true;
                            }
                            else {
                                byte[] challengeToken = null;
                                if (saslAuthType.hasChallenge()) {
                                    challengeToken = saslAuthType.getChallenge().toByteArray();
                                    saslAuthType = RpcHeaderProtos.RpcSaslProto.SaslAuth.newBuilder(saslAuthType).clearChallenge().build();
                                }
                                else if (this.saslClient.hasInitialResponse()) {
                                    challengeToken = new byte[0];
                                }
                                responseToken = ((challengeToken != null) ? this.saslClient.evaluateChallenge(challengeToken) : new byte[0]);
                            }
                            response = this.createSaslReply(RpcHeaderProtos.RpcSaslProto.SaslState.INITIATE, responseToken);
                            response.addAuths(saslAuthType);
                            break;
                        }
                        case CHALLENGE: {
                            if (this.saslClient == null) {
                                throw new SaslException("Server sent unsolicited challenge");
                            }
                            final byte[] responseToken2 = this.saslEvaluateToken(saslMessage, false);
                            response = this.createSaslReply(RpcHeaderProtos.RpcSaslProto.SaslState.RESPONSE, responseToken2);
                            break;
                        }
                        case SUCCESS: {
                            if (this.saslClient == null) {
                                this.authMethod = SaslRpcServer.AuthMethod.SIMPLE;
                            }
                            else {
                                this.saslEvaluateToken(saslMessage, true);
                            }
                            done = true;
                            break;
                        }
                        default: {
                            throw new SaslException("RPC client doesn't support SASL " + saslMessage.getState());
                        }
                    }
                    if (response != null) {
                        this.sendSaslMessage(ipcStreams.out, response.build());
                        continue;
                    }
                    continue;
                }
            }
        } while (!done);
        return this.authMethod;
    }
    
    private void sendSaslMessage(final OutputStream out, final RpcHeaderProtos.RpcSaslProto message) throws IOException {
        if (SaslRpcClient.LOG.isDebugEnabled()) {
            SaslRpcClient.LOG.debug("Sending sasl message " + message);
        }
        final ResponseBuffer buf = new ResponseBuffer();
        SaslRpcClient.saslHeader.writeDelimitedTo(buf);
        message.writeDelimitedTo(buf);
        synchronized (out) {
            buf.writeTo(out);
            out.flush();
        }
    }
    
    private byte[] saslEvaluateToken(final RpcHeaderProtos.RpcSaslProto saslResponse, final boolean serverIsDone) throws SaslException {
        byte[] saslToken = null;
        if (saslResponse.hasToken()) {
            saslToken = saslResponse.getToken().toByteArray();
            saslToken = this.saslClient.evaluateChallenge(saslToken);
        }
        else if (!serverIsDone) {
            throw new SaslException("Server challenge contains no token");
        }
        if (serverIsDone) {
            if (!this.saslClient.isComplete()) {
                throw new SaslException("Client is out of sync with server");
            }
            if (saslToken != null) {
                throw new SaslException("Client generated spurious response");
            }
        }
        return saslToken;
    }
    
    private RpcHeaderProtos.RpcSaslProto.Builder createSaslReply(final RpcHeaderProtos.RpcSaslProto.SaslState state, final byte[] responseToken) {
        final RpcHeaderProtos.RpcSaslProto.Builder response = RpcHeaderProtos.RpcSaslProto.newBuilder();
        response.setState(state);
        if (responseToken != null) {
            response.setToken(ByteString.copyFrom(responseToken));
        }
        return response;
    }
    
    private boolean useWrap() {
        final String qop = (String)this.saslClient.getNegotiatedProperty("javax.security.sasl.qop");
        return qop != null && !"auth".toLowerCase(Locale.ENGLISH).equals(qop);
    }
    
    public InputStream getInputStream(InputStream in) throws IOException {
        if (this.useWrap()) {
            in = new WrappedInputStream(in);
        }
        return in;
    }
    
    public OutputStream getOutputStream(OutputStream out) throws IOException {
        if (this.useWrap()) {
            final String maxBuf = (String)this.saslClient.getNegotiatedProperty("javax.security.sasl.rawsendsize");
            out = new BufferedOutputStream(new WrappedOutputStream(out), Integer.parseInt(maxBuf));
        }
        return out;
    }
    
    public void dispose() throws SaslException {
        if (this.saslClient != null) {
            this.saslClient.dispose();
            this.saslClient = null;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(SaslRpcClient.class);
        saslHeader = ProtoUtil.makeRpcRequestHeader(RPC.RpcKind.RPC_PROTOCOL_BUFFER, RpcHeaderProtos.RpcRequestHeaderProto.OperationProto.RPC_FINAL_PACKET, Server.AuthProtocol.SASL.callId, -1, RpcConstants.DUMMY_CLIENT_ID);
        negotiateRequest = RpcHeaderProtos.RpcSaslProto.newBuilder().setState(RpcHeaderProtos.RpcSaslProto.SaslState.NEGOTIATE).build();
    }
    
    class WrappedInputStream extends FilterInputStream
    {
        private ByteBuffer unwrappedRpcBuffer;
        
        public WrappedInputStream(final InputStream in) throws IOException {
            super(in);
            this.unwrappedRpcBuffer = ByteBuffer.allocate(0);
        }
        
        @Override
        public int read() throws IOException {
            final byte[] b = { 0 };
            final int n = this.read(b, 0, 1);
            return (n != -1) ? b[0] : -1;
        }
        
        @Override
        public int read(final byte[] b) throws IOException {
            return this.read(b, 0, b.length);
        }
        
        @Override
        public synchronized int read(final byte[] buf, final int off, final int len) throws IOException {
            if (len == 0) {
                return 0;
            }
            if (this.unwrappedRpcBuffer.remaining() == 0) {
                this.readNextRpcPacket();
            }
            final int readLen = Math.min(len, this.unwrappedRpcBuffer.remaining());
            this.unwrappedRpcBuffer.get(buf, off, readLen);
            return readLen;
        }
        
        private void readNextRpcPacket() throws IOException {
            SaslRpcClient.LOG.debug("reading next wrapped RPC packet");
            final DataInputStream dis = new DataInputStream(this.in);
            final int rpcLen = dis.readInt();
            final byte[] rpcBuf = new byte[rpcLen];
            dis.readFully(rpcBuf);
            final ByteArrayInputStream bis = new ByteArrayInputStream(rpcBuf);
            final RpcHeaderProtos.RpcResponseHeaderProto.Builder headerBuilder = RpcHeaderProtos.RpcResponseHeaderProto.newBuilder();
            headerBuilder.mergeDelimitedFrom(bis);
            boolean isWrapped = false;
            if (headerBuilder.getCallId() == Server.AuthProtocol.SASL.callId) {
                final RpcHeaderProtos.RpcSaslProto.Builder saslMessage = RpcHeaderProtos.RpcSaslProto.newBuilder();
                saslMessage.mergeDelimitedFrom(bis);
                if (saslMessage.getState() == RpcHeaderProtos.RpcSaslProto.SaslState.WRAP) {
                    isWrapped = true;
                    byte[] token = saslMessage.getToken().toByteArray();
                    if (SaslRpcClient.LOG.isDebugEnabled()) {
                        SaslRpcClient.LOG.debug("unwrapping token of length:" + token.length);
                    }
                    token = SaslRpcClient.this.saslClient.unwrap(token, 0, token.length);
                    this.unwrappedRpcBuffer = ByteBuffer.wrap(token);
                }
            }
            if (!isWrapped) {
                throw new SaslException("Server sent non-wrapped response");
            }
        }
    }
    
    class WrappedOutputStream extends FilterOutputStream
    {
        public WrappedOutputStream(final OutputStream out) throws IOException {
            super(out);
        }
        
        @Override
        public void write(byte[] buf, final int off, final int len) throws IOException {
            if (SaslRpcClient.LOG.isDebugEnabled()) {
                SaslRpcClient.LOG.debug("wrapping token of length:" + len);
            }
            buf = SaslRpcClient.this.saslClient.wrap(buf, off, len);
            final RpcHeaderProtos.RpcSaslProto saslMessage = RpcHeaderProtos.RpcSaslProto.newBuilder().setState(RpcHeaderProtos.RpcSaslProto.SaslState.WRAP).setToken(ByteString.copyFrom(buf, 0, buf.length)).build();
            SaslRpcClient.this.sendSaslMessage(this.out, saslMessage);
        }
    }
    
    private static class SaslClientCallbackHandler implements CallbackHandler
    {
        private final String userName;
        private final char[] userPassword;
        
        public SaslClientCallbackHandler(final Token<? extends TokenIdentifier> token) {
            this.userName = SaslRpcServer.encodeIdentifier(token.getIdentifier());
            this.userPassword = SaslRpcServer.encodePassword(token.getPassword());
        }
        
        @Override
        public void handle(final Callback[] callbacks) throws UnsupportedCallbackException {
            NameCallback nc = null;
            PasswordCallback pc = null;
            RealmCallback rc = null;
            for (final Callback callback : callbacks) {
                if (!(callback instanceof RealmChoiceCallback)) {
                    if (callback instanceof NameCallback) {
                        nc = (NameCallback)callback;
                    }
                    else if (callback instanceof PasswordCallback) {
                        pc = (PasswordCallback)callback;
                    }
                    else {
                        if (!(callback instanceof RealmCallback)) {
                            throw new UnsupportedCallbackException(callback, "Unrecognized SASL client callback");
                        }
                        rc = (RealmCallback)callback;
                    }
                }
            }
            if (nc != null) {
                if (SaslRpcClient.LOG.isDebugEnabled()) {
                    SaslRpcClient.LOG.debug("SASL client callback: setting username: " + this.userName);
                }
                nc.setName(this.userName);
            }
            if (pc != null) {
                if (SaslRpcClient.LOG.isDebugEnabled()) {
                    SaslRpcClient.LOG.debug("SASL client callback: setting userPassword");
                }
                pc.setPassword(this.userPassword);
            }
            if (rc != null) {
                if (SaslRpcClient.LOG.isDebugEnabled()) {
                    SaslRpcClient.LOG.debug("SASL client callback: setting realm: " + rc.getDefaultText());
                }
                rc.setText(rc.getDefaultText());
            }
        }
    }
}
