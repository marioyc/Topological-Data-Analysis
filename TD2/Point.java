import java.util.Scanner;

public class Point{
  double[] coords;

  Point(Scanner sc){
  	String line = sc.nextLine();
  	String[] tokens = line.replaceFirst("[ |\\t]+", "").split("[ |\\t]+");
  	int d = tokens.length;  // number of dimensions
  	coords = new double[d];

  	for (int i=0; i<d; i++){
  		coords[i] = Double.parseDouble(tokens[i]);
    }
  }

  static double sqDist(Point p, Point q){
    double ret = 0;
    int d = p.coords.length;

    for(int i = 0;i < d;++i){
      ret += (p.coords[i] - q.coords[i]) * (p.coords[i] - q.coords[i]);
    }

    return ret;
  }

  public String toString(){
    String ret = "(";
    int d = coords.length;

    for(int i = 0;i < d;++i){
      if(i > 0) ret += ",";
      ret += coords[i];
    }

    ret += ")";

    return ret;
  }
}
