// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.error;

import java.util.Iterator;
import java.util.Map;

public class ThreadDump
{
    public static String getStackDumpString() {
        final StringBuffer sb = new StringBuffer();
        for (final Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            final StackTraceElement[] array = entry.getValue();
            final Thread thread = entry.getKey();
            sb.append("Thread name=" + thread.getName() + " id=" + thread.getId() + " priority=" + thread.getPriority() + " state=" + thread.getState() + " isdaemon=" + thread.isDaemon() + "\n");
            for (int i = 0; i < array.length; ++i) {
                sb.append("\t" + array[i] + "\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
