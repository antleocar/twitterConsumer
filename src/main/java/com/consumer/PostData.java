package com.consumer;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class PostData {

  public HttpHeaders headers;
  private RestTemplate template;
  public static final String SERVER = "";

  public PostData(){
    this.template = new RestTemplate();
    this.headers = new HttpHeaders();
    headers.add("Content-Type", "multipart/form-data");
    headers.add("Accept", "*/*");
  }

  public void postData(byte[] bytes) {
    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("file", new ByteArrayResource(bytes, "Data") {
          @Override
          public String getFilename() {
            return "Data.docx";
          }
        });
      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
      this.template.postForObject(SERVER, requestEntity, String.class);
    }
}
