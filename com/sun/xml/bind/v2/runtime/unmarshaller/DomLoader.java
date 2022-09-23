// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import org.xml.sax.Locator;
import javax.xml.bind.ValidationEventHandler;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.SAXException;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.transform.Result;

public class DomLoader<ResultT extends Result> extends Loader
{
    private final DomHandler<?, ResultT> dom;
    
    public DomLoader(final DomHandler<?, ResultT> dom) {
        super(true);
        this.dom = dom;
    }
    
    @Override
    public void startElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        final UnmarshallingContext context = state.getContext();
        if (state.target == null) {
            state.target = new State(context);
        }
        final State s = (State)state.target;
        try {
            s.declarePrefixes(context, context.getNewlyDeclaredPrefixes());
            s.handler.startElement(ea.uri, ea.local, ea.getQname(), ea.atts);
        }
        catch (SAXException e) {
            context.handleError(e);
            throw e;
        }
    }
    
    @Override
    public void childElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        state.loader = this;
        final State state2;
        final State s = state2 = (State)state.prev.target;
        ++state2.depth;
        state.target = s;
    }
    
    @Override
    public void text(final UnmarshallingContext.State state, final CharSequence text) throws SAXException {
        if (text.length() == 0) {
            return;
        }
        try {
            final State s = (State)state.target;
            s.handler.characters(text.toString().toCharArray(), 0, text.length());
        }
        catch (SAXException e) {
            state.getContext().handleError(e);
            throw e;
        }
    }
    
    @Override
    public void leaveElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        final State s = (State)state.target;
        final UnmarshallingContext context = state.getContext();
        try {
            s.handler.endElement(ea.uri, ea.local, ea.getQname());
            s.undeclarePrefixes(context.getNewlyDeclaredPrefixes());
        }
        catch (SAXException e) {
            context.handleError(e);
            throw e;
        }
        final State state2 = s;
        final int depth = state2.depth - 1;
        state2.depth = depth;
        if (depth == 0) {
            try {
                s.undeclarePrefixes(context.getAllDeclaredPrefixes());
                s.handler.endDocument();
            }
            catch (SAXException e) {
                context.handleError(e);
                throw e;
            }
            state.target = s.getElement();
        }
    }
    
    private final class State
    {
        private final TransformerHandler handler;
        private final ResultT result;
        int depth;
        
        public State(final UnmarshallingContext context) throws SAXException {
            this.handler = JAXBContextImpl.createTransformerHandler();
            this.depth = 1;
            this.result = DomLoader.this.dom.createUnmarshaller(context);
            this.handler.setResult(this.result);
            try {
                this.handler.setDocumentLocator(context.getLocator());
                this.handler.startDocument();
                this.declarePrefixes(context, context.getAllDeclaredPrefixes());
            }
            catch (SAXException e) {
                context.handleError(e);
                throw e;
            }
        }
        
        public Object getElement() {
            return DomLoader.this.dom.getElement(this.result);
        }
        
        private void declarePrefixes(final UnmarshallingContext context, final String[] prefixes) throws SAXException {
            for (int i = prefixes.length - 1; i >= 0; --i) {
                final String nsUri = context.getNamespaceURI(prefixes[i]);
                if (nsUri == null) {
                    throw new IllegalStateException("prefix '" + prefixes[i] + "' isn't bound");
                }
                this.handler.startPrefixMapping(prefixes[i], nsUri);
            }
        }
        
        private void undeclarePrefixes(final String[] prefixes) throws SAXException {
            for (int i = prefixes.length - 1; i >= 0; --i) {
                this.handler.endPrefixMapping(prefixes[i]);
            }
        }
    }
}
