// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.mapping.MappingHelper;
import org.datanucleus.metadata.IdentityType;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import java.util.Iterator;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import java.util.Collection;
import org.datanucleus.store.rdbms.sql.expression.SQLExpression;
import java.util.ArrayList;
import java.util.List;

public class SQLText
{
    private String sql;
    private List<SQLStatementParameter> parameters;
    private boolean encloseInParentheses;
    private String postpend;
    private List appended;
    
    public SQLText() {
        this.parameters = null;
        this.encloseInParentheses = false;
        this.appended = new ArrayList();
    }
    
    public SQLText(final String initialSQLText) {
        this();
        this.append(initialSQLText);
    }
    
    public void clearStatement() {
        this.sql = null;
        this.appended.clear();
    }
    
    public void encloseInParentheses() {
        this.sql = null;
        this.encloseInParentheses = true;
    }
    
    public SQLText postpend(final String s) {
        this.sql = null;
        this.postpend = s;
        return this;
    }
    
    public SQLText prepend(final String s) {
        this.sql = null;
        this.appended.add(0, s);
        return this;
    }
    
    public SQLText append(final char c) {
        this.sql = null;
        this.appended.add(c);
        return this;
    }
    
    public SQLText append(final String s) {
        this.sql = null;
        this.appended.add(s);
        return this;
    }
    
    public SQLText append(final SQLStatement stmt) {
        this.sql = null;
        this.appended.add(stmt);
        return this;
    }
    
    public SQLText append(final SQLExpression.ColumnExpressionList exprList) {
        this.sql = null;
        this.appended.add(exprList);
        return this;
    }
    
    public SQLText append(final SQLText st) {
        this.sql = null;
        this.appended.add(st.toSQL());
        if (st.parameters != null) {
            if (this.parameters == null) {
                this.parameters = new ArrayList<SQLStatementParameter>();
            }
            this.parameters.addAll(st.parameters);
        }
        return this;
    }
    
    public SQLText append(final SQLExpression expr) {
        this.sql = null;
        this.appended.add(expr);
        return this;
    }
    
    public SQLText appendParameter(final String name, final JavaTypeMapping mapping, final Object value) {
        return this.appendParameter(name, mapping, value, -1);
    }
    
    public SQLText appendParameter(final String name, final JavaTypeMapping mapping, final Object value, final int columnNumber) {
        this.sql = null;
        this.appended.add(new SQLStatementParameter(name, mapping, value, columnNumber));
        return this;
    }
    
    public void changeMappingForParameter(final String parameterName, final JavaTypeMapping mapping) {
        for (final Object obj : this.appended) {
            if (obj instanceof SQLStatementParameter) {
                final SQLStatementParameter param = (SQLStatementParameter)obj;
                if (!param.getName().equalsIgnoreCase(parameterName)) {
                    continue;
                }
                param.setMapping(mapping);
            }
        }
    }
    
    public void applyParametersToStatement(final ExecutionContext ec, final PreparedStatement ps) {
        if (this.parameters != null) {
            int num = 1;
            for (final SQLStatementParameter param : this.parameters) {
                final JavaTypeMapping mapping = param.getMapping();
                if (mapping != null) {
                    final Object value = param.getValue();
                    if (param.getColumnNumber() >= 0) {
                        Object colValue = null;
                        if (value != null) {
                            final ClassLoaderResolver clr = ec.getClassLoaderResolver();
                            final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(mapping.getType(), clr);
                            final RDBMSStoreManager storeMgr = mapping.getStoreManager();
                            if (cmd.getIdentityType() == IdentityType.DATASTORE) {
                                colValue = mapping.getValueForDatastoreMapping(ec.getNucleusContext(), param.getColumnNumber(), value);
                            }
                            else if (cmd.getIdentityType() == IdentityType.APPLICATION) {
                                colValue = SQLStatementHelper.getValueForPrimaryKeyIndexOfObjectUsingReflection(value, param.getColumnNumber(), cmd, storeMgr, clr);
                            }
                        }
                        mapping.getDatastoreMapping(param.getColumnNumber()).setObject(ps, num, colValue);
                        ++num;
                    }
                    else {
                        mapping.setObject(ec, ps, MappingHelper.getMappingIndices(num, mapping), value);
                        if (mapping.getNumberOfDatastoreMappings() > 0) {
                            num += mapping.getNumberOfDatastoreMappings();
                        }
                        else {
                            ++num;
                        }
                    }
                }
            }
        }
    }
    
    public List<SQLStatementParameter> getParametersForStatement() {
        return this.parameters;
    }
    
    public String toSQL() {
        if (this.sql != null) {
            return this.sql;
        }
        final StringBuffer sql = new StringBuffer();
        if (this.encloseInParentheses) {
            sql.append("(");
        }
        for (int i = 0; i < this.appended.size(); ++i) {
            final Object item = this.appended.get(i);
            if (item instanceof SQLExpression) {
                final SQLExpression expr = (SQLExpression)item;
                final SQLText st = expr.toSQLText();
                sql.append(st.toSQL());
                if (st.parameters != null) {
                    if (this.parameters == null) {
                        this.parameters = new ArrayList<SQLStatementParameter>();
                    }
                    this.parameters.addAll(st.parameters);
                }
            }
            else if (item instanceof SQLStatementParameter) {
                final SQLStatementParameter param = (SQLStatementParameter)item;
                sql.append('?');
                if (this.parameters == null) {
                    this.parameters = new ArrayList<SQLStatementParameter>();
                }
                this.parameters.add(param);
            }
            else if (item instanceof SQLStatement) {
                final SQLStatement stmt = (SQLStatement)item;
                final SQLText st = stmt.getSelectStatement();
                sql.append(st.toSQL());
                if (st.parameters != null) {
                    if (this.parameters == null) {
                        this.parameters = new ArrayList<SQLStatementParameter>();
                    }
                    this.parameters.addAll(st.parameters);
                }
            }
            else if (item instanceof SQLText) {
                final SQLText st2 = (SQLText)item;
                sql.append(st2.toSQL());
                if (st2.parameters != null) {
                    if (this.parameters == null) {
                        this.parameters = new ArrayList<SQLStatementParameter>();
                    }
                    this.parameters.addAll(st2.parameters);
                }
            }
            else {
                sql.append(item);
            }
        }
        if (this.encloseInParentheses) {
            sql.append(")");
        }
        sql.append((this.postpend == null) ? "" : this.postpend);
        return this.sql = sql.toString();
    }
    
    @Override
    public String toString() {
        return this.toSQL();
    }
}
