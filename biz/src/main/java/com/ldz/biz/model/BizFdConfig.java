package com.ldz.biz.model;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "BIZ_FD_CONFIG")
@Data
public class BizFdConfig {

    @Id
    private String id;

    private String type;

    private String km;

    private String rate;

    private String cjsj;

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
