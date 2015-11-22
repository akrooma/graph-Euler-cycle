import java.util.*;

public class GraphTask {

   public static void main (String[] args) {
      GraphTask a = new GraphTask();
      a.run();
   }

   public void run() {
      Graph g = new Graph ("G");
      g.createRandomSimpleGraph (6, 12);
      System.out.println (g);
      g.printAdjMatrix();
      g.setEulerianCircuit();
      System.out.println("Eulerian circuit numbering: ");
      g.printInfo(2);
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
   } // class Arc.


   class Graph {

      private String id;
      private Vertex first;
      private int info = 0;
      
      private int edges = 0; // shows how many edges the graph has.
      private int setEdges = 0; // indicates how many edges have been traversed in the Euler circuit.
      
      private String startVertexId = null;
      private String endVertexId = null;

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
       * Checks if this graph meets the requirements of an Eulerian circuit.
       * @throws RuntimeException if this graph doesn't meet the requirements.
       * @throws RunTimeException if the graph is "empty" -- no vertices, no edges.
       */
      private void hasEulerianCircuit() {
    	  if (this.first == null) {
    		  throw new RuntimeException("Graph is empty.");
    	  }
    	  
    	  int x;
    	  int[][] adjMatrix = createAdjMatrix();
    	  
    	  for (int i = 0; i < adjMatrix.length; i++) {
    		  x = 0;
    		  
    		  for (int j = 0; j < adjMatrix[i].length; j++) {
    			  if (adjMatrix [i][j] == 1) {
    				  x++;
    			  } else if (adjMatrix [i][j] == 2) { // checks for loops in graph, just in case.
    				  throw new RuntimeException ("Eulerian circuit cannot be made for graph: " + this);
    			  }
    		  }
    		  
    		  if (x % 2 != 0) {
    			  throw new RuntimeException ("Eulerian circuit cannot be made for graph: " + this);
    		  }
    	  }
      }// hasEulerianCircuit();
      
      /**
       * Sets an Eulerian circuit for this graph by numbering the arcs according to the circuit's order.
       */
      public void setEulerianCircuit() {
    	  this.hasEulerianCircuit();
    	  
    	  int[][] matrix = this.createAdjMatrix();
    	  int vertexInfo = this.first.info;
    	  Vertex v = this.first;
    	  this.startVertexId = v.id;
    	  int counter = 1;
    	  
    	  this.traverseMatrix (matrix, vertexInfo, counter);
    	  
    	  while (setEdges != this.edges) {
    		  v = v.next;
    		  this.startVertexId = v.id;
    		  this.resetECircuit();
    		  matrix = this.createAdjMatrix();
    		  this.traverseMatrix (matrix, ++vertexInfo, counter);
    	  }
    	  
    	  if (setEdges == this.edges && this.startVertexId.equals(this.endVertexId)) {
    		  return;
    	  } else {
    		  throw new RuntimeException("Unsuccesful.");
    	  }
      }
      
      /**
       * Goes through an adjacency matrix to make an Eulerian circuit for the graph.
       * @param m -- a matrix showing which routes in the graph are open.
       * @param i -- the info property of a vertex.
       * @param counter -- indicates which arc is being traversed right now.
       */
      private void traverseMatrix (int[][] m, int i, int counter) {
    	  for (int j = 0; j < m[i].length; j++) {
    		  if (m [i][j] == 1) {
    			  m [i][j] = 2; // sets the arc to an "used" state.
    			  m [j][i] = 0; // "deletes" the arc. Equivalent to setting it to an "used" state.
    			  
    			  String si = String.valueOf(i+1);
    			  String sj = String.valueOf(j+1);
    			  
    			  String arcId = "av" + si + "_v" + sj;
    			  
    			  Arc arc = this.getArcByName (arcId);
    			  
    			  arc.info = counter;
    			  
    			  if (counter <= this.edges) {
    				  this.setEdges++;
        			  traverseMatrix (m, j, ++counter);
    			  }

    			  if (counter == this.edges+1) {
    				  this.endVertexId = "v"+sj;
    			  }
    			  
    			  return;
    		  }
    	  }
      }
      
      /**
       * Resets the arc info and setEdges variables for a new Eulerian circuit round.
       */
      private void resetECircuit() {
    	  this.setEdges = 0;
    	  Vertex vertex = first;
    	  
    	  while (vertex != null) {
    		  Arc arc = vertex.first;
    		  while (arc != null) {
    			  if (arc.info != 0) {
    				  arc.info = 0;
    			  }
    			  arc = arc.next;
    		  }
    		  vertex = vertex.next;
    	  }
      }
      
      /**
       * Fetches an arc by the specified parameter string. 
       * @param name -- the string by which the arc is looked for.
       * @return The arc with the specified id.
       * @throws RuntimeException if an arc with the specified name wasn't found.
       */
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
      
      /**
       * Prints out the graph's adjacency matrix in a simple format.
       */
      public void printAdjMatrix() {
    	  int[][] m = this.createAdjMatrix();
    	  System.out.print("Adjacency matrix: ");
    	  for (int i = 0; i < m.length; i++) {
    		  System.out.println();
    		  for (int j = 0; j < m.length; j++) {
    			  System.out.print(m [i][j] + ", ");
    		  }
    	  }
    	  System.out.println();
      }// printAdjMatrix();
      
      /**
       * Prints out various properties for various graph objects.
       * @param i -- indicates what is to be printed. Value 1 -- vertex id. Value 2 -- arc info. Value 3 -- arc id.
       */
      public void printInfo (int i) {
    	  Vertex v = first;
    	  
    	  if (i == 1) { // vertex id.
    		  while (v != null) {
    			  System.out.println ("Vertex " + v.id + " info property value: " + v.info);
    			  v = v.next;
    		  }
    	  }
    	  
    	  if (i == 2) { // Prints Arc.info
        	  while (v != null) {
        		  System.out.println ("Vertex " + v + " -- ");
        		  Arc a = v.first;
        		  while (a != null) {
        			  System.out.println ("Arc  " + a + "'s info --  " + a.info);
        			  a = a.next;
        		  }
            	  v = v.next;
        	  }
    	  }
    	  
    	  if (i == 3) { // arc id.
           	  while (v != null) {
        		  System.out.println ("Vertex " + v + " -- ");
        		  Arc a = v.first;
        		  while (a != null) {
        			  System.out.println ("Arc  " + a + "'s id --  " + a.id);
        			  a = a.next;
        		  }
            	  v = v.next;
        	  }
    	  }
      }// printInfo();
   }// class Graph.
}// class GraphTask.