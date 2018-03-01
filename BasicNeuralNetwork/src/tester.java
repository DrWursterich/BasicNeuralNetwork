import java.util.Arrays;

import BNN.*;

public class tester {
	public static void main(String...args) {
		double[][][] trainingData = mnistLoader.loadArrayZip("mnistData.zip", "TrainingData.ini");
		double[][][] testData = mnistLoader.loadArrayZip("mnistData.zip", "TestData.ini");
		if (trainingData == null || testData == null) {
			System.exit(0);
		}
		int[] nSizes = new int[] {784, 50, 10};
		double[][] nBiases = new double[nSizes.length-1][];
		double[][][] nWeights = new double[nSizes.length-1][][];
		for (int i=1;i>=0;i--) {
			nBiases[i] = new double[nSizes[i+1]];
			Arrays.fill(nBiases[i], 1);
			nWeights[i] = new double[nSizes[i+1]][];
			for (int j=nWeights[i].length-1;j>=0;j--) {
				nWeights[i][j] = new double[nSizes[i]];
				Arrays.fill(nWeights[i][j], .5);
			}
		}
		ArrayDebug.printArray(trainingData[0][1]);
		NeuralNet nn = new NeuralNet(nSizes, nBiases, nWeights);
		ArrayDebug.printArray(nn.feedForward(trainingData[0][0]));
		ArrayDebug.printArray(getWeights(nn));
		double[][][] newTrainingData = new double[1][][];
		newTrainingData[0] = trainingData[0];
		for (int i=0;i<5;i++) {
			nn.stochasticGradientDescent(newTrainingData, 1, 1, 0.1, 5.0, nn.new CrossEntropy());
			ArrayDebug.printArray(nn.feedForward(trainingData[0][0]));
			ArrayDebug.printArray(getWeights(nn));
		}
	}

	public static double[][] getWeights(NeuralNet nn) {
		double[][][] weights = new double[nn.getLayerAmount()-1][][];
		for (int i=weights.length-1;i>=0;i--) {
			weights[i] = new double[nn.getLayerSize(i+1)][];
			for (int j=weights[i].length-1;j>=0;j--) {
				weights[i][j] = new double[nn.getLayerSize(i)];
				for (int k=weights[i][j].length-1;k>=0;k--) {
					weights[i][j][k] = nn.getWeight(i+1, j+1, k+1);
				}
			}
		}
		return weights[weights.length-1];
	}
}
