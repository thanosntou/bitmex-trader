<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/trade-panel.css">
    <%--<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.3.1/css/all.css" integrity="sha384-mzrmE5qonljUremFsqc01SB46JvROS7bZs3IO2EmfFsd15uHvIt+Y8vEf7N7fWAU" crossorigin="anonymous">--%>
    <title>Trade Panel</title>
</head>
<body>
<div id="all" class="container-fluid" style="width: 100%">



    <%--wallstreetbar--%>
    <%--<div class="row">--%>
        <%--<div class="col-sm-12" id="wsbar">--%>
            <%--<%@include file="wallstreet-navbar.jsp" %>--%>
        <%--</div>--%>
    <%--</div>--%>

    <%-- menu--%>
    <%--<div class="row" id="dash-nav" style="margin-top: 1%; height: 5vh; background-color: #2e3f4d;" >--%>
        <%--<div class="col-sm-2" id="brand">--%>

        <%--</div>--%>
        <%--<div class="col-sm-10" id="menu">--%>
            <%--<a class="navbar-brand" href="${pageContext.request.contextPath}/trade">Trade</a>--%>
            <%--&lt;%&ndash;<form:form action="${pageContext.request.contextPath}/logout" method="POST">&ndash;%&gt;--%>
                <%--&lt;%&ndash;<input type="submit" class="btn btn-outline-dark" value="Logout"/>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</form:form>&ndash;%&gt;--%>
        <%--</div>--%>
    <%--</div>--%>

    <%--main content--%>
    <div style="height: 100vh;" class="row">

        <%--// side bar--%>
        <div class="col-sm-2" style="background-color: #3b5264">
            <div class="row" style="height: 18vh;">
                <div class="card" style="width: 18rem; background-color: inherit; border: none">
                    <div class="card-body" style="color: white; margin-top: 1%">
                        <h5 class="card-title"><i class="fas fa-user"></i> Welcome, </h5>
                        <%--<h6>${user.email}</h6>--%>
                        <br>

                        <%--<a href="#" class="card-link">Card link</a>--%>
                            <form:form action="${pageContext.request.contextPath}/logout" method="POST">
                                <input type="submit" class="btn btn-outline-info" value="Logout"/>
                            </form:form>
                    </div>
                </div>
            </div>
            <div class="row">

                <br>
                <hr style="width: 100%; border: none; height: 1px; color: #333; background-color: #333; ">
                <br>


                    <div class="col-sm-12">
                        <div class="nav flex-column nav-pills nav-fill" id="v-pills-tab" role="tablist" aria-orientation="vertical" >
                            <form:form action="${pageContext.request.contextPath}/dashboard" method="GET">
                                <input type="submit" class="btn btn-outline-info" value="Dashboard"/>
                            </form:form>
                                <%--<a class="nav-link active" id="v-pills-dashboard-tab" data-toggle="pill" href="#v-pills-dashboard" role="tab" aria-controls="v-pills-dashboard" aria-selected="true"><i class="fas fa-columns"></i> Dashboard</a>--%>
                            <%--<a class="nav-link" id="v-pills-traders-tab" data-toggle="pill" href="#v-pills-traders" role="tab" aria-controls="v-pills-traders" aria-selected="false"><i class="fas fa-chart-line"></i> Invest with Traders</a>--%>
                            <%--<a class="nav-link" id="v-pills-portofolio-tab" data-toggle="pill" href="#v-pills-portofolio" role="tab" aria-controls="v-pills-portofolio" aria-selected="false"><i class="fas fa-briefcase"></i> Portofolio</a>--%>
                            <%--<a class="nav-link" id="v-pills-th-tab" data-toggle="pill" href="#v-pills-th" role="tab" aria-controls="v-pills-th" aria-selected="false"><i class="fas fa-history"></i> Transaction History</a>--%>
                            <%--<a class="nav-link" id="v-pills-wallet-tab" data-toggle="pill" href="#v-pills-wallet" role="tab" aria-controls="v-pills-wallet" aria-selected="false"><i class="far fa-credit-card"></i> Wallet</a>--%>
                            <%--<a class="nav-link" id="v-pills-settings-tab" data-toggle="pill" href="#v-pills-settings" role="tab" aria-controls="v-pills-settings" aria-selected="false"><i class="fas fa-cogs"></i> Settings</a>--%>
                            <%--<security:authorize access="hasAnyRole('ADMIN', 'MEMBER')">--%>
                                <%--<a class="nav-link" id="v-pills-trades-tab" href="${pageContext.request.contextPath}/trade" role="button" aria-controls="v-pills-trades" aria-selected="false"><i class="fas fa-exchange-alt"></i> Trade</a>--%>
                            <%--</security:authorize>--%>
                        </div>
                    </div>

            </div>
        </div>

        <div class="col-sm-10" >
            <%-- main bar --%>
            <ul class="nav nav-pills mb-3" id="pills-tab" role="tablist">
                <li class="nav-item">
                    <a class="nav-link active" id="pills-limit-tab" data-toggle="pill" href="#pills-limit" role="tab" aria-controls="pills-limit" aria-selected="true">Limit</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="pills-market-tab" data-toggle="pill" href="#pills-market" role="tab" aria-controls="pills-market" aria-selected="false">Market</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="pills-stopMarket-tab" data-toggle="pill" href="#pills-stopMarket" role="tab" aria-controls="pills-stopMarket" aria-selected="false">Stop Market</a>
                </li>
            </ul>
            <div class="tab-content" id="pills-tabContent">

                <%--------------------------------------------------------------- LIMIT TAB ------------------------------------------------------------------------------- --%>
                <div class="tab-pane fade show active" id="pills-limit" role="tabpanel" aria-labelledby="pills-limit-tab">
                    <form:form action="${pageContext.request.contextPath}/trade/order" method="POST" id="limit-form" oninput="x.value=parseInt(a.value)">
                        <div class="row">
                            <div class="col-sm-1">

                            </div>
                            <div class="col-sm-1">
                                <input type="text" name="ordType" value="Limit" hidden /><br><br>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-1">
                                Client
                            </div>
                            <div class="col-sm-1">
                                <select name="client">
                                    <option value="bitmex">Bitmex</option>
                                    <option value="testnet">Testnet</option>
                                </select>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">
                                Contract
                            </div>
                            <div class="col-sm-1">
                                <select name="symbol">
                                    <option value="XBTUSD">XBTUSD</option>
                                    <option value="ETHUSD">ETHUSD</option>
                                    <option value="ADAZ18">ADAZ18</option>
                                    <option value="BCHZ18">BCHZ18</option>
                                    <option value="LTCZ18">LTCZ18</option>
                                    <option value="XRPZ18">XRPZ18</option>
                                    <option value="TRXZ18">TRXZ18</option>
                                </select>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">
                                Quantity
                            </div>
                            <div class="col-sm-1">
                                <input type="number" name="orderQty"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-1">
                                Limit Price
                            </div>
                            <div class="col-sm-1">
                                <input type="number" name="price"/>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">
                                Side
                            </div>
                            <div class="col-sm-1">
                                <select name="side">
                                    <option value="Buy">Buy</option>
                                    <option value="Sell">Sell</option>
                                </select>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">
                                Leverage
                            </div>
                            <div class="col-sm-2">
                                <input type="range" id="a" name="leverage" value="0"> <output name="x" for="a"></output>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">

                            </div>
                            <div class="col-sm-1">
                                <input type="submit" value="Place Order" /><br>
                            </div>
                        </div>

                    </form:form>

                </div>

                <%----------------------------------------------- MARKET TAB ------------------------------------------------------------------------------- --%>
                <div class="tab-pane fade" id="pills-market" role="tabpanel" aria-labelledby="pills-market-tab">
                    <form:form action="${pageContext.request.contextPath}/trade/order" method="POST" id="market-form" oninput="x.value=parseInt(a2.value)">
                        <div class="row">
                            <div class="col-sm-1">

                            </div>
                            <div class="col-sm-1">
                                <input type="text" name="ordType" value="Market" hidden /><br><br>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-1">
                                Client
                            </div>
                            <div class="col-sm-1">
                                <select name="client">
                                    <option value="bitmex">Bitmex</option>
                                    <option value="testnet">Testnet</option>
                                </select>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">
                                Contract
                            </div>
                            <div class="col-sm-1">
                                <select name="symbol">
                                    <option value="XBTUSD">XBTUSD</option>
                                    <option value="ETHUSD">ETHUSD</option>
                                    <option value="ADAZ18">ADAZ18</option>
                                    <option value="BCHZ18">BCHZ18</option>
                                    <option value="LTCZ18">LTCZ18</option>
                                    <option value="XRPZ18">XRPZ18</option>
                                    <option value="TRXZ18">TRXZ18</option>
                                </select>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">
                                Quantity
                            </div>
                            <div class="col-sm-1">
                                <input type="number" name="orderQty"/>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">
                                Side
                            </div>
                            <div class="col-sm-1">
                                <select name="side">
                                    <option value="Buy">Buy</option>
                                    <option value="Sell">Sell</option>
                                </select>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">
                                Leverage
                            </div>
                            <div class="col-sm-2">
                                <input type="range" id="a2" name="leverage" value="0"> <output name="x" for="a"></output>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">

                            </div>
                            <div class="col-sm-1">
                                <input type="submit" value="Place Order" /><br>
                            </div>
                        </div>

                    </form:form>
                </div>

                    <%------------------------------------------ LIMIT TAB ------------------------------------------------------------------------------- --%>
                    <div class="tab-pane fade" id="pills-stopMarket" role="tabpanel" aria-labelledby="pills-stopMarket-tab">
                    <form:form action="${pageContext.request.contextPath}/trade/order" method="POST" id="stop-form" oninput="x.value=parseInt(a3.value)">
                        <div class="row">
                            <div class="col-sm-1">

                            </div>
                            <div class="col-sm-1">
                                <input type="text" name="ordType" value="Stop" hidden /><br><br>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-1">
                                Client
                            </div>
                            <div class="col-sm-1">
                                <select name="client">
                                    <option value="bitmex">Bitmex</option>
                                    <option value="testnet">Testnet</option>
                                </select>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">
                                Contract
                            </div>
                            <div class="col-sm-1">
                                <select name="symbol">
                                    <option value="XBTUSD">XBTUSD</option>
                                    <option value="ETHUSD">ETHUSD</option>
                                    <option value="ADAZ18">ADAZ18</option>
                                    <option value="BCHZ18">BCHZ18</option>
                                    <option value="LTCZ18">LTCZ18</option>
                                    <option value="XRPZ18">XRPZ18</option>
                                    <option value="TRXZ18">TRXZ18</option>
                                </select>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">
                                Quantity
                            </div>
                            <div class="col-sm-1">
                                <input type="number" name="orderQty"/>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-sm-1">
                                Stop Price
                            </div>
                            <div class="col-sm-1">
                                <input type="number" name="stopPx"/>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">
                                Side
                            </div>
                            <div class="col-sm-1">
                                <select name="side">
                                    <option value="Buy">Buy</option>
                                    <option value="Sell">Sell</option>
                                </select>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">
                                Leverage
                            </div>
                            <div class="col-sm-2">
                                <input type="range" id="a3" name="leverage" value="0"> <output name="x" for="a"></output>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-sm-1">

                            </div>
                            <div class="col-sm-1">
                                <input type="submit" value="Place Order" /><br>
                            </div>
                        </div>
                        <input type="text" name="execInst" value="Close,LastPrice" hidden /><br><br>
                    </form:form>
                </div>
            </div>
        </div>
</div>
</div>

<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
</body>
</html>
