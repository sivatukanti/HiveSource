// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.util.Vector;

public interface IdentityRepository
{
    public static final int UNAVAILABLE = 0;
    public static final int NOTRUNNING = 1;
    public static final int RUNNING = 2;
    
    String getName();
    
    int getStatus();
    
    Vector getIdentities();
    
    boolean add(final byte[] p0);
    
    boolean remove(final byte[] p0);
    
    void removeAll();
    
    public static class Wrapper implements IdentityRepository
    {
        private IdentityRepository ir;
        private Vector cache;
        private boolean keep_in_cache;
        
        Wrapper(final IdentityRepository ir) {
            this(ir, false);
        }
        
        Wrapper(final IdentityRepository ir, final boolean keep_in_cache) {
            this.cache = new Vector();
            this.keep_in_cache = false;
            this.ir = ir;
            this.keep_in_cache = keep_in_cache;
        }
        
        public String getName() {
            return this.ir.getName();
        }
        
        public int getStatus() {
            return this.ir.getStatus();
        }
        
        public boolean add(final byte[] identity) {
            return this.ir.add(identity);
        }
        
        public boolean remove(final byte[] blob) {
            return this.ir.remove(blob);
        }
        
        public void removeAll() {
            this.cache.removeAllElements();
            this.ir.removeAll();
        }
        
        public Vector getIdentities() {
            final Vector result = new Vector();
            for (int i = 0; i < this.cache.size(); ++i) {
                final Identity identity = this.cache.elementAt(i);
                result.add(identity);
            }
            final Vector tmp = this.ir.getIdentities();
            for (int j = 0; j < tmp.size(); ++j) {
                result.add(tmp.elementAt(j));
            }
            return result;
        }
        
        void add(final Identity identity) {
            if (!this.keep_in_cache && !identity.isEncrypted() && identity instanceof IdentityFile) {
                try {
                    this.ir.add(((IdentityFile)identity).getKeyPair().forSSHAgent());
                }
                catch (JSchException e) {}
            }
            else {
                this.cache.addElement(identity);
            }
        }
        
        void check() {
            if (this.cache.size() > 0) {
                final Object[] identities = this.cache.toArray();
                for (int i = 0; i < identities.length; ++i) {
                    final Identity identity = (Identity)identities[i];
                    this.cache.removeElement(identity);
                    this.add(identity);
                }
            }
        }
    }
}
