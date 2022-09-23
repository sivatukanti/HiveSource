// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.local;

import java.net.SocketAddress;

public final class LocalAddress extends SocketAddress implements Comparable<LocalAddress>
{
    private static final long serialVersionUID = -3601961747680808645L;
    public static final String EPHEMERAL = "ephemeral";
    private final String id;
    private final boolean ephemeral;
    
    public LocalAddress(final int id) {
        this(String.valueOf(id));
    }
    
    public LocalAddress(String id) {
        if (id == null) {
            throw new NullPointerException("id");
        }
        id = id.trim().toLowerCase();
        if (id.length() == 0) {
            throw new IllegalArgumentException("empty id");
        }
        this.id = id;
        this.ephemeral = "ephemeral".equals(id);
    }
    
    public String getId() {
        return this.id;
    }
    
    public boolean isEphemeral() {
        return this.ephemeral;
    }
    
    @Override
    public int hashCode() {
        if (this.ephemeral) {
            return System.identityHashCode(this);
        }
        return this.id.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof LocalAddress)) {
            return false;
        }
        if (this.ephemeral) {
            return this == o;
        }
        return this.getId().equals(((LocalAddress)o).getId());
    }
    
    public int compareTo(final LocalAddress o) {
        if (this.ephemeral) {
            if (!o.ephemeral) {
                return 1;
            }
            if (this == o) {
                return 0;
            }
            final int a = System.identityHashCode(this);
            final int b = System.identityHashCode(o);
            if (a < b) {
                return -1;
            }
            if (a > b) {
                return 1;
            }
            throw new Error("Two different ephemeral addresses have same identityHashCode.");
        }
        else {
            if (o.ephemeral) {
                return -1;
            }
            return this.getId().compareTo(o.getId());
        }
    }
    
    @Override
    public String toString() {
        return "local:" + this.getId();
    }
}
