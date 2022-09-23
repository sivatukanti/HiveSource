// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;

public class ProtocolProxy<T>
{
    private Class<T> protocol;
    private T proxy;
    private HashSet<Integer> serverMethods;
    private final boolean supportServerMethodCheck;
    private boolean serverMethodsFetched;
    
    public ProtocolProxy(final Class<T> protocol, final T proxy, final boolean supportServerMethodCheck) {
        this.serverMethods = null;
        this.serverMethodsFetched = false;
        this.protocol = protocol;
        this.proxy = proxy;
        this.supportServerMethodCheck = supportServerMethodCheck;
    }
    
    private void fetchServerMethods(final Method method) throws IOException {
        final long clientVersion = RPC.getProtocolVersion(method.getDeclaringClass());
        final int clientMethodsHash = ProtocolSignature.getFingerprint(method.getDeclaringClass().getMethods());
        final ProtocolSignature serverInfo = ((VersionedProtocol)this.proxy).getProtocolSignature(RPC.getProtocolName(this.protocol), clientVersion, clientMethodsHash);
        final long serverVersion = serverInfo.getVersion();
        if (serverVersion != clientVersion) {
            throw new RPC.VersionMismatch(this.protocol.getName(), clientVersion, serverVersion);
        }
        final int[] serverMethodsCodes = serverInfo.getMethods();
        if (serverMethodsCodes != null) {
            this.serverMethods = new HashSet<Integer>(serverMethodsCodes.length);
            for (final int m : serverMethodsCodes) {
                this.serverMethods.add(m);
            }
        }
        this.serverMethodsFetched = true;
    }
    
    public T getProxy() {
        return this.proxy;
    }
    
    public synchronized boolean isMethodSupported(final String methodName, final Class<?>... parameterTypes) throws IOException {
        if (!this.supportServerMethodCheck) {
            return true;
        }
        Method method;
        try {
            method = this.protocol.getDeclaredMethod(methodName, parameterTypes);
        }
        catch (SecurityException e) {
            throw new IOException(e);
        }
        catch (NoSuchMethodException e2) {
            throw new IOException(e2);
        }
        if (!this.serverMethodsFetched) {
            this.fetchServerMethods(method);
        }
        return this.serverMethods == null || this.serverMethods.contains(ProtocolSignature.getFingerprint(method));
    }
}
