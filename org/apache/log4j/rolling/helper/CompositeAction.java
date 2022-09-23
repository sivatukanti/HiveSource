// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling.helper;

import java.io.IOException;
import org.apache.log4j.helpers.LogLog;
import java.util.List;

public class CompositeAction extends ActionBase
{
    private final Action[] actions;
    private final boolean stopOnError;
    
    public CompositeAction(final List actions, final boolean stopOnError) {
        actions.toArray(this.actions = new Action[actions.size()]);
        this.stopOnError = stopOnError;
    }
    
    public void run() {
        try {
            this.execute();
        }
        catch (IOException ex) {
            LogLog.warn("Exception during file rollover.", ex);
        }
    }
    
    public boolean execute() throws IOException {
        if (this.stopOnError) {
            for (int i = 0; i < this.actions.length; ++i) {
                if (!this.actions[i].execute()) {
                    return false;
                }
            }
            return true;
        }
        boolean status = true;
        IOException exception = null;
        for (int j = 0; j < this.actions.length; ++j) {
            try {
                status &= this.actions[j].execute();
            }
            catch (IOException ex) {
                status = false;
                if (exception == null) {
                    exception = ex;
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
        return status;
    }
}
