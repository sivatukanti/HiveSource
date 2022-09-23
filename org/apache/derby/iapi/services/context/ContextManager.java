// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.context;

import org.apache.derby.iapi.services.property.PropertyUtil;
import java.sql.SQLException;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.error.ShutdownException;
import org.apache.derby.iapi.services.info.JVMInfo;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.error.ExceptionUtil;
import org.apache.derby.iapi.error.PassThroughException;
import java.util.Collections;
import java.util.List;
import org.apache.derby.iapi.services.i18n.LocaleFinder;
import org.apache.derby.iapi.error.ErrorStringBuilder;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;

public class ContextManager
{
    private final HashMap ctxTable;
    private final ArrayList holder;
    private Locale messageLocale;
    final ContextService owningCsf;
    private int logSeverityLevel;
    private int extDiagSeverityLevel;
    private HeaderPrintWriter errorStream;
    private ErrorStringBuilder errorStringBuilder;
    private String threadDump;
    private boolean shutdown;
    private LocaleFinder finder;
    Thread activeThread;
    int activeCount;
    
    public void pushContext(final Context e) {
        this.checkInterrupt();
        final String idName = e.getIdName();
        CtxStack value = this.ctxTable.get(idName);
        if (value == null) {
            value = new CtxStack();
            this.ctxTable.put(idName, value);
        }
        value.push(e);
        this.holder.add(e);
    }
    
    public Context getContext(final String key) {
        this.checkInterrupt();
        final CtxStack ctxStack = this.ctxTable.get(key);
        return (ctxStack == null) ? null : ctxStack.top();
    }
    
    public void popContext() {
        this.checkInterrupt();
        if (this.holder.isEmpty()) {
            return;
        }
        this.ctxTable.get(this.holder.remove(this.holder.size() - 1).getIdName()).pop();
    }
    
    void popContext(final Context o) {
        this.checkInterrupt();
        this.holder.remove(this.holder.lastIndexOf(o));
        this.ctxTable.get(o.getIdName()).remove(o);
    }
    
    final boolean isEmpty() {
        return this.holder.isEmpty();
    }
    
    public final List getContextStack(final String key) {
        final CtxStack ctxStack = this.ctxTable.get(key);
        return (ctxStack == null) ? Collections.EMPTY_LIST : ctxStack.getUnmodifiableList();
    }
    
    public boolean cleanupOnError(Throwable cause, final boolean b) {
        if (this.shutdown) {
            return true;
        }
        if (this.errorStringBuilder == null) {
            this.errorStringBuilder = new ErrorStringBuilder(this.errorStream.getHeader());
        }
        ThreadDeath threadDeath = null;
        if (cause instanceof ThreadDeath) {
            threadDeath = (ThreadDeath)cause;
        }
        if (cause instanceof PassThroughException) {
            cause = cause.getCause();
        }
        boolean b2 = this.reportError(cause);
        if (b2) {
            StringBuffer appendErrorInfo = null;
            if (!this.shutdown) {
                final ContextImpl contextImpl = (ContextImpl)this.getContext("LanguageConnectionContext");
                if (contextImpl != null) {
                    appendErrorInfo = contextImpl.appendErrorInfo();
                }
            }
            String string = "Cleanup action starting";
            if (appendErrorInfo != null) {
                appendErrorInfo.append(string);
                string = appendErrorInfo.toString();
            }
            this.errorStringBuilder.appendln(string);
            if (!this.shutdown) {
                final ContextImpl contextImpl2 = (ContextImpl)this.getContext("StatementContext");
                if (contextImpl2 != null) {
                    final StringBuffer appendErrorInfo2 = contextImpl2.appendErrorInfo();
                    if (appendErrorInfo2 != null) {
                        this.errorStringBuilder.appendln(appendErrorInfo2.toString());
                    }
                }
            }
        }
    Label_0190:
        while (true) {
            final int errorSeverity = this.getErrorSeverity(cause);
            if (b2) {
                this.errorStringBuilder.stackTrace(cause);
                this.flushErrorString();
            }
            boolean lastHandler = false;
            for (int i = this.holder.size() - 1; i >= 0; --i) {
                try {
                    if (lastHandler) {
                        break;
                    }
                    final Context context = this.holder.get(i);
                    lastHandler = context.isLastHandler(errorSeverity);
                    context.cleanupOnError(cause);
                    if (b2 && b && errorSeverity >= this.extDiagSeverityLevel) {
                        this.threadDump = ExceptionUtil.dumpThreads();
                    }
                    else {
                        this.threadDump = null;
                    }
                }
                catch (StandardException ex) {
                    if (cause instanceof StandardException && ex.getSeverity() > ((StandardException)cause).getSeverity()) {
                        cause = ex;
                        b2 = this.reportError(ex);
                        if (b2) {
                            this.errorStream.println("New exception raised during cleanup " + cause.getMessage());
                            this.errorStream.flush();
                        }
                        continue Label_0190;
                    }
                    if (this.reportError(ex)) {
                        this.errorStringBuilder.appendln("Less severe exception raised during cleanup (ignored) " + ex.getMessage());
                        this.errorStringBuilder.stackTrace(ex);
                        this.flushErrorString();
                    }
                }
                catch (Throwable threadDeath2) {
                    b2 = this.reportError(threadDeath2);
                    if (cause instanceof StandardException) {
                        cause = threadDeath2;
                        if (b2) {
                            this.errorStream.println("New exception raised during cleanup " + cause.getMessage());
                            this.errorStream.flush();
                        }
                        continue Label_0190;
                    }
                    if (b2) {
                        this.errorStringBuilder.appendln("Equally severe exception raised during cleanup (ignored) " + threadDeath2.getMessage());
                        this.errorStringBuilder.stackTrace(threadDeath2);
                        this.flushErrorString();
                    }
                    if (threadDeath2 instanceof ThreadDeath) {
                        if (threadDeath != null) {
                            throw threadDeath;
                        }
                        threadDeath = threadDeath2;
                    }
                }
            }
            break;
        }
        if (this.threadDump != null) {
            this.errorStream.println(this.threadDump);
            JVMInfo.javaDump();
        }
        if (b2) {
            this.errorStream.println("Cleanup action completed");
            this.errorStream.flush();
        }
        if (threadDeath != null) {
            throw threadDeath;
        }
        return false;
    }
    
    synchronized boolean setInterrupted(final Context o) {
        final boolean b = o == null || this.holder.contains(o);
        if (b) {
            this.shutdown = true;
        }
        return b;
    }
    
    private void checkInterrupt() {
        if (this.shutdown) {
            throw new ShutdownException();
        }
    }
    
    public void setLocaleFinder(final LocaleFinder finder) {
        this.finder = finder;
    }
    
    public void setMessageLocale(final String s) throws StandardException {
        this.messageLocale = Monitor.getLocaleFromString(s);
    }
    
    public Locale getMessageLocale() {
        if (this.messageLocale != null) {
            return this.messageLocale;
        }
        if (this.finder != null) {
            try {
                return this.finder.getCurrentLocale();
            }
            catch (StandardException ex) {}
        }
        return Locale.getDefault();
    }
    
    private void flushErrorString() {
        this.errorStream.print(this.errorStringBuilder.get().toString());
        this.errorStream.flush();
        this.errorStringBuilder.reset();
    }
    
    private boolean reportError(final Throwable t) {
        if (!(t instanceof StandardException)) {
            return !(t instanceof ShutdownException);
        }
        final StandardException ex = (StandardException)t;
        switch (ex.report()) {
            case 0: {
                final int severity = ex.getSeverity();
                return severity >= this.logSeverityLevel || severity == 0;
            }
            case 1: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    public int getErrorSeverity(final Throwable t) {
        if (t instanceof StandardException) {
            return ((StandardException)t).getErrorCode();
        }
        if (t instanceof SQLException) {
            return ((SQLException)t).getErrorCode();
        }
        return 0;
    }
    
    ContextManager(final ContextService owningCsf, final HeaderPrintWriter errorStream) {
        this.ctxTable = new HashMap();
        this.holder = new ArrayList();
        this.errorStream = errorStream;
        this.owningCsf = owningCsf;
        this.logSeverityLevel = PropertyUtil.getSystemInt("derby.stream.error.logSeverityLevel", 40000);
        this.extDiagSeverityLevel = PropertyUtil.getSystemInt("derby.stream.error.extendedDiagSeverityLevel", 40000);
    }
    
    private static final class CtxStack
    {
        private final ArrayList stack_;
        private final List view_;
        private Context top_;
        
        private CtxStack() {
            this.stack_ = new ArrayList();
            this.view_ = Collections.unmodifiableList((List<?>)this.stack_);
            this.top_ = null;
        }
        
        void push(final Context context) {
            this.stack_.add(context);
            this.top_ = context;
        }
        
        void pop() {
            this.stack_.remove(this.stack_.size() - 1);
            this.top_ = (this.stack_.isEmpty() ? null : this.stack_.get(this.stack_.size() - 1));
        }
        
        void remove(final Context o) {
            if (o == this.top_) {
                this.pop();
                return;
            }
            this.stack_.remove(this.stack_.lastIndexOf(o));
        }
        
        Context top() {
            return this.top_;
        }
        
        boolean isEmpty() {
            return this.stack_.isEmpty();
        }
        
        List getUnmodifiableList() {
            return this.view_;
        }
    }
}
