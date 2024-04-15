package im.zhaojun.zfile.module.inode.addition_module.utile;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModelNames {
    TEXT_CLASSIFY("text_class");
    @EnumValue
    @JsonValue
    private final String value;
}
