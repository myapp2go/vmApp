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
		System.out.println("ETWarm Start");
		ETWarm house = new ETWarm();
	
		for (int i = 0; i < houseList.length; i++) {
			setCityZip(houseList[i]);
			System.out.println("Yahoo " + houseList[i]);

			String compFile = "C:\\Users\\" + GITLOC + "\\git\\vmApp\\Symbol\\src\\data\\ETWarm_"+getCityZip()+"_House.txt";
			String[][] compData = new String[constFieldCount][pageCount*pageSize*constExtraCount];

//			house.readHouse(compFile, compData);
			house.getCTWarm(compFile, compData);
		}
		System.out.println("ETWarm Done");
	}
	
	void getCTWarm(String name, String[][] data) {
		try {
			shareLinkCount = 2;
			
			Writer w = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");

			boolean found = true;
			for (int i = 1; found && i <= pageCount; i++) {
				found = procETWarm(w, i, data);
				System.out.println("Page " + i);
			}
			postProc(w, data, constDataCount);
			
			w.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}
	
	private boolean procETWarm(Writer w, int fileCount, String[][] data) {
		boolean found = true;
		StringBuffer doc = new StringBuffer();
        File f = new File("C:\\logs\\house\\" + getCityZip() + "+_yc_" + fileCount + ".html");
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        new FileInputStream("C:\\logs\\house\\" + getCityZip() + "_etwarm_" + fileCount + ".html"), "UTF-8"));
			
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				doc.append(sCurrentLine);
			}
		} catch (FileNotFoundException e1) {
			found = false;
			return found;
		} catch (IOException e1) {
			found = false;
			return found;
		}

		int nameInd = doc.indexOf("obj_item");
//		int boxInd = doc.indexOf("objlist__ItemWrap end");
		String yearArray[][][] = new String[2][2][100];
		yearArray[0][0][0] = "99";
		int yearIndex = 1;
		while (nameInd > 0) {
//			if (boxInd - nameInd > 500) {
				parseETWarm(doc, w, nameInd, data, yearArray, yearIndex);
				yearIndex++;
//			}
				
//			boxInd = doc.indexOf("item_titlebox", nameInd+20);
			nameInd = doc.indexOf("obj_item", nameInd+300);
		}
		
		try {
			for (int i = 0; i < yearIndex; i++) {			
				w.append(yearArray[1][1][i]);
				w.append("\r\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return found;
	}

	private void parseETWarm(StringBuffer doc, Writer w, int nameInd, String[][] data, String yearArray[][][], int yearIndex) {
		int start = doc.indexOf("obj_info", nameInd) + 7;
		int end = doc.indexOf("\" ", start);		
		
			String id = ""; //doc.substring(end+2, end+9);

			String[] info = new String[constInfoSize];
			boolean skip = false; // checkID(id, data, constDataCount, info);
			if (!skip) {
				// title
				StringBuffer result = new StringBuffer();
				
				start = doc.indexOf("target", end);
				start = doc.indexOf(">", start);
				end = doc.indexOf("</", start+5);
				String tmp = doc.substring(start+1, end);
				String title = tmp.replace(" ", "");
/*
				start = doc.indexOf("intro__add", end);
				end = doc.indexOf("</", start+5);
				String address = doc.substring(start+18, end);
*/	
				// skip
				start = doc.indexOf("<li>", end);
				
				// room
				start = doc.indexOf("<li>", start + 5);
				end = doc.indexOf("</", start+5);
				String room = doc.substring(start + 4, end);
				
				// floor
				start = doc.indexOf("<li>", start + 5);
				end = doc.indexOf("</", start+5);
				String floor = doc.substring(start + 4, end);
				
				// year
				start = doc.indexOf("<li>", start + 5);
				end = doc.indexOf("</", start+5);
				String year = doc.substring(start + 4, end-1);
	
				// skip
				start = doc.indexOf("<li>", end);
				
				// land
				start = doc.indexOf("<li>", start + 5);
				end = doc.indexOf("</", start+5);
				String land_record = doc.substring(start + 4, end);
		
				String land_main = "";
				String land = "";
				String priceOld = "";
				
				start = doc.indexOf("obj_price", end);
				start = doc.indexOf("price", start+10);
				end = doc.indexOf("</", start+5);
				String price = doc.substring(start + 7, end);	
				
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
				result.append(title + '\t');
				result.append(price + '\t');
				result.append(year + '\t');
				result.append(title + '\t');
				result.append(land_main + '\t');
				result.append('\t');
				result.append('\t');
				result.append(land_record + '\t');
				result.append(land + '\t');
				result.append(room + '\t');
				result.append('\t');
				result.append('\t');
				result.append('\t');
				result.append(id + '\t');
				result.append(priceOld + '\t');
				
				sortYear(yearArray, year, price, result.toString(), yearIndex);
				
//				sortUear(yearArray);
//				w.append(result);
//				w.append("\r\n");
/*
				// date
				if (info[5] != null) {
					w.append(info[5] + '\t');					
				} else {
					w.append(Calendar.getInstance().getTime().toString() + '\t');
				}
				
				String houseUrl = "http://buy.sinyi.com.tw/house/" + id.substring(1) + ".html";
				w.append(houseUrl + '\t');
				
				w.append("=HYPERLINK(N" + (shareLinkCount++) +")" + '\t');
				
				if (changePrice.length() > 0) {
					w.append(info[4] + "|" + info[3] + "|" + Calendar.getInstance().getTime().toString() + '\t');
				}
				*/
			}

	}
/*
	private void sortYear(String[][][] yearArray, String year, String price,
			String result, int yearIndex) {
		Float yearVal = Float.valueOf(year);
		
		String old = yearArray[0][0][0];
		Float oldVal = Float.valueOf(old);
		int index = 0;
		while (yearVal > oldVal && oldVal != 99.0) {
			index++;
			old = yearArray[0][0][index];
			oldVal = Float.valueOf(old);
		}
		
		for (int i = yearIndex; i > index; i--) {
			yearArray[0][0][i] = yearArray[0][0][i-1];
			yearArray[0][1][i] = yearArray[0][1][i-1];
			yearArray[1][1][i] = yearArray[1][1][i-1];
		}
		
		yearArray[0][0][index] = year;
		yearArray[0][1][index] = price;
		yearArray[1][1][index] = result;
	}
*/
	private float toYearNum(String year) {
		// TODO Auto-generated method stub
		java.lang.Character.isDigit(year.charAt(0));
		return 0;
	}

	private boolean checkyear(String floorNum) {
		// TODO Auto-generated method stub
		return false;
	}

}

