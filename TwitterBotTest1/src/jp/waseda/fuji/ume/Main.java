package jp.waseda.fuji.ume;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;

public class Main {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws TwitterException {

		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer("","");
		AccessToken accessToken = new AccessToken("", "");
		twitter.setOAuthAccessToken(accessToken);
	
		Wakemeshi wakemeshi = new Wakemeshi();

		if (args.length>=1){
			wakemeshi.twit(twitter,args);
		}
		wakemeshi.reply(twitter);


	}

}
