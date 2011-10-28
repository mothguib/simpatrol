package util.graph2;

import java.util.LinkedList;
import java.util.List;


/**
 * Weighted directed graph without parallel edges. Undirected graphs may be represented
 * using symmetric edges (and repeating their costs and identifiers). 
 * 
 * @author Pablo
 */
public class Graph {
	private String label;
	private Node[] nodes;
	private int nodesSize;
	
	private Edge[][]     matrix;      // adjacencies matrix
	private List<Edge>[] adjacencies; // adjacencies lists
	
	private  Representation representation; // indicate which of the structures above are used
	
	
	public Graph(int numVertices) {
		this("", numVertices, Representation.LISTS);
	}

	public Graph(int numVertices, Representation r) {
		this("", numVertices, r);
	}
	
	@SuppressWarnings("unchecked")
	public Graph(String name, int numVertices, Representation r) {
		this.label = name;
		this.nodes = new Node[numVertices];
		this.nodesSize = 0;
		
		this.representation = (r == null) ? Representation.LISTS : r;			
		
		if (r != Representation.LISTS) {
			matrix = new Edge[numVertices][numVertices];
		}
		if (r != Representation.MATRIX) {
			adjacencies = new LinkedList[numVertices];
			for (int i = 0; i < numVertices; i++) {
				adjacencies[i] = new LinkedList<Edge>();
			}
		}
	}
	
	public String getName() {
		return label;
	}
	
	public Node addNode(String identifier) {
		Node info = new Node(nodesSize, identifier);		
		
		nodes[nodesSize] = info;
		nodesSize ++;
		
		return info;
	}
	
	
	public Node getNode(int index) {
		return nodes[index];
	}
	
	public Node getNode(String identifier) {
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null && nodes[i].getIdentifier().equals(identifier)) {
				return nodes[i];
			}
		}
		return null;
	}
	
	//otimizado para: matrix, mixed
	public void addEdge(String identifier, Node v, Node u, double length) {
		addEdge(identifier, v.getIndex(), u.getIndex(), length);
	}

	//otimizado para: matrix, mixed
	public void addEdge(String identifier, int v, int u, double length) {
		Edge edge = new Edge(identifier, v, u, length);
		
		// atencão: este if tem que vir antes do if para matriz por 
		// causa da chamada a existsEdge(), que usa matriz (se houver)
		if (representation != Representation.MATRIX) {
			if (existsEdge(v, u)) {
				adjacencies[v].remove(edge);					
			}
			adjacencies[v].add(edge);
		}
		if (representation != Representation.LISTS) {
			matrix[v][u] = edge;
		}
	}

	//otimizado para: matrix
	public void removeEdge(Node source, Node target) {
		removeEdge(source.getIndex(), target.getIndex());
	}
	
	//otimizado para: matrix
	public void removeEdge(int v, int u) {
		if (representation != Representation.LISTS) {
			matrix[v][u] = null;
		}
		if (representation != Representation.MATRIX) {
			adjacencies[v].remove(new Edge(v,u));
		}		
	}

	public int getNumVertices() {
		if (representation != Representation.MATRIX) {
			return adjacencies.length;
		} else {
			return matrix.length;
		}
	}
	
	public int getNumEdges() {
		int count = 0;
		for (int v = 0; v < getNumVertices(); v++) {
			count += getOutEdges(v).size();
		}
		return count;
	}
	
	//otimizado para: matrix, mixed
	public boolean existsEdge(Node source, Node target) {
		return existsEdge(source.getIndex(), target.getIndex());
	}
	
	//otimizado para: matrix, mixed
	public boolean existsEdge(int v, int u) {
		if (representation != Representation.LISTS) {
			return matrix[v][u] != null;
		} else {
			return adjacencies[v].contains(new Edge(v,u));
		}
	}

	//otimizado para: matrix, mixed
	public double getLength(Node source, Node target) {
		return getLength(source.getIndex(), target.getIndex());
	}
	
	//otimizado para: matrix, mixed
	public double getLength(int source, int target) {
		if (representation != Representation.LISTS) {
			return matrix[source][target].getLength();
		
		} else {
			Edge vu = new Edge(source,target);
			for (Edge edge : adjacencies[source]) {
				if (edge.equals(vu)) {
					return edge.getLength();
				}
			}
			return 0;
		}
	}
	
	//otimizado para: lists
	public List<Node> getSuccessors(Node source) {
		List<Node> succ = new LinkedList<Node>();
		
		if (representation != Representation.MATRIX) {
			for (Edge e : adjacencies[source.getIndex()]) {
				succ.add( getNode(e.getTargetIndex()) );
			}
			
		} else {
			for (int target = 0; target < matrix.length; target++) {
				if (matrix[source.getIndex()][target] != null) {
					succ.add( getNode(target) );
				}
			}
		}
		
		return succ;
	}

	//otimizado para: lists
	public List<Integer> getSuccessors(int node) {
		List<Integer> succ = new LinkedList<Integer>();
		
		if (representation != Representation.MATRIX) {
			for (Edge e : adjacencies[node]) {
				succ.add(e.getTargetIndex());
			}
			
		} else {
			for (int u = 0; u < matrix.length; u++) {
				if (matrix[node][u] != null) {
					succ.add(u);
				}
			}
		}
		
		return succ;
	}
	
	//otimizado para: lists 
	public List<Edge> getOutEdges(Node source) {
		return getOutEdges(source.getIndex());
	}	
	
	//otimizado para: lists 
	public List<Edge> getOutEdges(int source) {
		List<Edge> succ = new LinkedList<Edge>();
		
		if (representation != Representation.MATRIX) {
			succ = new LinkedList<Edge>(adjacencies[source]);
			
		} else {
			for (int u = 0; u < matrix.length; u++) {
				if (matrix[source][u] != null) {
					succ.add(matrix[source][u]);
				}
			}
		}
		
		return succ;
	}

	public boolean isSymmetrical() {		
		for (int v = 0; v < getNumVertices(); v++) {
			for (Edge edge : this.getOutEdges(v)) {
				if (! existsEdge(edge.getTargetIndex(), edge.getSourceIndex()) 
						|| getLength(edge.getTargetIndex(), edge.getSourceIndex()) != getLength(edge.getSourceIndex(), edge.getTargetIndex())) {
					return false;
				}
			}
		}
		
		return true;
	}

	public Representation getRepresentation() {
		return representation;
	}
	
	@SuppressWarnings("unchecked")
	public void changeRepresentation(Representation newRepresentation) {
		Representation oldRepresetation = getRepresentation();
		if (oldRepresetation == newRepresentation) {
			return;
		}
		
		List<Edge>[] adj_ = null;
		Edge[][] mat_ = null;
		
		int numVertices = getNumVertices();
		List<Edge> outEdges;

		if (newRepresentation != Representation.LISTS 
				&& oldRepresetation == Representation.LISTS) {
			mat_ = new Edge[numVertices][numVertices];
		}
		if (newRepresentation != Representation.MATRIX
				&& oldRepresetation == Representation.MATRIX) {
			adj_ = new LinkedList[numVertices];
			for (int i = 0; i < numVertices; i++) {
				adj_[i] = new LinkedList<Edge>();
			}
		}
		
		for (int v = 0; v < numVertices; v++) {
			outEdges = getOutEdges(v);
			for (Edge e : outEdges) {
				if (mat_ != null) {
					mat_[v][e.getTargetIndex()] = e;
				}
				if (adj_ != null) {
					adj_[v].add(e);
				}
			}
		}
		
		if (representation == Representation.MATRIX) {
			this.adjacencies = null;
		}
		if (representation == Representation.LISTS) {
			this.matrix = null;
		}
		if (adj_ != null) {
			this.adjacencies = adj_;
		}
		if (mat_ != null) {
			this.matrix = mat_;
		}
		
		this.representation = newRepresentation;
	}
	
	public boolean equals(Object o) {
		if (! (o instanceof Graph)) {
			return false;
		}
		
		Graph other = (Graph)o;
		int numVertices = this.getNumVertices();
		
		if (numVertices != other.getNumVertices()) {
			return false;
		}
		
		for (int v = 0; v < numVertices; v++) {
			for (int u = 0; u < numVertices; u++) {
				if (this.getLength(v, u) 
						!= other.getLength(v, u)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();

		if (representation != Representation.LISTS) {
			builder.append("\n");
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix.length; j++) {
					builder.append(" ");
					builder.append(matrix[i][j]);
				}
				builder.append("\n");
			}
		
		}
		if (representation != Representation.MATRIX) {
			builder.append("\n");
			for (int u = 0; u < adjacencies.length; u++) {
				builder.append("Adj[");
				builder.append(u);
				builder.append("] = ");
				builder.append(adjacencies[u]);
				builder.append("\n");
			}
		}
		
		return builder.toString();
	}

}
