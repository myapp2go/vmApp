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

public class YungChing extends PCHouse {

	private static int pageCount = 50;
	private static int pageSize = 30;
	
	public static void main(String[] args) {
		YungChing house = new YungChing();
		String srcFileName = "yungching_";
		
		startHouse(house, srcFileName);
	}
	
	protected int getHouseData(Writer w, StringBuffer doc, String[][] data, String yearArray[][][], int yearIndex) {
		int nameInd = doc.indexOf("m-list-item");
		while (nameInd > 0) {
			parseYungChing(doc, w, nameInd, data, yearArray, yearIndex);
			yearIndex++;
			nameInd = doc.indexOf("m-list-item", nameInd+20);
		}
		
		return yearIndex;
	}

	private void parseYungChing(StringBuffer doc, Writer w, int nameInd, String[][] data, String yearArray[][][], int yearIndex) {
		int start = doc.indexOf("title", nameInd) + 7;
		int end = doc.indexOf("\"", start);		
		
//			String id = doc.substring(end+2, end+9);

			String[] info = new String[constInfoSize];
			boolean skip = false; // checkID(id, data, constDataCount, info);
			if (!skip) {
				// title
				StringBuffer result = new StringBuffer();
				String title = doc.substring(start, end);

				// ID
				start = doc.indexOf("item-description", end);
				start = doc.indexOf(">", start);
				String itemId = doc.substring(start + 1, start + 8);
				
				String retMark = checkNewID(itemId, data, constDataCount, info);
				
				// year
				start = doc.indexOf("item-info-detail", end);
				start = doc.indexOf("<li", start);
				start = doc.indexOf("<li", start+5);
				end = doc.indexOf("</li", start+5);
				String yearStr = doc.substring(start + 4, start + 9);
				String year = yearStr.replaceAll("[^.0-9]", "");
				
				// floor
				start = doc.indexOf("<li", start + 12);
				end = doc.indexOf("</li", start+5);
				String floor = doc.substring(start + 7, end);
				
				start = doc.indexOf("<li", start + 12);
				end = doc.indexOf("</li", start+5);
				String land = doc.substring(start + 7, end);

				start = doc.indexOf("<li", start + 12);
				end = doc.indexOf("</li", start+5);
				String land_main = doc.substring(start + 6, end);	
				if (end - start > 13) {
					land_main = doc.substring(start + 8, end);
				}
				
				start = doc.indexOf("<li", start + 12);
				String land_record = doc.substring(start + 7, start + 13);	

				start = doc.indexOf("<li", start + 12);
				String room = doc.substring(start + 4, start + 18);	

				start = doc.indexOf("price-num", start + 12);
				end = doc.indexOf("</span", start+5);
				String price = doc.substring(start + 11, end);	
				price = price.replace(",", "");
				
				result.append(retMark + '\t');
				result.append(itemId + '\t');
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
				result.append(title.substring(0, mark) + '\t');
				result.append(price + '\t');
				result.append(year + '\t');
				result.append(title.substring(mark+7, title.length()) + '\t');
				result.append(land_main + '\t');
				result.append(floor + '\t');
				result.append('\t');
				result.append(land_record + '\t');
				result.append(land + '\t');
				result.append(room + '\t');
				result.append('\t');
				result.append('\t');
				result.append('\t');
				
				sortYear(yearArray, year, price, result.toString(), yearIndex);
			}
	}

}

