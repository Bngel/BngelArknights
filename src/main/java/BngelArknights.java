import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;

public class BngelArknights {

    static final String arknights_token = "https://as.hypergryph.com/user/auth/v1/token_by_phone_password";
    static private String token = "";
    static int totalPage = 1;

    BngelArknights(String phone, String password) {
        initArknightsToken(phone, password);
    }

    /***
     *
     * @param phone 用户的手机号(即账号)
     * @param password 用户的密码
     */
    public static void initArknightsToken(String phone, String password) {
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
            System.out.println(jsonAll);
            if (jsonAll.getString("msg").equals("OK")) {
                token = URLEncoder.encode(jsonAll.getJSONObject("data").getString("token"), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     *
     * @param page 查询寻访的第 page 页
     * @return 返回合成的寻访接口 URL
     */
    public static String getRecruitingURL(int page) {
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
    public static List<RecruitingOperator> getRecruiting(int page) {
        String url = getRecruitingURL(page);
        ArrayList<RecruitingOperator> recruitingOperators = new ArrayList<RecruitingOperator>();
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
                    recruitingOperators.add(new RecruitingOperator(
                            operator.getString("name"),
                            operator.getBoolean("isNew"),
                            operator.getInt("rarity")));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recruitingOperators;
    }

    /***
     * 查询用户当前多少抽未出过六星
     */
    public static int getWaterLevel() {
        int cur = 0;
        List<RecruitingOperator> recruitingList = new ArrayList<RecruitingOperator>();
        for (int i=1; i<= totalPage; i++)
            recruitingList.addAll(getRecruiting(i));
        for (RecruitingOperator recruitingOperator : recruitingList) {
            if (recruitingOperator.rarity == 5 && recruitingOperator.isNew)
                break;
            cur ++;
        }
        return cur;
    }

    public static void main(String[] args){
        initArknightsToken(Config.phone, Config.password);
        for (int i=1; i<= totalPage; i++) {
            List<RecruitingOperator> recruitingList = getRecruiting(i);
            for (RecruitingOperator recruitingOperator : recruitingList) {
                System.out.printf("name:%s isNew:%s rarity:%d%n", recruitingOperator.name, recruitingOperator.isNew, recruitingOperator.rarity);
            }
        }
        System.out.println(getWaterLevel());
    }
}
