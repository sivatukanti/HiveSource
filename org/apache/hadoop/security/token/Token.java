// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token;

import org.slf4j.LoggerFactory;
import java.util.UUID;
import com.google.common.primitives.Bytes;
import org.apache.hadoop.io.WritableComparator;
import java.util.Arrays;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.io.DataOutputBuffer;
import java.io.DataOutput;
import org.apache.hadoop.io.WritableUtils;
import java.io.IOException;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ReflectionUtils;
import java.util.Iterator;
import com.google.common.collect.Maps;
import com.google.protobuf.ByteString;
import org.apache.hadoop.security.proto.SecurityProtos;
import java.util.ServiceLoader;
import org.apache.hadoop.io.Text;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class Token<T extends TokenIdentifier> implements Writable
{
    public static final Logger LOG;
    private static Map<Text, Class<? extends TokenIdentifier>> tokenKindMap;
    private byte[] identifier;
    private byte[] password;
    private Text kind;
    private Text service;
    private TokenRenewer renewer;
    private static ServiceLoader<TokenRenewer> renewers;
    private static final TokenRenewer TRIVIAL_RENEWER;
    
    public Token(final T id, final SecretManager<T> mgr) {
        this.password = mgr.createPassword(id);
        this.identifier = id.getBytes();
        this.kind = id.getKind();
        this.service = new Text();
    }
    
    public Token(final byte[] identifier, final byte[] password, final Text kind, final Text service) {
        this.identifier = ((identifier == null) ? new byte[0] : identifier);
        this.password = ((password == null) ? new byte[0] : password);
        this.kind = ((kind == null) ? new Text() : kind);
        this.service = ((service == null) ? new Text() : service);
    }
    
    public Token() {
        this.identifier = new byte[0];
        this.password = new byte[0];
        this.kind = new Text();
        this.service = new Text();
    }
    
    public Token(final Token<T> other) {
        this.identifier = other.identifier.clone();
        this.password = other.password.clone();
        this.kind = new Text(other.kind);
        this.service = new Text(other.service);
    }
    
    public Token<T> copyToken() {
        return new Token<T>(this);
    }
    
    public Token(final SecurityProtos.TokenProto tokenPB) {
        this.identifier = tokenPB.getIdentifier().toByteArray();
        this.password = tokenPB.getPassword().toByteArray();
        this.kind = new Text(tokenPB.getKindBytes().toByteArray());
        this.service = new Text(tokenPB.getServiceBytes().toByteArray());
    }
    
    public SecurityProtos.TokenProto toTokenProto() {
        return SecurityProtos.TokenProto.newBuilder().setIdentifier(ByteString.copyFrom(this.getIdentifier())).setPassword(ByteString.copyFrom(this.getPassword())).setKindBytes(ByteString.copyFrom(this.getKind().getBytes(), 0, this.getKind().getLength())).setServiceBytes(ByteString.copyFrom(this.getService().getBytes(), 0, this.getService().getLength())).build();
    }
    
    public byte[] getIdentifier() {
        return this.identifier;
    }
    
    private static Class<? extends TokenIdentifier> getClassForIdentifier(final Text kind) {
        Class<? extends TokenIdentifier> cls = null;
        synchronized (Token.class) {
            if (Token.tokenKindMap == null) {
                Token.tokenKindMap = (Map<Text, Class<? extends TokenIdentifier>>)Maps.newHashMap();
                for (final TokenIdentifier id : ServiceLoader.load(TokenIdentifier.class)) {
                    Token.tokenKindMap.put(id.getKind(), id.getClass());
                }
            }
            cls = Token.tokenKindMap.get(kind);
        }
        if (cls == null) {
            Token.LOG.debug("Cannot find class for token kind " + kind);
            return null;
        }
        return cls;
    }
    
    public T decodeIdentifier() throws IOException {
        final Class<? extends TokenIdentifier> cls = getClassForIdentifier(this.getKind());
        if (cls == null) {
            return null;
        }
        final TokenIdentifier tokenIdentifier = ReflectionUtils.newInstance(cls, null);
        final ByteArrayInputStream buf = new ByteArrayInputStream(this.identifier);
        final DataInputStream in = new DataInputStream(buf);
        tokenIdentifier.readFields(in);
        in.close();
        return (T)tokenIdentifier;
    }
    
    public byte[] getPassword() {
        return this.password;
    }
    
    public synchronized Text getKind() {
        return this.kind;
    }
    
    @InterfaceAudience.Private
    public synchronized void setKind(final Text newKind) {
        this.kind = newKind;
        this.renewer = null;
    }
    
    public Text getService() {
        return this.service;
    }
    
    public void setService(final Text newService) {
        this.service = newService;
    }
    
    public boolean isPrivate() {
        return false;
    }
    
    public boolean isPrivateCloneOf(final Text thePublicService) {
        return false;
    }
    
    public Token<T> privateClone(final Text newService) {
        return new PrivateToken<T>(this, newService);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        int len = WritableUtils.readVInt(in);
        if (this.identifier == null || this.identifier.length != len) {
            this.identifier = new byte[len];
        }
        in.readFully(this.identifier);
        len = WritableUtils.readVInt(in);
        if (this.password == null || this.password.length != len) {
            this.password = new byte[len];
        }
        in.readFully(this.password);
        this.kind.readFields(in);
        this.service.readFields(in);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        WritableUtils.writeVInt(out, this.identifier.length);
        out.write(this.identifier);
        WritableUtils.writeVInt(out, this.password.length);
        out.write(this.password);
        this.kind.write(out);
        this.service.write(out);
    }
    
    private static String encodeWritable(final Writable obj) throws IOException {
        final DataOutputBuffer buf = new DataOutputBuffer();
        obj.write(buf);
        final Base64 encoder = new Base64(0, null, true);
        final byte[] raw = new byte[buf.getLength()];
        System.arraycopy(buf.getData(), 0, raw, 0, buf.getLength());
        return encoder.encodeToString(raw);
    }
    
    private static void decodeWritable(final Writable obj, final String newValue) throws IOException {
        if (newValue == null) {
            throw new HadoopIllegalArgumentException("Invalid argument, newValue is null");
        }
        final Base64 decoder = new Base64(0, null, true);
        final DataInputBuffer buf = new DataInputBuffer();
        final byte[] decoded = decoder.decode(newValue);
        buf.reset(decoded, decoded.length);
        obj.readFields(buf);
    }
    
    public String encodeToUrlString() throws IOException {
        return encodeWritable(this);
    }
    
    public void decodeFromUrlString(final String newValue) throws IOException {
        decodeWritable(this, newValue);
    }
    
    @Override
    public boolean equals(final Object right) {
        if (this == right) {
            return true;
        }
        if (right == null || this.getClass() != right.getClass()) {
            return false;
        }
        final Token<T> r = (Token<T>)right;
        return Arrays.equals(this.identifier, r.identifier) && Arrays.equals(this.password, r.password) && this.kind.equals(r.kind) && this.service.equals(r.service);
    }
    
    @Override
    public int hashCode() {
        return WritableComparator.hashBytes(this.identifier, this.identifier.length);
    }
    
    private static void addBinaryBuffer(final StringBuilder buffer, final byte[] bytes) {
        for (int idx = 0; idx < bytes.length; ++idx) {
            if (idx != 0) {
                buffer.append(' ');
            }
            final String num = Integer.toHexString(0xFF & bytes[idx]);
            if (num.length() < 2) {
                buffer.append('0');
            }
            buffer.append(num);
        }
    }
    
    private void identifierToString(final StringBuilder buffer) {
        T id = null;
        try {
            id = this.decodeIdentifier();
        }
        catch (IOException ex) {}
        finally {
            if (id != null) {
                buffer.append("(").append(id).append(")");
            }
            else {
                addBinaryBuffer(buffer, this.identifier);
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("Kind: ");
        buffer.append(this.kind.toString());
        buffer.append(", Service: ");
        buffer.append(this.service.toString());
        buffer.append(", Ident: ");
        this.identifierToString(buffer);
        return buffer.toString();
    }
    
    public String buildCacheKey() {
        return UUID.nameUUIDFromBytes(Bytes.concat(new byte[][] { this.kind.getBytes(), this.identifier, this.password })).toString();
    }
    
    private synchronized TokenRenewer getRenewer() throws IOException {
        if (this.renewer != null) {
            return this.renewer;
        }
        this.renewer = Token.TRIVIAL_RENEWER;
        synchronized (Token.renewers) {
            for (final TokenRenewer canidate : Token.renewers) {
                if (canidate.handleKind(this.kind)) {
                    return this.renewer = canidate;
                }
            }
        }
        Token.LOG.warn("No TokenRenewer defined for token kind " + this.kind);
        return this.renewer;
    }
    
    public boolean isManaged() throws IOException {
        return this.getRenewer().isManaged(this);
    }
    
    public long renew(final Configuration conf) throws IOException, InterruptedException {
        return this.getRenewer().renew(this, conf);
    }
    
    public void cancel(final Configuration conf) throws IOException, InterruptedException {
        this.getRenewer().cancel(this, conf);
    }
    
    static {
        LOG = LoggerFactory.getLogger(Token.class);
        Token.renewers = ServiceLoader.load(TokenRenewer.class);
        TRIVIAL_RENEWER = new TrivialRenewer();
    }
    
    static class PrivateToken<T extends TokenIdentifier> extends Token<T>
    {
        private final Text publicService;
        
        PrivateToken(final Token<T> publicToken, final Text newService) {
            super(((Token<TokenIdentifier>)publicToken).identifier, ((Token<TokenIdentifier>)publicToken).password, ((Token<TokenIdentifier>)publicToken).kind, newService);
            assert !publicToken.isPrivate();
            this.publicService = ((Token<TokenIdentifier>)publicToken).service;
            if (PrivateToken.LOG.isDebugEnabled()) {
                PrivateToken.LOG.debug("Cloned private token " + this + " from " + publicToken);
            }
        }
        
        @Override
        public boolean isPrivate() {
            return true;
        }
        
        @Override
        public boolean isPrivateCloneOf(final Text thePublicService) {
            return this.publicService.equals(thePublicService);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            final PrivateToken<?> that = (PrivateToken<?>)o;
            return this.publicService.equals(that.publicService);
        }
        
        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.publicService.hashCode();
            return result;
        }
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static class TrivialRenewer extends TokenRenewer
    {
        protected Text getKind() {
            return null;
        }
        
        @Override
        public boolean handleKind(final Text kind) {
            return kind.equals(this.getKind());
        }
        
        @Override
        public boolean isManaged(final Token<?> token) {
            return false;
        }
        
        @Override
        public long renew(final Token<?> token, final Configuration conf) {
            throw new UnsupportedOperationException("Token renewal is not supported  for " + ((Token<TokenIdentifier>)token).kind + " tokens");
        }
        
        @Override
        public void cancel(final Token<?> token, final Configuration conf) throws IOException, InterruptedException {
            throw new UnsupportedOperationException("Token cancel is not supported  for " + ((Token<TokenIdentifier>)token).kind + " tokens");
        }
    }
}
