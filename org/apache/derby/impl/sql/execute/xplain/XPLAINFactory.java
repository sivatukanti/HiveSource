// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.xplain;

import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import org.apache.derby.iapi.sql.execute.xplain.XPLAINFactoryIF;

public class XPLAINFactory implements XPLAINFactoryIF
{
    private XPLAINVisitor currentVisitor;
    private String currentSchema;
    
    public XPLAINFactory() {
        this.currentVisitor = new XPLAINDefaultVisitor();
        this.currentSchema = null;
    }
    
    public XPLAINVisitor getXPLAINVisitor() throws StandardException {
        try {
            final String xplainSchema = ConnectionUtil.getCurrentLCC().getXplainSchema();
            if (xplainSchema != this.currentSchema) {
                this.currentSchema = xplainSchema;
                if (this.currentSchema == null) {
                    this.currentVisitor = new XPLAINDefaultVisitor();
                }
                else {
                    this.currentVisitor = new XPLAINSystemTableVisitor();
                }
            }
        }
        catch (SQLException ex) {
            throw StandardException.plainWrapException(ex);
        }
        return this.currentVisitor;
    }
    
    public void freeResources() {
        this.currentVisitor = null;
        this.currentSchema = null;
    }
}
