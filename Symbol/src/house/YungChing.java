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
		while (nameInd > 0) {
//			if (nameInd != boxInd) {
				parseYungChing(doc, w, nameInd, data);
//			}
//			boxInd = doc.indexOf("item_titlebox", nameInd+20);
			nameInd = doc.indexOf("m-list-item", nameInd+20);
		}
		
		return found;
	}

	private void parseYungChing(StringBuffer doc, Writer w, int nameInd, String[][] data) {
		int start = doc.indexOf("title", nameInd) + 7;
		int end = doc.indexOf("\" ", start);		
		
		try {
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
				String year = doc.substring(start + 4, start + 9);
			
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

				result.append(title + '\t');
				result.append(year + '\t');
				result.append(land + '\t');
				result.append(land_main + '\t');
				result.append(land_record + '\t');
				result.append(room + '\t');
				w.append(result);
				w.append("\r\n");
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean checkyear(String floorNum) {
		// TODO Auto-generated method stub
		return false;
	}

}

