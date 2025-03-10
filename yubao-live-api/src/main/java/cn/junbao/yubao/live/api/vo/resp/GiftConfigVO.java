package cn.junbao.yubao.live.api.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: Junbao
 * @Date: 2025/3/7 23:03
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiftConfigVO {
    private Integer giftId;
    private Integer price;
    private String giftName;
    private Integer status;
    private String coverImgUrl;
    private String svgaUrl;
    private Date createTime;
    private Date updateTime;

}
