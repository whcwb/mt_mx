package com.ldz.biz.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "BIZ_JL_CZ")
@Data
public class BizJlCz {

    @Id
    private String id;

    @Column(name = "jl_id")
    private String jlId;

    private int je;

    private String cjsj;

    private String type;

    private int czqje;

    private int czhje;

    public enum InnerColumn{
        id("ID"),
        jlId("JL_ID"),
        je("je"),
        cjsj("CJSJ"),
        type("type"),
        czqje("czqje"),
        czhje("czhje");
        private final String column;

        InnerColumn(String column) {
            this.column = column;
        }
        public String value(){
            return this.column;
        }
        public String getValue(){
            return  this.column;
        }
        public String asc(){
            return this.column + " asc";
        }
        public String desc(){
            return this.column + " desc";
        }


    }


}
