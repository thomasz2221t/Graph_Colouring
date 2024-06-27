package pl.polsl.graphs;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.nio.dimacs.DIMACSImporter;
import org.jgrapht.util.SupplierUtil;
import pl.polsl.exceptions.ProportionOutOfRange;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class CustomWeightedGraphHelper {
    public static class CustomWeightedEdge extends DefaultWeightedEdge {
        @Override
        public String toString() {
            return Double.toString(getWeight());
        }
    }

    public void testGraph() {
        DefaultUndirectedWeightedGraph<String, CustomWeightedGraphHelper.CustomWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(CustomWeightedGraphHelper.CustomWeightedEdge.class);

        String x1 = "v1";
        String x2 = "v2";
        String x3 = "v3";

        graph.addVertex(x1);
        graph.addVertex(x2);
        graph.addVertex(x3);

        CustomWeightedGraphHelper.CustomWeightedEdge e1 = graph.addEdge(x1, x2);
        graph.setEdgeWeight(e1, 2.0);
        CustomWeightedGraphHelper.CustomWeightedEdge e2 = graph.addEdge(x2, x3);
        graph.setEdgeWeight(e2, 3.0);
        CustomWeightedGraphHelper.CustomWeightedEdge e3 = graph.addEdge(x3, x1);
        graph.setEdgeWeight(e3, 0.19);

        System.out.println(graph);

        this.savingGraphVisualizationToFile(graph, "src/main/java/pl/polsl/images/graph2.png");
    }

    public ArrayList<DefaultUndirectedWeightedGraph<String, CustomWeightedEdge>> importDIMACSBenchmarkDataset(String folderPath) {
        ArrayList<DefaultUndirectedWeightedGraph<String, CustomWeightedEdge>> dimacsDataset = new ArrayList<>();
        DIMACSImporter<String, CustomWeightedEdge> dimacsImporter = new DIMACSImporter<>();
        try{
            File dimacsFolder = new File(folderPath);
            if(dimacsFolder.isDirectory()) {
                for(final File dimacsFile : dimacsFolder.listFiles()) {
                    if(!dimacsFile.isDirectory()) {
                        DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(SupplierUtil.createStringSupplier(), SupplierUtil.createSupplier(CustomWeightedGraphHelper.CustomWeightedEdge.class));
                        try {
                            dimacsImporter.importGraph(graph, dimacsFile);
                            dimacsDataset.add(graph);
//                            System.out.println("Good file:" + dimacsFile.getPath());
                        } catch (Exception e) {
//                            System.out.println(dimacsFile.getPath());
                            System.err.println("Exception: DIMACS file " + dimacsFile.getPath() + " corrupted");
                        }
                    }
                }
            } else {
                throw new NotDirectoryException("Given path does not lead to directory");
            }
        } catch(NotDirectoryException e) {
            System.err.println("Exception: Unable to open DIMACS graphs directory");
        }
        return dimacsDataset;
    }

    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> importGraphInDIMACSFormat(String filePath) {
        DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(SupplierUtil.createStringSupplier(), SupplierUtil.createSupplier(CustomWeightedGraphHelper.CustomWeightedEdge.class));
        DIMACSImporter<String, CustomWeightedEdge> dimacsImporter = new DIMACSImporter<>();

        try{
            File dimacsFile = new File(filePath);
            if(dimacsFile.exists()) {
                dimacsImporter.importGraph(graph, dimacsFile);
            } else {
                throw new FileNotFoundException("File does not exist");
            }
        } catch(NullPointerException e) {
            System.err.println("Exception: Unable to open DIMACS file, pathname is null.");
        } catch (FileNotFoundException e) {
            System.err.println("Exception: Unable to open DIMACS file, file not found.");
        }

        System.out.println(graph.toString());
        this.savingGraphVisualizationToFile(graph, "src/main/java/pl/polsl/images/graph4.png");
        return graph;
    }

    public ArrayList<DefaultUndirectedWeightedGraph<String, CustomWeightedEdge>> importDIMACSDatasetUnweightedGraphsAsWeighted(String folderPath) {
        ArrayList<DefaultUndirectedWeightedGraph<String, CustomWeightedEdge>> dimacsDataset = new ArrayList<>();
        DIMACSImporter<String, DefaultEdge> dimacsImporter = new DIMACSImporter<>();
        try {
            File dimacsFolder = new File(folderPath);
            if (dimacsFolder.isDirectory()) {
                for (final File dimacsFile : dimacsFolder.listFiles()) {
                    if (!dimacsFile.isDirectory()) {
                        DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graphWeighted = new DefaultUndirectedWeightedGraph<>(
                                SupplierUtil.createStringSupplier(),
                                SupplierUtil.createSupplier(CustomWeightedGraphHelper.CustomWeightedEdge.class));
                        DefaultUndirectedGraph<String, DefaultEdge> graphUnweighted = new DefaultUndirectedGraph<>(
                                SupplierUtil.createStringSupplier(),
                                SupplierUtil.createDefaultEdgeSupplier(),
                                false);
                        try {
                            dimacsImporter.importGraph(graphUnweighted, dimacsFile);
                            graphWeighted = this.convert(graphUnweighted);
                            dimacsDataset.add(graphWeighted);
                        } catch (Exception e) {
                            System.err.println("Exception: DIMACS file " + dimacsFile.getPath() + " corrupted");
                        }
                    }
                }
            } else {
                throw new NotDirectoryException("Given path does not lead to directory");
            }
        } catch (NotDirectoryException e) {
            System.err.println("Exception: Unable to open DIMACS graphs directory");
        }
        return dimacsDataset;
    }

    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> importDIMACSUnweightedGraphAsWeighted(String filePath) {
        DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> weightedGraph = new DefaultUndirectedWeightedGraph<>(
                SupplierUtil.createStringSupplier(),
                SupplierUtil.createSupplier(
                        CustomWeightedGraphHelper.CustomWeightedEdge.class
                ));
        DIMACSImporter<String, DefaultEdge> dimacsImporter = new DIMACSImporter<>();

        try{
            File dimacsFile = new File(filePath);
            if(dimacsFile.exists()) {
                DefaultUndirectedGraph<String, DefaultEdge> unweightedGraph = new DefaultUndirectedGraph<>(
                        SupplierUtil.createStringSupplier(),
                        SupplierUtil.createDefaultEdgeSupplier(),
                        false);
                dimacsImporter.importGraph(unweightedGraph, dimacsFile);
                weightedGraph = this.convert(unweightedGraph);
            } else {
                throw new FileNotFoundException("File does not exist");
            }
        } catch(NullPointerException e) {
            System.err.println("Exception: Unable to open DIMACS file, pathname is null.");
        } catch (FileNotFoundException e) {
            System.err.println("Exception: Unable to open DIMACS file, file not found.");
        }

        System.out.println(weightedGraph.toString());
        //this.savingGraphVisualizationToFile(weightedGraph, "src/main/java/pl/polsl/images/graphtest.png");
        return weightedGraph;
    }

    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> imposeUncertaintyToGraph(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, float proportionOfEdgesToFuzz, float lowerBoundaryOfUncertainty){
        List<CustomWeightedEdge> edgeList = graph.edgeSet().stream().toList();
        int numberOfEdgesToFuzz = 0;
        try {
            if (proportionOfEdgesToFuzz >= 0 && proportionOfEdgesToFuzz <= 1) {
                numberOfEdgesToFuzz = Math.round(edgeList.size() * proportionOfEdgesToFuzz);
            } else {
                throw new ProportionOutOfRange("Edge certainity proportion out of range");
            }

            Set<Integer> randomValues = new Random()
                    .ints(0, edgeList.size())
                    .limit(numberOfEdgesToFuzz)
                    .boxed()
                    .collect(Collectors.toSet());
            for (Integer edgeIndex: randomValues) {
                Random uncertaintyGenerator = new Random();
                double uncertaintyValue = uncertaintyGenerator.nextDouble(
                        1 - lowerBoundaryOfUncertainty) + lowerBoundaryOfUncertainty;
                graph.setEdgeWeight(edgeList.get(edgeIndex), uncertaintyValue);
            }
        } catch(ProportionOutOfRange e) {
            System.err.println(e.getMessage());
        }
        return graph;
    }

    public String getRandomVertexFromGraph(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph) {
        List<String> vertexList = graph.vertexSet().stream().toList();
        Random random = new Random();
        return vertexList.get(random.nextInt(vertexList.size()));
    }

    public Map<String, CustomWeightedEdge> getNeighbourhoodListOfVertex(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, String vertexName) {
        List<CustomWeightedEdge> vertexEdges = graph.edgesOf(vertexName).stream().toList();
        Map<String, CustomWeightedEdge> vertexNeighbourhoodList = new HashMap<>();
        for(CustomWeightedEdge edge : vertexEdges) {
            String edgeSource = graph.getEdgeSource(edge);
            String edgeTarget = graph.getEdgeTarget(edge);
            if(edgeSource != vertexName) {
                vertexNeighbourhoodList.put(edgeSource, edge);
            } else if(edgeTarget != vertexName) {
                vertexNeighbourhoodList.put(edgeTarget, edge);
            } else {
                System.err.println("Exception: Getting vertex neighbourhood list method found vertex with self referencing edge");
            }
        }
        return vertexNeighbourhoodList;
    }

    public void savingGraphVisualizationToFile(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, String path) {
        JGraphXAdapter<String, CustomWeightedEdge> graphAdapter = new JGraphXAdapter<>(graph);

        //TODO: Można zadziałać z JPanel czy coś, wtedy getContentPane().add(graphComponent);
        //usuwanie strzałek z wizualizacji
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);
        mxGraphModel graphModel = (mxGraphModel)graphComponent.getGraph().getModel();
        Collection<Object> cells =  graphModel.getCells().values();
        mxUtils.setCellStyles(graphComponent.getGraph().getModel(), cells.toArray(), mxConstants.STYLE_ENDARROW, mxConstants.NONE);

        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, false, null);
        File imgFile = new File(path);
        try {
            ImageIO.write(image, "PNG", imgFile);
        } catch (IOException error) {}
    }

    public void savingColouredGraphVisualizationToFile(DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph, Map<String, Integer> verticesColourMap, String path) {
        JGraphXAdapter<String, CustomWeightedEdge> graphAdapter = new JGraphXAdapter<>(graph);
        Random random = new Random();
        Map<Integer, String> colourCodingMap = new HashMap<>();
        for (String vertex : verticesColourMap.keySet()) {
            if(!colourCodingMap.containsKey(verticesColourMap.get(vertex))) {
                float r = random.nextFloat();
                float g = random.nextFloat();
                float b = random.nextFloat();
                Color randomColor = new Color(r, g, b);
                colourCodingMap.put(verticesColourMap.get(vertex),
                        "#"+Integer.toHexString(randomColor.getRGB()).substring(2)
                );
            }
        }

        for (String vertex : verticesColourMap.keySet()) {
            Object vertexCell = graphAdapter.getVertexToCellMap().get(vertex);
            graphAdapter.setCellStyles(mxConstants.STYLE_FILLCOLOR, colourCodingMap.get(verticesColourMap.get(vertex)), new Object[]{vertexCell});
        }

        //TODO: Można zadziałać z JPanel czy coś, wtedy getContentPane().add(graphComponent);
        //usuwanie strzałek z wizualizacji
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);
        mxGraphModel graphModel = (mxGraphModel)graphComponent.getGraph().getModel();
        Collection<Object> cells =  graphModel.getCells().values();
        mxUtils.setCellStyles(graphComponent.getGraph().getModel(), cells.toArray(), mxConstants.STYLE_ENDARROW, mxConstants.NONE);

        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, false, null);
        File imgFile = new File(path);
        try {
            ImageIO.write(image, "PNG", imgFile);
        } catch (IOException error) {}
    }

    private static <V, Vv, E> DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> convert(Graph<V, E> source) {
        DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> result = new DefaultUndirectedWeightedGraph<>(SupplierUtil.createStringSupplier(), SupplierUtil.createSupplier(CustomWeightedGraphHelper.CustomWeightedEdge.class));
        //source.vertexSet().forEach(v -> result.addVertex(vertexMapper.apply(v)));
        source.edgeSet()
                .forEach(
                        e -> {
                            String sourceV = (String) source.getEdgeSource(e);
                            //Vv mappedSourceV = vertexMapper.apply(sourceV);
                            String targetV = (String) source.getEdgeTarget(e);
                            //Vv mappedTargetV = vertexMapper.apply(targetV);
                            //result.addVertex(mappedSourceV);
                            //result.addVertex(mappedTargetV);
                            //CustomWeightedEdge ee = result.addEdge(mappedSourceV, mappedTargetV);
                            result.addVertex(sourceV);
                            result.addVertex(targetV);
                            CustomWeightedEdge ee = result.addEdge(sourceV, targetV);
                            //double edgeWeight = source.getEdgeWeight(e);
                            double edgeWeight = 1.0;
                            result.setEdgeWeight(ee, edgeWeight);
                        });
        return result;
    }
}
