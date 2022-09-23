// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jndi;

import java.util.Collections;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Iterator;
import javax.naming.CompoundName;
import javax.naming.OperationNotSupportedException;
import javax.naming.NamingEnumeration;
import javax.naming.InitialContext;
import javax.naming.LinkRef;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NotContextException;
import javax.naming.Reference;
import javax.naming.NameNotFoundException;
import javax.naming.Referenceable;
import javax.naming.spi.NamingManager;
import javax.naming.NamingException;
import javax.naming.Name;
import java.util.HashMap;
import java.util.Collection;
import javax.naming.NameParser;
import java.util.Map;
import java.util.Hashtable;
import javax.naming.Binding;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.Dumpable;
import javax.naming.Context;

public class NamingContext implements Context, Cloneable, Dumpable
{
    private static final Logger __log;
    private static final List<Binding> __empty;
    public static final String LOCK_PROPERTY = "org.eclipse.jndi.lock";
    public static final String UNLOCK_PROPERTY = "org.eclipse.jndi.unlock";
    protected final Hashtable<String, Object> _env;
    protected Map<String, Binding> _bindings;
    protected NamingContext _parent;
    protected String _name;
    protected NameParser _parser;
    private Collection<Listener> _listeners;
    
    public NamingContext(final Hashtable<String, Object> env, final String name, final NamingContext parent, final NameParser parser) {
        this._env = new Hashtable<String, Object>();
        this._bindings = new HashMap<String, Binding>();
        this._parent = null;
        this._name = null;
        this._parser = null;
        if (env != null) {
            this._env.putAll(env);
        }
        this._name = name;
        this._parent = parent;
        this._parser = parser;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final NamingContext ctx = (NamingContext)super.clone();
        ctx._env.putAll(this._env);
        ctx._bindings.putAll(this._bindings);
        return ctx;
    }
    
    public String getName() {
        return this._name;
    }
    
    public Context getParent() {
        return this._parent;
    }
    
    public void setNameParser(final NameParser parser) {
        this._parser = parser;
    }
    
    public void setEnv(final Hashtable<String, Object> env) {
        this._env.clear();
        this._env.putAll(env);
    }
    
    public Map<String, Binding> getBindings() {
        return this._bindings;
    }
    
    public void setBindings(final Map<String, Binding> bindings) {
        this._bindings = bindings;
    }
    
    public void bind(final Name name, final Object obj) throws NamingException {
        if (this.isLocked()) {
            throw new NamingException("This context is immutable");
        }
        final Name cname = this.toCanonicalName(name);
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
            this.addBinding(cname, objToBind);
        }
        else {
            if (NamingContext.__log.isDebugEnabled()) {
                NamingContext.__log.debug("Checking for existing binding for name=" + cname + " for first element of name=" + cname.get(0), new Object[0]);
            }
            final String firstComponent = cname.get(0);
            Object ctx = null;
            if (firstComponent.equals("")) {
                ctx = this;
            }
            else {
                final Binding binding = this.getBinding(firstComponent);
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
                        NamingContext.__log.warn("", e2);
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
    
    public void bind(final String name, final Object obj) throws NamingException {
        this.bind(this._parser.parse(name), obj);
    }
    
    public Context createSubcontext(final Name name) throws NamingException {
        if (this.isLocked()) {
            final NamingException ne = new NamingException("This context is immutable");
            ne.setRemainingName(name);
            throw ne;
        }
        final Name cname = this.toCanonicalName(name);
        if (cname == null) {
            throw new NamingException("Name is null");
        }
        if (cname.size() == 0) {
            throw new NamingException("Name is empty");
        }
        if (cname.size() == 1) {
            final Binding binding = this.getBinding(cname);
            if (binding != null) {
                throw new NameAlreadyBoundException(cname.toString());
            }
            final Context ctx = new NamingContext((Hashtable<String, Object>)this._env.clone(), cname.get(0), this, this._parser);
            this.addBinding(cname, ctx);
            return ctx;
        }
        else {
            final String firstComponent = cname.get(0);
            Object ctx2 = null;
            if (firstComponent.equals("")) {
                ctx2 = this;
            }
            else {
                final Binding binding2 = this.getBinding(firstComponent);
                if (binding2 == null) {
                    throw new NameNotFoundException(firstComponent + " is not bound");
                }
                ctx2 = binding2.getObject();
                if (ctx2 instanceof Reference) {
                    if (NamingContext.__log.isDebugEnabled()) {
                        NamingContext.__log.debug("Object bound at " + firstComponent + " is a Reference", new Object[0]);
                    }
                    try {
                        ctx2 = NamingManager.getObjectInstance(ctx2, this.getNameParser("").parse(firstComponent), this, this._env);
                    }
                    catch (NamingException e) {
                        throw e;
                    }
                    catch (Exception e2) {
                        NamingContext.__log.warn("", e2);
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
    
    public Context createSubcontext(final String name) throws NamingException {
        return this.createSubcontext(this._parser.parse(name));
    }
    
    public void destroySubcontext(final String name) throws NamingException {
        this.removeBinding(this._parser.parse(name));
    }
    
    public void destroySubcontext(final Name name) throws NamingException {
        this.removeBinding(name);
    }
    
    public Object lookup(final Name name) throws NamingException {
        if (NamingContext.__log.isDebugEnabled()) {
            NamingContext.__log.debug("Looking up name=\"" + name + "\"", new Object[0]);
        }
        final Name cname = this.toCanonicalName(name);
        if (cname == null || cname.size() == 0) {
            NamingContext.__log.debug("Null or empty name, returning copy of this context", new Object[0]);
            final NamingContext ctx = new NamingContext(this._env, this._name, this._parent, this._parser);
            ctx._bindings = this._bindings;
            return ctx;
        }
        if (cname.size() == 1) {
            final Binding binding = this.getBinding(cname);
            if (binding == null) {
                final NameNotFoundException nnfe = new NameNotFoundException();
                nnfe.setRemainingName(cname);
                throw nnfe;
            }
            final Object o = binding.getObject();
            if (!(o instanceof LinkRef)) {
                if (o instanceof Reference) {
                    try {
                        return NamingManager.getObjectInstance(o, cname, this, this._env);
                    }
                    catch (NamingException e) {
                        throw e;
                    }
                    catch (Exception e2) {
                        NamingContext.__log.warn("", e2);
                        throw new NamingException(e2.getMessage());
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
                final Binding binding2 = this.getBinding(firstComponent);
                if (binding2 == null) {
                    final NameNotFoundException nnfe2 = new NameNotFoundException();
                    nnfe2.setRemainingName(cname);
                    throw nnfe2;
                }
                ctx2 = binding2.getObject();
                if (ctx2 instanceof Reference) {
                    try {
                        ctx2 = NamingManager.getObjectInstance(ctx2, this.getNameParser("").parse(firstComponent), this, this._env);
                    }
                    catch (NamingException e3) {
                        throw e3;
                    }
                    catch (Exception e4) {
                        NamingContext.__log.warn("", e4);
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
    
    public Object lookup(final String name) throws NamingException {
        return this.lookup(this._parser.parse(name));
    }
    
    public Object lookupLink(final Name name) throws NamingException {
        final Name cname = this.toCanonicalName(name);
        if (cname == null) {
            final NamingContext ctx = new NamingContext(this._env, this._name, this._parent, this._parser);
            ctx._bindings = this._bindings;
            return ctx;
        }
        if (cname.size() == 0) {
            throw new NamingException("Name is empty");
        }
        if (cname.size() == 1) {
            final Binding binding = this.getBinding(cname);
            if (binding == null) {
                throw new NameNotFoundException();
            }
            final Object o = binding.getObject();
            if (o instanceof Reference) {
                try {
                    return NamingManager.getObjectInstance(o, cname.getPrefix(1), this, this._env);
                }
                catch (NamingException e) {
                    throw e;
                }
                catch (Exception e2) {
                    NamingContext.__log.warn("", e2);
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
                final Binding binding2 = this.getBinding(firstComponent);
                if (binding2 == null) {
                    throw new NameNotFoundException();
                }
                ctx2 = binding2.getObject();
                if (ctx2 instanceof Reference) {
                    try {
                        ctx2 = NamingManager.getObjectInstance(ctx2, this.getNameParser("").parse(firstComponent), this, this._env);
                    }
                    catch (NamingException e3) {
                        throw e3;
                    }
                    catch (Exception e4) {
                        NamingContext.__log.warn("", e4);
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
    
    public Object lookupLink(final String name) throws NamingException {
        return this.lookupLink(this._parser.parse(name));
    }
    
    public NamingEnumeration list(final Name name) throws NamingException {
        if (NamingContext.__log.isDebugEnabled()) {
            NamingContext.__log.debug("list() on Context=" + this.getName() + " for name=" + name, new Object[0]);
        }
        final Name cname = this.toCanonicalName(name);
        if (cname == null) {
            return new NameEnumeration(NamingContext.__empty.iterator());
        }
        if (cname.size() == 0) {
            return new NameEnumeration(this._bindings.values().iterator());
        }
        final String firstComponent = cname.get(0);
        Object ctx = null;
        if (firstComponent.equals("")) {
            ctx = this;
        }
        else {
            final Binding binding = this.getBinding(firstComponent);
            if (binding == null) {
                throw new NameNotFoundException();
            }
            ctx = binding.getObject();
            if (ctx instanceof Reference) {
                if (NamingContext.__log.isDebugEnabled()) {
                    NamingContext.__log.debug("Dereferencing Reference for " + name.get(0), new Object[0]);
                }
                try {
                    ctx = NamingManager.getObjectInstance(ctx, this.getNameParser("").parse(firstComponent), this, this._env);
                }
                catch (NamingException e) {
                    throw e;
                }
                catch (Exception e2) {
                    NamingContext.__log.warn("", e2);
                    throw new NamingException(e2.getMessage());
                }
            }
        }
        if (!(ctx instanceof Context)) {
            throw new NotContextException();
        }
        return ((Context)ctx).list(cname.getSuffix(1));
    }
    
    public NamingEnumeration list(final String name) throws NamingException {
        return this.list(this._parser.parse(name));
    }
    
    public NamingEnumeration listBindings(final Name name) throws NamingException {
        final Name cname = this.toCanonicalName(name);
        if (cname == null) {
            return new BindingEnumeration(NamingContext.__empty.iterator());
        }
        if (cname.size() == 0) {
            return new BindingEnumeration(this._bindings.values().iterator());
        }
        final String firstComponent = cname.get(0);
        Object ctx = null;
        if (firstComponent.equals("")) {
            ctx = this;
        }
        else {
            final Binding binding = this.getBinding(firstComponent);
            if (binding == null) {
                throw new NameNotFoundException();
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
                    NamingContext.__log.warn("", e2);
                    throw new NamingException(e2.getMessage());
                }
            }
        }
        if (!(ctx instanceof Context)) {
            throw new NotContextException();
        }
        return ((Context)ctx).listBindings(cname.getSuffix(1));
    }
    
    public NamingEnumeration listBindings(final String name) throws NamingException {
        return this.listBindings(this._parser.parse(name));
    }
    
    public void rebind(final Name name, final Object obj) throws NamingException {
        if (this.isLocked()) {
            throw new NamingException("This context is immutable");
        }
        final Name cname = this.toCanonicalName(name);
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
            this.removeBinding(cname);
            this.addBinding(cname, objToBind);
        }
        else {
            if (NamingContext.__log.isDebugEnabled()) {
                NamingContext.__log.debug("Checking for existing binding for name=" + cname + " for first element of name=" + cname.get(0), new Object[0]);
            }
            final String firstComponent = cname.get(0);
            Object ctx = null;
            if (firstComponent.equals("")) {
                ctx = this;
            }
            else {
                final Binding binding = this.getBinding(name.get(0));
                if (binding == null) {
                    throw new NameNotFoundException(name.get(0) + " is not bound");
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
                        NamingContext.__log.warn("", e2);
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
    
    public void rebind(final String name, final Object obj) throws NamingException {
        this.rebind(this._parser.parse(name), obj);
    }
    
    public void unbind(final String name) throws NamingException {
        this.unbind(this._parser.parse(name));
    }
    
    public void unbind(final Name name) throws NamingException {
        if (name.size() == 0) {
            return;
        }
        if (this.isLocked()) {
            throw new NamingException("This context is immutable");
        }
        final Name cname = this.toCanonicalName(name);
        if (cname == null) {
            throw new NamingException("Name is null");
        }
        if (cname.size() == 0) {
            throw new NamingException("Name is empty");
        }
        if (cname.size() == 1) {
            this.removeBinding(cname);
        }
        else {
            if (NamingContext.__log.isDebugEnabled()) {
                NamingContext.__log.debug("Checking for existing binding for name=" + cname + " for first element of name=" + cname.get(0), new Object[0]);
            }
            final String firstComponent = cname.get(0);
            Object ctx = null;
            if (firstComponent.equals("")) {
                ctx = this;
            }
            else {
                final Binding binding = this.getBinding(name.get(0));
                if (binding == null) {
                    throw new NameNotFoundException(name.get(0) + " is not bound");
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
                        NamingContext.__log.warn("", e2);
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
    
    public void rename(final Name oldName, final Name newName) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    public void rename(final String oldName, final String newName) throws NamingException {
        throw new OperationNotSupportedException();
    }
    
    public Name composeName(final Name name, final Name prefix) throws NamingException {
        if (name == null) {
            throw new NamingException("Name cannot be null");
        }
        if (prefix == null) {
            throw new NamingException("Prefix cannot be null");
        }
        final Name compoundName = (CompoundName)prefix.clone();
        compoundName.addAll(name);
        return compoundName;
    }
    
    public String composeName(final String name, final String prefix) throws NamingException {
        if (name == null) {
            throw new NamingException("Name cannot be null");
        }
        if (prefix == null) {
            throw new NamingException("Prefix cannot be null");
        }
        final Name compoundName = this._parser.parse(prefix);
        compoundName.add(name);
        return compoundName.toString();
    }
    
    public void close() throws NamingException {
    }
    
    public NameParser getNameParser(final Name name) {
        return this._parser;
    }
    
    public NameParser getNameParser(final String name) {
        return this._parser;
    }
    
    public String getNameInNamespace() throws NamingException {
        final Name name = this._parser.parse("");
        for (NamingContext c = this; c != null; c = (NamingContext)c.getParent()) {
            final String str = c.getName();
            if (str != null) {
                name.add(0, str);
            }
        }
        return name.toString();
    }
    
    public Object addToEnvironment(final String propName, final Object propVal) throws NamingException {
        if (this.isLocked() && !propName.equals("org.eclipse.jndi.unlock")) {
            throw new NamingException("This context is immutable");
        }
        return this._env.put(propName, propVal);
    }
    
    public Object removeFromEnvironment(final String propName) throws NamingException {
        if (this.isLocked()) {
            throw new NamingException("This context is immutable");
        }
        return this._env.remove(propName);
    }
    
    public Hashtable getEnvironment() {
        return (Hashtable)this._env.clone();
    }
    
    public void addBinding(final Name name, final Object obj) throws NameAlreadyBoundException {
        final String key = name.toString();
        Binding binding = new Binding(key, obj);
        final Collection<Listener> list = this.findListeners();
        for (final Listener listener : list) {
            binding = listener.bind(this, binding);
            if (binding == null) {
                break;
            }
        }
        if (NamingContext.__log.isDebugEnabled()) {
            NamingContext.__log.debug("Adding binding with key=" + key + " obj=" + obj + " for context=" + this._name + " as " + binding, new Object[0]);
        }
        if (binding != null) {
            if (this._bindings.containsKey(key)) {
                throw new NameAlreadyBoundException(name.toString());
            }
            this._bindings.put(key, binding);
        }
    }
    
    public Binding getBinding(final Name name) {
        return this._bindings.get(name.toString());
    }
    
    public Binding getBinding(final String name) {
        return this._bindings.get(name);
    }
    
    public void removeBinding(final Name name) {
        final String key = name.toString();
        if (NamingContext.__log.isDebugEnabled()) {
            NamingContext.__log.debug("Removing binding with key=" + key, new Object[0]);
        }
        final Binding binding = this._bindings.remove(key);
        if (binding != null) {
            final Collection<Listener> list = this.findListeners();
            for (final Listener listener : list) {
                listener.unbind(this, binding);
            }
        }
    }
    
    public Name toCanonicalName(final Name name) {
        Name canonicalName = name;
        if (name != null && canonicalName.size() > 1) {
            if (canonicalName.get(0).equals("")) {
                canonicalName = canonicalName.getSuffix(1);
            }
            if (canonicalName.get(canonicalName.size() - 1).equals("")) {
                canonicalName = canonicalName.getPrefix(canonicalName.size() - 1);
            }
        }
        return canonicalName;
    }
    
    public boolean isLocked() {
        if (this._env.get("org.eclipse.jndi.lock") == null && this._env.get("org.eclipse.jndi.unlock") == null) {
            return false;
        }
        final Object lockKey = this._env.get("org.eclipse.jndi.lock");
        final Object unlockKey = this._env.get("org.eclipse.jndi.unlock");
        return lockKey == null || unlockKey == null || !lockKey.equals(unlockKey);
    }
    
    public String dump() {
        final StringBuilder buf = new StringBuilder();
        try {
            this.dump(buf, "");
        }
        catch (Exception e) {
            NamingContext.__log.warn(e);
        }
        return buf.toString();
    }
    
    public void dump(final Appendable out, final String indent) throws IOException {
        out.append(this.getClass().getSimpleName()).append("@").append(Long.toHexString(this.hashCode())).append("\n");
        final int size = this._bindings.size();
        int i = 0;
        for (final Map.Entry<String, Binding> entry : this._bindings.entrySet()) {
            final boolean last = ++i == size;
            out.append(indent).append(" +- ").append(entry.getKey()).append(": ");
            final Binding binding = entry.getValue();
            final Object value = binding.getObject();
            if ("comp".equals(entry.getKey()) && value instanceof Reference && "org.eclipse.jetty.jndi.ContextFactory".equals(((Reference)value).getFactoryClassName())) {
                ContextFactory.dump(out, indent + (last ? "    " : " |  "));
            }
            else if (value instanceof Dumpable) {
                ((Dumpable)value).dump(out, indent + (last ? "    " : " |  "));
            }
            else {
                out.append(value.getClass().getSimpleName()).append("=");
                out.append(String.valueOf(value).replace('\n', '|').replace('\r', '|'));
                out.append("\n");
            }
        }
    }
    
    private Collection<Listener> findListeners() {
        final Collection<Listener> list = new ArrayList<Listener>();
        for (NamingContext ctx = this; ctx != null; ctx = (NamingContext)ctx.getParent()) {
            if (ctx._listeners != null) {
                list.addAll(ctx._listeners);
            }
        }
        return list;
    }
    
    public void addListener(final Listener listener) {
        if (this._listeners == null) {
            this._listeners = new ArrayList<Listener>();
        }
        this._listeners.add(listener);
    }
    
    public boolean removeListener(final Listener listener) {
        return this._listeners.remove(listener);
    }
    
    static {
        __log = NamingUtil.__log;
        __empty = Collections.emptyList();
    }
    
    public interface Listener
    {
        Binding bind(final NamingContext p0, final Binding p1);
        
        void unbind(final NamingContext p0, final Binding p1);
    }
}
