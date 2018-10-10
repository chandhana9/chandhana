package discord;

import java.util.Date;

import javax.security.auth.login.LoginException;

import bot.discord.dbsk.App;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDA.Status;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Discord  extends ListenerAdapter{
	
	private JDA jda;
	private String name = "Bot";
	
	//Use this constructor if you only have the token
	public Discord(String token) {
		try {
			jda = new JDABuilder(AccountType.BOT).setToken(token).build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
		
		waitForConnection();
	}
	
	//Use this constructor if you're using this starter kit to make something other than a chat bot, although this kit is not very compatible with anything else.
	public Discord(AccountType at) {
		try {
			jda = new JDABuilder(at).build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
		
		waitForConnection();
	}
	
	//Use this constructor if you have already initialised a JDA
	public Discord(JDA jda) {
		this.jda = jda;
		waitForConnection();
	}
		
	public JDA getJDA() {
		return jda;
	}
	
	public boolean isAppOnline() {
		return jda.getStatus().equals(Status.CONNECTED);
	}
	
	public void waitForConnection() {
		//Wait until the app is online.
		while(!isAppOnline()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStatusMessage() {
		Date currentDate = new Date();
		String date = Constants.DEFAULT_DATE_FORMAT.format(currentDate);
		String statusMessage = date + " " + name + " is online.";
		return statusMessage;
	}
	
	//Sends a message to the default channel, which is #dev
	public boolean sendMessage(String message) {
		//If the message is succesfully sent, return true, otherwise return false
		try {
			//Use the default channel
			MessageChannel objMsgCh = jda.getTextChannelById(Constants.DEFAULT_CHANNEL_ID);
			objMsgCh.sendMessage(message).queue();
			
			//No errors met, return true.
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			//Exception found, return false.
			return false;
		}
	}
	
	//Sends a message to the channel with the specificed id
	public boolean sendMessage(String message, Long id) {
		try {
			MessageChannel objMsgCh = jda.getTextChannelById(id);
			objMsgCh.sendMessage(message).queue();
			
			//No errors met, return true.
			return true;
		}catch(Exception e) {
			//Exception found, return false.
			return false;
		}
	}
	
	//Sends a message to the specified MessageChannel
	public boolean sendMessage(String message, MessageChannel objMsgCh) {
		try {
			objMsgCh.sendMessage(message).queue();
			
			//No errors met, return true.
			return true;
		}catch(Exception e) {
			//Exception found, return false.
			return false;
		}
	}
	
	@Override
    public void onMessageReceived(MessageReceivedEvent evt) {
		App.messageEvent(evt);
	}
	
	@Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
		App.emojiEvent(event);
	}
	
	
	
}
