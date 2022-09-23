// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.scostore.IteratorStatement;
import java.util.Map;
import org.datanucleus.store.rdbms.sql.SQLStatementParameter;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import java.util.List;

public class RDBMSQueryCompilation
{
    String sql;
    List<String> sqls;
    List<Boolean> sqlUseInCountFlags;
    StatementClassMapping resultsDefinitionForClass;
    StatementResultMapping resultsDefinition;
    List<SQLStatementParameter> inputParameters;
    Map<Integer, String> inputParameterNameByPosition;
    Map<String, IteratorStatement> scoIteratorStatementByMemberName;
    boolean precompilable;
    
    public RDBMSQueryCompilation() {
        this.sql = null;
        this.sqls = null;
        this.sqlUseInCountFlags = null;
        this.resultsDefinitionForClass = null;
        this.resultsDefinition = null;
        this.precompilable = true;
    }
    
    public void setSQL(final String sql) {
        this.sql = sql;
        this.sqls = null;
    }
    
    public void setSQL(final List<String> sqls, final List<Boolean> sqlUseInCountFlags) {
        this.sql = null;
        if (this.sqls == null) {
            this.sqls = new ArrayList<String>(sqls.size());
        }
        this.sqls.addAll(sqls);
        if (this.sqlUseInCountFlags == null) {
            this.sqlUseInCountFlags = new ArrayList<Boolean>(sqlUseInCountFlags.size());
        }
        this.sqlUseInCountFlags.addAll(sqlUseInCountFlags);
    }
    
    public String getSQL() {
        return this.sql;
    }
    
    public List<String> getSQLs() {
        return this.sqls;
    }
    
    public List<Boolean> getSQLUseInCountFlags() {
        return this.sqlUseInCountFlags;
    }
    
    public void setPrecompilable(final boolean precompilable) {
        this.precompilable = precompilable;
    }
    
    public boolean isPrecompilable() {
        return this.precompilable;
    }
    
    public void setResultDefinitionForClass(final StatementClassMapping def) {
        this.resultsDefinitionForClass = def;
    }
    
    public StatementClassMapping getResultDefinitionForClass() {
        return this.resultsDefinitionForClass;
    }
    
    public void setResultDefinition(final StatementResultMapping def) {
        this.resultsDefinition = def;
    }
    
    public StatementResultMapping getResultDefinition() {
        return this.resultsDefinition;
    }
    
    public void setStatementParameters(final List<SQLStatementParameter> params) {
        this.inputParameters = params;
    }
    
    public List<SQLStatementParameter> getStatementParameters() {
        return this.inputParameters;
    }
    
    public void setParameterNameByPosition(final Map<Integer, String> paramNameByPos) {
        this.inputParameterNameByPosition = paramNameByPos;
    }
    
    public Map<Integer, String> getParameterNameByPosition() {
        return this.inputParameterNameByPosition;
    }
    
    public void setSCOIteratorStatement(final String memberName, final IteratorStatement iterStmt) {
        if (this.scoIteratorStatementByMemberName == null) {
            this.scoIteratorStatementByMemberName = new HashMap<String, IteratorStatement>();
        }
        this.scoIteratorStatementByMemberName.put(memberName, iterStmt);
    }
    
    public Map<String, IteratorStatement> getSCOIteratorStatements() {
        return this.scoIteratorStatementByMemberName;
    }
}
