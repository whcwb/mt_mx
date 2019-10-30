package com.ldz.biz.mapper;

import com.ldz.biz.model.BizLcWxjl;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface BizLcWxjlMapper extends Mapper<BizLcWxjl> {

    @Select(" select CAST(ifnull(card_no,'0') as unsigned ) c from biz_lc_wxjl order by c limit 1 ")
    int getMaxNo();
}