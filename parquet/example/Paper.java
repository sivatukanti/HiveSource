// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example;

import parquet.example.data.Group;
import parquet.schema.GroupType;
import parquet.schema.PrimitiveType;
import parquet.schema.Type;
import parquet.example.data.simple.SimpleGroup;
import parquet.schema.MessageType;

public class Paper
{
    public static final MessageType schema;
    public static final MessageType schema2;
    public static final MessageType schema3;
    public static final SimpleGroup r1;
    public static final SimpleGroup r2;
    public static final SimpleGroup pr1;
    public static final SimpleGroup pr2;
    
    static {
        schema = new MessageType("Document", new Type[] { new PrimitiveType(Type.Repetition.REQUIRED, PrimitiveType.PrimitiveTypeName.INT64, "DocId"), new GroupType(Type.Repetition.OPTIONAL, "Links", new Type[] { new PrimitiveType(Type.Repetition.REPEATED, PrimitiveType.PrimitiveTypeName.INT64, "Backward"), new PrimitiveType(Type.Repetition.REPEATED, PrimitiveType.PrimitiveTypeName.INT64, "Forward") }), new GroupType(Type.Repetition.REPEATED, "Name", new Type[] { new GroupType(Type.Repetition.REPEATED, "Language", new Type[] { new PrimitiveType(Type.Repetition.REQUIRED, PrimitiveType.PrimitiveTypeName.BINARY, "Code"), new PrimitiveType(Type.Repetition.OPTIONAL, PrimitiveType.PrimitiveTypeName.BINARY, "Country") }), new PrimitiveType(Type.Repetition.OPTIONAL, PrimitiveType.PrimitiveTypeName.BINARY, "Url") }) });
        schema2 = new MessageType("Document", new Type[] { new PrimitiveType(Type.Repetition.REQUIRED, PrimitiveType.PrimitiveTypeName.INT64, "DocId"), new GroupType(Type.Repetition.REPEATED, "Name", new Type[] { new GroupType(Type.Repetition.REPEATED, "Language", new Type[] { new PrimitiveType(Type.Repetition.OPTIONAL, PrimitiveType.PrimitiveTypeName.BINARY, "Country") }) }) });
        schema3 = new MessageType("Document", new Type[] { new PrimitiveType(Type.Repetition.REQUIRED, PrimitiveType.PrimitiveTypeName.INT64, "DocId"), new GroupType(Type.Repetition.OPTIONAL, "Links", new Type[] { new PrimitiveType(Type.Repetition.REPEATED, PrimitiveType.PrimitiveTypeName.INT64, "Backward"), new PrimitiveType(Type.Repetition.REPEATED, PrimitiveType.PrimitiveTypeName.INT64, "Forward") }) });
        r1 = new SimpleGroup(Paper.schema);
        r2 = new SimpleGroup(Paper.schema);
        Paper.r1.add("DocId", 10L);
        Paper.r1.addGroup("Links").append("Forward", 20L).append("Forward", 40L).append("Forward", 60L);
        Group name = Paper.r1.addGroup("Name");
        name.addGroup("Language").append("Code", "en-us").append("Country", "us");
        name.addGroup("Language").append("Code", "en");
        name.append("Url", "http://A");
        name = Paper.r1.addGroup("Name");
        name.append("Url", "http://B");
        name = Paper.r1.addGroup("Name");
        name.addGroup("Language").append("Code", "en-gb").append("Country", "gb");
        Paper.r2.add("DocId", 20L);
        Paper.r2.addGroup("Links").append("Backward", 10L).append("Backward", 30L).append("Forward", 80L);
        Paper.r2.addGroup("Name").append("Url", "http://C");
        pr1 = new SimpleGroup(Paper.schema2);
        pr2 = new SimpleGroup(Paper.schema2);
        Paper.pr1.add("DocId", 10L);
        name = Paper.pr1.addGroup("Name");
        name.addGroup("Language").append("Country", "us");
        name.addGroup("Language");
        name = Paper.pr1.addGroup("Name");
        name = Paper.pr1.addGroup("Name");
        name.addGroup("Language").append("Country", "gb");
        Paper.pr2.add("DocId", 20L);
        Paper.pr2.addGroup("Name");
    }
}
