package BNN;

import BNN.NeuralNet.Layer;

public class ConvolutionalNeuralNet extends NeuralNet {
	protected static final int INPUT = 0;
	protected static final int OUTPUT = 1;
	protected int[] sizes;
	protected int imageWidth;
	protected int receptiveFieldSize;
	protected int strideLength;
	protected double[][] biases;
	protected double[][][] weights;

	protected final class Convolutional extends Layer {
		private final int layerImageWidth = imageWidth;
		public double[] feedForward(double[] input) {
			int layerImageHeight = input.length/(layerImageWidth+2*receptiveFieldSize);
			double[] ret = new double[input.length-layerImageHeight*(layerImageWidth+2*receptiveFieldSize)-2*receptiveFieldSize*layerImageWidth];
			for (int i=ret.length-1;i>=0;i--) {
				ret[i] = 0;
				for (int j=(int)Math.pow(receptiveFieldSize*2+1, 2)-1;j>=0;j--) {
					ret[i] += input[i-receptiveFieldSize-(layerImageHeight*receptiveFieldSize)+((int)j/5)+((j%5)*layerImageHeight)];
				}
			}
			return VecMath.divide(ret, Math.pow(receptiveFieldSize*2+1, 2));
		}
		public void backpropagate() {

		}
	}

	protected final class Pooling extends Layer {
		public double[] feedForward(double[] input) {
			double[] ret = new double[(input.length-(2*receptiveFieldSize))/2];
			return ret;
		}
		public void backpropagate() {

		}
	}

	protected final class Softmax extends Layer {
		public double[] feedForward(double[] input) {
			return new double[2];
		}
		public void backpropagate() {

		}
	}

	/**
	 * Creates a Convolutional Neural Net with the given parameters.
	 * Every entry in the size array creates a new convolutional layer and a pooling layer.
	 * The amount of featuremaps in those layers are the previous ones multiplied with the integer in sizes.
	 * The last entry represents the number of output neurons in a fully connected layer.<br>
	 * Convolutional Networks are made for imagerecognition, so the input should be understood as an image,
	 * where every pixel represents one input. Since this is a vector the width of that image is needed.<br>
	 * The receptive field size is the radius in pixels, excluding the origin, to consider in the next layer.
	 * <br>The distance between two receptive fields is the stride length, which can be modified, but it is
	 * generally best to set it to one.
	 *
	 * @param size the sizes of the layers
	 * @param imageWidth the width of the original picture
	 * @param receptiveFieldSize radius of the receptive fields
	 * @param strideLength distance between receptive field measurements
	 */
	public ConvolutionalNeuralNet (int[] size, int imageWidth, int receptiveFieldSize, int strideLength) {
		if (size[0]%imageWidth != 0) {
			throw new IllegalArgumentException("The amount of input-neurons has to be a multiple of the imageWidth");
		}
		if ((2*receptiveFieldSize+1) > imageWidth || (2*receptiveFieldSize+1) > size[0]/imageWidth) {
			throw new IllegalArgumentException("The ReceptiveField can not be larger than the input-size");
		}
		this.sizes = new int[sizes.length];
		System.arraycopy(sizes, 0, this.sizes, 0, sizes.length);
		this.layers = new Layer[this.sizes.length-1];
		for (int i=layers.length-1;i>=0;i--) {
			this.layers[i] = i==layers.length-1 ? new FullyConnected() : new ConvolutionalPooling();
		}
		this.biases = new double[this.sizes.length-1][];
		this.weights = new double[this.sizes.length-1][][];
		for (int i=this.weights.length-1;i>=0;i--) {
			this.biases[i] = new double[this.sizes[i]-1];
			this.weights[i] = new double[this.sizes[i]-1][];
			for (int j=this.weights[i].length;j>=0;j--) {
				this.biases[i][j] = Math.random()*2+1;
				this.weights[i][j] = new double[(int)Math.pow(receptiveFieldSize, 2)];
				for (int k=this.weights[j].length;k>=0;k--) {
					this.weights[i][j][k] = Math.random()*2+1;
				}
			}
		}
	}

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
}
