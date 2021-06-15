import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BngelArknights {

    static final String arknights_token = "https://as.hypergryph.com/user/auth/v1/token_by_phone_password";
    static String token = "";
    static int totalPage = 1;

    /***
     *
     * @param phone 用户的手机号(即账号)
     * @param password 用户的密码
     * @return 返回当前 token
     */
    public static String getArknightsToken(String phone, String password) {
        String token = "";
        try {
            Document doc = Jsoup.connect(arknights_token)
                    .ignoreContentType(true)
                    .data("phone", phone)
                    .data("password", password)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .post();
            String data = doc.body().text();
            JSONObject jsonAll = new JSONObject(data);
            if (jsonAll.getString("msg").equals("OK")) {
                token = URLEncoder.encode(jsonAll.getJSONObject("data").getString("token"), "UTF-8");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }

    /***
     *
     * @param page 查询寻访的第 page 页
     * @param token 输入之前获取的 token
     * @return 返回合成的寻访接口 URL
     */
    public static String getArknightsRecruitingURL(int page, String token) {
        return String.format("https://ak.hypergryph.com/user/api/inquiry/gacha?page=%d&token=%s", page, token);
    }

    /***
     *
     * @param page 查询寻访的第 page 页
     * @return 返回当前页数的List of Operators(即当前页数的所有干员信息)
     *          干员信息(chars)包括 name: 干员名
     *                    isNew: 是否全新获得
     *                    rarity: 稀有度等级(0-5 为 1-6 星)
     */
    public static List<Operator> getArknightsRecruiting(int page) {
        String url = getArknightsRecruitingURL(page, token);
        ArrayList<Operator> operators = new ArrayList<Operator>();
        try {
            Document doc = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
            String data = doc.body().text();
            JSONObject jsonAll = new JSONObject(data);
            JSONArray jsonList = jsonAll.getJSONObject("data").getJSONArray("list");
            totalPage = (int) Math.ceil(jsonAll.getJSONObject("data").getJSONObject("pagination").getInt("total") / 10.0);
            for (Object region : jsonList) {
                JSONArray item = ((JSONObject) region).getJSONArray("chars");
                for (Object it: item) {
                    JSONObject operator = (JSONObject) it;
                    operators.add(new Operator(
                            operator.getString("name"),
                            operator.getBoolean("isNew"),
                            operator.getInt("rarity")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return operators;
    }

    public static void main(String[] args){
        token = getArknightsToken(Config.phone, Config.password);
        for (int i=1; i<= totalPage; i++) {
            List<Operator> recruitingList = getArknightsRecruiting(i);
            for (Operator operator: recruitingList) {
                System.out.printf("name:%s isNew:%s rarity:%d%n", operator.name, operator.isNew, operator.rarity);
            }
        }
    }
}
