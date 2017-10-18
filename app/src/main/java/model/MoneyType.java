package model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017062017/6/27 0027下午 4:16.
 * sub: 上传钱币的参数值
 */

public class MoneyType implements Serializable {
    public String aliplay_money;
    public String paper_money1;
    public String paper_money5;
    public String paper_money10;
    public String paper_money20;
    public String paper_money50;
    public String coin_money1;

    public String getAliplay_money() {
        return aliplay_money;
    }

    public void setAliplay_money(String aliplay_money) {
        this.aliplay_money = aliplay_money;
    }

    public String getPaper_money1() {
        return paper_money1;
    }

    public void setPaper_money1(String paper_money1) {
        this.paper_money1 = paper_money1;
    }

    public String getPaper_money5() {
        return paper_money5;
    }

    public void setPaper_money5(String paper_money5) {
        this.paper_money5 = paper_money5;
    }

    public String getPaper_money10() {
        return paper_money10;
    }

    public void setPaper_money10(String paper_money10) {
        this.paper_money10 = paper_money10;
    }

    public String getPaper_money20() {
        return paper_money20;
    }

    public void setPaper_money20(String paper_money20) {
        this.paper_money20 = paper_money20;
    }

    public String getPaper_money50() {
        return paper_money50;
    }

    public void setPaper_money50(String paper_money50) {
        this.paper_money50 = paper_money50;
    }

    public String getCoin_money1() {
        return coin_money1;
    }

    public void setCoin_money1(String coin_money1) {
        this.coin_money1 = coin_money1;
    }

    public String getWeChat_money() {
        return weChat_money;
    }

    public void setWeChat_money(String weChat_money) {
        this.weChat_money = weChat_money;
    }

    public String weChat_money;


    public MoneyType() {
        super();
    }


    @Override
    public String toString() {
        return "MoneyType{" +
                "aliplay_money=" + aliplay_money +
                ", paper_money1=" + paper_money1 +
                ", paper_money5=" + paper_money5 +
                ", paper_money10=" + paper_money10 +
                ", paper_money20=" + paper_money20 +
                ", paper_money50=" + paper_money50 +
                ", coin_money1=" + coin_money1 +
                ", weChat_money=" + weChat_money +
                '}';
    }
}
