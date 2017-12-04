public class NeuralNet {
	private Node[][] nodes;
	private int[] dimensions;

	public NeuralNet(int[] dimensions) {
		this.dimensions = dimensions;
		Node[][] nodes = new Node[this.dimensions.length][];
		for (int i=nodes.length-1;i>=0;i--) {
			nodes[i] = new Node[this.dimensions[i]];
		}
		for (int i=0;i<nodes.length;i++) {
			for (int j=nodes[i].length-1;j>=0;j--) {
				nodes[i][j] = new Node(this, (i!=0 ? nodes[i-1] : new Node[0]));
			}
		}
		this.nodes = nodes;
	}

	public int[] getDimensions() {
		return this.dimensions;
	}

	public Node getNode(int layer, int node) {
		return this.nodes[layer][node];
	}

	public void setInput(int node, double input) {
		this.nodes[0][node].setContent(input);;
	}

	public double getOutput(int node) {
		return this.nodes[this.nodes.length-1][node].getContent();
	}

	public void setInputs(double[] inputs) {
		for (int i=this.nodes[0].length-1;i>=0;i--) {
			this.setInput(i, inputs[i]);
		}
	}

	public double[] getOutputs() {
		double[] outputs = new double[this.nodes[this.nodes.length-1].length];
		for (int i=this.nodes[this.nodes.length-1].length-1;i>=0;i--) {
			outputs[i] = this.nodes[this.nodes.length-1][i].getContent();
		}
		return outputs;
	}

	public void process() {
		for (int i=0;i<this.nodes.length;i++) {
			for (int j=this.nodes[i].length-1;j>=0;j--) {
				this.nodes[i][j].calculateContent();
			}
		}
	}

	public void processInputs(double[] inputs) {
		setInputs(inputs);
		process();
	}

	public double[] getOutputsForInputs(double[] inputs) {
		setInputs(inputs);
		process();
		return getOutputs();
	}

	public void backpropagate(double learningRate, double momentum, double[] idealOutputs) {
		int outputLayerLength = this.dimensions[this.dimensions.length-1];
		for (int i=outputLayerLength-1;i>=0;i--) {
			Node node = this.nodes[this.dimensions.length-1][i];
			Connection[] inputs = node.getInputs();
			for (int j=inputs.length-1;j>=0;j--) {
				inputs[j].calculateDeltaWeight(learningRate, idealOutputs[i] - node.getContent());
				inputs[j].calculateWeight(momentum);
			}
		}
		for (int i=this.nodes.length-2;i>=1;i--) {
			for (int j=this.nodes[i].length-1;j>=0;j--) {
				Connection[] inputs = this.nodes[i][j].getInputs();
				double sumErrorOutputs = 0;
				for (int l=outputLayerLength-1;l>=0;l--) {
					Node node = this.nodes[this.dimensions.length-1][l];
					sumErrorOutputs += (-(idealOutputs[l] - node.getContent()) * node.getContent()
							* (1 - node.getContent()) * this.nodes[i][j].getOutput(l).getWeight()/*works only with one hidden layer*/);
				}
				for (int k=inputs.length-1;k>=0;k--) {
					inputs[k].calculateDeltaWeight(learningRate, sumErrorOutputs);
					inputs[k].calculateWeight(momentum);
				}
			}
		}
	}

	public void train(double learningRate, double momentum, TrainingData td) {
		this.setInputs(td.getInputs());
		this.process();
		this.backpropagate(learningRate, momentum, td.getOutputs());
		td.increaseTimesTrained();
	}

	public void printNet() {
		int maxNodes = 0;
		for (int i=this.nodes.length-1;i>=0;i--) {
			maxNodes = (this.nodes[i].length > maxNodes ? this.nodes[i].length : maxNodes);
		}
		for (int i=0;i<maxNodes;i++) {
			for (int j=0;j<this.nodes.length;j++) {
				if (i < this.nodes[j].length) {
					System.out.print(StringFormat.dec(this.nodes[j][i].getContent(), 1, 2, true, ' ') + "  ");
				} else {
					System.out.print("       ");
				}
			}
			System.out.println();
		}
	}

	public void printIO() {
		for (int i=0;i<Math.max(this.nodes[0].length, this.nodes[this.nodes.length-1].length);i++) {
			for (int j=0;j<2;j++) {
				int layer = j * (this.nodes.length-1);
				if (i < this.nodes[layer].length) {
					System.out.print(StringFormat.dec(this.nodes[layer][i].getContent(), 1, 2, true, ' ') + "  ");
				} else {
					System.out.print("       ");
				}
			}
			System.out.println();
		}
	}
}
