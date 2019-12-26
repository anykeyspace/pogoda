import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static final String URL = "http://pogoda.spb.ru/";
    private static final String DELIMITER = "\t\t\t\t";
    private static Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");


    private static Document getPage() throws IOException {
        return Jsoup.parse(new URL(URL), 3000);
    }

    private static String getColumnsTitle() {
        String[] columns = {"Явления", "Температура", "Давление", "Влажность", "Ветер"};
        StringBuilder columsTitleSb = new StringBuilder();
        for (String columnName : columns) {
            columsTitleSb.append(DELIMITER);
            columsTitleSb.append(columnName);
        }
        return columsTitleSb.toString();
    }

    private static String parseDateFromString(String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new RuntimeException("Date parsing error");
    }

    private static int printWeather(Elements values, int index) {
        int iterationCount = 4;
        for (int i = 0; i < iterationCount; ++i) {
            Element value = values.get(i + index);
            for (Element td : value.select("td")) {
                System.out.print(td.text() + DELIMITER);
            }
            System.out.println();
            if (value.selectFirst("td").text().contains("Ночь")) {
                return i + 1;
            }
        }
        return iterationCount;
    }

    public static void main(String[] args) throws IOException {
        Document page = getPage();
        Element weatherTable = page.selectFirst("table[class=wt]");
        Elements names = weatherTable.select("tr[class=wth]");
        Elements values = weatherTable.select("tr[valign=top]");

        String columnsTitle = getColumnsTitle();

        int index = 0;
        for (Element name : names) {
            String date = parseDateFromString(name.select("th[id=dt]").text());
            System.out.println(date + columnsTitle);

            index += printWeather(values, index);
            System.out.println();
        }
    }
}
