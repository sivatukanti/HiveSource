// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.api;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.Set;

public interface EmptyElementHandler
{
    boolean allowEmptyElement(final String p0, final String p1, final String p2, final boolean p3);
    
    public static class SetEmptyElementHandler implements EmptyElementHandler
    {
        protected final Set<String> mEmptyElements;
        
        public SetEmptyElementHandler(final Set<String> emptyElements) {
            this.mEmptyElements = emptyElements;
        }
        
        @Override
        public boolean allowEmptyElement(final String prefix, final String localName, final String nsURI, final boolean allowEmpty) {
            return this.mEmptyElements.contains(localName);
        }
    }
    
    public static class HtmlEmptyElementHandler extends SetEmptyElementHandler
    {
        private static final HtmlEmptyElementHandler sInstance;
        
        public static HtmlEmptyElementHandler getInstance() {
            return HtmlEmptyElementHandler.sInstance;
        }
        
        protected HtmlEmptyElementHandler() {
            super(new TreeSet<String>(String.CASE_INSENSITIVE_ORDER));
            this.mEmptyElements.add("area");
            this.mEmptyElements.add("base");
            this.mEmptyElements.add("basefont");
            this.mEmptyElements.add("br");
            this.mEmptyElements.add("col");
            this.mEmptyElements.add("frame");
            this.mEmptyElements.add("hr");
            this.mEmptyElements.add("input");
            this.mEmptyElements.add("img");
            this.mEmptyElements.add("isindex");
            this.mEmptyElements.add("link");
            this.mEmptyElements.add("meta");
            this.mEmptyElements.add("param");
        }
        
        static {
            sInstance = new HtmlEmptyElementHandler();
        }
    }
}
