package com.bajajHealthService.destination_hash_generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

@Service
public class HashService {

    public String generateHash(String prn, String jsonFilePath) {
        try {
            // Parse JSON file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(jsonFilePath));
            String destinationValue = findDestinationValue(rootNode);

            if (destinationValue == null) {
                return "No 'destination' key found in JSON.";
            }

            // Generate a random string
            String randomString = generateRandomString(8);

            // Concatenate and hash
            String toHash = prn.toLowerCase() + destinationValue + randomString;
            String hash = generateMD5Hash(toHash);

            // Return the result
            return hash + ";" + randomString;

        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading the JSON file.";
        }
    }

    private String findDestinationValue(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().equals("destination")) {
                    return field.getValue().asText();
                }
                String result = findDestinationValue(field.getValue());
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                String result = findDestinationValue(arrayElement);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder(2 * messageDigest.length);
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}