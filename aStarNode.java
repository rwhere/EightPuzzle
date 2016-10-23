import java.util.Collections;
import java.util.ArrayList;
import java.lang.StringBuilder;
/*
* Written by: Ryan Waer
* CS 420 Project 1
* Date: 10-21-2016
* A* Node for 8-Puzzle
*/
public class aStarNode implements Comparable<aStarNode>
{
  private int[][] board;
  private int g, h, f;
  private aStarNode parent;
  private String stringRep;
  private int Heuristic;
  public aStarNode(String state, int pathCost, aStarNode theParent, int heuristic) //0 = H1, 1 = H2
  {
      initialize(state);
      stringRep = state;
      g = pathCost;
      parent = theParent;
      h = heuristic == 0 ? h1() : h2();
      Heuristic = heuristic;
      f = g + h;
  }
  public void initialize(String b)
  {
    board = new int[3][3];
    int counter = 0;
    for(int i = 0; i < 3; ++i)
      for(int j = 0; j < 3; ++j)
        board[i][j] = b.charAt(counter++) - '0';
  }
  //needed for PriorityQueue
  public int compareTo(aStarNode other)
  {
    if(f == other.f)
      return 0;
    else if(f < other.f)
      return -1;
    else
      return 1;
  }
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    int counter = 0;
    String string = getStringRep();
    for(int i = 0; i < string.length(); ++i)
    {
      sb.append(string.charAt(i));
      ++counter;
      if(counter%3==0)
        sb.append('\n');
    }
    return sb.toString();
  }
  public String getStringRep()
  {
    return stringRep;
  }
  public int getf()
  {
    return f;
  }
  public aStarNode getParent()
  {
    return parent;
  }
  private int h1()
  {
    if(stringRep=="012345678") //if at goal, heuristic = 0
      return 0;
    int counter = 0, num = 0;
    for(int r = 0; r < board.length; ++r)
      for(int c = 0; c < board[0].length; ++c)
      {
        if(num==board[r][c])
          ++counter;
        ++num;
      }
    return counter;
  }
  private int h2()
  {
    if(stringRep=="012345678") //if at goal, heuristic = 0
      return 0;
    int totMoves = 0;
    for(int r = 0; r < board.length; ++r)
      for(int c = 0; c < board[0].length; ++c)
      {
        totMoves += Math.abs(r - board[r][c] / 3);
        totMoves += Math.abs(c - board[r][c] % 3);
      }
      return totMoves;
  }
  private int findZeroLoc()
  {
    return getStringRep().indexOf('0');
  }

  public ArrayList<aStarNode> successors()
  {
    ArrayList<Integer> neighborLocs = new ArrayList<>();
    ArrayList<aStarNode> successors = new ArrayList<>();
    int zeroLoc = findZeroLoc();
    int row = zeroLoc / 3;
    int col = zeroLoc % 3;

    if(row+1 >= 0 && row+1 <= 2 ) //down
      neighborLocs.add(3*(row+1) + col);
    if(row-1 >= 0 && row-1 <= 2 ) //up
      neighborLocs.add(3*(row-1) + col);
    if(col+1 >= 0 && col+1 <= 2 ) //right
      neighborLocs.add(3*row + col+1);
    if(col-1 >= 0 && col-1 <= 2 ) //left
      neighborLocs.add(3*row + col-1);

      for(int i =0; i < neighborLocs.size(); ++i)
      {
        StringBuilder sb = new StringBuilder(getStringRep());
        char l = sb.charAt(zeroLoc), r = sb.charAt(neighborLocs.get(i));
        sb.setCharAt(zeroLoc, r);
        sb.setCharAt(neighborLocs.get(i), l);
        successors.add(new aStarNode(sb.toString(), g+1, this, Heuristic));
      }
    return successors;
  }
}
