public class Node {
	private NeuralNet neuralNet;
	private Connection[] inputs;
	private Connection[] outputs;
	private double content = 0;
	
	public Node(NeuralNet neuralNet, Node[] inputNodes) {
		this.neuralNet = neuralNet;
		this.inputs = new Connection[inputNodes.length];
		for (int i=inputs.length-1;i>=0;i--) {
			this.inputs[i] = new Connection(inputNodes[i], this);
		}
		this.outputs = new Connection[0];
	}
	
	public Node(NeuralNet neuralNet) {
		this(neuralNet, new Node[0]);
	}
	
	public NeuralNet getNeuralNet() {
		return this.neuralNet;
	}
	
	public void setContent(double content) {
		this.content = content;
	}
	
	public double getContent() {
		return this.content;
	}
	
	public int getInputCount() {
		return this.inputs.length;
	}
	
	public Connection getInput(int node) {
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
	
	public Connection getOutput(int node) {
		return this.outputs[node];
	}
	
	public Connection[] getOutputs() {
		return this.outputs;
	}
	
	public boolean isOutput() {
		return this.outputs.length == 0;
	}
	
	public void addOutput(Connection output) {
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
				//content += this.inputs[i].getResult();
			}
			this.content = (1 / (1 + Math.pow(Math.E, (-1 * content))));
		}
	}
}