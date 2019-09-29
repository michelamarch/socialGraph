import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Collection;
import java.lang.Long;
import java.util.Collection;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Enumeration;

/**
 * The graph class contains an implementation of a graph
 * @author Michela Marchini
 */

public class Graph<T>{

    protected Hashtable<Long, Vertex<T>> graph;

    /**
     * The graph constructor initializes the Hashtable that will store all the
     * vertices in the graph
     */
    public Graph(){
	graph = new Hashtable<Long, Vertex<T>>();
    }

    /**
     * addVertex adds a vertex to the graph
     * @param id the unique long that will be the key
     * @param vertex to be added
     */
    public void addVertex(Long id, Vertex<T> toAdd){
	graph.put(id, toAdd);
    }

    /**
     * removeVertex removes a vertex using its id number
     * @param id of vertex to be removed
     */
    public void removeVertex(Long id){
	graph.remove(id);
    }

    /**
     * getVertices returns a collection of all the vertices in the graph
     * @return all vertices
     */
    public Collection<Vertex<T>> getVertices(){
	return graph.values();
    }

    /**
     * getVertex returns the vertex associated with a given id
     * @param id
     * @return vertex with given id
     */
    public Vertex<T> getVertex(Long id){
	return graph.get(id);
    }

    /**
     * getEdges returns a collection of all the edges in the graph
     * @return all edges
     */
    public ArrayList<Edge<T>> getEdges(){
	ArrayList<Edge<T>> edges = new ArrayList<Edge<T>>();
	for(Vertex<T> vertex: graph.values()){
	    edges.addAll(new ArrayList<Edge<T>>(vertex.getOutgoing()));
	}
	return edges;
    }

    /**
     * addEdge adds an edge between given vertices
     * @param source vertex of new edge
     * @param sink vertex of new edge
     */
    public void addEdge(Vertex source, Vertex sink){
	Edge<T> edge = new Edge<T>(source, sink);
	source.addOutgoing(edge);
	sink.addIncoming(edge);
    }

    public void addEdge(Edge<T> edge){
	edge.getSource().addOutgoing(edge);
	edge.getSink().addIncoming(edge);
    }

    /**
     * removeEdge removes a given edge from the graph
     * @param edge to be removed
     */
    public void removeEdge(Edge<T> edge){
	Vertex<T> source = edge.getSource();
	Vertex<T> sink = edge.getSink();
	sink.getIncoming().remove(edge);
	source.getOutgoing().remove(edge);
    }

    /**
     * makeUndirected returns an undirected version of the current graph
     * @return undirected graph of the current graph
     */
    public Graph<T> makeUndirected(){
	Graph<T> undirected = new Graph<T>();
	Collection<Vertex<T>> vertices = graph.values();
	//for each vertex in the graph if an equivalent vertex doesn't already exist in the undirected graph
	//make the equivalent vertex and set it to newVertex, else, just make newVertex the equivalent
	for(Vertex<T> vertex : vertices){
	    Vertex<T> newVertex;
	    if(undirected.graph.get(vertex.getId()) == null){
		    newVertex = new Vertex(vertex.getData(), vertex.getId());
		    undirected.addVertex(vertex.getId(), newVertex);
	    }
	    else{
		newVertex = undirected.graph.get(vertex.getId());
	    }
	    //for each outgoing edge in the original vertex, make the sink vertex if it doesn't already exist
	    //in undirected
	    ArrayList<Edge<T>> outgoing = vertex.getOutgoing();
	    for(int i = 0; i < outgoing.size(); i++){
		Edge<T> outEdge = outgoing.get(i);
		Vertex<T> sink = null;
		if(undirected.graph.get(outEdge.getSink().getId()) == null){
		    sink = new Vertex(outEdge.getSink().getData(),
				      outEdge.getSink().getId());
		    undirected.addVertex(outEdge.getSink().getId(), sink);
		}
		else{
		    sink = undirected.graph.get(outEdge.getSink().getId());
		}
		//look through all incoming vertices to see if there is an equivalent incoming edge to the outgoing
		//edge you are looking at (ex if we are looking at a->b we want to know if b->a already exists)
		boolean edgeBothWays = false;
		ArrayList<Edge<T>> incoming = vertex.getIncoming();
		for(int j = 0; j < incoming.size(); j++){
		    Edge<T> inEdge = incoming.get(j);
		    if(inEdge.getSource().getId() == outEdge.getSink().getId()){
			//you have found a case of edges both ways, make the new edges in the undirected graph with
			//the weights equal to the sum of the originals
			Edge<T> toAddOut = new Edge(newVertex, sink,
						    inEdge.getWeight() + outEdge.getWeight());
			Edge<T> toAddIn = new Edge(sink, newVertex,
						    inEdge.getWeight() + outEdge.getWeight());
			newVertex.addOutgoing(toAddOut);
			sink.addIncoming(toAddOut);
			newVertex.addIncoming(toAddIn);
			sink.addOutgoing(toAddIn);
			edgeBothWays = true;
		    }
		}
		if(!edgeBothWays){
		    //you never found an complementary edge so make the new edges in the undirected graph with just
		    //the weight of the original outgoing edge
		    Edge<T> toAddOut = new Edge(newVertex, sink, outEdge.getWeight());
		    Edge<T> toAddIn = new Edge(sink, newVertex, outEdge.getWeight());
		    newVertex.addOutgoing(toAddOut);
		    sink.addIncoming(toAddOut);
		    newVertex.addIncoming(toAddIn);
		    sink.addOutgoing(toAddIn);
		}
	    }
	}
	return undirected;
    }

    /**
     * getShortestPath finds the cheapest path between two given vertices
     * @param start and end vertices
     * @return list of edges on the shortest path between start and end vertices
     */
    public ArrayList<Edge<T>> getShortestPath(Vertex<T> start, Vertex<T> end){
	PriorityQueue<Vertex<T>> unvisited = new PriorityQueue<Vertex<T>>();
	//add all nodes to the unvisited queue and set the cost for everything but the start to a crazy high
	//number so that the start will be at the top of the priority queue
	for(Vertex<T> vertex : graph.values()){
	    if(vertex.getId() == start.getId()){
		vertex.setCost(0);
	    }
	    else{
		vertex.setCost(300000);
	    }
	    unvisited.add(vertex);
	}
	while(unvisited.size() > 0){
	    //for all the as yet unvisted neighbors of the lowest cost vertex (the top of the queue), check if it is
	    //cheaper to get there through the lowest cost vertex than by whatever route they have so far, if it is
	    //change the cost to the cost through the lowsest cost vertex and set that vertex to the predecessor
	    Vertex<T> lowestCost = unvisited.poll();
	    for(Edge<T> edge : lowestCost.getOutgoing()){
		if(unvisited.contains(edge.getSink())){
		    if(edge.getSink().getCost() > lowestCost.getCost() + edge.getWeight()){
			edge.getSink().setCost(lowestCost.getCost() + edge.getWeight());
			edge.getSink().setPredecessor(edge);
			//cost has been changed so remove and re-add so the priority will be correct
			unvisited.remove(edge.getSink());
			unvisited.add(edge.getSink());
		    }
		}
	    }
	}
	ArrayList<Edge<T>> path = new ArrayList<Edge<T>>();
	Vertex<T> current = graph.get(end.getId());
	//starting from the end get each vertex's predecessor until you get to the start to get the list of
	//all the edges passed through on the shortest path
	while(!current.equals(graph.get(start.getId()))){
	    path.add(current.getPredecessor());
	    current = current.getPredecessor().getSource();
	}
	return path;
    }
    
    /**
     * shortestPaths returns a list of all the shortest paths between two vertices
     * @param start and end vertices
     * @return a list of lists of all the edges in all the shortest paths between the vertices
     */
    protected ArrayList<ArrayList<Edge<T>> > shortestPaths(Vertex<T> start, Vertex<T> end){
	ArrayList<ArrayList<Edge<T>> > shortest = new ArrayList<ArrayList<Edge<T>> >();
	Hashtable<ArrayList<Edge<T>> , Integer> paths = getAllPaths(start, end);
	Enumeration<ArrayList<Edge<T>> > keys = paths.keys();
	//make current min really big so that all costs will be smaller
	int currentMin = 30000;
	//for each path between the vertices check the cost, if it is smaller than the current minimum cost path
	//clear the previous shortest paths and make this new path the new shortest, if it is equal add this path
	//to the list of shortest paths
	while(keys.hasMoreElements()){
	    ArrayList<Edge<T>> currentPath = keys.nextElement();
	    if(paths.get(currentPath) != null){
		if(paths.get(currentPath) < currentMin){
		    shortest.clear();
		    shortest.add(currentPath);
		    currentMin = paths.get(currentPath);
		}
		else if(paths.get(currentPath) == currentMin){
		    shortest.add(currentPath);
		}
	    }
	}
	return shortest;
    }

    /**
     * getAllPaths gets every path between two given vertices and its cost
     * @param start and end verices
     * @return hashtable mapping each path to its cost
     */
    protected Hashtable<ArrayList<Edge<T>> , Integer> getAllPaths(Vertex<T> start, Vertex<T> end){
	Hashtable<ArrayList<Edge<T>> , Integer> paths = new Hashtable<ArrayList<Edge<T>> , Integer>();
	LinkedList<Vertex<T>> unvisited = new LinkedList<Vertex<T>>();
	for(Vertex<T> vertex : graph.values()){
	    unvisited.add(vertex);
	}
	ArrayList<Edge<T>> currentPath = new ArrayList<Edge<T>>();
	paths = getAllPathsHelper(start, end, unvisited, currentPath, paths, 0);
	return paths;
    }

    /**
     * getAllPathsHelper recursively finds all the paths between two given vertices and their costs
     * @param start and end vertices
     * @param unvisited - the list of all vertices as yet unvisted in the current path
     * @param currentPath - the current path between the two vertices
     * @param paths - hash table mapping all paths so far between the two vertices to their costs
     * @param count - the current cost of the current path
     * @return the hashtable of all the paths so far
     */
    protected Hashtable<ArrayList<Edge<T>> , Integer> getAllPathsHelper(Vertex<T> current, Vertex<T> end,
									LinkedList<Vertex<T>> unvisited,
									ArrayList<Edge<T>> currentPath,
									Hashtable<ArrayList<Edge<T>> , Integer> paths,
									int count){
	unvisited.remove(current); 
        //base case
        if (current.equals(end)){
            paths.put(new ArrayList(currentPath), new Integer(count));
            //once you reach the end of the path, this vertex can be used in other paths 
            unvisited.add(current); 
            return paths; 
        } 
        //for each neighbor if it hasn't already been visited on this path, start traversing down it
        for (Edge<T> edge : current.getOutgoing()){ 
            if (unvisited.contains(edge.getSink())){
                //add the edge to the list and up the current path's count
                currentPath.add(edge);
		count += edge.getWeight();
                getAllPathsHelper(edge.getSink(), end, unvisited, currentPath, paths, count); 
                  
                //you have finished this version of the path so remove the egde and subtract it's weight 
                currentPath.remove(edge);
		count -= edge.getWeight();
            } 
        } 
          
        //you have finished this node, so it can be used again in other paths 
	unvisited.add(current);
	return paths;
    }

    /**
     * isSinglyConnected compares the shortest and longest paths between each vertex in the graph to determine whether
     * or not the graph is singly connected (there is at most one simple path from u to v for all vertices u and v in 
     * the set of Vertices)
     * @return true if the shortest and longest path between every vertex are the same and the graph is singly connected
     * and false otherwise
     */
    public boolean isSinglyConnected(){
	for(Vertex<T> vertex1 : graph.values()){
	    for(Vertex<T> vertex2 : graph.values()){
		if(getAllPaths(vertex1, vertex2).values().size() > 1){
		    return false;
		}
	    }
	}
	//you made it through all the vertices without finding more than one path between any two
	return true;
    }

    /**
     * betweennessCentrality finds the fraction of shortest paths between any two verices that pass through a given 
     * vertex
     * @param vertex to find betweenness centrality of
     * @return the betweenness centrality of the given vertex
     */
    public float betweennessCentrality(Vertex<T> vertex){
	float totalPaths = 0;
	float containsVertex = 0;
	for(Vertex<T> vertex1 : graph.values()){
	    for(Vertex<T> vertex2 : graph.values()){
		if(!vertex1.equals(vertex2)){
		    ArrayList<ArrayList<Edge<T>> > paths = shortestPaths(vertex1, vertex2);
		    for(ArrayList<Edge<T>> path : paths){
			totalPaths++;
			for(Edge<T> edge : path){
			    if(!edge.getSource().equals(vertex1) && !edge.getSource().equals(vertex2) &&
			       edge.getSource().equals(vertex)){
				containsVertex++;
			    }
			}
		    }
		}
	    }
	}
	return containsVertex/totalPaths;
    }
    
    /**
     * getMinSpanningTree returns the minimum spanning tree, the subset of edges with the minimum
     * edge weight sum such that for every pair of vertices v and w in there is one and only one 
     * path from v to w
     * @return list of edges representing the minimum spanning tree of the graph
     */
    public ArrayList<Edge<T>> getMinSpanningTree(){
	return getSpanningTree(true);
    }

    /**
     * getMaxSpanningTree returns the maximum spanning tree, the subset of edges with the maximum
     * edge weight sum such that for every pair of vertices v and w in there is one and only one 
     * path from v to w
     * @return list of edges representing the maximum spanning tree of the graph
     */
    public ArrayList<Edge<T>> getMaxSpanningTree(){
	return getSpanningTree(false);
    }

    /**
     * getSpanningTree returns either the minimum or maximum spanning tree of a tree depending on the boolean passed in
     * @param boolean which is true if you want the minimum spanning tree and false if you want max
     * @return list of edges representing the minimum spanning tree of the graph if true and maximum if false
     */
    protected ArrayList<Edge<T>> getSpanningTree(boolean min){
	PriorityQueue<Vertex<T>> vertices;
	//if you want a minimum spanning tree create a minimum priority queue else create a max queue
	if(min){
	    vertices = new PriorityQueue<Vertex<T>>();
	}
	else{
	    vertices = new PriorityQueue<Vertex<T>>(Collections.reverseOrder());
	}
	Vertex<T> start = null;
	//add all nodes to the vertices queue and for min set the cost for everything but the start, which is
	//arbitrarily chosen, to a crazy high number and for max 0 so that the start will be at the top of the
	//priority queue
	for(Vertex<T> vertex : graph.values()){
	    if(start == null){
		start = vertex;
		if(min){
		    start.setCost(0);
		}
		else{
		    start.setCost(300000);
		}
	    }
	    else{
		if(min){
		    vertex.setCost(300000);
		}
		else{
		    vertex.setCost(0);
		}
	    }
	    vertex.setPredecessor(null);
	    vertices.add(vertex);
	}
	while(vertices.size() > 0){
	    //for all the as yet unvisted neighbors of the top of the queue, check if it is cheaper/more expensive
	    //(for min/max respectively) to get there through the top vertex than by whatever route they have so far,
	    //if it is change the cost to the cost through the top vertex and set that vertex to the predecessor
	    Vertex<T> top = vertices.poll();
	    for(Edge<T> edge : top.getOutgoing()){
		if(vertices.contains(edge.getSink())){
		    if(min){
			if(edge.getSink().getCost() > edge.getWeight()){
			    edge.getSink().setCost(edge.getWeight());
			    edge.getSink().setPredecessor(edge);
			    //cost has been changed so remove and re-add so the priority will be correct
			    vertices.remove(edge.getSink());
			    vertices.add(edge.getSink());
			}
		    }
		    else{
			if(edge.getSink().getCost() < edge.getWeight()){
			    edge.getSink().setCost(edge.getWeight());
			    edge.getSink().setPredecessor(edge);
			    //cost has been changed so remove and re-add so the priority will be correct
			    vertices.remove(edge.getSink());
			    vertices.add(edge.getSink());
			}
		    }
		}
	    }
	}
	//for all vertices in the graph except the start of the tree, add their predicessor to the list of MST edges
	ArrayList<Edge<T>> edgeTree = new ArrayList<Edge<T>>();
	for(Vertex<T> current : graph.values()){
	    if(!current.equals(start)){
		edgeTree.add(current.getPredecessor());
	    }
	}
	return edgeTree;
    }

    /**
     * graphDiameter returns the longest shortest path between any two vertices of the graph
     * @return the diameter of the graph
     */
    public int graphDiameter(){
	int maxSteps = 0;
	for(Vertex<T> vertex1 : graph.values()){
	    for(Vertex<T> vertex2 : graph.values()){
		//check every possible path between two vertices
		//if it is longer than the current max, update the current max
		int path = shortestPathSteps(vertex1, vertex2);
		if(path > maxSteps){
		    maxSteps = path;
		}
	    }
	}
	return maxSteps;
    }

    /**
     * shortestPathSteps returns the shortest path between two given vertices based on the number of edges traversed
     * @param start and end vertices
     * @return the length of the shortest path between the given vertices
     */
    protected int shortestPathSteps(Vertex<T> start, Vertex<T> end){
	int steps = 0;
	LinkedList<Vertex<T>> queue = new LinkedList<Vertex<T>>();
	LinkedList<Vertex<T>> unvisited = new LinkedList<Vertex<T>>();
	for(Vertex<T> vertex : graph.values()){
	    unvisited.add(vertex);
	}
	unvisited.remove(start);
	queue.add(start);
	boolean found = false;
	while(queue.size() > 0 && !found){
	    Vertex<T> vertex = queue.poll();
	    for(Edge<T> outEdge : vertex.getOutgoing()){
		if(unvisited.contains(outEdge.getSink())){
		    unvisited.remove(outEdge.getSink());
		    queue.add(outEdge.getSink());
		    outEdge.getSink().setPredecessor(outEdge);
		}
		if(outEdge.getSink().equals(end)){
		    found = true;
		    break;
		}
	    }
	}
	Vertex<T> current = graph.get(end.getId());
	while(!current.equals(graph.get(start.getId()))){
	    current = current.getPredecessor().getSource();
	    steps++;
	}
	return steps;
    }

    /**
     * toString overrides object toString
     * @return String representation of a Graph
     */
    public String toString(){
	String toRet = "";
	for(Vertex<T> vertex : graph.values()){
	    ArrayList<Edge<T>> outgoing = vertex.getOutgoing();
	    ArrayList<Edge<T>> incoming = vertex.getIncoming();
	    toRet += "Vertex " + vertex.getData() + " ||| Edges: ";
	    for(int i = 0; i < outgoing.size(); i++){
		toRet += outgoing.get(i).getSource().getData() + "->" + outgoing.get(i).getSink().getData() + " ";
	    }
	    for(int j = 0; j < incoming.size(); j++){
		toRet += incoming.get(j).getSource().getData() + "->" + incoming.get(j).getSink().getData() + " ";
	    }
	    toRet += "\n";
	}
	return toRet;
    }

    public static void main(String[] args){
	Graph graph = new Graph();
	Vertex<String> a = new Vertex<String>("a", new Long(111111));
	Vertex<String> b = new Vertex<String>("b", new Long(222222));
	Vertex<String> c = new Vertex<String>("c", new Long(333333));
	Vertex<String> d = new Vertex<String>("d", new Long(444444));
	Vertex<String> e = new Vertex<String>("e", new Long(555555));
	graph.addVertex(new Long(111111), a);
	graph.addVertex(new Long(222222), b);
	graph.addVertex(new Long(333333), c);
	graph.addVertex(new Long(444444), d);
	graph.addVertex(new Long(555555), e);
	graph.addEdge(a, d);
	graph.addEdge(d, e);
	graph.addEdge(e, a);
	graph.addEdge(a, c);
	graph.addEdge(c, b);
	graph.addEdge(b, a);
	System.out.println(graph.isSinglyConnected());
	graph = graph.makeUndirected();
	System.out.println(graph.isSinglyConnected());

	Graph graph2= new Graph();
	Vertex<String> zero = new Vertex<String>("0", new Long(111111));
	Vertex<String> one = new Vertex<String>("1", new Long(222222));
	Vertex<String> two = new Vertex<String>("2", new Long(333333));
	Vertex<String> three = new Vertex<String>("3", new Long(444444));
	Vertex<String> four = new Vertex<String>("4", new Long(555555));
	Vertex<String> five = new Vertex<String>("5", new Long(666666));
	Vertex<String> six = new Vertex<String>("6", new Long(777777));
	Vertex<String> seven = new Vertex<String>("7", new Long(888888));
	Vertex<String> eight = new Vertex<String>("8", new Long(999999));
	Edge<String> zero1 =  new Edge<String>(zero, one, 4);
	Edge<String> zero7 = new Edge<String>(zero, seven, 8);
	Edge<String> one2 = new Edge<String>(one, two, 8);
	Edge<String> one7 = new Edge<String>(one, seven, 11);
	Edge<String> two3 = new Edge<String>(two, three, 7);
	Edge<String> two5 = new Edge<String>(two, five, 4);
	Edge<String> two8 = new Edge<String>(two, eight, 2);
	Edge<String> three4 = new Edge<String>(three, four, 9);
	Edge<String> three5 = new Edge<String>(three, five, 14);
	Edge<String> four5 = new Edge<String>(four, five, 10);
	Edge<String> five6 = new Edge<String>(five, six, 2);
	Edge<String> six7 = new Edge<String>(six, seven, 1);
	Edge<String> six8 = new Edge<String>(six, eight, 6);
	Edge<String> seven8 =new Edge<String>(seven, eight, 7);
	graph2.addVertex(new Long(111111), zero);
	graph2.addVertex(new Long(222222), one);
	graph2.addVertex(new Long(333333), two);
	graph2.addVertex(new Long(444444), three);
	graph2.addVertex(new Long(555555), four);
	graph2.addVertex(new Long(666666), five);
	graph2.addVertex(new Long(777777), six);
	graph2.addVertex(new Long(888888), seven);
	graph2.addVertex(new Long(999999), eight);
	graph2.addEdge(zero1);
	graph2.addEdge(zero7);
	graph2.addEdge(one2);
	graph2.addEdge(one7);
	graph2.addEdge(two3);
	graph2.addEdge(two5);
	graph2.addEdge(two8);
	graph2.addEdge(three4);
	graph2.addEdge(three5);
	graph2.addEdge(four5);
	graph2.addEdge(five6);
	graph2.addEdge(six7);
	graph2.addEdge(six8);
	graph2.addEdge(seven8);
	graph2 = graph2.makeUndirected();
	ArrayList<Edge<String>> min = graph2.getMinSpanningTree();
	ArrayList<Edge<String>> max = graph2.getMaxSpanningTree();
	for(Edge<String> edge : min){
	    System.out.print(edge.getSource().getData() + edge.getSink().getData() + " ");
	}
	System.out.println();
	for(Edge<String> edge : max){
	    System.out.print(edge.getSource().getData() + edge.getSink().getData() + " ");
	}
	System.out.println();

	Graph graph3 = new Graph();
	Vertex<String> one1 = new Vertex<String>("1", new Long(222222));
	Vertex<String> two2 = new Vertex<String>("2", new Long(333333));
	Vertex<String> three3 = new Vertex<String>("3", new Long(444444));
	Vertex<String> four4 = new Vertex<String>("4", new Long(555555));
	graph3.addVertex(new Long(222222), one1);
	graph3.addVertex(new Long(333333), two2);
	graph3.addVertex(new Long(444444), three3);
	graph3.addVertex(new Long(555555), four4);
	graph3.addEdge(one1, two2);
	graph3.addEdge(one1, three3);
	graph3.addEdge(two2, four4);
	graph3.addEdge(three3, four4);
	System.out.println(graph3.betweennessCentrality(one1));
    }
}
