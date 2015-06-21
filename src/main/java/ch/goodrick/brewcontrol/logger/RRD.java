package ch.goodrick.brewcontrol.logger;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;
import org.slf4j.LoggerFactory;

import ch.goodrick.brewcontrol.common.PhysicalQuantity;

/**
 * This logger uses rrd4j to collect all the logged data and lets you retrieve a
 * nice rrd graph.
 * 
 * @author sebastian@goodrick.ch
 *
 */
public class RRD implements Logger {
	private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
	private String name = "BrewControl";
	private long startTime;
	private PhysicalQuantity physicalQuantity;
	private String rrdPath;
	RrdGraph graph;

	@Override
	public void log(Double value) {
		try {
			RrdDb rrdDb = new RrdDb(rrdPath);
			logIntoRRD(rrdDb, value);
			generateGraph(rrdDb);
			rrdDb.close();
		} catch (IOException e) {
			log.warn("Could not write sample to RRD.", e);
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return the File containing a PNG of the logged data. Please note, the
	 *         picuture is generated every time you call this function, you
	 *         might want to be careful if you work with low performance
	 *         devices.
	 * @throws IOException
	 */
	public File getGraph() throws IOException {
		if (graph == null || graph.getRrdGraphInfo() == null) {
			log.warn("RrdGraph not ready yet - no sample?");
			throw new IOException("RrdGraph not ready yet - no sample?");
		}
		return new File(graph.getRrdGraphInfo().getFilename());
	}

	/**
	 * Constructs the RRD logger object with a name and a physical quantity.
	 * 
	 * @param name
	 *            the name of the data series to be logged and displayed in the
	 *            PNG.
	 * @param physicalQuantity
	 *            the physical quanitity of the data.
	 * @throws IOException
	 */
	public RRD(String name, PhysicalQuantity physicalQuantity) throws IOException {
		this.name = name;
		this.physicalQuantity = physicalQuantity;
		rrdPath = name + ".rrd";
		RrdDef rrdDef = new RrdDef(rrdPath, 1);
		rrdDef.addDatasource(name, DsType.GAUGE, 60, 0D, 110D);
		rrdDef.addArchive(ConsolFun.MAX, 0.5, 1, 60 * 60 * 12);
		RrdDb rrdDb = new RrdDb(rrdDef);
		startTime = Util.getTimestamp();
		log.info("Created RRD database in " + rrdPath + ".");
		rrdDb.close();
	}

	private void logIntoRRD(RrdDb rrdDb, Double value) throws IOException {
		Sample sample = rrdDb.createSample();
		sample.setValue(name, value);
		sample.update();
		rrdDb.close();
	}

	private void generateGraph(RrdDb rrdDb) throws IOException {
		RrdGraphDef gDef = new RrdGraphDef();
		gDef.setWidth(500);
		gDef.setHeight(300);
		gDef.setFilename(name + ".png");
		gDef.setStartTime(startTime);
		gDef.setEndTime(rrdDb.getLastUpdateTime());
		gDef.setTitle(name);
		gDef.setVerticalLabel(physicalQuantity.getUnit());
		gDef.datasource(physicalQuantity.toString(), rrdPath, name, ConsolFun.MAX);
		gDef.line(physicalQuantity.toString(), Color.RED, physicalQuantity.toString() + " max");
		gDef.setImageFormat("png");
		graph = new RrdGraph(gDef);
	}
}
