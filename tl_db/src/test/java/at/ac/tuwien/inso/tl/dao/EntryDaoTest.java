package at.ac.tuwien.inso.tl.dao;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import at.ac.tuwien.inso.tl.model.Entry;

public class EntryDaoTest {
	
private static final Logger LOG = Logger.getLogger(TicketDaoTest.class);
	
	@Autowired
	private EntryDao edao;
	
	@Before
	public void setUp() throws Exception {
		LOG.info("setUp called.");
	}

	@After
	public void tearDown() throws Exception {
		LOG.info("tearDown called.");
	}	
	
	@Test
	public void test(){
		/*
		Entry e = new Entry();
		e.setAmount(50);
		e.setBuyWithPoints(false);
		e.setSold(false);
		
		edao.createEntry(e, 1);
		
		
		assertTrue();
		*/
	}
	
	@Test
	public void getEntryWithBasketId1TestShouldNotReturnEmpty(){
		LOG.info("getEntryWithBasketId1TestShouldNotReturnEmpty called");
		
		if(edao == null){
			LOG.error("Autowired EntryDao null");
		}
		//List<Map.Entry<Entry, Boolean>> entries = edao.getEntry(1);
		
		//assertFalse(entries.isEmpty());
		
	}

}