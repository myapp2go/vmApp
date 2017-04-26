package house;

import java.io.Writer;

public class New591 extends PCHouse {

	private static int sinyiPageCount = 50;
	private static int sinyiPageSize = 30;
	
	public static void main(String[] args) {
		System.out.println("591 Start");
		New591 house = new New591();
		String srcFileName = "new591_";
	
		for (int i = 0; i < houseList.length; i++) {
			setCityZip(houseList[i]);
			System.out.println("591 " + houseList[i]);

			String realtorFile = "C:\\Users\\" + GITLOC + "\\git\\vmApp\\Symbol\\src\\data\\"+srcFileName+getCityZip()+"_House.txt";
			String[][] realtorData = new String[constFieldCount][sinyiPageCount*sinyiPageSize*constExtraCount];

			house.readHouse(realtorFile, realtorData);
			house.getDataFromFile(realtorFile, realtorData, srcFileName);
		}
		System.out.println("591 Done");
	}

	protected int getHouseData(Writer w, StringBuffer doc, String[][] data, String yearArray[][][], int yearIndex) {
		int nameInd = doc.indexOf("shList");
		while (nameInd > 0) {
			parse591(doc, w, nameInd, data, yearArray, yearIndex);
			yearIndex++;
			// skip end tag
			nameInd = doc.indexOf("shList", nameInd+20);
		}
		
		return yearIndex;
	}

	private void parse591(StringBuffer doc, Writer w, int nameInd,
			String[][] data, String yearArray[][][], int yearIndex) {
		int start = doc.indexOf("data-bind", nameInd) + 11;
		int end = doc.indexOf(" ", start);

		String id = doc.substring(start, end - 1);

		String[] info = new String[constInfoSize];
		boolean skip = false; // checkID(id, data, constDataCount, info);
		if (!skip) {
			StringBuffer result = new StringBuffer();

			start = doc.indexOf("house_url", start) + 12;
			start = doc.indexOf("strong", start);
			end = doc.indexOf("</", start + 10);
			// title
			String title = doc.substring(start+7, end);

			String retMark = checkNewID(id, data, constDataCount, info);

			// address
			start = doc.indexOf("class='cm", end);
			String item_community = "";
			if (start > 0) {
				end = doc.indexOf("<", start);
				item_community = doc.substring(start + 11, end);
			}
			
			start = doc.indexOf("nbsp", end);
			end = doc.indexOf("<", start);			
			String address = doc.substring(start + 5, end);

			// unit price
			start = doc.indexOf("<p>", end);
			end = doc.indexOf("<", start+4);			
			String unitPrice = doc.substring(start + 9, end-2);

			// room
			start = doc.indexOf("layout", end);
			end = doc.indexOf("<", start+8);			
			String room = doc.substring(start + 8, end);

			// floor
			start = doc.indexOf("span", end) + 5;
			start = doc.indexOf("span", start);
			end = doc.indexOf("<", start);
			String floorNum = doc.substring(start+8, end);
			
			// size 1
			start = doc.indexOf("area saleByArea", end);
			start = doc.indexOf("\t", start);
			end = doc.indexOf("</", start);
			String land = doc.substring(start+1, end);

			// size 2
			String land_main = "XXX"; // doc.substring(start, end);

			// year old
//			start = doc.indexOf("num", end) + 5;
//			end = doc.indexOf("<", start);
			String year = ""; // doc.substring(start, end);

/*
			// item_community
			int commInd = doc.indexOf("item_community", end) + 5;

			if (commInd > 5 && commInd - end < 300) {
				end = doc.indexOf("<", commInd);
				item_community = doc.substring(commInd + 11, end);
			}

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
*/
			
			start = doc.indexOf("oldprice", end);
			end = doc.indexOf("<", start);
			String priceOld = doc.substring(start+9, end);
			
			// price_new
			start = doc.indexOf("class", end) + 5;
			start = doc.indexOf(">", start);
			end = doc.indexOf("<", start);
			String price = doc.substring(start+1, end-2);
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

			yearArray[1][1][yearIndex] = result.toString();
//			sortYear(yearArray, year, price, result.toString(), yearIndex);
		}
	}

	private boolean checkyear(String floorNum) {
		// TODO Auto-generated method stub
		return false;
	}

}
