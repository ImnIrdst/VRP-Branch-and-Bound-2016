package Main.AutomatedTests.AutoTests;

import Main.AutomatedTests.TestCases.IntegerTestCase.SCSTestCase;
import Main.AutomatedTests.TestCases.IntegerTestCase.SCSTestGenerator;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.Graph.Vertex;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class CplexAutoTest {
    private static final int INSTANCES_PER_TESTCASE = 10;

    static IloCplex SCS;                            // crew rostering problem
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
    static int vehicleCapacity;
    static Vertex depot;
    static Graph ppGraph;                    // preprocessed graph

    static String tableHeader;
    static String outputRow;
    static PrintWriter out;


    public static void main(String[] arg) throws Exception {
        ReadData();
//        Create_Model();
//        Solve_Model();
//        WriteData();
    }

    public static void ReadData() throws Exception {
        GlobalVars.log.println(GlobalVars.plusesLine);
        GlobalVars.log.println("BEGIN CplexAutoTest");
        GlobalVars.log.println(GlobalVars.plusesLine);

        FileOutputStream fileOutputStream = new FileOutputStream(new File("resources/AutoTestResults/cplex-tmp.csv"));
        out = new PrintWriter(fileOutputStream);

        tableHeader = "TestID," + SCSTestCase.getTableHeader() + ",Cost,CPUTime,Nodes,Status";
        out.println(tableHeader);
        out.flush();

        SCSTestGenerator testGenerator = new SCSTestGenerator();
        testGenerator.addSmallTestsV1();

        for (int testId = 0; testGenerator.hasNextTestCase();) {
            SCSTestCase testCase = testGenerator.getNextTestCase();
            for (int i = 0; i < INSTANCES_PER_TESTCASE; i++, testId++) {

                Graph originalGraph = Graph.buildRandomGraphFromIntegerTestCase(testCase, testId);
                if (testId > 0) break;

                Graph preprocessedGraph = originalGraph;
                preprocessedGraph.setIds();
                GlobalVars.setTheGlobalVariables(preprocessedGraph);
                GlobalVars.log.println(originalGraph.getVerticesFormattedString());

                t = preprocessedGraph.getAdjacencyMatrix();
                depotId = GlobalVars.depotId;
                depot = GlobalVars.depot;
                ppGraph = preprocessedGraph;
                vehicleFixCost = GlobalVars.depot.fixedCost;
                vehicleCapacity = GlobalVars.depot.capacity;
                nodesQty = GlobalVars.numberOfNodes;
                customersQty = GlobalVars.numberOfCustomers;
                vehiclesQty = GlobalVars.numberOfVehicles;

                GlobalVars.log.println("Number of Customers, Vehicles: " +
                        GlobalVars.numberOfCustomers + " " + GlobalVars.numberOfVehicles);

                outputRow = "" + testId + "," + testCase.getCSVRow() + ",";
                Create_Model();
                Solve_Model();
            }
        }
        out.close();
    }

    public static void Create_Model() throws Exception {
        SCS = new IloCplex();

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
                    x[i][j][k] = SCS.boolVar();
                }
            }
        }

        y = new IloNumVar[vehiclesQty];
        for (int k = 0; k < vehiclesQty; k++) {
            y[k] = SCS.boolVar();
        }

        A = new IloNumVar[nodesQty][nodesQty];
        for (int i = 0; i < nodesQty; i++) {
            for (int j = 0; j < nodesQty; j++) {
                A[i][j] = SCS.boolVar();
            }
        }

        D = new IloNumVar[nodesQty];
        for (int i = 0; i < nodesQty; i++) {
            D[i] = SCS.numVar(0, Double.MAX_VALUE);
        }

        T = new IloNumVar[nodesQty];
        for (int i = 0; i < nodesQty; i++) {
            T[i] = SCS.numVar(0, Double.MAX_VALUE);
        }

        S = new IloNumVar[vehiclesQty];
        for (int i = 0; i < vehiclesQty; i++) {
            S[i] = SCS.numVar(0, Double.MAX_VALUE);
        }
    }

    public static void createObjectiveFunctions() throws IloException {
        //-------------Objective Function-------------------
        IloLinearNumExpr obj = SCS.linearNumExpr();
        for (int i = 0; i < nodesQty; i++) {
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

        SCS.addMinimize(obj);

        obj.clear();
    }

    public static void addConstraint1() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            for (int i = 0; i < nodesQty; i++) {
                SCS.addEq(x[i][i][k], 0.0);
            }
        }
    }

    /**
     * 2: each node is visited exactly once (Except Depot)
     */
    public static void addConstraint2() throws IloException {
        for (int i = 0; i < nodesQty; i++) {
            if (i == depotId) continue; // (Except Depot)

            IloLinearNumExpr expr1 = SCS.linearNumExpr();
            for (int j = 0; j < nodesQty; j++) {
                for (int k = 0; k < vehiclesQty; k++) {
                    expr1.addTerm(1.0, x[j][i][k]);
                }
            }
            SCS.addEq(expr1, 1.0);
            expr1.clear();
        }
    }

    /**
     * 3: when vehicle k is not used (Y_k=0),
     * it does not drive between any pair of customers
     */
    public static void addConstraint3() throws IloException {
        //3: If not using vehicle k...
        for (int k = 0; k < vehiclesQty; k++) {
            IloLinearNumExpr expr2 = SCS.linearNumExpr();
            for (int i = 0; i < nodesQty; i++) {
                for (int j = 0; j < nodesQty; j++) {
                    expr2.addTerm(1.0, x[i][j][k]);
                }
            }
            SCS.add(SCS.ifThen(SCS.eq(y[k], 0), SCS.le(expr2, 0.0)));
            expr2.clear();
        }
    }

    /**
     * 4: Guarantee that no vehicle is
     * loaded with more than its capacity
     */
    public static void addConstraint4() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            IloLinearNumExpr expr2 = SCS.linearNumExpr();
            for (int i = 0; i < nodesQty; i++) {
                for (int j = 0; j < nodesQty; j++) {
//                    if (j == depotId) continue;
                    expr2.addTerm(1.0, x[i][j][k]);
                }
            }
            SCS.addLe(expr2, vehicleCapacity + 1);
            expr2.clear();
        }

//        GlobalVars.log.println("Vehicle Capacity: " + vehicleCapacity);
    }

    /**
     * 5: Ensures that if any vehicle is used,
     * it certainly leaves the origin, and vice versa. at customer
     */
    public static void addConstraint5() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            IloLinearNumExpr expr7 = SCS.linearNumExpr();
            for (int j = 0; j < nodesQty; j++) {
                expr7.addTerm(1.0, x[depotId][j][k]);
            }
            SCS.addEq(expr7, y[k]);
            expr7.clear();
        }
    }

    /**
     * 6: if a vehicle arrives to a point, either any
     * customer or the origin, the vehicle must also leave that point
     */
    public static void addConstraint6() throws IloException {
        for (int k = 0; k < vehiclesQty; k++) {
            for (int h = 0; h < nodesQty; h++) {
                IloLinearNumExpr expr5 = SCS.linearNumExpr();
                IloLinearNumExpr expr6 = SCS.linearNumExpr();
                if (h != depotId) {
                    for (int i = 0; i < nodesQty; i++) {
                        expr5.addTerm(1.0, x[i][h][k]);
                    }
                    for (int j = 0; j < nodesQty; j++) {
                        expr6.addTerm(1.0, x[h][j][k]);
                    }
                }
                SCS.addEq(expr5, expr6);
                expr5.clear();
                expr6.clear();
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
        for (int i = 0; i < customersQty; i++) {
            for (int j = 0; j < customersQty; j++) {
                if (i == j) continue;
                IloLinearNumExpr expr = SCS.linearNumExpr();
                expr.addTerm(1.0, A[i][j]);
                expr.addTerm(1.0, A[j][i]);
                SCS.addEq(expr, 1.0);
                expr.clear();
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
        for (int i = 0; i < customersQty; i++) {
            for (int j = 0; j < customersQty; j++) {
                for (int r = 0; r < customersQty; r++) {
                    if (i == j || j == r || r == i) continue;
                    IloLinearNumExpr expr = SCS.linearNumExpr();
                    expr.addTerm(1.0, A[i][j]);
                    expr.addTerm(1.0, A[j][r]);
                    expr.addTerm(1.0, A[r][i]);
                    SCS.addGe(expr, 1.0);
                    expr.clear();
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
                IloLinearNumExpr expr1 = SCS.linearNumExpr();
                for (int i = 0; i < nodesQty; i++) {
                    if (i == j) continue;
                    expr1.addTerm(1.0, x[i][j][k]);
                }
                IloLinearNumExpr expr2 = SCS.linearNumExpr();
                for (int i = 0; i < nodesQty; i++) {
                    if (i == j) continue;
                    expr2.addTerm(vertexi(i).processTime, A[i][j]);
                }
                SCS.add(SCS.ifThen(SCS.eq(expr1, 1),
                        SCS.ge(SCS.diff(S[k], vertexi(j).processTime), expr2)));
                expr1.clear();
                expr2.clear();
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
                SCS.add(SCS.ifThen(SCS.eq(x[i][j][k], 1),
                        SCS.ge(SCS.diff(D[j], S[k]), t[i][j])));
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
                IloLinearNumExpr expr = SCS.linearNumExpr();
                for (int k = 0; k < vehiclesQty; k++) {
                    expr.addTerm(x[i][j][k], 1.0);
                }
                SCS.add(SCS.ifThen(SCS.eq(expr, 1),
                        SCS.ge(SCS.diff(D[j], D[i]), (t[i][j]))));
                expr.clear();
            }
        }
    }

    public static void addConstraint12() throws IloException {
        for (int i = 0; i < customersQty; i++) {
            SCS.addGe(T[i], SCS.diff(D[i], vertexi(i).dueDate));
        }
    }

    public static void addConstraint16_18() throws IloException {
        for (int i = 0; i < nodesQty; i++) {
            SCS.addGe(D[i], 0);
            SCS.addGe(T[i], 0);
        }
        for (int k = 0; k < vehiclesQty; k++) {
            SCS.addGe(S[k], 0);
        }
    }

    public static void Solve_Model() throws Exception {
//        SCS.setParam(IloCplex.IntParam.Simplex.Display, 0);
        SCS.setParam(IloCplex.DoubleParam.EpInt, 1e-10);
        SCS.setOut(null);
        String expandedNodes = "?";
        String elapsedTime = "?";
        String optimalValue = "?";
        String status = "?";
        long startTime = System.currentTimeMillis();
        try {
            SCS.setParam(IloCplex.DoubleParam.TiLim, 400);
            if (SCS.solve()) {
                long finishTime = System.currentTimeMillis();
                elapsedTime = String.format("%.2f", (finishTime - startTime) / 1000.);
                optimalValue = String.format("%.2f", SCS.getObjValue());

                GlobalVars.log.println("Status = " + SCS.getStatus());
                GlobalVars.log.println("Objective Value = " + String.format("%.2f", SCS.getObjValue()));
                GlobalVars.log.println("yk, mdt, zk, dd, T, penalty");
                for (int k = 0; k < vehiclesQty; k++) {
                    long yk = Math.round(SCS.getValue(y[k]));
                    GlobalVars.log.printf("Y%d(%d, %.1f) ", k, yk, SCS.getValue(S[k]));
                    if (yk == 0) GlobalVars.log.println();
                    if (yk == 0) continue;
                    for (int i = nodesQty - 1; i >= 0; i--) {
                        for (int j = nodesQty - 1; j >= 0; j--) {
                        long xijk = Math.round(SCS.getValue(x[i][j][k]));
                            double zjk = (SCS.getValue(D[j]));
                            double djk = (SCS.getValue(T[j]));
//                            double xijk = (SCS.getValue(x[i][j][k]));

                            if (xijk == 0) continue;
                            Vertex u = ppGraph.getVertexById(i);
                            Vertex v = ppGraph.getVertexById(j);

                            GlobalVars.log.print(" "
                                    + u
                                    + " -("
                                    + String.format("%.2f", ppGraph.getDistance(u, v))
                                    + String.format(", %.2f", zjk) + String.format(", %.2f", djk * v.penalty)
                                    + ")-> "
                                    + v + ","
                            );
                        }
                    }
                    GlobalVars.log.println();
                }
                status = "" + SCS.getStatus();

                expandedNodes = "" + SCS.getNnodes64();
                GlobalVars.log.println("Number of Nodes: " + SCS.getNnodes64());
                GlobalVars.log.printf("Processing Time: %.2fs\n", (finishTime - startTime) / 1000.);
            } else {
                long finishTime = System.currentTimeMillis();
                elapsedTime = String.format("%.2f", (finishTime - startTime) / 1000.);
                optimalValue = "NA";
                GlobalVars.log.println("Can't be solved!!!!");
            }

        } catch (Exception | java.lang.OutOfMemoryError e) {
            optimalValue = "ML";
            long finishTime = System.currentTimeMillis();
            elapsedTime = String.format("%.2f", (finishTime - startTime) / 1000.);
        }
        SCS.clearUserCuts();
        SCS.clearCallbacks();
        SCS.clearLazyConstraints();
        SCS.clearCuts();
        SCS.clearModel();
        SCS.end();

        outputRow += optimalValue + "," + elapsedTime + "," + expandedNodes + "," + status;
        out.println(outputRow);
        out.flush();
        GlobalVars.log.flush();

//        GlobalVars.log.println(GlobalVars.dashesLine);
//        GlobalVars.log.println(tableHeader);
        GlobalVars.log.println(outputRow);
//        GlobalVars.log.println(GlobalVars.equalsLine);
//        GlobalVars.log.println();

        System.out.println("Cplex: " + outputRow);
    }

    public static Vertex vertexi(int i) {
        return ppGraph.getVertexById(i);
    }
}

