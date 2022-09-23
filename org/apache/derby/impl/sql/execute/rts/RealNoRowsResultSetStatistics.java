// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

import org.apache.derby.impl.sql.catalog.XPLAINResultSetTimingsDescriptor;
import org.apache.derby.impl.sql.catalog.XPLAINResultSetDescriptor;
import org.apache.derby.catalog.UUID;
import java.util.Vector;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.sql.execute.ResultSetStatistics;

abstract class RealNoRowsResultSetStatistics implements ResultSetStatistics
{
    protected String indent;
    protected String subIndent;
    protected int sourceDepth;
    public ResultSetStatistics sourceResultSetStatistics;
    public long executeTime;
    public long inspectOverall;
    public long inspectNum;
    public String inspectDesc;
    
    public RealNoRowsResultSetStatistics(final long n, final ResultSetStatistics resultSetStatistics) {
        if (resultSetStatistics instanceof RealBasicNoPutResultSetStatistics) {
            this.executeTime = n - ((RealBasicNoPutResultSetStatistics)resultSetStatistics).getTotalTime();
        }
    }
    
    protected void initFormatInfo(int i) {
        final char[] value = new char[i];
        final char[] value2 = new char[i + 1];
        this.sourceDepth = i + 1;
        value2[i] = '\t';
        while (i > 0) {
            value[i - 1] = (value2[i - 1] = '\t');
            --i;
        }
        this.indent = new String(value);
        this.subIndent = new String(value2);
    }
    
    protected String dumpTimeStats(final String str) {
        return str + MessageService.getTextMessage("43Y29.U") + " = " + this.executeTime + "\n";
    }
    
    public Vector getChildren() {
        final Vector<ResultSetStatistics> vector = new Vector<ResultSetStatistics>();
        vector.addElement(this.sourceResultSetStatistics);
        return vector;
    }
    
    public abstract String getNodeName();
    
    public double getEstimatedRowCount() {
        return 0.0;
    }
    
    public String getRSXplainDetails() {
        return null;
    }
    
    public Object getResultSetDescriptor(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new XPLAINResultSetDescriptor((UUID)o, this.getRSXplainType(), this.getRSXplainDetails(), null, null, null, null, (UUID)o2, null, null, null, null, null, null, null, null, null, null, null, (UUID)o3, (UUID)o4, (UUID)o5, (UUID)o6);
    }
    
    public Object getResultSetTimingsDescriptor(final Object o) {
        return new XPLAINResultSetTimingsDescriptor((UUID)o, null, null, null, null, new Long(this.executeTime), null, null, null, null, null);
    }
    
    public Object getSortPropsDescriptor(final Object o) {
        return null;
    }
    
    public Object getScanPropsDescriptor(final Object o) {
        return null;
    }
}
