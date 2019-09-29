import java.util.ArrayList;
import java.lang.Comparable;

/**
 * The vertex class holds a vertex of a graph. The vertex has a value stored
 * in it that can be accessed and changed
 */

public class Vertex<T> implements Comparable<Vertex<T>>{

    protected T data;
    protected long id;
    protected ArrayList<Edge<T>> incoming;
    protected ArrayList<Edge<T>> outgoing;
    //the incoming edge from the predecessor of the vertex
    protected Edge<T> predecessor;
    //cost for use in shortest path
    float cost;

    /**
     * vertex constructor takes a value and an long and sets them to the data 
     * and id of the vertex
     * @param value to be set
     * @param id of the vertex
     */
    public Vertex(T data, long id){
	this.data = data;
	this.id = id;
	incoming = new ArrayList<Edge<T>>();
	outgoing = new ArrayList<Edge<T>>();
	predecessor = null;
	cost = 0;
    }

    /**
     * getData returns the value of the vertex
     * @return data
     */
    public T getData(){
	return data;
    }

    /**
     * getId returns the unique id of the vertex
     * @return id
     */
    public long getId(){
	return id;
    }

    /**
     * setData takes a value and sets it to the data of the vertex
     * @param new data to be set
     */
    public void setData(T newData){
	data = newData;
    }

    /**
     * addIncoming adds a new incoming edge to the vertex
     * @param edge to be added
     */
    public void addIncoming(Edge<T> edge){
	incoming.add(edge);
    }

    /**
     * removeIncoming removes a given incoming edge
     * @param edge to be removed
     */
    public void removeIncoming(Edge<T> edge){
	incoming.remove(edge);
    }

    /**
     * addOutgoing adds a new outgoing edge to the vertex
     * @param edge to be added
     */
    public void addOutgoing(Edge<T> edge){
	outgoing.add(edge);
    }
    /**
     * removeOutgoing removes a given outgoing edge
     * @param edge to be removed
     */
    public void removeOutgoing(Edge<T> edge){
	outgoing.remove(edge);
    }

    /**
     * getIncoming returns a list of all the incoming edges to a vertex
     * @return incoming
     */
    public ArrayList<Edge<T>> getIncoming(){
	return incoming;
    }

    /**
     * getOutgoing returns a list of all the outgoing edges to a vertex
     * @return outgoing
     */
    public ArrayList<Edge<T>> getOutgoing(){
	return outgoing;
    }

    /**
     * getNeighbors returns an arrayList of all the verticies that can be
     * reached from the current vertex
     * @return ArrayList of neighbors
     */
    public ArrayList<Vertex<T>> getNeighbors(){
	ArrayList<Vertex<T>> neighbors = new ArrayList<Vertex<T>>();
	for(Edge<T> edge : outgoing){
	    neighbors.add(edge.getSink());
	}
	return neighbors;
    }

    /**
     * getPredecessor returns the incoming edge from the vetrex's predecessor
     * @return predecessor
     */
    public Edge<T> getPredecessor(){
	return predecessor;
    }

    /**
     * setPredecessor takes an edge and sets it to the predecessor of the vetrex
     * @param edge to be set to predecessor
     */
    public void setPredecessor(Edge<T> predecessor){
	this.predecessor = predecessor;
    }

    /**
     * getCost returns the cost of a vertex for use in shortest path calculation
     * @return cost
     */
    public float getCost(){
	return cost;
    }

    /**
     * setCost takes an float and sets it to the cost of the vertex
     * @param value to be set to cost
     */
    public void setCost(float cost){
	this.cost = cost;
    }

    /**
     * compareTo compares the current vertex and a given vertex on the basis of
     * cost
     * @return -1, 0, or 1 if the current vertex's cost is less than, equal to
     * or greater than the cost of the given vertex respectively
     */
    public int compareTo(Vertex<T> vertex2){
	if(this.getCost() > vertex2.getCost()){
	    return 1;
	}
	if(this.getCost() == vertex2.getCost()){
	    return 0;
	}
	else{
	    return -1;
	}
    }
    

}
