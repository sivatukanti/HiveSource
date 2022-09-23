// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.jsonexplain.tez;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.JsonParseException;
import org.json.JSONException;
import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import java.io.PrintStream;
import java.util.Map;
import org.json.JSONObject;
import org.apache.hadoop.hive.common.jsonexplain.JsonParser;

public class TezJsonParser implements JsonParser
{
    JSONObject inputObject;
    Map<String, Stage> stages;
    PrintStream outputStream;
    protected final Log LOG;
    public static Set<Object> printSet;
    public static Map<Op, List<Connection>> inlineMap;
    
    public TezJsonParser() {
        this.LOG = LogFactory.getLog(this.getClass().getName());
    }
    
    public void extractStagesAndPlans() throws JSONException, JsonParseException, JsonMappingException, Exception, IOException {
        this.stages = new HashMap<String, Stage>();
        final JSONObject dependency = this.inputObject.getJSONObject("STAGE DEPENDENCIES");
        if (dependency.length() > 0) {
            for (final String stageName : JSONObject.getNames(dependency)) {
                this.stages.put(stageName, new Stage(stageName));
            }
            for (final String stageName : JSONObject.getNames(dependency)) {
                final JSONObject dependentStageNames = dependency.getJSONObject(stageName);
                this.stages.get(stageName).addDependency(dependentStageNames, this.stages);
            }
        }
        final JSONObject stagePlans = this.inputObject.getJSONObject("STAGE PLANS");
        if (stagePlans.length() > 0) {
            for (final String stageName2 : JSONObject.getNames(stagePlans)) {
                final JSONObject stagePlan = stagePlans.getJSONObject(stageName2);
                this.stages.get(stageName2).extractVertex(stagePlan);
            }
        }
    }
    
    public static String prefixString(final List<Boolean> indentFlag) {
        final StringBuilder sb = new StringBuilder();
        for (int index = 0; index < indentFlag.size(); ++index) {
            if (indentFlag.get(index)) {
                sb.append("|  ");
            }
            else {
                sb.append("   ");
            }
        }
        return sb.toString();
    }
    
    public static String prefixString(final List<Boolean> indentFlag, final String tail) {
        final StringBuilder sb = new StringBuilder();
        for (int index = 0; index < indentFlag.size(); ++index) {
            if (indentFlag.get(index)) {
                sb.append("|  ");
            }
            else {
                sb.append("   ");
            }
        }
        final int len = sb.length();
        return sb.replace(len - tail.length(), len, tail).toString();
    }
    
    @Override
    public void print(final JSONObject inputObject, final PrintStream outputStream) throws Exception {
        this.LOG.info("JsonParser is parsing\n" + inputObject.toString());
        this.inputObject = inputObject;
        this.outputStream = outputStream;
        this.extractStagesAndPlans();
        if (inputObject.has("cboInfo")) {
            outputStream.println(inputObject.getString("cboInfo"));
            outputStream.println();
        }
        for (final Stage candidate : this.stages.values()) {
            if (candidate.tezStageDependency != null && candidate.tezStageDependency.size() > 0) {
                outputStream.println("Vertex dependency in root stage");
                for (final Map.Entry<Vertex, List<Connection>> entry : candidate.tezStageDependency.entrySet()) {
                    final StringBuffer sb = new StringBuffer();
                    sb.append(entry.getKey().name);
                    sb.append(" <- ");
                    boolean printcomma = false;
                    for (final Connection connection : entry.getValue()) {
                        if (printcomma) {
                            sb.append(", ");
                        }
                        else {
                            printcomma = true;
                        }
                        sb.append(connection.from.name + " (" + connection.type + ")");
                    }
                    outputStream.println(sb.toString());
                }
                outputStream.println();
            }
        }
        final List<Boolean> indentFlag = new ArrayList<Boolean>();
        for (final Stage candidate2 : this.stages.values()) {
            if (candidate2.childStages.isEmpty()) {
                candidate2.print(outputStream, indentFlag);
            }
        }
    }
    
    public static void addInline(final Op op, final Connection connection) {
        List<Connection> list = TezJsonParser.inlineMap.get(op);
        if (list == null) {
            list = new ArrayList<Connection>();
            list.add(connection);
            TezJsonParser.inlineMap.put(op, list);
        }
        else {
            list.add(connection);
        }
    }
    
    public static boolean isInline(final Vertex v) {
        for (final List<Connection> list : TezJsonParser.inlineMap.values()) {
            for (final Connection connection : list) {
                if (connection.from.equals(v)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static {
        TezJsonParser.printSet = new HashSet<Object>();
        TezJsonParser.inlineMap = new HashMap<Op, List<Connection>>();
    }
}
