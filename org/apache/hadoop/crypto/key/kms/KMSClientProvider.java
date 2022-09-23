// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key.kms;

import java.util.Date;
import com.google.common.base.Strings;
import org.apache.hadoop.crypto.key.KeyProviderFactory;
import org.apache.hadoop.security.token.TokenRenewer;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSelector;
import java.util.Collection;
import java.util.Queue;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenIdentifier;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.Credentials;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.concurrent.ExecutionException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.util.KMSUtil;
import java.io.InputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.util.HttpExceptionUtils;
import java.io.Closeable;
import org.apache.hadoop.io.IOUtils;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.security.PrivilegedExceptionAction;
import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import java.net.URLEncoder;
import java.util.Map;
import java.net.MalformedURLException;
import org.apache.hadoop.security.ProviderUtils;
import org.apache.hadoop.fs.Path;
import java.security.GeneralSecurityException;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import java.io.IOException;
import java.io.Writer;
import org.apache.hadoop.util.JsonSerialization;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import org.apache.hadoop.security.token.delegation.web.DelegationTokenAuthenticatedURL;
import org.apache.hadoop.security.authentication.client.ConnectionConfigurator;
import org.apache.hadoop.security.ssl.SSLFactory;
import java.net.URL;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.crypto.key.KeyProviderDelegationTokenExtension;
import org.apache.hadoop.crypto.key.KeyProviderCryptoExtension;
import org.apache.hadoop.crypto.key.KeyProvider;

@InterfaceAudience.Private
public class KMSClientProvider extends KeyProvider implements KeyProviderCryptoExtension.CryptoExtension, KeyProviderDelegationTokenExtension.DelegationTokenExtension
{
    static final Logger LOG;
    private static final String INVALID_SIGNATURE = "Invalid signature";
    private static final String ANONYMOUS_REQUESTS_DISALLOWED = "Anonymous requests are disallowed";
    public static final String TOKEN_KIND_STR = "kms-dt";
    public static final Text TOKEN_KIND;
    public static final String SCHEME_NAME = "kms";
    private static final String UTF8 = "UTF-8";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON_MIME = "application/json";
    private static final String HTTP_GET = "GET";
    private static final String HTTP_POST = "POST";
    private static final String HTTP_PUT = "PUT";
    private static final String HTTP_DELETE = "DELETE";
    private static final String CONFIG_PREFIX = "hadoop.security.kms.client.";
    public static final String AUTH_RETRY = "hadoop.security.kms.client.authentication.retry-count";
    public static final int DEFAULT_AUTH_RETRY = 1;
    private final ValueQueue<KeyProviderCryptoExtension.EncryptedKeyVersion> encKeyVersionQueue;
    private KeyProviderDelegationTokenExtension.DelegationTokenExtension clientTokenProvider;
    private final Text dtService;
    private final Text canonicalService;
    private URL kmsUrl;
    private SSLFactory sslFactory;
    private ConnectionConfigurator configurator;
    private DelegationTokenAuthenticatedURL.Token authToken;
    private final int authRetry;
    
    private static void writeJson(final Object obj, final OutputStream os) throws IOException {
        final Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
        JsonSerialization.writer().writeValue(writer, obj);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KMSClientProvider[");
        sb.append(this.kmsUrl).append("]");
        return sb.toString();
    }
    
    public KMSClientProvider(final URI uri, final Configuration conf) throws IOException {
        super(conf);
        this.clientTokenProvider = this;
        this.kmsUrl = createServiceURL(extractKMSPath(uri));
        this.dtService = getDtService(uri);
        final URI serviceUri = URI.create(this.kmsUrl.toString());
        this.canonicalService = SecurityUtil.buildTokenService(serviceUri);
        if ("https".equalsIgnoreCase(this.kmsUrl.getProtocol())) {
            this.sslFactory = new SSLFactory(SSLFactory.Mode.CLIENT, conf);
            try {
                this.sslFactory.init();
            }
            catch (GeneralSecurityException ex) {
                throw new IOException(ex);
            }
        }
        final int timeout = conf.getInt("hadoop.security.kms.client.timeout", 60);
        this.authRetry = conf.getInt("hadoop.security.kms.client.authentication.retry-count", 1);
        this.configurator = new TimeoutConnConfigurator(timeout, this.sslFactory);
        this.encKeyVersionQueue = new ValueQueue<KeyProviderCryptoExtension.EncryptedKeyVersion>(conf.getInt("hadoop.security.kms.client.encrypted.key.cache.size", 500), conf.getFloat("hadoop.security.kms.client.encrypted.key.cache.low-watermark", 0.3f), conf.getInt("hadoop.security.kms.client.encrypted.key.cache.expiry", 43200000), conf.getInt("hadoop.security.kms.client.encrypted.key.cache.num.refill.threads", 2), new EncryptedQueueRefiller());
        this.authToken = new DelegationTokenAuthenticatedURL.Token();
        KMSClientProvider.LOG.debug("KMSClientProvider created for KMS url: {} delegation token service: {} canonical service: {}.", this.kmsUrl, this.dtService, this.canonicalService);
    }
    
    protected static Text getDtService(final URI uri) {
        final String fragment = uri.getFragment();
        Text service;
        if (fragment != null) {
            service = new Text(uri.getScheme() + ":" + uri.getSchemeSpecificPart());
        }
        else {
            service = new Text(uri.toString());
        }
        return service;
    }
    
    private static Path extractKMSPath(final URI uri) throws MalformedURLException, IOException {
        return ProviderUtils.unnestUri(uri);
    }
    
    private static URL createServiceURL(final Path path) throws IOException {
        String str = new URL(path.toString()).toExternalForm();
        if (str.endsWith("/")) {
            str = str.substring(0, str.length() - 1);
        }
        return new URL(str + "/v1" + "/");
    }
    
    private URL createURL(final String collection, final String resource, final String subResource, final Map<String, ?> parameters) throws IOException {
        try {
            final StringBuilder sb = new StringBuilder();
            sb.append(this.kmsUrl);
            if (collection != null) {
                sb.append(collection);
                if (resource != null) {
                    sb.append("/").append(URLEncoder.encode(resource, "UTF-8"));
                    if (subResource != null) {
                        sb.append("/").append(subResource);
                    }
                }
            }
            final URIBuilder uriBuilder = new URIBuilder(sb.toString());
            if (parameters != null) {
                for (final Map.Entry<String, ?> param : parameters.entrySet()) {
                    final Object value = param.getValue();
                    if (value instanceof String) {
                        uriBuilder.addParameter(param.getKey(), (String)value);
                    }
                    else {
                        for (final String s : (String[])value) {
                            uriBuilder.addParameter(param.getKey(), s);
                        }
                    }
                }
            }
            return uriBuilder.build().toURL();
        }
        catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
    }
    
    private HttpURLConnection configureConnection(final HttpURLConnection conn) throws IOException {
        if (this.sslFactory != null) {
            final HttpsURLConnection httpsConn = (HttpsURLConnection)conn;
            try {
                httpsConn.setSSLSocketFactory(this.sslFactory.createSSLSocketFactory());
            }
            catch (GeneralSecurityException ex) {
                throw new IOException(ex);
            }
            httpsConn.setHostnameVerifier(this.sslFactory.getHostnameVerifier());
        }
        return conn;
    }
    
    private HttpURLConnection createConnection(final URL url, final String method) throws IOException {
        HttpURLConnection conn;
        try {
            final String doAsUser = this.getDoAsUser();
            conn = this.getActualUgi().doAs((PrivilegedExceptionAction<HttpURLConnection>)new PrivilegedExceptionAction<HttpURLConnection>() {
                @Override
                public HttpURLConnection run() throws Exception {
                    final DelegationTokenAuthenticatedURL authUrl = KMSClientProvider.this.createAuthenticatedURL();
                    return authUrl.openConnection(url, KMSClientProvider.this.authToken, doAsUser);
                }
            });
        }
        catch (ConnectException ex) {
            final String msg = "Failed to connect to: " + url.toString();
            KMSClientProvider.LOG.warn(msg);
            throw new IOException(msg, ex);
        }
        catch (SocketTimeoutException ex2) {
            KMSClientProvider.LOG.warn("Failed to connect to {}:{}", url.getHost(), url.getPort());
            throw ex2;
        }
        catch (IOException ex3) {
            throw ex3;
        }
        catch (UndeclaredThrowableException ex4) {
            throw new IOException(ex4.getUndeclaredThrowable());
        }
        catch (Exception ex5) {
            throw new IOException(ex5);
        }
        conn.setUseCaches(false);
        conn.setRequestMethod(method);
        if (method.equals("POST") || method.equals("PUT")) {
            conn.setDoOutput(true);
        }
        conn = this.configureConnection(conn);
        return conn;
    }
    
    private <T> T call(final HttpURLConnection conn, final Object jsonOutput, final int expectedResponse, final Class<T> klass) throws IOException {
        return this.call(conn, jsonOutput, expectedResponse, klass, this.authRetry);
    }
    
    private <T> T call(HttpURLConnection conn, final Object jsonOutput, final int expectedResponse, final Class<T> klass, final int authRetryCount) throws IOException {
        T ret = null;
        OutputStream os = null;
        try {
            if (jsonOutput != null) {
                os = conn.getOutputStream();
                writeJson(jsonOutput, os);
            }
        }
        catch (IOException ex) {
            if (os == null) {
                conn.disconnect();
            }
            else {
                IOUtils.closeStream(conn.getInputStream());
            }
            throw ex;
        }
        if ((conn.getResponseCode() == 403 && (conn.getResponseMessage().equals("Anonymous requests are disallowed") || conn.getResponseMessage().contains("Invalid signature"))) || conn.getResponseCode() == 401) {
            if (KMSClientProvider.LOG.isDebugEnabled()) {
                KMSClientProvider.LOG.debug("Response={}({}), resetting authToken", (Object)conn.getResponseCode(), conn.getResponseMessage());
            }
            this.authToken = new DelegationTokenAuthenticatedURL.Token();
            if (authRetryCount > 0) {
                final String contentType = conn.getRequestProperty("Content-Type");
                final String requestMethod = conn.getRequestMethod();
                final URL url = conn.getURL();
                conn = this.createConnection(url, requestMethod);
                if (contentType != null && !contentType.isEmpty()) {
                    conn.setRequestProperty("Content-Type", contentType);
                }
                return (T)this.call(conn, jsonOutput, expectedResponse, (Class<Object>)klass, authRetryCount - 1);
            }
        }
        HttpExceptionUtils.validateResponse(conn, expectedResponse);
        if (conn.getContentType() != null && conn.getContentType().trim().toLowerCase().startsWith("application/json") && klass != null) {
            final ObjectMapper mapper = new ObjectMapper();
            InputStream is = null;
            try {
                is = conn.getInputStream();
                ret = mapper.readValue(is, klass);
            }
            finally {
                IOUtils.closeStream(is);
            }
        }
        return ret;
    }
    
    @Override
    public KeyVersion getKeyVersion(final String versionName) throws IOException {
        KMSUtil.checkNotEmpty(versionName, "versionName");
        final URL url = this.createURL("keyversion", versionName, null, null);
        final HttpURLConnection conn = this.createConnection(url, "GET");
        final Map response = this.call(conn, null, 200, Map.class);
        return KMSUtil.parseJSONKeyVersion(response);
    }
    
    @Override
    public KeyVersion getCurrentKey(final String name) throws IOException {
        KMSUtil.checkNotEmpty(name, "name");
        final URL url = this.createURL("key", name, "_currentversion", null);
        final HttpURLConnection conn = this.createConnection(url, "GET");
        final Map response = this.call(conn, null, 200, Map.class);
        return KMSUtil.parseJSONKeyVersion(response);
    }
    
    @Override
    public List<String> getKeys() throws IOException {
        final URL url = this.createURL("keys/names", null, null, null);
        final HttpURLConnection conn = this.createConnection(url, "GET");
        final List response = this.call(conn, null, 200, List.class);
        return (List<String>)response;
    }
    
    private List<String[]> createKeySets(final String[] keyNames) {
        final List<String[]> list = new ArrayList<String[]>();
        List<String> batch = new ArrayList<String>();
        int batchLen = 0;
        for (final String name : keyNames) {
            final int additionalLen = "key".length() + 1 + name.length();
            batchLen += additionalLen;
            if (batchLen > 1500) {
                list.add(batch.toArray(new String[batch.size()]));
                batch = new ArrayList<String>();
                batchLen = additionalLen;
            }
            batch.add(name);
        }
        if (!batch.isEmpty()) {
            list.add(batch.toArray(new String[batch.size()]));
        }
        return list;
    }
    
    @Override
    public Metadata[] getKeysMetadata(final String... keyNames) throws IOException {
        final List<Metadata> keysMetadata = new ArrayList<Metadata>();
        final List<String[]> keySets = this.createKeySets(keyNames);
        for (final String[] keySet : keySets) {
            if (keyNames.length > 0) {
                final Map<String, Object> queryStr = new HashMap<String, Object>();
                queryStr.put("key", keySet);
                final URL url = this.createURL("keys/metadata", null, null, queryStr);
                final HttpURLConnection conn = this.createConnection(url, "GET");
                final List<Map> list = this.call(conn, null, 200, (Class<List<Map>>)List.class);
                for (final Map map : list) {
                    keysMetadata.add(KMSUtil.parseJSONMetadata(map));
                }
            }
        }
        return keysMetadata.toArray(new Metadata[keysMetadata.size()]);
    }
    
    private KeyVersion createKeyInternal(final String name, final byte[] material, final Options options) throws NoSuchAlgorithmException, IOException {
        KMSUtil.checkNotEmpty(name, "name");
        KMSUtil.checkNotNull(options, "options");
        final Map<String, Object> jsonKey = new HashMap<String, Object>();
        jsonKey.put("name", name);
        jsonKey.put("cipher", options.getCipher());
        jsonKey.put("length", options.getBitLength());
        if (material != null) {
            jsonKey.put("material", Base64.encodeBase64String(material));
        }
        if (options.getDescription() != null) {
            jsonKey.put("description", options.getDescription());
        }
        if (options.getAttributes() != null && !options.getAttributes().isEmpty()) {
            jsonKey.put("attributes", options.getAttributes());
        }
        final URL url = this.createURL("keys", null, null, null);
        final HttpURLConnection conn = this.createConnection(url, "POST");
        conn.setRequestProperty("Content-Type", "application/json");
        final Map response = this.call(conn, jsonKey, 201, Map.class);
        return KMSUtil.parseJSONKeyVersion(response);
    }
    
    @Override
    public KeyVersion createKey(final String name, final Options options) throws NoSuchAlgorithmException, IOException {
        return this.createKeyInternal(name, null, options);
    }
    
    @Override
    public KeyVersion createKey(final String name, final byte[] material, final Options options) throws IOException {
        KMSUtil.checkNotNull(material, "material");
        try {
            return this.createKeyInternal(name, material, options);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("It should not happen", ex);
        }
    }
    
    @Override
    public void invalidateCache(final String name) throws IOException {
        KMSUtil.checkNotEmpty(name, "name");
        final URL url = this.createURL("key", name, "_invalidatecache", null);
        final HttpURLConnection conn = this.createConnection(url, "POST");
        this.call(conn, null, 200, (Class<Object>)null);
        this.drain(name);
    }
    
    private KeyVersion rollNewVersionInternal(final String name, final byte[] material) throws NoSuchAlgorithmException, IOException {
        KMSUtil.checkNotEmpty(name, "name");
        final Map<String, String> jsonMaterial = new HashMap<String, String>();
        if (material != null) {
            jsonMaterial.put("material", Base64.encodeBase64String(material));
        }
        final URL url = this.createURL("key", name, null, null);
        final HttpURLConnection conn = this.createConnection(url, "POST");
        conn.setRequestProperty("Content-Type", "application/json");
        final Map response = this.call(conn, jsonMaterial, 200, Map.class);
        final KeyVersion keyVersion = KMSUtil.parseJSONKeyVersion(response);
        this.invalidateCache(name);
        return keyVersion;
    }
    
    @Override
    public KeyVersion rollNewVersion(final String name) throws NoSuchAlgorithmException, IOException {
        return this.rollNewVersionInternal(name, null);
    }
    
    @Override
    public KeyVersion rollNewVersion(final String name, final byte[] material) throws IOException {
        KMSUtil.checkNotNull(material, "material");
        try {
            return this.rollNewVersionInternal(name, material);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("It should not happen", ex);
        }
    }
    
    @Override
    public KeyProviderCryptoExtension.EncryptedKeyVersion generateEncryptedKey(final String encryptionKeyName) throws IOException, GeneralSecurityException {
        try {
            return this.encKeyVersionQueue.getNext(encryptionKeyName);
        }
        catch (ExecutionException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                throw (SocketTimeoutException)e.getCause();
            }
            throw new IOException(e);
        }
    }
    
    @Override
    public KeyVersion decryptEncryptedKey(final KeyProviderCryptoExtension.EncryptedKeyVersion encryptedKeyVersion) throws IOException, GeneralSecurityException {
        KMSUtil.checkNotNull(encryptedKeyVersion.getEncryptionKeyVersionName(), "versionName");
        KMSUtil.checkNotNull(encryptedKeyVersion.getEncryptedKeyIv(), "iv");
        Preconditions.checkArgument(encryptedKeyVersion.getEncryptedKeyVersion().getVersionName().equals("EEK"), "encryptedKey version name must be '%s', is '%s'", "EEK", encryptedKeyVersion.getEncryptedKeyVersion().getVersionName());
        KMSUtil.checkNotNull(encryptedKeyVersion.getEncryptedKeyVersion(), "encryptedKey");
        final Map<String, String> params = new HashMap<String, String>();
        params.put("eek_op", "decrypt");
        final Map<String, Object> jsonPayload = new HashMap<String, Object>();
        jsonPayload.put("name", encryptedKeyVersion.getEncryptionKeyName());
        jsonPayload.put("iv", Base64.encodeBase64String(encryptedKeyVersion.getEncryptedKeyIv()));
        jsonPayload.put("material", Base64.encodeBase64String(encryptedKeyVersion.getEncryptedKeyVersion().getMaterial()));
        final URL url = this.createURL("keyversion", encryptedKeyVersion.getEncryptionKeyVersionName(), "_eek", params);
        final HttpURLConnection conn = this.createConnection(url, "POST");
        conn.setRequestProperty("Content-Type", "application/json");
        final Map response = this.call(conn, jsonPayload, 200, Map.class);
        return KMSUtil.parseJSONKeyVersion(response);
    }
    
    @Override
    public KeyProviderCryptoExtension.EncryptedKeyVersion reencryptEncryptedKey(final KeyProviderCryptoExtension.EncryptedKeyVersion ekv) throws IOException, GeneralSecurityException {
        KMSUtil.checkNotNull(ekv.getEncryptionKeyVersionName(), "versionName");
        KMSUtil.checkNotNull(ekv.getEncryptedKeyIv(), "iv");
        KMSUtil.checkNotNull(ekv.getEncryptedKeyVersion(), "encryptedKey");
        Preconditions.checkArgument(ekv.getEncryptedKeyVersion().getVersionName().equals("EEK"), "encryptedKey version name must be '%s', is '%s'", "EEK", ekv.getEncryptedKeyVersion().getVersionName());
        final Map<String, String> params = new HashMap<String, String>();
        params.put("eek_op", "reencrypt");
        final Map<String, Object> jsonPayload = new HashMap<String, Object>();
        jsonPayload.put("name", ekv.getEncryptionKeyName());
        jsonPayload.put("iv", Base64.encodeBase64String(ekv.getEncryptedKeyIv()));
        jsonPayload.put("material", Base64.encodeBase64String(ekv.getEncryptedKeyVersion().getMaterial()));
        final URL url = this.createURL("keyversion", ekv.getEncryptionKeyVersionName(), "_eek", params);
        final HttpURLConnection conn = this.createConnection(url, "POST");
        conn.setRequestProperty("Content-Type", "application/json");
        final Map response = this.call(conn, jsonPayload, 200, Map.class);
        return KMSUtil.parseJSONEncKeyVersion(ekv.getEncryptionKeyName(), response);
    }
    
    @Override
    public void reencryptEncryptedKeys(final List<KeyProviderCryptoExtension.EncryptedKeyVersion> ekvs) throws IOException, GeneralSecurityException {
        KMSUtil.checkNotNull(ekvs, "ekvs");
        if (ekvs.isEmpty()) {
            return;
        }
        final List<Map> jsonPayload = new ArrayList<Map>();
        String keyName = null;
        for (final KeyProviderCryptoExtension.EncryptedKeyVersion ekv : ekvs) {
            KMSUtil.checkNotNull(ekv.getEncryptionKeyName(), "keyName");
            KMSUtil.checkNotNull(ekv.getEncryptionKeyVersionName(), "versionName");
            KMSUtil.checkNotNull(ekv.getEncryptedKeyIv(), "iv");
            KMSUtil.checkNotNull(ekv.getEncryptedKeyVersion(), "encryptedKey");
            Preconditions.checkArgument(ekv.getEncryptedKeyVersion().getVersionName().equals("EEK"), "encryptedKey version name must be '%s', is '%s'", "EEK", ekv.getEncryptedKeyVersion().getVersionName());
            if (keyName == null) {
                keyName = ekv.getEncryptionKeyName();
            }
            else {
                Preconditions.checkArgument(keyName.equals(ekv.getEncryptionKeyName()), (Object)"All EncryptedKey must have the same key name.");
            }
            jsonPayload.add(KMSUtil.toJSON(ekv));
        }
        final URL url = this.createURL("key", keyName, "_reencryptbatch", null);
        final HttpURLConnection conn = this.createConnection(url, "POST");
        conn.setRequestProperty("Content-Type", "application/json");
        final List<Map> response = this.call(conn, jsonPayload, 200, (Class<List<Map>>)List.class);
        Preconditions.checkArgument(response.size() == ekvs.size(), (Object)"Response size is different than input size.");
        for (int i = 0; i < response.size(); ++i) {
            final Map item = response.get(i);
            final KeyProviderCryptoExtension.EncryptedKeyVersion ekv2 = KMSUtil.parseJSONEncKeyVersion(keyName, item);
            ekvs.set(i, ekv2);
        }
    }
    
    @Override
    public List<KeyVersion> getKeyVersions(final String name) throws IOException {
        KMSUtil.checkNotEmpty(name, "name");
        final URL url = this.createURL("key", name, "_versions", null);
        final HttpURLConnection conn = this.createConnection(url, "GET");
        final List response = this.call(conn, null, 200, List.class);
        List<KeyVersion> versions = null;
        if (!response.isEmpty()) {
            versions = new ArrayList<KeyVersion>();
            for (final Object obj : response) {
                versions.add(KMSUtil.parseJSONKeyVersion((Map)obj));
            }
        }
        return versions;
    }
    
    @Override
    public Metadata getMetadata(final String name) throws IOException {
        KMSUtil.checkNotEmpty(name, "name");
        final URL url = this.createURL("key", name, "_metadata", null);
        final HttpURLConnection conn = this.createConnection(url, "GET");
        final Map response = this.call(conn, null, 200, Map.class);
        return KMSUtil.parseJSONMetadata(response);
    }
    
    @Override
    public void deleteKey(final String name) throws IOException {
        KMSUtil.checkNotEmpty(name, "name");
        final URL url = this.createURL("key", name, null, null);
        final HttpURLConnection conn = this.createConnection(url, "DELETE");
        this.call(conn, null, 200, (Class<Object>)null);
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    @Override
    public void warmUpEncryptedKeys(final String... keyNames) throws IOException {
        try {
            this.encKeyVersionQueue.initializeQueuesForKeys(keyNames);
        }
        catch (ExecutionException e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public void drain(final String keyName) {
        this.encKeyVersionQueue.drain(keyName);
    }
    
    @VisibleForTesting
    public int getEncKeyQueueSize(final String keyName) {
        return this.encKeyVersionQueue.getSize(keyName);
    }
    
    protected void setClientTokenProvider(final KeyProviderDelegationTokenExtension.DelegationTokenExtension provider) {
        this.clientTokenProvider = provider;
    }
    
    @VisibleForTesting
    DelegationTokenAuthenticatedURL createAuthenticatedURL() {
        return new DelegationTokenAuthenticatedURL(this.configurator) {
            @Override
            public org.apache.hadoop.security.token.Token<? extends TokenIdentifier> selectDelegationToken(final URL url, final Credentials creds) {
                if (KMSClientProvider.LOG.isDebugEnabled()) {
                    KMSClientProvider.LOG.debug("Looking for delegation token. creds: {}", creds.getAllTokens());
                }
                return (org.apache.hadoop.security.token.Token<? extends TokenIdentifier>)KMSClientProvider.this.clientTokenProvider.selectDelegationToken(creds);
            }
        };
    }
    
    @InterfaceAudience.Private
    @Override
    public Token<?> selectDelegationToken(final Credentials creds) {
        Token<?> token = selectDelegationToken(creds, this.dtService);
        if (token == null) {
            token = selectDelegationToken(creds, this.canonicalService);
        }
        return token;
    }
    
    protected static Token<?> selectDelegationToken(final Credentials creds, final Text service) {
        Token<?> token = creds.getToken(service);
        KMSClientProvider.LOG.debug("selected by alias={} token={}", service, token);
        if (token != null && KMSClientProvider.TOKEN_KIND.equals(token.getKind())) {
            return token;
        }
        token = TokenSelector.INSTANCE.selectToken(service, creds.getAllTokens());
        KMSClientProvider.LOG.debug("selected by service={} token={}", service, token);
        return token;
    }
    
    @Override
    public String getCanonicalServiceName() {
        return this.canonicalService.toString();
    }
    
    @Override
    public Token<?> getDelegationToken(final String renewer) throws IOException {
        final URL url = this.createURL(null, null, null, null);
        final DelegationTokenAuthenticatedURL authUrl = new DelegationTokenAuthenticatedURL(this.configurator);
        Token<?> token = null;
        try {
            final String doAsUser = this.getDoAsUser();
            token = this.getActualUgi().doAs((PrivilegedExceptionAction<Token<?>>)new PrivilegedExceptionAction<Token<?>>() {
                @Override
                public Token<?> run() throws Exception {
                    KMSClientProvider.LOG.debug("Getting new token from {}, renewer:{}", url, renewer);
                    return authUrl.getDelegationToken(url, new DelegationTokenAuthenticatedURL.Token(), renewer, doAsUser);
                }
            });
            if (token == null) {
                throw new IOException("Got NULL as delegation token");
            }
            token.setService(this.dtService);
            KMSClientProvider.LOG.info("New token created: ({})", token);
        }
        catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException)e;
            }
            throw new IOException(e);
        }
        return token;
    }
    
    @Override
    public long renewDelegationToken(final Token<?> dToken) throws IOException {
        try {
            final String doAsUser = this.getDoAsUser();
            final DelegationTokenAuthenticatedURL.Token token = this.generateDelegationToken(dToken);
            final URL url = this.createURL(null, null, null, null);
            KMSClientProvider.LOG.debug("Renewing delegation token {} with url:{}, as:{}", token, url, doAsUser);
            final DelegationTokenAuthenticatedURL authUrl = this.createAuthenticatedURL();
            return this.getActualUgi().doAs((PrivilegedExceptionAction<Long>)new PrivilegedExceptionAction<Long>() {
                @Override
                public Long run() throws Exception {
                    return authUrl.renewDelegationToken(url, token, doAsUser);
                }
            });
        }
        catch (Exception ex) {
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new IOException(ex);
        }
    }
    
    @Override
    public Void cancelDelegationToken(final Token<?> dToken) throws IOException {
        try {
            final String doAsUser = this.getDoAsUser();
            final DelegationTokenAuthenticatedURL.Token token = this.generateDelegationToken(dToken);
            return this.getActualUgi().doAs((PrivilegedExceptionAction<Void>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    final URL url = KMSClientProvider.this.createURL(null, null, null, null);
                    KMSClientProvider.LOG.debug("Cancelling delegation token {} with url:{}, as:{}", dToken, url, doAsUser);
                    final DelegationTokenAuthenticatedURL authUrl = KMSClientProvider.this.createAuthenticatedURL();
                    authUrl.cancelDelegationToken(url, token, doAsUser);
                    return null;
                }
            });
        }
        catch (Exception ex) {
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new IOException(ex);
        }
    }
    
    private String getDoAsUser() throws IOException {
        final UserGroupInformation currentUgi = UserGroupInformation.getCurrentUser();
        return (currentUgi.getAuthenticationMethod() == UserGroupInformation.AuthenticationMethod.PROXY) ? currentUgi.getShortUserName() : null;
    }
    
    private DelegationTokenAuthenticatedURL.Token generateDelegationToken(final Token<?> dToken) {
        final DelegationTokenAuthenticatedURL.Token token = new DelegationTokenAuthenticatedURL.Token();
        final Token<AbstractDelegationTokenIdentifier> dt = new Token<AbstractDelegationTokenIdentifier>(dToken.getIdentifier(), dToken.getPassword(), dToken.getKind(), dToken.getService());
        token.setDelegationToken(dt);
        return token;
    }
    
    private boolean containsKmsDt(final UserGroupInformation ugi) throws IOException {
        final Credentials creds = ugi.getCredentials();
        if (!creds.getAllTokens().isEmpty()) {
            KMSClientProvider.LOG.debug("Searching for KMS delegation token in user {}'s credentials", ugi);
            return this.clientTokenProvider.selectDelegationToken(creds) != null;
        }
        return false;
    }
    
    @VisibleForTesting
    UserGroupInformation getActualUgi() throws IOException {
        final UserGroupInformation currentUgi = UserGroupInformation.getCurrentUser();
        UserGroupInformation.logAllUserInfo(KMSClientProvider.LOG, currentUgi);
        UserGroupInformation actualUgi = currentUgi;
        if (currentUgi.getRealUser() != null) {
            actualUgi = currentUgi.getRealUser();
        }
        if (UserGroupInformation.isSecurityEnabled() && !this.containsKmsDt(actualUgi) && !actualUgi.shouldRelogin()) {
            KMSClientProvider.LOG.debug("Using loginUser when Kerberos is enabled but the actual user does not have either KMS Delegation Token or Kerberos Credentials");
            actualUgi = UserGroupInformation.getLoginUser();
        }
        return actualUgi;
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.encKeyVersionQueue.shutdown();
        }
        catch (Exception e) {
            throw new IOException(e);
        }
        finally {
            if (this.sslFactory != null) {
                this.sslFactory.destroy();
                this.sslFactory = null;
            }
        }
    }
    
    @VisibleForTesting
    String getKMSUrl() {
        return this.kmsUrl.toString();
    }
    
    static {
        LOG = LoggerFactory.getLogger(KMSClientProvider.class);
        TOKEN_KIND = KMSDelegationToken.TOKEN_KIND;
    }
    
    private class EncryptedQueueRefiller implements ValueQueue.QueueRefiller<KeyProviderCryptoExtension.EncryptedKeyVersion>
    {
        @Override
        public void fillQueueForKey(final String keyName, final Queue<KeyProviderCryptoExtension.EncryptedKeyVersion> keyQueue, final int numEKVs) throws IOException {
            KMSUtil.checkNotNull(keyName, "keyName");
            final Map<String, String> params = new HashMap<String, String>();
            params.put("eek_op", "generate");
            params.put("num_keys", "" + numEKVs);
            final URL url = KMSClientProvider.this.createURL("key", keyName, "_eek", params);
            final HttpURLConnection conn = KMSClientProvider.this.createConnection(url, "GET");
            conn.setRequestProperty("Content-Type", "application/json");
            final List response = (List)KMSClientProvider.this.call(conn, null, 200, (Class<Object>)List.class);
            final List<KeyProviderCryptoExtension.EncryptedKeyVersion> ekvs = KMSUtil.parseJSONEncKeyVersions(keyName, response);
            keyQueue.addAll((Collection<?>)ekvs);
        }
    }
    
    static class TokenSelector extends AbstractDelegationTokenSelector
    {
        static final TokenSelector INSTANCE;
        
        TokenSelector() {
            super(KMSClientProvider.TOKEN_KIND);
        }
        
        static {
            INSTANCE = new TokenSelector();
        }
    }
    
    public static class KMSTokenRenewer extends TokenRenewer
    {
        private static final Logger LOG;
        
        @Override
        public boolean handleKind(final Text kind) {
            return kind.equals(KMSClientProvider.TOKEN_KIND);
        }
        
        @Override
        public boolean isManaged(final Token<?> token) throws IOException {
            return true;
        }
        
        @Override
        public long renew(final Token<?> token, final Configuration conf) throws IOException {
            KMSTokenRenewer.LOG.debug("Renewing delegation token {}", token);
            final KeyProvider keyProvider = createKeyProvider(token, conf);
            try {
                if (!(keyProvider instanceof KeyProviderDelegationTokenExtension.DelegationTokenExtension)) {
                    throw new IOException(String.format("keyProvider %s cannot renew token [%s]", (keyProvider == null) ? "null" : keyProvider.getClass(), token));
                }
                return ((KeyProviderDelegationTokenExtension.DelegationTokenExtension)keyProvider).renewDelegationToken(token);
            }
            finally {
                if (keyProvider != null) {
                    keyProvider.close();
                }
            }
        }
        
        @Override
        public void cancel(final Token<?> token, final Configuration conf) throws IOException {
            KMSTokenRenewer.LOG.debug("Canceling delegation token {}", token);
            final KeyProvider keyProvider = createKeyProvider(token, conf);
            try {
                if (!(keyProvider instanceof KeyProviderDelegationTokenExtension.DelegationTokenExtension)) {
                    throw new IOException(String.format("keyProvider %s cannot cancel token [%s]", (keyProvider == null) ? "null" : keyProvider.getClass(), token));
                }
                ((KeyProviderDelegationTokenExtension.DelegationTokenExtension)keyProvider).cancelDelegationToken(token);
            }
            finally {
                if (keyProvider != null) {
                    keyProvider.close();
                }
            }
        }
        
        private static KeyProvider createKeyProvider(final Token<?> token, final Configuration conf) throws IOException {
            final String service = token.getService().toString();
            URI uri;
            if (service != null && service.startsWith("kms:/")) {
                KMSTokenRenewer.LOG.debug("Creating key provider with token service value {}", service);
                uri = URI.create(service);
            }
            else {
                uri = KMSUtil.getKeyProviderUri(conf);
            }
            return (uri != null) ? KMSUtil.createKeyProviderFromUri(conf, uri) : null;
        }
        
        static {
            LOG = LoggerFactory.getLogger(KMSTokenRenewer.class);
        }
    }
    
    public static class KMSEncryptedKeyVersion extends KeyProviderCryptoExtension.EncryptedKeyVersion
    {
        public KMSEncryptedKeyVersion(final String keyName, final String keyVersionName, final byte[] iv, final String encryptedVersionName, final byte[] keyMaterial) {
            super(keyName, keyVersionName, iv, new KMSKeyVersion(null, encryptedVersionName, keyMaterial));
        }
    }
    
    public static class Factory extends KeyProviderFactory
    {
        @Override
        public KeyProvider createProvider(final URI providerUri, final Configuration conf) throws IOException {
            if (!"kms".equals(providerUri.getScheme())) {
                return null;
            }
            final URL origUrl = new URL(extractKMSPath(providerUri).toString());
            final String authority = origUrl.getAuthority();
            if (Strings.isNullOrEmpty(authority)) {
                throw new IOException("No valid authority in kms uri [" + origUrl + "]");
            }
            int port = -1;
            String hostsPart = authority;
            if (authority.contains(":")) {
                final String[] t = authority.split(":");
                try {
                    port = Integer.parseInt(t[1]);
                }
                catch (Exception e) {
                    throw new IOException("Could not parse port in kms uri [" + origUrl + "]");
                }
                hostsPart = t[0];
            }
            final KMSClientProvider[] providers = this.createProviders(conf, origUrl, port, hostsPart);
            return new LoadBalancingKMSClientProvider(providerUri, providers, conf);
        }
        
        private KMSClientProvider[] createProviders(final Configuration conf, final URL origUrl, final int port, final String hostsPart) throws IOException {
            final String[] hosts = hostsPart.split(";");
            final KMSClientProvider[] providers = new KMSClientProvider[hosts.length];
            for (int i = 0; i < hosts.length; ++i) {
                try {
                    providers[i] = new KMSClientProvider(new URI("kms", origUrl.getProtocol(), hosts[i], port, origUrl.getPath(), null, null), conf);
                }
                catch (URISyntaxException e) {
                    throw new IOException("Could not instantiate KMSProvider.", e);
                }
            }
            return providers;
        }
    }
    
    private static class TimeoutConnConfigurator implements ConnectionConfigurator
    {
        private ConnectionConfigurator cc;
        private int timeout;
        
        public TimeoutConnConfigurator(final int timeout, final ConnectionConfigurator cc) {
            this.timeout = timeout;
            this.cc = cc;
        }
        
        @Override
        public HttpURLConnection configure(HttpURLConnection conn) throws IOException {
            if (this.cc != null) {
                conn = this.cc.configure(conn);
            }
            conn.setConnectTimeout(this.timeout * 1000);
            conn.setReadTimeout(this.timeout * 1000);
            return conn;
        }
    }
    
    public static class KMSKeyVersion extends KeyVersion
    {
        public KMSKeyVersion(final String keyName, final String versionName, final byte[] material) {
            super(keyName, versionName, material);
        }
    }
    
    public static class KMSMetadata extends Metadata
    {
        public KMSMetadata(final String cipher, final int bitLength, final String description, final Map<String, String> attributes, final Date created, final int versions) {
            super(cipher, bitLength, description, attributes, created, versions);
        }
    }
}
