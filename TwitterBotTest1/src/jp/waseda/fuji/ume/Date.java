package jp.waseda.fuji.ume;

import java.util.Calendar;

public class Date {
	
	public boolean isHoliday(Calendar calendar){
		return false;
	}
	
	public String getHolidayName(Calendar calendar){
		return null;
	}
	
	public boolean isSunday(Calendar calendar){
		return false;
	}
	
	
//	function check_holiday($time){
	//
//					if(date("w",$time)==0)
//						return "日曜日";
//					
//					//日曜日以外の場合は休日かどうかチェック	
//					$feed = "http://www.google.com/calendar/feeds/japanese__ja@holiday.calendar.google.com/public/full";
//					$xml = simplexml_load_file($feed);
//					$query = date("Y-m-d",$time);
//					
//					foreach( $xml->entry as $entry ){
//						$gd         = $entry->children( "http://schemas.google.com/g/2005" );
//						$attributes = $gd->when->attributes();
//						foreach( $attributes as $name => $value ){
//							if( $name == "startTime" && $value==$query){
//								return $entry->title;
//								break;
//							}
//						}
//					}
//					return null;
//				}
}
