package com.ldz.sys.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "SYS_ZDXM")
public class SysZdxm implements Serializable {
    @Id
    @Column(name = "ZD_ID")
    private String zdId;

    @Column(name = "ZDLMDM")
    private String zdlmdm;

    @Column(name = "ZDDM")
    private String zddm;

    @Column(name = "ZDMC")
    private String zdmc;

    @Column(name = "QZ")
    private Double qz;

    @Column(name = "CJSJ")
    private Date cjsj;

    @Column(name = "CJR")
    private String cjr;

    @Column(name = "BY1")
    private String by1;

    @Column(name = "BY2")
    private String by2;

    @Column(name = "BY3")
    private String by3;
    @Column(name = "BY4")
    private String by4;

    /**
     *  培优 费用
     */
    private String by5;
    /**
     * 开放费用
     */
    private String by6;

    private String by7;

    private String by8;

    private String by9;

    private String by10;

    private String jgdm;

    private static final long serialVersionUID = 1L;

    public String getBy10() {
        return by10;
    }

    public void setBy10(String by10) {
        this.by10 = by10;
    }

    public String getBy4() {
        return by4;
    }

    public String getBy5() {
        return by5;
    }

    public void setBy5(String by5) {
        this.by5 = by5;
    }

    public String getBy6() {
        return by6;
    }

    public void setBy6(String by6) {
        this.by6 = by6;
    }

    public String getBy7() {
        return by7;
    }

    public void setBy7(String by7) {
        this.by7 = by7;
    }

    public String getBy8() {
        return by8;
    }

    public void setBy8(String by8) {
        this.by8 = by8;
    }

    public String getBy9() {
        return by9;
    }

    public void setBy9(String by9) {
        this.by9 = by9;
    }

    public void setBy4(String by4) {
        this.by4 = by4;
    }

    /**
     * @return ZD_ID
     */
    public String getZdId() {
        return zdId;
    }

    /**
     * @param zdId
     */
    public void setZdId(String zdId) {
        this.zdId = zdId;
    }

    /**
     * @return ZDLMDM
     */
    public String getZdlmdm() {
        return zdlmdm;
    }

    /**
     * @param zdlmdm
     */
    public void setZdlmdm(String zdlmdm) {
        this.zdlmdm = zdlmdm;
    }

    /**
     * @return ZDDM
     */
    public String getZddm() {
        return zddm;
    }

    /**
     * @param zddm
     */
    public void setZddm(String zddm) {
        this.zddm = zddm;
    }

    /**
     * @return ZDMC
     */
    public String getZdmc() {
        return zdmc;
    }

    /**
     * @param zdmc
     */
    public void setZdmc(String zdmc) {
        this.zdmc = zdmc;
    }

    /**
     * @return QZ
     */
    public Double getQz() {
        return qz;
    }

    /**
     * @param qz
     */
    public void setQz(Double qz) {
        this.qz = qz;
    }

    /**
     * @return CJSJ
     */
    public Date getCjsj() {
        return cjsj;
    }

    /**
     * @param cjsj
     */
    public void setCjsj(Date cjsj) {
        this.cjsj = cjsj;
    }

    /**
     * @return CJR
     */
    public String getCjr() {
        return cjr;
    }

    /**
     * @param cjr
     */
    public void setCjr(String cjr) {
        this.cjr = cjr;
    }

    /**
     * @return BY1
     */
    public String getBy1() {
        return by1;
    }

    /**
     * @param by1
     */
    public void setBy1(String by1) {
        this.by1 = by1;
    }

    /**
     * @return BY2
     */
    public String getBy2() {
        return by2;
    }

    /**
     * @param by2
     */
    public void setBy2(String by2) {
        this.by2 = by2;
    }

    /**
     * @return BY3
     */
    public String getBy3() {
        return by3;
    }

    /**
     * @param by3
     */
    public void setBy3(String by3) {
        this.by3 = by3;
    }

    public String getJgdm() {
        return jgdm;
    }

    public void setJgdm(String jgdm) {
        this.jgdm = jgdm;
    }

    public enum InnerColumn {
        zdId("ZD_ID"),
        zdlmdm("ZDLMDM"),
        zddm("ZDDM"),
        zdmc("ZDMC"),
        qz("QZ"),
        cjsj("CJSJ"),
        cjr("CJR"),
        by1("BY1"),
        by2("BY2"),
        by3("BY3"),
        by4("BY4"),
        by5("BY5"),
        by6("BY6"),
        by7("BY7"),
        by8("BY8"),
        by9("BY9"),
        by10("BY10"),
        jgdm("JGDM");

        private final String column;

        InnerColumn(String column) {
            this.column = column;
        }

        public String desc() {
            return this.column + " DESC";
        }

        public String asc() {
            return this.column + " ASC";
        }
    }
}