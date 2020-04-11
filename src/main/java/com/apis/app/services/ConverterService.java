package com.apis.app.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ConverterService {
	List<File> readAllFiles(String input, String uniqueFolder) throws IOException;
	
	void removeDirectory(String uniqueFolder);
}
