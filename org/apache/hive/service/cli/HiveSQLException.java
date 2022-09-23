// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.hive.service.cli.thrift.TStatusCode;
import org.apache.hive.service.cli.thrift.TStatus;
import java.sql.SQLException;

public class HiveSQLException extends SQLException
{
    private static final long serialVersionUID = -6095254671958748094L;
    
    public HiveSQLException() {
    }
    
    public HiveSQLException(final String reason) {
        super(reason);
    }
    
    public HiveSQLException(final Throwable cause) {
        super(cause);
    }
    
    public HiveSQLException(final String reason, final String sqlState) {
        super(reason, sqlState);
    }
    
    public HiveSQLException(final String reason, final Throwable cause) {
        super(reason, cause);
    }
    
    public HiveSQLException(final String reason, final String sqlState, final int vendorCode) {
        super(reason, sqlState, vendorCode);
    }
    
    public HiveSQLException(final String reason, final String sqlState, final Throwable cause) {
        super(reason, sqlState, cause);
    }
    
    public HiveSQLException(final String reason, final String sqlState, final int vendorCode, final Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
    
    public HiveSQLException(final TStatus status) {
        super(status.getErrorMessage(), status.getSqlState(), status.getErrorCode());
        if (status.getInfoMessages() != null) {
            this.initCause(toCause(status.getInfoMessages()));
        }
    }
    
    public TStatus toTStatus() {
        final TStatus tStatus = new TStatus(TStatusCode.ERROR_STATUS);
        tStatus.setSqlState(this.getSQLState());
        tStatus.setErrorCode(this.getErrorCode());
        tStatus.setErrorMessage(this.getMessage());
        tStatus.setInfoMessages(toString(this));
        return tStatus;
    }
    
    public static TStatus toTStatus(final Exception e) {
        if (e instanceof HiveSQLException) {
            return ((HiveSQLException)e).toTStatus();
        }
        final TStatus tStatus = new TStatus(TStatusCode.ERROR_STATUS);
        tStatus.setErrorMessage(e.getMessage());
        tStatus.setInfoMessages(toString(e));
        return tStatus;
    }
    
    public static List<String> toString(final Throwable ex) {
        return toString(ex, null);
    }
    
    private static List<String> toString(Throwable cause, final StackTraceElement[] parent) {
        final StackTraceElement[] trace = cause.getStackTrace();
        int m = trace.length - 1;
        if (parent != null) {
            for (int n = parent.length - 1; m >= 0 && n >= 0 && trace[m].equals(parent[n]); --m, --n) {}
        }
        final List<String> detail = enroll(cause, trace, m);
        cause = cause.getCause();
        if (cause != null) {
            detail.addAll(toString(cause, trace));
        }
        return detail;
    }
    
    private static List<String> enroll(final Throwable ex, final StackTraceElement[] trace, final int max) {
        final List<String> details = new ArrayList<String>();
        final StringBuilder builder = new StringBuilder();
        builder.append('*').append(ex.getClass().getName()).append(':');
        builder.append(ex.getMessage()).append(':');
        builder.append(trace.length).append(':').append(max);
        details.add(builder.toString());
        for (int i = 0; i <= max; ++i) {
            builder.setLength(0);
            builder.append(trace[i].getClassName()).append(':');
            builder.append(trace[i].getMethodName()).append(':');
            final String fileName = trace[i].getFileName();
            builder.append((fileName == null) ? "" : fileName).append(':');
            builder.append(trace[i].getLineNumber());
            details.add(builder.toString());
        }
        return details;
    }
    
    public static Throwable toCause(final List<String> details) {
        return toStackTrace(details, null, 0);
    }
    
    private static Throwable toStackTrace(final List<String> details, final StackTraceElement[] parent, int index) {
        String detail = details.get(index++);
        if (!detail.startsWith("*")) {
            return null;
        }
        final int i1 = detail.indexOf(58);
        final int i2 = detail.lastIndexOf(58);
        final int i3 = detail.substring(0, i2).lastIndexOf(58);
        final String exceptionClass = detail.substring(1, i1);
        final String exceptionMessage = detail.substring(i1 + 1, i3);
        final Throwable ex = newInstance(exceptionClass, exceptionMessage);
        final Integer length = Integer.valueOf(detail.substring(i3 + 1, i2));
        final Integer unique = Integer.valueOf(detail.substring(i2 + 1));
        int j = 0;
        final StackTraceElement[] trace = new StackTraceElement[(int)length];
        while (j <= unique) {
            detail = details.get(index++);
            final int j2 = detail.indexOf(58);
            final int j3 = detail.lastIndexOf(58);
            final int j4 = detail.substring(0, j3).lastIndexOf(58);
            final String className = detail.substring(0, j2);
            final String methodName = detail.substring(j2 + 1, j4);
            String fileName = detail.substring(j4 + 1, j3);
            if (fileName.isEmpty()) {
                fileName = null;
            }
            final int lineNumber = Integer.valueOf(detail.substring(j3 + 1));
            trace[j] = new StackTraceElement(className, methodName, fileName, lineNumber);
            ++j;
        }
        final int common = trace.length - j;
        if (common > 0) {
            System.arraycopy(parent, parent.length - common, trace, trace.length - common, common);
        }
        if (details.size() > index) {
            ex.initCause(toStackTrace(details, trace, index));
        }
        ex.setStackTrace(trace);
        return ex;
    }
    
    private static Throwable newInstance(final String className, final String message) {
        try {
            return (Throwable)Class.forName(className).getConstructor(String.class).newInstance(message);
        }
        catch (Exception e) {
            return new RuntimeException(className + ":" + message);
        }
    }
}
