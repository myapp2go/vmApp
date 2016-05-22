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
			System.out.println("Yahoo " + houseList[i]);

			String sinyiFile = "C:\\Users\\" + GITLOC + "\\git\\vmApp\\Symbol\\src\\data\\sinyi_"+getCityZip()+"_House.txt";
			String[][] sinyiData = new String[constFieldCount][sinyiPageCount*sinyiPageSize*constExtraCount];

			house.readHouse(sinyiFile, sinyiData);
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
        File f = new File("C:\\logs\\house\\" + getCityZip() + "_" + fileCount + ".html");
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        new FileInputStream("C:\\logs\\house\\" + getCityZip() + "_" + fileCount + ".html"), "UTF-8"));
			
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

		int nameInd = doc.indexOf("item_title");
		int boxInd = doc.indexOf("item_titlebox");
		while (nameInd > 0) {
			if (nameInd != boxInd) {
				parseSinYi(doc, w, nameInd, data);
			}
			boxInd = doc.indexOf("item_titlebox", nameInd+20);
			nameInd = doc.indexOf("item_title", nameInd+20);
		}
		
		return found;
	}

	private void parseSinYi(StringBuffer doc, Writer w, int nameInd, String[][] data) {
		int start = doc.indexOf("html-attribute-value", nameInd) + 22;
		int end = doc.indexOf(" ", start);		
		
		try {
			String id = doc.substring(end+2, end+9);

			String[] info = new String[constInfoSize];
			boolean skip = checkID(id, data, constDataCount, info);
			if (!skip) {
				// title
				String title = doc.substring(start, end);

				// address
				start = doc.indexOf("detail_line1", end);
				start = doc.indexOf("html-tag", start);
				start = doc.indexOf("span>", start);
				end = doc.indexOf("<", start);
				String address = doc.substring(start + 5, end);
				
				// size 1
				start = doc.indexOf("detail_line2", end);
				
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
				
				start = doc.indexOf("num<", start) + 22;
				end = doc.indexOf("<", start);
				String size1 = doc.substring(start, end);

				// size 2
				start = doc.indexOf("num<", end) + 22;
				end = doc.indexOf("<", start);
				String size2 = doc.substring(start, end);

				// year old
				start = doc.indexOf("num<", end) + 22;
				end = doc.indexOf("<", start);
				String year = doc.substring(start, end);

				// floor
				start = doc.indexOf("num<", end) + 22;
				end = doc.indexOf("<", start);
				String floorNum = doc.substring(start, end);

				// room
				start = doc.indexOf("num<", end) + 22;
				end = doc.indexOf("<", start);
				String room = doc.substring(start, end);

				// price_old
				start = doc.indexOf("price_old", end);
				int comp = doc.indexOf("price_new", end);
				String priceOld = "XXX";
				if (start > 0 && start < comp) {
					start += 28;
					end = doc.indexOf("<", start);
					priceOld = doc.substring(start, end);
				}

				// price_new
				start = doc.indexOf("price_new", end) + 28;
				start = doc.indexOf("num", start) + 22;
				end = doc.indexOf("<", start);
				String price = doc.substring(start, end);
				
				String changePrice = priceChange(price, info);
				
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
