package com.tcs.artifacts.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.tcs.artifacts.exception.PipelineArtifactFileCreationException;

public class CommonJavaUtil {

	/**
	 * Creates a file with specified name at specified directory & Writes content to
	 * this file.
	 * 
	 * @param fileName
	 * @param content
	 * @return
	 */
	public static void writeToFile(Path directory, String fileName, String content)
			throws PipelineArtifactFileCreationException {

		String outputPath = null;
		try {
			if (!Files.exists(directory)) {
				Files.createDirectories(directory);
			}

			Path path = directory.resolve(fileName);
			Files.deleteIfExists(path);
			Files.write(path, content.getBytes(), StandardOpenOption.CREATE_NEW);

			outputPath = directory.toString();

		} catch (IOException e) {
			throw new PipelineArtifactFileCreationException(
					"Could not create & write file " + fileName + " to directory -" + outputPath, e);
		}
	}

}
