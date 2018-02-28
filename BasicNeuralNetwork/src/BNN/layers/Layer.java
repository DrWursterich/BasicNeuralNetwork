package BNN.layers;

public abstract class Layer {
	protected final int neurons;
	
	public Layer(int neurons) throws IllegalArgumentException {
		if (neurons <= 0) {
			throw new IllegalArgumentException("A layer needs at least one neuron");
		}
		this.neurons = neurons;
	}
	
	public int getNeurons() {
		return this.neurons;
	}
	
	public abstract int getInputs();
	public abstract double[] feedForward(double[] input);
	public abstract void backpropagate(double[] nablaBiases, double[][] nablaWeights, double regulation);
}
