package net.nightpool.bukkit.nightutils;

public class BadArgException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BadArgException(String string) {
		super(string);
	}

	public BadArgException() {
		super();
	}

}
