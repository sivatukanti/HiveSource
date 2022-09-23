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

public class JPQLSingleStringParser
{
    protected static final Localiser LOCALISER;
    private Query query;
    private String queryString;
    
    public JPQLSingleStringParser(final Query query, final String queryString) {
        if (NucleusLogger.QUERY.isDebugEnabled()) {
            NucleusLogger.QUERY.debug(JPQLSingleStringParser.LOCALISER.msg("043000", queryString));
        }
        this.query = query;
        this.queryString = queryString;
    }
    
    public void parse() {
        new Compiler(new Parser(this.queryString)).compile();
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
            this.compileQuery();
            final String keyword = this.tokenizer.parseKeyword();
            if (keyword != null && JPQLQueryHelper.isKeyword(keyword)) {
                throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("043001", keyword));
            }
        }
        
        private void compileQuery() {
            boolean update = false;
            boolean delete = false;
            if (!this.tokenizer.parseKeywordIgnoreCase("SELECT")) {
                if (this.tokenizer.parseKeywordIgnoreCase("UPDATE")) {
                    update = true;
                    JPQLSingleStringParser.this.query.setType((short)1);
                }
                else {
                    if (!this.tokenizer.parseKeywordIgnoreCase("DELETE")) {
                        throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("043002"));
                    }
                    delete = true;
                    JPQLSingleStringParser.this.query.setType((short)2);
                }
            }
            if (update) {
                this.compileUpdate();
            }
            else if (!delete) {
                this.compileResult();
            }
            if (this.tokenizer.parseKeywordIgnoreCase("FROM")) {
                this.compileFrom();
            }
            if (this.tokenizer.parseKeywordIgnoreCase("WHERE")) {
                this.compileWhere();
            }
            if (this.tokenizer.parseKeywordIgnoreCase("GROUP BY")) {
                if (update || delete) {
                    throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("043007"));
                }
                this.compileGroup();
            }
            if (this.tokenizer.parseKeywordIgnoreCase("HAVING")) {
                if (update || delete) {
                    throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("043008"));
                }
                this.compileHaving();
            }
            if (this.tokenizer.parseKeywordIgnoreCase("ORDER BY")) {
                if (update || delete) {
                    throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("043009"));
                }
                this.compileOrder();
            }
        }
        
        private void compileResult() {
            final String content = this.tokenizer.parseContent(null, false);
            if (content.length() > 0) {
                JPQLSingleStringParser.this.query.setResult(content);
            }
        }
        
        private void compileUpdate() {
            final String content = this.tokenizer.parseContent(null, false);
            if (content.length() == 0) {
                throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("043010"));
            }
            final String contentUpper = content.toUpperCase();
            final int setIndex = contentUpper.indexOf("SET");
            if (setIndex < 0) {
                throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("043011"));
            }
            JPQLSingleStringParser.this.query.setFrom(content.substring(0, setIndex).trim());
            JPQLSingleStringParser.this.query.setUpdate(content.substring(setIndex + 3).trim());
        }
        
        private void compileFrom() {
            final String content = this.tokenizer.parseContent(null, false);
            if (content.length() > 0) {
                JPQLSingleStringParser.this.query.setFrom(content);
            }
        }
        
        private void compileWhere() {
            final String content = this.tokenizer.parseContent("FROM", true);
            if (content.length() == 0) {
                throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("043004", "WHERE", "<filter>"));
            }
            final String contentUpper = content.toUpperCase();
            if (contentUpper.indexOf("SELECT ") > 0) {
                this.processFilterContent(content);
            }
            else {
                JPQLSingleStringParser.this.query.setFilter(content);
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
                    if (remains.toUpperCase().startsWith("SELECT")) {
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
                            throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("042017"));
                        }
                        final String subqueryStr = content.substring(i + 1, endPosition).trim();
                        final String subqueryVarName = "DATANUCLEUS_SUBQUERY_" + subqueryNum;
                        final Query subquery = (Query)ClassUtils.newInstance(JPQLSingleStringParser.this.query.getClass(), new Class[] { ClassConstants.STORE_MANAGER, ClassConstants.EXECUTION_CONTEXT, String.class }, new Object[] { JPQLSingleStringParser.this.query.getStoreManager(), JPQLSingleStringParser.this.query.getExecutionContext(), subqueryStr });
                        JPQLSingleStringParser.this.query.addSubquery(subquery, "double " + subqueryVarName, null, null);
                        stringContent.append(subqueryVarName);
                        i = endPosition;
                        ++subqueryNum;
                        subqueryProcessed = true;
                    }
                }
                if (!subqueryProcessed) {
                    stringContent.append(chr);
                }
            }
            if (withinLiteralDouble || withinLiteralSingle) {
                throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("042017"));
            }
            JPQLSingleStringParser.this.query.setFilter(stringContent.toString());
        }
        
        private void compileGroup() {
            final String content = this.tokenizer.parseContent(null, false);
            if (content.length() == 0) {
                throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("043004", "GROUP BY", "<grouping>"));
            }
            JPQLSingleStringParser.this.query.setGrouping(content);
        }
        
        private void compileHaving() {
            final String content = this.tokenizer.parseContent("FROM", true);
            if (content.length() == 0) {
                throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("043004", "HAVING", "<having>"));
            }
            JPQLSingleStringParser.this.query.setHaving(content);
        }
        
        private void compileOrder() {
            final String content = this.tokenizer.parseContent(null, false);
            if (content.length() == 0) {
                throw new NucleusUserException(JPQLSingleStringParser.LOCALISER.msg("043004", "ORDER BY", "<ordering>"));
            }
            JPQLSingleStringParser.this.query.setOrdering(content);
        }
    }
    
    private static class Parser
    {
        final String queryString;
        int queryStringPos;
        final String[] tokens;
        final String[] keywords;
        int tokenIndex;
        
        public Parser(final String str) {
            this.queryStringPos = 0;
            this.tokenIndex = -1;
            this.queryString = str;
            final StringTokenizer tokenizer = new StringTokenizer(str);
            this.tokens = new String[tokenizer.countTokens()];
            this.keywords = new String[tokenizer.countTokens()];
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                this.tokens[i++] = tokenizer.nextToken();
            }
            for (i = 0; i < this.tokens.length; ++i) {
                if (JPQLQueryHelper.isKeyword(this.tokens[i])) {
                    this.keywords[i] = this.tokens[i];
                }
                else if (i < this.tokens.length - 1 && JPQLQueryHelper.isKeyword(this.tokens[i] + ' ' + this.tokens[i + 1])) {
                    this.keywords[i] = this.tokens[i];
                    ++i;
                    this.keywords[i] = this.tokens[i];
                }
            }
        }
        
        public String parseContent(final String keywordToIgnore, final boolean allowSubentries) {
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
                if (level == 0 && JPQLQueryHelper.isKeyword(this.tokens[this.tokenIndex]) && !this.tokens[this.tokenIndex].equals(keywordToIgnore)) {
                    --this.tokenIndex;
                    break;
                }
                if (level == 0 && this.tokenIndex < this.tokens.length - 1 && JPQLQueryHelper.isKeyword(this.tokens[this.tokenIndex] + ' ' + this.tokens[this.tokenIndex + 1])) {
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
        
        public boolean parseKeywordIgnoreCase(final String keyword) {
            if (this.tokenIndex < this.tokens.length - 1) {
                ++this.tokenIndex;
                if (this.keywords[this.tokenIndex] != null) {
                    if (this.keywords[this.tokenIndex].equalsIgnoreCase(keyword)) {
                        this.queryStringPos = this.queryString.indexOf(this.keywords[this.tokenIndex], this.queryStringPos) + this.keywords[this.tokenIndex].length() + 1;
                        return true;
                    }
                    if (keyword.indexOf(32) > -1 && (this.keywords[this.tokenIndex] + ' ' + this.keywords[this.tokenIndex + 1]).equalsIgnoreCase(keyword)) {
                        this.queryStringPos = this.queryString.indexOf(this.keywords[this.tokenIndex], this.queryStringPos) + this.keywords[this.tokenIndex].length() + 1;
                        this.queryStringPos = this.queryString.indexOf(this.keywords[this.tokenIndex + 1], this.queryStringPos) + this.keywords[this.tokenIndex + 1].length() + 1;
                        ++this.tokenIndex;
                        return true;
                    }
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
