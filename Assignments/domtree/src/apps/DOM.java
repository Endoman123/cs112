package apps;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

import structures.Tree;

public class DOM {

	static Scanner stdin = new Scanner(System.in);
	static String options = "hprbdaq";

	static char getOption() {
		System.out.print("\nChoose action: ");
		System.out.print("(p)rint Tree, ");
		System.out.print("(h)tml, ");
		System.out.print("(r)eplace tag, ");
		System.out.print("(b)oldface row, ");
		System.out.print("(d)elete tag, ");
		System.out.print("(a)dd tag, or ");
		System.out.print("(q)uit? => ");
		char response = stdin.nextLine().toLowerCase().charAt(0);
		while (!options.contains(response+"")) {
			System.out.print("\tYou must enter one of p, h, r, b, d, a, or q => ");
			response = stdin.nextLine().toLowerCase().charAt(0);
		}
		return response;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
			throws IOException {
		// TODO Auto-generated method stub
		System.out.print("Enter HTML file name => ");
		String htmlFile = stdin.nextLine();
		Tree tree = new Tree(new Scanner(new File(htmlFile)));
		tree.build();

		char option;
		while ((option = getOption()) != 'q') {
			System.out.println();
			if (option == 'h') {
				System.out.print(tree.getHTML());
			} else if (option == 'p') {
				tree.print();
			} else if (option == 'r') {
				System.out.print("\tEnter old tag => ");
				String oldTag = stdin.nextLine();
				System.out.print("\tEnter new tag => ");
				String newTag = stdin.nextLine();
				tree.replaceTag(oldTag, newTag);
			} else if (option == 'b') {
				System.out.print("\tEnter row number (1..n) => ");
				int row;
				while (true) {
					try {
						row = Integer.parseInt(stdin.nextLine());
						if (row > 0) {
							break;
						} else {
							throw new NumberFormatException();
						}
					} catch (NumberFormatException e) {
						System.out.print("\tYou must enter a positive integer => ");
					}
				}
				try {
					tree.boldRow(row);
				} catch (IllegalArgumentException iae) {
					System.out.println("\tTable does not have row " + row);
				}
			} else if (option == 'd') {
				System.out.print("\tEnter tag to remove => ");
				tree.removeTag(stdin.nextLine().trim());
			} else if (option == 'a') {
				System.out.print("\tEnter text to tag => ");
				String text = stdin.nextLine().trim();
				System.out.print("\tEnter tag => ");
				String tag = stdin.nextLine().trim();
				tree.addTag(text, tag);
			}
		}
	}
}