package com.consumer;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
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

  }
}
