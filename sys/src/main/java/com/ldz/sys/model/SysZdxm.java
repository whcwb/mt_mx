package com.ldz.sys.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "SYS_ZDXM")
@Getter
@Setter
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

    /**
     * 套餐立即结算
     */
    private String ljjs;

    private static final long serialVersionUID = 1L;

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