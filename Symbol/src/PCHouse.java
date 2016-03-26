
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

import org.jsoup.Jsoup;

//import android.os.AsyncTask;

public class PCHouse extends AsyncTask {

	protected int sinyiCount = 2;
	
	protected void getReport() {
		String doc = "";
		String url = "http://buy.sinyi.com.tw/list/NewTaipei-city/234-zip/800-1200-price/house-use/index.html";
			
		try {
//			doc = Jsoup.connect("").get().html();

			url = "https://m.591.com.tw/mobile-list.html?version=1&type=sale&regionid=3&sectionidStr=37&kind=9&price=4";
			doc = Jsoup.connect(url).get().html();
			get591(doc, 1);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void get591(String doc, int count) {
		int nameInd = doc.indexOf("data-house-id");
		
		System.out.println(doc.substring(nameInd, nameInd+100));
	}

}

