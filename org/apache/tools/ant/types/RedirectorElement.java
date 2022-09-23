// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import org.apache.tools.ant.Project;
import java.util.Stack;
import java.util.ArrayList;
import org.apache.tools.ant.util.MergingMapper;
import org.apache.tools.ant.taskdefs.Redirector;
import java.io.File;
import org.apache.tools.ant.BuildException;
import java.util.Vector;

public class RedirectorElement extends DataType
{
    private boolean usingInput;
    private boolean usingOutput;
    private boolean usingError;
    private Boolean logError;
    private String outputProperty;
    private String errorProperty;
    private String inputString;
    private Boolean append;
    private Boolean alwaysLog;
    private Boolean createEmptyFiles;
    private Mapper inputMapper;
    private Mapper outputMapper;
    private Mapper errorMapper;
    private Vector<FilterChain> inputFilterChains;
    private Vector<FilterChain> outputFilterChains;
    private Vector<FilterChain> errorFilterChains;
    private String outputEncoding;
    private String errorEncoding;
    private String inputEncoding;
    private Boolean logInputString;
    
    public RedirectorElement() {
        this.usingInput = false;
        this.usingOutput = false;
        this.usingError = false;
        this.inputFilterChains = new Vector<FilterChain>();
        this.outputFilterChains = new Vector<FilterChain>();
        this.errorFilterChains = new Vector<FilterChain>();
    }
    
    public void addConfiguredInputMapper(final Mapper inputMapper) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.inputMapper == null) {
            this.setChecked(false);
            this.inputMapper = inputMapper;
            return;
        }
        if (this.usingInput) {
            throw new BuildException("attribute \"input\" cannot coexist with a nested <inputmapper>");
        }
        throw new BuildException("Cannot have > 1 <inputmapper>");
    }
    
    public void addConfiguredOutputMapper(final Mapper outputMapper) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.outputMapper == null) {
            this.setChecked(false);
            this.outputMapper = outputMapper;
            return;
        }
        if (this.usingOutput) {
            throw new BuildException("attribute \"output\" cannot coexist with a nested <outputmapper>");
        }
        throw new BuildException("Cannot have > 1 <outputmapper>");
    }
    
    public void addConfiguredErrorMapper(final Mapper errorMapper) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.errorMapper == null) {
            this.setChecked(false);
            this.errorMapper = errorMapper;
            return;
        }
        if (this.usingError) {
            throw new BuildException("attribute \"error\" cannot coexist with a nested <errormapper>");
        }
        throw new BuildException("Cannot have > 1 <errormapper>");
    }
    
    @Override
    public void setRefid(final Reference r) throws BuildException {
        if (this.usingInput || this.usingOutput || this.usingError || this.inputString != null || this.logError != null || this.append != null || this.createEmptyFiles != null || this.inputEncoding != null || this.outputEncoding != null || this.errorEncoding != null || this.outputProperty != null || this.errorProperty != null || this.logInputString != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }
    
    public void setInput(final File input) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (this.inputString != null) {
            throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
        }
        this.usingInput = true;
        this.inputMapper = this.createMergeMapper(input);
    }
    
    public void setInputString(final String inputString) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (this.usingInput) {
            throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
        }
        this.inputString = inputString;
    }
    
    public void setLogInputString(final boolean logInputString) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.logInputString = (logInputString ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setOutput(final File out) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (out == null) {
            throw new IllegalArgumentException("output file specified as null");
        }
        this.usingOutput = true;
        this.outputMapper = this.createMergeMapper(out);
    }
    
    public void setOutputEncoding(final String outputEncoding) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.outputEncoding = outputEncoding;
    }
    
    public void setErrorEncoding(final String errorEncoding) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.errorEncoding = errorEncoding;
    }
    
    public void setInputEncoding(final String inputEncoding) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.inputEncoding = inputEncoding;
    }
    
    public void setLogError(final boolean logError) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.logError = (logError ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setError(final File error) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (error == null) {
            throw new IllegalArgumentException("error file specified as null");
        }
        this.usingError = true;
        this.errorMapper = this.createMergeMapper(error);
    }
    
    public void setOutputProperty(final String outputProperty) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.outputProperty = outputProperty;
    }
    
    public void setAppend(final boolean append) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.append = (append ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setAlwaysLog(final boolean alwaysLog) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.alwaysLog = (alwaysLog ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setCreateEmptyFiles(final boolean createEmptyFiles) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.createEmptyFiles = (createEmptyFiles ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setErrorProperty(final String errorProperty) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.errorProperty = errorProperty;
    }
    
    public FilterChain createInputFilterChain() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        final FilterChain result = new FilterChain();
        result.setProject(this.getProject());
        this.inputFilterChains.add(result);
        this.setChecked(false);
        return result;
    }
    
    public FilterChain createOutputFilterChain() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        final FilterChain result = new FilterChain();
        result.setProject(this.getProject());
        this.outputFilterChains.add(result);
        this.setChecked(false);
        return result;
    }
    
    public FilterChain createErrorFilterChain() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        final FilterChain result = new FilterChain();
        result.setProject(this.getProject());
        this.errorFilterChains.add(result);
        this.setChecked(false);
        return result;
    }
    
    public void configure(final Redirector redirector) {
        this.configure(redirector, null);
    }
    
    public void configure(final Redirector redirector, final String sourcefile) {
        if (this.isReference()) {
            this.getRef().configure(redirector, sourcefile);
            return;
        }
        this.dieOnCircularReference();
        if (this.alwaysLog != null) {
            redirector.setAlwaysLog(this.alwaysLog);
        }
        if (this.logError != null) {
            redirector.setLogError(this.logError);
        }
        if (this.append != null) {
            redirector.setAppend(this.append);
        }
        if (this.createEmptyFiles != null) {
            redirector.setCreateEmptyFiles(this.createEmptyFiles);
        }
        if (this.outputProperty != null) {
            redirector.setOutputProperty(this.outputProperty);
        }
        if (this.errorProperty != null) {
            redirector.setErrorProperty(this.errorProperty);
        }
        if (this.inputString != null) {
            redirector.setInputString(this.inputString);
        }
        if (this.logInputString != null) {
            redirector.setLogInputString(this.logInputString);
        }
        if (this.inputMapper != null) {
            String[] inputTargets = null;
            try {
                inputTargets = this.inputMapper.getImplementation().mapFileName(sourcefile);
            }
            catch (NullPointerException enPeaEx) {
                if (sourcefile != null) {
                    throw enPeaEx;
                }
            }
            if (inputTargets != null && inputTargets.length > 0) {
                redirector.setInput(this.toFileArray(inputTargets));
            }
        }
        if (this.outputMapper != null) {
            String[] outputTargets = null;
            try {
                outputTargets = this.outputMapper.getImplementation().mapFileName(sourcefile);
            }
            catch (NullPointerException enPeaEx) {
                if (sourcefile != null) {
                    throw enPeaEx;
                }
            }
            if (outputTargets != null && outputTargets.length > 0) {
                redirector.setOutput(this.toFileArray(outputTargets));
            }
        }
        if (this.errorMapper != null) {
            String[] errorTargets = null;
            try {
                errorTargets = this.errorMapper.getImplementation().mapFileName(sourcefile);
            }
            catch (NullPointerException enPeaEx) {
                if (sourcefile != null) {
                    throw enPeaEx;
                }
            }
            if (errorTargets != null && errorTargets.length > 0) {
                redirector.setError(this.toFileArray(errorTargets));
            }
        }
        if (this.inputFilterChains.size() > 0) {
            redirector.setInputFilterChains(this.inputFilterChains);
        }
        if (this.outputFilterChains.size() > 0) {
            redirector.setOutputFilterChains(this.outputFilterChains);
        }
        if (this.errorFilterChains.size() > 0) {
            redirector.setErrorFilterChains(this.errorFilterChains);
        }
        if (this.inputEncoding != null) {
            redirector.setInputEncoding(this.inputEncoding);
        }
        if (this.outputEncoding != null) {
            redirector.setOutputEncoding(this.outputEncoding);
        }
        if (this.errorEncoding != null) {
            redirector.setErrorEncoding(this.errorEncoding);
        }
    }
    
    protected Mapper createMergeMapper(final File destfile) {
        final Mapper result = new Mapper(this.getProject());
        result.setClassname(MergingMapper.class.getName());
        result.setTo(destfile.getAbsolutePath());
        return result;
    }
    
    protected File[] toFileArray(final String[] name) {
        if (name == null) {
            return null;
        }
        final ArrayList<File> list = new ArrayList<File>(name.length);
        for (int i = 0; i < name.length; ++i) {
            if (name[i] != null) {
                list.add(this.getProject().resolveFile(name[i]));
            }
        }
        return list.toArray(new File[list.size()]);
    }
    
    @Override
    protected void dieOnCircularReference(final Stack<Object> stk, final Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        }
        else {
            final Mapper[] m = { this.inputMapper, this.outputMapper, this.errorMapper };
            for (int i = 0; i < m.length; ++i) {
                if (m[i] != null) {
                    stk.push(m[i]);
                    m[i].dieOnCircularReference(stk, p);
                    stk.pop();
                }
            }
            final List<? extends List<FilterChain>> filterChainLists = Arrays.asList(this.inputFilterChains, this.outputFilterChains, this.errorFilterChains);
            for (final List<FilterChain> filterChains : filterChainLists) {
                if (filterChains != null) {
                    for (final FilterChain fc : filterChains) {
                        DataType.pushAndInvokeCircularReferenceCheck(fc, stk, p);
                    }
                }
            }
            this.setChecked(true);
        }
    }
    
    private RedirectorElement getRef() {
        return (RedirectorElement)this.getCheckedRef();
    }
}
