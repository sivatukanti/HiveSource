// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import com.jolbox.bonecp.hooks.ConnectionHook;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.jolbox.bonecp.hooks.AcquireFailConfig;
import com.jolbox.bonecp.proxy.TransactionRecoveryResult;
import java.util.List;
import java.sql.SQLException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.jolbox.bonecp.proxy.CallableStatementProxy;
import java.sql.CallableStatement;
import com.jolbox.bonecp.proxy.PreparedStatementProxy;
import java.sql.PreparedStatement;
import com.jolbox.bonecp.proxy.StatementProxy;
import java.sql.Statement;
import java.lang.reflect.Proxy;
import com.jolbox.bonecp.proxy.ConnectionProxy;
import java.sql.Connection;
import org.slf4j.Logger;
import com.google.common.collect.ImmutableSet;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;

public class MemorizeTransactionProxy implements InvocationHandler
{
    private Object target;
    private WeakReference<ConnectionHandle> connectionHandle;
    private static final ImmutableSet<String> clearLogConditions;
    private static final Logger logger;
    
    public MemorizeTransactionProxy() {
    }
    
    protected static Connection memorize(final Connection target, final ConnectionHandle connectionHandle) {
        return (Connection)Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[] { ConnectionProxy.class }, new MemorizeTransactionProxy(target, connectionHandle));
    }
    
    protected static Statement memorize(final Statement target, final ConnectionHandle connectionHandle) {
        return (Statement)Proxy.newProxyInstance(StatementProxy.class.getClassLoader(), new Class[] { StatementProxy.class }, new MemorizeTransactionProxy(target, connectionHandle));
    }
    
    protected static PreparedStatement memorize(final PreparedStatement target, final ConnectionHandle connectionHandle) {
        return (PreparedStatement)Proxy.newProxyInstance(PreparedStatementProxy.class.getClassLoader(), new Class[] { PreparedStatementProxy.class }, new MemorizeTransactionProxy(target, connectionHandle));
    }
    
    protected static CallableStatement memorize(final CallableStatement target, final ConnectionHandle connectionHandle) {
        return (CallableStatement)Proxy.newProxyInstance(CallableStatementProxy.class.getClassLoader(), new Class[] { CallableStatementProxy.class }, new MemorizeTransactionProxy(target, connectionHandle));
    }
    
    private MemorizeTransactionProxy(final Object target, final ConnectionHandle connectionHandle) {
        this.target = target;
        this.connectionHandle = new WeakReference<ConnectionHandle>(connectionHandle);
    }
    
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        Object result = null;
        ConnectionHandle con = this.connectionHandle.get();
        if (con != null) {
            if (method.getName().equals("getProxyTarget")) {
                return this.target;
            }
            if (con.isInReplayMode()) {
                try {
                    return method.invoke(this.target, args);
                }
                catch (InvocationTargetException t) {
                    throw t.getCause();
                }
            }
            if (con.recoveryResult != null) {
                Object remap = con.recoveryResult.getReplaceTarget().get(this.target);
                if (remap != null) {
                    this.target = remap;
                }
                remap = con.recoveryResult.getReplaceTarget().get(con);
                if (remap != null) {
                    con = (ConnectionHandle)remap;
                }
            }
            if (!con.isInReplayMode() && !method.getName().equals("hashCode") && !method.getName().equals("equals") && !method.getName().equals("toString")) {
                con.getReplayLog().add(new ReplayLog(this.target, method, args));
            }
            try {
                result = this.runWithPossibleProxySwap(method, this.target, args);
                if (!con.isInReplayMode() && this.target instanceof Connection && MemorizeTransactionProxy.clearLogConditions.contains(method.getName())) {
                    con.getReplayLog().clear();
                }
                return result;
            }
            catch (Throwable t2) {
                final List<ReplayLog> oldReplayLog = con.getReplayLog();
                con.setInReplayMode(true);
                if (t2 instanceof SQLException || (t2.getCause() != null && t2.getCause() instanceof SQLException)) {
                    con.markPossiblyBroken((SQLException)t2.getCause());
                }
                if (!con.isPossiblyBroken()) {
                    con.setInReplayMode(false);
                    con.getReplayLog().clear();
                }
                else {
                    MemorizeTransactionProxy.logger.error("Connection failed. Attempting to recover transaction on Thread #" + Thread.currentThread().getId());
                    try {
                        con.recoveryResult = this.attemptRecovery(oldReplayLog);
                        con.setReplayLog(oldReplayLog);
                        con.setInReplayMode(false);
                        MemorizeTransactionProxy.logger.error("Recovery succeeded on Thread #" + Thread.currentThread().getId());
                        con.possiblyBroken = false;
                        return con.recoveryResult.getResult();
                    }
                    catch (Throwable t3) {
                        con.setInReplayMode(false);
                        con.getReplayLog().clear();
                    }
                }
                throw t2.getCause();
            }
        }
        result = method.invoke(this.target, args);
        return result;
    }
    
    private Object runWithPossibleProxySwap(final Method method, final Object target, final Object[] args) throws IllegalAccessException, InvocationTargetException {
        Object result;
        if (method.getName().equals("createStatement")) {
            result = memorize((Statement)method.invoke(target, args), this.connectionHandle.get());
        }
        else if (method.getName().equals("prepareStatement")) {
            result = memorize((PreparedStatement)method.invoke(target, args), this.connectionHandle.get());
        }
        else if (method.getName().equals("prepareCall")) {
            result = memorize((CallableStatement)method.invoke(target, args), this.connectionHandle.get());
        }
        else {
            result = method.invoke(target, args);
        }
        return result;
    }
    
    private TransactionRecoveryResult attemptRecovery(final List<ReplayLog> oldReplayLog) throws SQLException {
        boolean tryAgain = false;
        Throwable failedThrowable = null;
        final ConnectionHandle con = this.connectionHandle.get();
        if (con == null) {
            throw PoolUtil.generateSQLException("ConnectionHandle is gone!", new IllegalStateException());
        }
        final TransactionRecoveryResult recoveryResult = con.recoveryResult;
        final ConnectionHook connectionHook = con.getPool().getConfig().getConnectionHook();
        int acquireRetryAttempts = con.getPool().getConfig().getAcquireRetryAttempts();
        final long acquireRetryDelay = con.getPool().getConfig().getAcquireRetryDelayInMs();
        final AcquireFailConfig acquireConfig = new AcquireFailConfig();
        acquireConfig.setAcquireRetryAttempts(new AtomicInteger(acquireRetryAttempts));
        acquireConfig.setAcquireRetryDelayInMs(acquireRetryDelay);
        acquireConfig.setLogMessage("Failed to replay transaction");
        final Map<Object, Object> replaceTarget = new HashMap<Object, Object>();
        do {
            replaceTarget.clear();
            for (final Map.Entry<Object, Object> entry : recoveryResult.getReplaceTarget().entrySet()) {
                replaceTarget.put(entry.getKey(), entry.getValue());
            }
            final List<PreparedStatement> prepStatementTarget = new ArrayList<PreparedStatement>();
            final List<CallableStatement> callableStatementTarget = new ArrayList<CallableStatement>();
            final List<Statement> statementTarget = new ArrayList<Statement>();
            Object result = null;
            tryAgain = false;
            con.setInReplayMode(true);
            try {
                con.clearStatementCaches(true);
                con.getInternalConnection().close();
            }
            catch (Throwable t2) {}
            try {
                con.setInternalConnection(memorize(con.getPool().obtainInternalConnection(con), con));
            }
            catch (SQLException e) {
                throw con.markPossiblyBroken(e);
            }
            con.getOriginatingPartition().trackConnectionFinalizer(con);
            for (final ReplayLog replay : oldReplayLog) {
                if (replay.getTarget() instanceof Connection) {
                    replaceTarget.put(replay.getTarget(), con.getInternalConnection());
                }
                else if (replay.getTarget() instanceof CallableStatement) {
                    if (replaceTarget.get(replay.getTarget()) == null) {
                        replaceTarget.put(replay.getTarget(), callableStatementTarget.remove(0));
                    }
                }
                else if (replay.getTarget() instanceof PreparedStatement) {
                    if (replaceTarget.get(replay.getTarget()) == null) {
                        replaceTarget.put(replay.getTarget(), prepStatementTarget.remove(0));
                    }
                }
                else if (replay.getTarget() instanceof Statement && replaceTarget.get(replay.getTarget()) == null) {
                    replaceTarget.put(replay.getTarget(), statementTarget.remove(0));
                }
                try {
                    result = this.runWithPossibleProxySwap(replay.getMethod(), replaceTarget.get(replay.getTarget()), replay.getArgs());
                    recoveryResult.setResult(result);
                    if (result instanceof CallableStatement) {
                        callableStatementTarget.add((CallableStatement)result);
                    }
                    else if (result instanceof PreparedStatement) {
                        prepStatementTarget.add((PreparedStatement)result);
                    }
                    else {
                        if (!(result instanceof Statement)) {
                            continue;
                        }
                        statementTarget.add((Statement)result);
                    }
                }
                catch (Throwable t) {
                    if (connectionHook != null) {
                        tryAgain = connectionHook.onAcquireFail(t, acquireConfig);
                    }
                    else {
                        MemorizeTransactionProxy.logger.error("Failed to replay transaction. Sleeping for " + acquireRetryDelay + "ms and trying again. Attempts left: " + acquireRetryAttempts + ". Exception: " + t.getCause() + " Message:" + t.getMessage());
                        try {
                            Thread.sleep(acquireRetryDelay);
                            if (acquireRetryAttempts > 0) {
                                tryAgain = (--acquireRetryAttempts != 0);
                            }
                        }
                        catch (InterruptedException e2) {
                            tryAgain = false;
                        }
                    }
                    if (!tryAgain) {
                        failedThrowable = t;
                    }
                    break;
                }
            }
        } while (tryAgain);
        for (final Map.Entry<Object, Object> entry : replaceTarget.entrySet()) {
            recoveryResult.getReplaceTarget().put(entry.getKey(), entry.getValue());
        }
        for (final ReplayLog replay2 : oldReplayLog) {
            replay2.setTarget(replaceTarget.get(replay2.getTarget()));
        }
        if (failedThrowable != null) {
            throw PoolUtil.generateSQLException(failedThrowable.getMessage(), failedThrowable);
        }
        return recoveryResult;
    }
    
    static {
        clearLogConditions = ImmutableSet.of("rollback", "commit", "close");
        logger = LoggerFactory.getLogger(MemorizeTransactionProxy.class);
    }
}
