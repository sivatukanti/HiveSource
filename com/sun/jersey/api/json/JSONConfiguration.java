// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.json;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import com.sun.jersey.json.impl.ImplMessages;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.jersey.json.impl.JSONHelper;
import java.util.Map;
import java.util.Collection;

public class JSONConfiguration
{
    public static final String FEATURE_POJO_MAPPING = "com.sun.jersey.api.json.POJOMappingFeature";
    private final Notation notation;
    private final Collection<String> arrays;
    private final Collection<String> attrsAsElems;
    private final Collection<String> nonStrings;
    private final boolean rootUnwrapping;
    private final boolean humanReadableFormatting;
    private final Map<String, String> jsonXml2JsonNs;
    private final boolean usePrefixAtNaturalAttributes;
    private final Character namespaceSeparator;
    public static final JSONConfiguration DEFAULT;
    
    private JSONConfiguration(final Builder b) {
        this.notation = b.notation;
        this.arrays = b.arrays;
        this.attrsAsElems = b.attrsAsElems;
        this.nonStrings = b.nonStrings;
        this.rootUnwrapping = b.rootUnwrapping;
        this.humanReadableFormatting = b.humanReadableFormatting;
        this.jsonXml2JsonNs = b.jsonXml2JsonNs;
        this.usePrefixAtNaturalAttributes = b.usePrefixAtNaturalAttributes;
        this.namespaceSeparator = b.namespaceSeparator;
    }
    
    public static JSONConfiguration createJSONConfigurationWithFormatted(final JSONConfiguration c, final boolean formatted) throws IllegalArgumentException {
        if (c == null) {
            throw new IllegalArgumentException("JSONConfiguration can't be null");
        }
        if (c.isHumanReadableFormatting() == formatted) {
            return c;
        }
        final Builder b = copyBuilder(c);
        b.humanReadableFormatting = formatted;
        return b.build();
    }
    
    public static JSONConfiguration createJSONConfigurationWithRootUnwrapping(final JSONConfiguration c, final boolean rootUnwrapping) throws IllegalArgumentException {
        if (c == null) {
            throw new IllegalArgumentException("JSONConfiguration can't be null");
        }
        if (c.isRootUnwrapping() == rootUnwrapping) {
            return c;
        }
        final Builder b = copyBuilder(c);
        b.rootUnwrapping = rootUnwrapping;
        return b.build();
    }
    
    public static NaturalBuilder natural() {
        if (!JSONHelper.isNaturalNotationEnabled()) {
            Logger.getLogger(JSONConfiguration.class.getName()).log(Level.SEVERE, ImplMessages.ERROR_JAXB_RI_2_1_10_MISSING());
            throw new RuntimeException(ImplMessages.ERROR_JAXB_RI_2_1_10_MISSING());
        }
        return new NaturalBuilder(Notation.NATURAL);
    }
    
    public static MappedBuilder mapped() {
        return new MappedBuilder(Notation.MAPPED);
    }
    
    public static MappedJettisonBuilder mappedJettison() {
        return new MappedJettisonBuilder(Notation.MAPPED_JETTISON);
    }
    
    public static Builder badgerFish() {
        final Builder badgerFishBuilder = new Builder(Notation.BADGERFISH);
        badgerFishBuilder.rootUnwrapping = false;
        return badgerFishBuilder;
    }
    
    public static Builder copyBuilder(final JSONConfiguration jc) {
        Builder result = new Builder(jc.getNotation());
        switch (jc.notation) {
            case BADGERFISH: {
                result = new Builder(jc.getNotation());
                break;
            }
            case MAPPED_JETTISON: {
                result = new MappedJettisonBuilder(jc.getNotation());
                break;
            }
            case MAPPED: {
                result = new MappedBuilder(jc.getNotation());
                break;
            }
            case NATURAL: {
                result = new NaturalBuilder(jc.getNotation());
                break;
            }
        }
        result.copyAttributes(jc);
        return result;
    }
    
    public Notation getNotation() {
        return this.notation;
    }
    
    public Collection<String> getArrays() {
        return (this.arrays != null) ? Collections.unmodifiableCollection((Collection<? extends String>)this.arrays) : null;
    }
    
    public Collection<String> getAttributeAsElements() {
        return (this.attrsAsElems != null) ? Collections.unmodifiableCollection((Collection<? extends String>)this.attrsAsElems) : null;
    }
    
    public Map<String, String> getXml2JsonNs() {
        return (this.jsonXml2JsonNs != null) ? Collections.unmodifiableMap((Map<? extends String, ? extends String>)this.jsonXml2JsonNs) : null;
    }
    
    public Character getNsSeparator() {
        return this.namespaceSeparator;
    }
    
    public Collection<String> getNonStrings() {
        return (this.nonStrings != null) ? Collections.unmodifiableCollection((Collection<? extends String>)this.nonStrings) : null;
    }
    
    public boolean isRootUnwrapping() {
        return this.rootUnwrapping;
    }
    
    public boolean isUsingPrefixesAtNaturalAttributes() {
        return this.usePrefixAtNaturalAttributes;
    }
    
    public boolean isHumanReadableFormatting() {
        return this.humanReadableFormatting;
    }
    
    @Override
    public String toString() {
        return String.format("{notation:%s,rootStripping:%b}", this.notation, this.rootUnwrapping);
    }
    
    static {
        DEFAULT = mapped().rootUnwrapping(true).build();
    }
    
    public enum Notation
    {
        MAPPED, 
        MAPPED_JETTISON, 
        BADGERFISH, 
        NATURAL;
    }
    
    public static class Builder
    {
        private final Notation notation;
        protected Collection<String> arrays;
        protected Collection<String> attrsAsElems;
        protected Collection<String> nonStrings;
        protected boolean rootUnwrapping;
        protected boolean humanReadableFormatting;
        protected Map<String, String> jsonXml2JsonNs;
        protected boolean usePrefixAtNaturalAttributes;
        protected Character namespaceSeparator;
        
        private Builder(final Notation notation) {
            this.arrays = new HashSet<String>(0);
            this.attrsAsElems = new HashSet<String>(0);
            this.nonStrings = new HashSet<String>(0);
            this.rootUnwrapping = true;
            this.humanReadableFormatting = false;
            this.jsonXml2JsonNs = new HashMap<String, String>(0);
            this.usePrefixAtNaturalAttributes = false;
            this.namespaceSeparator = '.';
            this.notation = notation;
        }
        
        public JSONConfiguration build() {
            return new JSONConfiguration(this, null);
        }
        
        private void copyAttributes(final JSONConfiguration jc) {
            this.arrays.addAll(jc.getArrays());
            this.attrsAsElems.addAll(jc.getAttributeAsElements());
            this.nonStrings.addAll(jc.getNonStrings());
            this.rootUnwrapping = jc.isRootUnwrapping();
            this.humanReadableFormatting = jc.isHumanReadableFormatting();
            this.jsonXml2JsonNs.putAll(jc.getXml2JsonNs());
            this.usePrefixAtNaturalAttributes = jc.isUsingPrefixesAtNaturalAttributes();
            this.namespaceSeparator = jc.getNsSeparator();
        }
    }
    
    public static class NaturalBuilder extends Builder
    {
        private NaturalBuilder(final Notation notation) {
            super(notation);
        }
        
        public NaturalBuilder rootUnwrapping(final boolean rootUnwrapping) {
            this.rootUnwrapping = rootUnwrapping;
            return this;
        }
        
        public NaturalBuilder humanReadableFormatting(final boolean humanReadableFormatting) {
            this.humanReadableFormatting = humanReadableFormatting;
            return this;
        }
        
        public NaturalBuilder usePrefixesAtNaturalAttributes() {
            this.usePrefixAtNaturalAttributes = true;
            return this;
        }
    }
    
    public static class MappedJettisonBuilder extends Builder
    {
        private MappedJettisonBuilder(final Notation notation) {
            super(notation);
            this.rootUnwrapping = false;
        }
        
        public MappedJettisonBuilder xml2JsonNs(final Map<String, String> jsonXml2JsonNs) {
            this.jsonXml2JsonNs = jsonXml2JsonNs;
            return this;
        }
    }
    
    public static class MappedBuilder extends Builder
    {
        private MappedBuilder(final Notation notation) {
            super(notation);
        }
        
        public MappedBuilder arrays(final String... arrays) {
            this.arrays.addAll(Arrays.asList(arrays));
            return this;
        }
        
        public MappedBuilder attributeAsElement(final String... attributeAsElements) {
            this.attrsAsElems.addAll(Arrays.asList(attributeAsElements));
            return this;
        }
        
        public MappedBuilder nonStrings(final String... nonStrings) {
            this.nonStrings.addAll(Arrays.asList(nonStrings));
            return this;
        }
        
        public MappedBuilder xml2JsonNs(final Map<String, String> jsonXml2JsonNs) {
            this.jsonXml2JsonNs = jsonXml2JsonNs;
            return this;
        }
        
        public MappedBuilder nsSeparator(final Character separator) {
            if (separator == null) {
                throw new NullPointerException("Namespace separator can not be null!");
            }
            this.namespaceSeparator = separator;
            return this;
        }
        
        public MappedBuilder rootUnwrapping(final boolean rootUnwrapping) {
            this.rootUnwrapping = rootUnwrapping;
            return this;
        }
    }
}
