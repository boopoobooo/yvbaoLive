package cn.junbao.yubao.live.id.generate.dao.mapper;

import cn.junbao.yubao.live.id.generate.dao.po.IdGeneratePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 19:47 2023/5/25
 * @Description
 */
@Mapper
public interface IdGenerateMapper  {


    int updateNewIdCountAndVersion(@Param("id")int id,@Param("version")int version);

    List<IdGeneratePO> selectAll();

    IdGeneratePO selectById(@Param("id") int id);
}
