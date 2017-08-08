package com.huntkey.rx.sceo.monitor.commom.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 节点
 * @author fangkun
 *
 */
public class NodeDetailTO {
	@JSONField(name="id")
	private String nodeId;//节点id
	@JSONField(name="pid")
	private String pid;//临时单ID
	@JSONField(name="mode004")
	private String nodeCode;//节点编码
	@JSONField(name="mode005")
	private String nodeName;//节点名称
	@JSONField(name="mode006")
	private String nodeDefine;//节点定义
	@JSONField(name="mode007")
	private String majorStaff;//主管人
	@JSONField(name="mode008")
	private String assistStaff;//协管人
	@JSONField(name="mode009")
	private String beginDate;//生效时间
	@JSONField(name="mode010")
	private String endDate;//失效时间
	@JSONField(name="mode011")
	private String superNode;//上级节点
	@JSONField(name="mode012")
	private String subNode;//下级节点
	@JSONField(name="mode013")
	private String leftNode;//左节点
	@JSONField(name="mode014")
	private String rightNode;//右节点
	@JSONField(name="mode015")
	private String belongClass;//从属资源类
	@JSONField(name="mode016")
	private String levelCode;//层级编码
	@JSONField(name="mode017")
	private String currentLevel;//节点所在层级
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getNodeCode() {
		return nodeCode;
	}
	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeDefine() {
		return nodeDefine;
	}
	public void setNodeDefine(String nodeDefine) {
		this.nodeDefine = nodeDefine;
	}
	public String getMajorStaff() {
		return majorStaff;
	}
	public void setMajorStaff(String majorStaff) {
		this.majorStaff = majorStaff;
	}
	public String getAssistStaff() {
		return assistStaff;
	}
	public void setAssistStaff(String assistStaff) {
		this.assistStaff = assistStaff;
	}
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getSuperNode() {
		return superNode;
	}
	public void setSuperNode(String superNode) {
		this.superNode = superNode;
	}
	public String getSubNode() {
		return subNode;
	}
	public void setSubNode(String subNode) {
		this.subNode = subNode;
	}
	public String getLeftNode() {
		return leftNode;
	}
	public void setLeftNode(String leftNode) {
		this.leftNode = leftNode;
	}
	public String getRightNode() {
		return rightNode;
	}
	public void setRightNode(String rightNode) {
		this.rightNode = rightNode;
	}
	public String getBelongClass() {
		return belongClass;
	}
	public void setBelongClass(String belongClass) {
		this.belongClass = belongClass;
	}
	public String getLevelCode() {
		return levelCode;
	}
	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}
	public String getCurrentLevel() {
		return currentLevel;
	}
	public void setCurrentLevel(String currentLevel) {
		this.currentLevel = currentLevel;
	}
}
