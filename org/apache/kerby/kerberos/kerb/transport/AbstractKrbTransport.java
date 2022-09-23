// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.transport;

public abstract class AbstractKrbTransport implements KrbTransport
{
    private Object attachment;
    
    @Override
    public void setAttachment(final Object attachment) {
        this.attachment = attachment;
    }
    
    @Override
    public Object getAttachment() {
        return this.attachment;
    }
}
