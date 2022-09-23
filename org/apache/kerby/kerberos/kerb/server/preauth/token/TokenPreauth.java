// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.preauth.token;

import org.slf4j.LoggerFactory;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.io.InputStream;
import org.apache.kerby.kerberos.kerb.common.PrivateKeyReader;
import java.io.FileNotFoundException;
import org.apache.kerby.kerberos.kerb.common.PublicKeyReader;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.base.AuthToken;
import org.apache.kerby.kerberos.kerb.provider.TokenDecoder;
import org.apache.kerby.kerberos.kerb.type.pa.token.TokenInfo;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.KrbTokenBase;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.KrbRuntime;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.type.pa.token.PaTokenRequest;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;
import org.apache.kerby.kerberos.kerb.server.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;
import org.apache.kerby.kerberos.kerb.preauth.token.TokenPreauthMeta;
import org.slf4j.Logger;
import org.apache.kerby.kerberos.kerb.server.preauth.AbstractPreauthPlugin;

public class TokenPreauth extends AbstractPreauthPlugin
{
    private static final Logger LOG;
    
    public TokenPreauth() {
        super(new TokenPreauthMeta());
    }
    
    @Override
    public boolean verify(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataEntry paData) throws KrbException {
        if (!kdcRequest.getKdcContext().getConfig().isAllowTokenPreauth()) {
            throw new KrbException(KrbErrorCode.TOKEN_PREAUTH_NOT_ALLOWED, "Token preauth is not allowed.");
        }
        if (paData.getPaDataType() != PaDataType.TOKEN_REQUEST) {
            return false;
        }
        PaTokenRequest paTokenRequest;
        if (kdcRequest.isHttps()) {
            paTokenRequest = KrbCodec.decode(paData.getPaDataValue(), PaTokenRequest.class);
        }
        else {
            final EncryptedData encData = KrbCodec.decode(paData.getPaDataValue(), EncryptedData.class);
            final EncryptionKey clientKey = kdcRequest.getArmorKey();
            kdcRequest.setClientKey(clientKey);
            paTokenRequest = EncryptionUtil.unseal(encData, clientKey, KeyUsage.PA_TOKEN, PaTokenRequest.class);
        }
        final KrbTokenBase token = paTokenRequest.getToken();
        final List<String> issuers = kdcRequest.getKdcContext().getConfig().getIssuers();
        final TokenInfo tokenInfo = paTokenRequest.getTokenInfo();
        final String issuer = tokenInfo.getTokenVendor();
        if (!issuers.contains(issuer)) {
            throw new KrbException("Unconfigured issuer: " + issuer);
        }
        final TokenDecoder tokenDecoder = KrbRuntime.getTokenProvider().createTokenDecoder();
        this.configureKeys(tokenDecoder, kdcRequest, issuer);
        AuthToken authToken = null;
        try {
            authToken = tokenDecoder.decodeFromBytes(token.getTokenValue());
            if (!tokenDecoder.isSigned() && !kdcRequest.isHttps()) {
                throw new KrbException("Token should be signed.");
            }
        }
        catch (IOException e) {
            throw new KrbException("Decoding failed", e);
        }
        if (authToken == null) {
            throw new KrbException("Token Decoding failed");
        }
        final List<String> audiences = authToken.getAudiences();
        final PrincipalName serverPrincipal = kdcRequest.getKdcReq().getReqBody().getSname();
        serverPrincipal.setRealm(kdcRequest.getKdcReq().getReqBody().getRealm());
        kdcRequest.setServerPrincipal(serverPrincipal);
        if (audiences == null || !audiences.contains(serverPrincipal.getName())) {
            throw new KrbException("The token audience does not match with the target server principal!");
        }
        kdcRequest.setToken(authToken);
        return true;
    }
    
    private void configureKeys(final TokenDecoder tokenDecoder, final KdcRequest kdcRequest, final String issuer) {
        final String verifyKeyPath = kdcRequest.getKdcContext().getConfig().getVerifyKeyConfig();
        if (verifyKeyPath != null) {
            try {
                final InputStream verifyKeyFile = this.getKeyFileStream(verifyKeyPath, issuer);
                if (verifyKeyFile != null) {
                    final PublicKey verifyKey = PublicKeyReader.loadPublicKey(verifyKeyFile);
                    tokenDecoder.setVerifyKey(verifyKey);
                }
            }
            catch (FileNotFoundException e) {
                TokenPreauth.LOG.error("The verify key path is wrong. " + e);
            }
            catch (Exception e2) {
                TokenPreauth.LOG.error("Fail to load public key. " + e2);
            }
        }
        final String decryptionKeyPath = kdcRequest.getKdcContext().getConfig().getDecryptionKeyConfig();
        if (decryptionKeyPath != null) {
            try {
                final InputStream decryptionKeyFile = this.getKeyFileStream(decryptionKeyPath, issuer);
                if (decryptionKeyFile != null) {
                    final PrivateKey decryptionKey = PrivateKeyReader.loadPrivateKey(decryptionKeyFile);
                    tokenDecoder.setDecryptionKey(decryptionKey);
                }
            }
            catch (FileNotFoundException e3) {
                TokenPreauth.LOG.error("The decryption key path is wrong. " + e3);
            }
            catch (Exception e4) {
                TokenPreauth.LOG.error("Fail to load private key. " + e4);
            }
        }
    }
    
    private InputStream getKeyFileStream(final String path, final String issuer) throws IOException {
        final File file = new File(path);
        if (file.isDirectory()) {
            final File[] listOfFiles = file.listFiles();
            File verifyKeyFile = null;
            if (listOfFiles == null) {
                throw new FileNotFoundException("The key path is incorrect");
            }
            for (int i = 0; i < listOfFiles.length; ++i) {
                if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(issuer)) {
                    verifyKeyFile = listOfFiles[i];
                    break;
                }
            }
            if (verifyKeyFile == null) {
                throw new FileNotFoundException("No key found that matches the issuer name");
            }
            return Files.newInputStream(verifyKeyFile.toPath(), new OpenOption[0]);
        }
        else {
            if (file.isFile()) {
                return Files.newInputStream(file.toPath(), new OpenOption[0]);
            }
            return this.getClass().getClassLoader().getResourceAsStream(path);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(TokenPreauth.class);
    }
}
