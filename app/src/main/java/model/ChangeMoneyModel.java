package model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/6 0006.
 * sub:  接收Wifi通信传输的过来的数据
 */

public class ChangeMoneyModel implements Serializable {
    public String terminalId;
    public String bus_money;
    public String driverName;

    @Override
    public String toString() {
        return "ChangeMoneyModel{" +
                "terminalId='" + terminalId + '\'' +
                ", bus_money='" + bus_money + '\'' +
                ", driverName='" + driverName + '\'' +
                '}';
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getBus_money() {
        return bus_money;
    }

    public void setBus_money(String bus_money) {
        this.bus_money = bus_money;
    }

}
