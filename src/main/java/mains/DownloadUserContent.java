package mains;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.NavigateOptions;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;

import constants.Configurations;
import utils.FileHelper;

/**
 * ユーザーのコンテンツをダウンロード.
 *
 * @author cyrus
 */
public class DownloadUserContent {

	/**
	 * ダウンロード対象のユーザー名一覧.
	 */
	private static final String[] TARGET_USERNAME_ARRAY = {};

	/**
	 * メイン.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("■start.");

		// Playwrightを作成
		try (Playwright playwright = Playwright.create()) {
			// ブラウザ起動オプションを設定
			BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
					.setHeadless(Configurations.USE_HEADLESS_MODE);

			// ブラウザを起動
			try (Browser browser = playwright.chromium().launch(launchOptions)) {
				System.out.println("■launched");

				// 全てのダウンロード対象のユーザー名に対して実行
				for (String username : TARGET_USERNAME_ARRAY) {
					// ブラウザコンテキストオプションを設定
					Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions();

					// ブラウザコンテキストを取得
					try (BrowserContext context = browser.newContext(newContextOptions)) {
						// ページを取得
						try (Page page = context.newPage()) {
							// FIXME ページを設定
							page.onDialog(dialog -> {
								dialog.dismiss();
							});

							// ナビゲーションオプションを設定
							NavigateOptions navigateOptions = new NavigateOptions()
									.setWaitUntil(WaitUntilState.NETWORKIDLE);

							// ユーザー画面を表示
							page.navigate(String.format("https://candfans.jp/%s", username), navigateOptions);

							// 全てのプロフィール画像に対して実行
							for (Locator image : page.locator(".user-profile-images .image")
									.all()) {
								// プロフィール画像のURLを取得
								String src = image.getAttribute("src");
								System.out.println(src);

								// 保存
								FileHelper.saveContent(username, src);
							}

							// 投稿タブをクリック
							page.locator(".change-style-tab .tab:nth-child(1)").click();

							// タイムラインを読み込み
							loadTimeline(page);

							// 全てのサムネイル画像に対して実行
							for (Locator image : page.locator(
									"section.creator-contents > [data-testid='content-post'] .content-images .image")
									.all()) {
								// サムネイル画像のURLを取得
								String src = image.getAttribute("data-src");
								System.out.println(src);

								// 保存
								FileHelper.saveContent(username, src);
							}

							// 単品販売タブをクリック
							page.locator(".change-style-tab .tab:nth-child(2)").click();

							// タイムラインを読み込み
							loadTimeline(page);

							// 全てのサムネイル画像に対して実行
							for (Locator image : page
									.locator("section.creator-contents > div.creator-contents .content-images .image")
									.all()) { // サムネイル画像のURLを取得
								String src = image.getAttribute("data-src");
								System.out.println(src);

								// 保存
								FileHelper.saveContent(username, src);
							}

						}
					}
				}
			}
		} finally {
			System.out.println("■done.");
		}
	}

	/**
	 * タイムラインを読み込み.
	 * @param page
	 */
	private static void loadTimeline(Page page) {
		// FIXME
		for (int i = 0; i < 100; i++) {
			// FIXME 画面下までスクロール
			page.mouse().wheel(0, 10000);

			// ウエイト							
			page.waitForTimeout(500);
		}
	}
}