package test1;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IconDemo {
	
	static JsonNode DetaNode;

	public static void main(String[] args) {
		IconDemo app = new IconDemo();
		app.run();
	}

	/**
	 * システムトレイにアイコンを出すメソッド
	 */
	private void run() {
		SystemTray tray = SystemTray.getSystemTray();
		PopupMenu popup = new PopupMenu();
		Image image = Toolkit.getDefaultToolkit().createImage("icon.png"); // アイコン画像を準備
		TrayIcon icon = new TrayIcon(image, "Sample Java App", popup); // ※1 トレイアイコンとして生成
		icon.setImageAutoSize(true); // リサイズ

		MenuItem item1 = new MenuItem("Hello");
		item1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				icon.displayMessage("サンプルプログラム", "Hello world !!", MessageType.INFO);
				executeGet();
				icon.displayMessage("Hello world !!", DetaNode.get("timetables").get(0).get("departure_time").asText(), MessageType.INFO);
				DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
				System.out.println(LocalDateTime.parse(DetaNode.get("timetables").get(0).get("departure_time").asText(),dtf));
			}
		});

		MenuItem item2 = new MenuItem("exit");
		item2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});

		popup.add(item1);
		popup.add(item2);

		try {
			SystemTray.getSystemTray().add(icon); // ※2 システムトレイに追加
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private static void executeGet() {

		String base_url = "https://bus.t-lab.cs.teu.ac.jp/api/v1/timetables?";
		String from = "from=2";
		LocalDateTime ldt = LocalDateTime.now();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("'datetime='yyyy-MM-dd'%20'HH:mm");
		String date = ldt.format(dtf);
		System.out.println(base_url + from + "&" + date);

		try {
//			URL url = new URL("https://bus.t-lab.cs.teu.ac.jp/api/v1/timetables?from=2&datetime=2019-07-23%2016:00");
			URL url = new URL(base_url + from + "&" + date);

			HttpURLConnection connection = null;

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			BufferedReader bfr = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer responsBuffer = new StringBuffer();

			ObjectMapper mapper = new ObjectMapper();

			while (true) {
				String line = bfr.readLine();
				if (line == null) {
					break;
				}
//				System.out.println(line);
				responsBuffer.append(line);
			}

			bfr.close();
			connection.disconnect();

			String response = responsBuffer.toString();

			DetaNode = mapper.readTree(response);
			
			String ArrvalName = DetaNode.get("course").get("arrival").get("name").asText();
			
			System.out.println(ArrvalName);

			//			System.out.println(response);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private String getTimeTable() {
//		String 
//		return ;
//	}

}

