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

	private static int sinyiCount = 2;
	private static int lineCount = sinyiCount*20*4;
	protected static String[][] sinyiData = new String[fieldCount][lineCount];
	protected static String sinyiFile = "C:\\Users\\mspau\\git\\vmApp\\Symbol\\src\\sinyiHouse.txt";
	private static String mode = "";
	
	public static void main(String[] args) {		
		SinYi house = new SinYi();
		
		house.readHouse(sinyiFile, sinyiData);

		house.getSinYi(sinyiFile, sinyiData);
	}
	
	void getSinYi(String name, String[][] data) {
		try {
			Writer w = new OutputStreamWriter(new FileOutputStream(name), "UTF-8");

			for (int i = 1; i <= sinyiCount; i++) {
				procSinYi(w, i);
			}
			procDelete(w, data, lineCount);
			
			w.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}
	
	private void procSinYi(Writer w, int fileCount) {
		StringBuffer doc = new StringBuffer();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        new FileInputStream("C:\\logs\\house\\s" + fileCount + ".html"), "UTF-8"));
			
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				doc.append(sCurrentLine);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
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
	}

	private void parseSinYi(StringBuffer doc, Writer w, int nameInd) {
		int start = doc.indexOf("html-attribute-value", nameInd) + 22;
		int end = doc.indexOf(" ", start);		
		
		try {
			String id = doc.substring(end+2, end+9);
			mode = checkID(id, sinyiData, lineCount);
			if (mode != null) {
				w.append("\n"+mode+"\t");
			} else {
				w.append("\nN\t");
			}
			
			w.append(id + '\t');
			w.append(doc.substring(start, end) + '\t');

			start = doc.indexOf("detail_line1", end);
			start = doc.indexOf("html-tag", start);
			start = doc.indexOf("span>", start);
			end = doc.indexOf("<", start);
			w.append(doc.substring(start+5, end) + '\t');

			start = doc.indexOf("detail_line2", end);
			start = doc.indexOf("num<", start)+22;
			end = doc.indexOf("<", start);
			w.append(doc.substring(start, end) + '\t');
			
			start = doc.indexOf("num<", end)+22;
			end = doc.indexOf("<", start);
			w.append(doc.substring(start, end) + '\t');

			// year old
			start = doc.indexOf("num<", end)+22;
			end = doc.indexOf("<", start);
			w.append(doc.substring(start, end) + '\t');

			// floor
			start = doc.indexOf("num<", end)+22;
			end = doc.indexOf("<", start);
			w.append("#" + doc.substring(start, end) + '\t');

			start = doc.indexOf("price_old", end);
			int comp = doc.indexOf("price_new", end);
			if (start > 0 && start < comp) {
				start += 28;
				end = doc.indexOf("<", start);
				w.append(doc.substring(start, end) + '\t');
			} else {
				w.append("XXX" + '\t');
			}
			
			start = doc.indexOf("price_new", end) + 28;
			start = doc.indexOf("num", start) + 22;
			end = doc.indexOf("<", start);
			w.append(doc.substring(start, end) + '\t');
			
			w.append(Calendar.getInstance().getTime().toString() + '\t');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
