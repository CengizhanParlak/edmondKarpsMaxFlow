package deneme;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import edu.uci.ics.jung.graph.event.GraphEvent;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class Graph_Viz {
    static JFrame frame;
    static JFrame frame2;
    static JFrame frame3;
    static JPanel panel;
    static JPanel panel1;
    static JPanel panel2;
    static JPanel panel3;

    static JComboBox baslangicHarfleriDropdown;
    static JComboBox bitisHarfleriDropdown;

    static JComboBox baslangicSecilecekHarfler;
    static JComboBox bitisSecilecekHarfler;

    static JLabel baglantilar;

    static int edgeCount_Directed = 0; // This works with the inner MyEdge class

    static LinkedList<String> Distinct_Vertex = new LinkedList<String>();//used to enter vertexes
    static LinkedList<String> Source_Vertex = new LinkedList<String>();
    static LinkedList<String> Target_Vertex = new LinkedList<String>();
    static LinkedList<Double> Edge_Weight = new LinkedList<Double>();//used to enter edge weight
    static LinkedList<String> Edge_Label = new LinkedList<String>();
    static LinkedList<Double> Edge_Flow = new LinkedList<Double>();

    public static LinkedList<Double> getWeights() {
        return Edge_Weight;
    }

    public static LinkedList<String> getLabel() {
        return Edge_Label;
    }


    static Transformer<MyLink, Double> capTransformer;
    static Map<MyLink, Double> edgeFlowMap;
    static Factory<MyLink> edgeFactory;

    static Graph<MyNode, MyLink> g;

    class MyNode {
        // static int edgeCount = 0; // This works with the inner MyEdge class
        public String id;

        public MyNode(String id) {
            this.id = id;
        }

        public String toString() {
            return "V" + id;
        }

        public String Node_Property() {
            String node_prop = id;
            return (node_prop);
        }
    }

    class MyLink {
        double weight;
        String Label;
        int id;
        double currentFlow;

        public MyLink(double weight, String Label) {
            this.currentFlow = currentFlow;
            this.id = edgeCount_Directed++;
            this.weight = weight;
            this.Label = Label;
        }

        public String toString() {
            return Label;
        }

        public String Link_Property() {
            String Link_prop = Label;
            return (Link_prop);
        }

        public String Link_Property_wt() {
            String Link_prop_wt = "" + weight;
            return (Link_prop_wt);
        }

        public double getCurrentFlow() {
            return currentFlow;
        }
    }

//used to construct graph and call graph algorithm used in JUNG

    public void Visualize_Directed_Graph(LinkedList<String> Distinct_nodes, LinkedList<String> source_vertex,
                                         LinkedList<String> target_vertex, LinkedList<Double> Edge_Weight, LinkedList<String> Edge_Label, LinkedList<Double> Edge_Flow) {
        EdKarpOverride<MyNode, MyLink> alg2;
        // CREATING weighted directed graph
        g = new DirectedSparseGraph<Graph_Viz.MyNode, Graph_Viz.MyLink>();

        // create node objects

        Hashtable<String, MyNode> Graph_Nodes = new Hashtable<String, Graph_Viz.MyNode>();
        LinkedList<MyNode> Source_Node = new LinkedList<Graph_Viz.MyNode>();
        LinkedList<MyNode> Target_Node = new LinkedList<Graph_Viz.MyNode>();
        LinkedList<MyNode> Graph_Nodes_Only = new LinkedList<Graph_Viz.MyNode>();

        // LinkedList<MyLink> Graph_Links = new LinkedList<Graph_Algos.MyLink>();
        // create graph nodes

        for (int i = 0; i < Distinct_nodes.size(); i++) {
            String node_name = Distinct_nodes.get(i);
            MyNode data = new MyNode(node_name);
            Graph_Nodes.put(node_name, data);
            Graph_Nodes_Only.add(data);
        }

        // Now convert all source and target nodes into objects

        for (int t = 0; t < source_vertex.size(); t++) {

            Source_Node.add(Graph_Nodes.get(source_vertex.get(t)));

            Target_Node.add(Graph_Nodes.get(target_vertex.get(t)));

        }

        // Now add nodes and edges to the graph

        int i;
        for (i = 0; i < Edge_Weight.size(); i++) {
            g.addEdge(new MyLink(Edge_Weight.get(i), Edge_Label.get(i)), Source_Node.get(i), Target_Node.get(i),
                    EdgeType.DIRECTED);
        }

        // -------------
        CircleLayout<MyNode, MyLink> layout1 = new CircleLayout<MyNode, MyLink>(g);

        layout1.setSize(new Dimension(600, 600));

        BasicVisualizationServer<MyNode, MyLink> viz = new BasicVisualizationServer<MyNode, MyLink>(layout1);
        viz.setPreferredSize(new Dimension(600, 600));


        Transformer<MyNode, String> vertexLabelTransformer = new Transformer<MyNode, String>() {
            public String transform(MyNode vertex) {
                return (String) vertex.Node_Property();
            }
        };



        Transformer<MyNode, Paint> vertexColor = new Transformer<MyNode, Paint>() {
            public Paint transform(MyNode arg0) {
                if (arg0.id == baslangicSecilecekHarfler.getSelectedItem().toString())
                    return Color.GREEN;
                else if (arg0.id == bitisSecilecekHarfler.getSelectedItem().toString())
                    return Color.RED;
                else
                    return Color.GRAY;
            }
        };

        capTransformer = new Transformer<MyLink, Double>() {
            public Double transform(MyLink link) {
                return link.weight;
            }
        };

        edgeFlowMap = new HashMap<MyLink, Double>();
        // This Factory produces new edges for use by the algorithm
        edgeFactory = new Factory<MyLink>() {
            public MyLink create() {
                return new MyLink(1, "");
            }
        };

        MyNode baslangic = null, bitis = null;

        for (MyNode myNode : Graph_Nodes_Only) {
            if (myNode.id.equals(baslangicSecilecekHarfler.getSelectedItem().toString())) {
                baslangic = myNode;
            }
            if (myNode.id.equals(bitisSecilecekHarfler.getSelectedItem().toString())) {
                bitis = myNode;
            }
        }

        EdKarpOverride<MyNode, MyLink> alg = new EdKarpOverride((DirectedGraph) g, baslangic, bitis, capTransformer, edgeFlowMap,
                edgeFactory);
        alg.evaluate();

        Edge_Flow = EdKarpOverride.getFlowsOnEdges();

        for (i = 0; i < Edge_Weight.size(); i++) {
            System.out.println(Edge_Flow.get(i));
        }

        Transformer<MyLink, String> edgeLabelTransformer = new Transformer<MyLink, String>() {
            public String transform(MyLink edge) {
                String yazi = "[ " + edge.Link_Property() + " ]: Wt = " + edge.currentFlow + "/" + edge.Link_Property_wt();
                return yazi;
            }
        };

        viz.getRenderContext().setEdgeLabelTransformer(edgeLabelTransformer);
        viz.getRenderContext().setVertexFillPaintTransformer(vertexColor);
        viz.getRenderContext().setVertexLabelTransformer(vertexLabelTransformer);

        System.out.println("The max flow is: " + alg.getMaxFlow());

        System.out.println("The edge set is: " + alg.getMinCutEdges().toString());

        frame2 = new JFrame("The max flow is: " + alg.getMaxFlow() + "-----" + "The edge set is: " + alg.getMinCutEdges().toString());
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.getContentPane().add(viz);
        frame2.pack();
        frame2.setVisible(true);

    }

    public static void main(String[] args) {
        anaMenu();
    }

    public static void anaMenu() {
        frame = new JFrame("Program");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        panel1 = new JPanel();
        panel1.setLayout(null);
        panel1.setBounds(30, 30, 300, 550);
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.GRAY, Color.DARK_GRAY),
                " Musluk sayısını belirt"));


        //panel1
        JLabel aciklama1 = new JLabel("Graf yapısını belirlemek için musluk sayısını girin:", SwingConstants.CENTER);
        aciklama1.setBounds(0, 100, 300, 50);
        panel1.add(aciklama1);

        JSpinner muslukSayisi = new JSpinner(new SpinnerNumberModel(0, 0, 30, 1));
        muslukSayisi.setBounds(25, 150, 250, 50);
        muslukSayisi.setAlignmentX(SwingConstants.CENTER);

        //sayÄ± ortalama Ã§abasÄ±
        JComponent editor = muslukSayisi.getEditor();
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
        spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        //
        panel1.add(muslukSayisi);

        JButton ilerle1 = new JButton("İlerle");
        ilerle1.setBounds(25, 210, 250, 50);
        ilerle1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                for (int i = 0; i < (Integer) muslukSayisi.getValue(); i++) {
                    Distinct_Vertex.add(Character.toString((char) (65 + i)));
                }

                System.out.println(Distinct_Vertex);

                baslangicHarfleriDropdown = new JComboBox(Distinct_Vertex.toArray());
                baslangicHarfleriDropdown.setBounds(25, 150, 75, 50);
                panel2.add(baslangicHarfleriDropdown);

                bitisHarfleriDropdown = new JComboBox(Distinct_Vertex.toArray());
                bitisHarfleriDropdown.setBounds(110, 150, 75, 50);
                panel2.add(bitisHarfleriDropdown);

                panel2.setVisible(true);

            }
        });

        panel1.add(ilerle1);
        //
        panel.add(panel1);


        panel2 = new JPanel();
        panel2.setLayout(null);
        panel2.setBounds(360, 30, 300, 550);
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.GRAY, Color.DARK_GRAY),
                "Musluk bağlantılarını belirt"));


        //panel2
        JLabel aciklama2 = new JLabel("Graf kenarları - boru hattını belirleyin:", SwingConstants.CENTER);
        aciklama2.setBounds(0, 100, 300, 50);
        panel2.add(aciklama2);


        JSpinner agirlik = new JSpinner(new SpinnerNumberModel(0, 0, 30, 1));
        agirlik.setBounds(195, 150, 75, 50);

        //sayı ortalama çabası
        JComponent editor2 = agirlik.getEditor();
        JSpinner.DefaultEditor spinnerEditor2 = (JSpinner.DefaultEditor) editor2;
        spinnerEditor2.getTextField().setHorizontalAlignment(JTextField.CENTER);
        //
        panel2.add(agirlik);

        JButton ekle = new JButton("Ekle");
        ekle.setBounds(25, 210, 250, 50);
        ekle.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (baglantilar.getText() == "<html>") {
                    baglantilar.setText(baglantilar.getText() + "<br/>" + baslangicHarfleriDropdown.getSelectedItem().toString() + " - " + bitisHarfleriDropdown.getSelectedItem().toString() + " - " + agirlik.getValue() + "</html>");
                } else {
                    baglantilar.setText(baglantilar.getText().substring(0, baglantilar.getText().length() - 7) + "<br/>" + baslangicHarfleriDropdown.getSelectedItem().toString() + " - " + bitisHarfleriDropdown.getSelectedItem().toString() + " - " + agirlik.getValue() + "</html>");
                }


                Source_Vertex.add(baslangicHarfleriDropdown.getSelectedItem().toString());
                Target_Vertex.add(bitisHarfleriDropdown.getSelectedItem().toString());

                int gecici = (int) agirlik.getValue();
                Edge_Weight.add((double) gecici);
                Edge_Label.add(baslangicHarfleriDropdown.getSelectedItem().toString() + bitisHarfleriDropdown.getSelectedItem().toString());

            }
        });
        panel2.add(ekle);

        baglantilar = new JLabel("<html>", SwingConstants.CENTER);
        baglantilar.setBounds(5, 250, 325, 300);
        baglantilar.setBackground(Color.lightGray);
        baglantilar.setOpaque(true);
        panel2.add(baglantilar);


        JButton ilerle2 = new JButton("İlerle");
        ilerle2.setBounds(25, 475, 250, 50);
        ilerle2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                baslangicSecilecekHarfler = new JComboBox(Distinct_Vertex.toArray());
                baslangicSecilecekHarfler.setBounds(35, 150, 100, 50);
                panel3.add(baslangicSecilecekHarfler);

                bitisSecilecekHarfler = new JComboBox(Distinct_Vertex.toArray());
                bitisSecilecekHarfler.setBounds(150, 150, 100, 50);
                panel3.add(bitisSecilecekHarfler);

                panel3.setVisible(true);

            }
        });
        panel2.add(ilerle2);


        //
        panel2.setVisible(false);
        panel.add(panel2);

        panel3 = new JPanel();
        panel3.setLayout(null);
        panel3.setBounds(720, 30, 300, 550);
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.GRAY, Color.DARK_GRAY),
                "Başlangıç ve Bitiş musluklarını seç"));

        JLabel aciklama3 = new JLabel("Başlangıç ve Bitiş musluklarını belirleyin:", SwingConstants.CENTER);
        aciklama3.setBounds(0, 100, 300, 50);
        panel3.add(aciklama3);

        JButton ilerle3 = new JButton("Ekle");
        ilerle3.setBounds(25, 210, 250, 50);
        ilerle3.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (Distinct_Vertex.size() != 0) {
                    Graph_Viz GA1 = new Graph_Viz();

                    GA1.Visualize_Directed_Graph(Distinct_Vertex, Source_Vertex, Target_Vertex, Edge_Weight, Edge_Label, Edge_Flow);
                }
            }
        });
        panel3.add(ilerle3);

        panel3.setVisible(false);
        panel.add(panel3);

        frame.setVisible(true);
    }
}