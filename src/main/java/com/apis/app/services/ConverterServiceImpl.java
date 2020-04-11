package com.apis.app.services;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.apis.app.utils.CommonUtil;

@Service
public class ConverterServiceImpl implements ConverterService {
	private static final Logger logger = LoggerFactory.getLogger(ConverterServiceImpl.class);

	@Value("${pojo.file.base.path}")
	private String fileBasePath;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Override
	public List<File> readAllFiles(String input, String uniqueFolder) throws IOException {
	    
		StringBuilder builder = new StringBuilder("");
		builder.append(fileBasePath);
		builder.append("/");
		builder.append(uniqueFolder);
		
	    File newDir = new File(fileBasePath, uniqueFolder);
	    newDir.mkdir();
	    
	    Path path = Files.write(Paths.get(builder.toString()+"/input.json"), input.getBytes());
	    URL inputURI = path.toFile().toURI().toURL();
	    String packageName = "com.pojo";  
        File outputPojoDirectory = new File(builder.toString());  
        outputPojoDirectory.mkdirs();  
        try {  
             commonUtil.convert2JSON(inputURI, outputPojoDirectory, packageName, "Parent");  
        } catch (IOException e) {  
        	logger.info("Encountered issue while converting to pojo:{} ", e.getMessage());
            logger.error(e.fillInStackTrace().getMessage()); 
        }  

	    List<File> files = null;
	    try (Stream<Path> walk = Files.walk(Paths.get(builder.toString()))) {

	    	files = walk.filter(f-> !f.toString().endsWith(".json") && f.toFile().isFile())
					.map(Path::toFile).collect(Collectors.toList());
			
		} catch (IOException e) {
			logger.error("Exception : ", e);
		}
	    
		return files;
	}

	@Override
	public void removeDirectory(String uniqueFolder) {
		StringBuilder builder = new StringBuilder("");
		builder.append(fileBasePath);
		builder.append("/");
		builder.append(uniqueFolder);
		try(Stream<Path> walk = Files.walk(Paths.get(builder.toString()))){
	    	walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
	    }catch (Exception e) {
	    	logger.error("Exception : ", e);
		}
	}
}
