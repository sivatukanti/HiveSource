// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.util.Date;
import org.apache.hadoop.crypto.key.kms.KMSClientProvider;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.crypto.key.KeyProviderCryptoExtension;
import org.apache.commons.codec.binary.Base64;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.crypto.key.KeyProviderFactory;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.crypto.key.KeyProvider;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class KMSUtil
{
    public static final Logger LOG;
    
    private KMSUtil() {
    }
    
    public static KeyProvider createKeyProvider(final Configuration conf, final String configKeyName) throws IOException {
        KMSUtil.LOG.debug("Creating key provider with config key {}", configKeyName);
        final URI uri = getKeyProviderUri(conf, configKeyName);
        return (uri != null) ? createKeyProviderFromUri(conf, uri) : null;
    }
    
    public static URI getKeyProviderUri(final Configuration conf) {
        return getKeyProviderUri(conf, "hadoop.security.key.provider.path");
    }
    
    public static URI getKeyProviderUri(final Configuration conf, final String configKeyName) {
        final String providerUriStr = conf.getTrimmed(configKeyName);
        if (providerUriStr == null || providerUriStr.isEmpty()) {
            return null;
        }
        return URI.create(providerUriStr);
    }
    
    public static KeyProvider createKeyProviderFromUri(final Configuration conf, final URI providerUri) throws IOException {
        final KeyProvider keyProvider = KeyProviderFactory.get(providerUri, conf);
        if (keyProvider == null) {
            throw new IOException("Could not instantiate KeyProvider for uri: " + providerUri);
        }
        if (keyProvider.isTransient()) {
            throw new IOException("KeyProvider " + keyProvider.toString() + " was found but it is a transient provider.");
        }
        return keyProvider;
    }
    
    public static Map toJSON(final KeyProvider.KeyVersion keyVersion) {
        final Map json = new HashMap();
        if (keyVersion != null) {
            json.put("name", keyVersion.getName());
            json.put("versionName", keyVersion.getVersionName());
            json.put("material", Base64.encodeBase64URLSafeString(keyVersion.getMaterial()));
        }
        return json;
    }
    
    public static Map toJSON(final KeyProviderCryptoExtension.EncryptedKeyVersion encryptedKeyVersion) {
        final Map json = new HashMap();
        if (encryptedKeyVersion != null) {
            json.put("versionName", encryptedKeyVersion.getEncryptionKeyVersionName());
            json.put("iv", Base64.encodeBase64URLSafeString(encryptedKeyVersion.getEncryptedKeyIv()));
            json.put("encryptedKeyVersion", toJSON(encryptedKeyVersion.getEncryptedKeyVersion()));
        }
        return json;
    }
    
    public static <T> T checkNotNull(final T o, final String name) throws IllegalArgumentException {
        if (o == null) {
            throw new IllegalArgumentException("Parameter '" + name + "' cannot be null");
        }
        return o;
    }
    
    public static String checkNotEmpty(final String s, final String name) throws IllegalArgumentException {
        checkNotNull(s, name);
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Parameter '" + name + "' cannot be empty");
        }
        return s;
    }
    
    public static List<KeyProviderCryptoExtension.EncryptedKeyVersion> parseJSONEncKeyVersions(final String keyName, final List valueList) {
        checkNotNull(valueList, "valueList");
        final List<KeyProviderCryptoExtension.EncryptedKeyVersion> ekvs = new ArrayList<KeyProviderCryptoExtension.EncryptedKeyVersion>(valueList.size());
        if (!valueList.isEmpty()) {
            for (final Object values : valueList) {
                final Map valueMap = (Map)values;
                ekvs.add(parseJSONEncKeyVersion(keyName, valueMap));
            }
        }
        return ekvs;
    }
    
    public static KeyProviderCryptoExtension.EncryptedKeyVersion parseJSONEncKeyVersion(final String keyName, final Map valueMap) {
        checkNotNull(valueMap, "valueMap");
        final String versionName = checkNotNull((String)valueMap.get("versionName"), "versionName");
        final byte[] iv = Base64.decodeBase64(checkNotNull((String)valueMap.get("iv"), "iv"));
        final Map encValueMap = checkNotNull((Map<K, Map<K, Map<K, Map>>>)valueMap.get("encryptedKeyVersion"), "encryptedKeyVersion");
        final String encVersionName = checkNotNull(encValueMap.get("versionName"), "versionName");
        final byte[] encKeyMaterial = Base64.decodeBase64(checkNotNull(encValueMap.get("material"), "material"));
        return new KMSClientProvider.KMSEncryptedKeyVersion(keyName, versionName, iv, encVersionName, encKeyMaterial);
    }
    
    public static KeyProvider.KeyVersion parseJSONKeyVersion(final Map valueMap) {
        checkNotNull(valueMap, "valueMap");
        KeyProvider.KeyVersion keyVersion = null;
        if (!valueMap.isEmpty()) {
            final byte[] material = (byte[])(valueMap.containsKey("material") ? Base64.decodeBase64(valueMap.get("material")) : null);
            final String versionName = valueMap.get("versionName");
            final String keyName = valueMap.get("name");
            keyVersion = new KMSClientProvider.KMSKeyVersion(keyName, versionName, material);
        }
        return keyVersion;
    }
    
    public static KeyProvider.Metadata parseJSONMetadata(final Map valueMap) {
        checkNotNull(valueMap, "valueMap");
        KeyProvider.Metadata metadata = null;
        if (!valueMap.isEmpty()) {
            metadata = new KMSClientProvider.KMSMetadata(valueMap.get("cipher"), (int)valueMap.get("length"), valueMap.get("description"), (Map<String, String>)valueMap.get("attributes"), new Date((long)valueMap.get("created")), (int)valueMap.get("versions"));
        }
        return metadata;
    }
    
    static {
        LOG = LoggerFactory.getLogger(KMSUtil.class);
    }
}
