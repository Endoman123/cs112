package structures;

import java.util.*;
import java.util.function.Consumer;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	private static final String
		TOKEN_REGEX = "</?\\w+>|[^\\n<]+",
		OTAG_REGEX = "<\\w+>",
		CTAG_REGEX = "</\\w+>";

	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		Stack<TagNode> parents = new Stack<>();

		while (sc.hasNext()) {
			// Get token
			String token = sc.findWithinHorizon(TOKEN_REGEX, 0);

			// Parse token time
			if (token.matches(CTAG_REGEX)) { // Closing DOM tag
				String rootTag = null, curTag = token.replace("/", "");

				// Start pointing the root to popped tags
				while (!parents.isEmpty()) {
					root = parents.pop();

					if (curTag.equals(root.tag))
						break;
				}

				// Clear the triangle brackets on the tag
				// Push it back to the stack so that there is a pointer for siblings
				root.tag = root.tag.substring(1, root.tag.lastIndexOf(">"));
				parents.push(root);
			} else { // Opening DOM tag or text
				TagNode curTag = new TagNode(token, null, null);

				// Push opening tags into the parents stack as-is for now
				// We will remove the triangle brackets when finding closing tags
				if (!parents.isEmpty()) {
					if (parents.peek().tag.matches(OTAG_REGEX)) // If the top parent is a (still open) tag, it's a parent
						parents.peek().firstChild = curTag;
					else // If text, then it's a sibling
						parents.peek().sibling = curTag;
				}

				parents.push(curTag);
			}
		}
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		Stack<TagNode> workingStack = new Stack<>();
		TagNode curNode = root;

		// Full traversal of the tree
		while (!workingStack.isEmpty() || curNode != null) {
			// Iterate through tags
			while (curNode != null) {
				if (oldTag.matches(curNode.tag))
					curNode.tag = newTag;

				workingStack.push(curNode);
				curNode = curNode.firstChild;
			}

			// If we hit a dead end, go "right"
			// i.e.: go into the sibling
			// Also perform the the stuff
			if (!workingStack.isEmpty())
				curNode = workingStack.pop().sibling;
		}
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
        Stack<TagNode> workingStack = new Stack<>();
        TagNode curNode = root;

        // Full traversal of the tree
        while (!workingStack.isEmpty() || curNode != null) {
            // Iterate through tags
            while (curNode != null) {
                if ("table".matches(curNode.tag)) {
                    TagNode curRow = curNode.firstChild;

                    for (int i = 1; i < row; i++)
                        curRow = curRow.sibling;

                    // Go through all td
                    for (curRow = curRow.firstChild; curRow != null; curRow = curRow.sibling)
                        curRow.firstChild = new TagNode("b", curRow.firstChild, null);
                }

                workingStack.push(curNode);
                curNode = curNode.firstChild;
            }

            // If we hit a dead end, go "right"
            // i.e.: go into the sibling
            // Also perform the the stuff
            if (!workingStack.isEmpty())
                curNode = workingStack.pop().sibling;
        }
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		Stack<TagNode> workingStack = new Stack<>();
		TagNode curNode = root, back = null;

		// Full traversal of the tree
		while (!workingStack.isEmpty() || curNode != null) {
			// Iterate through tags
			while (curNode != null) {
			    if (tag.equals(curNode.tag)) { // If we find the tag we need
			        TagNode toRemove = curNode;

                    toRemove.firstChild.sibling = toRemove.sibling;

			        if (toRemove == back.sibling) { // Re-merge logic for siblings
                        back.sibling = toRemove.firstChild;
                        curNode = back.sibling;
                    } else { // Re-merge logic for children
			            back.firstChild = toRemove.firstChild;
                        curNode = back.firstChild;
                    }
			    }

				workingStack.push(curNode);
			    back = curNode;
				curNode = back.firstChild;
			}

			// If we hit a dead end, go "right"
			// i.e.: go into the sibling
			// Also perform the the stuff
			if (!workingStack.isEmpty()) {
                back = workingStack.pop();
                curNode = back.sibling;
            }
		}
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
