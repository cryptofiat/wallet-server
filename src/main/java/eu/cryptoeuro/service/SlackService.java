package eu.cryptoeuro.service;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;
import in.ashwanthkumar.slack.webhook.Slack;
import in.ashwanthkumar.slack.webhook.SlackMessage;
import java.io.IOException;
import eu.cryptoeuro.rest.command.CreateBankTransferCommand;


  
@Slf4j
@Component
public class SlackService {  

  @Value("${slack.webhook.bankinstruction.url}")
  private String webhookUrl;

  public void notifyPayout(long amount, String recipientName, String toIBAN, String  txHash, String ref) throws IOException {

	new Slack(webhookUrl)
	    //.icon(":smiling_imp:") // Ref - http://www.emoji-cheat-sheet.com/
	    //.sendToUser("slackbot")
	    //.displayName("slack-java-client")
	    .push(new SlackMessage("Send € ")
		.bold(String.format("%.2f",(float) amount / 100))
		.text(" to ")
		.bold(recipientName)
		.text(" at ")
		.code(toIBAN)
		.text(" with ref ")
		.bold(ref)
		.text(" from tx ")
		.link("https://etherscan.io/tx/"+txHash));

  }

  public void notifyPayout(CreateBankTransferCommand bankTransfer,String txHash) {
	try {
		notifyPayout(
			bankTransfer.getAmount(),
			bankTransfer.getRecipientName(),
			bankTransfer.getTargetBankAccountIBAN(),
			txHash,
			bankTransfer.getReference()
		);
	} catch (IOException ex) {
		log.error("Failed sending slack message: ", ex);
		
	}
		
  }
   
}  
