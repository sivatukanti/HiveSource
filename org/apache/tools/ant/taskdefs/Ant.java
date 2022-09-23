// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.Reference;
import java.lang.reflect.Method;
import org.apache.tools.ant.ProjectComponent;
import java.util.Enumeration;
import java.util.Set;
import java.util.HashSet;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.BuildException;
import java.util.Collection;
import org.apache.tools.ant.util.VectorSet;
import java.util.Iterator;
import java.util.Hashtable;
import java.io.IOException;
import org.apache.tools.ant.DefaultLogger;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.types.PropertySet;
import java.io.PrintStream;
import org.apache.tools.ant.Project;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public class Ant extends Task
{
    private static final FileUtils FILE_UTILS;
    private File dir;
    private String antFile;
    private String output;
    private boolean inheritAll;
    private boolean inheritRefs;
    private Vector<Property> properties;
    private Vector<Reference> references;
    private Project newProject;
    private PrintStream out;
    private Vector<PropertySet> propertySets;
    private Vector<String> targets;
    private boolean targetAttributeSet;
    private boolean useNativeBasedir;
    
    public Ant() {
        this.dir = null;
        this.antFile = null;
        this.output = null;
        this.inheritAll = true;
        this.inheritRefs = false;
        this.properties = new Vector<Property>();
        this.references = new Vector<Reference>();
        this.out = null;
        this.propertySets = new Vector<PropertySet>();
        this.targets = new Vector<String>();
        this.targetAttributeSet = false;
        this.useNativeBasedir = false;
    }
    
    public Ant(final Task owner) {
        this.dir = null;
        this.antFile = null;
        this.output = null;
        this.inheritAll = true;
        this.inheritRefs = false;
        this.properties = new Vector<Property>();
        this.references = new Vector<Reference>();
        this.out = null;
        this.propertySets = new Vector<PropertySet>();
        this.targets = new Vector<String>();
        this.targetAttributeSet = false;
        this.useNativeBasedir = false;
        this.bindToOwner(owner);
    }
    
    public void setUseNativeBasedir(final boolean b) {
        this.useNativeBasedir = b;
    }
    
    public void setInheritAll(final boolean value) {
        this.inheritAll = value;
    }
    
    public void setInheritRefs(final boolean value) {
        this.inheritRefs = value;
    }
    
    @Override
    public void init() {
        (this.newProject = this.getProject().createSubProject()).setJavaVersionProperty();
    }
    
    private void reinit() {
        this.init();
    }
    
    private void initializeProject() {
        this.newProject.setInputHandler(this.getProject().getInputHandler());
        final Iterator<BuildListener> iter = this.getBuildListeners();
        while (iter.hasNext()) {
            this.newProject.addBuildListener(iter.next());
        }
        if (this.output != null) {
            File outfile = null;
            if (this.dir != null) {
                outfile = Ant.FILE_UTILS.resolveFile(this.dir, this.output);
            }
            else {
                outfile = this.getProject().resolveFile(this.output);
            }
            try {
                this.out = new PrintStream(new FileOutputStream(outfile));
                final DefaultLogger logger = new DefaultLogger();
                logger.setMessageOutputLevel(2);
                logger.setOutputPrintStream(this.out);
                logger.setErrorPrintStream(this.out);
                this.newProject.addBuildListener(logger);
            }
            catch (IOException ex) {
                this.log("Ant: Can't set output to " + this.output);
            }
        }
        if (this.useNativeBasedir) {
            this.addAlmostAll(this.getProject().getUserProperties(), PropertyType.USER);
        }
        else {
            this.getProject().copyUserProperties(this.newProject);
        }
        if (!this.inheritAll) {
            this.newProject.initProperties();
        }
        else {
            this.addAlmostAll(this.getProject().getProperties(), PropertyType.PLAIN);
        }
        for (final PropertySet ps : this.propertySets) {
            this.addAlmostAll(ps.getProperties(), PropertyType.PLAIN);
        }
    }
    
    public void handleOutput(final String outputToHandle) {
        if (this.newProject != null) {
            this.newProject.demuxOutput(outputToHandle, false);
        }
        else {
            super.handleOutput(outputToHandle);
        }
    }
    
    public int handleInput(final byte[] buffer, final int offset, final int length) throws IOException {
        if (this.newProject != null) {
            return this.newProject.demuxInput(buffer, offset, length);
        }
        return super.handleInput(buffer, offset, length);
    }
    
    public void handleFlush(final String toFlush) {
        if (this.newProject != null) {
            this.newProject.demuxFlush(toFlush, false);
        }
        else {
            super.handleFlush(toFlush);
        }
    }
    
    public void handleErrorOutput(final String errorOutputToHandle) {
        if (this.newProject != null) {
            this.newProject.demuxOutput(errorOutputToHandle, true);
        }
        else {
            super.handleErrorOutput(errorOutputToHandle);
        }
    }
    
    public void handleErrorFlush(final String errorOutputToFlush) {
        if (this.newProject != null) {
            this.newProject.demuxFlush(errorOutputToFlush, true);
        }
        else {
            super.handleErrorFlush(errorOutputToFlush);
        }
    }
    
    @Override
    public void execute() throws BuildException {
        final File savedDir = this.dir;
        final String savedAntFile = this.antFile;
        final Vector<String> locals = new VectorSet<String>(this.targets);
        try {
            this.getNewProject();
            if (this.dir == null && this.inheritAll) {
                this.dir = this.getProject().getBaseDir();
            }
            this.initializeProject();
            if (this.dir != null) {
                if (!this.useNativeBasedir) {
                    this.newProject.setBaseDir(this.dir);
                    if (savedDir != null) {
                        this.newProject.setInheritedProperty("basedir", this.dir.getAbsolutePath());
                    }
                }
            }
            else {
                this.dir = this.getProject().getBaseDir();
            }
            this.overrideProperties();
            if (this.antFile == null) {
                this.antFile = this.getDefaultBuildFile();
            }
            final File file = Ant.FILE_UTILS.resolveFile(this.dir, this.antFile);
            this.antFile = file.getAbsolutePath();
            this.log("calling target(s) " + ((locals.size() > 0) ? locals.toString() : "[default]") + " in build file " + this.antFile, 3);
            this.newProject.setUserProperty("ant.file", this.antFile);
            final String thisAntFile = this.getProject().getProperty("ant.file");
            if (thisAntFile != null && file.equals(this.getProject().resolveFile(thisAntFile)) && this.getOwningTarget() != null && this.getOwningTarget().getName().equals("")) {
                if (this.getTaskName().equals("antcall")) {
                    throw new BuildException("antcall must not be used at the top level.");
                }
                throw new BuildException(this.getTaskName() + " task at the" + " top level must not invoke" + " its own build file.");
            }
            else {
                try {
                    ProjectHelper.configureProject(this.newProject, file);
                }
                catch (BuildException ex) {
                    throw ProjectHelper.addLocationToBuildException(ex, this.getLocation());
                }
                if (locals.size() == 0) {
                    final String defaultTarget = this.newProject.getDefaultTarget();
                    if (defaultTarget != null) {
                        locals.add(defaultTarget);
                    }
                }
                if (this.newProject.getProperty("ant.file").equals(this.getProject().getProperty("ant.file")) && this.getOwningTarget() != null) {
                    final String owningTargetName = this.getOwningTarget().getName();
                    if (locals.contains(owningTargetName)) {
                        throw new BuildException(this.getTaskName() + " task calling " + "its own parent target.");
                    }
                    boolean circular = false;
                    Target other;
                    for (Iterator<String> it = locals.iterator(); !circular && it.hasNext(); circular |= (other != null && other.dependsOn(owningTargetName))) {
                        other = this.getProject().getTargets().get(it.next());
                    }
                    if (circular) {
                        throw new BuildException(this.getTaskName() + " task calling a target" + " that depends on" + " its parent target '" + owningTargetName + "'.");
                    }
                }
                this.addReferences();
                if (locals.size() > 0 && (locals.size() != 1 || !"".equals(locals.get(0)))) {
                    BuildException be = null;
                    try {
                        this.log("Entering " + this.antFile + "...", 3);
                        this.newProject.fireSubBuildStarted();
                        this.newProject.executeTargets(locals);
                    }
                    catch (BuildException ex2) {
                        be = ProjectHelper.addLocationToBuildException(ex2, this.getLocation());
                        throw be;
                    }
                    finally {
                        this.log("Exiting " + this.antFile + ".", 3);
                        this.newProject.fireSubBuildFinished(be);
                    }
                }
            }
        }
        finally {
            this.newProject = null;
            for (final Property p : this.properties) {
                p.setProject(null);
            }
            if (this.output != null && this.out != null) {
                try {
                    this.out.close();
                }
                catch (Exception ex3) {}
            }
            this.dir = savedDir;
            this.antFile = savedAntFile;
        }
    }
    
    protected String getDefaultBuildFile() {
        return "build.xml";
    }
    
    private void overrideProperties() throws BuildException {
        final Set<String> set = new HashSet<String>();
        for (int i = this.properties.size() - 1; i >= 0; --i) {
            final Property p = this.properties.get(i);
            if (p.getName() != null && !p.getName().equals("")) {
                if (set.contains(p.getName())) {
                    this.properties.remove(i);
                }
                else {
                    set.add(p.getName());
                }
            }
        }
        final Enumeration<Property> e = this.properties.elements();
        while (e.hasMoreElements()) {
            final Property p = e.nextElement();
            p.setProject(this.newProject);
            p.execute();
        }
        if (this.useNativeBasedir) {
            this.addAlmostAll(this.getProject().getInheritedProperties(), PropertyType.INHERITED);
        }
        else {
            this.getProject().copyInheritedProperties(this.newProject);
        }
    }
    
    private void addReferences() throws BuildException {
        final Hashtable<String, Object> thisReferences = (Hashtable<String, Object>)this.getProject().getReferences().clone();
        for (final Reference ref : this.references) {
            final String refid = ref.getRefId();
            if (refid == null) {
                throw new BuildException("the refid attribute is required for reference elements");
            }
            if (!thisReferences.containsKey(refid)) {
                this.log("Parent project doesn't contain any reference '" + refid + "'", 1);
            }
            else {
                thisReferences.remove(refid);
                String toRefid = ref.getToRefid();
                if (toRefid == null) {
                    toRefid = refid;
                }
                this.copyReference(refid, toRefid);
            }
        }
        if (this.inheritRefs) {
            final Hashtable<String, Object> newReferences = this.newProject.getReferences();
            for (final String key : thisReferences.keySet()) {
                if (newReferences.containsKey(key)) {
                    continue;
                }
                this.copyReference(key, key);
                this.newProject.inheritIDReferences(this.getProject());
            }
        }
    }
    
    private void copyReference(final String oldKey, final String newKey) {
        final Object orig = this.getProject().getReference(oldKey);
        if (orig == null) {
            this.log("No object referenced by " + oldKey + ". Can't copy to " + newKey, 1);
            return;
        }
        final Class<?> c = orig.getClass();
        Object copy = orig;
        try {
            final Method cloneM = c.getMethod("clone", (Class<?>[])new Class[0]);
            if (cloneM != null) {
                copy = cloneM.invoke(orig, new Object[0]);
                this.log("Adding clone of reference " + oldKey, 4);
            }
        }
        catch (Exception ex) {}
        if (copy instanceof ProjectComponent) {
            ((ProjectComponent)copy).setProject(this.newProject);
        }
        else {
            try {
                final Method setProjectM = c.getMethod("setProject", Project.class);
                if (setProjectM != null) {
                    setProjectM.invoke(copy, this.newProject);
                }
            }
            catch (NoSuchMethodException e3) {}
            catch (Exception e2) {
                final String msg = "Error setting new project instance for reference with id " + oldKey;
                throw new BuildException(msg, e2, this.getLocation());
            }
        }
        this.newProject.addReference(newKey, copy);
    }
    
    private void addAlmostAll(final Hashtable<?, ?> props, final PropertyType type) {
        final Enumeration<?> e = props.keys();
        while (e.hasMoreElements()) {
            final String key = e.nextElement().toString();
            if (!"basedir".equals(key)) {
                if ("ant.file".equals(key)) {
                    continue;
                }
                final String value = props.get(key).toString();
                if (type == PropertyType.PLAIN) {
                    if (this.newProject.getProperty(key) != null) {
                        continue;
                    }
                    this.newProject.setNewProperty(key, value);
                }
                else if (type == PropertyType.USER) {
                    this.newProject.setUserProperty(key, value);
                }
                else {
                    if (type != PropertyType.INHERITED) {
                        continue;
                    }
                    this.newProject.setInheritedProperty(key, value);
                }
            }
        }
    }
    
    public void setDir(final File dir) {
        this.dir = dir;
    }
    
    public void setAntfile(final String antFile) {
        this.antFile = antFile;
    }
    
    public void setTarget(final String targetToAdd) {
        if (targetToAdd.equals("")) {
            throw new BuildException("target attribute must not be empty");
        }
        this.targets.add(targetToAdd);
        this.targetAttributeSet = true;
    }
    
    public void setOutput(final String outputFile) {
        this.output = outputFile;
    }
    
    public Property createProperty() {
        final Property p = new Property(true, this.getProject());
        p.setProject(this.getNewProject());
        p.setTaskName("property");
        this.properties.addElement(p);
        return p;
    }
    
    public void addReference(final Reference ref) {
        this.references.addElement(ref);
    }
    
    public void addConfiguredTarget(final TargetElement t) {
        if (this.targetAttributeSet) {
            throw new BuildException("nested target is incompatible with the target attribute");
        }
        final String name = t.getName();
        if (name.equals("")) {
            throw new BuildException("target name must not be empty");
        }
        this.targets.add(name);
    }
    
    public void addPropertyset(final PropertySet ps) {
        this.propertySets.addElement(ps);
    }
    
    protected Project getNewProject() {
        if (this.newProject == null) {
            this.reinit();
        }
        return this.newProject;
    }
    
    private Iterator<BuildListener> getBuildListeners() {
        return this.getProject().getBuildListeners().iterator();
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
    
    public static class Reference extends org.apache.tools.ant.types.Reference
    {
        private String targetid;
        
        public Reference() {
            this.targetid = null;
        }
        
        public void setToRefid(final String targetid) {
            this.targetid = targetid;
        }
        
        public String getToRefid() {
            return this.targetid;
        }
    }
    
    public static class TargetElement
    {
        private String name;
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
    
    private static final class PropertyType
    {
        private static final PropertyType PLAIN;
        private static final PropertyType INHERITED;
        private static final PropertyType USER;
        
        static {
            PLAIN = new PropertyType();
            INHERITED = new PropertyType();
            USER = new PropertyType();
        }
    }
}
