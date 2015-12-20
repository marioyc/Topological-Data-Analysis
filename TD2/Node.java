public class Node implements Comparable<Node>{
  Point p;
  int index;
  Double distance;

  Node(Point p, int index, double distance){
    this.p = p;
    this.index = index;
    this.distance = distance;
  }

  public int compareTo(Node x){
    return -distance.compareTo(x.distance);
  }
}
