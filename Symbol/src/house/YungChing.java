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
		System.out.println("YungChing Start");
		YungChing house = new YungChing();
	
		for (int i = 0; i < houseList.length; i++) {
			setCityZip(houseList[i]);
			System.out.println("Yahoo " + houseList[i]);

			String compFile = "C:\\Users\\" + GITLOC + "\\git\\vmApp\\Symbol\\src\\data\\yungching_"+getCityZip()+"_House.txt";
			String[][] compData = new String[constFieldCount][pageCount*pageSize*constExtraCount];

//			house.readHouse(compFile, compData);
			house.getSinYi(compFile, compData);
		}
		System.out.println("YungChing Done");
	}
	
	void getSinYi(String name, String[][] data) {
		try {
			shareLinkCount = 2;
			
			Writer w = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");

			boolean found = true;
			for (int i = 1; found && i <= pageCount; i++) {
				found = procYungChing(w, i, data);
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
	
	private boolean procYungChing(Writer w, int fileCount, String[][] data) {
		boolean found = true;
		StringBuffer doc = new StringBuffer();
        File f = new File("C:\\logs\\house\\" + getCityZip() + "+_yc_" + fileCount + ".html");
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        new FileInputStream("C:\\logs\\house\\" + getCityZip() + "_yc_" + fileCount + ".html"), "UTF-8"));
			
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

		int nameInd = doc.indexOf("m-list-item");
		int boxInd = -99; doc.indexOf("item_titlebox");
		String yearArray[][][] = new String[2][2][100];
		yearArray[0][0][0] = "99";
		int yearIndex = 1;
		while (nameInd > 0) {
//			if (nameInd != boxInd) {
				parseYungChing(doc, w, nameInd, data, yearArray, yearIndex);
				yearIndex++;
//			}
				
//			boxInd = doc.indexOf("item_titlebox", nameInd+20);
			nameInd = doc.indexOf("m-list-item", nameInd+20);
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

	private void parseYungChing(StringBuffer doc, Writer w, int nameInd, String[][] data, String yearArray[][][], int yearIndex) {
		int start = doc.indexOf("title", nameInd) + 7;
		int end = doc.indexOf("\" ", start);		
		

			String id = doc.substring(end+2, end+9);

			String[] info = new String[constInfoSize];
			boolean skip = false; checkID(id, data, constDataCount, info);
			if (!skip) {
				// title
				StringBuffer result = new StringBuffer();
				String title = doc.substring(start, end);

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
				result.append(title.substring(0, mark) + '\t');
				result.append(price + '\t');
				result.append(year + '\t');
				result.append(title.substring(mark+1, title.length()) + '\t');
				result.append(land_main + '\t');
				result.append('\t');
				result.append('\t');
				result.append(land_record + '\t');
				result.append(land + '\t');
				result.append(room + '\t');

				
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

