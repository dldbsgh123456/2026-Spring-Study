<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <!-- 톰캣 9버전이라  -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style type="text/css">
.row {
  width:900px;
  margin:0px auto;  
}
</style>
</head>
<body>
  <div class="container">
    <div class="row">
      <a href="../databoard/insert.do" class="btn btn-sm btn-danger">등록</a>
    </div>
    <div class="row" style="margin-top: 10px">
      <table class="table">
        <tr>
          <th width=10% class="text-center">번호</th>
          <th width=45% class="text-center">제목</th>
          <th width=15% class="text-center">이름</th>
          <th width=20% class="text-center">작성일</th>
          <th width=10% class="text-center">조회수</th>
        </tr>
        <c:forEach var="vo" items="${list}">
        <tr>
          <td width=10% class="text-center">${vo.no}</td>
          <td width=45% class="text-center">${vo.subject}</td>
          <td width=15% class="text-center">${vo.name}</td>
          <td width=20% class="text-center">${vo.dbday}</td>
          <td width=10% class="text-center">${vo.hit}</td>
        </tr>
        </c:forEach>
        <tr>
          <td colspan="5" class="text-center">
            <a href="#" class="btn btn-sm btn-info">이전</a>
             ${curpage} page / ${totalpage} pages
            <a href="#" class="btn btn-sm btn-info">다음</a>
          </td>
        </tr>
      </table>
    </div>
  </div>
</body>
</html>