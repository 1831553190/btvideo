package com.demo.btvideo.model;

import java.io.Serializable;
import java.util.List;


//分页信息的实体类
public class PageInfo<T> implements Serializable {

	private int unread;//未读数
	int pageNum;//页数
	private int pageSize;//数据量
	private int totalPage;//总页数
	private int total;  //总消息数
	List<T> list;       //数据列表

	
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
