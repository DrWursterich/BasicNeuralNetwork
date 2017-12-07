public class Node {
	private NeuralNet neuralNet;
	private Connection[] inputs;
	private Connection[] outputs;
	private double content = 0;

	public Node(NeuralNet neuralNet, Node[] inputNodes) throws IllegalArgumentException {
		if (neuralNet == null) {
			throw new IllegalArgumentException("Neural net can not be null.");
		}
		if (inputNodes == null) {
			inputNodes = new Node[0];
		}
		this.neuralNet = neuralNet;
		this.inputs = new Connection[inputNodes.length];
		for (int i=inputs.length-1;i>=0;i--) {
			if (inputNodes[i] == null) {
				throw new IllegalArgumentException("Input nodes can not contain null.");
			}
			this.inputs[i] = new Connection(inputNodes[i], this);
		}
		this.outputs = new Connection[0];
	}

	public Node(NeuralNet neuralNet) throws IllegalArgumentException {
		this(neuralNet, new Node[0]);
	}

	public NeuralNet getNeuralNet() {
		return this.neuralNet;
	}

	public void setContent(double content) throws IllegalArgumentException {
		if (content < 0 || content > 1) {
			throw new IllegalArgumentException("Node content has to be between 0 and 1.");
		}
		this.content = content;
	}

	public double getContent() {
		return this.content;
	}

	public int getInputCount() {
		return this.inputs.length;
	}

	public Connection getInput(int node) throws IllegalArgumentException {
		if (node < 0 || node > this.inputs.length-1) {
			throw new IllegalArgumentException("Node does not exist.");
		}
		return this.inputs[node];
	}

	public Connection[] getInputs() {
		return this.inputs;
	}

	public boolean isInput() {
		return this.inputs.length == 0;
	}

	public int getOutputCount() {
		return this.outputs.length;
	}

	public Connection getOutput(int node) throws IllegalArgumentException {
		if (node < 0 || node > this.outputs.length-1) {
			throw new IllegalArgumentException("Node does not exist.");
		}
		return this.outputs[node];
	}

	public Connection[] getOutputs() {
		return this.outputs;
	}

	public boolean isOutput() {
		return this.outputs.length == 0;
	}

	public void addOutput(Connection output) throws IllegalArgumentException {
		if (output == null) {
			throw new IllegalArgumentException("Output can not be null.");
		}
		Connection[] newOutputs = new Connection[this.outputs.length+1];
		System.arraycopy(this.outputs, 0, newOutputs, 0, newOutputs.length-1);
		newOutputs[newOutputs.length-1] = output;
		this.outputs = newOutputs;
	}

	public void calculateContent() {
		if (this.inputs.length != 0) {
			double content = 0;
			for (int i=this.inputs.length-1;i>=0;i--) {
				content += this.inputs[i].getChild().getContent() * this.inputs[i].getWeight();
			}
			this.content = (1 / (1 + Math.pow(Math.E, (-1 * content))));
		}
	}
}