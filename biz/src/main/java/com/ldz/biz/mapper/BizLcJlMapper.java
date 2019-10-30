package com.ldz.biz.mapper;

import com.ldz.biz.model.BizLcFd;
import com.ldz.biz.model.BizLcJl;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface BizLcJlMapper extends Mapper<BizLcJl> {


    @Select(" select * from biz_lc_jl where lc_cl_id = #{id} order by jssj desc limit 1 ")
    BizLcJl findByClid(@Param("id") String id);
}