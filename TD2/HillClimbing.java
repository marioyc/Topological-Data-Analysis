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
    ArrayList<Neighbor> aux = new ArrayList<Neighbor>(n);
    neighbors = new ArrayList<Integer[]>(n);

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
    density = new ArrayList<Double>(n);

    for(int i = 0;i < n;++i){
      double sum = 0;

      for(int j = 0;j < k;++j){
        sum += Point.sqDist(cloud.get(i),cloud.get( neighbors.get(i)[j] ));
      }

      density.add(1 / Math.sqrt(1.0 / k * sum));
    }
  }

  static int getRoot(int v){
    if(parent.get(v) == v) return v;
    int r = getRoot(parent.get(v));
    parent.set(v,r);
    return r;
  }

  static void computerForest(int k){
    int n = cloud.size();
    ArrayList<Neighbor> aux = new ArrayList<Neighbor>(n);
    parent = new ArrayList<Integer>(n);

    for(int i = 0;i < n;++i){
      aux.add(new Neighbor(density.get(i),i));
      parent.add(0);
    }

    Collections.sort(aux);

    for(int i = 0;i < n;++i){
      int cur = aux.get(n - 1 - i).index;
      int par = cur;

      for(int j = 0;j < k;++j){
        if(density.get( neighbors.get(cur)[j] ) > density.get(par)){
          par = neighbors.get(cur)[j];
        }
      }

      if(par == cur) parent.set(cur, cur);
      else parent.set(cur, getRoot(par));
    }
  }

  static void computeLabels(){
    int n = cloud.size();
    label = new ArrayList<Integer>(n);

    for(int i = 0;i < n;++i){
      label.add(getRoot(i));
    }
  }

  static void computePersistence(int k, double tau){
    int n = cloud.size();
    ArrayList<Neighbor> aux = new ArrayList<Neighbor>(n);

    for(int i = 0;i < n;++i){
      aux.add(new Neighbor(density.get(i),i));
    }

    Collections.sort(aux);

    for(int i = 0;i < n;++i){
      int cur = aux.get(n - 1 - i).index;
      int ei = getRoot(parent.get(cur));

      for(int j = 0;j < k;++j){
        int id = neighbors.get(cur)[j];
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

  static void ClusteringHillClimbing(String filename, int kDensity, int kGraph) throws FileNotFoundException{
    readData(filename);
    computeNeighbors(kDensity);
    computeDensity(kDensity);
    computerForest(kGraph);
    computeLabels();

    new ClusteringWindow(cloud, label, neighbors, kGraph);
  }

  static void ClusteringTomato(String filename, int kDensity, int kGraph, double tau) throws FileNotFoundException{
    readData(filename);
    computeNeighbors(kDensity);
    computeDensity(kDensity);
    computerForest(kGraph);
    computePersistence(kGraph,tau);
    computeLabels();

    new ClusteringWindow(cloud, label, neighbors, kGraph);
  }

  public static void main(String[] args) throws FileNotFoundException{
    ClusteringHillClimbing("test.xy",10,5);
    ClusteringHillClimbing("crater.xy",50,15);
    //ClusteringHillClimbing("spirals.xy",100,30);

    ClusteringTomato("test.xy",10,5,0.35);
    ClusteringTomato("crater.xy",50,15,2);
    //ClusteringTomato("spirals.xy",100,30,0.03);
  }
}
