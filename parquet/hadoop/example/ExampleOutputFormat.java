// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.example;

import org.apache.hadoop.mapreduce.JobContext;
import parquet.hadoop.util.ContextUtil;
import parquet.schema.MessageType;
import org.apache.hadoop.mapreduce.Job;
import parquet.example.data.Group;
import parquet.hadoop.ParquetOutputFormat;

public class ExampleOutputFormat extends ParquetOutputFormat<Group>
{
    public static void setSchema(final Job job, final MessageType schema) {
        GroupWriteSupport.setSchema(schema, ContextUtil.getConfiguration((JobContext)job));
    }
    
    public static MessageType getSchema(final Job job) {
        return GroupWriteSupport.getSchema(ContextUtil.getConfiguration((JobContext)job));
    }
    
    public ExampleOutputFormat() {
        super(new GroupWriteSupport());
    }
}
