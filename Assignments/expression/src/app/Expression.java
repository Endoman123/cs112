package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
    private static final Pattern
        VAR_PATTERN = Pattern.compile("/[A-Za-z]+\\b(?!\\[.+\\])/g"), 
        ARR_PATTERN = Pattern.compile("/[A-Za-z]+\\b(?=\\[.+\\])/g");


    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        for (String s : VAR_PATTERN.split(expr)) { // Find variable names
            Variable temp = new Variable(s);
            
            if (!vars.contains(temp))
                vars.add(temp);
        }

        for (String s : ARR_PATTERN.split(expr)) { // Find array names
            Array temp = new Array(s);
            
            if (!arrays.contains(temp))
                arrays.add(temp);
        }
    }

    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * 
     * @return Result of evaluation
     */
    public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        float ret = 0;
        String[] tokens = expr.split("(?<=[\\-\\+\\*\\(\\)\\[\\]])|(?=[\\-\\+\\*\\(\\)\\[\\]])");
        Stack<Integer> operands = new Stack<>();
        Stack<String>
            operators = new Stack<>(),
            brackets = new Stack<>();

        // Tokenize expresion
        for (int i = tokens.length - 1; i >= 0; i--) {
            if (tokens[i].matches("\\d+")) { // If a number
                operands.push(Integer.parseInt(tokens[i]));
            } else if (tokens[i].matches("[A-Za-z]+")) { // If a variable
                if (i + 1 == tokens.length || tokens[i + 1].matches("\\d+"))
                for (Variable v : vars) {
                    if (tokens[i].equals(v.name)) {
                        operands.push(v.value);
                        break;
                    }
                }
            } else if (tokens[i].matches("[+\\-*/]")) { // If an operator
                operators.push(tokens[i]);
            }
        }

        // Expression parse
        ret = operands.pop();

        while(!operands.isEmpty()) {
            
        }

        return ret;
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc     Scanner for values input
     * @param vars   The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     * 
     * @throws IOException If there is a problem with the input 
     */
    public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
}
