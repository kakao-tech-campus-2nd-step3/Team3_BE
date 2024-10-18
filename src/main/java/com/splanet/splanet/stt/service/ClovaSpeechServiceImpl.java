package com.splanet.splanet.stt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.splanet.splanet.core.exception.BusinessException;
import com.splanet.splanet.core.exception.ErrorCode;
import com.splanet.splanet.core.properties.ClovaProperties;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClovaSpeechServiceImpl implements ClovaSpeechService {

    private final ClovaProperties clovaProperties;

    public ClovaSpeechServiceImpl(ClovaProperties clovaProperties) {
        this.clovaProperties = clovaProperties;
    }

    @Override
    public String recognize(MultipartFile file) {
        String apiURL = clovaProperties.getUrl() + "?lang=" + clovaProperties.getLanguage();  // 언어 설정 반영
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            byte[] audioBytes = file.getBytes();

            HttpPost httpPost = new HttpPost(apiURL);
            httpPost.addHeader("Content-Type", "application/octet-stream");
            httpPost.addHeader("X-NCP-APIGW-API-KEY-ID", clovaProperties.getClientId());
            httpPost.addHeader("X-NCP-APIGW-API-KEY", clovaProperties.getClientSecret());

            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(audioBytes, ContentType.APPLICATION_OCTET_STREAM);
            httpPost.setEntity(byteArrayEntity);

            return httpClient.execute(httpPost, response -> {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

                if (statusCode == 200) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    return rootNode.path("text").asText();
                } else {
                    throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "CLOVA Speech API 호출 실패: " + responseBody);
                }
            });
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "오디오 파일 처리 중 오류 발생: " + e.getMessage());
        }
    }
}