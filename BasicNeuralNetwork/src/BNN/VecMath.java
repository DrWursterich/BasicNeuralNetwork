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

	public static double[] softmax(double[] a) {
		return VecMath.log(VecMath.divide(VecMath.exp(a), VecMath.sum(a)));
	}

	public static double[][] softmax(double[][] a) {
		return VecMath.log(VecMath.divide(VecMath.exp(a), VecMath.sum(a)));
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

	public static double[] divide(double[] a, double b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = a[i] / b;
		}
		return ret;
	}

	public static double[][] divide(double[][] a, double b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.divide(a[i], b);
		}
		return ret;
	}

	public static double[] divide(double[] a, double[] b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = a[i] / b[i];
		}
		return ret;
	}

	public static double[][] divide(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.divide(a[i], b[i]);
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

	public static double[] pow(double[] a, double b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = Math.pow(a[i], b);
		}
		return ret;
	}

	public static double[][] pow(double[][] a, double b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.pow(a[i], b);
		}
		return ret;
	}

	public static double[] pow(double a, double[] b) {
		double[] ret = new double[b.length];
		for (int i=b.length-1;i>=0;i--) {
			ret[i] = Math.pow(a, b[i]);
		}
		return ret;
	}

	public static double[][] pow(double a, double[][] b) {
		double[][] ret = new double[b.length][];
		for (int i=b.length-1;i>=0;i--) {
			ret[i] = VecMath.pow(a, b[i]);
		}
		return ret;
	}

	public static double[] pow(double[] a, double[] b) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = Math.pow(a[i], b[i]);
		}
		return ret;
	}

	public static double[][] pow(double[][] a, double[] b) {
		double[][] ret =  new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.pow(a[i], b[i]);
		}
		return ret;
	}

	public static double[][] pow(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][];
		for (int i=a.length;i>=0;i--) {
			ret[i] = VecMath.pow(a[i], b[i]);
		}
		return ret;
	}

	public static double exp(double a) {
		return Math.pow(Math.E, a);
	}

	public static double[] exp(double[] a) {
		double[] ret = new double[a.length];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.exp(a[i]);
		}
		return ret;
	}

	public static double[][] exp(double[][] a) {
		double[][] ret = new double[a.length][];
		for (int i=a.length-1;i>=0;i--) {
			ret[i] = VecMath.exp(a[i]);
		}
		return ret;
	}

	public static double[][] transpose(double[] a) {
		double[][] ret = new double[a.length][];
		for (int i=ret.length-1;i>=0;i--) {
			ret[i] = new double[1];
			ret[i][0] = a[i];
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

	public static double[] merge(double[][] a) {
		int sumLength = 0;
		for (int i=a.length-1;i>=0;i--) {
			sumLength += a[i].length;
		}
		double[] ret = new double[sumLength];
		for (int i=a.length-1;i>=0;i--) {
			for (int j=a[i].length-1;j>=0;j--) {
				ret[--sumLength] = a[i][j];
			}
		}
		return ret;
	}
}
