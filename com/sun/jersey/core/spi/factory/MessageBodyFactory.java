// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.factory;

import com.sun.jersey.core.reflection.ReflectionHelper;
import java.util.HashMap;
import java.util.Collection;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.sun.jersey.core.util.KeyComparatorLinkedHashMap;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import com.sun.jersey.core.header.MediaTypes;
import javax.ws.rs.Consumes;
import java.util.Set;
import com.sun.jersey.core.util.KeyComparatorHashMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;
import java.util.List;
import java.util.Map;
import com.sun.jersey.core.spi.component.ProviderServices;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.core.util.KeyComparator;
import com.sun.jersey.spi.MessageBodyWorkers;

public class MessageBodyFactory implements MessageBodyWorkers
{
    static final KeyComparator<MediaType> MEDIA_TYPE_COMPARATOR;
    private final ProviderServices providerServices;
    private final boolean deprecatedProviderPrecedence;
    private Map<MediaType, List<MessageBodyReader>> readerProviders;
    private Map<MediaType, List<MessageBodyWriter>> writerProviders;
    private List<MessageBodyWriterPair> writerListProviders;
    private Map<MediaType, List<MessageBodyReader>> customReaderProviders;
    private Map<MediaType, List<MessageBodyWriter>> customWriterProviders;
    private List<MessageBodyWriterPair> customWriterListProviders;
    
    public MessageBodyFactory(final ProviderServices providerServices, final boolean deprecatedProviderPrecedence) {
        this.providerServices = providerServices;
        this.deprecatedProviderPrecedence = deprecatedProviderPrecedence;
    }
    
    public void init() {
        this.initReaders();
        this.initWriters();
    }
    
    private void initReaders() {
        this.customReaderProviders = new KeyComparatorHashMap<MediaType, List<MessageBodyReader>>(MessageBodyFactory.MEDIA_TYPE_COMPARATOR);
        this.readerProviders = new KeyComparatorHashMap<MediaType, List<MessageBodyReader>>(MessageBodyFactory.MEDIA_TYPE_COMPARATOR);
        if (this.deprecatedProviderPrecedence) {
            this.initReaders(this.readerProviders, (Set<MessageBodyReader>)this.providerServices.getProvidersAndServices(MessageBodyReader.class));
        }
        else {
            this.initReaders(this.customReaderProviders, (Set<MessageBodyReader>)this.providerServices.getProviders(MessageBodyReader.class));
            this.initReaders(this.readerProviders, (Set<MessageBodyReader>)this.providerServices.getServices(MessageBodyReader.class));
        }
    }
    
    private void initReaders(final Map<MediaType, List<MessageBodyReader>> providersMap, final Set<MessageBodyReader> providersSet) {
        for (final MessageBodyReader provider : providersSet) {
            final List<MediaType> values = MediaTypes.createMediaTypes(provider.getClass().getAnnotation(Consumes.class));
            for (final MediaType type : values) {
                this.getClassCapability(providersMap, provider, type);
            }
        }
        final DistanceComparator<MessageBodyReader> dc = new DistanceComparator<MessageBodyReader>(MessageBodyReader.class);
        for (final Map.Entry<MediaType, List<MessageBodyReader>> e : providersMap.entrySet()) {
            Collections.sort(e.getValue(), dc);
        }
    }
    
    private void initWriters() {
        this.customWriterProviders = new KeyComparatorHashMap<MediaType, List<MessageBodyWriter>>(MessageBodyFactory.MEDIA_TYPE_COMPARATOR);
        this.customWriterListProviders = new ArrayList<MessageBodyWriterPair>();
        this.writerProviders = new KeyComparatorHashMap<MediaType, List<MessageBodyWriter>>(MessageBodyFactory.MEDIA_TYPE_COMPARATOR);
        this.writerListProviders = new ArrayList<MessageBodyWriterPair>();
        if (this.deprecatedProviderPrecedence) {
            this.initWriters(this.writerProviders, this.writerListProviders, (Set<MessageBodyWriter>)this.providerServices.getProvidersAndServices(MessageBodyWriter.class));
        }
        else {
            this.initWriters(this.customWriterProviders, this.customWriterListProviders, (Set<MessageBodyWriter>)this.providerServices.getProviders(MessageBodyWriter.class));
            this.initWriters(this.writerProviders, this.writerListProviders, (Set<MessageBodyWriter>)this.providerServices.getServices(MessageBodyWriter.class));
        }
    }
    
    private void initWriters(final Map<MediaType, List<MessageBodyWriter>> providersMap, final List<MessageBodyWriterPair> listProviders, final Set<MessageBodyWriter> providersSet) {
        for (final MessageBodyWriter provider : providersSet) {
            final List<MediaType> values = MediaTypes.createMediaTypes(provider.getClass().getAnnotation(Produces.class));
            for (final MediaType type : values) {
                this.getClassCapability(providersMap, provider, type);
            }
            listProviders.add(new MessageBodyWriterPair(provider, values));
        }
        final DistanceComparator<MessageBodyWriter> dc = new DistanceComparator<MessageBodyWriter>(MessageBodyWriter.class);
        for (final Map.Entry<MediaType, List<MessageBodyWriter>> e : providersMap.entrySet()) {
            Collections.sort(e.getValue(), dc);
        }
        Collections.sort(listProviders, new Comparator<MessageBodyWriterPair>() {
            @Override
            public int compare(final MessageBodyWriterPair p1, final MessageBodyWriterPair p2) {
                return dc.compare(p1.mbw, p2.mbw);
            }
        });
    }
    
    private <T> void getClassCapability(final Map<MediaType, List<T>> capabilities, final T provider, final MediaType mediaType) {
        if (!capabilities.containsKey(mediaType)) {
            capabilities.put(mediaType, new ArrayList<T>());
        }
        final List<T> providers = capabilities.get(mediaType);
        providers.add(provider);
    }
    
    @Override
    public Map<MediaType, List<MessageBodyReader>> getReaders(final MediaType mediaType) {
        final Map<MediaType, List<MessageBodyReader>> subSet = new KeyComparatorLinkedHashMap<MediaType, List<MessageBodyReader>>(MessageBodyFactory.MEDIA_TYPE_COMPARATOR);
        if (!this.customReaderProviders.isEmpty()) {
            this.getCompatibleReadersWritersMap(mediaType, this.customReaderProviders, subSet);
        }
        this.getCompatibleReadersWritersMap(mediaType, this.readerProviders, subSet);
        return subSet;
    }
    
    @Override
    public Map<MediaType, List<MessageBodyWriter>> getWriters(final MediaType mediaType) {
        final Map<MediaType, List<MessageBodyWriter>> subSet = new KeyComparatorLinkedHashMap<MediaType, List<MessageBodyWriter>>(MessageBodyFactory.MEDIA_TYPE_COMPARATOR);
        if (!this.customWriterProviders.isEmpty()) {
            this.getCompatibleReadersWritersMap(mediaType, this.customWriterProviders, subSet);
        }
        this.getCompatibleReadersWritersMap(mediaType, this.writerProviders, subSet);
        return subSet;
    }
    
    @Override
    public String readersToString(final Map<MediaType, List<MessageBodyReader>> readers) {
        return this.toString(readers);
    }
    
    @Override
    public String writersToString(final Map<MediaType, List<MessageBodyWriter>> writers) {
        return this.toString(writers);
    }
    
    private <T> String toString(final Map<MediaType, List<T>> set) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        for (final Map.Entry<MediaType, List<T>> e : set.entrySet()) {
            pw.append(e.getKey().toString()).println(" ->");
            for (final T t : e.getValue()) {
                pw.append("  ").println(t.getClass().getName());
            }
        }
        pw.flush();
        return sw.toString();
    }
    
    @Override
    public <T> MessageBodyReader<T> getMessageBodyReader(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType) {
        if (!this.customReaderProviders.isEmpty()) {
            final MessageBodyReader reader = this._getMessageBodyReader(c, t, as, mediaType, this.customReaderProviders);
            if (reader != null) {
                return (MessageBodyReader<T>)reader;
            }
        }
        final MessageBodyReader reader = this._getMessageBodyReader(c, t, as, mediaType, this.readerProviders);
        return (MessageBodyReader<T>)reader;
    }
    
    private <T> MessageBodyReader<T> _getMessageBodyReader(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType, final Map<MediaType, List<MessageBodyReader>> providers) {
        MessageBodyReader p = null;
        if (mediaType != null) {
            p = this._getMessageBodyReader(c, t, as, mediaType, mediaType, providers);
            if (p == null) {
                p = this._getMessageBodyReader(c, t, as, mediaType, MediaTypes.getTypeWildCart(mediaType), providers);
            }
        }
        if (p == null) {
            p = this._getMessageBodyReader(c, t, as, mediaType, MediaTypes.GENERAL_MEDIA_TYPE, providers);
        }
        return (MessageBodyReader<T>)p;
    }
    
    private <T> MessageBodyReader<T> _getMessageBodyReader(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType, final MediaType lookup) {
        if (!this.customReaderProviders.isEmpty()) {
            final MessageBodyReader reader = this._getMessageBodyReader(c, t, as, mediaType, lookup, this.customReaderProviders);
            if (reader != null) {
                return (MessageBodyReader<T>)reader;
            }
        }
        final MessageBodyReader reader = this._getMessageBodyReader(c, t, as, mediaType, lookup, this.readerProviders);
        return (MessageBodyReader<T>)reader;
    }
    
    private <T> MessageBodyReader<T> _getMessageBodyReader(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType, final MediaType lookup, final Map<MediaType, List<MessageBodyReader>> providers) {
        final List<MessageBodyReader> readers = providers.get(lookup);
        if (readers == null) {
            return null;
        }
        for (final MessageBodyReader p : readers) {
            if (p.isReadable(c, t, as, mediaType)) {
                return (MessageBodyReader<T>)p;
            }
        }
        return null;
    }
    
    @Override
    public <T> MessageBodyWriter<T> getMessageBodyWriter(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType) {
        if (!this.customWriterProviders.isEmpty()) {
            final MessageBodyWriter p = this._getMessageBodyWriter(c, t, as, mediaType, this.customWriterProviders);
            if (p != null) {
                return (MessageBodyWriter<T>)p;
            }
        }
        final MessageBodyWriter p = this._getMessageBodyWriter(c, t, as, mediaType, this.writerProviders);
        return (MessageBodyWriter<T>)p;
    }
    
    private <T> MessageBodyWriter<T> _getMessageBodyWriter(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType, final Map<MediaType, List<MessageBodyWriter>> providers) {
        MessageBodyWriter p = null;
        if (mediaType != null) {
            p = this._getMessageBodyWriter(c, t, as, mediaType, mediaType, providers);
            if (p == null) {
                p = this._getMessageBodyWriter(c, t, as, mediaType, MediaTypes.getTypeWildCart(mediaType), providers);
            }
        }
        if (p == null) {
            p = this._getMessageBodyWriter(c, t, as, mediaType, MediaTypes.GENERAL_MEDIA_TYPE, providers);
        }
        return (MessageBodyWriter<T>)p;
    }
    
    private <T> MessageBodyWriter<T> _getMessageBodyWriter(final Class<T> c, final Type t, final Annotation[] as, final MediaType mediaType, final MediaType lookup, final Map<MediaType, List<MessageBodyWriter>> providers) {
        final List<MessageBodyWriter> writers = providers.get(lookup);
        if (writers == null) {
            return null;
        }
        for (final MessageBodyWriter p : writers) {
            if (p.isWriteable(c, t, as, mediaType)) {
                return (MessageBodyWriter<T>)p;
            }
        }
        return null;
    }
    
    private <T> void getCompatibleReadersWritersMap(final MediaType mediaType, final Map<MediaType, List<T>> set, final Map<MediaType, List<T>> subSet) {
        if (mediaType.isWildcardType()) {
            this.getCompatibleReadersWritersList(mediaType, (Map<MediaType, List<Object>>)set, (Map<MediaType, List<Object>>)subSet);
        }
        else if (mediaType.isWildcardSubtype()) {
            this.getCompatibleReadersWritersList(mediaType, (Map<MediaType, List<Object>>)set, (Map<MediaType, List<Object>>)subSet);
            this.getCompatibleReadersWritersList(MediaTypes.GENERAL_MEDIA_TYPE, (Map<MediaType, List<Object>>)set, (Map<MediaType, List<Object>>)subSet);
        }
        else {
            this.getCompatibleReadersWritersList(mediaType, (Map<MediaType, List<Object>>)set, (Map<MediaType, List<Object>>)subSet);
            this.getCompatibleReadersWritersList(MediaTypes.getTypeWildCart(mediaType), (Map<MediaType, List<Object>>)set, (Map<MediaType, List<Object>>)subSet);
            this.getCompatibleReadersWritersList(MediaTypes.GENERAL_MEDIA_TYPE, (Map<MediaType, List<Object>>)set, (Map<MediaType, List<Object>>)subSet);
        }
    }
    
    private <T> void getCompatibleReadersWritersList(final MediaType mediaType, final Map<MediaType, List<T>> set, final Map<MediaType, List<T>> subSet) {
        final List<T> readers = set.get(mediaType);
        if (readers != null) {
            subSet.put(mediaType, Collections.unmodifiableList((List<? extends T>)readers));
        }
    }
    
    @Override
    public <T> List<MediaType> getMessageBodyWriterMediaTypes(final Class<T> c, final Type t, final Annotation[] as) {
        final List<MediaType> mtl = new ArrayList<MediaType>();
        for (final MessageBodyWriterPair mbwp : this.customWriterListProviders) {
            if (mbwp.mbw.isWriteable(c, t, as, MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
                mtl.addAll(mbwp.types);
            }
        }
        for (final MessageBodyWriterPair mbwp : this.writerListProviders) {
            if (mbwp.mbw.isWriteable(c, t, as, MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
                mtl.addAll(mbwp.types);
            }
        }
        Collections.sort(mtl, MediaTypes.MEDIA_TYPE_COMPARATOR);
        return mtl;
    }
    
    @Override
    public <T> MediaType getMessageBodyWriterMediaType(final Class<T> c, final Type t, final Annotation[] as, final List<MediaType> acceptableMediaTypes) {
        for (final MediaType acceptable : acceptableMediaTypes) {
            for (final MessageBodyWriterPair mbwp : this.customWriterListProviders) {
                for (final MediaType mt : mbwp.types) {
                    if (mt.isCompatible(acceptable) && mbwp.mbw.isWriteable(c, t, as, acceptable)) {
                        return MediaTypes.mostSpecific(mt, acceptable);
                    }
                }
            }
            for (final MessageBodyWriterPair mbwp : this.writerListProviders) {
                for (final MediaType mt : mbwp.types) {
                    if (mt.isCompatible(acceptable) && mbwp.mbw.isWriteable(c, t, as, acceptable)) {
                        return MediaTypes.mostSpecific(mt, acceptable);
                    }
                }
            }
        }
        return null;
    }
    
    static {
        MEDIA_TYPE_COMPARATOR = new KeyComparator<MediaType>() {
            @Override
            public boolean equals(final MediaType x, final MediaType y) {
                return x.getType().equalsIgnoreCase(y.getType()) && x.getSubtype().equalsIgnoreCase(y.getSubtype());
            }
            
            @Override
            public int hash(final MediaType k) {
                return k.getType().toLowerCase().hashCode() + k.getSubtype().toLowerCase().hashCode();
            }
            
            @Override
            public int compare(final MediaType o1, final MediaType o2) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }
    
    private static class MessageBodyWriterPair
    {
        final MessageBodyWriter mbw;
        final List<MediaType> types;
        
        MessageBodyWriterPair(final MessageBodyWriter mbw, final List<MediaType> types) {
            this.mbw = mbw;
            this.types = types;
        }
    }
    
    private static class DistanceComparator<T> implements Comparator<T>
    {
        private final Class<T> c;
        private final Map<Class, Integer> distanceMap;
        
        DistanceComparator(final Class c) {
            this.distanceMap = new HashMap<Class, Integer>();
            this.c = (Class<T>)c;
        }
        
        @Override
        public int compare(final T o1, final T o2) {
            final int d1 = this.getDistance(o1);
            final int d2 = this.getDistance(o2);
            return d2 - d1;
        }
        
        int getDistance(final T t) {
            Integer d = this.distanceMap.get(t.getClass());
            if (d != null) {
                return d;
            }
            final ReflectionHelper.DeclaringClassInterfacePair p = ReflectionHelper.getClass(t.getClass(), this.c);
            final Class[] as = ReflectionHelper.getParameterizedClassArguments(p);
            Class a = (as != null) ? as[0] : null;
            d = 0;
            while (a != null && a != Object.class) {
                ++d;
                a = a.getSuperclass();
            }
            this.distanceMap.put(t.getClass(), d);
            return d;
        }
    }
}
