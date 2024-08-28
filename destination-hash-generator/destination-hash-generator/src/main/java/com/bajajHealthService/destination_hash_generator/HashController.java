package com.bajajHealthService.destination_hash_generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HashController {
	
	 @Autowired
	    private HashService hashService;
	 @GetMapping("/generate-hash")
	    public String generateHash(
	            @RequestParam String prn,
	            @RequestParam String jsonFilePath) {
	        return hashService.generateHash(prn, jsonFilePath);
	    }

}
