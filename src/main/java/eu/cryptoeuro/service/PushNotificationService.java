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
import java.io.InputStreamReader;
import java.io.BufferedReader;
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

                          //log.info("Putting msg: "+cmdJson.toString()+ " to: " + url.toString());

                          HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                          httpCon.setRequestMethod("POST");
                          httpCon.setRequestProperty("Content-Type", "application/json");
                          httpCon.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                          httpCon.setDoOutput(true);
                          httpCon.getOutputStream().write(postDataBytes);
                          httpCon.getOutputStream().flush();
                          httpCon.getOutputStream().close();
			  BufferedReader rd = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
			  String line;
			  // just print  out the first line ... this should be returned to controller  as json/object
			  if ((line = rd.readLine()) != null) {
			     System.out.println( "Push server responded for "+cmd.getAddress()+" with: " + line);
			  }
			  rd.close();
			/* TODO: best to replace  with a check if {"name":"-Kfa44Yk16G_UcfRKw9H"} is returned
			*/
                } catch (IOException io) {
                        log.warn("IO excpetion");
                } catch (JSONException ex) {
                        log.warn("Malformed JSON command in pushing notification");
                }


  }
    
}  
