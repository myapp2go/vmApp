package house;
/**
 * File get from GIT need to convert to UTF-8 format
 * File for excel *.csv need to save as unicode
 * 
 * 1. read passMARK -> passIDMark
 * 2. write passIDMark -> passMark
 * 		passMark -> SoldMark
 * 
 * 1. find zip code map(constZip) for 591
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;

public class PCHouse {

	protected static String GITLOC = "mspau";
	
	protected static int TIMEOUT = 15000;
	
	protected static int[] constZip = {
		  1,   2,   2,   2,   3,   4,   5,   1,   6, 101,
		  7,   8,   9, 101,  10,  11,  12, 101, 101, 101,
		120, 101, 101, 101, 101, 101, 101, 101, 101, 101,
		130, 101, 101, 101, 101, 101, 101, 101, 101, 101,
		140, 101, 101, 101, 101, 101, 101, 101, 101, 101,
		150, 101, 101, 101, 101, 101, 101, 101, 101, 101,
		160, 101, 101, 101, 101, 101, 101, 101, 101, 101,
		170, 101, 101, 101, 101, 101, 101, 101, 101, 101,
		180, 101, 101, 101, 101, 101, 101, 101, 101, 101,
		190, 101, 101, 101, 101, 101, 101, 101, 101, 101,
		200, 101, 101, 101, 101, 101, 101, 101, 101, 101,
		210, 101, 101, 101, 101, 101, 101, 101, 101, 101,
		 26,  27,  28, 101, 101, 101, 101, 101, 101, 101,
		230,  34, 101, 101,  37,  38,  39,  40,  41, 101,
		240,  43,  44,  45,  46, 101, 101,  47,  48, 101,
		250,  50, 101, 101, 101, 101, 101, 101, 101, 101
		};

//	protected static int[] houseList = {100, 103, 104, 105, 106, 108, 110, 111, 112, 114, 115, 116, 234, 235, 242, 244};
//	protected static int[] houseList = {234, 235, 242, 244};
	protected static int[] houseList = {235};
//	protected static int[] houseList = {100, 103, 104, 105, 106, 108, 110, 111, 112, 114, 115, 116};
//	protected static int constCityZip = 221;
	protected static int cityZip;
	
	protected static int constFieldCount = 17;
	protected static int constExtraCount = 4;
	protected static int shareLinkCount = 2;
	protected static int constDataCount = 2;
	protected static int maxPageCount = 20;
	public static int constInfoSize = 7;
	
	protected static String watchMark = "W";
	protected static String bankOwnMark = "B";
	
	protected static String existMark = "X";
	protected static String newMark = "N";
	protected static String updateMark = "U";
	protected static String priceMoreMark = "M";
	protected static String priceLessMark = "L";
	
	protected static String deleteMark = "D";	// REMOVE
	
	protected static final String passMark = "P";		// skip
	protected static final String passIDMark = "I";
	protected static final String soldMark = "S";
	protected static final String repostMark = "R";
	
	protected void readHouse(String name, String[][] data) {
		try {
			File f = new File(name);
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        new FileInputStream(name), "UTF-8"));
			
			String sCurrentLine;
			
			int line = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(sCurrentLine, "\t");
				int field = 0;
				while (st.hasMoreElements() && field < constFieldCount) {
					data[field][line] = st.nextElement().toString();
					field++;
				}
				line++;
			}
			constDataCount = line;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}

	protected void postProc(Writer w, String[][] data, int lineCount) {
		try {
			for (int i = 0; i < lineCount; i++) {
				boolean sold = false;
				if (data[0][i] != null && data[1][i] != null 
						&& !existMark.equals(data[1][i]) && !deleteMark.equals(data[0][i])) {
					switch (data[0][i].substring(0, 1)) {
					case passIDMark :
						w.append("\r\n" + passMark + data[0][i].substring(1) + "\t");
						break;
					case passMark :
						sold = true;
						w.append("\r\n" + soldMark + data[0][i].substring(1) + "\t");
						break;
					case soldMark :
						w.append("\r\n" + soldMark + data[0][i].substring(1) + "\t");
						break;	
					default :
						sold = true;
						w.append("\r\n" + soldMark + data[0][i].substring(1) + "\t");
						break;							
					}
					
					for (int j = 1; j < constFieldCount-1; j++) {
						if (j == 14) {
							w.append("=HYPERLINK(N" + (shareLinkCount++) +")" + '\t');
						} else if (j == 15) {
							if (sold) {
								w.append(Calendar.getInstance().getTime().toString() + '\t');
							} else {
								if (data[j][i] != null && data[j][i].length() > 4) {
									w.append(data[j][i] + "\t");
								}
							}
						} else {
							w.append(data[j][i] + "\t");
						}			
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected boolean checkID(String id, String[][] data, int lineCount, String[] info) {
		boolean skip = false;
		info[0] = "\r\n" + newMark;
		boolean found = false;
		for (int i = 0; !found && i < lineCount; i++) {
			if (id.equals(data[1][i])) {
				if (passMark.equals(data[0][i].substring(0, 1))) {
					data[0][i] = passIDMark + data[0][i].substring(1);
					skip = true;
				} else if (deleteMark.equals(data[0][i].substring(0, 1))) {
					skip = true;
				} else {
					data[1][i] = existMark;
					if (soldMark.equals(data[0][i].substring(0, 1)) || repostMark.equals(data[0][i].substring(0, 1))) {
						info[0] = "\r\n" + repostMark + data[0][i].substring(1);
					} else if (bankOwnMark.equals(data[0][i].substring(0, 1))) {
						info[0] = "\r\n" + bankOwnMark + data[0][i].substring(1);					
					} else if (watchMark.equals(data[0][i].substring(0, 1))) {
						info[0] = "\r\n" + watchMark + data[0][i].substring(1);					
					} else {	
						info[0] = "\r\n" + updateMark + data[0][i].substring(1);
					}
					info[1] = data[2][i];	// floor
					info[2] = data[5][i];	// year
					info[3] = data[4][i];	// price
					info[4] = data[15][i];	// old price
					if (data[12][i] != null && data[12][i].indexOf("http") < 0) {
						info[5] = data[12][i];	// old date
					}
					info[6] = data[9][i];	// car
				}	
				found = true;
			}
		}
				
		return skip;
	}
	
	protected String checkNewID(String id, String[][] data, int lineCount, String[] info) {
		boolean skip = false;
		info[0] = newMark;
		boolean found = false;
		String retMark = newMark;
		for (int i = 0; !found && i < lineCount; i++) {
			if (id.equals(data[1][i])) {
				if (updateMark.equals(data[0][i].substring(0, 1))) {
					retMark = data[0][i];
					data[0][i] = deleteMark;
				} else {
					retMark = updateMark +  data[0][i];
				}	
				found = true;
			}
		}
				
		return retMark;
	}	
	
	protected String priceChange(String price, String[] info) {
		String changePrice = "";
		if (info[3] != null && price != null) {
			int priceVal = getInt(price);
			int infoVal = getInt(info[3]);
			if (priceVal < infoVal) {
				changePrice = priceLessMark;
			} else if (priceVal > infoVal) {
				changePrice = priceMoreMark;
			}
		}
		
		return changePrice;
	}

	private int getInt(String val) {
		boolean found = true;
		String tmp = "";
		for (int i = 0; found && i < val.length(); i++) {
			if (Character.isDigit(val.charAt(i))) {
				tmp += val.charAt(i);
			} else {
				found = false;
			}
		}
		
		return Integer.parseInt(tmp);
	}

	public static int getCityZip() {
		return cityZip;
	}

	public static void setCityZip(int cityZip) {
		PCHouse.cityZip = cityZip;
	}
	
	public void sortYear(String[][][] yearArray, String year, String price,
			String result, int yearIndex) {
		float yearVal = Float.valueOf(year);
		int priceVal = Integer.valueOf(price);


		String old = yearArray[0][0][0];
		String oldPrice = yearArray[0][1][0];
		float oldVal = Float.valueOf(old);
		int oldPriceVal = Integer.valueOf(oldPrice);
		int index = 0;
		while (oldVal != 99.0 && ((yearVal > oldVal) || ((yearVal == oldVal) && (priceVal > oldPriceVal)))) {
			index++;
			old = yearArray[0][0][index];
			oldVal = Float.valueOf(old);
			oldPrice = yearArray[0][1][index];
			oldPriceVal = Integer.valueOf(oldPrice);
		}
		
		for (int i = yearIndex; i > index; i--) {
			yearArray[0][0][i] = yearArray[0][0][i-1];
			yearArray[0][1][i] = yearArray[0][1][i-1];
			yearArray[1][1][i] = yearArray[1][1][i-1];
		}
		
		yearArray[0][0][index] = year;
		yearArray[0][1][index] = price;
		yearArray[1][1][index] = result;
	}
	
	void getDataFromFile(String name, String[][] data, String srcFileName) {
		try {
			shareLinkCount = 2;
			
			Writer w = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");

			boolean found = true;
			String yearArray[][][] = new String[2][2][100];
			yearArray[0][0][0] = "99";
			yearArray[0][1][0] = "9999";
			int yearIndex = 1;
			int count = 1;
			for (int i = 1; count > 0 && i <= maxPageCount; i++) {
				count = getSrcData(w, i, data, yearArray, yearIndex, srcFileName);
				yearIndex += count;
				System.out.println("Page " + i);
			}
			
			for (int i = 0; i < yearIndex-1; i++) {	
				if (yearArray[1][1][i] != null) {
					w.append(yearArray[1][1][i]);
					w.append("\r\n");
				}
			}

			for (int i = 0; !found && i < constDataCount; i++) {
				if (updateMark.equals(data[0][i].substring(0, 1))) {
					
				} else {
					w.append("S\t" + data[1][i] + "\r\n");
				}			
			}
			
//			postProc(w, data, constDataCount);
			
			w.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}

	protected int getSrcData(Writer w, int fileCount, String[][] data, String yearArray[][][], int yearIndex, String srcFileName) {
		boolean found = true;
		StringBuffer doc = new StringBuffer();
//        File f = new File("C:\\logs\\house\\" + getCityZip() + srcFileName + fileCount + ".html");
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        new FileInputStream("C:\\logs\\house\\" + getCityZip() + "_" + srcFileName + fileCount + ".html"), "UTF-8"));
			
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				doc.append(sCurrentLine);
			}
		} catch (FileNotFoundException e1) {
			found = false;
			return 0;
		} catch (IOException e1) {
			found = false;
			return 0;
		}
		
		int count = getHouseData(w, doc, data, yearArray, yearIndex);
		return count;		
	}

	protected int getHouseData(Writer w, StringBuffer doc, String[][] data, String yearArray[][][], int yearIndex) {

		return 0;		
	}
}

