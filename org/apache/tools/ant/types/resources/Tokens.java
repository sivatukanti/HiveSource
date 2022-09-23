// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.Project;
import java.util.Stack;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import org.apache.tools.ant.BuildException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.ConcatResourceInputStream;
import org.apache.tools.ant.util.LineTokenizer;
import java.util.Collections;
import org.apache.tools.ant.types.Resource;
import java.util.Collection;
import org.apache.tools.ant.util.Tokenizer;

public class Tokens extends BaseResourceCollectionWrapper
{
    private Tokenizer tokenizer;
    private String encoding;
    
    @Override
    protected synchronized Collection<Resource> getCollection() {
        final ResourceCollection rc = this.getResourceCollection();
        if (rc.size() == 0) {
            return (Collection<Resource>)Collections.emptySet();
        }
        if (this.tokenizer == null) {
            this.tokenizer = new LineTokenizer();
        }
        final ConcatResourceInputStream cat = new ConcatResourceInputStream(rc);
        cat.setManagingComponent(this);
        try {
            InputStreamReader rdr = null;
            if (this.encoding == null) {
                rdr = new InputStreamReader(cat);
            }
            else {
                try {
                    rdr = new InputStreamReader(cat, this.encoding);
                }
                catch (UnsupportedEncodingException e) {
                    throw new BuildException(e);
                }
            }
            final ArrayList<Resource> result = new ArrayList<Resource>();
            for (String s = this.tokenizer.getToken(rdr); s != null; s = this.tokenizer.getToken(rdr)) {
                final StringResource resource = new StringResource(s);
                resource.setProject(this.getProject());
                result.add(resource);
            }
            return result;
        }
        catch (IOException e2) {
            throw new BuildException("Error reading tokens", e2);
        }
        finally {
            FileUtils.close(cat);
        }
    }
    
    public synchronized void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public synchronized void add(final Tokenizer tokenizer) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.tokenizer != null) {
            throw new BuildException("Only one nested tokenizer allowed.");
        }
        this.tokenizer = tokenizer;
        this.setChecked(false);
    }
    
    @Override
    protected synchronized void dieOnCircularReference(final Stack<Object> stk, final Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        super.dieOnCircularReference(stk, p);
        if (!this.isReference()) {
            if (this.tokenizer instanceof DataType) {
                DataType.pushAndInvokeCircularReferenceCheck((DataType)this.tokenizer, stk, p);
            }
            this.setChecked(true);
        }
    }
}
