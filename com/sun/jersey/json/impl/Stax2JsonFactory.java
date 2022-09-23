// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import java.io.BufferedReader;
import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONTokener;
import com.sun.jersey.core.util.ReaderWriter;
import com.sun.jersey.json.impl.reader.JsonXmlStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import java.util.Map;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter;
import com.sun.jersey.json.impl.writer.JsonXmlStreamWriter;
import com.sun.jersey.json.impl.writer.Stax2JacksonWriter;
import com.sun.jersey.json.impl.writer.JacksonRootStrippingGenerator;
import com.sun.jersey.json.impl.writer.JacksonArrayWrapperGenerator;
import org.codehaus.jackson.JsonFactory;
import com.sun.jersey.api.json.JSONJAXBContext;
import java.io.IOException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.bind.JAXBContext;
import com.sun.jersey.api.json.JSONConfiguration;
import java.io.Writer;

public class Stax2JsonFactory
{
    private Stax2JsonFactory() {
    }
    
    public static XMLStreamWriter createWriter(final Writer writer, final JSONConfiguration config, final Class<?> expectedType, final JAXBContext jaxbContext) throws IOException {
        return createWriter(writer, config, expectedType, jaxbContext, false);
    }
    
    public static XMLStreamWriter createWriter(final Writer writer, final JSONConfiguration config, final Class<?> expectedType, JAXBContext jaxbContext, final boolean writingList) throws IOException {
        if (jaxbContext instanceof JSONJAXBContext) {
            jaxbContext = ((JSONJAXBContext)jaxbContext).getOriginalJaxbContext();
        }
        switch (config.getNotation()) {
            case NATURAL: {
                final JsonGenerator rawGenerator = new JsonFactory().createJsonGenerator(writer);
                if (config.isHumanReadableFormatting()) {
                    rawGenerator.useDefaultPrettyPrinter();
                }
                final JsonGenerator bodyGenerator = writingList ? JacksonArrayWrapperGenerator.createArrayWrapperGenerator(rawGenerator, config.isRootUnwrapping() ? 0 : 1) : rawGenerator;
                if (config.isRootUnwrapping()) {
                    return new Stax2JacksonWriter(JacksonRootStrippingGenerator.createRootStrippingGenerator(bodyGenerator, writingList ? 2 : 1), config, expectedType, jaxbContext);
                }
                return new Stax2JacksonWriter(bodyGenerator, config, expectedType, jaxbContext);
            }
            case MAPPED: {
                return JsonXmlStreamWriter.createWriter(writer, config, JSONHelper.getRootElementName((Class<Object>)expectedType));
            }
            case BADGERFISH: {
                return new BadgerFishXMLStreamWriter(writer);
            }
            case MAPPED_JETTISON: {
                Configuration jmConfig;
                if (null == config.getXml2JsonNs()) {
                    jmConfig = new Configuration();
                }
                else {
                    jmConfig = new Configuration(config.getXml2JsonNs());
                }
                return new MappedXMLStreamWriter(new MappedNamespaceConvention(jmConfig), writer);
            }
            default: {
                return null;
            }
        }
    }
    
    public static XMLStreamReader createReader(final Reader reader, final JSONConfiguration config, final String rootName, final Class<?> expectedType, final JAXBContext jaxbContext) throws XMLStreamException {
        return createReader(reader, config, rootName, expectedType, jaxbContext, false);
    }
    
    public static XMLStreamReader createReader(final Reader reader, final JSONConfiguration config, final String rootName, final Class<?> expectedType, final JAXBContext jaxbContext, final boolean readingList) throws XMLStreamException {
        final Reader nonEmptyReader = ensureNonEmptyReader(reader);
        switch (config.getNotation()) {
            case NATURAL:
            case MAPPED: {
                return JsonXmlStreamReader.create(nonEmptyReader, config, rootName, expectedType, jaxbContext, readingList);
            }
            case MAPPED_JETTISON: {
                try {
                    Configuration jmConfig;
                    if (null == config.getXml2JsonNs()) {
                        jmConfig = new Configuration();
                    }
                    else {
                        jmConfig = new Configuration(config.getXml2JsonNs());
                    }
                    return new MappedXMLStreamReader(new JSONObject(new JSONTokener(ReaderWriter.readFromAsString(nonEmptyReader))), new MappedNamespaceConvention(jmConfig));
                }
                catch (Exception ex) {
                    throw new XMLStreamException(ex);
                }
            }
            case BADGERFISH: {
                try {
                    return new BadgerFishXMLStreamReader(new JSONObject(new JSONTokener(ReaderWriter.readFromAsString(nonEmptyReader))));
                }
                catch (Exception ex) {
                    throw new XMLStreamException(ex);
                }
                break;
            }
        }
        throw new IllegalArgumentException("Unknown JSON config");
    }
    
    private static Reader ensureNonEmptyReader(final Reader reader) throws XMLStreamException {
        try {
            final Reader mr = reader.markSupported() ? reader : new BufferedReader(reader);
            mr.mark(1);
            if (mr.read() == -1) {
                throw new XMLStreamException("JSON expression can not be empty!");
            }
            mr.reset();
            return mr;
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
}
