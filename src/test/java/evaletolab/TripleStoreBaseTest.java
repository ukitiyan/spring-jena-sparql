package evaletolab;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;

import evaletolab.config.WebConfig;
import evaletolab.controller.TripleStore;
import evaletolab.tool.FileUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public class TripleStoreBaseTest extends TripleStore {

	private static final String TEST_VERSION = "http://crick:8000/en-US/app/search/sparql_queries_time_performance?form.operator=%3E&form.minTime=0&earliest=0&latest=&form.testId=";
	
    @Rule public TestName currentTest = new TestName();

	@Before
	public void setup() throws Exception {
		open();
	}

	protected void runSparql(String sparqlFileName) {
		String q = null;
		try {
			q = FileUtil.getResourceAsString("sparql/" + sparqlFileName);
		} catch (Exception e) {
			e.printStackTrace();
			org.junit.Assert.fail();
		}

		QueryExecution qe = createQueryExecution(q, currentTest.getMethodName());
		ResultSet rs = qe.execSelect();
		
		while(rs.hasNext()){
			System.out.println(rs.next());
		}

	}
	
	protected void testSparql(String sparqlFileName) {
		String q = null;
		try {
			q = FileUtil.getResourceAsString("sparql/" + sparqlFileName);
		} catch (Exception e) {
			e.printStackTrace();
			org.junit.Assert.fail();
		}

		// execute query
		String acs = getMetaInfo(q).get("acs");
		if(acs==null) acs="";
		int count = getQueryMetaCount(q);
		String title=currentTest.getMethodName();
		
		QueryExecution qe = createQueryExecution(q, title);
		ResultSet rs = qe.execSelect();

		// validate result
		List<String> uri = getLiterals(rs);
		System.out.println(uri.size() + " results found for \"" + title + "\" (" + sparqlFileName + "):");
		for(String u : uri){
			System.out.print(u);
			System.out.print(",");
		}
		assertTrue(rs.getRowNumber() >= count);
		for (String ac : acs.split(","))
			assertTrue(ac, uri.contains(ac.trim()));

	}
	
	@AfterClass
	public static void done() {
			System.out.println();
			System.out.println("Access your test results in: \n" + TEST_VERSION + instanceSignature);
	}

}
