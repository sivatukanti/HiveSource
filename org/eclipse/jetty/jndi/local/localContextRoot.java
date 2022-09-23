// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jndi.local;

import javax.naming.CompoundName;
import java.util.Properties;
import org.eclipse.jetty.jndi.NamingUtil;
import org.eclipse.jetty.jndi.BindingEnumeration;
import java.util.List;
import org.eclipse.jetty.jndi.NameEnumeration;
import java.util.Collections;
import javax.naming.NamingEnumeration;
import javax.naming.NameParser;
import javax.naming.NameAlreadyBoundException;
import javax.naming.OperationNotSupportedException;
import javax.naming.Referenceable;
import javax.naming.InitialContext;
import javax.naming.LinkRef;
import javax.naming.Binding;
import javax.naming.NotContextException;
import javax.naming.spi.NamingManager;
import javax.naming.Reference;
import javax.naming.NameNotFoundException;
import javax.naming.Name;
import javax.naming.NamingException;
import java.util.Map;
import java.util.Hashtable;
import org.eclipse.jetty.jndi.NamingContext;
import org.eclipse.jetty.util.log.Logger;
import javax.naming.Context;

public class localContextRoot implements Context
{
    private static final Logger __log;
    protected static final NamingContext __root;
    private final Hashtable<String, Object> _env;
    
    public static NamingContext getRoot() {
        return localContextRoot.__root;
    }
    
    public localContextRoot(final Hashtable env) {
        this._env = new Hashtable<String, Object>(env);
    }
    
    public void close() throws NamingException {
    }
    
    public String getNameInNamespace() throws NamingException {
        return "";
    }
    
    public void destroySubcontext(final Name name) throws NamingException {
        synchronized (localContextRoot.__root) {
            localContextRoot.__root.destroySubcontext(this.getSuffix(name));
        }
    }
    
    public void destroySubcontext(final String name) throws NamingException {
        synchronized (localContextRoot.__root) {
            this.destroySubcontext(localContextRoot.__root.getNameParser("").parse(this.getSuffix(name)));
        }
    }
    
    public Hashtable getEnvironment() throws NamingException {
        return this._env;
    }
    
    public void unbind(final Name name) throws NamingException {
        synchronized (localContextRoot.__root) {
            if (name.size() == 0) {
                return;
            }
            if (localContextRoot.__root.isLocked()) {
                throw new NamingException("This context is immutable");
            }
            final Name cname = localContextRoot.__root.toCanonicalName(name);
            if (cname == null) {
                throw new NamingException("Name is null");
            }
            if (cname.size() == 0) {
                throw new NamingException("Name is empty");
            }
            if (cname.size() == 1) {
                localContextRoot.__root.removeBinding(cname);
            }
            else {
                if (localContextRoot.__log.isDebugEnabled()) {
                    localContextRoot.__log.debug("Checking for existing binding for name=" + cname + " for first element of name=" + cname.get(0), new Object[0]);
                }
                final String firstComponent = cname.get(0);
                Object ctx = null;
                if (firstComponent.equals("")) {
                    ctx = this;
                }
                else {
                    final Binding binding = localContextRoot.__root.getBinding(name.get(0));
                    if (binding == null) {
                        throw new NameNotFoundException(name.get(0) + " is not bound");
                    }
                    ctx = binding.getObject();
                    if (ctx instanceof Reference) {
                        try {
                            ctx = NamingManager.getObjectInstance(ctx, this.getNameParser("").parse(firstComponent), localContextRoot.__root, this._env);
                        }
                        catch (NamingException e) {
                            throw e;
                        }
                        catch (Exception e2) {
                            localContextRoot.__log.warn("", e2);
                            throw new NamingException(e2.getMessage());
                        }
                    }
                }
                if (!(ctx instanceof Context)) {
                    throw new NotContextException("Object bound at " + firstComponent + " is not a Context");
                }
                ((Context)ctx).unbind(cname.getSuffix(1));
            }
        }
    }
    
    public void unbind(final String name) throws NamingException {
        this.unbind(localContextRoot.__root.getNameParser("").parse(this.getSuffix(name)));
    }
    
    public Object lookupLink(final String name) throws NamingException {
        synchronized (localContextRoot.__root) {
            return this.lookupLink(localContextRoot.__root.getNameParser("").parse(this.getSuffix(name)));
        }
    }
    
    public Object lookupLink(final Name name) throws NamingException {
        synchronized (localContextRoot.__root) {
            final Name cname = localContextRoot.__root.toCanonicalName(name);
            if (cname == null) {
                final NamingContext ctx = new NamingContext(this._env, null, null, localContextRoot.__root.getNameParser(""));
                ctx.setBindings(localContextRoot.__root.getBindings());
                return ctx;
            }
            if (cname.size() == 0) {
                throw new NamingException("Name is empty");
            }
            if (cname.size() == 1) {
                final Binding binding = localContextRoot.__root.getBinding(cname);
                if (binding == null) {
                    throw new NameNotFoundException();
                }
                final Object o = binding.getObject();
                if (o instanceof Reference) {
                    try {
                        return NamingManager.getObjectInstance(o, cname.getPrefix(1), localContextRoot.__root, this._env);
                    }
                    catch (NamingException e) {
                        throw e;
                    }
                    catch (Exception e2) {
                        localContextRoot.__log.warn("", e2);
                        throw new NamingException(e2.getMessage());
                    }
                }
                return o;
            }
            else {
                final String firstComponent = cname.get(0);
                Object ctx2 = null;
                if (firstComponent.equals("")) {
                    ctx2 = this;
                }
                else {
                    final Binding binding2 = localContextRoot.__root.getBinding(firstComponent);
                    if (binding2 == null) {
                        throw new NameNotFoundException();
                    }
                    ctx2 = binding2.getObject();
                    if (ctx2 instanceof Reference) {
                        try {
                            ctx2 = NamingManager.getObjectInstance(ctx2, this.getNameParser("").parse(firstComponent), localContextRoot.__root, this._env);
                        }
                        catch (NamingException e3) {
                            throw e3;
                        }
                        catch (Exception e4) {
                            localContextRoot.__log.warn("", e4);
                            throw new NamingException(e4.getMessage());
                        }
                    }
                }
                if (!(ctx2 instanceof Context)) {
                    throw new NotContextException();
                }
                return ((Context)ctx2).lookup(cname.getSuffix(1));
            }
        }
    }
    
    public Object removeFromEnvironment(final String propName) throws NamingException {
        return this._env.remove(propName);
    }
    
    public Object lookup(final Name name) throws NamingException {
        synchronized (localContextRoot.__root) {
            if (localContextRoot.__log.isDebugEnabled()) {
                localContextRoot.__log.debug("Looking up name=\"" + name + "\"", new Object[0]);
            }
            final Name cname = localContextRoot.__root.toCanonicalName(name);
            if (cname == null || cname.size() == 0) {
                localContextRoot.__log.debug("Null or empty name, returning copy of this context", new Object[0]);
                final NamingContext ctx = new NamingContext(this._env, null, null, localContextRoot.__root.getNameParser(""));
                ctx.setBindings(localContextRoot.__root.getBindings());
                return ctx;
            }
            if (cname.size() == 1) {
                final Binding binding = localContextRoot.__root.getBinding(cname);
                if (binding == null) {
                    final NameNotFoundException nnfe = new NameNotFoundException();
                    nnfe.setRemainingName(cname);
                    throw nnfe;
                }
                final Object o = binding.getObject();
                if (!(o instanceof LinkRef)) {
                    if (o instanceof Reference) {
                        try {
                            return NamingManager.getObjectInstance(o, cname, localContextRoot.__root, this._env);
                        }
                        catch (NamingException e) {
                            throw e;
                        }
                        catch (Exception e2) {
                            throw new NamingException(e2.getMessage()) {
                                {
                                    this.initCause(e2);
                                }
                            };
                        }
                    }
                    return o;
                }
                final String linkName = ((LinkRef)o).getLinkName();
                if (linkName.startsWith("./")) {
                    return this.lookup(linkName.substring(2));
                }
                final InitialContext ictx = new InitialContext();
                return ictx.lookup(linkName);
            }
            else {
                final String firstComponent = cname.get(0);
                Object ctx2 = null;
                if (firstComponent.equals("")) {
                    ctx2 = this;
                }
                else {
                    final Binding binding2 = localContextRoot.__root.getBinding(firstComponent);
                    if (binding2 == null) {
                        final NameNotFoundException nnfe2 = new NameNotFoundException();
                        nnfe2.setRemainingName(cname);
                        throw nnfe2;
                    }
                    ctx2 = binding2.getObject();
                    if (ctx2 instanceof Reference) {
                        try {
                            ctx2 = NamingManager.getObjectInstance(ctx2, this.getNameParser("").parse(firstComponent), localContextRoot.__root, this._env);
                        }
                        catch (NamingException e3) {
                            throw e3;
                        }
                        catch (Exception e4) {
                            localContextRoot.__log.warn("", e4);
                            throw new NamingException(e4.getMessage());
                        }
                    }
                }
                if (!(ctx2 instanceof Context)) {
                    throw new NotContextException();
                }
                return ((Context)ctx2).lookup(cname.getSuffix(1));
            }
        }
    }
    
    public Object lookup(final String name) throws NamingException {
        synchronized (localContextRoot.__root) {
            return this.lookup(localContextRoot.__root.getNameParser("").parse(this.getSuffix(name)));
        }
    }
    
    public void bind(final String name, final Object obj) throws NamingException {
        synchronized (localContextRoot.__root) {
            this.bind(localContextRoot.__root.getNameParser("").parse(this.getSuffix(name)), obj);
        }
    }
    
    public void bind(final Name name, final Object obj) throws NamingException {
        synchronized (localContextRoot.__root) {
            if (localContextRoot.__root.isLocked()) {
                throw new NamingException("This context is immutable");
            }
            final Name cname = localContextRoot.__root.toCanonicalName(name);
            if (cname == null) {
                throw new NamingException("Name is null");
            }
            if (cname.size() == 0) {
                throw new NamingException("Name is empty");
            }
            if (cname.size() == 1) {
                Object objToBind = NamingManager.getStateToBind(obj, name, this, this._env);
                if (objToBind instanceof Referenceable) {
                    objToBind = ((Referenceable)objToBind).getReference();
                }
                localContextRoot.__root.addBinding(cname, objToBind);
            }
            else {
                if (localContextRoot.__log.isDebugEnabled()) {
                    localContextRoot.__log.debug("Checking for existing binding for name=" + cname + " for first element of name=" + cname.get(0), new Object[0]);
                }
                final String firstComponent = cname.get(0);
                Object ctx = null;
                if (firstComponent.equals("")) {
                    ctx = this;
                }
                else {
                    final Binding binding = localContextRoot.__root.getBinding(firstComponent);
                    if (binding == null) {
                        throw new NameNotFoundException(firstComponent + " is not bound");
                    }
                    ctx = binding.getObject();
                    if (ctx instanceof Reference) {
                        try {
                            ctx = NamingManager.getObjectInstance(ctx, this.getNameParser("").parse(firstComponent), this, this._env);
                        }
                        catch (NamingException e) {
                            throw e;
                        }
                        catch (Exception e2) {
                            localContextRoot.__log.warn("", e2);
                            throw new NamingException(e2.getMessage());
                        }
                    }
                }
                if (!(ctx instanceof Context)) {
                    throw new NotContextException("Object bound at " + firstComponent + " is not a Context");
                }
                ((Context)ctx).bind(cname.getSuffix(1), obj);
            }
        }
    }
    
    public void rebind(final Name name, final Object obj) throws NamingException {
        synchronized (localContextRoot.__root) {
            if (localContextRoot.__root.isLocked()) {
                throw new NamingException("This context is immutable");
            }
            final Name cname = localContextRoot.__root.toCanonicalName(name);
            if (cname == null) {
                throw new NamingException("Name is null");
            }
            if (cname.size() == 0) {
                throw new NamingException("Name is empty");
            }
            if (cname.size() == 1) {
                Object objToBind = NamingManager.getStateToBind(obj, name, localContextRoot.__root, this._env);
                if (objToBind instanceof Referenceable) {
                    objToBind = ((Referenceable)objToBind).getReference();
                }
                localContextRoot.__root.removeBinding(cname);
                localContextRoot.__root.addBinding(cname, objToBind);
            }
            else {
                if (localContextRoot.__log.isDebugEnabled()) {
                    localContextRoot.__log.debug("Checking for existing binding for name=" + cname + " for first element of name=" + cname.get(0), new Object[0]);
                }
                final String firstComponent = cname.get(0);
                Object ctx = null;
                if (firstComponent.equals("")) {
                    ctx = this;
                }
                else {
                    final Binding binding = localContextRoot.__root.getBinding(name.get(0));
                    if (binding == null) {
                        throw new NameNotFoundException(name.get(0) + " is not bound");
                    }
                    ctx = binding.getObject();
                    if (ctx instanceof Reference) {
                        try {
                            ctx = NamingManager.getObjectInstance(ctx, this.getNameParser("").parse(firstComponent), localContextRoot.__root, this._env);
                        }
                        catch (NamingException e) {
                            throw e;
                        }
                        catch (Exception e2) {
                            localContextRoot.__log.warn("", e2);
                            throw new NamingException(e2.getMessage());
                        }
                    }
                }
                if (!(ctx instanceof Context)) {
                    throw new NotContextException("Object bound at " + firstComponent + " is not a Context");
                }
                ((Context)ctx).rebind(cname.getSuffix(1), obj);
            }
        }
    }
    
    public void rebind(final String name, final Object obj) throws NamingException {
        synchronized (localContextRoot.__root) {
            this.rebind(localContextRoot.__root.getNameParser("").parse(this.getSuffix(name)), obj);
        }
    }
    
    public void rename(final Name oldName, final Name newName) throws NamingException {
        synchronized (localContextRoot.__root) {
            throw new OperationNotSupportedException();
        }
    }
    
    public void rename(final String oldName, final String newName) throws NamingException {
        synchronized (localContextRoot.__root) {
            throw new OperationNotSupportedException();
        }
    }
    
    public Context createSubcontext(final String name) throws NamingException {
        synchronized (localContextRoot.__root) {
            return this.createSubcontext(localContextRoot.__root.getNameParser("").parse(name));
        }
    }
    
    public Context createSubcontext(final Name name) throws NamingException {
        synchronized (localContextRoot.__root) {
            if (localContextRoot.__root.isLocked()) {
                final NamingException ne = new NamingException("This context is immutable");
                ne.setRemainingName(name);
                throw ne;
            }
            final Name cname = localContextRoot.__root.toCanonicalName(name);
            if (cname == null) {
                throw new NamingException("Name is null");
            }
            if (cname.size() == 0) {
                throw new NamingException("Name is empty");
            }
            if (cname.size() == 1) {
                final Binding binding = localContextRoot.__root.getBinding(cname);
                if (binding != null) {
                    throw new NameAlreadyBoundException(cname.toString());
                }
                final Context ctx = new NamingContext((Hashtable<String, Object>)this._env.clone(), cname.get(0), localContextRoot.__root, localContextRoot.__root.getNameParser(""));
                localContextRoot.__root.addBinding(cname, ctx);
                return ctx;
            }
            else {
                final String firstComponent = cname.get(0);
                Object ctx2 = null;
                if (firstComponent.equals("")) {
                    ctx2 = this;
                }
                else {
                    final Binding binding2 = localContextRoot.__root.getBinding(firstComponent);
                    if (binding2 == null) {
                        throw new NameNotFoundException(firstComponent + " is not bound");
                    }
                    ctx2 = binding2.getObject();
                    if (ctx2 instanceof Reference) {
                        if (localContextRoot.__log.isDebugEnabled()) {
                            localContextRoot.__log.debug("Object bound at " + firstComponent + " is a Reference", new Object[0]);
                        }
                        try {
                            ctx2 = NamingManager.getObjectInstance(ctx2, this.getNameParser("").parse(firstComponent), localContextRoot.__root, this._env);
                        }
                        catch (NamingException e) {
                            throw e;
                        }
                        catch (Exception e2) {
                            localContextRoot.__log.warn("", e2);
                            throw new NamingException(e2.getMessage());
                        }
                    }
                }
                if (ctx2 instanceof Context) {
                    return ((Context)ctx2).createSubcontext(cname.getSuffix(1));
                }
                throw new NotContextException(firstComponent + " is not a Context");
            }
        }
    }
    
    public NameParser getNameParser(final String name) throws NamingException {
        return localContextRoot.__root.getNameParser(name);
    }
    
    public NameParser getNameParser(final Name name) throws NamingException {
        return localContextRoot.__root.getNameParser(name);
    }
    
    public NamingEnumeration list(final String name) throws NamingException {
        synchronized (localContextRoot.__root) {
            return this.list(localContextRoot.__root.getNameParser("").parse(this.getSuffix(name)));
        }
    }
    
    public NamingEnumeration list(final Name name) throws NamingException {
        synchronized (localContextRoot.__root) {
            final Name cname = localContextRoot.__root.toCanonicalName(name);
            if (cname == null) {
                final List<Binding> empty = Collections.emptyList();
                return new NameEnumeration(empty.iterator());
            }
            if (cname.size() == 0) {
                return new NameEnumeration(localContextRoot.__root.getBindings().values().iterator());
            }
            final String firstComponent = cname.get(0);
            Object ctx = null;
            if (firstComponent.equals("")) {
                ctx = this;
            }
            else {
                final Binding binding = localContextRoot.__root.getBinding(firstComponent);
                if (binding == null) {
                    throw new NameNotFoundException();
                }
                ctx = binding.getObject();
                if (ctx instanceof Reference) {
                    if (localContextRoot.__log.isDebugEnabled()) {
                        localContextRoot.__log.debug("Dereferencing Reference for " + name.get(0), new Object[0]);
                    }
                    try {
                        ctx = NamingManager.getObjectInstance(ctx, this.getNameParser("").parse(firstComponent), localContextRoot.__root, this._env);
                    }
                    catch (NamingException e) {
                        throw e;
                    }
                    catch (Exception e2) {
                        localContextRoot.__log.warn("", e2);
                        throw new NamingException(e2.getMessage());
                    }
                }
            }
            if (!(ctx instanceof Context)) {
                throw new NotContextException();
            }
            return ((Context)ctx).list(cname.getSuffix(1));
        }
    }
    
    public NamingEnumeration listBindings(final Name name) throws NamingException {
        synchronized (localContextRoot.__root) {
            final Name cname = localContextRoot.__root.toCanonicalName(name);
            if (cname == null) {
                final List<Binding> empty = Collections.emptyList();
                return new BindingEnumeration(empty.iterator());
            }
            if (cname.size() == 0) {
                return new BindingEnumeration(localContextRoot.__root.getBindings().values().iterator());
            }
            final String firstComponent = cname.get(0);
            Object ctx = null;
            if (firstComponent.equals("")) {
                ctx = this;
            }
            else {
                final Binding binding = localContextRoot.__root.getBinding(firstComponent);
                if (binding == null) {
                    throw new NameNotFoundException();
                }
                ctx = binding.getObject();
                if (ctx instanceof Reference) {
                    try {
                        ctx = NamingManager.getObjectInstance(ctx, this.getNameParser("").parse(firstComponent), localContextRoot.__root, this._env);
                    }
                    catch (NamingException e) {
                        throw e;
                    }
                    catch (Exception e2) {
                        localContextRoot.__log.warn("", e2);
                        throw new NamingException(e2.getMessage());
                    }
                }
            }
            if (!(ctx instanceof Context)) {
                throw new NotContextException();
            }
            return ((Context)ctx).listBindings(cname.getSuffix(1));
        }
    }
    
    public NamingEnumeration listBindings(final String name) throws NamingException {
        synchronized (localContextRoot.__root) {
            return this.listBindings(localContextRoot.__root.getNameParser("").parse(this.getSuffix(name)));
        }
    }
    
    public Object addToEnvironment(final String propName, final Object propVal) throws NamingException {
        return this._env.put(propName, propVal);
    }
    
    public String composeName(final String name, final String prefix) throws NamingException {
        return localContextRoot.__root.composeName(name, prefix);
    }
    
    public Name composeName(final Name name, final Name prefix) throws NamingException {
        return localContextRoot.__root.composeName(name, prefix);
    }
    
    protected String getSuffix(final String url) throws NamingException {
        return url;
    }
    
    protected Name getSuffix(final Name name) throws NamingException {
        return name;
    }
    
    static {
        __log = NamingUtil.__log;
        __root = new NamingRoot();
    }
    
    static class NamingRoot extends NamingContext
    {
        public NamingRoot() {
            super(null, null, null, new LocalNameParser());
        }
    }
    
    static class LocalNameParser implements NameParser
    {
        Properties syntax;
        
        LocalNameParser() {
            (this.syntax = new Properties()).put("jndi.syntax.direction", "left_to_right");
            this.syntax.put("jndi.syntax.separator", "/");
            this.syntax.put("jndi.syntax.ignorecase", "false");
        }
        
        public Name parse(final String name) throws NamingException {
            return new CompoundName(name, this.syntax);
        }
    }
}
