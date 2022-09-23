// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.jaas;

import org.slf4j.LoggerFactory;
import com.nimbusds.jwt.JWT;
import java.io.InputStream;
import org.apache.kerby.kerberos.kerb.provider.TokenEncoder;
import org.apache.kerby.kerberos.kerb.provider.TokenDecoder;
import org.apache.kerby.kerberos.kerb.client.KrbTokenClient;
import org.apache.kerby.kerberos.kerb.client.KrbClient;
import org.apache.kerby.kerberos.kerb.client.KrbConfig;
import org.apache.kerby.kerberos.kerb.type.base.TokenFormat;
import java.text.ParseException;
import org.apache.kerby.kerberos.provider.token.JwtAuthToken;
import com.nimbusds.jwt.JWTParser;
import org.apache.kerby.kerberos.kerb.KrbException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import org.apache.kerby.kerberos.kerb.common.PrivateKeyReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.kerby.kerberos.provider.token.JwtTokenEncoder;
import org.apache.kerby.kerberos.kerb.KrbRuntime;
import java.util.Iterator;
import java.security.Principal;
import org.apache.kerby.kerberos.kerb.type.kdc.EncKdcRepPart;
import java.io.IOException;
import java.net.InetAddress;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KerberosPrincipal;
import java.util.Date;
import javax.security.auth.login.LoginException;
import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import java.io.File;
import org.apache.kerby.kerberos.kerb.type.base.KrbToken;
import org.apache.kerby.kerberos.kerb.type.base.AuthToken;
import javax.security.auth.Subject;
import org.slf4j.Logger;
import javax.security.auth.spi.LoginModule;

public class TokenAuthLoginModule implements LoginModule
{
    public static final String PRINCIPAL = "principal";
    public static final String TOKEN = "token";
    public static final String TOKEN_CACHE = "tokenCache";
    public static final String ARMOR_CACHE = "armorCache";
    public static final String CREDENTIAL_CACHE = "credentialCache";
    public static final String SIGN_KEY_FILE = "signKeyFile";
    private static final Logger LOG;
    private Subject subject;
    private String tokenCacheName;
    private boolean succeeded;
    private boolean commitSucceeded;
    private String princName;
    private String tokenStr;
    private AuthToken authToken;
    private KrbToken krbToken;
    private File armorCache;
    private File cCache;
    private File signKeyFile;
    private TgtTicket tgtTicket;
    
    public TokenAuthLoginModule() {
        this.tokenCacheName = null;
        this.succeeded = false;
        this.commitSucceeded = false;
        this.princName = null;
        this.tokenStr = null;
        this.authToken = null;
        this.krbToken = null;
    }
    
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
        this.subject = subject;
        this.princName = (String)options.get("principal");
        this.tokenStr = (String)options.get("token");
        this.tokenCacheName = (String)options.get("tokenCache");
        if (options.get("armorCache") != null) {
            this.armorCache = new File((String)options.get("armorCache"));
        }
        if (options.get("credentialCache") != null) {
            this.cCache = new File((String)options.get("credentialCache"));
        }
        if (options.get("signKeyFile") != null) {
            this.signKeyFile = new File((String)options.get("signKeyFile"));
        }
    }
    
    @Override
    public boolean login() throws LoginException {
        this.validateConfiguration();
        return this.succeeded = this.tokenLogin();
    }
    
    @Override
    public boolean commit() throws LoginException {
        if (!this.succeeded) {
            return false;
        }
        KerberosTicket ticket = null;
        try {
            final EncKdcRepPart encKdcRepPart = this.tgtTicket.getEncKdcRepPart();
            final boolean[] flags = new boolean[7];
            final int flag = encKdcRepPart.getFlags().getFlags();
            for (int i = 6; i >= 0; --i) {
                flags[i] = ((flag & 1 << i) != 0x0);
            }
            Date startTime = null;
            if (encKdcRepPart.getStartTime() != null) {
                startTime = encKdcRepPart.getStartTime().getValue();
            }
            ticket = new KerberosTicket(this.tgtTicket.getTicket().encode(), new KerberosPrincipal(this.tgtTicket.getClientPrincipal().getName()), new KerberosPrincipal(this.tgtTicket.getEncKdcRepPart().getSname().getName()), encKdcRepPart.getKey().getKeyData(), encKdcRepPart.getKey().getKeyType().getValue(), flags, encKdcRepPart.getAuthTime().getValue(), startTime, encKdcRepPart.getEndTime().getValue(), encKdcRepPart.getRenewTill().getValue(), null);
        }
        catch (IOException e) {
            TokenAuthLoginModule.LOG.error("Commit Failed. " + e.toString());
        }
        this.subject.getPrivateCredentials().add(ticket);
        if (this.princName != null) {
            this.subject.getPrincipals().add(new KerberosPrincipal(this.princName));
        }
        this.commitSucceeded = true;
        TokenAuthLoginModule.LOG.info("Commit Succeeded \n");
        return true;
    }
    
    @Override
    public boolean abort() throws LoginException {
        if (!this.succeeded) {
            return false;
        }
        if (this.succeeded && !this.commitSucceeded) {
            this.succeeded = false;
        }
        else {
            this.logout();
        }
        return true;
    }
    
    @Override
    public boolean logout() throws LoginException {
        TokenAuthLoginModule.LOG.info("\t\t[TokenAuthLoginModule]: Entering logout");
        if (this.subject.isReadOnly()) {
            throw new LoginException("Subject is Readonly");
        }
        for (final Principal principal : this.subject.getPrincipals()) {
            if (principal.getName().equals(this.princName)) {
                this.subject.getPrincipals().remove(principal);
            }
        }
        final Iterator<Object> it = this.subject.getPrivateCredentials().iterator();
        while (it.hasNext()) {
            final Object o = it.next();
            if (o instanceof KrbToken) {
                it.remove();
            }
        }
        this.cleanup();
        this.succeeded = false;
        this.commitSucceeded = false;
        TokenAuthLoginModule.LOG.info("\t\t[TokenAuthLoginModule]: logged out Subject");
        return true;
    }
    
    private void validateConfiguration() throws LoginException {
        if (this.armorCache == null) {
            throw new LoginException("An armor cache must be specified via the armorCache configuration option");
        }
        if (this.cCache == null) {
            TokenAuthLoginModule.LOG.info("No credential cache was specified via 'credentialCache'. The TGT will be stored internally instead");
        }
        String error = "";
        if (this.tokenStr == null && this.tokenCacheName == null) {
            error = "useToken is specified but no token or token cache is provided";
        }
        else if (this.tokenStr != null && this.tokenCacheName != null) {
            error = "either token or token cache should be provided but not both";
        }
        if (!error.isEmpty()) {
            throw new LoginException(error);
        }
    }
    
    private boolean tokenLogin() throws LoginException {
        if (this.tokenStr == null) {
            this.tokenStr = TokenCache.readToken(this.tokenCacheName);
            if (this.tokenStr == null) {
                throw new LoginException("No valid token was found in token cache: " + this.tokenCacheName);
            }
        }
        this.krbToken = new KrbToken();
        Label_0420: {
            if (this.signKeyFile != null) {
                try {
                    final TokenDecoder tokenDecoder = KrbRuntime.getTokenProvider().createTokenDecoder();
                    try {
                        this.authToken = tokenDecoder.decodeFromString(this.tokenStr);
                    }
                    catch (IOException e) {
                        TokenAuthLoginModule.LOG.error("Token decode failed. " + e.toString());
                    }
                    final TokenEncoder tokenEncoder = KrbRuntime.getTokenProvider().createTokenEncoder();
                    if (tokenEncoder instanceof JwtTokenEncoder) {
                        PrivateKey signKey = null;
                        try (final InputStream is = Files.newInputStream(this.signKeyFile.toPath(), new OpenOption[0])) {
                            signKey = PrivateKeyReader.loadPrivateKey(is);
                        }
                        catch (IOException e8) {
                            TokenAuthLoginModule.LOG.error("Failed to load private key from file: " + this.signKeyFile.getName());
                        }
                        catch (Exception e2) {
                            TokenAuthLoginModule.LOG.error(e2.toString());
                        }
                        ((JwtTokenEncoder)tokenEncoder).setSignKey(signKey);
                    }
                    this.krbToken.setTokenValue(tokenEncoder.encodeAsBytes(this.authToken));
                    break Label_0420;
                }
                catch (KrbException e3) {
                    throw new RuntimeException("Failed to encode AuthToken", e3);
                }
            }
            this.krbToken.setTokenValue(this.tokenStr.getBytes());
            if (this.authToken == null) {
                try {
                    final JWT jwt = JWTParser.parse(this.tokenStr);
                    this.authToken = new JwtAuthToken(jwt.getJWTClaimsSet());
                }
                catch (ParseException e4) {
                    throw new RuntimeException("Failed to parse JWT token string", e4);
                }
            }
        }
        this.krbToken.setInnerToken(this.authToken);
        this.krbToken.setTokenType();
        this.krbToken.setTokenFormat(TokenFormat.JWT);
        KrbClient krbClient = null;
        try {
            final File confFile = new File(System.getProperty("java.security.krb5.conf"));
            final KrbConfig krbConfig = new KrbConfig();
            krbConfig.addKrb5Config(confFile);
            krbClient = new KrbClient(krbConfig);
            krbClient.init();
        }
        catch (KrbException | IOException ex2) {
            final Exception ex;
            final Exception e5 = ex;
            TokenAuthLoginModule.LOG.error("KrbClient init failed. " + e5.toString());
        }
        final KrbTokenClient tokenClient = new KrbTokenClient(krbClient);
        try {
            this.tgtTicket = tokenClient.requestTgt(this.krbToken, this.armorCache.getAbsolutePath());
        }
        catch (KrbException e6) {
            this.throwWith("Failed to do login with token: " + this.tokenStr, e6);
            return false;
        }
        if (this.cCache != null) {
            try {
                this.cCache = this.makeTgtCache();
            }
            catch (IOException e7) {
                TokenAuthLoginModule.LOG.error("Failed to make tgtCache. " + e7.toString());
            }
            try {
                if (krbClient != null) {
                    krbClient.storeTicket(this.tgtTicket, this.cCache);
                }
            }
            catch (KrbException e6) {
                TokenAuthLoginModule.LOG.error("Failed to store tgtTicket to " + this.cCache.getName());
            }
        }
        return true;
    }
    
    private File makeTgtCache() throws IOException {
        if (!this.cCache.exists() && !this.cCache.createNewFile()) {
            throw new IOException("Failed to create tgtcache file " + this.cCache.getAbsolutePath());
        }
        this.cCache.setExecutable(false);
        this.cCache.setReadable(true);
        this.cCache.setWritable(true);
        return this.cCache;
    }
    
    private void cleanup() {
        if (this.cCache != null && this.cCache.exists()) {
            final boolean delete = this.cCache.delete();
            if (!delete) {
                throw new RuntimeException("File delete error!");
            }
        }
    }
    
    private void throwWith(final String error, final Exception cause) throws LoginException {
        final LoginException le = new LoginException(error);
        le.initCause(cause);
        throw le;
    }
    
    static {
        LOG = LoggerFactory.getLogger(TokenAuthLoginModule.class);
    }
}
