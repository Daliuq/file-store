package im.zhaojun.zfile.module.storage.model.param;

import im.zhaojun.zfile.module.storage.annotation.StorageParamItem;
import im.zhaojun.zfile.module.storage.model.enums.StorageParamTypeEnum;
import lombok.Getter;

/**
 * 代理上传下载参数
 *
 * @author zhaojun
 */
@Getter
public class ProxyTransferParam extends BaseParam{

	@StorageParamItem(name = "历史服务存储源",
			order = 10000000,
			type = StorageParamTypeEnum.SWITCH,
			defaultValue = "true",description = "是否作为历史服务存储源",ignoreInput = false)
	private boolean history;



	@StorageParamItem(name = "加速域名", required = false,
			description = "如不配置加速域名，则使用服务器中转下载, 反之则使用加速域名下载.")
	private String domain;

	@StorageParamItem(name = "生成签名链接",
			type = StorageParamTypeEnum.SWITCH,
			defaultValue = "true",
			description = "下载会生成带签名的下载链接, 如不想对外开放直链, 可以防止被当做直链使用.")
	private boolean isPrivate;

	@StorageParamItem(name = "下载签名有效期",
			required = false,
			defaultValue = "1800",
			description = "用于下载签名的有效期, 单位为秒, 如不配置则默认为 1800 秒.")
	private Integer tokenTime;

}
