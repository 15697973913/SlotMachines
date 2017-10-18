package com.zbar.lib.http.model;

/**
 * Created by Administrator on 2017062017/6/26 0026上午 10:23.
 * sub:
 */

public class ResponseVersionModel {
    // 文件存放路径
    public String filepath;
    // 文件名称
    public String version;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
