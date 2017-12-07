public class Connection {
	private Node child;
	private Node parent;
	private double weight;
	private double deltaWeight;
	private double prevDeltaWeight;

	public Connection(Node child, Node parent, double weight) throws IllegalArgumentException {
		if (child == null) {
			throw new IllegalArgumentException("Child can not be null.");
		}
		if (parent == null) {
			throw new IllegalArgumentException("Parent can not be null.");
		}
		if (weight < -1 || weight > 1) {
			throw new IllegalArgumentException("Weight has to be between -1 and 1.");
		}
		this.child = child;
		this.parent = parent;
		this.weight = weight;
		this.child.addOutput(this);
	}

	public Connection(Node child, Node parent) throws IllegalArgumentException {
		this(child, parent, Math.random() * 2 -1);
	}

	public Node getChild() {
		return child;
	}

	public void setChild(Node child) throws IllegalArgumentException {
		if (parent == null) {
			throw new IllegalArgumentException("Child can not be null.");
		}
		this.child = child;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) throws IllegalArgumentException {
		if (parent == null) {
			throw new IllegalArgumentException("Parent can not be null.");
		}
		this.parent = parent;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) throws IllegalArgumentException {
		if (weight < -1 || weight > 1) {
			throw new IllegalArgumentException("Weight has to be between -1 and 1.");
		}
		this.weight = weight;
	}

	public double getDeltaWeight() {
		return deltaWeight;
	}

	public double getPrevDeltaWeight() {
		return prevDeltaWeight;
	}

	public void calculateDeltaWeight(double learningRate, double sumErrorOutputs) {
		this.prevDeltaWeight = this.deltaWeight;
		this.deltaWeight = -learningRate * (-this.parent.getContent() * (1 - this.parent.getContent())
				* this.child.getContent() * sumErrorOutputs);
	}

	public void calculateWeight(double momentum) {
		this.weight = Math.min(Math.max(this.weight + this.deltaWeight + momentum * this.prevDeltaWeight, 0), 1);
	}
}