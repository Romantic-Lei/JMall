package test;

import java.util.List;

import org.apache.solr.client.solrj.response.UpdateResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring/applicationContext-solr.xml")
public class SorlTest {

	@Autowired
	private SolrTemplate solrTemplate;
	
	@Test
	public void testDeleteAll(){
		Query query=new SimpleQuery("*:*");
		
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
	
}
