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
      height: 90px;
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
      width:400px;
      }
      .glyphicon
      {
      text-align: center;
      font-size:18px;
      padding-left:2px;
      padding-right:2px;
      }
      .sign-minus
      {
      text-decoration: none;
      text-align: center;
      font-family: "monospace";
      display: inline-block;
      color: black;
      border: 1px solid black;
      border-radius:0px 0px 6px 6px;
      width:20px;
      height:20px;
      background-color:white;
      }
  
      .sign-plus
      {
      text-decoration: none;
      text-align: center;
      font-family: "monospace";
      display: inline-block;
      color: black;
      border: 1px solid black;
      border-radius:6px 6px 0px 0px;
      width:20px;
      height:20px;
      background-color:white;
      }
      .sign-plus:hover, .sign-minus:hover
      {
      text-decoration: none;
      font-weight: bold;
      color: white;
      background-color:#5cb85c;
      }
      
      
    </style>
  </head>
  <body>
    <c:if test="${empty requestScope.message}">
      <c:redirect url="/index"/>
    </c:if>
    <nav class="navbar navbar-default">
      <div class="container-fluid">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#eafNavbar">
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>                        
          </button>
          <a class="navbar-brand" href="#">EAF online</a>
        </div>
        <div class="collapse navbar-collapse" id="eafNavbar">
          <ul class="nav navbar-nav">
            <li><a href="${pageContext.request.contextPath}/index">Home</a></li>
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
          	<c:if test="${empty sessionScope.loggedIn or sessionScope.loggedIn eq 'false'}">
            	<li><a href="${pageContext.request.contextPath}/login"><span class="glyphicon glyphicon-log-in"></span> Login </a></li>
            </c:if>
            <c:if test="${not empty sessionScope.loggedIn and sessionScope.loggedIn eq 'true'}">
            	<li><a href="${pageContext.request.contextPath}/login"><span class="glyphicon glyphicon-user"></span> Mijn Profiel </a></li>
            </c:if>
            <li class="dropdown">
              <a class="shoppingIcon dropdown-toggle" data-toggle="dropdown">
                <span class="glyphicon glyphicon-shopping-cart"></span> 
                <span class="badge">
                  <c:out value="${not empty itemCount ? itemCount : '0'}"/>
                </span>
              </a>
              <!-- shoopingbasket -->
              <ul class="dropdown-menu">
                <li>
                  <table class="table table-striped">
                    <thead>
                      <tr>
                      	<th>nr.</th>
                        <th>artikel</th>
                        <th>artikelprijs</th>
                        <th>aantal</th>
                        <th>bedrag</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:if test="${not empty sessionScope.cartItemList}">
                        <c:forEach items="${sessionScope.cartItemList}" var="item">
                          <c:url value="/addToCart" var="deleteFromCart">
                            <c:param name="artikelnr" value="${item[0]}"/>
                            <c:param name="action" value="deleteFromCart"/>
                          </c:url>
                          <tr>
                          	<td>${item[0]}</td>
                            <td>${item[1]}</td>
                            <td>&#128; ${item[2]}</td>
                            <td>
                              <c:url value="/addToCart" var="decr">
                                <c:param name="artikelnr" value="${item[0]}"/>
                                <c:param name="action" value="decrease"/>
                              </c:url>
                              
                              <c:url value="/addToCart" var="incr">
                                <c:param name="artikelnr" value="${item[0]}"/>
                                <c:param name="action" value="increase"/>
                              </c:url>
                              
                              <div class="container-fluid">
                              	<a class="sign-plus" href="${incr}">+</a>
                              	<span style="text-align:center; display:block;">${item[3]}</span>
                              	<a class="sign-minus" href="${decr}">-</a>                              	
                              </div>
                              
                            </td>
                            <td>&#128; ${item[4]}</td>
                            <td><a href="${deleteFromCart}" style="text-decoration: none;" class="glyphicon glyphicon-remove-circle"></a></td>
                          </tr>
                        </c:forEach>
                      </c:if>
                      <c:if test="${itemCount eq '0'}">
                        <tr>
                          <td colspan="5"> Het winkelmandje bevat geen items </td>
                        </tr>
                      </c:if>
                      <tr>
                        <td colspan ="5">
                          <h4>totaal: &#128; ${sessionScope.totaal}</h4>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </li>
                <c:url value="/addToCart" var="emptyCart">
                  <c:param name="action" value="emptyCart"/>
                </c:url>
                <li><a href="${emptyCart}" style="margin:10px;" class="btn btn-default">Winkelmandje leeghalen</a></li>
                <li><a href="#" style="margin:10px;" class="btn btn-success">Besteloverzicht</a></li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="alert alert-info">
      <strong>Message: </strong>
      <c:out value="${requestScope.message}"/>
      , Content: 
      <c:out value="${requestScope.contentRoot}"/>
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
            <h3>
              <span>
                <a href="/EAFwebshop/artikel/${artikel[0]}" class="artikelBtn label label-success">Artikel bekijken</a>
                <c:url value="/addToCart" var="addItem">
                  <c:param name="artikelnr" value="${artikel[0]}"/>
                  <c:param name="action" value="newItem"/>
                </c:url>
                <a href="${addItem}" class="artikelBtn label label-default"> In winkelmandje <span class="glyphicon glyphicon-shopping-cart"></span></a>
              </span>
            </h3>
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
              <p>artikelnummer: ${artikelProps[0]}</p>
              <p>categorie id: ${artikelProps[1]}</p>
            </div>
            <div class="col-sm-9">
              <p>${artikelProps[3]}</p>
            </div>
          </div>
          <c:url value="/addToCart" var="addItem">
            <c:param name="artikelnr" value="${artikelProps[0]}"/>
            <c:param name="action" value="newItem"/>
          </c:url>
          <h3 style="float:right"><a href="${addItem}" class="artikelBtn label label-default"> Toevoegen aan winkelmandje <span class="glyphicon glyphicon-shopping-cart"></span></a></h3>
        </div>
      </c:if>
      
      <c:if test="${not empty requestScope.contentRoot and requestScope.contentRoot eq 'loginForm'}">
      	<form class="form-horizontal" method="post" action="${pageContext.request.contextPath}/login">
				  <div class="form-group">
				    <label class="control-label col-sm-2" for="email">Gebruikersnaam / Email:</label>
				    <div class="col-sm-10">
				      <input name="user" type="email" class="form-control" id="email">
				    </div>
				  </div>
				  <div class="form-group">
				    <label class="control-label col-sm-2" for="pwd">Wachtwoord:</label>
				    <div class="col-sm-10"> 
				      <input name="pw" type="password" class="form-control" id="pwd">
				    </div>
				  </div>
				  <div class="form-group"> 
				    <div class="col-sm-offset-2 col-sm-10">
				      <button type="submit" class="btn btn-default">Login</button>
				    </div>
				  </div>
				</form>
				<c:if test="${not empty requestScope.errMsg}">
					<p style="color:red;">${requestScope.errMsg}</p>
				</c:if>
      </c:if> 
      
      <c:if test="${not empty requestScope.contentRoot and requestScope.contentRoot eq 'profiel'}">
	      <div class="row">
	      	<div class="col-md-6 col-sm-12">
	      		<table class="table table-bordered">
		      		<tr><th colspan="2">Mijn gegevens</th></tr>
		      		<tr><td>Klantnummer</td><td>${profielDataList.get(0)}</td></tr>
		      		<tr><td>Gebruikersnaam / email</td><td>${profielDataList.get(1)}</td></tr>
		      		<tr><td>Voornaam</td><td>${profielDataList.get(2)}</td></tr>
		      		<tr><td>Achternaam</td><td>${profielDataList.get(3)}</td></tr>
		      		<tr><td>Straatnaam</td><td>${profielDataList.get(4)}</td></tr>
		      		<tr><td>Huisnummer</td><td>${profielDataList.get(5)}</td></tr>
		      		<tr><td>Postcode</td><td>${profielDataList.get(6)}</td></tr>
		      		<tr><td>Woonplaats</td><td>${profielDataList.get(7)}</td></tr>
		      		<tr><td colspan="2"><a href="#" style="width:100%" class="btn btn-primary">Wijzig gegevens</a></td></tr>
		      		<tr><td colspan="2"><a href="#" style="width:100%" class="btn btn-primary">Wijzig wachtwoord</a></td></tr>
	      		</table>
	      	</div>
	      	<div class="col-md-6 col-sm-12">
	      	<table class="table table-bordered">
	      	<tr><th>Mijn Bestellingen</th></tr>
	      	<tr><td>* bestelling 1</td></tr>
	      	<tr><td>* bestelling 2</td></tr>
	      	</table>
	      	</div>
	      </div>
      </c:if>
        
    </div>
  </body>
</html>