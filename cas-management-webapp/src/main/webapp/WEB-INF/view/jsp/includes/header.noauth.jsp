<!doctype html>
<%@ page language="java" session="false" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html xml:lang="en" lang="en" ng-app="casmgmt">
    <head>
        <title><spring:message code="management.services.header.apptitle" /></title>
        <meta name="version" content="<%=org.jasig.cas.CasVersion.getVersion()%>" />
        <link rel="icon" href="<c:url value="/images/favicon.ico" />" type="image/x-icon" />
        <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
        <link rel="stylesheet" href="<c:url value="/css/cas-management.css" />" type="text/css" />
    </head>
    <body class="casmgmt-app" ng-controller="actionsController as action">

<header id="casmgmt-header">
    <nav class="navbar navbar-default">
        <div class="container-fluid">
            <div class="navbar-header">
                <a href="manage.html">
                    <div class="navbar-brand">
                        <img src="<c:url value="/images/logo_cas.png"/>" alt="Jasig CAS logo" />
                    </div>
                    <h4><spring:message code="management.services.header.apptitle" /></h4>
                </a>
            </div><%-- end .navbar-header div --%>
        </div><%-- end .container-fluid div --%>
    </nav><%-- end .navbar div --%>
</header><%-- end .casmgmt-header header --%>
