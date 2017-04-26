package house;

import java.io.Writer;

public class SinYi extends PCHouse {

	private static int sinyiPageCount = 50;
	private static int sinyiPageSize = 30;
	
	public static void main(String[] args) {
		System.out.println("SinYi Start");
		SinYi house = new SinYi();
		String srcFileName = "sinyi_";
	
		for (int i = 0; i < houseList.length; i++) {
			setCityZip(houseList[i]);
			System.out.println("SinYi " + houseList[i]);

			String realtorFile = "C:\\Users\\" + GITLOC + "\\git\\vmApp\\Symbol\\src\\data\\"+srcFileName+getCityZip()+"_House.txt";
			String[][] realtorData = new String[constFieldCount][sinyiPageCount*sinyiPageSize*constExtraCount];

			house.readHouse(realtorFile, realtorData);
			house.getDataFromFile(realtorFile, realtorData, srcFileName);
		}
		System.out.println("SinYi Done");
	}

	protected int getHouseData(Writer w, StringBuffer doc, String[][] data, String yearArray[][][], int yearIndex) {
		int nameInd = doc.indexOf("item_titlebox");
		int boxInd = doc.indexOf("item_titlebox", nameInd+10);
		while (nameInd > 0) {
			parseSinYi(doc, w, nameInd, data, yearArray, yearIndex);
			yearIndex++;
			boxInd = doc.indexOf("item_titlebox", nameInd+20);
			// skip end tag
			nameInd = doc.indexOf("item_titlebox", nameInd+20);
			nameInd = doc.indexOf("item_titlebox", nameInd+20);
		}
		
		return yearIndex;
	}

	private void parseSinYi(StringBuffer doc, Writer w, int nameInd,
			String[][] data, String yearArray[][][], int yearIndex) {
		int start = doc.indexOf("item_title", nameInd) + 22;
		int end = doc.indexOf(" ", start);

		String id = doc.substring(end + 2, end + 9);

		String[] info = new String[constInfoSize];
		boolean skip = false; // checkID(id, data, constDataCount, info);
		if (!skip) {
			StringBuffer result = new StringBuffer();

			start = doc.indexOf("title", start) + 12;
			end = doc.indexOf("\"", start + 10);
			// title
			String tmp = doc.substring(start, end);
			int idMark = tmp.indexOf("-");
			String title = tmp;
			if (idMark > 0) {
				title = tmp.substring(0, idMark - 1);
				id = tmp.substring(idMark + 2, tmp.length());
			}

			String retMark = checkNewID(id, data, constDataCount, info);

			// address
			start = doc.indexOf("detail_line1", end);
			start = doc.indexOf("span>", start);
			end = doc.indexOf("<", start);
			String address = doc.substring(start + 11, end);

			// size 1
			start = doc.indexOf("detail_line2", end);

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
				item_community = doc.substring(commInd + 11, end);
			}

			String room = ""; // doc.substring(start, end);

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
			start = doc.indexOf("num", newInd) + 5;
			end = doc.indexOf("<", start);
			String price = doc.substring(start, end);
			price = price.replace(",", "");
			// String changePrice = priceChange(price, info);

			String land_record = " ";

			result.append(retMark + '\t');
			result.append(id + '\t');
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
			result.append(priceOld + '\t');

			sortYear(yearArray, year, price, result.toString(), yearIndex);
		}
	}

	private boolean checkyear(String floorNum) {
		// TODO Auto-generated method stub
		return false;
	}

}
