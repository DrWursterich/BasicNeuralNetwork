package BNN.layers;

import BNN.VecMath;

public final class FullyConnected extends Layer {
	protected double[] biases;
	protected double[][] weights;

	public FullyConnected(int neurons, int previousNeurons) {
		super(neurons);
		if (previousNeurons <= 0) {
			throw new IllegalArgumentException("The amount of previous neurons has to be higher than 0");
		}
		this.biases = new double[this.neurons];
		this.weights = new double[this.neurons][];
		for (int i=this.weights.length-1;i>=0;i--) {
			this.biases[i] = 1;//Math.random()*2-1;
			this.weights[i] = new double[previousNeurons];
			for (int j=this.weights[i].length-1;j>=0;j--) {
				this.weights[i][j] = .5;//(Math.random()*2-1)/Math.sqrt(this.neurons);
			}
		}
	}

	public FullyConnected(int neurons, double[][] weights, double[] biases) {
		super(neurons);
		if (weights.length != this.neurons) {
			throw new IllegalArgumentException("The weight length does not match the amount of neurons");
		}
		if (weights.length != biases.length) {
			throw new IllegalArgumentException("The weight length does not match the biases length");
		}
		this.biases = new double[biases.length];
		System.arraycopy(biases, 0, this.biases, 0, biases.length);
		this.weights = new double[weights.length][];
		int previousNeurons = weights[0].length;
		for (int i=this.weights.length-1;i>=0;i--) {
			if (weights[i].length != previousNeurons) {
				throw new IllegalArgumentException("The amount of weights has to be equal for ever neuron");
			}
			this.weights[i] = new double[previousNeurons];
			System.arraycopy(weights[i], 0, this.weights[i], 0, previousNeurons);
		}
	}

	public double getBias(int neuron) {
		if (neuron < 0 || neuron >= this.neurons) {
			throw new IllegalArgumentException("Neuron " + neuron + " does not exist in layer " + this);
		}
		return this.biases[neuron];
	}

	public double getWeight(int neuron, int neuronFrom) {
		if (neuron < 0 || neuron >= this.neurons) {
			throw new IllegalArgumentException("Neuron " + neuron + " does not exist");
		}
		if (neuronFrom < 0 || neuron >= this.weights[0].length) {
			throw new IllegalArgumentException("No weight for neuron " + neuronFrom + " exist");
		}
		return this.weights[neuron][neuronFrom];
	}

	public double[] getBiases() {
		double[] ret = new double[this.biases.length];
		System.arraycopy(this.biases, 0, ret, 0, ret.length);
		return ret;
	}

	public double[][] getWeights() {
		double[][] ret = new double[this.biases.length][];
		for (int i=ret.length-1;i>=0;i--) {
			ret[i] = new double[this.weights[i].length];
			System.arraycopy(this.weights[i], 0, ret[i], 0, ret[i].length);
		}
		return ret;
	}

	public double[] getDelta(double[] previousDelta, double[] z) {
		return VecMath.multiply(VecMath.dot(VecMath.transpose(this.weights), previousDelta), z);
	}

	public double[] getZ(double[] activation) {
		return VecMath.add(VecMath.dot(this.weights, activation), this.biases);
	}

	public double getNorm() {
		return Math.pow(VecMath.norm(this.weights), 2);
	}

	@Override
	public int getInputs() {
		return this.weights[0].length;
	}

	@Override
	public double[] feedForward(double[] input) {
		if (input.length != this.weights[0].length) {
			throw new IllegalArgumentException("Input length does not match the amount of input-neurons");
		}
		return VecMath.sigmoid(VecMath.add(VecMath.dot(this.weights, input), this.biases));
	}

	@Override
	public void backpropagate(double[] nablaBiases, double[][] nablaWeights, double regulation) {
		this.biases = VecMath.subtract(this.biases, nablaBiases);
		this.weights = VecMath.multiply(VecMath.subtract(this.weights, nablaWeights), regulation);
	}
}