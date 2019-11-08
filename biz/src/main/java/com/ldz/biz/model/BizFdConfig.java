package com.ldz.biz.model;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 返点配置表
 */
@Table(name = "BIZ_FD_CONFIG")
@Data
public class BizFdConfig {

    @Id
    private String id;
    /**
     * 返点类型
     */
    private String type;
    /**
     * 返点科目
     */
    private String km;
    /**
     * 返点比例
     */
    private String rate;
    /**
     * 创建时间
     */
    private String cjsj;
    /**
     * 创建人
     */
    private String cjr;

    public enum InnerColumn{
        id("ID"),
        type("TYPE"),
        km("KM"),
        rate("RATE"),
        cjsj("CJSJ"),
        cjr("CJR");
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
            return this.column + " asc";
        }
        public String desc(){
            return this.column + " desc";
        }

    }

}
