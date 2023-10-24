<%@page import="zx.deploy.config.DistributeLevel"%>
<%@page import="zx.deploy.config.Config"%>
<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="zx.deploy.config.ProjectInfo"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" 			uri="http://java.sun.com/jsp/jstl/core"			%>
<%@ taglib prefix="fmt" 		uri="http://java.sun.com/jsp/jstl/fmt"			%>
<%@ taglib prefix="fn" 			uri="http://java.sun.com/jsp/jstl/functions" 	%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.ico">
	
	<%@include file="/view/$inc/inc-head.jspf"%>
	<link href="srcMng.css" rel="STYLESHEET" type="text/css" lang="utf-8">
	<script src="/js/Forms.js" type="text/javascript"></script>
	<script src="/js/Table.js" type="text/javascript"></script>
	<script src="srcMng.js" type="text/javascript"></script>
</head>
<body>
	<jsp:include page="/view/$inc/inc-layout-top.jsp"/>
	<form id="sourceMng" class='flex-c dstrForm' :dstr="distributeLevel">
		<div id='topMenu' class='topMenu'>
			<div class='dstrStatusBox'>
				
				<!-- MAIN ICON with DSTR MARKS -->
				<div id='prjDstr_home' class="dstrMarkHome">
					<i v-for="v in ['DEV','STG','OPE','OPE']" class='dstrMark' :dstr='v'></i>
				</div>
				
				<!-- DEPLOY LEVELS SWITCH -->
				<label v-for="(v,i) in ['DEV','STG','OPE','ALL']" class='dstrStatus' :dstr='v'>
					<input type="radio" class='rectBox' v-model="distributeLevel" name="distributeLevel" :value="v" :accesskey="'QWER'[i]">{{v}}
				</label>
				
			</div>
			
			<!-- PROJECTS TOUCH-->
			<div class='projects'>
				<label v-for="x in PROJECT_NAMES">
					<input type="radio" name='project' :value="x" v-model="project">{{x}}
				</label>
				<button type='button' class='dstrBtn' v-on:click="touchJsp(distributeLevel,project)">touch</button>
			</div>
			
			<div id='toggleTableColBox'>
				<span>FIELD SHOW : </span> 
<!-- 						<label><input type="checkbox">PATH</label> -->
				<label title="카피다운"><input type="checkbox" ref="CD" name='fieldShow' id='fieldShow-CD' value='CD'>CD</label>
				<label title="서버백업"><input type="checkbox" ref="BK" name='fieldShow' id='fieldShow-BK' value='BK'>BK</label>
<%-- 						<label title="<%=Config.LOCAL_PROJECT_PRE_PATH%>"><input type="checkbox">LOCAL_PATH</label> --%>
<!-- 						<label ><input type="checkbox">FTP_PATH</label> -->
<%-- 						<label title="<%=Config.SERVER_BACKUP_PRE_PATH%>"><input type="checkbox">BACKUP_PATH</label> --%>
			</div>
			<div>
				<span>OPTION :</span> 
				<label><input type="checkbox" id="hasUpload" checked="checked">Upload</label>
				<label><input type="checkbox" id="hasBackup" checked="checked">Backup(Daily)</label>
				<label><input type="checkbox" id="makeDirectory">makeDirectory</label>
			</div>
			<div>
				<span>limitDays : </span>
				<select id='limitDays' name='limitDays'>
					<option value='1'>1<option value='3' selected="selected">3<option value='7'>7<option value='31'>31<option value='365'>365<option value=''>전체</select>
				<span class="menu-reload btnTxt">[↻]</span>
			</div>
			<div>
				<label v-for="x in ['java','xml','jar']" class="extension" :extension="x">
					<input type="checkbox" name='extension' :value="x">{{x.toUpperCase()}}
				</label> | 
				<label v-for="x in ['jsp','js','css','html','etc']" class="extension" :extension="x">
					<input type="checkbox" name='extension' :value="x">{{x.toUpperCase()}}
				</label>
			</div>
		</div>
		
		<!-- ### CONTENTS -->
		<div id='tabBox-00' class='flex-c list-body'>
			<div class='tabHeadBox' id='tabHead-00'>
				<div class='tabHead workingSet' v-on:click='onTabByWorkingset' tab-type='workingset'>
					★ WORKING SET <span class='reload btnTxt' v-on:click=reload>[↻]</span>
				</div>
				<!-- <div class='tabHead'>
						★ Bookmark<span class='reload btnTxt'>[↻]</span>
				</div> -->
				
				<!-- PROJECT NAME -->
				<div v-for="(x,k,i) in PROJECT_INFO_MAP" class='tabHead projectTab' :project-tab='k' :data-path='x.localPath' v-on:click='onTabByProject(x,i)' tab-type='project'>
					<img class='project-favicon' :src='favicons[k]'>
					<span style='font-weight: bold;'>{{k}}</span>(<span>{{getServerProjectNames(x)}}</span>) <span class='reload btnTxt'>[↻]</span>
					<!-- DSTR MARK -->
					<div class='dstrMarkBox'>
						<template v-for="level in DSTR_LEVELS">
							<span v-for="a,i in x.was.dstrMap[level]" class='dstrMark' :dstr='level' :title='(x.web?.dstrMap[level][i].ip||"NONE") +" / "+ x.was?.dstrMap[level][i].ip'></span>
						</template>
					</div>
				</div>
				
			</div>
			<div class='tabBodyBox' id='tabBody-00'>
				<ws-tab :tabs="tabBox01" class='tabBody workingsetTabBox' path-type='/'>## WORKING_SET ##</ws-tab>
<!-- 				<div class='tabBody' id='tabBox-01'>## WORKING_SET ##</div> -->
	<!-- 			<div class='tabBody' id='tabBox-02'>## PROJECT BOOKMARK ##</div> -->
				<ws-tab v-for="(x,k) in PROJECT_INFO_MAP" :key="k" :tabs="pjTabBox[k]" class='tabBody' path-type='\'>No Loading {{k}}</ws-tab>
			</div>
		</div>
		
		<!-- ### PROJECT SERVER INFO -->
		<div id='serverInfo' style=''>
			<table style='width:100%;'><thead><tr><th>프로젝트<th>WAS/WEB서버폴더<th>WAS IP<th>WEB IP<th>DEV IP<th>P폴더(STG)
			<tbody>
			<tr v-for="info in PROJECT_INFO_MAP">
				<td class='tac'>
					<li><b>{{info.nicName}}</b></li>
					<li>{{info.name}}</li>
				<td><li>{{info.was.prePath}}<b>{{info.serverProjectName}}</b></li>
					<li v-if="info.web">{{info.web.prePath}}<b>{{info.serverProjectName}}</b></li>
				<td><li v-for="a in info.was?.dstrMap.OPE">{{a.ip}}</li>
				<td><li v-for="b in info.web?.dstrMap.OPE">{{b.ip}}</li>
				<td><li v-for="c in info.web?.dstrMap.DEV">{{c.ip}}</li>
				<td><li v-if="info.web?.dstrMap.STG">{{info.web.prePath}}<b>{{info.serverProjectNames[1]}}</b></li>
					<li v-if="info.was?.dstrMap.STG">{{info.was.prePath}}<b>{{info.serverProjectNames[1]}}</b></li>
			</tr>
			</table>
		</div>
		
		<div class='flex' style='background: #FFF;width:100%;border-top:1px solid #BBB;outline: 1px solid #fff;color:#000;'>
			<div style='padding:8px;'>
		<%-- 		LOCAL_PROJECT_PRE_PATH : <strong><%=Config.LOCAL_PROJECT_PRE_PATH%></strong> |  --%>
		<%-- 		MYWEB_LOG_PRE_PATH : <strong><%=Config.MYWEB_LOG_PRE_PATH%></strong> | --%>
		<%-- 		WORKINGSET_XML_PATH : <strong><%=Config.WORKINGSET_XML_PATH%></strong> | --%>
				경매시간 : 11:30~12:00 / 13:30~14:00 / 15:30~16:00 |
				BACKUP_PATH : <strong><%=Config.SERVER_BACKUP_PRE_PATH%></strong>
			</div>
			<div doublebar></div>
			<div id='projectAttrBx' style='padding:8px;'>
			</div>
		</div>
	</form>
	
	<!-- 제우스 관리자 -->
	<div id="JEUS">
		<div class='tabHeadBox'>
			<div dstr='DEV' v-on:click="tab=tab===0?'':0" :class='["dstrStatus",{on:tab===0}]' accesskey="Z">DEV00</div>
			<div dstr='OPE' v-on:click="tab=tab===1?'':1" :class='["dstrStatus",{on:tab===1}]' accesskey="X">OPE01</div>
			<div dstr='OPE' v-on:click="tab=tab===2?'':2" :class='["dstrStatus",{on:tab===2}]' accesskey="C">OPE02</div>
		</div>
		<div class='tabBodyBox'>
			<iframe :class="{on:tab===0}" :src="'http://192.168.1.150:9744/webadmin'" c></iframe>
			<iframe :class="{on:tab===1}" :src="'http://172.30.0.8:9744/webadmin'"></iframe>
			<iframe :class="{on:tab===2}" :src="'http://172.30.0.10:9744/webadmin'"></iframe>
		</div>
	</div>
	
	<template id="cmTab">
		<div>
			tab test
			<div class='tabHeadBox'>
				<div v-for="x,i in heads" v-on:click="tab=tab===i?'':i" :class='["tabHead",{on:tab===i}]'>{{x}}</div>
			</div>
			<div class='tabBodyBox'>
				<div v-for="x,i in a" :class="{on:tab===i}" class='tabBody'>{{x}}</div>
			</div>
		</div>
	</template>
	
	<template id="wsTab">
		<div v-if="tabs===null">
			<slot></slot>
		</div>
		<div v-else>
			<div class='tabHeadBox'>
				<div v-for="x,i in tabs" :class='["tabHead"]' v-on:click='headClick(i)'>{{x.name||x}}</div>
			</div>
			<div class='tabBodyBox'>
				<div v-for="wsList,i in bodys" :class='["tabBody",{on:tab===i}]'>
					<table  class='list workingsetTable'>
					<template v-for="ws,i in wsList">
					<thead class='workingSet_head' :id="ws.id">
						<tr>
							<td class='checkBoxAll'>
								<div class='toggleWorkingSet'>■</div>
							</td>
							<th class="tar projectName">{{getNameFirst(ws.name)}}</td>
							<th class="w9 tal titleBar">
								<span class='workingsetName'>{{getNameLast(ws.name)}}</span>
							</td>
							<td class="w1 btn_bb"><span class='dstrBtn btnUploadAll'>▲</span></td>
							<td class="w1 btn_bb" toggleColumnName='CD'><span class='dstrBtn btnDownFormServerAll'>▼</span></td>
							<td class="w1 btn_bb" toggleColumnName='BK'><span class='red'>▼</span></td>
							<th class='w2'>수정</td>
							<th class='w2 ls-1'>OPE업로드
							<th class='w1'>용량</td>
							<td class='wn selectExtensioBtnnBox' style='min-width:200px;'>비고
							</td>
						</tr>
					</thead>
					<tbody class='workingSet_body' :data-name='ws.name'>
					<tr v-for="f,i in ws.projectFiles||ws.subList" :data-path="f.path" :title="f.path" :id="f.id">
						<td class='tac extension' :extension='getExtenstion(f.path)'></td>
						<td class='tar file projectName'>{{pathType==='/'?getPathFirst(f.path,pathType):''}}</td>
						<td class='tal file'>{{getPathLast(f.path,pathType)}}<a v-if="checkResOpen(f.path)" class='open' :href='"/"+f.path'>[OPEN]</a></td>
						<td class='tac oneUploadBtn '><a href='#' class='dstrTxt'>UP</a></td>
						<td class='tac btnDownFormServer'><span class='dstrTxt'>CD</span></td>
						<td class='tac btnBackup'><span class='red'>BK</span></td>
						<td class='tac lastModified' :v='f.lastModified' :title='toDate(f.lastModified)' v-html="toSimpleDate(f.lastModified)"></td>
						<td class='tac opeUploadTime' :title='toDate(f.note)' :v='f.note' v-html="toSimpleDate(f.note)"></td>
						<td class='tac' :title='f.size' v-html="toSimpleFileSize(f.size)"></td>
						<td class='result'></td>
					</tr>
					</template>
					</table>
				</div>
			</div>
		</div>
	</template>
	
	<jsp:include page="srcMng-template.jsp"/>
	
	<textarea id='uploadRes' wrap='off' style='padding: 5px;position: absolute;right:5px;bottom:42px;background: #fff;color: #000;width:600px;height:100px;resize: none;font-size: 12px;line-height: 1.1em;' contenteditable="false" translate="no" readonly="readonly" spellcheck="false"></textarea>
	
<jsp:include page="/view/$inc/inc-layout-btm.jsp"/>
<jsp:include page="srcMng-main-vue.jsp"/>

</body>
</html>