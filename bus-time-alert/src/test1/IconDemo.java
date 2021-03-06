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
				executeGet();
				icon.displayMessage(DetaNode.get("course").get("arrival").get("name").asText() , getTimeTable(), MessageType.INFO);
				System.out.println(getTimeTable()); 
			}
		});
		
		MenuItem item2 = new MenuItem("setting");
		item2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		MenuItem item3 = new MenuItem("exit");
		item3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});

		popup.add(item1);
		popup.add(item2);
		popup.add(item3);

		try {
			SystemTray.getSystemTray().add(icon); // ※2 システムトレイに追加
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private static void executeGet() {
		PropertyUtil propUtil = new PropertyUtil();
		String base_url = "https://bus.t-lab.cs.teu.ac.jp/api/v1/timetables?";
		String from = "from=" + propUtil.getProperty("from");
		LocalDateTime ldt = LocalDateTime.now();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("'datetime='yyyy-MM-dd'%20'HH:mm");
		String date = ldt.format(dtf);
		System.out.println(base_url + from + "&" + date);

		try {
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

			// System.out.println(response);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getTimeTable() {
		DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		LocalDateTime busTime1 = LocalDateTime.parse(DetaNode.get("timetables").get(0).get("departure_time").asText(), dtf);
		LocalDateTime busTime2 = LocalDateTime.parse(DetaNode.get("timetables").get(1).get("departure_time").asText(), dtf);
		LocalDateTime busTime3 = LocalDateTime.parse(DetaNode.get("timetables").get(2).get("departure_time").asText(), dtf);
		dtf = DateTimeFormatter.ofPattern("HH時間mm分");

		return busTime1.format(dtf) + "\n" + busTime2.format(dtf) + "\n" + busTime3.format(dtf) + "に来ます";
	}

}
