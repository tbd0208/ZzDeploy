<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>

<script src="https://unpkg.com/react@15/dist/react.min.js"></script> <!--react -->
<script src="https://unpkg.com/react-dom@15/dist/react-dom.min.js"></script> <!-- react-dom -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/babel-core/5.8.38/browser.min.js"></script> <!-- babel -->

<title></title>
</head>
<body>
	<div id="root"></div>
	//화면구현을 위해 react-dom 이 필요합니다. 
	<script type="text/babel"> // text/javascript가 아니에요 babel입니다.
		ReactDOM.render(<h1>Hello,React!!</h1>, document.getElementById('root'))
	</script>
</body>
</html>