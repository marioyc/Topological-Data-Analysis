#include <iostream>
#include <set>
#include <fstream>

using namespace std;

typedef set<int> vertices;

struct simplex{
  int dim;
  float val;
  vertices vert;

  bool operator < (const simplex &j) const {
    if(val != j.val) return val < j.val;
    if(dim != j.dim) return dim < j.dim;
    return vert < j.vert;
  }
};

int main (int argc, char **argv) {
  if (argc != 2) {
    cout << "Syntax: " << argv[0] << " <OFF_file>" << endl;
    return 0;
  }

  ifstream ifs(argv[1]);
  char buf[256];

  ifs.getline(buf, 255);  // skip "OFF"
  int n, m;
  ifs >> n >> m;
  ifs.getline(buf, 255);  // skip "0"

  set<simplex> S;
  float vals[n];

  // read vertices
  double x,y,z;
  int count = 0;
  while(n-->0) {
    ifs >> x >> z >> y;  // BEWARE: z coordinate is second in file
    simplex v; v.dim = 0; v.vert.insert(count); v.val = z;
    vals[count++] = z;
    S.insert(v);
  }

  // read triangles
  int d, p, q, s;
  while (m-->0) {
    ifs >> d >> p >> q >> s;
    // add the three edges
    simplex e1; e1.dim = 1; e1.vert.insert(p); e1.vert.insert(q); e1.val = max(vals[p], vals[q]);
    S.insert(e1);
    simplex e2; e2.dim = 1; e2.vert.insert(p); e2.vert.insert(s); e2.val = max(vals[p], vals[s]);
    S.insert(e2);
    simplex e3; e3.dim = 1; e3.vert.insert(q); e3.vert.insert(s); e3.val = max(vals[q], vals[s]);
    S.insert(e3);
    // add the triangle
    simplex t; t.dim = 2; t.vert.insert(p); t.vert.insert(q); t.vert.insert(s); t.val = max(vals[p], max(vals[q], vals[s]));
    S.insert(t);
  }

  // output filtration
  for (set<simplex>::iterator sit = S.begin(); sit != S.end(); sit++) {
    cout << sit->val << " " << sit->dim << " ";
    for (vertices::iterator vit = sit->vert.begin(); vit != sit->vert.end(); vit++)
      cout << *vit << " ";
    cout << endl;
  }

  return 0;
}
