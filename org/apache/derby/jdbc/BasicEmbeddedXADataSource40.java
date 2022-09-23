// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import java.sql.SQLException;
import javax.sql.XAConnection;
import org.apache.derby.iapi.jdbc.ResourceAdapter;
import javax.sql.XADataSource;

public class BasicEmbeddedXADataSource40 extends BasicEmbeddedDataSource40 implements EmbeddedXADataSourceInterface, XADataSource
{
    private static final long serialVersionUID = -5715798975598379739L;
    private transient ResourceAdapter ra;
    
    @Override
    public final XAConnection getXAConnection() throws SQLException {
        if (this.ra == null || !this.ra.isActive()) {
            this.ra = EmbeddedBaseDataSource.setupResourceAdapter(this, this.ra, null, null, false);
        }
        return this.createXAConnection(this.ra, this.getUser(), this.getPassword(), false);
    }
    
    @Override
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
    
    @Override
    public ResourceAdapter getResourceAdapter() {
        return this.ra;
    }
}
