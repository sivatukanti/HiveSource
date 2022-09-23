// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key;

import java.util.HashMap;
import java.io.Reader;
import com.google.gson.stream.JsonReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.Writer;
import com.google.gson.stream.JsonWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Date;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import java.util.Iterator;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import java.util.List;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class KeyProvider
{
    public static final String DEFAULT_CIPHER_NAME = "hadoop.security.key.default.cipher";
    public static final String DEFAULT_CIPHER = "AES/CTR/NoPadding";
    public static final String DEFAULT_BITLENGTH_NAME = "hadoop.security.key.default.bitlength";
    public static final int DEFAULT_BITLENGTH = 128;
    public static final String JCEKS_KEY_SERIALFILTER_DEFAULT = "java.lang.Enum;java.security.KeyRep;java.security.KeyRep$Type;javax.crypto.spec.SecretKeySpec;org.apache.hadoop.crypto.key.JavaKeyStoreProvider$KeyMetadata;!*";
    public static final String JCEKS_KEY_SERIAL_FILTER = "jceks.key.serialFilter";
    private final Configuration conf;
    
    public KeyProvider(final Configuration conf) {
        this.conf = new Configuration(conf);
        if (System.getProperty("jceks.key.serialFilter") == null) {
            final String serialFilter = conf.get("hadoop.security.crypto.jceks.key.serialfilter", "java.lang.Enum;java.security.KeyRep;java.security.KeyRep$Type;javax.crypto.spec.SecretKeySpec;org.apache.hadoop.crypto.key.JavaKeyStoreProvider$KeyMetadata;!*");
            System.setProperty("jceks.key.serialFilter", serialFilter);
        }
    }
    
    public Configuration getConf() {
        return this.conf;
    }
    
    public static Options options(final Configuration conf) {
        return new Options(conf);
    }
    
    public boolean isTransient() {
        return false;
    }
    
    public abstract KeyVersion getKeyVersion(final String p0) throws IOException;
    
    public abstract List<String> getKeys() throws IOException;
    
    public Metadata[] getKeysMetadata(final String... names) throws IOException {
        final Metadata[] result = new Metadata[names.length];
        for (int i = 0; i < names.length; ++i) {
            result[i] = this.getMetadata(names[i]);
        }
        return result;
    }
    
    public abstract List<KeyVersion> getKeyVersions(final String p0) throws IOException;
    
    public KeyVersion getCurrentKey(final String name) throws IOException {
        final Metadata meta = this.getMetadata(name);
        if (meta == null) {
            return null;
        }
        return this.getKeyVersion(buildVersionName(name, meta.getVersions() - 1));
    }
    
    public abstract Metadata getMetadata(final String p0) throws IOException;
    
    public abstract KeyVersion createKey(final String p0, final byte[] p1, final Options p2) throws IOException;
    
    private String getAlgorithm(final String cipher) {
        final int slash = cipher.indexOf(47);
        if (slash == -1) {
            return cipher;
        }
        return cipher.substring(0, slash);
    }
    
    protected byte[] generateKey(final int size, String algorithm) throws NoSuchAlgorithmException {
        algorithm = this.getAlgorithm(algorithm);
        final KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(size);
        final byte[] key = keyGenerator.generateKey().getEncoded();
        return key;
    }
    
    public KeyVersion createKey(final String name, final Options options) throws NoSuchAlgorithmException, IOException {
        final byte[] material = this.generateKey(options.getBitLength(), options.getCipher());
        return this.createKey(name, material, options);
    }
    
    public abstract void deleteKey(final String p0) throws IOException;
    
    public abstract KeyVersion rollNewVersion(final String p0, final byte[] p1) throws IOException;
    
    public void close() throws IOException {
    }
    
    public KeyVersion rollNewVersion(final String name) throws NoSuchAlgorithmException, IOException {
        final Metadata meta = this.getMetadata(name);
        if (meta == null) {
            throw new IOException("Can't find Metadata for key " + name);
        }
        final byte[] material = this.generateKey(meta.getBitLength(), meta.getCipher());
        return this.rollNewVersion(name, material);
    }
    
    public void invalidateCache(final String name) throws IOException {
    }
    
    public abstract void flush() throws IOException;
    
    public static String getBaseName(final String versionName) throws IOException {
        final int div = versionName.lastIndexOf(64);
        if (div == -1) {
            throw new IOException("No version in key path " + versionName);
        }
        return versionName.substring(0, div);
    }
    
    protected static String buildVersionName(final String name, final int version) {
        return name + "@" + version;
    }
    
    public static KeyProvider findProvider(final List<KeyProvider> providerList, final String keyName) throws IOException {
        for (final KeyProvider provider : providerList) {
            if (provider.getMetadata(keyName) != null) {
                return provider;
            }
        }
        throw new IOException("Can't find KeyProvider for key " + keyName);
    }
    
    public boolean needsPassword() throws IOException {
        return false;
    }
    
    public String noPasswordWarning() {
        return null;
    }
    
    public String noPasswordError() {
        return null;
    }
    
    public static class KeyVersion
    {
        private final String name;
        private final String versionName;
        private final byte[] material;
        
        protected KeyVersion(final String name, final String versionName, final byte[] material) {
            this.name = ((name == null) ? null : name.intern());
            this.versionName = ((versionName == null) ? null : versionName.intern());
            this.material = material;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getVersionName() {
            return this.versionName;
        }
        
        public byte[] getMaterial() {
            return this.material;
        }
        
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();
            buf.append("key(");
            buf.append(this.versionName);
            buf.append(")=");
            if (this.material == null) {
                buf.append("null");
            }
            else {
                for (final byte b : this.material) {
                    buf.append(' ');
                    final int right = b & 0xFF;
                    if (right < 16) {
                        buf.append('0');
                    }
                    buf.append(Integer.toHexString(right));
                }
            }
            return buf.toString();
        }
        
        @Override
        public boolean equals(final Object rhs) {
            if (this == rhs) {
                return true;
            }
            if (rhs == null || this.getClass() != rhs.getClass()) {
                return false;
            }
            final KeyVersion kv = (KeyVersion)rhs;
            return new EqualsBuilder().append(this.name, kv.name).append(this.versionName, kv.versionName).append(this.material, kv.material).isEquals();
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(this.name).append(this.versionName).append(this.material).toHashCode();
        }
    }
    
    public static class Metadata
    {
        private static final String CIPHER_FIELD = "cipher";
        private static final String BIT_LENGTH_FIELD = "bitLength";
        private static final String CREATED_FIELD = "created";
        private static final String DESCRIPTION_FIELD = "description";
        private static final String VERSIONS_FIELD = "versions";
        private static final String ATTRIBUTES_FIELD = "attributes";
        private final String cipher;
        private final int bitLength;
        private final String description;
        private final Date created;
        private int versions;
        private Map<String, String> attributes;
        
        protected Metadata(final String cipher, final int bitLength, final String description, final Map<String, String> attributes, final Date created, final int versions) {
            this.cipher = cipher;
            this.bitLength = bitLength;
            this.description = description;
            this.attributes = ((attributes == null || attributes.isEmpty()) ? null : attributes);
            this.created = created;
            this.versions = versions;
        }
        
        @Override
        public String toString() {
            final StringBuilder metaSB = new StringBuilder();
            metaSB.append("cipher: ").append(this.cipher).append(", ");
            metaSB.append("length: ").append(this.bitLength).append(", ");
            metaSB.append("description: ").append(this.description).append(", ");
            metaSB.append("created: ").append(this.created).append(", ");
            metaSB.append("version: ").append(this.versions).append(", ");
            metaSB.append("attributes: ");
            if (this.attributes != null && !this.attributes.isEmpty()) {
                for (final Map.Entry<String, String> attribute : this.attributes.entrySet()) {
                    metaSB.append("[");
                    metaSB.append(attribute.getKey());
                    metaSB.append("=");
                    metaSB.append(attribute.getValue());
                    metaSB.append("], ");
                }
                metaSB.deleteCharAt(metaSB.length() - 2);
            }
            else {
                metaSB.append("null");
            }
            return metaSB.toString();
        }
        
        public String getDescription() {
            return this.description;
        }
        
        public Date getCreated() {
            return this.created;
        }
        
        public String getCipher() {
            return this.cipher;
        }
        
        public Map<String, String> getAttributes() {
            return (this.attributes == null) ? Collections.emptyMap() : this.attributes;
        }
        
        public String getAlgorithm() {
            final int slash = this.cipher.indexOf(47);
            if (slash == -1) {
                return this.cipher;
            }
            return this.cipher.substring(0, slash);
        }
        
        public int getBitLength() {
            return this.bitLength;
        }
        
        public int getVersions() {
            return this.versions;
        }
        
        protected int addVersion() {
            return this.versions++;
        }
        
        protected byte[] serialize() throws IOException {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final JsonWriter writer = new JsonWriter(new OutputStreamWriter(buffer, StandardCharsets.UTF_8));
            try {
                writer.beginObject();
                if (this.cipher != null) {
                    writer.name("cipher").value(this.cipher);
                }
                if (this.bitLength != 0) {
                    writer.name("bitLength").value(this.bitLength);
                }
                if (this.created != null) {
                    writer.name("created").value(this.created.getTime());
                }
                if (this.description != null) {
                    writer.name("description").value(this.description);
                }
                if (this.attributes != null && this.attributes.size() > 0) {
                    writer.name("attributes").beginObject();
                    for (final Map.Entry<String, String> attribute : this.attributes.entrySet()) {
                        writer.name(attribute.getKey()).value(attribute.getValue());
                    }
                    writer.endObject();
                }
                writer.name("versions").value(this.versions);
                writer.endObject();
                writer.flush();
            }
            finally {
                writer.close();
            }
            return buffer.toByteArray();
        }
        
        protected Metadata(final byte[] bytes) throws IOException {
            String cipher = null;
            int bitLength = 0;
            Date created = null;
            int versions = 0;
            String description = null;
            Map<String, String> attributes = null;
            final JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8));
            try {
                reader.beginObject();
                while (reader.hasNext()) {
                    final String field = reader.nextName();
                    if ("cipher".equals(field)) {
                        cipher = reader.nextString();
                    }
                    else if ("bitLength".equals(field)) {
                        bitLength = reader.nextInt();
                    }
                    else if ("created".equals(field)) {
                        created = new Date(reader.nextLong());
                    }
                    else if ("versions".equals(field)) {
                        versions = reader.nextInt();
                    }
                    else if ("description".equals(field)) {
                        description = reader.nextString();
                    }
                    else {
                        if (!"attributes".equalsIgnoreCase(field)) {
                            continue;
                        }
                        reader.beginObject();
                        attributes = new HashMap<String, String>();
                        while (reader.hasNext()) {
                            attributes.put(reader.nextName(), reader.nextString());
                        }
                        reader.endObject();
                    }
                }
                reader.endObject();
            }
            finally {
                reader.close();
            }
            this.cipher = cipher;
            this.bitLength = bitLength;
            this.created = created;
            this.description = description;
            this.attributes = attributes;
            this.versions = versions;
        }
    }
    
    public static class Options
    {
        private String cipher;
        private int bitLength;
        private String description;
        private Map<String, String> attributes;
        
        public Options(final Configuration conf) {
            this.cipher = conf.get("hadoop.security.key.default.cipher", "AES/CTR/NoPadding");
            this.bitLength = conf.getInt("hadoop.security.key.default.bitlength", 128);
        }
        
        public Options setCipher(final String cipher) {
            this.cipher = cipher;
            return this;
        }
        
        public Options setBitLength(final int bitLength) {
            this.bitLength = bitLength;
            return this;
        }
        
        public Options setDescription(final String description) {
            this.description = description;
            return this;
        }
        
        public Options setAttributes(final Map<String, String> attributes) {
            if (attributes != null) {
                if (attributes.containsKey(null)) {
                    throw new IllegalArgumentException("attributes cannot have a NULL key");
                }
                this.attributes = new HashMap<String, String>(attributes);
            }
            return this;
        }
        
        public String getCipher() {
            return this.cipher;
        }
        
        public int getBitLength() {
            return this.bitLength;
        }
        
        public String getDescription() {
            return this.description;
        }
        
        public Map<String, String> getAttributes() {
            return (this.attributes == null) ? Collections.emptyMap() : this.attributes;
        }
        
        @Override
        public String toString() {
            return "Options{cipher='" + this.cipher + '\'' + ", bitLength=" + this.bitLength + ", description='" + this.description + '\'' + ", attributes=" + this.attributes + '}';
        }
    }
}
