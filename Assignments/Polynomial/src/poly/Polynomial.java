package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	private static final Term ZERO = new Term(0, 0);
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		Node cur1 = poly1, cur2 = poly2, ret = null, temp = new Node(0, 0, null);

		// Use a merge algorithm to try and add both the polynomials together.
		// This should go swimmingly
		while (cur1 != null || cur2 != null) {
			if (cur1 != null && cur2 == null || cur1 != null && cur2 != null && cur1.term.degree < cur2.term.degree) { // Either cur2 no longer has terms or it comes after cur1
				temp.term.coeff = cur1.term.coeff;
				temp.term.degree = cur1.term.degree;

				cur1 = cur1.next;
			} else if (cur1 == null && cur2 != null || cur1 != null && cur2 != null && cur1.term.degree > cur2.term.degree) { // Either cur1 no longer has terms or it comes after cur2
				temp.term.coeff = cur2.term.coeff;
				temp.term.degree = cur2.term.degree;

				cur2 = cur2.next;
			} else { // Merge case
				temp.term.coeff = cur1.term.coeff + cur2.term.coeff;
				temp.term.degree = cur1.term.degree;

				cur1 = cur1.next;
				cur2 = cur2.next;
			}

			// Because I am lazy as hell, reset the degree here to 0 if the current coeff is 0
			if (temp.term.coeff == 0)
				temp.term.degree = 0;
			
			// Progress the temp variable (if we should)
			if (cur1 != null && temp.term.coeff != 0 || cur2 != null && temp.term.coeff != 0) {
				temp.next = new Node(0, 0, null);
				
				// If ret is not set, now would be a good time to set it
				if (ret == null)
					ret = temp;
				
				temp = temp.next;
			}
		}
		
		return ret;
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		Node ret = new Node(0, 0, null), mult, temp = new Node(0, 0, null);

		// Multiplication by disributing terms.
		for (Node cur1 = poly1; cur1 != null; cur1 = cur1.next) { 
			// Use mult as a temp for containing the product of the current term and the second polynomial
			mult = null;

			for (Node cur2 = poly2; cur2 != null; cur2 = cur2.next) {
				// Get the coefficient of this term by multiplying them together
				temp.term.coeff = cur1.term.coeff * cur2.term.coeff;

				// Get the degres of this term by adding them together
				temp.term.degree = cur1.term.degree + cur2.term.degree;

				// Progress if possible
				if (cur2.next != null && temp.term.coeff != 0) {
					temp.next = new Node(0, 0, null);

					if (mult == null)
						mult = temp;

					temp = temp.next;
				}
			}

			System.out.println(toString(mult));

			// Add the current iteration of terms to the resulting polynomial
			ret = add(ret, mult);
		}
			
		return ret;
	}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		float ret = 0;

		// Iterate through polynomial, adding each term to ret
		for (Node cur = poly; cur != null; cur = cur.next)
			ret += cur.term.coeff * Math.pow(x, cur.term.degree);

		return ret;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
