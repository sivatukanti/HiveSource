// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.message;

import org.apache.kerby.KOptions;
import org.apache.kerby.xdr.type.AbstractXdrType;
import org.apache.kerby.xdr.type.XdrString;
import org.apache.kerby.xdr.type.XdrInteger;
import org.apache.kerby.xdr.type.XdrType;
import org.apache.kerby.xdr.XdrFieldInfo;
import org.apache.kerby.xdr.XdrDataType;
import org.apache.kerby.xdr.type.XdrStructType;

public class AdminMessageCode extends XdrStructType
{
    public AdminMessageCode() {
        super(XdrDataType.STRUCT);
    }
    
    public AdminMessageCode(final XdrFieldInfo[] fieldInfos) {
        super(XdrDataType.STRUCT, fieldInfos);
    }
    
    @Override
    protected void getStructTypeInstance(final XdrType[] fields, final XdrFieldInfo[] fieldInfos) {
        for (int i = 0; i < fieldInfos.length; ++i) {
            switch (fieldInfos[i].getDataType()) {
                case INTEGER: {
                    fields[i] = new XdrInteger((Integer)fieldInfos[i].getValue());
                    break;
                }
                case ENUM: {
                    fields[i] = new AdminMessageEnum((AdminMessageType)fieldInfos[i].getValue());
                    break;
                }
                case STRING: {
                    fields[i] = new XdrString((String)fieldInfos[i].getValue());
                    break;
                }
                default: {
                    fields[i] = null;
                    break;
                }
            }
        }
    }
    
    @Override
    protected XdrStructType fieldsToValues(final AbstractXdrType[] fields) {
        final int paramNum = fields[1].getValue();
        final XdrFieldInfo[] xdrFieldInfos = new XdrFieldInfo[paramNum + 2];
        xdrFieldInfos[0] = new XdrFieldInfo(0, XdrDataType.ENUM, fields[0].getValue());
        xdrFieldInfos[1] = new XdrFieldInfo(1, XdrDataType.INTEGER, fields[1].getValue());
        xdrFieldInfos[2] = new XdrFieldInfo(2, XdrDataType.STRING, fields[2].getValue());
        if (paramNum == 2 && fields[3].getValue() instanceof KOptions) {
            xdrFieldInfos[3] = new XdrFieldInfo(3, XdrDataType.STRUCT, fields[3].getValue());
        }
        else if (paramNum == 2 && fields[3].getValue() instanceof String) {
            xdrFieldInfos[3] = new XdrFieldInfo(3, XdrDataType.STRING, fields[3].getValue());
        }
        else if (paramNum == 3) {
            xdrFieldInfos[3] = new XdrFieldInfo(3, XdrDataType.STRUCT, fields[3].getValue());
            xdrFieldInfos[4] = new XdrFieldInfo(4, XdrDataType.STRING, fields[4].getValue());
        }
        return new AdminMessageCode(xdrFieldInfos);
    }
    
    @Override
    protected AbstractXdrType[] getAllFields() {
        final AbstractXdrType[] fields = { new AdminMessageEnum(), new XdrInteger(), new XdrString(), new XdrString(), null };
        return fields;
    }
}
