import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/***
 * class to load mnist data from specific files
 *
 * @author Mario Schaeper
 */
public class mnistLoader {
	private static String path = "mnistData/";

	/**
	 * Loads training data from <b>trainingData.txt</b> and
	 * <b>trainingDataResults.txt</b>
	 *
	 * @return fileName combined array of inputs and outputs of the loaded
	 *         training data
	 */
	public static double[][][] loadTrainingDataTxt() {
		final double[][] trainingInputs = mnistLoader.loadInputsTxt("TestData.txt");
		final double[][] trainingOutputs = mnistLoader.loadResultsTxtWrapper("TestDataResults.txt");
		final double[][][] trainingData = new double[trainingOutputs.length][][];
		for (int i = 0; i < trainingData.length; i++) {
			trainingData[i] = new double[2][];
			trainingData[i][0] = trainingInputs[i];
			trainingData[i][1] = trainingOutputs[i];
		}
		return trainingData;
	}

	/**
	 * Loads training data outputs from a file and formats it to fit a neural
	 * net output
	 *
	 * @param fileName
	 *            the file to load from
	 * @return formatted array of the loaded outputs
	 */
	public static double[][] loadResultsTxtWrapper(final String fileName) {
		final double[] trainingResults = mnistLoader.loadResultsTxt(fileName);
		final double[][] trainingOutputs = new double[trainingResults.length][];
		double[] temp;
		for (int i = 0; i < trainingOutputs.length; i++) {
			temp = new double[10];
			for (int j = 0; j < temp.length; j++) {
				temp[j] = j == trainingResults[i] ? 1 : 0;
			}
			trainingOutputs[i] = temp;
		}
		return trainingOutputs;
	}

	/**
	 * Loads training data inputs from a file
	 *
	 * @param fileName
	 *            the file to load from
	 * @return array of the loaded outputs
	 */
	public static double[][] loadInputsTxt(final String fileName) {
		final double startTime = System.nanoTime();
		double[][] ret = new double[0][];
		try {
			final RandomAccessFile stream = new RandomAccessFile(path + fileName, "r");
			final FileChannel channel = stream.getChannel();
			ArrayList<Double> whatever = new ArrayList<>();
			final int fileLength = (int)stream.length();
			int count = 0;
			double currentNumber = 0;
			int currentDigits = 0;
			char lastChar = ' ';
			boolean dot = false;
			for (int i = 0; i < fileLength; i++) {
				final char toAdd = (char)stream.readByte();
				switch (toAdd) {
				case '[':
					whatever = new ArrayList<>();
					break;
				case ']':
					final double[][] tempRet = new double[ret.length + 1][];
					System.arraycopy(ret, 0, tempRet, 0, ret.length);
					ret = tempRet;
					ret[count] = new double[whatever.size()];
					for (int j = ret[count].length - 1; j >= 0; j--) {
						ret[count][j] = whatever.get(j);
					}
					if (++count % 250 == 0) {
						System.out.println(String.format("inputs loaded: %5d", count));
					}
					break;
				case 13:
				case 10:
				case ' ':
					if (lastChar == '.' || lastChar >= '0' && lastChar <= '9') {
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
							final int mult = (int)Math.pow(10, ++currentDigits);
							currentNumber = (currentNumber * mult + Integer.parseInt("" + toAdd)) / mult;
						} else {
							currentNumber = currentNumber * 10 + Integer.parseInt("" + toAdd);
						}
					} else {
						if (("" + toAdd).equals(System.getProperty("line.separator"))) {
							System.out.println("character " + toAdd + " is missused at " + i);
						}
					}
					break;
				}
				lastChar = toAdd;
			}
			System.out.println(
					String.format(
							"loading inputs complete in %4.8f seconds",
							(System.nanoTime() - startTime) / 1000000000));
			stream.close();
			channel.close();
		} catch (final IOException e) {
			System.out.println("load inputs failed");
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Loads training data outputs from a file
	 *
	 * @param fileName
	 *            the file to load from
	 * @return array fo the loaded outputs
	 */
	public static double[] loadResultsTxt(final String fileName) {
		final double startTime = System.nanoTime();
		double[] ret = null;
		try {
			final RandomAccessFile stream = new RandomAccessFile(path + fileName, "r");
			final FileChannel channel = stream.getChannel();
			ret = new double[(int)stream.length()];
			for (int i = 0; i < ret.length; i++) {
				final char toAdd = (char)stream.readByte();
				ret[i] = Integer.parseInt("" + toAdd);
				if (i % 250 == 0) {
					System.out.println(String.format("loaded results: %5d", i));
				}
			}
			stream.close();
			channel.close();
			System.out.println(
					String.format(
							"loading results complete in %4.8f seconds",
							(System.nanoTime() - startTime) / 1000000000));
		} catch (final IOException e) {
			System.out.println("loading results failed");
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Saves a three dimensional array into a binaryfile
	 *
	 * @param data
	 *            the array to save
	 * @param fileName
	 *            the name of the file
	 * @param overwrite
	 *            whether the file should be overwritten, if it exists or not
	 */
	public static void saveData(final double[][][] data, final String fileName, final boolean overwrite) {
		final double startTime = System.nanoTime();
		try {
			final RandomAccessFile stream = new RandomAccessFile(path + fileName, "rw");
			final FileChannel channel = stream.getChannel();
			FileLock lock = null;
			try {
				lock = channel.tryLock();
			} catch (final OverlappingFileLockException e) {
				stream.close();
				channel.close();
				throw new IOException(e);
			}
			if (overwrite) {
				stream.setLength(0);
			}
			stream.seek(stream.length());
			stream.writeInt(data.length);
			for (int i = data.length - 1; i >= 0; i--) {
				stream.writeInt(data[i].length);
				for (int j = data[i].length - 1; j >= 0; j--) {
					stream.writeInt(data[i][j].length);
				}
			}
			for (int i = data.length - 1; i >= 0; i--) {
				for (int j = data[i].length - 1; j >= 0; j--) {
					for (int k = data[i][j].length - 1; k >= 0; k--) {
						stream.writeDouble(data[i][j][k]);
					}
				}
				if ((data.length - i) % 250 == 0) {
					System.out.println(String.format("data saved: %5d / %5d", data.length - i, data.length));
				}
			}
			lock.release();
			stream.close();
			channel.close();
			System.out.println(
					String.format(
							"saving data complete in %4.8f seconds",
							(System.nanoTime() - startTime) / 1000000000));
		} catch (final IOException e) {
			System.out.println("data saving failed");
			e.printStackTrace();
		}
	}

	/**
	 * Loads a three dimensional array from a binaryfile
	 *
	 * @param fileName
	 *            the name of the file to load from
	 * @return a array of the loaded data
	 */
	public static double[][][] loadData(final String fileName) {
		final double startTime = System.nanoTime();
		double[][][] ret = null;
		try {
			final RandomAccessFile stream = new RandomAccessFile(path + fileName, "r");
			final FileChannel channel = stream.getChannel();
			ret = new double[stream.readInt()][][];
			for (int i = ret.length - 1; i >= 0; i--) {
				ret[i] = new double[stream.readInt()][];
				for (int j = ret[i].length - 1; j >= 0; j--) {
					ret[i][j] = new double[stream.readInt()];
					for (int k = ret[i][j].length - 1; k >= 0; k--) {
						ret[i][j][k] = stream.readDouble();
					}
				}
				if ((ret.length - i) % 250 == 0) {
					System.out.println(String.format("data loaded: %5d / %5d", ret.length - i, ret.length));
				}
			}
			stream.close();
			channel.close();
			System.out.println(
					String.format(
							"loading data complete in %4.8f seconds",
							(System.nanoTime() - startTime) / 1000000000));
		} catch (final IOException e) {
			System.out.println("loading data failed");
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Loads a three dimensional array from a binaryfile
	 *
	 * @param fileName
	 *            the name of the file to load from
	 * @return a array of the loaded data
	 */
	public static double[][][] loadDataAlternative(final String fileName) {
		final double startTime = System.nanoTime();
		double[][][] ret = null;
		try {
			final RandomAccessFile stream = new RandomAccessFile(path + fileName, "r");
			final FileChannel channel = stream.getChannel();
			ret = new double[stream.readInt()][][];
			for (int i = ret.length - 1; i >= 0; i--) {
				ret[i] = new double[stream.readInt()][];
				for (int j = ret[i].length - 1; j >= 0; j--) {
					ret[i][j] = new double[stream.readInt()];
				}
			}
			for (int i = ret.length - 1; i >= 0; i--) {
				for (int j = ret[i].length - 1; j >= 0; j--) {
					for (int k = ret[i][j].length - 1; k >= 0; k--) {
						ret[i][j][k] = stream.readDouble();
					}
				}
				if ((ret.length - i) % 250 == 0) {
					System.out.println(String.format("data loaded: %5d / %5d", ret.length - i, ret.length));
				}
			}
			stream.close();
			channel.close();
			System.out.println(
					String.format(
							"loading data complete in %4.8f seconds",
							(System.nanoTime() - startTime) / 1000000000));

		} catch (final IOException e) {
			System.out.println("loading data failed");
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Saves an array to a file
	 *
	 * @param fileName
	 *            the file name
	 */
	public static void saveArray(final double[][][] a, final String fileName) {
		try {
			final double startTime = System.nanoTime();
			final FileOutputStream f = new FileOutputStream(path + fileName);
			final ObjectOutput s = new ObjectOutputStream(f);
			s.writeObject(a);
			s.close();
			f.close();
			System.out.println(
					String.format(
							"saving array finished in %4.8f seconds",
							(System.nanoTime() - startTime) / 1000000000));
		} catch (final IOException e) {
			System.out.println("array saving failed");
			e.printStackTrace();
		}
	}

	/**
	 * Loads an array from a file
	 *
	 * @param fileName
	 *            the file name
	 * @return the loaded array
	 */
	public static double[][][] loadArray(final String fileName) {
		double[][][] ret = null;
		try {
			final double startTime = System.nanoTime();
			final FileInputStream f = new FileInputStream(path + fileName);
			final ObjectInputStream s = new ObjectInputStream(f);
			ret = (double[][][])s.readObject();
			s.close();
			f.close();
			System.out.println(
					String.format(
							"loading array finished in %4.8f seconds",
							(System.nanoTime() - startTime) / 1000000000));
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("array loading failed");
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * unpacks a .zip file
	 *
	 * @param zipName
	 *            the .zip-file name
	 */
	public static void unzip(final String zipName) {
		final double startTime = System.nanoTime();
		final byte[] buffer = new byte[1024];
		try {
			final ZipInputStream s = new ZipInputStream(new FileInputStream(path + zipName));
			ZipEntry ze = s.getNextEntry();
			while (ze != null) {
				final File newFile = new File(path + ze.getName());
				if (!newFile.exists()) {
					final FileOutputStream fs = new FileOutputStream(newFile);
					int len;
					while ((len = s.read(buffer)) > 0) {
						fs.write(buffer, 0, len);
					}
					fs.close();
					System.out.println("unzipped " + newFile.getAbsoluteFile());
				}
				ze = s.getNextEntry();
			}
			s.closeEntry();
			s.close();
			System.out.println(
					String.format("unzipping finished in %4.8f seconds", (System.nanoTime() - startTime) / 1000000000));
		} catch (final IOException e) {
			System.out.println("unzipping failed");
			e.printStackTrace();
		}
	}

	/**
	 * loads an array from a file inside a .zip file
	 *
	 * @param zipName
	 *            the name of the .zip-file
	 * @param fileName
	 *            the name of the file to load from
	 */
	public static double[][][] loadArrayZip(final String zipName, final String fileName) {
		double[][][] ret = null;
		final double startTime = System.nanoTime();
		try {
			final ZipFile zipFile = new ZipFile(path + zipName);
			final ZipInputStream s = new ZipInputStream(new FileInputStream(path + zipName));
			ZipEntry ze = s.getNextEntry();
			while (ze != null) {
				if (ze.getName().equals(fileName)) {
					final InputStream f = zipFile.getInputStream(ze);
					final ObjectInputStream is = new ObjectInputStream(f);
					ret = (double[][][])is.readObject();
					is.close();
					f.close();
					break;
				}
				ze = s.getNextEntry();
			}
			zipFile.close();
			s.closeEntry();
			s.close();
			if (ret == null) {
				System.out.println("file not found");
			} else {
				System.out.println(
						String.format(
								"loading %s finished in %4.8f seconds",
								fileName,
								(System.nanoTime() - startTime) / 1000000000));
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("loading failed");
			e.printStackTrace();
		}
		return ret;
	}
}
