package jp.waseda.fuji.ume;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.io.*;

public class Menu {
	public static final int MENU_TIME_ASA = 1;
	public static final int MENU_TIME_HIRU = 2;
	public static final int MENU_TIME_YORU = 3;
	private static String MENU_URL1 = "http://www.wakei.org/jukusei/index.html";
	private static String MENU_URL2 = "http://www.wakei.org/jukusei/index2.html";
	private static String MENU_START_WORD1 = "献立表";
	private static String MENU_START_WORD2 = "tbody";
	private static String MENU_END_WORD = "/tbody";
	
	private GregorianCalendar menuDate;
	private int menuTime;
	
	/**
	 * [function]getMenu
	 * @return (String)和敬飯のメニュー文字列
	 */
	public String getMenu(){
		String[][] menuList = this.getMenuList();
		for(int i=0;i<menuList.length;i++){
			if(menuList[i][0].indexOf(new SimpleDateFormat("M月d日").format(this.menuDate.getTime()))!=-1){
				return menuList[i][0]+this.getMenuType(menuTime)+" "+menuList[i][menuTime];
			}
		}
		return null;
	}
	
	/**
	 * [function]getMenu
	 * @param menuDate 日指定
	 * @param menuTime 朝、昼、晩飯の区別
	 * @return 和敬飯のメニュー文字列
	 */
	public String getMenu(GregorianCalendar menuDate,int menuTime){
		this.menuDate = menuDate;
		this.menuTime = menuTime;
		return getMenu();
	}
	
	public void setMenuTime(int menuTime){
		this.menuTime = menuTime;
	}
	
	public void setMenuDate(GregorianCalendar menuDate){
		this.menuDate = menuDate;
	}
	
	private String getMenuType(int menuTime){
		switch(menuTime){
		case MENU_TIME_ASA:
			return "朝飯";
		case MENU_TIME_HIRU:
			return "昼飯";
		case MENU_TIME_YORU:
			return "夜飯";
		default:
			return "";
		}
	}
	
	private String[][] getMenuList() {
		StringBuffer menuBuffer;
		
		menuBuffer = this.getMenuFromWeb(MENU_URL1);
		menuBuffer = menuBuffer.append(this.getMenuFromWeb(MENU_URL2));
		
		String menuData = menuBuffer.toString();
		menuData = menuData.replaceAll("</td>", "");
		menuData = menuData.replaceAll("</tr>", "");
		menuData = menuData.replaceAll(" ", "");
		menuData = menuData.replaceAll("<br/>", "・");
		menuData = menuData.replaceFirst("<tr>","");
		
		String[] menu = menuData.split("<tr>");
		String[][] menuList = new String[menu.length][];
		
		for(int i=0;i<menu.length;i++){
			menu[i] = menu[i].replaceFirst("<td>","");
			menuList[i] = menu[i].split("<td>");
		}
		
		return menuList;
	}
	
	private StringBuffer getMenuFromWeb(String menuUrl){
		try {
			URL url = new URL(menuUrl);
			InputStreamReader ir1 = new InputStreamReader(url.openStream(),"SJIS");
			BufferedReader br1 = new BufferedReader(ir1);
			String line;
			StringBuffer sb = new StringBuffer();
			boolean flg1 = false;
			boolean flg2 = false;

			while ((line = br1.readLine()) != null) {
				if(!flg1 && line.indexOf(MENU_START_WORD1)!=-1){
					flg1 = true;
				}
				if(!flg2 && line.indexOf(MENU_START_WORD2)!=-1){
					flg2 = true;
					continue;
				}
				if(flg2 && line.indexOf(MENU_END_WORD)!=-1){
					break;
				}
				if(flg1 && flg2) {
					//System.out.println(line);
					sb.append(line + " ");
				}
			}
			
			br1.close();
			return sb;
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}