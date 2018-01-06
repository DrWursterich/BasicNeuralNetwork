package BNN;

public class AdvancedNet {
	private static final int INPUT = 0;
	private static final int OUTPUT = 1;
	private int layers;
	private int[] sizes;
	private double[][] biases;
	private double[][][] weights;

	public AdvancedNet(int[] size) {
		this.layers = size.length;
		this.sizes = size;
		this.biases = new double[this.layers-1][];
		this.weights = new double[this.layers-1][][];
		for (int i=this.layers-1;i>0;i--) {
			this.weights[i-1] = new double[this.sizes[i]][];
			this.biases[i-1] = new double[this.sizes[i]];
			for (int j=this.sizes[i]-1;j>=0;j--) {
				this.biases[i-1][j] = Math.random();
				this.weights[i-1][j] = new double[this.sizes[i-1]];
				for (int k=this.sizes[i-1]-1;k>=0;k--) {
					this.weights[i-1][j][k] = Math.random();
				}
			}
		}
	}

	public double[] feedForward(double[] input) {
		for (int i=0;i<this.layers-1;i++) {
			input = this.sigmoid(this.add(this.dot(this.weights[i], input), this.biases[i]));
		}
		return input;
	}

	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize, double learningRate, double[][][] testData) {
		for (int i=0;i<epochs;i++) {
			for (int j=trainingData.length-1;j>=0;j--) {
				int swapWith = (int)Math.random()*(trainingData.length-1);
				double[][] temp = trainingData[j];
				trainingData[j] = trainingData[swapWith];
				trainingData[swapWith] = temp;
			}
			double[][][] miniBatch = new double[miniBatchSize][][];
			for (int j=0;j<trainingData.length/miniBatchSize;j++) {
				System.arraycopy(trainingData, j*miniBatchSize, miniBatch, 0, miniBatchSize);
				this.updateMiniBatch(miniBatch, learningRate);
			}
			System.out.println(String.format("Epoch %5d: %8d / %8d", i+1, this.evaluate(testData, true), testData.length));
		}
	}
	
	public void stochasticGradientDescent(double[][][] trainingData, int epochs, int miniBatchSize, double learningRate) {
		for (int i=0;i<epochs;i++) {
			for (int j=trainingData.length-1;j>=0;j--) {
				int swapWith = (int)Math.random()*(trainingData.length-1);
				double[][] temp = trainingData[j];
				trainingData[j] = trainingData[swapWith];
				trainingData[swapWith] = temp;
			}
			double[][][] miniBatch = new double[miniBatchSize][][];
			for (int j=0;j<trainingData.length/miniBatchSize;j++) {
				System.arraycopy(trainingData, j*miniBatchSize, miniBatch, 0, miniBatchSize);
				this.updateMiniBatch(miniBatch, learningRate);
			}
			System.out.println(String.format("Epoch %5d complete", i+1));
		}
	}

	private void updateMiniBatch(double[][][] miniBatch, double learningRate) {
		double[][] nablaBiases = new double[this.biases.length][];
		double[][][] nablaWeights = new double[this.weights.length][][];
		for (int i=miniBatch.length-1;i>=0;i--) {
			double[][][][] deltaNablas = this.backpropagate(miniBatch[i]);
			double[][] deltaNablaBiases = deltaNablas[0][0];
			double[][][] deltaNablaWeights = deltaNablas[1];
			nablaBiases = this.add(deltaNablaBiases, 0);
			for (int j=nablaWeights.length-1;j>=0;j--) {
				nablaWeights[j] = this.add(deltaNablaWeights[j], 0);
			}
		}
		this.biases = this.subtract(this.biases, this.multiply(nablaBiases, learningRate/miniBatch.length));
		for (int j=this.weights.length-1;j>=0;j--) {
			this.weights[j] = this.subtract(this.weights[j], this.multiply(nablaWeights[j], learningRate/miniBatch.length));
		}
	}

	private double[][][][] backpropagate(double[][] touple) {
		double[][] nablaBiases = new double[this.biases.length][];
		double[][][] nablaWeights = new double[this.weights.length][][];
		double[][] zVecs = new double[this.layers-1][];
		double[][] activations = new double[this.layers][];
		double[] activation = touple[INPUT];
		double[] z;
		activations[0] = activation;
		for (int i=1;i<this.layers;i++) {
			z = this.add(this.dot(this.weights[i-1], activation), this.biases[i-1]);
			zVecs[i-1] = z;
			activation = this.sigmoid(z);
			activations[i] = activation;
		}
		double[] delta = this.multiply(this.costDeivative(activations[activations.length-1], touple[OUTPUT]),
				sigmoidPrime(zVecs[zVecs.length-1]));
		nablaBiases[nablaBiases.length-1] = delta;
		//nablaWeights[nablaWeights.length-1] = this.dot(delta, activations[activations.length-2]);
		for (int i=this.weights.length-1;i>=0;i--) {
			nablaWeights[i] = new double[this.weights[i].length][];
			for (int j=this.weights[i].length-1;j>=0;j--) {
				nablaWeights[i][j] = new double[this.weights[i][j].length];
			}
		}
		for (int i=nablaWeights[nablaWeights.length-1].length-1;i>=0;i--) {
			for (int j=nablaWeights[nablaWeights.length-1][i].length-1;j>=0;j--) {
				nablaWeights[nablaWeights.length-1][i][j] = delta[i] * activations[activation.length-2][i];
			}
		}
		for (int i=this.layers-3;i>=0;i--) {
			z = zVecs[i];
			delta = this.multiply(this.dot(this.transpose(this.weights[i+1]), delta), this.sigmoidPrime(z));
			nablaBiases[i] = delta;
			for (int j=nablaWeights[nablaWeights.length-1].length-1;j>=0;j--) {
				for (int k=nablaWeights[nablaWeights.length-1][j].length-1;k>=0;k--) {
					nablaWeights[nablaWeights.length-1][j][k] = delta[j] * activations[activation.length-2][j];
				}
			}
		}
		double[][][][] ret = new double[2][][][];
		ret[0] = new double[1][][];
		ret[0][0] = nablaBiases;
		ret[1] = nablaWeights;
		return ret;
	}
	
	public int evaluate(double[][][] testData, boolean binary) {
		double[][] results = new double [testData.length][];
		int sum = 0;
		for (int i=testData.length-1;i>=0;i--) {
			results[i] = this.feedForward(testData[i][INPUT]);
			boolean equal = true;
			for (int j=results[i].length-1;j>=0;j--) {
				if (binary) {
					//System.out.println(String.format("result: %1.3f | expected: %1.3f", results[i][j], testData[i][OUTPUT][j]));
					if (Math.round(results[i][j]) != Math.round(testData[i][OUTPUT][j])) {
						equal = false;
					}
				} else {
					if (Math.abs(results[i][j] - testData[i][OUTPUT][j]) <= .1) {
						equal = false;
					}
				}
			}
			sum += equal ? 1 : 0;
		}
		return sum;
	}
	
	private double[] costDeivative(double[] outputActivations, double[] idealOutput) {
		for (int i=outputActivations.length-1;i>=0;i--) {
			outputActivations[i] -= idealOutput[i];
		}
		return outputActivations;
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
	
	private double[] add(double[] a, double[] b) {
		for (int i=a.length-1;i>=0;i--) {
			a[i] += b[i];
		}
		return a;
	}
	
	private double[][] add(double[][] a, double[][] b) {
		for (int i=a.length-1;i>=0;i--) {
			a[i] = this.add(a[i], b[i]);
		}
		return a;
	}
	
	private double[] add(double[] a, double b) {
		for (int i=a.length-1;i>=0;i--) {
			a[i] += b;
		}
		return a;
	}
	
	private double[][] add(double[][] a, double b) {
		for (int i=a.length-1;i>=0;i--) {
			a[i] = this.add(a[i], b);
		}
		return a;
	}
	
	private double[] subtract(double[] a, double[] b) {
		for (int i=a.length-1;i>=0;i--) {
			a[i] -= b[i];
		}
		return a;
	}
	
	private double[][] subtract(double[][] a, double[][] b) {
		for (int i=a.length-1;i>=0;i--) {
			a[i] = this.subtract(a[i], b[i]);
		}
		return a;
	}
	
	private double[] multiply(double[] a, double[] b) {
		for (int i=a.length-1;i>=0;i--) {
			a[i] *= b[i];
		}
		return a;
	}
	
	private double[][] multiply(double[][] a, double[][] b) {
		for (int i=a.length-1;i>=0;i--) {
			a[i] = this.multiply(a[i], b[i]);
		}
		return a;
	}
	
	private double[] multiply(double[] a, double b) {
		for (int i=a.length-1;i>=0;i--) {
			a[i] *= b;
		}
		return a;
	}
	
	private double[][] multiply(double[][] a, double b) {
		for (int i=a.length-1;i>=0;i--) {
			a[i] = this.multiply(a[i], b);
		}
		return a;
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
	
	private double dot(double[] a, double[] b) {
		double temp = 0;
		for (int k=a.length-1;k>=0;k--) {
			temp += a[k] * b[k];
		}
		return temp;
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
}