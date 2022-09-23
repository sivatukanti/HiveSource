// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.error;

import java.sql.SQLWarning;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.sql.SQLException;

public class StandardException extends Exception
{
    public static final int REPORT_DEFAULT = 0;
    public static final int REPORT_NEVER = 1;
    public static final int REPORT_ALWAYS = 2;
    private transient Object[] arguments;
    private int severity;
    private String textMessage;
    private String sqlState;
    private transient int report;
    
    protected StandardException(final String s) {
        this(s, null, null);
    }
    
    protected StandardException(final String s, final Object[] array) {
        this(s, null, array);
    }
    
    protected StandardException(final String message, final Throwable cause, final Object[] arguments) {
        super(message);
        this.severity = getSeverityFromIdentifier(message);
        this.sqlState = getSQLStateFromIdentifier(message);
        this.arguments = arguments;
        if (cause != null) {
            this.initCause(cause);
        }
    }
    
    private StandardException(final String s, final String textMessage) {
        this(s);
        this.textMessage = textMessage;
    }
    
    public final Object[] getArguments() {
        return this.arguments;
    }
    
    public final int report() {
        return this.report;
    }
    
    public final void setReport(final int report) {
        this.report = report;
    }
    
    public final void setSeverity(final int severity) {
        this.severity = severity;
    }
    
    public final int getSeverity() {
        return this.severity;
    }
    
    public final int getErrorCode() {
        return this.severity;
    }
    
    public final String getSQLState() {
        return this.sqlState;
    }
    
    public static String getSQLStateFromIdentifier(final String s) {
        if (s.length() == 5) {
            return s;
        }
        return s.substring(0, 5);
    }
    
    public static int getSeverityFromIdentifier(final String s) {
        int n = 0;
        Label_0318: {
            switch (s.length()) {
                case 5: {
                    Label_0183: {
                        switch (s.charAt(0)) {
                            case '0': {
                                switch (s.charAt(1)) {
                                    case '1': {
                                        n = 10000;
                                        break;
                                    }
                                    case '7':
                                    case 'A': {
                                        n = 20000;
                                        break;
                                    }
                                    case '8': {
                                        n = 40000;
                                        break;
                                    }
                                }
                                break;
                            }
                            case '2':
                            case '3': {
                                n = 20000;
                                break;
                            }
                            case '4': {
                                switch (s.charAt(1)) {
                                    case '0': {
                                        n = 30000;
                                        break Label_0183;
                                    }
                                    case '2': {
                                        n = 20000;
                                        break Label_0183;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                default: {
                    switch (s.charAt(6)) {
                        case 'M': {
                            n = 50000;
                            break Label_0318;
                        }
                        case 'D': {
                            n = 45000;
                            break Label_0318;
                        }
                        case 'C': {
                            n = 40000;
                            break Label_0318;
                        }
                        case 'T': {
                            n = 30000;
                            break Label_0318;
                        }
                        case 'S': {
                            n = 20000;
                            break Label_0318;
                        }
                        case 'U': {
                            n = 0;
                            break Label_0318;
                        }
                    }
                    break;
                }
            }
        }
        return n;
    }
    
    public static StandardException normalClose() {
        final StandardException exception = newException("XXXXX.C.6");
        exception.report = 1;
        return exception;
    }
    
    public static StandardException newException(final String s) {
        return new StandardException(s);
    }
    
    public static StandardException newException(final String s, final Throwable t) {
        return new StandardException(s, t, null);
    }
    
    public static StandardException newException(final String s, final Object o) {
        return new StandardException(s, new Object[] { o });
    }
    
    public static StandardException newException(final String s, final Object[] array) {
        return new StandardException(s, array);
    }
    
    public static StandardException newException(final String s, final Throwable t, final Object o) {
        return new StandardException(s, t, new Object[] { o });
    }
    
    public static StandardException newException(final String s, final Object o, final Object o2) {
        return new StandardException(s, new Object[] { o, o2 });
    }
    
    public static StandardException newException(final String s, final Object o, final Throwable t) throws BadMessageArgumentException {
        throw new BadMessageArgumentException();
    }
    
    public static StandardException newException(final String s, final Throwable t, final Object o, final Object o2) {
        return new StandardException(s, t, new Object[] { o, o2 });
    }
    
    public static StandardException newException(final String s, final Object o, final Object o2, final Object o3) {
        return new StandardException(s, new Object[] { o, o2, o3 });
    }
    
    public static StandardException newException(final String s, final Object o, final Object o2, final Throwable t) throws BadMessageArgumentException {
        throw new BadMessageArgumentException();
    }
    
    public static StandardException newException(final String s, final Throwable t, final Object o, final Object o2, final Object o3) {
        return new StandardException(s, t, new Object[] { o, o2, o3 });
    }
    
    public static StandardException newException(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        return new StandardException(s, new Object[] { o, o2, o3, o4 });
    }
    
    public static StandardException newException(final String s, final Throwable t, final Object o, final Object o2, final Object o3, final Object o4) {
        return new StandardException(s, t, new Object[] { o, o2, o3, o4 });
    }
    
    public static StandardException newException(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        return new StandardException(s, new Object[] { o, o2, o3, o4, o5 });
    }
    
    public static StandardException newException(final String s, final Throwable t, final Object o, final Object o2, final Object o3, final Object o4, final Object o5) {
        return new StandardException(s, t, new Object[] { o, o2, o3, o4, o5 });
    }
    
    public static StandardException newException(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new StandardException(s, new Object[] { o, o2, o3, o4, o5, o6 });
    }
    
    public static StandardException newException(final String s, final Throwable t, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        return new StandardException(s, t, new Object[] { o, o2, o3, o4, o5, o6 });
    }
    
    public static StandardException newException(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) {
        return new StandardException(s, new Object[] { o, o2, o3, o4, o5, o6, o7 });
    }
    
    public static StandardException newException(final String s, final Throwable t, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) {
        return new StandardException(s, t, new Object[] { o, o2, o3, o4, o5, o6, o7 });
    }
    
    public static StandardException newException(final String s, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8) {
        return new StandardException(s, new Object[] { o, o2, o3, o4, o5, o6, o7, o8 });
    }
    
    public static StandardException newException(final String s, final Throwable t, final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8) {
        return new StandardException(s, t, new Object[] { o, o2, o3, o4, o5, o6, o7, o8 });
    }
    
    public static StandardException newPreLocalizedException(final String s, final Throwable cause, final String s2) {
        final StandardException ex = new StandardException(s, s2);
        if (cause != null) {
            ex.initCause(cause);
        }
        return ex;
    }
    
    public static SQLException getArgumentFerry(final SQLException ex) {
        if (ex instanceof DerbySQLException) {
            return ex;
        }
        final Throwable cause = ex.getCause();
        if (cause == null || !(cause instanceof DerbySQLException)) {
            return ex;
        }
        return (SQLException)cause;
    }
    
    public static StandardException unexpectedUserException(final Throwable t) {
        Object o = null;
        if (t instanceof SQLException) {
            final SQLException argumentFerry = getArgumentFerry((SQLException)t);
            if (argumentFerry instanceof DerbySQLException) {
                o = argumentFerry;
            }
        }
        if (t instanceof SQLException && o == null) {
            final SQLException ex = (SQLException)t;
            final String sqlState = ex.getSQLState();
            if (sqlState != null && sqlState.length() == 5 && sqlState.startsWith("38") && !sqlState.equals("38000")) {
                final StandardException ex2 = new StandardException(sqlState, ex.getMessage());
                if (ex.getNextException() != null) {
                    ex2.initCause(ex.getNextException());
                }
                return ex2;
            }
        }
        if (o != null && ((DerbySQLException)o).isSimpleWrapper()) {
            final Throwable cause = ((SQLException)o).getCause();
            if (cause instanceof StandardException) {
                return (StandardException)cause;
            }
        }
        if (t instanceof StandardException) {
            return (StandardException)t;
        }
        boolean b = false;
        String s;
        if (o != null) {
            s = o.toString();
            b = true;
        }
        else {
            s = t.getMessage();
        }
        String str;
        if (s == null) {
            str = "";
        }
        else {
            str = s.trim();
        }
        if (str.length() == 0) {
            str = t.getClass().getName();
        }
        else if (!b) {
            str = t.getClass().getName() + ": " + str;
        }
        return newException("38000", t, str);
    }
    
    public static StandardException plainWrapException(final Throwable t) {
        if (t instanceof StandardException) {
            return (StandardException)t;
        }
        if (t instanceof SQLException) {
            final SQLException ex = (SQLException)t;
            final String sqlState = ex.getSQLState();
            if (sqlState != null) {
                final StandardException ex2 = new StandardException(sqlState, "(" + ex.getErrorCode() + ") " + ex.getMessage());
                final SQLException nextException = ex.getNextException();
                if (nextException != null) {
                    ex2.initCause(plainWrapException(nextException));
                }
                return ex2;
            }
        }
        final String message = t.getMessage();
        String trim;
        if (message == null) {
            trim = "";
        }
        else {
            trim = message.trim();
        }
        return newException("XJ001.U", t, trim, t.getClass().getName());
    }
    
    public static StandardException closeException() {
        final StandardException exception = newException("close.C.1");
        exception.setReport(1);
        return exception;
    }
    
    public String getMessage() {
        if (this.textMessage == null) {
            this.textMessage = MessageService.getCompleteMessage(this.getMessageId(), this.getArguments());
        }
        return this.textMessage;
    }
    
    public final String getMessageId() {
        return super.getMessage();
    }
    
    public String getErrorProperty(final String s) {
        return getErrorProperty(this.getMessageId(), s);
    }
    
    public String toString() {
        return "ERROR " + this.getSQLState() + ": " + this.getMessage();
    }
    
    private static String getErrorProperty(final String s, final String s2) {
        return MessageService.getProperty(s, s2);
    }
    
    public static StandardException interrupt(final InterruptedException ex) {
        return newException("08000", ex);
    }
    
    public static SQLWarning newWarning(final String s) {
        return newWarningCommon(s, null);
    }
    
    public static SQLWarning newWarning(final String s, final Object o) {
        return newWarningCommon(s, new Object[] { o });
    }
    
    public static SQLWarning newWarning(final String s, final Object o, final Object o2) {
        return newWarningCommon(s, new Object[] { o, o2 });
    }
    
    private static SQLWarning newWarningCommon(final String s, final Object[] array) {
        return new SQLWarning(MessageService.getCompleteMessage(s, array), getSQLStateFromIdentifier(s), 10000);
    }
    
    public final boolean isLockTimeout() {
        return "40XL1".equals(this.getSQLState());
    }
    
    public final boolean isLockTimeoutOrDeadlock() {
        return "40XL1".equals(this.getSQLState()) || "40001".equals(this.getSQLState());
    }
    
    public static class BadMessageArgumentException extends Throwable
    {
    }
}
