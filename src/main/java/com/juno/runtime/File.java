package com.juno.runtime;

// ===== FILE OPERATIONS (nested module) =====

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Nested File module for file operations.
 * Provides smart file handling similar to Python.
 */
public class File {

	/**
	 * Smart file handle that automatically closes resources.
	 * Tracks whether the file is open and prevents double-closing.
	 */
	public static class Handle {
		private final BufferedReader reader;
		private final PrintWriter writer;
		private final String path;
		private boolean isOpen;
		private final boolean isReader;

		private Handle(String path, BufferedReader reader) {
			this.path = path;
			this.reader = reader;
			this.writer = null;
			this.isOpen = true;
			this.isReader = true;
		}

		private Handle(String path, PrintWriter writer) {
			this.path = path;
			this.reader = null;
			this.writer = writer;
			this.isOpen = true;
			this.isReader = false;
		}

		/**
		 * Read a line from the file.
		 */
		public String readLine() throws IOException {
			if (!isOpen) {
				throw new IOException("File is closed: " + path);
			}
			if (!isReader) {
				throw new IOException("File is not open for reading: " + path);
			}
			assert reader != null;
			return reader.readLine();
		}

		/**
		 * Read all content from the file.
		 */
		public String readAll() throws IOException {
			if (!isOpen) {
				throw new IOException("File is closed: " + path);
			}
			if (!isReader) {
				throw new IOException("File is not open for reading: " + path);
			}

			StringBuilder content = new StringBuilder();
			String line;
			while (true) {
				assert reader != null;
				if ((line = reader.readLine()) == null) break;
				content.append(line).append("\\n");
			}
			return content.toString();
		}

		/**
		 * Write a string to the file.
		 */
		public void write(String content) throws IOException {
			if (!isOpen) {
				throw new IOException("File is closed: " + path);
			}
			if (isReader) {
				throw new IOException("File is not open for writing: " + path);
			}
			assert writer != null;
			writer.write(content);
			writer.flush(); // Ensure content is written
		}

		/**
		 * Write a line to the file (adds newline).
		 */
		public void writeLine(String line) throws IOException {
			write(line + "\\n");
		}

		/**
		 * Close the file handle. Safe to call multiple times.
		 */
		public void close() throws IOException {
			if (isOpen) {
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
				isOpen = false;
			}
		}

		/**
		 * Check if the file handle is open.
		 */
		public boolean isOpen() {
			return isOpen;
		}

		/**
		 * Get the file path.
		 */
		public String getPath() {
			return path;
		}
	}

	/**
	 * Open a file for reading.
	 * Similar to Python's open(path, 'r').
	 */
	public static Handle openRead(String path) throws IOException {
		BufferedReader reader = Files.newBufferedReader(Paths.get(path));
		return new Handle(path, reader);
	}

	/**
	 * Open a file for writing (creates/overwrites).
	 * Similar to Python's open(path, 'w').
	 */
	public static Handle openWrite(String path) throws IOException {
		PrintWriter writer = new PrintWriter(Files.newBufferedWriter(
				Paths.get(path),
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING
		));
		return new Handle(path, writer);
	}

	/**
	 * Open a file for appending.
	 * Similar to Python's open(path, 'a').
	 */
	public static Handle openAppend(String path) throws IOException {
		PrintWriter writer = new PrintWriter(Files.newBufferedWriter(
				Paths.get(path),
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND
		));
		return new Handle(path, writer);
	}

	/**
	 * Read entire file content as string.
	 * Convenience method for small files.
	 */
	public static String readAll(String path) throws IOException {
		return Files.readString(Paths.get(path));
	}

	/**
	 * Write string content to file (creates/overwrites).
	 * Convenience method for small files.
	 */
	public static void writeAll(String path, String content) throws IOException {
		Files.writeString(Paths.get(path), content);
	}

	/**
	 * Check if a file exists.
	 */
	public static boolean exists(String path) {
		return Files.exists(Paths.get(path));
	}

	/**
	 * Delete a file.
	 */
	public static boolean delete(String path) throws IOException {
		return Files.deleteIfExists(Paths.get(path));
	}
}
