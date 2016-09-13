package Main.AutomatedTests.Experiment1.MathematicalModel;

import Main.Algorithms.Dijkstra.Dijkstra;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.Graph.Vertex;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class CplexAutoTest {
    static IloCplex VRPD;                            // crew rostering problem
    static IloNumVar[][][] x;
    static IloNumVar[] y;
    static IloNumVar[][] z;
    static IloNumVar[] delta;

    static int nodesQty;
    static int customersQty;
    static int vehiclesQty;

    static double t[][];
    static List<Integer> vehicleIds;         // indexes of nodes with vehicles

    static int depotId;
    static Vertex depot;
    static Graph ppGraph;                    // preprocessed graph

    static PrintWriter out;

    public static void main(String[] arg) throws Exception {
        ReadData();
//        Create_Model();
//        Solve_Model();
//        WriteData();
    }

    public static void ReadData() throws Exception {
        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
                "resources/ISFNodes-10-09-Ex2.csv",
                "resources/ISFRoads.csv"
        );
//        Main.Graph originalGraph = Main.Graph.buildAGraphFromCSVFile("resources/input.csv");

        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph reducedGraph = dijkstra.makeShortestPathGraph();
        reducedGraph.setIds();

        FileInputStream fileInputStream = new FileInputStream(new File("resources/t1-input-subset-01.csv"));
        Scanner sc = new Scanner(fileInputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(new File("resources/t1-automated-test-results-cplex-tmp.csv"));
        out = new PrintWriter(fileOutputStream);
        out.println(sc.nextLine() + ",CPLEXValue,CPLEXTime,CPLEXNodes,CPLEXStatus");
        out.flush();

        while (sc.hasNextLine()) {
            String autoTestRow = sc.nextLine();
            out.print(autoTestRow);
            System.out.println(autoTestRow);

            Graph preprocessedGraph = reducedGraph.getCopy();
//            Utils.modifyGraphByAutomatedInput(preprocessedGraph, autoTestRow);

            Dijkstra dijkstra2 = new Dijkstra(preprocessedGraph);
            preprocessedGraph = dijkstra2.makeShortestPathGraph();

            // fill the global variables
            preprocessedGraph.setIds();
            GlobalVars.setTheGlobalVariables(preprocessedGraph);
//            preprocessedGraph.printVertices();
//            preprocessedGraph.printGraph();

            System.out.println("Number of Customers, Vehicles: " +
                    GlobalVars.numberOfCustomers + " " + GlobalVars.numberOfVehicles);

            t = preprocessedGraph.getAdjacencyMatrix();
            depotId = GlobalVars.depotId;
            depot = preprocessedGraph.getVertexById(depotId);
            ppGraph = preprocessedGraph;

            nodesQty = GlobalVars.numberOfNodes;
            customersQty = GlobalVars.numberOfCustomers;

//            vehicleIds = new ArrayList<>();
//            for (Vertex v : ppGraph.getVertices()) {
//                if (v.hasVehicle == 1) vehicleIds.add(v.getId());
//            }
            Create_Model();
            Solve_Model();
            VRPD.clearModel();
        }
        out.close();
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

        z = new IloNumVar[nodesQty][vehiclesQty];
        for (int i = 0; i < nodesQty; i++) {
            for (int k = 0; k < vehiclesQty; k++) {
                z[i][k] = VRPD.numVar(0, Double.MAX_VALUE);
            }
        }

        delta = new IloNumVar[vehiclesQty];
        for (int k = 0; k < vehiclesQty; k++) {
            delta[k] = VRPD.numVar(0, Double.MAX_VALUE);
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
            obj.addTerm(getVehicle(k).fixedCost, y[k]);
        }

        for (int k = 0; k < vehiclesQty; k++) {
            obj.addTerm(depot.penalty, delta[k]);
        }

        VRPD.addMinimize(obj);
    }

    // self edges
    public static void addConstraint1() throws IloException {
        for (int k = 0; k < GlobalVars.numberOfVehicles; k++) {
            for (int i = 0; i < GlobalVars.numberOfNodes; i++) {
                VRPD.addEq(x[i][i][k], 0.0);
            }
        }

//        for (int k = 0; k < vehiclesQty; k++) {
//            for (int i = 0; i < nodesQty; i++) {
//                for (int j = 0; j < nodesQty; j++) {
//                    if (i != depotId && j != depotId) {
//                        VRPD.addEq(x[i][j][k], VRPD.diff(1, x[j][i][k]));
//                    }
//                }
//            }
//        }
    }

    //2: each node is visited exactly once (Except Depot)
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

    //3: when yk is zero or one
    public static void addConstraint3() throws IloException {
        //3-1: If not using vehicle k...
        for (int k = 0; k < vehiclesQty; k++) {
            IloLinearNumExpr expr2 = VRPD.linearNumExpr();
            for (int i = 0; i < nodesQty; i++) {
                for (int j = 0; j < nodesQty; j++) {
                    expr2.addTerm(1.0, x[i][j][k]);
                }
            }
            VRPD.add(VRPD.ifThen(VRPD.eq(y[k], 0), VRPD.le(expr2, 0)));
            //3-2: if using vehicle k...
            VRPD.add(VRPD.ifThen(VRPD.eq(y[k], 1), VRPD.ge(expr2, 2)));

            //3-3: if vehicle k has been used then the edge depot -> vehicle(k) is must be in the graph
            VRPD.add(VRPD.ifThen(VRPD.eq(y[k], 1), VRPD.eq(x[depotId][getVehicle(k).getId()][k], 1)));
        }
    }

    //4: no vehicle is loaded with more than its capacity
    public static void addConstraint4() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            IloLinearNumExpr expr3 = VRPD.linearNumExpr();
            for (int i = 0; i < nodesQty; i++) {
                for (int j = 0; j < nodesQty; j++) {
                    if (j == depotId) continue;
                    Vertex vj = ppGraph.getVertexById(j);
//                    expr3.addTerm(vj.demand, x[i][j][k]);
                }
            }

            VRPD.addLe(expr3, getVehicle(k).capacity);
        }
    }

    //5: after arriving at a customer the vehicle leaves again
    public static void addConstraint5() throws IloException {
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

    //6:for each vehicle: Emanations from depot=1
    public static void addConstraint6() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            IloLinearNumExpr expr7 = VRPD.linearNumExpr();
            for (int j = 0; j < nodesQty; j++) {
                expr7.addTerm(1.0, x[depotId][j][k]);
            }
            VRPD.addEq(expr7, y[k]);
        }
    }

    //7:for each vehicle: links toward depot=1
    public static void addConstraint7() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            IloLinearNumExpr expr8 = VRPD.linearNumExpr();
            for (int i = 0; i < nodesQty; i++) {
                expr8.addTerm(1.0, x[i][depotId][k]);
            }
            VRPD.addEq(expr8, y[k]);
        }
    }

    // 8: arrival time to depot
    public static void addConstraint8() throws IloException {
        // 8: arrival time to each customer
        for (int k = 0; k < vehiclesQty; k++) {
            for (int i = 0; i < nodesQty; i++) {
                for (int j = 0; j < nodesQty; j++) {
                    if (i == j) continue;
                    Vertex v = ppGraph.getVertexById(j);

//                    if (i == depotId)
//                        VRPD.add(VRPD.ifThen(VRPD.eq(x[i][j][k], 1),
//                                VRPD.eq(z[j][k], v.serviceTime + v.mdt)));
//                    else
//                        VRPD.add(VRPD.ifThen(VRPD.eq(x[i][j][k], 1),
//                                VRPD.eq(VRPD.diff(z[j][k], z[i][k]), (t[i][j] + v.serviceTime))));
                }
            }
        }
    }

    // 9: depot delay
    public static void addConstraint9() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            VRPD.add(VRPD.ifThen(VRPD.ge(z[depotId][k], depot.dueDate),
                    VRPD.eq(delta[k], VRPD.diff(z[depotId][k], depot.dueDate)))
            );
        }
    }

    public static void Solve_Model() throws Exception {
//        VRPD.setParam(IloCplex.IntParam.Simplex.Display, 0);
        VRPD.setOut(null);
        String expandedNodes = "?";
        String elapsedTime = "?";
        String optimalValue = "?";
        String status = "?";
        long startTime = System.currentTimeMillis();
        try {
            VRPD.setParam(IloCplex.DoubleParam.TiLim, 100);
            if (VRPD.solve()) {
                long finishTime = System.currentTimeMillis();
                elapsedTime = String.format("%.2f", (finishTime - startTime) / 1000.);
                optimalValue = String.format("%.2f", VRPD.getObjValue());


                System.out.println("Status = " + VRPD.getStatus());
                System.out.println("Objective Value = " + String.format("%.2f", VRPD.getObjValue()));
//                System.out.println("yk, mdt, zk, dd, delta, penalty");

                status = "" + VRPD.getStatus();
//                for (int k = 0; k < vehiclesQty; k++) {
//                    long yk = Math.round(VRPD.getValue(y[k]));
//                    double zk = (VRPD.getValue(z[depotId][k]));
//                    double dk = (VRPD.getValue(delta[k]));
//                    String zjkd = String.format("%.2f", zk);
//                    String dkd = String.format("%.2f", dk);
//                    String pjkd = String.format("%.1f", VRPD.getValue(delta[k]) * depot.penalty);
//
//                    System.out.print("Y" + k + " (" + yk + ", " + getVehicle(k).mdt
//                            + ", " + zjkd + ", " + depot.dueDate + ", " + dkd + ", " + pjkd + ")" + ",");
//                    if (yk == 0) System.out.println();
//                    if (yk == 0) continue;
//                    for (int i = nodesQty - 1; i >= 0; i--) {
//                        for (int j = nodesQty - 1; j >= 0; j--) {
////                        long xijk = Math.round(VRPD.getValue(x[i][j][k]));
//                            double zjk = (VRPD.getValue(z[j][k]));
////                        long djk = Math.round(VRPD.getValue(delta[j][k]))
//                            double xijk = (VRPD.getValue(x[i][j][k]));
//
//                            if (xijk == 0) continue;
//                            Vertex u = ppGraph.getVertexById(i);
//                            Vertex v = ppGraph.getVertexById(j);
//
//                            System.out.print(" "
//                                    + u
//                                    + " -("
//                                    + String.format("%.2f", ppGraph.getDistance(u, v))
//                                    + String.format(", %.2f", zjk)
//                                    + ")-> "
//                                    + v + ","
//                            );
//                        }
//                    }
//                    System.out.println();
//                }

                expandedNodes = "" + VRPD.getNnodes64();
//                System.out.println();
                System.out.println("Number of Nodes: " + VRPD.getNnodes64());
                System.out.printf("Processing Time: %.2fs\n", (finishTime - startTime) / 1000.);
            } else {
                long finishTime = System.currentTimeMillis();
                elapsedTime = String.format("%.2f", (finishTime - startTime) / 1000.);
                optimalValue = "NA";
//                System.out.println();
                System.out.println("Can't be solved!!!!");
            }
        } catch (Exception e) {
            optimalValue = "ML";
            long finishTime = System.currentTimeMillis();
            elapsedTime = String.format("%.2f", (finishTime - startTime) / 1000.);
        }
        System.out.println("--------------------------------------------------------");
        out.println("," + optimalValue + "," + elapsedTime + "," + expandedNodes + "," + status);
        out.flush();
    }

    public static Vertex getVehicle(int id) {
        return ppGraph.getVertexById(vehicleIds.get(id));
    }
//    public static void WriteData() throws Exception {
//
////            FileOutputStream object = new FileOutputStream("Output_Crew_Phase3_Crew Assignment_Depot.csv");
////            PrintWriter DepotAssignment = new PrintWriter(object);
////            DepotAssignment.println("Phase 3:"+ ","+ "Assignment of Pairings to Crew");
////            DepotAssignment.println("Depot: " + ","+Pairings_SelectedDepot.get(0).Depot);
////            DepotAssignment.println("Number of Pairings in the Depot: " + "," + Pairings_SelectedDepot.size());
////            DepotAssignment.println("Number of Employees: "+ "," + Number_Employees);
////            DepotAssignment.println("Computation Time: " + ","+ (End_ProcessTime - Start_ProcessTime) / 1000 + " Sec");
////            DepotAssignment.println("," +"Pairing"+","+ "Depot" + "," + "Lable of Crew" +","+ "StartTime" + ","+"EndTime");
////            for (int p = 0; p < Pairings_SelectedDepot.size(); p++) {
////                DepotAssignment.println((p+1)+","+"Pairing "+Pairings_SelectedDepot.get(p).idOfPairing+","+Pairings_SelectedDepot.get(p).Depot+
////                        ","+Pairings_SelectedDepot.get(p).Crew_AssignedPerson+","+Pairings_SelectedDepot.get(p).Starttime+","+Pairings_SelectedDepot.get(p).Endtime);
////            }
////            DepotAssignment.close();
//    }
}

