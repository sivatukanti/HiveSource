// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.listener;

import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.BuildEvent;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;
import java.util.Map;
import org.apache.tools.ant.DefaultLogger;

public class ProfileLogger extends DefaultLogger
{
    private Map<Object, Date> profileData;
    
    public ProfileLogger() {
        this.profileData = new ConcurrentHashMap<Object, Date>();
    }
    
    @Override
    public void targetStarted(final BuildEvent event) {
        final Date now = new Date();
        final String name = "Target " + event.getTarget().getName();
        this.logStart(event, now, name);
        this.profileData.put(event.getTarget(), now);
    }
    
    @Override
    public void targetFinished(final BuildEvent event) {
        final Date start = this.profileData.remove(event.getTarget());
        final String name = "Target " + event.getTarget().getName();
        this.logFinish(event, start, name);
    }
    
    @Override
    public void taskStarted(final BuildEvent event) {
        final String name = event.getTask().getTaskName();
        final Date now = new Date();
        this.logStart(event, now, name);
        this.profileData.put(event.getTask(), now);
    }
    
    @Override
    public void taskFinished(final BuildEvent event) {
        final Date start = this.profileData.remove(event.getTask());
        final String name = event.getTask().getTaskName();
        this.logFinish(event, start, name);
    }
    
    private void logFinish(final BuildEvent event, final Date start, final String name) {
        final Date now = new Date();
        String msg = null;
        if (start != null) {
            final long diff = now.getTime() - start.getTime();
            msg = StringUtils.LINE_SEP + name + ": finished " + now + " (" + diff + "ms)";
        }
        else {
            msg = StringUtils.LINE_SEP + name + ": finished " + now + " (unknown duration, start not detected)";
        }
        this.printMessage(msg, this.out, event.getPriority());
        this.log(msg);
    }
    
    private void logStart(final BuildEvent event, final Date start, final String name) {
        final String msg = StringUtils.LINE_SEP + name + ": started " + start;
        this.printMessage(msg, this.out, event.getPriority());
        this.log(msg);
    }
}
