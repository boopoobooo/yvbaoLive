package cn.junbao.yubao.live.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7516137443387758078L;
    private Long userId;
    private String phone ;
    private boolean loginSucces;
    private String desc;

    public static UserLoginDTO loginSuccess(Long userId,String phone) {
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setLoginSucces(true);
        userLoginDTO.setUserId(userId);
        userLoginDTO.setPhone(phone);
        return userLoginDTO;
    }
    public static UserLoginDTO loginError(String desc) {
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setLoginSucces(false);
        userLoginDTO.setDesc(desc);
        return userLoginDTO;
    }
}
