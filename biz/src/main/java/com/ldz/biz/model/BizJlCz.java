package com.ldz.biz.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 教练流水记录表
 */
@Table(name = "BIZ_JL_CZ")
@Data
public class BizJlCz {

    @Id
    private String id;
    /**
     * 教练Id
     */
    @Column(name = "jl_id")
    private String jlId;
    /**
     *  金额 (充值 或 消费金额)
     */
    @Column(name = "je")
    private int je;
    /**
     * 创建时间
     */
    private String cjsj;
    /**
     *  类型  10 : 卡片充值 00: 开放日充值 20: 卡片消费  30 : 抵扣消费
     */
    private String type;
    /**
     * 充值前金额
     */
    @Column(name = "czqje")
    private int czqje;
    /**
     * 充值后金额
     */
    @Column(name = "czhje")
    private int czhje;
    /**
     * 摘要
     */
    private String zy;
    /**
     * 备注
     */
    private String bz;
    /**
     * 实付金额
     */
    @Column(name = "sfje")
    private int sfje;
    /**
     * 票据编号
     */
    private String pjbh;
    /**
     * 驾校名称
     */
    @Transient
    private String jx;
    /**
     * 教练姓名
     */
    @Transient
    private String xm;

    public enum InnerColumn {
        // id
        id("ID"),
        // jl_id
        jlId("JL_ID"),
        // je
        je("je"),
        // cjsj
        cjsj("CJSJ"),
        // type
        type("type"),
        // qzqje
        czqje("czqje"),
        // czhje
        czhje("czhje"),
        // zy
        zy("zy"),
        // bz
        bz("bz"),
        // pjbh
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
