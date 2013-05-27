package at.newmedialab.lmf.eswchack.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.marmotta.ldpath.api.backend.RDFBackend;
import org.apache.marmotta.platform.core.api.http.HttpClientService;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import at.newmedialab.lmf.enhancer.model.ldpath.EnhancementProcessor;
import at.newmedialab.lmf.enhancer.model.ldpath.EnhancementResult;

@ApplicationScoped
public class DoiLinkerProcessor implements EnhancementProcessor {

    @Inject
    private HttpClientService hcService;
    
    
    @Override
    public EnhancementResult transform(RDFBackend<Value> backend, Value value,
            Map<String, String> args) throws IllegalArgumentException {

        try {
            
            Set<Resource> enh = new HashSet<Resource>();
            Repository rep = new SailRepository(new MemoryStore());
            rep.initialize();
            try {
                String title = null, author = null;
                String doi = hcService.execute(buidRequest(title, author), doiResponstHandler());

                return null;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            return new EnhancementResult(enh, rep);
        } catch (RepositoryException e1) {
            throw new IllegalArgumentException(e1);
        }
    }

    private ResponseHandler<String> doiResponstHandler() {
        return new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response)
                    throws ClientProtocolException, IOException {
                
                String line = EntityUtils.toString(response.getEntity(), "utf-8");
                
                return line.replaceAll(".*\\|", "");
            }
        };
    }

    private HttpGet buidRequest(String title, String author) throws IOException {
        final String service = "http://doi.crossref.org/servlet/query";
        
        StringBuilder urlBuilder = new StringBuilder(service);
        appendQuery(urlBuilder, "pid", "jakob.frank@salzburgresearch.at");
//        appendQuery(urlBuilder, "format", "xml_xsd");
        appendQuery(urlBuilder, "qdata", buildQueryXML(title, author));
        
        return new HttpGet(urlBuilder.toString());
    }

    private String buildQueryXML(String title, String author) throws IOException {
        Document doc = new  Document();
        /*
          <?xml version="1.0"?>
<query_batch version="2.0" xsi:schemaLocation="http://www.crossref.org/qschema/2.0 http://www.crossref.org/qschema/crossref_query_input2.0.xsd" xmlns="http://www.crossref.org/qschema/2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"> 
<head><email_address>support@crossref.org</email_address><doi_batch_id>ABC_123_fff</doi_batch_id> </head>
 <body> 
<query enable-multiple-hits="false" key="key1">
  <article_title match="fuzzy">The Linked Media Framework</article_title>
  <author search-all-authors="true">Schaffert</author>
</query>
</body>
</query_batch>
         */
        
        Element root = new Element("query_batch");
        root.setAttribute("version", "2.0");
        doc.setRootElement(root);
        
        Element head = new Element("head");
        root.addContent(head);
        head.addContent(new Element("emails_address").setText("support@crossref.org"));
        head.addContent(new Element("doi_batch_id").setText(UUID.randomUUID().toString()));
        
        Element body = new Element("body");
        root.addContent(body);
        
        Element q = new Element("query");
        q.setAttribute("enable-multiple-hits", "false");
        q.setAttribute("key", "key1");
        
        q.addContent(new Element("article_title").setAttribute("match", "fuzzy").setText(title));
        q.addContent(new Element("author").setAttribute("search-all-authors", "true").setText(author));
        
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getCompactFormat());
        StringBuilderWriter sbw = new StringBuilderWriter();
        out.output(doc, sbw);
        return sbw.toString();
    }

    private static void appendQuery(StringBuilder urlBuilder, String key,
            String val) {
        final String delim = (urlBuilder.indexOf("?")>0)?"&":"?";
        urlBuilder.append(delim).append(urlEncode(key)).append("=").append(urlEncode(val));
    }

    private static String urlEncode(String val) {
        try {
            return URLEncoder.encode(val, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return val;
        }
    }

    @Override
    public String getNamespaceUri() {
        return "http://exwc-hackthon.org/";
    }

    @Override
    public String getNamespacePrefix() {
        return "service";
    }

    @Override
    public String getLocalName() {
        return "doi";
    }


}
