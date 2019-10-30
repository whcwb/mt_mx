package com.ldz.biz.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Table(name = "biz_lc_jl")
public class BizLcJl implements Serializable {
    @Id
    private String id;

    /**
     * 教练姓名
     */
    @Column(name = "jl_xm")
    private String jlXm;

    /**
     * 安全员id
     */
    @Column(name = "zg_id")
    private String zgId;

    /**
     * 安全员姓名
     */
    @Column(name = "zg_xm")
    private String zgXm;

    /**
     * 学员数量
     */
    @Column(name = "xy_sl")
    private Integer xySl;

    /**
     * 教练所属驾校  本校为队号
     */
    @Column(name = "jl_jx")
    private String jlJx;

    /**
     * 教练类型  00 本校 10 外校
     */
    @Column(name = "jl_lx")
    private String jlLx;

    /**
     * 练车开始时间
     */
    private String kssj;

    /**
     * 练车结束时间
     */
    private String jssj;

    /**
     * 预计练车时间
     */
    private Integer sc;

    /**
     * 练车费用
     */
    @Column(name = "lc_fy")
    private Integer lcFy;

    private String cjsj;

    private String cjr;

    /**
     * 练车车辆id
     */
    @Column(name = "lc_cl_id")
    private String lcClId;
    /**
     * 练车车辆编号
     */
    @Column(name = "cl_bh")
    private String clBh;

    /**
     * 练车科目
     */
    @Column(name = "lc_km")
    private String lcKm;

    /**
     * 关联的学员id
     */
    @Transient
    private String xyIds;

    /**
     * 关联的教练id
     */
    @Column(name = "jl_id")
    private String jlId;
    /**
     * 修改人
     */
    private String xgr;
    /**
     * 修改时间
     */
    private String xgsj;
    /**
     * 车型
     */
    @Column(name = "jl_cx")
    private String jlCx;
    /**
     * 押金
     */
    @Column(name = "lc_yj")
    private Integer lcYj;
    /**
     * 备注
     */
    @Column(name ="bz")
    private String bz;
    /**
     * 练车单价
     */
    @Column(name = "lc_dj")
    private float lcDj;
    /**
     * 卡号
     */
    @Column(name = "card_no")
    private String cardNo;
    /**
     * 返点状态  00 未返点   10 已返点  30 预约
     */
    @Column(name = "fd_zt")
    private String fdZt;
    /**
     *  00 计时  10 按把 20 培优 30 开放
     */
    @Column(name = "lc_lx")
    private String lcLx;
    /**
     * 返点时间
     */
    private String fdsj;
    /**
     * 返点人
     */
    private String fdr;
    /**
     * 考场
     */
    private String kc;
    /**
     * 返点记录id
     */
    @Column(name = "fd_id")
    private String fdId;
    /**
     * 实际应付金额
     */
    @Column(name = "yf_je")
    private int yfJe;

    private String xyXm;

    private String xyZjhm;

    private String xyDh;

    @Transient
    private BizLcCl lcCl;
    @Transient
    private String jlDh;

    @Transient
    private List<BizLcJlXy> xyList;

    @Transient
    private String km;

    private static final long serialVersionUID = 1L;

    public enum InnerColumn {
        id("id"),
        jlXm("jl_xm"),
        zgId("zg_id"),
        zgXm("zg_xm"),
        xySl("xy_sl"),
        jlJx("jl_jx"),
        jlLx("jl_lx"),
        kssj("kssj"),
        jssj("jssj"),
        lcJssj("lc_jssj"),
        lcLx("lc_lx"),
        lcFy("lc_fy"),
        cjsj("cjsj"),
        cjr("cjr"),
        lcClId("lc_cl_id"),
        lcKm("lc_km"),
        jlCx("jl_cx"),
        cardNo("card_no"),
        fdZt("fd_zt"),jlId("jl_id"),
        fdsj("fdsj"),
        fdr("fdr"),
        fdId("fd_id"),
        xyXm("xy_xm"),
        xyZjhm("xy_zjhm"),
        xyDh("xy_dh");

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