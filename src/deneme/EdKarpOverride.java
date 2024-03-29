package deneme;

import edu.uci.ics.jung.algorithms.util.IterativeProcess;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.apache.commons.collections15.Buffer;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.buffer.UnboundedFifoBuffer;

import java.util.*;

public class EdKarpOverride<V, E> extends IterativeProcess {

    private DirectedGraph<V, E> mFlowGraph;
    private DirectedGraph<V, E> mOriginalGraph;
    private V source;
    private V target;
    private int mMaxFlow;
    private Set<V> mSourcePartitionNodes;
    private Set<V> mSinkPartitionNodes;
    private Set<E> mMinCutEdges;


    public static LinkedList<String> edgeLabels = Graph_Viz.getLabel();
    public static LinkedList<Double> edgeWeights = Graph_Viz.getWeights();
    public static LinkedList<Double> flowsOnEdges = new LinkedList<Double>();

    private int i = 0;

    private Map<E, Number> residualCapacityMap = new HashMap<E, Number>();
    private Map<V, V> parentMap = new HashMap<V, V>();
    private Map<V, Number> parentCapacityMap = new HashMap<V, Number>();
    private Transformer<E, Number> edgeCapacityTransformer;
    private Map<E, Number> edgeFlowMap;
    private Factory<E> edgeFactory;

    /**
     * Constructs a new instance of the algorithm solver for a given graph, source, and sink.
     * Source and sink vertices must be elements of the specified graph, and must be
     * distinct.
     *
     * @param directedGraph           the flow graph
     * @param source                  the source vertex
     * @param sink                    the sink vertex
     * @param edgeCapacityTransformer the transformer that gets the capacity for each edge.
     * @param edgeFlowMap             the map where the solver will place the value of the flow for each edge
     * @param edgeFactory             used to create new edge instances for backEdges
     */
    @SuppressWarnings("unchecked")
    public EdKarpOverride(DirectedGraph<V, E> directedGraph, V source, V sink,
                          Transformer<E, Number> edgeCapacityTransformer, Map<E, Number> edgeFlowMap,
                          Factory<E> edgeFactory) {
        assignFlowsOnEdges();
        if (directedGraph.getVertices().contains(source) == false ||
                directedGraph.getVertices().contains(sink) == false) {
            throw new IllegalArgumentException("source and sink vertices must be elements of the specified graph");
        }
        if (source.equals(sink)) {
            throw new IllegalArgumentException("source and sink vertices must be distinct");
        }
        mOriginalGraph = directedGraph;

        this.source = source;
        this.target = sink;
        this.edgeFlowMap = edgeFlowMap;
        this.edgeCapacityTransformer = edgeCapacityTransformer;
        this.edgeFactory = edgeFactory;
        try {
            mFlowGraph = directedGraph.getClass().newInstance();
            for (E e : mOriginalGraph.getEdges()) {
                mFlowGraph.addEdge(e, mOriginalGraph.getSource(e),
                        mOriginalGraph.getDest(e), EdgeType.DIRECTED);
                //***********************************************  flowsOnEdges.add(0.0);
            }
            for (V v : mOriginalGraph.getVertices()) {
                mFlowGraph.addVertex(v);
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mMaxFlow = 0;
        mSinkPartitionNodes = new HashSet<V>();
        mSourcePartitionNodes = new HashSet<V>();
        mMinCutEdges = new HashSet<E>();
    }

    public static void assignFlowsOnEdges(){
        int j;
        for(j = 0; j < 15 ; j++){
            flowsOnEdges.add(0.0);
        }
    }

    private void clearParentValues() {
        parentMap.clear();
        parentCapacityMap.clear();
        parentCapacityMap.put(source, Integer.MAX_VALUE);
        parentMap.put(source, source);
    }

    protected boolean hasAugmentingPath() {

        mSinkPartitionNodes.clear();
        mSourcePartitionNodes.clear();
        mSinkPartitionNodes.addAll(mFlowGraph.getVertices());

        Set<E> visitedEdgesMap = new HashSet<E>();
        Buffer<V> queue = new UnboundedFifoBuffer<V>();
        queue.add(source);

        while (!queue.isEmpty()) {
            V currentVertex = queue.remove();
            mSinkPartitionNodes.remove(currentVertex);
            mSourcePartitionNodes.add(currentVertex);
            Number currentCapacity = parentCapacityMap.get(currentVertex);


            Collection<E> neighboringEdges = mFlowGraph.getOutEdges(currentVertex);

            for (E neighboringEdge : neighboringEdges) {

                V neighboringVertex = mFlowGraph.getDest(neighboringEdge);

                Number residualCapacity = residualCapacityMap.get(neighboringEdge);

                System.out.println("residual capacity/index: " + residualCapacity.intValue());
                int index = edgeWeights.indexOf(residualCapacity.doubleValue());

                if (residualCapacity.intValue() <= 0 || visitedEdgesMap.contains(neighboringEdge)) {
                    try {
                        flowsOnEdges.set(index, edgeWeights.get(index));
                        System.out.println("flows eklendi");
                    } catch (Exception e) {
                        System.out.println("index size uyuşmazlığı");
                    }
                    continue;
                }
                V neighborsParent = parentMap.get(neighboringVertex);
                Number neighborCapacity = parentCapacityMap.get(neighboringVertex);
                int newCapacity = Math.min(residualCapacity.intValue(), currentCapacity.intValue());

                try {
                    flowsOnEdges.set(index, edgeWeights.get(index));
                    System.out.println("flows eklendi");
                } catch (Exception e) {
                    System.out.println("index size uyuşmazlığı");
                }

                if ((neighborsParent == null) || newCapacity > neighborCapacity.intValue()) {
                    parentMap.put(neighboringVertex, currentVertex);
                    parentCapacityMap.put(neighboringVertex, new Integer(newCapacity));
                    visitedEdgesMap.add(neighboringEdge);
                    if (neighboringVertex != target) {
                        queue.add(neighboringVertex);
                    }
                }
            }
        }

        boolean hasAugmentingPath = false;
        Number targetsParentCapacity = parentCapacityMap.get(target);
        if (targetsParentCapacity != null && targetsParentCapacity.intValue() > 0) {
            updateResidualCapacities();
            hasAugmentingPath = true;
        }
        clearParentValues();
        return hasAugmentingPath;
    }

    @Override
    public void step() {
        while (hasAugmentingPath()) {
        }
        computeMinCut();
//        return 0;
    }

    public static LinkedList<Double> getFlowsOnEdges() {
        int i;
        for (i = 0; i < flowsOnEdges.size(); i++) {
            System.out.println((i+1) + ".eleman: " + flowsOnEdges.get(i));
        }
        return flowsOnEdges;
    }

    private void computeMinCut() {

        for (E e : mOriginalGraph.getEdges()) {

            V source = mOriginalGraph.getSource(e);
            V destination = mOriginalGraph.getDest(e);
            if (mSinkPartitionNodes.contains(source) && mSinkPartitionNodes.contains(destination)) {
                continue;
            }
            if (mSourcePartitionNodes.contains(source) && mSourcePartitionNodes.contains(destination)) {
                continue;
            }
            if (mSinkPartitionNodes.contains(source) && mSourcePartitionNodes.contains(destination)) {
                continue;
            }
            mMinCutEdges.add(e);
        }
    }

    /**
     * Returns the value of the maximum flow from the source to the sink.
     */
    public int getMaxFlow() {
        return mMaxFlow;
    }

    /**
     * Returns the nodes which share the same partition (as defined by the min-cut edges)
     * as the sink node.
     */
    public Set<V> getNodesInSinkPartition() {
        return mSinkPartitionNodes;
    }

    /**
     * Returns the nodes which share the same partition (as defined by the min-cut edges)
     * as the source node.
     */
    public Set<V> getNodesInSourcePartition() {
        return mSourcePartitionNodes;
    }

    /**
     * Returns the edges in the minimum cut.
     */
    public Set<E> getMinCutEdges() {
        return mMinCutEdges;
    }

    /**
     * Returns the graph for which the maximum flow is calculated.
     */
    public DirectedGraph<V, E> getFlowGraph() {
        return mFlowGraph;
    }

    @Override
    protected void initializeIterations() {
        parentCapacityMap.put(source, Integer.MAX_VALUE);
        parentMap.put(source, source);

        List<E> edgeList = new ArrayList<E>(mFlowGraph.getEdges());

        for (int eIdx = 0; eIdx < edgeList.size(); eIdx++) {
            E edge = edgeList.get(eIdx);
            Number capacity = edgeCapacityTransformer.transform(edge);

            if (capacity == null) {
                throw new IllegalArgumentException("Edge capacities must be provided in Transformer passed to constructor");
            }
            residualCapacityMap.put(edge, capacity);

            V source = mFlowGraph.getSource(edge);
            V destination = mFlowGraph.getDest(edge);

            if (mFlowGraph.isPredecessor(source, destination) == false) {
                E backEdge = edgeFactory.create();
                mFlowGraph.addEdge(backEdge, destination, source, EdgeType.DIRECTED);
                residualCapacityMap.put(backEdge, 0);
            }
        }
    }

    @Override
    protected void finalizeIterations() {

        for (E currentEdge : mFlowGraph.getEdges()) {
            Number capacity = edgeCapacityTransformer.transform(currentEdge);

            Number residualCapacity = residualCapacityMap.get(currentEdge);
            if (capacity != null) {
                Integer flowValue = new Integer(capacity.intValue() - residualCapacity.intValue());
                this.edgeFlowMap.put(currentEdge, flowValue);
            }
        }

        Set<E> backEdges = new HashSet<E>();
        for (E currentEdge : mFlowGraph.getEdges()) {

            if (edgeCapacityTransformer.transform(currentEdge) == null) {
                backEdges.add(currentEdge);
            } else {
                residualCapacityMap.remove(currentEdge);
            }
        }
        for (E e : backEdges) {
            mFlowGraph.removeEdge(e);
        }
    }

    private void updateResidualCapacities() {

        Number augmentingPathCapacity = parentCapacityMap.get(target);
        mMaxFlow += augmentingPathCapacity.intValue();
        V currentVertex = target;
        V parentVertex = null;
        while ((parentVertex = parentMap.get(currentVertex)) != currentVertex) {
            E currentEdge = mFlowGraph.findEdge(parentVertex, currentVertex);

            Number residualCapacity = residualCapacityMap.get(currentEdge);

            residualCapacity = residualCapacity.intValue() - augmentingPathCapacity.intValue();
            residualCapacityMap.put(currentEdge, residualCapacity);

            E backEdge = mFlowGraph.findEdge(currentVertex, parentVertex);
            residualCapacity = residualCapacityMap.get(backEdge);
            residualCapacity = residualCapacity.intValue() + augmentingPathCapacity.intValue();
            residualCapacityMap.put(backEdge, residualCapacity);
            currentVertex = parentVertex;
        }
    }
}
