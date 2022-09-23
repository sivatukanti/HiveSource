// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.jsonexplain.tez;

import java.util.Collections;
import java.util.Collection;
import java.io.PrintStream;
import org.apache.hadoop.fs.Path;
import java.io.IOException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.JsonParseException;
import java.util.Iterator;
import org.json.JSONArray;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

public class Stage
{
    String name;
    List<Stage> parentStages;
    List<Stage> childStages;
    Map<String, Vertex> vertexs;
    List<Attr> attrs;
    LinkedHashMap<Vertex, List<Connection>> tezStageDependency;
    Op op;
    
    public Stage(final String name) {
        this.name = name;
        this.parentStages = new ArrayList<Stage>();
        this.childStages = new ArrayList<Stage>();
        this.attrs = new ArrayList<Attr>();
        this.vertexs = new LinkedHashMap<String, Vertex>();
    }
    
    public void addDependency(final JSONObject object, final Map<String, Stage> stages) throws JSONException {
        if (!object.has("ROOT STAGE")) {
            final String names = object.getString("DEPENDENT STAGES");
            for (final String name : names.split(",")) {
                final Stage parent = stages.get(name.trim());
                this.parentStages.add(parent);
                parent.childStages.add(this);
            }
        }
    }
    
    public void extractVertex(final JSONObject object) throws Exception {
        if (object.has("Tez")) {
            this.tezStageDependency = new LinkedHashMap<Vertex, List<Connection>>();
            final JSONObject tez = (JSONObject)object.get("Tez");
            final JSONObject vertices = tez.getJSONObject("Vertices:");
            if (tez.has("Edges:")) {
                final JSONObject edges = tez.getJSONObject("Edges:");
                for (final String to : JSONObject.getNames(edges)) {
                    this.vertexs.put(to, new Vertex(to, vertices.getJSONObject(to)));
                }
                for (final String to : JSONObject.getNames(edges)) {
                    final Object o = edges.get(to);
                    final Vertex v = this.vertexs.get(to);
                    if (o instanceof JSONObject) {
                        final JSONObject obj = (JSONObject)o;
                        final String parent = obj.getString("parent");
                        Vertex parentVertex = this.vertexs.get(parent);
                        if (parentVertex == null) {
                            parentVertex = new Vertex(parent, vertices.getJSONObject(parent));
                            this.vertexs.put(parent, parentVertex);
                        }
                        final String type = obj.getString("type");
                        if (!"CONTAINS".equals(type)) {
                            v.addDependency(new Connection(type, parentVertex));
                            parentVertex.children.add(v);
                        }
                        else {
                            parentVertex.addDependency(new Connection(type, v));
                            v.children.add(parentVertex);
                        }
                        this.tezStageDependency.put(v, Arrays.asList(new Connection(type, parentVertex)));
                    }
                    else {
                        final JSONArray from = (JSONArray)o;
                        final List<Connection> list = new ArrayList<Connection>();
                        for (int index = 0; index < from.length(); ++index) {
                            final JSONObject obj2 = from.getJSONObject(index);
                            final String parent2 = obj2.getString("parent");
                            Vertex parentVertex2 = this.vertexs.get(parent2);
                            if (parentVertex2 == null) {
                                parentVertex2 = new Vertex(parent2, vertices.getJSONObject(parent2));
                                this.vertexs.put(parent2, parentVertex2);
                            }
                            final String type2 = obj2.getString("type");
                            if (!"CONTAINS".equals(type2)) {
                                v.addDependency(new Connection(type2, parentVertex2));
                                parentVertex2.children.add(v);
                            }
                            else {
                                parentVertex2.addDependency(new Connection(type2, v));
                                v.children.add(parentVertex2);
                            }
                            list.add(new Connection(type2, parentVertex2));
                        }
                        this.tezStageDependency.put(v, list);
                    }
                }
            }
            else {
                for (final String vertexName : JSONObject.getNames(vertices)) {
                    this.vertexs.put(vertexName, new Vertex(vertexName, vertices.getJSONObject(vertexName)));
                }
            }
            for (final Vertex v2 : this.vertexs.values()) {
                if (!v2.union) {
                    v2.extractOpTree();
                    v2.checkMultiReduceOperator();
                }
            }
        }
        else {
            final String[] names5;
            final String[] names = names5 = JSONObject.getNames(object);
            for (final String name : names5) {
                if (name.contains("Operator")) {
                    this.op = this.extractOp(name, object.getJSONObject(name));
                }
                else {
                    this.attrs.add(new Attr(name, object.get(name).toString()));
                }
            }
        }
    }
    
    Op extractOp(final String opName, final JSONObject opObj) throws JSONException, JsonParseException, JsonMappingException, IOException, Exception {
        final List<Attr> attrs = new ArrayList<Attr>();
        Vertex v = null;
        if (opObj.length() > 0) {
            final String[] names2;
            final String[] names = names2 = JSONObject.getNames(opObj);
            for (final String name : names2) {
                final Object o = opObj.get(name);
                if (this.isPrintable(o)) {
                    attrs.add(new Attr(name, o.toString()));
                }
                else {
                    if (!(o instanceof JSONObject)) {
                        throw new Exception("Unsupported object in " + this.name);
                    }
                    final JSONObject attrObj = (JSONObject)o;
                    if (attrObj.length() > 0) {
                        if (name.equals("Processor Tree:")) {
                            final JSONObject object = new JSONObject();
                            object.put(name, attrObj);
                            v = new Vertex(null, object);
                            v.extractOpTree();
                        }
                        else {
                            for (final String attrName : JSONObject.getNames(attrObj)) {
                                attrs.add(new Attr(attrName, attrObj.get(attrName).toString()));
                            }
                        }
                    }
                }
            }
        }
        final Op op = new Op(opName, null, null, null, attrs, null, v);
        if (v != null) {
            TezJsonParser.addInline(op, new Connection(null, v));
        }
        return op;
    }
    
    private boolean isPrintable(final Object val) {
        return val instanceof Boolean || val instanceof String || val instanceof Integer || val instanceof Long || val instanceof Byte || val instanceof Float || val instanceof Double || val instanceof Path || (val != null && val.getClass().isPrimitive());
    }
    
    public void print(final PrintStream out, final List<Boolean> indentFlag) throws JSONException, Exception {
        if (TezJsonParser.printSet.contains(this)) {
            out.println(TezJsonParser.prefixString(indentFlag) + " Please refer to the previous " + this.name);
            return;
        }
        TezJsonParser.printSet.add(this);
        out.println(TezJsonParser.prefixString(indentFlag) + this.name);
        final List<Boolean> nextIndentFlag = new ArrayList<Boolean>();
        nextIndentFlag.addAll(indentFlag);
        nextIndentFlag.add(false);
        for (final Vertex candidate : this.vertexs.values()) {
            if (!TezJsonParser.isInline(candidate) && candidate.children.isEmpty()) {
                candidate.print(out, nextIndentFlag, null, null);
            }
        }
        if (!this.attrs.isEmpty()) {
            Collections.sort(this.attrs);
            for (final Attr attr : this.attrs) {
                out.println(TezJsonParser.prefixString(nextIndentFlag) + attr.toString());
            }
        }
        if (this.op != null) {
            this.op.print(out, nextIndentFlag, false);
        }
        nextIndentFlag.add(false);
        for (final Stage stage : this.parentStages) {
            stage.print(out, nextIndentFlag);
        }
    }
}
