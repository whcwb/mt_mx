package com.ldz.biz.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Table(name = "biz_lc_fd")
@Data
public class BizLcFd implements Serializable {
    @Id
    private String id;

    /**
     * 返点时间
     */
    private String cjsj;

    /**
     * 返点人
     */
    private String cjr;

    /**
     * 返点总金额
     */
    private Integer fdje;

    /**
     * 返点数量
     */
    private Integer fdsl;

    /**
     * 返点类型 (已废弃)
     */
    private String fdlx;
    /**
     * 教练 id
     */
    @Column(name = "jl_id")
    private String jlId;
    /**
     * 教练姓名
     */
    @Column(name = "jl_xm")
    private String jlXm;
    /**
     * 确认时间
     */
    private String qrsj;
    /**
     * 确认人
     */
    private String qrr;
    /**
     * 练车记录ID  用逗号拼接
     */
    @Column(name = "lc_id")
    private String lcId;

    @Transient
    private List<BizLcJl> jlList;
    /**
     * 练车费用 (可能是多条练车记录的总费用, 记录的支付现金金额)
     */
    @Column(name = "lc_fy")
    private Integer lcFy;
    /**
     * 练车总时长
     */
    @Column(name = "sc")
    private Integer sc;
    /**
     * 练车科目
     */
    @Column(name = "lc_km")
    private String lcKm;

    @Column(name = "bz")
    private String bz;

    @Transient
    private int xySl;

    @Transient
    private String jlJx;

    private static final long serialVersionUID = 1L;

    public enum InnerColumn {
        id("id"),
        cjsj("cjsj"),
        cjr("cjr"),
        fdje("fdje"),
        fdsl("fdsl"),
        fdlx("fdlx"),
        jlId("JL_ID"),
        jlXm("JL_XM"),
        qrsj("qrsj"),
        qrr("qrr"),
        lcId("LC_ID");

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