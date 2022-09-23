// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.input.SecureInputHandler;
import org.apache.tools.ant.input.GreedyInputHandler;
import org.apache.tools.ant.input.PropertyFileInputHandler;
import org.apache.tools.ant.input.DefaultInputHandler;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.input.InputHandler;
import java.util.Vector;
import org.apache.tools.ant.input.InputRequest;
import org.apache.tools.ant.input.MultipleChoiceInputRequest;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.Task;

public class Input extends Task
{
    private String validargs;
    private String message;
    private String addproperty;
    private String defaultvalue;
    private Handler handler;
    private boolean messageAttribute;
    
    public void setValidargs(final String validargs) {
        this.validargs = validargs;
    }
    
    public void setAddproperty(final String addproperty) {
        this.addproperty = addproperty;
    }
    
    public void setMessage(final String message) {
        this.message = message;
        this.messageAttribute = true;
    }
    
    public void setDefaultvalue(final String defaultvalue) {
        this.defaultvalue = defaultvalue;
    }
    
    public void addText(final String msg) {
        if (this.messageAttribute && "".equals(msg.trim())) {
            return;
        }
        this.message += this.getProject().replaceProperties(msg);
    }
    
    public Input() {
        this.validargs = null;
        this.message = "";
        this.addproperty = null;
        this.defaultvalue = null;
        this.handler = null;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.addproperty != null && this.getProject().getProperty(this.addproperty) != null) {
            this.log("skipping " + this.getTaskName() + " as property " + this.addproperty + " has already been set.");
            return;
        }
        InputRequest request = null;
        if (this.validargs != null) {
            final Vector<String> accept = StringUtils.split(this.validargs, 44);
            request = new MultipleChoiceInputRequest(this.message, accept);
        }
        else {
            request = new InputRequest(this.message);
        }
        request.setDefaultValue(this.defaultvalue);
        final InputHandler h = (this.handler == null) ? this.getProject().getInputHandler() : this.handler.getInputHandler();
        h.handleInput(request);
        String value = request.getInput();
        if ((value == null || value.trim().length() == 0) && this.defaultvalue != null) {
            value = this.defaultvalue;
        }
        if (this.addproperty != null && value != null) {
            this.getProject().setNewProperty(this.addproperty, value);
        }
    }
    
    public Handler createHandler() {
        if (this.handler != null) {
            throw new BuildException("Cannot define > 1 nested input handler");
        }
        return this.handler = new Handler();
    }
    
    public class Handler extends DefBase
    {
        private String refid;
        private HandlerType type;
        private String classname;
        
        public Handler() {
            this.refid = null;
            this.type = null;
            this.classname = null;
        }
        
        public void setRefid(final String refid) {
            this.refid = refid;
        }
        
        public String getRefid() {
            return this.refid;
        }
        
        public void setClassname(final String classname) {
            this.classname = classname;
        }
        
        public String getClassname() {
            return this.classname;
        }
        
        public void setType(final HandlerType type) {
            this.type = type;
        }
        
        public HandlerType getType() {
            return this.type;
        }
        
        private InputHandler getInputHandler() {
            if (this.type != null) {
                return this.type.getInputHandler();
            }
            if (this.refid != null) {
                try {
                    return this.getProject().getReference(this.refid);
                }
                catch (ClassCastException e) {
                    throw new BuildException(this.refid + " does not denote an InputHandler", e);
                }
            }
            if (this.classname != null) {
                return (InputHandler)ClasspathUtils.newInstance(this.classname, this.createLoader(), InputHandler.class);
            }
            throw new BuildException("Must specify refid, classname or type");
        }
    }
    
    public static class HandlerType extends EnumeratedAttribute
    {
        private static final String[] VALUES;
        private static final InputHandler[] HANDLERS;
        
        @Override
        public String[] getValues() {
            return HandlerType.VALUES;
        }
        
        private InputHandler getInputHandler() {
            return HandlerType.HANDLERS[this.getIndex()];
        }
        
        static {
            VALUES = new String[] { "default", "propertyfile", "greedy", "secure" };
            HANDLERS = new InputHandler[] { new DefaultInputHandler(), new PropertyFileInputHandler(), new GreedyInputHandler(), new SecureInputHandler() };
        }
    }
}
