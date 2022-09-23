// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.util.Vector;

class LocalIdentityRepository implements IdentityRepository
{
    private static final String name = "Local Identity Repository";
    private Vector identities;
    private JSch jsch;
    
    LocalIdentityRepository(final JSch jsch) {
        this.identities = new Vector();
        this.jsch = jsch;
    }
    
    public String getName() {
        return "Local Identity Repository";
    }
    
    public int getStatus() {
        return 2;
    }
    
    public synchronized Vector getIdentities() {
        this.removeDupulicates();
        final Vector v = new Vector();
        for (int i = 0; i < this.identities.size(); ++i) {
            v.addElement(this.identities.elementAt(i));
        }
        return v;
    }
    
    public synchronized void add(final Identity identity) {
        if (!this.identities.contains(identity)) {
            final byte[] blob1 = identity.getPublicKeyBlob();
            if (blob1 == null) {
                this.identities.addElement(identity);
                return;
            }
            for (int i = 0; i < this.identities.size(); ++i) {
                final byte[] blob2 = this.identities.elementAt(i).getPublicKeyBlob();
                if (blob2 != null && Util.array_equals(blob1, blob2)) {
                    if (identity.isEncrypted() || !this.identities.elementAt(i).isEncrypted()) {
                        return;
                    }
                    this.remove(blob2);
                }
            }
            this.identities.addElement(identity);
        }
    }
    
    public synchronized boolean add(final byte[] identity) {
        try {
            final Identity _identity = IdentityFile.newInstance("from remote:", identity, null, this.jsch);
            this.add(_identity);
            return true;
        }
        catch (JSchException e) {
            return false;
        }
    }
    
    synchronized void remove(final Identity identity) {
        if (this.identities.contains(identity)) {
            this.identities.removeElement(identity);
            identity.clear();
        }
        else {
            this.remove(identity.getPublicKeyBlob());
        }
    }
    
    public synchronized boolean remove(final byte[] blob) {
        if (blob == null) {
            return false;
        }
        for (int i = 0; i < this.identities.size(); ++i) {
            final Identity _identity = this.identities.elementAt(i);
            final byte[] _blob = _identity.getPublicKeyBlob();
            if (_blob != null && Util.array_equals(blob, _blob)) {
                this.identities.removeElement(_identity);
                _identity.clear();
                return true;
            }
        }
        return false;
    }
    
    public synchronized void removeAll() {
        for (int i = 0; i < this.identities.size(); ++i) {
            final Identity identity = this.identities.elementAt(i);
            identity.clear();
        }
        this.identities.removeAllElements();
    }
    
    private void removeDupulicates() {
        final Vector v = new Vector();
        final int len = this.identities.size();
        if (len == 0) {
            return;
        }
        for (int i = 0; i < len; ++i) {
            final Identity foo = this.identities.elementAt(i);
            final byte[] foo_blob = foo.getPublicKeyBlob();
            if (foo_blob != null) {
                for (int j = i + 1; j < len; ++j) {
                    final Identity bar = this.identities.elementAt(j);
                    final byte[] bar_blob = bar.getPublicKeyBlob();
                    if (bar_blob != null) {
                        if (Util.array_equals(foo_blob, bar_blob) && foo.isEncrypted() == bar.isEncrypted()) {
                            v.addElement(foo_blob);
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < v.size(); ++i) {
            this.remove(v.elementAt(i));
        }
    }
}
