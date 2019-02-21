package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
    private static final Pattern
        VAR_PATTERN = Pattern.compile("[A-Za-z]+\\b(?!\\[)"), 
        ARR_PATTERN = Pattern.compile("[A-Za-z]+\\b(?=\\[)"),
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
        Matcher mVar = VAR_PATTERN.matcher(exp), mArr = ARR_PATTERN.matcher(exp);

        while(mVar.find()) { // Find variable names
            Variable temp = new Variable(mVar.group());
            
            if (!vars.contains(temp))
                vars.add(temp);
        }

        while(mArr.find()) { // Find array names
            Array temp = new Array(mArr.group());
            
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
        Stack<String>
            operands = new Stack<>(),
            operators = new Stack<>();

        // Tokenize expresion
        String[] tokens = TOKEN_SPLIT.split(expr.replace(" ", ""));
        System.out.println(Arrays.toString(tokens));

        // Perform Shunting-yard algorithm
        // Evaluate as we go
        for (String token : tokens) {
            if (token.matches("(\\d+\\.?\\d+)|\\w+")) { // If an operand
                boolean isVar = false;

                for (Variable v : vars) {
                    isVar = token.equals(v.name);
                    
                    if (isVar) {
                        operands.push("" + v.value);
                        break;
                    }
                }

                if (!isVar) // Either a constant or an array identifier, push it as-is for now.
                    operands.push(token);
            } else if (token.matches("[+\\-*/()\\[\\]]")) { // If an operator
                switch(token) {
                    case "(": // Push the closing bracket or parenthesis
                        operators.push(")");
                        break;
                    case "[":
                        operators.push("]");
                        break;
                    case "*": // High-precedence operators
                    case "/":
                        operators.push(token);
                        break;
                    case ")": // Bracket matching time
                    case "]":
                        StringBuilder subExp = new StringBuilder();
                        boolean balanced = false;

                        // Pop operators to find the matching bracket or parenthesis
                        while (!balanced && !operands.isEmpty()) {
                            String curOp = operators.pop();
                            balanced = token.equals(curOp);

                            if (balanced) {
                                float val = evaluate(subExp.insert(0, operands.pop()).toString(), vars, arrays);

                                if ("]".equals(token)) { // val is an array index, find the corresponding array
                                    boolean arrFound = false;
                                    String name = operands.pop();
                                    Iterator<Array> it = arrays.iterator();

                                    while (!arrFound && it.hasNext()) {
                                        Array a = it.next();
                                        arrFound = name.equals(a.name);

                                        if (arrFound)
                                            val = a.values[(int) val];
                                    }

                                    if (!arrFound)
                                        throw new NoSuchElementException("Variable missing from values file: " + name);
                                }

                                if (val < 0) { // Negatives get fussy, fix now
                                    String op = operators.pop();

                                    if ("+".equals(op))
                                        op = "-";
                                    else if ("-".equals(op))
                                        op = "+";

                                    operators.push(op);

                                    val = -val;
                                }

                                operands.push("" + val);

                            } else
                                subExp.insert(0, operands.pop()).insert(0, curOp);
                        }

                        if (!balanced)
                            throw new NoSuchElementException("Missing opening parenthesis");
                        break;
                    case "+": // Low-precedence operators
                    case "-":
                        while (!operators.isEmpty() && !")]".contains(operators.peek())) { // Until we do all the preceding operations or parens
                            float 
                                op2 = Float.parseFloat(operands.pop()), 
                                op1 = Float.parseFloat(operands.pop()),
                                val = evaluate(operators.pop(), op1, op2);

                            if (val < 0) { // Negatives get fussy, fix now
                                String op = operators.isEmpty() ? "" : operators.peek();

                                if ("+".equals(op)) {
                                    operators.pop();
                                    operators.push("-");
                                } else if ("-".equals(op)) {
                                    operators.pop();
                                    operators.push("+");
                                }

                                val = -val;
                            }

                            operands.push("" + val);
                        }

                        operators.push(token);
                }
            } else 
                throw new IllegalArgumentException("Token is not identifiable: " + token);
        }

        // Evaluate remaining operations
        while (!operators.isEmpty()) {
            float 
                op2 = Float.parseFloat(operands.pop()), 
                op1 = Float.parseFloat(operands.pop());
            
            operands.push("" + evaluate(operators.pop(), op1, op2));
        }

        // The answer should be on the top of the operand stack 
        return Float.parseFloat(operands.pop());
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
