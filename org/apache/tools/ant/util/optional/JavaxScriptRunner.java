// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util.optional;

import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.ReflectWrapper;
import org.apache.tools.ant.util.ScriptRunnerBase;

public class JavaxScriptRunner extends ScriptRunnerBase
{
    private ReflectWrapper engine;
    
    @Override
    public String getManagerName() {
        return "javax";
    }
    
    @Override
    public boolean supportsLanguage() {
        if (this.engine != null) {
            return true;
        }
        this.checkLanguage();
        final ClassLoader origLoader = this.replaceContextLoader();
        try {
            return this.createEngine() != null;
        }
        catch (Exception ex) {
            return false;
        }
        finally {
            this.restoreContextLoader(origLoader);
        }
    }
    
    @Override
    public void executeScript(final String execName) throws BuildException {
        this.evaluateScript(execName);
    }
    
    @Override
    public Object evaluateScript(final String execName) throws BuildException {
        this.checkLanguage();
        final ClassLoader origLoader = this.replaceContextLoader();
        try {
            final ReflectWrapper engine = this.createEngine();
            if (engine == null) {
                throw new BuildException("Unable to create javax script engine for " + this.getLanguage());
            }
            for (final String key : this.getBeans().keySet()) {
                final Object value = this.getBeans().get(key);
                if ("FX".equalsIgnoreCase(this.getLanguage())) {
                    engine.invoke("put", String.class, key + ":" + value.getClass().getName(), Object.class, value);
                }
                else {
                    engine.invoke("put", String.class, key, Object.class, value);
                }
            }
            return engine.invoke("eval", String.class, this.getScript());
        }
        catch (BuildException be) {
            throw unwrap(be);
        }
        catch (Exception be2) {
            Throwable t = be2;
            final Throwable te = be2.getCause();
            if (te != null) {
                if (te instanceof BuildException) {
                    throw (BuildException)te;
                }
                t = te;
            }
            throw new BuildException(t);
        }
        finally {
            this.restoreContextLoader(origLoader);
        }
    }
    
    private ReflectWrapper createEngine() throws Exception {
        if (this.engine != null) {
            return this.engine;
        }
        final ReflectWrapper manager = new ReflectWrapper(this.getClass().getClassLoader(), "javax.script.ScriptEngineManager");
        final Object e = manager.invoke("getEngineByName", String.class, this.getLanguage());
        if (e == null) {
            return null;
        }
        final ReflectWrapper ret = new ReflectWrapper(e);
        if (this.getKeepEngine()) {
            this.engine = ret;
        }
        return ret;
    }
    
    private static BuildException unwrap(final Throwable t) {
        BuildException deepest = (t instanceof BuildException) ? ((BuildException)t) : null;
        Throwable current = t;
        while (current.getCause() != null) {
            current = current.getCause();
            if (current instanceof BuildException) {
                deepest = (BuildException)current;
            }
        }
        return deepest;
    }
}
