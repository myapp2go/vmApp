
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

	protected static int fieldCount = 14;
	protected static int extraCount = 2;
	protected static String existMark = "X";
	
	protected void readHouse(String name, String[][] data) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
			        new FileInputStream(name), "UTF-8"));
			
			String sCurrentLine;
			
			int line = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(sCurrentLine, "\t");
				if (st.hasMoreElements()) {
					String mode = st.nextElement().toString();
					if (!existMark.equals(mode)) {
						data[0][line] = mode;
						int field = 1;
						while (st.hasMoreElements()) {
							String val = st.nextElement().toString();
							data[field][line] = val;
							field++;
						}
					}
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

	protected void procDelete(Writer w, String[][] data, int lineCount) {
		try {
			for (int i = 0; i < lineCount; i++) {
				if (data[1][i] != null && data[1][i].length() > 2) {
					w.append("\nD\t");
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
		info[0] = "\nN\t";
		boolean found = false;
		for (int i = 0; !found && i < lineCount; i++) {
			if (id.equals(data[1][i])) {
				data[1][i] = existMark;

				info[0] = "\nU" + data[0][i].substring(1) + "\t";
				info[1] = data[2][i];
				info[2] = data[5][i];
				found = true;
			}
		}
		
		boolean skip = false;
		
		return skip;
	}
}

