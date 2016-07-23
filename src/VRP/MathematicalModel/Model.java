package VRP.MathematicalModel;

import VRP.Algorithms.Dijkstra.Dijkstra;
import VRP.GlobalVars;
import VRP.Graph.Graph;
//import VRP.Graph.Vertex;
//import ilog.concert.*;
//import ilog.cplex.*;
//
//import ilog.cplex.*;
//import ilog.concert.IloException;
//import ilog.concert.IloNumExpr;
//import ilog.concert.IloNumVar;
//import ilog.concert.IloRange;
//import ilog.cplex.IloCplex;
//import ilog.cplex.IloCplex.UnknownObjectException;

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
//    static IloCplex VRPD;                            // crew rostering problem
//    static IloNumVar[][][] x;
//    static IloNumVar[] y;
//    static IloNumVar[][] z;
//    static IloNumVar[][] delta;
//    static IloNumVar[][] alpha;

    static int Number_Customers;                    // the maximum number of crew = number of pairings
    static int Max_Number_Vehicles;                    // the maximum number of crew = number of pairings
    static double Cost_ShortestPath[][];
    static double ServiceTime[];
    static int FixedCost_Vehicle = 10000;          // fix cost for employing one crew
    static int PenaltyCost[];
    static int Demand[];
    static int Capacity[];
    static double DD[];
    static int BigM = 1000000;                      // Big M
    static double Start_ProcessTime;
    static double End_ProcessTime;

    public static void main(String[] arg) throws Exception {
        ReadData();
//        Create_Model();
//        Solve_Model();
//        WriteData();
    }

    public static void ReadData() throws Exception {
        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
                "/home/iman/Workspace/QGIS/IsfahanAttributeTables/ISFNodes.csv",
                "/home/iman/Workspace/QGIS/IsfahanAttributeTables/ISFRoads.csv"
        );
        // Graph originalGraph = Graph.buildAGraphFromCSVFile("resources/input.csv");

        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();

        // fill the global variables
        GlobalVars.setTheGlobalVariables(preprocessedGraph);

        // fill the arrays needed for model
        Cost_ShortestPath = preprocessedGraph.getTheAdjacencyMatrix();
        ServiceTime = GlobalVars.customerServiceTimes;
        PenaltyCost = GlobalVars.customerPenaltyCosts;
        Demand = GlobalVars.customerDemands;
        Capacity = GlobalVars.vehicleCapacities;
        DD = GlobalVars.nodeDueDates;
    }
//
//    public static void Create_Model() throws Exception {
//        VRPD = new IloCplex();
////--------------Decision Variables-------------------
//        x = new IloNumVar[Number_Customers][Number_Customers][Max_Number_Vehicles];
//        for (int i = 0; i < Number_Customers; i++) {
//            for (int j = 0; j < Number_Customers; j++) {
//                for (int k = 0; k < Max_Number_Vehicles; k++) {
//                    x[i][j][k] = VRPD.boolVar();
//                }
//            }
//        }
//        y = new IloNumVar[Max_Number_Vehicles];
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            y[k] = VRPD.boolVar();
//        }
//        z = new IloNumVar[Number_Customers][Max_Number_Vehicles];
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Customers; i++) {
//                z[i][k] = VRPD.boolVar();
//            }
//        }
//        delta = new IloNumVar[Number_Customers][Max_Number_Vehicles];
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Customers; i++) {
//                delta[i][k] = VRPD.boolVar();
//            }
//        }
//        alpha = new IloNumVar[Number_Customers][Max_Number_Vehicles];
//        for (int i = 0; i < Number_Customers; i++) {
//            for (int k = 0; k < Max_Number_Vehicles; k++) {
//                alpha[i][k] = VRPD.boolVar();
//            }
//        }
////-------------Objective Function-------------------
//        IloLinearNumExpr obj = VRPD.linearNumExpr();
//        for (int i = 0; i < Number_Customers; i++) {
//            for (int j = 0; j < Number_Customers; j++) {
//                for (int k = 0; k < Max_Number_Vehicles; k++) {
//                    obj.addTerm(Cost_ShortestPath[i][j], x[i][j][k]);
//                }
//            }
//        }
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            obj.addTerm(FixedCost_Vehicle, y[k]);
//        }
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Customers; i++) {
//                obj.addTerm(PenaltyCost[i], delta[i][k]);
//            }
//        }
//        VRPD.addMinimize(obj);
////---------------Constraints-----------------------
//        //2: each customer is visited exactly once
//        for (int i = 0; i < Number_Customers; i++) {
//            IloLinearNumExpr expr1 = VRPD.linearNumExpr();
//            for (int j = 0; j < Number_Customers; j++) {
//                for (int k = 0; k < Number_Customers; k++) {
//                    expr1.addTerm(1.0, x[i][j][k]);
//                }
//            }
//            VRPD.addEq(expr1, 1.0);
//        }
//        //3-1: If not using vehicle k...
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            IloLinearNumExpr expr2 = VRPD.linearNumExpr();
//            for (int i = 0; i < Number_Customers; i++) {
//                for (int j = 0; j < Number_Customers; j++) {
//                    expr2.addTerm(1.0, x[i][j][k]);
//                }
//            }
//            VRPD.addLe(expr2, VRPD.prod(BigM, y[k]));
//            //3-2: if using vehicle k...
//            VRPD.addGe(expr2, VRPD.diff(2, VRPD.prod(BigM, VRPD.diff(1, y[k]))));
//        }
//        //4: no vehicle is loaded with more than its capacity
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            IloLinearNumExpr expr3 = VRPD.linearNumExpr();
//            for (int i = 0; i < Number_Customers; i++) {
//                IloLinearNumExpr expr4 = VRPD.linearNumExpr();
//                for (int j = 0; j < Number_Customers; j++) {
//                    expr4.addTerm(1.0, x[i][j][k]);
//                }
//                expr3.addTerm(Demand[i], (IloNumVar) expr4);
//            }
//            VRPD.addGe(expr3, Capacity[k]);
//        }
//        //5: after arriving at a customer the vehicle leaves again
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            IloLinearNumExpr expr5 = VRPD.linearNumExpr();
//            IloLinearNumExpr expr6 = VRPD.linearNumExpr();
//            for (int h = 0; h < Number_Customers; h++) {
//                if (h != 0) {                          // how to define depot???
//                    for (int i = 0; i < Number_Customers; i++) {
//                        expr5.addTerm(1.0, x[i][h][k]);
//                    }
//                    for (int j = 0; j < Number_Customers; j++) {
//                        expr6.addTerm(1.0, x[h][j][k]);
//                    }
//                }
//            }
//            VRPD.addEq(expr5, expr6);
//        }
//        //6-1:for each vehicle: Emanations from depot=1
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            IloLinearNumExpr expr7 = VRPD.linearNumExpr();
//            for (int j = 0; j < Number_Customers; j++) {
//                expr7.addTerm(1.0, x[0][j][k]);
//            }
//            VRPD.addLe(expr7, VRPD.sum(1.0, VRPD.prod(BigM, VRPD.diff(1.0, y[k]))));
//        }
//        //6-2:for each vehicle: Emanations from depot=1
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            IloLinearNumExpr expr7 = VRPD.linearNumExpr();
//            for (int j = 0; j < Number_Customers; j++) {
//                expr7.addTerm(1.0, x[0][j][k]);
//            }
//            VRPD.addGe(expr7, VRPD.diff(1.0, VRPD.prod(BigM, VRPD.diff(1.0, y[k]))));
//        }
//        //7-1:for each vehicle: links toward depot=1
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            IloLinearNumExpr expr8 = VRPD.linearNumExpr();
//            for (int i = 0; i < Number_Customers; i++) {
//                expr8.addTerm(1.0, x[i][0][k]);
//            }
//            VRPD.addLe(expr8, VRPD.sum(1.0, VRPD.prod(BigM, VRPD.diff(1.0, y[k]))));
//        }
//        //7-2:for each vehicle: links toward depot=1
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            IloLinearNumExpr expr8 = VRPD.linearNumExpr();
//            for (int i = 0; i < Number_Customers; i++) {
//                expr8.addTerm(1.0, x[i][0][k]);
//            }
//            VRPD.addGe(expr8, VRPD.diff(1.0, VRPD.prod(BigM, VRPD.diff(1.0, y[k]))));
//        }
//        //8: arrival time to each customer
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Customers; i++) {
//                for (int j = 0; j < Number_Customers; j++) {
//                    VRPD.addGe(VRPD.diff(z[j][k], z[i][k]), VRPD.diff((Cost_ShortestPath[i][j] + ServiceTime[i]), VRPD.prod(BigM, VRPD.diff(1, x[i][j][k]))));
//                }
//            }
//        }
//        //9: ArrivalTime =0 if k not used
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Customers; i++) {
//                VRPD.addLe(z[i][k], VRPD.prod(BigM, y[k]));
//            }
//        }
//        //10: Arrival Delay =0 if k not used
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Customers; i++) {
//                VRPD.addLe(delta[i][k], VRPD.prod(BigM, y[k]));
//            }
//        }
//        //11: Relation between alpha and (z-dd)
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Customers; i++) {
//                VRPD.addLe(VRPD.diff(VRPD.diff(z[i][k], DD[i]), VRPD.prod(BigM, VRPD.diff(2, VRPD.sum(alpha[i][k], y[k])))), 0);
//            }
//        }
//        //12: Relation between alpha and (z-dd)
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Customers; i++) {
//                VRPD.addLe(VRPD.sum(VRPD.diff(z[i][k], DD[i]), VRPD.prod(BigM, VRPD.sum(1, VRPD.diff(alpha[i][k], y[k])))), 0);
//            }
//        }
//        //13: delta=0
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Customers; i++) {
//                VRPD.addLe(delta[i][k], VRPD.sum(BigM, VRPD.diff(2, VRPD.sum(alpha[i][k], y[k]))));
//            }
//        }
//        //14-1: delta=z-dd
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Customers; i++) {
//                VRPD.addLe(delta[i][k], VRPD.sum(VRPD.diff(z[i][k], DD[i]), VRPD.prod(BigM, VRPD.sum(1, VRPD.diff(alpha[i][k], y[k])))));
//            }
//        }
//        //14-2: delta=z-dd
//        for (int k = 0; k < Max_Number_Vehicles; k++) {
//            for (int i = 0; i < Number_Customers; i++) {
//                VRPD.addGe(delta[i][k], VRPD.diff(VRPD.diff(z[i][k], DD[i]), VRPD.prod(BigM, VRPD.sum(1, VRPD.diff(alpha[i][k], y[k])))));
//            }
//        }
//    }
//
//    public static void Solve_Model() throws Exception {
//        Start_ProcessTime = System.currentTimeMillis();
//        if (VRPD.solve()) {
//            End_ProcessTime = System.currentTimeMillis();
//            System.out.println("Status = " + VRPD.getStatus());
//            System.out.println("Objective Value = " + VRPD.getObjValue());
//            for (int k = 0; k < Max_Number_Vehicles; k++) {
//                if (VRPD.getValue(y[k]) == 1) {
//                    if (VRPD.getValue(y[k]) == 1) {
//                        System.out.println("Vehicle " + k + " is used.");
//                    }
//                }
//            }
//        }
//    }
//
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