// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.jsonexplain.tez;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class Op
{
    String name;
    String operatorId;
    Op parent;
    List<Op> children;
    List<Attr> attrs;
    JSONObject opObject;
    Vertex vertex;
    String outputVertexName;
    
    public Op(final String name, final String id, final String outputVertexName, final List<Op> children, final List<Attr> attrs, final JSONObject opObject, final Vertex vertex) throws JSONException {
        this.name = name;
        this.operatorId = id;
        this.outputVertexName = outputVertexName;
        this.children = children;
        this.attrs = attrs;
        this.opObject = opObject;
        this.vertex = vertex;
    }
    
    private void inlineJoinOp() throws Exception {
        if (this.name.equals("Map Join Operator")) {
            final JSONObject mapjoinObj = this.opObject.getJSONObject("Map Join Operator");
            final JSONObject verticeObj = mapjoinObj.getJSONObject("input vertices:");
            final Map<String, String> posToVertex = new HashMap<String, String>();
            for (final String pos : JSONObject.getNames(verticeObj)) {
                final String vertexName = verticeObj.getString(pos);
                posToVertex.put(pos, vertexName);
                Connection c = null;
                for (final Connection connection : this.vertex.parentConnections) {
                    if (connection.from.name.equals(vertexName)) {
                        c = connection;
                        break;
                    }
                }
                if (c != null) {
                    TezJsonParser.addInline(this, c);
                }
            }
            this.removeAttr("input vertices:");
            final JSONObject keys = mapjoinObj.getJSONObject("keys:");
            if (keys.length() != 0) {
                final JSONObject newKeys = new JSONObject();
                for (final String key : JSONObject.getNames(keys)) {
                    final String vertexName2 = posToVertex.get(key);
                    if (vertexName2 != null) {
                        newKeys.put(vertexName2, keys.get(key));
                    }
                    else {
                        newKeys.put(this.vertex.name, keys.get(key));
                    }
                }
                this.removeAttr("keys:");
                this.attrs.add(new Attr("keys:", newKeys.toString()));
            }
        }
        else {
            if (!this.name.equals("Merge Join Operator")) {
                throw new Exception("Unknown join operator");
            }
            if (this.vertex != null) {
                for (final Vertex v : this.vertex.mergeJoinDummyVertexs) {
                    TezJsonParser.addInline(this, new Connection(null, v));
                }
            }
        }
    }
    
    private String getNameWithOpId() {
        if (this.operatorId != null) {
            return this.name + " [" + this.operatorId + "]";
        }
        return this.name;
    }
    
    public void print(final PrintStream out, final List<Boolean> indentFlag, boolean branchOfJoinOp) throws Exception {
        if (TezJsonParser.printSet.contains(this)) {
            out.println(TezJsonParser.prefixString(indentFlag) + " Please refer to the previous " + this.getNameWithOpId());
            return;
        }
        TezJsonParser.printSet.add(this);
        if (!branchOfJoinOp) {
            out.println(TezJsonParser.prefixString(indentFlag) + this.getNameWithOpId());
        }
        else {
            out.println(TezJsonParser.prefixString(indentFlag, "|<-") + this.getNameWithOpId());
        }
        branchOfJoinOp = false;
        if (this.name.contains("Join")) {
            this.inlineJoinOp();
            branchOfJoinOp = true;
        }
        final List<Connection> noninlined = new ArrayList<Connection>();
        if (this.parent == null && this.vertex != null) {
            for (final Connection connection : this.vertex.parentConnections) {
                if (!TezJsonParser.isInline(connection.from)) {
                    noninlined.add(connection);
                }
            }
        }
        final List<Boolean> attFlag = new ArrayList<Boolean>();
        attFlag.addAll(indentFlag);
        if (branchOfJoinOp || (this.parent == null && !noninlined.isEmpty())) {
            attFlag.add(true);
        }
        else {
            attFlag.add(false);
        }
        Collections.sort(this.attrs);
        for (final Attr attr : this.attrs) {
            out.println(TezJsonParser.prefixString(attFlag) + attr.toString());
        }
        if (TezJsonParser.inlineMap.containsKey(this)) {
            for (int index = 0; index < TezJsonParser.inlineMap.get(this).size(); ++index) {
                final Connection connection2 = TezJsonParser.inlineMap.get(this).get(index);
                final List<Boolean> vertexFlag = new ArrayList<Boolean>();
                vertexFlag.addAll(indentFlag);
                if (branchOfJoinOp) {
                    vertexFlag.add(true);
                }
                else {
                    vertexFlag.add(false);
                }
                connection2.from.print(out, vertexFlag, connection2.type, this.vertex);
            }
        }
        if (this.parent != null) {
            final List<Boolean> parentFlag = new ArrayList<Boolean>();
            parentFlag.addAll(indentFlag);
            parentFlag.add(false);
            this.parent.print(out, parentFlag, branchOfJoinOp);
        }
        else {
            for (int index = 0; index < noninlined.size(); ++index) {
                final Vertex v = noninlined.get(index).from;
                final List<Boolean> vertexFlag = new ArrayList<Boolean>();
                vertexFlag.addAll(indentFlag);
                if (index != noninlined.size() - 1) {
                    vertexFlag.add(true);
                }
                else {
                    vertexFlag.add(false);
                }
                v.print(out, vertexFlag, noninlined.get(index).type, this.vertex);
            }
        }
    }
    
    public void removeAttr(final String name) {
        int removeIndex = -1;
        for (int index = 0; index < this.attrs.size(); ++index) {
            if (this.attrs.get(index).name.equals(name)) {
                removeIndex = index;
                break;
            }
        }
        if (removeIndex != -1) {
            this.attrs.remove(removeIndex);
        }
    }
}
