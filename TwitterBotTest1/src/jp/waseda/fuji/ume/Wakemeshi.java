package jp.waseda.fuji.ume;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


/**
 * [class]Wakemeshi
 * 
 */
public class Wakemeshi {
	public static final String lastPostIdLogFile = "WakemeReplyLast.log";
	private static final int REPLY_TYPE_MENU = 1;
	private static final int REPLY_TYPE_RT = 2;
	private static final int REPLY_TYPE_FEEDBACK = 3;
	private static final int REPLY_TYPE_THX = 4;
	private static final boolean DEBUG = true;
	private Menu menu = new Menu();

	public void twit(Twitter instTwit, String[] args) {
		GregorianCalendar menuDate = new GregorianCalendar();
		int menuTime = Integer.parseInt(args[0]);
		switch(menuTime) {
		case Menu.MENU_TIME_ASA:
		case Menu.MENU_TIME_HIRU:
		case Menu.MENU_TIME_YORU:
			break;
		default:
			return;
		}
		try {
			System.out.println(menu.getMenu(menuDate, menuTime));
			if(!DEBUG){
				instTwit.updateStatus(menu.getMenu(menuDate, menuTime));
				System.out.println("呟きました!");	
			}
		} catch (TwitterException e) {
			System.err.println("Twitter関係のエラー");
			e.printStackTrace();
		}
		
	}
	
	public void reply(Twitter instTwit) {
		// file: "WakemeReplyLast.log" に、最後にリプを返した対象のTweetIDが保存されている。
		// このファイルを読み込んで、どのTweetに対してリプを返さなければいけないかを判断する。
		long lastPostId = 0;
		try {
			FileReader in = new FileReader(lastPostIdLogFile);
			BufferedReader br = new BufferedReader(in);
			lastPostId = Long.parseLong(br.readLine());
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("指定されたパス名で示されるファイルが開けなかったことを通知します。  ");
			System.out.println(e.getMessage());
			e.printStackTrace();
			lastPostId = 0;
		} catch (NumberFormatException e) {
			System.err.println("アプリケーションが文字列を数値型に変換しようとしたとき、文字列の形式が正しくない場合にスローされます。");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("その他。brの読み込み失敗、close失敗");
			e.printStackTrace();
		}

		try {
			ResponseList<Status> mentions = instTwit.getMentions();
			if (lastPostId == 0)
				lastPostId = mentions.get(1).getId();
			for (Status mention : mentions) {
				if (lastPostId >= mention.getId())continue;
				String reply = decideWhatToSay(mention.getText(),mention.getUser().getScreenName());
				if (reply==null) {
					continue;
				}
				System.out.println(reply);
				if(!DEBUG){
					if (reply.charAt(0) == '@') {	// "@hogehoge"でmentionを返す時は、In Reply Toのパラメータを付加する。
						instTwit.updateStatus(reply, mention.getId());
					} else {
						instTwit.updateStatus(reply);
					}
					System.out.println("呟きました!");
				}
			}
			if(!DEBUG){
				FileWriter fr = new FileWriter(lastPostIdLogFile);
				fr.write(Long.toString(mentions.get(0).getId()));
				fr.close();
			}
		} catch (TwitterException e) {
			System.err.println("Twitter関係のエラー");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("LastLogの書き込みに失敗");
			e.printStackTrace();
		}
	}

	/**
	 * [function]decideWhatToSay
	 * つぶやく内容を選択、文字列を整形して返す。
	 * @param text mentionの文字列
	 * @param name mentionを発したユーザのスクリーンネーム
	 * @return replyすべき文字列
	 */
	private String decideWhatToSay(String text,String name) {
		int replyTipe = decideReplyTipe(text);
		GregorianCalendar menuDate;
		int menuTime;

		switch (replyTipe) {
		case REPLY_TYPE_MENU:
			menuDate = decideMenuDate(text);
			menuTime = decideMenuTime(text);
			return "@" + name + " " + menu.getMenu(menuDate, menuTime);
		case REPLY_TYPE_RT:
			//menuDate = decideMenuDate(text);
			//menuTime = decideMenuTime(text);
			//return menu.getMenu(menuDate, menuTime) + "のRT";
			return null;
		case REPLY_TYPE_FEEDBACK:
			return "RT @" + name + ": " +text;
		case REPLY_TYPE_THX:
		//return "Replyありがとうございます。";
			return null;
		default:
			break;
		}
		return null;

	}

	private int decideReplyTipe(String text) {
		if (text.indexOf("RT")>3 && text.matches(".*好.*|.*味.*|.*良.*|.*美.*|.*微妙.*|.*当たり.*|.*あたり.*|.*アタリ.*|.*ハズレ.*|.*はずれ.*"))
			return REPLY_TYPE_FEEDBACK;

		if (text.matches(".*RT.*"))
			return REPLY_TYPE_RT;
		else if (text
				.matches(".*朝.*|.*昼.*|.*夕.*|.*夜.*|.*明日.*|.*明後日.*|.*？.*|.*何.*|.*なに.*|.*\\?.*"))
			return REPLY_TYPE_MENU;
		else if (text.matches(".*好.*|.*味.*|.*良.*|.*美.*|.*微妙.*|.*当たり.*|.*あたり.*|.*アタリ.*|.*ハズレ.*|.*はずれ.*"))
			return REPLY_TYPE_FEEDBACK;
		else if (text.matches(".*Hi.*"))
			return REPLY_TYPE_THX;
		else
			// TODO RTでの感想も出せるようにする
			return REPLY_TYPE_THX;

	}

	private GregorianCalendar decideMenuDate(String text) {
		GregorianCalendar menuDate = new GregorianCalendar();
		if (text.matches(".*一昨日.*|.*おととい.*|.*いっさくじつ.*")) {
			menuDate.add(GregorianCalendar.DATE, -2);
		} else if (text.matches(".*昨日.*|.*きのう.*|.*さくじつ.*")) {
			menuDate.add(GregorianCalendar.DATE, -1);
		} else if (text.matches(".*明日.*|.*あす.*|.*あした.*")) {
			menuDate.add(GregorianCalendar.DATE, +1);
		} else if (text.matches(".*明明後日.*|.*しあさって.*")) {
			menuDate.add(GregorianCalendar.DATE, +3);
		} else if (text.matches(".*明後日.*|.*あさって.*")) {
			menuDate.add(GregorianCalendar.DATE, +2);
		}
		// TODO 日付での質問にも対応できるようにする
		return menuDate;
	}

	private int decideMenuTime(String text) {
		if (text.matches(".*朝.*"))
			return Menu.MENU_TIME_ASA;
		else if (text.matches(".*昼.*"))
			return Menu.MENU_TIME_HIRU;
		else if (text.matches(".*夜.*|.*夕.*"))
			return Menu.MENU_TIME_YORU;
		else {
			// TODO「質問者が前回尋ねたMenuTime」にする
			return Menu.MENU_TIME_YORU;
		}
	}

}
