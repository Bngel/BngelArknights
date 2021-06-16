public class RecruitingOperator {
    private String name;
    private Boolean isNew;
    private int rarity;

    RecruitingOperator(String name, Boolean isNew, int rarity) {
        this.name = name;
        this.isNew = isNew;
        this.rarity = rarity;
    }

    public String getName() {
        return name;
    }

    public Boolean getNew() {
        return isNew;
    }

    public int getRarity() {
        return rarity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }

}
