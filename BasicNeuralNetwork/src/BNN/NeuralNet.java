package BNN;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Arrays;

/***
 * This class represents a basic neural network.<br>
 * It contains methods to train a network of variable sizes with different costfunctions.
 * It is also possible to monitor various parts of the training process.
 *
 * @author Mario Schaeper
 */
public class NeuralNet {
	protected static final int INPUT = 0;
	protected static final int OUTPUT = 1;
	protected int[] sizes;
	protected double[][] biases;
	protected double[][][] weights;
	protected boolean monitorTrainingCost = false;
	protected boolean monitorTrainingAccuracy = false;
	protected boolean monitorEvaluationCost = false;
	protected boolean monitorEvaluationAccuracy = true;

	/***
	 * Abstract class for costfunction classes to inherit from.
	 *
	 * @author Mario Schaeper
	 */
	protected interface CostFunction {
		public abstract double[] delta(double[] outputActivations, double[] idealOutput, double[] z);
		public abstract double fn(double[] outputActivations, double[] idealOutput);
	}

	/***
	 * Quadratic costfunction class. <br>
	 * Standard function for simple Networks.
	 *
	 * @author schäper
	 */
	public final class Quadratic implements CostFunction {
		public double[] delta(double[] outputActivations, double[] idealOutput, double[] z) {
			return VecMath.multiply(VecMath.subtract(outputActivations, idealOutput), VecMath.sigmoidPrime(z));
		}
		public double fn(double[] outputActivations, double[] idealOutput) {
			return 0.5*VecMath.norm(VecMath.subtract(outputActivations, idealOutput));
		}
	}

	/***
	 * Cross-entropy costfunction class. <br>
	 * Improves the learningrate for very high differences between neuron outputs and ideal-values,
	 * making it the better choice most of the time.
	 *
	 * @author Mario Schaeper
	 */
	public final class CrossEntropy implements CostFunction {
		public double[] delta(double[] outputActivations, double[] idealOutput, double[] z) {
			return VecMath.subtract(outputActivations, idealOutput);
		}
		public double fn(double[] outputActivations, double[] idealOutput) {
			return VecMath.sum(VecMath.subtract(VecMath.multiply(VecMath.multiply(idealOutput, -1),
					VecMath.log(outputActivations)), VecMath.multiply(VecMath.add(VecMath.multiply(outputActivations, -1), 1),
					VecMath.log(VecMath.add(VecMath.multiply(outputActivations, -1), 1)))));
		}
	}

	/**
	 * Creates a Neural Net with the given sizes. Biases have to match the values
	 * in the size array without the input nodes and weights are the matrices between the layers.<br>
	 * <em>e.g. size = {10, 6, 2}, biases = {double[6], double[2]}, weights = {double[10][6], double[6][2]}</em>
	 *
	 * @param size Array of sizes for each layer
	 * @param biases Pre set biases
	 * @param weights Pre set weights
	 */
	public NeuralNet(int[] size, double[][] biases, double[][][] weights) {
		if (size.length < 1) {
			throw new IllegalArgumentException("No size values given");
		}
		if (Arrays.stream(size).anyMatch(i -> i<=0)) {
			throw new IllegalArgumentException("Size values have to be positive");
		}
		if (size.length != biases.length+1) {
			throw new IllegalArgumentException("Bias length does not match size length");
		}
		if (size.length != weights.length+1) {
			throw new IllegalArgumentException("Weight length does not match size length");
		}
		this.sizes = new int[size.length];
		System.arraycopy(size, 0, this.sizes, 0, size.length);
		this.biases = new double[this.sizes.length-1][];
		this.weights = new double[this.sizes.length-1][][];
		for (int i=this.sizes.length-1;i>0;i--) {
			if (size[i] != biases[i-1].length) {
				throw new IllegalArgumentException("Biases lenght does not match sizes in layer " + (i-1));
			}
			if (size[i] != weights[i-1].length) {
				throw new IllegalArgumentException("Weights length does not match sizes in layer " + (i-1));
			}
			this.biases[i-1] = new double[biases[i-1].length];
			System.arraycopy(biases[i-1], 0, this.biases[i-1], 0, biases[i-1].length);
			this.weights[i-1] = new double[this.sizes[i]][];
			for (int j=this.sizes[i]-1;j>=0;j--) {
				if (size[i-1] != weights[i-1][j].length) {
					throw new IllegalArgumentException("Weights length does not match sizes in layer " + i + " for neuron " + j);
				}
				this.weights[i-1][j] = new double[weights[i-1][j].length];
				System.arraycopy(weights[i-1][j], 0, this.weights[i-1][j], 0, weights[i-1][j].length);
			}
		}
	}

	/**
	 * Creates a Neural Net with the given sizes. Biases and are initialized randomly between -1 an 1.
	 * Weights are distributed evenly across each layer.
	 *
	 * @param size Array of sizes for each layer
	 */
	public NeuralNet(int...size) {
		if (size.length < 1) {
			throw new IllegalArgumentException("No size values given");
		}
		if (Arrays.stream(size).anyMatch(i -> i<=0)) {
			throw new IllegalArgumentException("Size values have to be positive");
		}
		this.sizes = new int[size.length];
		System.arraycopy(size, 0, this.sizes, 0, size.length);
		this.biases = new double[this.sizes.length-1][];
		this.weights = new double[this.sizes.length-1][][];
		for (int i=this.sizes.length-1;i>0;i--) {
			this.biases[i-1] = new double[this.sizes[i]];
			this.weights[i-1] = new double[this.sizes[i]][];
			for (int j=this.sizes[i]-1;j>=0;j--) {
				this.biases[i-1][j] = 2*Math.random()-1;
				this.weights[i-1][j] = new double[this.sizes[i-1]];
				for (int k=this.sizes[i-1]-1;k>=0;k--) {
					this.weights[i-1][j][k] = (2*Math.random()-1)/Math.sqrt(this.sizes[i-1]);
				}
			}
		}
	}

	/**
	 * Returns the amount of layers, including input and output.
	 *
	 * @return amount of layers
	 */
	public int getLayerAmount() {
		return this.sizes.length;
	}

	/**
	 * Returns the amount of neurons in the given layer.
	 *
	 * @param layer the layer
	 * @return amount of neurons
	 */
	public int getLayerSize(int layer) {
		if (layer < 0 || layer >= this.sizes.length) {
			throw new IllegalArgumentException("Layer " + layer + " does not exist");
		}
		return this.sizes[layer];
	}

	/**
	 * Returns the amount of neurons in the input layer.
	 *
	 * @return input neurons
	 */
	public int getInputSize() {
		return this.sizes[0];
	}

	/**
	 * Returns the amount of neurons in the output layer.
	 *
	 * @return output neurons
	 */
	public int getOutputSize() {
		return this.sizes[this.sizes.length-1];
	}

	/**
	 * Returns a bias of a specific neuron.
	 *
	 * @param layer the layer of the neuron
	 * @param neuron the neuron in the layer
	 * @return the bias of the described neuron
	 */
	public double getBias(int layer, int neuron) {
		if (layer < 0 || layer >= this.sizes.length) {
			throw new IllegalArgumentException("Layer " + layer + " does not exist");
		}
		if (neuron < 0 || neuron >= this.sizes[layer]) {
			throw new IllegalArgumentException("Neuron " + neuron + " does not exist in layer " + layer);
		}
		return this.biases[layer][neuron];
	}

	/**
	 * Returns a weight of a connection between two neurons.
	 * The Layer has to be the one, in which the receiving neuron is.
	 *
	 * @param layer the layer of the neuron the connection is to
	 * @param neuron the neuron in the layer
	 * @param neuronFrom the neuron of the previous layer, where the connection comes from
	 * @return the weight of the described connection
	 */
	public double getWeight(int layer, int neuron, int neuronFrom) {
		if (layer <= 0 || layer > this.sizes.length) {
			throw new IllegalArgumentException("Layer " + layer + " does not exist or has no connections to previous neurons");
		}
		if (neuron <= 0 || neuron > this.sizes[layer]) {
			throw new IllegalArgumentException("Neuron " + neuron + " does not exist in layer " + layer);
		}
		if (neuronFrom <= 0 || neuron > this.sizes[layer-1]) {
			throw new IllegalArgumentException("Neuron " + neuronFrom + " does not exist in layer " + (layer-1));
		}
		return this.weights[layer-1][neuron-1][neuronFrom-1];
	}

	/**
	 * Changes the monitoring properties. Every state set to <b>true</b> will cause
	 * training methods to print the according values after each iteration.
	 *
	 * @param trainingCost state of monitoring the training cost
	 * @param trainingAccuracy state of monitoring the training accuracy
	 * @param evaluationCost state of monitoring the evaluation cost
	 * @param evaluationAccuracy state of monitoring the evaluation accuracy
	 */
	public void setMonitoring(boolean trainingCost, boolean trainingAccuracy, boolean evaluationCost, boolean evaluationAccuracy) {
		this.monitorTrainingCost = trainingCost;
		this.monitorTrainingAccuracy = trainingAccuracy;
		this.monitorEvaluationCost = evaluationCost;
		this.monitorEvaluationAccuracy = evaluationAccuracy;
	}

	/**
	 * Processes the given inputs through the network and returns the outputs.
	 *
	 * @param input array of values used as input for the network
	 * @return array of the network outputs
	 */
	public double[] feedForward(double[] input) {
		if (input.length != this.sizes[0]) {
			throw new IllegalArgumentException("Input length does not match the amount of input-neurons");
		}
		double[] ret = new double[input.length];
		System.arraycopy(input, 0, ret, 0, input.length);
		for (int i=0;i<this.sizes.length-1;i++) {
			ret = VecMath.sigmoid(VecMath.add(VecMath.dot(this.weights[i], ret), this.biases[i]));
		}
		return ret;
	}

	protected void stochasticGradientDescentCheckArguments(double[][][] trainingData, int epochs, int miniBatchSize,
			double learningRate, double regularization, CostFunction costFunction,
			double[][][] testData) throws IllegalArgumentException {
		stochasticGradientDescentCheckArguments(trainingData, epochs, miniBatchSize, learningRate, regularization, costFunction);
		if (testData.length <= 0) {
			throw new IllegalArgumentException("TestData can not be empty");
		}
		for (int i=testData.length-1;i>=0;i--) {
			if (testData[i].length != 2) {
				throw new IllegalArgumentException("Wrong structure for testData");
			}
			if (testData[i][INPUT].length != this.sizes[0]) {
				throw new IllegalArgumentException("Input size does not match amount of input-neurons in testData set " + i);
			}
			if (testData[i][OUTPUT].length != this.sizes[this.sizes.length-1]) {
				throw new IllegalArgumentException("Output size does not match amount of output-neurons in testData set " + i);
			}
		}
	}

	protected void stochasticGradientDescentCheckArguments(double[][][] trainingData, int epochs, int miniBatchSize,
			double learningRate, double regularization, CostFunction costFunction) throws IllegalArgumentException {
		if (epochs <= 0) {
			throw new IllegalArgumentException("Epochs has to be positive");
		}
		if (miniBatchSize <= 0) {
			throw new IllegalArgumentException("MiniBatchSize has to be positive");
		}
		if (costFunction == null) {
			throw new IllegalArgumentException("CostFunction can not be null");
		}
		if (trainingData.length <= 0) {
			throw new IllegalArgumentException("TrainingData can not be empty");
		}
		for (int i=trainingData.length-1;i>=0;i--) {
			if (trainingData[i].length != 2) {
				throw new IllegalArgumentException("Wrong structure for trainingData");
			}
			if (trainingData[i][INPUT].length != this.sizes[0]) {
				throw new IllegalArgumentException("Input size does not match amount of input-neurons in trainingData set " + i);
			}
			if (trainingData[i][OUTPUT].length != this.sizes[this.sizes.length-1]) {
				throw new IllegalArgumentException("Output size does not match amount of output-neurons in trainingData set " + i);
			}
		}
	}

	/**
	 * Trains the network by using the stochastic gradient descent.<br>
	 * The training data has to consist of trainingsets, meaning arrays with an array of inputs
	 * in index 0 and an array of desired outputs in index 1.<br>This method groups trainingssets in
	 * mini-batches, which are evaluated after all sets and ragularization functions to reduce overfitting.<br>
	 * The costfunction has to be an instance of one of the local classes. The test data parameter should be
	 * alternative training data. The training will be evaluated on how good the results on the test data are, while
	 * training with the training data. For this reason the test data should not be the training data.<br><br>
	 * <b>Pick these parameters with caution!</b> If one is off by too much the entire network might missfunction. This
	 * beeing said, the situation is different for every scenario. It takes time and experience to figure out how to set
	 * them.<br>These values might make a good start:<ul><li>epochs: 15</li><li>miniBatchSize: 10</li><li>learningRate: 0.1
	 * </li><li>regularization: 5</li><li>costFunction: {@link BNN.NeuralNet.CrossEntropy CrossEntropy}</li></ul>
	 *
	 * @param trainingData array of trainingsets
	 * @param epochs the amount of repetitions for the entire trainingData
	 * @param miniBatchSize the amount of trainingSets trained before backpropagating
	 * @param learningRate determines the speed with which the weights are adjusted
	 * @param regularization defines how much small weights are prefered
	 * @param costFunction the costfunction to use
	 * @param testData data to test and evaluate the network on
	 */
	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize,
			double learningRate, double regularization, CostFunction costFunction, double[][][] testData) {
		stochasticGradientDescentCheckArguments(trainingData, epochs, miniBatchSize, learningRate, regularization, costFunction, testData);
		double startTime = System.nanoTime();
		double[] trainingCost = new double[epochs];
		int[] trainingAccuracy = new int[epochs];
		double[] evaluationCost = new double[epochs];
		int[] evaluationAccuracy = new int[epochs];
		for (int i=0;i<epochs;i++) {
			this.stochasticGradientDescentInner(trainingData, miniBatchSize, learningRate, regularization, costFunction);
			System.out.print(String.format("\nEpoch %3d complete", i+1));
			if (monitorTrainingCost) {
				trainingCost[i] = this.totalCost(trainingData, regularization, costFunction);
				System.out.print(String.format("\n   Cost on training data:       %f", trainingCost[i]));
			}
			if (this.monitorTrainingAccuracy) {
				trainingAccuracy[i] = this.accuracy(trainingData);
				System.out.print(String.format("\n   Accuracy on training data:   %8d / %8d", trainingAccuracy[i], trainingData.length));
			}
			if (this.monitorEvaluationCost) {
				evaluationCost[i] = this.totalCost(testData, regularization, costFunction);
				System.out.print(String.format("\n   Cost on evaluation data:     %f", evaluationCost[i]));
			}
			if (this.monitorEvaluationAccuracy) {
				evaluationAccuracy[i] = this.accuracy(testData);
				System.out.print(String.format("\n   Accuracy on evaluation data: %8d / %8d", evaluationAccuracy[i], testData.length));
			}
			System.out.println();
		}
		System.out.println(String.format("finished training in %5.8f seconds", (System.nanoTime()-startTime)/1000000000));
	}

	/**
	 * Trains the network by using the stochastic gradient descent.<br>
	 * The training data has to consist of trainingsets, meaning arrays with an array of inputs
	 * in index 0 and an array of desired outputs in index 1.<br>This method groups trainingssets in
	 * mini-batches, which are evaluated after all sets and ragularization functions to reduce overfitting.<br>
	 * The costfunction has to be an instance of one of the local classes.<br><br>
	 * <b>Pick these parameters with caution!</b> If one is off by too much the entire network might missfunction. This
	 * beeing said, the situation is different for every scenario. It takes time and experience to figure out how to set
	 * them.<br>These values might make a good start:<ul><li>epochs: 15</li><li>miniBatchSize: 10</li><li>learningRate: 0.1
	 * </li><li>regularization: 5</li><li>costFunction: {@link BNN.NeuralNet.CrossEntropy CrossEntropy}</li></ul>
	 *
	 * @param trainingData array of trainingsets
	 * @param epochs the amount of repetitions for the entire trainingData
	 * @param miniBatchSize the amount of trainingSets trained before backpropagating
	 * @param learningRate determines the speed with which the weights are adjusted
	 * @param regularization defines how much small weights are prefered
	 * @param costFunction the costfunction to use
	 */
	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize,
			double learningRate, double regularization, CostFunction costFunction) {
		stochasticGradientDescentCheckArguments(trainingData, epochs, miniBatchSize, learningRate, regularization, costFunction);
		if (epochs <= 0) {
			throw new IllegalArgumentException("Epochs has to be positive");
		}
		if (miniBatchSize <= 0) {
			throw new IllegalArgumentException("MiniBatchSize has to be positive");
		}
		if (costFunction == null) {
			throw new IllegalArgumentException("CostFunction can not be null");
		}
		double startTime = System.nanoTime();
		double[] trainingCost = new double[epochs];
		int[] trainingAccuracy = new int[epochs];
		for (int i=0;i<epochs;i++) {
			this.stochasticGradientDescentInner(trainingData, miniBatchSize, learningRate, regularization, costFunction);
			System.out.print(String.format("\nEpoch %3d complete", i+1));
			if (this.monitorTrainingCost) {
				trainingCost[i] = this.totalCost(trainingData, regularization, costFunction);
				System.out.print(String.format("\n   Cost on training data:       %f", trainingCost[i]));
			}
			if (this.monitorTrainingAccuracy) {
				trainingAccuracy[i] = this.accuracy(trainingData);
				System.out.print(String.format("\n   Accuracy on training data:   %8d / %8d", trainingAccuracy[i], trainingData.length));
			}
		}
		System.out.println(String.format("finished training in %5.8f seconds", (System.nanoTime()-startTime)/1000000000));
	}

	/**
	 * Trains the network by using the stochastic gradient descent.<br>
	 * The training data has to consist of trainingsets, meaning arrays with an array of inputs
	 * in index 0 and an array of desired outputs in index 1.<br>This method groups trainingssets in
	 * mini-batches, which are evaluated after all sets and ragularization functions to reduce overfitting.<br>
	 * The test data parameter should be alternative training data. The training will be evaluated on how good the
	 * results on the test data are, while training with the training data. For this reason the test data should
	 * not be the training data.<br><br><b>Pick these parameters with caution!</b> If one is off by too much the
	 * entire network might missfunction. This beeing said, the situation is different for every scenario.
	 * It takes time and experience to figure out how to set them.<br>These values might make a good start:
	 * <ul><li>epochs: 15</li><li>miniBatchSize: 10</li><li>learningRate: 0.1</li><li>regularization: 5</li></ul>
	 *
	 * @param trainingData array of trainingsets
	 * @param epochs the amount of repetitions for the entire trainingData
	 * @param miniBatchSize the amount of trainingSets trained before backpropagating
	 * @param learningRate determines the speed with which the weights are adjusted
	 * @param regularization defines how much small weights are prefered
	 * @param testData data to test and evaluate the network on
	 */
	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize,
			double learningRate, double regularization, double[][][] testData) {
		this.stochasticGradientDescent(trainingData, epochs, miniBatchSize,
				learningRate, regularization, new CrossEntropy(), testData);
	}

	/**
	 * Trains the network by using the stochastic gradient descent.<br>
	 * The training data has to consist of trainingsets, meaning arrays with an array of inputs
	 * in index 0 and an array of desired outputs in index 1.<br>This method groups trainingssets in
	 * mini-batches, which are evaluated after all sets.<br>The test data parameter should be alternative
	 * training data. The training will be evaluated on how good the results on the test data are, while
	 * training with the training data. For this reason the test data should not be the
	 * training data.<br><br><b>Pick these parameters with caution!</b> If one is off by too much the
	 * entire network might missfunction. This beeing said, the situation is different for every scenario.
	 * It takes time and experience to figure out how to set them.<br>These values might make a good start:
	 * <ul><li>epochs: 15</li><li>miniBatchSize: 10</li><li>learningRate: 0.1</li></ul>
	 *
	 * @param trainingData array of trainingsets
	 * @param epochs the amount of repetitions for the entire trainingData
	 * @param miniBatchSize the amount of trainingSets trained before backpropagating
	 * @param learningRate determines the speed with which the weights are adjusted
	 * @param regularization defines how much small weights are prefered
	 */
	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize,
			double regularization, double learningRate) {
		this.stochasticGradientDescent(trainingData, epochs, miniBatchSize, learningRate, regularization, new CrossEntropy());
	}

	protected void stochasticGradientDescentInner(double[][][] trainingData, int miniBatchSize,
			double learningRate, double regularization, CostFunction costFunction) {
		double[][][] newData = new double[trainingData.length][][];
		for (int j=trainingData.length-1;j>=0;j--) {
			newData[j] = VecMath.add(trainingData[j], 0);
		}
		for (int j=newData.length-1;j>=0;j--) {
			int swapWith = (int)Math.random()*(newData.length-1);
			double[][] temp = newData[j];
			newData[j] = newData[swapWith];
			newData[swapWith] = temp;
		}
		int j = 0;
		while (j<trainingData.length) {
			int finalMiniBatchSize = Math.min(miniBatchSize, trainingData.length-j);
			double[][][] miniBatch = new double[finalMiniBatchSize][][];
			System.arraycopy(newData, j, miniBatch, 0, finalMiniBatchSize);
			this.updateMiniBatch(miniBatch, learningRate, regularization, trainingData.length, costFunction);
			j+=finalMiniBatchSize;
		}
	}

	protected void updateMiniBatch(double[][][] miniBatch, double learningRate,
			double regularization, double trainingDataLength, CostFunction costFunction) {
		double[][] nablaBiases = new double[this.biases.length][];
		double[][][] nablaWeights = new double[this.weights.length][][];
		nablaBiases = VecMath.multiply(this.biases, 0);
		for (int i=nablaWeights.length-1;i>=0;i--) {
			nablaWeights[i] = VecMath.multiply(this.weights[i], 0);
		}
		for (int i=miniBatch.length-1;i>=0;i--) {
			double[][][][] deltaNablas = this.backpropagate(miniBatch[i], costFunction);
			double[][] deltaNablaBiases = deltaNablas[0][0];
			double[][][] deltaNablaWeights = deltaNablas[1];
			nablaBiases = VecMath.add(nablaBiases, deltaNablaBiases);
			for (int j=nablaWeights.length-1;j>=0;j--) {
				nablaWeights[j] = VecMath.add(nablaWeights[j], deltaNablaWeights[j]);
			}
		}
		this.biases = VecMath.subtract(this.biases, VecMath.multiply(nablaBiases, learningRate/miniBatch.length));
		ArrayDebug.printArray(this.weights[this.weights.length-1]);
		for (int j=this.weights.length-1;j>=0;j--) {
			this.weights[j] = VecMath.multiply(VecMath.subtract(this.weights[j], VecMath.multiply(nablaWeights[j],
					learningRate/miniBatch.length)), 1-learningRate*(regularization/trainingDataLength));
		}
		ArrayDebug.printArray(this.weights[this.weights.length-1]);
	}

	protected double[][][][] backpropagate(double[][] touple, CostFunction costFunction) {
		double[][] nablaBiases = new double[this.biases.length][];
		double[][][] nablaWeights = new double[this.weights.length][][];
		double[][] zVecs = new double[this.sizes.length-1][];
		double[][] activations = new double[this.sizes.length][];
		double[] activation = touple[INPUT];
		double[] delta;
		double[] z;
		activations[0] = activation;
		for (int i=1;i<this.sizes.length;i++) {
			z = VecMath.add(VecMath.dot(this.weights[i-1], activation), this.biases[i-1]);
			zVecs[i-1] = z;
			activation = VecMath.sigmoid(z);
			activations[i] = activation;
		}
		delta = costFunction.delta(activations[activations.length-1], touple[OUTPUT], zVecs[zVecs.length-1]);
		nablaBiases[nablaBiases.length-1] = delta;
		nablaWeights[nablaWeights.length-1] = VecMath.dot(delta, activations[activations.length-2]);
		for (int i=this.sizes.length-3;i>=0;i--) {
			z = zVecs[i];
			delta = VecMath.multiply(VecMath.dot(VecMath.transpose(this.weights[i+1]), delta), VecMath.sigmoidPrime(z));
			nablaBiases[i] = delta;
			nablaWeights[i] = VecMath.dot(delta, activations[i]);
		}
		double[][][][] ret = new double[2][][][];
		ret[0] = new double[1][][];
		ret[0][0] = nablaBiases;
		ret[1] = nablaWeights;
		return ret;
	}

	protected double totalCost(double[][][] data, double regularization, CostFunction costFunction) {
		double cost = 0;
		for (int i=data.length-1;i>=0;i--) {
			cost += costFunction.fn(this.feedForward(data[i][INPUT]), data[i][OUTPUT])/data.length;
		}
		double normSum = 0;
		for (int i=this.weights.length-1;i>=0;i--) {
			normSum += Math.pow(VecMath.norm(this.weights[i]), 2);
		}
		return cost+0.5*(regularization/data.length)*normSum;
	}

	protected int accuracy(double[][][] data) {
		double[][] results = new double [data.length][];
		int sum = 0;
		for (int i=data.length-1;i>=0;i--) {
			results[i] = this.feedForward(data[i][INPUT]);
			int highestResult = 0;
			int wanted = 0;
			for (int j=results[i].length-1;j>=0;j--) {
				highestResult = results[i][j] > results[i][highestResult] ? j : highestResult;
				wanted = data[i][OUTPUT][j] > data[i][OUTPUT][wanted] ? j : wanted;
			}
			sum += highestResult == wanted ? 1 : 0;
		}
		return sum;
	}

	/**
	 * Saves the network to a file.
	 *
	 * @param file path and filename to save to
	 * @param overwrite wether to overwrite an existing file
	 * @throws IOException if the file exists and is locked or an I/O error occours
	 */
	public void saveToFile(String file, boolean overwrite) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("File can not be null");
		}
		RandomAccessFile stream = new RandomAccessFile(file, "rw");
		FileChannel channel = stream.getChannel();
		FileLock lock = null;
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
		stream.writeDouble(this.sizes.length);
		stream.writeDouble(this.sizes[0]);
		for (int i=0;i<this.sizes.length-1;i++) {
			stream.writeDouble(this.sizes[i+1]);
			for (int j=0;j<this.sizes[i+1];j++) {
				stream.writeDouble(this.biases[i][j]);
				for (int k=0;k<this.sizes[i];k++) {
					stream.writeDouble(this.weights[i][j][k]);
				}
			}
		}
		lock.release();
		stream.close();
		channel.close();
	}

	/**
	 * Loads a network from a file.
	 *
	 * @param file path and name of the file to load
	 * @return the loaded {@link NeuralNet AdvancedNet} Object
	 * @throws IOException if a I/O error occours
	 */
	public static NeuralNet loadFromFile(String file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("File can not be null");
		}
		RandomAccessFile stream = new RandomAccessFile(file, "rw");
		FileChannel channel = stream.getChannel();
		int[] sizes = new int[(int)stream.readDouble()];
		double[][] biases = new double[sizes.length-1][];
		double[][][] weights = new double[sizes.length-1][][];
		sizes[0] = (int)stream.readDouble();
		for (int i=0;i<sizes.length-1;i++) {
			sizes[i+1] = (int)stream.readDouble();
			biases[i] = new double[sizes[i+1]];
			weights[i] = new double[sizes[i+1]][];
			for (int j=0;j<sizes[i+1];j++) {
				biases[i][j] = stream.readDouble();
				weights[i][j] = new double[sizes[i]];
				for (int k=0;k<sizes[i];k++) {
					weights[i][j][k] = stream.readDouble();
				}
			}
		}
		stream.close();
		channel.close();
		return new NeuralNet(sizes, biases, weights);
	}
}
