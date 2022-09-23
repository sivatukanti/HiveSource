// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.cache.ClassSize;
import java.util.List;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;
import java.io.InputStream;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.io.IOException;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.StreamStorable;

public class XML extends DataType implements XMLDataValue, StreamStorable
{
    protected static final short UTF8_IMPL_ID = 0;
    private static final int BASE_MEMORY_USAGE;
    public static final short XQ_PASS_BY_REF = 1;
    public static final short XQ_PASS_BY_VALUE = 2;
    public static final short XQ_RETURN_SEQUENCE = 3;
    public static final short XQ_RETURN_CONTENT = 4;
    public static final short XQ_EMPTY_ON_EMPTY = 5;
    public static final short XQ_NULL_ON_EMPTY = 6;
    public static final int XML_DOC_ANY = 0;
    public static final int XML_SEQUENCE = 1;
    private int xType;
    private SQLChar xmlStringValue;
    private static String xmlReqCheck;
    private boolean containsTopLevelAttr;
    private SqlXmlUtil tmpUtil;
    
    public XML() {
        this.xmlStringValue = null;
        this.xType = -1;
        this.containsTopLevelAttr = false;
    }
    
    private XML(final SQLChar sqlChar, final int xType, final boolean b, final boolean b2) {
        this.xmlStringValue = ((sqlChar == null) ? null : ((SQLChar)sqlChar.cloneValue(b2)));
        this.setXType(xType);
        if (b) {
            this.markAsHavingTopLevelAttr();
        }
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        return new XML(this.xmlStringValue, this.getXType(), this.hasTopLevelAttr(), b);
    }
    
    public DataValueDescriptor getNewNull() {
        return new XML();
    }
    
    public String getTypeName() {
        return "XML";
    }
    
    public int typePrecedence() {
        return 180;
    }
    
    public String getString() throws StandardException {
        return (this.xmlStringValue == null) ? null : this.xmlStringValue.getString();
    }
    
    public int getLength() throws StandardException {
        return (this.xmlStringValue == null) ? 0 : this.xmlStringValue.getLength();
    }
    
    public int estimateMemoryUsage() {
        int base_MEMORY_USAGE = XML.BASE_MEMORY_USAGE;
        if (this.xmlStringValue != null) {
            base_MEMORY_USAGE += this.xmlStringValue.estimateMemoryUsage();
        }
        return base_MEMORY_USAGE;
    }
    
    public void readExternalFromArray(final ArrayInputStream arrayInputStream) throws IOException {
        if (this.xmlStringValue == null) {
            this.xmlStringValue = new SQLChar();
        }
        arrayInputStream.readShort();
        this.xmlStringValue.readExternalFromArray(arrayInputStream);
        this.setXType(0);
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        final String string = dataValueDescriptor.getString();
        if (string == null) {
            this.xmlStringValue = null;
            this.setXType(0);
            return;
        }
        if (this.xmlStringValue == null) {
            this.xmlStringValue = new SQLChar();
        }
        this.xmlStringValue.setValue(string);
        if (dataValueDescriptor instanceof XMLDataValue) {
            this.setXType(((XMLDataValue)dataValueDescriptor).getXType());
            if (((XMLDataValue)dataValueDescriptor).hasTopLevelAttr()) {
                this.markAsHavingTopLevelAttr();
            }
        }
    }
    
    public final void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException {
        if (this.xmlStringValue == null) {
            this.xmlStringValue = new SQLChar();
        }
        String value = set.getString(n);
        if (this.tmpUtil == null) {
            try {
                this.tmpUtil = new SqlXmlUtil();
            }
            catch (StandardException ex) {
                this.xmlStringValue.setValue(value);
                this.setXType(-1);
                return;
            }
        }
        try {
            value = this.tmpUtil.serializeToString(value);
            this.xmlStringValue.setValue(value);
            this.setXType(0);
        }
        catch (Throwable t) {
            this.xmlStringValue.setValue(value);
            this.setXType(-1);
        }
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (this.isNull()) {
            if (dataValueDescriptor.isNull()) {
                return 0;
            }
            return -1;
        }
        else {
            if (dataValueDescriptor.isNull()) {
                return 1;
            }
            return 0;
        }
    }
    
    public void normalize(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor value) throws StandardException {
        if (((XMLDataValue)value).getXType() != 0) {
            throw StandardException.newException("2200L");
        }
        this.setValue(value);
    }
    
    public int getTypeFormatId() {
        return 458;
    }
    
    public boolean isNull() {
        return this.xmlStringValue == null || this.xmlStringValue.isNull();
    }
    
    public void restoreToNull() {
        if (this.xmlStringValue != null) {
            this.xmlStringValue.restoreToNull();
        }
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        if (this.xmlStringValue == null) {
            this.xmlStringValue = new SQLChar();
        }
        objectInput.readShort();
        this.xmlStringValue.readExternal(objectInput);
        this.setXType(0);
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeShort(0);
        this.xmlStringValue.writeExternal(objectOutput);
    }
    
    public InputStream returnStream() {
        return (this.xmlStringValue == null) ? null : this.xmlStringValue.returnStream();
    }
    
    public void setStream(final InputStream stream) {
        if (this.xmlStringValue == null) {
            this.xmlStringValue = new SQLChar();
        }
        try {
            stream.read();
            stream.read();
        }
        catch (Exception ex) {}
        this.xmlStringValue.setStream(stream);
        this.setXType(0);
    }
    
    public void loadStream() throws StandardException {
        this.getString();
    }
    
    public XMLDataValue XMLParse(final StringDataValue stringDataValue, final boolean b, final SqlXmlUtil sqlXmlUtil) throws StandardException {
        if (stringDataValue.isNull()) {
            this.setToNull();
            return this;
        }
        String value = stringDataValue.getString();
        try {
            if (b) {
                value = sqlXmlUtil.serializeToString(value);
            }
        }
        catch (Throwable t) {
            throw StandardException.newException("2200M", t, t.getMessage());
        }
        this.setXType(0);
        if (this.xmlStringValue == null) {
            this.xmlStringValue = new SQLChar();
        }
        this.xmlStringValue.setValue(value);
        return this;
    }
    
    public StringDataValue XMLSerialize(StringDataValue value, final int n, final int n2, final int n3) throws StandardException {
        if (value == null) {
            SQLChar sqlChar = null;
            switch (n) {
                case 1: {
                    sqlChar = new SQLChar();
                    break;
                }
                case 12: {
                    sqlChar = new SQLVarchar();
                    break;
                }
                case -1: {
                    sqlChar = new SQLLongvarchar();
                    break;
                }
                case 2005: {
                    sqlChar = new SQLClob();
                    break;
                }
                default: {
                    return null;
                }
            }
            try {
                value = sqlChar.getValue(ConnectionUtil.getCurrentLCC().getDataValueFactory().getCharacterCollator(n3));
            }
            catch (SQLException ex) {
                throw StandardException.plainWrapException(ex);
            }
        }
        if (this.isNull()) {
            value.setToNull();
            return value;
        }
        if (this.hasTopLevelAttr()) {
            throw StandardException.newException("2200W");
        }
        value.setValue(this.getString());
        value.setWidth(n2, 0, true);
        return value;
    }
    
    public BooleanDataValue XMLExists(final SqlXmlUtil sqlXmlUtil) throws StandardException {
        if (this.isNull()) {
            return SQLBoolean.unknownTruthValue();
        }
        try {
            return new SQLBoolean(null != sqlXmlUtil.evalXQExpression(this, false, new int[1]));
        }
        catch (StandardException ex) {
            throw ex;
        }
        catch (Throwable t) {
            throw StandardException.newException("10000", "XMLEXISTS", t.getMessage());
        }
    }
    
    public XMLDataValue XMLQuery(final SqlXmlUtil sqlXmlUtil, XMLDataValue xmlDataValue) throws StandardException {
        if (this.isNull()) {
            if (xmlDataValue == null) {
                xmlDataValue = (XMLDataValue)this.getNewNull();
            }
            else {
                xmlDataValue.setToNull();
            }
            return xmlDataValue;
        }
        try {
            final int[] array = { 0 };
            final List evalXQExpression = sqlXmlUtil.evalXQExpression(this, true, array);
            if (xmlDataValue == null) {
                xmlDataValue = new XML();
            }
            xmlDataValue.setValue(new SQLChar(sqlXmlUtil.serializeToString(evalXQExpression, xmlDataValue)));
            xmlDataValue.setXType(array[0]);
            return xmlDataValue;
        }
        catch (StandardException ex) {
            throw ex;
        }
        catch (Throwable t) {
            throw StandardException.newException("10000", "XMLQUERY", t.getMessage());
        }
    }
    
    public void setXType(final int xType) {
        this.xType = xType;
        if (xType == 0) {
            this.containsTopLevelAttr = false;
        }
    }
    
    public int getXType() {
        return this.xType;
    }
    
    public void markAsHavingTopLevelAttr() {
        this.containsTopLevelAttr = true;
    }
    
    public boolean hasTopLevelAttr() {
        return this.containsTopLevelAttr;
    }
    
    public static void checkXMLRequirements() throws StandardException {
        if (XML.xmlReqCheck == null) {
            XML.xmlReqCheck = "";
            final Object checkJAXPRequirement = checkJAXPRequirement();
            if (checkJAXPRequirement == null) {
                XML.xmlReqCheck = "JAXP";
            }
            else if (!checkXPathRequirement(checkJAXPRequirement)) {
                XML.xmlReqCheck = "XPath 3.0";
            }
        }
        if (XML.xmlReqCheck.length() != 0) {
            throw StandardException.newException("XML00", XML.xmlReqCheck);
        }
    }
    
    private static Object checkJAXPRequirement() {
        try {
            final Class<?> forName = Class.forName("javax.xml.parsers.DocumentBuilderFactory");
            return Class.forName("javax.xml.parsers.DocumentBuilder").getMethod("getDOMImplementation", (Class<?>[])new Class[0]).invoke(forName.getMethod("newDocumentBuilder", (Class[])new Class[0]).invoke(forName.getMethod("newInstance", (Class<?>[])new Class[0]).invoke(null, new Object[0]), new Object[0]), new Object[0]);
        }
        catch (Throwable t) {
            return null;
        }
    }
    
    private static boolean checkXPathRequirement(final Object obj) {
        try {
            return Class.forName("org.w3c.dom.DOMImplementation").getMethod("getFeature", String.class, String.class).invoke(obj, "+XPath", "3.0") != null;
        }
        catch (Throwable t) {
            return false;
        }
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(XML.class);
        XML.xmlReqCheck = null;
    }
}
