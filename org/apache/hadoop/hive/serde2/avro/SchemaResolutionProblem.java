// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import org.apache.avro.Schema;

class SchemaResolutionProblem
{
    static final String sentinelString = "{\n    \"namespace\": \"org.apache.hadoop.hive\",\n    \"name\": \"CannotDetermineSchemaSentinel\",\n    \"type\": \"record\",\n    \"fields\": [\n        {\n            \"name\":\"ERROR_ERROR_ERROR_ERROR_ERROR_ERROR_ERROR\",\n            \"type\":\"string\"\n        },\n        {\n            \"name\":\"Cannot_determine_schema\",\n            \"type\":\"string\"\n        },\n        {\n            \"name\":\"check\",\n            \"type\":\"string\"\n        },\n        {\n            \"name\":\"schema\",\n            \"type\":\"string\"\n        },\n        {\n            \"name\":\"url\",\n            \"type\":\"string\"\n        },\n        {\n            \"name\":\"and\",\n            \"type\":\"string\"\n        },\n        {\n            \"name\":\"literal\",\n            \"type\":\"string\"\n        }\n    ]\n}";
    public static final Schema SIGNAL_BAD_SCHEMA;
    
    static {
        SIGNAL_BAD_SCHEMA = AvroSerdeUtils.getSchemaFor("{\n    \"namespace\": \"org.apache.hadoop.hive\",\n    \"name\": \"CannotDetermineSchemaSentinel\",\n    \"type\": \"record\",\n    \"fields\": [\n        {\n            \"name\":\"ERROR_ERROR_ERROR_ERROR_ERROR_ERROR_ERROR\",\n            \"type\":\"string\"\n        },\n        {\n            \"name\":\"Cannot_determine_schema\",\n            \"type\":\"string\"\n        },\n        {\n            \"name\":\"check\",\n            \"type\":\"string\"\n        },\n        {\n            \"name\":\"schema\",\n            \"type\":\"string\"\n        },\n        {\n            \"name\":\"url\",\n            \"type\":\"string\"\n        },\n        {\n            \"name\":\"and\",\n            \"type\":\"string\"\n        },\n        {\n            \"name\":\"literal\",\n            \"type\":\"string\"\n        }\n    ]\n}");
    }
}
