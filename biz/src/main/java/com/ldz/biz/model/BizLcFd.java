package com.ldz.biz.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Table(name = "biz_lc_fd")
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
    private List<BizLcJl> jlList;
    @Column(name = "lc_fy")
    private Integer lcFy;
    @Column(name = "sc")
    private Integer sc;
    @Column(name = "lc_km")
    private String lcKm;

    @Transient
    private int xySl;


    private static final long serialVersionUID = 1L;

    public int getXySl() {
        return xySl;
    }

    public void setXySl(int xySl) {
        this.xySl = xySl;
    }

    public Integer getLcFy() {
        return lcFy;
    }

    public void setLcFy(Integer lcFy) {
        this.lcFy = lcFy;
    }

    public Integer getSc() {
        return sc;
    }

    public void setSc(Integer sc) {
        this.sc = sc;
    }

    public String getLcKm() {
        return lcKm;
    }

    public void setLcKm(String lcKm) {
        this.lcKm = lcKm;
    }

    public String getFdlx() {
        return fdlx;
    }

    public void setFdlx(String fdlx) {
        this.fdlx = fdlx;
    }

    public String getJlId() {
        return jlId;
    }

    public void setJlId(String jlId) {
        this.jlId = jlId;
    }

    public String getJlXm() {
        return jlXm;
    }

    public void setJlXm(String jlXm) {
        this.jlXm = jlXm;
    }

    public String getQrsj() {
        return qrsj;
    }

    public void setQrsj(String qrsj) {
        this.qrsj = qrsj;
    }

    public String getQrr() {
        return qrr;
    }

    public void setQrr(String qrr) {
        this.qrr = qrr;
    }

    public String getLcId() {
        return lcId;
    }

    public void setLcId(String lcId) {
        this.lcId = lcId;
    }

    public List<BizLcJl> getJlList() {
        return jlList;
    }

    public void setJlList(List<BizLcJl> jlList) {
        this.jlList = jlList;
    }

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取返点时间
     *
     * @return cjsj - 返点时间
     */
    public String getCjsj() {
        return cjsj;
    }

    /**
     * 设置返点时间
     *
     * @param cjsj 返点时间
     */
    public void setCjsj(String cjsj) {
        this.cjsj = cjsj;
    }

    /**
     * 获取返点人
     *
     * @return cjr - 返点人
     */
    public String getCjr() {
        return cjr;
    }

    /**
     * 设置返点人
     *
     * @param cjr 返点人
     */
    public void setCjr(String cjr) {
        this.cjr = cjr;
    }

    /**
     * 获取返点总金额
     *
     * @return fdje - 返点总金额
     */
    public Integer getFdje() {
        return fdje;
    }

    /**
     * 设置返点总金额
     *
     * @param fdje 返点总金额
     */
    public void setFdje(Integer fdje) {
        this.fdje = fdje;
    }

    /**
     * 获取返点数量
     *
     * @return fdsl - 返点数量
     */
    public Integer getFdsl() {
        return fdsl;
    }

    /**
     * 设置返点数量
     *
     * @param fdsl 返点数量
     */
    public void setFdsl(Integer fdsl) {
        this.fdsl = fdsl;
    }

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