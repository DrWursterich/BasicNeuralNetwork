import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;

/***
 * class to load mnist data from specific files
 *
 * @author schäper
 */
public class mnistLoader {
	private static String path = "C:\\Users\\Schäper\\Desktop\\mnist_data\\";

	/***
	 * <u>converts</u> plain txt files of training data into binary ini files
	 * @param args
	 */
	public static void main(String[] args) {
		double[][][] trainingData = mnistLoader.loadOldData("JavaTestData.ini");
		mnistLoader.saveData(trainingData, "JavaTestData2.ini", true);
		trainingData = mnistLoader.loadOldData("JavaTrainingData.ini");
		mnistLoader.saveData(trainingData, "JavaTrainingData2.ini", true);
		trainingData = mnistLoader.loadOldData("JavaValidationData.ini");
		mnistLoader.saveData(trainingData, "JavaValidationData2.ini", true);
	}

	/***
	 * Loads training data from <b>trainingData.txt</b> and <b>trainingDataResults.txt</b>
	 * @return fileName combined array of inputs and outputs of the loaded training data
	 */
	@SuppressWarnings("unused")
	private static double[][][] loadTrainingDataTxt() {
		double[][] trainingInputs = loadInputs("TestData.txt");
		double[][] trainingOutputs = loadResultsWrapper("TestDataResults.txt");
		double[][][] trainingData = new double[trainingOutputs.length][][];
		for (int i=0;i<trainingData.length;i++) {
			trainingData[i] = new double[2][];
			trainingData[i][0] = trainingInputs[i];
			trainingData[i][1] = trainingOutputs[i];
		}
		return trainingData;
	}

	/***
	 * Loads training data outputs from a file and formats it to fit a neural net output
	 * @param fileName the file to load from
	 * @return formatted array of the loaded outputs
	 */
	public static double[][] loadResultsWrapper(String fileName) {
		double[] trainingResults = loadResults(fileName);
		double[][] trainingOutputs = new double[trainingResults.length][];
		double[] temp;
		for (int i=0;i<trainingOutputs.length;i++) {
			temp = new double[10];
			for (int j=0;j<temp.length;j++) {
				temp[j] = j==trainingResults[i] ? 1 : 0;
			}
			trainingOutputs[i] = temp;
		}
		return trainingOutputs;
	}

	/***
	 * Loads training data inputs from a file
	 * @param fileName the file to load from
	 * @return array of the loaded outputs
	 */
	public static double[][] loadInputs(String fileName) {
		double[][] ret = new double[0][];
		try {
			RandomAccessFile stream = new RandomAccessFile(path + fileName, "r");
			FileChannel channel = stream.getChannel();
			ArrayList<Double> whatever = new ArrayList<Double>();
			int fileLength = (int)stream.length();
			int count = 0;
			double currentNumber = 0;
			int currentDigits = 0;
			char lastChar = ' ';
			boolean dot = false;
			for (int i=0;i<fileLength;i++) {
				char toAdd = (char)stream.readByte();
				switch (toAdd) {
				case '[':
					whatever = new ArrayList<Double>();
					break;
				case ']':
					double[][] tempRet = new double[ret.length+1][];
					System.arraycopy(ret, 0, tempRet, 0, ret.length);
					ret = tempRet;
					ret[count] = new double[whatever.size()];
					for (int j=ret[count].length-1;j>=0;j--) {
						ret[count][j] = whatever.get(j);
					}
					if (++count%250 == 0) {
						System.out.println(String.format("inputs loaded: %5d", count));
					}
					break;
				case 13:
				case 10:
				case ' ':
					if (lastChar == '.' || (lastChar >= '0' && lastChar <= '9')) {
						whatever.add(currentNumber);
						currentNumber = 0;
						dot = false;
						currentDigits = 0;
					}
					break;
				case '.':
					if (dot == true) {
						System.out.println("unexpected '.' at " + i);
					}
					dot = true;
					break;
				default:
					if (toAdd >= '0' && toAdd <= '9') {
						if (dot) {
							int mult = (int)Math.pow(10, ++currentDigits);
							currentNumber = (currentNumber*mult+Integer.parseInt(""+toAdd))/mult;
						} else {
							currentNumber = currentNumber*10+Integer.parseInt(""+toAdd);
						}
					} else {
						if ((""+toAdd).equals(System.getProperty("line.separator"))) {
							System.out.println("character " + toAdd + " is missused at " + i);
						}
					}
					break;
				}
				lastChar = toAdd;
			}
			System.out.println("loading inputs complete");
			stream.close();
			channel.close();
		} catch(IOException e) {
			System.out.println("load inputs failed");
			e.printStackTrace();
		}
		return ret;
	}

	/***
	 * Loads training data outputs from a file
	 * @param fileName the file to load from
	 * @return array fo the loaded outputs
	 */
	public static double[] loadResults(String fileName) {
		double[] ret = null;
		try {
			RandomAccessFile stream = new RandomAccessFile(path + fileName, "r");
			FileChannel channel = stream.getChannel();
			ret = new double[(int)stream.length()];
			for (int i=0;i<ret.length;i++) {
				char toAdd = (char)stream.readByte();
				ret[i] = Integer.parseInt(""+toAdd);
				if (i%250 == 0) {
					System.out.println(String.format("loaded results: %5d", i));
				}
			}
			stream.close();
			channel.close();
			System.out.println("loading results complete");
		} catch(IOException e) {
			System.out.println("loading results failed");
			e.printStackTrace();
		}
		return ret;
	}

	/***
	 * Saves a three dimensional array into a binaryfile
	 * @param data the array to save
	 * @param fileName the name of the file
	 * @param overwrite whether the file should be overwritten, if it exists or not
	 */
	public static void saveData(double[][][] data, String fileName, boolean overwrite) {
		try {
			RandomAccessFile stream = new RandomAccessFile(path + fileName, "rw");
			FileChannel channel = stream.getChannel();
			FileLock lock = null;
			try {
				lock = channel.tryLock();
			} catch (OverlappingFileLockException e) {
				stream.close();
				channel.close();
				throw new IOException(e);
			}
			if (overwrite) {
				stream.setLength(0);
			}
			stream.seek(stream.length());
			stream.writeInt(data.length);
			for (int i=data.length-1;i>=0;i--) {
				stream.writeInt(data[i].length);
				for (int j=data.length-1;j>=0;j--) {
					stream.writeInt(data[i][j].length);
				}
			}
			for (int i=data.length-1;i>=0;i--) {
				for (int j=data[i].length-1;j>=0;j--) {
					for (int k=data[i][j].length-1;k>=0;k--) {
						stream.writeDouble(data[i][j][k]);
					}
				}
				if ((data.length-i)%250 == 0) {
					System.out.println(String.format("data saved: %5d / %5d", data.length-i, data.length));
				}
			}
			lock.release();
			stream.close();
			channel.close();
			System.out.println("data saving complete");
		} catch (IOException e) {
			System.out.println("data saving failed");
			e.printStackTrace();
		}
	}

	/***
	 * Loads a three dimensional array from a binaryfile
	 * @param fileName the name of the file to load from
	 * @return a array of the loaded data
	 */
	public static double[][][] loadOldData(String fileName) {
		double[][][] ret = null;
		try {
			RandomAccessFile stream = new RandomAccessFile(path + fileName, "r");
			FileChannel channel = stream.getChannel();
			ret = new double[stream.readInt()][][];
			for (int i=ret.length-1;i>=0;i--) {
				ret[i] = new double[stream.readInt()][];
				for (int j=ret[i].length-1;j>=0;j--) {
					ret[i][j] = new double[stream.readInt()];
					for (int k=ret[i][j].length-1;k>=0;k--) {
						ret[i][j][k] = stream.readDouble();
					}
				}
				if ((ret.length-i)%250 == 0) {
					System.out.println(String.format("data loaded: %5d / %5d", ret.length-i, ret.length));
				}
			}
			stream.close();
			channel.close();
			System.out.println("loading data complete");
		} catch(IOException e) {
			System.out.println("loading data failed");
			e.printStackTrace();
		}
		return ret;
	}

	/***
	 * Loads a three dimensional array from a binaryfile
	 * @param fileName the name of the file to load from
	 * @return a array of the loaded data
	 */
	public static double[][][] loadData(String fileName) {
		double[][][] ret = null;
		try {
			RandomAccessFile stream = new RandomAccessFile(path + fileName, "r");
			FileChannel channel = stream.getChannel();
			ret = new double[stream.readInt()][][];
			for (int i=ret.length-1;i>=0;i--) {
				ret[i] = new double[stream.readInt()][];
				for (int j=ret[i].length-1;j>=0;j--) {
					ret[i][j] = new double[stream.readInt()];
				}
			}
			for (int i=ret.length-1;i>=0;i--) {
				for (int j=ret[i].length-1;j>=0;j--) {
					for (int k=ret[i][j].length-1;k>=0;k--) {
						ret[i][j][k] = stream.readDouble();
					}
				}
				if ((ret.length-i)%250 == 0) {
					System.out.println(String.format("data loaded: %5d / %5d", ret.length-i, ret.length));
				}
			}
			stream.close();
			channel.close();
			System.out.println("loading data complete");
		} catch(IOException e) {
			System.out.println("loading data failed");
			e.printStackTrace();
		}
		return ret;
	}
}
