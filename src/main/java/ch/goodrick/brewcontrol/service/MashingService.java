package ch.goodrick.brewcontrol.service;

import static ch.goodrick.brewcontrol.mashing.RestState.ACTIVE;
import static ch.goodrick.brewcontrol.mashing.RestState.HEATING;
import static ch.goodrick.brewcontrol.mashing.RestState.WAITING_COMPLETE;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

import java.io.File;
import java.io.IOException;
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
import ch.goodrick.brewcontrol.mashing.MashingException;
import ch.goodrick.brewcontrol.mashing.Rest;
import ch.goodrick.brewcontrol.sensor.SensorDS18B20;

@Path("/mashing")
public class MashingService {
	// private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Context
	private HttpHeaders headers;

	@GET
	@Produces({ "application/json", "application/xml" })
	public Response getMashing() {
		MashingVO mash = new MashingVO();
		mash.setName(Mashing.getInstance().getName());
		mash.setTemperature(Mashing.getInstance().getCurrentTemperature());
		if (Mashing.getInstance().getTemperatureSensor() instanceof SensorDS18B20) {
			SensorDS18B20 s = (SensorDS18B20) Mashing.getInstance().getTemperatureSensor();
			mash.setAltitude(s.getAltitude());
			mash.setMeasuredTemperatureBoilingWater(s.getTempBoilingWater());
			mash.setMeasuredTemperatureIceWater(s.getTempIceWater());
		}
		Rest rest = Mashing.getInstance().getFirstRest();
		while (rest != null) {
			if (rest.getState() == HEATING || rest.getState() == WAITING_COMPLETE || rest.getState() == ACTIVE) {
				mash.setActiveRest(rest.getUuid());
				break;
			}
			rest = rest.getNextRest();
		}
		return Response.ok(mash).header("Access-Control-Allow-Origin", "*").build();
	}

	@POST
	@Consumes({ "application/json", "application/xml" })
	public Response newName(MashingVO mashing) {
		return saveName(mashing.getName(), mashing);
	}

	@POST
	@Path("/{name}")
	@Consumes({ "application/json", "application/xml" })
	public Response saveName(@PathParam("name") String name, MashingVO mashing) {
		Mashing.getInstance().setName(mashing.getName());

		if (Mashing.getInstance().getTemperatureSensor() instanceof SensorDS18B20) {
			SensorDS18B20 s = (SensorDS18B20) Mashing.getInstance().getTemperatureSensor();
			// TODO persist this value for the sensor id
			s.calibrate(mashing.getMeasuredTemperatureIceWater(), mashing.getMeasuredTemperatureBoilingWater(), mashing.getAltitude());
		}

		return Response.ok().header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "POST")
				.header("Content-Type", "application/json").build();
	}

	@DELETE
	@Path("/{uuid}")
	public Response deleteRest(@PathParam("uuid") UUID uuid) {
		// don't delete the name
		return Response.status(HTTP_BAD_METHOD).header("Access-Control-Allow-Origin", "*").build();
	}

	@GET
	@Path("/start")
	public Response startMashing() {
		try {
			Mashing.getInstance().startMashing();
			return Response.ok().header("Access-Control-Allow-Origin", "*").build();
		} catch (MashingException e) {
			return Response.status(HTTP_UNAVAILABLE).header("Access-Control-Allow-Origin", "*").build();
		}
	}

	@GET
	@Path("/continue")
	public Response continueMashing() throws IOException {
		Mashing.getInstance().continueRest();
		return Response.ok().header("Access-Control-Allow-Origin", "*").build();
	}

	@GET
	@Path("/terminate")
	public Response terminateMashing() {
		Mashing.getInstance().terminate();
		return Response.ok().header("Access-Control-Allow-Origin", "*").build();
	}

	@GET
	@Path("/graph")
	@Produces("image/png")
	public Response getGraph() {
		if (Mashing.getInstance().getTempLogger() == null) {
			return Response.status(HTTP_UNAVAILABLE).build();
		}
		try {
			File graph = Mashing.getInstance().getTempLogger().getGraph();
			return Response.ok(graph).header("Access-Control-Allow-Origin", "*").header("Content-disposition", "inline; filename=" + graph.getName()).build();
		} catch (IOException e) {
			return Response.status(HTTP_UNAVAILABLE).header("Access-Control-Allow-Origin", "*").build();
		}
	}

	// CORS detection
	@OPTIONS
	@Path("/{name}")
	public Response options() {
		String origin = headers.getRequestHeader("Origin").get(0);
		String method = headers.getRequestHeader("Access-Control-Request-Method").get(0);
		String header = headers.getRequestHeader("Access-Control-Request-Headers").get(0);
		return Response.ok().header("Access-Control-Allow-Methods", method).header("Access-Control-Allow-Credentials", "false")
				.header("Access-Control-Allow-Origin", origin).header("Access-Control-Allow-Headers", header).build();
	}
}
