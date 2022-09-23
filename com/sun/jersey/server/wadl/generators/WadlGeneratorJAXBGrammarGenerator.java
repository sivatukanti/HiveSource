// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl.generators;

import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Param;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.JAXBIntrospector;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import java.util.logging.Level;
import java.io.Writer;
import java.io.CharArrayWriter;
import javax.xml.transform.Result;
import java.util.List;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.stream.StreamResult;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import java.lang.reflect.ParameterizedType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.HashSet;
import com.sun.jersey.server.wadl.ApplicationDescription;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.jersey.server.wadl.WadlGeneratorImpl;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

public class WadlGeneratorJAXBGrammarGenerator extends AbstractWadlGeneratorGrammarGenerator<QName>
{
    private static final Logger LOGGER;
    
    public WadlGeneratorJAXBGrammarGenerator() {
        super(new WadlGeneratorImpl(), QName.class);
    }
    
    @Override
    public boolean acceptMediaType(final MediaType type) {
        return type.equals(MediaType.APPLICATION_XML_TYPE) || type.equals(MediaType.TEXT_XML_TYPE) || type.getSubtype().endsWith("+xml") || (type.equals(MediaType.APPLICATION_JSON_TYPE) || type.getSubtype().endsWith("+json")) || type.equals(MediaType.WILDCARD_TYPE);
    }
    
    @Override
    protected WadlGenerator.Resolver buildModelAndSchemas(final Map<String, ApplicationDescription.ExternalGrammar> extraFiles) {
        final Set<Class> classSet = new HashSet<Class>(this._seeAlso);
        for (final Pair pair : this._hasTypeWantsName) {
            final HasType hasType = pair.hasType;
            final Class clazz = hasType.getPrimaryClass();
            if (clazz.getAnnotation(XmlRootElement.class) != null) {
                classSet.add(clazz);
            }
            else {
                if (!WadlGeneratorJAXBGrammarGenerator.SPECIAL_GENERIC_TYPES.contains(clazz)) {
                    continue;
                }
                final Type type = hasType.getType();
                if (!(type instanceof ParameterizedType)) {
                    continue;
                }
                final Type parameterType = ((ParameterizedType)type).getActualTypeArguments()[0];
                if (!(parameterType instanceof Class)) {
                    continue;
                }
                classSet.add((Class)parameterType);
            }
        }
        JAXBIntrospector introspector = null;
        try {
            final JAXBContext context = JAXBContext.newInstance((Class[])classSet.toArray(new Class[classSet.size()]));
            final List<StreamResult> results = new ArrayList<StreamResult>();
            context.generateSchema(new SchemaOutputResolver() {
                int counter = 0;
                
                @Override
                public Result createOutput(final String namespaceUri, final String suggestedFileName) {
                    final StreamResult result = new StreamResult(new CharArrayWriter());
                    result.setSystemId("xsd" + this.counter++ + ".xsd");
                    results.add(result);
                    return result;
                }
            });
            for (final StreamResult result : results) {
                final CharArrayWriter writer = (CharArrayWriter)result.getWriter();
                final byte[] contents = writer.toString().getBytes("UTF8");
                extraFiles.put(result.getSystemId(), new ApplicationDescription.ExternalGrammar(MediaType.APPLICATION_XML_TYPE, contents, true));
            }
            introspector = context.createJAXBIntrospector();
        }
        catch (JAXBException e) {
            WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.SEVERE, "Failed to generate the schema for the JAX-B elements", e);
        }
        catch (IOException e2) {
            WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.SEVERE, "Failed to generate the schema for the JAX-B elements due to an IO error", e2);
        }
        if (introspector != null) {
            final JAXBIntrospector copy = introspector;
            return new WadlGenerator.Resolver() {
                @Override
                public <T> T resolve(final Class type, final MediaType mt, final Class<T> resolvedType) {
                    if (!QName.class.equals(resolvedType)) {
                        return null;
                    }
                    if (!WadlGeneratorJAXBGrammarGenerator.this.acceptMediaType(mt)) {
                        return null;
                    }
                    Object parameterClassInstance = null;
                    try {
                        final Constructor<?> defaultConstructor = type.getDeclaredConstructor((Class<?>[])new Class[0]);
                        defaultConstructor.setAccessible(true);
                        parameterClassInstance = defaultConstructor.newInstance(new Object[0]);
                    }
                    catch (InstantiationException ex) {
                        WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.FINE, null, ex);
                    }
                    catch (IllegalAccessException ex2) {
                        WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.FINE, null, ex2);
                    }
                    catch (IllegalArgumentException ex3) {
                        WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.FINE, null, ex3);
                    }
                    catch (InvocationTargetException ex4) {
                        WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.FINE, null, ex4);
                    }
                    catch (SecurityException ex5) {
                        WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.FINE, null, ex5);
                    }
                    catch (NoSuchMethodException ex6) {
                        WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.FINE, null, ex6);
                    }
                    if (parameterClassInstance == null) {
                        return null;
                    }
                    try {
                        return resolvedType.cast(copy.getElementName(parameterClassInstance));
                    }
                    catch (NullPointerException e) {
                        return null;
                    }
                }
            };
        }
        return null;
    }
    
    @Override
    protected WantsName<QName> createParmWantsName(final Param param) {
        return new WantsName<QName>() {
            @Override
            public boolean isElement() {
                return false;
            }
            
            @Override
            public void setName(final QName name) {
                param.setType(name);
            }
        };
    }
    
    @Override
    protected WantsName<QName> createRepresentationWantsName(final Representation rt) {
        return new WantsName<QName>() {
            @Override
            public boolean isElement() {
                return true;
            }
            
            @Override
            public void setName(final QName name) {
                rt.setElement(name);
            }
        };
    }
    
    static {
        LOGGER = Logger.getLogger(WadlGeneratorJAXBGrammarGenerator.class.getName());
    }
}
