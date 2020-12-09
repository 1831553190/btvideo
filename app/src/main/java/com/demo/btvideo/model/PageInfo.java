package com.demo.btvideo.model;

import java.io.Serializable;
import java.util.List;


//分页信息的实体类
public class PageInfo<T> implements Serializable {

	private int unread;

	int pageNum;

	private int pageSize;
	private int totalPage;
	private int total;
	List<T> list;



	
	public int getUnread() {
		return unread;
	}
	public List<T> getList() {
		return list;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}

	public void setList(List<T> list) {
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
