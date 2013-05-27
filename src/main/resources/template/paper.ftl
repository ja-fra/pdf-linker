<@namespace dcterms="http://purl.org/dc/terms/"/>
<@namespace swrc="http://swrc.ontoware.org/ontology#"/>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de" lang="de">
<head>
  <title><@ldpath path="."/></title>
    <link href="${ROOT}/ext/bootstrap/css/bootstrap.css" rel="stylesheet" media="screen" />
    <script src="${ROOT}/ext/bootstrap/js/bootstrap.js"></script>
    <style type="text/css">
        body {
            padding-top: 20px;
            padding-bottom: 40px;
        }

            /* Custom container */
        .container-narrow {
            margin: 0 auto;
            max-width: 700px;
        }
        .container-narrow > hr {
            margin: 30px 0;
        }

            /* Main marketing message and sign up button */
        .jumbotron {
            margin: 60px 0;
            text-align: center;
        }
        .jumbotron h1 {
            font-size: 72px;
            line-height: 1;
        }
        .jumbotron .btn {
            font-size: 21px;
            padding: 14px 24px;
        }

            /* Supporting marketing content */
        .marketing {
            margin: 60px 0;
        }
        .marketing p + h4 {
            margin-top: 28px;
        }
    </style>
</head>
<body>
<#setting url_escaping_charset="UTF-8">
<#assign uri><@ldpath path="."/></#assign>
<div class="container-narrow">

    <div class="masthead">
        <ul class="nav nav-pills pull-right">
            <li><a href="${ROOT}">Home</a></li>
            <li><a href="${ROOT}sparql/admin/snorql/snorql.html?describe=${uri?url}">Snorql</a></li>
            <li><a href="<@ldpath path="."/>">LD Browser</a></li>
        </ul>
        <h3 class="muted"><@ldpath path="."/></h3>
    </div>

    <hr>

    <div class="jumbotron">
        <h1><@ldpath path="dcterms:title"/></h1>
        <p class="lead"><@ldpath path="swrc:abstract"/></p>
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