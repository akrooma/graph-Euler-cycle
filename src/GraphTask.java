import java.util.*;

public class GraphTask {

   public static void main (String[] args) {
      GraphTask a = new GraphTask();
      a.run();
   }

   public void run() {
      Graph g = new Graph ("G");
      g.createRandomSimpleGraph (4, 4);
      System.out.println (g);
      g.printAdjMatrix();
      g.setEulerianCircuit(0);
      g.printInfos(4);
      // TODO!!! Your experiments here
   }


   class Vertex {

      private String id;
      private Vertex next;
      private Arc first;
      private int info = 0;

      Vertex (String s, Vertex v, Arc e) {
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

      public Arc getArc() {
    	  Arc arc = first;
    	  
    	  if (arc.info != 0) {
    		  arc = arc.next;
    	  }
    	  
//    	  while (arc.info != 0) {
//    		  arc = arc.next;
//    	  }
    	  
    	  return arc;
      }
   } // class Vertex.


   class Arc {

      private String id;
      private Vertex target;
      private Arc next;
      private int info = 0;

      Arc (String s, Vertex v, Arc a) {
         id = s;
         target = v;
         next = a;
      }

      Arc (String s) {
         this (s, null, null);
      }

      @Override
      public String toString() {
         return id;
      }

      public Arc next() {
    	  return next;
      }
   } // class Arc.


   class Graph {

      private String id;
      private Vertex first;
      private int info = 0;
      private int edges = 0;

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
            Arc a = v.first;
            while (a != null) {
               sb.append (" ");
               sb.append (a.toString());
               sb.append (" (");
               sb.append (v.toString());
               sb.append ("->");
               sb.append (a.target.toString());
               sb.append (")");
               a = a.next;
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

      public Arc createArc (String aid, Vertex from, Vertex to) {
         Arc res = new Arc (aid);
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
               createArc ("a" + varray [vnr].toString() + "_"
                  + varray [i].toString(), varray [vnr], varray [i]);
               createArc ("a" + varray [i].toString() + "_"
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
            Arc a = v.first;
            while (a != null) {
               int j = a.target.info;
               res [i][j]++;
               a = a.next;
            }
            v = v.next;
         }
         return res;
      }

      /**
       * Create a connected simple (undirected, no loops, no multiple
       * arcs) random graph with n vertices and m edges.
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
            createArc ("a" + vi.toString() + "_" + vj.toString(), vi, vj);
            connected [i][j] = 1;
            createArc ("a" + vj.toString() + "_" + vi.toString(), vj, vi);
            connected [j][i] = 1;
            edgeCount--;  // a new edge happily created
         }
         
         this.edges = m;
      }
      
      /**
       * Checks if the graph meets the requirements of an Eulerian curcuit.
       * @throws RuntimeException if the graph doesn't meet the requirements.
       */
      private void hasEulerianCircuit() {
    	  int x;
    	  int[][] adjMatrix = createAdjMatrix();
    	  
    	  for (int i = 0; i < adjMatrix.length; i++) {
    		  x = 0;
    		  
    		  for (int j = 0; j < adjMatrix[i].length; j++) {
    			  if (adjMatrix[i][j] == 1) {
    				  x++;
    			  }
    		  }
    		  
    		  if (x%2 != 0) {
    			  throw new RuntimeException("Eulerian circuit cannot be made for graph: " + this);
    		  }
    	  }
      }// hasEulerianCycle()
      
      /**
       * 
       * @param i -- info property of a vertex in the graph. It must be between [0; number of vertices in graph - 1].
       */
      public void setEulerianCircuit (int i) {
    	  this.hasEulerianCircuit();
    	  
    	  if (i < 0 || i > this.info) {
    		  throw new RuntimeException(i + " is an illegal vertex info property value for this graph.");
    	  }
    	  
    	  int[][] m = this.createAdjMatrix();
    	  int counter = 1;
    	  
    	  traverseMatrix(m, i, counter);
      }
      
      private void traverseMatrix (int[][] m, int i, int counter) {
    	  for (int j = 0; j < m[i].length; j++) {
    		  if (m[i][j] == 1) {
    			  m[i][j] = 2;
    			  m[j][i] = 0;
    			  
    			  String si = String.valueOf(i+1);
    			  String sj = String.valueOf(j+1);
    			  
    			  String name = "av" + si + "_v" + sj;
    			  
    			  Arc arc = this.getArcByName (name);
    			  
    			  arc.info = counter;
    			  
    			  if (counter <= this.edges) {
        			  traverseMatrix (m, j, ++counter);
    			  }
    			  
    			  return;
    		  }
    	  }
      }
      
      
      
      private Arc getArcByName (String name) {
    	  Vertex vertex = first;
    	  
    	  while (vertex != null) {
    		  Arc arc = vertex.first;
    		  while (arc != null) {
    			  if (arc.id.equals(name)) {
    				  return arc;
    			  }
    			  arc = arc.next;
    		  }
    		  vertex = vertex.next;
    	  }
    	  throw new RuntimeException ("Something went wrong. " + name + " found no match.");
      }
      
      /*
       * Assistant method.
       * i = 1 => prints out graph's all vertex.degree values.
       * i = 2 => prints out graph's all arc.info values.
       */
      public void printInfos (int i) {
    	  Vertex v = first;
    	  
    	  if (i == 1) { // vertex id.
    		  while (v != null) {
    			  System.out.println("Vertex " + v.id + " info property value: " + v.info);
    			  v = v.next;
    		  }
    	  }
    	  
    	  if (i == 2) { // Prints Arc.info
        	  while (v != null) {
        		  System.out.println("Vertex " + v + " -- ");
        		  Arc a = v.first;
        		  while (a != null) {
        			  System.out.println("Arc  " + a + "'s info --  " + a.info);
        			  a = a.next;
        		  }
            	  v = v.next;
        	  }
    	  }
    	  
    	  if (i == 3) { // arc id.
           	  while (v != null) {
        		  System.out.println("Vertex " + v + " -- ");
        		  Arc a = v.first;
        		  while (a != null) {
        			  System.out.println("Arc  " + a + "'s id --  " + a.id);
        			  a = a.next;
        		  }
            	  v = v.next;
        	  }
    	  }
    	  
    	  if (i == 4) {
           	  while (v != null) { // arc info
        		  Arc a = v.first;
        		  while (a != null) {
        			  System.out.println("Arc " + a + "'s info --  " + a.info);
        			  a = a.next;
        		  }
            	  v = v.next;
        	  }
    	  }
      }// printInfos() end.
      
      /**
       * Prints out the graph's adjacency matrix in a simple format.
       */
      public void printAdjMatrix() {
    	  int[][] m = this.createAdjMatrix();
    	  
    	  for (int i = 0; i < m.length; i++) {
    		  System.out.println();
    		  for (int j = 0; j < m.length; j++) {
    			  System.out.print(m[i][j] + ", ");
    		  }
    	  }
    	  
    	  System.out.println();
      }// printAdjMatrix();
      
   }// class Graph.
}// class GraphTask.