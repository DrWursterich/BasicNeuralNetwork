package BNN;

public final class ArrayDebug {
	private static final String STANDARD_DOUBLE_FORMAT = " 2.8";
	private static final String STANDARD_BYTE_FORMAT = " 2";
	private static final String STANDARD_INT_FORMAT = " 2";
	private static final String STANDARD_SIZES_FORMAT = " 2";
	private static String doubleFormat = ArrayDebug.STANDARD_DOUBLE_FORMAT;
	private static String byteFormat = ArrayDebug.STANDARD_BYTE_FORMAT;
	private static String intFormat = ArrayDebug.STANDARD_INT_FORMAT;
	private static String sizesFormat = ArrayDebug.STANDARD_SIZES_FORMAT;

	public static String getDoubleFormat() {
		return ArrayDebug.doubleFormat;
	}

	public static String getByteFormat() {
		return ArrayDebug.byteFormat;
	}

	public static String getIntFormat() {
		return ArrayDebug.intFormat;
	}

	public static String getSizesFormat() {
		return ArrayDebug.sizesFormat;
	}

	public static void setDoubleFormat(String format) {
		ArrayDebug.doubleFormat = format;
	}

	public static void setByteFormat(String format) {
		ArrayDebug.byteFormat = format;
	}

	public static void setIntFormat(String format) {
		ArrayDebug.intFormat = format;
	}

	public static void setSizesFormat(String format) {
		ArrayDebug.sizesFormat = format;
	}

	public static void resetDoubleFormat() {
		ArrayDebug.doubleFormat = ArrayDebug.STANDARD_DOUBLE_FORMAT;
	}

	public static void resetByteFormat() {
		ArrayDebug.byteFormat = ArrayDebug.STANDARD_BYTE_FORMAT;
	}

	public static void resetIntFormat() {
		ArrayDebug.intFormat = ArrayDebug.STANDARD_INT_FORMAT;
	}

	public static void resetSizesFormat() {
		ArrayDebug.sizesFormat = ArrayDebug.STANDARD_SIZES_FORMAT;
	}

	public static void printArray(double[] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[ ]");
		} else {
			System.out.print(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				System.out.print(String.format("%" + ArrayDebug.doubleFormat + "f", a[i]));
				System.out.print(i==a.length-1 ? "]" : ", ");
			}
		}
	}

	public static void printArray(double[][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(double[][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(double[][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(double[][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(double[][][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(double[][][][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(byte[] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[ ]");
		} else {
			System.out.print(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				System.out.print(String.format("%" + ArrayDebug.byteFormat + "d", a[i]));
				System.out.print(i==a.length-1 ? "]" : ", ");
			}
		}
	}

	public static void printArray(byte[][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(byte[][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(byte[][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(byte[][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(byte[][][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(byte[][][][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(int[] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[ ]");
		} else {
			System.out.print(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				System.out.print(String.format("%" + ArrayDebug.intFormat + "d", a[i]));
				System.out.print(i==a.length-1 ? "]" : ", ");
			}
		}
	}

	public static void printArray(int[][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(int[][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(int[][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(int[][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(int[][][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(int[][][][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[\n" + ArrayDebug.printSpaces(s) + "]");
		} else {
			System.out.println(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printArray(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printSizes(double[] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}", a.length));
		}
	}

	public static void printSizes(double[][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n"
					+ ArrayDebug.printSpaces(s+1), a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], 0);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ", ");
			}
		}
	}

	public static void printSizes(double[][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "}[\n", a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printSizes(double[][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n", a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printSizes(double[][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n", a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printSizes(double[][][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n", a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printSizes(byte[] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n", a.length));
		}
	}

	public static void printSizes(byte[][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n" +
					ArrayDebug.printSpaces(s+1), a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], 0);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ", ");
			}
		}
	}

	public static void printSizes(byte[][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n", a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printSizes(byte[][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n", a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printSizes(byte[][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n", a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printSizes(byte[][][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n", a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printSizes(int[] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}", a.length));
		}
	}

	public static void printSizes(int[][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n" +
					ArrayDebug.printSpaces(s+1), a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], 0);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ", ");
			}
		}
	}

	public static void printSizes(int[][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n", a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printSizes(int[][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n", a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printSizes(int[][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n", a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printSizes(int[][][][][][] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "{ }");
		} else {
			System.out.print(ArrayDebug.printSpaces(s)+String.format("{:%" + ArrayDebug.sizesFormat + "d}[\n", a.length));
			for (int i=0;i<a.length;i++) {
				ArrayDebug.printSizes(a[i], s+1);
				System.out.print(i==a.length-1 ? "\n" + ArrayDebug.printSpaces(s) + "]" : ",\n");
			}
		}
	}

	public static void printArray(double[] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(double[][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(double[][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(double[][][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(double[][][][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(double[][][][][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(double[][][][][][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(byte[] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(byte[][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(byte[][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(byte[][][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(byte[][][][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(byte[][][][][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(byte[][][][][][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(int[] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(int[][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(int[][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(int[][][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(int[][][][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(int[][][][][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printArray(int[][][][][][][] a) {
		ArrayDebug.printArray(a, 0);
		System.out.println();
	}

	public static void printSizes(double[] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(double[][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(double[][][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(double[][][][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(double[][][][][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(double[][][][][][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(byte[] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(byte[][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(byte[][][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(byte[][][][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(byte[][][][][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(byte[][][][][][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(int[] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(int[][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(int[][][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(int[][][][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(int[][][][][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	public static void printSizes(int[][][][][][] a) {
		ArrayDebug.printSizes(a, 0);
		System.out.println();
	}

	private static String printSpaces(int s) {
		String ret = "";
		for (int i=s;i>0;i--) {
			ret += "   ";
		}
		return ret;
	}
}
