package com.ldz.biz.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Table(name = "BIZ_LC_FDS")
@Data
public class BizLcFds {


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

    private String fdlx;

    @Column(name = "jl_id")
    private String jlId;

    @Column(name = "jl_xm")
    private String jlXm;

    private String qrsj;

    private String qrr;

    @Column(name = "lc_id")
    private String lcId;

    @Transient
    private List<BizLcFd> fds;

    @Column(name = "lc_fy")
    private Integer lcFy;

    @Column(name = "sc")
    private Integer sc;

    @Column(name = "lc_km")
    private String lcKm;

    private String bz;

    private String jx;

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
