package com.ruoyi.web.controller.biz.activeprofile.domain;

import com.ruoyi.common.annotation.Excel;

public class CriminalImportDto {

    @Excel(name = "罪犯编号")
    private String bh;

    @Excel(name = "姓名")
    private String xm;

    @Excel(name = "捕前职业")
    private String bczy;

    @Excel(name = "出生日期")
    private String csny;

    @Excel(name = "籍贯/国籍")
    private String jg;

    @Excel(name = "入监日期")
    private String rjrq;

    @Excel(name = "罪名")
    private String zm;

    @Excel(name = "原判刑期")
    private String ypxq;

    @Excel(name = "止日")
    private String xqzr;

    @Excel(name = "重控类别")
    private String zdzf;

    @Excel(name = "三类罪犯")
    private String slzf;

    @Excel(name = "老病残")
    private String stzk; // 对应身体状况

    @Excel(name = "监管干警")
    private String zgjc; // 对应专管警察

    // ====== 生成 getter 和 setter 方法 ======
    public String getBh() {
        return bh;
    }

    public void setBh(String bh) {
        this.bh = bh;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getBczy() {
        return bczy;
    }

    public void setBczy(String bczy) {
        this.bczy = bczy;
    }

    public String getCsny() {
        return csny;
    }

    public void setCsny(String csny) {
        this.csny = csny;
    }

    public String getJg() {
        return jg;
    }

    public void setJg(String jg) {
        this.jg = jg;
    }

    public String getRjrq() {
        return rjrq;
    }

    public void setRjrq(String rjrq) {
        this.rjrq = rjrq;
    }

    public String getZm() {
        return zm;
    }

    public void setZm(String zm) {
        this.zm = zm;
    }

    public String getYpxq() {
        return ypxq;
    }

    public void setYpxq(String ypxq) {
        this.ypxq = ypxq;
    }

    public String getXqzr() {
        return xqzr;
    }

    public void setXqzr(String xqzr) {
        this.xqzr = xqzr;
    }

    public String getZdzf() {
        return zdzf;
    }

    public void setZdzf(String zdzf) {
        this.zdzf = zdzf;
    }

    public String getSlzf() {
        return slzf;
    }

    public void setSlzf(String slzf) {
        this.slzf = slzf;
    }

    public String getStzk() {
        return stzk;
    }

    public void setStzk(String stzk) {
        this.stzk = stzk;
    }

    public String getZgjc() {
        return zgjc;
    }

    public void setZgjc(String zgjc) {
        this.zgjc = zgjc;
    }
}
