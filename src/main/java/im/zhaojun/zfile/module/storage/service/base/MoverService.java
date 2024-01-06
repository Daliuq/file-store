package im.zhaojun.zfile.module.storage.service.base;

/**
 * 能下载并且转移文件存放解压的存储源
 * 说人话就是能够通过申请一块服务器缓存区，进行将一个实现DownloadStreamableAndUploadStreamableService的服务源下载到本地
 * 或者从本地上传
 * 或者直接转移
 * 当一个存储源实现了下载到（服务器）本地时，这个存储源才能在线解压
 *
 * @author liuquan
 */
public interface MoverService extends DownloadStreamableService, UploadStreamableService {
    /***
     * 下载指定路径下的文件，上传到本地缓冲区
     * 由于这是由文件服务server实现的，怎么下载文件由服务自己实现
     * @param   downloadFilePathAndName
     *          下载路径文件路径
     * @param   localService
     *          想要上传本地文件服务
     * @param   unloadFilePathAndName
     *          上传本地文件服务路径
     * @return  布尔值
     *
     * @throws  Exception  获取文件列表中出现的异常
     */
     Boolean downloadFromThisToLocalServer(String downloadFilePathAndName ,String unloadFilePathAndName,LocalService localService) throws Exception;
    /***
     * 上传本地缓冲区中指定路径下的文件，到对应服务
     * 由于这是由文件服务server实现的，怎么下载文件由服务自己实现
     * @param   uploadFilePathAndName
     *          上传路径文件路径
     * @param   localService
     *          本地文件服务
     * @param   localFilePathAndName
     *          上传文件在本地文件服务中的path
     * @return  布尔值
     *
     * @throws  Exception  获取文件列表中出现的异常
     */
     Boolean uploadFromLocalServerToThis(String uploadFilePathAndName ,String localFilePathAndName,LocalService localService) throws Exception;
    /***
     * 下载指定路径下的文件，上传到另一个缓冲区
     *
     * @param   unloadFilePathAndName
     *          上传文件夹路径
     * @param   downloadFilePathAndName
     *          下载文件路径
     * @param   localService
     *          move过程中走这个本地文件服务缓冲区
     * @return  文件及文件夹列表
     *
     * @throws  Exception  获取文件列表中出现的异常
     */
     Boolean moveFromThisToOtherServer(String downloadFilePathAndName , String unloadFilePathAndName, MoverService DownloadStreamableAndUploadStreamableService) throws Exception;
}
