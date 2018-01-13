package BNN;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;


public class NeuralNet {
	private Node[][] nodes;
	private int[] dimensions;

	public NeuralNet(int[] dimensions) throws IllegalArgumentException {
		if (dimensions == null) {
			throw new IllegalArgumentException("Dimensions can not be null.");
		}
		if (dimensions.length < 2) {
			throw new IllegalArgumentException("There have to be atleast 2 dimensions.");
		}
		this.dimensions = dimensions;
		Node[][] nodes = new Node[this.dimensions.length][];
		for (int i=nodes.length-1;i>=0;i--) {
			if (this.dimensions[i] <= 0) {
				throw new IllegalArgumentException("Dimensions have to be larger than 0.");
			}
			nodes[i] = new Node[this.dimensions[i]];
		}
		for (int i=0;i<nodes.length;i++) {
			for (int j=nodes[i].length-1;j>=0;j--) {
				nodes[i][j] = new Node(this, (i!=0 ? nodes[i-1] : new Node[0]));
			}
		}
		this.nodes = nodes;
	}

	NeuralNet(boolean p, int[] dimensions) {
		this.dimensions = dimensions;
	}

	void setNodes(Node[][] nodes) {
		this.nodes = nodes;
	}

	public int[] getDimensions() {
		return this.dimensions;
	}

	public Node getNode(int layer, int node) throws IllegalArgumentException {
		if (layer < 0 || layer >= this.nodes.length) {
			throw new IllegalArgumentException("Layer does not exist.");
		}
		if (node < 0 || node >= this.nodes[layer].length) {
			throw new IllegalArgumentException("Node does not exist.");
		}
		return this.nodes[layer][node];
	}

	public void setInput(int node, double input) throws IllegalArgumentException {
		if (node < 0 || node >= this.nodes[0].length) {
			throw new IllegalArgumentException("Node does not exist.");
		}
		if (input < -1 || input > 1) {
			throw new IllegalArgumentException("Input has to be between -1 and 1");
		}
		this.nodes[0][node].setContent(input);
	}

	public double getOutput(int node) throws IllegalArgumentException {
		if (node < 0 || node >= this.nodes[this.nodes.length-1].length) {
			throw new IllegalArgumentException("Node does not exist.");
		}
		return this.nodes[this.nodes.length-1][node].getContent();
	}

	public void setInputs(double[] inputs) throws IllegalArgumentException {
		if (inputs == null) {
			throw new IllegalArgumentException("Inputs can not be null.");
		}
		if (inputs.length != this.nodes[0].length ) {
			throw new IllegalArgumentException("The number of inputs has to match the number of input-nodes");
		}
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

	public void processInputs(double[] inputs) throws IllegalArgumentException {
		setInputs(inputs);
		process();
	}

	public double[] getOutputsForInputs(double[] inputs) throws IllegalArgumentException {
		setInputs(inputs);
		process();
		return getOutputs();
	}
	
	public void backpropagate2(double learningRate, double momentum, double[] idealOutputs) throws IllegalArgumentException {
		if (idealOutputs == null) {
			throw new IllegalArgumentException("Ideal outputs can not be null.");
		}
		if (idealOutputs.length != this.nodes[this.nodes.length-1].length ) {
			throw new IllegalArgumentException("The number of expected outputs has to match the number of output-nodes.");
		}
		int outputLayerLength = this.dimensions[this.dimensions.length-1];
		for (int i=outputLayerLength-1;i>=0;i--) {
			Connection[] inputs = this.nodes[this.dimensions.length-1][i].getInputs();
			for (int j=inputs.length-1;j>=0;j--) {
				//errorfunction = each * tiny * step * forward;
			}
		}
	}

	public void backpropagate(double learningRate, double momentum, double[] idealOutputs) throws IllegalArgumentException {
		if (idealOutputs == null) {
			throw new IllegalArgumentException("Ideal outputs can not be null.");
		}
		if (idealOutputs.length != this.nodes[this.nodes.length-1].length ) {
			throw new IllegalArgumentException("The number of expected outputs has to match the number of output-nodes.");
		}
		int outputLayerLength = this.dimensions[this.dimensions.length-1];
		for (int i=outputLayerLength-1;i>=0;i--) {
			Node node = this.nodes[this.dimensions.length-1][i];
			Connection[] inputs = node.getInputs();
			for (int j=inputs.length-1;j>=0;j--) {
				if (idealOutputs[i] < -1 || idealOutputs[0] > 1) {
					throw new IllegalArgumentException("Ideal outputs have to be between -1 and 1.");
				}
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
					sumErrorOutputs += (-(idealOutputs[l] - node.getContent()) * Functions.sigmoidDer(node.getContent())
							* this.nodes[i][j].getOutput(l).getWeight()/*works only with one hidden layer*/);
				}
				for (int k=inputs.length-1;k>=0;k--) {
					inputs[k].calculateDeltaWeight(learningRate, sumErrorOutputs);
					inputs[k].calculateWeight(momentum);
				}
			}
		}
	}

	public void train(double learningRate, double momentum, TrainingData td) throws IllegalArgumentException {
		if (td == null) {
			throw new IllegalArgumentException("Training data can not be null");
		}
		if (td.getInputs().length != this.nodes[0].length || td.getOutputs().length != this.nodes[this.nodes.length-1].length) {
			throw new IllegalArgumentException("Training data has to have the same dimensions as the network.");
		}
		this.setInputs(td.getInputs());
		this.process();
		this.backpropagate(learningRate, momentum, td.getOutputs());
		td.increaseTimesTrained();
	}

	public void trainSet(double learningRate, double momentum, TrainingData[] td, int iterations) throws IllegalArgumentException {
		if (td == null) {
			throw new IllegalArgumentException("Training data can not be null.");
		}
		if (iterations < 1) {
			throw new IllegalArgumentException("Iterations have to be larger than 0.");
		}
		while (iterations-- > 0) {
			train(learningRate, momentum, td[(int)(Math.random()*(td.length))]);
		}
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
	
	public void test(TrainingData[] td, boolean absolute) {
		double error = 0;
		for (int i=0;i<td.length;i++) {
			double[] outputs = getOutputsForInputs(td[i].getInputs());
			double[] expectedOutputs = td[i].getOutputs();
			double errorTd = 0;
			for (int j=outputs.length-1;j>=0;j--) {
				errorTd += Math.abs(absolute ? ((outputs[j] > 0.5 ? 1 : 0) - (expectedOutputs[j] > 0.5 ? 1 : 0))
											 : (outputs[j] - expectedOutputs[j]));
			}
			errorTd /= outputs.length;
			System.out.println("TrainingData " + StringFormat.dec(i+1, (""+(td.length)).length())
					+ " Error: " + StringFormat.dec(errorTd, (""+(outputs.length-1)).length(), 4, false, ' '));
			error += errorTd;
		}
		System.out.println("===============================\nError Average: " 
					+ StringFormat.dec(error/(td.length), 1, 4, false, ' '));
	}

	public void safeToFile(String file, boolean overwrite) throws IOException {
		RandomAccessFile stream = new RandomAccessFile(file, "rw");
		FileChannel channel = stream.getChannel();
		FileLock lock = null;
		boolean abort;
		try {
			lock = channel.tryLock();
		} catch (OverlappingFileLockException e) {
			stream.close();
			channel.close();
			throw new IOException("Network has not been saved, file is locked");
		}
		if (overwrite) {
			stream.setLength(0);
		}
		stream.seek(stream.length());
		stream.writeDouble(this.dimensions.length);
		for (int i=0;i<this.dimensions.length;i++) {
			stream.writeDouble(this.dimensions[i]);
		}
		for (int i=0;i<this.nodes.length;i++) {
			for (int j=this.nodes[i].length-1;j>=0;j--) {
				Connection[] inputs = this.nodes[i][j].getInputs();
				stream.writeDouble(inputs.length);
				for (int k=0;k<inputs.length;k++) {
					abort = false;
					for (int l=0;l<this.nodes.length && !abort;l++) {
						for (int m=0;m<this.nodes[l].length && !abort;m++) {
							if (this.nodes[l][m] == inputs[k].getChild()) {
								stream.writeDouble(l);
								stream.writeDouble(m);
								abort = true;
							}
						}
					}
					stream.writeDouble(inputs[k].getWeight());
					stream.writeDouble(inputs[k].getDeltaWeight());
					stream.writeDouble(inputs[k].getPrevDeltaWeight());
				}
			}
		}
		lock.release();
		stream.close();
		channel.close();
	}

	public static NeuralNet loadFromFile(String file) throws IOException {
		NeuralNet nn;
		RandomAccessFile stream = new RandomAccessFile(file, "r");
		FileChannel channel = stream.getChannel();
		int[] dimensions = new int[(int)stream.readDouble()];
		for (int i=0;i<dimensions.length;i++) {
			dimensions[i] = (int)stream.readDouble();
		}
		nn = new NeuralNet(false, dimensions);
		Node[][] nodes = new Node[dimensions.length][];
		for (int i=0;i<dimensions.length;i++) {
			nodes[i] = new Node[dimensions[i]];
			for (int j=dimensions[i]-1;j>=0;j--) {
				nodes[i][j] = new Node(false, nn);
				Connection[] inputs = new Connection[(int)stream.readDouble()];
				for (int k=0;k<inputs.length;k++) {
					int layer = (int)stream.readDouble();
					int node = (int)stream.readDouble();
					double weight = stream.readDouble();
					double deltaWeight = stream.readDouble();
					double prevDeltaWeight = stream.readDouble();
					inputs[k] = new Connection(nodes[layer][node], nodes[i][j], weight, deltaWeight, prevDeltaWeight);
				}
				nodes[i][j].setInputs(inputs);
			}
		}
		nn.setNodes(nodes);
		stream.close();
		channel.close();
		return nn;
	}

	public void dumpInfo() {
		for (int i=0;i<this.nodes.length;i++) {
			for (int j=0;j<this.nodes[i].length;j++) {
				Connection[] inputs = this.nodes[i][j].getInputs();
				Connection[] outputs = this.nodes[i][j].getOutputs();
				System.out.println("node["+i+"]["+j+"]: ");
				for (int k=0;k<inputs.length;k++) {
					Node c = inputs[k].getChild();
					Node p = inputs[k].getParent();
					int cx = 0, cy = 0, px = 0, py = 0;
					for (int l=0;l<this.nodes.length;l++) {
						for (int m=0;m<this.nodes[l].length;m++) {
							if (this.nodes[l][m] == c) {
								cx = l; cy = m;
							}
							if (this.nodes[l][m] == p) {
								px = l; py = m;
							}
						}
					}
					System.out.println("I: c["+cx+"]["+cy+"], p["+px+"]["+py+"], " + inputs[k].getWeight() + ", " + inputs[k].getDeltaWeight() + ", " + inputs[k].getPrevDeltaWeight());
				}
				for (int k=0;k<outputs.length;k++) {
					Node c = outputs[k].getChild();
					Node p = outputs[k].getParent();
					int cx = 0, cy = 0, px = 0, py = 0;
					for (int l=0;l<this.nodes.length;l++) {
						for (int m=0;m<this.nodes[l].length;m++) {
							if (this.nodes[l][m] == c) {
								cx = l; cy = m;
							}
							if (this.nodes[l][m] == p) {
								px = l; py = m;
							}
						}
					}
					System.out.println("O: c["+cx+"]["+cy+"], p["+px+"]["+py+"], " + outputs[k].getWeight() + ", " + outputs[k].getDeltaWeight() + ", " + outputs[k].getPrevDeltaWeight());
				}
			}
		}
	}
}
