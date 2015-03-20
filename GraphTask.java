
import java.util.*;

public class GraphTask {

   public static void main (String[] args) {
      GraphTask a = new GraphTask();
      a.run();
      throw new RuntimeException ("Nothing implemented yet!"); // delete this
   }

   public void run() {
      Graph g = new Graph ("G");
      g.createRandomSimpleGraph (6, 9);
      System.out.println (g);

      // TODO!!! YOUR TESTS HERE!
   }


   class Vertex {

      String id;
      Vertex next;
      Edge first;
      int info = 0;

      Vertex (String s, Vertex v, Edge e) {
         id = s;
         next = v;
         first = e;
      }

      Vertex (String s) {
         this (s, null, null);
      }

      @Override
      public String toString() {
         return id;
      }

      // TODO!!! Your Vertex methods here!

   } // Vertex


   class Edge {

      String id;
      Vertex target;
      Edge next;
      int info = 0;

      Edge (String s, Vertex v, Edge e) {
         id = s;
         target = v;
         next = e;
      }

      Edge (String s) {
         this (s, null, null);
      }

      @Override
      public String toString() {
         return id;
      }

      // TODO!!! Your Edge methods here!

   } // Edge


   class Graph {

      String id;
      Vertex first;
      int info = 0;

      Graph (String s, Vertex v) {
         id = s;
         first = v;
      }

      Graph (String s) {
         this (s, null);
      }

      @Override
      public String toString() {
         String nl = System.getProperty ("line.separator");
         StringBuffer sb = new StringBuffer (nl);
         sb.append (id);
         sb.append (nl);
         Vertex v = first;
         while (v != null) {
            sb.append (v.toString());
            sb.append (" -->");
            Edge e = v.first;
            while (e != null) {
               sb.append (" ");
               sb.append (e.toString());
               sb.append (" (");
               sb.append (v.toString());
               sb.append ("->");
               sb.append (e.target.toString());
               sb.append (")");
               e = e.next;
            }
            sb.append (nl);
            v = v.next;
         }
         return sb.toString();
      }

      public Vertex createVertex (String vid) {
         Vertex res = new Vertex (vid);
         res.next = first;
         first = res;
         return res;
      }

      public Edge createArc (String eid, Vertex from, Vertex to) {
         Edge res = new Edge (eid);
         res.next = from.first;
         from.first = res;
         res.target = to;
         return res;
      }

      /**
       * Create a connected undirected random tree with n vertices.
       * Each new vertex is connected to some random existing vertex.
       * @param n number of vertices added to this graph
       */
      public void createRandomTree (int n) {
         if (n <= 0)
            return;
         Vertex[] varray = new Vertex [n];
         for (int i = 0; i < n; i++) {
            varray [i] = createVertex ("v" + String.valueOf(n-i));
            if (i > 0) {
               int vnr = (int)(Math.random()*i);
               createArc ("e" + varray [vnr].toString() + "_"
                  + varray [i].toString(), varray [vnr], varray [i]);
               createArc ("e" + varray [i].toString() + "_"
                  + varray [vnr].toString(), varray [i], varray [vnr]);
            } else {}
         }
      }

      /**
       * Create an adjacency matrix of this graph.
       * Side effect: corrupts info fields in the graph
       * @return adjacency matrix
       */
      public int[][] createAdjMatrix() {
         info = 0;
         Vertex v = first;
         while (v != null) {
            v.info = info++;
            v = v.next;
         }
         int[][] res = new int [info][info];
         v = first;
         while (v != null) {
            int i = v.info;
            Edge e = v.first;
            while (e != null) {
               int j = e.target.info;
               res [i][j]++;
               e = e.next;
            }
            v = v.next;
         }
         return res;
      }

      /**
       * Create a connected simple (undirected, no loops, no multiple
       * edges) random graph with n vertices and m edges.
       * @param n number of vertices
       * @param m number of edges
       */
      public void createRandomSimpleGraph (int n, int m) {
         if (n <= 0)
            return;
         if (n > 2500)
            throw new IllegalArgumentException ("Too many vertices: " + n);
         if (m < n-1 || m > n*(n-1)/2)
            throw new IllegalArgumentException 
               ("Impossible number of edges: " + m);
         first = null;
         createRandomTree (n);       // n-1 edges created here
         Vertex[] vert = new Vertex [n];
         Vertex v = first;
         int c = 0;
         while (v != null) {
            vert[c++] = v;
            v = v.next;
         }
         int[][] connected = createAdjMatrix();
         int edgeCount = m - n + 1;  // remaining edges
         while (edgeCount > 0) {
            int i = (int)(Math.random()*n);  // random source
            int j = (int)(Math.random()*n);  // random target
            if (i==j) 
               continue;  // no loops
            if (connected [i][j] != 0 || connected [j][i] != 0) 
               continue;  // no multiple edges
            Vertex vi = vert [i];
            Vertex vj = vert [j];
            createArc ("e" + vi.toString() + "_" + vj.toString(), vi, vj);
            connected [i][j] = 1;
            createArc ("e" + vj.toString() + "_" + vi.toString(), vj, vi);
            connected [j][i] = 1;
            edgeCount--;  // a new edge happily created
         }
      }

      // TODO!!! Your Graph methods here!

   } // Graph

} // GraphTask

