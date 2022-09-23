// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.core;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ObjectWriter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ObjectReader;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ObjectMapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MilliSpanDeserializer.class)
public class MilliSpan implements Span
{
    private static ObjectMapper OBJECT_MAPPER;
    private static ObjectReader JSON_READER;
    private static ObjectWriter JSON_WRITER;
    private static final SpanId[] EMPTY_PARENT_ARRAY;
    private static final String EMPTY_STRING = "";
    private long begin;
    private long end;
    private final String description;
    private SpanId[] parents;
    private final SpanId spanId;
    private Map<String, String> traceInfo;
    private String tracerId;
    private List<TimelineAnnotation> timeline;
    
    @Override
    public Span child(final String childDescription) {
        return new Builder().begin(System.currentTimeMillis()).end(0L).description(childDescription).parents(new SpanId[] { this.spanId }).spanId(this.spanId.newChildId()).tracerId(this.tracerId).build();
    }
    
    public MilliSpan() {
        this.traceInfo = null;
        this.timeline = null;
        this.begin = 0L;
        this.end = 0L;
        this.description = "";
        this.parents = MilliSpan.EMPTY_PARENT_ARRAY;
        this.spanId = SpanId.INVALID;
        this.traceInfo = null;
        this.tracerId = "";
        this.timeline = null;
    }
    
    private MilliSpan(final Builder builder) {
        this.traceInfo = null;
        this.timeline = null;
        this.begin = builder.begin;
        this.end = builder.end;
        this.description = builder.description;
        this.parents = builder.parents;
        this.spanId = builder.spanId;
        this.traceInfo = builder.traceInfo;
        this.tracerId = builder.tracerId;
        this.timeline = builder.timeline;
    }
    
    @Override
    public synchronized void stop() {
        if (this.end == 0L) {
            if (this.begin == 0L) {
                throw new IllegalStateException("Span for " + this.description + " has not been started");
            }
            this.end = System.currentTimeMillis();
        }
    }
    
    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }
    
    @Override
    public synchronized boolean isRunning() {
        return this.begin != 0L && this.end == 0L;
    }
    
    @Override
    public synchronized long getAccumulatedMillis() {
        if (this.begin == 0L) {
            return 0L;
        }
        if (this.end > 0L) {
            return this.end - this.begin;
        }
        return this.currentTimeMillis() - this.begin;
    }
    
    @Override
    public String toString() {
        return this.toJson();
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public SpanId getSpanId() {
        return this.spanId;
    }
    
    @Override
    public SpanId[] getParents() {
        return this.parents;
    }
    
    @Override
    public void setParents(final SpanId[] parents) {
        this.parents = parents;
    }
    
    @Override
    public long getStartTimeMillis() {
        return this.begin;
    }
    
    @Override
    public long getStopTimeMillis() {
        return this.end;
    }
    
    @Override
    public void addKVAnnotation(final String key, final String value) {
        if (this.traceInfo == null) {
            this.traceInfo = new HashMap<String, String>();
        }
        this.traceInfo.put(key, value);
    }
    
    @Override
    public void addTimelineAnnotation(final String msg) {
        if (this.timeline == null) {
            this.timeline = new ArrayList<TimelineAnnotation>();
        }
        this.timeline.add(new TimelineAnnotation(System.currentTimeMillis(), msg));
    }
    
    @Override
    public Map<String, String> getKVAnnotations() {
        if (this.traceInfo == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)this.traceInfo);
    }
    
    @Override
    public List<TimelineAnnotation> getTimelineAnnotations() {
        if (this.timeline == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends TimelineAnnotation>)this.timeline);
    }
    
    @Override
    public String getTracerId() {
        return this.tracerId;
    }
    
    @Override
    public void setTracerId(final String tracerId) {
        this.tracerId = tracerId;
    }
    
    @Override
    public String toJson() {
        final StringWriter writer = new StringWriter();
        try {
            MilliSpan.JSON_WRITER.writeValue(writer, this);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }
    
    public static MilliSpan fromJson(final String json) throws IOException {
        return MilliSpan.JSON_READER.readValue(json);
    }
    
    static {
        MilliSpan.OBJECT_MAPPER = new ObjectMapper();
        MilliSpan.JSON_READER = MilliSpan.OBJECT_MAPPER.reader(MilliSpan.class);
        MilliSpan.JSON_WRITER = MilliSpan.OBJECT_MAPPER.writer();
        EMPTY_PARENT_ARRAY = new SpanId[0];
    }
    
    public static class Builder
    {
        private long begin;
        private long end;
        private String description;
        private SpanId[] parents;
        private SpanId spanId;
        private Map<String, String> traceInfo;
        private String tracerId;
        private List<TimelineAnnotation> timeline;
        
        public Builder() {
            this.description = "";
            this.parents = MilliSpan.EMPTY_PARENT_ARRAY;
            this.spanId = SpanId.INVALID;
            this.traceInfo = null;
            this.tracerId = "";
            this.timeline = null;
        }
        
        public Builder begin(final long begin) {
            this.begin = begin;
            return this;
        }
        
        public Builder end(final long end) {
            this.end = end;
            return this;
        }
        
        public Builder description(final String description) {
            this.description = description;
            return this;
        }
        
        public Builder parents(final SpanId[] parents) {
            this.parents = parents;
            return this;
        }
        
        public Builder parents(final List<SpanId> parentList) {
            final SpanId[] parents = new SpanId[parentList.size()];
            for (int i = 0; i < parentList.size(); ++i) {
                parents[i] = parentList.get(i);
            }
            this.parents = parents;
            return this;
        }
        
        public Builder spanId(final SpanId spanId) {
            this.spanId = spanId;
            return this;
        }
        
        public Builder traceInfo(final Map<String, String> traceInfo) {
            this.traceInfo = (traceInfo.isEmpty() ? null : traceInfo);
            return this;
        }
        
        public Builder tracerId(final String tracerId) {
            this.tracerId = tracerId;
            return this;
        }
        
        public Builder timeline(final List<TimelineAnnotation> timeline) {
            this.timeline = (timeline.isEmpty() ? null : timeline);
            return this;
        }
        
        public MilliSpan build() {
            return new MilliSpan(this, null);
        }
    }
    
    public static class MilliSpanDeserializer extends JsonDeserializer<MilliSpan>
    {
        @Override
        public MilliSpan deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            final JsonNode root = jp.getCodec().readTree(jp);
            final Builder builder = new Builder();
            final JsonNode bNode = root.get("b");
            if (bNode != null) {
                builder.begin(bNode.asLong());
            }
            final JsonNode eNode = root.get("e");
            if (eNode != null) {
                builder.end(eNode.asLong());
            }
            final JsonNode dNode = root.get("d");
            if (dNode != null) {
                builder.description(dNode.asText());
            }
            final JsonNode sNode = root.get("a");
            if (sNode != null) {
                builder.spanId(SpanId.fromString(sNode.asText()));
            }
            final JsonNode rNode = root.get("r");
            if (rNode != null) {
                builder.tracerId(rNode.asText());
            }
            final JsonNode parentsNode = root.get("p");
            final LinkedList<SpanId> parents = new LinkedList<SpanId>();
            if (parentsNode != null) {
                final Iterator<JsonNode> iter = parentsNode.elements();
                while (iter.hasNext()) {
                    final JsonNode parentIdNode = iter.next();
                    parents.add(SpanId.fromString(parentIdNode.asText()));
                }
            }
            builder.parents(parents);
            final JsonNode traceInfoNode = root.get("n");
            if (traceInfoNode != null) {
                final HashMap<String, String> traceInfo = new HashMap<String, String>();
                final Iterator<String> iter2 = traceInfoNode.fieldNames();
                while (iter2.hasNext()) {
                    final String field = iter2.next();
                    traceInfo.put(field, traceInfoNode.get(field).asText());
                }
                builder.traceInfo(traceInfo);
            }
            final JsonNode timelineNode = root.get("t");
            if (timelineNode != null) {
                final LinkedList<TimelineAnnotation> timeline = new LinkedList<TimelineAnnotation>();
                final Iterator<JsonNode> iter3 = timelineNode.elements();
                while (iter3.hasNext()) {
                    final JsonNode ann = iter3.next();
                    timeline.add(new TimelineAnnotation(ann.get("t").asLong(), ann.get("m").asText()));
                }
                builder.timeline(timeline);
            }
            return builder.build();
        }
    }
}
