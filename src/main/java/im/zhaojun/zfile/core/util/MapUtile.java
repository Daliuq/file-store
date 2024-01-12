package im.zhaojun.zfile.core.util;

import im.zhaojun.zfile.core.model.entity.OwnIdInterface;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

public class MapUtile {
    public static <T, V extends OwnIdInterface> HashMap<T, V> changeListToMapWithId(List<V> list) {
        HashMap<T, V> resultMap = new HashMap<>();

        for (V v : list) {
            @SuppressWarnings("unchecked")
            T id = (T) v.getId();

            if (id != null) {
                resultMap.put(id, v);
            }
        }
        return resultMap;
    }

}
