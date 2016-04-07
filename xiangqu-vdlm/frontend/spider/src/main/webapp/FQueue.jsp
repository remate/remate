<%@ page language="java"  contentType="text/html;charset=UTF-8"%>
<%@ page import="com.vdlm.spider.queue.TaskQueues"%>
<html>
<head>
<title>FQueue</title>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<meta http-equiv="Cache-Control" content="no-store"/>
<meta http-equiv="Pragma" content="no-cache"/>
<meta http-equiv="Expires" content="0"/>
<%request.setCharacterEncoding("UTF-8");%>
</head>
<body>
<h3>ParseShopTaskQueue：<%=TaskQueues.getParseShopTaskQueue().size()%></h3>
<h3>ParseItemTaskQueue：<%=TaskQueues.getParseItemTaskQueue().size()%></h3>
<h3>SmsMessageTaskQueue：<%=TaskQueues.getSmsMessageTaskQueue().size()%></h3>
<h3>PushMessageTaskQueue：<%=TaskQueues.getPushMessageTaskQueue().size()%></h3>
</body>
</html>