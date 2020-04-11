package com.apis.app.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.apis.app.services.ConverterServiceImpl;

@RestController
@RequestMapping("/home")
@CrossOrigin(origins = "*")
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private ConverterServiceImpl converterServiceImpl;

	@GetMapping("/welcome")
	public ResponseEntity<String> welcome() {
		String resp = "{\"key\":\"This content is provided by server\"}";		
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}
	
	@PostMapping(value = "/create", produces = MediaType.APPLICATION_PDF_VALUE)
	public void createPojo(HttpServletResponse response, @RequestBody String reqBody) throws IOException {
		logger.debug("Request data : {} ", reqBody);
		String uniqueFolder = Base64.getEncoder().encodeToString(reqBody.getBytes());
		response.setStatus(HttpServletResponse.SC_OK);
	    response.addHeader("Content-Disposition", "attachment; filename=\"JsonPojo.zip\"");
	    ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
	    
	    List<File> files = converterServiceImpl.readAllFiles(reqBody, uniqueFolder);
	    
	    for (File file : files) {
	        zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
	        FileInputStream fileInputStream = new FileInputStream(file);

	        IOUtils.copy(fileInputStream, zipOutputStream);

	        fileInputStream.close();
	        zipOutputStream.closeEntry();
	    }    
	    zipOutputStream.close();
	    
	    converterServiceImpl.removeDirectory(uniqueFolder);
	    
	    logger.debug("******Process executed successfully.******** ");
	}
}