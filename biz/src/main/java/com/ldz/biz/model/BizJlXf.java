package com.ldz.biz.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name=  "BIZ_JL_XF")
@Data
public class BizJlXf {

    @Id
    private String id;

    @Column(name = "jl_id")
    private String jlId;

    private int je;

    private String cjsj;

    private int xgqje;

    private int xfhje;

public enum  InnerColumn{
    id("ID"),
    jlId("JL_ID"),
    je("je"),
    cjsj("CJSJ"),
    xfqje("xfqje"),
    xfhje("xfhje");

    private final String column;

    InnerColumn(String column) {
        this.column = column;
    }
    public String value(){
        return this.column;
    }
    public String getValue(){
        return this.column;
    }
    public String asc(){
        return  this.column + " asc";
    }
    public String desc(){
        return this.column + " desc";
    }
}

}
