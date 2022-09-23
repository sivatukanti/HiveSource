// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.text.similarity;

import java.util.Set;
import java.util.HashSet;

public class JaccardSimilarity implements SimilarityScore<Double>
{
    @Override
    public Double apply(final CharSequence left, final CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        return Math.round(this.calculateJaccardSimilarity(left, right) * 100.0) / 100.0;
    }
    
    private Double calculateJaccardSimilarity(final CharSequence left, final CharSequence right) {
        final Set<String> intersectionSet = new HashSet<String>();
        final Set<String> unionSet = new HashSet<String>();
        boolean unionFilled = false;
        final int leftLength = left.length();
        final int rightLength = right.length();
        if (leftLength == 0 || rightLength == 0) {
            return 0.0;
        }
        for (int leftIndex = 0; leftIndex < leftLength; ++leftIndex) {
            unionSet.add(String.valueOf(left.charAt(leftIndex)));
            for (int rightIndex = 0; rightIndex < rightLength; ++rightIndex) {
                if (!unionFilled) {
                    unionSet.add(String.valueOf(right.charAt(rightIndex)));
                }
                if (left.charAt(leftIndex) == right.charAt(rightIndex)) {
                    intersectionSet.add(String.valueOf(left.charAt(leftIndex)));
                }
            }
            unionFilled = true;
        }
        return intersectionSet.size() / (double)unionSet.size();
    }
}
