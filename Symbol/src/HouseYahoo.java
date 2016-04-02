import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Calendar;

import org.jsoup.Jsoup;

public class HouseYahoo extends House591 {

	protected static String yahooFile = "C:\\Users\\mspau\\git\\vmApp\\Symbol\\src\\data\\houseYahoo_" + city + "_House.txt";
	private static int yahooPageCount = 1;
	private static int yahooPageSize = 30;
	protected static String[][] yahooData = new String[fieldCount][yahooPageCount*yahooPageSize*extraCount];

	public static void main(String[] args) {
		System.out.println("START");
		HouseYahoo house = new HouseYahoo();

//		house.readHouse(sinyiFile, sinyiData);
//		house.getSinYi(sinyiFile, sinyiData);		
		System.out.println("SinYi DONE");
		
		house.readHouse(yahooFile, yahooData);
		house.getHouseYahoo(yahooFile, yahooData);
		System.out.println("Yahoo DONE");
		
//		house.readHouse(houseFile, houseData);
//		house.getHouse591(houseFile, houseData);
		System.out.println("House591 DONE");
	}

	private void getHouseYahoo(String name, String[][] data) {
		try {
			linkCount = 2;
			
			Writer w = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");

			for (int i = 1; i <= yahooPageCount; i++) {
				procHouseYahoo(w, i);
			}
			
			postProc(w, data, constDataCount);
			
			w.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	private void procHouseYahoo(Writer w, int i) {
		String doc = "";
		String url = "https://tw.v2.house.yahoo.com/object_search_result.html?&homes_type=preowned&zone=3&zip=234&price_min=800&price_max=1500&area_min=30&area_max=60&preowned_main_type=1&preowned_sub_type=0&preowned_keyword=&homes_search=";
			
		try {
			doc = Jsoup.connect(url).get().html();
			
			int nameInd = doc.indexOf("info-title");
			int count = 0;
			while (nameInd > 0 && count < 50) {
				count++;
				parseHouseYahoo(doc, w, nameInd);

				nameInd = doc.indexOf("info-title", nameInd+20);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private void parseHouseYahoo(String doc, Writer w, int ind) {
		try {				
			int start = doc.indexOf("href", ind) + 6;
			int end = doc.indexOf("\"", start);
			String href = doc.substring(start, end);

			// address
			start = doc.indexOf("<li>", end) + 8;
			end = doc.indexOf("<", start);
			String address = doc.substring(start, end);

			// price
			start = doc.indexOf("<li>", end) + 11;
			end = doc.indexOf("<", start);
			String price = doc.substring(start, end);

			// size
			start = doc.indexOf("<li>", end) + 7;
			end = doc.indexOf("<", start);
			String size = doc.substring(start, end);

			// room
			start = doc.indexOf("<li>", end) + 7;
			end = doc.indexOf("<", start);
			String room = doc.substring(start, end);

			// floor
			start = doc.indexOf("<li>", end) + 4;
			start = doc.indexOf("<li>", start) + 7;
			end = doc.indexOf("<", start);
			String floor_car = doc.substring(start, end);
			String floor = noDataMark;
			String car = floor_car;
			if (floor_car.length() > 4) {
				floor = floor_car.substring(4);
				
				start = doc.indexOf("<li>", end) + 7;
				end = doc.indexOf("<", start);
				car = doc.substring(start, end);					
			}
				
			start = doc.indexOf("<li>", end) + 7;
			end = doc.indexOf("<", start);
			String year = doc.substring(start, end);
				
			// id
			start = doc.indexOf("<li>", end) + 9;
			end = doc.indexOf("<", start);
			String id = doc.substring(start, end);

			String[] info = new String[infoSize];
			boolean skip = checkID(id, yahooData, constDataCount, info);			
			if (!skip) {
				String changePrice = priceChange(price, info);
				
				w.append(info[0] + changePrice + '\t');
				
				w.append(id + '\t');
				
				if (noDataMark.equals(floor)) {
					w.append(info[1] + '\t');
				} else {
					w.append(floor + '\t');
				}
				
				if (changePrice.length() > 0) {
					w.append(info[3] + '\t');
				} else {
					w.append(price + '\t');
				}
				w.append(price + '\t');
				
				w.append(year + '\t');
				
				w.append(room + '\t');

				w.append(size + '\t');
				w.append(size + '\t');
				w.append(size + '\t');

				w.append(address + '\t');

				w.append(car + '\t');
				
				if (info[5] != null) {
					w.append(info[5] + '\t');					
				} else {
					w.append(Calendar.getInstance().getTime().toString() + '\t');
				}
				
				w.append(href + '\t');
				
				w.append("=HYPERLINK(N" + (linkCount++) +")" + '\t');
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
