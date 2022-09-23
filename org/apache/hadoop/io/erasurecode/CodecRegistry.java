// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode;

import org.slf4j.LoggerFactory;
import java.util.Set;
import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import org.apache.hadoop.io.erasurecode.rawcoder.NativeXORRawErasureCoderFactory;
import org.apache.hadoop.io.erasurecode.rawcoder.NativeRSRawErasureCoderFactory;
import java.util.ArrayList;
import java.util.ServiceLoader;
import java.util.HashMap;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureCoderFactory;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class CodecRegistry
{
    private static final Logger LOG;
    private static CodecRegistry instance;
    private Map<String, List<RawErasureCoderFactory>> coderMap;
    private Map<String, String[]> coderNameMap;
    private HashMap<String, String> coderNameCompactMap;
    
    public static CodecRegistry getInstance() {
        return CodecRegistry.instance;
    }
    
    private CodecRegistry() {
        this.coderMap = new HashMap<String, List<RawErasureCoderFactory>>();
        this.coderNameMap = new HashMap<String, String[]>();
        this.coderNameCompactMap = new HashMap<String, String>();
        final ServiceLoader<RawErasureCoderFactory> coderFactories = ServiceLoader.load(RawErasureCoderFactory.class);
        this.updateCoders(coderFactories);
    }
    
    @VisibleForTesting
    void updateCoders(final Iterable<RawErasureCoderFactory> coderFactories) {
        for (final RawErasureCoderFactory coderFactory : coderFactories) {
            final String codecName = coderFactory.getCodecName();
            List<RawErasureCoderFactory> coders = this.coderMap.get(codecName);
            if (coders == null) {
                coders = new ArrayList<RawErasureCoderFactory>();
                coders.add(coderFactory);
                this.coderMap.put(codecName, coders);
                CodecRegistry.LOG.debug("Codec registered: codec = {}, coder = {}", coderFactory.getCodecName(), coderFactory.getCoderName());
            }
            else {
                Boolean hasConflit = false;
                for (final RawErasureCoderFactory coder : coders) {
                    if (coder.getCoderName().equals(coderFactory.getCoderName())) {
                        hasConflit = true;
                        CodecRegistry.LOG.error("Coder {} cannot be registered because its coder name {} has conflict with {}", coderFactory.getClass().getName(), coderFactory.getCoderName(), coder.getClass().getName());
                        break;
                    }
                }
                if (hasConflit) {
                    continue;
                }
                if (coderFactory instanceof NativeRSRawErasureCoderFactory || coderFactory instanceof NativeXORRawErasureCoderFactory) {
                    coders.add(0, coderFactory);
                }
                else {
                    coders.add(coderFactory);
                }
                CodecRegistry.LOG.debug("Codec registered: codec = {}, coder = {}", coderFactory.getCodecName(), coderFactory.getCoderName());
            }
        }
        this.coderNameMap.clear();
        for (final Map.Entry<String, List<RawErasureCoderFactory>> entry : this.coderMap.entrySet()) {
            final String codecName = entry.getKey();
            final List<RawErasureCoderFactory> coders = entry.getValue();
            this.coderNameMap.put(codecName, coders.stream().map((Function<? super Object, ?>)RawErasureCoderFactory::getCoderName).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()).toArray(new String[0]));
            this.coderNameCompactMap.put(codecName, coders.stream().map((Function<? super Object, ?>)RawErasureCoderFactory::getCoderName).collect((Collector<? super Object, ?, String>)Collectors.joining(", ")));
        }
    }
    
    public String[] getCoderNames(final String codecName) {
        final String[] coderNames = this.coderNameMap.get(codecName);
        return coderNames;
    }
    
    public List<RawErasureCoderFactory> getCoders(final String codecName) {
        final List<RawErasureCoderFactory> coders = this.coderMap.get(codecName);
        return coders;
    }
    
    public Set<String> getCodecNames() {
        return this.coderMap.keySet();
    }
    
    public RawErasureCoderFactory getCoderByName(final String codecName, final String coderName) {
        final List<RawErasureCoderFactory> coders = this.getCoders(codecName);
        for (final RawErasureCoderFactory coder : coders) {
            if (coder.getCoderName().equals(coderName)) {
                return coder;
            }
        }
        return null;
    }
    
    public Map<String, String> getCodec2CoderCompactMap() {
        return this.coderNameCompactMap;
    }
    
    static {
        LOG = LoggerFactory.getLogger(CodecRegistry.class);
        CodecRegistry.instance = new CodecRegistry();
    }
}
