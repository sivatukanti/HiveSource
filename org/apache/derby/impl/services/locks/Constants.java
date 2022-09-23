// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.locks;

public class Constants
{
    public static final String LOCK_TRACE = "LockTrace";
    public static final String LOCK_STACK_TRACE = "LockStackTrace";
    public static final String LOCK_TRACE_ADD_THREAD_INFO = "LockTraceAddThreadInfo";
    static final byte WAITING_LOCK_IN_WAIT = 0;
    static final byte WAITING_LOCK_GRANT = 1;
    static final byte WAITING_LOCK_DEADLOCK = 2;
    static final byte WAITING_LOCK_INTERRUPTED = 3;
}
