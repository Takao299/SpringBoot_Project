package com.example.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.example.entity.AttachedFile;
import com.example.info.S3Info;
import com.example.repository.AttachedFileRepository;
import com.example.util.CopyObject;
import com.example.util.DeleteObjectNonVersionedBucket;
import com.example.util.Utils;

import io.awspring.cloud.core.io.s3.PathMatchingSimpleStorageResourcePatternResolver;
import io.awspring.cloud.core.io.s3.SimpleStorageProtocolResolver;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3Service {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private S3Info s3Info;

    @Autowired
    private CopyObject copyObj;

    @Autowired
    private DeleteObjectNonVersionedBucket deleteObj;

    @Autowired
	private AttachedFileRepository attachedFileRepository;

    @Autowired
    public void setupResolver(ApplicationContext applicationContext, AmazonS3 amazonS3) {
    	this.resourcePatternResolver = new PathMatchingSimpleStorageResourcePatternResolver(amazonS3,applicationContext);
    }

    @Autowired
    public void configureResourceLoader(AmazonS3 amazonS3, DefaultResourceLoader resourceLoader) {
        SimpleStorageProtocolResolver simpleStorageProtocolResolver = new SimpleStorageProtocolResolver(amazonS3);
        // As we are calling it by hand, we must initialize it properly.
        simpleStorageProtocolResolver.afterPropertiesSet();
        resourceLoader.addProtocolResolver(simpleStorageProtocolResolver);
    }

    //ファイルリスト取得
    public List<Resource> fileList(String fileName) {
        if (fileName == null) {
            fileName = "";
        }

        List<Resource> resourceList = null;
        try {
            Resource[] resourceArray = resourcePatternResolver
                    .getResources("s3://" + s3Info.getBuckectName() + "/**/*" + fileName + "*.*");
            resourceList = Arrays.asList(resourceArray);
        } catch (IOException e) {
            log.error("S3FileListError", e);
        }
        return resourceList;
    }

    //ファイルアップロード
    public void upload(MultipartFile uploadFile) {

        Resource resource = this.resourceLoader
                .getResource("s3://" + s3Info.getBuckectName() + "/" + uploadFile.getOriginalFilename());
        WritableResource writableResource = (WritableResource) resource;

        try (OutputStream outputStream = writableResource.getOutputStream()) {
            outputStream.write(uploadFile.getBytes());
        } catch (IOException e) {
            log.error("S3FileUploadError", e);
        }
    }

    //ファイルダウンロード (未使用)
    public InputStream download(String fileName) throws IOException {
        Resource resource = this.resourceLoader.getResource("s3://" + s3Info.getBuckectName() + "/" + fileName);
        return resource.getInputStream();
    }

    //tempファイルアップロード
    public AttachedFile uploadTemp(int foreignId, MultipartFile uploadFile) {

        // upload時刻を取得
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String createTime = sdf.format(new Date());

        // テーブルへ格納するインスタンス作成
        AttachedFile af = new AttachedFile();
        af.setForeignId(foreignId);
        af.setFileName(uploadFile.getOriginalFilename());
        af.setCreateTime(createTime);
        af.setDelete_pic("no_delete");

        Resource resource = this.resourceLoader
                .getResource("s3://" + s3Info.getBuckectTempName() + "/" + createTime + uploadFile.getOriginalFilename());
        WritableResource writableResource = (WritableResource) resource;

        try (OutputStream outputStream = writableResource.getOutputStream()) {
            outputStream.write(uploadFile.getBytes());
        } catch (IOException e) {
            log.error("S3FileUploadError", e);
        }

        return af;
    }

    //ファイルコピーとDB保存
    public void copyAndSave(AttachedFile af) {

    	//S3の別バケットにコピーする
        String[] str = {af.getCreateTime() + af.getFileName(), s3Info.getBuckectTempName(), s3Info.getBuckectName()};
        copyObj.copy(str);

        // テーブルへ記録
        attachedFileRepository.saveAndFlush(af);
    }


    //ファイルダウンロード（stats有りは画像プレビュー用）
    public ResponseEntity<Resource> download(String fileName, String stats) {
    	String path = s3Info.getBuckectTempName();
    	if(stats==null || stats.equals("dl"))
    		path = s3Info.getBuckectName();

        Resource resource = this.resourceLoader.getResource("s3://" + path + "/" + fileName);

        ByteArrayResource ba_resource = null;

        // オブジェクト取得
        try (InputStream is = resource.getInputStream()) {
            // ByteArrayResource生成
        	ba_resource = new ByteArrayResource(is.readAllBytes());

	        // 拡張子からContent-Typeタイプを求める
	        String fext = fileName.substring(fileName.lastIndexOf(".") + 1); // 拡張子(. の後ろ)
	        String contentType = Utils.ext2contentType(fext);
	        String newTab = "inline";		//画像ファイルは別タブで開く（pdfも）
	        if (stats!=null || contentType.equals("")) {	//画像ファイル以外はローカル保存
	        	contentType = "application/octet-stream";
	        	newTab = "attachment; filename=" + fileName;
	        }

	        // HTTPヘッダー生成
	        HttpHeaders header = new HttpHeaders();
	        header.add(HttpHeaders.CONTENT_DISPOSITION, newTab);

	        // ファイルダウンロード
	        return ResponseEntity.ok()
	                .headers(header)
	                .contentLength(ba_resource.contentLength())
	                .contentType(MediaType.parseMediaType(contentType))
	                .body(ba_resource);

        } catch (IOException e) {
            log.error("FileDownloadError", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }


    //S3からファイルを物理削除
    public void delete(AttachedFile af) {

    	//S3の別バケットにコピーする
        String[] str = {s3Info.getBuckectName(), af.getCreateTime() + af.getFileName()};
        try {
			deleteObj.delete(str);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }

}
