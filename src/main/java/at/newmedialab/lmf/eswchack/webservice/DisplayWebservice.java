package at.newmedialab.lmf.eswchack.webservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.marmotta.commons.sesame.repository.ResourceUtils;
import org.apache.marmotta.ldpath.backend.sesame.SesameConnectionBackend;
import org.apache.marmotta.ldpath.template.engine.TemplateEngine;
import org.apache.marmotta.platform.core.api.config.ConfigurationService;
import org.apache.marmotta.platform.core.api.triplestore.SesameService;
import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.emory.mathcs.backport.java.util.Collections;
import freemarker.template.TemplateException;

@Path("/display")
@ApplicationScoped
public class DisplayWebservice {
    
    private static Logger log = LoggerFactory.getLogger(DisplayWebservice.class);
    
    private static final String DEF_TEMPLATE = "paper.ftl";

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private SesameService sesameService;
    
    private File templateDir;

    @PostConstruct
    protected void setup() {
        templateDir = new File(configurationService.getHome(), "templates");
        if (!templateDir.exists()) templateDir.mkdirs();

        final File dT = new File(templateDir, DEF_TEMPLATE);
        if (!dT.exists()) {
            try {
                log.info("Default Template not found, using fallback...");
                final InputStream str = this.getClass().getResourceAsStream("/template/"+DEF_TEMPLATE);
                FileUtils.copyInputStreamToFile(str, dT);
            } catch (IOException e) {
                log.error("Could not create fallback template, templating might react weird!", e);
            }
        }
    }

    @GET
    @Produces("text/html")
    @Path("/{lname:.*}")
    public Response getLocal(@PathParam("lname") String localName) {
        return get(configurationService.getBaseUri() + ConfigurationService.RESOURCE_PATH + "/" + localName);
    }

    @GET
    @Produces("text/html")
    public Response getRemote(@QueryParam("uri") String uri) {
        return get(uri);
    }

    private Response get(final String uri) {
        if (StringUtils.isBlank(uri)) {
            return Response.status(Status.BAD_REQUEST).entity("no URI provided").build();
        }
        StreamingOutput resp = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    final RepositoryConnection con = sesameService.getConnection();
                    try {
                        con.begin();
                        if (!ResourceUtils.isSubject(con, uri)) {
                            throw new NoLogWebApplicationException(Response.status(Status.NOT_FOUND).entity("Resource with URI " + uri + " not found").build());
                        }
                        
                        final URI s = con.getValueFactory().createURI(uri);

                        // Redirect non-articles!
                        if (!ResourceUtils.hasType(con, s, con.getValueFactory().createURI("http://purl.org/spar/fabio/Expression"))) {
                            throw new NoLogWebApplicationException(Response.status(Status.TEMPORARY_REDIRECT).location(java.net.URI.create(s.stringValue())).build());
                        }
                        
                        final TemplateEngine<Value> engine = createEngine(con);
                        
                        OutputStreamWriter writer = new OutputStreamWriter(output, Charset.forName("utf-8"));
                        engine.processFileTemplate(s, configurationService.getStringConfiguration("sn.archive.template", DEF_TEMPLATE),Collections.singletonMap("ROOT", configurationService.getServerUri()), writer);
                    } finally {
                        con.commit();
                        con.close();
                    }
                } catch (TemplateException e) {
                    throw new WebApplicationException(e);
                } catch (RepositoryException e) {
                    throw new WebApplicationException(e);
                }
                
            }
        };
        return Response.ok(resp).build();
        
    }

    private TemplateEngine<Value> createEngine(final RepositoryConnection con) {
        final SesameConnectionBackend backend = SesameConnectionBackend.withConnection(con);
        final TemplateEngine<Value> engine = new TemplateEngine<Value>(backend);
        
        try {
            engine.setDirectoryForTemplateLoading(templateDir);
        } catch (IOException e) {
            log.warn("Could not set templateDirectory", e);
        }
        
        return engine;
    }
    
    

}
