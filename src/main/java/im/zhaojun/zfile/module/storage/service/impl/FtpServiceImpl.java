package im.zhaojun.zfile.module.storage.service.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpMode;
import im.zhaojun.zfile.core.exception.file.operator.DisableProxyDownloadException;
import im.zhaojun.zfile.core.util.CodeMsg;
import im.zhaojun.zfile.core.util.RequestHolder;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.storage.model.enums.FileTypeEnum;
import im.zhaojun.zfile.module.storage.model.enums.StorageTypeEnum;
import im.zhaojun.zfile.module.storage.model.param.FtpParam;
import im.zhaojun.zfile.module.storage.model.result.FileItemResult;
import im.zhaojun.zfile.module.storage.service.base.AbstractProxyTransferService;
import im.zhaojun.zfile.module.storage.service.base.LocalService;
import im.zhaojun.zfile.module.storage.service.base.MoverService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhaojun
 */
@Service
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FtpServiceImpl extends AbstractProxyTransferService<FtpParam> {

    private Ftp ftp;

    @SneakyThrows(IOException.class)
    @Override
    public void init() {
        Charset charset = Charset.forName(param.getEncoding());
        ftp = new Ftp(param.getHost(), param.getPort(), param.getUsername(),
                        param.getPassword(), charset);
        ftp.getClient().type(FTP.BINARY_FILE_TYPE);
        ftp.setMode(FtpMode.Passive);
    }


    @Override
    public synchronized List<FileItemResult> fileList(String folderPath) {
        ftp.reconnectIfTimeout();
        String fullPath = StringUtils.concat(param.getBasePath(), folderPath);
        ftp.cd(fullPath);
        FTPFile[] ftpFiles;
        try {
            ftp.getClient().changeWorkingDirectory("/");
            ftpFiles = ftp.getClient().listFiles(fullPath);
        } catch (Exception e) {
            throw ExceptionUtil.wrapRuntime(e);
        }

        List<FileItemResult> fileItemList = new ArrayList<>();

        for (FTPFile ftpFile : ftpFiles) {
            // 跳过 ftp 的本目录和上级目录
            if (Arrays.asList(".", "..").contains(ftpFile.getName())) {
                continue;
            }
            FileItemResult fileItemResult = ftpFileToFileItem(ftpFile, folderPath);
            fileItemList.add(fileItemResult);
        }
        return fileItemList;
    }


    @Override
    public StorageTypeEnum getStorageTypeEnum() {
        return StorageTypeEnum.FTP;
    }


    @Override
    public FileItemResult getFileItem(String pathAndName) {
        FTPFile[] ftpFiles;
        try {
            ftpFiles = ftp.getClient().listFiles(pathAndName);
        } catch (IOException e) {
            throw ExceptionUtil.wrapRuntime(e);
        }

        if (ArrayUtil.isEmpty(ftpFiles)) {
            throw ExceptionUtil.wrapRuntime(new FileNotFoundException());
        }

        FTPFile ftpFile = ftpFiles[0];

        String folderPath = StringUtils.getParentPath(pathAndName);
        return ftpFileToFileItem(ftpFile, folderPath);
    }


    @Override
    public synchronized boolean uncheckNewFolder(String path, String name) {
        String fullPath = StringUtils.concat(param.getBasePath(), path, name);
        return ftp.mkdir(fullPath);
    }


    @Override
    public synchronized boolean deleteFile(String path, String name) {
        String fullPath = StringUtils.concat(param.getBasePath(), path, name);
        return ftp.delFile(fullPath);
    }


    @Override
    public synchronized boolean deleteFolder(String path, String name) {
        String fullPath = StringUtils.concat(param.getBasePath(), path, name);
        return ftp.delDir(fullPath);
    }


    @Override
    public synchronized boolean renameFile(String path, String name, String newName) {
        ftp.reconnectIfTimeout();
        String srcPath = StringUtils.concat(param.getBasePath(), path, name);
        String distPath = StringUtils.concat(param.getBasePath(), path, newName);

        try {
            return ftp.getClient().rename(srcPath, distPath);
        } catch (IOException e) {
            throw ExceptionUtil.wrapRuntime(e);
        }
    }


    @Override
    public boolean renameFolder(String path, String name, String newName) {
        return renameFile(path, name, newName);
    }


    @Override
    public synchronized ResponseEntity<Resource> downloadToStream(String pathAndName) throws IOException {
        // 如果配置了域名，还访问代理下载 URL, 则抛出异常进行提示.
        if (StrUtil.isNotEmpty(param.getDomain())) {
            throw new DisableProxyDownloadException(CodeMsg.STORAGE_SOURCE_FILE_DISABLE_PROXY_DOWNLOAD, storageId);
        }

        ftp.reconnectIfTimeout();
        HttpServletResponse response = RequestHolder.getResponse();
        pathAndName = StringUtils.concat(param.getBasePath(), pathAndName);
        String fileName = FileUtil.getName(pathAndName);
        String folderName = FileUtil.getParent(pathAndName, 1);

        OutputStream outputStream = response.getOutputStream();

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM.getType());
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + StringUtils.encodeAllIgnoreSlashes(fileName));

        ftp.download(folderName, fileName, outputStream);
        return null;
    }


    @Override
    public synchronized Long uploadFileWithoutInodeChange(String pathAndName, InputStream inputStream) {
        try {
            String fullPath = StringUtils.concat(param.getBasePath(), pathAndName);
            String fileName = FileUtil.getName(pathAndName);
            String folderName = FileUtil.getParent(fullPath, 1);
            // 使用 CountingInputStream 包装原始输入流
            CountingInputStream countingInputStream = new CountingInputStream(inputStream);
            // 上传文件
            ftp.upload(folderName, fileName, countingInputStream);
            // 获取上传的文件大小
            // 返回上传的文件大小
            return countingInputStream.getByteCount();
        } catch (Exception e) {
            throw ExceptionUtil.wrapRuntime(e);
        }
    }


    private FileItemResult ftpFileToFileItem(FTPFile ftpFile, String folderPath) {
        FileItemResult fileItemResult = new FileItemResult();
        fileItemResult.setName(ftpFile.getName());
        fileItemResult.setSize(ftpFile.getSize());
        fileItemResult.setTime(ftpFile.getTimestamp().getTime());
        fileItemResult.setType(ftpFile.isDirectory() ? FileTypeEnum.FOLDER : FileTypeEnum.FILE);
        fileItemResult.setPath(folderPath);

        if (fileItemResult.getType() == FileTypeEnum.FILE) {
            fileItemResult.setUrl(getDownloadUrl(StringUtils.concat(folderPath, fileItemResult.getName())));
        }
        return fileItemResult;
    }

    @Override
    public InputStream getDownloadStream(String pathAndName) throws IOException {
        //TODO 未测试
        // 如果配置了域名，还访问代理下载 URL, 则抛出异常进行提示.
        if (StrUtil.isNotEmpty(param.getDomain())) {
            throw new DisableProxyDownloadException(CodeMsg.STORAGE_SOURCE_FILE_DISABLE_PROXY_DOWNLOAD, storageId);
        }

        ftp.reconnectIfTimeout();
        pathAndName = StringUtils.concat(param.getBasePath(), pathAndName);
        String fileName = FileUtil.getName(pathAndName);
        String folderName = FileUtil.getParent(pathAndName, 1);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ftp.download(folderName, fileName, byteArrayOutputStream);

        // 通过 ByteArrayInputStream 将 ByteArrayOutputStream 中的数据转换为 InputStream
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    @Override
    public Boolean downloadFromThisToLocalServer(String downloadFilePathAndName, String unloadFilePathAndName, LocalService localService) throws Exception {
        //1，调用this里的方法，获取输入流资源InputStreamResource
        InputStream downloadStreamResource = this.getDownloadStream(downloadFilePathAndName);
        //2. 调用localService的写入方法，写入//3. 返回成功失败
        if (downloadStreamResource != null) {
            //InputStream在uploadFileStreamToLocal中关闭
            localService.uploadFileStreamToLocal(unloadFilePathAndName,downloadStreamResource);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean uploadFromLocalServerToThis(String uploadFilePathAndName, String localFilePathAndName, LocalService localService) throws Exception {
        //1，调用localService中方法，get输入流
        InputStream localFileResource = localService.getLocalFileResource(localFilePathAndName);
        //2. 调用localService的写入方法，写入//3. 返回成功失败
        //InputStream在uploadStream中关闭
        if (localFileResource != null) {
            ByteArrayOutputStream baos =new ByteArrayOutputStream();
            IOUtils.copy(localFileResource,baos);
            localFileResource.close();
            this.uploadFile(uploadFilePathAndName,baos.toByteArray());
            baos.close();
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean moveFromThisToOtherServer(
            String downloadFilePathAndName,
            String unloadFilePathAndName,
            MoverService otherService) throws Exception {
        //1，调用this里的方法，获取输入流资源InputStreamResource
        InputStream downloadStreamResource = this.getDownloadStream(downloadFilePathAndName);
        //2. 调用localService的写入方法，写入//3. 返回成功失败
        if (downloadStreamResource != null) {
            //InputStream在uploadStream中关闭
            otherService.uploadStream(unloadFilePathAndName,downloadStreamResource);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void uploadStream(String unloadFilePathAndName, InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        IOUtils.copy(inputStream,baos);
        inputStream.close();
        this.uploadFile(unloadFilePathAndName,baos.toByteArray());
        baos.close();
    }
}
