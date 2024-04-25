package pl.polsl.graphs;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;
import org.jgrapht.nio.dimacs.DIMACSImporter;
import org.jgrapht.util.SupplierUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Collection;

public class CustomWeightedGraph {
    public static class CustomWeightedEdge extends DefaultWeightedEdge {
        @Override
        public String toString() {
            return Double.toString(getWeight());
        }
    }

    public void testGraph() {
        DefaultUndirectedWeightedGraph<String, CustomWeightedGraph.CustomWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(CustomWeightedGraph.CustomWeightedEdge.class);

        String x1 = "v1";
        String x2 = "v2";
        String x3 = "v3";

        graph.addVertex(x1);
        graph.addVertex(x2);
        graph.addVertex(x3);

        CustomWeightedGraph.CustomWeightedEdge e1 = graph.addEdge(x1, x2);
        graph.setEdgeWeight(e1, 2.0);
        CustomWeightedGraph.CustomWeightedEdge e2 = graph.addEdge(x2, x3);
        graph.setEdgeWeight(e2, 3.0);
        CustomWeightedGraph.CustomWeightedEdge e3 = graph.addEdge(x3, x1);
        graph.setEdgeWeight(e3, 0.19);

        System.out.println(graph);

        this.savingGraphVisualizationToFile(graph, "src/main/java/pl/polsl/images/graph2.png");
    }

    public DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> importGraphInDIMACSFormat(String filePath) {
        DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(SupplierUtil.createStringSupplier(), SupplierUtil.createSupplier(CustomWeightedGraph.CustomWeightedEdge.class));
        DIMACSImporter<String, CustomWeightedEdge> dimacsImporter = new DIMACSImporter<>();

        try{
            File dimacsFile = new File(filePath);
            if(dimacsFile.exists()) {
                System.out.println(dimacsFile.exists());
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

    public DefaultUndirectedGraph<String, DefaultEdge> testImportForUndirected(String filePath) {
        DefaultUndirectedGraph<String, DefaultEdge> graph = new DefaultUndirectedGraph<>(SupplierUtil.createStringSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
        DIMACSImporter<String, DefaultEdge> dimacsImporter = new DIMACSImporter<>();

        try{
            File dimacsFile = new File(filePath);
            if(dimacsFile.exists()) {
                System.out.println(dimacsFile.exists());
                dimacsImporter.importGraph(graph, dimacsFile);
            } else {
                throw new FileNotFoundException("File does not exist");
            }
        } /*catch(NullPointerException e) {
            System.err.println("Exception: Unable to open DIMACS file, pathname is null.");
        }*/ catch (FileNotFoundException e) {
            System.err.println("Exception: Unable to open DIMACS file, file not found.");
        }

        System.out.println(graph.toString());
        //this.savingGraphVisualizationToFile(graph, "src/main/java/pl/polsl/images/graphtest.png");
        return graph;
    }

    public ArrayList<DefaultUndirectedWeightedGraph<String, CustomWeightedEdge>> importDIMACSBenchmarkDatasetAsUndirected(String folderPath) {
        ArrayList<DefaultUndirectedWeightedGraph<String, CustomWeightedEdge>> dimacsDataset = new ArrayList<>();
        DIMACSImporter<String, DefaultEdge> dimacsImporter = new DIMACSImporter<>();
        try{
            File dimacsFolder = new File(folderPath);
            if(dimacsFolder.isDirectory()) {
                for(final File dimacsFile : dimacsFolder.listFiles()) {
                    if(!dimacsFile.isDirectory()) {
                        DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graphWeighted = new DefaultUndirectedWeightedGraph<>(SupplierUtil.createStringSupplier(), SupplierUtil.createSupplier(CustomWeightedGraph.CustomWeightedEdge.class));
                        DefaultUndirectedGraph<String, DefaultEdge> graphUnweighted = new DefaultUndirectedGraph<>(SupplierUtil.createStringSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
                        try {
                            dimacsImporter.importGraph(graphUnweighted, dimacsFile);
                            //TODO: Casting undirected graph to directed (e. g. by iterating thorugh verticies and also setting 1.0 wage)
                            //graphWeighted = graphUnweighted.iterables()
                            dimacsDataset.add(graphWeighted);
                            System.out.println("//=========================================================");
                            System.out.println(graphUnweighted);
                            System.out.println(graphWeighted);
                            System.out.println("//=========================================================");
                            System.out.println("Good file:" + dimacsFile.getPath());
                        } catch (Exception e) {
                            System.out.println(dimacsFile.getPath());
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

    public ArrayList<DefaultUndirectedWeightedGraph<String, CustomWeightedEdge>> importDIMACSBenchmarkDatasetAsDirected(String folderPath) {
        ArrayList<DefaultUndirectedWeightedGraph<String, CustomWeightedEdge>> dimacsDataset = new ArrayList<>();
        DIMACSImporter<String, CustomWeightedEdge> dimacsImporter = new DIMACSImporter<>();
        try{
            File dimacsFolder = new File(folderPath);
            if(dimacsFolder.isDirectory()) {
                for(final File dimacsFile : dimacsFolder.listFiles()) {
                    if(!dimacsFile.isDirectory()) {
                        DefaultUndirectedWeightedGraph<String, CustomWeightedEdge> graph = new DefaultUndirectedWeightedGraph<>(SupplierUtil.createStringSupplier(), SupplierUtil.createSupplier(CustomWeightedGraph.CustomWeightedEdge.class));
                        try {
                            dimacsImporter.importGraph(graph, dimacsFile);
                            dimacsDataset.add(graph);
                            System.out.println("Good file:" + dimacsFile.getPath());
                        } catch (Exception e) {
                            System.out.println(dimacsFile.getPath());
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
}
