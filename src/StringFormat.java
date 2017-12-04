public class StringFormat {
	
	
	public static String dec(double number, int digits, int commaDigits, boolean signed, char symbol) {
		String str = (signed ? (number < 0 ? "-" : " ") : "") + (int)(number = Math.abs(number));
		if (commaDigits > 0) {
			int comma = (int)Math.round((number - Math.floor(number)) * Math.pow(10, commaDigits));
			comma -= (comma >= Math.pow(10, commaDigits-1) ? 1 : 0);
			str = str + "." + dec(comma, commaDigits, '0');
		}
		while (number<Math.pow(10, digits-1) && digits != 1) {
			str = symbol + str;
			number = (number == 0 ? 1: number)*10;
		}
		return str;
	}
	
	public static String dec(int number, int digits, boolean signed, char symbol) {
		return dec((double)number, digits, 0, signed, symbol);
	}
	
	public static String dec(int number, int digits, char symbol) {
		return dec((double)number, digits, 0, false, symbol);
	}
	
	public static String dec(int number, int digits) {
		return dec((double)number, digits, 0, false, ' ');
	}
	
}
