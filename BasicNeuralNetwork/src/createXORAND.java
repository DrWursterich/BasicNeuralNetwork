import java.io.IOException;
import BNN.*;

public class createXORAND {
	public static void main(String[] args) {
		NeuralNet nn = new NeuralNet(new int[] {2, 4, 2});	//AND | XOR
		String path = "C:\\Users\\Schäper\\Desktop\\Ablage\\BNN\\";
		String file = "XORAND";
		String fileType = "";

		try {
			nn.safeToFile(path + "Networks\\" + file + fileType, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

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
		try {
			for (int i=0;i<td.length;i++) {
				td[i].saveToFile(path + "TrainingData\\" + file + fileType, i==0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
