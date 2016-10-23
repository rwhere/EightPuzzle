/*
* Written by: Ryan Waer
* CS 420 Project 1
* Date: 10-21-2016
* 8-Puzzle: Game in which there are 9 tiles where 8 are numbered 1-9
*   and the empty tile can be swapped with any adjacent tile. The objective
*   is to rearrange the puzzle into the following order:
*    |1|2
*   3|4|5
*   6|7|8
* This program solves randomly generated 8-Puzzles and puzzles entered by the
*   user
*/
import java.util.Collections;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.lang.RuntimeException;
import java.util.Scanner;
import java.io.*;

public class EightPuzzle
{
  public static int searchCost;
  public static void main(String[] args)
  {
    printSolution(solvePuzzle(getUserInput(), 1));
  }
  /* alternate program used for analysis and statistics
  public static void main(String[] args) throws IOException
  {
    //first do h1
    Pair pair = processStats(0);
    System.out.println("h1");
    for(int i =2; i <= 20; i+=2)
      System.out.println("Depth: "+i+"\tAverage Time: "
        + (pair.time.get(i)/100)+ "ms" + "\tAverage Search Cost: " + (pair.cost.get(i)/100));
    //now do h2
    pair = processStats(1);
    System.out.println("h2");
    for(int i =2; i <= 20; i+=2)
      System.out.println("Depth: "+i+"\tAverage Time: "
        + (pair.time.get(i)/100)+ "ms" + "\tAverage Search Cost: " + (pair.cost.get(i)/100));
  }
  */
  public static String getUserInput()
  {
    int response;
    String puzzle;
    Scanner kb = new Scanner(System.in);
    boolean invalid = false;
    System.out.print("1.) solve randomly generated 8-Puzzle"
      + "\n2.) enter custom 8-Puzzle and solve\nSelection: ");
    response = kb.nextInt();
    while(response < 1 || response > 2)
    {
      System.out.print("Invalid option. Please try again.\nSelection: ");
      response = kb.nextInt();
    }
    if(response==1)
      puzzle = randomPuzzle();
    else
    {
      System.out.print("Please input custom puzzle (e.g. 142305678)\nPuzzle: ");
      puzzle = kb.next();
      if(puzzle.length() != 9)
        invalid = true;
      for(int i = 0; i < 9; ++i)
      {
        if(puzzle.indexOf((char)(i+'0'))==-1)
          invalid = true;
      }
      while(invalid)
      {
        System.out.print("invalid puzzle. Please try again.\nPuzzle: ");
        puzzle = kb.next();
        invalid = false;
        if(puzzle.length() != 9)
          invalid = true;
        for(int i = 0; i < 9; ++i)
          if(puzzle.indexOf((char)(i+'0'))==-1)
            invalid = true;
      }
    }
    return puzzle;
  }
  public static Pair processStats(int heuristic) throws IOException
  {
    //create a program that reads in 100 cases and solves them
    String line = null;
    FileReader fileReader = new FileReader("test_cases.txt");
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    HashMap<Integer, Long> timeKeeper = new HashMap<>();
    HashMap<Integer, Integer> costKeeper = new HashMap<>();

    int depth = 0, sigmaSearchCost = 0;
    long startTime = 0;
    while((line = bufferedReader.readLine()) != null)
    {
      //if its not a digit then we are starting fresh
      if(!Character.isDigit(line.charAt(0)))
      {
        timeKeeper.put(depth, System.currentTimeMillis()-startTime);
        costKeeper.put(depth, sigmaSearchCost);
        depth+=2;
        sigmaSearchCost=0;
        startTime = System.currentTimeMillis();
        line = bufferedReader.readLine();
      }
      ArrayList<aStarNode> s = solvePuzzle(line, heuristic);
      sigmaSearchCost += searchCost;
    }
    //the last entry depth 20
    timeKeeper.put(depth, System.currentTimeMillis()-startTime);
    costKeeper.put(depth, sigmaSearchCost);
    bufferedReader.close();
    return new Pair(timeKeeper, costKeeper);
  }
  public static ArrayList<aStarNode> solvePuzzle(String puzzle, int heuristic)
  {
    if(!solvable(puzzle))
      throw new RuntimeException("Puzzle not solvable:\n" + (new aStarNode(puzzle, 0, null, 0)));
    searchCost=0;
    ArrayList<aStarNode> a = heuristic == 0 ? aStarSearch(puzzle, 0) : aStarSearch(puzzle, 1);
    if(a==null)
      throw new RuntimeException("Puzzle not solvable:\n" + (new aStarNode(puzzle, 0, null, 0)));
    return a;
  }
  public static void printSolution(ArrayList<aStarNode> traceList)
  {
    for(int i = 0; i < traceList.size(); ++i)
      System.out.println(traceList.get(i));
    System.out.println("Depth: " + traceList.get(traceList.size()-1).getf()
    + "\tSearch Cost: " + searchCost);

  }
  public static ArrayList<aStarNode> aStarSearch(String initialState, int heuristic)
  {
    PriorityQueue<aStarNode> frontier = new PriorityQueue<>();
    HashMap<String, String> frontierHelper = new HashMap<>();
    HashMap<String, String> explored = new HashMap<>();
    frontier.add(new aStarNode(initialState, 0, null, heuristic));
    frontierHelper.put(frontier.peek().getStringRep(), frontier.peek().getStringRep());
    while(!frontier.isEmpty())
    {
      aStarNode currentNode = frontier.poll();
      ++searchCost;
      if(goalTest(currentNode.getStringRep()))
        return traceBackList(currentNode);
      frontierHelper.remove(currentNode.getStringRep());
      explored.put(currentNode.getStringRep(), currentNode.getStringRep());
      ArrayList<aStarNode> successors = currentNode.successors();
      for(int i =0; i < successors.size(); ++i)
      {
        aStarNode node = successors.get(i);
        if(explored.containsKey(node.getStringRep()))
          continue;
        if(!frontierHelper.containsKey(node.getStringRep()));
        {
          frontier.add(node);
          frontierHelper.put(node.getStringRep(), node.getStringRep());
        }
      }
    }
    System.out.println("gonna return null"); //SOMETHING TO LOOK FOR HEREEEEEEE!!!!!
    return null;
  }
  public static ArrayList<aStarNode> traceBackList(aStarNode node)
  {
    ArrayList<aStarNode> path = new ArrayList<>();
    while(node!=null)
    {
      path.add(node);
      node = node.getParent();
    }
    Collections.reverse(path); //reverse so we have start to goal
    return path;
  }
  public static boolean solvable(String stringRep)
  {
    int inversions = 0;
    for(int i=0; i < stringRep.length(); ++i)
    {
      if(stringRep.charAt(i)=='0') //don't consider empty tile
        continue;
      for(int j=i+1; j < stringRep.length(); ++j)
      {
        if(stringRep.charAt(j)==0) //don't consider empty tile
          continue;
        if(stringRep.charAt(i) > stringRep.charAt(j))
          ++inversions;
      }
    }
    return inversions%2==0;
  }
  public static boolean goalTest(String stringRep)
  {
    return stringRep.equals("012345678");
  }
  public static String randomPuzzle()
  {
    ArrayList<Integer> list = new ArrayList<Integer>();
    for(int i = 0; i < 9; ++i)
      list.add(i);
    Collections.shuffle(list);
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < 9; ++i)
      sb.append(list.get(i));
    return sb.toString();
  }
  public static class Pair
  {
    public HashMap<Integer, Long> time;
    public HashMap<Integer, Integer> cost;
    public Pair(HashMap<Integer, Long> t, HashMap<Integer, Integer> c)
    {
      time = t;
      cost = c;
    }
  }
}
