// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.state;

import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class VisualizeStateMachine
{
    public static Graph getGraphFromClasses(final String graphName, final List<String> classes) throws Exception {
        Graph ret = null;
        if (classes.size() != 1) {
            ret = new Graph(graphName);
        }
        for (final String className : classes) {
            final Class clz = Class.forName(className);
            final Field factoryField = clz.getDeclaredField("stateMachineFactory");
            factoryField.setAccessible(true);
            final StateMachineFactory factory = (StateMachineFactory)factoryField.get(null);
            if (classes.size() == 1) {
                return factory.generateStateGraph(graphName);
            }
            String gname = clz.getSimpleName();
            if (gname.endsWith("Impl")) {
                gname = gname.substring(0, gname.length() - 4);
            }
            ret.addSubGraph(factory.generateStateGraph(gname));
        }
        return ret;
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 3) {
            System.err.printf("Usage: %s <GraphName> <class[,class[,...]]> <OutputFile>\n", VisualizeStateMachine.class.getName());
            System.exit(1);
        }
        final String[] classes = args[1].split(",");
        final ArrayList<String> validClasses = new ArrayList<String>();
        for (final String c : classes) {
            final String vc = c.trim();
            if (vc.length() > 0) {
                validClasses.add(vc);
            }
        }
        final Graph g = getGraphFromClasses(args[0], validClasses);
        g.save(args[2]);
    }
}
