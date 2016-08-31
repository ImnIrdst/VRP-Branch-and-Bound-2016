package VRP.MathematicalModel;

import VRP.Algorithms.Dijkstra.Dijkstra;
import VRP.GlobalVars;
import VRP.Graph.Graph;
import VRP.Graph.Vertex;
import ilog.concert.*;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Model {
    static IloCplex VRPD;                            // crew rostering problem
    static IloNumVar[][][] x;
    static IloNumVar[] y;
    static IloNumVar[][] A;
    static IloNumVar[] S;
    static IloNumVar[] D;
    static IloNumVar[] T;

    static int nodesQty;
    static int customersQty;
    static int vehiclesQty;

    static double t[][];

    static int depotId;
    static double vehicleFixCost;
    static double vehicleCapacity;
    static Vertex depot;
    static Graph ppGraph;                    // preprocessed graph


    public static void main(String[] arg) throws Exception {
        ReadData();
        Create_Model();
        Solve_Model();
//        WriteData();
    }

    public static void ReadData() throws Exception {
        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
                "resources/ISFNodes-06-Customers.csv",
                "resources/ISFRoads.csv"
        );
//        Graph originalGraph = Graph.buildAGraphFromCSVFile("resources/input.csv");

        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();

        preprocessedGraph.setIds();
        GlobalVars.setTheGlobalVariables(preprocessedGraph); // fill the global variables
//        preprocessedGraph.printVertices();
//        preprocessedGraph.printGraph();

        t = preprocessedGraph.getAdjacencyMatrix();
        depotId = GlobalVars.depotId;
        depot = GlobalVars.depot;
        ppGraph = preprocessedGraph;
        vehicleFixCost = GlobalVars.depot.fixedCost;
        vehicleCapacity = GlobalVars.depot.capacity;
        nodesQty = GlobalVars.numberOfNodes;
        customersQty = GlobalVars.numberOfCustomers;
        vehiclesQty = GlobalVars.numberOfVehicles;

        System.out.println("customersQty " + customersQty);
        System.out.println("vehicleQty: " + vehiclesQty);
    }

    public static void Create_Model() throws Exception {
        VRPD = new IloCplex();

        createDecisionVariables();
        createObjectiveFunctions();

        addConstraint1();
        addConstraint2();
        addConstraint3();
        addConstraint4();
        addConstraint5();
        addConstraint6();
        addConstraint7();
        addConstraint8();
        addConstraint9();
        addConstraint10();
        addConstraint11();
        addConstraint12();
        addConstraint16_18();
    }

    public static void createDecisionVariables() throws IloException {
        //--------------Decision Variables-------------------
        x = new IloNumVar[nodesQty][nodesQty][vehiclesQty];
        for (int i = 0; i < nodesQty; i++) {
            for (int j = 0; j < nodesQty; j++) {
                for (int k = 0; k < vehiclesQty; k++) {
                    x[i][j][k] = VRPD.boolVar();
                }
            }
        }

        y = new IloNumVar[vehiclesQty];
        for (int k = 0; k < vehiclesQty; k++) {
            y[k] = VRPD.boolVar();
        }

        A = new IloNumVar[nodesQty][nodesQty];
        for (int i = 0; i < nodesQty; i++) {
            for (int j = 0; j < nodesQty; j++) {
                A[i][j] = VRPD.boolVar();
            }
        }

        D = new IloNumVar[nodesQty];
        for (int i = 0; i < nodesQty; i++) {
            D[i] = VRPD.numVar(0, Double.MAX_VALUE);
        }

        T = new IloNumVar[nodesQty];
        for (int i = 0; i < nodesQty; i++) {
            T[i] = VRPD.numVar(0, Double.MAX_VALUE);
        }

        S = new IloNumVar[vehiclesQty];
        for (int i = 0; i < vehiclesQty; i++) {
            S[i] = VRPD.numVar(0, Double.MAX_VALUE);
        }
    }

    public static void createObjectiveFunctions() throws IloException {
        //-------------Objective Function-------------------
        IloLinearNumExpr obj = VRPD.linearNumExpr();
        for (int i = 0; i < nodesQty; i++) {
            if (i == depotId) continue;
            for (int j = 0; j < nodesQty; j++) {
                for (int k = 0; k < vehiclesQty; k++) {
                    obj.addTerm(t[i][j], x[i][j][k]);
                }
            }
        }

        for (int k = 0; k < vehiclesQty; k++) {
            obj.addTerm(vehicleFixCost, y[k]);
        }

        for (int i = 0; i < nodesQty; i++) {
            obj.addTerm(vertexi(i).penalty, T[i]);
        }

        VRPD.addMinimize(obj);
    }

    public static void addConstraint1() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            for (int i = 0; i < nodesQty; i++) {
                VRPD.addEq(x[i][i][k], 0.0);
            }
        }
    }

    /**
     * 2: each node is visited exactly once (Except Depot)
     */
    public static void addConstraint2() throws IloException {
        for (int i = 0; i < nodesQty; i++) {
            if (i == depotId) continue; // (Except Depot)

            IloLinearNumExpr expr1 = VRPD.linearNumExpr();
            for (int j = 0; j < nodesQty; j++) {
                for (int k = 0; k < vehiclesQty; k++) {
                    expr1.addTerm(1.0, x[j][i][k]);
                }
            }
            VRPD.addEq(expr1, 1.0);
        }
    }

    /**
     * 3: when vehicle k is not used (Y_k=0),
     * it does not drive between any pair of customers
     */
    public static void addConstraint3() throws IloException {
        //3: If not using vehicle k...
        for (int k = 0; k < vehiclesQty; k++) {
            IloLinearNumExpr expr2 = VRPD.linearNumExpr();
            for (int i = 0; i < nodesQty; i++) {
                for (int j = 0; j < nodesQty; j++) {
                    expr2.addTerm(1.0, x[i][j][k]);
                }
            }
            VRPD.add(VRPD.ifThen(VRPD.eq(y[k], 0), VRPD.le(expr2, 0.0)));
        }
    }

    /**
     * 4: Guarantee that no vehicle is
     * loaded with more than its capacity
     */
    public static void addConstraint4() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            IloLinearNumExpr expr2 = VRPD.linearNumExpr();
            for (int i = 0; i < nodesQty; i++) {
                for (int j = 0; j < nodesQty; j++) {
                    expr2.addTerm(1.0, x[i][j][k]);
                }
            }
            VRPD.le(expr2, vehicleCapacity); // TODO: +1
        }
    }

    /**
     * 5: Ensures that if any vehicle is used,
     * it certainly leaves the origin, and vice versa. at customer
     */
    public static void addConstraint5() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            IloLinearNumExpr expr7 = VRPD.linearNumExpr();
            for (int j = 0; j < nodesQty; j++) {
                expr7.addTerm(1.0, x[depotId][j][k]);
            }
            VRPD.addEq(expr7, y[k]);
        }
    }

    /**
     * 6: if a vehicle arrives to a point, either any
     * customer or the origin, the vehicle must also leave that point
     */
    public static void addConstraint6() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            for (int h = 0; h < nodesQty; h++) {
                IloLinearNumExpr expr5 = VRPD.linearNumExpr();
                IloLinearNumExpr expr6 = VRPD.linearNumExpr();
                if (h != depotId) {
                    for (int i = 0; i < nodesQty; i++) {
                        expr5.addTerm(1.0, x[i][h][k]);
                    }
                    for (int j = 0; j < nodesQty; j++) {
                        expr6.addTerm(1.0, x[h][j][k]);
                    }
                }
                VRPD.addEq(expr5, expr6);
            }
        }
    }

    /**
     * 8:Constraints (7) and (8) are related to the processing
     * sequence of the jobs, which is required to compute the completion
     * time of each job. The completion time of a job is the process time
     * of that job, in addition to sum of the process times of the jobs produced
     * before that job
     */
    public static void addConstraint7() throws IloException {
        for (int i = 0; i < nodesQty; i++) {
            for (int j = 0; j < nodesQty; j++) {
                if (i == j) continue;
                IloLinearNumExpr expr = VRPD.linearNumExpr();
                expr.addTerm(1.0, A[i][j]);
                expr.addTerm(1.0, A[j][i]);
                VRPD.addLe(expr, 1.0);
            }
        }
    }

    /**
     * 8:Constraints (7) and (8) are related to the processing
     * sequence of the jobs, which is required to compute the completion
     * time of each job. The completion time of a job is the process time
     * of that job, in addition to sum of the process times of the jobs produced
     * before that job
     */
    public static void addConstraint8() throws IloException {
        for (int i = 0; i < nodesQty; i++) {
            for (int j = 0; j < nodesQty; j++) {
                for (int r = 0; r < nodesQty; r++) {
                    if (i == j || j == r || r == i) continue;
                    IloLinearNumExpr expr = VRPD.linearNumExpr();
                    expr.addTerm(1.0, A[i][j]);
                    expr.addTerm(1.0, A[j][r]);
                    expr.addTerm(1.0, A[r][i]);
                    VRPD.addGe(expr, 1.0);
                }
            }
        }
    }

    /**
     * 9: state that if the job corresponding to customer j
     * is carried by vehicle k, then the start time of vehicle k
     * must be greater than the completion time of that job.
     */
    public static void addConstraint9() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            for (int j = 0; j < nodesQty; j++) {
                IloLinearNumExpr expr1 = VRPD.linearNumExpr();
                for (int i = 0; i < nodesQty; i++) {
                    if (i == j) continue;
                    expr1.addTerm(1.0, x[i][j][k]);
                }
                IloLinearNumExpr expr2 = VRPD.linearNumExpr();
                for (int i = 0; i < nodesQty; i++) {
                    if (i == j) continue;
                    expr2.addTerm(vertexi(i).processTime, A[i][j]);
                }
                VRPD.add(VRPD.ifThen(VRPD.eq(expr1, 1),
                        VRPD.ge(VRPD.diff(S[k], vertexi(j).processTime), expr2)));
            }
        }
    }

    /**
     * Constraints (10) and (11) are related to the delivery
     * times to the customers. According to constraints (10),
     * the time a vehicle delivers the job of its first customer
     * , is greater than the start time of that vehicle, in addition
     * to the travel time between the origin and that customer.
     * Constraints (11) ensure that for the customers visited by
     * vehicle k, the delivery time of a customer must be greater
     * than the delivery time of the previous customer, plus the
     * travel time between the two customers
     */
    public static void addConstraint10() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            for (int j = 0; j < nodesQty; j++) {
                int i = depotId;
                if (i == j) continue;
                VRPD.add(VRPD.ifThen(VRPD.eq(x[i][j][k], 1),
                        VRPD.ge(VRPD.diff(D[j], S[k]), t[i][j])));
            }
        }
    }

    /**
     * Constraints (10) and (11) are related to the delivery
     * times to the customers. According to constraints (10),
     * the time a vehicle delivers the job of its first customer
     * , is greater than the start time of that vehicle, in addition
     * to the travel time between the origin and that customer.
     * Constraints (11) ensure that for the customers visited by
     * vehicle k, the delivery time of a customer must be greater
     * than the delivery time of the previous customer, plus the
     * travel time between the two customers
     */
    public static void addConstraint11() throws IloException {
        for (int i = 0; i < nodesQty; i++) {
            for (int j = 0; j < nodesQty; j++) {
                if (i == j) continue;
                if (i == depotId) continue;
                if (j == depotId) continue;
                IloLinearNumExpr expr = VRPD.linearNumExpr();
                for (int k = 0; k < vehiclesQty; k++) {
                    expr.addTerm(x[i][j][k], 1.0);
                }
                VRPD.add(VRPD.ifThen(VRPD.eq(expr, 1),
                        VRPD.ge(VRPD.diff(D[j], D[i]), (t[i][j]))));
            }
        }
    }

    public static void addConstraint12() throws IloException {
        for (int i = 0; i < customersQty; i++) {
            VRPD.addGe(T[i], VRPD.diff(D[i], vertexi(i).dueDate));
        }
    }

    public static void addConstraint16_18() throws IloException {
        for (int i = 0; i < nodesQty; i++) {
            VRPD.addGe(D[i], 0);
            VRPD.addGe(T[i], 0);
        }
        for (int k = 0; k < vehiclesQty; k++) {
            VRPD.addGe(S[k], 0);
        }
    }

    public static void Solve_Model() throws Exception {
//        VRPD.setParam(IloCplex.IntParam.Simplex.Display, 0);
        long startTime = System.currentTimeMillis();
        if (VRPD.solve()) {
            long finishTime = System.currentTimeMillis();
            System.out.println("Status = " + VRPD.getStatus());
            System.out.println("Objective Value = " + String.format("%.2f", VRPD.getObjValue()));
            System.out.println("yk, mdt, zk, dd, T, penalty");
            for (int k = 0; k < vehiclesQty; k++) {
                long yk = Math.round(VRPD.getValue(y[k]));
                double zk = (VRPD.getValue(D[depotId]));
                double dk = (VRPD.getValue(T[k]));
                String zjkd = String.format("%.2f", zk);
                String dkd = String.format("%.2f", dk);
                String pjkd = String.format("%.1f", VRPD.getValue(T[k]) * depot.penalty);

                System.out.print("Y" + k + " (" + yk + ", "
                        + ", " + zjkd + ", " + depot.dueDate + ", " + dkd + ", " + pjkd + ")" + ",");
                if (yk == 0) System.out.println();
                if (yk == 0) continue;
                for (int i = nodesQty - 1; i >= 0; i--) {
                    for (int j = nodesQty - 1; j >= 0; j--) {
//                        long xijk = Math.round(VRPD.getValue(x[i][j][k]));
                        double zjk = (VRPD.getValue(D[j]));
//                        long djk = Math.round(VRPD.getValue(T[j][k]))
                        double xijk = (VRPD.getValue(x[i][j][k]));

                        if (xijk == 0) continue;
                        Vertex u = ppGraph.getVertexById(i);
                        Vertex v = ppGraph.getVertexById(j);

                        System.out.print(" "
                                + u
                                + " -("
                                + String.format("%.2f", ppGraph.getDistance(u, v))
                                + String.format(", %.2f", zjk)
                                + ")-> "
                                + v + ","
                        );
                    }
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("Number of Nodes: " + VRPD.getNnodes64());
            System.out.printf("Processing Time: %.2fs\n", (finishTime - startTime) / 1000.);
        } else {
            System.out.println();
            System.out.println("Can't be solved!!!!");
        }
    }

    public static Vertex vertexi(int i) {
        return ppGraph.getVertexById(i);
    }
}

