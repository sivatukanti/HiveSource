// 
// Decompiled by Procyon v0.5.36
// 

package parquet.encoding.bitpacking;

import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

public class IntBasedBitPackingGenerator
{
    private static final String CLASS_NAME_PREFIX = "LemireBitPacking";
    
    public static void main(final String[] args) throws Exception {
        final String basePath = args[0];
        generateScheme("LemireBitPackingBE", true, basePath);
        generateScheme("LemireBitPackingLE", false, basePath);
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
        fw.append(" * Based on the original implementation at at https://github.com/lemire/JavaFastPFOR/blob/master/src/integercompression/BitPacking.java\n");
        fw.append(" * Which is released under the\n");
        fw.append(" * Apache License Version 2.0 http://www.apache.org/licenses/.\n");
        fw.append(" * By Daniel Lemire, http://lemire.me/en/\n");
        fw.append(" * \n");
        fw.append(" * Scheme designed by D. Lemire\n");
        if (msbFirst) {
            fw.append(" * Adapted to pack from the Most Significant Bit first\n");
        }
        fw.append(" * \n");
        fw.append(" * @author automatically generated\n");
        fw.append(" * @see IntBasedBitPackingGenerator\n");
        fw.append(" *\n");
        fw.append(" */\n");
        fw.append("abstract class " + className + " {\n");
        fw.append("\n");
        fw.append("  private static final IntPacker[] packers = new IntPacker[32];\n");
        fw.append("  static {\n");
        for (int i = 0; i < 32; ++i) {
            fw.append("    packers[" + i + "] = new Packer" + i + "();\n");
        }
        fw.append("  }\n");
        fw.append("\n");
        fw.append("  public static final IntPackerFactory factory = new IntPackerFactory() {\n");
        fw.append("    public IntPacker newIntPacker(int bitWidth) {\n");
        fw.append("      return packers[bitWidth];\n");
        fw.append("    }\n");
        fw.append("  };\n");
        fw.append("\n");
        for (int i = 0; i < 32; ++i) {
            generateClass(fw, i, msbFirst);
            fw.append("\n");
        }
        fw.append("}\n");
        fw.close();
    }
    
    private static void generateClass(final FileWriter fw, final int bitWidth, final boolean msbFirst) throws IOException {
        int mask = 0;
        for (int i = 0; i < bitWidth; ++i) {
            mask <<= 1;
            mask |= 0x1;
        }
        fw.append("  private static final class Packer" + bitWidth + " extends IntPacker {\n");
        fw.append("\n");
        fw.append("    private Packer" + bitWidth + "() {\n");
        fw.append("      super(" + bitWidth + ");\n");
        fw.append("    }\n");
        fw.append("\n");
        fw.append("    public final void pack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {\n");
        for (int i = 0; i < bitWidth; ++i) {
            fw.append("      out[" + align(i, 2) + " + outPos] =\n");
            final int startIndex = i * 32 / bitWidth;
            for (int endIndex = ((i + 1) * 32 + bitWidth - 1) / bitWidth, j = startIndex; j < endIndex; ++j) {
                if (j == startIndex) {
                    fw.append("          ");
                }
                else {
                    fw.append("\n        | ");
                }
                final String shiftString = getPackShiftString(bitWidth, i, startIndex, j, msbFirst);
                fw.append("((in[" + align(j, 2) + " + inPos] & " + mask + ")" + shiftString + ")");
            }
            fw.append(";\n");
        }
        fw.append("    }\n");
        fw.append("    public final void unpack32Values(final int[] in, final int inPos, final int[] out, final int outPos) {\n");
        if (bitWidth > 0) {
            for (int i = 0; i < 32; ++i) {
                fw.append("      out[" + align(i, 2) + " + outPos] =");
                final int byteIndex = i * bitWidth / 32;
                final String shiftString2 = getUnpackShiftString(bitWidth, i, msbFirst);
                fw.append(" ((in[" + align(byteIndex, 2) + " + inPos] " + shiftString2 + ") & " + mask + ")");
                if (((i + 1) * bitWidth - 1) / 32 != byteIndex) {
                    final int bitsRead = ((i + 1) * bitWidth - 1) % 32 + 1;
                    fw.append(" | ((in[" + align(byteIndex + 1, 2) + " + inPos]");
                    if (msbFirst) {
                        fw.append(") >>> " + align(32 - bitsRead, 2) + ")");
                    }
                    else {
                        int lowerMask = 0;
                        for (int k = 0; k < bitsRead; ++k) {
                            lowerMask <<= 1;
                            lowerMask |= 0x1;
                        }
                        fw.append(" & " + lowerMask + ") << " + align(bitWidth - bitsRead, 2) + ")");
                    }
                }
                fw.append(";\n");
            }
        }
        fw.append("    }\n");
        fw.append("  }\n");
    }
    
    private static String getUnpackShiftString(final int bitWidth, final int i, final boolean msbFirst) {
        final int regularShift = i * bitWidth % 32;
        String shiftString;
        if (msbFirst) {
            final int shift = 32 - (regularShift + bitWidth);
            if (shift < 0) {
                shiftString = "<<  " + align(-shift, 2);
            }
            else {
                shiftString = ">>> " + align(shift, 2);
            }
        }
        else {
            shiftString = ">>> " + align(regularShift, 2);
        }
        return shiftString;
    }
    
    private static String getPackShiftString(final int bitWidth, final int integerIndex, final int startIndex, final int valueIndex, final boolean msbFirst) {
        final int regularShift = valueIndex * bitWidth % 32;
        String shiftString;
        if (msbFirst) {
            final int shift = 32 - (regularShift + bitWidth);
            if (valueIndex == startIndex && integerIndex * 32 % bitWidth != 0) {
                shiftString = " <<  " + align(32 - (valueIndex + 1) * bitWidth % 32, 2);
            }
            else if (shift < 0) {
                shiftString = " >>> " + align(-shift, 2);
            }
            else {
                shiftString = " <<  " + align(shift, 2);
            }
        }
        else if (valueIndex == startIndex && integerIndex * 32 % bitWidth != 0) {
            shiftString = " >>> " + align(32 - regularShift, 2);
        }
        else {
            shiftString = " <<  " + align(regularShift, 2);
        }
        return shiftString;
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
