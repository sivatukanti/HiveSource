// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.sql.dictionary.SystemColumn;

public abstract class XPLAINTableDescriptor
{
    private String tableInsertStmt;
    
    public abstract String getCatalogName();
    
    protected abstract SystemColumn[] buildColumnList();
    
    public String[] getTableDDL(final String s) {
        final String normalToDelimited = IdUtil.normalToDelimited(s);
        final String normalToDelimited2 = IdUtil.normalToDelimited(this.getCatalogName());
        final SystemColumn[] buildColumnList = this.buildColumnList();
        final StringBuffer sb = new StringBuffer();
        final StringBuffer sb2 = new StringBuffer();
        final StringBuffer sb3 = new StringBuffer();
        for (int i = 0; i < buildColumnList.length; ++i) {
            if (i == 0) {
                sb.append("(");
                sb2.append("(");
                sb3.append("(");
            }
            else {
                sb.append(",");
                sb2.append(",");
                sb3.append(",");
            }
            sb.append(buildColumnList[i].getName());
            sb2.append(buildColumnList[i].getName());
            sb3.append("?");
            sb.append(" ");
            sb.append(buildColumnList[i].getType().getCatalogType().getSQLstring());
        }
        sb.append(")");
        sb2.append(")");
        sb3.append(")");
        final String string = "create table " + normalToDelimited + "." + normalToDelimited2 + sb.toString();
        this.tableInsertStmt = "insert into " + normalToDelimited + "." + normalToDelimited2 + sb2.toString() + " values " + sb3.toString();
        return new String[] { string };
    }
    
    public String getTableInsert() {
        return this.tableInsertStmt;
    }
}
