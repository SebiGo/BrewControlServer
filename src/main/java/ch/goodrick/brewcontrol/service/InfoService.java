package ch.goodrick.brewcontrol.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/info")
public class InfoService {
	// private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Context
	private HttpHeaders headers;

	@GET
	@Produces({ "application/json", "application/xml" })
	public Response getInfo() {
		InfoVO info = new InfoVO();
		info.setVersion(getClass().getPackage().getImplementationVersion());
		return Response.ok(info).header("Access-Control-Allow-Origin", "*").build();
	}
}
