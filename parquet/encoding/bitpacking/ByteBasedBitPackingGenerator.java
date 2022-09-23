// 
// Decompiled by Procyon v0.5.36
// 

package parquet.encoding.bitpacking;

import parquet.bytes.BytesUtils;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

public class ByteBasedBitPackingGenerator
{
    private static final String CLASS_NAME_PREFIX = "ByteBitPacking";
    private static final int PACKER_COUNT = 32;
    
    public static void main(final String[] args) throws Exception {
        final String basePath = args[0];
        generateScheme("ByteBitPackingBE", true, basePath);
        generateScheme("ByteBitPackingLE", false, basePath);
    }
    
    private static void generateScheme(final String className, final boolean msbFirst, final String basePath) throws IOException {
        final File file = new File(basePath + "/parquet/column/values/bitpacking/" + className + ".java").getAbsoluteFile();
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        final FileWriter fw = new FileWriter(file);
        fw.append("package parquet.column.values.bitpacking;\n");
        fw.append("\n");
        fw.append("/**\n");
        if (msbFirst) {
            fw.append(" * Packs from the Most Significant Bit first\n");
        }
        else {
            fw.append(" * Packs from the Least Significant Bit first\n");
        }
        fw.append(" * \n");
        fw.append(" * @author automatically generated\n");
        fw.append(" * @see ByteBasedBitPackingGenerator\n");
        fw.append(" *\n");
        fw.append(" */\n");
        fw.append("public abstract class " + className + " {\n");
        fw.append("\n");
        fw.append("  private static final BytePacker[] packers = new BytePacker[33];\n");
        fw.append("  static {\n");
        for (int i = 0; i <= 32; ++i) {
            fw.append("    packers[" + i + "] = new Packer" + i + "();\n");
        }
        fw.append("  }\n");
        fw.append("\n");
        fw.append("  public static final BytePackerFactory factory = new BytePackerFactory() {\n");
        fw.append("    public BytePacker newBytePacker(int bitWidth) {\n");
        fw.append("      return packers[bitWidth];\n");
        fw.append("    }\n");
        fw.append("  };\n");
        fw.append("\n");
        for (int i = 0; i <= 32; ++i) {
            generateClass(fw, i, msbFirst);
            fw.append("\n");
        }
        fw.append("}\n");
        fw.close();
    }
    
    private static void generateClass(final FileWriter fw, final int bitWidth, final boolean msbFirst) throws IOException {
        fw.append("  private static final class Packer" + bitWidth + " extends BytePacker {\n");
        fw.append("\n");
        fw.append("    private Packer" + bitWidth + "() {\n");
        fw.append("      super(" + bitWidth + ");\n");
        fw.append("    }\n");
        fw.append("\n");
        generatePack(fw, bitWidth, 1, msbFirst);
        generatePack(fw, bitWidth, 4, msbFirst);
        generateUnpack(fw, bitWidth, 1, msbFirst);
        generateUnpack(fw, bitWidth, 4, msbFirst);
        fw.append("  }\n");
    }
    
    private static int getShift(final FileWriter fw, final int bitWidth, final boolean msbFirst, final int byteIndex, final int valueIndex) throws IOException {
        final int valueStartBitIndex = valueIndex * bitWidth - 8 * byteIndex;
        final int valueEndBitIndex = (valueIndex + 1) * bitWidth - 8 * (byteIndex + 1);
        int valueStartBitWanted;
        int valueEndBitWanted;
        int byteStartBitWanted;
        int byteEndBitWanted;
        int shift;
        if (msbFirst) {
            valueStartBitWanted = ((valueStartBitIndex < 0) ? (bitWidth - 1 + valueStartBitIndex) : (bitWidth - 1));
            valueEndBitWanted = ((valueEndBitIndex > 0) ? valueEndBitIndex : 0);
            byteStartBitWanted = ((valueStartBitIndex < 0) ? 8 : (7 - valueStartBitIndex));
            byteEndBitWanted = ((valueEndBitIndex > 0) ? 0 : (-valueEndBitIndex));
            shift = valueEndBitWanted - byteEndBitWanted;
        }
        else {
            valueStartBitWanted = bitWidth - 1 - ((valueEndBitIndex > 0) ? valueEndBitIndex : 0);
            valueEndBitWanted = bitWidth - 1 - ((valueStartBitIndex < 0) ? (bitWidth - 1 + valueStartBitIndex) : (bitWidth - 1));
            byteStartBitWanted = 7 - ((valueEndBitIndex > 0) ? 0 : (-valueEndBitIndex));
            byteEndBitWanted = 7 - ((valueStartBitIndex < 0) ? 8 : (7 - valueStartBitIndex));
            shift = valueStartBitWanted - byteStartBitWanted;
        }
        visualizeAlignment(fw, bitWidth, valueEndBitIndex, valueStartBitWanted, valueEndBitWanted, byteStartBitWanted, byteEndBitWanted, shift);
        return shift;
    }
    
    private static void visualizeAlignment(final FileWriter fw, final int bitWidth, final int valueEndBitIndex, final int valueStartBitWanted, final int valueEndBitWanted, final int byteStartBitWanted, final int byteEndBitWanted, final int shift) throws IOException {
        fw.append("//");
        final int buf = 2 + Math.max(0, bitWidth + 8);
        for (int i = 0; i < buf; ++i) {
            fw.append(" ");
        }
        fw.append("[");
        for (int i = 7; i >= 0; --i) {
            if (i <= byteStartBitWanted && i >= byteEndBitWanted) {
                fw.append(String.valueOf(i));
            }
            else {
                fw.append("_");
            }
        }
        fw.append("]\n          //");
        for (int i = 0; i < buf + (8 - bitWidth + shift); ++i) {
            fw.append(" ");
        }
        fw.append("[");
        for (int i = bitWidth - 1; i >= 0; --i) {
            if (i <= valueStartBitWanted && i >= valueEndBitWanted) {
                fw.append(String.valueOf(i % 10));
            }
            else {
                fw.append("_");
            }
        }
        fw.append("]\n");
        fw.append("           ");
    }
    
    private static void generatePack(final FileWriter fw, final int bitWidth, final int batch, final boolean msbFirst) throws IOException {
        final int mask = genMask(bitWidth);
        fw.append("    public final void pack" + batch * 8 + "Values(final int[] in, final int inPos, final byte[] out, final int outPos) {\n");
        for (int byteIndex = 0; byteIndex < bitWidth * batch; ++byteIndex) {
            fw.append("      out[" + align(byteIndex, 2) + " + outPos] = (byte)((\n");
            final int startIndex = byteIndex * 8 / bitWidth;
            for (int endIndex = ((byteIndex + 1) * 8 + bitWidth - 1) / bitWidth, valueIndex = startIndex; valueIndex < endIndex; ++valueIndex) {
                if (valueIndex == startIndex) {
                    fw.append("          ");
                }
                else {
                    fw.append("\n        | ");
                }
                final int shift = getShift(fw, bitWidth, msbFirst, byteIndex, valueIndex);
                String shiftString = "";
                if (shift > 0) {
                    shiftString = " >>> " + shift;
                }
                else if (shift < 0) {
                    shiftString = " <<  " + -shift;
                }
                fw.append("((in[" + align(valueIndex, 2) + " + inPos] & " + mask + ")" + shiftString + ")");
            }
            fw.append(") & 255);\n");
        }
        fw.append("    }\n");
    }
    
    private static void generateUnpack(final FileWriter fw, final int bitWidth, final int batch, final boolean msbFirst) throws IOException {
        fw.append("    public final void unpack" + batch * 8 + "Values(final byte[] in, final int inPos, final int[] out, final int outPos) {\n");
        if (bitWidth > 0) {
            final int mask = genMask(bitWidth);
            for (int valueIndex = 0; valueIndex < batch * 8; ++valueIndex) {
                fw.append("      out[" + align(valueIndex, 2) + " + outPos] =\n");
                final int startIndex = valueIndex * bitWidth / 8;
                for (int endIndex = BytesUtils.paddedByteCountFromBits((valueIndex + 1) * bitWidth), byteIndex = startIndex; byteIndex < endIndex; ++byteIndex) {
                    if (byteIndex == startIndex) {
                        fw.append("          ");
                    }
                    else {
                        fw.append("\n        | ");
                    }
                    final int shift = getShift(fw, bitWidth, msbFirst, byteIndex, valueIndex);
                    String shiftString = "";
                    if (shift < 0) {
                        shiftString = ">>>  " + -shift;
                    }
                    else if (shift > 0) {
                        shiftString = "<<  " + shift;
                    }
                    fw.append(" (((((int)in[" + align(byteIndex, 2) + " + inPos]) & 255) " + shiftString + ") & " + mask + ")");
                }
                fw.append(";\n");
            }
        }
        fw.append("    }\n");
    }
    
    private static int genMask(final int bitWidth) {
        int mask = 0;
        for (int i = 0; i < bitWidth; ++i) {
            mask <<= 1;
            mask |= 0x1;
        }
        return mask;
    }
    
    private static String align(final int value, final int digits) {
        final String valueString = String.valueOf(value);
        final StringBuilder result = new StringBuilder();
        for (int i = valueString.length(); i < digits; ++i) {
            result.append(" ");
        }
        result.append(valueString);
        return result.toString();
    }
}
