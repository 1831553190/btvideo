package com.demo.btvideo.model;

import java.util.List;

public class PageInfo {

	private int pageNum;
	private int pageSize;
	private int totalPage;
	private int total;
	List<VideoInfo> list;

	public List<VideoInfo> getList() {
		return list;
	}

	public void setList(List<VideoInfo> list) {
		this.list = list;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
