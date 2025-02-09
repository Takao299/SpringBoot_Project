package com.example.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.info.S3Info;
import com.example.service.AttachedFileService;
import com.example.service.S3Service;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {

    private final AttachedFileService attachedFileService;
    private final S3Service s3Service;
    private final S3Info s3Info;

    // ファイルリストの表示
    @GetMapping("/list")
    public String getFileList(@ModelAttribute("fileName") String fileName, Model model) {
        List<Resource> resourceList = s3Service.fileList(fileName);
        model.addAttribute("fileList", resourceList);
        return "s3/fileList";
    }

    // ファイルリストの検索
    @PostMapping("/search")
    public String searchFileList(@RequestParam("fileName") String fileName, RedirectAttributes redirectAttributes) {
        // リダイレクト先にパラメーターを渡す
        redirectAttributes.addFlashAttribute("fileName", fileName);
        return "redirect:/s3/list";
    }

    // ファイルアップロード
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("uploadFile") MultipartFile uploadFile) {
        s3Service.upload(uploadFile);
        return "redirect:/s3/list";
    }

    // ファイルダウンロード
    @GetMapping("/download/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileName") String fileName) {
    	if(s3Info.getBuckectName()==null || s3Info.getBuckectName().equals("no_bucket")) {
    		// ローカルストレージからファイルをダウンロード
    		return attachedFileService.download(fileName, null);
    	}else {
    		//AWS S3ストレージからファイルをダウンロード
    		return s3Service.download(fileName, null);
    	}
    }

    // 画像プレビュー
    @GetMapping("/images/{stats}/{imageName}")
    @ResponseBody
    public ResponseEntity<Resource> previewImage(@PathVariable("imageName") String fileName,
												@PathVariable("stats") String stats) {	//statsにはdlかtmpのどちらかが入る
    	if(s3Info.getBuckectName()==null || s3Info.getBuckectName().equals("no_bucket")) {
    		// ローカルストレージからファイルをダウンロード
    		return attachedFileService.download(fileName, stats);
    	}else {
    		//AWS S3ストレージからファイルをダウンロード
    		return s3Service.download(fileName, stats);
    	}
    }


}
