#include <cstdlib>
#include <string>
#include <iostream>
#include <sstream>
#include <fstream>
#include <vector>
#include <algorithm>
#include <set>
//#include <map>
#include <limits>
#include <cassert>

using namespace std;

typedef set<int> vertices;

struct simplex{
  int dim;
  float val;
  vertices vert;

  bool operator < (simplex X) const{
    if(val != X.val) return val < X.val;
    if(dim != X.dim) return dim < X.dim;
    return vert < X.vert;
  }
};

vector<simplex> read_filtration(string name){
  vector<simplex> F;
  ifstream input(name.c_str());

  if(input){
    string line;

    while(getline(input,line)){
      simplex s; s.vert.clear();
      //int i = 0,start = 0,end;

      stringstream stream(line);

      s.dim = -1; stream >> s.val; stream >> s.dim;
      int i = 0;
      while(i <= s.dim){
        int f; stream >> f;
        s.vert.insert(f); i++;
      }

      /*do{
        stringstream stream(line);
        end = line.find_first_of(' ',start);
        string temp = line.substr(start, end);

        // first value is the function value
        if(isdigit(temp[0]) and i == 0)
          s.val = atof(temp.c_str());

        // second value is the dimension
      	if(isdigit(temp[0]) and i == 1)
      	  s.dim = atoi(temp.c_str());

        // the rest is the vertices
      	if(isdigit(temp[0]) and i > 1)
      	  s.vert.insert(atoi(temp.c_str()));

        i += 1;
        start = end + 1;
      }while(start);*/

      F.push_back(s);
    }
  }else{
    cout << "Failed to read file " << name << endl;
  }

  return F;
}

// is a face of b?
bool is_face(simplex &a, simplex &b){
  if(a.dim != b.dim - 1) return false;

  for(set<int>::iterator it = a.vert.begin();it != a.vert.end();++it)
    if(b.vert.find(*it) == b.vert.end())
      return false;

  return true;
}

// sum of columns mod 2, symmetric difference
vector<int> merge(vector<int> &a, vector<int> &b){
  vector<int> ret;
  int na = a.size(),nb = b.size(),pa = 0,pb = 0;

  while(pa < na && pb < nb){
    if(a[pa] < b[pb]) ret.push_back(a[pa++]);
    else if(a[pa] > b[pb]) ret.push_back(b[pb++]);
    else{
      ++pa;
      ++pb;
    }
  }

  assert(pa == na && pb == nb);
  return ret;
}

void gaussian_elimination(vector< vector<int> > &cols, vector<simplex> &F){
  int n = cols.size();
  int low[n],get_col[n],close[n];
  memset(get_col,-1,sizeof get_col);
  memset(close,-1,sizeof close);

  for(int i = 0;i < n;++i){
    if(!cols[i].empty()){
      low[i] = cols[i].back();
    }else{
      low[i] = -1;
    }

    // check if there is a column with the same low
    while(low[i] != -1 && get_col[ low[i] ] != -1){
      int j = get_col[ low[i] ];

      cols[i] = merge(cols[j],cols[i]);

      if(!cols[i].empty()){
        low[i] = cols[i].back();
      }else{
        low[i] = -1;
      }
    }

    if(low[i] != -1){
      get_col[ low[i] ] = i;
    }
  }

  // columns with non-zero values
  for(int i = 0;i < n;++i){
    if(low[i] != -1){
      close[ low[i] ] = i;
    }
  }

  // columns full of zeros
  for(int i = 0;i < n;++i){
    if(low[i] == -1){
      if(close[i] == -1){
        cout << F[i].dim << " " << F[i].val << " inf" << endl;
      }else if(F[i].val != F[ close[i] ].val){
        cout << F[i].dim << " " << F[i].val << " " << F[ close[i] ].val << endl;
      }
    }
  }
}

int main(int argc, char** argv) {
  if(argc != 2){
    cout << "Syntax: " << argv[0] << " <filtration_file>" << endl;
    return 0;
  }

  string name = argv[1];

  //cout << "Reading filtration..." << endl;

  vector<simplex> F = read_filtration(name);

  //cout << "Done." << endl;

  sort(F.begin(),F.end());

  /*for(vector<simplex>::iterator sit = F.begin(); sit != F.end(); sit++){
    cout << "{val=" << sit->val << "; dim=" << sit->dim << "; [";

    for(vertices::iterator vit = sit->vert.begin(); vit != sit->vert.end(); vit++){
      cout << *vit;
      vertices::iterator vvit = vit; vvit++;

      if(vvit != sit->vert.end())
	     cout << ", ";
    }

    cout << "]}" << endl;
  }*/

  int n = F.size();

  vector< vector<int> > cols;

  //cout << "n = " << n << endl;
  //cout << "Build matrix..." << endl;

  int max_dim = 0;

  for(int i = 0;i < n;++i){
    max_dim = max(max_dim,F[i].dim);
  }

  // keep list of the ids of the simplexes with the same dimension
  vector< vector<int> > id_by_dim(max_dim + 1);

  for(int i = 0;i < n;++i)
    id_by_dim[ F[i].dim ].push_back(i);

  // build the matrix
  for(int j = 0;j < n;++j){
    vector<int> aux;

    if(F[j].dim > 0){
      vector<int> &v = id_by_dim[ F[j].dim - 1 ];

      for(int i = 0;i < v.size();++i)
        if(is_face(F[ v[i] ],F[j])){
          assert(v[i] < j);
          aux.push_back(v[i]);
        }
    }

    cols.push_back(aux);
  }

  //cout << "Done." << endl;

  //cout << "Gaussian elimination..." << endl;
  gaussian_elimination(cols,F);
  //cout << "Done." << endl;

  return 0;
}
