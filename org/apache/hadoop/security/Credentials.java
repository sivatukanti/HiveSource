// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.nio.charset.StandardCharsets;
import org.slf4j.LoggerFactory;
import java.io.OutputStream;
import com.google.protobuf.ByteString;
import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.DataInput;
import java.util.Arrays;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import org.apache.hadoop.fs.FSDataInputStream;
import java.io.Closeable;
import java.io.IOException;
import org.apache.hadoop.io.IOUtils;
import java.io.DataInputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.io.Text;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class Credentials implements Writable
{
    private static final Logger LOG;
    private Map<Text, byte[]> secretKeysMap;
    private Map<Text, Token<? extends TokenIdentifier>> tokenMap;
    private static final byte[] TOKEN_STORAGE_MAGIC;
    
    public Credentials() {
        this.secretKeysMap = new HashMap<Text, byte[]>();
        this.tokenMap = new HashMap<Text, Token<? extends TokenIdentifier>>();
    }
    
    public Credentials(final Credentials credentials) {
        this.secretKeysMap = new HashMap<Text, byte[]>();
        this.tokenMap = new HashMap<Text, Token<? extends TokenIdentifier>>();
        this.addAll(credentials);
    }
    
    public Token<? extends TokenIdentifier> getToken(final Text alias) {
        return this.tokenMap.get(alias);
    }
    
    public void addToken(final Text alias, final Token<? extends TokenIdentifier> t) {
        if (t == null) {
            Credentials.LOG.warn("Null token ignored for " + alias);
        }
        else if (this.tokenMap.put(alias, t) != null) {
            final Map<Text, Token<? extends TokenIdentifier>> tokensToAdd = new HashMap<Text, Token<? extends TokenIdentifier>>();
            for (final Map.Entry<Text, Token<? extends TokenIdentifier>> e : this.tokenMap.entrySet()) {
                final Token<? extends TokenIdentifier> token = e.getValue();
                if (token.isPrivateCloneOf(alias)) {
                    tokensToAdd.put(e.getKey(), t.privateClone(token.getService()));
                }
            }
            this.tokenMap.putAll(tokensToAdd);
        }
    }
    
    public Collection<Token<? extends TokenIdentifier>> getAllTokens() {
        return this.tokenMap.values();
    }
    
    public int numberOfTokens() {
        return this.tokenMap.size();
    }
    
    public byte[] getSecretKey(final Text alias) {
        return this.secretKeysMap.get(alias);
    }
    
    public int numberOfSecretKeys() {
        return this.secretKeysMap.size();
    }
    
    public void addSecretKey(final Text alias, final byte[] key) {
        this.secretKeysMap.put(alias, key);
    }
    
    public void removeSecretKey(final Text alias) {
        this.secretKeysMap.remove(alias);
    }
    
    public List<Text> getAllSecretKeys() {
        final List<Text> list = new ArrayList<Text>();
        list.addAll(this.secretKeysMap.keySet());
        return list;
    }
    
    public static Credentials readTokenStorageFile(final Path filename, final Configuration conf) throws IOException {
        FSDataInputStream in = null;
        final Credentials credentials = new Credentials();
        try {
            in = filename.getFileSystem(conf).open(filename);
            credentials.readTokenStorageStream(in);
            in.close();
            return credentials;
        }
        catch (IOException ioe) {
            throw IOUtils.wrapException(filename.toString(), "Credentials.readTokenStorageFile", ioe);
        }
        finally {
            IOUtils.cleanupWithLogger(Credentials.LOG, in);
        }
    }
    
    public static Credentials readTokenStorageFile(final File filename, final Configuration conf) throws IOException {
        DataInputStream in = null;
        final Credentials credentials = new Credentials();
        try {
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
            credentials.readTokenStorageStream(in);
            return credentials;
        }
        catch (IOException ioe) {
            throw new IOException("Exception reading " + filename, ioe);
        }
        finally {
            IOUtils.cleanupWithLogger(Credentials.LOG, in);
        }
    }
    
    public void readTokenStorageStream(final DataInputStream in) throws IOException {
        final byte[] magic = new byte[Credentials.TOKEN_STORAGE_MAGIC.length];
        in.readFully(magic);
        if (!Arrays.equals(magic, Credentials.TOKEN_STORAGE_MAGIC)) {
            throw new IOException("Bad header found in token storage.");
        }
        SerializedFormat format;
        try {
            format = SerializedFormat.valueOf(in.readByte());
        }
        catch (IllegalArgumentException e) {
            throw new IOException(e);
        }
        switch (format) {
            case WRITABLE: {
                this.readFields(in);
                break;
            }
            case PROTOBUF: {
                this.readProto(in);
                break;
            }
            default: {
                throw new IOException("Unsupported format " + format);
            }
        }
    }
    
    public void writeTokenStorageToStream(final DataOutputStream os) throws IOException {
        this.writeTokenStorageToStream(os, SerializedFormat.WRITABLE);
    }
    
    public void writeTokenStorageToStream(final DataOutputStream os, final SerializedFormat format) throws IOException {
        switch (format) {
            case WRITABLE: {
                this.writeWritableOutputStream(os);
                break;
            }
            case PROTOBUF: {
                this.writeProtobufOutputStream(os);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported serialized format: " + format);
            }
        }
    }
    
    private void writeWritableOutputStream(final DataOutputStream os) throws IOException {
        os.write(Credentials.TOKEN_STORAGE_MAGIC);
        os.write(SerializedFormat.WRITABLE.value);
        this.write(os);
    }
    
    private void writeProtobufOutputStream(final DataOutputStream os) throws IOException {
        os.write(Credentials.TOKEN_STORAGE_MAGIC);
        os.write(SerializedFormat.PROTOBUF.value);
        this.writeProto(os);
    }
    
    public void writeTokenStorageFile(final Path filename, final Configuration conf) throws IOException {
        this.writeTokenStorageFile(filename, conf, SerializedFormat.WRITABLE);
    }
    
    public void writeTokenStorageFile(final Path filename, final Configuration conf, final SerializedFormat format) throws IOException {
        try (final FSDataOutputStream os = filename.getFileSystem(conf).create(filename)) {
            this.writeTokenStorageToStream(os, format);
        }
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        WritableUtils.writeVInt(out, this.tokenMap.size());
        for (final Map.Entry<Text, Token<? extends TokenIdentifier>> e : this.tokenMap.entrySet()) {
            e.getKey().write(out);
            e.getValue().write(out);
        }
        WritableUtils.writeVInt(out, this.secretKeysMap.size());
        for (final Map.Entry<Text, byte[]> e2 : this.secretKeysMap.entrySet()) {
            e2.getKey().write(out);
            WritableUtils.writeVInt(out, e2.getValue().length);
            out.write(e2.getValue());
        }
    }
    
    void writeProto(final DataOutput out) throws IOException {
        final SecurityProtos.CredentialsProto.Builder storage = SecurityProtos.CredentialsProto.newBuilder();
        for (final Map.Entry<Text, Token<? extends TokenIdentifier>> e : this.tokenMap.entrySet()) {
            final SecurityProtos.CredentialsKVProto.Builder kv = SecurityProtos.CredentialsKVProto.newBuilder().setAliasBytes(ByteString.copyFrom(e.getKey().getBytes(), 0, e.getKey().getLength())).setToken(e.getValue().toTokenProto());
            storage.addTokens(kv.build());
        }
        for (final Map.Entry<Text, byte[]> e2 : this.secretKeysMap.entrySet()) {
            final SecurityProtos.CredentialsKVProto.Builder kv = SecurityProtos.CredentialsKVProto.newBuilder().setAliasBytes(ByteString.copyFrom(e2.getKey().getBytes(), 0, e2.getKey().getLength())).setSecret(ByteString.copyFrom(e2.getValue()));
            storage.addSecrets(kv.build());
        }
        storage.build().writeDelimitedTo((OutputStream)out);
    }
    
    void readProto(final DataInput in) throws IOException {
        final SecurityProtos.CredentialsProto storage = SecurityProtos.CredentialsProto.parseDelimitedFrom((InputStream)in);
        for (final SecurityProtos.CredentialsKVProto kv : storage.getTokensList()) {
            this.addToken(new Text(kv.getAliasBytes().toByteArray()), new Token<TokenIdentifier>(kv.getToken()));
        }
        for (final SecurityProtos.CredentialsKVProto kv : storage.getSecretsList()) {
            this.addSecretKey(new Text(kv.getAliasBytes().toByteArray()), kv.getSecret().toByteArray());
        }
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.secretKeysMap.clear();
        this.tokenMap.clear();
        for (int size = WritableUtils.readVInt(in), i = 0; i < size; ++i) {
            final Text alias = new Text();
            alias.readFields(in);
            final Token<? extends TokenIdentifier> t = new Token<TokenIdentifier>();
            t.readFields(in);
            this.tokenMap.put(alias, t);
        }
        for (int size = WritableUtils.readVInt(in), i = 0; i < size; ++i) {
            final Text alias = new Text();
            alias.readFields(in);
            final int len = WritableUtils.readVInt(in);
            final byte[] value = new byte[len];
            in.readFully(value);
            this.secretKeysMap.put(alias, value);
        }
    }
    
    public void addAll(final Credentials other) {
        this.addAll(other, true);
    }
    
    public void mergeAll(final Credentials other) {
        this.addAll(other, false);
    }
    
    private void addAll(final Credentials other, final boolean overwrite) {
        for (final Map.Entry<Text, byte[]> secret : other.secretKeysMap.entrySet()) {
            final Text key = secret.getKey();
            if (!this.secretKeysMap.containsKey(key) || overwrite) {
                this.secretKeysMap.put(key, secret.getValue());
            }
        }
        for (final Map.Entry<Text, Token<?>> token : other.tokenMap.entrySet()) {
            final Text key = token.getKey();
            if (!this.tokenMap.containsKey(key) || overwrite) {
                this.addToken(key, (Token<? extends TokenIdentifier>)token.getValue());
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(Credentials.class);
        TOKEN_STORAGE_MAGIC = "HDTS".getBytes(StandardCharsets.UTF_8);
    }
    
    public enum SerializedFormat
    {
        WRITABLE((byte)0), 
        PROTOBUF((byte)1);
        
        private static final SerializedFormat[] FORMATS;
        final byte value;
        
        private SerializedFormat(final byte val) {
            this.value = val;
        }
        
        public static SerializedFormat valueOf(final int val) {
            try {
                return SerializedFormat.FORMATS[val];
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Unknown credential format: " + val);
            }
        }
        
        static {
            FORMATS = values();
        }
    }
}
