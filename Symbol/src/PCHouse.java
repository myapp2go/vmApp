/**
 * File get from GIT need to convert tp UTF-8 format
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

public class PCHouse extends AsyncTask {

	protected static int[] constZip = {
		  1,   2,   2,   2,   3,   4,   5,   1,   6, 101,
		110, 101, 101, 101, 101, 101, 101, 101, 101, 101,
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
		 26, 101, 101, 101, 101, 101, 101, 101, 101, 101,
		230, 101, 101, 101,  37,  38, 101, 101, 101, 101,
		240,  43, 101, 101, 101, 101, 101, 101, 101, 101,
		250, 101, 101, 101, 101, 101, 101, 101, 101, 101
		};
	
	protected static int constCityZip = 235;
	
	protected static int constFieldCount = 17;
	protected static int constExtraCount = 4;
	protected static int shareLinkCount = 2;
	protected static int constDataCount = 2;
	protected static int constPageCount = 30;
	public static int constInfoSize = 7;
	
	protected static String watchMark = "W";
	
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
}

