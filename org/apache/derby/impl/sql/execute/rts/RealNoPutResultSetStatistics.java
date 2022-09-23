// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute.rts;

abstract class RealNoPutResultSetStatistics extends RealBasicNoPutResultSetStatistics
{
    public int resultSetNumber;
    protected String indent;
    protected String subIndent;
    protected int sourceDepth;
    
    public RealNoPutResultSetStatistics(final int n, final int n2, final int n3, final long n4, final long n5, final long n6, final long n7, final int resultSetNumber, final double n8, final double n9) {
        super(n, n2, n3, n4, n5, n6, n7, n8, n9);
        this.resultSetNumber = resultSetNumber;
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
}
