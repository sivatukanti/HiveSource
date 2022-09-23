// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAccessControlException;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthzPluginException;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveAuthzContext;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HivePrivilegeObject;
import java.util.List;
import org.apache.hadoop.hive.ql.security.authorization.plugin.HiveOperationType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.OperationState;
import org.apache.hive.service.cli.OperationType;
import org.apache.hive.service.cli.session.HiveSession;
import org.apache.hive.service.cli.TableSchema;

public abstract class MetadataOperation extends Operation
{
    protected static final String DEFAULT_HIVE_CATALOG = "";
    protected static TableSchema RESULT_SET_SCHEMA;
    private static final char SEARCH_STRING_ESCAPE = '\\';
    
    protected MetadataOperation(final HiveSession parentSession, final OperationType opType) {
        super(parentSession, opType, false);
        this.setHasResultSet(true);
    }
    
    @Override
    public void close() throws HiveSQLException {
        this.setState(OperationState.CLOSED);
        this.cleanupOperationLog();
    }
    
    protected String convertIdentifierPattern(final String pattern, final boolean datanucleusFormat) {
        if (pattern == null) {
            return this.convertPattern("%", true);
        }
        return this.convertPattern(pattern, datanucleusFormat);
    }
    
    protected String convertSchemaPattern(final String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return this.convertPattern("%", true);
        }
        return this.convertPattern(pattern, true);
    }
    
    private String convertPattern(final String pattern, final boolean datanucleusFormat) {
        String wStr;
        if (datanucleusFormat) {
            wStr = "*";
        }
        else {
            wStr = ".*";
        }
        return pattern.replaceAll("([^\\\\])%", "$1" + wStr).replaceAll("\\\\%", "%").replaceAll("^%", wStr).replaceAll("([^\\\\])_", "$1.").replaceAll("\\\\_", "_").replaceAll("^_", ".");
    }
    
    protected boolean isAuthV2Enabled() {
        final SessionState ss = SessionState.get();
        return ss.isAuthorizationModeV2() && HiveConf.getBoolVar(ss.getConf(), HiveConf.ConfVars.HIVE_AUTHORIZATION_ENABLED);
    }
    
    protected void authorizeMetaGets(final HiveOperationType opType, final List<HivePrivilegeObject> inpObjs) throws HiveSQLException {
        this.authorizeMetaGets(opType, inpObjs, null);
    }
    
    protected void authorizeMetaGets(final HiveOperationType opType, final List<HivePrivilegeObject> inpObjs, final String cmdString) throws HiveSQLException {
        final SessionState ss = SessionState.get();
        final HiveAuthzContext.Builder ctxBuilder = new HiveAuthzContext.Builder();
        ctxBuilder.setUserIpAddress(ss.getUserIpAddress());
        ctxBuilder.setCommandString(cmdString);
        try {
            ss.getAuthorizerV2().checkPrivileges(opType, (List)inpObjs, (List)null, ctxBuilder.build());
        }
        catch (HiveAuthzPluginException | HiveAccessControlException ex2) {
            final HiveException ex;
            final HiveException e = ex;
            throw new HiveSQLException(e.getMessage(), (Throwable)e);
        }
    }
}
