package ch.goodrick.brewcontrol.service;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import ch.goodrick.brewcontrol.mashing.Mashing;
import ch.goodrick.brewcontrol.mashing.Rest;

@Path("/rest")
public class RestService {
	@Context
	private HttpHeaders headers;

	// private Logger log = LoggerFactory.getLogger(this.getClass());

	@GET
	@Produces({ "application/json", "application/xml" })
	public Response getRests() {
		Rest rest = Mashing.getInstance().getFirstRest();

		// count number of rests
		int i = 0;
		while (rest != null) {
			i++;
			rest = rest.getNextRest();
		}

		// return rests
		RestVO[] rests = new RestVO[i];
		i = 0;
		rest = Mashing.getInstance().getFirstRest();
		while (rest != null) {
			rests[i] = transform(rest, i);
			i++;
			rest = rest.getNextRest();
		}
		return Response.ok(rests).header("Access-Control-Allow-Origin", "*").build();
	}

	@POST
	@Consumes({ "application/json", "application/xml" })
	public Response newRest(RestVO rest) {
		Mashing.getInstance().addRest(transform(rest));
		return Response.ok().header("Access-Control-Allow-Origin", "*").build();
	}

	@GET
	@Path("/{uuid}")
	@Produces({ "application/json", "application/xml" })
	public Response getRest(@PathParam("uuid") UUID uuid) {
		Rest rest = Mashing.getInstance().getFirstRest();
		while (rest != null) {
			if (rest.getUuid().equals(uuid)) {
				return Response.ok(transform(rest, 0)).header("Access-Control-Allow-Origin", "*").build();
			}
			rest = rest.getNextRest();
		}
		return Response.status(HTTP_NOT_FOUND).header("Access-Control-Allow-Origin", "*").build();
	}

	@POST
	@Path("/{uuid}")
	@Consumes({ "application/json", "application/xml" })
	public Response saveRest(@PathParam("uuid") UUID uuid, RestVO restVO) {
		Rest rest = Mashing.getInstance().getFirstRest();
		while (rest != null) {
			if (rest.getUuid().equals(uuid)) {
				rest.setRestName(restVO.getName());
				rest.setDuration(restVO.getDuration());
				rest.setTemperature(restVO.getTemperature());
				rest.setContinueAutomatically(restVO.getContinueAutomatically());
				return Response.status(HTTP_OK).header("Access-Control-Allow-Origin", "*").build();
			}
			rest = rest.getNextRest();
		}
		return Response.status(HTTP_NOT_FOUND).header("Access-Control-Allow-Origin", "*").build();
	}

	@DELETE
	@Path("/{uuid}")
	public Response deleteRest(@PathParam("uuid") UUID uuid) {
		Rest rest = Mashing.getInstance().getFirstRest();
		Rest previous = null;
		while (rest != null) {
			if (rest.getUuid().equals(uuid)) {
				if (previous == null) {
					Mashing.getInstance().setFirstRest(rest.getNextRest());
				} else {
					previous.setNextRest(rest.getNextRest());
				}
				return Response.status(HTTP_OK).header("Access-Control-Allow-Origin", "*").build();
			}
			previous = rest;
			rest = rest.getNextRest();
		}
		return Response.status(HTTP_NOT_FOUND).header("Access-Control-Allow-Origin", "*").build();
	}

	private RestVO transform(Rest rest, long priority) {
		RestVO restVO = new RestVO();
		restVO.setPriority(priority);
		restVO.setName(rest.getName());
		restVO.setContinueAutomatically(rest.isContinueAutomatically());
		restVO.setDuration(rest.getDuration());
		restVO.setTemperature(rest.getTemperature());
		restVO.setStatus(rest.getState());
		restVO.setUuid(rest.getUuid());
		if (rest.getHeating() != null) {
			restVO.setHeating(rest.getHeating().getTimeInMillis());
		}
		if (rest.getActive() != null) {
			restVO.setActive(rest.getActive().getTimeInMillis());
		}
		if (rest.getCompleted() != null) {
			restVO.setCompleted(rest.getCompleted().getTimeInMillis());
		}
		return restVO;
	}

	private Rest transform(RestVO restVO) {
		Rest rest = new Rest(restVO.getName(), restVO.getTemperature(), restVO.getDuration(), restVO.getContinueAutomatically());
		return rest;
	}

	// CORS detection
	@OPTIONS
	@Path("/{name}")
	public Response optionsSub() {
		String origin = headers.getRequestHeader("Origin").get(0);
		String method = headers.getRequestHeader("Access-Control-Request-Method").get(0);
		String header = headers.getRequestHeader("Access-Control-Request-Headers").get(0);
		return Response.ok().header("Access-Control-Allow-Methods", method).header("Access-Control-Allow-Credentials", "false")
				.header("Access-Control-Allow-Origin", origin).header("Access-Control-Allow-Headers", header).build();
	}

	@OPTIONS
	@Path("/")
	public Response options() {
		String origin = headers.getRequestHeader("Origin").get(0);
		String method = headers.getRequestHeader("Access-Control-Request-Method").get(0);
		String header = headers.getRequestHeader("Access-Control-Request-Headers").get(0);
		return Response.ok().header("Access-Control-Allow-Methods", method).header("Access-Control-Allow-Credentials", "false")
				.header("Access-Control-Allow-Origin", origin).header("Access-Control-Allow-Headers", header).build();
	}
}