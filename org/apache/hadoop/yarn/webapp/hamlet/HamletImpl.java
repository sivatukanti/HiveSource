// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.hamlet;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import org.apache.hadoop.yarn.webapp.WebAppException;
import com.google.common.collect.Iterables;
import org.apache.hadoop.yarn.webapp.SubView;
import org.apache.commons.lang.StringEscapeUtils;
import java.util.EnumSet;
import java.io.PrintWriter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class HamletImpl extends HamletSpec
{
    private static final String INDENT_CHARS = "  ";
    private static final Splitter SS;
    private static final Joiner SJ;
    private static final Joiner CJ;
    static final int S_ID = 0;
    static final int S_CLASS = 1;
    int nestLevel;
    int indents;
    private final PrintWriter out;
    private final StringBuilder sb;
    private boolean wasInline;
    
    public HamletImpl(final PrintWriter out, final int nestLevel, final boolean wasInline) {
        this.sb = new StringBuilder();
        this.wasInline = false;
        this.out = out;
        this.nestLevel = nestLevel;
        this.wasInline = wasInline;
    }
    
    public int nestLevel() {
        return this.nestLevel;
    }
    
    public boolean wasInline() {
        return this.wasInline;
    }
    
    public void setWasInline(final boolean state) {
        this.wasInline = state;
    }
    
    public PrintWriter getWriter() {
        return this.out;
    }
    
    public <T extends _> Generic<T> root(final String name, final EnumSet<EOpt> opts) {
        return new Generic<T>(name, null, opts);
    }
    
    public <T extends _> Generic<T> root(final String name) {
        return this.root(name, EnumSet.of(EOpt.ENDTAG));
    }
    
    protected void printStartTag(final String name, final EnumSet<EOpt> opts) {
        this.indent(opts);
        this.sb.setLength(0);
        this.out.print(this.sb.append('<').append(name).toString());
    }
    
    protected void indent(final EnumSet<EOpt> opts) {
        if (opts.contains(EOpt.INLINE) && this.wasInline) {
            return;
        }
        if (this.wasInline) {
            this.out.println();
        }
        this.wasInline = (opts.contains(EOpt.INLINE) || opts.contains(EOpt.PRE));
        for (int i = 0; i < this.nestLevel; ++i) {
            this.out.print("  ");
        }
        ++this.indents;
    }
    
    protected void printEndTag(final String name, final EnumSet<EOpt> opts) {
        if (!opts.contains(EOpt.ENDTAG)) {
            return;
        }
        if (!opts.contains(EOpt.PRE)) {
            this.indent(opts);
        }
        else {
            this.wasInline = opts.contains(EOpt.INLINE);
        }
        this.sb.setLength(0);
        this.out.print(this.sb.append("</").append(name).append('>').toString());
        if (!opts.contains(EOpt.INLINE)) {
            this.out.println();
        }
    }
    
    protected void printAttr(final String name, final String value) {
        this.sb.setLength(0);
        this.sb.append(' ').append(name);
        if (value != null) {
            this.sb.append("=\"").append(StringEscapeUtils.escapeHtml(value)).append("\"");
        }
        this.out.print(this.sb.toString());
    }
    
    protected void subView(final Class<? extends SubView> cls) {
        this.indent(EnumSet.of(EOpt.ENDTAG));
        this.sb.setLength(0);
        this.out.print(this.sb.append('[').append(cls.getName()).append(']').toString());
        this.out.println();
    }
    
    public static String[] parseSelector(final String selector) {
        final String[] result = { null, null };
        final Iterable<String> rs = HamletImpl.SS.split(selector);
        final Iterator<String> it = rs.iterator();
        if (it.hasNext()) {
            final String maybeId = it.next();
            if (maybeId.charAt(0) == '#') {
                result[0] = maybeId.substring(1);
                if (it.hasNext()) {
                    result[1] = HamletImpl.SJ.join(Iterables.skip(rs, 1));
                }
            }
            else {
                result[1] = HamletImpl.SJ.join(rs);
            }
            return result;
        }
        throw new WebAppException("Error parsing selector: " + selector);
    }
    
    public static <E extends CoreAttrs> E setSelector(final E e, final String selector) {
        final String[] res = parseSelector(selector);
        if (res[0] != null) {
            e.$id(res[0]);
        }
        if (res[1] != null) {
            e.$class(res[1]);
        }
        return e;
    }
    
    public static <E extends LINK> E setLinkHref(final E e, final String href) {
        if (href.endsWith(".css")) {
            e.$rel("stylesheet");
        }
        e.$href(href);
        return e;
    }
    
    public static <E extends SCRIPT> E setScriptSrc(final E e, final String src) {
        if (src.endsWith(".js")) {
            e.$type("text/javascript");
        }
        e.$src(src);
        return e;
    }
    
    static {
        SS = Splitter.on('.').omitEmptyStrings().trimResults();
        SJ = Joiner.on(' ');
        CJ = Joiner.on(", ");
    }
    
    public enum EOpt
    {
        ENDTAG, 
        INLINE, 
        PRE;
    }
    
    public class EImp<T extends _> implements _Child
    {
        private final String name;
        private final T parent;
        private final EnumSet<EOpt> opts;
        private boolean started;
        private boolean attrsClosed;
        
        EImp(final String name, final T parent, final EnumSet<EOpt> opts) {
            this.started = false;
            this.attrsClosed = false;
            this.name = name;
            this.parent = parent;
            this.opts = opts;
        }
        
        @Override
        public T _() {
            this.closeAttrs();
            final HamletImpl this$0 = HamletImpl.this;
            --this$0.nestLevel;
            HamletImpl.this.printEndTag(this.name, this.opts);
            return this.parent;
        }
        
        protected void _p(final boolean quote, final Object... args) {
            this.closeAttrs();
            for (final Object s : args) {
                if (!this.opts.contains(EOpt.PRE)) {
                    HamletImpl.this.indent(this.opts);
                }
                HamletImpl.this.out.print(quote ? StringEscapeUtils.escapeHtml(String.valueOf(s)) : String.valueOf(s));
                if (!this.opts.contains(EOpt.INLINE) && !this.opts.contains(EOpt.PRE)) {
                    HamletImpl.this.out.println();
                }
            }
        }
        
        protected void _v(final Class<? extends SubView> cls) {
            this.closeAttrs();
            HamletImpl.this.subView(cls);
        }
        
        protected void closeAttrs() {
            if (!this.attrsClosed) {
                this.startIfNeeded();
                final HamletImpl this$0 = HamletImpl.this;
                ++this$0.nestLevel;
                HamletImpl.this.out.print('>');
                if (!this.opts.contains(EOpt.INLINE) && !this.opts.contains(EOpt.PRE)) {
                    HamletImpl.this.out.println();
                }
                this.attrsClosed = true;
            }
        }
        
        protected void addAttr(final String name, final String value) {
            Preconditions.checkState(!this.attrsClosed, (Object)"attribute added after content");
            this.startIfNeeded();
            HamletImpl.this.printAttr(name, value);
        }
        
        protected void addAttr(final String name, final Object value) {
            this.addAttr(name, String.valueOf(value));
        }
        
        protected void addMediaAttr(final String name, final EnumSet<Media> media) {
            this.addAttr(name, HamletImpl.CJ.join(media));
        }
        
        protected void addRelAttr(final String name, final EnumSet<LinkType> types) {
            this.addAttr(name, HamletImpl.SJ.join(types));
        }
        
        private void startIfNeeded() {
            if (!this.started) {
                HamletImpl.this.printStartTag(this.name, this.opts);
                this.started = true;
            }
        }
        
        protected void _inline(final boolean choice) {
            if (choice) {
                this.opts.add(EOpt.INLINE);
            }
            else {
                this.opts.remove(EOpt.INLINE);
            }
        }
        
        protected void _endTag(final boolean choice) {
            if (choice) {
                this.opts.add(EOpt.ENDTAG);
            }
            else {
                this.opts.remove(EOpt.ENDTAG);
            }
        }
        
        protected void _pre(final boolean choice) {
            if (choice) {
                this.opts.add(EOpt.PRE);
            }
            else {
                this.opts.remove(EOpt.PRE);
            }
        }
    }
    
    public class Generic<T extends _> extends EImp<T> implements PCData
    {
        Generic(final String name, final T parent, final EnumSet<EOpt> opts) {
            super(name, parent, opts);
        }
        
        public Generic<T> _inline() {
            super._inline(true);
            return this;
        }
        
        public Generic<T> _noEndTag() {
            super._endTag(false);
            return this;
        }
        
        public Generic<T> _pre() {
            super._pre(true);
            return this;
        }
        
        public Generic<T> _attr(final String name, final String value) {
            this.addAttr(name, value);
            return this;
        }
        
        public Generic<Generic<T>> _elem(final String name, final EnumSet<EOpt> opts) {
            this.closeAttrs();
            return new Generic<Generic<T>>(name, this, opts);
        }
        
        public Generic<Generic<T>> elem(final String name) {
            return this._elem(name, EnumSet.of(EOpt.ENDTAG));
        }
        
        @Override
        public Generic<T> _(final Object... lines) {
            this._p(true, lines);
            return this;
        }
        
        @Override
        public Generic<T> _r(final Object... lines) {
            this._p(false, lines);
            return this;
        }
    }
}
