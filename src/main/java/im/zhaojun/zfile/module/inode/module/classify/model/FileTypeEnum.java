package im.zhaojun.zfile.module.inode.module.classify.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileTypeEnum {


    TEXT("text"),
    IMAGE("image");
    @EnumValue
    @JsonValue
    private final String value;
}
