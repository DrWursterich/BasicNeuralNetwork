import BNN.*;

public class TestMain {
	public static void main(String[] args) {
		double learningRate = 0.1;
		double momentum = 0.1;
		NeuralNet nn = new NeuralNet(new int[] {
				2, 4, 2
		});
		
		TrainingData[] td = new TrainingData[]{
				new TrainingData(new double[]{0, 0}, new double[]{0, 0}),
				new TrainingData(new double[]{0, 1}, new double[]{1, 0}),
				new TrainingData(new double[]{1, 0}, new double[]{1, 0}),
				new TrainingData(new double[]{1, 1}, new double[]{0, 1})};
		
		nn.trainSet(learningRate, momentum, td, 100_000);
		nn.test(td, false);
	}
}
