import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.Desktop;
import java.net.HttpURLConnection;

public class Main {

    private static String getRequest() {
        Scanner in = new Scanner(System.in);
        System.out.print("Введите запрос для Википедии или 0, чтобы выйти: ");
        String question = in.nextLine();
        if (question.equals("0")) {
            return null;
        }
        question = "\"" + question + "\"";
        question = URLEncoder.encode(question, StandardCharsets.UTF_8);
        return "https://ru.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&srsearch=" + question;
    }

    private static List<PageType> parseFromUrl(String request) {
        try {
            if (request == null) {
                return null;
            }
            URL url = new URL(request);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");
            connect.setRequestProperty("User-Agent", "WikiSearchApp/1.0");
            BufferedReader readJson = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF-8"));
            StringBuilder jsonStr = new StringBuilder();
            String line;
            while ((line = readJson.readLine()) != null) {
                jsonStr.append(line);
            }
            readJson.close();
            connect.disconnect();
            Type listOfPages = new TypeToken<List<PageType>>() {
            }.getType();
            Gson gson = new Gson();
            StringBuilder jsonPages = new StringBuilder();
            for (int i = 0; i < jsonStr.length(); i++) {
                if (jsonStr.charAt(i) == '[') {
                    while (jsonStr.charAt(i) != ']') {
                        jsonPages.append(jsonStr.charAt(i));
                        i++;
                    }
                    jsonPages.append(jsonStr.charAt(i));
                }
            }
            return gson.fromJson(jsonPages.toString(), listOfPages);
        } catch (java.net.MalformedURLException e) {
            System.out.println("Ошибка в ссылке");
            return null;
        } catch (IOException e) {
            System.out.println("Ошибка открытия json по ссылке");
            return null;
        }
    }

    private static int getChoice(List<PageType> Pages) {
        try {
            if (Pages == null) {
                return -1;
            }
            Scanner in = new Scanner(System.in);
            int i = 1;
            for (PageType page : Pages) {
                System.out.println(i + ". " + page.title);
                i++;
            }
            System.out.print("Введите номер страницы для открытия в браузере\n" +
                    "или введите 0, чтобы повторить запрос, и -1, чтобы выйти: ");
            int j = in.nextInt();
            while (j < -1 || j > 10) {
                j = in.nextInt();
            }
            if (j > 0) {
                String UrlToOpen = "https://ru.wikipedia.org/w/index.php?curid=" + Pages.get(j - 1).pageid;
                URI uri = new URI(UrlToOpen);
                Desktop.getDesktop().browse(uri);
            }
            return j;
        } catch (IOException e) {
            System.out.println("Ошибка открытия браузера");
            return -1;
        } catch (URISyntaxException e) {
            System.out.println("Ошибка в ссылке Википедии");
            return -1;
        }
    }

    public static void main(String[] args) {
        int choice = 0;
        while(choice != -1) {
            choice = getChoice(parseFromUrl(getRequest()));
        }
    }
}