<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- 템플릿 모음 -->
<form name='template' class='none'>
	<textarea name='workingsetGroupNames'>
		<!-- workingsetGroupNames -->
		<div class='tabHeadBox'>
			{{#data}}
			<div class='tabHead'>{{this}}</div>
			{{/data}}
		</div>
		<div class='tabBodyBox'>
			{{#data}}
			<div class='tabBody'>No Loding</div>
			{{/data}}
		</div>
	</textarea>
	
	<!-- <textarea name='projects'>
		<div class='tabHeadBox'>
			{{#each data}}
			<div class='tabHead' data-path="{{this.path}}" data-name="{{@key}}">{{@key}}({{this.severProjectName}})</div>
			{{/each}}
		</div>
		<div class='tabBodyBox'>
			{{#each data}}
			<div class='tabBody'>No Loding</div>
			{{/each}}
		</div>
	</textarea> -->

	<textarea name='workingset'>
		<table class='list workingsetTable'>
		{{#data}}
		<thead class='workingSet_head' id="{{id}}">
			<tr>
				<td class='checkBoxAll'>
					<div class='toggleWorkingSet'>■</div>
				</td>
<!-- 				<td class="wn">PATH</td> -->
				<th class="w3 tar projectName">{{nameFirst name}}</td>
				<th class="w9 tal titleBar">
					<span class='workingsetName'>{{nameLast name}}</span>
<!-- 						<span class='reload btnTxt fr'>[↻]</span> -->
				</td>
				<td class="w1 btn_bb"><span class='dstrBtn btnUploadAll'>▲</span></td>
				<td class="w1 btn_bb" toggleColumnName='CD'><span class='dstrBtn btnUploadAll'>▼</span></td>
				<td class="w1 btn_bb" toggleColumnName='BK'><span class='red'>▼</span></td>
				<th class='w2'>수정</td>
				<th class='w2 ls-1'>OPE업로드
				<th class='w1'>용량</td>
				<td class='wn selectExtensioBtnnBox' style='min-width:200px;'>비고
			</tr>
		</thead>
		<tbody class='workingSet_body' data-name='{{workingSetName}}'>
		{{#each projectFiles}}
		<tr data-path="{{path}}" title="{{path}}" id="{{id}}">
			<td class='tac extension extension_{{extenstion path}}' extension='{{extenstion path}}'></td>
			<td class='tar file projectName'>{{urlFirst path}}</td>
			<td class='tal file'>{{urlLast path}} {{#xif "checkResOpen(path)"}}<a class='open' href='/{{path}}'>[OPEN]</a>{{/xif}}</td>
			<td class='tac oneUploadBtn '><a href='#' class='dstrTxt'>UP</a></td>
			<td class='tac btnDownFormServer'><span class='dstrTxt'>CD</span></td>
			<td class='tac btnBackup'><span class='red'>BK</span></td>
			<td class='tac lastModified' v='{{lastModified}}' title='{{{toDate lastModified}}}'>{{{simpleDateTime lastModified}}}</td>
			<td class='tac opeUploadTime' title='{{{toDate note}}}' v='{{note}}'>{{{simpleDateTime note}}}</td>
			<td class='tac' title='{{size}}'>{{simpleFileSize size}}</td>
			<td class='result'></td>
		</tr>{{/each}}</tbody>
		{{/data}}
		</table>
	</textarea>
	
	<!-- 리로드용 -->
	<textarea name='workingset_projectFile'>
		<tr data-path="{{path}}" title="{{path}}" id="{{id}}">
			<td class='tac extension extension_{{extenstion path}} off' extension='{{extenstion path}}'></td>
			<td class='tal source'>{{path}}</td>
			<td class='tar path file'>{{urlFirst path}}</td>
			<td class='tal path file'>{{urlLast path}}</td>
			<td class='tac path '><a class='dstrTxt'>UP</a></td>
			<td class='tac btnDownFormServer'><span class='dstrTxt'>CD</span></td>
			<td class='result'></td>
			<td class='none'></td>
			<td class='none'></td>
			<td class='none'></td>
		</tr>
	</textarea>
	
	
	
	<textarea name='getFolders'>
		<div class='tabHeadBox'>
			{{#data}}
			<div class='tabHead' data-path="{{this.path}}" >
				{{#xif "this.limitCount!=null"}}
				<span class='bg0' style='z-index:2;padding:4px;margin:-4px;'>{{this.limitCount}} / {{this.count}}</span>
				{{/xif}}
				{{this.name}}<span class='getFolders'>[+]</span>
			</div>
			{{/data}}
		</div>
		<div class='tabBodyBox'>
			{{#data}}
			<div class='tabBody'>{{this.name}} Loding</div>
			{{/data}}
		</div>
	</textarea>
	<textarea name='getRecentFiles'>
		<table class='list workingsetTable'>
		{{#data}}
		<thead class='workingSet_head' id="{{id}}">
			<tr>
				<td class='w1 tac checkBoxAll'></td>
				<th class="w0 tar projectName"></td>
				<th class="w9 tal titleBar">
					<span class='workingsetName'>{{name}}</span>
				</td>
				<td class="w1 btn_bb"><span class='dstrBtn btnUploadAll'>▲</span></td>
				<td class="w1 btn_bb" toggleColumnName='CD'><span class='dstr btnUploadAll'>▼</span></td>
				<td class="w1 btn_bb" toggleColumnName='BK'><span class='red'>▼</span></td>
				<th class='w2'>수정</td>
				<th class='w2 ls-1'>OPE업로드
				<th class='w1'>용량</td>
				<td class='wn selectExtensioBtnnBox'>비고
				</td>
			</tr>
		</thead>
		<tbody class='workingSet_body' data-name='{{workingSetName}}'>
		{{#each subList}}
			<tr data-path="{{path}}" title="{{path}}" id="{{id}}">
				<td class='tac extension extension_{{extenstion path}}' extension='{{extenstion path}}'></td>
				<td></td>
				<td class='tal file'><span class='oneUploadBtn'>{{pathLast path}}</span> {{#xif "checkResOpen(path)"}}<a class='open' href='{{path}}'>[OPEN]</a>{{/xif}}</td>
				<td class='tac oneUploadBtn'><a href="#" class='dstrTxt'>UP</a></td>
				<td class='tac btnDownFormServer'><span class='dstrTxt'>CD</span></td>
				<td class='tac btnBackup'><span class='red'>BK</span></td>
				<td class='tac lastModified' v='{{lastModified}}' title='{{{toDate lastModified}}}'>{{{simpleDateTime lastModified}}}</td>
				<td class='tac opeUploadTime' title='{{{toDate note}}}' v='{{note}}'>{{{simpleDateTime note}}}</td>
				<td class='tac'>{{simpleFileSize size}}</td>
				<td class='result'></td>
			</tr>
		{{/each}}
		{{/data}}
		</table>
	</textarea>
	
	
	<textarea name='projectFileGroups'>
		<div class='tabHeadBox'>
			{{#data}}
			<div class='tabHead'>{{this}}</div>
			{{/data}}
		</div>
		<div class='tabBodyBox' :dstr="distributeLevel">
			{{#data}}
			<div class='tabBody'>No Loding</div>
			{{/data}}
		</div>
	</textarea>
	
	<textarea name='projectFiles'>
		<table>
			{{#data}}
			<tr><td>{{this}}</td><td></td></tr>
			{{/data}}
		</table>
	</textarea>
	
	<textarea name='bookmarks'>
		<div class='tabHeadBox'>
			{{#each data}}
			<div class='tabHead' data-path="{{this.path}}" data-name="{{this.name}}">{{this.name}}</div>
			{{/each}}
		</div>
		<div class='tabBodyBox' :dstr="distributeLevel">
			{{#each data}}
			<div class='tabBody'>No Loding</div>
			{{/each}}
		</div>
	</textarea>
</form>

<script type="text/javascript" src="/js/lib/handlebars-v4.0.11.js"></script>
<script type="text/javascript">
Handlebars.registerHelper('extenstion',	function(v){return v&&v.substring(v.lastIndexOf("\.")+1); });
Handlebars.registerHelper('urlLast',	function(v){return v&&v.substring(v.lastIndexOf("/")+1,v.length); });
Handlebars.registerHelper('urlFirst',	function(v){return v&&v.substring(v.charAt(0)==="/"?1:0,v.indexOf("/",1)); });
Handlebars.registerHelper('pathLast',	function(v){return v&&v.substring(v.lastIndexOf("\\")+1,v.length); });
// Handlebars.registerHelper('pathFirst',	function(v){return v&&v.substring(v.charAt(0)==="/"?1:0,v.indexOf("/",1)); });
Handlebars.registerHelper('nameFirst',	function(v){return v&&v.substring(0,v.indexOf(" ",1)); });
Handlebars.registerHelper('nameLast',	function(v){return v&&v.substring(v.indexOf(" ",1)+1); });
Handlebars.registerHelper('toDate',	function(v){
	return toDate(v);
});
Handlebars.registerHelper('simpleDateTime',	function(v){
	return toSimpleDate(v);
});
Handlebars.registerHelper('simpleFileSize',	function(v){
	if(v<1024*1024) return '1Mb';
	return parseInt(v/1024/1024)+'Mb'
});
Handlebars.registerHelper("xif", function (expression, options) {
	return Handlebars.helpers["x"].apply(this, [expression, options]) ? options.fn(this) : options.inverse(this);
});
Handlebars.registerHelper("x", function(expression, options) {
  var result;
  var context = this;
  with(context) {
    result = (function() {
      try {
        return eval(expression);
      } catch (e) {
        console.warn('Expression: {{x \'' + expression + '\'}}\nJS-Error: ', e, '\nContext: ', context);
      }
    }).call(context); // to make eval's lexical this=context
  }
  return result;
});
function toDate(v){
	return new Date(parseInt(v)).format('yyyyMMdd HH:mm:ss');
}
function toSimpleDate(v){
	// 1분:60 1시간:3600 하루:86400
	if(!v) return ;
	var date = new Date(parseInt(v));
	var now = new Date();
	var s = parseInt((now.getTime()-date.getTime())/1000);

	if(s>86400*30) return "<span class='dt_df'>"+date.format('yyyy-MM-dd')+"</span>"; // 30일 후
	if(s>86400) return "<span class='dt_dd'>"+parseInt(s/86400)+"일전"+"</span>";
	if(s>3600*8) return "<span class='dt_hh'>"+parseInt(s/3600)+"시간전"+"</span>";
	if(s>3600) return "<span class='dt_hh8'>"+parseInt(s/3600)+"시간전"+"</span>";
	if(s>60) return "<span class='dt_mm'>"+parseInt(s/60)+"분전"+"</span>";
	return "<span class='dt_ss'>"+s+"초전"+"</span>";
}
function checkResOpen(path){
	return path.endsWith('jsp') || path.endsWith('html') || path.endsWith('jpg') || path.endsWith('png') || path.endsWith('jpeg');
}
</script>