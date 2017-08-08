package com.huntkey.rx.sceo.monitor.commom.model;

public class NodeDetailSaveTO {
	private String nodeId;//节点ID
	private String pid;//临时单ID
	private String nodeCode;//节点编码
	private String nodeName;//节点名称
	private String nodeDefine;//节点定义
	private String majorStaff;//主管人
	private String assistStaff;//协管人
	private String beginDate;//生效时间
	private String endDate;//失效时间
	private String superNode;//上级节点
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
	private String subNode;//下级节点
	private String leftNode;//左节点
	private String rightNode;//右节点
	private String belongClass;//从属资源类
	private String levelCode;//层级编码
	private String currentLevel;//节点所在层级
}
