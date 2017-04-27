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

public class CTHouse extends PCHouse {

	private static int pageCount = 50;
	private static int pageSize = 30;
	
	public static void main(String[] args) {
		CTHouse house = new CTHouse();	
		String srcFileName = "cthouse_";
		
		startHouse(house, srcFileName);
	}
	
	protected int getHouseData(Writer w, StringBuffer doc, String[][] data, String yearArray[][][], int yearIndex) {
		int nameInd = doc.indexOf("objlist__item");
		int objEnd = doc.indexOf("objlist__ItemWrap", nameInd+10);
		while (nameInd > 0 && nameInd < objEnd) {
			parseCTHouse(doc, w, nameInd, data, yearArray, yearIndex);
			yearIndex++;

			nameInd = doc.indexOf("objlist__item", nameInd+300);
			nameInd = doc.indexOf("objlist__item", nameInd+10);
		}
		
		return yearIndex;
	}

	private void parseCTHouse(StringBuffer doc, Writer w, int nameInd,
			String[][] data, String yearArray[][][], int yearIndex) {
		int start = nameInd;
		int end = doc.indexOf("\" ", start);

		String[] info = new String[constInfoSize];
		
		start = doc.indexOf("href", start);
		end = doc.indexOf(".", start);
		String id = doc.substring(start + 13, end);
		String retMark = checkNewID(id, data, constDataCount, info);
		
		boolean skip = false; // checkID(id, data, constDataCount, info);
		if (!skip) {
			// title
			StringBuffer result = new StringBuffer();

			start = doc.indexOf("intro__name", end);
			start = doc.indexOf(">", start);
			end = doc.indexOf("</", start + 5);
			String title = doc.substring(start + 1, end);

			start = doc.indexOf("intro__add", end);
			end = doc.indexOf("</", start + 5);
			String address = doc.substring(start + 18, end);

			// year
			start = doc.indexOf("<i>", end);
			end = doc.indexOf("</", start + 5);
			String year = doc.substring(start + 3, end);

			// land
			start = doc.indexOf("<i>", end);
			end = doc.indexOf("</", start + 5);
			String land_record = doc.substring(start + 3, end);

			// land_main
			start = doc.indexOf("<i>", end);
			end = doc.indexOf("</", start + 5);
			String land_main = doc.substring(start + 3, end);

			String land = "";

			start = doc.indexOf("<i>", end);
			end = doc.indexOf("</", start);
			String rm1 = doc.substring(start + 3, end);
			start = doc.indexOf("<i>", end);
			end = doc.indexOf("</", start);
			String rm2 = doc.substring(start + 3, end);
			start = doc.indexOf("<i>", end);
			end = doc.indexOf("</", start);
			String rm3 = doc.substring(start + 3, end);
			String room = rm1 + "/" + rm2 + "/" + rm3;

			// floor
			start = doc.indexOf("<i>", end);
			end = doc.indexOf("</", start);
			String fl1 = doc.substring(start + 3, end);
			start = doc.indexOf("<i>", end);
			end = doc.indexOf("</", start);
			String fl2 = doc.substring(start + 3, end);
			String floor = fl1 + "/" + fl2;

			start = doc.indexOf("price--original", end);
			start = doc.indexOf(">", start);
			end = doc.indexOf("</", start + 5);
			String priceOld = doc.substring(start + 1, end);

			start = doc.indexOf("price--real", end);
			start = doc.indexOf(">", start);
			end = doc.indexOf("</", start + 5);
			String price = doc.substring(start + 4, end);
			price = price.replace(",", "");

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
			result.append(title + '\t');
			result.append(price + '\t');
			result.append(year + '\t');
			result.append(address + '\t');
			result.append(land_main + '\t');
			result.append('\t');
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

