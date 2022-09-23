// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.jsonexplain.tez;

import java.util.Collection;
import java.io.PrintStream;
import java.util.Iterator;
import java.io.IOException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.JsonParseException;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.List;

public class Vertex
{
    public String name;
    public List<Connection> parentConnections;
    public List<Vertex> children;
    public JSONObject vertexObject;
    public boolean union;
    public boolean dummy;
    public List<Op> rootOps;
    public List<Vertex> mergeJoinDummyVertexs;
    boolean hasMultiReduceOp;
    
    public Vertex(final String name, final JSONObject vertexObject) {
        this.name = name;
        if (this.name != null && this.name.contains("Union")) {
            this.union = true;
        }
        else {
            this.union = false;
        }
        this.dummy = false;
        this.vertexObject = vertexObject;
        this.parentConnections = new ArrayList<Connection>();
        this.children = new ArrayList<Vertex>();
        this.rootOps = new ArrayList<Op>();
        this.mergeJoinDummyVertexs = new ArrayList<Vertex>();
        this.hasMultiReduceOp = false;
    }
    
    public void addDependency(final Connection connection) throws JSONException {
        this.parentConnections.add(connection);
    }
    
    public void extractOpTree() throws JSONException, JsonParseException, JsonMappingException, IOException, Exception {
        if (this.vertexObject.length() != 0) {
            for (final String key : JSONObject.getNames(this.vertexObject)) {
                if (key.equals("Map Operator Tree:")) {
                    this.extractOp(this.vertexObject.getJSONArray(key).getJSONObject(0));
                }
                else if (key.equals("Reduce Operator Tree:") || key.equals("Processor Tree:")) {
                    this.extractOp(this.vertexObject.getJSONObject(key));
                }
                else {
                    if (!key.equals("Join:")) {
                        throw new Exception("unsupported operator tree in vertex " + this.name);
                    }
                    final JSONArray array = this.vertexObject.getJSONArray(key);
                    for (int index = 0; index < array.length(); ++index) {
                        final JSONObject mpOpTree = array.getJSONObject(index);
                        final Vertex v = new Vertex("", mpOpTree);
                        v.extractOpTree();
                        v.dummy = true;
                        this.mergeJoinDummyVertexs.add(v);
                    }
                }
            }
        }
    }
    
    Op extractOp(final JSONObject operator) throws JSONException, JsonParseException, JsonMappingException, IOException, Exception {
        final String[] names = JSONObject.getNames(operator);
        if (names.length != 1) {
            throw new Exception("Expect only one operator in " + operator.toString());
        }
        final String opName = names[0];
        final JSONObject attrObj = (JSONObject)operator.get(opName);
        final List<Attr> attrs = new ArrayList<Attr>();
        final List<Op> children = new ArrayList<Op>();
        String id = null;
        String outputVertexName = null;
        for (final String attrName : JSONObject.getNames(attrObj)) {
            if (attrName.equals("children")) {
                final Object childrenObj = attrObj.get(attrName);
                if (childrenObj instanceof JSONObject) {
                    if (((JSONObject)childrenObj).length() != 0) {
                        children.add(this.extractOp((JSONObject)childrenObj));
                    }
                }
                else {
                    if (!(childrenObj instanceof JSONArray)) {
                        throw new Exception("Unsupported operator " + this.name + "'s children operator is neither a jsonobject nor a jsonarray");
                    }
                    if (((JSONArray)childrenObj).length() != 0) {
                        final JSONArray array = (JSONArray)childrenObj;
                        for (int index = 0; index < array.length(); ++index) {
                            children.add(this.extractOp(array.getJSONObject(index)));
                        }
                    }
                }
            }
            else if (attrName.equals("OperatorId:")) {
                id = attrObj.get(attrName).toString();
            }
            else if (attrName.equals("outputname:")) {
                outputVertexName = attrObj.get(attrName).toString();
            }
            else {
                attrs.add(new Attr(attrName, attrObj.get(attrName).toString()));
            }
        }
        final Op op = new Op(opName, id, outputVertexName, children, attrs, operator, this);
        if (!children.isEmpty()) {
            for (final Op child : children) {
                child.parent = op;
            }
        }
        else {
            this.rootOps.add(op);
        }
        return op;
    }
    
    public void print(final PrintStream out, final List<Boolean> indentFlag, final String type, final Vertex callingVertex) throws JSONException, Exception {
        if (TezJsonParser.printSet.contains(this) && !this.hasMultiReduceOp) {
            if (type != null) {
                out.println(TezJsonParser.prefixString(indentFlag, "|<-") + " Please refer to the previous " + this.name + " [" + type + "]");
            }
            else {
                out.println(TezJsonParser.prefixString(indentFlag, "|<-") + " Please refer to the previous " + this.name);
            }
            return;
        }
        TezJsonParser.printSet.add(this);
        if (type != null) {
            out.println(TezJsonParser.prefixString(indentFlag, "|<-") + this.name + " [" + type + "]");
        }
        else if (this.name != null) {
            out.println(TezJsonParser.prefixString(indentFlag) + this.name);
        }
        if (this.hasMultiReduceOp && !callingVertex.union) {
            Op choose = null;
            for (final Op op : this.rootOps) {
                if (op.outputVertexName.equals(callingVertex.name)) {
                    choose = op;
                }
            }
            if (choose == null) {
                throw new Exception("Can not find the right reduce output operator for vertex " + this.name);
            }
            choose.print(out, indentFlag, false);
        }
        else {
            for (final Op op2 : this.rootOps) {
                if (this.dummy) {
                    op2.print(out, indentFlag, true);
                }
                else {
                    op2.print(out, indentFlag, false);
                }
            }
        }
        if (this.union) {
            for (int index = 0; index < this.parentConnections.size(); ++index) {
                final Connection connection = this.parentConnections.get(index);
                final List<Boolean> unionFlag = new ArrayList<Boolean>();
                unionFlag.addAll(indentFlag);
                if (index != this.parentConnections.size() - 1) {
                    unionFlag.add(true);
                }
                else {
                    unionFlag.add(false);
                }
                connection.from.print(out, unionFlag, connection.type, this);
            }
        }
    }
    
    public void checkMultiReduceOperator() {
        if (!this.name.contains("Reduce") || this.rootOps.size() < 2) {
            return;
        }
        for (final Op op : this.rootOps) {
            if (!op.name.contains("Reduce")) {
                return;
            }
        }
        this.hasMultiReduceOp = true;
    }
}
