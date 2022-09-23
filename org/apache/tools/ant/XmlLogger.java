// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

import java.util.Enumeration;
import java.io.OutputStream;
import java.io.Writer;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import org.apache.tools.ant.util.DOMElementWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import org.w3c.dom.Node;
import org.apache.tools.ant.util.StringUtils;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Stack;
import java.util.Hashtable;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.PrintStream;

public class XmlLogger implements BuildLogger
{
    private int msgOutputLevel;
    private PrintStream outStream;
    private static DocumentBuilder builder;
    private static final String BUILD_TAG = "build";
    private static final String TARGET_TAG = "target";
    private static final String TASK_TAG = "task";
    private static final String MESSAGE_TAG = "message";
    private static final String NAME_ATTR = "name";
    private static final String TIME_ATTR = "time";
    private static final String PRIORITY_ATTR = "priority";
    private static final String LOCATION_ATTR = "location";
    private static final String ERROR_ATTR = "error";
    private static final String STACKTRACE_TAG = "stacktrace";
    private Document doc;
    private Hashtable<Task, TimedElement> tasks;
    private Hashtable<Target, TimedElement> targets;
    private Hashtable<Thread, Stack<TimedElement>> threadStacks;
    private TimedElement buildElement;
    
    private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (Exception exc) {
            throw new ExceptionInInitializerError(exc);
        }
    }
    
    public XmlLogger() {
        this.msgOutputLevel = 4;
        this.doc = XmlLogger.builder.newDocument();
        this.tasks = new Hashtable<Task, TimedElement>();
        this.targets = new Hashtable<Target, TimedElement>();
        this.threadStacks = new Hashtable<Thread, Stack<TimedElement>>();
        this.buildElement = null;
    }
    
    public void buildStarted(final BuildEvent event) {
        (this.buildElement = new TimedElement()).startTime = System.currentTimeMillis();
        this.buildElement.element = this.doc.createElement("build");
    }
    
    public void buildFinished(final BuildEvent event) {
        final long totalTime = System.currentTimeMillis() - this.buildElement.startTime;
        this.buildElement.element.setAttribute("time", DefaultLogger.formatTime(totalTime));
        if (event.getException() != null) {
            this.buildElement.element.setAttribute("error", event.getException().toString());
            final Throwable t = event.getException();
            final Text errText = this.doc.createCDATASection(StringUtils.getStackTrace(t));
            final Element stacktrace = this.doc.createElement("stacktrace");
            stacktrace.appendChild(errText);
            this.synchronizedAppend(this.buildElement.element, stacktrace);
        }
        String outFilename = event.getProject().getProperty("XmlLogger.file");
        if (outFilename == null) {
            outFilename = "log.xml";
        }
        String xslUri = event.getProject().getProperty("ant.XmlLogger.stylesheet.uri");
        if (xslUri == null) {
            xslUri = "log.xsl";
        }
        Writer out = null;
        try {
            OutputStream stream = this.outStream;
            if (stream == null) {
                stream = new FileOutputStream(outFilename);
            }
            out = new OutputStreamWriter(stream, "UTF8");
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            if (xslUri.length() > 0) {
                out.write("<?xml-stylesheet type=\"text/xsl\" href=\"" + xslUri + "\"?>\n\n");
            }
            new DOMElementWriter().write(this.buildElement.element, out, 0, "\t");
            out.flush();
        }
        catch (IOException exc) {
            throw new BuildException("Unable to write log file", exc);
        }
        finally {
            FileUtils.close(out);
        }
        this.buildElement = null;
    }
    
    private Stack<TimedElement> getStack() {
        Stack<TimedElement> threadStack = this.threadStacks.get(Thread.currentThread());
        if (threadStack == null) {
            threadStack = new Stack<TimedElement>();
            this.threadStacks.put(Thread.currentThread(), threadStack);
        }
        return threadStack;
    }
    
    public void targetStarted(final BuildEvent event) {
        final Target target = event.getTarget();
        final TimedElement targetElement = new TimedElement();
        targetElement.startTime = System.currentTimeMillis();
        targetElement.element = this.doc.createElement("target");
        targetElement.element.setAttribute("name", target.getName());
        this.targets.put(target, targetElement);
        this.getStack().push(targetElement);
    }
    
    public void targetFinished(final BuildEvent event) {
        final Target target = event.getTarget();
        final TimedElement targetElement = this.targets.get(target);
        if (targetElement != null) {
            final long totalTime = System.currentTimeMillis() - targetElement.startTime;
            targetElement.element.setAttribute("time", DefaultLogger.formatTime(totalTime));
            TimedElement parentElement = null;
            final Stack<TimedElement> threadStack = this.getStack();
            if (!threadStack.empty()) {
                final TimedElement poppedStack = threadStack.pop();
                if (poppedStack != targetElement) {
                    throw new RuntimeException("Mismatch - popped element = " + poppedStack + " finished target element = " + targetElement);
                }
                if (!threadStack.empty()) {
                    parentElement = threadStack.peek();
                }
            }
            if (parentElement == null) {
                this.synchronizedAppend(this.buildElement.element, targetElement.element);
            }
            else {
                this.synchronizedAppend(parentElement.element, targetElement.element);
            }
        }
        this.targets.remove(target);
    }
    
    public void taskStarted(final BuildEvent event) {
        final TimedElement taskElement = new TimedElement();
        taskElement.startTime = System.currentTimeMillis();
        taskElement.element = this.doc.createElement("task");
        final Task task = event.getTask();
        String name = event.getTask().getTaskName();
        if (name == null) {
            name = "";
        }
        taskElement.element.setAttribute("name", name);
        taskElement.element.setAttribute("location", event.getTask().getLocation().toString());
        this.tasks.put(task, taskElement);
        this.getStack().push(taskElement);
    }
    
    public void taskFinished(final BuildEvent event) {
        final Task task = event.getTask();
        final TimedElement taskElement = this.tasks.get(task);
        if (taskElement == null) {
            throw new RuntimeException("Unknown task " + task + " not in " + this.tasks);
        }
        final long totalTime = System.currentTimeMillis() - taskElement.startTime;
        taskElement.element.setAttribute("time", DefaultLogger.formatTime(totalTime));
        final Target target = task.getOwningTarget();
        TimedElement targetElement = null;
        if (target != null) {
            targetElement = this.targets.get(target);
        }
        if (targetElement == null) {
            this.synchronizedAppend(this.buildElement.element, taskElement.element);
        }
        else {
            this.synchronizedAppend(targetElement.element, taskElement.element);
        }
        final Stack<TimedElement> threadStack = this.getStack();
        if (!threadStack.empty()) {
            final TimedElement poppedStack = threadStack.pop();
            if (poppedStack != taskElement) {
                throw new RuntimeException("Mismatch - popped element = " + poppedStack + " finished task element = " + taskElement);
            }
        }
        this.tasks.remove(task);
    }
    
    private TimedElement getTaskElement(final Task task) {
        final TimedElement element = this.tasks.get(task);
        if (element != null) {
            return element;
        }
        final Enumeration<Task> e = this.tasks.keys();
        while (e.hasMoreElements()) {
            final Task key = e.nextElement();
            if (key instanceof UnknownElement && ((UnknownElement)key).getTask() == task) {
                return this.tasks.get(key);
            }
        }
        return null;
    }
    
    public void messageLogged(final BuildEvent event) {
        final int priority = event.getPriority();
        if (priority > this.msgOutputLevel) {
            return;
        }
        final Element messageElement = this.doc.createElement("message");
        String name = "debug";
        switch (priority) {
            case 0: {
                name = "error";
                break;
            }
            case 1: {
                name = "warn";
                break;
            }
            case 2: {
                name = "info";
                break;
            }
            default: {
                name = "debug";
                break;
            }
        }
        messageElement.setAttribute("priority", name);
        final Throwable ex = event.getException();
        if (4 <= this.msgOutputLevel && ex != null) {
            final Text errText = this.doc.createCDATASection(StringUtils.getStackTrace(ex));
            final Element stacktrace = this.doc.createElement("stacktrace");
            stacktrace.appendChild(errText);
            this.synchronizedAppend(this.buildElement.element, stacktrace);
        }
        final Text messageText = this.doc.createCDATASection(event.getMessage());
        messageElement.appendChild(messageText);
        TimedElement parentElement = null;
        final Task task = event.getTask();
        final Target target = event.getTarget();
        if (task != null) {
            parentElement = this.getTaskElement(task);
        }
        if (parentElement == null && target != null) {
            parentElement = this.targets.get(target);
        }
        if (parentElement != null) {
            this.synchronizedAppend(parentElement.element, messageElement);
        }
        else {
            this.synchronizedAppend(this.buildElement.element, messageElement);
        }
    }
    
    public void setMessageOutputLevel(final int level) {
        this.msgOutputLevel = level;
    }
    
    public void setOutputPrintStream(final PrintStream output) {
        this.outStream = new PrintStream(output, true);
    }
    
    public void setEmacsMode(final boolean emacsMode) {
    }
    
    public void setErrorPrintStream(final PrintStream err) {
    }
    
    private void synchronizedAppend(final Node parent, final Node child) {
        synchronized (parent) {
            parent.appendChild(child);
        }
    }
    
    static {
        XmlLogger.builder = getDocumentBuilder();
    }
    
    private static class TimedElement
    {
        private long startTime;
        private Element element;
        
        @Override
        public String toString() {
            return this.element.getTagName() + ":" + this.element.getAttribute("name");
        }
    }
}
