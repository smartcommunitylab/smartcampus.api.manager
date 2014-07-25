<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html lang="en" ng-app="apiManager">

<head>
  <title>Api Manager</title>
  <base href="<%=request.getContextPath() %>/" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
 
  <link rel="stylesheet" href="css/bootstrap.min.css" />
  <link rel="stylesheet" href="css/font-awesome.min.css" />
  <link rel="stylesheet" href="css/animate.min.css" />
  <link rel="stylesheet" href="css/datepicker3.css" />  
  <link rel="stylesheet" href="css/railscasts.css">
  <link rel="stylesheet" href="css/strength.css">
  <link rel="stylesheet" href="css/openservices.css" />
</head>

<body>
    <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
    <div class="container-fluid">
      <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
          <span class="sr-only">Toggle navigation</span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="#">Api Manager</a>
      </div>

      <div ng-controller="navCtrl" class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
        <ul class="nav navbar-nav">
          <li ng-class="{active: loc[0] === 'dashboard'}">
            <a href="#">Dashboard</a>
          </li>
          <li ng-class="{active: loc[0] === 'apis'}">
            <a href="apis">Apis</a>
          </li>
          <li ng-class="{active: loc[0] === 'publish'}">
          	<a href="#">Publish</a>
          </li>
        </ul>
       
      </div>
    </div>
    <!-- /.navbar-collapse -->
  </nav>
  <!-- ng-hide="loc[0] === ''" -->
  <div class="container-fluid" id="bread">
  <!-- ng-hide="location === '/'" -->
    <ol ng-controller="breadCtrl" ng-hide="loc[0] === ''" class="animated fadeIn breadcrumb">
      <li ng-repeat="location in loc">
        <a ng-href="{{loc.slice(0,$index+1).join('/')}}">{{locTitles[$index] ? locTitles[$index] : location}}</a>
      </li>
    </ol>
  </div>
  <div class="container">
    <div class="view" ng-view></div>
  </div>

  <div id="footer">
    <div class="container">
      <p class="text-muted credit">&copy; 2013 Smart Campus Lab &middot; <a href="#">Privacy</a> &middot; <a href="#">Terms</a>
      </p>
    </div>
  </div>
  
  <script src="js/vendor/underscore.min.js"></script>
  <script src="js/vendor/jquery-2.1.0.min.js"></script>
  <script src="js/vendor/bootstrap.min.js"></script>
  <script src="js/vendor/datepicker.js"></script>
  <script src="js/vendor/highlight.pack.js"></script>
  <script src="js/vendor/moment-with-langs.min.js"></script>
  
  <script src="js/vendor/angular.min.js"></script>
  <script src="js/vendor/angular-route.min.js"></script>
  <script src="js/vendor/angular-resource.min.js"></script>
  <script src="js/vendor/angular-cookies.min.js"></script>
  <script src="js/vendor/angular-highlight.min.js"></script>
  <script src="js/vendor/ui-bootstrap-tpls-0.10.0.min.js"></script>
  <script src="js/routingConfig.js"></script>
  <script src="js/app.js"></script>
  <script src="js/controllers.js"></script>
  <script src="js/directives.js"></script>
  <script src="js/services.js"></script>
  <script src="js/remoteapi.js"></script>

</body>

</html>

