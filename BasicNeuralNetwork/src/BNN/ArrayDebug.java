package BNN;

public final class ArrayDebug {
	public static void printArray(double[] a, int s) {
		if (a == null) {
			System.out.print(ArrayDebug.printSpaces(s) + "[ ]");
		} else {
			System.out.print(ArrayDebug.printSpaces(s) + "[");
			for (int i=0;i<a.length;i++) {
				System.out.print(String.format("% 2.8f", a[i]));
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

	private static String printSpaces(int s) {
		String ret = "";
		for (int i=s;i>0;i--) {
			ret += "   ";
		}
		return ret;
	}
}
