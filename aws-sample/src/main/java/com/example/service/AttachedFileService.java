package com.example.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.AttachedFile;
import com.example.info.LocalInfo;
import com.example.repository.AttachedFileRepository;
import com.example.util.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachedFileService {

	private final AttachedFileRepository attachedFileRepository;
	private final LocalInfo localInfo;

    // --------------------------------------------------------------------------------
    // 添付ファイル格納処理
    // --------------------------------------------------------------------------------
    public AttachedFile tempAttachedFile(int foreignId, MultipartFile fileContents) {
        // アップロード元ファイル名
        String fileName = fileContents.getOriginalFilename();

        // 格納フォルダの存在チェック
        File uploadDir = new File(localInfo.getAttachedTempFilePath());
        if (!uploadDir.exists()) {
            // ないのでディレクトリ作成
            uploadDir.mkdirs();
        }

        // 添付ファイルの格納(upload)時刻を取得
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String createTime = sdf.format(new Date());

        // テーブルへ格納するインスタンス作成
        AttachedFile af = new AttachedFile();
        af.setForeignId(foreignId);
        af.setFileName(fileName);
        af.setCreateTime(createTime);
        af.setDelete_pic("no_delete");

        // アップロードファイルの内容を取得
        byte[] contents;
        try (BufferedOutputStream bos
               = new BufferedOutputStream(
                     new FileOutputStream(
                         Utils.makeAttahcedFilePath(localInfo.getAttachedTempFilePath(), af)	//返り値は"C:/spbt/uploadFiles/2024062118394813394fcdd510c7d8f95.png"という感じ
         ))) {
            // アップロードファイルを書き込む
            contents = fileContents.getBytes();
            bos.write(contents);

            // テーブルへ記録
            //attachedFileRepository.saveAndFlush(af);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return af;
	}

    public void saveAttachedFile(AttachedFile af) throws IOException {

        // 移動先　格納フォルダの存在チェック
        File uploadDir = new File(localInfo.getAttachedFilePath());
        if (!uploadDir.exists()) {
            // ないのでディレクトリ作成
            uploadDir.mkdirs();
        }
    	//ファイル名
    	String fname = af.getCreateTime()+af.getFileName();
		//移動元
		Path path_1 = Paths.get(localInfo.getAttachedTempFilePath(),fname);
        // 一時保存ファイルが存在しなければリターン
        if (!path_1.toFile().exists()) {
        	//System.out.println("tmp_file is not exist");
        	return;
        }
		//移動先
		Path path_2 = Paths.get(localInfo.getAttachedFilePath(),fname);
		//移動を実行
		Files.move(path_1,path_2);

        // テーブルへ記録
        attachedFileRepository.saveAndFlush(af);  // ⑦
    }


    // --------------------------------------------------------------------------------
    // 添付ファイル削除処理(AttachedFile.idで削除)　単体削除
    // --------------------------------------------------------------------------------
    public void deleteAttachedFile(int afId) {
        AttachedFile af = attachedFileRepository.findById(afId).get();
        File file = new File(Utils.makeAttahcedFilePath(localInfo.getAttachedFilePath(), af));
        file.delete();
        //System.out.println("deleteAttachedFile by afId "+afId);
    }

    //↓使わない
    // --------------------------------------------------------------------------------
    // 添付ファイル削除処理(Facility.idで削除)　複数削除
    // --------------------------------------------------------------------------------
    public void deleteAttachedFiles(Integer foreignId) {
        File file;
        List<AttachedFile> attachedFiles
            = attachedFileRepository.findByForeignIdOrderByAfId(foreignId);
        for (AttachedFile af : attachedFiles) {
            file = new File(Utils.makeAttahcedFilePath(localInfo.getAttachedFilePath(), af));
            file.delete();
        }
        //System.out.println("deleteAttachedFiles by facilityId "+foreignId);
    }


    // --------------------------------------------------------------------------------
    // 一時保存ファイル削除　最終更新日時が60秒経っていたら削除する
    // --------------------------------------------------------------------------------
    public void deleteTempAttachedFiles() {
        Calendar calendar = Calendar.getInstance();
    	File dir = new File(localInfo.getAttachedFilePath());
        String[] tmpFileList = dir.list();

        for (String str : tmpFileList) {
        	File file = new File(localInfo.getAttachedTempFilePath()+"/"+str);
            if( calendar.getTimeInMillis() - file.lastModified() > 60000L) { //60,000ミリ秒＝60秒
            	System.out.println("current:"+calendar.getTimeInMillis() +" lastModified:"+ file.lastModified()
            	+" progress:" +(calendar.getTimeInMillis() - file.lastModified())+ " "+ str);
            	file.delete();
            }
        }
    }


    // --------------------------------------------------------------------------------
    // ファイルダウンロード（stats有りは画像プレビュー用）
    // --------------------------------------------------------------------------------
    public ResponseEntity<Resource> download(String fileName, String stats) {
    	String path = localInfo.getAttachedTempFilePath();
    	if(stats==null || stats.equals("dl"))
    		path = localInfo.getAttachedFilePath();

    	//バイトデータ格納のため
        ByteArrayResource ba_resource = null;

        // オブジェクト取得
        try (InputStream is = new BufferedInputStream(new FileInputStream(path + "/" + fileName))) { //resource.getInputStream()から変更
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

}