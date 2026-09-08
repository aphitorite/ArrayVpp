package io.github.arrayv.utils;


import java.util.HashMap;
import java.util.function.UnaryOperator;

public final class Constants {
	private static long fact(Long n) {
		int m = (int) (long) n; // wtf
		while(--m>1) n*=m;
		return n;
	}
	@SuppressWarnings("serial")
	public static final HashMap<String, UnaryOperator<Long>> constants = new HashMap<String, UnaryOperator<Long>>() {{
		put("unknown", n -> -1L);
		
		// linearithmic
		put("n log n", n -> n * (long) (Math.log(n) / Math.log(2D)));
		put("n log^1.5 n", n -> (long) (n * Math.pow(Math.log(n) / Math.log(2D), 1.5)));
		put("n log^2 n", n -> {
			long log = (long) (Math.log(n) / Math.log(2D));
			return n * log * log;
		});
		put("n log^3 n", n -> {
			long log = (long) (Math.log(n) / Math.log(2D));
			return n * log * log;
		});
		
		// tsrt ~ fcrt (n^5/4 ~ n^5/3)
		put("n tsrt n", n -> n * (long) (Math.sqrt(Math.sqrt(n))));
		put("n cbrt n", n -> n * (long) (Math.cbrt(n)));
		put("n sqrt n", n -> n * (long) (Math.sqrt(n)));
		put("n fcrt n", n -> n * (n / (long) (Math.cbrt(n))));
		
		// fractional exponents that haven't been properly named yet
		put("n^1.75", n -> n * (n / (long) (Math.sqrt(Math.sqrt(n)))));
		
		put("n^2.5", n -> n * n * (long) (Math.sqrt(n)));
		
		
		// straight exponents up to 10
		put("n",    n -> n);
		put("n^2",  n -> n * n);
		put("n^3",  n -> n * n * n);
		put("n^4",  n -> n * n * n * n);
		put("n^5",  n -> n * n * n * n * n);
		put("n^6",  n -> n * n * n * n * n * n);
		put("n^7",  n -> n * n * n * n * n * n * n);
		put("n^8",  n -> n * n * n * n * n * n * n * n);
		put("n^9",  n -> n * n * n * n * n * n * n * n * n);
		put("n^10", n -> n * n * n * n * n * n * n * n * n * n);
		put("n^n", n -> (long) Math.pow(n, n));

		put("n^2 log^-2 n", n -> {
			long log = (long) (Math.log(n) / Math.log(2D));
			return n * n / log / log;
		});
		put("2^n", n -> 1L << (long)n);
		put("2^n/2", n -> (long)Math.pow(Math.sqrt(2),n));
		
		// specialized
		put("n^1.5 log^0.5 n", n -> (long) (n * Math.pow(n * Math.log(n) / Math.log(2D), 0.5)));
		put("bosenelson", n -> (long) Math.pow(n, Math.log(3) / Math.log(2)));
		put("goblin", n -> (long) (n * n * n) * (long) (Math.pow(n * fact(n), fact(n) + 1)));
	}};
}