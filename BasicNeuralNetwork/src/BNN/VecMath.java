package BNN;

public class VecMath {
	public static double sigmoid(double x) {
		return 1.0/(1.0+Math.exp(-x));
	}

	public static double[] sigmoid(double[] x) {
		double[] ret = new double[x.length];
		for (int i=x.length-1;i>=0;i--) {
			ret[i] = VecMath.sigmoid(x[i]);
		}
		return ret;
	}

	public static double sigmoidPrime(double x) {
		return VecMath.sigmoid(x)*(1-VecMath.sigmoid(x));
	}

	public static double[] sigmoidPrime(double[] x) {
		double[] ret = new double[x.length];
		for (int i=x.length-1;i>=0;i--) {
			ret[i] = VecMath.sigmoidPrime(x[i]);
		}
		return ret;
	}

	public static double reLu(double a) {
		return Math.max(0, a);
	}

	public static double[] reLu(double[] a) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.reLu(a[i]);
		}
		return ret;
	}

	public static double norm(double[] a) {
		double ret = 0;
		for (int i=a.length-1;i>=0;i--) {
			ret += Math.pow(a[i], 2);
		}
		return Math.sqrt(ret);
	}

	public static double norm(double[][] a) {
		double ret = 0;
		for (int i=a.length-1;i>=0;i--) {
			for (int j=a[i].length-1;j>=0;j--) {
				ret += Math.pow(a[i][j], 2);
			}
		}
		return Math.sqrt(ret);
	}

	public static double[] add(double[] a, double[] b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = a[i] + b[i];
		}
		return ret;
	}

	public static double[][] add(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.add(a[i], b[i]);
		}
		return ret;
	}

	public static double[] add(double[] a, double b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = a[i] + b;
		}
		return ret;
	}

	public static double[][] add(double[][] a, double b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.add(a[i], b);
		}
		return ret;
	}

	public static double[] subtract(double[] a, double[] b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = a[i] - b[i];
		}
		return ret;
	}

	public static double[][] subtract(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.subtract(a[i], b[i]);
		}
		return ret;
	}

	public static double[] multiply(double[] a, double[] b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = a[i] * b[i];
		}
		return ret;
	}

	public static double[][] multiply(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.multiply(a[i], b[i]);
		}
		return ret;
	}

	public static double[] multiply(double[] a, double b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = a[i] * b;
		}
		return ret;
	}

	public static double[][] multiply(double[][] a, double b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.multiply(a[i], b);
		}
		return ret;
	}

	public static double sum(double[] a) {
		double ret = 0;
		for (int i=a.length-1;i>=0;i--) {
			ret += a[i];
		}
		return ret;
	}

	public static double sum(double[][] a) {
		double ret = 0;
		for (int i=a.length-1;i>=0;i--) {
			ret += VecMath.sum(a[i]);
		}
		return ret;
	}

	public static double[] log(double[] a) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = Math.log(a[i]);
		}
		return ret;
	}

	public static double[][] log(double[][] a) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.log(a[i]);
		}
		return ret;
	}

	public static double[][] dot(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			for (int j=a[i].length-1;j>=0;j--) {
				double temp = 0;
				for (int k=a[i].length-1;k>=0;k--) {
					temp += a[i][k] * b[k][i];
				}
				ret[i][j] = temp;
			}
		}
		return ret;
	}

	public static double[] dot(double[][] a, double[] b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			double temp = 0;
			for (int k=a[i].length-1;k>=0;k--) {
				temp += a[i][k] * b[k];
			}
			ret[i] = temp;
		}
		return ret;
	}

	public static double[] dot(double[] a, double[][] b) {
		double[] ret = new double[b[0].length];
		for (int i=ret.length-1;i>=0;i--) {
			double temp = 0;
			for (int k=a.length-1;k>=0;k--) {
				temp += a[k] * b[k][i];
			}
			ret[i] = temp;
		}
		return ret;
	}

	public static double[][] dot(double[] a, double[] b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = new double[b.length];
			for (int j=b.length-1;j>=0;j--) {
				ret[i][j] = a[i] * b[j];
			}
		}
		return ret;
	}

	public static double[][] transpose(double[][] a) {
		double[][] ret = new double[a[0].length][];
		for (int i=ret.length-1;i>=0;i--) {
			ret[i] = new double[a.length];
			for (int j=ret[i].length-1;j>=0;j--) {
				ret[i][j] = a[j][i];
			}
		}
		return ret;
	}
}