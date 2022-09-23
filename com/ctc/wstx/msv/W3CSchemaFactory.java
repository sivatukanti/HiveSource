// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.msv;

import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.xml.sax.InputSource;
import com.sun.msv.reader.util.IgnoreController;
import com.sun.msv.reader.GrammarReaderController;

public class W3CSchemaFactory extends BaseSchemaFactory
{
    protected final GrammarReaderController mDummyController;
    
    public W3CSchemaFactory() {
        super("http://relaxng.org/ns/structure/0.9");
        this.mDummyController = (GrammarReaderController)new IgnoreController();
    }
    
    @Override
    protected XMLValidationSchema loadSchema(final InputSource src, final Object sysRef) throws XMLStreamException {
        final SAXParserFactory saxFactory = BaseSchemaFactory.getSaxFactory();
        final MyGrammarController ctrl = new MyGrammarController();
        final XMLSchemaGrammar grammar = XMLSchemaReader.parse(src, saxFactory, (GrammarReaderController)ctrl);
        if (grammar == null) {
            String msg = "Failed to load W3C Schema from '" + sysRef + "'";
            final String emsg = ctrl.mErrorMsg;
            if (emsg != null) {
                msg = msg + ": " + emsg;
            }
            throw new XMLStreamException(msg);
        }
        return new W3CSchema(grammar);
    }
}
