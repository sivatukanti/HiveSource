// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata.annotations;

import java.lang.annotation.Annotation;
import org.datanucleus.ClassConstants;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.PackageMetaData;
import org.datanucleus.plugin.ConfigurationElement;
import org.datanucleus.plugin.PluginManager;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.util.Localiser;

public class AnnotationManagerImpl implements AnnotationManager
{
    protected static final Localiser LOCALISER;
    protected final MetaDataManager metadataMgr;
    Map<String, String> annotationReaderLookup;
    Map<String, AnnotationReader> annotationReaders;
    Set<String> classAnnotationHandlerAnnotations;
    Map<String, ClassAnnotationHandler> classAnnotationHandlers;
    Set<String> memberAnnotationHandlerAnnotations;
    Map<String, MemberAnnotationHandler> memberAnnotationHandlers;
    
    public AnnotationManagerImpl(final MetaDataManager metadataMgr) {
        this.annotationReaderLookup = new HashMap<String, String>();
        this.annotationReaders = new HashMap<String, AnnotationReader>();
        this.classAnnotationHandlerAnnotations = null;
        this.classAnnotationHandlers = null;
        this.memberAnnotationHandlerAnnotations = null;
        this.memberAnnotationHandlers = null;
        this.metadataMgr = metadataMgr;
        final PluginManager pluginMgr = metadataMgr.getNucleusContext().getPluginManager();
        ConfigurationElement[] elems = pluginMgr.getConfigurationElementsForExtension("org.datanucleus.annotations", null, (String)null);
        if (elems != null) {
            for (int i = 0; i < elems.length; ++i) {
                this.annotationReaderLookup.put(elems[i].getAttribute("annotation-class"), elems[i].getAttribute("reader"));
            }
        }
        elems = pluginMgr.getConfigurationElementsForExtension("org.datanucleus.class_annotation_handler", null, (String)null);
        if (elems != null && elems.length > 0) {
            this.classAnnotationHandlerAnnotations = new HashSet<String>(elems.length);
            this.classAnnotationHandlers = new HashMap<String, ClassAnnotationHandler>(elems.length);
            for (int i = 0; i < elems.length; ++i) {
                this.classAnnotationHandlerAnnotations.add(elems[i].getAttribute("annotation-class"));
            }
        }
        elems = pluginMgr.getConfigurationElementsForExtension("org.datanucleus.member_annotation_handler", null, (String)null);
        if (elems != null && elems.length > 0) {
            this.memberAnnotationHandlerAnnotations = new HashSet<String>(elems.length);
            this.memberAnnotationHandlers = new HashMap<String, MemberAnnotationHandler>(elems.length);
            for (int i = 0; i < elems.length; ++i) {
                this.memberAnnotationHandlerAnnotations.add(elems[i].getAttribute("annotation-class"));
            }
        }
    }
    
    @Override
    public AbstractClassMetaData getMetaDataForClass(final Class cls, final PackageMetaData pmd, final ClassLoaderResolver clr) {
        if (cls == null) {
            return null;
        }
        final Annotation[] annotations = cls.getAnnotations();
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        String readerClassName = null;
        for (int i = 0; i < annotations.length; ++i) {
            final String reader = this.annotationReaderLookup.get(annotations[i].annotationType().getName());
            if (reader != null) {
                readerClassName = reader;
                break;
            }
        }
        if (readerClassName == null) {
            NucleusLogger.METADATA.debug(AnnotationManagerImpl.LOCALISER.msg("044202", cls.getName()));
            return null;
        }
        AnnotationReader reader2 = this.annotationReaders.get(readerClassName);
        if (reader2 == null) {
            try {
                final Class[] ctrArgs = { ClassConstants.METADATA_MANAGER };
                final Object[] ctrParams = { this.metadataMgr };
                final PluginManager pluginMgr = this.metadataMgr.getNucleusContext().getPluginManager();
                reader2 = (AnnotationReader)pluginMgr.createExecutableExtension("org.datanucleus.annotations", "reader", readerClassName, "reader", ctrArgs, ctrParams);
                this.annotationReaders.put(readerClassName, reader2);
            }
            catch (Exception e) {
                NucleusLogger.METADATA.warn(AnnotationManagerImpl.LOCALISER.msg("MetaData.AnnotationReaderNotFound", readerClassName));
                return null;
            }
        }
        return reader2.getMetaDataForClass(cls, pmd, clr);
    }
    
    @Override
    public boolean getClassAnnotationHasHandler(final String annotationName) {
        return this.classAnnotationHandlerAnnotations != null && this.classAnnotationHandlerAnnotations.contains(annotationName);
    }
    
    @Override
    public boolean getMemberAnnotationHasHandler(final String annotationName) {
        return this.memberAnnotationHandlerAnnotations != null && this.memberAnnotationHandlerAnnotations.contains(annotationName);
    }
    
    @Override
    public ClassAnnotationHandler getHandlerForClassAnnotation(final String annotationName) {
        if (this.classAnnotationHandlerAnnotations == null || !this.classAnnotationHandlerAnnotations.contains(annotationName)) {
            return null;
        }
        ClassAnnotationHandler handler = this.classAnnotationHandlers.get(annotationName);
        if (handler == null) {
            try {
                final PluginManager pluginMgr = this.metadataMgr.getNucleusContext().getPluginManager();
                handler = (ClassAnnotationHandler)pluginMgr.createExecutableExtension("org.datanucleus.class_annotation_handler", "annotation-class", annotationName, "handler", null, null);
                this.classAnnotationHandlers.put(annotationName, handler);
            }
            catch (Exception e) {
                NucleusLogger.METADATA.warn(AnnotationManagerImpl.LOCALISER.msg("MetaData.ClassAnnotationHandlerNotFound", annotationName));
                return null;
            }
        }
        return handler;
    }
    
    @Override
    public MemberAnnotationHandler getHandlerForMemberAnnotation(final String annotationName) {
        if (this.memberAnnotationHandlerAnnotations == null || !this.memberAnnotationHandlerAnnotations.contains(annotationName)) {
            return null;
        }
        MemberAnnotationHandler handler = this.memberAnnotationHandlers.get(annotationName);
        if (handler == null) {
            try {
                final PluginManager pluginMgr = this.metadataMgr.getNucleusContext().getPluginManager();
                handler = (MemberAnnotationHandler)pluginMgr.createExecutableExtension("org.datanucleus.member_annotation_handler", "annotation-class", annotationName, "handler", null, null);
                this.memberAnnotationHandlers.put(annotationName, handler);
            }
            catch (Exception e) {
                NucleusLogger.METADATA.warn(AnnotationManagerImpl.LOCALISER.msg("MetaData.MemberAnnotationHandlerNotFound", annotationName));
                return null;
            }
        }
        return handler;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
