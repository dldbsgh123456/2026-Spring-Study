<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style type="text/css">
.row {
  width:700px;
  margin:0px auto;  
}
</style>
<script type="text/javascript" src="http://unpkg.com/vue@3/dist/vue.global.js"></script>
</head>
<body>
  <div class="container" id="app">
    <div class="row">
      <h3 class="text-center">자료 등록</h3>
      <form method="post" action="../databoard/insert_ok.do" enctype="multipart/form-data">
      <table class="table">
        <tr>
          <th width=15% class="text-center success">이름</th>
          <td width=85%>
            <input type=text name=name size=20 class="input-sm" required>
          </td>
        </tr>
        <tr>
          <th width=15% class="text-center success">제목</th>
          <td width=85%>
            <input type=text name=subject size=60 class="input-sm" required>
          </td>
        </tr>
        <tr>
          <th width=15% class="text-center success">내용</th>
          <td width=85%>
            <textarea rows="10" cols="6" name=content required></textarea>
          </td>
        </tr>
        <tr>
          <th width=15% class="text-center success">첨부파일</th>
          <td width=85%>
            <table class="table">
              <tr>
                <td class="text-right">
                  <button class="btn-xs btn-success" @click="addFile()" type="button">Add</button>
                  <button class="btn-xs btn-success" @click="removeFile()" type="button">Remove</button>
                </td>
              </tr>
            </table>
            <table class="table">
              <tbody>
                <tr v-for="(file,index) in files" :key="index">
                  <th width=20%>files{{index+1}}</th>
                  <td width=80%>
                    <input type=file :name="'files['+index+']'">
                  </td>
                </tr>
              </tbody>
            </table>
          </td>
        </tr>
        <tr>
          <th width=15% class="text-center success">비밀번호</th>
          <td width=85%>
            <input type=password name=pwd size=10 class="input-sm" required>
          </td>
        </tr>
        <tr>
          <td colspan="2" class="text-center">
            <button class="btn-sm btn-primary" type=submit>등록</button>
            <button class="btn-sm btn-primary" type=button onclick="javascript:history.back()">취소</button>
          </td>
        </tr>
      </table>
      </form>
    </div>
  </div>
  <script>
   const app=Vue.createApp({
	   data(){
		   return{
			   files:[]
		   }
	   },
	   methods:{
		   addFile(){
			   this.files.push({})
		   },
		   removeFile(){
			   if(this.files.length>0)
			   {
				   this.files.pop()
			   }
		   }
	   }
   }).mount("#app")
  </script>
</body>
</html>