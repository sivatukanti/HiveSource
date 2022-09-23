// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.core.JsonGenerator;
import java.util.Date;
import org.apache.log4j.spi.ThrowableInformation;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.ISO8601DateFormat;
import java.text.DateFormat;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.core.JsonFactory;
import org.apache.log4j.Layout;

public class Log4Json extends Layout
{
    private static final JsonFactory factory;
    private static final ObjectReader READER;
    public static final String DATE = "date";
    public static final String EXCEPTION_CLASS = "exceptionclass";
    public static final String LEVEL = "level";
    public static final String MESSAGE = "message";
    public static final String NAME = "name";
    public static final String STACK = "stack";
    public static final String THREAD = "thread";
    public static final String TIME = "time";
    public static final String JSON_TYPE = "application/json";
    private final DateFormat dateFormat;
    
    public Log4Json() {
        this.dateFormat = new ISO8601DateFormat();
    }
    
    @Override
    public String getContentType() {
        return "application/json";
    }
    
    @Override
    public String format(final LoggingEvent event) {
        try {
            return this.toJson(event);
        }
        catch (IOException e) {
            return "{ \"logfailure\":\"" + e.getClass().toString() + "\"}";
        }
    }
    
    public String toJson(final LoggingEvent event) throws IOException {
        final StringWriter writer = new StringWriter();
        this.toJson(writer, event);
        return writer.toString();
    }
    
    public Writer toJson(final Writer writer, final LoggingEvent event) throws IOException {
        final ThrowableInformation ti = event.getThrowableInformation();
        this.toJson(writer, event.getLoggerName(), event.getTimeStamp(), event.getLevel().toString(), event.getThreadName(), event.getRenderedMessage(), ti);
        return writer;
    }
    
    public Writer toJson(final Writer writer, final String loggerName, final long timeStamp, final String level, final String threadName, final String message, final ThrowableInformation ti) throws IOException {
        final JsonGenerator json = Log4Json.factory.createGenerator(writer);
        json.writeStartObject();
        json.writeStringField("name", loggerName);
        json.writeNumberField("time", timeStamp);
        final Date date = new Date(timeStamp);
        json.writeStringField("date", this.dateFormat.format(date));
        json.writeStringField("level", level);
        json.writeStringField("thread", threadName);
        json.writeStringField("message", message);
        if (ti != null) {
            final Throwable thrown = ti.getThrowable();
            final String eclass = (thrown != null) ? thrown.getClass().getName() : "";
            json.writeStringField("exceptionclass", eclass);
            final String[] stackTrace = ti.getThrowableStrRep();
            json.writeArrayFieldStart("stack");
            for (final String row : stackTrace) {
                json.writeString(row);
            }
            json.writeEndArray();
        }
        json.writeEndObject();
        json.flush();
        json.close();
        return writer;
    }
    
    @Override
    public boolean ignoresThrowable() {
        return false;
    }
    
    @Override
    public void activateOptions() {
    }
    
    public static ContainerNode parse(final String json) throws IOException {
        final JsonNode jsonNode = Log4Json.READER.readTree(json);
        if (!(jsonNode instanceof ContainerNode)) {
            throw new IOException("Wrong JSON data: " + json);
        }
        return (ContainerNode)jsonNode;
    }
    
    static {
        factory = new MappingJsonFactory();
        READER = new ObjectMapper(Log4Json.factory).reader();
    }
}
