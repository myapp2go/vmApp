package house;

import java.io.FileNotFoundException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Calendar;

import org.jsoup.Jsoup;

public class HouseYahoo extends House591 {
	
	private static int yahooPageCount = 30;
	private static int yahooPageSize = 10;

	public static void main(String[] args) {
		System.out.println("START");
		HouseYahoo house = new HouseYahoo();
		
		for (int i = 0; i < houseList.length; i++) {
			setCityZip(houseList[i]);
			System.out.println("Yahoo " + houseList[i]);
			
			yahooPageCount = house.getHousePageCount();
			String yahooFile = "C:\\Users\\mspau\\git\\vmApp\\Symbol\\src\\data\\houseYahoo_" + getCityZip() + "_House.txt";
			String[][] yahooData = new String[constFieldCount][yahooPageCount*yahooPageSize*constExtraCount];
				
			house.readHouse(yahooFile, yahooData);
			house.getHouseYahoo(yahooFile, yahooData);
		}
		System.out.println("Yahoo DONE");
	}

	private void getHouseYahoo(String name, String[][] data) {
		try {
			shareLinkCount = 2;
	        File f = new File(name);
			
			Writer w = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");

			String urlBase = getUrlBase();			
			for (int i = 0; i < yahooPageCount; i++) {
				procHouseYahoo(w, urlBase+(i+1), data);
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

	private String procHouseYahoo(Writer w, String url, String[][] data) {
		String doc = "";

		try {
			doc = Jsoup.connect(url).timeout(TIMEOUT).get().html();
			
			int nameInd = doc.indexOf("info-title");
			int count = 0;
			while (nameInd > 0 && count < 50) {
				count++;
				parseHouseYahoo(doc, w, nameInd, data);

				nameInd = doc.indexOf("info-title", nameInd+20);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return doc;
	}

	private void parseHouseYahoo(String doc, Writer w, int ind, String[][] data) {
		try {				
			int start = doc.indexOf("href", ind) + 6;
			int end = doc.indexOf("\"", start);
			String href = doc.substring(start, end);
			href = href.replaceAll("amp;", "");

			// title
			start = doc.indexOf(">", end) + 1;
			end = doc.indexOf("<", start);
			String title = doc.substring(start, end);
			
			// address
			start = doc.indexOf("<li>", end) + 7;
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

			// type
			start = doc.indexOf("<li>", end) + 7;
			end = doc.indexOf("<", start);
			String floor = doc.substring(start, end);
			
			// floor
			String[] info = new String[constInfoSize];
			start = doc.indexOf("<li>", start) + 7;
			end = doc.indexOf("<", start);
			String floor_car = doc.substring(start, end);
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

			boolean skip = checkID(id, data, constDataCount, info);			
			if (!skip) {
				if (info[1] == null || info[1].length() < 3) {
					getMoreInfo(href, info);
					if (info[1] != null) {
						floor = info[1];
					}
				}
				
				String changePrice = priceChange(price, info);
				
				String fl = info[1];
				if (info[1] == null) {
					fl = floor;
				} 
				String tmp = fl.substring(0,1);
				if ((fl.indexOf("/4") > 0 || fl.indexOf("/5") > 0 || fl.indexOf("/ 4") > 0 || fl.indexOf("/ 5") > 0) && !tmp.equals("1") && !tmp.equals("B")) {
					w.append("\r\n" + passMark + changePrice + '\t');
				} else {
					w.append(info[0] + changePrice + '\t');
				}
				
				w.append(id + '\t');
				
				if (info[1] != null) {
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
				w.append(car + '\t');

				w.append(address + '\t');

				w.append(title + '\t');
				
				if (info[5] != null) {
					w.append(info[5] + '\t');					
				} else {
					w.append(Calendar.getInstance().getTime().toString() + '\t');
				}
				
				w.append(href + '\t');
				
				w.append("=HYPERLINK(N" + (shareLinkCount++) +")" + '\t');

				if (changePrice.length() > 0) {
					w.append(info[4] + "|" + info[3] + "|" + Calendar.getInstance().getTime().toString() + '\t');
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean getMoreInfo(String url, String[] info) {
		try {
			String doc = "";
			
			if (url.indexOf("housefun") > 0) {
				doc = Jsoup.connect(url).get().html();
				getHousefunInfo(doc, info);
			} else if (url.indexOf("etwarm") > 0) {
				doc = Jsoup.connect(url).get().html();
				getEtwarmInfo(doc, info);
			} else if (url.indexOf("twhg") > 0) {
//				getTwhgInfo(doc, info);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR === " + url);
//			e.printStackTrace();
		}
						
		return false;
	}

	private boolean getHousefunInfo(String doc, String[] info) {
		int nameInd = doc.indexOf("title-list both");
		if (nameInd > 0) {
			int start = nameInd;
			start = doc.indexOf("value", start)+5;
			start = doc.indexOf("value", start)+5;
			start = doc.indexOf("value", start)+5;
			start = doc.indexOf("value", start)+5;
			start = doc.indexOf("value", start)+5;
			start = doc.indexOf("value", start)+5;
			start = doc.indexOf(">", start)+1;
			int end = doc.indexOf("<", start);
			info[1] = doc.substring(start, end);
		}
		
		return false;
	}
	
	private boolean getEtwarmInfo(String doc, String[] info) {
		int nameInd = doc.indexOf("obj_data_contain");
		if (nameInd > 0) {
			int start = nameInd;
			start = doc.indexOf("obj_data_contain", start)+16;
			start = doc.indexOf("obj_data_contain", start)+16;
			start = doc.indexOf("obj_data_contain", start)+16;
			start = doc.indexOf("obj_data_contain", start)+16;
			start = doc.indexOf("obj_data_contain", start)+16;
			start = doc.indexOf(">", start)+10;
			String doc1 = doc.substring(start);
			int end = doc.indexOf("<", start);
			if (end - start > 15) {
				info[1] = doc.substring(start, start+10);
			}	
		}
		
		return false;
	}
	
	private boolean getTwhgInfo(String doc, String[] info) {
			int nameInd = doc.indexOf("price_num");
			/*
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
*/
		
		return false;
	}
	
	private int getHousePageCount() {
		int ret = 1;
		String doc = "";
		// first page
		String url = getUrlBase()+1;

		try {
			doc = Jsoup.connect(url).timeout(TIMEOUT).get().html();

			int start = doc.indexOf("yom-button last");
			if (start > 0) {
				start = doc.indexOf("page", start-30) + 5;
				int end = doc.indexOf("\"", start);
				String val =  doc.substring(start, end);
				ret = Integer.valueOf(val);
			}
		} catch (IOException e) {
			ret = 1;
		}
		
		return ret;
	}
	
	private String getUrlBase() {
		String urlBase = "https://tw.v2.house.yahoo.com/object_search_result.html?&homes_type=preowned&zone=3&zip=" + getCityZip() + "&price_min=800&price_max=1500&area_min=30&area_max=60&preowned_main_type=1&preowned_sub_type=0&preowned_keyword=&homes_search=&page=";
		
		return urlBase;
	}
}
