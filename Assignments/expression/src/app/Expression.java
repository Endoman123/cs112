package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
    private static final Pattern
        VAR_SPLIT = Pattern.compile("[\\d()+\\-/*]|[A-Za-z]+\\b\\[.+\\]"), 
        ARR_PATTERN = Pattern.compile("[A-Za-z]+\\b(?=\\[.+\\])"),
        TOKEN_SPLIT = Pattern.compile("(?<=[\\-\\+\\*\\/\\(\\)\\[\\]])|(?=[\\-\\+\\*\\/\\(\\)\\[\\]])");

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
        String exp = expr.replace(" ", "");

        for (String s : VAR_SPLIT.split(exp)) { // Find variable names
            Variable temp = new Variable(s);
            
            if (!vars.contains(temp))
                vars.add(temp);
        }

        for (String s : ARR_PATTERN.split(exp)) { // Find array names
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
        boolean balanced = true;
        StringBuilder subExp = new StringBuilder();
        Stack<String>
            operands = new Stack<>(),
            operators = new Stack<>();

        // Tokenize expresion
        String[] tokens = TOKEN_SPLIT.split(expr.replace(" ", ""));
        System.out.println(Arrays.toString(tokens));

        // Perform Shunting-yard algorithm
        // Evaluate as we go
        for (String token : tokens) {
            if (token.matches("\\w+")) { // If an operand
                operands.push(token);
            } else if (token.matches("[A-Za-z]+")) { // If a variable
                boolean isSimple = false;
                
                for (Variable v : vars) {
                    isSimple = token.equals(v.name);
                    
                    if (isSimple) {
                        operands.push("" + v.value);
                        break;
                    }
                }

                if (!isSimple) // Most likely an array, push it as-is for now.
                    operands.push(token);
            } else if (token.matches("[+\\-*/()\\[\\]]")) { // If an operator
                switch(token) {
                    case "(": // High-precedence operators
                    case "[":
                    case "*": 
                    case "/":
                        operators.push(token);
                        break;
                    case ")": // Close parenthesis, start looking for the opening parenthesis
                        subExp.setLength(0);
                        balanced = false;

                        while (!operands.isEmpty() && !balanced) {
                            String curOp = operators.pop();
                            balanced = "(".equals(curOp);

                            if (balanced) {
                                operands.push("" + evaluate(subExp.insert(0, operands.pop()).toString(), vars, arrays));
                                break;
                            } else
                                subExp.insert(0, operands.pop()).insert(0, curOp);
                        }

                        if (!balanced)
                            throw new NoSuchElementException("Missing opening parenthesis");
                        break;
                    case "]": // Close bracket, start looking for the opening bracket
                        subExp.setLength(0);    
                        balanced = false;

                        while (!operands.isEmpty() && !balanced) {
                            String curOp = operators.pop();
                            balanced = "[".equals(curOp);

                            if (balanced) {
                                String name = operands.pop();
                                for (Array a : arrays) {
                                    if (name.equals(a.name)) {
                                        int ind = (int) evaluate(subExp.insert(0, operands.pop()).toString(), vars, arrays);

                                        System.out.println("I am pushing " + a.values[ind]);

                                        operands.push("" + a.values[ind]);
                                    }
                                }
                                break;
                            } else
                                subExp.insert(0, operands.pop()).insert(0, curOp);
                        }

                        if (!balanced) // Expression is unbalanced
                            throw new NoSuchElementException("Missing opening bracket");
                        break;
                    case "+": // Low-precedence operators
                    case "-":
                        if (!operators.isEmpty() && "*/".contains(operators.peek())) { // If there aren't any operators of precedence.
                            while (!operators.isEmpty() || !"+-".contains(operators.peek())) { // Unti there is at most an operator of equal precedence
                                float 
                                    op2 = Float.parseFloat(operands.pop()), 
                                    op1 = Float.parseFloat(operands.pop());
                                
                                operands.push("" + evaluate(operators.pop(), op1, op2));
                            }
                        }

                        operators.push(token);
                }
            }
        }

        // Final expression parse
        while (!operators.isEmpty()) {
            float 
                op2 = Float.parseFloat(operands.pop()), 
                op1 = Float.parseFloat(operands.pop());
            
            operands.push("" + evaluate(operators.pop(), op1, op2));
        }

        ret = Float.parseFloat(operands.pop());

        return ret;
    }

    /**
     * Helper function to evaluate simple expressions
     */
    private static float evaluate(String op, float op1, float op2) {
        float ret = 0;

        switch(op) {
            case "*":
                ret = op1 * op2;
                break;
            case "/":
                ret = op1 / op2;
                break;
            case "+":
                ret = op1 + op2;
                break;
            case "-":
                ret = op1 - op2;
                break;
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
