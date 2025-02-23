package cn.junbao.yubao.live.living.provider.dao.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @Author linhao
 * @Date created in 9:07 下午 2023/1/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LivingRoomPO {

    private Integer id;
    private Long roomId;
    private Long anchorId;
    private Integer type;
    private String roomName;
    private String covertImg;
    private Integer status;
    private Integer watchNum;
    private Integer goodNum;
    private Date startTime;
    private Date updateTime;

}
