<@namespace dcterms="http://purl.org/dc/terms/"/>
<@namespace swrc="http://swrc.ontoware.org/ontology#"/>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
<head>
  <title>${URI}</title>
    <link href="${ROOT}/ext/bootstrap/css/bootstrap.css" rel="stylesheet" media="screen" />
    <script src="${ROOT}/ext/bootstrap/js/bootstrap.js"></script>
</head>
<body>
<#setting url_escaping_charset="UTF-8">
<#assign uri><@ldpath path="."/></#assign>
<div class="container-narrow">

    <div class="masthead">
        <ul class="nav nav-pills pull-right">
            <li><a href="${ROOT}">Home</a></li>
            <li><a href="${ROOT}sparql/admin/snorql/snorql.html?describe="${uri?url}>Snorql</a></li>
            <li><a href="<@ldpath path="."/>">LD Browser</a></li>
        </ul>
        <h3 class="muted"><@ldpath path="."/></h3>
    </div>

    <hr>

    <div class="jumbotron">
        <h1><@ldpath path="dcterms:title"/></h1>
        <p class="lead"><@ldpath path="swrc:abstract"/></p>
        <a class="btn btn-large btn-success" href="#">Sign up today</a>
    </div>

    <hr>

    <div class="row-fluid marketing">
        <h4>Authors</h4>

    </div>

    <hr>

    <div class="footer">
        <p>&copy; Salzburg Research 2013</p>
    </div>

</div>
</body>
</html>