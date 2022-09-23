// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.euclidean.twod;

import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math3.geometry.partitioning.utilities.OrderedTuple;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.geometry.partitioning.utilities.AVLTree;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import java.util.Iterator;
import org.apache.commons.math3.geometry.partitioning.Side;
import java.util.List;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.Vector;
import java.util.ArrayList;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import java.util.Collection;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;

public class PolygonsSet extends AbstractRegion<Euclidean2D, Euclidean1D>
{
    private Vector2D[][] vertices;
    
    public PolygonsSet() {
    }
    
    public PolygonsSet(final BSPTree<Euclidean2D> tree) {
        super(tree);
    }
    
    public PolygonsSet(final Collection<SubHyperplane<Euclidean2D>> boundary) {
        super(boundary);
    }
    
    public PolygonsSet(final double xMin, final double xMax, final double yMin, final double yMax) {
        super(boxBoundary(xMin, xMax, yMin, yMax));
    }
    
    public PolygonsSet(final double hyperplaneThickness, final Vector2D... vertices) {
        super(verticesToTree(hyperplaneThickness, vertices));
    }
    
    private static Line[] boxBoundary(final double xMin, final double xMax, final double yMin, final double yMax) {
        final Vector2D minMin = new Vector2D(xMin, yMin);
        final Vector2D minMax = new Vector2D(xMin, yMax);
        final Vector2D maxMin = new Vector2D(xMax, yMin);
        final Vector2D maxMax = new Vector2D(xMax, yMax);
        return new Line[] { new Line(minMin, maxMin), new Line(maxMin, maxMax), new Line(maxMax, minMax), new Line(minMax, minMin) };
    }
    
    private static BSPTree<Euclidean2D> verticesToTree(final double hyperplaneThickness, final Vector2D... vertices) {
        final int n = vertices.length;
        if (n == 0) {
            return new BSPTree<Euclidean2D>(Boolean.TRUE);
        }
        final Vertex[] vArray = new Vertex[n];
        for (int i = 0; i < n; ++i) {
            vArray[i] = new Vertex(vertices[i]);
        }
        final List<Edge> edges = new ArrayList<Edge>();
        for (int j = 0; j < n; ++j) {
            final Vertex start = vArray[j];
            final Vertex end = vArray[(j + 1) % n];
            Line line = start.sharedLineWith(end);
            if (line == null) {
                line = new Line(start.getLocation(), end.getLocation());
            }
            edges.add(new Edge(start, end, line));
            for (final Vertex vertex : vArray) {
                if (vertex != start && vertex != end && FastMath.abs(line.getOffset(vertex.getLocation())) <= hyperplaneThickness) {
                    vertex.bindWith(line);
                }
            }
        }
        final BSPTree<Euclidean2D> tree = new BSPTree<Euclidean2D>();
        insertEdges(hyperplaneThickness, tree, edges);
        return tree;
    }
    
    private static void insertEdges(final double hyperplaneThickness, final BSPTree<Euclidean2D> node, final List<Edge> edges) {
        int index = 0;
        Edge inserted = null;
        while (inserted == null && index < edges.size()) {
            inserted = edges.get(index++);
            if (inserted.getNode() == null) {
                if (node.insertCut(inserted.getLine())) {
                    inserted.setNode(node);
                }
                else {
                    inserted = null;
                }
            }
            else {
                inserted = null;
            }
        }
        if (inserted == null) {
            final BSPTree<Euclidean2D> parent = node.getParent();
            if (parent == null || node == parent.getMinus()) {
                node.setAttribute(Boolean.TRUE);
            }
            else {
                node.setAttribute(Boolean.FALSE);
            }
            return;
        }
        final List<Edge> plusList = new ArrayList<Edge>();
        final List<Edge> minusList = new ArrayList<Edge>();
        for (final Edge edge : edges) {
            if (edge != inserted) {
                final double startOffset = inserted.getLine().getOffset(edge.getStart().getLocation());
                final double endOffset = inserted.getLine().getOffset(edge.getEnd().getLocation());
                final Side startSide = (FastMath.abs(startOffset) <= hyperplaneThickness) ? Side.HYPER : ((startOffset < 0.0) ? Side.MINUS : Side.PLUS);
                final Side endSide = (FastMath.abs(endOffset) <= hyperplaneThickness) ? Side.HYPER : ((endOffset < 0.0) ? Side.MINUS : Side.PLUS);
                switch (startSide) {
                    case PLUS: {
                        if (endSide == Side.MINUS) {
                            final Vertex splitPoint = edge.split(inserted.getLine());
                            minusList.add(splitPoint.getOutgoing());
                            plusList.add(splitPoint.getIncoming());
                            continue;
                        }
                        plusList.add(edge);
                        continue;
                    }
                    case MINUS: {
                        if (endSide == Side.PLUS) {
                            final Vertex splitPoint = edge.split(inserted.getLine());
                            minusList.add(splitPoint.getIncoming());
                            plusList.add(splitPoint.getOutgoing());
                            continue;
                        }
                        minusList.add(edge);
                        continue;
                    }
                    default: {
                        if (endSide == Side.PLUS) {
                            plusList.add(edge);
                            continue;
                        }
                        if (endSide == Side.MINUS) {
                            minusList.add(edge);
                            continue;
                        }
                        continue;
                    }
                }
            }
        }
        if (!plusList.isEmpty()) {
            insertEdges(hyperplaneThickness, node.getPlus(), plusList);
        }
        else {
            node.getPlus().setAttribute(Boolean.FALSE);
        }
        if (!minusList.isEmpty()) {
            insertEdges(hyperplaneThickness, node.getMinus(), minusList);
        }
        else {
            node.getMinus().setAttribute(Boolean.TRUE);
        }
    }
    
    @Override
    public PolygonsSet buildNew(final BSPTree<Euclidean2D> tree) {
        return new PolygonsSet(tree);
    }
    
    @Override
    protected void computeGeometricalProperties() {
        final Vector2D[][] v = this.getVertices();
        if (v.length == 0) {
            final BSPTree<Euclidean2D> tree = ((AbstractRegion<Euclidean2D, T>)this).getTree(false);
            if (tree.getCut() == null && (boolean)tree.getAttribute()) {
                this.setSize(Double.POSITIVE_INFINITY);
                ((AbstractRegion<Euclidean2D, T>)this).setBarycenter(Vector2D.NaN);
            }
            else {
                this.setSize(0.0);
                ((AbstractRegion<Euclidean2D, T>)this).setBarycenter(new Vector2D(0.0, 0.0));
            }
        }
        else if (v[0][0] == null) {
            this.setSize(Double.POSITIVE_INFINITY);
            ((AbstractRegion<Euclidean2D, T>)this).setBarycenter(Vector2D.NaN);
        }
        else {
            double sum = 0.0;
            double sumX = 0.0;
            double sumY = 0.0;
            for (final Vector2D[] loop : v) {
                double x1 = loop[loop.length - 1].getX();
                double y1 = loop[loop.length - 1].getY();
                for (final Vector2D point : loop) {
                    final double x2 = x1;
                    final double y2 = y1;
                    x1 = point.getX();
                    y1 = point.getY();
                    final double factor = x2 * y1 - y2 * x1;
                    sum += factor;
                    sumX += factor * (x2 + x1);
                    sumY += factor * (y2 + y1);
                }
            }
            if (sum < 0.0) {
                this.setSize(Double.POSITIVE_INFINITY);
                ((AbstractRegion<Euclidean2D, T>)this).setBarycenter(Vector2D.NaN);
            }
            else {
                this.setSize(sum / 2.0);
                ((AbstractRegion<Euclidean2D, T>)this).setBarycenter(new Vector2D(sumX / (3.0 * sum), sumY / (3.0 * sum)));
            }
        }
    }
    
    public Vector2D[][] getVertices() {
        if (this.vertices == null) {
            if (((AbstractRegion<Euclidean2D, T>)this).getTree(false).getCut() == null) {
                this.vertices = new Vector2D[0][];
            }
            else {
                final SegmentsBuilder visitor = new SegmentsBuilder();
                ((AbstractRegion<Euclidean2D, T>)this).getTree(true).visit(visitor);
                final AVLTree<ComparableSegment> sorted = visitor.getSorted();
                final ArrayList<List<ComparableSegment>> loops = new ArrayList<List<ComparableSegment>>();
                while (!sorted.isEmpty()) {
                    final AVLTree.Node node = sorted.getSmallest();
                    final List<ComparableSegment> loop = this.followLoop(node, sorted);
                    if (loop != null) {
                        loops.add(loop);
                    }
                }
                this.vertices = new Vector2D[loops.size()][];
                int i = 0;
                for (final List<ComparableSegment> loop2 : loops) {
                    if (loop2.size() < 2) {
                        final Line line = loop2.get(0).getLine();
                        this.vertices[i++] = new Vector2D[] { null, line.toSpace((Vector<Euclidean1D>)new Vector1D(-3.4028234663852886E38)), line.toSpace((Vector<Euclidean1D>)new Vector1D(3.4028234663852886E38)) };
                    }
                    else if (loop2.get(0).getStart() == null) {
                        final Vector2D[] array = new Vector2D[loop2.size() + 2];
                        int j = 0;
                        for (final Segment segment : loop2) {
                            if (j == 0) {
                                double x = segment.getLine().toSubSpace((Vector<Euclidean2D>)segment.getEnd()).getX();
                                x -= FastMath.max(1.0, FastMath.abs(x / 2.0));
                                array[j++] = null;
                                array[j++] = segment.getLine().toSpace((Vector<Euclidean1D>)new Vector1D(x));
                            }
                            if (j < array.length - 1) {
                                array[j++] = segment.getEnd();
                            }
                            if (j == array.length - 1) {
                                double x = segment.getLine().toSubSpace((Vector<Euclidean2D>)segment.getStart()).getX();
                                x += FastMath.max(1.0, FastMath.abs(x / 2.0));
                                array[j++] = segment.getLine().toSpace((Vector<Euclidean1D>)new Vector1D(x));
                            }
                        }
                        this.vertices[i++] = array;
                    }
                    else {
                        final Vector2D[] array = new Vector2D[loop2.size()];
                        int j = 0;
                        for (final Segment segment : loop2) {
                            array[j++] = segment.getStart();
                        }
                        this.vertices[i++] = array;
                    }
                }
            }
        }
        return this.vertices.clone();
    }
    
    private List<ComparableSegment> followLoop(final AVLTree.Node node, final AVLTree<ComparableSegment> sorted) {
        final ArrayList<ComparableSegment> loop = new ArrayList<ComparableSegment>();
        ComparableSegment segment = node.getElement();
        loop.add(segment);
        final Vector2D globalStart = segment.getStart();
        Vector2D end = segment.getEnd();
        node.delete();
        final boolean open = segment.getStart() == null;
        while (end != null && (open || globalStart.distance(end) > 1.0E-10)) {
            AVLTree.Node selectedNode = null;
            ComparableSegment selectedSegment = null;
            double selectedDistance = Double.POSITIVE_INFINITY;
            final ComparableSegment lowerLeft = new ComparableSegment(end, -1.0E-10, -1.0E-10);
            final ComparableSegment upperRight = new ComparableSegment(end, 1.0E-10, 1.0E-10);
            for (AVLTree.Node n = sorted.getNotSmaller(lowerLeft); n != null && n.getElement().compareTo(upperRight) <= 0; n = n.getNext()) {
                segment = n.getElement();
                final double distance = end.distance(segment.getStart());
                if (distance < selectedDistance) {
                    selectedNode = n;
                    selectedSegment = segment;
                    selectedDistance = distance;
                }
            }
            if (selectedDistance > 1.0E-10) {
                return null;
            }
            end = selectedSegment.getEnd();
            loop.add(selectedSegment);
            selectedNode.delete();
        }
        if (loop.size() == 2 && !open) {
            return null;
        }
        if (end == null && !open) {
            throw new MathInternalError();
        }
        return loop;
    }
    
    private static class Vertex
    {
        private final Vector2D location;
        private Edge incoming;
        private Edge outgoing;
        private final List<Line> lines;
        
        public Vertex(final Vector2D location) {
            this.location = location;
            this.incoming = null;
            this.outgoing = null;
            this.lines = new ArrayList<Line>();
        }
        
        public Vector2D getLocation() {
            return this.location;
        }
        
        public void bindWith(final Line line) {
            this.lines.add(line);
        }
        
        public Line sharedLineWith(final Vertex vertex) {
            for (final Line line1 : this.lines) {
                for (final Line line2 : vertex.lines) {
                    if (line1 == line2) {
                        return line1;
                    }
                }
            }
            return null;
        }
        
        public void setIncoming(final Edge incoming) {
            this.incoming = incoming;
            this.bindWith(incoming.getLine());
        }
        
        public Edge getIncoming() {
            return this.incoming;
        }
        
        public void setOutgoing(final Edge outgoing) {
            this.outgoing = outgoing;
            this.bindWith(outgoing.getLine());
        }
        
        public Edge getOutgoing() {
            return this.outgoing;
        }
    }
    
    private static class Edge
    {
        private final Vertex start;
        private final Vertex end;
        private final Line line;
        private BSPTree<Euclidean2D> node;
        
        public Edge(final Vertex start, final Vertex end, final Line line) {
            this.start = start;
            this.end = end;
            this.line = line;
            this.node = null;
            start.setOutgoing(this);
            end.setIncoming(this);
        }
        
        public Vertex getStart() {
            return this.start;
        }
        
        public Vertex getEnd() {
            return this.end;
        }
        
        public Line getLine() {
            return this.line;
        }
        
        public void setNode(final BSPTree<Euclidean2D> node) {
            this.node = node;
        }
        
        public BSPTree<Euclidean2D> getNode() {
            return this.node;
        }
        
        public Vertex split(final Line splitLine) {
            final Vertex splitVertex = new Vertex(this.line.intersection(splitLine));
            splitVertex.bindWith(splitLine);
            final Edge startHalf = new Edge(this.start, splitVertex, this.line);
            final Edge endHalf = new Edge(splitVertex, this.end, this.line);
            startHalf.node = this.node;
            endHalf.node = this.node;
            return splitVertex;
        }
    }
    
    private static class ComparableSegment extends Segment implements Comparable<ComparableSegment>
    {
        private OrderedTuple sortingKey;
        
        public ComparableSegment(final Vector2D start, final Vector2D end, final Line line) {
            super(start, end, line);
            this.sortingKey = ((start == null) ? new OrderedTuple(new double[] { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY }) : new OrderedTuple(new double[] { start.getX(), start.getY() }));
        }
        
        public ComparableSegment(final Vector2D start, final double dx, final double dy) {
            super(null, null, null);
            this.sortingKey = new OrderedTuple(new double[] { start.getX() + dx, start.getY() + dy });
        }
        
        public int compareTo(final ComparableSegment o) {
            return this.sortingKey.compareTo(o.sortingKey);
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || (other instanceof ComparableSegment && this.compareTo((ComparableSegment)other) == 0);
        }
        
        @Override
        public int hashCode() {
            return this.getStart().hashCode() ^ this.getEnd().hashCode() ^ this.getLine().hashCode() ^ this.sortingKey.hashCode();
        }
    }
    
    private static class SegmentsBuilder implements BSPTreeVisitor<Euclidean2D>
    {
        private AVLTree<ComparableSegment> sorted;
        
        public SegmentsBuilder() {
            this.sorted = new AVLTree<ComparableSegment>();
        }
        
        public Order visitOrder(final BSPTree<Euclidean2D> node) {
            return Order.MINUS_SUB_PLUS;
        }
        
        public void visitInternalNode(final BSPTree<Euclidean2D> node) {
            final BoundaryAttribute<Euclidean2D> attribute = (BoundaryAttribute<Euclidean2D>)node.getAttribute();
            if (attribute.getPlusOutside() != null) {
                this.addContribution(attribute.getPlusOutside(), false);
            }
            if (attribute.getPlusInside() != null) {
                this.addContribution(attribute.getPlusInside(), true);
            }
        }
        
        public void visitLeafNode(final BSPTree<Euclidean2D> node) {
        }
        
        private void addContribution(final SubHyperplane<Euclidean2D> sub, final boolean reversed) {
            final AbstractSubHyperplane<Euclidean2D, Euclidean1D> absSub = (AbstractSubHyperplane<Euclidean2D, Euclidean1D>)(AbstractSubHyperplane)sub;
            final Line line = (Line)sub.getHyperplane();
            final List<Interval> intervals = ((IntervalsSet)absSub.getRemainingRegion()).asList();
            for (final Interval i : intervals) {
                final Vector2D start = Double.isInfinite(i.getInf()) ? null : line.toSpace((Vector<Euclidean1D>)new Vector1D(i.getInf()));
                final Vector2D end = Double.isInfinite(i.getSup()) ? null : line.toSpace((Vector<Euclidean1D>)new Vector1D(i.getSup()));
                if (reversed) {
                    this.sorted.insert(new ComparableSegment(end, start, line.getReverse()));
                }
                else {
                    this.sorted.insert(new ComparableSegment(start, end, line));
                }
            }
        }
        
        public AVLTree<ComparableSegment> getSorted() {
            return this.sorted;
        }
    }
}
