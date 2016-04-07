package com.vdlm.bos.file;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.vdlm.dal.type.FileBelong;

public class ImageUploadForm {
	
	/**
	 * 文件属于产品、店铺
	 */
	@NotNull(message = "{valid.notBlank.message}")
	private FileBelong belong;
	
	@NotNull(message = "{valid.fileBelong.message}")
	@Valid
	private List<MultipartFile> file;

	
	public FileBelong getBelong() {
		return belong;
	}

	public void setBelong(FileBelong belong) {
		this.belong = belong;
	}
	
	public List<MultipartFile> getFile() { 
		return file;
	}

	public void setFile(List<MultipartFile> file) {
		this.file = file;
	}
}
