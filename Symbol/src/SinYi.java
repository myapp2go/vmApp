import java.io.BufferedReader;
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

	private static int sinyiPageCount = 7;
	private static int sinyiPageSize = 30;
	protected static String[][] sinyiData = new String[constFieldCount][sinyiPageCount*sinyiPageSize*constExtraCount];
	protected static String sinyiFile = "C:\\Users\\mspau\\git\\vmApp\\Symbol\\src\\data\\sinyi_"+constCityZip+"_House.txt";
	
	public static void main(String[] args) {
		System.out.println("SinYi Main");
		SinYi house = new SinYi();
		
		house.readHouse(sinyiFile, sinyiData);
		house.getSinYi(sinyiFile, sinyiData);
		System.out.println("SinYi Done");
	}
	
	void getSinYi(String name, String[][] data) {
		try {
			shareLinkCount = 2;
			
			Writer w = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");

			boolean found = true;
			for (int i = 1; found && i <= sinyiPageCount; i++) {
				found = procSinYi(w, i);
			}
			postProc(w, data, constDataCount);
			
			w.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}
	
	private boolean procSinYi(Writer w, int fileCount) {
		boolean found = true;
		StringBuffer doc = new StringBuffer();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        new FileInputStream("C:\\logs\\house\\" + constCityZip + "_" + fileCount + ".html"), "UTF-8"));
			
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
				parseSinYi(doc, w, nameInd);
			}
			boxInd = doc.indexOf("item_titlebox", nameInd+20);
			nameInd = doc.indexOf("item_title", nameInd+20);
		}
		
		return found;
	}

	private void parseSinYi(StringBuffer doc, Writer w, int nameInd) {
		int start = doc.indexOf("html-attribute-value", nameInd) + 22;
		int end = doc.indexOf(" ", start);		
		
		try {
			String id = doc.substring(end+2, end+9);

			String[] info = new String[constInfoSize];
			boolean skip = checkID(id, sinyiData, constDataCount, info);
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
					info[6] = doc.substring(carStart+69, carStart+73);
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
				w.append(info[0] + changePrice + '\t');

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
					w.append(info[4] + "|" + info[3] + '\t');
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
