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
    IMAGE("Image"),

    /**
     * 文本
     */
    TEXT("Text"),

    /**
     * 其他
     */
    Other("Other");


    @EnumValue
    @JsonValue
    private final String value;
}
