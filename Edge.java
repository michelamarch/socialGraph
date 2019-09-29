public class Edge<T>{

    protected float weight;
    protected Vertex<T> source;
    protected Vertex<T> sink;

    /**
     * This edge constructor takes a start and end vertex for the edge and sets
     * them. The weight is automatically set to 1.
     * @param source, sink verticies to be set
     */
    public Edge(Vertex<T> source, Vertex<T> sink){
	this.source = source;
	this.sink = sink;
	weight = 1;
    }
    
    /**
     * This edge constructor takes a weight and a start and end vertex for the 
     * edge and sets them.
     * @param source, sink verticies to be set
     * @param weight to be set
     */
    public Edge(Vertex<T> source, Vertex<T> sink, float weight){
	this.source = source;
	this.sink = sink;
	this.weight = weight;
    }

    /**
     * getWeight returns the weight of the edge
     * @return weight
     */
    public float getWeight(){
	return weight;
    }

    /**
     * setWeight takes a value and sets it to the weight of the edge
     * @param new weight to be set
     */
    public void setWieght(float newWeight){
	weight = newWeight;
    }

    /**
     * incrementWeight adds one to the edge's weight
     */
    public void incrementWeight(){
	weight++;
    }

    /**
     * getSource returns the source vertex of the edge
     * @return source
     */
    public Vertex<T> getSource(){
	return source;
    }

    /**
     * getSink returns the sink vertex of the edge
     * @return sink
     */
    public Vertex<T> getSink(){
	return sink;
    }

    

}
