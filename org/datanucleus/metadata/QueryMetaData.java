// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;

public class QueryMetaData extends MetaData
{
    protected String scope;
    protected String name;
    protected String language;
    protected boolean unmodifiable;
    protected String query;
    protected String resultClass;
    protected String resultMetaDataName;
    protected boolean unique;
    protected String fetchPlanName;
    
    public QueryMetaData(final String name) {
        this.unmodifiable = false;
        this.resultClass = null;
        this.resultMetaDataName = null;
        this.unique = false;
        this.fetchPlanName = null;
        this.name = name;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public QueryMetaData setScope(final String scope) {
        this.scope = (StringUtils.isWhitespace(scope) ? null : scope);
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getLanguage() {
        if (this.language == null) {
            this.language = QueryLanguage.JDOQL.toString();
        }
        return this.language;
    }
    
    public QueryMetaData setLanguage(final String language) {
        if (!StringUtils.isWhitespace(language)) {
            this.language = language;
            if (this.language.equals("javax.jdo.query.JDOQL")) {
                this.language = QueryLanguage.JDOQL.toString();
            }
            else if (this.language.equals("javax.jdo.query.SQL")) {
                this.language = QueryLanguage.SQL.toString();
            }
            else if (this.language.equals("javax.jdo.query.JPQL")) {
                this.language = QueryLanguage.JPQL.toString();
            }
        }
        return this;
    }
    
    public boolean isUnmodifiable() {
        return this.unmodifiable;
    }
    
    public QueryMetaData setUnmodifiable(final boolean unmodifiable) {
        this.unmodifiable = unmodifiable;
        return this;
    }
    
    public QueryMetaData setUnmodifiable(final String unmodifiable) {
        if (!StringUtils.isWhitespace(unmodifiable)) {
            this.unmodifiable = Boolean.parseBoolean(unmodifiable);
        }
        return this;
    }
    
    public String getQuery() {
        return this.query;
    }
    
    public QueryMetaData setQuery(final String query) {
        this.query = query;
        return this;
    }
    
    public String getResultClass() {
        return this.resultClass;
    }
    
    public QueryMetaData setResultClass(final String resultClass) {
        this.resultClass = (StringUtils.isWhitespace(resultClass) ? null : resultClass);
        return this;
    }
    
    public String getResultMetaDataName() {
        return this.resultMetaDataName;
    }
    
    public QueryMetaData setResultMetaDataName(final String mdName) {
        this.resultMetaDataName = (StringUtils.isWhitespace(mdName) ? null : mdName);
        return this;
    }
    
    public boolean isUnique() {
        return this.unique;
    }
    
    public QueryMetaData setUnique(final boolean unique) {
        this.unique = unique;
        return this;
    }
    
    public QueryMetaData setUnique(final String unique) {
        if (!StringUtils.isWhitespace(unique)) {
            this.unique = Boolean.parseBoolean(unique);
        }
        return this;
    }
    
    public String getFetchPlanName() {
        return this.fetchPlanName;
    }
    
    public QueryMetaData setFetchPlanName(final String fpName) {
        this.fetchPlanName = (StringUtils.isWhitespace(fpName) ? null : fpName);
        return this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<query name=\"" + this.name + "\"\n");
        sb.append(prefix).append("       language=\"" + this.language + "\"\n");
        if (this.unique) {
            sb.append(prefix).append("       unique=\"true\"\n");
        }
        if (this.resultClass != null) {
            sb.append(prefix).append("       result-class=\"" + this.resultClass + "\"\n");
        }
        if (this.fetchPlanName != null) {
            sb.append(prefix).append("       fetch-plan=\"" + this.fetchPlanName + "\"\n");
        }
        sb.append(prefix).append("       unmodifiable=\"" + this.unmodifiable + "\">\n");
        sb.append(prefix).append(this.query).append("\n");
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix + "</query>\n");
        return sb.toString();
    }
}
