// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import java.util.Set;
import java.util.HashMap;
import org.antlr.stringtemplate.StringTemplateGroup;
import antlr.MismatchedTokenException;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Map;
import antlr.NoViableAltException;
import java.io.Writer;
import java.io.StringWriter;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.collections.impl.BitSet;
import org.antlr.stringtemplate.StringTemplateWriter;
import org.antlr.stringtemplate.StringTemplate;
import antlr.TreeParser;

public class ActionEvaluator extends TreeParser implements ActionEvaluatorTokenTypes
{
    protected StringTemplate self;
    protected StringTemplateWriter out;
    protected ASTExpr chunk;
    public static final String[] _tokenNames;
    public static final BitSet _tokenSet_0;
    
    public ActionEvaluator(final StringTemplate self, final ASTExpr chunk, final StringTemplateWriter out) {
        this.self = null;
        this.out = null;
        this.chunk = null;
        this.self = self;
        this.chunk = chunk;
        this.out = out;
    }
    
    public void reportError(final RecognitionException e) {
        this.self.error("eval tree parse error", e);
    }
    
    public ActionEvaluator() {
        this.self = null;
        this.out = null;
        this.chunk = null;
        this.tokenNames = ActionEvaluator._tokenNames;
    }
    
    public final int action(AST _t) throws RecognitionException {
        int numCharsWritten = 0;
        final StringTemplateAST action_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        Object e = null;
        try {
            e = this.expr(_t);
            _t = this._retTree;
            numCharsWritten = this.chunk.writeAttribute(this.self, e, this.out);
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return numCharsWritten;
    }
    
    public final Object expr(AST _t) throws RecognitionException {
        Object value = null;
        final StringTemplateAST expr_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        Object a = null;
        Object b = null;
        Object e = null;
        final Map argumentContext = null;
        try {
            if (_t == null) {
                _t = ActionEvaluator.ASTNULL;
            }
            switch (_t.getType()) {
                case 24: {
                    final AST __t3 = _t;
                    final StringTemplateAST tmp1_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 24);
                    _t = _t.getFirstChild();
                    a = this.expr(_t);
                    _t = this._retTree;
                    b = this.expr(_t);
                    _t = this._retTree;
                    value = this.chunk.add(a, b);
                    _t = __t3;
                    _t = _t.getNextSibling();
                    break;
                }
                case 4:
                case 5: {
                    value = this.templateApplication(_t);
                    _t = this._retTree;
                    break;
                }
                case 20:
                case 25:
                case 33:
                case 34:
                case 35: {
                    value = this.attribute(_t);
                    _t = this._retTree;
                    break;
                }
                case 7: {
                    value = this.templateInclude(_t);
                    _t = this._retTree;
                    break;
                }
                case 11: {
                    value = this.function(_t);
                    _t = this._retTree;
                    break;
                }
                case 13: {
                    value = this.list(_t);
                    _t = this._retTree;
                    break;
                }
                case 9: {
                    final AST __t4 = _t;
                    final StringTemplateAST tmp2_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 9);
                    _t = _t.getFirstChild();
                    e = this.expr(_t);
                    _t = this._retTree;
                    _t = __t4;
                    _t = _t.getNextSibling();
                    final StringWriter buf = new StringWriter();
                    final StringTemplateWriter sw = this.self.getGroup().getStringTemplateWriter(buf);
                    final int n = this.chunk.writeAttribute(this.self, e, sw);
                    if (n > 0) {
                        value = buf.toString();
                        break;
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return value;
    }
    
    public final Object templateApplication(AST _t) throws RecognitionException {
        Object value = null;
        final StringTemplateAST templateApplication_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        StringTemplateAST anon = null;
        Object a = null;
        final Vector templatesToApply = new Vector();
        final List attributes = new ArrayList();
        try {
            if (_t == null) {
                _t = ActionEvaluator.ASTNULL;
            }
            switch (_t.getType()) {
                case 4: {
                    final AST __t14 = _t;
                    final StringTemplateAST tmp3_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 4);
                    _t = _t.getFirstChild();
                    a = this.expr(_t);
                    _t = this._retTree;
                    int _cnt16 = 0;
                    while (true) {
                        if (_t == null) {
                            _t = ActionEvaluator.ASTNULL;
                        }
                        if (_t.getType() != 10) {
                            break;
                        }
                        this.template(_t, templatesToApply);
                        _t = this._retTree;
                        ++_cnt16;
                    }
                    if (_cnt16 >= 1) {
                        value = this.chunk.applyListOfAlternatingTemplates(this.self, a, templatesToApply);
                        _t = __t14;
                        _t = _t.getNextSibling();
                        break;
                    }
                    throw new NoViableAltException(_t);
                }
                case 5: {
                    final AST __t15 = _t;
                    final StringTemplateAST tmp4_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 5);
                    _t = _t.getFirstChild();
                    int _cnt17 = 0;
                    while (true) {
                        if (_t == null) {
                            _t = ActionEvaluator.ASTNULL;
                        }
                        if (!ActionEvaluator._tokenSet_0.member(_t.getType())) {
                            break;
                        }
                        a = this.expr(_t);
                        _t = this._retTree;
                        attributes.add(a);
                        ++_cnt17;
                    }
                    if (_cnt17 >= 1) {
                        final StringTemplateAST tmp5_AST_in = (StringTemplateAST)_t;
                        this.match(_t, 22);
                        _t = _t.getNextSibling();
                        anon = (StringTemplateAST)_t;
                        this.match(_t, 33);
                        _t = _t.getNextSibling();
                        final StringTemplate anonymous = anon.getStringTemplate();
                        templatesToApply.addElement(anonymous);
                        value = this.chunk.applyTemplateToListOfAttributes(this.self, attributes, anon.getStringTemplate());
                        _t = __t15;
                        _t = _t.getNextSibling();
                        break;
                    }
                    throw new NoViableAltException(_t);
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return value;
    }
    
    public final Object attribute(AST _t) throws RecognitionException {
        Object value = null;
        final StringTemplateAST attribute_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        StringTemplateAST prop = null;
        StringTemplateAST i3 = null;
        StringTemplateAST j = null;
        StringTemplateAST s = null;
        StringTemplateAST at = null;
        Object obj = null;
        Object propName = null;
        Object e = null;
        try {
            if (_t == null) {
                _t = ActionEvaluator.ASTNULL;
            }
            switch (_t.getType()) {
                case 25: {
                    final AST __t33 = _t;
                    final StringTemplateAST tmp6_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 25);
                    _t = _t.getFirstChild();
                    obj = this.expr(_t);
                    _t = this._retTree;
                    if (_t == null) {
                        _t = ActionEvaluator.ASTNULL;
                    }
                    switch (_t.getType()) {
                        case 20: {
                            prop = (StringTemplateAST)_t;
                            this.match(_t, 20);
                            _t = _t.getNextSibling();
                            propName = prop.getText();
                            break;
                        }
                        case 9: {
                            final AST __t34 = _t;
                            final StringTemplateAST tmp7_AST_in = (StringTemplateAST)_t;
                            this.match(_t, 9);
                            _t = _t.getFirstChild();
                            e = this.expr(_t);
                            _t = this._retTree;
                            _t = __t34;
                            _t = _t.getNextSibling();
                            if (e != null) {
                                propName = e;
                                break;
                            }
                            break;
                        }
                        default: {
                            throw new NoViableAltException(_t);
                        }
                    }
                    _t = __t33;
                    _t = _t.getNextSibling();
                    value = this.chunk.getObjectProperty(this.self, obj, propName);
                    break;
                }
                case 20: {
                    i3 = (StringTemplateAST)_t;
                    this.match(_t, 20);
                    _t = _t.getNextSibling();
                    value = this.self.getAttribute(i3.getText());
                    break;
                }
                case 35: {
                    j = (StringTemplateAST)_t;
                    this.match(_t, 35);
                    _t = _t.getNextSibling();
                    value = new Integer(j.getText());
                    break;
                }
                case 34: {
                    s = (StringTemplateAST)_t;
                    this.match(_t, 34);
                    _t = _t.getNextSibling();
                    value = s.getText();
                    break;
                }
                case 33: {
                    at = (StringTemplateAST)_t;
                    this.match(_t, 33);
                    _t = _t.getNextSibling();
                    value = at.getText();
                    if (at.getText() != null) {
                        final StringTemplate valueST = new StringTemplate(this.self.getGroup(), at.getText());
                        valueST.setEnclosingInstance(this.self);
                        valueST.setName("<anonymous template argument>");
                        value = valueST;
                        break;
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return value;
    }
    
    public final Object templateInclude(AST _t) throws RecognitionException {
        Object value = null;
        final StringTemplateAST templateInclude_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        StringTemplateAST id = null;
        StringTemplateAST a1 = null;
        StringTemplateAST a2 = null;
        StringTemplateAST args = null;
        String name = null;
        Object n = null;
        try {
            final AST __t10 = _t;
            final StringTemplateAST tmp8_AST_in = (StringTemplateAST)_t;
            this.match(_t, 7);
            _t = _t.getFirstChild();
            if (_t == null) {
                _t = ActionEvaluator.ASTNULL;
            }
            switch (_t.getType()) {
                case 20: {
                    id = (StringTemplateAST)_t;
                    this.match(_t, 20);
                    _t = _t.getNextSibling();
                    a1 = (StringTemplateAST)_t;
                    if (_t == null) {
                        throw new MismatchedTokenException();
                    }
                    _t = _t.getNextSibling();
                    name = id.getText();
                    args = a1;
                    break;
                }
                case 9: {
                    final AST __t11 = _t;
                    final StringTemplateAST tmp9_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 9);
                    _t = _t.getFirstChild();
                    n = this.expr(_t);
                    _t = this._retTree;
                    a2 = (StringTemplateAST)_t;
                    if (_t == null) {
                        throw new MismatchedTokenException();
                    }
                    _t = _t.getNextSibling();
                    _t = __t11;
                    _t = _t.getNextSibling();
                    if (n != null) {
                        name = n.toString();
                    }
                    args = a2;
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            _t = __t10;
            _t = _t.getNextSibling();
            if (name != null) {
                value = this.chunk.getTemplateInclude(this.self, name, args);
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return value;
    }
    
    public final Object function(AST _t) throws RecognitionException {
        Object value = null;
        final StringTemplateAST function_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        try {
            final AST __t21 = _t;
            final StringTemplateAST tmp10_AST_in = (StringTemplateAST)_t;
            this.match(_t, 11);
            _t = _t.getFirstChild();
            if (_t == null) {
                _t = ActionEvaluator.ASTNULL;
            }
            switch (_t.getType()) {
                case 26: {
                    final StringTemplateAST tmp11_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 26);
                    _t = _t.getNextSibling();
                    final Object a = this.singleFunctionArg(_t);
                    _t = this._retTree;
                    value = this.chunk.first(a);
                    break;
                }
                case 27: {
                    final StringTemplateAST tmp12_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 27);
                    _t = _t.getNextSibling();
                    final Object a = this.singleFunctionArg(_t);
                    _t = this._retTree;
                    value = this.chunk.rest(a);
                    break;
                }
                case 28: {
                    final StringTemplateAST tmp13_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 28);
                    _t = _t.getNextSibling();
                    final Object a = this.singleFunctionArg(_t);
                    _t = this._retTree;
                    value = this.chunk.last(a);
                    break;
                }
                case 29: {
                    final StringTemplateAST tmp14_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 29);
                    _t = _t.getNextSibling();
                    final Object a = this.singleFunctionArg(_t);
                    _t = this._retTree;
                    value = this.chunk.length(a);
                    break;
                }
                case 30: {
                    final StringTemplateAST tmp15_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 30);
                    _t = _t.getNextSibling();
                    final Object a = this.singleFunctionArg(_t);
                    _t = this._retTree;
                    value = this.chunk.strip(a);
                    break;
                }
                case 31: {
                    final StringTemplateAST tmp16_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 31);
                    _t = _t.getNextSibling();
                    final Object a = this.singleFunctionArg(_t);
                    _t = this._retTree;
                    value = this.chunk.trunc(a);
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            _t = __t21;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return value;
    }
    
    public final Object list(AST _t) throws RecognitionException {
        Object value = null;
        final StringTemplateAST list_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        Object e = null;
        final List elements = new ArrayList();
        Label_0356: {
            try {
                final AST __t6 = _t;
                final StringTemplateAST tmp17_AST_in = (StringTemplateAST)_t;
                this.match(_t, 13);
                _t = _t.getFirstChild();
                int _cnt8 = 0;
                while (true) {
                    if (_t == null) {
                        _t = ActionEvaluator.ASTNULL;
                    }
                    switch (_t.getType()) {
                        case 4:
                        case 5:
                        case 7:
                        case 9:
                        case 11:
                        case 13:
                        case 20:
                        case 24:
                        case 25:
                        case 33:
                        case 34:
                        case 35: {
                            e = this.expr(_t);
                            _t = this._retTree;
                            if (e != null) {
                                elements.add(e);
                                break;
                            }
                            break;
                        }
                        case 14: {
                            final StringTemplateAST tmp18_AST_in = (StringTemplateAST)_t;
                            this.match(_t, 14);
                            _t = _t.getNextSibling();
                            final List nullSingleton = new ArrayList() {
                                {
                                    this.add(null);
                                }
                            };
                            elements.add(nullSingleton.iterator());
                            break;
                        }
                        default: {
                            if (_cnt8 >= 1) {
                                _t = __t6;
                                _t = _t.getNextSibling();
                                value = new Cat(elements);
                                break Label_0356;
                            }
                            throw new NoViableAltException(_t);
                        }
                    }
                    ++_cnt8;
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
        }
        this._retTree = _t;
        return value;
    }
    
    public final void template(AST _t, final Vector templatesToApply) throws RecognitionException {
        final StringTemplateAST template_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        StringTemplateAST t = null;
        StringTemplateAST args = null;
        StringTemplateAST anon = null;
        StringTemplateAST args2 = null;
        final Map argumentContext = null;
        Object n = null;
        try {
            final AST __t26 = _t;
            final StringTemplateAST tmp19_AST_in = (StringTemplateAST)_t;
            this.match(_t, 10);
            _t = _t.getFirstChild();
            if (_t == null) {
                _t = ActionEvaluator.ASTNULL;
            }
            switch (_t.getType()) {
                case 20: {
                    t = (StringTemplateAST)_t;
                    this.match(_t, 20);
                    _t = _t.getNextSibling();
                    args = (StringTemplateAST)_t;
                    if (_t == null) {
                        throw new MismatchedTokenException();
                    }
                    _t = _t.getNextSibling();
                    final String templateName = t.getText();
                    final StringTemplateGroup group = this.self.getGroup();
                    final StringTemplate embedded = group.getEmbeddedInstanceOf(this.self, templateName);
                    if (embedded != null) {
                        embedded.setArgumentsAST(args);
                        templatesToApply.addElement(embedded);
                        break;
                    }
                    break;
                }
                case 33: {
                    anon = (StringTemplateAST)_t;
                    this.match(_t, 33);
                    _t = _t.getNextSibling();
                    final StringTemplate anonymous = anon.getStringTemplate();
                    anonymous.setGroup(this.self.getGroup());
                    templatesToApply.addElement(anonymous);
                    break;
                }
                case 9: {
                    final AST __t27 = _t;
                    final StringTemplateAST tmp20_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 9);
                    _t = _t.getFirstChild();
                    n = this.expr(_t);
                    _t = this._retTree;
                    args2 = (StringTemplateAST)_t;
                    if (_t == null) {
                        throw new MismatchedTokenException();
                    }
                    _t = _t.getNextSibling();
                    StringTemplate embedded = null;
                    if (n != null) {
                        final String templateName2 = n.toString();
                        final StringTemplateGroup group2 = this.self.getGroup();
                        embedded = group2.getEmbeddedInstanceOf(this.self, templateName2);
                        if (embedded != null) {
                            embedded.setArgumentsAST(args2);
                            templatesToApply.addElement(embedded);
                        }
                    }
                    _t = __t27;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            _t = __t26;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
    }
    
    public final Object singleFunctionArg(AST _t) throws RecognitionException {
        Object value = null;
        final StringTemplateAST singleFunctionArg_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        try {
            final AST __t24 = _t;
            final StringTemplateAST tmp21_AST_in = (StringTemplateAST)_t;
            this.match(_t, 12);
            _t = _t.getFirstChild();
            value = this.expr(_t);
            _t = this._retTree;
            _t = __t24;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return value;
    }
    
    public final boolean ifCondition(AST _t) throws RecognitionException {
        boolean value = false;
        final StringTemplateAST ifCondition_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        Object a = null;
        final Object b = null;
        try {
            if (_t == null) {
                _t = ActionEvaluator.ASTNULL;
            }
            switch (_t.getType()) {
                case 4:
                case 5:
                case 7:
                case 9:
                case 11:
                case 13:
                case 20:
                case 24:
                case 25:
                case 33:
                case 34:
                case 35: {
                    a = this.ifAtom(_t);
                    _t = this._retTree;
                    value = this.chunk.testAttributeTrue(a);
                    break;
                }
                case 23: {
                    final AST __t30 = _t;
                    final StringTemplateAST tmp22_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 23);
                    _t = _t.getFirstChild();
                    a = this.ifAtom(_t);
                    _t = this._retTree;
                    _t = __t30;
                    _t = _t.getNextSibling();
                    value = !this.chunk.testAttributeTrue(a);
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return value;
    }
    
    public final Object ifAtom(AST _t) throws RecognitionException {
        Object value = null;
        final StringTemplateAST ifAtom_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        try {
            value = this.expr(_t);
            _t = this._retTree;
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return value;
    }
    
    public final Map argList(AST _t, final StringTemplate embedded, final Map initialContext) throws RecognitionException {
        Map argumentContext = null;
        final StringTemplateAST argList_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        argumentContext = initialContext;
        if (argumentContext == null) {
            argumentContext = new HashMap();
        }
        try {
            if (_t == null) {
                _t = ActionEvaluator.ASTNULL;
            }
            switch (_t.getType()) {
                case 6: {
                    final AST __t37 = _t;
                    final StringTemplateAST tmp23_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 6);
                    _t = _t.getFirstChild();
                    while (true) {
                        if (_t == null) {
                            _t = ActionEvaluator.ASTNULL;
                        }
                        if (_t.getType() != 21 && _t.getType() != 38) {
                            break;
                        }
                        this.argumentAssignment(_t, embedded, argumentContext);
                        _t = this._retTree;
                    }
                    _t = __t37;
                    _t = _t.getNextSibling();
                    break;
                }
                case 12: {
                    this.singleTemplateArg(_t, embedded, argumentContext);
                    _t = this._retTree;
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return argumentContext;
    }
    
    public final void argumentAssignment(AST _t, final StringTemplate embedded, final Map argumentContext) throws RecognitionException {
        final StringTemplateAST argumentAssignment_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        StringTemplateAST arg = null;
        Object e = null;
        try {
            if (_t == null) {
                _t = ActionEvaluator.ASTNULL;
            }
            switch (_t.getType()) {
                case 21: {
                    final AST __t43 = _t;
                    final StringTemplateAST tmp24_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 21);
                    _t = _t.getFirstChild();
                    arg = (StringTemplateAST)_t;
                    this.match(_t, 20);
                    _t = _t.getNextSibling();
                    e = this.expr(_t);
                    _t = this._retTree;
                    _t = __t43;
                    _t = _t.getNextSibling();
                    if (e != null) {
                        this.self.rawSetArgumentAttribute(embedded, argumentContext, arg.getText(), e);
                        break;
                    }
                    break;
                }
                case 38: {
                    final StringTemplateAST tmp25_AST_in = (StringTemplateAST)_t;
                    this.match(_t, 38);
                    _t = _t.getNextSibling();
                    embedded.setPassThroughAttributes(true);
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
    }
    
    public final void singleTemplateArg(AST _t, final StringTemplate embedded, final Map argumentContext) throws RecognitionException {
        final StringTemplateAST singleTemplateArg_AST_in = (_t == ActionEvaluator.ASTNULL) ? null : ((StringTemplateAST)_t);
        Object e = null;
        try {
            final AST __t41 = _t;
            final StringTemplateAST tmp26_AST_in = (StringTemplateAST)_t;
            this.match(_t, 12);
            _t = _t.getFirstChild();
            e = this.expr(_t);
            _t = this._retTree;
            _t = __t41;
            _t = _t.getNextSibling();
            if (e != null) {
                String soleArgName = null;
                boolean error = false;
                final Map formalArgs = embedded.getFormalArguments();
                if (formalArgs != null) {
                    final Set argNames = formalArgs.keySet();
                    if (argNames.size() == 1) {
                        soleArgName = (String)argNames.toArray()[0];
                    }
                    else {
                        error = true;
                    }
                }
                else {
                    error = true;
                }
                if (error) {
                    this.self.error("template " + embedded.getName() + " must have exactly one formal arg in template context " + this.self.getEnclosingInstanceStackString());
                }
                else {
                    this.self.rawSetArgumentAttribute(embedded, argumentContext, soleArgName, e);
                }
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            if (_t != null) {
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
    }
    
    private static final long[] mk_tokenSet_0() {
        final long[] data = { 60180933296L, 0L };
        return data;
    }
    
    static {
        _tokenNames = new String[] { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "APPLY", "MULTI_APPLY", "ARGS", "INCLUDE", "\"if\"", "VALUE", "TEMPLATE", "FUNCTION", "SINGLEVALUEARG", "LIST", "NOTHING", "SEMI", "LPAREN", "RPAREN", "\"elseif\"", "COMMA", "ID", "ASSIGN", "COLON", "NOT", "PLUS", "DOT", "\"first\"", "\"rest\"", "\"last\"", "\"length\"", "\"strip\"", "\"trunc\"", "\"super\"", "ANONYMOUS_TEMPLATE", "STRING", "INT", "LBRACK", "RBRACK", "DOTDOTDOT", "TEMPLATE_ARGS", "NESTED_ANONYMOUS_TEMPLATE", "ESC_CHAR", "WS", "WS_CHAR" };
        _tokenSet_0 = new BitSet(mk_tokenSet_0());
    }
    
    public static class NameValuePair
    {
        public String name;
        public Object value;
    }
}
