// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell.find;

import java.util.Deque;
import java.util.Iterator;
import org.apache.hadoop.fs.shell.PathData;
import java.io.IOException;

final class And extends BaseExpression
{
    private static final String[] USAGE;
    private static final String[] HELP;
    
    public static void registerExpression(final ExpressionFactory factory) throws IOException {
        factory.addClass(And.class, "-a");
        factory.addClass(And.class, "-and");
    }
    
    public And() {
        this.setUsage(And.USAGE);
        this.setHelp(And.HELP);
    }
    
    @Override
    public Result apply(final PathData item, final int depth) throws IOException {
        Result result = Result.PASS;
        for (final Expression child : this.getChildren()) {
            final Result childResult = child.apply(item, -1);
            result = result.combine(childResult);
            if (!result.isPass()) {
                return result;
            }
        }
        return result;
    }
    
    @Override
    public boolean isOperator() {
        return true;
    }
    
    @Override
    public int getPrecedence() {
        return 200;
    }
    
    @Override
    public void addChildren(final Deque<Expression> expressions) {
        this.addChildren(expressions, 2);
    }
    
    static {
        USAGE = new String[] { "expression -a expression", "expression -and expression", "expression expression" };
        HELP = new String[] { "Logical AND operator for joining two expressions. Returns", "true if both child expressions return true. Implied by the", "juxtaposition of two expressions and so does not need to be", "explicitly specified. The second expression will not be", "applied if the first fails." };
    }
}
