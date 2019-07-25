package test1;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertyUtil {
	private static final String INIT_FILE_PATH = "setting.properties";
	private static Properties properties;

	public PropertyUtil() {
		properties = new Properties();
		try {
			properties.load(Files.newBufferedReader(Paths.get(INIT_FILE_PATH), StandardCharsets.UTF_8));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(String.format("ファイルの読み込みに失敗しました。ファイル名:%s", INIT_FILE_PATH));
		}
	}

	/**
	 * プロパティ値を取得する
	 *
	 * @param key キー
	 * @return 値
	 */
	public String getProperty(final String key) {
		return getProperty(key, "");
	}

	/**
	 * プロパティ値を取得する
	 *
	 * @param key キー
	 * @param defaultValue デフォルト値
	 * @return キーが存在しない場合、デフォルト値
	 *          存在する場合、値
	 */
	public String getProperty(final String key, final String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
}
