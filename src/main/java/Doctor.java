public class Doctor {
    private String uid;
    private String nickName;
    private int guest;

    Doctor(String uid, String nickName, int guest) {
        this.uid = uid;
        this.nickName = nickName;
        this.guest = guest;
    }

    public String getUid() {
        return uid;
    }

    public String getNickName() {
        return nickName;
    }

    public int getGuest() {
        return guest;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setGuest(int guest) {
        this.guest = guest;
    }
}
