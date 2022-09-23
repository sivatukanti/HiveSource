// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.script;

import java.util.Locale;
import org.apache.tools.ant.types.ResourceCollection;
import java.io.File;
import org.apache.tools.ant.util.ScriptRunnerBase;
import org.apache.tools.ant.util.ClasspathUtils;
import java.util.Iterator;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.ProjectHelper;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Project;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.List;
import org.apache.tools.ant.util.ScriptRunnerHelper;
import org.apache.tools.ant.taskdefs.DefBase;

public class ScriptDef extends DefBase
{
    private ScriptRunnerHelper helper;
    private String name;
    private List attributes;
    private List nestedElements;
    private Set attributeSet;
    private Map nestedElementMap;
    
    public ScriptDef() {
        this.helper = new ScriptRunnerHelper();
        this.attributes = new ArrayList();
        this.nestedElements = new ArrayList();
    }
    
    @Override
    public void setProject(final Project project) {
        super.setProject(project);
        this.helper.setProjectComponent(this);
        this.helper.setSetBeans(false);
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public boolean isAttributeSupported(final String attributeName) {
        return this.attributeSet.contains(attributeName);
    }
    
    public void addAttribute(final Attribute attribute) {
        this.attributes.add(attribute);
    }
    
    public void addElement(final NestedElement nestedElement) {
        this.nestedElements.add(nestedElement);
    }
    
    @Override
    public void execute() {
        if (this.name == null) {
            throw new BuildException("scriptdef requires a name attribute to name the script");
        }
        if (this.helper.getLanguage() == null) {
            throw new BuildException("<scriptdef> requires a language attribute to specify the script language");
        }
        if (this.getAntlibClassLoader() != null || this.hasCpDelegate()) {
            this.helper.setClassLoader(this.createLoader());
        }
        this.attributeSet = new HashSet();
        for (final Attribute attribute : this.attributes) {
            if (attribute.name == null) {
                throw new BuildException("scriptdef <attribute> elements must specify an attribute name");
            }
            if (this.attributeSet.contains(attribute.name)) {
                throw new BuildException("scriptdef <" + this.name + "> declares " + "the " + attribute.name + " attribute more than once");
            }
            this.attributeSet.add(attribute.name);
        }
        this.nestedElementMap = new HashMap();
        for (final NestedElement nestedElement : this.nestedElements) {
            if (nestedElement.name == null) {
                throw new BuildException("scriptdef <element> elements must specify an element name");
            }
            if (this.nestedElementMap.containsKey(nestedElement.name)) {
                throw new BuildException("scriptdef <" + this.name + "> declares " + "the " + nestedElement.name + " nested element more " + "than once");
            }
            if (nestedElement.className == null && nestedElement.type == null) {
                throw new BuildException("scriptdef <element> elements must specify either a classname or type attribute");
            }
            if (nestedElement.className != null && nestedElement.type != null) {
                throw new BuildException("scriptdef <element> elements must specify only one of the classname and type attributes");
            }
            this.nestedElementMap.put(nestedElement.name, nestedElement);
        }
        final Map scriptRepository = this.lookupScriptRepository();
        scriptRepository.put(this.name = ProjectHelper.genComponentName(this.getURI(), this.name), this);
        final AntTypeDefinition def = new AntTypeDefinition();
        def.setName(this.name);
        def.setClass(ScriptDefBase.class);
        ComponentHelper.getComponentHelper(this.getProject()).addDataTypeDefinition(def);
    }
    
    private Map lookupScriptRepository() {
        Map scriptRepository = null;
        final Project p = this.getProject();
        synchronized (p) {
            scriptRepository = p.getReference("org.apache.ant.scriptrepo");
            if (scriptRepository == null) {
                scriptRepository = new HashMap();
                p.addReference("org.apache.ant.scriptrepo", scriptRepository);
            }
        }
        return scriptRepository;
    }
    
    public Object createNestedElement(final String elementName) {
        final NestedElement definition = this.nestedElementMap.get(elementName);
        if (definition == null) {
            throw new BuildException("<" + this.name + "> does not support " + "the <" + elementName + "> nested element");
        }
        Object instance = null;
        final String classname = definition.className;
        if (classname == null) {
            instance = this.getProject().createTask(definition.type);
            if (instance == null) {
                instance = this.getProject().createDataType(definition.type);
            }
        }
        else {
            final ClassLoader loader = this.createLoader();
            try {
                instance = ClasspathUtils.newInstance(classname, loader);
            }
            catch (BuildException e) {
                instance = ClasspathUtils.newInstance(classname, ScriptDef.class.getClassLoader());
            }
            this.getProject().setProjectReference(instance);
        }
        if (instance == null) {
            throw new BuildException("<" + this.name + "> is unable to create " + "the <" + elementName + "> nested element");
        }
        return instance;
    }
    
    @Deprecated
    public void executeScript(final Map attributes, final Map elements) {
        this.executeScript(attributes, elements, null);
    }
    
    public void executeScript(final Map attributes, final Map elements, final ScriptDefBase instance) {
        final ScriptRunnerBase runner = this.helper.getScriptRunner();
        runner.addBean("attributes", attributes);
        runner.addBean("elements", elements);
        runner.addBean("project", this.getProject());
        if (instance != null) {
            runner.addBean("self", instance);
        }
        runner.executeScript("scriptdef_" + this.name);
    }
    
    public void setManager(final String manager) {
        this.helper.setManager(manager);
    }
    
    public void setLanguage(final String language) {
        this.helper.setLanguage(language);
    }
    
    public void setSrc(final File file) {
        this.helper.setSrc(file);
    }
    
    public void addText(final String text) {
        this.helper.addText(text);
    }
    
    public void add(final ResourceCollection resource) {
        this.helper.add(resource);
    }
    
    public static class Attribute
    {
        private String name;
        
        public void setName(final String name) {
            this.name = name.toLowerCase(Locale.ENGLISH);
        }
    }
    
    public static class NestedElement
    {
        private String name;
        private String type;
        private String className;
        
        public void setName(final String name) {
            this.name = name.toLowerCase(Locale.ENGLISH);
        }
        
        public void setType(final String type) {
            this.type = type;
        }
        
        public void setClassName(final String className) {
            this.className = className;
        }
    }
}
