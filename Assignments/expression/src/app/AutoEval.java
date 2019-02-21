package app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class AutoEval {
    public static void main(String[] args) {
        try {
            testExpression("(64 * 74) - (64 - (72 / (31 / (34 / 83 / 23) + 63 / 96) - 77)) - (90 + 88 + (52 + 36)) + (23 * 47) / 25 + (36) + (15 * ((5 * 6) * (32))) - (38 + 21) - (60 + 18) + 26 * (92 / 88 / 25 * 53 + 70 - 68 + 40 / 43 - (73) + (39 / 9) * (90 + 75) + 59 - 92 + 85) - 92 + 56 + 16 / 14 / ((93 - 56) / (((43 + 76 - 91 + 13)) / 11)) / (23 - ((36 / 54) + 30 * 33 - 91 + (48 / 77) * (61 / 8)) - (25 / 68) - 30 + 38 / 81 + 85 * 59) * 2 / (33 * (74 - 26)) + 46 - 10 - 97 + (61 / 61 - 33 - ((86) / 62)) + 53");
            testExpression("a - ( b + A[ B[ 2 ] ] ) * d + 3", "etest1.txt");
            testExpression("arrayA[ arrayA[9] * ( arrayA[ 3 ] + 2 ) + 1 ] - varx", "etest2.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testExpression(String expr) throws IOException {
        testExpression(expr, "");
    }

    private static void testExpression (String expr, String fileName) throws IOException {
        ArrayList<Variable> vars = new ArrayList<>();
        ArrayList<Array> arrays = new ArrayList<>();

        if (expr.length() == 0)
            throw new IllegalArgumentException("Can't pass an empty expression!");

        System.out.println("Testing " + expr);
        Expression.makeVariableLists(expr, vars, arrays);

        if (fileName.length() != 0) {
            Scanner scfile = new Scanner(new File(fileName));
            Expression.loadVariableValues(scfile, vars, arrays);
        }

        System.out.println("Value of expression = " + Expression.evaluate(expr,vars,arrays));
        System.out.println();
    }
}
