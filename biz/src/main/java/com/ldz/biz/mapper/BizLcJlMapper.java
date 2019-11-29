package com.ldz.biz.mapper;

import com.ldz.biz.model.BizLcFd;
import com.ldz.biz.model.BizLcJl;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BizLcJlMapper extends Mapper<BizLcJl> {


    @Select(" select * from biz_lc_jl where lc_cl_id = #{id} order by jssj desc limit 1 ")
    BizLcJl findByClid(@Param("id") String id);

    @Select(" select * from biz_lc_jl where id in (SELECT a.id from biz_lc_jl a left join biz_lc_wxjl b on  a.jl_Id = b.id where b.ye > 0 and lc_km= '2' and ((a.jssj = '' or a.jssj is null) or a.zfzt = '00'))  ")
    List<BizLcJl> getAllInfo();
}