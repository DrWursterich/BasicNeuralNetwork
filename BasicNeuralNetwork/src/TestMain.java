import java.io.IOException;

import BNN.*;

public class TestMain {
	public static void main(String[] args) {
		double learningRate = 0.1;
		double momentum = 0.1;
		try {
			TrainingData[] td = TrainingData.loadFromFile("C:\\Users\\Schäper\\Desktop\\Ablage\\BNN\\TrainingData\\XORAND");
			NeuralNet nn = NeuralNet.loadFromFile("C:\\Users\\Schäper\\Desktop\\Ablage\\BNN\\Networks\\XORAND");
			nn.trainSet(learningRate, momentum, td, 100_000);

			for (int i=0;i<td.length;i++) {
				nn.setInputs(td[i].getInputs());
				nn.process();
				nn.printNet();
				System.out.println();
				System.out.println("Training Data " + (i+1) + " trained " + StringFormat.dec(td[i].getTimesTrained(), 5) + "\n");
			}
			//nn.safeToFile(file, true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
