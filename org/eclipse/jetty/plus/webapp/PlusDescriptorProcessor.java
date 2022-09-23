// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.webapp;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.plus.jndi.Link;
import org.eclipse.jetty.jndi.NamingUtil;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.plus.jndi.NamingEntryUtil;
import org.eclipse.jetty.plus.jndi.NamingEntry;
import javax.naming.InitialContext;
import org.eclipse.jetty.plus.annotation.Injection;
import org.eclipse.jetty.plus.annotation.PreDestroyCallback;
import org.eclipse.jetty.plus.annotation.LifeCycleCallback;
import org.eclipse.jetty.plus.annotation.PostConstructCallback;
import java.util.Iterator;
import org.eclipse.jetty.webapp.Origin;
import org.eclipse.jetty.webapp.FragmentDescriptor;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.xml.XmlParser;
import org.eclipse.jetty.plus.annotation.RunAsCollection;
import org.eclipse.jetty.plus.annotation.LifeCycleCallbackCollection;
import org.eclipse.jetty.plus.annotation.InjectionCollection;
import org.eclipse.jetty.webapp.Descriptor;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.webapp.IterativeDescriptorProcessor;

public class PlusDescriptorProcessor extends IterativeDescriptorProcessor
{
    private static final Logger LOG;
    
    public PlusDescriptorProcessor() {
        try {
            this.registerVisitor("env-entry", this.getClass().getDeclaredMethod("visitEnvEntry", PlusDescriptorProcessor.__signature));
            this.registerVisitor("resource-ref", this.getClass().getDeclaredMethod("visitResourceRef", PlusDescriptorProcessor.__signature));
            this.registerVisitor("resource-env-ref", this.getClass().getDeclaredMethod("visitResourceEnvRef", PlusDescriptorProcessor.__signature));
            this.registerVisitor("message-destination-ref", this.getClass().getDeclaredMethod("visitMessageDestinationRef", PlusDescriptorProcessor.__signature));
            this.registerVisitor("post-construct", this.getClass().getDeclaredMethod("visitPostConstruct", PlusDescriptorProcessor.__signature));
            this.registerVisitor("pre-destroy", this.getClass().getDeclaredMethod("visitPreDestroy", PlusDescriptorProcessor.__signature));
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public void start(final WebAppContext context, final Descriptor descriptor) {
        InjectionCollection injections = (InjectionCollection)context.getAttribute("org.eclipse.jetty.injectionCollection");
        if (injections == null) {
            injections = new InjectionCollection();
            context.setAttribute("org.eclipse.jetty.injectionCollection", injections);
        }
        LifeCycleCallbackCollection callbacks = (LifeCycleCallbackCollection)context.getAttribute("org.eclipse.jetty.lifecyleCallbackCollection");
        if (callbacks == null) {
            callbacks = new LifeCycleCallbackCollection();
            context.setAttribute("org.eclipse.jetty.lifecyleCallbackCollection", callbacks);
        }
        RunAsCollection runAsCollection = (RunAsCollection)context.getAttribute("org.eclipse.jetty.runAsCollection");
        if (runAsCollection == null) {
            runAsCollection = new RunAsCollection();
            context.setAttribute("org.eclipse.jetty.runAsCollection", runAsCollection);
        }
    }
    
    @Override
    public void end(final WebAppContext context, final Descriptor descriptor) {
    }
    
    public void visitEnvEntry(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) throws Exception {
        final String name = node.getString("env-entry-name", false, true);
        final String type = node.getString("env-entry-type", false, true);
        final String valueStr = node.getString("env-entry-value", false, true);
        if (valueStr == null || valueStr.equals("")) {
            PlusDescriptorProcessor.LOG.warn("No value for env-entry-name " + name, new Object[0]);
            return;
        }
        final Origin o = context.getMetaData().getOrigin("env-entry." + name);
        switch (o) {
            case NotSet: {
                context.getMetaData().setOrigin("env-entry." + name, descriptor);
                this.addInjections(context, descriptor, node, name, TypeUtil.fromName(type));
                final Object value = TypeUtil.valueOf(type, valueStr);
                this.bindEnvEntry(name, value);
                break;
            }
            case WebXml:
            case WebDefaults:
            case WebOverride: {
                if (!(descriptor instanceof FragmentDescriptor)) {
                    context.getMetaData().setOrigin("env-entry." + name, descriptor);
                    this.addInjections(context, descriptor, node, name, TypeUtil.fromName(type));
                    final Object value = TypeUtil.valueOf(type, valueStr);
                    this.bindEnvEntry(name, value);
                    break;
                }
                final Descriptor d = context.getMetaData().getOriginDescriptor("env-entry." + name + ".injection");
                if (d == null || d instanceof FragmentDescriptor) {
                    this.addInjections(context, descriptor, node, name, TypeUtil.fromName(type));
                }
                break;
            }
            case WebFragment: {
                throw new IllegalStateException("Conflicting env-entry " + name + " in " + descriptor.getResource());
            }
        }
    }
    
    public void visitResourceRef(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) throws Exception {
        final String jndiName = node.getString("res-ref-name", false, true);
        String type = node.getString("res-type", false, true);
        String auth = node.getString("res-auth", false, true);
        String shared = node.getString("res-sharing-scope", false, true);
        final Origin o = context.getMetaData().getOrigin("resource-ref." + jndiName);
        switch (o) {
            case NotSet: {
                context.getMetaData().setOrigin("resource-ref." + jndiName, descriptor);
                Class<?> typeClass = TypeUtil.fromName(type);
                if (typeClass == null) {
                    typeClass = context.loadClass(type);
                }
                this.addInjections(context, descriptor, node, jndiName, typeClass);
                this.bindResourceRef(context, jndiName, typeClass);
                break;
            }
            case WebXml:
            case WebDefaults:
            case WebOverride: {
                if (!(descriptor instanceof FragmentDescriptor)) {
                    context.getMetaData().setOrigin("resource-ref." + jndiName, descriptor);
                    Class<?> typeClass = TypeUtil.fromName(type);
                    if (typeClass == null) {
                        typeClass = context.loadClass(type);
                    }
                    this.addInjections(context, descriptor, node, jndiName, typeClass);
                    this.bindResourceRef(context, jndiName, typeClass);
                    break;
                }
                final Descriptor d = context.getMetaData().getOriginDescriptor("resource-ref." + jndiName + ".injection");
                if (d == null || d instanceof FragmentDescriptor) {
                    Class<?> typeClass2 = TypeUtil.fromName(type);
                    if (typeClass2 == null) {
                        typeClass2 = context.loadClass(type);
                    }
                    this.addInjections(context, descriptor, node, jndiName, TypeUtil.fromName(type));
                }
                break;
            }
            case WebFragment: {
                final Descriptor otherFragment = context.getMetaData().getOriginDescriptor("resource-ref." + jndiName);
                final XmlParser.Node otherFragmentRoot = otherFragment.getRoot();
                final Iterator<Object> iter = otherFragmentRoot.iterator();
                XmlParser.Node otherNode = null;
                while (iter.hasNext() && otherNode == null) {
                    final Object obj = iter.next();
                    if (!(obj instanceof XmlParser.Node)) {
                        continue;
                    }
                    final XmlParser.Node n = (XmlParser.Node)obj;
                    if (!"resource-ref".equals(n.getTag()) || !jndiName.equals(n.getString("res-ref-name", false, true))) {
                        continue;
                    }
                    otherNode = n;
                }
                if (otherNode == null) {
                    throw new IllegalStateException("resource-ref." + jndiName + " not found in declaring descriptor " + otherFragment);
                }
                String otherType = otherNode.getString("res-type", false, true);
                String otherAuth = otherNode.getString("res-auth", false, true);
                String otherShared = otherNode.getString("res-sharing-scope", false, true);
                type = ((type == null) ? "" : type);
                otherType = ((otherType == null) ? "" : otherType);
                auth = ((auth == null) ? "" : auth);
                otherAuth = ((otherAuth == null) ? "" : otherAuth);
                shared = ((shared == null) ? "" : shared);
                otherShared = ((otherShared == null) ? "" : otherShared);
                if (!type.equals(otherType) || !auth.equals(otherAuth) || !shared.equals(otherShared)) {
                    throw new IllegalStateException("Conflicting resource-ref " + jndiName + " in " + descriptor.getResource());
                }
                this.addInjections(context, descriptor, node, jndiName, TypeUtil.fromName(type));
                break;
            }
        }
    }
    
    public void visitResourceEnvRef(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) throws Exception {
        final String jndiName = node.getString("resource-env-ref-name", false, true);
        String type = node.getString("resource-env-ref-type", false, true);
        final Origin o = context.getMetaData().getOrigin("resource-env-ref." + jndiName);
        switch (o) {
            case NotSet: {
                Class<?> typeClass = TypeUtil.fromName(type);
                if (typeClass == null) {
                    typeClass = context.loadClass(type);
                }
                this.addInjections(context, descriptor, node, jndiName, typeClass);
                this.bindResourceEnvRef(context, jndiName, typeClass);
                break;
            }
            case WebXml:
            case WebDefaults:
            case WebOverride: {
                if (!(descriptor instanceof FragmentDescriptor)) {
                    context.getMetaData().setOrigin("resource-env-ref." + jndiName, descriptor);
                    Class<?> typeClass = TypeUtil.fromName(type);
                    if (typeClass == null) {
                        typeClass = context.loadClass(type);
                    }
                    this.addInjections(context, descriptor, node, jndiName, typeClass);
                    this.bindResourceEnvRef(context, jndiName, typeClass);
                    break;
                }
                final Descriptor d = context.getMetaData().getOriginDescriptor("resource-env-ref." + jndiName + ".injection");
                if (d == null || d instanceof FragmentDescriptor) {
                    Class<?> typeClass2 = TypeUtil.fromName(type);
                    if (typeClass2 == null) {
                        typeClass2 = context.loadClass(type);
                    }
                    this.addInjections(context, descriptor, node, jndiName, typeClass2);
                }
                break;
            }
            case WebFragment: {
                final Descriptor otherFragment = context.getMetaData().getOriginDescriptor("resource-env-ref." + jndiName);
                final XmlParser.Node otherFragmentRoot = otherFragment.getRoot();
                final Iterator<Object> iter = otherFragmentRoot.iterator();
                XmlParser.Node otherNode = null;
                while (iter.hasNext() && otherNode == null) {
                    final Object obj = iter.next();
                    if (!(obj instanceof XmlParser.Node)) {
                        continue;
                    }
                    final XmlParser.Node n = (XmlParser.Node)obj;
                    if (!"resource-env-ref".equals(n.getTag()) || !jndiName.equals(n.getString("resource-env-ref-name", false, true))) {
                        continue;
                    }
                    otherNode = n;
                }
                if (otherNode == null) {
                    throw new IllegalStateException("resource-env-ref." + jndiName + " not found in declaring descriptor " + otherFragment);
                }
                String otherType = otherNode.getString("resource-env-ref-type", false, true);
                type = ((type == null) ? "" : type);
                otherType = ((otherType == null) ? "" : otherType);
                if (!type.equals(otherType)) {
                    throw new IllegalStateException("Conflicting resource-env-ref " + jndiName + " in " + descriptor.getResource());
                }
                this.addInjections(context, descriptor, node, jndiName, TypeUtil.fromName(type));
                break;
            }
        }
    }
    
    public void visitMessageDestinationRef(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) throws Exception {
        final String jndiName = node.getString("message-destination-ref-name", false, true);
        String type = node.getString("message-destination-type", false, true);
        String usage = node.getString("message-destination-usage", false, true);
        final Origin o = context.getMetaData().getOrigin("message-destination-ref." + jndiName);
        switch (o) {
            case NotSet: {
                Class<?> typeClass = TypeUtil.fromName(type);
                if (typeClass == null) {
                    typeClass = context.loadClass(type);
                }
                this.addInjections(context, descriptor, node, jndiName, typeClass);
                this.bindMessageDestinationRef(context, jndiName, typeClass);
                context.getMetaData().setOrigin("message-destination-ref." + jndiName, descriptor);
                break;
            }
            case WebXml:
            case WebDefaults:
            case WebOverride: {
                if (!(descriptor instanceof FragmentDescriptor)) {
                    Class<?> typeClass = TypeUtil.fromName(type);
                    if (typeClass == null) {
                        typeClass = context.loadClass(type);
                    }
                    this.addInjections(context, descriptor, node, jndiName, typeClass);
                    this.bindMessageDestinationRef(context, jndiName, typeClass);
                    context.getMetaData().setOrigin("message-destination-ref." + jndiName, descriptor);
                    break;
                }
                final Descriptor d = context.getMetaData().getOriginDescriptor("message-destination-ref." + jndiName + ".injection");
                if (d == null || d instanceof FragmentDescriptor) {
                    Class<?> typeClass2 = TypeUtil.fromName(type);
                    if (typeClass2 == null) {
                        typeClass2 = context.loadClass(type);
                    }
                    this.addInjections(context, descriptor, node, jndiName, typeClass2);
                }
                break;
            }
            case WebFragment: {
                final Descriptor otherFragment = context.getMetaData().getOriginDescriptor("message-destination-ref." + jndiName);
                final XmlParser.Node otherFragmentRoot = otherFragment.getRoot();
                final Iterator<Object> iter = otherFragmentRoot.iterator();
                XmlParser.Node otherNode = null;
                while (iter.hasNext() && otherNode == null) {
                    final Object obj = iter.next();
                    if (!(obj instanceof XmlParser.Node)) {
                        continue;
                    }
                    final XmlParser.Node n = (XmlParser.Node)obj;
                    if (!"message-destination-ref".equals(n.getTag()) || !jndiName.equals(n.getString("message-destination-ref-name", false, true))) {
                        continue;
                    }
                    otherNode = n;
                }
                if (otherNode == null) {
                    throw new IllegalStateException("message-destination-ref." + jndiName + " not found in declaring descriptor " + otherFragment);
                }
                final String otherType = node.getString("message-destination-type", false, true);
                final String otherUsage = node.getString("message-destination-usage", false, true);
                type = ((type == null) ? "" : type);
                usage = ((usage == null) ? "" : usage);
                if (!type.equals(otherType) || !usage.equalsIgnoreCase(otherUsage)) {
                    throw new IllegalStateException("Conflicting message-destination-ref " + jndiName + " in " + descriptor.getResource());
                }
                this.addInjections(context, descriptor, node, jndiName, TypeUtil.fromName(type));
                break;
            }
        }
    }
    
    public void visitPostConstruct(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        final String className = node.getString("lifecycle-callback-class", false, true);
        final String methodName = node.getString("lifecycle-callback-method", false, true);
        if (className == null || className.equals("")) {
            PlusDescriptorProcessor.LOG.warn("No lifecycle-callback-class specified", new Object[0]);
            return;
        }
        if (methodName == null || methodName.equals("")) {
            PlusDescriptorProcessor.LOG.warn("No lifecycle-callback-method specified for class " + className, new Object[0]);
            return;
        }
        final Origin o = context.getMetaData().getOrigin("post-construct");
        switch (o) {
            case NotSet: {
                context.getMetaData().setOrigin("post-construct", descriptor);
                try {
                    final Class<?> clazz = context.loadClass(className);
                    final LifeCycleCallback callback = new PostConstructCallback();
                    callback.setTarget(clazz, methodName);
                    ((LifeCycleCallbackCollection)context.getAttribute("org.eclipse.jetty.lifecyleCallbackCollection")).add(callback);
                }
                catch (ClassNotFoundException e) {
                    PlusDescriptorProcessor.LOG.warn("Couldn't load post-construct target class " + className, new Object[0]);
                }
                break;
            }
            case WebXml:
            case WebDefaults:
            case WebOverride: {
                if (!(descriptor instanceof FragmentDescriptor)) {
                    try {
                        final Class<?> clazz = context.loadClass(className);
                        final LifeCycleCallback callback = new PostConstructCallback();
                        callback.setTarget(clazz, methodName);
                        ((LifeCycleCallbackCollection)context.getAttribute("org.eclipse.jetty.lifecyleCallbackCollection")).add(callback);
                    }
                    catch (ClassNotFoundException e) {
                        PlusDescriptorProcessor.LOG.warn("Couldn't load post-construct target class " + className, new Object[0]);
                    }
                    break;
                }
                break;
            }
            case WebFragment: {
                try {
                    final Class<?> clazz = context.loadClass(className);
                    final LifeCycleCallback callback = new PostConstructCallback();
                    callback.setTarget(clazz, methodName);
                    ((LifeCycleCallbackCollection)context.getAttribute("org.eclipse.jetty.lifecyleCallbackCollection")).add(callback);
                }
                catch (ClassNotFoundException e) {
                    PlusDescriptorProcessor.LOG.warn("Couldn't load post-construct target class " + className, new Object[0]);
                }
                break;
            }
        }
    }
    
    public void visitPreDestroy(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) {
        final String className = node.getString("lifecycle-callback-class", false, true);
        final String methodName = node.getString("lifecycle-callback-method", false, true);
        if (className == null || className.equals("")) {
            PlusDescriptorProcessor.LOG.warn("No lifecycle-callback-class specified for pre-destroy", new Object[0]);
            return;
        }
        if (methodName == null || methodName.equals("")) {
            PlusDescriptorProcessor.LOG.warn("No lifecycle-callback-method specified for pre-destroy class " + className, new Object[0]);
            return;
        }
        final Origin o = context.getMetaData().getOrigin("pre-destroy");
        switch (o) {
            case NotSet: {
                context.getMetaData().setOrigin("pre-destroy", descriptor);
                try {
                    final Class<?> clazz = context.loadClass(className);
                    final LifeCycleCallback callback = new PreDestroyCallback();
                    callback.setTarget(clazz, methodName);
                    ((LifeCycleCallbackCollection)context.getAttribute("org.eclipse.jetty.lifecyleCallbackCollection")).add(callback);
                }
                catch (ClassNotFoundException e) {
                    PlusDescriptorProcessor.LOG.warn("Couldn't load pre-destory target class " + className, new Object[0]);
                }
                break;
            }
            case WebXml:
            case WebDefaults:
            case WebOverride: {
                if (!(descriptor instanceof FragmentDescriptor)) {
                    try {
                        final Class<?> clazz = context.loadClass(className);
                        final LifeCycleCallback callback = new PreDestroyCallback();
                        callback.setTarget(clazz, methodName);
                        ((LifeCycleCallbackCollection)context.getAttribute("org.eclipse.jetty.lifecyleCallbackCollection")).add(callback);
                    }
                    catch (ClassNotFoundException e) {
                        PlusDescriptorProcessor.LOG.warn("Couldn't load pre-destory target class " + className, new Object[0]);
                    }
                    break;
                }
                break;
            }
            case WebFragment: {
                try {
                    final Class<?> clazz = context.loadClass(className);
                    final LifeCycleCallback callback = new PreDestroyCallback();
                    callback.setTarget(clazz, methodName);
                    ((LifeCycleCallbackCollection)context.getAttribute("org.eclipse.jetty.lifecyleCallbackCollection")).add(callback);
                }
                catch (ClassNotFoundException e) {
                    PlusDescriptorProcessor.LOG.warn("Couldn't load pre-destory target class " + className, new Object[0]);
                }
                break;
            }
        }
    }
    
    public void addInjections(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node, final String jndiName, final Class<?> valueClass) {
        final Iterator<XmlParser.Node> itor = node.iterator("injection-target");
        while (itor.hasNext()) {
            final XmlParser.Node injectionNode = itor.next();
            final String targetClassName = injectionNode.getString("injection-target-class", false, true);
            final String targetName = injectionNode.getString("injection-target-name", false, true);
            if (targetClassName == null || targetClassName.equals("")) {
                PlusDescriptorProcessor.LOG.warn("No classname found in injection-target", new Object[0]);
            }
            else if (targetName == null || targetName.equals("")) {
                PlusDescriptorProcessor.LOG.warn("No field or method name in injection-target", new Object[0]);
            }
            else {
                InjectionCollection injections = (InjectionCollection)context.getAttribute("org.eclipse.jetty.injectionCollection");
                if (injections == null) {
                    injections = new InjectionCollection();
                    context.setAttribute("org.eclipse.jetty.injectionCollection", injections);
                }
                try {
                    final Class<?> clazz = context.loadClass(targetClassName);
                    final Injection injection = new Injection();
                    injection.setJndiName(jndiName);
                    injection.setTarget(clazz, targetName, valueClass);
                    injections.add(injection);
                    if (context.getMetaData().getOriginDescriptor(node.getTag() + "." + jndiName + ".injection") != null) {
                        continue;
                    }
                    context.getMetaData().setOrigin(node.getTag() + "." + jndiName + ".injection", descriptor);
                }
                catch (ClassNotFoundException e) {
                    PlusDescriptorProcessor.LOG.warn("Couldn't load injection target class " + targetClassName, new Object[0]);
                }
            }
        }
    }
    
    public void bindEnvEntry(final String name, final Object value) throws Exception {
        InitialContext ic = null;
        boolean bound = false;
        ic = new InitialContext();
        try {
            final NamingEntry ne = (NamingEntry)ic.lookup("java:comp/env/" + NamingEntryUtil.makeNamingEntryName(ic.getNameParser(""), name));
            if (ne != null && ne instanceof EnvEntry) {
                final EnvEntry ee = (EnvEntry)ne;
                bound = ee.isOverrideWebXml();
            }
        }
        catch (NameNotFoundException e) {
            bound = false;
        }
        if (!bound) {
            final Context envCtx = (Context)ic.lookup("java:comp/env");
            NamingUtil.bind(envCtx, name, value);
        }
    }
    
    public void bindResourceRef(final WebAppContext context, final String name, final Class<?> typeClass) throws Exception {
        this.bindEntry(context, name, typeClass);
    }
    
    public void bindResourceEnvRef(final WebAppContext context, final String name, final Class<?> typeClass) throws Exception {
        this.bindEntry(context, name, typeClass);
    }
    
    public void bindMessageDestinationRef(final WebAppContext context, final String name, final Class<?> typeClass) throws Exception {
        this.bindEntry(context, name, typeClass);
    }
    
    protected void bindEntry(final WebAppContext context, final String name, final Class<?> typeClass) throws Exception {
        String nameInEnvironment = name;
        boolean bound = false;
        Object scope = context;
        final NamingEntry ne = NamingEntryUtil.lookupNamingEntry(scope, name);
        if (ne != null && ne instanceof Link) {
            nameInEnvironment = ((Link)ne).getLink();
        }
        scope = context;
        bound = NamingEntryUtil.bindToENC(scope, name, nameInEnvironment);
        if (bound) {
            return;
        }
        scope = context.getServer();
        bound = NamingEntryUtil.bindToENC(scope, name, nameInEnvironment);
        if (bound) {
            return;
        }
        bound = NamingEntryUtil.bindToENC(null, name, nameInEnvironment);
        if (bound) {
            return;
        }
        nameInEnvironment = typeClass.getName() + "/default";
        NamingEntry defaultNE = NamingEntryUtil.lookupNamingEntry(context.getServer(), nameInEnvironment);
        if (defaultNE == null) {
            defaultNE = NamingEntryUtil.lookupNamingEntry(null, nameInEnvironment);
        }
        if (defaultNE != null) {
            defaultNE.bindToENC(name);
            return;
        }
        throw new IllegalStateException("Nothing to bind for name " + nameInEnvironment);
    }
    
    static {
        LOG = Log.getLogger(PlusDescriptorProcessor.class);
    }
}
