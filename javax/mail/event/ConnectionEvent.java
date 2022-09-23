// 
// Decompiled by Procyon v0.5.36
// 

package javax.mail.event;

public class ConnectionEvent extends MailEvent
{
    public static final int OPENED = 1;
    public static final int DISCONNECTED = 2;
    public static final int CLOSED = 3;
    protected int type;
    private static final long serialVersionUID = -1855480171284792957L;
    
    public ConnectionEvent(final Object source, final int type) {
        super(source);
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void dispatch(final Object listener) {
        if (this.type == 1) {
            ((ConnectionListener)listener).opened(this);
        }
        else if (this.type == 2) {
            ((ConnectionListener)listener).disconnected(this);
        }
        else if (this.type == 3) {
            ((ConnectionListener)listener).closed(this);
        }
    }
}
