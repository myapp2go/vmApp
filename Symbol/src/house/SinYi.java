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
import java.util.StringTokenizer;

public class SinYi extends PCHouse {

	private static int sinyiPageCount = 50;
	private static int sinyiPageSize = 30;
	
	public static void main(String[] args) {
		System.out.println("SinYi Start");
		SinYi house = new SinYi();
	
		for (int i = 0; i < houseList.length; i++) {
			setCityZip(houseList[i]);
			System.out.println("SinYi " + houseList[i]);

			String sinyiFile = "C:\\Users\\" + GITLOC + "\\git\\vmApp\\Symbol\\src\\data\\sinyi_"+getCityZip()+"_House.txt";
			String[][] sinyiData = new String[constFieldCount][sinyiPageCount*sinyiPageSize*constExtraCount];

//			house.readHouse(sinyiFile, sinyiData);
			house.getSinYi(sinyiFile, sinyiData);
		}
		System.out.println("SinYi Done");
	}
	
	void getSinYi(String name, String[][] data) {
		try {
			shareLinkCount = 2;
			
			Writer w = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");

			boolean found = true;
			for (int i = 1; found && i <= sinyiPageCount; i++) {
				found = procSinYi(w, i, data);
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
	
	private boolean procSinYi(Writer w, int fileCount, String[][] data) {
		boolean found = true;
		StringBuffer doc = new StringBuffer();
        File f = new File("C:\\logs\\house\\" + getCityZip() + "_sy_" + fileCount + ".html");
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        new FileInputStream("C:\\logs\\house\\" + getCityZip() + "_sy_" + fileCount + ".html"), "UTF-8"));
			
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

		int nameInd = doc.indexOf("item_titlebox");
//		nameInd = doc.indexOf("item_title");
//		System.out.println(doc.substring(nameInd, nameInd+200));
		int boxInd = doc.indexOf("item_titlebox", nameInd+10);
		String yearArray[][][] = new String[2][2][100];
		yearArray[0][0][0] = "99";
		int yearIndex = 1;
		while (nameInd > 0) {
//			if (nameInd != boxInd) {
				parseSinYi(doc, w, nameInd, data, yearArray, yearIndex);
				yearIndex++;
//			}
			boxInd = doc.indexOf("item_titlebox", nameInd+20);
			// skip end tag
			nameInd = doc.indexOf("item_titlebox", nameInd+20);
			nameInd = doc.indexOf("item_titlebox", nameInd+20);
		}
		
		try {
			for (int i = 0; i < yearIndex-1; i++) {			
				w.append(yearArray[1][1][i]);
				w.append("\r\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return found;
	}

	private void parseSinYi(StringBuffer doc, Writer w, int nameInd, String[][] data, String yearArray[][][], int yearIndex) {
		int start = doc.indexOf("item_title", nameInd) + 22;
		int end = doc.indexOf(" ", start);		
		
//		try {
			String id = doc.substring(end+2, end+9);

			
			String[] info = new String[constInfoSize];
			boolean skip = false; //checkID(id, data, constDataCount, info);
			if (!skip) {
				StringBuffer result = new StringBuffer();

				start = doc.indexOf("title", start) + 12;
				end = doc.indexOf("\"", start+10);
				// title
				String tmp = doc.substring(start, end);
				int idMark = tmp.indexOf("-");
				String title = tmp;
				if (idMark > 0) {
					title = tmp.substring(0, idMark-1);
					id = tmp.substring(idMark+2, tmp.length());
				}
				
				// address
				start = doc.indexOf("detail_line1", end);
				start = doc.indexOf("span>", start);
				end = doc.indexOf("<", start);
				String address = doc.substring(start + 11, end);
				
				// size 1
				start = doc.indexOf("detail_line2", end);
/*				
				// check car
				int carStart = doc.indexOf("line-content", end) + 5;
				carStart = doc.indexOf("line-content", carStart) + 5;
				carStart = doc.indexOf("line-content", carStart) + 5;

				info[6] = "XXX";
				if (carStart < start) {
					int carEnd = doc.indexOf("<", carStart+3);
					String tmp = doc.substring(carStart+69, carStart+73);
					if (tmp.indexOf("htm") < 0) {
						info[6] = tmp;
					}
				}
*/				
				start = doc.indexOf("num", start) + 5;
				end = doc.indexOf("<", start);
				String land = doc.substring(start, end);

				// size 2
				start = doc.indexOf("num", end) + 5;
				end = doc.indexOf("<", start);
				String land_main = doc.substring(start, end);

				// year old
				start = doc.indexOf("num", end) + 5;
				end = doc.indexOf("<", start);
				String year = doc.substring(start, end);

				// floor
				start = doc.indexOf("num", end) + 5;
				end = doc.indexOf("<", start);
				String floorNum = doc.substring(start, end);
				
				// item_community
				int commInd = doc.indexOf("item_community", end) + 5;
				String item_community = "";
				if (commInd > 5 && commInd - end < 300) {
					end = doc.indexOf("<", commInd);
					item_community = doc.substring(commInd+11, end);
				}
				
/*
				// room
				start = doc.indexOf("num", end) + 22;
				end = doc.indexOf("<", start);
				*/
				String room = ""; //doc.substring(start, end);

				start = doc.indexOf("detail_price", end);
				// price_old				
				int newInd = doc.indexOf("price_new", start);
				int oldInd = doc.indexOf("price_old", start);
				String priceOld = "XXX";
				if (oldInd > 0 && oldInd < newInd) {
					oldInd += 11;
					end = doc.indexOf("<", oldInd);
					priceOld = doc.substring(oldInd, end);
				}

				// price_new
//				start = newInd;
				start = doc.indexOf("num", newInd) + 5;
				end = doc.indexOf("<", start);
				String price = doc.substring(start, end);
//				String changePrice = priceChange(price, info);

				String land_record = " ";
				
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
				result.append(address + '\t');
				result.append(land_main + '\t');
				result.append(floorNum + '\t');
				result.append('\t');
				result.append(land_record + '\t');
				result.append(land + '\t');
				result.append(room + '\t');
				result.append(item_community + '\t');
				result.append('\t');
				result.append('\t');
				result.append(id + '\t');
				result.append(priceOld + '\t');
				
				sortYear(yearArray, year, price, result.toString(), yearIndex);
/*				
				// mode
				String tmp = floorNum.substring(0,1);
				if ((floorNum.indexOf("/4") > 0 || floorNum.indexOf("/5") > 0) && (tmp != null) && !tmp.equals("1") && !tmp.equals("B")) {
					w.append("\r\n" + passMark + changePrice + '\t');
				} else {
					w.append(info[0] + changePrice + '\t');
				}
				
				// id
				w.append(id + '\t');

				// floor
				w.append("#" + floorNum + '\t');
				
				// price old
				w.append(priceOld + '\t');
				
				w.append(price + '\t');

				w.append(year + '\t');

				w.append(room + '\t');

				w.append(size1 + '\t');

				w.append(size2 + '\t');

				w.append(info[6] + '\t');

				w.append(address + '\t');

				w.append(title + '\t');

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
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private boolean checkyear(String floorNum) {
		// TODO Auto-generated method stub
		return false;
	}

}
