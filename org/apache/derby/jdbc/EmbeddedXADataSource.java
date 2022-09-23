// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import java.sql.SQLException;
import javax.sql.XAConnection;
import org.apache.derby.iapi.jdbc.ResourceAdapter;

public class EmbeddedXADataSource extends EmbeddedDataSource implements EmbeddedXADataSourceInterface
{
    private static final long serialVersionUID = -5715798975598379738L;
    private transient ResourceAdapter ra;
    
    public final XAConnection getXAConnection() throws SQLException {
        if (this.ra == null || !this.ra.isActive()) {
            this.ra = EmbeddedBaseDataSource.setupResourceAdapter(this, this.ra, null, null, false);
        }
        return this.createXAConnection(this.ra, this.getUser(), this.getPassword(), false);
    }
    
    public final XAConnection getXAConnection(final String s, final String s2) throws SQLException {
        if (this.ra == null || !this.ra.isActive()) {
            this.ra = EmbeddedBaseDataSource.setupResourceAdapter(this, this.ra, s, s2, true);
        }
        return this.createXAConnection(this.ra, s, s2, true);
    }
    
    @Override
    protected void update() {
        this.ra = null;
        super.update();
    }
    
    private XAConnection createXAConnection(final ResourceAdapter resourceAdapter, final String s, final String s2, final boolean b) throws SQLException {
        return ((Driver30)this.findDriver()).getNewXAConnection(this, resourceAdapter, s, s2, b);
    }
    
    public ResourceAdapter getResourceAdapter() {
        return this.ra;
    }
}
