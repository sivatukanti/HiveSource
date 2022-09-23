// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Arrays;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import java.util.List;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = SpanSerializer.class)
public interface Span
{
    void stop();
    
    long getStartTimeMillis();
    
    long getStopTimeMillis();
    
    long getAccumulatedMillis();
    
    boolean isRunning();
    
    String getDescription();
    
    SpanId getSpanId();
    
    @Deprecated
    Span child(final String p0);
    
    String toString();
    
    SpanId[] getParents();
    
    void setParents(final SpanId[] p0);
    
    void addKVAnnotation(final String p0, final String p1);
    
    void addTimelineAnnotation(final String p0);
    
    Map<String, String> getKVAnnotations();
    
    List<TimelineAnnotation> getTimelineAnnotations();
    
    String getTracerId();
    
    void setTracerId(final String p0);
    
    String toJson();
    
    public static class SpanSerializer extends JsonSerializer<Span>
    {
        @Override
        public void serialize(final Span span, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
            jgen.writeStartObject();
            if (span.getSpanId().isValid()) {
                jgen.writeStringField("a", span.getSpanId().toString());
            }
            if (span.getStartTimeMillis() != 0L) {
                jgen.writeNumberField("b", span.getStartTimeMillis());
            }
            if (span.getStopTimeMillis() != 0L) {
                jgen.writeNumberField("e", span.getStopTimeMillis());
            }
            if (!span.getDescription().isEmpty()) {
                jgen.writeStringField("d", span.getDescription());
            }
            final String tracerId = span.getTracerId();
            if (!tracerId.isEmpty()) {
                jgen.writeStringField("r", tracerId);
            }
            jgen.writeArrayFieldStart("p");
            for (final SpanId parent : span.getParents()) {
                jgen.writeString(parent.toString());
            }
            jgen.writeEndArray();
            final Map<String, String> traceInfoMap = span.getKVAnnotations();
            if (!traceInfoMap.isEmpty()) {
                jgen.writeObjectFieldStart("n");
                final String[] keys = traceInfoMap.keySet().toArray(new String[traceInfoMap.size()]);
                Arrays.sort(keys);
                for (final String key : keys) {
                    jgen.writeStringField(key, traceInfoMap.get(key));
                }
                jgen.writeEndObject();
            }
            final List<TimelineAnnotation> timelineAnnotations = span.getTimelineAnnotations();
            if (!timelineAnnotations.isEmpty()) {
                jgen.writeArrayFieldStart("t");
                for (final TimelineAnnotation tl : timelineAnnotations) {
                    jgen.writeStartObject();
                    jgen.writeNumberField("t", tl.getTime());
                    jgen.writeStringField("m", tl.getMessage());
                    jgen.writeEndObject();
                }
                jgen.writeEndArray();
            }
            jgen.writeEndObject();
        }
    }
}
