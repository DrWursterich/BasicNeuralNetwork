import BNN.*;

public class TestMain {
	public static void main(String[] args) {
		AdvancedNet nn = new AdvancedNet(new int[]{2, 4, 2});
		double[][][] trainingData = {
				{
					{0, 0},
					{0, 0}
				},
				{
					{0, 1},
					{0, 1}
				},
				{
					{1, 0},
					{0, 1}
				},
				{
					{1, 1},
					{1, 0}
				}
		};
		nn.stochasticGradientDescent(trainingData, 4000, 3, 0.4, trainingData);
		/*
		double learningRate = 0.1;
		double momentum = 0.1;
		NeuralNet nn = new NeuralNet(new int[] {2, 4, 2});

		TrainingData[] td = new TrainingData[]{
				new TrainingData(new double[]{0, 0}, new double[]{0, 0}),
				new TrainingData(new double[]{0, 1}, new double[]{1, 0}),
				new TrainingData(new double[]{1, 0}, new double[]{1, 0}),
				new TrainingData(new double[]{1, 1}, new double[]{0, 1})};

		nn.trainSet(learningRate, momentum, td, 100_000);

		for (int i=0;i<td.length;i++) {
			nn.processInputs(td[i].getInputs());
			nn.printNet();
			System.out.println("Times trained : " + td[i].getTimesTrained() + "\n");
		}

		nn.test(td, false);
		*/
	}
}
