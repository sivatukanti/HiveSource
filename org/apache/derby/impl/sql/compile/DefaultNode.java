// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.catalog.types.DefaultInfoImpl;
import java.util.List;

public class DefaultNode extends ValueNode
{
    private String columnName;
    private String defaultText;
    private ValueNode defaultTree;
    
    public void init(final Object o, final Object o2) {
        this.defaultTree = (ValueNode)o;
        this.defaultText = (String)o2;
    }
    
    public void init(final Object o) {
        this.columnName = (String)o;
    }
    
    public String getDefaultText() {
        return this.defaultText;
    }
    
    ValueNode getDefaultTree() {
        return this.defaultTree;
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        final ColumnDescriptor columnDescriptor = ((FromBaseTable)list.elementAt(0)).getTableDescriptor().getColumnDescriptor(this.columnName);
        final DefaultInfoImpl defaultInfoImpl = (DefaultInfoImpl)columnDescriptor.getDefaultInfo();
        if (defaultInfoImpl != null) {
            final ValueNode default1 = parseDefault(defaultInfoImpl.getDefaultText(), this.getLanguageConnectionContext(), this.getCompilerContext());
            this.getCompilerContext().createDependency(columnDescriptor.getDefaultDescriptor(this.getDataDictionary()));
            return default1.bindExpression(list, list2, list3);
        }
        return (ValueNode)this.getNodeFactory().getNode(13, this.getContextManager());
    }
    
    public static ValueNode parseDefault(final String str, final LanguageConnectionContext languageConnectionContext, final CompilerContext compilerContext) throws StandardException {
        final String string = "VALUES " + str;
        final CompilerContext pushCompilerContext = languageConnectionContext.pushCompilerContext();
        final ValueNode expression = ((ResultColumn)((CursorNode)pushCompilerContext.getParser().parseStatement(string)).getResultSetNode().getResultColumns().elementAt(0)).getExpression();
        languageConnectionContext.popCompilerContext(pushCompilerContext);
        return expression;
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
    }
    
    protected boolean isEquivalent(final ValueNode valueNode) {
        return false;
    }
}
