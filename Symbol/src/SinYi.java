import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.StringTokenizer;


public class SinYi extends PCHouse {

	private static int sinyiCount = 2;
	private static int fieldCount = 10;
	private static int lineCount = sinyiCount*20*2;
	private static String[][] data = new String[fieldCount][lineCount];
	private static String fileName = "C:\\Users\\mspau\\git\\vmApp\\Symbol\\src\\sinyi.txt";
	private static String mode = "";
	
	public static void main(String[] args) {		
		SinYi house = new SinYi();
		house.getReport();
		
		house.readSinYi();

		house.getSinYi();
	}
	
	private void readSinYi() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        new FileInputStream(fileName), "UTF-8"));
			
			String sCurrentLine;
			
			int line = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println("** " + sCurrentLine);
				StringTokenizer st = new StringTokenizer(sCurrentLine, "\t");
				int field = 0;
				while (st.hasMoreElements()) {
//					System.out.println(st.nextElement());
					data[field][line] = st.nextElement().toString();
					System.out.println(data[field][line]);
					field++;
				}
				line++;
//				doc.append(sCurrentLine);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}

	void getSinYi() {
		try {
			Writer w = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");

			for (int i = 1; i <= sinyiCount; i++) {
				procSinYi(w, i);
			}
			procDelete(w);
			
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
			// TODO Auto-generated catch block
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

	private void procDelete(Writer w) {
		try {
			for (int i = 0; i < lineCount; i++) {
				if (data[1][i] != null && data[1][i].length() > 2) {
					System.out.println("DDD " + data[1][i]);			
					w.append("\nD\t");
					w.append(data[1][i] + "\t");
					w.append(data[2][i] + "\t");
					w.append(data[3][i] + "\t");
				}
//			System.out.println("IIII " + data[0][i]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseSinYi(StringBuffer doc, Writer w, int nameInd) {
		int start = doc.indexOf("html-attribute-value", nameInd) + 22;
		int end = doc.indexOf(" ", start);		
		
		try {
			String id = doc.substring(end+2, end+9);
			mode = "";
			boolean isExist = checkID(id);
			if (isExist) {
				w.append("\nU"+mode+"\t");
			} else {
				w.append("\nN\t");
			}
			
			w.append(id + '\t');
			w.append(doc.substring(start, end) + '\t');
			
			start = doc.indexOf("price_new", end) + 28;
			start = doc.indexOf("num", start) + 22;
			end = doc.indexOf("<", start);
			w.append(doc.substring(start, end) + '\t');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean checkID(String id) {
		// TODO Auto-generated method stub
		boolean found = false;
		for (int i = 0; !found && i < lineCount; i++) {
			if (id.equals(data[1][i])) {
				data[1][i] = "";
				mode = data[0][i].substring(1);
				System.out.println("mode " + mode);
				found = true;
			}
//			System.out.println("IIII " + data[0][i]);
		}
		return found;
	}

}
