package house;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Calendar;

public class ETWarm extends PCHouse {

	private static int pageCount = 50;
	private static int pageSize = 30;
	
	public static void main(String[] args) {
		ETWarm house = new ETWarm();
	
		String srcFileName = "etwarm_";
		
		startHouse(house, srcFileName);
	}
	
	protected int getHouseData(Writer w, StringBuffer doc, String[][] data, String yearArray[][][], int yearIndex) {
		int nameInd = doc.indexOf("obj_item");
		while (nameInd > 0) {
			parseETWarm(doc, w, nameInd, data, yearArray, yearIndex);
			yearIndex++;

			nameInd = doc.indexOf("obj_item", nameInd+300);
		}
		
		return yearIndex;
	}

	private void parseETWarm(StringBuffer doc, Writer w, int nameInd,
			String[][] data, String yearArray[][][], int yearIndex) {
		int start = doc.indexOf("id=", nameInd) + 3;
		int end = doc.indexOf("\"", start);
		String id = doc.substring(start, end);
		
		String[] info = new String[constInfoSize];
		String retMark = checkNewID(id, data, constDataCount, info);
		
		start = doc.indexOf("obj_info", nameInd) + 7;
		end = doc.indexOf("\" ", start);
				
		boolean skip = false; // checkID(id, data, constDataCount, info);
		if (!skip) {
			// title
			StringBuffer result = new StringBuffer();

			start = doc.indexOf("target", end);
			start = doc.indexOf(">", start);
			end = doc.indexOf("</", start + 5);
			String tmp = doc.substring(start + 1, end);
			String title = tmp.replace(" ", "");
			/*
			 * start = doc.indexOf("intro__add", end); end = doc.indexOf("</",
			 * start+5); String address = doc.substring(start+18, end);
			 */
			// skip
			start = doc.indexOf("<li>", end);

			// room
			start = doc.indexOf("<li>", start + 5);
			end = doc.indexOf("</", start + 5);
			String room = doc.substring(start + 4, end);

			// floor
			start = doc.indexOf("<li>", start + 5);
			end = doc.indexOf("</", start + 5);
			String floor = doc.substring(start + 4, end);

			// year
			start = doc.indexOf("<li>", start + 5);
			end = doc.indexOf("</", start + 5);
			String year = doc.substring(start + 4, end - 1);
			char dig = year.charAt(0);
			if (!Character.isDigit(dig)) {
				year = "0";
			}
			
			// skip land
			start = doc.indexOf("<li>", end);
			end = doc.indexOf("</", start + 5);
			String land_record = doc.substring(start + 4, end);
			dig = land_record.charAt(1);
			if (!Character.isDigit(dig)) {
				start = doc.indexOf("<li>", start + 5);
				end = doc.indexOf("</", start + 5);
				land_record = doc.substring(start + 4, end);
			}

			String land_main = "";
			String land = "";
			String priceOld = "";

			start = doc.indexOf("obj_price", end);
			start = doc.indexOf("price", start + 10);
			end = doc.indexOf("</", start + 5);
			String price = doc.substring(start + 7, end);
			price = price.replace(",", "");
			dig = price.charAt(1);
			if (!Character.isDigit(dig)) {
				price = "0";
			}
			
			result.append(retMark + '\t');
			result.append(id + '\t');
			result.append('\t');
			result.append('\t');
			result.append('\t');
			result.append('\t');
			result.append('\t');
			result.append('\t');
			result.append('\t');
			result.append('\t');
			result.append('\t');
			result.append('\t');
			// int mark = title.charAt('\t');
			result.append(title + '\t');
			result.append(price + '\t');
			result.append(year + '\t');
			result.append(title.substring(title.length() - 6, title.length()) + '\t');
			result.append(land_main + '\t');
			result.append(floor + '\t');
			result.append('\t');
			result.append(land_record + '\t');
			result.append(land + '\t');
			result.append(room + '\t');
			result.append('\t');
			result.append('\t');
			result.append('\t');
			result.append(priceOld + '\t');

			sortYear(yearArray, year, price, result.toString(), yearIndex);
		}

	}

}

