// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.session;

import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.service.cli.SessionHandle;
import java.io.File;
import org.apache.hive.service.cli.operation.OperationManager;
import org.apache.hive.service.cli.thrift.TProtocolVersion;

public interface HiveSessionBase
{
    TProtocolVersion getProtocolVersion();
    
    void setSessionManager(final SessionManager p0);
    
    SessionManager getSessionManager();
    
    void setOperationManager(final OperationManager p0);
    
    boolean isOperationLogEnabled();
    
    File getOperationLogSessionDir();
    
    void setOperationLogSessionDir(final File p0);
    
    SessionHandle getSessionHandle();
    
    String getUsername();
    
    String getPassword();
    
    HiveConf getHiveConf();
    
    SessionState getSessionState();
    
    String getUserName();
    
    void setUserName(final String p0);
    
    String getIpAddress();
    
    void setIpAddress(final String p0);
    
    long getLastAccessTime();
}
