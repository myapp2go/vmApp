package house;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Calendar;

import org.jsoup.Jsoup;

public class House591 extends SinYi {

	private static int housePageCount = 23;
	
	private static int housePageSize = 30;
	private static int houseTotalCount = 1;
	private static int houseRegionId = 3;
	
	// change to 0 or 1 for 800-1200 or 1200-2000
	private static int priceInd = 0;
	private static int[] priceAr = {4, 5};
	private static int[] sharpAr = {1, 2, 3};
	private static int[] areaAr = {2, 3, 4};
	private static int[] ageAr = {1, 2, 3, 4};

	protected static String houseFile = "C:\\Users\\mspau\\git\\vmApp\\Symbol\\src\\data\\house591_" + constCityZip + "_" + priceAr[priceInd] + "_House.txt";

	protected static String[][] houseData = new String[constFieldCount][housePageCount*housePageSize*constExtraCount];

	public static void main(String[] args) {	
		System.out.println("House591 MAIN");
		House591 house = new House591();
		
		house.readHouse(houseFile, houseData);
		house.getHouse591(houseFile, houseData);
		System.out.println("House591 Done");
	}
	
	void getHouse591(String name, String[][] data) {
		try {
			shareLinkCount = 2;
			if (constCityZip < 117) {
				houseRegionId = 1;
			}
			
			File f = new File(name);
			Writer w = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");

			String urlBase = "https://m.591.com.tw/mobile-list.html?version=1&type=sale&regionid=" + houseRegionId + "&sectionidStr=" + constZip[constCityZip-100] + "&kind=9&price=" + priceAr[priceInd];
			String doc = procHouse591(w, urlBase);
			houseTotalCount = getHouseTotalCount(doc);
			housePageCount = houseTotalCount / housePageSize + 1;
			
			for (int i = 1; i < housePageCount; i++) {
				String url = "https://m.591.com.tw/mobile-list.html?firstRow=" + (i*housePageSize) + "&totalRows=" + houseTotalCount + "&%1=&version=1&type=sale&regionid=" + houseRegionId + "&sectionidStr=" + constZip[constCityZip-100] + "&kind=9&price=" + priceAr[priceInd];
				procHouse591(w, url);
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


	protected String procHouse591(Writer w, String url) {
		String doc = "";
			
		try {
			doc = Jsoup.connect(url).timeout(TIMEOUT).get().html();
			
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
		
		return doc;
	}

	private void parseHouse591(String doc, Writer w, int ind) {
		try {
			int start = ind+16;
			int end = doc.indexOf("\"", start);
			String id = doc.substring(start, end);	
			String[] info = new String[constInfoSize];

			boolean skip = checkID(id, houseData, constDataCount, info);
			if (!skip) {
				if (info[1] == null || info[2] == null) {
					getMoreInfo(id, info);
				}
					
				// price
				start = doc.indexOf("n1\"", end) + 10;
				end = doc.indexOf("<", start);
				String price = doc.substring(start, end);
				String changePrice = priceChange(price, info);
				
				String tmp = info[1].substring(0,1);
				if ((info[1].indexOf("/4") > 0 || info[1].indexOf("/5") > 0) && (tmp != null) && !tmp.equals("1") && !tmp.equals("B")) {
					w.append("\r\n" + passMark + changePrice + '\t');
				} else {
					w.append(info[0] + changePrice + '\t');
				}
				
				w.append(id + '\t');

				// floor
				w.append(info[1] + '\t');				
				
				if (changePrice.length() > 0) {
					w.append(info[3] + '\t');
				} else {
					w.append(price + '\t');
				}

				w.append(price + '\t');

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

				w.append(info[6] + '\t');

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
				if (info[5] != null) {
					w.append(info[5] + '\t');					
				} else {
					w.append(Calendar.getInstance().getTime().toString() + '\t');
				}
				
				String houseUrl = "https://m.591.com.tw/mobile-detail.html?houseId="+id;
				w.append(houseUrl + '\t');
				
				w.append("=HYPERLINK(N" + (shareLinkCount++) +")" + '\t');

				if (changePrice.length() > 0) {
					w.append(info[4] + "|" + info[3] + "|" + Calendar.getInstance().getTime().toString() + '\t');
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}

	private boolean getMoreInfo(String hid, String[] info) {
		String doc = "";
		String url = "https://m.591.com.tw/mobile-detail.html?type=sale&regionid=3&sectionidStr=37&price=" + priceAr[priceInd] + "&room=&area=&kind=9&shape=&firstRow=0&houseId="+hid;
			
		try {
			doc = Jsoup.connect(url).timeout(TIMEOUT).get().html();
			
			int nameInd = doc.indexOf("price_num");
			if (nameInd > 0) {
				int start = nameInd;
				start = doc.indexOf("<b>", start)+3;
				start = doc.indexOf("<b>", start)+3;
				start = doc.indexOf("<b>", start)+3;
				start = doc.indexOf("<b>", start)+10;
				int end = doc.indexOf("<", start);
				info[1] = doc.substring(start, end);
				
				// year skip 2 <b> tab
				start = doc.indexOf("b>", end)+3;
				start = doc.indexOf("b>", start)+2;
				end = doc.indexOf("<", start);
				info[2] = doc.substring(start, end);	
				
				// car skip 2 <b> tab
				start = doc.indexOf("<b>", end)+3;
				start = doc.indexOf("<b>", start)+3;
				start = doc.indexOf("<b>", start)+3;
				start = doc.indexOf("b>", start)+2;
				end = doc.indexOf("<", start);
				info[6] = doc.substring(start, end);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	private int getHouseTotalCount(String doc) {
		int start = doc.indexOf("total_count") + 10;
		start = doc.indexOf("value", start) + 7;
		int end = doc.indexOf("\"", start);
		String val =  doc.substring(start, end);
		
		return Integer.valueOf(val);
	}
}
