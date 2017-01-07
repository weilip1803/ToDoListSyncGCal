package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @@author A0121628L
 *
 */
public class FileHandler {
	/**
	 * This method rewrites the file with the given text.
	 * 
	 * 
	 * @param file
	 * @param text
	 * @throws IOException
	 */
	public static void writeStringToFile(File file, String text)
			throws IOException {
		FileWriter out = new FileWriter(file);
		out.write(text);
		out.close();
	}

	/**
	 * This method get string from the file given the file path
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String getStringFromFile(String path)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}
}
