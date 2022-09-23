// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.IOException;
import org.apache.tools.ant.Task;

public class RetryHandler
{
    private int retriesAllowed;
    private Task task;
    
    public RetryHandler(final int retriesAllowed, final Task task) {
        this.retriesAllowed = 0;
        this.retriesAllowed = retriesAllowed;
        this.task = task;
    }
    
    public void execute(final Retryable exe, final String desc) throws IOException {
        int retries = 0;
        while (true) {
            try {
                exe.execute();
            }
            catch (IOException e) {
                if (++retries > this.retriesAllowed && this.retriesAllowed > -1) {
                    this.task.log("try #" + retries + ": IO error (" + desc + "), number of maximum retries reached (" + this.retriesAllowed + "), giving up", 1);
                    throw e;
                }
                this.task.log("try #" + retries + ": IO error (" + desc + "), retrying", 1);
                continue;
            }
            break;
        }
    }
}
