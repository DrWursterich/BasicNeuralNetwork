package BNN;

import java.util.Arrays;

public class AdvancedNet {
	private static final int INPUT = 0;
	private static final int OUTOUT = 1;
	private int layers;
	private int[] sizes;
	private double[] biases;
	private double[][][] weights;

	public AdvancedNet(int[] size) {
		this.layers = size.length;
		this.sizes = size;
		this.biases = new double[this.layers-1];
		this.weights = new double[this.layers-1][][];
		for (int i=this.layers-1;i>0;i--) {
			this.biases[i] = Math.random();
			for (int j=this.sizes[i]-1;j>=0;j--) {
				for (int k=this.sizes[i-1]-1;k>=0;k--) {
					this.weights[i][j][k] = Math.random();
				}
			}
		}
	}

	public void feedForward(double[] input) {
		for (int i=1;i<this.layers;i++) {
			double[] temp = new double[this.sizes[i]];
			Arrays.fill(temp, 0);
			for (int j=this.sizes[i]-1;j>=0;j--) {
				for (int k=input.length-1;k>=0;k--) {
					temp[j] += input[k] * this.weights[i][j][k];
				}
			}
			input = this.sigmoid(temp);
		}
	}

	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize, double learningRate, double[][][] testData) {
		int n = trainingData.length;
		for (int i=epochs;i>0;i--) {
			for (int j=trainingData.length-1;j>=0;j--) {
				int swapWith = (int)Math.random()*(trainingData.length-1);
				double[][] temp = trainingData[j];
				trainingData[j] = trainingData[swapWith];
				trainingData[swapWith] = temp;
			}
			double[][][] miniBatch = new double[miniBatchSize][][];
			for (int j=0;j<trainingData.length/miniBatchSize;j++) {
				System.arraycopy(trainingData, j*miniBatchSize, miniBatch, 0, miniBatchSize-1);
				this.updateMiniBatch(miniBatch, learningRate);
			}
			//evaluate here
		}
	}

	private void updateMiniBatch(double[][][] miniBatch, double learningRate) {
		double[] nablaBiases = new double[this.biases.length];
		double[][][] nablaWeights = new double[this.weights.length][][];
		for (int i=miniBatch.length-1;i>=0;i--) {
			double[][][][] deltaNablas = this.backpropagate(miniBatch[i]);
			double[] deltaNablaBiases = deltaNablas[0][0][0];
			double[][][] deltaNablaWeights = deltaNablas[1];
			for (int j=nablaBiases.length-1;j>=0;j--) {
				nablaBiases[j] += deltaNablaBiases[j];
			}
			for (int j=nablaWeights.length-1;j>=0;j--) {
				for (int k=nablaWeights[j].length-1;k>=0;k--) {
					for (int l=nablaWeights[j][k].length-1;l>=0;l--) {
						nablaWeights[j][k][l] += deltaNablaWeights[j][k][l];
					}
				}
			}
		}
		for (int j=this.biases.length-1;j>=0;j--) {
			this.biases[j] -= (learningRate/miniBatch.length)*nablaBiases[j];
		}
		for (int j=this.weights.length-1;j>=0;j--) {
			for (int k=this.weights[j].length-1;k>=0;k--) {
				for (int l=this.weights[j][k].length-1;l>=0;l--) {
					this.weights[j][k][l] -= (learningRate/miniBatch.length)*nablaWeights[j][k][l];
				}
			}
		}
	}

	private double[][][][] backpropagate(double[][] toupel) {
		//Hier fehlt die übersetzung des Python codes aus network1
		return new double[][][][] {};
	}

	private double sigmoid(double x) {
		return 1.0/(1.0+Math.exp(-x));
	}

	private double[] sigmoid(double[] x) {
		for (int i=x.length-1;i>=0;i--) {
			x[i] = this.sigmoid(x[i]);
		}
		return x;
	}

	private double sigmoidPrime(double x) {
		return this.sigmoid(x)*(1-this.sigmoid(x));
	}

	private double[] sigmoidPrime(double[] x) {
		for (int i=x.length-1;i>=0;i--) {
			x[i] = this.sigmoidPrime(x[i]);
		}
		return x;
	}
}