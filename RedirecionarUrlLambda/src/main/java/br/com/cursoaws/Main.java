package br.com.cursoaws;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private final S3Client s3Client = S3Client.builder().build();
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
		
		Map<String, Object> response = new HashMap<>();
		
		String paramUUID = (String) input.get("rawPath");
		
		String codigoUUID = paramUUID.replace("/", "");
		
		if (codigoUUID == null || codigoUUID.isEmpty()) {
			throw new IllegalArgumentException("Código de entrada inválido, informe o código de entrada");
		}
		
		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket("bd-url-curtas")
				.key(codigoUUID + ".json")
				.build();
		
		InputStream s3ObjectInputStream;
		
		try {
			s3ObjectInputStream = s3Client.getObject(getObjectRequest);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao consultar url de dados no s3 aws" + e.getMessage());
		}
		
		DadosUrl dadoUrl;
		
		try {
			
			dadoUrl = objectMapper.readValue(s3ObjectInputStream, DadosUrl.class);
			
		} catch (Exception e) {
			throw new RuntimeException("Erro ao converter json em objeto com dados da url: " + e.getMessage()); 
		}
		
		long tempoAtual = System.currentTimeMillis() / 1000;
		
		if (tempoAtual < dadoUrl.getExpiracaoTempo()) {
			
			response.put("statusCode", 302);
			
			Map<String, String> headers = new HashMap<>();
			headers.put("Location", dadoUrl.getOriginalUrl());
			
			response.put("headers", headers);
			
			return response;
		} else {
			response.put("statusCode", 410);
			response.put("body", "Está URL está expirada");
			
			return response;
		}
	}
	
}
