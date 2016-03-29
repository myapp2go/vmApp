
import java.io.BufferedReader;
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
import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;

public class PCHouse extends AsyncTask {

	protected static int fieldCount = 16;
	protected static int extraCount = 4;
	protected static int linkCount = 2;
	
	protected static String existMark = "X";
	protected static String newMark = "N";
	protected static String updateMark = "U";
	protected static String priceMark = "C";
	
	protected static String deleteMark = "D";	// REMOVE
	
	protected static String passMark = "P";		// skip
	protected static String soldMark = "S";

	
	protected void readHouse(String name, String[][] data) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        new FileInputStream(name), "UTF-8"));
			
			String sCurrentLine;
			
			int line = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(sCurrentLine, "\t");
				int field = 0;
				while (st.hasMoreElements() && field < fieldCount) {
					data[field][line] = st.nextElement().toString();
					field++;
				}
				line++;
			}
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
				if (data[0][i] != null && data[1][i] != null 
						&& !existMark.equals(data[1][i]) && !deleteMark.equals(data[0][i])) {
					switch (data[0][i]) {
					case "P" :
						w.append("\r\n" + passMark + "\t");
						break;
					case "S" :
						w.append("\r\n" + soldMark + "\t");
						break;	
					default :
						w.append("\r\n" + soldMark + "\t");
						break;							
					}
					
					for (int j = 1; j < fieldCount-1; j++) {
						w.append(data[j][i] + "\t");
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
				if (passMark.equals(data[0][i]) || deleteMark.equals(data[0][i])) {
					skip = true;
				} else {
					data[1][i] = existMark;
					info[0] = "\r\n" + updateMark + data[0][i].substring(1);
					info[1] = data[2][i];
					info[2] = data[5][i];
					info[3] = data[4][i];
				}	
				found = true;
			}
		}
				
		return skip;
	}
}

