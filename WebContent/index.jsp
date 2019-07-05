<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
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
      .sign-minus, .sign-plus
      {
      text-decoration: none;
      text-align: center;
      display: inline-block;
      font-family: "monospace";
      color: black;
      margin:3px;
      width:16px;
      border: 1px solid black;
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
          </ul>
          <ul class="nav navbar-nav navbar-right">

          	<c:if test="${empty sessionScope.loggedIn or sessionScope.loggedIn eq 'false'}">
            	<li><a href="${pageContext.request.contextPath}/login"><span class="glyphicon glyphicon-log-in"></span> Log in </a></li>
            </c:if>
            
            <c:if test="${not empty sessionScope.loggedIn and sessionScope.loggedIn eq 'true'}">
            	<li><a href="${pageContext.request.contextPath}/login"><span class="glyphicon glyphicon-user"></span> Mijn Profiel </a></li>
            	<li><a href="${pageContext.request.contextPath}/login?logout=true"><span class="glyphicon glyphicon-log-out"></span> Log uit </a></li>
            </c:if>
      
            <li class="dropdown">
              <a class="shoppingIcon dropdown-toggle" data-toggle="dropdown">
                <span class="glyphicon glyphicon-shopping-cart"></span> 
                <span class="badge">
                  <c:out value="${not empty itemCount ? itemCount : '0'}"/>
                </span>
              </a>
              <ul class="dropdown-menu">
                <li>
                  <table class="table table-striped">
                    <thead>
                      <tr>
                      	<th>nr.</th>
                        <th>artikel</th>
                        <th>gewicht</th>
                        <th>btw</th>
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
                            <td>${item[2]} gram</td>
                            <td>${item[3]} %</td>
                            <td>&#128; ${item[4]}</td>
                             <c:url value="/addToCart" var="decr">
                               <c:param name="artikelnr" value="${item[0]}"/>
                               <c:param name="action" value="decrease"/>
                             </c:url>
                              
                             <c:url value="/addToCart" var="incr">
                               <c:param name="artikelnr" value="${item[0]}"/>
                               <c:param name="action" value="increase"/>
                            </c:url>
                            <td>
                              <a class="sign-plus" href="${incr}">+</a>${item[5]}<a class="sign-minus" href="${decr}">-</a>   	                              	 
                            </td>
                            <td>&#128; ${item[6]}</td>
                            <td><a href="${deleteFromCart}" style="text-decoration: none;"> X </a></td>
                          </tr>
                        </c:forEach>
                      </c:if>
                      <c:if test="${itemCount eq '0'}">
                        <tr>
                          <td colspan="7"> Het winkelmandje bevat geen items </td>
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
                <li><a href="${pageContext.request.contextPath}/newOrder" style="margin:10px;" class="btn btn-success">Besteloverzicht</a></li>
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
            <p>Gewicht: ${artikel[3]} gram</p>
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
              <p>gewicht: ${artikelProps[5]} gram</p>
              <p>BTW: ${artikelProps[6]} %</p>
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
	      	<div class="col-md-4 col-sm-12">
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
	      		</table>
	      		<p><a href="#" class="btn btn-success">Wijzig gegevens</a></p>
		      	<p><a href="#" class="btn btn-success">Wijzig wachtwoord</a></p>
	      	</div>
	      	<div class="col-md-4 col-sm-12">
	      	<table class="table table-bordered">
	      	<tr><th colspan="5">Mijn Bestellingen</th></tr>
	      	<tr>
		      	<th>#nr</th>
		      	<th>Besteldatum</th>
		      	<th>Bedrag</th>
		      	<th>Status</th>
		      	<th style="text-align:center;">Order</th>
		      </tr>
	      	<c:forEach items="${sessionScope.customerOrderList}" var="customerOrder">
	      		<tr>
	      			<td>${customerOrder[0]}</td>
	      			<td>${customerOrder[1]}</td>
	      			<td>&#128; ${customerOrder[2]}</td>
	      			<td>${customerOrder[3]}</td>
	      			<td style="text-align:center;"><a href="${pageContext.request.contextPath}/order/${customerOrder[0]}" class="btn btn-success"><span class="glyphicon glyphicon-eye-open"></span></a></td>
	      		</tr>
	      	</c:forEach>
	      	</table>
	      	</div>
	      </div>
      </c:if>
      
      <c:if test="${not empty requestScope.contentRoot and requestScope.contentRoot eq 'order'}">
	      <div>
	      	<table class="table table-bordered">
	      		<tr><td colspan ="9">Order nr: ${bestelnr}</td></tr>
	      		<tr>
		      		<th>artikelnr</th>
		      		<th>productnaam</th>
		      		<th>prijs</th>
		      		<th>aantal</th>
		      		<th>gewicht</th>
		      		<th>totaalgewicht</th>
		      		<th>BTW</th>
		      		<th>bedragExBTW</th>
		      		<th>bedrag</th>
	      		</tr>
	      		<c:forEach items="${orderArtikelList}" var="orderArtikel">
	      			<tr>
		      			<td>${orderArtikel[0]}</td>
		      			<td>${orderArtikel[1]}</td>
		      			<td>&#128; ${orderArtikel[2]}</td>
		      			<td>${orderArtikel[3]}</td>
		      			<td>${orderArtikel[4]} gram</td>
		      			<td>${orderArtikel[5]} gram</td>
		      			<td>${orderArtikel[6]} %</td>
		      			<td>&#128; ${orderArtikel[7]}</td>
		      			<td>&#128; ${orderArtikel[8]}</td>
	      			</tr>
	      		</c:forEach>
	      	</table>
	      	
	      	<table class="table table-bordered">
	      		<tr><td>Aantal Artikelen: </td><td>${requestScope.costSpecificationList.get(0)}</td></tr>
	      	  <tr><td>Totaal bedrag ex BTW: </td><td> &#128; ${costSpecificationList.get(1)}</td></tr>
	      		<tr><td>BTW (9,0%): </td><td> &#128; ${costSpecificationList.get(2)} </td></tr>
	      		<tr><td>BTW (21,0%): </td><td> &#128; ${costSpecificationList.get(3)} </td></tr>
	      		<tr><td>Totaal bedrag inc. BTW</td><td> &#128; ${costSpecificationList.get(4)} </td></tr>
	      		<tr><td>Ordergewicht: </td><td>${costSpecificationList.get(5)} kg</td></tr>
	      		<tr><td>Aantal paketten: </td><td>${costSpecificationList.get(6)}</td></tr>
	      		<tr><td>Verzendkosten inc. btw: </td><td> &#128; ${costSpecificationList.get(7)} </td></tr>
	      		<tr style="font-size:16px;"><td><b>Totaal inclusief verzendkosten: </b></td><td><b>&#128; ${costSpecificationList.get(8)}</b></td></tr>
	      	</table>
	      	<p><a href="${pageContext.request.contextPath}/login">Terug naar Mijn Profiel</a></p>
	      </div>
      </c:if>
      
      <c:if test="${not empty requestScope.contentRoot and requestScope.contentRoot eq 'newOrder'}">
	      <div>
				   <table class="table table-bordered">
				   		<tr style="background-color:#5cb85c;font-size:16px;color:white;"><td colspan="10"><b>Mijn Winkelmandje</b></td></tr>
				      <tr>
				         <th>#nr</th>
				         <th>product</th>
				         <th>aantal</th>
				         <th>prijs per stuk</th>
				         <th>stukgewicht</th>
				         <th>totaalgewicht</th>
				         <th>bedrag ex. BTW</th>
				         <th>BTW (9%)</th>
				         <th>BTW (21%)</th>
				         <th>bedrag inc. BTW</th>
				      </tr>
				      <c:forEach items="${orderArtikelDetailList}" var="artikelItem">
				         <tr>
				            <td>${artikelItem[0]}</td>
				            <td>${artikelItem[1]}</td>
				            <td>${artikelItem[2]} stuks</td>
				            <td>&#128; ${artikelItem[3]}</td>
				            <td>${artikelItem[4]} gram</td>
				            <td>${artikelItem[5]} kg</td>
				            <td>&#128; ${artikelItem[6]}</td>
				            <td>&#128; ${artikelItem[7]}</td>
				            <td>&#128; ${artikelItem[8]}</td>
				            <td>&#128; ${artikelItem[9]}</td>
				         </tr>
				      </c:forEach>
				   </table>
				</div>
				<div>
			   <table class="table table-bordered">
			   		<tr style="background-color:#5cb85c;font-size:16px;color:white;"><td colspan="8"><b>Mijn Order Specificatie</b></td></tr>
			      <tr>
			         <th>Aantal Artikelen</th>
			         <th>Totaalbedrag ex. BTW</th>
			         <th>BTW totaal (9%)</th>
			         <th>BTW totaal (21%)</th>
			         <th>Ordergewicht</th>
			         <th>Aantal Paketten</th>
			         <th>Verzendkosten inc. btw</th>
			         <th>Totaalbedrag inc. Verzendkosten</th>
			      </tr>
			      <tr>
			         <td>${totalOrderSpecification[0]} artikelen</td>
			         <td>&#128; ${totalOrderSpecification[1]}</td>
			         <td>&#128; ${totalOrderSpecification[2]}</td>
			         <td>&#128; ${totalOrderSpecification[3]}</td>
			         <td>${totalOrderSpecification[4]} kg</td>
			         <td>${totalOrderSpecification[5]}</td>
			         <td>&#128; ${totalOrderSpecification[6]}</td>
			         <td><b style="font-size:16px;">&#128; ${totalOrderSpecification[7]}</b></td>
			      </tr>
			   </table>
				</div>
      </c:if>   
    </div>
  </body>
</html>