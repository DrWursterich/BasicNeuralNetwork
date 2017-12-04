import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.io.IOException;

public class TrainingData {
	private double[] inputs;
	private double[] outputs;
	private int timesTrained = 0;
	
	public TrainingData(double[] inputs, double[] outputs) {
		this.inputs = inputs;
		this.outputs = outputs;
	}
	
	/* public TrainingData(String file) {
		TODO
	} */
	
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
	
	public void saveToFile(String file) throws IOException {
		RandomAccessFile stream = new RandomAccessFile(file, "rw");
		FileChannel channel = stream.getChannel();
		FileLock lock = null;
		try {
			lock = channel.tryLock();
		} catch (OverlappingFileLockException e) {
			stream.close();
			channel.close();
		}
		stream.seek(stream.length());
		for (int i=0;i<this.inputs.length;i++) {
			stream.writeBytes(this.inputs[i] + (i==this.inputs.length-1 ? System.getProperty("line.separator") : " "));
		}
		for (int i=0;i<this.outputs.length;i++) {
			stream.writeBytes(this.outputs[i] + (i==this.outputs.length-1 ? System.getProperty("line.separator") : " "));
		}
		lock.release();
		stream.close();
		channel.close();
	}
}