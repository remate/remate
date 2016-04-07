<%@ page language="java"  contentType="text/html;charset=UTF-8"%>
<%@ page import="com.vdlm.service.sync.queue.SyncProductQueues"%>
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
<h3>ParseShopTaskQueue0：<%=SyncProductQueues.getQueues()[0].size()%></h3>
<h3>ParseShopTaskQueue1：<%=SyncProductQueues.getQueues()[1].size()%></h3>
<h3>ParseShopTaskQueue2：<%=SyncProductQueues.getQueues()[2].size()%></h3>
<h3>ParseShopTaskQueue3：<%=SyncProductQueues.getQueues()[3].size()%></h3>
<h3>ParseShopTaskQueue4：<%=SyncProductQueues.getQueues()[4].size()%></h3>
<h3>ParseShopTaskQueue5：<%=SyncProductQueues.getQueues()[5].size()%></h3>
<h3>ParseShopTaskQueue6：<%=SyncProductQueues.getQueues()[6].size()%></h3>
<h3>ParseShopTaskQueue7：<%=SyncProductQueues.getQueues()[7].size()%></h3>
</body>
</html>