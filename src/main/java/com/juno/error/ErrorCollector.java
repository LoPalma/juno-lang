package com.juno.error;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects multiple compiler errors and warnings for batch reporting.
 * Allows the compiler to continue after encountering errors.
 */
public class ErrorCollector {
	private final List<CompilerError> errors = new ArrayList<>();
	private final List<CompilerError> warnings = new ArrayList<>();

	public void addError(CompilerError error) {
		if (error.getErrorCode().isError()) {
			errors.add(error);
		}
		else {
			warnings.add(error);
		}
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}

	public int getErrorCount() {
		return errors.size();
	}

	public int getWarningCount() {
		return warnings.size();
	}

	public List<CompilerError> getErrors() {
		return new ArrayList<>(errors);
	}

	public List<CompilerError> getWarnings() {
		return new ArrayList<>(warnings);
	}

	public List<CompilerError> getAllIssues() {
		List<CompilerError> all = new ArrayList<>();
		all.addAll(errors);
		all.addAll(warnings);
		return all;
	}

	public void clear() {
		errors.clear();
		warnings.clear();
	}

	/**
	 * Prints all collected errors and warnings to stderr.
	 */
	public void printAll() {
		for (CompilerError error : errors) {
			System.err.println(ErrorReporter.formatError(error));
			System.err.println(); // Add blank line between errors
		}

		for (CompilerError warning : warnings) {
			System.err.println(ErrorReporter.formatError(warning));
			System.err.println(); // Add blank line between warnings
		}
	}

	/**
	 * Creates a summary message about the compilation results.
	 */
	public String getSummary() {
		if (hasErrors() && hasWarnings()) {
			return String.format("Compilation failed with %d error(s) and %d warning(s).",
													 getErrorCount(), getWarningCount());
		}
		else if (hasErrors()) {
			return String.format("Compilation failed with %d error(s).", getErrorCount());
		}
		else if (hasWarnings()) {
			return String.format("Compilation completed with %d warning(s).", getWarningCount());
		}
		else {
			return "Compilation completed successfully.";
		}
	}
}