public class Connection {
	private Node child;
	private Node parent;
	private double weight;
	private double deltaWeight;
	private double prevDeltaWeight;

	public Connection(Node child, Node parent, double weight) {
		this.child = child;
		this.parent = parent;
		this.weight = weight;
		this.child.addOutput(this);
	}

	public Connection(Node child, Node parent) {
		this(child, parent, Math.random() * 2 -1);
	}

	public Node getChild() {
		return child;
	}

	public void setChild(Node child) {
		this.child = child;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getDeltaWeight() {
		return deltaWeight;
	}

	public double getPrevDeltaWeight() {
		return prevDeltaWeight;
	}

	public void calculateDeltaWeight(double learningRate, double sumErrorOutputs) {
		//sumErrorOutputs = (idealOutput - this.parent.getContent());
		this.prevDeltaWeight = this.deltaWeight;
		this.deltaWeight = -learningRate * (-this.parent.getContent() * (1 - this.parent.getContent())
				* this.child.getContent() * sumErrorOutputs);
	}

	public void calculateWeight(double momentum) {
		this.weight += this.deltaWeight + momentum * this.prevDeltaWeight;
	}
}