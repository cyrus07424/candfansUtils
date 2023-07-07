package utils;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import constants.Configurations;

/**
 * ファイルヘルパー.
 *
 * @author cyrus
 */
public class FileHelper {

	/**
	 * URLのデータを保存.
	 *
	 * @param prefix
	 * @param urlString
	 */
	public static void saveContent(String prefix, String urlString) {
		try {
			// 接続
			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();

			// 保存先ファイルを作成
			String fileName;
			String regex = "candfans.jp/user/(\\d+)/post/(\\d+)/secret.jpeg";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(urlString);
			if (matcher.find()) {
				fileName = String.format("%s.%s", matcher.group(2), FilenameUtils.getExtension(urlString));
			} else {
				fileName = FilenameUtils.getName(url.getPath());
			}
			if (Configurations.DEBUG_MODE) {
				System.out.println("fileName: " + fileName);
			}
			File destinationFile = new File(String.format("./downloads/%s/%s", prefix, fileName));
			if (!destinationFile.exists()) {
				Files.createDirectories(destinationFile.getParentFile().toPath());

				// データを保存
				FileUtils.copyToFile(urlConnection.getInputStream(), destinationFile);

				// 更新日時を設定
				if (urlConnection.getLastModified() != 0) {
					destinationFile.setLastModified(urlConnection.getLastModified());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}