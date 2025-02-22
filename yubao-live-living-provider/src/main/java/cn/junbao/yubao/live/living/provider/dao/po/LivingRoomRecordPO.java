package cn.junbao.yubao.live.living.provider.dao.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author linhao
 * @Date created in 9:07 下午 2023/1/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LivingRoomRecordPO {

    private Integer id;
    private Long anchorId;
    private Integer type;
    private String roomName;
    private String covertImg;
    private Integer status;
    private Integer watchNum;
    private Integer goodNum;
    private Date startTime;
    private Date endTime;
    private Date updateTime;


    @Override
    public String toString() {
        return "LivingRoomRecordPO{" +
                "id=" + id +
                ", anchorId=" + anchorId +
                ", type=" + type +
                ", roomName='" + roomName + '\'' +
                ", covertImg='" + covertImg + '\'' +
                ", status=" + status +
                ", watchNum=" + watchNum +
                ", goodNum=" + goodNum +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
