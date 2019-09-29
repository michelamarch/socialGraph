import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Collection;
import java.io.File;
import java.util.Scanner;
import java.lang.Long;
import java.util.Enumeration;

public class SocialGraph extends Graph<String>{

    /**
     * the constructor for SocialGraph takes a file and reads it into a graph
     * @param fileName of file to be graph-ified
     */
    public SocialGraph(String fileName){
	try{
	    File file = new File(fileName);

	    Scanner fileScanner = new Scanner(file);

	    Hashtable<Long, ArrayList<String[]>> likesAndFollows =
		new Hashtable<Long, ArrayList<String[]>>();

	    //read through the file and make the vertices and add all the likes and follows to the likesAndFollows hashtable
	    while(fileScanner.hasNextLine()){
	        Long id = Long.parseLong(fileScanner.nextLine());
		String name = fileScanner.nextLine();
		Vertex<String> toAdd = new Vertex(name, id.longValue());
		ArrayList<String[]> userActivity = new ArrayList<String[]>();
		String[] follows = fileScanner.nextLine().split(",");
		String[] likes = fileScanner.nextLine().split(",");
		userActivity.add(follows);
		userActivity.add(likes);
		likesAndFollows.put(id, userActivity);
		graph.put(id, toAdd);
	    }

	    //for every vertex add the edges and change the weights according to likesAndFollows
	    Enumeration<Long> keys = likesAndFollows.keys();
	    while(keys.hasMoreElements()){
		Long id = keys.nextElement();
		Vertex<String> vertex = graph.get(id);
		String[] follows = likesAndFollows.get(id).get(0);
		String[] likes = likesAndFollows.get(id).get(1);
		for(String following : follows){
		    if(!following.equals("")){
			Vertex<String> sink = graph.get(Long.parseLong(following));
			Edge<String> edge = new Edge(vertex, sink);
			vertex.addOutgoing(edge);
			sink.addIncoming(edge);
		    }
		}
		for(String like : likes){
		    if(!like.equals("")){
			Vertex<String> liked = graph.get(Long.parseLong(like));
			for(Edge<String> edge : vertex.getOutgoing()){
			    if(edge.getSink().equals(liked)){
				edge.incrementWeight();
			    }
			}
		    }
		}

	    }
		

	}
	catch (Exception e){
	    e.printStackTrace();
	}
    }

    /**
     * getIDs returns an array of all the ids in the graph
     * @return array of all ids in the graph
     */
    public long[] getIDs(){
	ArrayList<Long> ids = new ArrayList<Long>();
	Enumeration<Long> keys = graph.keys();
	while(keys.hasMoreElements()){
	    ids.add(keys.nextElement());
	}
	long[] idArray = new long[ids.size()];
	for(int i = 0; i < idArray.length; i++){
	    idArray[i] = ids.get(i);
	}
	return idArray;
    }

    /**
     * getName takes and id and returns the name of the vertex associated with that id
     * @param id
     * @return name for given id
     */
    public String getName(long id){
        Vertex<String> vertex = graph.get(id);
        if(vertex != null){
	    return vertex.getData();
	}
	else{
	    return null;
	}
    }

    /**
     * getFollows gets an array of all the ids for the accounts a given account follows
     * @param id
     * @return array of all of the accounts id follows
     */
    public long[] getFollows(long id){
        Vertex<String> vertex = graph.get(id);
        if(vertex != null){
	    ArrayList<Vertex<String>> follows = vertex.getNeighbors();
	    long[] followsArray = new long[follows.size()];
	    for(int i = 0; i < followsArray.length; i++){
		followsArray[i] = follows.get(i).getId();
	    }
	    return followsArray;
	}
	else{
	    return null;
	}
    }

    /**
     * getFollowers gets an array of all the ids for the accounts that follow a given account
     * @param id
     * @return array of all of the accounts that follow id
     */
    public long[] getFollowers(long id){
        Vertex<String> vertex = graph.get(id);
        if(vertex != null){
	    ArrayList<Edge<String>> followers = vertex.getIncoming();
	    long[] followersArray = new long[followers.size()];
	    for(int i = 0; i < followersArray.length; i++){
		followersArray[i] = followers.get(i).getSource().getId();
	    }
	    return followersArray;
	}
	else{
	    return null;
	}
    }

    /**
     * getLikes returns the number of times a given vertex has been liked by another given vertex
     * @param id1 the account doing the liking
     * @param id2 the account being liked
     * @return the number of times id1 liked id2
     */
    public float getLikes(long id1, long id2){
	Vertex<String> vertexId1 = graph.get(id1);
	int count = 0;
	while(count < vertexId1.getOutgoing().size() &&
	      vertexId1.getOutgoing().get(count).getSink().getId() != id2){
	    count++;
	}
	if(count >= vertexId1.getOutgoing().size()){
	    return 0;
	}
	else{
	    return vertexId1.getOutgoing().get(count).getWeight() - 1;
	}
    }

    public static void main(String[] args){
	//make a new SocialGraph baswed off of a file read in from the command line
	if(args.length > 0){
	    String fileName = args[0];
	    SocialGraph graph = new SocialGraph(fileName);
	    //find the first and second vertices from the file
	    Vertex<String> firstVertex = null;
	    Vertex<String> secondVertex = null;
	    try{
		File file = new File(fileName);
		Scanner fileScanner = new Scanner(file);
		int count = 0;
		while(count <= 4){
		    if(count == 0){
			firstVertex = graph.getVertex(Long.parseLong(fileScanner.nextLine()));
		    }
		    else if(count == 4){
			secondVertex = graph.getVertex(Long.parseLong(fileScanner.nextLine()));
		    }
		    else{
			fileScanner.nextLine();
		    }
		    count++;
		}
		//print the path between the first and second vertices
		System.out.println("The shortest path between " + firstVertex.getData() + " and " + secondVertex.getData() +
				   " is: ");
		ArrayList<Edge<String>> path = graph.getShortestPath(firstVertex, secondVertex);
		for(int i = path.size() - 1; i >= 0; i--){
		    System.out.print(path.get(i).getSource().getData() + " ");
		}
		System.out.print(secondVertex.getData());
		System.out.println();
	    }
	    catch(Exception e){
		e.printStackTrace();
	    }
	    
	    //print the minimum spanning tree
	    System.out.println("The minimum spanning tree contains the edges: ");
	    ArrayList<Edge<String>> minTree = graph.getMinSpanningTree();
	    for(Edge<String> edge : minTree){
		System.out.print(edge.getSource().getData() + "->" + edge.getSink().getData() + " ");
	    }
	    System.out.println();
	    
	    //print the maximum spanning tree
	    System.out.println("The maximum spanning tree contains the edges: ");
	    ArrayList<Edge<String>> maxTree = graph.getMinSpanningTree();
	    for(Edge<String> edge1 : maxTree){
		System.out.print(edge1.getSource().getData() + "->" + edge1.getSink().getData() + " ");
	    }
	    System.out.println();
	    
	    //print graph's diameter
	    System.out.println("The graph's diameter is " + graph.graphDiameter());
	    
	    //print if the graph is singly connected
	    boolean connected = graph.isSinglyConnected();
	    if(connected){
		System.out.println("The graph is singly connected.");
	    }
	else{
	    System.out.println("The graph is not singly connected.");
	}
	    
	    //print the name and betweenness centrality of each vertex
	    for(Vertex<String> vertex : graph.getVertices()){
		System.out.println(vertex.getData() + " has a betweenness centrality of: " +
				   graph.betweennessCentrality(vertex));
	    }
	}
	else{
	    System.out.println("Please input the name of the file in the command line when running");
	}
	
        
    }
	    

}
