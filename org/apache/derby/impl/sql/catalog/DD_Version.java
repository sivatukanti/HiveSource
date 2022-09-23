// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.services.info.ProductVersionHolder;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.store.access.ConglomerateController;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.store.access.ScanController;
import org.apache.derby.iapi.sql.execute.ExecIndexRow;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.CatalogRowFactory;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import java.util.HashSet;
import java.io.Serializable;
import org.apache.derby.iapi.util.IdUtil;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.services.io.Formatable;

public class DD_Version implements Formatable
{
    private transient DataDictionaryImpl bootingDictionary;
    int majorVersionNumber;
    private int minorVersionNumber;
    
    public DD_Version() {
    }
    
    DD_Version(final DataDictionaryImpl bootingDictionary, final int majorVersionNumber) {
        this.majorVersionNumber = majorVersionNumber;
        this.minorVersionNumber = this.getJBMSMinorVersionNumber();
        this.bootingDictionary = bootingDictionary;
    }
    
    public String toString() {
        return majorToString(this.majorVersionNumber);
    }
    
    private static String majorToString(final int n) {
        switch (n) {
            case 80: {
                return "5.0";
            }
            case 90: {
                return "5.1";
            }
            case 100: {
                return "5.2";
            }
            case 110: {
                return "8.1";
            }
            case 120: {
                return "10.0";
            }
            case 130: {
                return "10.1";
            }
            case 140: {
                return "10.2";
            }
            case 150: {
                return "10.3";
            }
            case 160: {
                return "10.4";
            }
            case 170: {
                return "10.5";
            }
            case 180: {
                return "10.6";
            }
            case 190: {
                return "10.7";
            }
            case 200: {
                return "10.8";
            }
            case 210: {
                return "10.9";
            }
            case 220: {
                return "10.10";
            }
            default: {
                return null;
            }
        }
    }
    
    void upgradeIfNeeded(final DD_Version dd_Version, final TransactionController transactionController, final Properties properties) throws StandardException {
        if (dd_Version.majorVersionNumber > this.majorVersionNumber) {
            throw StandardException.newException("XCL20.S", dd_Version, this);
        }
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        final boolean readOnly = this.bootingDictionary.af.isReadOnly();
        if (dd_Version.majorVersionNumber == this.majorVersionNumber) {
            if (dd_Version.minorVersionNumber == this.minorVersionNumber) {
                return;
            }
            b = true;
        }
        else if (Monitor.isFullUpgrade(properties, dd_Version.toString())) {
            b2 = true;
        }
        else {
            b3 = true;
        }
        transactionController.commit();
        if (b2) {
            this.doFullUpgrade(transactionController, dd_Version.majorVersionNumber, IdUtil.getUserAuthorizationId(IdUtil.getUserNameFromURLProps(properties)));
            this.bootingDictionary.af.createReadMeFiles();
        }
        if (!b && !readOnly) {
            final DD_Version dd_Version2 = (DD_Version)transactionController.getProperty("derby.softDataDictionaryVersion");
            int majorVersionNumber = 0;
            if (dd_Version2 != null) {
                majorVersionNumber = dd_Version2.majorVersionNumber;
            }
            if (majorVersionNumber < this.majorVersionNumber) {
                this.applySafeChanges(transactionController, dd_Version.majorVersionNumber, majorVersionNumber);
            }
        }
        this.handleMinorRevisionChange(transactionController, dd_Version, b3);
        transactionController.commit();
    }
    
    private void applySafeChanges(final TransactionController transactionController, final int n, final int n2) throws StandardException {
        if (n2 <= 140 && n <= 140) {
            this.modifySysTableNullability(transactionController, 11);
            this.modifySysTableNullability(transactionController, 8);
        }
        transactionController.setProperty("derby.softDataDictionaryVersion", this, true);
    }
    
    private void doFullUpgrade(final TransactionController transactionController, final int n, final String s) throws StandardException {
        if (n < 120) {
            throw StandardException.newException("XCW00.D", majorToString(n), this);
        }
        this.bootingDictionary.updateMetadataSPSes(transactionController);
        final HashSet<String> set = new HashSet<String>();
        if (n <= 150) {
            this.bootingDictionary.upgradeMakeCatalog(transactionController, 19);
        }
        if (n <= 130) {
            this.bootingDictionary.upgradeMakeCatalog(transactionController, 16);
            this.bootingDictionary.upgradeMakeCatalog(transactionController, 17);
            this.bootingDictionary.upgradeMakeCatalog(transactionController, 18);
        }
        if (n == 120) {
            this.bootingDictionary.create_10_1_system_procedures(transactionController, set, this.bootingDictionary.getSystemUtilSchemaDescriptor().getUUID());
        }
        if (n <= 170) {
            this.bootingDictionary.create_10_6_system_procedures(transactionController, set);
            this.bootingDictionary.upgradeMakeCatalog(transactionController, 20);
            this.bootingDictionary.upgradeMakeCatalog(transactionController, 21);
        }
        if (n <= 130) {
            this.bootingDictionary.create_10_2_system_procedures(transactionController, set, this.bootingDictionary.getSystemUtilSchemaDescriptor().getUUID());
            this.bootingDictionary.updateSystemSchemaAuthorization(s, transactionController);
            set.add("SYSCS_INPLACE_COMPRESS_TABLE");
            set.add("SYSCS_GET_RUNTIMESTATISTICS");
            set.add("SYSCS_SET_RUNTIMESTATISTICS");
            set.add("SYSCS_COMPRESS_TABLE");
            set.add("SYSCS_SET_STATISTICS_TIMING");
        }
        if (n <= 140) {
            this.bootingDictionary.create_10_3_system_procedures(transactionController, set);
        }
        if (n <= 160) {
            this.bootingDictionary.create_10_5_system_procedures(transactionController, set);
        }
        if (n > 140 && n < 180) {
            this.bootingDictionary.upgradeCLOBGETSUBSTRING_10_6(transactionController);
        }
        if (n > 130 && n < 180) {
            this.bootingDictionary.upgradeSYSROUTINEPERMS_10_6(transactionController);
        }
        if (n <= 200) {
            this.bootingDictionary.create_10_9_system_procedures(transactionController, set);
            this.bootingDictionary.upgradeMakeCatalog(transactionController, 22);
            this.bootingDictionary.upgradeJarStorage(transactionController);
        }
        if (n <= 210) {
            this.bootingDictionary.create_10_10_system_procedures(transactionController, set);
        }
        this.bootingDictionary.grantPublicAccessToSystemRoutines(set, transactionController, s);
    }
    
    private void handleMinorRevisionChange(final TransactionController transactionController, final DD_Version dd_Version, final boolean b) throws StandardException {
        if (!this.bootingDictionary.af.isReadOnly()) {
            this.bootingDictionary.clearSPSPlans();
            if (dd_Version.majorVersionNumber >= 170) {
                this.bootingDictionary.updateMetadataSPSes(transactionController);
            }
            if (b) {
                dd_Version.minorVersionNumber = 1;
            }
            else {
                dd_Version.majorVersionNumber = this.majorVersionNumber;
                dd_Version.minorVersionNumber = this.minorVersionNumber;
            }
            transactionController.setProperty("DataDictionaryVersion", dd_Version, true);
        }
        else {
            this.bootingDictionary.setReadOnlyUpgrade();
        }
        this.bootingDictionary.clearCaches();
    }
    
    protected void makeSystemCatalog(final TransactionController transactionController, final TabInfoImpl tabInfoImpl) throws StandardException {
        this.bootingDictionary.makeCatalog(tabInfoImpl, this.bootingDictionary.getSystemSchemaDescriptor(), transactionController);
    }
    
    protected void dropSystemCatalogDescription(final TransactionController transactionController, final TableDescriptor tableDescriptor) throws StandardException {
        this.bootingDictionary.dropAllColumnDescriptors(tableDescriptor.getUUID(), transactionController);
        this.bootingDictionary.dropAllConglomerateDescriptors(tableDescriptor, transactionController);
        this.bootingDictionary.dropTableDescriptor(tableDescriptor, tableDescriptor.getSchemaDescriptor(), transactionController);
        this.bootingDictionary.clearCaches();
    }
    
    protected void dropSystemCatalog(final TransactionController transactionController, final CatalogRowFactory catalogRowFactory) throws StandardException {
        final TableDescriptor tableDescriptor = this.bootingDictionary.getTableDescriptor(catalogRowFactory.getCatalogName(), this.bootingDictionary.getSystemSchemaDescriptor(), transactionController);
        final ConglomerateDescriptor[] conglomerateDescriptors = tableDescriptor.getConglomerateDescriptors();
        for (int i = 0; i < conglomerateDescriptors.length; ++i) {
            transactionController.dropConglomerate(conglomerateDescriptors[i].getConglomerateNumber());
        }
        this.dropSystemCatalogDescription(transactionController, tableDescriptor);
    }
    
    protected void fillIndex(final TransactionController transactionController, final long n, final TabInfoImpl tabInfoImpl, final int n2) throws StandardException {
        final long indexConglomerate = tabInfoImpl.getIndexConglomerate(n2);
        final IndexRowGenerator indexRowGenerator = tabInfoImpl.getIndexRowGenerator(n2);
        final ExecRow emptyRow = tabInfoImpl.getCatalogRowFactory().makeEmptyRow();
        final ExecIndexRow indexRowTemplate = indexRowGenerator.getIndexRowTemplate();
        final ScanController openScan = transactionController.openScan(n, false, 0, 7, 4, null, null, 1, null, null, -1);
        final RowLocation rowLocationTemplate = openScan.newRowLocationTemplate();
        final ConglomerateController openConglomerate = transactionController.openConglomerate(indexConglomerate, false, 4, 7, 4);
        while (openScan.fetchNext(emptyRow.getRowArray())) {
            openScan.fetchLocation(rowLocationTemplate);
            indexRowGenerator.getIndexRow(emptyRow, rowLocationTemplate, indexRowTemplate, null);
            openConglomerate.insert(indexRowTemplate.getRowArray());
        }
        openConglomerate.close();
        openScan.close();
    }
    
    public int getTypeFormatId() {
        return (this.majorVersionNumber == 90) ? 402 : 401;
    }
    
    public final void readExternal(final ObjectInput objectInput) throws IOException {
        this.majorVersionNumber = objectInput.readInt();
        this.minorVersionNumber = objectInput.readInt();
    }
    
    public final void writeExternal(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeInt(this.majorVersionNumber);
        objectOutput.writeInt(this.minorVersionNumber);
    }
    
    private int getJBMSMinorVersionNumber() {
        final ProductVersionHolder engineVersion = Monitor.getMonitor().getEngineVersion();
        return engineVersion.getMinorVersion() * 100 + engineVersion.getMaintVersion() + (engineVersion.isBeta() ? 0 : 1) + 2;
    }
    
    private void modifySysTableNullability(final TransactionController transactionController, final int n) throws StandardException {
        final CatalogRowFactory catalogRowFactory = this.bootingDictionary.getNonCoreTIByNumber(n).getCatalogRowFactory();
        if (n == 11) {
            this.bootingDictionary.upgradeFixSystemColumnDefinition(catalogRowFactory, 8, transactionController);
        }
        else if (n == 8) {
            this.bootingDictionary.upgradeFixSystemColumnDefinition(catalogRowFactory, 4, transactionController);
        }
    }
    
    boolean checkVersion(final int n, final String s) throws StandardException {
        if (this.majorVersionNumber >= n) {
            return true;
        }
        if (s != null) {
            throw StandardException.newException("XCL47.S", s, majorToString(this.majorVersionNumber), majorToString(n));
        }
        return false;
    }
}
