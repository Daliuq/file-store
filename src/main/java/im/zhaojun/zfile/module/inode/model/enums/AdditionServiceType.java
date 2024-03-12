package im.zhaojun.zfile.module.inode.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdditionServiceType {
    TEXT_CLASSIFY("text_classify");
    @EnumValue
    @JsonValue
    private final String value;
}
