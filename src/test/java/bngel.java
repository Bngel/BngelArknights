public class bngel {

    public static void main(String[] args) {
        BngelArknights test = new BngelArknights(Config.phone, Config.password);
        int level = test.getWaterLevel();
        System.out.println(level);
    }
}
