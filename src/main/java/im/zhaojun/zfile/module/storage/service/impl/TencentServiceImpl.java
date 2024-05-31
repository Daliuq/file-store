package im.zhaojun.zfile.module.storage.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import im.zhaojun.zfile.core.util.StringUtils;
import im.zhaojun.zfile.module.storage.model.enums.StorageTypeEnum;
import im.zhaojun.zfile.module.storage.model.param.TencentParam;
import im.zhaojun.zfile.module.storage.service.base.AbstractS3BaseFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.net.URL;

/**
 * @author zhaojun
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class TencentServiceImpl extends AbstractS3BaseFileService<TencentParam> {

    @Override
    public void init() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(param.getAccessKey(), param.getSecretKey());
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration
                        (new AwsClientBuilder.EndpointConfiguration(
                                param.getEndPoint(), "cos")).build();
        setUploadCors();
    }
    @Override
    public String getUploadUrl(String path, String name, Long size) {
        String bucketName = param.getBucketName();
        String uploadToPath = StringUtils.concat(param.getBasePath(), path, name);
        uploadToPath = StringUtils.trimStartSlashes(uploadToPath);

        GeneratePresignedUrlRequest req =
                new GeneratePresignedUrlRequest(bucketName, uploadToPath, HttpMethod.PUT);
        URL url = s3Client.generatePresignedUrl(req);

        return url.toExternalForm();
    }
    @Override
    public StorageTypeEnum getStorageTypeEnum() {
        return StorageTypeEnum.TENCENT;
    }

}
