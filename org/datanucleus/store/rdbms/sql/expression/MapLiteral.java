// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql.expression;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.ArrayList;
import java.util.List;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.sql.SQLTable;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import java.util.Map;

public class MapLiteral extends MapExpression implements SQLLiteral
{
    private final Map value;
    private final MapValueLiteral mapValueLiteral;
    private final MapKeyLiteral mapKeyLiteral;
    
    public MapLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value, final String parameterName) {
        super(stmt, null, mapping);
        this.parameterName = parameterName;
        if (value == null) {
            this.value = null;
            this.mapKeyLiteral = null;
            this.mapValueLiteral = null;
        }
        else {
            if (!(value instanceof Map)) {
                throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + value.getClass().getName());
            }
            final Map mapValue = (Map)value;
            this.value = mapValue;
            if (parameterName != null) {
                this.mapKeyLiteral = null;
                this.mapValueLiteral = null;
                this.st.appendParameter(parameterName, mapping, this.value);
            }
            else {
                this.mapValueLiteral = new MapValueLiteral(stmt, mapping, value);
                this.mapKeyLiteral = new MapKeyLiteral(stmt, mapping, value);
            }
        }
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    public MapKeyLiteral getKeyLiteral() {
        return this.mapKeyLiteral;
    }
    
    public MapValueLiteral getValueLiteral() {
        return this.mapValueLiteral;
    }
    
    @Override
    public void setNotParameter() {
        if (this.parameterName == null) {
            return;
        }
        this.parameterName = null;
        this.st.clearStatement();
    }
    
    public static class MapKeyLiteral extends SQLExpression implements SQLLiteral
    {
        private final Map value;
        private List<SQLExpression> keyExpressions;
        
        public MapKeyLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value) {
            super(stmt, null, mapping);
            if (value instanceof Map) {
                final Map mapValue = (Map)value;
                this.value = mapValue;
                this.setStatement();
                return;
            }
            throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + ((value != null) ? value.getClass().getName() : null));
        }
        
        public List<SQLExpression> getKeyExpressions() {
            return this.keyExpressions;
        }
        
        @Override
        public SQLExpression invoke(final String methodName, final List args) {
            if (!methodName.equals("get") || args.size() != 1) {
                return super.invoke(methodName, args);
            }
            final SQLExpression argExpr = args.get(0);
            if (!(argExpr instanceof SQLLiteral)) {
                throw new IllegalExpressionOperationException(this, "get", argExpr);
            }
            final Object val = this.value.get(((SQLLiteral)argExpr).getValue());
            if (val == null) {
                return new NullLiteral(this.stmt, null, null, null);
            }
            final JavaTypeMapping m = this.stmt.getRDBMSManager().getSQLExpressionFactory().getMappingForType(val.getClass(), false);
            return new ObjectLiteral(this.stmt, m, val, null);
        }
        
        @Override
        public Object getValue() {
            return this.value;
        }
        
        @Override
        public void setNotParameter() {
            if (this.parameterName == null) {
                return;
            }
            this.parameterName = null;
            this.st.clearStatement();
            this.setStatement();
        }
        
        protected void setStatement() {
            final boolean isEmpty = this.value == null || this.value.size() == 0;
            if (!isEmpty) {
                final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
                this.st.append("(");
                this.keyExpressions = new ArrayList<SQLExpression>();
                boolean hadPrev = false;
                final Set keys = this.value.keySet();
                for (final Object current : keys) {
                    if (null != current) {
                        final JavaTypeMapping m = storeMgr.getSQLExpressionFactory().getMappingForType(current.getClass(), false);
                        final SQLExpression expr = storeMgr.getSQLExpressionFactory().newLiteral(this.stmt, m, current);
                        this.st.append(hadPrev ? "," : "");
                        this.st.append(expr);
                        this.keyExpressions.add(expr);
                        hadPrev = true;
                    }
                }
                this.st.append(")");
            }
        }
    }
    
    public static class MapValueLiteral extends SQLExpression implements SQLLiteral
    {
        private final Map value;
        private List<SQLExpression> valueExpressions;
        
        public MapValueLiteral(final SQLStatement stmt, final JavaTypeMapping mapping, final Object value) {
            super(stmt, null, mapping);
            if (value instanceof Map) {
                final Map mapValue = (Map)value;
                this.value = mapValue;
                this.setStatement();
                return;
            }
            throw new NucleusException("Cannot create " + this.getClass().getName() + " for value of type " + ((value != null) ? value.getClass().getName() : null));
        }
        
        public List<SQLExpression> getValueExpressions() {
            return this.valueExpressions;
        }
        
        @Override
        public Object getValue() {
            return this.value;
        }
        
        @Override
        public void setNotParameter() {
            if (this.parameterName == null) {
                return;
            }
            this.parameterName = null;
            this.st.clearStatement();
            this.setStatement();
        }
        
        protected void setStatement() {
            final boolean isEmpty = this.value == null || this.value.size() == 0;
            if (!isEmpty) {
                final RDBMSStoreManager storeMgr = this.stmt.getRDBMSManager();
                this.valueExpressions = new ArrayList<SQLExpression>();
                this.st.append("(");
                boolean hadPrev = false;
                final Collection values = this.value.values();
                for (final Object current : values) {
                    if (null != current) {
                        final JavaTypeMapping m = storeMgr.getSQLExpressionFactory().getMappingForType(current.getClass(), false);
                        final SQLExpression expr = storeMgr.getSQLExpressionFactory().newLiteral(this.stmt, m, current);
                        this.st.append(hadPrev ? "," : "");
                        this.st.append(expr);
                        this.valueExpressions.add(expr);
                        hadPrev = true;
                    }
                }
                this.st.append(")");
            }
        }
    }
}
