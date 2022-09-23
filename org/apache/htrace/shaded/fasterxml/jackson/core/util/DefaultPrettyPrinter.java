// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core.util;

import java.util.Arrays;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.core.SerializableString;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.SerializedString;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.core.PrettyPrinter;

public class DefaultPrettyPrinter implements PrettyPrinter, Instantiatable<DefaultPrettyPrinter>, Serializable
{
    private static final long serialVersionUID = -5512586643324525213L;
    public static final SerializedString DEFAULT_ROOT_VALUE_SEPARATOR;
    protected Indenter _arrayIndenter;
    protected Indenter _objectIndenter;
    protected final SerializableString _rootSeparator;
    protected boolean _spacesInObjectEntries;
    protected transient int _nesting;
    
    public DefaultPrettyPrinter() {
        this(DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
    }
    
    public DefaultPrettyPrinter(final String rootSeparator) {
        this((rootSeparator == null) ? null : new SerializedString(rootSeparator));
    }
    
    public DefaultPrettyPrinter(final SerializableString rootSeparator) {
        this._arrayIndenter = FixedSpaceIndenter.instance;
        this._objectIndenter = Lf2SpacesIndenter.instance;
        this._spacesInObjectEntries = true;
        this._nesting = 0;
        this._rootSeparator = rootSeparator;
    }
    
    public DefaultPrettyPrinter(final DefaultPrettyPrinter base) {
        this(base, base._rootSeparator);
    }
    
    public DefaultPrettyPrinter(final DefaultPrettyPrinter base, final SerializableString rootSeparator) {
        this._arrayIndenter = FixedSpaceIndenter.instance;
        this._objectIndenter = Lf2SpacesIndenter.instance;
        this._spacesInObjectEntries = true;
        this._nesting = 0;
        this._arrayIndenter = base._arrayIndenter;
        this._objectIndenter = base._objectIndenter;
        this._spacesInObjectEntries = base._spacesInObjectEntries;
        this._nesting = base._nesting;
        this._rootSeparator = rootSeparator;
    }
    
    public DefaultPrettyPrinter withRootSeparator(final SerializableString rootSeparator) {
        if (this._rootSeparator == rootSeparator || (rootSeparator != null && rootSeparator.equals(this._rootSeparator))) {
            return this;
        }
        return new DefaultPrettyPrinter(this, rootSeparator);
    }
    
    public void indentArraysWith(final Indenter i) {
        this._arrayIndenter = ((i == null) ? NopIndenter.instance : i);
    }
    
    public void indentObjectsWith(final Indenter i) {
        this._objectIndenter = ((i == null) ? NopIndenter.instance : i);
    }
    
    @Deprecated
    public void spacesInObjectEntries(final boolean b) {
        this._spacesInObjectEntries = b;
    }
    
    public DefaultPrettyPrinter withArrayIndenter(Indenter i) {
        if (i == null) {
            i = NopIndenter.instance;
        }
        if (this._arrayIndenter == i) {
            return this;
        }
        final DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
        pp._arrayIndenter = i;
        return pp;
    }
    
    public DefaultPrettyPrinter withObjectIndenter(Indenter i) {
        if (i == null) {
            i = NopIndenter.instance;
        }
        if (this._objectIndenter == i) {
            return this;
        }
        final DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
        pp._objectIndenter = i;
        return pp;
    }
    
    public DefaultPrettyPrinter withSpacesInObjectEntries() {
        return this._withSpaces(true);
    }
    
    public DefaultPrettyPrinter withoutSpacesInObjectEntries() {
        return this._withSpaces(false);
    }
    
    protected DefaultPrettyPrinter _withSpaces(final boolean state) {
        if (this._spacesInObjectEntries == state) {
            return this;
        }
        final DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
        pp._spacesInObjectEntries = state;
        return pp;
    }
    
    @Override
    public DefaultPrettyPrinter createInstance() {
        return new DefaultPrettyPrinter(this);
    }
    
    @Override
    public void writeRootValueSeparator(final JsonGenerator jg) throws IOException, JsonGenerationException {
        if (this._rootSeparator != null) {
            jg.writeRaw(this._rootSeparator);
        }
    }
    
    @Override
    public void writeStartObject(final JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw('{');
        if (!this._objectIndenter.isInline()) {
            ++this._nesting;
        }
    }
    
    @Override
    public void beforeObjectEntries(final JsonGenerator jg) throws IOException, JsonGenerationException {
        this._objectIndenter.writeIndentation(jg, this._nesting);
    }
    
    @Override
    public void writeObjectFieldValueSeparator(final JsonGenerator jg) throws IOException, JsonGenerationException {
        if (this._spacesInObjectEntries) {
            jg.writeRaw(" : ");
        }
        else {
            jg.writeRaw(':');
        }
    }
    
    @Override
    public void writeObjectEntrySeparator(final JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw(',');
        this._objectIndenter.writeIndentation(jg, this._nesting);
    }
    
    @Override
    public void writeEndObject(final JsonGenerator jg, final int nrOfEntries) throws IOException, JsonGenerationException {
        if (!this._objectIndenter.isInline()) {
            --this._nesting;
        }
        if (nrOfEntries > 0) {
            this._objectIndenter.writeIndentation(jg, this._nesting);
        }
        else {
            jg.writeRaw(' ');
        }
        jg.writeRaw('}');
    }
    
    @Override
    public void writeStartArray(final JsonGenerator jg) throws IOException, JsonGenerationException {
        if (!this._arrayIndenter.isInline()) {
            ++this._nesting;
        }
        jg.writeRaw('[');
    }
    
    @Override
    public void beforeArrayValues(final JsonGenerator jg) throws IOException, JsonGenerationException {
        this._arrayIndenter.writeIndentation(jg, this._nesting);
    }
    
    @Override
    public void writeArrayValueSeparator(final JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw(',');
        this._arrayIndenter.writeIndentation(jg, this._nesting);
    }
    
    @Override
    public void writeEndArray(final JsonGenerator jg, final int nrOfValues) throws IOException, JsonGenerationException {
        if (!this._arrayIndenter.isInline()) {
            --this._nesting;
        }
        if (nrOfValues > 0) {
            this._arrayIndenter.writeIndentation(jg, this._nesting);
        }
        else {
            jg.writeRaw(' ');
        }
        jg.writeRaw(']');
    }
    
    static {
        DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(" ");
    }
    
    public static class NopIndenter implements Indenter, Serializable
    {
        public static final NopIndenter instance;
        
        @Override
        public void writeIndentation(final JsonGenerator jg, final int level) throws IOException, JsonGenerationException {
        }
        
        @Override
        public boolean isInline() {
            return true;
        }
        
        static {
            instance = new NopIndenter();
        }
    }
    
    public static class FixedSpaceIndenter extends NopIndenter
    {
        public static final FixedSpaceIndenter instance;
        
        @Override
        public void writeIndentation(final JsonGenerator jg, final int level) throws IOException, JsonGenerationException {
            jg.writeRaw(' ');
        }
        
        @Override
        public boolean isInline() {
            return true;
        }
        
        static {
            instance = new FixedSpaceIndenter();
        }
    }
    
    public static class Lf2SpacesIndenter extends NopIndenter
    {
        private static final String SYS_LF;
        static final int SPACE_COUNT = 64;
        static final char[] SPACES;
        public static final Lf2SpacesIndenter instance;
        protected final String _lf;
        
        public Lf2SpacesIndenter() {
            this(Lf2SpacesIndenter.SYS_LF);
        }
        
        public Lf2SpacesIndenter(final String lf) {
            this._lf = lf;
        }
        
        public Lf2SpacesIndenter withLinefeed(final String lf) {
            if (lf.equals(this._lf)) {
                return this;
            }
            return new Lf2SpacesIndenter(lf);
        }
        
        @Override
        public boolean isInline() {
            return false;
        }
        
        @Override
        public void writeIndentation(final JsonGenerator jg, int level) throws IOException, JsonGenerationException {
            jg.writeRaw(this._lf);
            if (level > 0) {
                for (level += level; level > 64; level -= Lf2SpacesIndenter.SPACES.length) {
                    jg.writeRaw(Lf2SpacesIndenter.SPACES, 0, 64);
                }
                jg.writeRaw(Lf2SpacesIndenter.SPACES, 0, level);
            }
        }
        
        static {
            String lf = null;
            try {
                lf = System.getProperty("line.separator");
            }
            catch (Throwable t) {}
            SYS_LF = ((lf == null) ? "\n" : lf);
            Arrays.fill(SPACES = new char[64], ' ');
            instance = new Lf2SpacesIndenter();
        }
    }
    
    public interface Indenter
    {
        void writeIndentation(final JsonGenerator p0, final int p1) throws IOException, JsonGenerationException;
        
        boolean isInline();
    }
}
