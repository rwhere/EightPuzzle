import java.util.Collections;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.lang.RuntimeException;

public class EightPuzzle
{
  public static void main(String[] args)
  {
    //generate randomPuzzle
    String puzzle = randomPuzzle();
    solvePuzzle(puzzle);
  }
  public static void solvePuzzle(String puzzle)
  {
    if(!solvable(puzzle))
      throw new RuntimeException("Puzzle not solvable");
    ArrayList<aStarNode> traceList = aStarSearch(puzzle, 0); //h1 first
    printSolution(traceList);
    traceList = aStarSearch(puzzle, 1); //then h2
    printSolution(traceList);
  }
  public static void printSolution(ArrayList<aStarNode> traceList)
  {
    for(int i = 0; i < traceList.size(); ++i)
    {
      System.out.println(traceList.get(i));
      if(i==traceList.size()-1)
        System.out.println("search cost: " + traceList.get(i).getf());
    }
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
      frontierHelper.remove(currentNode.getStringRep());
      if(goalTest(currentNode.getStringRep()))
        return traceBackList(currentNode);
      explored.put(currentNode.getStringRep(), currentNode.getStringRep());
      for(int i =0; i < currentNode.successors().size(); ++i)
      {
        aStarNode node = currentNode.successors().get(i);
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
    Collections.reverse(path);
    return path;
  }
  public static boolean solvable(String stringRep)
  {
    int inversions = 0;
    for(int i=0; i < stringRep.length(); ++i)
    {
      if(stringRep.charAt(i)=='0')
        continue;
      for(int j=i+1; j < stringRep.length(); ++j)
      {
        if(stringRep.charAt(j)==0)
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
}
