package ch.goodrick.brewcontrol.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import ch.goodrick.brewcontrol.mashing.Mashing;
import ch.goodrick.brewcontrol.mashing.Rest;

public class RestServiceTest {
	final private String test = "test";
	final private Rest rest = new Rest(test, 57d, 1, Boolean.FALSE);
	final private RestService rs = new RestService();
	final private RestVO rvo = new RestVO();

	@Before
	public void setUp() {
		Mashing.getInstance().setName(test);
		rvo.setName(rest.getName());
		rvo.setDuration(rest.getDuration());
		rvo.setTemperature(rest.getTemperature());
		rvo.setContinueAutomatically(rest.isContinueAutomatically());
		rvo.setPriority(1);
	}

	@Test
	public void testNewRest() {
		deleteRests();
		Response r = rs.getRests();
		RestVO[] restvos = (RestVO[]) r.getEntity();
		assertEquals(r.getHeaderString("Access-Control-Allow-Origin"), "*");
		assertEquals(restvos.length, 0);

		rs.newRest(rvo);

		r = rs.getRests();
		restvos = (RestVO[]) r.getEntity();
		assertEquals(restvos.length, 1);
	}

	@Test
	public void testGetRests() {
		deleteRests();
		rs.newRest(rvo);
		Response r = rs.getRests();
		RestVO[] restvos = (RestVO[]) r.getEntity();
		assertEquals(r.getHeaderString("Access-Control-Allow-Origin"), "*");
		assertEquals(restvos.length, 1);
		assertEquals(restvos[0].getName(), rest.getName());
		assertEquals(restvos[0].getTemperature(), rest.getTemperature(), 0.001d);
		assertTrue(restvos[0].getDuration() == rest.getDuration());
	}

	@Test
	public void testGetRest() {
		deleteRests();
		rs.newRest(rvo);
		Response r = rs.getRests();
		RestVO[] restvos = (RestVO[]) r.getEntity();
		Response r2 = rs.getRest(restvos[0].getUuid());
		RestVO restvo = (RestVO) r2.getEntity();

		assertEquals(r.getHeaderString("Access-Control-Allow-Origin"), "*");
		assertEquals(restvo.getName(), rest.getName());
		assertEquals(restvo.getTemperature(), rest.getTemperature(), 0.001d);
		assertTrue(restvo.getDuration() == rest.getDuration());
	}

	@Test
	public void testSaveRest() {
		String newName = "changed";
		deleteRests();
		rs.newRest(rvo);
		Response r = rs.getRests();
		RestVO[] restvos = (RestVO[]) r.getEntity();
		Response r2 = rs.getRest(restvos[0].getUuid());
		RestVO restvo = (RestVO) r2.getEntity();
		restvo.setName(newName);

		rs.saveRest(restvo.getUuid(), restvo);

		r = rs.getRests();
		restvos = (RestVO[]) r.getEntity();
		r2 = rs.getRest(restvos[0].getUuid());
		restvo = (RestVO) r2.getEntity();
		assertEquals(restvo.getName(), newName);
	}

	@Test
	public void testDeleteTest() {
		deleteRests();
		rs.newRest(rvo);
		Response r = rs.getRests();
		RestVO[] restvos = (RestVO[]) r.getEntity();
		Response r2 = rs.getRest(restvos[0].getUuid());
		RestVO restvo = (RestVO) r2.getEntity();
		assertEquals(restvos.length, 1);

		rs.deleteRest(restvo.getUuid());

		r = rs.getRests();
		restvos = (RestVO[]) r.getEntity();
		assertEquals(restvos.length, 0);
	}

	@Test
	public void testOptions() {
		// TODO inject request context
	}
	
	@Test
	public void testOptionsSub() {
		// TODO inject request context
	}

	private void deleteRests() {
		Response r = rs.getRests();
		RestVO[] restvos = (RestVO[]) r.getEntity();
		for (RestVO restvo : restvos) {
			rs.deleteRest(restvo.getUuid());
		}
	}
	
}
