/*
 * FileFilterExtensions.java
 * Created on 02.06.2004 at 11:13:24. 
 */
package cpdetector.io;

import java.io.File;
import java.io.FileFilter;
import java.util.StringTokenizer;


/**
 * <p>
 * Configureable implementation of {@link java.io.FileFilter} used for 
 * selection of the test documents under the collection root. 
 * </p>
 * <p>
 * Kept private because no state-verification is performed: The configuration 
 * may (but shouldn't) be made at runtime. 
 * </p>
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 */
public final class FileFilterExtensions implements FileFilter {
	private String[] extensions;
	/**
	 * 
	 * @param extensionsWithoutDot A String[] containing extension strings without the dot like: <nobr><code>new String[]{"bat","txt","dict"}</code></nobr>. 
	 */
	public FileFilterExtensions(String[] extensionsWithoutDot) throws IllegalArgumentException {
		this.verify(extensionsWithoutDot);
		this.extensions = extensionsWithoutDot;
	}

	/**
	 * @param extensions The array with the Strings of extensions.
	 * @throws IllegalArgumentException If a String of the array is null or contains a dot ('.').
	 */
	private void verify(String[] extensions) throws IllegalArgumentException {
		String current;
		StringBuffer msg = new StringBuffer();
		for (int i = extensions.length - 1; i >= 0; i--) {
			current = extensions[i];
			if (current == null) {
				msg.append("Extension at index " + i + " is null!\n");
			} else if (current.indexOf('.') != -1) {
				msg.append("Extension \"" + current + "\" contains a dot!\n");
			}
		}
		if (msg.length() > 0) {
			throw new IllegalArgumentException(msg.toString());
		}
	}

	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		boolean ret = false;
		// search for extension without dot. 
		StringTokenizer tokenizer = new StringTokenizer(pathname.getAbsolutePath(), ".");
		String extension = "no.txt"; // a dot, because verify will not allow these tokens: won't accept, if no extension in pathname.
		while (tokenizer.hasMoreElements()) {
			extension = tokenizer.nextToken();
		}
		for (int i = this.extensions.length - 1; i >= 0; i--) {
			if (this.extensions[i].equals(extension)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

}