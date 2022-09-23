// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.io.WritableFactories;
import org.apache.hadoop.io.WritableFactory;
import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import java.util.HashMap;
import org.apache.hadoop.io.Writable;

public class ProtocolSignature implements Writable
{
    private long version;
    private int[] methods;
    private static final HashMap<String, ProtocolSigFingerprint> PROTOCOL_FINGERPRINT_CACHE;
    
    public ProtocolSignature() {
        this.methods = null;
    }
    
    public ProtocolSignature(final long version, final int[] methodHashcodes) {
        this.methods = null;
        this.version = version;
        this.methods = methodHashcodes;
    }
    
    public long getVersion() {
        return this.version;
    }
    
    public int[] getMethods() {
        return this.methods;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.version = in.readLong();
        final boolean hasMethods = in.readBoolean();
        if (hasMethods) {
            final int numMethods = in.readInt();
            this.methods = new int[numMethods];
            for (int i = 0; i < numMethods; ++i) {
                this.methods[i] = in.readInt();
            }
        }
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeLong(this.version);
        if (this.methods == null) {
            out.writeBoolean(false);
        }
        else {
            out.writeBoolean(true);
            out.writeInt(this.methods.length);
            for (final int method : this.methods) {
                out.writeInt(method);
            }
        }
    }
    
    static int getFingerprint(final Method method) {
        int hashcode = method.getName().hashCode();
        hashcode += 31 * method.getReturnType().getName().hashCode();
        for (final Class<?> type : method.getParameterTypes()) {
            hashcode = (31 * hashcode ^ type.getName().hashCode());
        }
        return hashcode;
    }
    
    private static int[] getFingerprints(final Method[] methods) {
        if (methods == null) {
            return null;
        }
        final int[] hashCodes = new int[methods.length];
        for (int i = 0; i < methods.length; ++i) {
            hashCodes[i] = getFingerprint(methods[i]);
        }
        return hashCodes;
    }
    
    static int getFingerprint(final Method[] methods) {
        return getFingerprint(getFingerprints(methods));
    }
    
    static int getFingerprint(final int[] hashcodes) {
        Arrays.sort(hashcodes);
        return Arrays.hashCode(hashcodes);
    }
    
    @VisibleForTesting
    public static void resetCache() {
        ProtocolSignature.PROTOCOL_FINGERPRINT_CACHE.clear();
    }
    
    private static ProtocolSigFingerprint getSigFingerprint(final Class<?> protocol, final long serverVersion) {
        final String protocolName = RPC.getProtocolName(protocol);
        synchronized (ProtocolSignature.PROTOCOL_FINGERPRINT_CACHE) {
            ProtocolSigFingerprint sig = ProtocolSignature.PROTOCOL_FINGERPRINT_CACHE.get(protocolName);
            if (sig == null) {
                final int[] serverMethodHashcodes = getFingerprints(protocol.getMethods());
                sig = new ProtocolSigFingerprint(new ProtocolSignature(serverVersion, serverMethodHashcodes), getFingerprint(serverMethodHashcodes));
                ProtocolSignature.PROTOCOL_FINGERPRINT_CACHE.put(protocolName, sig);
            }
            return sig;
        }
    }
    
    public static ProtocolSignature getProtocolSignature(final int clientMethodsHashCode, final long serverVersion, final Class<? extends VersionedProtocol> protocol) {
        final ProtocolSigFingerprint sig = getSigFingerprint(protocol, serverVersion);
        if (clientMethodsHashCode == sig.fingerprint) {
            return new ProtocolSignature(serverVersion, null);
        }
        return sig.signature;
    }
    
    public static ProtocolSignature getProtocolSignature(final String protocolName, final long version) throws ClassNotFoundException {
        final Class<?> protocol = Class.forName(protocolName);
        return getSigFingerprint(protocol, version).signature;
    }
    
    public static ProtocolSignature getProtocolSignature(final VersionedProtocol server, final String protocol, final long clientVersion, final int clientMethodsHash) throws IOException {
        Class<? extends VersionedProtocol> inter;
        try {
            inter = (Class<? extends VersionedProtocol>)Class.forName(protocol);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
        final long serverVersion = server.getProtocolVersion(protocol, clientVersion);
        return getProtocolSignature(clientMethodsHash, serverVersion, inter);
    }
    
    static {
        WritableFactories.setFactory(ProtocolSignature.class, new WritableFactory() {
            @Override
            public Writable newInstance() {
                return new ProtocolSignature();
            }
        });
        PROTOCOL_FINGERPRINT_CACHE = new HashMap<String, ProtocolSigFingerprint>();
    }
    
    private static class ProtocolSigFingerprint
    {
        private ProtocolSignature signature;
        private int fingerprint;
        
        ProtocolSigFingerprint(final ProtocolSignature sig, final int fingerprint) {
            this.signature = sig;
            this.fingerprint = fingerprint;
        }
    }
}
