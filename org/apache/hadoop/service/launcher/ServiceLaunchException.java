// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service.launcher;

import java.util.Locale;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.util.ExitCodeProvider;
import org.apache.hadoop.util.ExitUtil;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ServiceLaunchException extends ExitUtil.ExitException implements ExitCodeProvider, LauncherExitCodes
{
    public ServiceLaunchException(final int exitCode, final Throwable cause) {
        super(exitCode, cause);
    }
    
    public ServiceLaunchException(final int exitCode, final String message) {
        super(exitCode, message);
    }
    
    public ServiceLaunchException(final int exitCode, final String format, final Object... args) {
        super(exitCode, String.format(Locale.ENGLISH, format, args));
        if (args.length > 0 && args[args.length - 1] instanceof Throwable) {
            this.initCause((Throwable)args[args.length - 1]);
        }
    }
}
