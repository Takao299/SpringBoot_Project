package com.example.data;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.entity.AttachedFile;
import com.example.validator.FileSizeCheck;

import lombok.Data;

@Data
public class FormData implements Serializable {

    private List<AttachedFile> attachedFiles;
    private List<AttachedFile> tempAFs;

    @FileSizeCheck(message="1MB未満のファイルを選択して下さい")
    private List<MultipartFile> fileContents;

    private String context_path; // (/aws-sample)

    public FormData() {
		this.attachedFiles = null;
		this.tempAFs = null;
		this.fileContents = null;
		this.context_path = "";
	}

    public FormData(String path) {
		this.attachedFiles = null;
		this.tempAFs = null;
		this.fileContents = null;
		this.context_path = path;
	}

}

