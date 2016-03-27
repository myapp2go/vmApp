import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.jsoup.Jsoup;

public class HouseYahoo extends House591 {

	protected static String houseYahooFile = "C:\\Users\\mspau\\git\\vmApp\\Symbol\\src\\houseYahooHouse.txt";
	private static int houseYahooCount = 1;
	private static int lineCount = houseYahooCount*20*4;
	protected static String[][] houseYahooData = new String[fieldCount][lineCount];

	public static void main(String[] args) {		
		HouseYahoo house = new HouseYahoo();

//		house.readHouse(houseYahooFile, house591Data);
//		house.getHouseYahoo(houseYahooFile, house591Data);
		
//		house.readHouse(house591File, house591Data);
//		house.getHouse591(house591File, house591Data);
		
		house.readHouse(sinyiFile, sinyiData);
		house.getSinYi(sinyiFile, sinyiData);
	}

	private void getHouseYahoo(String name, String[][] data) {
		try {
			Writer w = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");

			for (int i = 1; i <= houseYahooCount; i++) {
				procHouseYahoo(w, i);
			}
			
			procDelete(w, data, lineCount);
			
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
			String floor = "XXX";
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
				
			boolean skip = false; // checkFloor				
			if (!skip) {
				String strMode = checkID(id, houseYahooData, lineCount);
				w.append(strMode);
				
				w.append(id + '\t');
				
				w.append(floor + '\t');
				
				w.append(price + '\t');
				w.append(price + '\t');
				
				w.append(year + '\t');
				
				w.append(room + '\t');

				w.append(size + '\t');
				w.append(size + '\t');
				w.append(size + '\t');

				w.append(address + '\t');

				w.append(car + '\t');
				
				w.append(href + '\t');
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
