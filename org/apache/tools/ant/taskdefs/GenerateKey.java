// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Enumeration;
import java.util.Vector;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class GenerateKey extends Task
{
    protected String alias;
    protected String keystore;
    protected String storepass;
    protected String storetype;
    protected String keypass;
    protected String sigalg;
    protected String keyalg;
    protected String dname;
    protected DistinguishedName expandedDname;
    protected int keysize;
    protected int validity;
    protected boolean verbose;
    
    public DistinguishedName createDname() throws BuildException {
        if (null != this.expandedDname) {
            throw new BuildException("DName sub-element can only be specified once.");
        }
        if (null != this.dname) {
            throw new BuildException("It is not possible to specify dname  both as attribute and element.");
        }
        return this.expandedDname = new DistinguishedName();
    }
    
    public void setDname(final String dname) {
        if (null != this.expandedDname) {
            throw new BuildException("It is not possible to specify dname  both as attribute and element.");
        }
        this.dname = dname;
    }
    
    public void setAlias(final String alias) {
        this.alias = alias;
    }
    
    public void setKeystore(final String keystore) {
        this.keystore = keystore;
    }
    
    public void setStorepass(final String storepass) {
        this.storepass = storepass;
    }
    
    public void setStoretype(final String storetype) {
        this.storetype = storetype;
    }
    
    public void setKeypass(final String keypass) {
        this.keypass = keypass;
    }
    
    public void setSigalg(final String sigalg) {
        this.sigalg = sigalg;
    }
    
    public void setKeyalg(final String keyalg) {
        this.keyalg = keyalg;
    }
    
    public void setKeysize(final String keysize) throws BuildException {
        try {
            this.keysize = Integer.parseInt(keysize);
        }
        catch (NumberFormatException nfe) {
            throw new BuildException("KeySize attribute should be a integer");
        }
    }
    
    public void setValidity(final String validity) throws BuildException {
        try {
            this.validity = Integer.parseInt(validity);
        }
        catch (NumberFormatException nfe) {
            throw new BuildException("Validity attribute should be a integer");
        }
    }
    
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
    
    @Override
    public void execute() throws BuildException {
        if (null == this.alias) {
            throw new BuildException("alias attribute must be set");
        }
        if (null == this.storepass) {
            throw new BuildException("storepass attribute must be set");
        }
        if (null == this.dname && null == this.expandedDname) {
            throw new BuildException("dname must be set");
        }
        final StringBuffer sb = new StringBuffer();
        sb.append("-genkey ");
        if (this.verbose) {
            sb.append("-v ");
        }
        sb.append("-alias \"");
        sb.append(this.alias);
        sb.append("\" ");
        if (null != this.dname) {
            sb.append("-dname \"");
            sb.append(this.dname);
            sb.append("\" ");
        }
        if (null != this.expandedDname) {
            sb.append("-dname \"");
            sb.append(this.expandedDname);
            sb.append("\" ");
        }
        if (null != this.keystore) {
            sb.append("-keystore \"");
            sb.append(this.keystore);
            sb.append("\" ");
        }
        if (null != this.storepass) {
            sb.append("-storepass \"");
            sb.append(this.storepass);
            sb.append("\" ");
        }
        if (null != this.storetype) {
            sb.append("-storetype \"");
            sb.append(this.storetype);
            sb.append("\" ");
        }
        sb.append("-keypass \"");
        if (null != this.keypass) {
            sb.append(this.keypass);
        }
        else {
            sb.append(this.storepass);
        }
        sb.append("\" ");
        if (null != this.sigalg) {
            sb.append("-sigalg \"");
            sb.append(this.sigalg);
            sb.append("\" ");
        }
        if (null != this.keyalg) {
            sb.append("-keyalg \"");
            sb.append(this.keyalg);
            sb.append("\" ");
        }
        if (0 < this.keysize) {
            sb.append("-keysize \"");
            sb.append(this.keysize);
            sb.append("\" ");
        }
        if (0 < this.validity) {
            sb.append("-validity \"");
            sb.append(this.validity);
            sb.append("\" ");
        }
        this.log("Generating Key for " + this.alias);
        final ExecTask cmd = new ExecTask(this);
        cmd.setExecutable(JavaEnvUtils.getJdkExecutable("keytool"));
        final Commandline.Argument arg = cmd.createArg();
        arg.setLine(sb.toString());
        cmd.setFailonerror(true);
        cmd.setTaskName(this.getTaskName());
        cmd.execute();
    }
    
    public static class DnameParam
    {
        private String name;
        private String value;
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setValue(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    public static class DistinguishedName
    {
        private Vector<DnameParam> params;
        
        public DistinguishedName() {
            this.params = new Vector<DnameParam>();
        }
        
        public Object createParam() {
            final DnameParam param = new DnameParam();
            this.params.addElement(param);
            return param;
        }
        
        public Enumeration<DnameParam> getParams() {
            return this.params.elements();
        }
        
        @Override
        public String toString() {
            final int size = this.params.size();
            final StringBuffer sb = new StringBuffer();
            boolean firstPass = true;
            for (int i = 0; i < size; ++i) {
                if (!firstPass) {
                    sb.append(" ,");
                }
                firstPass = false;
                final DnameParam param = this.params.elementAt(i);
                sb.append(this.encode(param.getName()));
                sb.append('=');
                sb.append(this.encode(param.getValue()));
            }
            return sb.toString();
        }
        
        public String encode(final String string) {
            int end = string.indexOf(44);
            if (-1 == end) {
                return string;
            }
            final StringBuffer sb = new StringBuffer();
            int start;
            for (start = 0; -1 != end; end = string.indexOf(44, start)) {
                sb.append(string.substring(start, end));
                sb.append("\\,");
                start = end + 1;
            }
            sb.append(string.substring(start));
            return sb.toString();
        }
    }
}
