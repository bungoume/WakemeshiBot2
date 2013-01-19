package jp.waseda.fuji.ume;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class GetAccessToken {
	void getAccessToken(Twitter twitter) throws TwitterException{
		RequestToken requestToken = twitter.getOAuthRequestToken();
		AccessToken accessToken = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (null == accessToken) {
			System.out.println("Open the following URL and grant access to your account:");
			System.out.println(requestToken.getAuthorizationURL());
			System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
			try{
				String pin = br.readLine();
				if(pin!=null && pin.length() > 0){
					accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				}else{
					accessToken = twitter.getOAuthAccessToken();
				}
			} catch (TwitterException te) {
				if(401 == te.getStatusCode()){
					System.out.println("Unable to get the access token.");
				}else{
					te.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//将来の参照用に accessToken を永続化する
		storeAccessToken(twitter.verifyCredentials().getId() , accessToken);
	}

	private static void storeAccessToken(int useId, AccessToken accessToken){
		System.out.println(accessToken.getToken());
		System.out.println(accessToken.getTokenSecret());
	}
}
