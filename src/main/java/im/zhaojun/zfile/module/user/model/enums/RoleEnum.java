package im.zhaojun.zfile.module.user.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    /**
     * 文件
     */
    USER(1),

    /**
     * 文件夹
     */
    FOLDER(2);

    @EnumValue
    @JsonValue
    private final Integer value;
}
