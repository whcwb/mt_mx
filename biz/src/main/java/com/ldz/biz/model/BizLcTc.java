package com.ldz.biz.model;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 练车套餐表
 */
@Data
@Table(name = "BIZ_LC_TC")
public class BizLcTc {

    @Id
    private String id;

    /**
     * 套餐名称
     */
    private String tcMc;

    /**
     * 套餐代码
     */
    private String tcDm;

    /**
     * 套餐单价
     */
    private Integer tcDj;

    /**
     * 套餐时间
     */
    private Integer tcSj;

    /**
     * 套餐科目
     */
    private String tcKm;

    /**
     * 是否启用刷卡
     */
    private String tcSk;

    /**
     * 是否启用刷卡点火
     */
    private String tcDh;

    /**
     * 返点率
     */
    private Double tcFd;

    /**
     * 套餐类型
     */
    private String tcLx;

    /**
     * 套餐车型
     */
    private String tcCx;

    private String cjsj;

    private String cjr;

    public enum InnerColumn {
        id("ID"),
        tcMc("TC_MC"),
        tcDm("TC_DM"),
        tcDj("TC_DJ"),
        tcSj("TC_SJ"),
        tcKm("TC_KM"),
        tcSk("TC_SK"),
        tcDh("TC_DH"),
        tcFd("TC_FD"),
        tcLx("TC_LX"),
        tcCx("TC_CX"),
        cjsj("CJSJ"),
        cjr("CJR");

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
