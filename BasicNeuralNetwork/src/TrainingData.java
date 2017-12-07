import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.io.IOException;

public class TrainingData {
	private static final int INPUT_START = -1;
	private static final int OUTPUT_START = -2;
	private static final int DATASET_END = -3;
	private double[] inputs;
	private double[] outputs;
	private int timesTrained = 0;

	public TrainingData(double[] inputs, double[] outputs) {
		this.inputs = inputs;
		this.outputs = outputs;
	}

	public void increaseTimesTrained() {
		this.timesTrained++;
	}

	public int getTimesTrained() {
		return this.timesTrained;
	}

	public double getInput(int input) {
		return this.inputs[input];
	}

	public double[] getInputs() {
		return this.inputs;
	}

	public double getOutput(int output) {
		return this.outputs[output];
	}

	public double[] getOutputs() {
		return this.outputs;
	}

	public int[] getDimensions() {
		return new int[] {this.inputs.length, this.outputs.length};
	}

	public void saveToFile(String file, boolean overwrite) {
		try {
			RandomAccessFile stream = new RandomAccessFile(file, "rw");
			FileChannel channel = stream.getChannel();
			FileLock lock = null;
			try {
				lock = channel.tryLock();
			} catch (OverlappingFileLockException e) {
				stream.close();
				channel.close();
				System.out.println("TrainingData has not been saved, file is locked");
			}
			if (!overwrite) {
				stream.seek(stream.length());
			}
			stream.writeDouble(-1.0);
			for (int i=0;i<this.inputs.length;i++) {
				stream.writeDouble(this.inputs[i]);
				//stream.writeBytes((i==this.inputs.length-1 ? System.getProperty("line.separator") : " "));
			}
			stream.writeDouble(-2.0);
			for (int i=0;i<this.outputs.length;i++) {
				stream.writeDouble(this.outputs[i]);
				//stream.writeBytes((i==this.outputs.length-1 ? System.getProperty("line.separator") : " "));
			}
			stream.writeDouble(-3.0);
			lock.release();
			stream.close();
			channel.close();
		} catch (IOException e) {
			System.out.println("TrainingData has not been saved, path invalid");
			e.printStackTrace();
		}
	}

	public static TrainingData[] loadFromFile(String file) {
		TrainingData[] fileData = new TrainingData[0];
		try {
			RandomAccessFile stream = new RandomAccessFile(file, "r");
			FileChannel channel = stream.getChannel();

			int writerPos = 0;
			double[] fileDataDouble = new double[0];
			while(writerPos < stream.length()) {
				fileDataDouble = arrayAdd(fileDataDouble, stream.readDouble());
				writerPos += 8;
			}
			double[] input = new double[0];
			double[] output = new double[0];
			int state = 0;
			for (int i=0;i<fileDataDouble.length;i++) {
				if (fileDataDouble[i] == INPUT_START || fileDataDouble[i] == OUTPUT_START || fileDataDouble[i] == DATASET_END) {
					state = (int)fileDataDouble[i];
					if (state == DATASET_END) {
						fileData = arrayAdd(fileData, new TrainingData(input, output));
						input = new double[0];
						output = new double[0];
					}
				} else {
					switch (state) {
					case INPUT_START:
						input = arrayAdd(input, fileDataDouble[i]);
						break;
					case OUTPUT_START:
						output = arrayAdd(output, fileDataDouble[i]);
						break;
					}
				}
			}
			stream.close();
			channel.close();
			if (state != DATASET_END) {
				System.out.println("File seems to be corrupt");
				return null;
			}
		} catch (IOException e) {
			System.out.println("TrainingData has not been loaded, path invalid");
		}
		return fileData;
	}

	private static double[] arrayAdd(double[] array, double value) {
		double[] temp = new double[array.length+1];
		System.arraycopy(array, 0, temp, 0, array.length);
		temp[temp.length-1] = value;
		return temp;
	}

	private static TrainingData[] arrayAdd(TrainingData[] array, TrainingData value) {
		TrainingData[] temp = new TrainingData[array.length+1];
		System.arraycopy(array, 0, temp, 0, array.length);
		temp[temp.length-1] = value;
		return temp;
	}
}