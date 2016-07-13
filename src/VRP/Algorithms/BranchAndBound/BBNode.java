package VRP.Algorithms.BranchAndBound;

import VRP.Graph.Vertex;
import VRP.Graph.VertexType;

import java.util.Arrays;

/**
 * a node that used in branch and bound for VRP
 */
public class BBNode {

    public Vertex vertex;          // current vertex of the graph
    public int vehicleUsed;        // number of vehicle used in this node
    public int timeElapsed;        // the time elapsed after moving the vehicle
    public int remainedGoods;      // remained goods in the car
    public int penaltyTaken;       // penalty taken for the
    public boolean[] servicedNodes;// nodes that are serviced
    public int numberOfServicedCustomers; // for easily terminate the algorithm
    public BBNode parent;          // parent of the node in the BB tree


    /**
     * constructor for the branch and bound node
     */
    public BBNode(Vertex vertex, int timeElapsed, int penaltyTaken, BBNode parent) {
        this.vertex = vertex;
        this.timeElapsed = timeElapsed;
        this.penaltyTaken = penaltyTaken;
        this.parent = parent;

        // for root node
        if (parent == null)
            servicedNodes = new boolean[BBGlobalVariables.numberOfCustomers];

        // inherit from parent
        if (parent != null){
            this.vehicleUsed = parent.vehicleUsed;
            if (parent.vertex.type == VertexType.DEPOT) vehicleUsed++;
            this.timeElapsed = parent.timeElapsed;
            this.remainedGoods = parent.remainedGoods;
            this.servicedNodes = Arrays.copyOf(parent.servicedNodes, parent.servicedNodes.length);
            this.numberOfServicedCustomers = parent.numberOfServicedCustomers;
        }

        // service to this node
        if (parent != null && vertex.type != VertexType.DEPOT){
            remainedGoods -= vertex.demand;
            numberOfServicedCustomers++;
            servicedNodes[vertex.customerId] = true;
        }

        // if the we are in the depot, we must use a new vehicle, and reset time and remained goods
        if (vertex.type == VertexType.DEPOT){
            this.timeElapsed = 0;
            this.remainedGoods = BBGlobalVariables.vehicleCapacity;
        }
    }

    /**
     * @return cost of the node that we are there
     */
    public int cost() {            // calculates branch and bound cost of the node
        return penaltyTaken + vehicleUsed * BBGlobalVariables.vehicleFixedCost;
    }

    /**
     * go up int the tree and print the path
     */
    void printPath(){
        if (this.parent == null){
            //System.out.print(this.vertex);
        }
        else {
            this.parent.printPath(); // recursive part of the function
            if (vertex.type == VertexType.DEPOT)
                System.out.print("\n");
            else
                System.out.printf(" -> %s", this.vertex);
        }
    }

    @Override
    public String toString() {
        return vertex + ", " + cost();
    }
}
