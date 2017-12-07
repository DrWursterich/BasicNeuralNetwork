package BNN;

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
		this.child = child;
		this.parent = parent;
		this.weight = weight;
		this.child.addOutput(this);
	}

	public Connection(Node child, Node parent) throws IllegalArgumentException {
		this(child, parent, Math.random() * 2 -1);
	}

	Connection(Node child, Node parent, double weight, double deltaWeight, double prevDeltaWeight) {
		this(child, parent, weight);
		this.deltaWeight = deltaWeight;
		this.prevDeltaWeight = prevDeltaWeight;
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
		this.prevDeltaWeight = this.deltaWeight;
		this.deltaWeight = -learningRate * (-this.parent.getContent() * (1 - this.parent.getContent())
				* this.child.getContent() * sumErrorOutputs);
	}

	public void calculateWeight(double momentum) {
		this.weight = this.weight + this.deltaWeight + momentum * this.prevDeltaWeight;
	}
}