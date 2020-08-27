package com.ldz.biz.mapper;

import com.ldz.biz.model.BizLcWxjl;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface BizLcWxjlMapper extends Mapper<BizLcWxjl> {

    @Select(" select ifnull(card_no,'1') c from biz_lc_wxjl where jgdm like '${jgdm}%' order by c limit 1 ")
    String getMaxNo(@Param("jgdm") String jgdm);

    @Update(" update Biz_lc_jl set jl_lx = #{jlLx} where jl_id = #{id}")
    int updateAllJl(@Param("id") String id, @Param("jlLx") String jlLx);
}