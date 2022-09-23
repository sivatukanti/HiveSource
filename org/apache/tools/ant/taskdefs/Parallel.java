// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.property.LocalProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import java.util.Vector;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.Task;

public class Parallel extends Task implements TaskContainer
{
    private static final int NUMBER_TRIES = 100;
    private Vector nestedTasks;
    private final Object semaphore;
    private int numThreads;
    private int numThreadsPerProcessor;
    private long timeout;
    private volatile boolean stillRunning;
    private boolean timedOut;
    private boolean failOnAny;
    private TaskList daemonTasks;
    private StringBuffer exceptionMessage;
    private int numExceptions;
    private Throwable firstException;
    private Location firstLocation;
    
    public Parallel() {
        this.nestedTasks = new Vector();
        this.semaphore = new Object();
        this.numThreads = 0;
        this.numThreadsPerProcessor = 0;
        this.numExceptions = 0;
    }
    
    public void addDaemons(final TaskList daemonTasks) {
        if (this.daemonTasks != null) {
            throw new BuildException("Only one daemon group is supported");
        }
        this.daemonTasks = daemonTasks;
    }
    
    public void setPollInterval(final int pollInterval) {
    }
    
    public void setFailOnAny(final boolean failOnAny) {
        this.failOnAny = failOnAny;
    }
    
    public void addTask(final Task nestedTask) {
        this.nestedTasks.addElement(nestedTask);
    }
    
    public void setThreadsPerProcessor(final int numThreadsPerProcessor) {
        this.numThreadsPerProcessor = numThreadsPerProcessor;
    }
    
    public void setThreadCount(final int numThreads) {
        this.numThreads = numThreads;
    }
    
    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }
    
    @Override
    public void execute() throws BuildException {
        this.updateThreadCounts();
        if (this.numThreads == 0) {
            this.numThreads = this.nestedTasks.size();
        }
        this.spinThreads();
    }
    
    private void updateThreadCounts() {
        if (this.numThreadsPerProcessor != 0) {
            this.numThreads = Runtime.getRuntime().availableProcessors() * this.numThreadsPerProcessor;
        }
    }
    
    private void processExceptions(final TaskRunnable[] runnables) {
        if (runnables == null) {
            return;
        }
        for (int i = 0; i < runnables.length; ++i) {
            final Throwable t = runnables[i].getException();
            if (t != null) {
                ++this.numExceptions;
                if (this.firstException == null) {
                    this.firstException = t;
                }
                if (t instanceof BuildException && this.firstLocation == Location.UNKNOWN_LOCATION) {
                    this.firstLocation = ((BuildException)t).getLocation();
                }
                this.exceptionMessage.append(StringUtils.LINE_SEP);
                this.exceptionMessage.append(t.getMessage());
            }
        }
    }
    
    private void spinThreads() throws BuildException {
        final int numTasks = this.nestedTasks.size();
        final TaskRunnable[] runnables = new TaskRunnable[numTasks];
        this.stillRunning = true;
        this.timedOut = false;
        boolean interrupted = false;
        int threadNumber = 0;
        final Enumeration e = this.nestedTasks.elements();
        while (e.hasMoreElements()) {
            final Task nestedTask = e.nextElement();
            runnables[threadNumber] = new TaskRunnable(nestedTask);
            ++threadNumber;
        }
        final int maxRunning = (numTasks < this.numThreads) ? numTasks : this.numThreads;
        final TaskRunnable[] running = new TaskRunnable[maxRunning];
        threadNumber = 0;
        final ThreadGroup group = new ThreadGroup("parallel");
        TaskRunnable[] daemons = null;
        if (this.daemonTasks != null && this.daemonTasks.tasks.size() != 0) {
            daemons = new TaskRunnable[this.daemonTasks.tasks.size()];
        }
        synchronized (this.semaphore) {
        }
        // monitorexit(this.semaphore)
        synchronized (this.semaphore) {
            if (daemons != null) {
                for (int i = 0; i < daemons.length; ++i) {
                    daemons[i] = new TaskRunnable(this.daemonTasks.tasks.get(i));
                    final Thread daemonThread = new Thread(group, daemons[i]);
                    daemonThread.setDaemon(true);
                    daemonThread.start();
                }
            }
            for (int i = 0; i < maxRunning; ++i) {
                running[i] = runnables[threadNumber++];
                final Thread thread = new Thread(group, running[i]);
                thread.start();
            }
            if (this.timeout != 0L) {
                final Thread timeoutThread = new Thread() {
                    @Override
                    public synchronized void run() {
                        try {
                            this.wait(Parallel.this.timeout);
                            synchronized (Parallel.this.semaphore) {
                                Parallel.this.stillRunning = false;
                                Parallel.this.timedOut = true;
                                Parallel.this.semaphore.notifyAll();
                            }
                        }
                        catch (InterruptedException ex) {}
                    }
                };
                timeoutThread.start();
            }
            try {
            Label_0342:
                while (threadNumber < numTasks && this.stillRunning) {
                    for (int i = 0; i < maxRunning; ++i) {
                        if (running[i] == null || running[i].isFinished()) {
                            running[i] = runnables[threadNumber++];
                            final Thread thread = new Thread(group, running[i]);
                            thread.start();
                            continue Label_0342;
                        }
                    }
                    this.semaphore.wait();
                }
                Label_0436:
                while (this.stillRunning) {
                    for (int i = 0; i < maxRunning; ++i) {
                        if (running[i] != null && !running[i].isFinished()) {
                            this.semaphore.wait();
                            continue Label_0436;
                        }
                    }
                    this.stillRunning = false;
                }
            }
            catch (InterruptedException ie) {
                interrupted = true;
            }
            if (!this.timedOut && !this.failOnAny) {
                this.killAll(running);
            }
        }
        if (interrupted) {
            throw new BuildException("Parallel execution interrupted.");
        }
        if (this.timedOut) {
            throw new BuildException("Parallel execution timed out");
        }
        this.exceptionMessage = new StringBuffer();
        this.numExceptions = 0;
        this.firstException = null;
        this.firstLocation = Location.UNKNOWN_LOCATION;
        this.processExceptions(daemons);
        this.processExceptions(runnables);
        if (this.numExceptions == 1) {
            if (this.firstException instanceof BuildException) {
                throw (BuildException)this.firstException;
            }
            throw new BuildException(this.firstException);
        }
        else if (this.numExceptions > 1) {
            throw new BuildException(this.exceptionMessage.toString(), this.firstLocation);
        }
    }
    
    private void killAll(final TaskRunnable[] running) {
        int tries = 0;
        boolean oneAlive;
        do {
            oneAlive = false;
            for (int i = 0; i < running.length; ++i) {
                if (running[i] != null && !running[i].isFinished()) {
                    running[i].interrupt();
                    Thread.yield();
                    oneAlive = true;
                }
            }
            if (oneAlive) {
                ++tries;
                Thread.yield();
            }
        } while (oneAlive && tries < 100);
    }
    
    public static class TaskList implements TaskContainer
    {
        private List tasks;
        
        public TaskList() {
            this.tasks = new ArrayList();
        }
        
        public void addTask(final Task nestedTask) {
            this.tasks.add(nestedTask);
        }
    }
    
    private class TaskRunnable implements Runnable
    {
        private Throwable exception;
        private Task task;
        private boolean finished;
        private volatile Thread thread;
        
        TaskRunnable(final Task task) {
            this.task = task;
        }
        
        public void run() {
            try {
                LocalProperties.get(Parallel.this.getProject()).copy();
                this.thread = Thread.currentThread();
                this.task.perform();
            }
            catch (Throwable t) {
                this.exception = t;
                if (Parallel.this.failOnAny) {
                    Parallel.this.stillRunning = false;
                }
                synchronized (Parallel.this.semaphore) {
                    this.finished = true;
                    Parallel.this.semaphore.notifyAll();
                }
            }
            finally {
                synchronized (Parallel.this.semaphore) {
                    this.finished = true;
                    Parallel.this.semaphore.notifyAll();
                }
            }
        }
        
        public Throwable getException() {
            return this.exception;
        }
        
        boolean isFinished() {
            return this.finished;
        }
        
        void interrupt() {
            this.thread.interrupt();
        }
    }
}
