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
  static int ITERATIONS = 5;
  KdTree lson = null,rson = null;
  //Node[] Q;
  //int nQ;

  KdTree(ArrayList<Point> p, ArrayList<Integer> ind, int cur){
    this.points = p;
    this.indexes = ind;
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
    //System.out.println("KdTree n = " + n);
  }

  void BuildKdTree(int cur){
      int n = points.size(),d = points.get(0).coords.length;
      //System.out.println("BuildKdTree n = " + n);

      if(n == 1){
        points.add(points.get(0));
        this.cur = cur;
      }else{
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

        //System.out.println(p1.size() + " " + p2.size());

        if(!p1.isEmpty()) lson = new KdTree(p1,ind1,(cur + 1) % d);
        if(!p2.isEmpty()) rson = new KdTree(p2,ind2,(cur + 1) % d);
      }
  }

  Integer[] KNN(Point p, int k){
    System.out.println("KNN");
    Node[] Q = new Node[k];
    MutableInteger nQ = new MutableInteger();
    searchKNN(p,k,Q,nQ);
    Integer[] ret = new Integer[k];

    for(int i = 0;i < nQ.value;++i)
      ret[i] = Q[i].index;

    if(nQ.value != k)
      System.out.println(" :O");

    if(nQ.value == 0)
      System.out.println(" :X");

    return ret;
  }

  void searchKNN(Point p, int k, Node[] Q, MutableInteger nQ){
    if(nQ.value < k){
      if(points.size() == 1){
        System.out.println("Caso 1.1");
        Q[nQ.value].p = points.get(0);
        Q[nQ.value].index = indexes.get(0);
        Q[nQ.value].distance = Point.sqDist(p,points.get(0));
        ++nQ.value;
        System.out.print(nQ.value + " ");
        int pos = nQ.value - 1;

        while(pos > 0){
          if(Q[pos].compareTo(Q[pos - 1]) == -1){
            Node aux = Q[pos];
            Q[pos] = Q[pos - 1];
            Q[pos - 1] = aux;
          }else break;
          --pos;
        }
      }else{
        System.out.println("Caso 1.2");
        if(lson != null){
          System.out.println("lson " + points.size() + " -> " + lson.points.size());
          lson.searchKNN(p,k,Q,nQ);
        }
        if(rson != null){
          System.out.println("rson " + points.size() + " -> " + rson.points.size());
          rson.searchKNN(p,k,Q,nQ);
        }
      }
    }else if(points.size() == 1){
      System.out.println("Caso 2");
      double distance = Point.sqDist(p,points.get(0));
      if(distance < Q[k - 1].distance){
        Q[k - 1].p = points.get(0);
        Q[k - 1].index = indexes.get(0);
        Q[k - 1].distance = distance;
      }
      //Q[k - 1] = min(Q[k - 1],points.get(0));
      int pos = k - 1;

      while(pos > 0){
        if(Q[pos].compareTo(Q[pos - 1]) == -1){
          Node aux = Q[pos];
          Q[pos] = Q[pos - 1];
          Q[pos - 1] = aux;
        }else break;
        --pos;
      }
    }else{
      System.out.println("Caso 3");
      int d = p.coords.length;
      double[] cmin = new double[d];
      double[] cmax = new double[d];

      double r = Q[k - 1].distance;
      boolean intersects = true;

      for(int i = 0;i < d;++i){
        if(Math.max(minValue[i],p.coords[i] - r) > Math.min(maxValue[i],p.coords[i] + r)){
          intersects = false;
        }
      }

      if(lson != null)
        lson.searchKNN(p,k,Q,nQ);
      if(rson != null)
        rson.searchKNN(p,k,Q,nQ);
    }
  }
}
