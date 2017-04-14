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
		System.out.println("CTHouse Start");
		CTHouse house = new CTHouse();
	
		for (int i = 0; i < houseList.length; i++) {
			setCityZip(houseList[i]);
			System.out.println("Yahoo " + houseList[i]);

			String compFile = "C:\\Users\\" + GITLOC + "\\git\\vmApp\\Symbol\\src\\data\\CTHouse_"+getCityZip()+"_House.txt";
			String[][] compData = new String[constFieldCount][pageCount*pageSize*constExtraCount];

//			house.readHouse(compFile, compData);
			house.getSinYi(compFile, compData);
		}
		System.out.println("CTHouse Done");
	}
	
	void getSinYi(String name, String[][] data) {
		try {
			shareLinkCount = 2;
			
			Writer w = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");

			boolean found = true;
			for (int i = 1; found && i <= pageCount; i++) {
				found = procCTHouse(w, i, data);
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
	
	private boolean procCTHouse(Writer w, int fileCount, String[][] data) {
		boolean found = true;
		StringBuffer doc = new StringBuffer();
        File f = new File("C:\\logs\\house\\" + getCityZip() + "+_yc_" + fileCount + ".html");
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        new FileInputStream("C:\\logs\\house\\" + getCityZip() + "_cthouse_" + fileCount + ".html"), "UTF-8"));
			
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

		int nameInd = doc.indexOf("objlist__item");
		int boxInd = doc.indexOf("objlist__ItemWrap end");
		String yearArray[][][] = new String[2][2][100];
		yearArray[0][0][0] = "99";
		int yearIndex = 1;
		while (nameInd > 0) {
			if (boxInd - nameInd > 500) {
				parseCTHouse(doc, w, nameInd, data, yearArray, yearIndex);
				yearIndex++;
			}
				
//			boxInd = doc.indexOf("item_titlebox", nameInd+20);
			nameInd = doc.indexOf("objlist__item", nameInd+300);
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

	private void parseCTHouse(StringBuffer doc, Writer w, int nameInd, String[][] data, String yearArray[][][], int yearIndex) {
		int start = doc.indexOf("objlist__item", nameInd) + 7;
		int end = doc.indexOf("\" ", start);		
		
			String id = ""; //doc.substring(end+2, end+9);

			String[] info = new String[constInfoSize];
			boolean skip = false; // checkID(id, data, constDataCount, info);
			if (!skip) {
				// title
				StringBuffer result = new StringBuffer();
				
				start = doc.indexOf("intro__name", end);
				start = doc.indexOf(">", start);
				end = doc.indexOf("</", start+5);
				String title = doc.substring(start+1, end);

				start = doc.indexOf("intro__add", end);
				end = doc.indexOf("</", start+5);
				String address = doc.substring(start+18, end);
				
				// ID
//				start = doc.indexOf("intro__name", end);
//				start = doc.indexOf(">", start);
				String itemId = ""; // doc.substring(start + 1, start + 8);
				
				// year
				start = doc.indexOf("<i>", end);
				end = doc.indexOf("</", start+5);
				String year = doc.substring(start + 3, end);
	
				// land
				start = doc.indexOf("<i>", end);
				end = doc.indexOf("</", start+5);
				String land_record = doc.substring(start + 3, end);

				// land_main
				start = doc.indexOf("<i>", end);
				end = doc.indexOf("</", start+5);
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
				end = doc.indexOf("</", start+5);
				String priceOld = doc.substring(start + 1, end);	

				start = doc.indexOf("price--real", end);
				start = doc.indexOf(">", start);
				end = doc.indexOf("</", start+5);
				String price = doc.substring(start + 4, end);	
				
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
				result.append(itemId + '\t');
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

