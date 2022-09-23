// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.example;

import parquet.example.data.Group;
import parquet.hadoop.ParquetInputFormat;

public class ExampleInputFormat extends ParquetInputFormat<Group>
{
    public ExampleInputFormat() {
        super(GroupReadSupport.class);
    }
}
