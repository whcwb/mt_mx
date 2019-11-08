package com.ldz.biz.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "BIZ_JL_CZ")
@Data
public class BizJlCz {

    @Id
    private String id;

    @Column(name = "jl_id")
    private String jlId;

    @Column(name = "je")
    private int je;

    private String cjsj;

    private String type;

    @Column(name = "czqje")
    private int czqje;

    @Column(name = "czhje")
    private int czhje;

    private String zy;

    private String bz;
    @Column(name = "sfje")
    private int sfje;

    private String pjbh;

    @Transient
    private String jx;

    @Transient
    private String xm;

    public enum InnerColumn {
        id("ID"),
        jlId("JL_ID"),
        je("je"),
        cjsj("CJSJ"),
        type("type"),
        czqje("czqje"),
        czhje("czhje"),
        zy("zy"),
        bz("bz"),
        pjbh("pjbh");
        private final String column;

        InnerColumn(String column) {
            this.column = column;
        }

        public String value() {
            return this.column;
        }

        public String getValue() {
            return this.column;
        }

        public String asc() {
            return this.column + " asc";
        }

        public String desc() {
            return this.column + " desc";
        }


    }


}
