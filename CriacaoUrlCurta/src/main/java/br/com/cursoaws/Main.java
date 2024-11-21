package br.com.cursoaws;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {
	//Cria um objeto usando Jackson para converter eobejtos map
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	//Cria uma instacia do S3 client aws do package awssdk 
	private final S3Client s3Client = S3Client.builder().build();
	
	//sobresscreve a função handlerRequest da implementação da função aws services lambdas mais espcifica as funções
	@Override
	public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
		String body = (String) input.get("body");	
		Map<String, String> bodyMap;
		try {
			bodyMap = objectMapper.readValue(body, Map.class);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao converter body em string: " + e.getMessage()); 
		}
		
		String orginalUrl = bodyMap.get("originalUrl");
		String expiracaoTempo = bodyMap.get("expiracaoTempo");
		
		String codigoCurto = UUID.randomUUID().toString().substring(0, 8);
		
		DadosUrl dadoUrl = new DadosUrl(orginalUrl, Long.parseLong(expiracaoTempo));
		
		try {
			String dadoUrlJson = objectMapper.writeValueAsString(dadoUrl);
			//Monto a requsicao http usando o metodo da classe services da awsSDk para gravar dados fisicos no S3 aws
			PutObjectRequest request = PutObjectRequest.builder()
					.bucket("bd-url-curtas")
					.key(codigoCurto + ".json")
					.build();
			//envia os dados para o S3
			s3Client.putObject(request, RequestBody.fromString(dadoUrlJson));
		} catch (Exception e) {
			throw new RuntimeException("Erro ao gravar arquivo de url encurtada para S3: " + e.getMessage());
		}
		//monta a resposta para o usuário da api
		Map<String, String> resposta = new HashMap<>();
		resposta.put("codigo", codigoCurto);
		
		return resposta;
		
	}
}
