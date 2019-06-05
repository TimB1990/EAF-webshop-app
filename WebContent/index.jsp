<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>  
  
<!DOCTYPE html>
<html>
	<head>
	<title>EAF Home</title>
	<meta charset="utf-8">
	  <meta name="viewport" content="width=device-width, initial-scale=1">
	  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
	  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.0/jquery.min.js"></script>
	  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
	  <style>
	  .info
	  {
			border-radius: 25px;
  		border: 2px solid #73AD21;
  		padding: 20px;
  		margin:10px;  
	  	
	  }
	  
	  .artikelBtn
	  {
	  	height:60px;
	  	width: 120px;
	  	color:white;
	  	text-decoration: none;
	  }
	  
	  .shoppingIcon
	  {
	  	color:white;
	  	text-decoration: none;
	  }
	  
	  .shoppingIcon:focus
	  {
	  	outline: none;
	  	border: none;
	  }
	  
	  .shop-item
	  {
	  	display:inline-block;
	  	padding: 10px;
	  	width:200px;
	  }
	  
	  .glyphicon
	  {
	  	font-size:20px;
	  	padding-left:4px;
	  	padding-right:4px;
	  }
	  
	  </style>
	</head>
	<body>
	
		<c:if test="${empty requestScope.message}">
		<c:redirect url="/index"/>
		</c:if>

		<nav class="navbar navbar-inverse">
		  <div class="container-fluid">
		    <div class="navbar-header">
		      <a class="navbar-brand">EAF online</a>
		    </div>
		    <ul class="nav navbar-nav">
		      <li class="active"><a href="${pageContext.request.contextPath}/index">Home</a></li>
		      <li class="dropdown">
		        <a class="dropdown-toggle" data-toggle="dropdown" href="#">Product categorie
		        <span class="caret"></span></a>
		        <ul class="dropdown-menu">
		          <c:forEach items="${sessionScope.categorieList}" var="categorie">
		          	<c:url value="/categorie/${categorie[1]}" var="cat"></c:url>
								<li><a href="${cat}"> ${categorie[1]}</a></li>
							</c:forEach>
		        </ul>
		      </li>
		      <li><a href="#">About</a></li>
		      <li><a href="#">Contact</a></li>
		      <!--  <li><a href="#">login</a></li> -->
		    </ul>
		    <ul class="nav navbar-nav navbar-right">
		    	<li><a href="#"><span class="glyphicon glyphicon-log-in"></span> Login </a></li>
		    	<li class="dropdown">
		    		<a class="shoppingIcon dropdown-toggle" data-toggle="dropdown">
		    			<span class="glyphicon glyphicon-shopping-cart"></span> <span class="badge"> 2</span>
		    		</a>
		    		<ul class="dropdown-menu">
		          <li class="shop-item">205 productnaam <input style="width:40px" type="number" min="1"/></li>
		          <li class="shop-item">205 productnaam <input style="width:40px" type="number" min="1"/></li>
		        </ul>
		    	</li>
	      	
		    </ul>
		  </div>
		</nav>
		
		<div class="alert alert-info">
  		<strong>Message: </strong><c:out value="${requestScope.message}"/>, Content: <c:out value="${requestScope.contentRoot}"/>
		</div>
		
		<div class="container">
			<c:if test="${not empty requestScope.contentRoot and requestScope.contentRoot eq 'berichten'}">
			<c:forEach items="${requestScope.berichtList}" var="bericht">
				<div class="well" style="border-radius:20px">
				<h2>${bericht[1]}</h2>
				<p>${bericht[2]}</p>
				<p>${bericht[3]}</p>			
				</div>
			</c:forEach>
			</c:if>
			
			<c:if test="${not empty requestScope.contentRoot and requestScope.contentRoot eq 'artikelen'}">
			<div class="info">
			<h2>${requestScope.categorieProps[3]}</h2>
			<p>categorie ID: ${requestScope.categorieProps[0]}</p>
			<p>${requestScope.categorieProps[1]}</p>
			<p>${requestScope.categorieProps[2]}</p>
			</div>
			
			<c:forEach items="${requestScope.artikelList}" var="artikel">
			<div class="well well-sm" style="border-radius:20px">
			<h3>${artikel[1]}</h3>
			<p>Artikelnummer: ${artikel[0]}</p>
			<h3><span class="label label-default"> &#128; ${artikel[2]} </span></h3>
			<h3><span><a href="/EAFwebshop/artikel/${artikel[0]}" class="artikelBtn label label-success">Artikel bekijken</a>
			<a href="#" class="artikelBtn label label-default"> In winkelmandje <span class="glyphicon glyphicon-shopping-cart"></span></a></span></h3>
			</div>
			</c:forEach>
			</c:if>
			
			<c:if test="${not empty requestScope.contentRoot and requestScope.contentRoot eq 'single-artikel'}">
			<div class="info">
			<h1>${requestScope.artikelProps[2]} <span style="float:right" class="label label label-success"> &#128; ${requestScope.artikelProps[4]}</span></h1>
			</div>
			
			<div class="container-fluid info">
				<div class="row">
					<div class="col-sm-3">
						<p>artikelnummer: ${requestScope.artikelProps[0]}</p>
						<p>categorie id: ${requestScope.artikelProps[1]}</p>
					</div>
					<div class="col-sm-9">
						<p>${requestScope.artikelProps[3]}</p>
					</div>
				</div>
				<h3 style="float:right"><a href="#" class="artikelBtn label label-default"> Toevoegen aan winkelmandje <span class="glyphicon glyphicon-shopping-cart"></span></a></h3>
			</div>

			</c:if>
			
		</div>
	</body>
</html>