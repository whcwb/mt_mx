package com.ldz.biz.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "biz_lc_wxjl")
@Data
public class BizLcWxjl implements Serializable {
    @Id
    private String id;

    /**
     * 教练姓名
     */
    @Column(name = "jl_xm")
    private String jlXm;

    /**
     * 教练所属驾校
     */
    @Column(name = "jl_jx")
    private String jlJx;

    private String cjr;

    private String cjsj;

    @Column(name = "jl_lxdh")
    private String jlLxdh;

    @Column(name = "ye")
    private int ye;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "pay_type")
    private String payType;

    private String pwd;

    @Column(name = "card_je")
    private int cardJe;

    /**
     * 教练类型  00 本校 10 外校
     */
    @Column(name = "JL_LX")
    private String jlLx;

    private String jgdm;

    private String dh;

    private String dm;

    private static final long serialVersionUID = 1L;

    public enum InnerColumn {
        id("id"),
        jlXm("jl_xm"),
        jlJx("jl_jx"),
        ye("YE"),
        cjr("cjr"),
        cjsj("cjsj"),
        jlLxdh("jl_lxdh"),
        cardNo("card_no"),
        payType("pay_type"),
        pwd("pwd"),
        jlLx("JL_LX"),
        jgdm("JGDM"),
        dh("DH"),
        dm("DM");

        private final String column;

        public String value() {
            return this.column;
        }

        public String getValue() {
            return this.column;
        }

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