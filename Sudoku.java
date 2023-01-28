/*A point is defined as the smallest possible square of a single value
A block is defined as a larger section of the puzzle which is one part out of the puzzle's length, and made up of equal points to the puzzle's length*/
import java.util.*;
import java.awt.Point;

public class Sudoku {
	private Integer[][] solution;  //contains current state of puzzle
	//private Sudoku save;  //last correct state of puzzle
	private Point[][][] blocks;  //contains values for all points in the same block of specific point
	private ArrayList<ArrayList<ArrayList<Integer>>> possible;  //contains all possible values for point
	public Sudoku(Integer[][] s) {  //constructor; inputs raw puzzle with null values for empty points
		this.solution=s;
		this.blocks = new Point[s.length][s.length][s.length-1];
		this.possible = new ArrayList<ArrayList<ArrayList<Integer>>>();
		Point[][] blocksList = new Point[s.length][s.length];  //contains all points in each block
		int block=0;  //used to iterate through the blocks(s.length blocks per puzzle)
		/*precondition: blocksList has been initialized to the same dimensions as the puzzle; represents s.length blocks containing s.length points each
		postconditon: blockList contains the point values for all blocks 0 to s.length-1, 0 representing the top left block and s.length-1 representing the bottom right block
		points ordered from 0 to s.length-1 in each block, where 0 represents the top left most point in a block and s.length-1 represents the bottom right most point*/
		for(int a=0; a<s.length; a+=(int)Math.sqrt((double)s.length)) {
			for(int b=0; b<s.length; b+=(int)Math.sqrt((double)s.length)) {
				int num=0;
				for(int y=a; y<(a+(int)Math.sqrt((double)s.length)); y++) {
					for(int x=b; x<(b+(int)Math.sqrt((double)s.length)); x++) {
						blocksList[block][num] = new Point(x,y);
						num++;
					}
				}
				block++;
			}
		}
		/*precondition: blocksList has been defined for all blocks, and all points within each block
		postcondition: blocks is defined so that for the input values [a][b][c], a and b will represent a point on the puzzle, and c will be 0 to s.length-2 out of the remaining points in the block
		points ordered in the same order as blocksList, with the point (b,a) subtracted*/
		for(int y=0; y<s.length; y++) {  //sets blocks values
			for(int x=0; x<s.length; x++) {
				Point p = new Point(x,y);
				int blockN=-1;
				int a=0;
				int b=0;
				int c=0;
				while((a<s.length)&&(blockN==-1)) {  //iterates through blocksList to find block that point (x,y) is located in; sets blockN equal to block
					while((b<s.length)&&(blockN==-1)) {
						if(blocksList[a][b].equals(p)) {
							blockN=a;
						}
						b++;
					}
					b=0;
					a++;
				}
				for(int n=0; n<s.length; n++) {  //adds all points, besides (x,y), in blockN to blocks[y][x]
					if(!blocksList[blockN][n].equals(p)) {
						blocks[y][x][c]=blocksList[blockN][n];
						c++;
					}
				}
			}
		}
		for(int y=0; y<s.length; y++) {  //initiaizes first dimension of ArrayLists
			this.possible.add(new ArrayList<ArrayList<Integer>>());
		}
		for(int y=0; y<s.length; y++) {  
			for(int x=0; x<s.length; x++) {  //initializes second dimension of ArrayLists
				this.possible.get(y).add(new ArrayList<Integer>());
			}
		}
		/*precondition: possible has been initialzed such that it represents a 2D ArrayList with dimensions equal to that of s, and each point contains an ArrayList of size()=0
		postcondition: all ArrayLists that represent null points are initialized to contain values from 1 to s.length*/
		for(int y=0; y<this.solution.length; y++) {
			for(int x=0; x<this.solution.length; x++) {
				if(this.solution[y][x]==null) {
					for(int n=1; n<=this.solution.length; n++) {
						this.possible.get(y).get(x).add(n);
					}
				}
			}
		}
	}
	/*precondition: Sudoku puzzle has been initialized, and therefore solution and blocks are filled, and possible is correctly sized and filled
	postcondition: possible values for each null point will be narrowed down as far as possible or until the point can be determined using numerous rules which dictate the behavior of sudoku puzzles
	terminating case: no change is made to possible values
	recursive relationship: a change is made to the possible values of a point, casuing the need for all possible values to reevaluated*/
	public void possible() {
		/*precondition: possible may contain possible values for points already possesing a set point
		postcondition: possible contains possible values only for null points*/
		for(int y=0; y<this.solution.length; y++) {  //any points with a value has all of their null points removed
			for(int x=0; x<this.solution.length; x++) {
				if(solution[y][x]!=null) {
					for(int n=this.possible.get(y).get(x).size(); n>0; n--) {
						this.possible.get(y).get(x).remove(n-1);
					}
				}
			}
		}
		/*precondition: possible contains values for null points
		postcondition: any possible value will be removed from the point (x,y) if that point is already in the same row, column, or box as the point (x,y)*/
		for(int y=0; y<this.solution.length; y++) {  //removes any values in the same row of a null point
			for(int x=0; x<this.solution.length; x++) {
				if(this.solution[y][x]==null) {
					for(int n=1; n<=this.solution.length; n++) {
						for(int c=0; c<this.solution.length;  c++) {
							if((this.solution[y][c]!=null)&&(this.solution[y][c]==n)) {
								if(this.possible.get(y).get(x).indexOf(n)!=-1) {
									this.possible.get(y).get(x).remove(this.possible.get(y).get(x).indexOf(n));
								}
								c=this.solution.length;
							}
						}
					}
				}
			}
		}
		for(int y=0; y<this.solution.length; y++) {  //removes any values in the same column of a null point
			for(int x=0; x<this.solution.length; x++) {
				if(this.solution[y][x]==null) {
					for(int n=1; n<=this.solution.length; n++) {
						for(int r=0; r<this.solution.length;  r++) {
							if((this.solution[r][x]!=null)&&(this.solution[r][x]==n)) {
								if(this.possible.get(y).get(x).indexOf(n)!=-1) {
									this.possible.get(y).get(x).remove(this.possible.get(y).get(x).indexOf(n));
								}
								r=this.solution.length;
							}
						}
					}
				}
			}
		}
		for(int y=0; y<this.solution.length; y++) {  //removes any values in the same box of a null point
			for(int x=0; x<this.solution.length; x++) {
				if(this.solution[y][x]==null) {
					for(int n=1; n<=this.solution.length; n++) {
						for(int i=0; i<this.solution.length-1;  i++) {
							if((this.solution[(int)blocks[y][x][i].getY()][(int)blocks[y][x][i].getX()]!=null)&&(this.solution[(int)blocks[y][x][i].getY()][(int)blocks[y][x][i].getX()]==n)) {
								if(this.possible.get(y).get(x).indexOf(n)!=-1) {
									this.possible.get(y).get(x).remove(this.possible.get(y).get(x).indexOf(n));
								}
								i=this.solution.length-1;
							}
						}
					}
				}
			}
		}
		for(int y=0; y<this.solution.length; y++) {  //any points that have a single possible value assume that value
			for(int x=0; x<this.solution.length; x++) {
				if(this.possible.get(y).get(x).size()==1) {
					this.solution[y][x]=this.possible.get(y).get(x).get(0);
					possible();
				}
			}
		}
		for(int y=0; y<this.solution.length; y++) {  //any point that is the only one in its row possesing a specific possible value is set to that value; two points in the same row sharing the same exclusive pair of possible values can be the only points in row with said values
			for(int x=0; x<this.solution.length; x++) {
				for(int n=0; n<this.possible.get(y).get(x).size(); n++) {
					boolean single=true;
					for(int c=0; c<this.solution.length; c++) {
						if((this.possible.get(y).get(x).size()==2)&&(this.possible.get(y).get(c).size()==2)) {
							if((this.possible.get(y).get(x).get(0)==this.possible.get(y).get(c).get(0))&&(this.possible.get(y).get(x).get(1)==this.possible.get(y).get(c).get(1))) {
								for(int e=0; e<this.solution.length; e++) {
									for(int d=0; d<this.possible.get(y).get(e).size(); d++) {
										if(this.possible.get(y).get(e).get(d)==this.possible.get(y).get(x).get(0)) {
											this.possible.get(y).get(e).remove(d);
											possible();
										}
										else if(this.possible.get(y).get(e).get(d)==this.possible.get(y).get(x).get(1)) {
											this.possible.get(y).get(e).remove(d);
											possible();
										}
									}
								}
							}
							single=false;
						}
						else {
							for(int i=0; i<this.possible.get(y).get(c).size(); i++) {
								if(this.possible.get(y).get(x).get(n)==this.possible.get(y).get(c).get(i)) {
									single=false;
								}
							}
						}
					}
					if(single) {
						this.solution[y][x]=this.possible.get(y).get(x).get(n);
						n=this.possible.get(y).get(x).size();
						possible();
					}
				}
			}
		}
		for(int y=0; y<this.solution.length; y++) {  //any point that is the only one in its column possesing a specific possible value is set to that value; two points in the same column sharing the same exclusive pair of possible values can be the only points in column with said values
			for(int x=0; x<this.solution.length; x++) {
				for(int n=0; n<this.possible.get(y).get(x).size(); n++) {
					boolean single=true;
					for(int r=0; r<this.solution.length; r++) {
						if((this.possible.get(y).get(x).size()==2)&&(this.possible.get(r).get(x).size()==2)) {
							if((this.possible.get(y).get(x).get(0)==this.possible.get(r).get(x).get(0))&&(this.possible.get(y).get(x).get(1)==this.possible.get(r).get(x).get(1))) {
								for(int e=0; e<this.solution.length; e++) {
									for(int d=0; d<this.possible.get(e).get(x).size(); d++) {
										if(this.possible.get(e).get(x).get(d)==this.possible.get(y).get(x).get(0)) {
											this.possible.get(e).get(x).remove(d);
											possible();
										}
										else if(this.possible.get(e).get(x).get(d)==this.possible.get(y).get(x).get(1)) {
											this.possible.get(e).get(x).remove(d);
											possible();
										}
									}
								}
							}
							single=false;
						}
						else {
							for(int i=0; i<this.possible.get(r).get(x).size(); i++) {
								if(this.possible.get(y).get(x).get(n)==this.possible.get(r).get(x).get(i)) {
									single=false;
								}
							}
						}
					}
					if(single) {
						this.solution[y][x]=this.possible.get(y).get(x).get(n);
						n=this.possible.get(y).get(x).size();
						possible();
					}
				}
			}
		}
		for(int y=0; y<this.solution.length; y++) {  //any point that is the only one in its block possesing a specific possible value is set to that value; two points in the same box sharing the same exclusive pair of possible values can be the only points in box with said values
			for(int x=0; x<this.solution.length; x++) {
				for(int n=0; n<this.possible.get(y).get(x).size(); n++) {
					boolean single=true;
					for(int b=0; b<this.blocks[y][x].length; b++) {
						if((this.possible.get(y).get(x).size()==2)&&(this.possible.get((int)this.blocks[y][x][b].getY()).get((int)this.blocks[y][x][b].getX()).size()==2)) {
							if((this.possible.get(y).get(x).get(0)==this.possible.get((int)this.blocks[y][x][b].getY()).get((int)this.blocks[y][x][b].getX()).get(0))&&(this.possible.get(y).get(x).get(1)==this.possible.get((int)this.blocks[y][x][b].getY()).get((int)this.blocks[y][x][b].getX()).get(1))) {
								for(int e=0; e<this.solution.length; e++) {
									for(int d=0; d<this.possible.get((int)this.blocks[y][x][e].getY()).get((int)this.blocks[y][x][e].getX()).size(); d++) {
										if(this.possible.get((int)this.blocks[y][x][e].getY()).get((int)this.blocks[y][x][e].getX()).get(d)==this.possible.get(y).get(x).get(0)) {
											this.possible.get((int)this.blocks[y][x][e].getY()).get((int)this.blocks[y][x][e].getX()).remove(d);
											possible();
										}
										else if(this.possible.get((int)this.blocks[y][x][e].getY()).get((int)this.blocks[y][x][e].getX()).get(d)==this.possible.get(y).get(x).get(1)) {
											this.possible.get((int)this.blocks[y][x][e].getY()).get((int)this.blocks[y][x][e].getX()).remove(d);
											possible();
										}
									}
								}
							}
							single=false;
						}
						else {
							for(int i=0; i<this.possible.get((int)this.blocks[y][x][b].getY()).get((int)this.blocks[y][x][b].getX()).size(); i++) {
								if(this.possible.get(y).get(x).get(n)==this.possible.get((int)this.blocks[y][x][b].getY()).get((int)this.blocks[y][x][b].getX()).get(i)) {
									single=false;
								}
							}
						}
					}
					if(single) {
						this.solution[y][x]=this.possible.get(y).get(x).get(n);
						n=this.possible.get(y).get(x).size();
						possible();
					}
				}
			}
		}
	}
	public boolean isCorrect() {
		for(int y=0; y<this.solution.length; y++) {
			for(int x=0; x<this.solution.length; x++) {
				if(this.solution[y][x]!=null) {
					for(int a=0; a<this.solution.length; a++) {
						if((x!=a)&&(this.solution[y][x]==this.solution[y][a])) {
							System.out.println("a");
							return false;
						}
					}
					for(int b=0; b<this.solution.length; b++) {
						if((y!=b)&&(this.solution[y][x]==this.solution[b][x])) {
							System.out.println("b");
							return false;
						}
					}
					for(int c=0; c<this.blocks[y][x].length; c++) {
						if(this.solution[y][x]==this.solution[(int)this.blocks[y][x][c].getY()][(int)this.blocks[y][x][c].getX()]) {
							System.out.println("c");
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	public boolean isSolved() {
		if(!isCorrect()) {
			return false;
		}
		for(int y=0; y<this.solution.length; y++) {
			for(int x=0; x<this.solution.length; x++) {
				if(this.solution[y][x]==null) {
					return false;
				}
			}
		}
		return true;
	}
	public Integer[][] getSolution() {
		return this.solution;
	}
}
