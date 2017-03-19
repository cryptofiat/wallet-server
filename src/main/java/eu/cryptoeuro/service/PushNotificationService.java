package eu.cryptoeuro.service;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import eu.cryptoeuro.config.ContractConfig;
import eu.cryptoeuro.rest.command.NotifyTransferCommand;

import org.springframework.beans.factory.annotation.Value;
import java.util.Properties;  
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStreamWriter;
import org.json.JSONObject;
import org.json.JSONException;

  
@Slf4j
@Component
public class PushNotificationService {  

  @Autowired
  private ContractConfig contractConfig;

  @Value("${firebase.url}")
  private String firebaseUrl;

  @Value("${firebase.messaging.server.key}")
  private String messagingKey;

  @Value("${firebase.super.key}")
  private String superKey;

  public void pushNotifyTransfer(NotifyTransferCommand cmd)  {

                try {
                          JSONObject cmdJson = new JSONObject(cmd);

                          byte[] postDataBytes = cmdJson.toString().getBytes("UTF-8");

                          URL url = new URL(firebaseUrl+"/push/"+cmd.getAddress()+".json?auth="+superKey);
                          HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                          httpCon.setRequestMethod("POST");
                          httpCon.setRequestProperty("Content-Type", "application/json");
                          httpCon.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                          httpCon.setDoOutput(true);
                          httpCon.getOutputStream().write(postDataBytes);

                          OutputStreamWriter out = new OutputStreamWriter(
                              httpCon.getOutputStream());
                          log.info("Putting ref: "+httpCon.getResponseCode()+ " msg: " + httpCon.getResponseMessage());
                          out.close();
                } catch (IOException io) {
                        log.warn("IO excpetion");
                } catch (JSONException ex) {
                        log.warn("Malformed JSON command in pushing notification");
                }


  }
    
}  
