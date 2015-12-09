import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

class Neighbor implements Comparable<Neighbor>{
  Double value;
  int index;

  Neighbor(double value, int index){
    this.value = value;
    this.index = index;
  }

  public int compareTo(Neighbor p){
    return value.compareTo(p.value);
  }
}

public class HillClimbing{
  static ArrayList<Point> cloud;
  static ArrayList<Integer[]> neighbors;
  static ArrayList<Double> density;
  static ArrayList<Integer> parent;
  static ArrayList<Integer> label;

  static void readData(String filename) throws FileNotFoundException{
    cloud = new ArrayList<Point>();
    File file = new File(filename);
    Scanner sc = new Scanner(file);

    while(sc.hasNext()){
      cloud.add(new Point(sc));
    }
  }

  static void computeNeighbors(int k){
    int n = cloud.size();
    ArrayList<Neighbor> aux = new ArrayList<Neighbor>();
    neighbors = new ArrayList<Integer[]>();

    for(int i = 0;i < n;++i){
      aux.clear();

      for(int j = 0;j < n;++j){
        if(j != i){
          aux.add(new Neighbor(Point.sqDist(cloud.get(i),cloud.get(j)),j));
        }
      }

      Collections.sort(aux);
      Integer[] indexes = new Integer[k];

      for(int j = 0;j < k;++j){
        indexes[j] = aux.get(j).index;
      }

      neighbors.add(indexes);
    }
  }

  static void computeDensity(int k){
    int n = cloud.size();
    density = new ArrayList<Double>();

    for(int i = 0;i < n;++i){
      double sum = 0;

      for(int j = 0;j < k;++j){
        sum += Point.sqDist(cloud.get(i),cloud.get( neighbors.get(i)[j] ));
      }

      density.add(1 / Math.sqrt(1.0 / k * sum));
    }
  }

  static void computerForest(int k){
    int n = cloud.size();
    parent = new ArrayList<Integer>();

    for(int i = 0;i < n;++i){
      int par = i;

      for(int j = 0;j < k;++j){
        if(density.get( neighbors.get(i)[j] ) > density.get(par)){
          par = neighbors.get(i)[j];
        }
      }

      parent.add(par);
    }
  }

  static int getRoot(int v){
    int r = v;

    while(parent.get(r) != r){
      r = parent.get(r);
    }

    return r;
  }

  static void computeLabels(){
    int n = cloud.size();
    //Set<Integer> S = new TreeSet<Integer>();
    label = new ArrayList<Integer>();

    for(int i = 0;i < n;++i){
      /*int r = i;

      while(parent.get(r) != r){
        r = parent.get(r);
      }*/

      //S.add(r);
      label.add(getRoot(i));
    }

    //label = new ArrayList<Integer>(S);
  }

  static void computePersistence(int k, double tau){
    int n = cloud.size();
    ArrayList<Neighbor> aux = new ArrayList<Neighbor>();

    for(int i = 0;i < n;++i){
      aux.add(new Neighbor(density.get(i),i));
    }

    Collections.sort(aux);

    for(int i = 0;i < n;++i){
      /*for(int j = 0;j < neighbors.get(i).size();++j){
        int id = neighbors.get(i)[j];
        aux.add(new Neighbor(density.get(id),id));
      }

      Collections.sort(aux);*/
      int cur = aux.get(n - 1 - i).index;
      int ei = getRoot(parent.get(cur));

      for(int j = 0;j < k;++j){
        int id = neighbors.get(cur)[j];//aux.get(aux.size() - 1 - j).index;
        int e = getRoot(id);

        if(e != ei && Math.min(density.get(ei),density.get(e)) < density.get(cur) + tau){
          if(density.get(e) < density.get(ei)){
            parent.set(e,ei);
          }else{
            parent.set(ei,e);
          }
        }
      }
    }
  }

  static void Clustering(String filename, int kDensity, int kGraph, double tau) throws FileNotFoundException{
    readData(filename);
    computeNeighbors(kDensity);
    computeDensity(kDensity);
    computerForest(kGraph);
    computePersistence(kGraph,tau);
    computeLabels();

    new ClusteringWindow(cloud, label, neighbors, kGraph);
  }

  public static void main(String[] args) throws FileNotFoundException{
    //Clustering("test.xy",10,5,0.35);
    //Clustering("crater.xy",50,15,2);
    Clustering("spirals.xy",100,30,0.03);
  }
}
