package BNN;

import BNN.layers.Layer;
import java.util.Arrays;

/***
 * This class represents a basic neural network.<br>
 * It contains methods to train a network of variable sizes with different costfunctions.
 * It is also possible to monitor various parts of the training process.
 *
 * @author Mario Schaeper
 */
public abstract class NeuralNet {
	protected static final int INPUT = 0;
	protected static final int OUTPUT = 1;
	protected Layer[] layers;
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
		this.layers = new Layer[size.length-1];
	}

	/**
	 * Returns the amount of layers, including input and output.
	 *
	 * @return amount of layers
	 */
	public int getLayerAmount() {
		return this.layers.length+1;
	}

	/**
	 * Returns the amount of neurons in the given layer.
	 *
	 * @param layer the layer
	 * @return amount of neurons
	 */
	public int getLayerSize(int layer) {
		if (layer < 0 || layer > this.layers.length) {
			throw new IllegalArgumentException("Layer " + layer + " does not exist");
		}
		return layer==0 ? this.layers[0].getInputs() : this.layers[layer-1].getNeurons();
	}

	/**
	 * Returns the amount of neurons in the input layer.
	 *
	 * @return input neurons
	 */
	public int getInputSize() {
		return this.layers[0].getInputs();
	}

	/**
	 * Returns the amount of neurons in the output layer.
	 *
	 * @return output neurons
	 */
	public int getOutputSize() {
		return this.layers[this.layers.length-1].getNeurons();
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
		if (input.length != this.layers[0].getInputs()) {
			throw new IllegalArgumentException("Input length does not match the amount of input-neurons");
		}
		double[] ret = new double[input.length];
		System.arraycopy(input, 0, ret, 0, input.length);
		for (int i=0;i<this.layers.length;i++) {
			ret = this.layers[i].feedForward(ret);
		}
		return ret;
	}
}
