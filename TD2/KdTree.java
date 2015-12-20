import java.util.ArrayList;
import java.util.PriorityQueue;

class MutableInteger{
  int value;

  MutableInteger(){
    value = 0;
  }
}

public class KdTree{
  ArrayList<Point> points;
  ArrayList<Integer> indexes;
  int cur;
  double minValue[],maxValue[];
  KdTree lson = null,rson = null;

  static int ITERATIONS = 5;

  KdTree(ArrayList<Point> p, ArrayList<Integer> ind, int cur){
    this.points = p;
    this.indexes = ind;
    this.cur = cur;
    int n = points.size(),d = points.get(0).coords.length;

    minValue = new double[d];
    maxValue = new double[d];

    for(int i = 0;i < n;++i){
      for(int j = 0;j < d;++j){
        minValue[j] = points.get(i).coords[j];
        maxValue[j] = points.get(i).coords[j];
      }
    }

    BuildKdTree(cur);
  }

  void BuildKdTree(int cur){
      int n = points.size(),d = points.get(0).coords.length;

      if(n > 1){
        double m = 0;

        for(int i = 0;i < ITERATIONS;++i){
          int pos = (int)Math.floor(n * Math.random());
          m += points.get(pos).coords[cur];
        }

        m /= ITERATIONS;

        ArrayList<Point> p1 = new ArrayList<Point>(),p2 = new ArrayList<Point>();
        ArrayList<Integer> ind1 = new ArrayList<Integer>(),ind2 = new ArrayList<Integer>();

        for(int i = 0;i < n;++i){
          if(points.get(i).coords[cur] <= m){
            p1.add(points.get(i));
            ind1.add(indexes.get(i));
          }else{
            p2.add(points.get(i));
            ind2.add(indexes.get(i));
          }
        }

        if(!p1.isEmpty()) lson = new KdTree(p1,ind1,(cur + 1) % d);
        if(!p2.isEmpty()) rson = new KdTree(p2,ind2,(cur + 1) % d);
      }
  }

  Integer[] KNN(Point p, int index, int k){
    PriorityQueue<Node> Q = new PriorityQueue<Node>(k + 1);
    MutableInteger nQ = new MutableInteger();
    searchKNN(p,index,k,Q,nQ);

    Integer[] ret = new Integer[k];

    for(int i = 0;i < k;++i)
      ret[k - 1 - i] = Q.poll().index;

    return ret;
  }

  void searchKNN(Point p, int index, int k, PriorityQueue<Node> Q, MutableInteger nQ){
    if(nQ.value < k){ // Queue has les than k elements
      if(points.size() == 1 && index != indexes.get(0)){ // leaf node
        Q.add(new Node(points.get(0), indexes.get(0), Point.sqDist(p,points.get(0))));
        ++nQ.value;
      }else if(points.size() > 1){ // non-leaf node
        if(lson != null)
          lson.searchKNN(p,index,k,Q,nQ);
        if(rson != null)
          rson.searchKNN(p,index,k,Q,nQ);
      }
    }else if(points.size() == 1 && index != indexes.get(0)){ // leaf node
      double distance = Point.sqDist(p,points.get(0));

      if(distance < Q.peek().distance){
        Q.poll();
        Q.add(new Node(points.get(0), indexes.get(0), distance));
      }
    }else if(points.size() > 1){ // non-leaf node
      int d = p.coords.length;
      double[] cmin = new double[d];
      double[] cmax = new double[d];

      double r = Q.peek().distance;
      // check intersection
      boolean intersects = true;

      for(int i = 0;i < d;++i){
        if(Math.max(minValue[i],p.coords[i] - r) > Math.min(maxValue[i],p.coords[i] + r)){
          intersects = false;
        }
      }

      if(lson != null)
        lson.searchKNN(p,index,k,Q,nQ);
      if(rson != null)
        rson.searchKNN(p,index,k,Q,nQ);
    }
  }
}
