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
		sudoku = new Sudoku(mediumGrid);

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

		public HashSet<Integer> getPossibleValues() {
			HashSet<Integer> possibleValues = new HashSet<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));

			for (int x: rowSets.get(row)) {
				possibleValues.remove(x);
			}
			for (int x: colSets.get(col)) {
				possibleValues.remove(x);
			}
			for (int x: squareSets.get((col/PART)+(row/PART)*PART)) {
				possibleValues.remove(x);
			}
			return possibleValues;
		}

		public int getNumChoices() {
			return this.getPossibleValues().size();
		}

		public int getSquareIndex() {
			return (this.col/PART)+(this.row/PART)*PART;
		}

	}

	private int[][] grid = new int[SIZE][SIZE];
	private int[][] solnGrid = new int[SIZE][SIZE];
	private List<Spot> spots = new ArrayList<Spot>();
	private List<HashSet<Integer>> rowSets, colSets, squareSets;
	private List<Spot> sortedSpots = new ArrayList<Spot>();
	private boolean ifSolved = false;
	private int solnCount = 0;
	private long startTime, endTime = 0;


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
			squareSets.add(new HashSet<Integer>());
			rowSets.add(new HashSet<Integer>());
			colSets.add(new HashSet<Integer>());
		}

		for (int i = 0; i < SIZE; i++) {
			System.arraycopy(ints[i], 0, grid[i], 0, SIZE);
			for (int j = 0; j < SIZE; j++) {
				Spot newSpot = new Spot(i, j);
				newSpot.set(grid[i][j]);
				if (grid[i][j] != 0) {
					squareSets.get(newSpot.getSquareIndex()).add(grid[i][j]);
					rowSets.get(i).add(grid[i][j]);
					colSets.get(j).add(grid[i][j]);
				} else {
					spots.add(newSpot);
				}
			}
		}
		sortedSpots = sortSpots(spots);
	}

	// sort a list of spots by the number of assignable numbers
	private List<Spot> sortSpots(List<Spot> spotsList) {
		List<HashSet<Spot>> sortedSpotsSet = new ArrayList<HashSet<Spot>>(SIZE);
		List<Spot> sortedSpots = new ArrayList<Spot>();
		int index;
		for (int i = 0; i < SIZE; i++) {
			sortedSpotsSet.add(new HashSet<Spot>());
		}
		for (int i = 0; i < spotsList.size(); i++) {
			index = spotsList.get(i).getNumChoices();
			sortedSpotsSet.get(index).add(spotsList.get(i));
		}
		for (int i = 0; i < SIZE; i++) {
			for (Spot s: sortedSpotsSet.get(i)){
				sortedSpots.add(s);
			}
		}
		return sortedSpots;
	}

	public Sudoku(String puzzle) {
		this(textToGrid(puzzle));
	}

	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		startTime = System.currentTimeMillis();
		solveHelper(0);
		endTime = System.currentTimeMillis();
		return solnCount; // YOUR CODE HERE
	}

	private void solveHelper(int index) {
		if (solnCount > MAX_SOLUTIONS) return ;
		if (index == sortedSpots.size()) {
			solnCount++;
			if (solnCount == 1) {
				ifSolved = true;
				for (int i = 0; i < SIZE; i++) {
					System.arraycopy(grid[i], 0, solnGrid[i], 0, SIZE);
				}

			}
		} else {
			Spot currSpot = sortedSpots.get(index);
			HashSet<Integer> choices = sortedSpots.get(index).getPossibleValues();
			for (int x: choices){
				// choose
				currSpot.set(x);
				rowSets.get(currSpot.row).add(x);
				colSets.get(currSpot.col).add(x);
				squareSets.get(currSpot.getSquareIndex()).add(x);
				// explore
				solveHelper(index+1);
				// unchoose
				currSpot.set(0);
				rowSets.get(currSpot.row).remove(x);
				colSets.get(currSpot.col).remove(x);
				squareSets.get(currSpot.getSquareIndex()).remove(x);

			}
		}
	}

	@Override
	public String toString() {
		return gridToString(grid);
	}

	private String gridToString(int[][] g) {
		String gridString = "";
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (j != SIZE - 1){
					gridString += Integer.toString(g[i][j]) + " ";
				} else {
					gridString += Integer.toString(g[i][j]) + System.lineSeparator();
				}
			}
		}
		return gridString;
	}


	public String getSolutionText() {
		if (!ifSolved) throw new RuntimeException("Sudoku has not been solved!");

		return gridToString(solnGrid); // YOUR CODE HERE
	}

	public long getElapsed() {
		return endTime - startTime; // YOUR CODE HERE
	}

}
