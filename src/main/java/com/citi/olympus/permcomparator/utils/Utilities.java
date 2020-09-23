package com.citi.olympus.permcomparator.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Utilities {
	
	public static Object parseStringToObject(String content) {
		
		JSONParser parser = new JSONParser();
		Object object = null;
		try {
			object = (JSONObject) parser.parse(content);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return object;
	}

}
