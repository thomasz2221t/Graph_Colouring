package pl.polsl.graphs;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.nio.dimacs.DIMACSImporter;
import org.jgrapht.util.SupplierUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;

public class CustomWeightedGraph {
    public static class CustomWeightedEdge extends DefaultWeightedEdge {
        @Override
        public String toString() {
            return Double.toString(getWeight());
        }
    }

    public void testGraph() {
        SimpleWeightedGraph<String, CustomWeightedGraph.CustomWeightedEdge> graph = new SimpleWeightedGraph<>(CustomWeightedGraph.CustomWeightedEdge.class);

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

        CustomWeightedGraph customWeightedGraph = new CustomWeightedGraph();
        customWeightedGraph.savingGraphVisualizationToFile(graph, "src/main/java/pl/polsl/images/graph2.png");
    }

    public void importGraphInDIMACSFormat(String filePath) {
        //SimpleWeightedGraph<Integer, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultWeightedEdgeSupplier());
        SimpleWeightedGraph<String, CustomWeightedEdge> graph = new SimpleWeightedGraph<>(SupplierUtil.createStringSupplier(), SupplierUtil.createSupplier(CustomWeightedGraph.CustomWeightedEdge.class));
        DIMACSImporter<String, CustomWeightedEdge> dimacsImporter = new DIMACSImporter<>();

        File dimacsFile = new File(filePath);
        try{
            dimacsFile.createNewFile();
        } catch(IOException exception) {
            System.err.println("Exception: Unable to open DIMACS file.");
        }
        //TODO: Ładnie pozabezpieczać czytanie pliku
        System.out.println(dimacsFile.exists());
        dimacsImporter.importGraph(graph, dimacsFile);

//        try {
//            Reader graphReader = new FileReader(dimacsFile);
//            graphReader.close();
//            dimacsImporter.importGraph(graph, dimacsFile);
//        } catch (FileNotFoundException exception) {
//            System.err.println("Exception: DIMACS file not found.");
//        } catch (IOException exception) {
//            System.err.println("Exception: Unable to open DIMACS file.");
//        } catch (ImportException exception) {
//            System.err.println("Exception: Unable to import DIMACS graph.");
//        }
        System.out.println(graph.toString());
        //SimpleWeightedGraph<String, CustomWeightedGraph.CustomWeightedEdge> customGraph = graph.;
        //this.savingDIMACSGraphVisualizationToFile(graph, "src/main/java/pl/polsl/images/graph3.png");
        this.savingGraphVisualizationToFile(graph, "src/main/java/pl/polsl/images/graph4.png");
    }

    public void savingGraphVisualizationToFile(SimpleWeightedGraph<String, CustomWeightedEdge> graph, String path) {
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
