// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query;

import java.util.StringTokenizer;
import java.util.Map;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ClassConstants;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.query.Query;
import org.datanucleus.util.Localiser;

public class JDOQLSingleStringParser
{
    protected static final Localiser LOCALISER;
    private Query query;
    private String queryString;
    boolean allowDelete;
    boolean allowUpdate;
    
    public JDOQLSingleStringParser(final Query query, final String queryString) {
        this.allowDelete = false;
        this.allowUpdate = false;
        NucleusLogger.QUERY.debug(JDOQLSingleStringParser.LOCALISER.msg("042010", queryString));
        this.query = query;
        this.queryString = queryString;
    }
    
    public void setAllowDelete(final boolean allow) {
        this.allowDelete = allow;
    }
    
    public void setAllowUpdate(final boolean allow) {
        this.allowUpdate = allow;
    }
    
    public void parse() {
        new Compiler(new Parser(this.queryString, this.allowDelete || this.allowUpdate)).compile();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    private class Compiler
    {
        Parser tokenizer;
        
        Compiler(final Parser tokenizer) {
            this.tokenizer = tokenizer;
        }
        
        private void compile() {
            this.compileSelect();
            final String keyword = this.tokenizer.parseKeyword();
            if (keyword != null && JDOQLQueryHelper.isKeyword(keyword)) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042011", keyword));
            }
        }
        
        private void compileSelect() {
            boolean update = false;
            boolean delete = false;
            if (JDOQLSingleStringParser.this.allowUpdate && (this.tokenizer.parseKeyword("UPDATE") || this.tokenizer.parseKeyword("update"))) {
                update = true;
                JDOQLSingleStringParser.this.query.setType((short)1);
            }
            else if (JDOQLSingleStringParser.this.allowDelete && (this.tokenizer.parseKeyword("DELETE") || this.tokenizer.parseKeyword("delete"))) {
                delete = true;
                JDOQLSingleStringParser.this.query.setType((short)2);
            }
            else if (!this.tokenizer.parseKeyword("SELECT")) {
                if (!this.tokenizer.parseKeyword("select")) {
                    throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042012"));
                }
            }
            if (update) {
                this.compileUpdate();
            }
            else if (delete) {
                if (this.tokenizer.parseKeyword("FROM") || this.tokenizer.parseKeyword("from")) {
                    this.compileFrom();
                }
            }
            else {
                if (this.tokenizer.parseKeyword("UNIQUE") || this.tokenizer.parseKeyword("unique")) {
                    this.compileUnique();
                }
                this.compileResult();
                if (this.tokenizer.parseKeyword("INTO") || this.tokenizer.parseKeyword("into")) {
                    this.compileInto();
                }
                if (this.tokenizer.parseKeyword("FROM") || this.tokenizer.parseKeyword("from")) {
                    this.compileFrom();
                }
            }
            if (this.tokenizer.parseKeyword("WHERE") || this.tokenizer.parseKeyword("where")) {
                this.compileWhere();
            }
            if (this.tokenizer.parseKeyword("VARIABLES") || this.tokenizer.parseKeyword("variables")) {
                this.compileVariables();
            }
            if (this.tokenizer.parseKeyword("PARAMETERS") || this.tokenizer.parseKeyword("parameters")) {
                this.compileParameters();
            }
            if (this.tokenizer.parseKeyword("import")) {
                this.compileImport();
            }
            if (this.tokenizer.parseKeyword("GROUP") || this.tokenizer.parseKeyword("group")) {
                this.compileGroup();
            }
            if (this.tokenizer.parseKeyword("ORDER") || this.tokenizer.parseKeyword("order")) {
                this.compileOrder();
            }
            if (this.tokenizer.parseKeyword("RANGE") || this.tokenizer.parseKeyword("range")) {
                this.compileRange();
            }
        }
        
        private void compileUnique() {
            JDOQLSingleStringParser.this.query.setUnique(true);
        }
        
        private void compileResult() {
            final String content = this.tokenizer.parseContent(false);
            if (content.length() > 0) {
                JDOQLSingleStringParser.this.query.setResult(content);
            }
        }
        
        private void compileUpdate() {
            String content = this.tokenizer.parseContent(false);
            if (content.length() == 0) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("043010"));
            }
            JDOQLSingleStringParser.this.query.setFrom(content);
            JDOQLSingleStringParser.this.query.setCandidateClassName(content);
            if (this.tokenizer.parseKeyword("set") || this.tokenizer.parseKeyword("SET")) {
                content = this.tokenizer.parseContent(false);
                JDOQLSingleStringParser.this.query.setUpdate(content.trim());
                return;
            }
            throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("043011"));
        }
        
        private void compileInto() {
            final String content = this.tokenizer.parseContent(false);
            if (content.length() == 0) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042014", "INTO", "<result class>"));
            }
            final String resultClassName = content.trim();
            JDOQLSingleStringParser.this.query.setResultClassName(resultClassName);
        }
        
        private void compileFrom() {
            String content = this.tokenizer.parseContent(false);
            if (content.length() == 0) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042014", "FROM", "<candidate class>"));
            }
            if (content.indexOf(32) > 0) {
                JDOQLSingleStringParser.this.query.setFrom(content.trim());
            }
            else {
                JDOQLSingleStringParser.this.query.setCandidateClassName(content);
            }
            if (this.tokenizer.parseKeyword("EXCLUDE") || this.tokenizer.parseKeyword("exclude")) {
                if (!this.tokenizer.parseKeyword("SUBCLASSES") && !this.tokenizer.parseKeyword("subclasses")) {
                    throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042015", "SUBCLASSES", "EXCLUDE"));
                }
                content = this.tokenizer.parseContent(false);
                if (content.length() > 0) {
                    throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042013", "EXCLUDE SUBCLASSES", content));
                }
                JDOQLSingleStringParser.this.query.setSubclasses(false);
            }
        }
        
        private void compileWhere() {
            final String content = this.tokenizer.parseContent(true);
            if (content.length() == 0) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042014", "WHERE", "<filter>"));
            }
            if (content.indexOf("SELECT ") > 0 || content.indexOf("select ") > 0) {
                this.processFilterContent(content);
            }
            else {
                JDOQLSingleStringParser.this.query.setFilter(content);
            }
        }
        
        private void processFilterContent(final String content) {
            final StringBuilder stringContent = new StringBuilder();
            boolean withinLiteralDouble = false;
            boolean withinLiteralSingle = false;
            int subqueryNum = 1;
            for (int i = 0; i < content.length(); ++i) {
                boolean subqueryProcessed = false;
                final char chr = content.charAt(i);
                if (chr == '\"') {
                    withinLiteralDouble = !withinLiteralDouble;
                }
                else if (chr == '\'') {
                    withinLiteralSingle = !withinLiteralSingle;
                }
                if (!withinLiteralDouble && !withinLiteralSingle && chr == '(') {
                    String remains = content.substring(i + 1).trim();
                    if (remains.startsWith("select") || remains.startsWith("SELECT")) {
                        remains = content.substring(i);
                        int endPosition = -1;
                        int braceLevel = 0;
                        for (int j = 1; j < remains.length(); ++j) {
                            if (remains.charAt(j) == '(') {
                                ++braceLevel;
                            }
                            else if (remains.charAt(j) == ')' && --braceLevel < 0) {
                                endPosition = i + j;
                                break;
                            }
                        }
                        if (endPosition < 0) {
                            throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042017"));
                        }
                        final String subqueryStr = content.substring(i + 1, endPosition).trim();
                        final String subqueryVarName = "DATANUCLEUS_SUBQUERY_" + subqueryNum;
                        final Query subquery = (Query)ClassUtils.newInstance(JDOQLSingleStringParser.this.query.getClass(), new Class[] { ClassConstants.STORE_MANAGER, ClassConstants.EXECUTION_CONTEXT, String.class }, new Object[] { JDOQLSingleStringParser.this.query.getStoreManager(), JDOQLSingleStringParser.this.query.getExecutionContext(), subqueryStr });
                        JDOQLSingleStringParser.this.query.addSubquery(subquery, "double " + subqueryVarName, null, null);
                        stringContent.append(subqueryVarName);
                        i = endPosition;
                        subqueryProcessed = true;
                        ++subqueryNum;
                    }
                }
                if (!subqueryProcessed) {
                    stringContent.append(chr);
                }
            }
            if (withinLiteralDouble || withinLiteralSingle) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042017"));
            }
            JDOQLSingleStringParser.this.query.setFilter(stringContent.toString());
        }
        
        private void compileVariables() {
            final String content = this.tokenizer.parseContent(false);
            if (content.length() == 0) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042014", "VARIABLES", "<variable declarations>"));
            }
            JDOQLSingleStringParser.this.query.declareExplicitVariables(content);
        }
        
        private void compileParameters() {
            final String content = this.tokenizer.parseContent(false);
            if (content.length() == 0) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042014", "PARAMETERS", "<parameter declarations>"));
            }
            JDOQLSingleStringParser.this.query.declareExplicitParameters(content);
        }
        
        private void compileImport() {
            final StringBuilder content = new StringBuilder("import " + this.tokenizer.parseContent(false));
            while (this.tokenizer.parseKeyword("import")) {
                content.append("import ").append(this.tokenizer.parseContent(false));
            }
            JDOQLSingleStringParser.this.query.declareImports(content.toString());
        }
        
        private void compileGroup() {
            String content = this.tokenizer.parseContent(false);
            if (!this.tokenizer.parseKeyword("BY") && !this.tokenizer.parseKeyword("by")) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042015", "BY", "GROUP"));
            }
            content = this.tokenizer.parseContent(false);
            if (content.length() == 0) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042014", "GROUP BY", "<grouping>"));
            }
            JDOQLSingleStringParser.this.query.setGrouping(content);
        }
        
        private void compileOrder() {
            String content = this.tokenizer.parseContent(false);
            if (!this.tokenizer.parseKeyword("BY") && !this.tokenizer.parseKeyword("by")) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042015", "BY", "ORDER"));
            }
            content = this.tokenizer.parseContent(false);
            if (content.length() == 0) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042014", "ORDER BY", "<ordering>"));
            }
            JDOQLSingleStringParser.this.query.setOrdering(content);
        }
        
        private void compileRange() {
            final String content = this.tokenizer.parseContent(false);
            if (content.length() == 0) {
                throw new NucleusUserException(JDOQLSingleStringParser.LOCALISER.msg("042014", "RANGE", "<range>"));
            }
            JDOQLSingleStringParser.this.query.setRange(content);
        }
    }
    
    private static class Parser
    {
        final boolean extended;
        final String queryString;
        int queryStringPos;
        final String[] tokens;
        final String[] keywords;
        int tokenIndex;
        
        public Parser(final String str, final boolean extended) {
            this.queryStringPos = 0;
            this.tokenIndex = -1;
            this.queryString = str;
            this.extended = extended;
            final StringTokenizer tokenizer = new StringTokenizer(str);
            this.tokens = new String[tokenizer.countTokens()];
            this.keywords = new String[tokenizer.countTokens()];
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                this.tokens[i] = tokenizer.nextToken();
                if ((extended && JDOQLQueryHelper.isKeywordExtended(this.tokens[i])) || (!extended && JDOQLQueryHelper.isKeyword(this.tokens[i]))) {
                    this.keywords[i] = this.tokens[i];
                }
                ++i;
            }
        }
        
        public String parseContent(final boolean allowSubentries) {
            String content = "";
            int level = 0;
            while (this.tokenIndex < this.tokens.length - 1) {
                ++this.tokenIndex;
                if (allowSubentries) {
                    for (int i = 0; i < this.tokens[this.tokenIndex].length(); ++i) {
                        final char c = this.tokens[this.tokenIndex].charAt(i);
                        if (c == '(') {
                            ++level;
                        }
                        else if (c == ')') {
                            --level;
                        }
                    }
                }
                if (level == 0 && ((this.extended && JDOQLQueryHelper.isKeywordExtended(this.tokens[this.tokenIndex])) || (!this.extended && JDOQLQueryHelper.isKeyword(this.tokens[this.tokenIndex])))) {
                    --this.tokenIndex;
                    break;
                }
                final int endPos = this.queryString.indexOf(this.tokens[this.tokenIndex], this.queryStringPos) + this.tokens[this.tokenIndex].length();
                final String contentValue = this.queryString.substring(this.queryStringPos, endPos);
                this.queryStringPos = endPos;
                if (content.length() == 0) {
                    content = contentValue;
                }
                else {
                    content += contentValue;
                }
            }
            return content;
        }
        
        public boolean parseKeyword(final String keyword) {
            if (this.tokenIndex < this.tokens.length - 1) {
                ++this.tokenIndex;
                if (this.keywords[this.tokenIndex] != null && this.keywords[this.tokenIndex].equals(keyword)) {
                    this.queryStringPos = this.queryString.indexOf(this.keywords[this.tokenIndex], this.queryStringPos) + this.keywords[this.tokenIndex].length() + 1;
                    return true;
                }
                --this.tokenIndex;
            }
            return false;
        }
        
        public String parseKeyword() {
            if (this.tokenIndex < this.tokens.length - 1) {
                ++this.tokenIndex;
                if (this.keywords[this.tokenIndex] != null) {
                    return this.keywords[this.tokenIndex];
                }
                --this.tokenIndex;
            }
            return null;
        }
    }
}
