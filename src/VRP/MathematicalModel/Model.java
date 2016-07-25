package VRP.MathematicalModel;

import VRP.Algorithms.Dijkstra.Dijkstra;
import VRP.GlobalVars;
import VRP.Graph.Graph;
import VRP.Graph.Vertex;
import ilog.concert.*;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;


//class Pairing_ {
//    public String Depot;
//    public int idOfPairing;
//    public double Starttime;
//    public double Endtime;
//    public double Duration;
//    public double Cost_Pairing;
//    public double Rest_Pairing;
//    public int Number_of_Trips;
//    public int Crew_AssignedPerson;
//}

public class Model {
    static IloCplex VRPD;                            // crew rostering problem
    static IloNumVar[][][] x;
    static IloNumVar[] y;
    static IloNumVar[][] z;
    static IloNumVar[][] delta;
    static IloNumVar[][] alpha;

    static int Number_Customers;                    // the maximum number of crew = number of pairings
    static int Number_Nodes;
    static int Max_Number_Vehicles;                    // the maximum number of crew = number of pairings
    static double Cost_ShortestPath[][];
    static double ServiceTime[];
    static int FixedCost_Vehicle;          // fix cost for employing one crew
    static int PenaltyCost[];
    static int Demand[];
    static int Capacity[];
    static double DD[];
    static int BigM = (int) 1e9;                      // Big M
    static double Start_ProcessTime;
    static double End_ProcessTime;
    static int depotId;
    static Graph ppGraph;                    // preprocessed graph

    public static void main(String[] arg) throws Exception {
        ReadData();
        Create_Model();
        Solve_Model();
//        WriteData();
    }

    public static void ReadData() throws Exception {
//        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
//                "/home/iman/Workspace/QGIS/IsfahanAttributeTables/ISFNodes.csv",
//                "/home/iman/Workspace/QGIS/IsfahanAttributeTables/ISFRoads.csv"
//        );
        Graph originalGraph = Graph.buildAGraphFromCSVFile("resources/input.csv");

        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();
        preprocessedGraph.printGraph();

        // fill the global variables
        GlobalVars.setTheGlobalVariables(preprocessedGraph);

        // fill the arrays needed for model
        Cost_ShortestPath = preprocessedGraph.getTheAdjacencyMatrix();
        ServiceTime = GlobalVars.customerServiceTimes;
        PenaltyCost = GlobalVars.customerPenaltyCosts;
        Demand = GlobalVars.customerDemands;
        Capacity = GlobalVars.vehicleCapacities;
        DD = GlobalVars.nodeDueDates;
        Number_Customers = GlobalVars.numberOfCustomers;
        Number_Nodes = GlobalVars.numberOfNodes;
        Max_Number_Vehicles = GlobalVars.numberOfVehicles;
        FixedCost_Vehicle = (int) GlobalVars.vehicleFixedCost;
        depotId = GlobalVars.numberOfCustomers;
        ppGraph = preprocessedGraph;
    }

    public static void Create_Model() throws Exception {
        VRPD = new IloCplex();

        createDecisionVariables();
        createObjectiveFunctions();

        addConstraint2();
        addConstraint3();
        addConstraint4();
        addConstraint5();
        addConstraint6();
        addConstraint7();
        addConstraint7();
        addConstraint8();
        addConstraint9();
//---------------Constraints-----------------------
        //11: Relation between alpha and (z-dd)
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Nodes; i++) {
//                VRPD.addLe(VRPD.diff(VRPD.diff(z[i][k], DD[i]), VRPD.prod(BigM, VRPD.diff(2, VRPD.sum(alpha[i][k], y[k])))), 0);
//            }
//        }
//        //12: Relation between alpha and (z-dd)
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Nodes; i++) {
//                VRPD.addLe(VRPD.sum(VRPD.diff(z[i][k], DD[i]), VRPD.prod(BigM, VRPD.sum(1, VRPD.diff(alpha[i][k], y[k])))), 0);
//            }
//        }
//        //13: delta=0
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Nodes; i++) {
//                VRPD.addLe(delta[i][k], VRPD.sum(BigM, VRPD.diff(2, VRPD.sum(alpha[i][k], y[k]))));
//            }
//        }
//        //14-1: delta=z-dd
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Nodes; i++) {
//                VRPD.addLe(delta[i][k], VRPD.sum(VRPD.diff(z[i][k], DD[i]), VRPD.prod(BigM, VRPD.sum(1, VRPD.diff(alpha[i][k], y[k])))));
//            }
//        }
//        //14-2: delta=z-dd
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Nodes; i++) {
//                VRPD.addGe(delta[i][k], VRPD.diff(VRPD.diff(z[i][k], DD[i]), VRPD.prod(BigM, VRPD.sum(1, VRPD.diff(alpha[i][k], y[k])))));
//            }
//        }
        for (int k = 0; k < Max_Number_Vehicles; k++) {
            for (int i = 0; i < Number_Nodes; i++) {
                VRPD.addEq(x[i][i][k], 0.0);
            }
        }
    }

    public static void createDecisionVariables() throws IloException {
        //--------------Decision Variables-------------------
        x = new IloNumVar[Number_Nodes][Number_Nodes][Max_Number_Vehicles];
        for (int i = 0; i < Number_Nodes; i++) {
            for (int j = 0; j < Number_Nodes; j++) {
                for (int k = 0; k < Max_Number_Vehicles; k++) {
                    x[i][j][k] = VRPD.boolVar();
                }
            }
        }

        y = new IloNumVar[Max_Number_Vehicles];
        for (int k = 0; k < Max_Number_Vehicles; k++) {
            y[k] = VRPD.boolVar();
        }

        z = new IloNumVar[Number_Nodes][Max_Number_Vehicles];
        for (int k = 0; k < Max_Number_Vehicles; k++) {
            for (int i = 0; i < Number_Nodes; i++) {
                z[i][k] = VRPD.numVar(0, Double.MAX_VALUE);
            }
        }

        delta = new IloNumVar[Number_Nodes][Max_Number_Vehicles];
        for (int k = 0; k < Max_Number_Vehicles; k++) {
            for (int i = 0; i < Number_Nodes; i++) {
                delta[i][k] = VRPD.numVar(0, Double.MAX_VALUE);
            }
        }

        alpha = new IloNumVar[Number_Nodes][Max_Number_Vehicles];
        for (int i = 0; i < Number_Nodes; i++) {
            for (int k = 0; k < Max_Number_Vehicles; k++) {
                alpha[i][k] = VRPD.boolVar();
            }
        }
    }

    public static void createObjectiveFunctions() throws IloException {
        //-------------Objective Function-------------------
        IloLinearNumExpr obj = VRPD.linearNumExpr();
        for (int i = 0; i < Number_Nodes; i++) {
            for (int j = 0; j < Number_Nodes; j++) {
                for (int k = 0; k < Max_Number_Vehicles; k++) {
                    obj.addTerm(Cost_ShortestPath[i][j], x[i][j][k]);
                }
            }
        }

        for (int k = 0; k < Max_Number_Vehicles; k++) {
            obj.addTerm(FixedCost_Vehicle, y[k]);
        }

        for (int k = 0; k < Max_Number_Vehicles; k++) {
            for (int i = 0; i < Number_Nodes; i++) {
                obj.addTerm(PenaltyCost[i], delta[i][k]);
            }
        }

        VRPD.addMinimize(obj);
    }

    public static void addConstraint2() throws IloException {
        //2: each customer is visited exactly once (Except Depot)
        for (int i = 0; i < Number_Nodes; i++) {
            if (i == depotId) continue; // (Except Depot)

            IloLinearNumExpr expr1 = VRPD.linearNumExpr();
            for (int j = 0; j < Number_Nodes; j++) {
                for (int k = 0; k < Max_Number_Vehicles; k++) {
                    expr1.addTerm(1.0, x[j][i][k]);
                }
            }
            VRPD.addEq(expr1, 1.0);
        }
    }

    public static void addConstraint3() throws IloException {
        //3-1: If not using vehicle k...
        for (int k = 0; k < Max_Number_Vehicles; k++) {
            IloLinearNumExpr expr2 = VRPD.linearNumExpr();
            for (int i = 0; i < Number_Nodes; i++) {
                for (int j = 0; j < Number_Nodes; j++) {
                    expr2.addTerm(1.0, x[i][j][k]);
                }
            }
            VRPD.addLe(expr2, VRPD.prod(BigM, y[k]));
            //3-2: if using vehicle k...
            VRPD.addGe(expr2, VRPD.diff(2, VRPD.prod(BigM, VRPD.diff(1, y[k]))));
        }
    }

    public static void addConstraint4() throws IloException {
        //4: no vehicle is loaded with more than its capacity
        for (int k = 0; k < Max_Number_Vehicles; k++) {
            IloLinearNumExpr expr3 = VRPD.linearNumExpr();
            for (int i = 0; i < Number_Nodes; i++) {
                for (int j = 0; j < Number_Nodes; j++) {
                    expr3.addTerm(Demand[j], x[i][j][k]);
                }
            }
            VRPD.addLe(expr3, Capacity[k]);
        }
    }

    public static void addConstraint5() throws IloException {
        //5: after arriving at a customer the vehicle leaves again
        for (int k = 0; k < Max_Number_Vehicles; k++) {
            for (int h = 0; h < Number_Nodes; h++) {
                IloLinearNumExpr expr5 = VRPD.linearNumExpr();
                IloLinearNumExpr expr6 = VRPD.linearNumExpr();
                if (h != depotId) {                          // how to define depot???
                    for (int i = 0; i < Number_Nodes; i++) {
                        expr5.addTerm(1.0, x[i][h][k]);
                    }
                    for (int j = 0; j < Number_Nodes; j++) {
                        expr6.addTerm(1.0, x[h][j][k]);
                    }
                }
                VRPD.addEq(expr5, expr6);
            }
        }
    }

    public static void addConstraint6() throws IloException {
        //6:for each vehicle: Emanations from depot=1
        for (int k = 0; k < Max_Number_Vehicles; k++) {
            IloLinearNumExpr expr7 = VRPD.linearNumExpr();
            for (int j = 0; j < Number_Nodes; j++) {
                expr7.addTerm(1.0, x[depotId][j][k]);
            }
            VRPD.addEq(expr7, y[k]);
            // VRPD.addEq(expr7, VRPD.sum(1.0, VRPD.prod(BigM, VRPD.diff(1.0, y[k]))));
        }
    }

    public static void addConstraint7() throws IloException {
        //7-1:for each vehicle: links toward depot=1
        for (int k = 0; k < Max_Number_Vehicles; k++) {
            IloLinearNumExpr expr8 = VRPD.linearNumExpr();
            for (int i = 0; i < Number_Nodes; i++) {
                expr8.addTerm(1.0, x[i][depotId][k]);
            }
            VRPD.addEq(expr8, y[k]);
            // VRPD.addLe(expr8, VRPD.sum(1.0, VRPD.prod(BigM, VRPD.diff(1.0, y[k]))));
        }
    }

    public static void addConstraint8() throws IloException {
        // 8: arrival time to each customer
        for (int k = 0; k < Max_Number_Vehicles; k++) {
            for (int i = 0; i < Number_Nodes; i++) {
                for (int j = 0; j < Number_Nodes; j++) {
                    if (i == j) continue;
                    if (i == depotId)
                        VRPD.addGe(z[j][k],
                                VRPD.diff((Cost_ShortestPath[i][j] + ServiceTime[j]), VRPD.prod(BigM, VRPD.diff(1, x[i][j][k]))));
                    else
                        VRPD.addGe(VRPD.diff(z[j][k], z[i][k]),
                                VRPD.diff((Cost_ShortestPath[i][j] + ServiceTime[j]), VRPD.prod(BigM, VRPD.diff(1, x[i][j][k]))));
                }
            }
        }
    }

    public static void addConstraint9() throws IloException {
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Nodes; i++) {
//                Cplex
//                VRPD.add((z[i][k] == delta[i][k]));
//            }
//        }
    }


    public static void Solve_Model() throws Exception {
//        VRPD.setParam(IloCplex.IntParam.Simplex.Display, 0);
        Start_ProcessTime = System.currentTimeMillis();
        if (VRPD.solve()) {
            End_ProcessTime = System.currentTimeMillis();
            System.out.println("--------------------------------------------------------");
            System.out.println("--------------------------------------------------------");
            System.out.println("Status = " + VRPD.getStatus());
            System.out.println("Objective Value = " + VRPD.getObjValue());
            for (int k = 0; k < Max_Number_Vehicles; k++) {
                System.out.println("Y (" + k + ") = " + VRPD.getValue(y[k]));
            }
            for (int k = 0; k < Max_Number_Vehicles; k++) {
                if (VRPD.getValue(y[k]) == 1.0) {
                    System.out.print("y" + k + ":");
                    for (int i = Number_Nodes - 1; i >= 0; i--) {
                        for (int j = Number_Nodes - 1; j >= 0; j--) {
                            if (VRPD.getValue(x[i][j][k]) == 1.0) {
                                Vertex u = ppGraph.getVertexById(i);
                                Vertex v = ppGraph.getVertexById(j);
                                System.out.print(" "
                                        + u
                                        + " -("
                                        + ppGraph.getDistance(u, v)
                                        + ", " + VRPD.getValue(z[j][k])
                                        + ")-> "
                                        + v + ","
                                );
//
                            }
                        }
                    }
                    System.out.println();
                }
            }
            System.out.println();
            System.out.println("Processing Time: " + (End_ProcessTime - Start_ProcessTime) / 1000. + " s");
        } else {
            System.out.println();
            System.out.println("Can't be solved!!!!");
        }
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

