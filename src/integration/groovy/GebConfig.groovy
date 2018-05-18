import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

waiting {
    timeout = 10
}

driver = {
    ChromeOptions o = new ChromeOptions()

    if (System.getenv('bamboo_working_directory') != null) {
        o.addArguments(
                'headless',
                'no-sandbox',
                'disable-gpu'
        )
    }

    return new ChromeDriver(o)
}