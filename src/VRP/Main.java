package VRP;

import VRP.Algorithms.Dijkstra;
import VRP.Graph.Graph;
import VRP.Graph.Vertex;

/**
 * for running the algorithm
 */
public class Main {
    public static void main(String[] args){
        Graph originalGraph = new Graph("resources/input.csv");

        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();

        preprocessedGraph.printGraph();
    }
}