<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
     톰캣 : 9버전 => javax
           10버전이상 => jakarata
           버전 호환 
 --%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style type="text/css">
.row{
 width:960px;
 margin: 0px auto
}
p{
 overflow: hidden;
 white-space: nowrap;
 text-overflow: ellipsis;
}
</style>
</head>
<body>
  <div class="container">
    <div class="row">
       <c:forEach var="vo" items="${list}">
		  <div class="col-md-4">
		    <div class="thumbnail">
		      <a href="/w3images/lights.jpg">
		        <img src="${vo.poster }" title="${vo.address}" style="width:250px;height:130px;object-fit:cover">
		        <div class="caption">
		          <p>${vo.name }</p>
		        </div>
		      </a>
		    </div>
		  </div>
		</c:forEach>
   </div>
   <div class="row text-center" style="margin-top: 10px">
     <ul class="pagination">
      <c:if test="${startPage>1}">
       <li><a href="../main/main.do?page=${startPage-1}">&laquo;</a></li>
        </c:if>
      <c:forEach var="i" begin="${startPage }" end="${endPage }">
       <li ${i==curpage?"class=active":"" }><a href="../main/main.do?page=${i}">${i}</a></li>
      </c:forEach> 
      <c:if test="${endPage<totalpage}">
       <li><a href="../main/main.do?page=${endPage+1}">&raquo;</a></li>
      </c:if>
     
     </ul>
   </div>
  </div>
</body>
</html>