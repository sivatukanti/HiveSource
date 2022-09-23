// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.testing;

import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.util.WorkerAnt;
import org.apache.tools.ant.taskdefs.WaitFor;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.Parallel;
import org.apache.tools.ant.Task;

public class Funtest extends Task
{
    private NestedCondition condition;
    private Parallel timedTests;
    private Sequential setup;
    private Sequential application;
    private BlockFor block;
    private Sequential tests;
    private Sequential reporting;
    private Sequential teardown;
    private long timeout;
    private long timeoutUnitMultiplier;
    private long shutdownTime;
    private long shutdownUnitMultiplier;
    private String failureProperty;
    private String failureMessage;
    private boolean failOnTeardownErrors;
    private BuildException testException;
    private BuildException teardownException;
    private BuildException applicationException;
    private BuildException taskException;
    public static final String WARN_OVERRIDING = "Overriding previous definition of ";
    public static final String APPLICATION_FORCIBLY_SHUT_DOWN = "Application forcibly shut down";
    public static final String SHUTDOWN_INTERRUPTED = "Shutdown interrupted";
    public static final String SKIPPING_TESTS = "Condition failed -skipping tests";
    public static final String APPLICATION_EXCEPTION = "Application Exception";
    public static final String TEARDOWN_EXCEPTION = "Teardown Exception";
    
    public Funtest() {
        this.timeoutUnitMultiplier = 1L;
        this.shutdownTime = 10000L;
        this.shutdownUnitMultiplier = 1L;
        this.failureMessage = "Tests failed";
        this.failOnTeardownErrors = true;
    }
    
    private void logOverride(final String name, final Object definition) {
        if (definition != null) {
            this.log("Overriding previous definition of <" + name + '>', 2);
        }
    }
    
    public ConditionBase createCondition() {
        this.logOverride("condition", this.condition);
        return this.condition = new NestedCondition();
    }
    
    public void addApplication(final Sequential sequence) {
        this.logOverride("application", this.application);
        this.application = sequence;
    }
    
    public void addSetup(final Sequential sequence) {
        this.logOverride("setup", this.setup);
        this.setup = sequence;
    }
    
    public void addBlock(final BlockFor sequence) {
        this.logOverride("block", this.block);
        this.block = sequence;
    }
    
    public void addTests(final Sequential sequence) {
        this.logOverride("tests", this.tests);
        this.tests = sequence;
    }
    
    public void addReporting(final Sequential sequence) {
        this.logOverride("reporting", this.reporting);
        this.reporting = sequence;
    }
    
    public void addTeardown(final Sequential sequence) {
        this.logOverride("teardown", this.teardown);
        this.teardown = sequence;
    }
    
    public void setFailOnTeardownErrors(final boolean failOnTeardownErrors) {
        this.failOnTeardownErrors = failOnTeardownErrors;
    }
    
    public void setFailureMessage(final String failureMessage) {
        this.failureMessage = failureMessage;
    }
    
    public void setFailureProperty(final String failureProperty) {
        this.failureProperty = failureProperty;
    }
    
    public void setShutdownTime(final long shutdownTime) {
        this.shutdownTime = shutdownTime;
    }
    
    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }
    
    public void setTimeoutUnit(final WaitFor.Unit unit) {
        this.timeoutUnitMultiplier = unit.getMultiplier();
    }
    
    public void setShutdownUnit(final WaitFor.Unit unit) {
        this.shutdownUnitMultiplier = unit.getMultiplier();
    }
    
    public BuildException getApplicationException() {
        return this.applicationException;
    }
    
    public BuildException getTeardownException() {
        return this.teardownException;
    }
    
    public BuildException getTestException() {
        return this.testException;
    }
    
    public BuildException getTaskException() {
        return this.taskException;
    }
    
    private void bind(final Task task) {
        task.bindToOwner(this);
        task.init();
    }
    
    private Parallel newParallel(final long parallelTimeout) {
        final Parallel par = new Parallel();
        this.bind(par);
        par.setFailOnAny(true);
        par.setTimeout(parallelTimeout);
        return par;
    }
    
    private Parallel newParallel(final long parallelTimeout, final Task child) {
        final Parallel par = this.newParallel(parallelTimeout);
        par.addTask(child);
        return par;
    }
    
    private void validateTask(final Task task, final String role) {
        if (task != null && task.getProject() == null) {
            throw new BuildException(role + " task is not bound to the project" + task);
        }
    }
    
    @Override
    public void execute() throws BuildException {
        this.validateTask(this.setup, "setup");
        this.validateTask(this.application, "application");
        this.validateTask(this.tests, "tests");
        this.validateTask(this.reporting, "reporting");
        this.validateTask(this.teardown, "teardown");
        if (this.condition != null && !this.condition.eval()) {
            this.log("Condition failed -skipping tests");
            return;
        }
        final long timeoutMillis = this.timeout * this.timeoutUnitMultiplier;
        final Parallel applicationRun = this.newParallel(timeoutMillis);
        final WorkerAnt worker = new WorkerAnt(applicationRun, null);
        if (this.application != null) {
            applicationRun.addTask(this.application);
        }
        long testRunTimeout = 0L;
        final Sequential testRun = new Sequential();
        this.bind(testRun);
        if (this.block != null) {
            final TaskAdapter ta = new TaskAdapter(this.block);
            ta.bindToOwner(this);
            this.validateTask(ta, "block");
            testRun.addTask(ta);
            testRunTimeout = this.block.calculateMaxWaitMillis();
        }
        if (this.tests != null) {
            testRun.addTask(this.tests);
            testRunTimeout += timeoutMillis;
        }
        if (this.reporting != null) {
            testRun.addTask(this.reporting);
            testRunTimeout += timeoutMillis;
        }
        this.timedTests = this.newParallel(testRunTimeout, testRun);
        try {
            if (this.setup != null) {
                final Parallel setupRun = this.newParallel(timeoutMillis, this.setup);
                setupRun.execute();
            }
            worker.start();
            this.timedTests.execute();
        }
        catch (BuildException e) {
            this.testException = e;
        }
        finally {
            if (this.teardown != null) {
                try {
                    final Parallel teardownRun = this.newParallel(timeoutMillis, this.teardown);
                    teardownRun.execute();
                }
                catch (BuildException e2) {
                    this.teardownException = e2;
                }
            }
        }
        try {
            final long shutdownTimeMillis = this.shutdownTime * this.shutdownUnitMultiplier;
            worker.waitUntilFinished(shutdownTimeMillis);
            if (worker.isAlive()) {
                this.log("Application forcibly shut down", 1);
                worker.interrupt();
                worker.waitUntilFinished(shutdownTimeMillis);
            }
        }
        catch (InterruptedException e3) {
            this.log("Shutdown interrupted", e3, 3);
        }
        this.applicationException = worker.getBuildException();
        this.processExceptions();
    }
    
    protected void processExceptions() {
        this.taskException = this.testException;
        if (this.applicationException != null) {
            if (this.taskException == null || this.taskException instanceof BuildTimeoutException) {
                this.taskException = this.applicationException;
            }
            else {
                this.ignoringThrowable("Application Exception", this.applicationException);
            }
        }
        if (this.teardownException != null) {
            if (this.taskException == null && this.failOnTeardownErrors) {
                this.taskException = this.teardownException;
            }
            else {
                this.ignoringThrowable("Teardown Exception", this.teardownException);
            }
        }
        if (this.failureProperty != null && this.getProject().getProperty(this.failureProperty) != null) {
            this.log(this.failureMessage);
            if (this.taskException == null) {
                this.taskException = new BuildException(this.failureMessage);
            }
        }
        if (this.taskException != null) {
            throw this.taskException;
        }
    }
    
    protected void ignoringThrowable(final String type, final Throwable thrown) {
        this.log(type + ": " + thrown.toString(), thrown, 1);
    }
    
    private static class NestedCondition extends ConditionBase implements Condition
    {
        public boolean eval() {
            if (this.countConditions() != 1) {
                throw new BuildException("A single nested condition is required.");
            }
            return this.getConditions().nextElement().eval();
        }
    }
}
