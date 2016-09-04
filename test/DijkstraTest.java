import Main.Algorithms.Dijkstra.Dijkstra;
import Main.Graph.Edge;
import Main.Graph.Graph;

/**
 *  Tests a dijkstra for a simple graph
 */
public class DijkstraTest {
    public static void main(String[] args) {
        Edge[] edges = {
                new Edge("a", "b", 7),
                new Edge("a", "c", 9),
                new Edge("a", "f", 14),
                new Edge("b", "c", 10),
                new Edge("b", "d", 15),
                new Edge("c", "d", 11),
                new Edge("c", "f", 2),
                new Edge("d", "e", 6),
                new Edge("f", "e", 9),
        };

        Graph graph = new Graph(edges);
        Dijkstra dijkstra = new Dijkstra(graph);

        dijkstra.run("c");
        dijkstra.printAllPaths();
    }
}
