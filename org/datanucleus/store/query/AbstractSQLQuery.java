// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import org.datanucleus.store.Extent;
import org.datanucleus.PersistenceConfiguration;
import java.util.StringTokenizer;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;
import org.datanucleus.metadata.QueryResultMetaData;

public abstract class AbstractSQLQuery extends Query
{
    protected final transient String inputSQL;
    protected transient String compiledSQL;
    protected QueryResultMetaData resultMetaData;
    
    public AbstractSQLQuery(final StoreManager storeMgr, final ExecutionContext ec, final AbstractSQLQuery query) {
        this(storeMgr, ec, query.inputSQL);
    }
    
    public AbstractSQLQuery(final StoreManager storeMgr, final ExecutionContext ec, final String sqlText) {
        super(storeMgr, ec);
        this.compiledSQL = null;
        this.resultMetaData = null;
        this.candidateClass = null;
        this.filter = null;
        this.imports = null;
        this.explicitVariables = null;
        this.explicitParameters = null;
        this.ordering = null;
        if (sqlText == null) {
            throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059001"));
        }
        this.inputSQL = sqlText.replace("\n", " ").trim();
        final String firstToken = new StringTokenizer(this.inputSQL, " ").nextToken();
        if (firstToken.equalsIgnoreCase("SELECT")) {
            this.type = 0;
        }
        else if (firstToken.equalsIgnoreCase("DELETE")) {
            this.type = 2;
            this.unique = true;
        }
        else if (firstToken.equalsIgnoreCase("UPDATE") || firstToken.equalsIgnoreCase("INSERT") || firstToken.equalsIgnoreCase("MERGE") || firstToken.equalsIgnoreCase("CREATE")) {
            this.type = 1;
            this.unique = true;
        }
        else {
            this.type = 3;
            this.unique = true;
        }
        final PersistenceConfiguration conf = ec.getNucleusContext().getPersistenceConfiguration();
        if (ec.getApiAdapter().getName().equalsIgnoreCase("JDO")) {
            boolean allowAllSyntax = conf.getBooleanProperty("datanucleus.query.sql.allowAll");
            if (ec.getProperty("datanucleus.query.sql.allowAll") != null) {
                allowAllSyntax = ec.getBooleanProperty("datanucleus.query.sql.allowAll");
            }
            if (!allowAllSyntax && !firstToken.equals("SELECT") && !firstToken.startsWith("select")) {
                throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059002", this.inputSQL));
            }
        }
    }
    
    @Override
    public String getLanguage() {
        return "SQL";
    }
    
    @Override
    protected void discardCompiled() {
        super.discardCompiled();
        this.compiledSQL = null;
    }
    
    public String getInputSQL() {
        return this.inputSQL;
    }
    
    @Override
    public void setCandidates(final Extent pcs) {
        throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059004"));
    }
    
    @Override
    public void setCandidates(final Collection pcs) {
        throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059005"));
    }
    
    @Override
    public void setResult(final String result) {
        throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059006"));
    }
    
    @Override
    public void setResultMetaData(final QueryResultMetaData qrmd) {
        this.resultMetaData = qrmd;
        super.setResultClass(null);
    }
    
    @Override
    public void setResultClass(final Class result_cls) {
        super.setResultClass(result_cls);
        this.resultMetaData = null;
    }
    
    public void setRange(final int fromIncl, final int toExcl) {
        throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059007"));
    }
    
    @Override
    public void setSubclasses(final boolean subclasses) {
        throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059004"));
    }
    
    @Override
    public void setFilter(final String filter) {
        throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059008"));
    }
    
    @Override
    public void declareExplicitVariables(final String variables) {
        throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059009"));
    }
    
    @Override
    public void declareExplicitParameters(final String parameters) {
        throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059016"));
    }
    
    @Override
    public void declareImports(final String imports) {
        throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059026"));
    }
    
    @Override
    public void setGrouping(final String grouping) {
        throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059010"));
    }
    
    @Override
    public void setOrdering(final String ordering) {
        throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059011"));
    }
    
    @Override
    protected long performDeletePersistentAll(final Map parameters) {
        throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059000"));
    }
    
    @Override
    public Object executeWithArray(final Object[] parameters) {
        final HashMap parameterMap = new HashMap();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                parameterMap.put(i + 1, parameters[i]);
            }
        }
        final Map executionMap = this.prepareForExecution(parameterMap);
        return super.executeQuery(executionMap);
    }
    
    @Override
    public Object executeWithMap(final Map executeParameters) {
        final Map executionMap = this.prepareForExecution(executeParameters);
        return super.executeQuery(executionMap);
    }
    
    protected Map prepareForExecution(final Map executeParameters) {
        final Map params = new HashMap();
        if (this.implicitParameters != null) {
            params.putAll(this.implicitParameters);
        }
        if (executeParameters != null) {
            params.putAll(executeParameters);
        }
        this.compileInternal(executeParameters);
        final List paramNames = new ArrayList();
        final Collection expectedParams = new ArrayList();
        boolean complete = false;
        int charPos = 0;
        final char[] statement = this.compiledSQL.toCharArray();
        StringBuffer paramName = null;
        int paramPos = 0;
        boolean colonParam = true;
        final StringBuffer runtimeJdbcText = new StringBuffer();
        while (!complete) {
            final char c = statement[charPos];
            boolean endOfParam = false;
            if (c == '?') {
                colonParam = false;
                ++paramPos;
                paramName = new StringBuffer();
            }
            else if (c == ':') {
                if (charPos > 0) {
                    final char prev = statement[charPos - 1];
                    if (!Character.isLetterOrDigit(prev)) {
                        colonParam = true;
                        ++paramPos;
                        paramName = new StringBuffer();
                    }
                }
                else {
                    colonParam = true;
                    ++paramPos;
                    paramName = new StringBuffer();
                }
            }
            else if (paramName != null) {
                if (Character.isLetterOrDigit(c)) {
                    paramName.append(c);
                }
                else {
                    endOfParam = true;
                }
            }
            if (paramName != null) {
                if (endOfParam) {
                    runtimeJdbcText.append('?');
                    runtimeJdbcText.append(c);
                }
            }
            else {
                runtimeJdbcText.append(c);
            }
            complete = (++charPos == this.compiledSQL.length());
            if (complete && paramName != null && !endOfParam) {
                runtimeJdbcText.append('?');
            }
            if (paramName != null && (complete || endOfParam)) {
                Label_0438: {
                    if (paramName.length() > 0) {
                        if (colonParam) {
                            expectedParams.add(paramName.toString());
                            break Label_0438;
                        }
                        try {
                            final Integer num = Integer.valueOf(paramName.toString());
                            expectedParams.add(num);
                            break Label_0438;
                        }
                        catch (NumberFormatException nfe) {
                            throw new NucleusUserException("SQL query " + this.inputSQL + " contains an invalid parameter specification " + paramName.toString());
                        }
                    }
                    if (!colonParam) {
                        expectedParams.add(paramPos);
                    }
                }
                paramName = null;
            }
        }
        this.compiledSQL = runtimeJdbcText.toString();
        if (expectedParams.size() > 0 && params.isEmpty()) {
            throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059028", this.inputSQL, "" + expectedParams.size()));
        }
        final Map executeMap = new HashMap();
        paramPos = 1;
        for (final Object key : expectedParams) {
            if (!params.containsKey(key)) {
                throw new NucleusUserException(AbstractSQLQuery.LOCALISER.msg("059030", this.inputSQL, "" + key));
            }
            executeMap.put(paramPos, params.get(key));
            paramNames.add("" + paramPos);
            ++paramPos;
        }
        this.parameterNames = paramNames.toArray(new String[paramNames.size()]);
        return executeMap;
    }
    
    @Override
    protected boolean shouldReturnSingleRow() {
        return this.unique;
    }
}
