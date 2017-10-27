package assign4;

import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	
	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}
	
	
	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}
	
	// Spot inner class
	private class Spot {
		private int row, col;

		public Spot(int x, int y) {
			this.row = x;
			this.col = y;
		}

		public void set(int value) {
			grid[row][col] = value;
		}

		public Set<Integer> getPossibleValues() {

			Set<Integer> possibleValues = new HashSet<Integer>();
			Boolean[] checkValues = new Boolean[SIZE];
			Arrays.fill(checkValues, false);

			for (int x: rowSets.get(row)) {
				checkValues[x-1] = true;
			}
			for (int x: colSets.get(col)) {
				checkValues[x-1] = true;
			}
			for (int x: squareSets.get((col/PART)+(row/PART)*PART)) {
				checkValues[x-1] = true;
			}
			for (int i = 0; i < SIZE; i++) {
				if (!checkValues[i]) {
					possibleValues.add(i+1);
				}
			}
			return possibleValues;
		}

		public int getNumChoices() {
			return this.getPossibleValues().size();
		}

	}

	private int[][] grid = new int[SIZE][SIZE];
	private List<Spot> spots;
	private List<HashSet<Integer>> rowSets, colSets, squareSets;

	private void sortSpots(List<Spot> spotsList) {
		List<HashSet<Spot>> sortedSpots = new ArrayList<HashSet<Spot>>(9);
		int index;
		for (int i = 0; i < spotsList.size(); i++) {
			index = spotsList.get(i).getNumChoices()
		}
	}


	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		// YOUR CODE HERE
		if (ints.length != SIZE || ints[0].length != SIZE) {
			throw new RuntimeException("grid size is wrong");
		}
		rowSets = new ArrayList<HashSet<Integer>>();
		colSets = new ArrayList<HashSet<Integer>>();
		squareSets = new ArrayList<HashSet<Integer>>();


		for (int i = 0; i < SIZE; i++) {
			System.arraycopy(ints[i], 0, grid[i], 0, SIZE);
			HashSet<Integer> rowS = new HashSet<Integer>();
			HashSet<Integer> colS = new HashSet<Integer>();
			squareSets.add(new HashSet<Integer>());
			for (int j = 0; j < SIZE; j++) {
				Spot newSpot = new Spot(i, j);
				newSpot.set(grid[i][j]);
				spots.add(newSpot);
				if (grid[i][j] != 0) {
					int squareIndex = (j/PART)+(i/PART)*PART;
					squareSets.get(squareIndex).add(grid[i][j]);
					rowS.add(grid[i][j]);
				}
				if (grid[j][i] != 0 && colS.contains(grid[j][i])) {
					colS.add(grid[j][i]);
				}
			}
			rowSets.add(rowS);
			colSets.add(colS);
		}
	}


	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		return 0; // YOUR CODE HERE
	}
	
	public String getSolutionText() {
		return ""; // YOUR CODE HERE
	}
	
	public long getElapsed() {
		return 0; // YOUR CODE HERE
	}

}
