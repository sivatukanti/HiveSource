// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service.launcher;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface LauncherExitCodes
{
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_FAIL = -1;
    public static final int EXIT_CLIENT_INITIATED_SHUTDOWN = 1;
    public static final int EXIT_TASK_LAUNCH_FAILURE = 2;
    public static final int EXIT_INTERRUPTED = 3;
    public static final int EXIT_OTHER_FAILURE = 5;
    public static final int EXIT_COMMAND_ARGUMENT_ERROR = 40;
    public static final int EXIT_UNAUTHORIZED = 41;
    public static final int EXIT_USAGE = 42;
    public static final int EXIT_FORBIDDEN = 43;
    public static final int EXIT_NOT_FOUND = 44;
    public static final int EXIT_OPERATION_NOT_ALLOWED = 45;
    public static final int EXIT_NOT_ACCEPTABLE = 46;
    public static final int EXIT_CONNECTIVITY_PROBLEM = 48;
    public static final int EXIT_BAD_CONFIGURATION = 49;
    public static final int EXIT_EXCEPTION_THROWN = 50;
    public static final int EXIT_UNIMPLEMENTED = 51;
    public static final int EXIT_SERVICE_UNAVAILABLE = 53;
    public static final int EXIT_UNSUPPORTED_VERSION = 55;
    public static final int EXIT_SERVICE_CREATION_FAILURE = 56;
    public static final int EXIT_SERVICE_LIFECYCLE_EXCEPTION = 57;
}
