// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.writer;

import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import com.sun.jersey.api.json.JSONConfiguration;
import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.io.Writer;
import javax.xml.stream.XMLStreamWriter;

public class JsonXmlStreamWriter extends DefaultXmlStreamWriter implements XMLStreamWriter
{
    Writer mainWriter;
    boolean stripRoot;
    char nsSeparator;
    final List<ProcessingState> processingStack;
    int depth;
    final Collection<String> arrayElementNames;
    final Collection<String> nonStringElementNames;
    final Map<String, String> xml2JsonNs;
    private final String rootName;
    
    private JsonXmlStreamWriter(final Writer writer, final JSONConfiguration config, final String rootName) {
        this.processingStack = new ArrayList<ProcessingState>();
        this.arrayElementNames = new LinkedList<String>();
        this.nonStringElementNames = new LinkedList<String>();
        this.xml2JsonNs = new HashMap<String, String>();
        this.mainWriter = writer;
        this.stripRoot = config.isRootUnwrapping();
        this.rootName = rootName;
        this.nsSeparator = config.getNsSeparator();
        if (null != config.getArrays()) {
            this.arrayElementNames.addAll(config.getArrays());
        }
        if (null != config.getNonStrings()) {
            this.nonStringElementNames.addAll(config.getNonStrings());
        }
        if (null != config.getXml2JsonNs()) {
            this.xml2JsonNs.putAll(config.getXml2JsonNs());
        }
        this.processingStack.add(this.createProcessingState());
        this.depth = 0;
    }
    
    public static XMLStreamWriter createWriter(final Writer writer, final JSONConfiguration config, final String rootName) {
        final Collection<String> attrsAsElems = config.getAttributeAsElements();
        if (attrsAsElems != null && !attrsAsElems.isEmpty()) {
            return new A2EXmlStreamWriterProxy(new JsonXmlStreamWriter(writer, config, rootName), attrsAsElems);
        }
        return new JsonXmlStreamWriter(writer, config, rootName);
    }
    
    @Override
    public void close() throws XMLStreamException {
        try {
            this.mainWriter.close();
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void flush() throws XMLStreamException {
        try {
            this.mainWriter.flush();
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void writeEndDocument() throws XMLStreamException {
        try {
            if (null != this.processingStack.get(this.depth).lastElementWriter) {
                this.processingStack.get(this.depth).writer.write(this.processingStack.get(this.depth).lastElementWriter.getContent());
            }
            if (null == this.processingStack.get(this.depth).lastWasPrimitive || !this.processingStack.get(this.depth).lastWasPrimitive) {
                this.processingStack.get(this.depth).writer.write("}");
            }
            this.pollStack();
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void writeEndElement() throws XMLStreamException {
        try {
            if (null != this.processingStack.get(this.depth).lastElementWriter) {
                if (this.processingStack.get(this.depth).lastIsArray) {
                    this.processingStack.get(this.depth).writer.write(",");
                    this.processingStack.get(this.depth).writer.write(this.processingStack.get(this.depth).lastElementWriter.getContent());
                    this.processingStack.get(this.depth).writer.write("]");
                }
                else if (this.isArrayElement(this.processingStack.get(this.depth).lastName)) {
                    this.processingStack.get(this.depth).writer.write(this.processingStack.get(this.depth).lastIsArray ? "," : "[");
                    this.processingStack.get(this.depth).lastIsArray = true;
                    this.processingStack.get(this.depth).writer.write(this.processingStack.get(this.depth).lastElementWriter.getContent());
                    this.processingStack.get(this.depth).writer.write("]");
                }
                else {
                    this.processingStack.get(this.depth).writer.write(this.processingStack.get(this.depth).lastElementWriter.getContent());
                }
            }
            if (this.processingStack.get(this.depth).writer.isEmpty) {
                String currentName = this.processingStack.get(this.depth).currentName;
                currentName = ((currentName == null) ? this.processingStack.get(this.depth - 1).currentName : currentName);
                if (this.arrayElementNames.contains(currentName) || this.nonStringElementNames.contains(currentName) || this.rootName.equals(currentName)) {
                    this.processingStack.get(this.depth).writer.write("{}");
                }
                else {
                    this.processingStack.get(this.depth).writer.write("null");
                }
            }
            else if (null == this.processingStack.get(this.depth).lastWasPrimitive || !this.processingStack.get(this.depth).lastWasPrimitive) {
                this.processingStack.get(this.depth).writer.write("}");
            }
            this.processingStack.get(this.depth - 1).lastName = this.processingStack.get(this.depth - 1).currentName;
            this.processingStack.get(this.depth - 1).lastWasPrimitive = false;
            this.processingStack.get(this.depth - 1).lastElementWriter = this.processingStack.get(this.depth).writer;
            this.pollStack();
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    private QName getQName(final String s) {
        final String[] currentName = s.split(Character.toString(this.nsSeparator));
        QName name = new QName(s);
        if (currentName.length > 1) {
            name = new QName(currentName[0], currentName[1]);
        }
        return name;
    }
    
    @Override
    public void writeCharacters(final char[] text, final int start, final int length) throws XMLStreamException {
        this.writeCharacters(new String(text, start, length));
    }
    
    @Override
    public void writeCharacters(final String text) throws XMLStreamException {
        if (this.processingStack.get(this.depth).isNotEmpty) {
            this.writeStartElement(null, "$", null);
            this._writeCharacters(text);
            this.writeEndElement();
        }
        else {
            this._writeCharacters(text);
        }
    }
    
    private void _writeCharacters(final String text) throws XMLStreamException {
        try {
            if (this.isNonString(this.processingStack.get(this.depth - 1).currentName)) {
                this.processingStack.get(this.depth).writer.write(JsonEncoder.encode(text));
            }
            else {
                this.processingStack.get(this.depth).writer.write("\"" + JsonEncoder.encode(text) + "\"");
            }
            this.processingStack.get(this.depth).lastWasPrimitive = true;
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException {
        this.writeEmptyElement(null, localName, null);
    }
    
    @Override
    public void writeStartElement(final String localName) throws XMLStreamException {
        this.writeStartElement(null, localName, null);
    }
    
    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException {
        this.writeAttribute(null, null, localName, value);
    }
    
    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writeEmptyElement(null, localName, null);
    }
    
    @Override
    public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
        this.writeStartElement(null, localName, namespaceURI);
    }
    
    @Override
    public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.writeAttribute(null, namespaceURI, localName, value);
    }
    
    @Override
    public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        this.writeStartElement(localName);
        this.writeEndElement();
    }
    
    private void pollStack() throws IOException {
        this.processingStack.remove(this.depth--);
    }
    
    private void printStack(final String localName) {
        try {
            for (int d = 0; d <= this.depth; ++d) {
                this.mainWriter.write("\n**" + d + ":" + this.processingStack.get(d));
            }
            this.mainWriter.write("\n*** [" + localName + "]");
        }
        catch (IOException ex) {
            Logger.getLogger(JsonXmlStreamWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean isArrayElement(final String name) {
        return null != name && this.arrayElementNames.contains(name);
    }
    
    private boolean isNonString(final String name) {
        return null != name && this.nonStringElementNames.contains(name);
    }
    
    private String getEffectiveName(final String namespaceURI, final String localName) {
        if (namespaceURI != null && this.xml2JsonNs.containsKey(namespaceURI)) {
            return String.format("%s%c%s", this.xml2JsonNs.get(namespaceURI), this.nsSeparator, localName);
        }
        return localName;
    }
    
    @Override
    public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
        final String effectiveName = this.getEffectiveName(namespaceURI, localName);
        this.processingStack.get(this.depth).isNotEmpty = true;
        this.processingStack.get(this.depth).currentName = effectiveName;
        try {
            final boolean isNextArrayElement = this.processingStack.get(this.depth).currentName.equals(this.processingStack.get(this.depth).lastName);
            if (!isNextArrayElement) {
                if (this.isArrayElement(this.processingStack.get(this.depth).lastName)) {
                    this.processingStack.get(this.depth).writer.write(this.processingStack.get(this.depth).lastIsArray ? "," : "[");
                    this.processingStack.get(this.depth).lastIsArray = true;
                    this.processingStack.get(this.depth).writer.write(this.processingStack.get(this.depth).lastElementWriter.getContent());
                }
                else {
                    if (null != this.processingStack.get(this.depth).lastElementWriter) {
                        if (this.processingStack.get(this.depth).lastIsArray) {
                            this.processingStack.get(this.depth).writer.write(",");
                            this.processingStack.get(this.depth).writer.write(this.processingStack.get(this.depth).lastElementWriter.getContent());
                            this.processingStack.get(this.depth).writer.write("]");
                        }
                        else {
                            this.processingStack.get(this.depth).writer.write(this.processingStack.get(this.depth).lastElementWriter.getContent());
                        }
                    }
                    this.processingStack.get(this.depth).lastIsArray = false;
                }
                if (null != this.processingStack.get(this.depth).lastName) {
                    if (this.processingStack.get(this.depth).lastIsArray) {
                        this.processingStack.get(this.depth).writer.write("]");
                        this.processingStack.get(this.depth).lastIsArray = false;
                    }
                    this.processingStack.get(this.depth).writer.write(",");
                }
                if (null == this.processingStack.get(this.depth).lastWasPrimitive) {
                    this.processingStack.get(this.depth).writer.write("{");
                }
                this.processingStack.get(this.depth).writer.write("\"" + effectiveName + "\":");
            }
            else {
                this.processingStack.get(this.depth).writer.write(this.processingStack.get(this.depth).lastIsArray ? "," : "[");
                this.processingStack.get(this.depth).lastIsArray = true;
                this.processingStack.get(this.depth).writer.write(this.processingStack.get(this.depth).lastElementWriter.getContent());
            }
            ++this.depth;
            this.processingStack.add(this.depth, this.createProcessingState());
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
    
    @Override
    public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
        this.writeStartElement(prefix, "@" + this.getEffectiveName(namespaceURI, localName), null);
        this.writeCharacters(value);
        this.writeEndElement();
    }
    
    private ProcessingState createProcessingState() {
        switch (this.depth) {
            case 0: {
                return new ProcessingState(this.stripRoot ? new DummyWriterAdapter() : new WriterAdapter(this.mainWriter));
            }
            case 1: {
                return this.stripRoot ? new ProcessingState(new WriterAdapter(this.mainWriter)) : new ProcessingState();
            }
            default: {
                return new ProcessingState();
            }
        }
    }
    
    private static class WriterAdapter
    {
        Writer writer;
        boolean isEmpty;
        
        WriterAdapter() {
            this.isEmpty = true;
        }
        
        WriterAdapter(final Writer w) {
            this.isEmpty = true;
            this.writer = w;
        }
        
        void write(final String s) throws IOException {
            assert null != this.writer;
            this.writer.write(s);
            this.isEmpty = false;
        }
        
        String getContent() {
            return null;
        }
    }
    
    private static final class StringWriterAdapter extends WriterAdapter
    {
        StringWriterAdapter() {
            this.writer = new StringWriter();
        }
        
        @Override
        String getContent() {
            return this.writer.toString();
        }
    }
    
    private static final class DummyWriterAdapter extends WriterAdapter
    {
        DummyWriterAdapter() {
        }
        
        @Override
        void write(final String s) throws IOException {
        }
        
        @Override
        String getContent() {
            return null;
        }
    }
    
    private static final class ProcessingState
    {
        String lastName;
        String currentName;
        WriterAdapter lastElementWriter;
        Boolean lastWasPrimitive;
        boolean lastIsArray;
        boolean isNotEmpty;
        WriterAdapter writer;
        
        ProcessingState() {
            this.isNotEmpty = false;
            this.writer = new StringWriterAdapter();
        }
        
        ProcessingState(final WriterAdapter w) {
            this.isNotEmpty = false;
            this.writer = w;
        }
        
        @Override
        public String toString() {
            return String.format("{currentName:%s, writer: \"%s\", lastName:%s, lastWriter: %s}", this.currentName, (this.writer != null) ? this.writer.getContent() : null, this.lastName, (this.lastElementWriter != null) ? this.lastElementWriter.getContent() : null);
        }
    }
}
