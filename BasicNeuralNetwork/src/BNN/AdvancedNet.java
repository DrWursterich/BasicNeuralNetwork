package BNN;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

@SuppressWarnings("unused")
public class AdvancedNet {
	private static final int INPUT = 0;
	private static final int OUTPUT = 1;
	private int layers;
	private int[] sizes;
	private double[][] biases;
	private double[][][] weights;
	private boolean monitorTrainingCost = false;
	private boolean monitorTrainingAccuracy = false;
	private boolean monitorEvaluationCost = false;
	private boolean monitorEvaluationAccuracy = true;

	public AdvancedNet(int[] size, double[][] biases, double[][][] weights) {
		this.sizes = new int[size.length];
		System.arraycopy(size, 0, this.sizes, 0, size.length);
		this.layers = this.sizes.length;
		this.biases = new double[this.layers-1][];
		this.weights = new double[this.layers-1][][];
		for (int i=this.layers-1;i>0;i--) {
			this.biases[i-1] = new double[biases[i-1].length];
			System.arraycopy(biases[i-1], 0, this.biases[i-1], 0, biases[i-1].length);
			this.weights[i-1] = new double[this.sizes[i]][];
			for (int j=this.sizes[i]-1;j>=0;j--) {
				this.weights[i-1][j] = new double[weights[i-1][j].length];
				System.arraycopy(weights[i-1][j], 0, this.weights[i-1][j], 0, weights[i-1][j].length);
			}
		}
	}

	public AdvancedNet(int[] size) {
		this.sizes = new int[size.length];
		System.arraycopy(size, 0, this.sizes, 0, size.length);
		this.layers = this.sizes.length;
		this.biases = new double[this.layers-1][];
		this.weights = new double[this.layers-1][][];
		for (int i=this.layers-1;i>0;i--) {
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

	public void setMonitoring(boolean trainingCost, boolean trainingAccuracy, boolean evaluationCost, boolean evaluationAccuracy) {
		this.monitorTrainingCost = trainingCost;
		this.monitorTrainingAccuracy = trainingAccuracy;
		this.monitorEvaluationCost = evaluationCost;
		this.monitorEvaluationAccuracy = evaluationAccuracy;
	}

	public double[] feedForward(double[] input) {
		double[] ret = new double[input.length];
		System.arraycopy(input, 0, ret, 0, input.length);
		for (int i=0;i<this.layers-1;i++) {
			ret = this.sigmoid(this.add(this.dot(this.weights[i], ret), this.biases[i]));
		}
		return ret;
	}

	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize,
			double learningRate, double regularization, boolean crossEntropy, double[][][] testData) {
		double startTime = System.nanoTime();
		double[] trainingCost = new double[epochs];
		int[] trainingAccuracy = new int[epochs];
		double[] evaluationCost = new double[epochs];
		int[] evaluationAccuracy = new int[epochs];
		for (int i=0;i<epochs;i++) {
			this.stochasticGradientDescentInner(trainingData, miniBatchSize, learningRate, regularization, crossEntropy);
			System.out.print(String.format("\nEpoch %3d complete", i+1));
			if (monitorTrainingCost) {
				trainingCost[i] = this.totalCost(trainingData, regularization, crossEntropy);
				System.out.print(String.format("\n   Cost on training data:       %f", trainingCost[i]));
			}
			if (this.monitorTrainingAccuracy) {
				trainingAccuracy[i] = this.accuracy(trainingData);
				System.out.print(String.format("\n   Accuracy on training data:   %8d / %8d", trainingAccuracy[i], trainingData.length));
			}
			if (this.monitorEvaluationCost) {
				evaluationCost[i] = this.totalCost(testData, regularization, crossEntropy);
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

	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize,
			double learningRate, double regularization, boolean crossEntropy) {
		double startTime = System.nanoTime();
		double[] trainingCost = new double[epochs];
		int[] trainingAccuracy = new int[epochs];
		for (int i=0;i<epochs;i++) {
			this.stochasticGradientDescentInner(trainingData, miniBatchSize, learningRate, regularization, crossEntropy);
			System.out.print(String.format("\nEpoch %3d complete", i+1));
			if (this.monitorTrainingCost) {
				trainingCost[i] = this.totalCost(trainingData, regularization, crossEntropy);
				System.out.print(String.format("\n   Cost on training data:       %f", trainingCost[i]));
			}
			if (this.monitorTrainingAccuracy) {
				trainingAccuracy[i] = this.accuracy(trainingData);
				System.out.print(String.format("\n   Accuracy on training data:   %8d / %8d", trainingAccuracy[i], trainingData.length));
			}
		}
		System.out.println(String.format("finished training in %5.8f seconds", (System.nanoTime()-startTime)/1000000000));
	}

	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize,
			double learningRate, boolean crossEntropy, double[][][] testData) {
		this.stochasticGradientDescent(trainingData, epochs, miniBatchSize, learningRate, 0.0, crossEntropy, testData);
	}

	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize,
			double learningRate, boolean crossEntropy) {
		this.stochasticGradientDescent(trainingData, epochs, miniBatchSize, learningRate, 0.0, crossEntropy);
	}

	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize,
			double learningRate, double regularization, double[][][] testData) {
		this.stochasticGradientDescent(trainingData, epochs, miniBatchSize, learningRate, regularization, true, testData);
	}

	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize,
			double regularization, double learningRate) {
		this.stochasticGradientDescent(trainingData, epochs, miniBatchSize, learningRate, regularization, true);
	}

	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize,
			double learningRate, double[][][] testData) {
		this.stochasticGradientDescent(trainingData, epochs, miniBatchSize, learningRate, 0.0, false, testData);
	}

	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize, double learningRate) {
		this.stochasticGradientDescent(trainingData, epochs, miniBatchSize, learningRate, 0.0, false);
	}

	private void stochasticGradientDescentInner(double[][][] trainingData, int miniBatchSize,
			double learningRate, double regularization, boolean crossEntropy) {
		double[][][] newData = new double[trainingData.length][][];
		for (int j=trainingData.length-1;j>=0;j--) {
			newData[j] = this.add(trainingData[j], 0);
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
			this.updateMiniBatch(miniBatch, learningRate, regularization, trainingData.length, crossEntropy);
			j+=finalMiniBatchSize;
		}
	}

	private void updateMiniBatch(double[][][] miniBatch, double learningRate,
			double regularization, double trainingDataLength, boolean crossEntropy) {
		double[][] nablaBiases = new double[this.biases.length][];
		double[][][] nablaWeights = new double[this.weights.length][][];
		nablaBiases = this.multiply(this.biases, 0);
		for (int i=nablaWeights.length-1;i>=0;i--) {
			nablaWeights[i] = this.multiply(this.weights[i], 0);
		}
		for (int i=miniBatch.length-1;i>=0;i--) {
			double[][][][] deltaNablas = this.backpropagate(miniBatch[i], crossEntropy);
			double[][] deltaNablaBiases = deltaNablas[0][0];
			double[][][] deltaNablaWeights = deltaNablas[1];
			nablaBiases = this.add(nablaBiases, deltaNablaBiases);
			for (int j=nablaWeights.length-1;j>=0;j--) {
				nablaWeights[j] = this.add(nablaWeights[j], deltaNablaWeights[j]);
			}
		}
		this.biases = this.subtract(this.biases, this.multiply(nablaBiases, learningRate/miniBatch.length));
		for (int j=this.weights.length-1;j>=0;j--) {
			this.weights[j] = this.multiply(this.subtract(this.weights[j], this.multiply(nablaWeights[j],
					learningRate/miniBatch.length)), 1-learningRate*(regularization/trainingDataLength));
		}
	}

	private double[][][][] backpropagate(double[][] touple, boolean crossEntropy) {
		double[][] nablaBiases = new double[this.biases.length][];
		double[][][] nablaWeights = new double[this.weights.length][][];
		double[][] zVecs = new double[this.layers-1][];
		double[][] activations = new double[this.layers][];
		double[] activation = touple[INPUT];
		double[] delta;
		double[] z;
		activations[0] = activation;
		for (int i=1;i<this.layers;i++) {
			z = this.add(this.dot(this.weights[i-1], activation), this.biases[i-1]);
			zVecs[i-1] = z;
			activation = this.sigmoid(z);
			activations[i] = activation;
		}
		if (crossEntropy) {
			delta = this.crossEntropyCostDelta(activations[activations.length-1], touple[OUTPUT]);
		} else {
			delta = this.quadraticCostDelta(activations[activations.length-1], touple[OUTPUT], zVecs[zVecs.length-1]);
		}
		nablaBiases[nablaBiases.length-1] = delta;
		nablaWeights[nablaWeights.length-1] = this.dot(delta, activations[activations.length-2]);
		for (int i=this.layers-3;i>=0;i--) {
			z = zVecs[i];
			delta = this.multiply(this.dot(this.transpose(this.weights[i+1]), delta), this.sigmoidPrime(z));
			nablaBiases[i] = delta;
			nablaWeights[i] = this.dot(delta, activations[i]);
		}
		double[][][][] ret = new double[2][][][];
		ret[0] = new double[1][][];
		ret[0][0] = nablaBiases;
		ret[1] = nablaWeights;
		return ret;
	}

	private double[] quadraticCostDelta(double[] outputActivations, double[] idealOutput, double[] z) {
		double[] ret = new double[outputActivations.length];
		for (int i=outputActivations.length-1;i>=0;i--) {
			ret[i] = outputActivations[i] - idealOutput[i];
		}
		return this.multiply(this.subtract(outputActivations, idealOutput), this.sigmoidPrime(z));
	}

	private double[] crossEntropyCostDelta(double[] outputActivations, double[] idealOutput) {
		return this.subtract(outputActivations, idealOutput);
	}

	private double quadraticCostFn(double[] outputActivations, double[] idealOutput) {
		return 0.5*this.norm(this.subtract(outputActivations, idealOutput));
	}

	private double crossEntropyCostFn(double[] outputActivations, double[] idealOutput) {
		return this.sum(this.subtract(this.multiply(this.multiply(idealOutput, -1), this.log(outputActivations)),
				this.multiply(this.add(this.multiply(outputActivations, -1), 1),
				this.log(this.add(this.multiply(outputActivations, -1), 1)))));
	}

	private double totalCost(double[][][] data, double regularization, boolean crossEntropy) {
		double cost = 0;
		for (int i=data.length-1;i>=0;i--) {
			if (crossEntropy) {
				cost += this.crossEntropyCostFn(this.feedForward(data[i][INPUT]), data[i][OUTPUT])/data.length;
			} else {
				cost += this.quadraticCostFn(this.feedForward(data[i][INPUT]), data[i][OUTPUT])/data.length;
			}
		}
		double normSum = 0;
		for (int i=this.weights.length-1;i>=0;i--) {
			normSum += Math.pow(this.norm(this.weights[i]), 2);
		}
		return cost+0.5*(regularization/data.length)*normSum;
	}

	private int accuracy(double[][][] data) {
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

	public void saveToFile(String file, boolean overwrite) throws IOException {
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

	public static AdvancedNet loadFromFile(String file) throws IOException {
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
		return new AdvancedNet(sizes, biases, weights);
	}

	//MISCELLANEOUS FUNCTIONS

	private double sigmoid(double x) {
		return 1.0/(1.0+Math.exp(-x));
	}

	private double[] sigmoid(double[] x) {
		double[] ret = new double[x.length];
		for (int i=x.length-1;i>=0;i--) {
			ret[i] = this.sigmoid(x[i]);
		}
		return ret;
	}

	private double sigmoidPrime(double x) {
		return this.sigmoid(x)*(1-this.sigmoid(x));
	}

	private double[] sigmoidPrime(double[] x) {
		double[] ret = new double[x.length];
		for (int i=x.length-1;i>=0;i--) {
			ret[i] = this.sigmoidPrime(x[i]);
		}
		return ret;
	}

	private double norm(double[] a) {
		double ret = 0;
		for (int i=a.length-1;i>=0;i--) {
			ret += Math.pow(a[i], 2);
		}
		return Math.sqrt(ret);
	}

	private double norm(double[][] a) {
		double ret = 0;
		for (int i=a.length-1;i>=0;i--) {
			for (int j=a[i].length-1;j>=0;j--) {
				ret += Math.pow(a[i][j], 2);
			}
		}
		return Math.sqrt(ret);
	}

	private double[] add(double[] a, double[] b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = a[i] + b[i];
		}
		return ret;
	}

	private double[][] add(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = this.add(a[i], b[i]);
		}
		return ret;
	}

	private double[] add(double[] a, double b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = a[i] + b;
		}
		return ret;
	}

	private double[][] add(double[][] a, double b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = this.add(a[i], b);
		}
		return ret;
	}

	private double[] subtract(double[] a, double[] b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = a[i] - b[i];
		}
		return ret;
	}

	private double[][] subtract(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = this.subtract(a[i], b[i]);
		}
		return ret;
	}

	private double[] multiply(double[] a, double[] b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = a[i] * b[i];
		}
		return ret;
	}

	private double[][] multiply(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = this.multiply(a[i], b[i]);
		}
		return ret;
	}

	private double[] multiply(double[] a, double b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = a[i] * b;
		}
		return ret;
	}

	private double[][] multiply(double[][] a, double b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = this.multiply(a[i], b);
		}
		return ret;
	}

	private double sum(double[] a) {
		double ret = 0;
		for (int i=a.length-1;i>=0;i--) {
			ret += a[i];
		}
		return ret;
	}

	private double sum(double[][] a) {
		double ret = 0;
		for (int i=a.length-1;i>=0;i--) {
			ret += this.sum(a[i]);
		}
		return ret;
	}

	private double[] log(double[] a) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = Math.log(a[i]);
		}
		return ret;
	}

	private double[][] log(double[][] a) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = this.log(a[i]);
		}
		return ret;
	}

	private double[][] dot(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			for (int j=a[i].length-1;j>=0;j--) {
				double temp = 0;
				for (int k=a[i].length-1;k>=0;k--) {
					temp += a[i][k] * b[k][i];
				}
				ret[i][j] = temp;
			}
		}
		return ret;
	}

	private double[] dot(double[][] a, double[] b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			double temp = 0;
			for (int k=a[i].length-1;k>=0;k--) {
				temp += a[i][k] * b[k];
			}
			ret[i] = temp;
		}
		return ret;
	}

	private double[] dot(double[] a, double[][] b) {
		double[] ret = new double[b[0].length];
		for (int i=ret.length-1;i>=0;i--) {
			double temp = 0;
			for (int k=a.length-1;k>=0;k--) {
				temp += a[k] * b[k][i];
			}
			ret[i] = temp;
		}
		return ret;
	}

	private double[][] dot(double[] a, double[] b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = new double[b.length];
			for (int j=b.length-1;j>=0;j--) {
				ret[i][j] = a[i] * b[j];
			}
		}
		return ret;
	}

	private double[][] transpose(double[][] a) {
		double[][] ret = new double[a[0].length][];
		for (int i=ret.length-1;i>=0;i--) {
			ret[i] = new double[a.length];
			for (int j=ret[i].length-1;j>=0;j--) {
				ret[i][j] = a[j][i];
			}
		}
		return ret;
	}

	//DEBUGGING FUNCTIONS

	private void printArray(double[] a, int s) {
		if (a == null) {
			System.out.print(this.printSpaces(s) + "[ ]");
		} else {
			System.out.print(this.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				System.out.print(String.format("% 2.8f", a[i]));
				System.out.print(i==a.length-1 ? "]" : ", ");
			}
		}
	}

	private void printArray(double[][] a, int s) {
		if (a == null) {
			System.out.print(this.printSpaces(s) + "[\n" + this.printSpaces(s) + "]");
		} else {
			System.out.println(this.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				this.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + this.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	private void printArray(double[][][] a, int s) {
		if (a == null) {
			System.out.print(this.printSpaces(s) + "[\n" + this.printSpaces(s) + "]");
		} else {
			System.out.println(this.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				this.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + this.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	private void printArray(double[] a) {
		this.printArray(a, 0);
		System.out.println();
	}

	private void printArray(double[][] a) {
		this.printArray(a, 0);
		System.out.println();
	}

	private void printArray(double[][][] a) {
		this.printArray(a, 0);
		System.out.println();
	}

	private String printSpaces(int s) {
		String ret = "";
		for (int i=s;i>0;i--) {
			ret += "   ";
		}
		return ret;
	}
}
