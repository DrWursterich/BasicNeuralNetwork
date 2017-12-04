//import java.io.IOException;

public class TestMain {
	public static void main(String[] args) {
		double learningRate = 0.1;
		double momentum = 0.1;
		NeuralNet nn = new NeuralNet(new int[] {2, 4, 2});	//AND | XOR
		TrainingData[] td = new TrainingData[] {
					new TrainingData(
						new double[]{0, 0},
						new double[]{0, 0}
					),
					new TrainingData(
						new double[]{0, 1},
						new double[]{0, 1}
					),
					new TrainingData(
						new double[]{1, 0},
						new double[]{0, 1}
					),
					new TrainingData(
						new double[]{1, 1},
						new double[]{1, 0}
					)
				};
		/*for (int i=0;i<td.length;i++) {
			try {
				td[i].saveToFile("C:\\Users\\Admin\\Desktop\\td1.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/

		for (int i=100000;i>0;i--) {
			nn.train(learningRate, momentum, td[(int)(Math.random()*(td.length))]);
		}
		for (int i=0;i<td.length;i++) {
			nn.setInputs(td[i].getInputs());
			nn.process();
			nn.printNet();
			System.out.println("Training Data " + (i+1) + " trained " + StringFormat.dec(td[i].getTimesTrained(), 5) + "\n");
		}
	}
}


/* TODO!
 *
 * -Bias
 * -backpropagating multiple hiddenlayers
 * -training method
 * -getTrainingDataFromFile
 */