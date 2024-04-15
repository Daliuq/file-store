package im.zhaojun.zfile.module.inode.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileTypeEnum {
    /**
     * 图片
     */
    image("image"),

    /**
     * 文本
     */
    text("text"),

    /**
     * 其他
     */
    other("other");


    @EnumValue
    @JsonValue
    private final String value;
}
