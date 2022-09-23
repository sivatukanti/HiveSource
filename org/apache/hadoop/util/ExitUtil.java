// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce", "YARN" })
@InterfaceStability.Unstable
public final class ExitUtil
{
    private static final Logger LOG;
    private static volatile boolean systemExitDisabled;
    private static volatile boolean systemHaltDisabled;
    private static volatile ExitException firstExitException;
    private static volatile HaltException firstHaltException;
    public static final String EXIT_EXCEPTION_MESSAGE = "ExitException";
    public static final String HALT_EXCEPTION_MESSAGE = "HaltException";
    
    private ExitUtil() {
    }
    
    public static void disableSystemExit() {
        ExitUtil.systemExitDisabled = true;
    }
    
    public static void disableSystemHalt() {
        ExitUtil.systemHaltDisabled = true;
    }
    
    public static boolean terminateCalled() {
        return ExitUtil.firstExitException != null;
    }
    
    public static boolean haltCalled() {
        return ExitUtil.firstHaltException != null;
    }
    
    public static ExitException getFirstExitException() {
        return ExitUtil.firstExitException;
    }
    
    public static HaltException getFirstHaltException() {
        return ExitUtil.firstHaltException;
    }
    
    public static void resetFirstExitException() {
        ExitUtil.firstExitException = null;
    }
    
    public static void resetFirstHaltException() {
        ExitUtil.firstHaltException = null;
    }
    
    public static synchronized void terminate(final ExitException ee) throws ExitException {
        final int status = ee.getExitCode();
        final String msg = ee.getMessage();
        if (status != 0) {
            ExitUtil.LOG.debug("Exiting with status {}: {}", status, msg, ee);
            ExitUtil.LOG.info("Exiting with status {}: {}", (Object)status, msg);
        }
        if (ExitUtil.systemExitDisabled) {
            ExitUtil.LOG.error("Terminate called", ee);
            if (!terminateCalled()) {
                ExitUtil.firstExitException = ee;
            }
            throw ee;
        }
        System.exit(status);
    }
    
    public static synchronized void halt(final HaltException ee) throws HaltException {
        final int status = ee.getExitCode();
        final String msg = ee.getMessage();
        try {
            if (status != 0) {
                ExitUtil.LOG.debug("Halt with status {}: {}", status, msg, ee);
                ExitUtil.LOG.info("Halt with status {}: {}", status, msg, msg);
            }
        }
        catch (Exception ex) {}
        if (ExitUtil.systemHaltDisabled) {
            ExitUtil.LOG.error("Halt called", ee);
            if (!haltCalled()) {
                ExitUtil.firstHaltException = ee;
            }
            throw ee;
        }
        Runtime.getRuntime().halt(status);
    }
    
    public static void terminate(final int status, final Throwable t) throws ExitException {
        if (t instanceof ExitException) {
            terminate((ExitException)t);
        }
        else {
            terminate(new ExitException(status, t));
        }
    }
    
    public static void halt(final int status, final Throwable t) throws HaltException {
        if (t instanceof HaltException) {
            halt((HaltException)t);
        }
        else {
            halt(new HaltException(status, t));
        }
    }
    
    public static void terminate(final int status) throws ExitException {
        terminate(status, "ExitException");
    }
    
    public static void terminate(final int status, final String msg) throws ExitException {
        terminate(new ExitException(status, msg));
    }
    
    public static void halt(final int status) throws HaltException {
        halt(status, "HaltException");
    }
    
    public static void halt(final int status, final String message) throws HaltException {
        halt(new HaltException(status, message));
    }
    
    public static void haltOnOutOfMemory(final OutOfMemoryError oome) {
        try {
            System.err.println("Halting due to Out Of Memory Error...");
        }
        catch (Throwable t) {}
        Runtime.getRuntime().halt(-1);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ExitUtil.class.getName());
        ExitUtil.systemExitDisabled = false;
        ExitUtil.systemHaltDisabled = false;
    }
    
    public static class ExitException extends RuntimeException implements ExitCodeProvider
    {
        private static final long serialVersionUID = 1L;
        public final int status;
        
        public ExitException(final int status, final String msg) {
            super(msg);
            this.status = status;
        }
        
        public ExitException(final int status, final String message, final Throwable cause) {
            super(message, cause);
            this.status = status;
        }
        
        public ExitException(final int status, final Throwable cause) {
            super(cause);
            this.status = status;
        }
        
        @Override
        public int getExitCode() {
            return this.status;
        }
        
        @Override
        public String toString() {
            String message = this.getMessage();
            if (message == null) {
                message = super.toString();
            }
            return Integer.toString(this.status) + ": " + message;
        }
    }
    
    public static class HaltException extends RuntimeException implements ExitCodeProvider
    {
        private static final long serialVersionUID = 1L;
        public final int status;
        
        public HaltException(final int status, final Throwable cause) {
            super(cause);
            this.status = status;
        }
        
        public HaltException(final int status, final String msg) {
            super(msg);
            this.status = status;
        }
        
        public HaltException(final int status, final String message, final Throwable cause) {
            super(message, cause);
            this.status = status;
        }
        
        @Override
        public int getExitCode() {
            return this.status;
        }
        
        @Override
        public String toString() {
            String message = this.getMessage();
            if (message == null) {
                message = super.toString();
            }
            return Integer.toString(this.status) + ": " + message;
        }
    }
}
