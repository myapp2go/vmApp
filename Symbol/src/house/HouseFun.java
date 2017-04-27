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

public class HouseFun extends PCHouse {

	private static int pageCount = 50;
	private static int pageSize = 30;
	
	public static void main(String[] args) {
		HouseFun house = new HouseFun();
	
		String srcFileName = "housefun_";
		
		startHouse(house, srcFileName);
	}
	
	protected int getHouseData(Writer w, StringBuffer doc, String[][] data, String yearArray[][][], int yearIndex) {
		int nameInd = doc.indexOf("m-list-obj");
		int indEnd = doc.indexOf("m-list-obj ");
		while (nameInd > 0 && nameInd != indEnd) {
			parseHouseFun(doc, w, nameInd, data, yearArray, yearIndex);
			yearIndex++;
			nameInd = doc.indexOf("m-list-obj", nameInd+300);
		}
		
		return yearIndex;
	}

	private void parseHouseFun(StringBuffer doc, Writer w, int nameInd, String[][] data, String yearArray[][][], int yearIndex) {
		int start = doc.indexOf("m-list-data", nameInd) + 7;
		int end = doc.indexOf("\" ", start);		
		
			String id = ""; //doc.substring(end+2, end+9);

			String[] info = new String[constInfoSize];
			boolean skip = false; // checkID(id, data, constDataCount, info);
			if (!skip) {
				// title
				StringBuffer result = new StringBuffer();
				
				start = doc.indexOf("title", end);
				start = doc.indexOf(">", start);
				end = doc.indexOf("</", start+5);
				String title = doc.substring(start+1, end);
//				String title = tmp.replace(" ", "");
/*
				start = doc.indexOf("intro__add", end);
				end = doc.indexOf("</", start+5);
				String address = doc.substring(start+18, end);
*/	
				// land_record
				start = doc.indexOf("ping-number", end);
				start = doc.indexOf("number", start + 15);
				end = doc.indexOf("</", start+5);
				String land_record = doc.substring(start + 8, end);
				
				// room
				start = doc.indexOf("pattern", end + 5);
				end = doc.indexOf("</", start+5);
				String room = doc.substring(start + 9, end);
				
				// floor
				start = doc.indexOf("floor", end + 5);
				end = doc.indexOf("<", start+5);
				String floor = doc.substring(start + 7, end);
				
				// year
//				start = doc.indexOf("<li>", start + 5);
//				end = doc.indexOf("</", start+5);
				String year = "1"; // doc.substring(start + 4, end-1);
	
				// skip
//				start = doc.indexOf("<li>", end);
					
				String land_main = "";
				String land = "";
				String priceOld = "";
				
				start = doc.indexOf("price", end);
				start = doc.indexOf("wording", start+10);
				end = doc.indexOf("</", start+5);
				String unitPrice = doc.substring(start + 9, end);
				
				start = doc.indexOf("number", end+10);
				end = doc.indexOf("</", start+5);
				String price = doc.substring(start + 8, end);	
				price = price.replace(",", "");
							
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
				result.append('\t');
				int mark = title.indexOf(" ");
				if (mark == -1) {
					result.append(title + '\t');
				} else {
					result.append(title.substring(0, mark) + '\t');
				}
				result.append(price + '\t');
				result.append(year + '\t');
				if (mark == -1) {
					result.append(title + '\t');
				} else {
					result.append(title.substring(mark+7, title.length()) + '\t');
				}				
				result.append(land_main + '\t');
				result.append('\t');
				result.append(unitPrice + '\t');
				result.append(land_record + '\t');
				result.append(land + '\t');
				result.append(room + '\t');
				result.append('\t');
				result.append('\t');
				result.append('\t');
				result.append(id + '\t');
				result.append(priceOld + '\t');
				
				sortYear(yearArray, year, price, result.toString(), yearIndex);
			}

	}

}

