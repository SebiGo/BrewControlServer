package ch.goodrick.brewcontrol.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import ch.goodrick.brewcontrol.mashing.Mashing;
import ch.goodrick.brewcontrol.mashing.Rest;

public class RestServiceTest {
	final private String test = "test";
	final private Rest rest = new Rest(test, 57d, 1, Boolean.FALSE);
	final private RestService rs = new RestService();
	final private RestVO rvo1 = new RestVO();
	final private RestVO rvo2 = new RestVO();
	final private RestVO rvo3 = new RestVO();

	@Before
	public void setUp() {
		Mashing.getInstance().setName(test);
		rvo1.setName(rest.getName());
		rvo1.setDuration(rest.getDuration());
		rvo1.setTemperature(rest.getTemperature());
		rvo1.setContinueAutomatically(rest.isContinueAutomatically());
		rvo1.setPriority(1);
		rvo2.setName(rest.getName() + "2");
		rvo2.setDuration(rest.getDuration());
		rvo2.setTemperature(rest.getTemperature());
		rvo2.setContinueAutomatically(rest.isContinueAutomatically());
		rvo2.setPriority(2);
		rvo3.setName(rest.getName() + "2");
		rvo3.setDuration(rest.getDuration());
		rvo3.setTemperature(rest.getTemperature());
		rvo3.setContinueAutomatically(rest.isContinueAutomatically());
		rvo3.setPriority(3);
	}

	@Test
	public void testNewRest() {
		deleteRests();
		Response r = rs.getRests();
		RestVO[] restvos = (RestVO[]) r.getEntity();
		assertEquals(r.getHeaderString("Access-Control-Allow-Origin"), "*");
		assertEquals(restvos.length, 0);

		rs.newRest(rvo1);

		r = rs.getRests();
		restvos = (RestVO[]) r.getEntity();
		assertEquals(restvos.length, 1);
	}

	@Test
	public void testGetRests() {
		deleteRests();
		rs.newRest(rvo1);
		rs.newRest(rvo2);
		Response r = rs.getRests();
		RestVO[] restvos = (RestVO[]) r.getEntity();
		assertEquals(r.getHeaderString("Access-Control-Allow-Origin"), "*");
		assertEquals(restvos.length, 2);
		assertEquals(restvos[0].getName(), rest.getName());
		assertEquals(restvos[0].getTemperature(), rest.getTemperature(), 0.001d);
		assertTrue(restvos[0].getDuration() == rest.getDuration());
	}

	@Test
	public void testGetRest() {
		deleteRests();
		rs.newRest(rvo1);
		rs.newRest(rvo2);
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
	public void testGetRestEmpty() {
		deleteRests();
		Response r2 = rs.getRest(UUID.randomUUID());
		assertNull(r2.getEntity());
	}

	@Test
	public void testSaveRest() {
		String newName = "changed";
		deleteRests();
		rs.newRest(rvo2);
		rs.newRest(rvo1);
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
	public void testSaveRestUnknown() {
		deleteRests();
		assertEquals(HttpURLConnection.HTTP_NOT_FOUND, rs.saveRest(UUID.randomUUID(), rvo1).getStatus());
	}

	@Test
	public void testDeleteTest() {
		deleteRests();
		rs.newRest(rvo1);
		rs.newRest(rvo2);
		rs.newRest(rvo3);
		Response r = rs.getRests();
		RestVO[] restvos = (RestVO[]) r.getEntity();
		assertEquals(restvos.length, 3);
		Map<Long, RestVO> set = new HashMap<Long, RestVO>();
		for (RestVO restvo : restvos) {
			set.put(restvo.getPriority(), restvo);
		}

		rs.deleteRest(set.get(new Long(1)).getUuid());
		r = rs.getRests();
		restvos = (RestVO[]) r.getEntity();
		assertEquals(restvos.length, 2);

		rs.deleteRest(set.get(new Long(0)).getUuid());
		r = rs.getRests();
		restvos = (RestVO[]) r.getEntity();
		assertEquals(restvos.length, 1);
	}

	@Test
	public void testDeleteTestUnknown() {
		deleteRests();
		assertEquals(HttpURLConnection.HTTP_NOT_FOUND, rs.deleteRest(UUID.randomUUID()).getStatus());
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
