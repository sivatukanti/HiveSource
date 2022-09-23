// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

import java.util.Collection;
import org.apache.avro.generic.GenericRecord;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import org.codehaus.jackson.node.TextNode;
import java.util.Map;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.avro.generic.GenericData;
import org.codehaus.jackson.io.JsonStringEncoder;
import java.nio.ByteBuffer;
import org.codehaus.jackson.JsonNode;

public class SchemaBuilder
{
    private static final Schema NULL_SCHEMA;
    
    private SchemaBuilder() {
    }
    
    public static TypeBuilder<Schema> builder() {
        return new TypeBuilder<Schema>((Completion)new SchemaCompletion(), new NameContext());
    }
    
    public static TypeBuilder<Schema> builder(final String namespace) {
        return new TypeBuilder<Schema>((Completion)new SchemaCompletion(), new NameContext().namespace(namespace));
    }
    
    public static RecordBuilder<Schema> record(final String name) {
        return builder().record(name);
    }
    
    public static EnumBuilder<Schema> enumeration(final String name) {
        return builder().enumeration(name);
    }
    
    public static FixedBuilder<Schema> fixed(final String name) {
        return builder().fixed(name);
    }
    
    public static ArrayBuilder<Schema> array() {
        return builder().array();
    }
    
    public static MapBuilder<Schema> map() {
        return builder().map();
    }
    
    public static BaseTypeBuilder<UnionAccumulator<Schema>> unionOf() {
        return builder().unionOf();
    }
    
    public static BaseTypeBuilder<Schema> nullable() {
        return builder().nullable();
    }
    
    private static void checkRequired(final Object reference, final String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage);
        }
    }
    
    private static JsonNode toJsonNode(final Object o) {
        try {
            String s;
            if (o instanceof ByteBuffer) {
                final ByteBuffer bytes = (ByteBuffer)o;
                bytes.mark();
                final byte[] data = new byte[bytes.remaining()];
                bytes.get(data);
                bytes.reset();
                s = new String(data, "ISO-8859-1");
                final char[] quoted = JsonStringEncoder.getInstance().quoteAsString(s);
                s = "\"" + new String(quoted) + "\"";
            }
            else {
                s = GenericData.get().toString(o);
            }
            return new ObjectMapper().readTree(s);
        }
        catch (IOException e) {
            throw new SchemaBuilderException(e);
        }
    }
    
    static {
        NULL_SCHEMA = Schema.create(Schema.Type.NULL);
    }
    
    public abstract static class PropBuilder<S extends PropBuilder<S>>
    {
        private Map<String, JsonNode> props;
        
        protected PropBuilder() {
            this.props = null;
        }
        
        public final S prop(final String name, final String val) {
            return this.prop(name, TextNode.valueOf(val));
        }
        
        final S prop(final String name, final JsonNode val) {
            if (!this.hasProps()) {
                this.props = new HashMap<String, JsonNode>();
            }
            this.props.put(name, val);
            return this.self();
        }
        
        private boolean hasProps() {
            return this.props != null;
        }
        
        final <T extends JsonProperties> T addPropsTo(final T jsonable) {
            if (this.hasProps()) {
                for (final Map.Entry<String, JsonNode> prop : this.props.entrySet()) {
                    jsonable.addProp(prop.getKey(), prop.getValue());
                }
            }
            return jsonable;
        }
        
        protected abstract S self();
    }
    
    public abstract static class NamedBuilder<S extends NamedBuilder<S>> extends PropBuilder<S>
    {
        private final String name;
        private final NameContext names;
        private String doc;
        private String[] aliases;
        
        protected NamedBuilder(final NameContext names, final String name) {
            checkRequired(name, "Type must have a name");
            this.names = names;
            this.name = name;
        }
        
        public final S doc(final String doc) {
            this.doc = doc;
            return this.self();
        }
        
        public final S aliases(final String... aliases) {
            this.aliases = aliases;
            return this.self();
        }
        
        final String doc() {
            return this.doc;
        }
        
        final String name() {
            return this.name;
        }
        
        final NameContext names() {
            return this.names;
        }
        
        final Schema addAliasesTo(final Schema schema) {
            if (null != this.aliases) {
                for (final String alias : this.aliases) {
                    schema.addAlias(alias);
                }
            }
            return schema;
        }
        
        final Schema.Field addAliasesTo(final Schema.Field field) {
            if (null != this.aliases) {
                for (final String alias : this.aliases) {
                    field.addAlias(alias);
                }
            }
            return field;
        }
    }
    
    public abstract static class NamespacedBuilder<R, S extends NamespacedBuilder<R, S>> extends NamedBuilder<S>
    {
        private final Completion<R> context;
        private String namespace;
        
        protected NamespacedBuilder(final Completion<R> context, final NameContext names, final String name) {
            super(names, name);
            this.context = context;
        }
        
        public final S namespace(final String namespace) {
            this.namespace = namespace;
            return this.self();
        }
        
        final String space() {
            if (null == this.namespace) {
                return this.names().namespace;
            }
            return this.namespace;
        }
        
        final Schema completeSchema(final Schema schema) {
            this.addPropsTo(schema);
            this.addAliasesTo(schema);
            this.names().put(schema);
            return schema;
        }
        
        final Completion<R> context() {
            return this.context;
        }
    }
    
    private abstract static class PrimitiveBuilder<R, P extends PrimitiveBuilder<R, P>> extends PropBuilder<P>
    {
        private final Completion<R> context;
        private final Schema immutable;
        
        protected PrimitiveBuilder(final Completion<R> context, final NameContext names, final Schema.Type type) {
            this.context = context;
            this.immutable = names.getFullname(type.getName());
        }
        
        private R end() {
            Schema schema = this.immutable;
            if (((PropBuilder<PropBuilder>)this).hasProps()) {
                schema = Schema.create(this.immutable.getType());
                this.addPropsTo(schema);
            }
            return this.context.complete(schema);
        }
    }
    
    public static final class BooleanBuilder<R> extends PrimitiveBuilder<R, BooleanBuilder<R>>
    {
        private BooleanBuilder(final Completion<R> context, final NameContext names) {
            super(context, names, Schema.Type.BOOLEAN);
        }
        
        private static <R> BooleanBuilder<R> create(final Completion<R> context, final NameContext names) {
            return new BooleanBuilder<R>(context, names);
        }
        
        @Override
        protected BooleanBuilder<R> self() {
            return this;
        }
        
        public R endBoolean() {
            return (R)((PrimitiveBuilder<Object, PrimitiveBuilder>)this).end();
        }
    }
    
    public static final class IntBuilder<R> extends PrimitiveBuilder<R, IntBuilder<R>>
    {
        private IntBuilder(final Completion<R> context, final NameContext names) {
            super(context, names, Schema.Type.INT);
        }
        
        private static <R> IntBuilder<R> create(final Completion<R> context, final NameContext names) {
            return new IntBuilder<R>(context, names);
        }
        
        @Override
        protected IntBuilder<R> self() {
            return this;
        }
        
        public R endInt() {
            return (R)((PrimitiveBuilder<Object, PrimitiveBuilder>)this).end();
        }
    }
    
    public static final class LongBuilder<R> extends PrimitiveBuilder<R, LongBuilder<R>>
    {
        private LongBuilder(final Completion<R> context, final NameContext names) {
            super(context, names, Schema.Type.LONG);
        }
        
        private static <R> LongBuilder<R> create(final Completion<R> context, final NameContext names) {
            return new LongBuilder<R>(context, names);
        }
        
        @Override
        protected LongBuilder<R> self() {
            return this;
        }
        
        public R endLong() {
            return (R)((PrimitiveBuilder<Object, PrimitiveBuilder>)this).end();
        }
    }
    
    public static final class FloatBuilder<R> extends PrimitiveBuilder<R, FloatBuilder<R>>
    {
        private FloatBuilder(final Completion<R> context, final NameContext names) {
            super(context, names, Schema.Type.FLOAT);
        }
        
        private static <R> FloatBuilder<R> create(final Completion<R> context, final NameContext names) {
            return new FloatBuilder<R>(context, names);
        }
        
        @Override
        protected FloatBuilder<R> self() {
            return this;
        }
        
        public R endFloat() {
            return (R)((PrimitiveBuilder<Object, PrimitiveBuilder>)this).end();
        }
    }
    
    public static final class DoubleBuilder<R> extends PrimitiveBuilder<R, DoubleBuilder<R>>
    {
        private DoubleBuilder(final Completion<R> context, final NameContext names) {
            super(context, names, Schema.Type.DOUBLE);
        }
        
        private static <R> DoubleBuilder<R> create(final Completion<R> context, final NameContext names) {
            return new DoubleBuilder<R>(context, names);
        }
        
        @Override
        protected DoubleBuilder<R> self() {
            return this;
        }
        
        public R endDouble() {
            return (R)((PrimitiveBuilder<Object, PrimitiveBuilder>)this).end();
        }
    }
    
    public static final class StringBldr<R> extends PrimitiveBuilder<R, StringBldr<R>>
    {
        private StringBldr(final Completion<R> context, final NameContext names) {
            super(context, names, Schema.Type.STRING);
        }
        
        private static <R> StringBldr<R> create(final Completion<R> context, final NameContext names) {
            return new StringBldr<R>(context, names);
        }
        
        @Override
        protected StringBldr<R> self() {
            return this;
        }
        
        public R endString() {
            return (R)((PrimitiveBuilder<Object, PrimitiveBuilder>)this).end();
        }
    }
    
    public static final class BytesBuilder<R> extends PrimitiveBuilder<R, BytesBuilder<R>>
    {
        private BytesBuilder(final Completion<R> context, final NameContext names) {
            super(context, names, Schema.Type.BYTES);
        }
        
        private static <R> BytesBuilder<R> create(final Completion<R> context, final NameContext names) {
            return new BytesBuilder<R>(context, names);
        }
        
        @Override
        protected BytesBuilder<R> self() {
            return this;
        }
        
        public R endBytes() {
            return (R)((PrimitiveBuilder<Object, PrimitiveBuilder>)this).end();
        }
    }
    
    public static final class NullBuilder<R> extends PrimitiveBuilder<R, NullBuilder<R>>
    {
        private NullBuilder(final Completion<R> context, final NameContext names) {
            super(context, names, Schema.Type.NULL);
        }
        
        private static <R> NullBuilder<R> create(final Completion<R> context, final NameContext names) {
            return new NullBuilder<R>(context, names);
        }
        
        @Override
        protected NullBuilder<R> self() {
            return this;
        }
        
        public R endNull() {
            return (R)((PrimitiveBuilder<Object, PrimitiveBuilder>)this).end();
        }
    }
    
    public static final class FixedBuilder<R> extends NamespacedBuilder<R, FixedBuilder<R>>
    {
        private FixedBuilder(final Completion<R> context, final NameContext names, final String name) {
            super(context, names, name);
        }
        
        private static <R> FixedBuilder<R> create(final Completion<R> context, final NameContext names, final String name) {
            return new FixedBuilder<R>(context, names, name);
        }
        
        @Override
        protected FixedBuilder<R> self() {
            return this;
        }
        
        public R size(final int size) {
            final Schema schema = Schema.createFixed(this.name(), super.doc(), this.space(), size);
            this.completeSchema(schema);
            return this.context().complete(schema);
        }
    }
    
    public static final class EnumBuilder<R> extends NamespacedBuilder<R, EnumBuilder<R>>
    {
        private EnumBuilder(final Completion<R> context, final NameContext names, final String name) {
            super(context, names, name);
        }
        
        private static <R> EnumBuilder<R> create(final Completion<R> context, final NameContext names, final String name) {
            return new EnumBuilder<R>(context, names, name);
        }
        
        @Override
        protected EnumBuilder<R> self() {
            return this;
        }
        
        public R symbols(final String... symbols) {
            final Schema schema = Schema.createEnum(this.name(), this.doc(), this.space(), Arrays.asList(symbols));
            this.completeSchema(schema);
            return this.context().complete(schema);
        }
    }
    
    public static final class MapBuilder<R> extends PropBuilder<MapBuilder<R>>
    {
        private final Completion<R> context;
        private final NameContext names;
        
        private MapBuilder(final Completion<R> context, final NameContext names) {
            this.context = context;
            this.names = names;
        }
        
        private static <R> MapBuilder<R> create(final Completion<R> context, final NameContext names) {
            return new MapBuilder<R>(context, names);
        }
        
        @Override
        protected MapBuilder<R> self() {
            return this;
        }
        
        public TypeBuilder<R> values() {
            return new TypeBuilder<R>((Completion)new MapCompletion((MapBuilder)this, (Completion)this.context), this.names);
        }
        
        public R values(final Schema valueSchema) {
            return new MapCompletion<R>(this, (Completion)this.context).complete(valueSchema);
        }
    }
    
    public static final class ArrayBuilder<R> extends PropBuilder<ArrayBuilder<R>>
    {
        private final Completion<R> context;
        private final NameContext names;
        
        public ArrayBuilder(final Completion<R> context, final NameContext names) {
            this.context = context;
            this.names = names;
        }
        
        private static <R> ArrayBuilder<R> create(final Completion<R> context, final NameContext names) {
            return new ArrayBuilder<R>(context, names);
        }
        
        @Override
        protected ArrayBuilder<R> self() {
            return this;
        }
        
        public TypeBuilder<R> items() {
            return new TypeBuilder<R>((Completion)new ArrayCompletion((ArrayBuilder)this, (Completion)this.context), this.names);
        }
        
        public R items(final Schema itemsSchema) {
            return new ArrayCompletion<R>(this, (Completion)this.context).complete(itemsSchema);
        }
    }
    
    private static class NameContext
    {
        private static final Set<String> PRIMITIVES;
        private final HashMap<String, Schema> schemas;
        private final String namespace;
        
        private NameContext() {
            NameContext.PRIMITIVES.add("null");
            NameContext.PRIMITIVES.add("boolean");
            NameContext.PRIMITIVES.add("int");
            NameContext.PRIMITIVES.add("long");
            NameContext.PRIMITIVES.add("float");
            NameContext.PRIMITIVES.add("double");
            NameContext.PRIMITIVES.add("bytes");
            NameContext.PRIMITIVES.add("string");
            this.schemas = new HashMap<String, Schema>();
            this.namespace = null;
            this.schemas.put("null", Schema.create(Schema.Type.NULL));
            this.schemas.put("boolean", Schema.create(Schema.Type.BOOLEAN));
            this.schemas.put("int", Schema.create(Schema.Type.INT));
            this.schemas.put("long", Schema.create(Schema.Type.LONG));
            this.schemas.put("float", Schema.create(Schema.Type.FLOAT));
            this.schemas.put("double", Schema.create(Schema.Type.DOUBLE));
            this.schemas.put("bytes", Schema.create(Schema.Type.BYTES));
            this.schemas.put("string", Schema.create(Schema.Type.STRING));
        }
        
        private NameContext(final HashMap<String, Schema> schemas, final String namespace) {
            NameContext.PRIMITIVES.add("null");
            NameContext.PRIMITIVES.add("boolean");
            NameContext.PRIMITIVES.add("int");
            NameContext.PRIMITIVES.add("long");
            NameContext.PRIMITIVES.add("float");
            NameContext.PRIMITIVES.add("double");
            NameContext.PRIMITIVES.add("bytes");
            NameContext.PRIMITIVES.add("string");
            this.schemas = schemas;
            this.namespace = ("".equals(namespace) ? null : namespace);
        }
        
        private NameContext namespace(final String namespace) {
            return new NameContext(this.schemas, namespace);
        }
        
        private Schema get(final String name, final String namespace) {
            return this.getFullname(this.resolveName(name, namespace));
        }
        
        private Schema getFullname(final String fullName) {
            final Schema schema = this.schemas.get(fullName);
            if (schema == null) {
                throw new SchemaParseException("Undefined name: " + fullName);
            }
            return schema;
        }
        
        private void put(final Schema schema) {
            final String fullName = schema.getFullName();
            if (this.schemas.containsKey(fullName)) {
                throw new SchemaParseException("Can't redefine: " + fullName);
            }
            this.schemas.put(fullName, schema);
        }
        
        private String resolveName(final String name, String space) {
            if (NameContext.PRIMITIVES.contains(name) && space == null) {
                return name;
            }
            final int lastDot = name.lastIndexOf(46);
            if (lastDot < 0) {
                if (space == null) {
                    space = this.namespace;
                }
                if (space != null && !"".equals(space)) {
                    return space + "." + name;
                }
            }
            return name;
        }
        
        static {
            PRIMITIVES = new HashSet<String>();
        }
    }
    
    public static class BaseTypeBuilder<R>
    {
        private final Completion<R> context;
        private final NameContext names;
        
        private BaseTypeBuilder(final Completion<R> context, final NameContext names) {
            this.context = context;
            this.names = names;
        }
        
        public final R type(final Schema schema) {
            return this.context.complete(schema);
        }
        
        public final R type(final String name) {
            return this.type(name, null);
        }
        
        public final R type(final String name, final String namespace) {
            return this.type(this.names.get(name, namespace));
        }
        
        public final R booleanType() {
            return this.booleanBuilder().endBoolean();
        }
        
        public final BooleanBuilder<R> booleanBuilder() {
            return (BooleanBuilder<R>)create((Completion<Object>)this.context, this.names);
        }
        
        public final R intType() {
            return this.intBuilder().endInt();
        }
        
        public final IntBuilder<R> intBuilder() {
            return (IntBuilder<R>)create((Completion<Object>)this.context, this.names);
        }
        
        public final R longType() {
            return this.longBuilder().endLong();
        }
        
        public final LongBuilder<R> longBuilder() {
            return (LongBuilder<R>)create((Completion<Object>)this.context, this.names);
        }
        
        public final R floatType() {
            return this.floatBuilder().endFloat();
        }
        
        public final FloatBuilder<R> floatBuilder() {
            return (FloatBuilder<R>)create((Completion<Object>)this.context, this.names);
        }
        
        public final R doubleType() {
            return this.doubleBuilder().endDouble();
        }
        
        public final DoubleBuilder<R> doubleBuilder() {
            return (DoubleBuilder<R>)create((Completion<Object>)this.context, this.names);
        }
        
        public final R stringType() {
            return this.stringBuilder().endString();
        }
        
        public final StringBldr<R> stringBuilder() {
            return (StringBldr<R>)create((Completion<Object>)this.context, this.names);
        }
        
        public final R bytesType() {
            return this.bytesBuilder().endBytes();
        }
        
        public final BytesBuilder<R> bytesBuilder() {
            return (BytesBuilder<R>)create((Completion<Object>)this.context, this.names);
        }
        
        public final R nullType() {
            return this.nullBuilder().endNull();
        }
        
        public final NullBuilder<R> nullBuilder() {
            return (NullBuilder<R>)create((Completion<Object>)this.context, this.names);
        }
        
        public final MapBuilder<R> map() {
            return (MapBuilder<R>)create((Completion<Object>)this.context, this.names);
        }
        
        public final ArrayBuilder<R> array() {
            return (ArrayBuilder<R>)create((Completion<Object>)this.context, this.names);
        }
        
        public final FixedBuilder<R> fixed(final String name) {
            return (FixedBuilder<R>)create((Completion<Object>)this.context, this.names, name);
        }
        
        public final EnumBuilder<R> enumeration(final String name) {
            return (EnumBuilder<R>)create((Completion<Object>)this.context, this.names, name);
        }
        
        public final RecordBuilder<R> record(final String name) {
            return (RecordBuilder<R>)create((Completion<Object>)this.context, this.names, name);
        }
        
        protected BaseTypeBuilder<UnionAccumulator<R>> unionOf() {
            return (BaseTypeBuilder<UnionAccumulator<R>>)create((Completion<Object>)this.context, this.names);
        }
        
        protected BaseTypeBuilder<R> nullable() {
            return new BaseTypeBuilder<R>(new NullableCompletion<R>((Completion)this.context), this.names);
        }
    }
    
    public static final class TypeBuilder<R> extends BaseTypeBuilder<R>
    {
        private TypeBuilder(final Completion<R> context, final NameContext names) {
            super((Completion)context, names);
        }
        
        public BaseTypeBuilder<UnionAccumulator<R>> unionOf() {
            return super.unionOf();
        }
        
        public BaseTypeBuilder<R> nullable() {
            return super.nullable();
        }
    }
    
    private static final class UnionBuilder<R> extends BaseTypeBuilder<UnionAccumulator<R>>
    {
        private UnionBuilder(final Completion<R> context, final NameContext names) {
            this(context, names, (List)new ArrayList());
        }
        
        private static <R> UnionBuilder<R> create(final Completion<R> context, final NameContext names) {
            return new UnionBuilder<R>(context, names);
        }
        
        private UnionBuilder(final Completion<R> context, final NameContext names, final List<Schema> schemas) {
            super((Completion)new UnionCompletion((Completion)context, names, (List)schemas), names);
        }
    }
    
    public static class BaseFieldTypeBuilder<R>
    {
        protected final FieldBuilder<R> bldr;
        protected final NameContext names;
        private final CompletionWrapper wrapper;
        
        protected BaseFieldTypeBuilder(final FieldBuilder<R> bldr, final CompletionWrapper wrapper) {
            this.bldr = bldr;
            this.names = bldr.names();
            this.wrapper = wrapper;
        }
        
        public final BooleanDefault<R> booleanType() {
            return this.booleanBuilder().endBoolean();
        }
        
        public final BooleanBuilder<BooleanDefault<R>> booleanBuilder() {
            return (BooleanBuilder<BooleanDefault<R>>)create(this.wrap(new BooleanDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public final IntDefault<R> intType() {
            return this.intBuilder().endInt();
        }
        
        public final IntBuilder<IntDefault<R>> intBuilder() {
            return (IntBuilder<IntDefault<R>>)create(this.wrap(new IntDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public final LongDefault<R> longType() {
            return this.longBuilder().endLong();
        }
        
        public final LongBuilder<LongDefault<R>> longBuilder() {
            return (LongBuilder<LongDefault<R>>)create(this.wrap(new LongDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public final FloatDefault<R> floatType() {
            return this.floatBuilder().endFloat();
        }
        
        public final FloatBuilder<FloatDefault<R>> floatBuilder() {
            return (FloatBuilder<FloatDefault<R>>)create(this.wrap(new FloatDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public final DoubleDefault<R> doubleType() {
            return this.doubleBuilder().endDouble();
        }
        
        public final DoubleBuilder<DoubleDefault<R>> doubleBuilder() {
            return (DoubleBuilder<DoubleDefault<R>>)create(this.wrap(new DoubleDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public final StringDefault<R> stringType() {
            return this.stringBuilder().endString();
        }
        
        public final StringBldr<StringDefault<R>> stringBuilder() {
            return (StringBldr<StringDefault<R>>)create(this.wrap(new StringDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public final BytesDefault<R> bytesType() {
            return this.bytesBuilder().endBytes();
        }
        
        public final BytesBuilder<BytesDefault<R>> bytesBuilder() {
            return (BytesBuilder<BytesDefault<R>>)create(this.wrap(new BytesDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public final NullDefault<R> nullType() {
            return this.nullBuilder().endNull();
        }
        
        public final NullBuilder<NullDefault<R>> nullBuilder() {
            return (NullBuilder<NullDefault<R>>)create(this.wrap(new NullDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public final MapBuilder<MapDefault<R>> map() {
            return (MapBuilder<MapDefault<R>>)create(this.wrap(new MapDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public final ArrayBuilder<ArrayDefault<R>> array() {
            return (ArrayBuilder<ArrayDefault<R>>)create(this.wrap(new ArrayDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public final FixedBuilder<FixedDefault<R>> fixed(final String name) {
            return (FixedBuilder<FixedDefault<R>>)create(this.wrap(new FixedDefault((FieldBuilder)this.bldr)), this.names, name);
        }
        
        public final EnumBuilder<EnumDefault<R>> enumeration(final String name) {
            return (EnumBuilder<EnumDefault<R>>)create(this.wrap(new EnumDefault((FieldBuilder)this.bldr)), this.names, name);
        }
        
        public final RecordBuilder<RecordDefault<R>> record(final String name) {
            return (RecordBuilder<RecordDefault<R>>)create(this.wrap(new RecordDefault((FieldBuilder)this.bldr)), this.names, name);
        }
        
        private <C> Completion<C> wrap(final Completion<C> completion) {
            if (this.wrapper != null) {
                return this.wrapper.wrap(completion);
            }
            return completion;
        }
    }
    
    public static final class FieldTypeBuilder<R> extends BaseFieldTypeBuilder<R>
    {
        private FieldTypeBuilder(final FieldBuilder<R> bldr) {
            super(bldr, null);
        }
        
        public UnionFieldTypeBuilder<R> unionOf() {
            return new UnionFieldTypeBuilder<R>((FieldBuilder)this.bldr);
        }
        
        public BaseFieldTypeBuilder<R> nullable() {
            return new BaseFieldTypeBuilder<R>(this.bldr, new NullableCompletionWrapper());
        }
        
        public BaseTypeBuilder<FieldAssembler<R>> optional() {
            return new BaseTypeBuilder<FieldAssembler<R>>((Completion)new OptionalCompletion(this.bldr), this.names);
        }
    }
    
    public static final class UnionFieldTypeBuilder<R>
    {
        private final FieldBuilder<R> bldr;
        private final NameContext names;
        
        private UnionFieldTypeBuilder(final FieldBuilder<R> bldr) {
            this.bldr = bldr;
            this.names = bldr.names();
        }
        
        public UnionAccumulator<BooleanDefault<R>> booleanType() {
            return this.booleanBuilder().endBoolean();
        }
        
        public BooleanBuilder<UnionAccumulator<BooleanDefault<R>>> booleanBuilder() {
            return (BooleanBuilder<UnionAccumulator<BooleanDefault<R>>>)create((Completion<Object>)this.completion(new BooleanDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public UnionAccumulator<IntDefault<R>> intType() {
            return this.intBuilder().endInt();
        }
        
        public IntBuilder<UnionAccumulator<IntDefault<R>>> intBuilder() {
            return (IntBuilder<UnionAccumulator<IntDefault<R>>>)create((Completion<Object>)this.completion(new IntDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public UnionAccumulator<LongDefault<R>> longType() {
            return this.longBuilder().endLong();
        }
        
        public LongBuilder<UnionAccumulator<LongDefault<R>>> longBuilder() {
            return (LongBuilder<UnionAccumulator<LongDefault<R>>>)create((Completion<Object>)this.completion(new LongDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public UnionAccumulator<FloatDefault<R>> floatType() {
            return this.floatBuilder().endFloat();
        }
        
        public FloatBuilder<UnionAccumulator<FloatDefault<R>>> floatBuilder() {
            return (FloatBuilder<UnionAccumulator<FloatDefault<R>>>)create((Completion<Object>)this.completion(new FloatDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public UnionAccumulator<DoubleDefault<R>> doubleType() {
            return this.doubleBuilder().endDouble();
        }
        
        public DoubleBuilder<UnionAccumulator<DoubleDefault<R>>> doubleBuilder() {
            return (DoubleBuilder<UnionAccumulator<DoubleDefault<R>>>)create((Completion<Object>)this.completion(new DoubleDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public UnionAccumulator<StringDefault<R>> stringType() {
            return this.stringBuilder().endString();
        }
        
        public StringBldr<UnionAccumulator<StringDefault<R>>> stringBuilder() {
            return (StringBldr<UnionAccumulator<StringDefault<R>>>)create((Completion<Object>)this.completion(new StringDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public UnionAccumulator<BytesDefault<R>> bytesType() {
            return this.bytesBuilder().endBytes();
        }
        
        public BytesBuilder<UnionAccumulator<BytesDefault<R>>> bytesBuilder() {
            return (BytesBuilder<UnionAccumulator<BytesDefault<R>>>)create((Completion<Object>)this.completion(new BytesDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public UnionAccumulator<NullDefault<R>> nullType() {
            return this.nullBuilder().endNull();
        }
        
        public NullBuilder<UnionAccumulator<NullDefault<R>>> nullBuilder() {
            return (NullBuilder<UnionAccumulator<NullDefault<R>>>)create((Completion<Object>)this.completion(new NullDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public MapBuilder<UnionAccumulator<MapDefault<R>>> map() {
            return (MapBuilder<UnionAccumulator<MapDefault<R>>>)create((Completion<Object>)this.completion(new MapDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public ArrayBuilder<UnionAccumulator<ArrayDefault<R>>> array() {
            return (ArrayBuilder<UnionAccumulator<ArrayDefault<R>>>)create((Completion<Object>)this.completion(new ArrayDefault((FieldBuilder)this.bldr)), this.names);
        }
        
        public FixedBuilder<UnionAccumulator<FixedDefault<R>>> fixed(final String name) {
            return (FixedBuilder<UnionAccumulator<FixedDefault<R>>>)create((Completion<Object>)this.completion(new FixedDefault((FieldBuilder)this.bldr)), this.names, name);
        }
        
        public EnumBuilder<UnionAccumulator<EnumDefault<R>>> enumeration(final String name) {
            return (EnumBuilder<UnionAccumulator<EnumDefault<R>>>)create((Completion<Object>)this.completion(new EnumDefault((FieldBuilder)this.bldr)), this.names, name);
        }
        
        public RecordBuilder<UnionAccumulator<RecordDefault<R>>> record(final String name) {
            return (RecordBuilder<UnionAccumulator<RecordDefault<R>>>)create((Completion<Object>)this.completion(new RecordDefault((FieldBuilder)this.bldr)), this.names, name);
        }
        
        private <C> UnionCompletion<C> completion(final Completion<C> context) {
            return new UnionCompletion<C>((Completion)context, this.names, (List)new ArrayList());
        }
    }
    
    public static final class RecordBuilder<R> extends NamespacedBuilder<R, RecordBuilder<R>>
    {
        private RecordBuilder(final Completion<R> context, final NameContext names, final String name) {
            super(context, names, name);
        }
        
        private static <R> RecordBuilder<R> create(final Completion<R> context, final NameContext names, final String name) {
            return new RecordBuilder<R>(context, names, name);
        }
        
        @Override
        protected RecordBuilder<R> self() {
            return this;
        }
        
        public FieldAssembler<R> fields() {
            final Schema record = Schema.createRecord(this.name(), this.doc(), this.space(), false);
            this.completeSchema(record);
            return new FieldAssembler<R>((Completion)this.context(), this.names().namespace(record.getNamespace()), record);
        }
    }
    
    public static final class FieldAssembler<R>
    {
        private final List<Schema.Field> fields;
        private final Completion<R> context;
        private final NameContext names;
        private final Schema record;
        
        private FieldAssembler(final Completion<R> context, final NameContext names, final Schema record) {
            this.fields = new ArrayList<Schema.Field>();
            this.context = context;
            this.names = names;
            this.record = record;
        }
        
        public FieldBuilder<R> name(final String fieldName) {
            return new FieldBuilder<R>(this, this.names, fieldName);
        }
        
        public FieldAssembler<R> requiredBoolean(final String fieldName) {
            return (FieldAssembler<R>)this.name(fieldName).type().booleanType().noDefault();
        }
        
        public FieldAssembler<R> optionalBoolean(final String fieldName) {
            return this.name(fieldName).type().optional().booleanType();
        }
        
        public FieldAssembler<R> nullableBoolean(final String fieldName, final boolean defaultVal) {
            return this.name(fieldName).type().nullable().booleanType().booleanDefault(defaultVal);
        }
        
        public FieldAssembler<R> requiredInt(final String fieldName) {
            return (FieldAssembler<R>)this.name(fieldName).type().intType().noDefault();
        }
        
        public FieldAssembler<R> optionalInt(final String fieldName) {
            return this.name(fieldName).type().optional().intType();
        }
        
        public FieldAssembler<R> nullableInt(final String fieldName, final int defaultVal) {
            return this.name(fieldName).type().nullable().intType().intDefault(defaultVal);
        }
        
        public FieldAssembler<R> requiredLong(final String fieldName) {
            return (FieldAssembler<R>)this.name(fieldName).type().longType().noDefault();
        }
        
        public FieldAssembler<R> optionalLong(final String fieldName) {
            return this.name(fieldName).type().optional().longType();
        }
        
        public FieldAssembler<R> nullableLong(final String fieldName, final long defaultVal) {
            return this.name(fieldName).type().nullable().longType().longDefault(defaultVal);
        }
        
        public FieldAssembler<R> requiredFloat(final String fieldName) {
            return (FieldAssembler<R>)this.name(fieldName).type().floatType().noDefault();
        }
        
        public FieldAssembler<R> optionalFloat(final String fieldName) {
            return this.name(fieldName).type().optional().floatType();
        }
        
        public FieldAssembler<R> nullableFloat(final String fieldName, final float defaultVal) {
            return this.name(fieldName).type().nullable().floatType().floatDefault(defaultVal);
        }
        
        public FieldAssembler<R> requiredDouble(final String fieldName) {
            return (FieldAssembler<R>)this.name(fieldName).type().doubleType().noDefault();
        }
        
        public FieldAssembler<R> optionalDouble(final String fieldName) {
            return this.name(fieldName).type().optional().doubleType();
        }
        
        public FieldAssembler<R> nullableDouble(final String fieldName, final double defaultVal) {
            return this.name(fieldName).type().nullable().doubleType().doubleDefault(defaultVal);
        }
        
        public FieldAssembler<R> requiredString(final String fieldName) {
            return (FieldAssembler<R>)this.name(fieldName).type().stringType().noDefault();
        }
        
        public FieldAssembler<R> optionalString(final String fieldName) {
            return this.name(fieldName).type().optional().stringType();
        }
        
        public FieldAssembler<R> nullableString(final String fieldName, final String defaultVal) {
            return this.name(fieldName).type().nullable().stringType().stringDefault(defaultVal);
        }
        
        public FieldAssembler<R> requiredBytes(final String fieldName) {
            return (FieldAssembler<R>)this.name(fieldName).type().bytesType().noDefault();
        }
        
        public FieldAssembler<R> optionalBytes(final String fieldName) {
            return this.name(fieldName).type().optional().bytesType();
        }
        
        public FieldAssembler<R> nullableBytes(final String fieldName, final byte[] defaultVal) {
            return this.name(fieldName).type().nullable().bytesType().bytesDefault(defaultVal);
        }
        
        public R endRecord() {
            this.record.setFields(this.fields);
            return this.context.complete(this.record);
        }
        
        private FieldAssembler<R> addField(final Schema.Field field) {
            this.fields.add(field);
            return this;
        }
    }
    
    public static final class FieldBuilder<R> extends NamedBuilder<FieldBuilder<R>>
    {
        private final FieldAssembler<R> fields;
        private Schema.Field.Order order;
        
        private FieldBuilder(final FieldAssembler<R> fields, final NameContext names, final String name) {
            super(names, name);
            this.order = Schema.Field.Order.ASCENDING;
            this.fields = fields;
        }
        
        public FieldBuilder<R> orderAscending() {
            this.order = Schema.Field.Order.ASCENDING;
            return this.self();
        }
        
        public FieldBuilder<R> orderDescending() {
            this.order = Schema.Field.Order.DESCENDING;
            return this.self();
        }
        
        public FieldBuilder<R> orderIgnore() {
            this.order = Schema.Field.Order.IGNORE;
            return this.self();
        }
        
        public FieldTypeBuilder<R> type() {
            return new FieldTypeBuilder<R>(this);
        }
        
        public GenericDefault<R> type(final Schema type) {
            return new GenericDefault<R>(this, type);
        }
        
        public GenericDefault<R> type(final String name) {
            return this.type(name, null);
        }
        
        public GenericDefault<R> type(final String name, final String namespace) {
            final Schema schema = this.names().get(name, namespace);
            return this.type(schema);
        }
        
        private FieldAssembler<R> completeField(final Schema schema, final Object defaultVal) {
            final JsonNode defaultNode = toJsonNode(defaultVal);
            return this.completeField(schema, defaultNode);
        }
        
        private FieldAssembler<R> completeField(final Schema schema) {
            return this.completeField(schema, null);
        }
        
        private FieldAssembler<R> completeField(final Schema schema, final JsonNode defaultVal) {
            final Schema.Field field = new Schema.Field(this.name(), schema, this.doc(), defaultVal, this.order);
            this.addPropsTo(field);
            this.addAliasesTo(field);
            return (FieldAssembler<R>)((FieldAssembler<Object>)this.fields).addField(field);
        }
        
        @Override
        protected FieldBuilder<R> self() {
            return this;
        }
    }
    
    public abstract static class FieldDefault<R, S extends FieldDefault<R, S>> extends Completion<S>
    {
        private final FieldBuilder<R> field;
        private Schema schema;
        
        FieldDefault(final FieldBuilder<R> field) {
            this.field = field;
        }
        
        public final FieldAssembler<R> noDefault() {
            return (FieldAssembler<R>)((FieldBuilder<Object>)this.field).completeField(this.schema);
        }
        
        private FieldAssembler<R> usingDefault(final Object defaultVal) {
            return (FieldAssembler<R>)((FieldBuilder<Object>)this.field).completeField(this.schema, defaultVal);
        }
        
        @Override
        final S complete(final Schema schema) {
            this.schema = schema;
            return this.self();
        }
        
        abstract S self();
    }
    
    public static class BooleanDefault<R> extends FieldDefault<R, BooleanDefault<R>>
    {
        private BooleanDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final FieldAssembler<R> booleanDefault(final boolean defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        @Override
        final BooleanDefault<R> self() {
            return this;
        }
    }
    
    public static class IntDefault<R> extends FieldDefault<R, IntDefault<R>>
    {
        private IntDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final FieldAssembler<R> intDefault(final int defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        @Override
        final IntDefault<R> self() {
            return this;
        }
    }
    
    public static class LongDefault<R> extends FieldDefault<R, LongDefault<R>>
    {
        private LongDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final FieldAssembler<R> longDefault(final long defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        @Override
        final LongDefault<R> self() {
            return this;
        }
    }
    
    public static class FloatDefault<R> extends FieldDefault<R, FloatDefault<R>>
    {
        private FloatDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final FieldAssembler<R> floatDefault(final float defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        @Override
        final FloatDefault<R> self() {
            return this;
        }
    }
    
    public static class DoubleDefault<R> extends FieldDefault<R, DoubleDefault<R>>
    {
        private DoubleDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final FieldAssembler<R> doubleDefault(final double defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        @Override
        final DoubleDefault<R> self() {
            return this;
        }
    }
    
    public static class StringDefault<R> extends FieldDefault<R, StringDefault<R>>
    {
        private StringDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final FieldAssembler<R> stringDefault(final String defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        @Override
        final StringDefault<R> self() {
            return this;
        }
    }
    
    public static class BytesDefault<R> extends FieldDefault<R, BytesDefault<R>>
    {
        private BytesDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final FieldAssembler<R> bytesDefault(final byte[] defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(ByteBuffer.wrap(defaultVal));
        }
        
        public final FieldAssembler<R> bytesDefault(final ByteBuffer defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        public final FieldAssembler<R> bytesDefault(final String defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        @Override
        final BytesDefault<R> self() {
            return this;
        }
    }
    
    public static class NullDefault<R> extends FieldDefault<R, NullDefault<R>>
    {
        private NullDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final FieldAssembler<R> nullDefault() {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(null);
        }
        
        @Override
        final NullDefault<R> self() {
            return this;
        }
    }
    
    public static class MapDefault<R> extends FieldDefault<R, MapDefault<R>>
    {
        private MapDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final <K, V> FieldAssembler<R> mapDefault(final Map<K, V> defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        @Override
        final MapDefault<R> self() {
            return this;
        }
    }
    
    public static class ArrayDefault<R> extends FieldDefault<R, ArrayDefault<R>>
    {
        private ArrayDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final <V> FieldAssembler<R> arrayDefault(final List<V> defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        @Override
        final ArrayDefault<R> self() {
            return this;
        }
    }
    
    public static class FixedDefault<R> extends FieldDefault<R, FixedDefault<R>>
    {
        private FixedDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final FieldAssembler<R> fixedDefault(final byte[] defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(ByteBuffer.wrap(defaultVal));
        }
        
        public final FieldAssembler<R> fixedDefault(final ByteBuffer defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        public final FieldAssembler<R> fixedDefault(final String defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        @Override
        final FixedDefault<R> self() {
            return this;
        }
    }
    
    public static class EnumDefault<R> extends FieldDefault<R, EnumDefault<R>>
    {
        private EnumDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final FieldAssembler<R> enumDefault(final String defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        @Override
        final EnumDefault<R> self() {
            return this;
        }
    }
    
    public static class RecordDefault<R> extends FieldDefault<R, RecordDefault<R>>
    {
        private RecordDefault(final FieldBuilder<R> field) {
            super(field);
        }
        
        public final FieldAssembler<R> recordDefault(final GenericRecord defaultVal) {
            return (FieldAssembler<R>)((FieldDefault<Object, FieldDefault>)this).usingDefault(defaultVal);
        }
        
        @Override
        final RecordDefault<R> self() {
            return this;
        }
    }
    
    public static final class GenericDefault<R>
    {
        private final FieldBuilder<R> field;
        private final Schema schema;
        
        private GenericDefault(final FieldBuilder<R> field, final Schema schema) {
            this.field = field;
            this.schema = schema;
        }
        
        public FieldAssembler<R> noDefault() {
            return (FieldAssembler<R>)((FieldBuilder<Object>)this.field).completeField(this.schema);
        }
        
        public FieldAssembler<R> withDefault(final Object defaultVal) {
            return (FieldAssembler<R>)((FieldBuilder<Object>)this.field).completeField(this.schema, defaultVal);
        }
    }
    
    private abstract static class Completion<R>
    {
        abstract R complete(final Schema p0);
    }
    
    private static class SchemaCompletion extends Completion<Schema>
    {
        protected Schema complete(final Schema schema) {
            return schema;
        }
    }
    
    private static class NullableCompletion<R> extends Completion<R>
    {
        private final Completion<R> context;
        
        private NullableCompletion(final Completion<R> context) {
            this.context = context;
        }
        
        protected R complete(final Schema schema) {
            final Schema nullable = Schema.createUnion(Arrays.asList(schema, SchemaBuilder.NULL_SCHEMA));
            return this.context.complete(nullable);
        }
    }
    
    private static class OptionalCompletion<R> extends Completion<FieldAssembler<R>>
    {
        private final FieldBuilder<R> bldr;
        
        public OptionalCompletion(final FieldBuilder<R> bldr) {
            this.bldr = bldr;
        }
        
        protected FieldAssembler<R> complete(final Schema schema) {
            final Schema optional = Schema.createUnion(Arrays.asList(SchemaBuilder.NULL_SCHEMA, schema));
            return (FieldAssembler<R>)((FieldBuilder<Object>)this.bldr).completeField(optional, null);
        }
    }
    
    private abstract static class CompletionWrapper
    {
        abstract <R> Completion<R> wrap(final Completion<R> p0);
    }
    
    private static final class NullableCompletionWrapper extends CompletionWrapper
    {
        @Override
         <R> Completion<R> wrap(final Completion<R> completion) {
            return new NullableCompletion<R>((Completion)completion);
        }
    }
    
    private abstract static class NestedCompletion<R> extends Completion<R>
    {
        private final Completion<R> context;
        private final PropBuilder<?> assembler;
        
        private NestedCompletion(final PropBuilder<?> assembler, final Completion<R> context) {
            this.context = context;
            this.assembler = assembler;
        }
        
        protected final R complete(final Schema schema) {
            final Schema outer = this.outerSchema(schema);
            this.assembler.addPropsTo(outer);
            return this.context.complete(outer);
        }
        
        protected abstract Schema outerSchema(final Schema p0);
    }
    
    private static class MapCompletion<R> extends NestedCompletion<R>
    {
        private MapCompletion(final MapBuilder<R> assembler, final Completion<R> context) {
            super((PropBuilder)assembler, (Completion)context);
        }
        
        @Override
        protected Schema outerSchema(final Schema inner) {
            return Schema.createMap(inner);
        }
    }
    
    private static class ArrayCompletion<R> extends NestedCompletion<R>
    {
        private ArrayCompletion(final ArrayBuilder<R> assembler, final Completion<R> context) {
            super((PropBuilder)assembler, (Completion)context);
        }
        
        @Override
        protected Schema outerSchema(final Schema inner) {
            return Schema.createArray(inner);
        }
    }
    
    private static class UnionCompletion<R> extends Completion<UnionAccumulator<R>>
    {
        private final Completion<R> context;
        private final NameContext names;
        private final List<Schema> schemas;
        
        private UnionCompletion(final Completion<R> context, final NameContext names, final List<Schema> schemas) {
            this.context = context;
            this.names = names;
            this.schemas = schemas;
        }
        
        protected UnionAccumulator<R> complete(final Schema schema) {
            final List<Schema> updated = new ArrayList<Schema>(this.schemas);
            updated.add(schema);
            return new UnionAccumulator<R>((Completion)this.context, this.names, (List)updated);
        }
    }
    
    public static final class UnionAccumulator<R>
    {
        private final Completion<R> context;
        private final NameContext names;
        private final List<Schema> schemas;
        
        private UnionAccumulator(final Completion<R> context, final NameContext names, final List<Schema> schemas) {
            this.context = context;
            this.names = names;
            this.schemas = schemas;
        }
        
        public BaseTypeBuilder<UnionAccumulator<R>> and() {
            return (BaseTypeBuilder<UnionAccumulator<R>>)new UnionBuilder((Completion)this.context, this.names, (List)this.schemas);
        }
        
        public R endUnion() {
            final Schema schema = Schema.createUnion(this.schemas);
            return this.context.complete(schema);
        }
    }
}
