import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Calendar;

import org.jsoup.Jsoup;

public class House591 extends SinYi {

	protected static String houseFile = "C:\\Users\\mspau\\git\\vmApp\\Symbol\\src\\data\\house591House.txt";
	private static int housePageCount = 7;
	private static int housePageSize = 30;
	private static int houseTotalCount = 209;
	private static int houseLineCount = housePageCount*housePageSize*extraCount;
	protected static String[][] houseData = new String[fieldCount][houseLineCount];

	public static void main(String[] args) {	
		System.out.println("House591 MAIN");
		House591 house = new House591();
		
		house.readHouse(houseFile, houseData);
		house.getHouse591(houseFile, houseData);
		
//		house.readHouse(sinyiFile, sinyiData);
//		house.getSinYi(sinyiFile, sinyiData);
	}
	
	void getHouse591(String name, String[][] data) {
		try {
			Writer w = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");

			for (int i = 0; i < housePageCount; i++) {
				procHouse591(w, i);
			}
			
			postProc(w, data, houseLineCount);
			
			w.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}
	
	protected void procHouse591(Writer w, int fileCount) {
		String doc = "";
//		String url = "https://m.591.com.tw/mobile-list.html?version=1&type=sale&regionid=3&sectionidStr=37&kind=9&price=4";
		String url = "https://m.591.com.tw/mobile-list.html?firstRow=" + (fileCount*housePageSize) + "&totalRows=" + houseTotalCount + "&%1=&version=1&type=sale&regionid=3&sectionidStr=37&kind=9&price=4";
			
		try {
			doc = Jsoup.connect(url).get().html();
			
			int nameInd = doc.indexOf(" data-house-id");
			int count = 0;
			while (nameInd > 0 && count < 50) {
				count++;
				parseHouse591(doc, w, nameInd);

				nameInd = doc.indexOf(" data-house-id", nameInd+20);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseHouse591(String doc, Writer w, int ind) {
		try {
			int start = ind+16;
			int end = doc.indexOf("\"", start);
			String id = doc.substring(start, end);	
			String[] info = new String[3];

			boolean skip = checkID(id, houseData, houseLineCount, info);
			if (!skip) {
				if (info[1] == null || info[2] == null) {
					getMoreInfo(id, info);
				}
					
				w.append(info[0]);
				
				w.append(id + '\t');

				// floor
				w.append(info[1] + '\t');

				// price
				start = doc.indexOf("n1\"", end) + 10;
				end = doc.indexOf("<", start);
				w.append(doc.substring(start, end) + '\t');

				w.append(doc.substring(start, end) + '\t');

				// year
				w.append(info[2] + '\t');
				
				// room
				start = doc.indexOf("<span", end) + 6;
				end = doc.indexOf(" ", start);
				w.append(doc.substring(start, end) + '\t');

				// size
				start = end + 1;
				end = doc.indexOf("<", start);
				w.append(doc.substring(start, end) + '\t');

				w.append(doc.substring(start, end) + '\t');

				w.append(doc.substring(start, end) + '\t');

				// address
				start = doc.indexOf("n2\"", end);
				start = doc.indexOf("span>", start) + 5;
				end = doc.indexOf("<", start);
				w.append(doc.substring(start, end) + '\t');

				// title
				start = doc.indexOf("n3\"", end) + 4;
				end = doc.indexOf("<", start);
				w.append(doc.substring(start, end) + '\t');
				
				// date
				w.append(Calendar.getInstance().getTime().toString() + '\t');
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}

	private boolean getMoreInfo(String hid, String[] info) {
		String doc = "";
		String url = "https://m.591.com.tw/mobile-detail.html?type=sale&regionid=3&sectionidStr=37&price=4&room=&area=&kind=9&shape=&firstRow=0&houseId="+hid;
			
		try {
			doc = Jsoup.connect(url).get().html();
			
			int nameInd = doc.indexOf("price_num");
			if (nameInd > 0) {
				int start = nameInd;
				start = doc.indexOf("<b>", start)+3;
				start = doc.indexOf("<b>", start)+3;
				start = doc.indexOf("<b>", start)+3;
				start = doc.indexOf("<b>", start)+10;
				int end = doc.indexOf("<", start);
				info[1] = doc.substring(start, end);
				
				// skip 2 <b> tab
				start = doc.indexOf("b>", end)+3;
				start = doc.indexOf("b>", start)+2;
				end = doc.indexOf("<", start);
				info[2] = doc.substring(start, end);		
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

}
